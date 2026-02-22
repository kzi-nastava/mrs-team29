-- Migration: Add driver_inconsistency_note table for passenger reporting (2.6.2)

CREATE TABLE IF NOT EXISTS driver_inconsistency_note (
    id VARCHAR(255) PRIMARY KEY,
    ride_id VARCHAR(255) NOT NULL,
    passenger_id VARCHAR(255) NOT NULL,
    note_text TEXT NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    FOREIGN KEY (ride_id) REFERENCES ride(id) ON DELETE CASCADE,
    FOREIGN KEY (passenger_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_inconsistency_note_ride ON driver_inconsistency_note(ride_id);
CREATE INDEX idx_inconsistency_note_passenger ON driver_inconsistency_note(passenger_id);
