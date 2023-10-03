CREATE TABLE IF NOT EXISTS qr_code
(
    id          uuid         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    store_id    VARCHAR(255),
    created_by  VARCHAR(255),
    created_at  timestamptz  NOT NULL,
    updated_by  VARCHAR(255),
    updated_at  timestamptz  NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);