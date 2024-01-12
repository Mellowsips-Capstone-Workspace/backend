ALTER TABLE "transaction"
    DROP CONSTRAINT transaction_status_chk,
    ADD CONSTRAINT transaction_status_chk CHECK ( status IN ('PENDING', 'SUCCESS', 'EXPIRED', 'FAILED') );