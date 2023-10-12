CREATE TABLE IF NOT EXISTS transaction
(
    id                    uuid         NOT NULL,
    method                VARCHAR(255) NOT NULL CHECK (method IN ('ZALO_PAY', 'CASH')),
    type                  VARCHAR(255) NOT NULL CHECK (type IN ('PURCHASE', 'REFUND')),
    status                VARCHAR(255) NOT NULL CHECK (status IN ('PENDING', 'SUCCESS', 'FAILED')),
    amount                BIGINT       NOT NULL,
    external_payment_info jsonb,
    order_id              uuid         NOT NULL,
    store_id              VARCHAR(255),
    partner_id            VARCHAR(255),
    created_by            VARCHAR(255),
    created_at            timestamptz  NOT NULL,
    updated_by            VARCHAR(255),
    updated_at            timestamptz  NOT NULL,
    is_deleted            BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS transaction
    ADD CONSTRAINT FK_order_transaction FOREIGN KEY (order_id) REFERENCES "order";