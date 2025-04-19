package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/BookingServlet")
public class BookingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Integer customerId = (Integer) session.getAttribute("customer_id");

        if (customerId == null) {
            request.setAttribute("error", "Please login first.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        int roomNumber = Integer.parseInt(request.getParameter("room_number"));
        String hotelName = request.getParameter("hotel_name");
        LocalDate checkInDate = LocalDate.parse(request.getParameter("start_date"));
        LocalDate checkOutDate = LocalDate.parse(request.getParameter("end_date"));

        try (Connection conn = ConnectionTest.connect()) {

            String checkSql = """
                SELECT COUNT(*) FROM Booking
                WHERE room_number = ? AND hotel_name = ?
                AND (CheckInDate <= ? AND CheckOutDate >= ?)
            """;
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, roomNumber);
            checkStmt.setString(2, hotelName);
            checkStmt.setDate(3, Date.valueOf(checkOutDate));
            checkStmt.setDate(4, Date.valueOf(checkInDate));
            ResultSet checkRs = checkStmt.executeQuery();

            if (checkRs.next() && checkRs.getInt(1) > 0) {

                request.setAttribute("error", "This room is already booked for the selected dates.");
                request.setAttribute("room", getRoomDetails(conn, roomNumber, hotelName));
                request.getRequestDispatcher("roomDetails.jsp").forward(request, response);
                return;
            }


            String chainSql = "SELECT chain_name FROM Hotel WHERE hotel_name = ?";
            PreparedStatement chainStmt = conn.prepareStatement(chainSql);
            chainStmt.setString(1, hotelName);
            ResultSet chainRs = chainStmt.executeQuery();
            String chainName = chainRs.next() ? chainRs.getString("chain_name") : null;


            String insertSql = "INSERT INTO Booking (CheckInDate, CheckOutDate, customer_id, room_number, hotel_name, chain_name) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setDate(1, Date.valueOf(checkInDate));
            insertStmt.setDate(2, Date.valueOf(checkOutDate));
            insertStmt.setInt(3, customerId);
            insertStmt.setInt(4, roomNumber);
            insertStmt.setString(5, hotelName);
            insertStmt.setString(6, chainName);
            insertStmt.executeUpdate();


            session.setAttribute("success", "Booking successfully created!");
            response.sendRedirect("CustomerDashboardServlet");

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> room = null;
            try {
                room = getRoomDetails(null, roomNumber, hotelName);
            } catch (SQLException ex) {
                ex.printStackTrace();
                request.setAttribute("error", "Booking failed: " + e.getMessage() + "; Failed to load room details: " + ex.getMessage());
                request.getRequestDispatcher("roomDetails.jsp").forward(request, response);
                return;
            }
            request.setAttribute("error", "Booking failed: " + e.getMessage());
            request.setAttribute("room", room);
            request.getRequestDispatcher("roomDetails.jsp").forward(request, response);
        }
    }


    private Map<String, String> getRoomDetails(Connection conn, int roomNumber, String hotelName) throws SQLException {
        Map<String, String> room = new HashMap<>();
        Connection localConn = null;
        try {
            if (conn == null) {
                localConn = ConnectionTest.connect();
                conn = localConn;
            }
            String sql = """
                SELECT r.room_number, r.hotel_name, r.price, r.capacity, r.view_type, r.status, h.star_rating, h.city, h.chain_name
                FROM Room r
                JOIN Hotel h ON r.hotel_name = h.hotel_name
                WHERE r.room_number = ? AND r.hotel_name = ?
            """;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, roomNumber);
            stmt.setString(2, hotelName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                room.put("room_number", rs.getString("room_number"));
                room.put("hotel_name", rs.getString("hotel_name"));
                room.put("price", rs.getString("price"));
                room.put("capacity", rs.getString("capacity"));
                room.put("view_type", rs.getString("view_type"));
                room.put("status", rs.getString("status"));
                room.put("star_rating", rs.getString("star_rating"));
                room.put("city", rs.getString("city"));
                room.put("chain_name", rs.getString("chain_name"));

                room.put("expandable", "No");
            } else {

                throw new SQLException("Room not found for room_number: " + roomNumber + ", hotel_name: " + hotelName);
            }
        } finally {
            if (localConn != null) {
                localConn.close();
            }
        }
        return room;
    }
}