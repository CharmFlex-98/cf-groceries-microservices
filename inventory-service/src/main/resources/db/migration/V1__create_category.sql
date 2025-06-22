-- Category table
CREATE TABLE IF NOT EXISTS inventory_categories (
    id SERIAL PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);