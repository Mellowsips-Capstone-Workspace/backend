ALTER TABLE "order"
    ADD COLUMN initial_transaction_method VARCHAR(255) NOT NULL CHECK (initial_transaction_method IN ('ZALO_PAY', 'CASH')),
    ADD CONSTRAINT order_status_chk CHECK (status IN ('PENDING', 'ORDERED', 'REJECTED', 'PROCESSING', 'COMPLETED', 'RECEIVED', 'DECLINED'));