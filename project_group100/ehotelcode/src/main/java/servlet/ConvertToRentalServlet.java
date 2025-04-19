package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/ConvertToRentalServlet")
public class ConvertToRentalServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        String bookingNumberStr = request.getParameter("booking_number");
        HttpSession session = request.getSession();
        String employeeSSN = (String) session.getAttribute("employee_ssn");


        if (bookingNumberStr == null || employeeSSN == null) {
            request.setAttribute("error", "Missing booking number or employee information.");
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            return;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        PreparedStatement archiveStmt = null;
        PreparedStatement delStmt = null;

        try {

            conn = ConnectionTest.connect();
            if (conn == null) {
                throw new ServletException("Database connection failed.");
            }
            conn.setAutoCommit(false);


            String query = "SELECT b.customer_id, b.room_number, b.hotel_name, b.chain_name, b.CheckInDate, b.CheckOutDate, b.BookingDate " +
                    "FROM Booking b WHERE b.booking_number = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(bookingNumberStr));
            rs = ps.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                int roomNumber = rs.getInt("room_number");
                String hotelName = rs.getString("hotel_name");
                String chainName = rs.getString("chain_name");
                Date checkInDate = rs.getDate("CheckInDate");
                Date checkOutDate = rs.getDate("CheckOutDate");
                Date bookingDate = rs.getDate("BookingDate");


                String updateRoomStatus = "UPDATE Room SET status = 'rented' " +
                        "WHERE room_number = ? AND hotel_name = ?";
                updateStmt = conn.prepareStatement(updateRoomStatus);
                updateStmt.setInt(1, roomNumber);
                updateStmt.setString(2, hotelName);
                int updatedRows = updateStmt.executeUpdate();

                if (updatedRows == 0) {
                    throw new SQLException("Room not found or could not be updated.");
                }


                String insertRental = "INSERT INTO Renting (customer_id, room_number, hotel_name, chain_name, employee_SSN, checkin_date, checkout_date, rent_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_DATE)";
                insertStmt = conn.prepareStatement(insertRental);
                insertStmt.setInt(1, customerId);
                insertStmt.setInt(2, roomNumber);
                insertStmt.setString(3, hotelName);
                insertStmt.setString(4, chainName);
                insertStmt.setString(5, employeeSSN);
                insertStmt.setDate(6, checkInDate != null ? new java.sql.Date(checkInDate.getTime()) : null);
                insertStmt.setDate(7, checkOutDate != null ? new java.sql.Date(checkOutDate.getTime()) : null);
                insertStmt.executeUpdate();


                String insertArchive = "INSERT INTO Booking_archive (booking_number, BookingDate, CheckInDate, CheckOutDate, customer_id, room_number, hotel_name, chain_name) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                archiveStmt = conn.prepareStatement(insertArchive);
                archiveStmt.setInt(1, Integer.parseInt(bookingNumberStr));
                archiveStmt.setDate(2, bookingDate != null ? new java.sql.Date(bookingDate.getTime()) : null);
                archiveStmt.setDate(3, checkInDate != null ? new java.sql.Date(checkInDate.getTime()) : null);
                archiveStmt.setDate(4, checkOutDate != null ? new java.sql.Date(checkOutDate.getTime()) : null);
                archiveStmt.setInt(5, customerId);
                archiveStmt.setInt(6, roomNumber);
                archiveStmt.setString(7, hotelName);
                archiveStmt.setString(8, chainName);
                archiveStmt.executeUpdate();


                String deleteBooking = "DELETE FROM Booking WHERE booking_number = ?";
                delStmt = conn.prepareStatement(deleteBooking);
                delStmt.setInt(1, Integer.parseInt(bookingNumberStr));
                delStmt.executeUpdate();

                conn.commit();
                response.sendRedirect("EmployeeDashboardServlet");
            } else {
                conn.rollback();
                request.setAttribute("error", "Booking not found.");
                request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            }

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            request.setAttribute("error", "Failed to convert booking: " + e.getMessage());
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
        } finally {

            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (updateStmt != null) updateStmt.close();
                if (insertStmt != null) insertStmt.close();
                if (archiveStmt != null) archiveStmt.close();
                if (delStmt != null) delStmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}