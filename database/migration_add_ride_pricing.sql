-- Migration script: add admin-configurable ride pricing per vehicle type

CREATE TABLE IF NOT EXISTS ride_pricing (
    vehicle_type VARCHAR(20) PRIMARY KEY,
    base_price DOUBLE PRECISION NOT NULL CHECK (base_price >= 0),
    price_per_km DOUBLE PRECISION NOT NULL CHECK (price_per_km >= 0),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO ride_pricing (vehicle_type, base_price, price_per_km)
VALUES
    ('STANDARD', 300, 120),
    ('LUXURY', 500, 120),
    ('VAN', 400, 120)
ON CONFLICT (vehicle_type) DO NOTHING;
