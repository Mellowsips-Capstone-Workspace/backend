CREATE TABLE review
(
    id         uuid         NOT NULL,
    order_id   uuid         NOT NULL,
    point      INTEGER      NOT NULL,
    comment    VARCHAR(255),
    created_by VARCHAR(255),
    created_at TIMESTAMP(6) NOT NULL,
    updated_by VARCHAR(255),
    updated_at TIMESTAMP(6) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (order_id) REFERENCES "order" (id)
);