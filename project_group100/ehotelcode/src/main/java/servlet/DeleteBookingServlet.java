package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/DeleteBookingServlet")
public class DeleteBookingServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer_id") == null) {
            response.sendRedirect("index.jsp");
            return;
        }


        String bookingId = request.getParameter("booking_id");
        Integer customerId = (Integer) session.getAttribute("customer_id");

        if (bookingId == null || bookingId.isEmpty()) {
            request.setAttribute("error", "Invalid booking ID.");
            request.getRequestDispatcher("CustomerDashboardServlet").forward(request, response);
            return;
        }

        try (Connection conn = ConnectionTest.connect()) {

            String checkSql = "SELECT customer_id FROM Booking WHERE booking_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, Integer.parseInt(bookingId));
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next() || rs.getInt("customer_id") != customerId) {
                request.setAttribute("error", "Booking not found or you do not have permission to delete it.");
                request.getRequestDispatcher("CustomerDashboardServlet").forward(request, response);
                return;
            }


            String deleteSql = "DELETE FROM Booking WHERE booking_number = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, Integer.parseInt(bookingId));
            int rowsAffected = deleteStmt.executeUpdate();

            if (rowsAffected > 0) {
                session.setAttribute("success", "Booking deleted successfully!");
            } else {
                request.setAttribute("error", "Failed to delete booking.");
            }


            response.sendRedirect("CustomerDashboardServlet");

        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("CustomerDashboardServlet").forward(request, response);
        }
    }
}