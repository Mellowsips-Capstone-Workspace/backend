CREATE TABLE voucher
(
    id                  uuid              NOT NULL,
    quantity            INTEGER           NOT NULL,
    discount_type       VARCHAR(255)      NOT NULL CHECK ( discount_type IN ('CASH', 'PERCENT')),
    start_date          timestamptz       NOT NULL,
    end_date            timestamptz,
    max_uses_per_user   INTEGER           NOT NULL,
    max_discount_amount BIGINT,
    min_order_amount    BIGINT            NOT NULL,
    code                VARCHAR(9) UNIQUE NOT NULL,
    partner_id          VARCHAR(255),
    store_id            VARCHAR(255),
    created_by          VARCHAR(255),
    created_at          timestamptz       NOT NULL,
    updated_by          VARCHAR(255),
    updated_at          timestamptz       NOT NULL,
    is_deleted          BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

CREATE TABLE voucher_order
(
    id              uuid         NOT NULL,
    description     VARCHAR(255) NOT NULL,
    discount_amount BIGINT       NOT NULL,
    status          VARCHAR(255) NOT NULL CHECK ( status IN ('PENDING', 'SUCCESS', 'REVOKED')),
    source          VARCHAR(255) NOT NULL CHECK ( source IN ('SYSTEM', 'STORE')),
    voucher_id      uuid         NOT NULL,
    order_id        uuid         NOT NULL,
    created_by      VARCHAR(255),
    created_at      timestamptz  NOT NULL,
    updated_by      VARCHAR(255),
    updated_at      timestamptz  NOT NULL,
    is_deleted      BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (voucher_id) REFERENCES voucher (id),
    FOREIGN KEY (order_id) REFERENCES "order" (id)
);