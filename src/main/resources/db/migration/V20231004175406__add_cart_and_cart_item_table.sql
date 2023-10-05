CREATE TABLE IF NOT EXISTS cart
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

CREATE TABLE IF NOT EXISTS cart_item
(
    id         uuid        NOT NULL,
    cart_id    uuid        NOT NULL,
    product_id uuid        NOT NULL,
    quantity   INTEGER     NOT NULL,
    note       VARCHAR(255),
    addons     uuid[],
    created_by VARCHAR(255),
    created_at timestamptz NOT NULL,
    updated_by VARCHAR(255),
    updated_at timestamptz NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS cart_item
    ADD CONSTRAINT FK_cart_cart_item FOREIGN KEY (cart_id) REFERENCES cart,
    ADD CONSTRAINT FK_product_cart_item FOREIGN KEY (product_id) REFERENCES product;