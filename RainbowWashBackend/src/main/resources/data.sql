-- 1. LAUNDRY & CLEANING SERVICES (Populates the laundry_services table)
INSERT INTO laundry_services (name, category, description, price, stock, available)
VALUES
-- Cleaning options (Book Cleaning page)
('Home Cleaning (1 - 2 Bedrooms)', 'Cleaning', 'Standard cleaning for 1-2 bedroom apartments.', 15000.00, 50, true),
('Home Cleaning (3 - 4 Bedrooms)', 'Cleaning', 'Comprehensive cleaning for 3-4 bedroom properties.', 25000.00, 30, true),
('Home Cleaning (5+ Bedrooms / Duplex)', 'Cleaning', 'Full detailed cleaning for large homes and duplexes.', 40000.00, 20, true),
('Office Cleaning (Small Workspace)', 'Cleaning', 'Regular maintenance cleaning for small office workspaces.', 20000.00, 25, true),
('Office Cleaning (Large Suite)', 'Cleaning', 'Thorough cleaning service for large office suites.', 45000.00, 15, true),
('Deep Clean (Standard)', 'Cleaning', 'Detailed deep cleaning covering intense detail work.', 35000.00, 20, true),
('Deep Clean (Heavy Duty / Post-Construction)', 'Cleaning', 'Intensive heavy-duty cleaning for post-construction sites.', 60000.00, 10, true),
-- Laundry options (Order Laundry page)
('Wash & Dry (Per kg)', 'Self Wash', 'Standard everyday washing, drying and neat folding.', 1500.00, 200, true),
('Wash & Iron (Per kg)', 'Self Wash', 'Complete wash, thorough drying, and crisp professional ironing.', 2000.00, 150, true),
('Staff Wash (Standard Load)', 'Staff Wash', 'Handled completely by our professional staff from start to finish.', 3500.00, 80, true),
('Dry Cleaning (Suit / Blazer / Native)', 'Dry Cleaning', 'Specialized care and pressing for formal wear and traditional attire.', 3500.00, 60, true),
('Shoe Cleaning & Care', 'Shoe & Leather', 'Professional cleaning, restoration, and care for footwear.', 4000.00, 40, true),
('Leather Jacket Conditioning', 'Shoe & Leather', 'Conditioning and polish to maintain leather jackets and gear.', 6000.00, 25, true),
('Heavy Duvet Wash (Extra)', 'Add Extras', 'Deep cleaning and sanitization for heavy duvets and comforters.', 5000.00, 35, true);

-- 2. SHOP PRODUCTS (Populates the products table)
-- NOTE: the Product entity only has name, price, stock, status — no category,
-- description, or available columns exist on this table.
INSERT INTO products (name, category, description, price, stock, status)
VALUES
    ('Liquid Detergent 2L', 'Shop', 'High-efficiency liquid detergent for everyday laundry.', 3500.00, 100, 'Active'),
    ('Fabric Softener 1L', 'Shop', 'Long-lasting fragrance and softness for your fabrics.', 2200.00, 80, 'Active'),
    ('Stain Remover Spray', 'Shop', 'Tough on stains, gentle on clothes.', 2800.00, 60, 'Active'),
    ('Powder Detergent 1kg', 'Shop', 'Deep cleaning powder detergent.', 2600.00, 90, 'Active'),
    ('Starch Spray', 'Shop', 'Crisp finish spray for shirts and native wears.', 1800.00, 50, 'Active'),
    ('Laundry Bag (Mesh)', 'Shop', 'Durable mesh laundry bag for delicates.', 1500.00, 40, 'Active'),
    ('Ironing Spray', 'Shop', 'Easy-glide spray for smooth and neat ironing.', 2000.00, 45, 'Active'),
    ('Colour Catcher Sheets', 'Shop', 'Prevents color bleeding during mixed washes.', 3000.00, 70, 'Active');