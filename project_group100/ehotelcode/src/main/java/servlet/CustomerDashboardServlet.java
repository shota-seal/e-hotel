package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/CustomerDashboardServlet")
public class CustomerDashboardServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer_id") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        Integer customerId = (Integer) session.getAttribute("customer_id");
        String firstName = (String) session.getAttribute("first_name");

        if (firstName == null) {
            try (Connection conn = ConnectionTest.connect()) {
                String sql = "SELECT first_name FROM Customer WHERE customer_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, customerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    firstName = rs.getString("first_name");
                    session.setAttribute("first_name", firstName);
                } else {
                    firstName = "Customer";
                }
            } catch (SQLException e) {
                e.printStackTrace();
                firstName = "Customer";
            }
        }

        List<Map<String, String>> bookings = new ArrayList<>();

        try (Connection conn = ConnectionTest.connect()) {
            String sql = """
                SELECT booking_number, hotel_name, room_number, CheckInDate, CheckOutDate
                FROM Booking
                WHERE customer_id = ?
                ORDER BY CheckInDate DESC
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> booking = new HashMap<>();
                booking.put("booking_id", rs.getString("booking_number"));
                booking.put("hotel_name", rs.getString("hotel_name"));
                booking.put("room_number", rs.getString("room_number"));
                booking.put("checkin_date", rs.getString("CheckInDate"));
                booking.put("checkout_date", rs.getString("CheckOutDate"));
                bookings.add(booking);
            }

            request.setAttribute("first_name", firstName);
            request.setAttribute("bookings", bookings);
            request.getRequestDispatcher("customerDashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to load dashboard: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}