ALTER TABLE grocery_inventories
ADD CONSTRAINT uq_grocery_expiry UNIQUE (grocery_id, expiry_date);