ALTER TABLE "order"
    DROP CONSTRAINT order_status_chk,
    ADD CONSTRAINT order_status_chk CHECK ( status IN ('PENDING', 'ORDERED', 'CANCELED', 'REJECTED', 'PROCESSING', 'COMPLETED', 'RECEIVED', 'DECLINED', 'EXPIRED') );

ALTER TABLE "transaction"
    DROP CONSTRAINT transaction_status_check,
    ADD CONSTRAINT transaction_status_chk CHECK ( status IN ('PENDING', 'SUCCESS', 'EXPIRED') );