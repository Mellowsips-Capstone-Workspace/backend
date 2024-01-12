ALTER TABLE product
    ADD COLUMN menu_id uuid REFERENCES menu(id),
    ADD COLUMN parent_id uuid REFERENCES product(id);