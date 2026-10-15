CREATE TABLE customers (id BIGINT PRIMARY KEY, email VARCHAR(120), display_name VARCHAR(200), city VARCHAR(100), notes VARCHAR(500), amount DECIMAL(12,2));
CREATE TABLE orders (id BIGINT PRIMARY KEY, reference VARCHAR(120), description VARCHAR(200), status VARCHAR(100), notes VARCHAR(500), amount DECIMAL(12,2));
CREATE TABLE inventory (id BIGINT PRIMARY KEY, sku VARCHAR(120), product_name VARCHAR(200), warehouse VARCHAR(100), notes VARCHAR(500), amount DECIMAL(12,2));
CREATE TABLE invoices (id BIGINT PRIMARY KEY, invoice_number VARCHAR(120), description VARCHAR(200), status VARCHAR(100), notes VARCHAR(500), amount DECIMAL(12,2));
CREATE TABLE shipments (id BIGINT PRIMARY KEY, tracking_number VARCHAR(120), destination VARCHAR(200), carrier VARCHAR(100), notes VARCHAR(500), amount DECIMAL(12,2));
CREATE TABLE tickets (id BIGINT PRIMARY KEY, ticket_number VARCHAR(120), subject VARCHAR(200), priority VARCHAR(100), notes VARCHAR(500), amount DECIMAL(12,2));

