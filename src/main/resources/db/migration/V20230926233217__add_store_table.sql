CREATE TABLE store
(
    id            uuid         NOT NULL,
    name          VARCHAR(255) NOT NULL,
    phone         VARCHAR(255),
    email         VARCHAR(255),
    address       VARCHAR(255) NOT NULL,
    profile_image VARCHAR(255),
    cover_image   VARCHAR(255),
    categories    TEXT[],
    is_active     BOOLEAN,
    is_open       BOOLEAN,
    partner_id    VARCHAR(255),
    created_by    VARCHAR(255),
    created_at    TIMESTAMP(6) NOT NULL,
    updated_by    VARCHAR(255),
    updated_at    TIMESTAMP(6) NOT NULL,
    is_deleted    BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);