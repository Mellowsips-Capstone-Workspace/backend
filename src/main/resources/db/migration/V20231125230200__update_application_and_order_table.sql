ALTER TABLE application
    ADD COLUMN reject_reason VARCHAR(255);

ALTER TABLE "order"
    ADD COLUMN reject_reason VARCHAR(255);