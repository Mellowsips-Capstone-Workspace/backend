ALTER TABLE voucher_order
    DROP CONSTRAINT voucher_order_source_check,
    ADD CONSTRAINT voucher_order_source_chk CHECK ( source IN ('SYSTEM', 'BUSINESS') );