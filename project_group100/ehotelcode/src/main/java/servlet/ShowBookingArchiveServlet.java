package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/ShowBookingArchiveServlet")
public class ShowBookingArchiveServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Map<String, String>> bookingArchiveList = new ArrayList<>();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionTest.connect();
            if (conn == null) {
                throw new ServletException("Database connection failed.");
            }

            String bookingArchiveSql = """
                SELECT booking_number, BookingDate, CheckInDate, CheckOutDate, 
                       customer_id, room_number, hotel_name, chain_name
                FROM Booking_archive
            """;
            stmt = conn.prepareStatement(bookingArchiveSql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, String> booking = new HashMap<>();
                booking.put("booking_number", String.valueOf(rs.getInt("booking_number")));
                booking.put("BookingDate", rs.getString("BookingDate"));
                booking.put("CheckInDate", rs.getString("CheckInDate"));
                booking.put("CheckOutDate", rs.getString("CheckOutDate"));
                booking.put("customer_id", String.valueOf(rs.getInt("customer_id")));
                booking.put("room_number", String.valueOf(rs.getInt("room_number")));
                booking.put("hotel_name", rs.getString("hotel_name"));
                booking.put("chain_name", rs.getString("chain_name"));
                bookingArchiveList.add(booking);
            }

            rs.close();
            stmt.close();
            conn.close();

            request.setAttribute("bookingArchives", bookingArchiveList);
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