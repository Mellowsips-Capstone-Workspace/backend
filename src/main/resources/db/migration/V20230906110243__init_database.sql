CREATE TYPE user_type AS ENUM ('ADMIN', 'EMPLOYEE', 'CUSTOMER');
CREATE CAST (CHARACTER VARYING AS user_type) WITH INOUT AS ASSIGNMENT;

CREATE TYPE auth_provider_type AS ENUM ('PHONE', 'USERNAME');
CREATE CAST (CHARACTER VARYING AS auth_provider_type) WITH INOUT AS ASSIGNMENT;

CREATE TABLE IF NOT EXISTS "user"
(
    id           uuid         NOT NULL,
    username     VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    phone        VARCHAR(255),
    email        VARCHAR(255),
    is_verified  BOOLEAN DEFAULT FALSE,
    avatar       VARCHAR(255),
    type         user_type,
    provider     auth_provider_type,
    created_by   VARCHAR(255),
    created_at   TIMESTAMP(6) NOT NULL,
    updated_by   VARCHAR(255),
    updated_at   TIMESTAMP(6) NOT NULL,
    is_deleted   BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS receiver_profile
(
    id         uuid         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    phone      VARCHAR(255) NOT NULL,
    user_id    uuid         NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP(6) NOT NULL,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP(6) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS receiver_profile
    ADD CONSTRAINT FK_user_receiver_profile FOREIGN KEY (user_id) REFERENCES "user";