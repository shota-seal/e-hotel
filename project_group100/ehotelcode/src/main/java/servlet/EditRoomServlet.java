package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/EditRoomServlet")
public class EditRoomServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hotelName = request.getParameter("hotel_name");
        String roomNumber = request.getParameter("room_number");

        if (hotelName == null || roomNumber == null) {
            request.setAttribute("error", "Hotel name and room number are required.");
            request.getRequestDispatcher("HotelDetailsServlet?hotel_name=" + hotelName).forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            String sql = "SELECT room_number, price, amenities, capacity, view_type, expandable, issues, status FROM Room WHERE hotel_name = ? AND room_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hotelName);
            stmt.setInt(2, Integer.parseInt(roomNumber));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                request.setAttribute("room_number", rs.getString("room_number"));
                request.setAttribute("price", rs.getDouble("price"));
                request.setAttribute("amenities", rs.getString("amenities"));
                request.setAttribute("capacity", rs.getString("capacity"));
                request.setAttribute("view_type", rs.getString("view_type"));
                request.setAttribute("expandable", rs.getBoolean("expandable"));
                request.setAttribute("issues", rs.getString("issues"));
                request.setAttribute("status", rs.getString("status"));
                request.setAttribute("hotel_name", hotelName);
                request.getRequestDispatcher("editRoom.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Room not found.");
                request.getRequestDispatcher("HotelDetailsServlet?hotel_name=" + hotelName).forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading room: " + e.getMessage());
            request.getRequestDispatcher("HotelDetailsServlet?hotel_name=" + hotelName).forward(request, response);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);

            String sql = "UPDATE Room SET price = ?, amenities = ?, capacity = ?, view_type = ?, expandable = ?, issues = ?, status = ? WHERE hotel_name = ? AND room_number = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setDouble(1, Double.parseDouble(price));
            stmt.setString(2, amenities.isEmpty() ? null : amenities);
            stmt.setString(3, capacity);
            stmt.setString(4, viewType);
            stmt.setBoolean(5, "Yes".equalsIgnoreCase(expandable));
            stmt.setString(6, issues.isEmpty() ? null : issues);
            stmt.setString(7, status);
            stmt.setString(8, hotelName);
            stmt.setInt(9, Integer.parseInt(roomNumber));
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
            request.setAttribute("error", "Error updating room: " + e.getMessage());
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