package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/EditEmployeeServlet")
public class EditEmployeeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hotelName = request.getParameter("hotel_name");
        String ssn = request.getParameter("SSN");

        if (hotelName == null || ssn == null) {
            request.setAttribute("error", "Hotel name and SSN are required.");
            request.getRequestDispatcher("HotelDetailsServlet?hotel_name=" + hotelName).forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            String sql = "SELECT SSN, first_name, last_name, role FROM Employee WHERE hotel_name = ? AND SSN = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hotelName);
            stmt.setString(2, ssn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                request.setAttribute("SSN", rs.getString("SSN"));
                request.setAttribute("first_name", rs.getString("first_name"));
                request.setAttribute("last_name", rs.getString("last_name"));
                request.setAttribute("role", rs.getString("role"));
                request.setAttribute("hotel_name", hotelName);
                request.getRequestDispatcher("editEmployee.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Employee not found.");
                request.getRequestDispatcher("HotelDetailsServlet?hotel_name=" + hotelName).forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading employee: " + e.getMessage());
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
        String ssn = request.getParameter("SSN");
        String firstName = request.getParameter("first_name");
        String lastName = request.getParameter("last_name");
        String role = request.getParameter("role");

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);

            String sql = "UPDATE Employee SET first_name = ?, last_name = ?, role = ? WHERE hotel_name = ? AND SSN = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, role);
            stmt.setString(4, hotelName);
            stmt.setString(5, ssn);
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
            request.setAttribute("error", "Error updating employee: " + e.getMessage());
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