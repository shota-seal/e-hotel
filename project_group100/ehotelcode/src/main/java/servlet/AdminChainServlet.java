package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/AdminChainServlet")
public class AdminChainServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String selectedChain = request.getParameter("chain");
        List<Map<String, String>> hotelList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionTest.connect();
            if (conn == null) {
                throw new ServletException("Database connection failed.");
            }

            String hotelSql = """
                SELECT hotel_name, country, city, street_number, unit_number, zip_code, 
                       star_rating, number_of_rooms, contact_email, phone_number
                FROM hotel
                WHERE chain_name = ?
            """;
            stmt = conn.prepareStatement(hotelSql);
            stmt.setString(1, selectedChain);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> hotel = new HashMap<>();
                hotel.put("hotel_name", rs.getString("hotel_name"));
                hotel.put("country", rs.getString("country"));
                hotel.put("city", rs.getString("city"));
                hotel.put("street_number", rs.getString("street_number"));
                hotel.put("unit_number", rs.getString("unit_number"));
                hotel.put("zip_code", rs.getString("zip_code"));
                hotel.put("star_rating", String.valueOf(rs.getInt("star_rating")));
                hotel.put("number_of_rooms", String.valueOf(rs.getInt("number_of_rooms")));
                hotel.put("contact_email", rs.getString("contact_email"));
                hotel.put("phone_number", rs.getString("phone_number"));
                hotelList.add(hotel);
            }

            rs.close();
            stmt.close();
            conn.close();

            request.setAttribute("hotels", hotelList);
            request.setAttribute("chain", selectedChain);
            RequestDispatcher dispatcher = request.getRequestDispatcher("adminDashboard.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            throw new ServletException("SQL error: " + e.getMessage(), e);
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