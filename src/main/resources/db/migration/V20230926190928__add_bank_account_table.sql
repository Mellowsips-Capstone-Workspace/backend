CREATE TABLE IF NOT EXISTS bank_account
(
    id              uuid         NOT NULL,
    bank_name       VARCHAR(255) NOT NULL,
    bank_branch     VARCHAR(255) NOT NULL,
    account_name    VARCHAR(255) NOT NULL,
    account_number  VARCHAR(255) NOT NULL,
    identity_images TEXT[],
    partner_id      VARCHAR(255),
    created_by      VARCHAR(255),
    created_at      TIMESTAMP(6) NOT NULL,
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMP(6) NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);