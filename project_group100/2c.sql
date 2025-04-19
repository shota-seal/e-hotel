-- Query 1: Count of available rooms per area (city) - aggregate query
SELECT h.city, COUNT(r.room_number) AS available_rooms
FROM Hotel h
JOIN Room r ON h.hotel_name = r.hotel_name
WHERE r.status = 'available'
GROUP BY h.city;

-- Query 2: Search for rooms belonging to a specific hotel chain (Hilton) - nested query
SELECT r.room_number, r.hotel_name, r.price
FROM Room r
WHERE r.hotel_name IN (
    SELECT hotel_name
    FROM Hotel
    WHERE chain_name = 'Hilton'
);

-- Query 3: Count number of rooms in 5-star hotels
SELECT h.hotel_name, h.star_rating, COUNT(r.room_number) AS room_count
FROM Hotel h
JOIN Room r ON h.hotel_name = r.hotel_name
WHERE h.star_rating = 5
GROUP BY h.hotel_name, h.star_rating;

-- Query 4: Number of rentals per hotel - aggregate query
SELECT hotel_name, COUNT(*) AS rental_count
FROM Renting
GROUP BY hotel_name;
