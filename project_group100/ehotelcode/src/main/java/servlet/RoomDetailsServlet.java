package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/RoomDetailsServlet")
public class RoomDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String roomNumberStr = request.getParameter("room_number");
        String hotelName = request.getParameter("hotel_name");

        if (roomNumberStr == null || hotelName == null) {
            request.setAttribute("error", "Missing room or hotel information.");
            request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
            return;
        }

        int roomNumber;
        try {
            roomNumber = Integer.parseInt(roomNumberStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid room number.");
            request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
            return;
        }

        try (Connection conn = ConnectionTest.connect()) {
            String sql = "SELECT r.*, h.star_rating FROM Room r " +
                    "JOIN Hotel h ON r.hotel_name = h.hotel_name " +
                    "WHERE r.room_number = ? AND r.hotel_name = ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, roomNumber);
            stmt.setString(2, hotelName);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Map<String, String> room = new HashMap<>();
                room.put("room_number", rs.getString("room_number"));
                room.put("hotel_name", rs.getString("hotel_name"));
                room.put("price", rs.getString("price"));
                room.put("capacity", rs.getString("capacity"));
                room.put("view_type", rs.getString("view_type"));
                room.put("expandable", rs.getString("expandable"));
                room.put("status", rs.getString("status"));
                room.put("star_rating", rs.getString("star_rating"));

                request.setAttribute("room", room);


                request.getRequestDispatcher("roomDetails.jsp").forward(request, response);

            } else {
                request.setAttribute("error", "Room not found.");
                request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
        }
    }
}
