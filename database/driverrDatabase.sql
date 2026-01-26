CREATE DATABASE "Driverr"
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'English_United States.1252'
    LC_CTYPE = 'English_United States.1252'
    LOCALE_PROVIDER = 'libc'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

COMMENT ON DATABASE "Driverr"
    IS 'Database created for Driverr project';

--Tables

CREATE TABLE address (
    id VARCHAR(36) PRIMARY KEY,
    street VARCHAR(100) NOT NULL,
    street_number VARCHAR(20) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(100) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL
);

CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    gender VARCHAR(20),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    phone_number VARCHAR(30),
    address_id VARCHAR(36),
    profile_picture_url TEXT,
    is_blocked BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,

    CONSTRAINT fk_user_address
        FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE vehicle (
    id VARCHAR(36) PRIMARY KEY,
    vehicle_model VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    registration_plate VARCHAR(30) UNIQUE NOT NULL,
    seats INT NOT NULL,
    pets_allowed BOOLEAN NOT NULL,
    babies_allowed BOOLEAN NOT NULL
);

CREATE TABLE driver (
    id VARCHAR(36) PRIMARY KEY,
    vehicle_id VARCHAR(36),
    status VARCHAR(20) NOT NULL,

    CONSTRAINT fk_driver_user
        FOREIGN KEY (id) REFERENCES users(id),

    CONSTRAINT fk_driver_vehicle
        FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);

CREATE TABLE ride (
    id VARCHAR(36) PRIMARY KEY,
    pickup_address_id VARCHAR(36) NOT NULL,
    destination_address_id VARCHAR(36) NOT NULL,
    driver_id VARCHAR(36),
    status VARCHAR(30) NOT NULL,
    price DOUBLE PRECISION NOT NULL,

    CONSTRAINT fk_ride_pickup
        FOREIGN KEY (pickup_address_id) REFERENCES address(id),

    CONSTRAINT fk_ride_destination
        FOREIGN KEY (destination_address_id) REFERENCES address(id),

    CONSTRAINT fk_ride_driver
        FOREIGN KEY (driver_id) REFERENCES driver(id)
);

CREATE TABLE ride_stops (
    ride_id VARCHAR(36),
    address_id VARCHAR(36),
    stop_order INT NOT NULL,

    PRIMARY KEY (ride_id, stop_order),

    CONSTRAINT fk_rs_ride
        FOREIGN KEY (ride_id) REFERENCES ride(id),

    CONSTRAINT fk_rs_address
        FOREIGN KEY (address_id) REFERENCES address(id)
);

CREATE TABLE ride_timestamps (
    id SERIAL PRIMARY KEY,
    ride_id VARCHAR(36) NOT NULL,
    timestamp TIMESTAMP NOT NULL,

    CONSTRAINT fk_rt_ride
        FOREIGN KEY (ride_id) REFERENCES ride(id)
);

CREATE TABLE activation_token (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_activation_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE profile_change_request (
    id UUID PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    field_name VARCHAR(100) NOT NULL,
    old_value TEXT,
    new_value TEXT,
    status VARCHAR(20) NOT NULL, -- PENDING, APPROVED, REJECTED
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_profile_change_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE favorite_route (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    pickup_address_id VARCHAR(255) NOT NULL,
    destination_address_id VARCHAR(255) NOT NULL,

    CONSTRAINT fk_favorite_route_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favorite_pickup
        FOREIGN KEY (pickup_address_id)
        REFERENCES address(id),

    CONSTRAINT fk_favorite_destination
        FOREIGN KEY (destination_address_id)
        REFERENCES address(id)
);


CREATE TABLE favorite_route_stops (
    id VARCHAR(255) PRIMARY KEY,
    favorite_route_id VARCHAR(255) NOT NULL,
    address_id VARCHAR(255) NOT NULL,
    stop_order INT NOT NULL,

    CONSTRAINT fk_favorite_route
        FOREIGN KEY (favorite_route_id)
        REFERENCES favorite_route(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favorite_route_address
        FOREIGN KEY (address_id)
        REFERENCES address(id)
);

--Insert Data