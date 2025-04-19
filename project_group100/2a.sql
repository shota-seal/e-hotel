DROP TABLE IF EXISTS Renting_archive CASCADE;
DROP TABLE IF EXISTS Booking_archive CASCADE;
DROP TABLE IF EXISTS Renting CASCADE;
DROP TABLE IF EXISTS Booking CASCADE;
DROP TABLE IF EXISTS Customer CASCADE;
DROP TABLE IF EXISTS Employee CASCADE;
DROP TABLE IF EXISTS Room CASCADE;
DROP TABLE IF EXISTS Hotel CASCADE;
DROP TABLE IF EXISTS Hotel_Chain CASCADE;
CREATE TABLE Hotel_Chain (
    chain_name VARCHAR(100) PRIMARY KEY,
    country VARCHAR(50),
    city VARCHAR(50),
    street_number VARCHAR(10),
    unit_number VARCHAR(10),
    zip_code VARCHAR(10),
    number_of_hotels INT CHECK (number_of_hotels >= 0),
    contact_email VARCHAR(100),
    phone_number VARCHAR(20)
);


CREATE TABLE Hotel (
    hotel_name VARCHAR(100) PRIMARY KEY,
    chain_name VARCHAR(100) NOT NULL,
    country VARCHAR(50),
    city VARCHAR(50),
    street_number VARCHAR(10),
    unit_number VARCHAR(10),
    zip_code VARCHAR(10),
    star_rating INT CHECK (star_rating BETWEEN 1 AND 5),
    number_of_rooms INT CHECK (number_of_rooms >= 0),
    contact_email VARCHAR(100),
    phone_number VARCHAR(20),
    FOREIGN KEY (chain_name) REFERENCES Hotel_Chain(chain_name) ON DELETE CASCADE
);

CREATE TABLE Room (
    room_number INT,
    hotel_name VARCHAR(100),
    price DECIMAL(10, 2) CHECK (price >= 0),
    amenities TEXT, -- : "TV, air condition, fridge"
    capacity VARCHAR(20) CHECK (capacity IN ('single', 'double', 'triple', 'family')),
    view_type VARCHAR(20) CHECK (view_type IN ('sea', 'mountain', 'none')),
    expandable BOOLEAN DEFAULT FALSE, 
    issues TEXT, -- : "broken window"
    status VARCHAR(20) CHECK (status IN ('available', 'booked', 'rented', 'maintenance')) DEFAULT 'available',
    PRIMARY KEY (room_number, hotel_name),
    FOREIGN KEY (hotel_name) REFERENCES Hotel(hotel_name) ON DELETE CASCADE
);


CREATE TABLE Employee (
    SSN VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(50),
    mid_name VARCHAR(50),
    last_name VARCHAR(50),
    country VARCHAR(50),
    city VARCHAR(50),
    street_number VARCHAR(10),
    unit_number VARCHAR(10),
    zip_code VARCHAR(10),
    role VARCHAR(50),
    hotel_name VARCHAR(100),
    manager_SSN VARCHAR(20),
    FOREIGN KEY (hotel_name) REFERENCES Hotel(hotel_name) ON DELETE SET NULL,
    FOREIGN KEY (manager_SSN) REFERENCES Employee(SSN) ON DELETE SET NULL
);


CREATE TABLE Customer (
    customer_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    mid_name VARCHAR(50),
    last_name VARCHAR(50),
    country VARCHAR(50),
    city VARCHAR(50),
    street_number VARCHAR(10),
    unit_number VARCHAR(10),
    zip_code VARCHAR(10),
    ID_type VARCHAR(20) CHECK (ID_type IN ('SSN', 'SIN', 'driving_license')),
    registration_date DATE DEFAULT CURRENT_DATE
);


CREATE TABLE Booking (
    booking_number SERIAL PRIMARY KEY,
    BookingDate DATE DEFAULT CURRENT_DATE,
    CheckInDate DATE NOT NULL,
    CheckOutDate DATE NOT NULL,
    customer_id INT,
    room_number INT,
    hotel_name VARCHAR(100),
    chain_name VARCHAR(100),
    CONSTRAINT valid_dates CHECK (CheckInDate < CheckOutDate),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (room_number, hotel_name) REFERENCES Room(room_number, hotel_name) ON DELETE SET NULL,
    FOREIGN KEY (chain_name) REFERENCES Hotel_Chain(chain_name) ON DELETE SET NULL
);

CREATE TABLE Renting (
    renting_number SERIAL PRIMARY KEY,
    rent_date DATE DEFAULT CURRENT_DATE,
    checkin_date DATE NOT NULL,
    checkout_date DATE NOT NULL,
    customer_id INT,
    room_number INT,
    hotel_name VARCHAR(100),
    chain_name VARCHAR(100),
    employee_SSN VARCHAR(20),
    CONSTRAINT valid_renting_dates CHECK (checkin_date < checkout_date),
    FOREIGN KEY (customer_id) REFERENCES Customer(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (room_number, hotel_name) REFERENCES Room(room_number, hotel_name) ON DELETE SET NULL,
    FOREIGN KEY (chain_name) REFERENCES Hotel_Chain(chain_name) ON DELETE SET NULL,
    FOREIGN KEY (employee_SSN) REFERENCES Employee(SSN) ON DELETE SET NULL
);


CREATE TABLE Booking_archive (
    booking_number INT PRIMARY KEY,
    BookingDate DATE,
    CheckInDate DATE,
    CheckOutDate DATE,
    customer_id INT,
    room_number INT,
    hotel_name VARCHAR(100),
    chain_name VARCHAR(100)
);

CREATE TABLE Renting_archive (
    renting_number INT PRIMARY KEY,
    rent_date DATE,
    checkin_date DATE,
    checkout_date DATE,
    customer_id INT,
    room_number INT,
    hotel_name VARCHAR(100),
    chain_name VARCHAR(100),
    employee_SSN VARCHAR(20),
    CONSTRAINT valid_renting_archive_dates CHECK (checkin_date < checkout_date)
);

