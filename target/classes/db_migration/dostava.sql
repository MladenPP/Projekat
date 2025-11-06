-- Kreiranje baze
CREATE DATABASE IF NOT EXISTS dostava;
USE dostava;

-- --------------------------
-- TABLE: users
-- --------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    expired BOOLEAN DEFAULT FALSE,
    locked BOOLEAN DEFAULT FALSE,
    credentials_expired BOOLEAN DEFAULT FALSE,
    should_change_password BOOLEAN DEFAULT FALSE
);

-- --------------------------
-- TABLE: user_roles (string roles)
-- --------------------------
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY(user_id, role),
    CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- --------------------------
-- TABLE: restaurants
-- --------------------------
CREATE TABLE IF NOT EXISTS restaurants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255)
);

-- --------------------------
-- TABLE: items
-- --------------------------
CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    restaurant_id BIGINT,
    CONSTRAINT fk_item_restaurant FOREIGN KEY(restaurant_id) REFERENCES restaurants(id) ON DELETE SET NULL
);

-- --------------------------
-- TABLE: orders
-- --------------------------
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'PENDING',
    CONSTRAINT fk_order_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- --------------------------
-- TABLE: order_items (many-to-many with quantity)
-- --------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    item_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    CONSTRAINT fk_order FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_item FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE
);


