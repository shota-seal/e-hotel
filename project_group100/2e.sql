CREATE INDEX idx_hotel_city ON Hotel(city);

CREATE INDEX idx_room_hotel_name ON Room(hotel_name);

CREATE INDEX idx_renting_room ON Renting(room_number, hotel_name);
