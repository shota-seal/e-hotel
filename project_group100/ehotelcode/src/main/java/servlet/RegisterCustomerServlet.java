package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

@WebServlet("/RegisterCustomerServlet")
public class RegisterCustomerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            int customerId = Integer.parseInt(request.getParameter("customer_id"));
            String firstName = request.getParameter("first_name");
            String midName = request.getParameter("mid_name");
            String lastName = request.getParameter("last_name");
            String country = request.getParameter("country");
            String city = request.getParameter("city");
            String streetNumber = request.getParameter("street_number");
            String unitNumber = request.getParameter("unit_number");
            String zipCode = request.getParameter("zip_code");
            String idType = request.getParameter("id_type");
            LocalDate registrationDate = LocalDate.now();

            try (Connection conn = ConnectionTest.connect()) {
                String sql = "INSERT INTO customer (customer_id, first_name, mid_name, last_name, country, city, street_number, unit_number, zip_code, id_type, registration_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, customerId);
                stmt.setString(2, firstName);
                stmt.setString(3, midName);
                stmt.setString(4, lastName);
                stmt.setString(5, country);
                stmt.setString(6, city);
                stmt.setString(7, streetNumber);
                stmt.setString(8, unitNumber);
                stmt.setString(9, zipCode);
                stmt.setString(10, idType);
                stmt.setDate(11, Date.valueOf(registrationDate));

                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    response.sendRedirect("index.jsp");
                } else {
                    request.setAttribute("error", "Registration failed.");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}
