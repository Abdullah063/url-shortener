const tabs = document.querySelectorAll(".tab");
const panels = document.querySelectorAll(".panel");
const shortenForm = document.querySelector("#shorten-form");
const statsForm = document.querySelector("#stats-form");
const shortenMessage = document.querySelector("#shorten-message");
const statsMessage = document.querySelector("#stats-message");
const shortenResult = document.querySelector("#shorten-result");
const statsResult = document.querySelector("#stats-result");
const shortUrlElement = document.querySelector("#short-url");
const resultMeta = document.querySelector("#result-meta");
const copyButton = document.querySelector("#copy-button");
const copyLabel = document.querySelector("#copy-label");
const toast = document.querySelector("#toast");
const expiresAtInput = document.querySelector("#expires-at");

let toastTimer;

tabs.forEach((tab) => {
    tab.addEventListener("click", () => {
        tabs.forEach((item) => {
            const isSelected = item === tab;
            item.classList.toggle("is-active", isSelected);
            item.setAttribute("aria-selected", String(isSelected));
        });

        panels.forEach((panel) => {
            panel.hidden = panel.id !== tab.dataset.panel;
        });
    });
});

expiresAtInput.min = toLocalDateTimeValue(new Date());

shortenForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessage(shortenMessage);
    shortenResult.hidden = true;

    const originalUrl = document.querySelector("#original-url").value.trim();
    const expiresAt = expiresAtInput.value;
    const submitButton = shortenForm.querySelector("button[type='submit']");

    if (!originalUrl) {
        showMessage(shortenMessage, "Lütfen bir bağlantı gir.");
        return;
    }

    setLoading(submitButton, true, "Kısaltılıyor...");

    try {
        const payload = {originalUrl};
        if (expiresAt) {
            payload.expiresAt = expiresAt;
        }

        const response = await fetch("/api/v1/urls", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(payload)
        });
        const data = await readJson(response);

        if (!response.ok) {
            throw new Error(getErrorMessage(data, "Bağlantı kısaltılamadı."));
        }

        const shortUrl = `${window.location.origin}/${data.code}`;
        shortUrlElement.textContent = shortUrl;
        shortUrlElement.href = shortUrl;
        resultMeta.textContent = data.expiresAt
            ? `${formatDate(data.expiresAt)} tarihine kadar aktif`
            : "Süresiz olarak aktif";
        copyLabel.textContent = "Kopyala";
        shortenResult.hidden = false;
    } catch (error) {
        showMessage(shortenMessage, error.message);
    } finally {
        setLoading(submitButton, false, "Kısalt");
    }
});

statsForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    clearMessage(statsMessage);
    statsResult.hidden = true;

    const input = document.querySelector("#stats-code").value.trim();
    const code = extractCode(input);
    const submitButton = statsForm.querySelector("button[type='submit']");

    if (!code) {
        showMessage(statsMessage, "Lütfen kısa bağlantıyı veya kodu gir.");
        return;
    }

    setLoading(submitButton, true, "Sorgulanıyor...");

    try {
        const response = await fetch(`/api/v1/urls/${encodeURIComponent(code)}/stats`);
        const data = await readJson(response);

        if (!response.ok) {
            throw new Error(getErrorMessage(data, "İstatistik bulunamadı."));
        }

        document.querySelector("#visit-count").textContent = data.visitCount.toLocaleString("tr-TR");
        document.querySelector("#stats-result-code").textContent = data.code;
        document.querySelector("#stats-created-at").textContent = formatDate(data.createdAt);

        const originalUrlElement = document.querySelector("#stats-original-url");
        originalUrlElement.textContent = data.originalUrl;
        originalUrlElement.href = data.originalUrl;
        originalUrlElement.title = data.originalUrl;
        statsResult.hidden = false;
    } catch (error) {
        showMessage(statsMessage, error.message);
    } finally {
        setLoading(submitButton, false, "Sorgula");
    }
});

copyButton.addEventListener("click", async () => {
    const value = shortUrlElement.href;

    try {
        await navigator.clipboard.writeText(value);
        copyLabel.textContent = "Kopyalandı";
        showToast("Bağlantı panoya kopyalandı.");
    } catch {
        showMessage(shortenMessage, "Bağlantı kopyalanamadı. Elle seçerek kopyalayabilirsin.");
    }
});

function extractCode(value) {
    if (!value) {
        return "";
    }

    try {
        const url = new URL(value);
        const segments = url.pathname.split("/").filter(Boolean);
        return segments.at(-1) ?? "";
    } catch {
        return value.replace(/^\/+|\/+$/g, "");
    }
}

function getErrorMessage(data, fallback) {
    const fieldError = data?.fieldErrors && Object.values(data.fieldErrors)[0];
    return fieldError || data?.message || fallback;
}

async function readJson(response) {
    try {
        return await response.json();
    } catch {
        return {};
    }
}

function formatDate(value) {
    return new Intl.DateTimeFormat("tr-TR", {
        dateStyle: "medium",
        timeStyle: "short"
    }).format(new Date(value));
}

function toLocalDateTimeValue(date) {
    const offset = date.getTimezoneOffset();
    return new Date(date.getTime() - offset * 60_000).toISOString().slice(0, 16);
}

function setLoading(button, loading, label) {
    button.disabled = loading;
    button.querySelector("span").textContent = label;
}

function showMessage(element, message) {
    element.textContent = message;
}

function clearMessage(element) {
    element.textContent = "";
}

function showToast(message) {
    window.clearTimeout(toastTimer);
    toast.textContent = message;
    toast.classList.add("is-visible");
    toastTimer = window.setTimeout(() => toast.classList.remove("is-visible"), 2400);
}
