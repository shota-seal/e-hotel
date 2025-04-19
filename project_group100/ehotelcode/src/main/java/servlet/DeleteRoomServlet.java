package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/DeleteRoomServlet")
public class DeleteRoomServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hotelName = request.getParameter("hotel_name");
        String roomNumber = request.getParameter("room_number");

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);

            String sql = "DELETE FROM Room WHERE hotel_name = ? AND room_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hotelName);
            stmt.setInt(2, Integer.parseInt(roomNumber));
            stmt.executeUpdate();

            conn.commit();
            response.sendRedirect("HotelDetailsServlet?hotel_name=" + hotelName);

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            request.setAttribute("error", "Error deleting room: " + e.getMessage());
            request.getRequestDispatcher("HotelDetailsServlet?hotel_name=" + hotelName).forward(request, response);
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