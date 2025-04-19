package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String role = request.getParameter("role");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (role == null || username == null || password == null) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }


        if ("admin".equals(role)) {
            if ("admin".equals(username) && "12345678".equals(password)) {
                request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Invalid admin credentials.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            return;
        }


        if ("customer".equals(role)) {
            try (Connection conn = ConnectionTest.connect()) {
                int customerId = Integer.parseInt(password); // customer_id をパスワードとする

                String sql = "SELECT * FROM customer WHERE first_name = ? AND customer_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setInt(2, customerId);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("customer_id", customerId);
                    session.setAttribute("first_name", rs.getString("first_name"));
                    session.setAttribute("role", "customer");

                    response.sendRedirect("CustomerDashboardServlet");
                } else {
                    request.setAttribute("error", "Invalid customer credentials.");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                }

            } catch (NumberFormatException e) {
                request.setAttribute("error", "Customer ID must be a number.");
                request.getRequestDispatcher("index.jsp").forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Database error: " + e.getMessage());
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            return;
        }


        if ("employee".equals(role)) {
            try (Connection conn = ConnectionTest.connect()) {
                String sql = "SELECT * FROM employee WHERE first_name = ? AND ssn = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, username);
                stmt.setString(2, password);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    HttpSession session = request.getSession();
                    session.setAttribute("employee_ssn", rs.getString("ssn"));
                    session.setAttribute("first_name", rs.getString("first_name"));
                    session.setAttribute("hotel_name", rs.getString("hotel_name"));
                    session.setAttribute("role", "employee");

                    response.sendRedirect("EmployeeDashboardServlet");
                } else {
                    request.setAttribute("error", "Invalid employee credentials.");
                    request.getRequestDispatcher("index.jsp").forward(request, response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Database error: " + e.getMessage());
                request.getRequestDispatcher("index.jsp").forward(request, response);
            }
            return;
        }


        request.setAttribute("error", "Login for " + role + " is not implemented.");
        request.getRequestDispatcher("index.jsp").forward(request, response);
    }
}
