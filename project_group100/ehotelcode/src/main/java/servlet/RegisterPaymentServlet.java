package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@WebServlet("/RegisterPaymentServlet")
public class RegisterPaymentServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String rentalIdStr = request.getParameter("rental_id");

        if (rentalIdStr == null || rentalIdStr.isEmpty()) {
            request.setAttribute("error", "Rental ID is required.");
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();


            String rentalSql = "SELECT r.checkin_date, r.checkout_date, r.room_number, r.hotel_name, " +
                    "rm.price " +
                    "FROM Renting r " +
                    "JOIN Room rm ON r.room_number = rm.room_number AND r.hotel_name = rm.hotel_name " +
                    "WHERE r.renting_number = ?";
            PreparedStatement rentalStmt = conn.prepareStatement(rentalSql);
            rentalStmt.setInt(1, Integer.parseInt(rentalIdStr));
            ResultSet rs = rentalStmt.executeQuery();

            if (rs.next()) {
                java.sql.Date checkinDate = rs.getDate("checkin_date");
                java.sql.Date checkoutDate = rs.getDate("checkout_date");
                double pricePerDay = rs.getDouble("price");


                LocalDate checkinLocalDate = checkinDate.toLocalDate();
                LocalDate checkoutLocalDate = checkoutDate.toLocalDate();
                long totalDays = ChronoUnit.DAYS.between(checkinLocalDate, checkoutLocalDate);
                if (totalDays < 1) totalDays = 1;


                double totalAmount = pricePerDay * totalDays;


                request.setAttribute("rental_id", rentalIdStr);
                request.setAttribute("total_days", totalDays);
                request.setAttribute("price_per_day", pricePerDay);
                request.setAttribute("total_amount", totalAmount);
                request.getRequestDispatcher("confirmPayment.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Rental not found.");
                request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error calculating total amount: " + e.getMessage());
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
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
        String rentalIdStr = request.getParameter("rental_id");
        String amountStr = request.getParameter("amount");

        if (rentalIdStr == null || amountStr == null || rentalIdStr.isEmpty() || amountStr.isEmpty()) {
            request.setAttribute("error", "Rental ID and amount are required.");
            request.getRequestDispatcher("EmployeeDashboardServlet").forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);


            String rentalSql = "SELECT * FROM Renting WHERE renting_number = ?";
            PreparedStatement rentalStmt = conn.prepareStatement(rentalSql);
            rentalStmt.setInt(1, Integer.parseInt(rentalIdStr));
            ResultSet rs = rentalStmt.executeQuery();

            if (rs.next()) {
                int rentingNumber = rs.getInt("renting_number");
                int roomNumber = rs.getInt("room_number");
                String hotelName = rs.getString("hotel_name");
                int customerId = rs.getInt("customer_id");
                java.sql.Date rentDate = rs.getDate("rent_date");
                java.sql.Date checkinDate = rs.getDate("checkin_date");
                java.sql.Date checkoutDate = rs.getDate("checkout_date");
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

                conn.commit();
                response.sendRedirect("EmployeeDashboardServlet");
            } else {
                conn.rollback();
                request.setAttribute("error", "Rental not found.");
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
            request.setAttribute("error", "Failed to register payment: " + e.getMessage());
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