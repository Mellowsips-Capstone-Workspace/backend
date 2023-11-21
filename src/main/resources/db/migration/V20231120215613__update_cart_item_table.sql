ALTER TABLE cart_item
    ADD COLUMN is_bought BOOLEAN DEFAULT FALSE;

UPDATE cart_item SET is_bought = TRUE WHERE cart_id::text IN (SELECT jsonb_extract_path_text(details, 'id') FROM "order");