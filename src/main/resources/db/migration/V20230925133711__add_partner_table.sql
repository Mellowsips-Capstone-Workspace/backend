CREATE TABLE IF NOT EXISTS partner
(
    id            uuid         NOT NULL,
    name          VARCHAR(255),
    logo          VARCHAR(255),
    business_code VARCHAR(255),
    tax_code      VARCHAR(255),
    type          VARCHAR(255) NOT NULL CHECK ( type IN ('PERSONAL', 'HOUSEHOLD', 'ENTERPRISE') ),
    created_by    VARCHAR(255),
    created_at    TIMESTAMP(6) NOT NULL,
    updated_by    VARCHAR(255),
    updated_at    TIMESTAMP(6) NOT NULL,
    is_deleted    BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);