-- FOREIGN KEY & CASCADE settings
ALTER TABLE Hotel
ADD CONSTRAINT hotel_fk_chain FOREIGN KEY (chain_name) 
REFERENCES Hotel_Chain (chain_name) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE Room
ADD CONSTRAINT room_fk_hotel FOREIGN KEY (hotel_name) 
REFERENCES Hotel (hotel_name) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

ALTER TABLE Renting
ADD CONSTRAINT renting_fk_room FOREIGN KEY (room_number, hotel_name) 
REFERENCES Room (room_number, hotel_name) 
ON DELETE CASCADE 
ON UPDATE CASCADE;

-- CHECK constraints
ALTER TABLE Room
ADD CONSTRAINT check_room_price CHECK (price >= 10 AND price <= 10000);

ALTER TABLE Hotel
ADD CONSTRAINT check_star_rating CHECK (star_rating BETWEEN 1 AND 5);

-- TRIGGER 1: Update room status when a rental is made
CREATE OR REPLACE FUNCTION update_room_status_on_renting()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE Room
    SET status = 'rented'
    WHERE room_number = NEW.room_number AND hotel_name = NEW.hotel_name;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER renting_room_status_trigger
AFTER INSERT ON Renting
FOR EACH ROW
EXECUTE FUNCTION update_room_status_on_renting();

-- TRIGGER 2: Log room price changes
CREATE TABLE Room_Price_Log (
    log_id SERIAL PRIMARY KEY,
    room_number INT,
    hotel_name VARCHAR(255),
    old_price DECIMAL(10,2),
    new_price DECIMAL(10,2),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE OR REPLACE FUNCTION log_room_price_changes()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO Room_Price_Log (room_number, hotel_name, old_price, new_price)
    VALUES (OLD.room_number, OLD.hotel_name, OLD.price, NEW.price);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER room_price_change_trigger
BEFORE UPDATE ON Room
FOR EACH ROW
WHEN (OLD.price IS DISTINCT FROM NEW.price)
EXECUTE FUNCTION log_room_price_changes();
