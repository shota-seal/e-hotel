CREATE VIEW AvailableRoomsByCity AS
SELECT h.city, COUNT(r.room_number) AS available_rooms
FROM Hotel h
JOIN Room r ON h.hotel_name = r.hotel_name
WHERE r.status = 'available'
GROUP BY h.city;

CREATE VIEW TotalCapacityByHotel AS
SELECT r.hotel_name, SUM(
    CASE 
        WHEN r.capacity = 'single' THEN 1
        WHEN r.capacity = 'double' THEN 2
        WHEN r.capacity = 'triple' THEN 3
        WHEN r.capacity = 'family' THEN 4
        ELSE 0 
    END
) AS total_capacity
FROM Room r
GROUP BY r.hotel_name;
