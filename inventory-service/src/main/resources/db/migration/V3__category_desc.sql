ALTER TABLE inventory_categories
    ADD COLUMN description TEXT NOT NULL DEFAULT 'No description provided';

INSERT INTO inventory_categories (name, description) VALUES
  ('Fresh Produce', 'Includes fruits and vegetables that are perishable and usually stored in the fridge.'),
  ('Dairy & Eggs', 'Milk, cheese, butter, yogurt, and eggs. Requires refrigeration.'),
  ('Meat & Seafood', 'All types of raw or cooked meat, fish, and other seafood products.'),
  ('Pantry Staples', 'Long-shelf-life foods like rice, pasta, flour, sugar, and canned goods.'),
  ('Frozen Foods', 'Items stored in the freezer such as frozen meals, vegetables, and ice cream.'),
  ('Snacks & Sweets', 'Chips, cookies, chocolates, candies, and other snack items.'),
  ('Beverages', 'Includes bottled water, juices, soda, coffee, tea, and other drinks.'),
  ('Condiments & Spices', 'Sauces, oils, seasonings, spices, and herbs for cooking.');
