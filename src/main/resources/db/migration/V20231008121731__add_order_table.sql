CREATE TABLE IF NOT EXISTS "order"
(
    id          uuid         NOT NULL,
    status      VARCHAR(255) NOT NULL CHECK ( status IN ('ORDERED', 'REJECTED', 'PROCESSING', 'COMPLETED', 'RECEIVED', 'DECLINED') ),
    final_price BIGINT       NOT NULL,
    details     jsonb        NOT NULL,
    store_id    VARCHAR(255),
    partner_id  VARCHAR(255),
    created_by  VARCHAR(255),
    created_at  timestamptz  NOT NULL,
    updated_by  VARCHAR(255),
    updated_at  timestamptz  NOT NULL,
    is_deleted  BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id)
);