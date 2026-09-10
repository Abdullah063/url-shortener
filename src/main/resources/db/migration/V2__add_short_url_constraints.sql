ALTER TABLE short_urls
    ALTER COLUMN code TYPE VARCHAR(8),
    ALTER COLUMN code SET NOT NULL,
    ALTER COLUMN original_url TYPE VARCHAR(2048),
    ALTER COLUMN original_url SET NOT NULL,
    ALTER COLUMN created_at SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_short_urls_code ON short_urls (code);
