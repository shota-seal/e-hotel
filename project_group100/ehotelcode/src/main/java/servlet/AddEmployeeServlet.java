package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/AddEmployeeServlet")
public class AddEmployeeServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String hotelName = request.getParameter("hotel_name");
        String ssn = request.getParameter("SSN");
        String firstName = request.getParameter("first_name");
        String midName = request.getParameter("mid_name");
        String lastName = request.getParameter("last_name");
        String country = request.getParameter("country");
        String city = request.getParameter("city");
        String streetNumber = request.getParameter("street_number");
        String unitNumber = request.getParameter("unit_number");
        String zipCode = request.getParameter("zip_code");
        String role = request.getParameter("role");
        String managerSSN = request.getParameter("manager_SSN");


        if (ssn == null || ssn.trim().isEmpty()) {
            request.setAttribute("error", "SSN is required and cannot be empty.");
            request.getRequestDispatcher("addEmployee.jsp?hotel_name=" + hotelName).forward(request, response);
            return;
        }


        if (firstName == null || firstName.trim().isEmpty() || lastName == null || lastName.trim().isEmpty() || role == null || role.trim().isEmpty()) {
            request.setAttribute("error", "First Name, Last Name, and Role are required.");
            request.getRequestDispatcher("addEmployee.jsp?hotel_name=" + hotelName).forward(request, response);
            return;
        }

        Connection conn = null;
        try {
            conn = ConnectionTest.connect();
            conn.setAutoCommit(false);


            String checkSql = "SELECT COUNT(*) FROM Employee WHERE SSN = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, ssn);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                request.setAttribute("error", "SSN '" + ssn + "' already exists. Please use a different SSN.");
                request.getRequestDispatcher("addEmployee.jsp?hotel_name=" + hotelName).forward(request, response);
                return;
            }


            String sql = "INSERT INTO Employee (SSN, first_name, mid_name, last_name, country, city, street_number, unit_number, zip_code, role, hotel_name, manager_SSN) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, ssn);
            stmt.setString(2, firstName);
            stmt.setString(3, midName.isEmpty() ? null : midName);
            stmt.setString(4, lastName);
            stmt.setString(5, country.isEmpty() ? null : country);
            stmt.setString(6, city.isEmpty() ? null : city);
            stmt.setString(7, streetNumber.isEmpty() ? null : streetNumber);
            stmt.setString(8, unitNumber.isEmpty() ? null : unitNumber);
            stmt.setString(9, zipCode.isEmpty() ? null : zipCode);
            stmt.setString(10, role);
            stmt.setString(11, hotelName);
            stmt.setString(12, managerSSN.isEmpty() ? null : managerSSN);
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
            request.setAttribute("error", "Error adding employee: " + e.getMessage());
            request.getRequestDispatcher("addEmployee.jsp?hotel_name=" + hotelName).forward(request, response);
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