package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/DeleteEmployeeServlet")
public class DeleteEmployeeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hotelName = request.getParameter("hotel_name");
        String ssn = request.getParameter("SSN");

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);

            String sql = "DELETE FROM Employee WHERE hotel_name = ? AND SSN = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hotelName);
            stmt.setString(2, ssn);
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
            request.setAttribute("error", "Error deleting employee: " + e.getMessage());
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