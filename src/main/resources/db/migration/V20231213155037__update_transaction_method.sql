ALTER TABLE "order"
    DROP CONSTRAINT order_initial_transaction_method_check,
    ADD CONSTRAINT order_initial_transaction_method_chk CHECK (initial_transaction_method IN ('ZALO_PAY', 'CASH', 'NONE'));

ALTER TABLE "transaction"
DROP CONSTRAINT transaction_method_check,
    ADD CONSTRAINT transaction_method_chk CHECK ("method" IN ('ZALO_PAY', 'CASH', 'NONE'));