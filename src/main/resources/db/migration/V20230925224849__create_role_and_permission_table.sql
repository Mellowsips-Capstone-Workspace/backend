CREATE TABLE IF NOT EXISTS role
(
    id              uuid         NOT NULL,
    name            VARCHAR(255) NOT NULL,
    description     VARCHAR(255),
    is_allowed_edit BOOLEAN DEFAULT TRUE,
    partner_id      uuid,
    created_by      VARCHAR(255),
    created_at      TIMESTAMP(6) NOT NULL,
    updated_by      VARCHAR(255),
    updated_at      TIMESTAMP(6) NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS permission
(
    id          VARCHAR(255) NOT NULL UNIQUE,
    name        VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS role_permission
(
    role_id       uuid,
    permission_id VARCHAR(255),
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role (id),
    FOREIGN KEY (permission_id) REFERENCES permission (id)
);

CREATE TABLE IF NOT EXISTS user_role
(
    role_id uuid,
    user_id uuid,
    PRIMARY KEY (role_id, user_id),
    FOREIGN KEY (role_id) REFERENCES role (id),
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);