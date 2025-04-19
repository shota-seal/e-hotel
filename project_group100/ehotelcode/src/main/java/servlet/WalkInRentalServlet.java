package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet("/WalkInRentalServlet")
public class WalkInRentalServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hotelName = request.getParameter("hotel_name");
        String checkin = request.getParameter("checkin_date");
        String checkout = request.getParameter("checkout_date");


        response.setContentType("application/json");
        PrintWriter out = response.getWriter();


        if (hotelName == null || checkin == null || checkout == null ||
                hotelName.isEmpty() || checkin.isEmpty() || checkout.isEmpty()) {
            out.print("{\"error\": \"Hotel name, check-in date, and check-out date are required.\"}");
            out.flush();
            return;
        }


        java.sql.Date checkinDate;
        java.sql.Date checkoutDate;
        try {
            LocalDate checkinLocalDate = LocalDate.parse(checkin);
            LocalDate checkoutLocalDate = LocalDate.parse(checkout);
            if (checkinLocalDate.isAfter(checkoutLocalDate)) {
                out.print("{\"error\": \"Check-in date must be before check-out date.\"}");
                out.flush();
                return;
            }
            checkinDate = java.sql.Date.valueOf(checkinLocalDate);
            checkoutDate = java.sql.Date.valueOf(checkoutLocalDate);
        } catch (DateTimeParseException e) {
            out.print("{\"error\": \"Invalid date format. Please use YYYY-MM-DD. Received: checkin=" + checkin + ", checkout=" + checkout + "\"}");
            out.flush();
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();


            String availableRoomsSql = """
                SELECT r.room_number, r.hotel_name
                FROM Room r
                WHERE r.status = 'available'
                AND NOT EXISTS (
                    SELECT 1
                    FROM Booking b
                    WHERE b.hotel_name = r.hotel_name
                    AND b.room_number = r.room_number
                    AND (
                        (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                        OR (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                        OR (b.CheckInDate >= ? AND b.CheckOutDate <= ?)
                    )
                )
                AND NOT EXISTS (
                    SELECT 1
                    FROM Renting rt
                    WHERE rt.hotel_name = r.hotel_name
                    AND rt.room_number = r.room_number
                    AND (
                        (rt.checkin_date <= ? AND rt.checkout_date >= ?)
                        OR (rt.checkin_date <= ? AND rt.checkout_date >= ?)
                        OR (rt.checkin_date >= ? AND rt.checkout_date <= ?)
                    )
                )
                AND r.hotel_name = ?
            """;
            PreparedStatement availableStmt = conn.prepareStatement(availableRoomsSql);
            availableStmt.setDate(1, checkoutDate);
            availableStmt.setDate(2, checkinDate);
            availableStmt.setDate(3, checkoutDate);
            availableStmt.setDate(4, checkinDate);
            availableStmt.setDate(5, checkinDate);
            availableStmt.setDate(6, checkoutDate);
            availableStmt.setDate(7, checkoutDate);
            availableStmt.setDate(8, checkinDate);
            availableStmt.setDate(9, checkoutDate);
            availableStmt.setDate(10, checkinDate);
            availableStmt.setDate(11, checkinDate);
            availableStmt.setDate(12, checkoutDate);
            availableStmt.setString(13, hotelName);
            ResultSet availableRs = availableStmt.executeQuery();

            List<Map<String, String>> availableRooms = new ArrayList<>();
            while (availableRs.next()) {
                Map<String, String> room = new HashMap<>();
                room.put("room_number", availableRs.getString("room_number"));
                room.put("hotel_name", availableRs.getString("hotel_name"));
                availableRooms.add(room);
            }


            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < availableRooms.size(); i++) {
                Map<String, String> room = availableRooms.get(i);
                json.append("{\"room_number\": \"").append(room.get("room_number")).append("\", \"hotel_name\": \"").append(room.get("hotel_name")).append("\"}");
                if (i < availableRooms.size() - 1) {
                    json.append(",");
                }
            }
            json.append("]");
            out.print(json.toString());
            out.flush();

        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"error\": \"Database error: " + e.getMessage() + "\"}");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\": \"Unexpected error: " + e.getMessage() + "\"}");
            out.flush();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String employeeSSN = (String) session.getAttribute("employee_ssn");

        if (employeeSSN == null) {
            request.setAttribute("error", "Employee not logged in.");
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            return;
        }

        String customerIdStr = request.getParameter("customer_id");
        String roomNumberStr = request.getParameter("room_number");
        String hotelName = request.getParameter("hotel_name");
        String checkin = request.getParameter("checkin_date");
        String checkout = request.getParameter("checkout_date");


        if (customerIdStr == null || roomNumberStr == null || hotelName == null || checkin == null || checkout == null ||
                customerIdStr.isEmpty() || roomNumberStr.isEmpty() || hotelName.isEmpty() || checkin.isEmpty() || checkout.isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            return;
        }


        java.sql.Date checkinDate;
        java.sql.Date checkoutDate;
        try {
            LocalDate checkinLocalDate = LocalDate.parse(checkin);
            LocalDate checkoutLocalDate = LocalDate.parse(checkout);
            if (checkinLocalDate.isAfter(checkoutLocalDate)) {
                request.setAttribute("error", "Check-in date must be before check-out date.");
                request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
                return;
            }
            checkinDate = java.sql.Date.valueOf(checkinLocalDate);
            checkoutDate = java.sql.Date.valueOf(checkoutLocalDate);
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD.");
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);


            String checkRoomSql = "SELECT status FROM Room WHERE room_number = ? AND hotel_name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkRoomSql);
            checkStmt.setInt(1, Integer.parseInt(roomNumberStr));
            checkStmt.setString(2, hotelName);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                throw new SQLException("Room not found.");
            }
            String roomStatus = rs.getString("status");
            if (!"available".equals(roomStatus)) {
                throw new SQLException("Room is not available.");
            }


            String checkBookingSql = """
                SELECT 1
                FROM Booking b
                WHERE b.hotel_name = ?
                AND b.room_number = ?
                AND (
                    (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                    OR (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                    OR (b.CheckInDate >= ? AND b.CheckOutDate <= ?)
                )
            """;
            PreparedStatement checkBookingStmt = conn.prepareStatement(checkBookingSql);
            checkBookingStmt.setString(1, hotelName);
            checkBookingStmt.setInt(2, Integer.parseInt(roomNumberStr));
            checkBookingStmt.setDate(3, checkoutDate);
            checkBookingStmt.setDate(4, checkinDate);
            checkBookingStmt.setDate(5, checkoutDate);
            checkBookingStmt.setDate(6, checkinDate);
            checkBookingStmt.setDate(7, checkinDate);
            checkBookingStmt.setDate(8, checkoutDate);
            ResultSet bookingRs = checkBookingStmt.executeQuery();

            boolean hasConflict = bookingRs.next();


            if (!hasConflict) {
                String checkRentingSql = """
                    SELECT 1
                    FROM Renting r
                    WHERE r.hotel_name = ?
                    AND r.room_number = ?
                    AND (
                        (r.checkin_date <= ? AND r.checkout_date >= ?)
                        OR (r.checkin_date <= ? AND r.checkout_date >= ?)
                        OR (r.checkin_date >= ? AND r.checkout_date <= ?)
                    )
                """;
                PreparedStatement checkRentingStmt = conn.prepareStatement(checkRentingSql);
                checkRentingStmt.setString(1, hotelName);
                checkRentingStmt.setInt(2, Integer.parseInt(roomNumberStr));
                checkRentingStmt.setDate(3, checkoutDate);
                checkRentingStmt.setDate(4, checkinDate);
                checkRentingStmt.setDate(5, checkoutDate);
                checkRentingStmt.setDate(6, checkinDate);
                checkRentingStmt.setDate(7, checkinDate);
                checkRentingStmt.setDate(8, checkoutDate);
                ResultSet rentingRs = checkRentingStmt.executeQuery();

                hasConflict = rentingRs.next();
            }

            if (hasConflict) {

                String availableRoomsSql = """
                    SELECT r.room_number, r.hotel_name
                    FROM Room r
                    WHERE r.status = 'available'
                    AND NOT EXISTS (
                        SELECT 1
                        FROM Booking b
                        WHERE b.hotel_name = r.hotel_name
                        AND b.room_number = r.room_number
                        AND (
                            (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                            OR (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                            OR (b.CheckInDate >= ? AND b.CheckOutDate <= ?)
                        )
                    )
                    AND NOT EXISTS (
                        SELECT 1
                        FROM Renting rt
                        WHERE rt.hotel_name = r.hotel_name
                        AND rt.room_number = r.room_number
                        AND (
                            (rt.checkin_date <= ? AND rt.checkout_date >= ?)
                            OR (rt.checkin_date <= ? AND rt.checkout_date >= ?)
                            OR (rt.checkin_date >= ? AND rt.checkout_date <= ?)
                        )
                    )
                    AND r.hotel_name = ?
                """;
                PreparedStatement availableStmt = conn.prepareStatement(availableRoomsSql);
                availableStmt.setDate(1, checkoutDate);
                availableStmt.setDate(2, checkinDate);
                availableStmt.setDate(3, checkoutDate);
                availableStmt.setDate(4, checkinDate);
                availableStmt.setDate(5, checkinDate);
                availableStmt.setDate(6, checkoutDate);
                availableStmt.setDate(7, checkoutDate);
                availableStmt.setDate(8, checkinDate);
                availableStmt.setDate(9, checkoutDate);
                availableStmt.setDate(10, checkinDate);
                availableStmt.setDate(11, checkinDate);
                availableStmt.setDate(12, checkoutDate);
                availableStmt.setString(13, hotelName);
                ResultSet availableRs = availableStmt.executeQuery();

                List<String> availableRooms = new ArrayList<>();
                while (availableRs.next()) {
                    availableRooms.add(availableRs.getString("room_number"));
                }

                String errorMessage = "The room is already booked or rented during the specified period.";
                if (!availableRooms.isEmpty()) {
                    errorMessage += " Available rooms: " + String.join(", ", availableRooms) + ".";
                } else {
                    errorMessage += " No other rooms are available. Please change the dates.";
                }
                throw new SQLException(errorMessage);
            }


            String insertRentalSql = "INSERT INTO Renting (customer_id, room_number, hotel_name, employee_SSN, checkin_date, checkout_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertRentalSql);
            insertStmt.setInt(1, Integer.parseInt(customerIdStr));
            insertStmt.setInt(2, Integer.parseInt(roomNumberStr));
            insertStmt.setString(3, hotelName);
            insertStmt.setString(4, employeeSSN);
            insertStmt.setDate(5, checkinDate);
            insertStmt.setDate(6, checkoutDate);
            insertStmt.executeUpdate();


            String updateRoomSql = "UPDATE Room SET status = 'rented' WHERE room_number = ? AND hotel_name = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateRoomSql);
            updateStmt.setInt(1, Integer.parseInt(roomNumberStr));
            updateStmt.setString(2, hotelName);
            updateStmt.executeUpdate();

            conn.commit();
            response.sendRedirect("EmployeeDashboardServlet");

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}