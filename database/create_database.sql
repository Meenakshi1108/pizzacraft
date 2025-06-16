-- Create the pizza delivery database
CREATE DATABASE IF NOT EXISTS pizza_delivery;

-- Use the pizza delivery database
USE pizza_delivery;

-- Create users table for storing customer and staff accounts
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(100) NOT NULL,
  full_name VARCHAR(100) NOT NULL,
  email VARCHAR(100) NOT NULL UNIQUE,
  phone VARCHAR(15),
  role VARCHAR(20) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create table for pizza categories (like Vegetarian, Non-Vegetarian, Specialty)
CREATE TABLE IF NOT EXISTS categories (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  description TEXT
);

-- Create table for pizza menu items
CREATE TABLE IF NOT EXISTS pizzas (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT,
  price DECIMAL(10,2) NOT NULL,
  category_id INT,
  image_url VARCHAR(255),
  is_vegetarian BOOLEAN DEFAULT FALSE,
  is_available BOOLEAN DEFAULT TRUE,
  FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create table for toppings
CREATE TABLE IF NOT EXISTS toppings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  is_vegetarian BOOLEAN DEFAULT FALSE
);

-- Create table for customer orders
CREATE TABLE IF NOT EXISTS orders (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  total_amount DECIMAL(10,2) NOT NULL,
  order_status VARCHAR(20) NOT NULL,
  delivery_address TEXT NOT NULL,
  contact_number VARCHAR(15) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Create table for items within an order
CREATE TABLE IF NOT EXISTS order_items (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT,
  pizza_id INT,
  quantity INT NOT NULL,
  price DECIMAL(10,2) NOT NULL,
  FOREIGN KEY (order_id) REFERENCES orders(id),
  FOREIGN KEY (pizza_id) REFERENCES pizzas(id)
);

-- Create table to track which toppings are added to which order items
CREATE TABLE IF NOT EXISTS order_item_toppings (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_item_id INT,
  topping_id INT,
  FOREIGN KEY (order_item_id) REFERENCES order_items(id),
  FOREIGN KEY (topping_id) REFERENCES toppings(id)
);
