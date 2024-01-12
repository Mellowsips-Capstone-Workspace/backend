ALTER TABLE partner
    ADD COLUMN business_identity_issue_date DATE,
    ADD COLUMN business_identity_images     TEXT[];

CREATE TABLE representative
(
    id                   uuid         NOT NULL,
    name                 VARCHAR(255) NOT NULL,
    identity_type        VARCHAR(255) NOT NULL,
    identity_number      VARCHAR(255) NOT NULL,
    identity_issue_date  DATE         NOT NULL,
    address              VARCHAR(255) NOT NULL,
    phone                VARCHAR(255) NOT NULL,
    email                VARCHAR(255) NOT NULL,
    identity_front_image VARCHAR(255),
    identity_back_image  VARCHAR(255),
    partner_id           VARCHAR(255),
    created_by           VARCHAR(255),
    created_at           TIMESTAMP(6) NOT NULL,
    updated_by           VARCHAR(255),
    updated_at           TIMESTAMP(6) NOT NULL,
    is_deleted           BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);