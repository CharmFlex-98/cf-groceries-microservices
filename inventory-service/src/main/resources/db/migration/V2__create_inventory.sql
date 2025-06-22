CREATE TABLE IF NOT EXISTS groceries (
    id BIGSERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    created_by INTEGER NOT NULL,
    item_name TEXT UNIQUE NOT NULL,

    -- each user can only have one same item name
    CONSTRAINT uq_user_item_name UNIQUE (created_by, item_name),
    -- Foreign key on category_id
    CONSTRAINT fk_category_id FOREIGN KEY (category_id)
        REFERENCES inventory_categories(id)
        ON DELETE RESTRICT
);
-- Index on category_id to filter by category
CREATE INDEX IF NOT EXISTS idx_groceries_category_id ON groceries(category_id);


-- user_id here is just a UUID, NOT a foreign key.
CREATE TABLE grocery_inventories (
    id BIGSERIAL PRIMARY KEY,
    grocery_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    expiry_date DATE,

    -- foreign key
    CONSTRAINT fk_grocery_id FOREIGN KEY (grocery_id)
        REFERENCES groceries(id)
        ON DELETE RESTRICT
);
-- Index on expiry_time (for fast range queries or sorting)
CREATE INDEX IF NOT EXISTS idx_inventory_expiry_date ON grocery_inventories(expiry_date);

