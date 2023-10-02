CREATE TABLE IF NOT EXISTS menu
(
    id         uuid        NOT NULL,
    store_id   VARCHAR(255),
    created_by VARCHAR(255),
    created_at timestamptz NOT NULL,
    updated_by VARCHAR(255),
    updated_at timestamptz NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS menu_section
(
    id         uuid         NOT NULL,
    name       VARCHAR(255) NOT NULL,
    "order"    INTEGER      NOT NULL,
    menu_id    uuid         NOT NULL,
    created_by VARCHAR(255),
    created_at timestamptz  NOT NULL,
    updated_by VARCHAR(255),
    updated_at timestamptz  NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS menu_section
    ADD CONSTRAINT FK_menu_menu_section FOREIGN KEY (menu_id) REFERENCES menu;

CREATE TABLE IF NOT EXISTS product
(
    id          uuid         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    price       BIGINT       NOT NULL,
    cover_image VARCHAR(255),
    categories  VARCHAR(255)[],
    is_sold_out BOOLEAN DEFAULT FALSE,
    store_id    VARCHAR(255),
    partner_id  VARCHAR(255),
    created_by  VARCHAR(255),
    created_at  timestamptz  NOT NULL,
    updated_by  VARCHAR(255),
    updated_at  timestamptz  NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS menu_section_product
(
    product_id      uuid,
    menu_section_id uuid,
    PRIMARY KEY (product_id, menu_section_id),
    FOREIGN KEY (product_id) REFERENCES product (id),
    FOREIGN KEY (menu_section_id) REFERENCES menu_section (id)
);

CREATE TABLE IF NOT EXISTS product_option_section
(
    id                  uuid         NOT NULL,
    name                VARCHAR(255) NOT NULL,
    "order"             INTEGER      NOT NULL,
    is_required         BOOLEAN,
    max_allowed_choices INTEGER,
    product_id          uuid         NOT NULL,
    created_by          VARCHAR(255),
    created_at          timestamptz  NOT NULL,
    updated_by          VARCHAR(255),
    updated_at          timestamptz  NOT NULL,
    is_deleted          BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS product_option_section
    ADD CONSTRAINT FK_product_product_option_section FOREIGN KEY (product_id) REFERENCES product;

CREATE TABLE IF NOT EXISTS product_addon
(
    id                        uuid         NOT NULL,
    name                      VARCHAR(255) NOT NULL,
    price                     BIGINT       NOT NULL,
    is_sold_out               BOOLEAN DEFAULT FALSE,
    product_option_section_id uuid         NOT NULL,
    created_by                VARCHAR(255),
    created_at                timestamptz  NOT NULL,
    updated_by                VARCHAR(255),
    updated_at                timestamptz  NOT NULL,
    is_deleted                BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS product_addon
    ADD CONSTRAINT FK_product_option_section_product_addon FOREIGN KEY (product_option_section_id) REFERENCES product_option_section;