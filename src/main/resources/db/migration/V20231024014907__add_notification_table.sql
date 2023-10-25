CREATE TABLE IF NOT EXISTS notification
(
    id                uuid         NOT NULL,
    receiver          VARCHAR(255) NOT NULL,
    key               VARCHAR(255) NOT NULL,
    subject           VARCHAR(255) NOT NULL,
    short_description VARCHAR(255),
    content           VARCHAR      NOT NULL,
    metadata          jsonb,
    is_seen           BOOLEAN DEFAULT FALSE,
    seen_at           timestamptz,
    created_at        timestamptz  NOT NULL,
    is_deleted        BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);