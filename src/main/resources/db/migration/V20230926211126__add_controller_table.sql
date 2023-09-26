CREATE TABLE controller
(
    id                   uuid         NOT NULL,
    name                 VARCHAR(255),
    phone                VARCHAR(255),
    email                VARCHAR(255),
    partner_id           VARCHAR(255),
    created_by           VARCHAR(255),
    created_at           TIMESTAMP(6) NOT NULL,
    updated_by           VARCHAR(255),
    updated_at           TIMESTAMP(6) NOT NULL,
    is_deleted           BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);