-- Database is already created via POSTGRES_DB environment variable
-- Connect to the platinumrx database
-- \c platinumrx;

-- Order Transactions Table
CREATE TABLE IF NOT EXISTS order_transactions (
    order_id VARCHAR(255) PRIMARY KEY,
    amount DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    gateway VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on status for faster queries
CREATE INDEX IF NOT EXISTS idx_order_transactions_status ON order_transactions(status);
CREATE INDEX IF NOT EXISTS idx_order_transactions_gateway ON order_transactions(gateway);
CREATE INDEX IF NOT EXISTS idx_order_transactions_created_at ON order_transactions(created_at);

-- Payment Gateway Status Table
CREATE TABLE IF NOT EXISTS payment_gateway_status (
    id BIGSERIAL PRIMARY KEY,
    gateway VARCHAR(50) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on gateway for faster lookups
CREATE INDEX IF NOT EXISTS idx_payment_gateway_status_gateway ON payment_gateway_status(gateway);

-- Insert initial gateway statuses
INSERT INTO payment_gateway_status (gateway, status, last_updated)
VALUES 
    ('Razorpay', 'ENABLED', CURRENT_TIMESTAMP),
    ('PayU', 'ENABLED', CURRENT_TIMESTAMP),
    ('Cashfree', 'ENABLED', CURRENT_TIMESTAMP)
ON CONFLICT (gateway) DO NOTHING;

-- Function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to auto-update updated_at on order_transactions
CREATE TRIGGER update_order_transactions_updated_at BEFORE UPDATE
    ON order_transactions FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
