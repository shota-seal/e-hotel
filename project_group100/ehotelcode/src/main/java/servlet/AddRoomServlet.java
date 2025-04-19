package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AddRoomServlet")
public class AddRoomServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String hotelName = request.getParameter("hotel_name");
        String roomNumber = request.getParameter("room_number");
        String price = request.getParameter("price");
        String amenities = request.getParameter("amenities");
        String capacity = request.getParameter("capacity");
        String viewType = request.getParameter("view_type");
        String expandable = request.getParameter("expandable");
        String issues = request.getParameter("issues");
        String status = request.getParameter("status");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionTest.connect();

            // Check if the room number already exists for this hotel
            String checkSql = "SELECT COUNT(*) FROM Room WHERE hotel_name = ? AND room_number = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setString(1, hotelName);
            stmt.setInt(2, Integer.parseInt(roomNumber));
            rs = stmt.executeQuery();

            if (rs.next() && rs.getInt(1) > 0) {
                // If a duplicate is found
                request.setAttribute("error", "This room number already exists for this hotel.");
                request.getRequestDispatcher("addRoom.jsp").forward(request, response);
                return;
            }

            // If amenities or issues are empty, set them to "None"
            if (amenities == null || amenities.trim().isEmpty()) {
                amenities = "None";
            }
            if (issues == null || issues.trim().isEmpty()) {
                issues = "None";
            }

            // Insert the new room if no duplicate exists
            String insertSql = "INSERT INTO Room (hotel_name, room_number, price, amenities, capacity, view_type, expandable, issues, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertSql);
            stmt.setString(1, hotelName);
            stmt.setInt(2, Integer.parseInt(roomNumber));
            stmt.setBigDecimal(3, new java.math.BigDecimal(price));
            stmt.setString(4, amenities);
            stmt.setString(5, capacity);
            stmt.setString(6, viewType);
            stmt.setBoolean(7, Boolean.parseBoolean(expandable));
            stmt.setString(8, issues);
            stmt.setString(9, status);

            stmt.executeUpdate();

            // Redirect to the hotel details page
            response.sendRedirect("HotelDetailsServlet?hotel_name=" + hotelName);

        } catch (Exception e) {
            throw new ServletException("Error adding room: " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ServletException("Error closing database resources: " + e.getMessage(), e);
            }
        }
    }
}