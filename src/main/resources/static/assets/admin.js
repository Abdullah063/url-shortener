const linksBody = document.querySelector("#links-body");
const emptyState = document.querySelector("#empty-state");
const adminMessage = document.querySelector("#admin-message");
const totalElements = document.querySelector("#total-elements");
const pagination = document.querySelector("#pagination");
const pageInfo = document.querySelector("#page-info");
const previousPage = document.querySelector("#previous-page");
const nextPage = document.querySelector("#next-page");
const refreshButton = document.querySelector("#refresh-button");
const logoutButton = document.querySelector("#logout-button");
const toast = document.querySelector("#toast");

let currentPage = 0;
let totalPages = 0;
let csrfHeader = "X-XSRF-TOKEN";
let csrfToken = "";
let toastTimer;

start();

async function start() {
    try {
        await loadCsrf();
        await loadLinks();
    } catch (error) {
        showError(error.message);
    }
}

async function loadCsrf() {
    const response = await fetch("/api/v1/admin/csrf");
    const data = await readJson(response);
    if (!response.ok) {
        throw new Error("Güvenlik bilgisi alınamadı.");
    }
    csrfHeader = data.headerName;
    csrfToken = data.token;
}

async function loadLinks(page = currentPage) {
    clearError();
    refreshButton.disabled = true;

    try {
        const response = await fetch(`/api/v1/admin/urls?page=${page}&size=20`);
        const data = await readJson(response);
        if (!response.ok) {
            throw new Error(data.message || "Bağlantılar yüklenemedi.");
        }

        currentPage = data.page;
        totalPages = data.totalPages;
        totalElements.textContent = Number(data.totalElements).toLocaleString("tr-TR");
        renderLinks(data.content);
        renderPagination();
    } finally {
        refreshButton.disabled = false;
    }
}

function renderLinks(links) {
    linksBody.replaceChildren();
    emptyState.hidden = links.length > 0;

    links.forEach((link) => {
        const row = document.createElement("tr");
        row.append(
            createLinkCell(link),
            createTargetCell(link),
            createTextCell("Tıklanma", link.visitCount.toLocaleString("tr-TR"), "visit-count"),
            createStatusCell(link),
            createTextCell("Oluşturulma", formatDate(link.createdAt), "date-cell"),
            createActionsCell(link)
        );
        linksBody.append(row);
    });
}

function createLinkCell(link) {
    const cell = document.createElement("td");
    const anchor = document.createElement("a");
    anchor.className = "short-link";
    anchor.href = `/${link.code}`;
    anchor.target = "_blank";
    anchor.rel = "noreferrer";
    anchor.textContent = `${window.location.host}/${link.code}`;
    cell.dataset.label = "Kısa bağlantı";
    cell.append(anchor);
    return cell;
}

function createTargetCell(link) {
    const cell = document.createElement("td");
    const anchor = document.createElement("a");
    anchor.className = "target-link";
    anchor.href = link.originalUrl;
    anchor.target = "_blank";
    anchor.rel = "noreferrer";
    anchor.title = link.originalUrl;
    anchor.textContent = link.originalUrl;
    cell.dataset.label = "Hedef";
    cell.append(anchor);
    return cell;
}

function createTextCell(label, text, className) {
    const cell = document.createElement("td");
    const value = document.createElement("span");
    value.className = className;
    value.textContent = text;
    cell.dataset.label = label;
    cell.append(value);
    return cell;
}

function createStatusCell(link) {
    const cell = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = "status-badge";

    if (link.expired) {
        badge.classList.add("is-expired");
        badge.textContent = "Süresi doldu";
    } else if (!link.active) {
        badge.classList.add("is-inactive");
        badge.textContent = "Pasif";
    } else {
        badge.textContent = "Aktif";
    }

    cell.dataset.label = "Durum";
    cell.append(badge);
    return cell;
}

function createActionsCell(link) {
    const cell = document.createElement("td");
    const actions = document.createElement("div");
    const statusButton = document.createElement("button");
    const deleteButton = document.createElement("button");

    actions.className = "row-actions";
    statusButton.className = "row-action";
    statusButton.type = "button";
    statusButton.textContent = link.active ? "Pasifleştir" : "Aktifleştir";
    statusButton.addEventListener("click", () => updateStatus(link, statusButton));

    deleteButton.className = "row-action is-danger";
    deleteButton.type = "button";
    deleteButton.textContent = "Sil";
    deleteButton.addEventListener("click", () => deleteLink(link, deleteButton));

    actions.append(statusButton, deleteButton);
    cell.dataset.label = "İşlemler";
    cell.append(actions);
    return cell;
}

async function updateStatus(link, button) {
    button.disabled = true;
    clearError();

    try {
        const response = await fetch(`/api/v1/admin/urls/${link.id}/status`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json",
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({active: !link.active})
        });
        const data = await readJson(response);
        if (!response.ok) {
            throw new Error(data.message || "Bağlantının durumu değiştirilemedi.");
        }
        showToast(data.active ? "Bağlantı aktifleştirildi." : "Bağlantı pasifleştirildi.");
        await loadLinks();
    } catch (error) {
        button.disabled = false;
        showError(error.message);
    }
}

async function deleteLink(link, button) {
    if (!window.confirm(`${link.code} kodlu bağlantı kalıcı olarak silinsin mi?`)) {
        return;
    }

    button.disabled = true;
    clearError();

    try {
        const response = await fetch(`/api/v1/admin/urls/${link.id}`, {
            method: "DELETE",
            headers: {[csrfHeader]: csrfToken}
        });
        if (!response.ok) {
            const data = await readJson(response);
            throw new Error(data.message || "Bağlantı silinemedi.");
        }
        showToast("Bağlantı silindi.");
        const targetPage = linksBody.children.length === 1 && currentPage > 0
            ? currentPage - 1
            : currentPage;
        await loadLinks(targetPage);
    } catch (error) {
        button.disabled = false;
        showError(error.message);
    }
}

function renderPagination() {
    pagination.hidden = totalPages <= 1;
    previousPage.disabled = currentPage === 0;
    nextPage.disabled = currentPage + 1 >= totalPages;
    pageInfo.textContent = `${currentPage + 1} / ${Math.max(totalPages, 1)}`;
}

previousPage.addEventListener("click", () => loadLinks(currentPage - 1).catch(handleError));
nextPage.addEventListener("click", () => loadLinks(currentPage + 1).catch(handleError));
refreshButton.addEventListener("click", () => loadLinks().catch(handleError));

logoutButton.addEventListener("click", async () => {
    logoutButton.disabled = true;
    await fetch("/logout", {
        method: "POST",
        headers: {[csrfHeader]: csrfToken}
    });
    window.location.assign("/");
});

function formatDate(value) {
    return new Intl.DateTimeFormat("tr-TR", {
        dateStyle: "medium",
        timeStyle: "short"
    }).format(new Date(value));
}

async function readJson(response) {
    try {
        return await response.json();
    } catch {
        return {};
    }
}

function handleError(error) {
    showError(error.message);
}

function showError(message) {
    adminMessage.textContent = message;
}

function clearError() {
    adminMessage.textContent = "";
}

function showToast(message) {
    window.clearTimeout(toastTimer);
    toast.textContent = message;
    toast.classList.add("is-visible");
    toastTimer = window.setTimeout(() => toast.classList.remove("is-visible"), 2400);
}
