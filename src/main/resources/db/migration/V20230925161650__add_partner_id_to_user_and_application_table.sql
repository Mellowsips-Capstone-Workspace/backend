ALTER TABLE "user"
    ADD COLUMN partner_id uuid,
    ADD COLUMN store_id uuid;

ALTER TABLE application
    ADD COLUMN partner_id uuid,
    ADD COLUMN store_id uuid;