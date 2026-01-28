-- Insert Addresses in Novi Sad
INSERT INTO address (id, street, street_number, city, postal_code, country, latitude, longitude) VALUES
('addr-001', 'Mažestičkog vojevode', '5', 'Novi Sad', '21000', 'Serbia', 45.2516, 19.8369),
('addr-002', 'Sutjeske', '12', 'Novi Sad', '21000', 'Serbia', 45.2672, 19.8372),
('addr-003', 'Mihailova', '45', 'Novi Sad', '21000', 'Serbia', 45.2610, 19.8215),
('addr-004', 'Futoška', '76', 'Novi Sad', '21000', 'Serbia', 45.2500, 19.8450),
('addr-005', 'Dunaveska', '33', 'Novi Sad', '21000', 'Serbia', 45.2580, 19.8300);

-- Insert Admin User
INSERT INTO users (id, first_name, last_name, gender, user_name, email, password, type, phone_number, address_id, profile_picture_url, is_blocked, is_active, dtype) VALUES
('admin-001', 'Admin', 'User', 'MALE', 'admin', 'admin@driverr.com', 'admin', 'ADMIN', '+381611234567', 'addr-001', NULL, false, true, 'User');

-- Insert Vehicles
INSERT INTO vehicles (id, vehicle_model, type, registration_plate, seats, pets_allowed, babies_allowed) VALUES
('vehicle-001', 'BMW 320i', 'STANDARD', 'NS-001-AB', 4, true, true),
('vehicle-002', 'Mercedes-Benz S-Class', 'LUXURY', 'NS-002-XY', 4, false, true),
('vehicle-003', 'Ford Transit Custom', 'VAN', 'NS-003-VAN', 7, true, true),
('vehicle-004', 'Audi A4', 'STANDARD', 'NS-004-CD', 4, true, false),
('vehicle-005', 'BMW 7 Series', 'LUXURY', 'NS-005-LX', 4, false, true);

-- Insert Drivers
INSERT INTO users (id, first_name, last_name, gender, user_name, email, password, type, phone_number, address_id, profile_picture_url, is_blocked, is_active, dtype) VALUES
('driver-001', 'Marko', 'Marković', 'MALE', 'marko.markovic', 'marko@driverr.com', 'password123', 'DRIVER', '+381611111111', 'addr-002', NULL, false, true, 'Driver'),
('driver-002', 'Ana', 'Anić', 'FEMALE', 'ana.anic', 'ana@driverr.com', 'password123', 'DRIVER', '+381622222222', 'addr-003', NULL, false, true, 'Driver'),
('driver-003', 'Petar', 'Petrović', 'MALE', 'petar.petrovic', 'petar@driverr.com', 'password123', 'DRIVER', '+381633333333', 'addr-004', NULL, false, true, 'Driver'),
('driver-004', 'Jovan', 'Jovanović', 'MALE', 'jovan.jovanovic', 'jovan@driverr.com', 'password123', 'DRIVER', '+381644444444', 'addr-005', NULL, false, true, 'Driver');

-- Insert Driver Details with Vehicles
INSERT INTO driver (id, vehicle_id, status) VALUES
('driver-001', 'vehicle-001', 'AVAILABLE'),
('driver-002', 'vehicle-002', 'INACTIVE'),
('driver-003', 'vehicle-003', 'AVAILABLE'),
('driver-004', 'vehicle-004', 'OFFLINE');

-- Insert Passengers (regular users)
INSERT INTO users (id, first_name, last_name, gender, user_name, email, password, type, phone_number, address_id, profile_picture_url, is_blocked, is_active, dtype) VALUES
('user-001', 'Milan', 'Milanović', 'MALE', 'milan.milanovic', 'milan@email.com', 'password123', 'CLIENT', '+381655555555', 'addr-001', NULL, false, true, 'User'),
('user-002', 'Jovana', 'Jovanović', 'FEMALE', 'jovana.jovanovic', 'jovana@email.com', 'password123', 'CLIENT', '+381666666666', 'addr-002', NULL, false, true, 'User');
