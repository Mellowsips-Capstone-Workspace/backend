ALTER TABLE menu
    ADD COLUMN name VARCHAR(255) NOT NULL DEFAULT '',
    ADD COLUMN is_active BOOLEAN DEFAULT FALSE,
    ADD COLUMN partner_id VARCHAR(255);

UPDATE menu SET partner_id = (SELECT store.partner_id FROM store WHERE id::text = menu.store_id), is_active = TRUE;