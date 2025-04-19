package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

@WebServlet("/EmployeeDashboardServlet")
public class EmployeeDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String hotelName = (String) session.getAttribute("hotel_name");
        String firstName = (String) session.getAttribute("first_name");

        if (hotelName == null || firstName == null) {
            request.setAttribute("error", "Please login as employee first.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);


            processExpiredRentals(conn);


            String bookingSql = "SELECT * FROM Booking WHERE hotel_name = ?";
            PreparedStatement bookingStmt = conn.prepareStatement(bookingSql);
            bookingStmt.setString(1, hotelName);
            ResultSet bookingRs = bookingStmt.executeQuery();

            List<Map<String, String>> bookings = new ArrayList<>();
            while (bookingRs.next()) {
                Map<String, String> booking = new HashMap<>();
                booking.put("booking_number", bookingRs.getString("booking_number"));
                booking.put("customer_id", bookingRs.getString("customer_id"));
                booking.put("room_number", bookingRs.getString("room_number"));
                booking.put("check_in", bookingRs.getString("CheckInDate"));
                booking.put("check_out", bookingRs.getString("CheckOutDate"));
                bookings.add(booking);
            }
            request.setAttribute("bookings", bookings);


            String rentSql = "SELECT r.renting_number, r.customer_id, r.room_number, r.hotel_name, r.rent_date, " +
                    "r.checkin_date, r.checkout_date, c.first_name, c.last_name " +
                    "FROM Renting r JOIN Customer c ON r.customer_id = c.customer_id " +
                    "WHERE r.hotel_name = ?";
            PreparedStatement rentStmt = conn.prepareStatement(rentSql);
            rentStmt.setString(1, hotelName);
            ResultSet rentRs = rentStmt.executeQuery();

            List<Map<String, String>> rentings = new ArrayList<>();
            while (rentRs.next()) {
                Map<String, String> renting = new HashMap<>();
                renting.put("renting_number", rentRs.getString("renting_number"));
                renting.put("customer_id", rentRs.getString("customer_id"));
                renting.put("room_number", rentRs.getString("room_number"));
                renting.put("hotel_name", rentRs.getString("hotel_name"));
                renting.put("rent_date", rentRs.getString("rent_date"));
                renting.put("checkin_date", rentRs.getString("checkin_date"));
                renting.put("checkout_date", rentRs.getString("checkout_date"));
                renting.put("first_name", rentRs.getString("first_name"));
                renting.put("last_name", rentRs.getString("last_name"));
                rentings.add(renting);
            }
            request.setAttribute("rentings", rentings);


            String availableRoomsSql = "SELECT room_number, hotel_name FROM Room WHERE hotel_name = ? AND status = 'available'";
            PreparedStatement availableStmt = conn.prepareStatement(availableRoomsSql);
            availableStmt.setString(1, hotelName);
            ResultSet availableRs = availableStmt.executeQuery();

            List<Map<String, String>> availableRooms = new ArrayList<>();
            while (availableRs.next()) {
                Map<String, String> room = new HashMap<>();
                room.put("room_number", availableRs.getString("room_number"));
                room.put("hotel_name", availableRs.getString("hotel_name"));
                availableRooms.add(room);
            }
            request.setAttribute("available_rooms", availableRooms);


            Map<String, String> employee = new HashMap<>();
            employee.put("first_name", firstName);
            employee.put("last_name", getLastName(conn, firstName));
            employee.put("role", (String) session.getAttribute("role"));
            employee.put("hotel_name", hotelName);

            request.setAttribute("employee", employee);
            request.getRequestDispatcher("employeeDashboard.jsp").forward(request, response);

            conn.commit();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            request.setAttribute("error", "Error loading data: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
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

    private String getLastName(Connection conn, String firstName) throws SQLException {
        String sql = "SELECT last_name FROM Employee WHERE first_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("last_name");
            }
        }
        return "";
    }

    private void processExpiredRentals(Connection conn) throws SQLException {

        LocalDate currentDate = LocalDate.now();


        String selectExpiredSql = "SELECT renting_number, room_number, hotel_name, customer_id, rent_date, " +
                "checkin_date, checkout_date, chain_name, employee_SSN " +
                "FROM Renting WHERE checkout_date < ?";
        PreparedStatement selectStmt = conn.prepareStatement(selectExpiredSql);
        selectStmt.setDate(1, java.sql.Date.valueOf(currentDate));
        ResultSet rs = selectStmt.executeQuery();

        while (rs.next()) {
            int rentingNumber = rs.getInt("renting_number");
            int roomNumber = rs.getInt("room_number");
            String hotelName = rs.getString("hotel_name");
            int customerId = rs.getInt("customer_id");
            Date rentDate = rs.getDate("rent_date");
            Date checkinDate = rs.getDate("checkin_date");
            Date checkoutDate = rs.getDate("checkout_date");
            String chainName = rs.getString("chain_name");
            String employeeSSN = rs.getString("employee_SSN");

            String insertArchiveSql = "INSERT INTO Renting_archive (renting_number, rent_date, checkin_date, checkout_date, " +
                    "customer_id, room_number, hotel_name, chain_name, employee_SSN) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertArchiveSql);
            insertStmt.setInt(1, rentingNumber);
            insertStmt.setDate(2, rentDate);
            insertStmt.setDate(3, checkinDate);
            insertStmt.setDate(4, checkoutDate);
            insertStmt.setInt(5, customerId);
            insertStmt.setInt(6, roomNumber);
            insertStmt.setString(7, hotelName);
            insertStmt.setString(8, chainName);
            insertStmt.setString(9, employeeSSN);
            insertStmt.executeUpdate();


            String updateRoomSql = "UPDATE Room SET status = 'available' " +
                    "WHERE room_number = ? AND hotel_name = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateRoomSql);
            updateStmt.setInt(1, roomNumber);
            updateStmt.setString(2, hotelName);
            updateStmt.executeUpdate();


            String deleteRentingSql = "DELETE FROM Renting WHERE renting_number = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteRentingSql);
            deleteStmt.setInt(1, rentingNumber);
            deleteStmt.executeUpdate();
        }
    }
}