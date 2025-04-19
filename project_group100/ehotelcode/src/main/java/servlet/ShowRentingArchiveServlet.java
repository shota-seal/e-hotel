package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/ShowRentingArchiveServlet")
public class ShowRentingArchiveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Map<String, String>> rentingArchiveList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionTest.connect();
            if (conn == null) {
                throw new ServletException("Database connection failed.");
            }

            String rentingArchiveSql = """
                SELECT renting_number, rent_date, checkin_date, checkout_date, 
                       customer_id, room_number, hotel_name, chain_name, employee_SSN
                FROM Renting_archive
            """;
            stmt = conn.prepareStatement(rentingArchiveSql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> renting = new HashMap<>();
                renting.put("renting_number", String.valueOf(rs.getInt("renting_number")));
                renting.put("rent_date", rs.getString("rent_date"));
                renting.put("checkin_date", rs.getString("checkin_date"));
                renting.put("checkout_date", rs.getString("checkout_date"));
                renting.put("customer_id", String.valueOf(rs.getInt("customer_id")));
                renting.put("room_number", String.valueOf(rs.getInt("room_number")));
                renting.put("hotel_name", rs.getString("hotel_name"));
                renting.put("chain_name", rs.getString("chain_name"));
                renting.put("employee_SSN", rs.getString("employee_SSN"));
                rentingArchiveList.add(renting);
            }

            rs.close();
            stmt.close();
            conn.close();

            request.setAttribute("rentingArchives", rentingArchiveList);
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