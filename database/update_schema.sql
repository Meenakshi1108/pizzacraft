-- Select the database first
USE pizza_delivery;

-- Temporarily disable safe updates
SET SQL_SAFE_UPDATES = 0;

-- Clear existing menu data for a fresh start
DELETE FROM order_item_toppings;
DELETE FROM order_items;
DELETE FROM toppings;
DELETE FROM pizzas;
-- Don't delete categories yet, we'll update them

-- Clear categories and reset auto-increment
DELETE FROM categories;
ALTER TABLE categories AUTO_INCREMENT = 1;

-- Add Indian-focused categories
INSERT INTO categories (name, description) VALUES 
('Classic', 'Timeless pizza favorites with an Indian touch'),
('Vegetarian', 'Pure vegetarian pizzas loaded with fresh vegetables'),
('Non-Vegetarian', 'Delicious meat-topped pizzas for non-veg lovers'),
('Tandoori Specials', 'Pizzas with authentic tandoori flavors'),
('Indian Fusion', 'The perfect blend of Italian bases with Indian masala'),
('Cheese Lovers', 'Extra cheesy pizzas with cheese-filled crusts');

-- Add columns to the orders table
ALTER TABLE orders 
ADD COLUMN assigned_to_user_id INT NULL,
ADD COLUMN assigned_at TIMESTAMP NULL,
ADD COLUMN delivered_at TIMESTAMP NULL,
ADD FOREIGN KEY (assigned_to_user_id) REFERENCES users(id);

-- Insert indian-style pizzas
INSERT INTO pizzas (name, description, price, category_id, is_vegetarian, is_available) VALUES 
-- Classic
('Margherita', 'Classic delight with 100% real mozzarella cheese', 199.00, 1, TRUE, TRUE),
('Cheese & Corn', 'Sweet corn kernels with extra cheese', 249.00, 1, TRUE, TRUE),

-- Vegetarian
('Paneer Special', 'Fresh paneer, capsicum, onions with spicy sauce', 299.00, 2, TRUE, TRUE),
('Veggie Paradise', 'Golden corn, black olives, capsicum & red paprika', 269.00, 2, TRUE, TRUE),
('Spicy Veg', 'Onion, capsicum, jalapeños & green chilies for the spice lovers', 249.00, 2, TRUE, TRUE),

-- Non-Vegetarian
('Chicken Supreme', 'Loaded with grilled chicken chunks & vegetables', 349.00, 3, FALSE, TRUE),
('Pepper Barbecue Chicken', 'Pepper barbecue chicken with cheese', 319.00, 3, FALSE, TRUE),
('Chicken Fiesta', 'Grilled chicken with pineapple, jalapeño & extra cheese', 369.00, 3, FALSE, TRUE),

-- Tandoori Specials
('Tandoori Paneer', 'Tandoori paneer with onion, capsicum & red paprika', 329.00, 4, TRUE, TRUE),
('Chicken Tikka', 'Tandoori chicken with onion & green chillies', 349.00, 4, FALSE, TRUE),
('Malai Chicken Tikka', 'Creamy malai chicken tikka with bell peppers', 369.00, 4, FALSE, TRUE),

-- Indian Fusion
('Paneer Makhani', 'Paneer in makhani gravy with capsicum & onions', 329.00, 5, TRUE, TRUE),
('Butter Chicken', 'Classic butter chicken flavor on a pizza base', 369.00, 5, FALSE, TRUE),
('Keema Do Pyaza', 'Spicy minced meat with double onion flavor', 389.00, 5, FALSE, TRUE),

-- Cheese Lovers
('Cheese Burst', 'Liquid cheese stuffed crust with extra cheese toppings', 349.00, 6, TRUE, TRUE),
('Double Cheese Margherita', 'Classic margherita with extra cheese', 299.00, 6, TRUE, TRUE),
('4 Cheese Pizza', 'Blend of mozzarella, cheddar, parmesan & aged cheeses', 399.00, 6, TRUE, TRUE);

-- Clear toppings and reset auto-increment
DELETE FROM toppings;
ALTER TABLE toppings AUTO_INCREMENT = 1;

-- Add Indian-style toppings
INSERT INTO toppings (name, price, is_vegetarian) VALUES 
-- Vegetarian Toppings
('Extra Cheese', 50.00, TRUE),
('Paneer', 60.00, TRUE),
('Mushroom', 35.00, TRUE),
('Onion', 20.00, TRUE),
('Capsicum', 20.00, TRUE),
('Black Olive', 30.00, TRUE),
('Sweet Corn', 25.00, TRUE),
('Jalapeño', 30.00, TRUE),
('Green Chilli', 15.00, TRUE),
('Pineapple', 25.00, TRUE),
('Tomato', 20.00, TRUE),

-- Non-vegetarian Toppings
('Chicken Tikka', 70.00, FALSE),
('Tandoori Chicken', 70.00, FALSE),
('Grilled Chicken', 60.00, FALSE),
('Chicken Keema', 65.00, FALSE),
('Chicken Sausage', 60.00, FALSE);

-- Re-enable safe updates when done
SET SQL_SAFE_UPDATES = 1;