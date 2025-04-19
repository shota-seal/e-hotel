package servlet;

import db.ConnectionTest;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class ShowHotelsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String chainName = request.getParameter("chainName");
        List<Map<String, String>> hotels = new ArrayList<>();

        try (Connection conn = ConnectionTest.connect()) {
            String sql = "SELECT hotel_name, city, country, star_rating FROM hotel WHERE chain_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, chainName);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> hotel = new HashMap<>();
                hotel.put("hotel_name", rs.getString("hotel_name"));
                hotel.put("city", rs.getString("city"));
                hotel.put("country", rs.getString("country"));
                hotel.put("star_rating", String.valueOf(rs.getInt("star_rating")));
                hotels.add(hotel);
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            throw new ServletException("Database error", e);
        }

        request.setAttribute("hotels", hotels);
        RequestDispatcher dispatcher = request.getRequestDispatcher("hotelList.jsp");
        dispatcher.forward(request, response);
    }
}
