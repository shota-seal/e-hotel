package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.util.*;

@WebServlet("/HotelDetailsServlet")
public class HotelDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hotelName = request.getParameter("hotel_name");
        if (hotelName == null || hotelName.isEmpty()) {
            request.setAttribute("error", "Hotel name is required.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();


            String roomSql = "SELECT room_number, hotel_name, price, amenities, capacity, view_type, expandable, issues, status FROM Room WHERE hotel_name = ?";
            PreparedStatement roomStmt = conn.prepareStatement(roomSql);
            roomStmt.setString(1, hotelName);
            ResultSet roomRs = roomStmt.executeQuery();

            List<Map<String, String>> rooms = new ArrayList<>();
            while (roomRs.next()) {
                Map<String, String> room = new HashMap<>();
                room.put("room_number", roomRs.getString("room_number"));
                room.put("hotel_name", roomRs.getString("hotel_name"));
                room.put("price", String.format("$%.2f", roomRs.getDouble("price")));
                room.put("amenities", roomRs.getString("amenities") != null ? roomRs.getString("amenities") : "None");
                room.put("capacity", roomRs.getString("capacity"));
                room.put("view_type", roomRs.getString("view_type"));
                room.put("expandable", roomRs.getBoolean("expandable") ? "Yes" : "No");
                room.put("issues", roomRs.getString("issues") != null ? roomRs.getString("issues") : "None");
                room.put("status", roomRs.getString("status"));
                rooms.add(room);
            }
            request.setAttribute("rooms", rooms);


            String employeeSql = "SELECT SSN, first_name, last_name, role FROM Employee WHERE hotel_name = ?";
            PreparedStatement employeeStmt = conn.prepareStatement(employeeSql);
            employeeStmt.setString(1, hotelName);
            ResultSet employeeRs = employeeStmt.executeQuery();

            List<Map<String, String>> employees = new ArrayList<>();
            while (employeeRs.next()) {
                Map<String, String> employee = new HashMap<>();
                employee.put("SSN", employeeRs.getString("SSN"));
                employee.put("name", employeeRs.getString("first_name") + " " + employeeRs.getString("last_name"));
                employee.put("role", employeeRs.getString("role"));
                employees.add(employee);
            }
            request.setAttribute("employees", employees);

            request.setAttribute("hotel_name", hotelName);
            request.getRequestDispatcher("hotelDetails.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading hotel details: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
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
}