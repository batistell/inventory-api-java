CREATE TABLE inventory (
    id SERIAL PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL UNIQUE,
    quantity INTEGER NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
