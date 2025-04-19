package servlet;

import db.ConnectionTest;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;

@WebServlet("/MakeBookingServlet")
public class MakeBookingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer_id") == null) {
            response.sendRedirect("index.jsp");
            return;
        }


        String minPrice = request.getParameter("min_price");
        String maxPrice = request.getParameter("max_price");
        String capacity = request.getParameter("capacity");
        String view = request.getParameter("view_type");
        String minStar = request.getParameter("min_star");
        String checkin = request.getParameter("checkin_date");
        String checkout = request.getParameter("checkout_date");
        String chain = request.getParameter("hotel_chain");
        String city = request.getParameter("city");


        if (checkin == null || checkin.isEmpty() || checkout == null || checkout.isEmpty()) {
            request.setAttribute("error", "Check-in and check-out dates are required.");
            request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
            return;
        }


        java.sql.Date checkinDate;
        java.sql.Date checkoutDate;
        try {
            LocalDate checkinLocalDate = LocalDate.parse(checkin);
            LocalDate checkoutLocalDate = LocalDate.parse(checkout);
            if (checkinLocalDate.isAfter(checkoutLocalDate)) {
                request.setAttribute("error", "Check-in date must be before check-out date.");
                request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
                return;
            }
            checkinDate = java.sql.Date.valueOf(checkinLocalDate);
            checkoutDate = java.sql.Date.valueOf(checkoutLocalDate);
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Invalid date format. Please use YYYY-MM-DD.");
            request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
            return;
        }

        List<Map<String, String>> results = new ArrayList<>();

        try (Connection conn = ConnectionTest.connect()) {
            StringBuilder sql = new StringBuilder("""
                SELECT r.room_number, r.hotel_name, r.price, r.capacity, r.view_type, r.status, h.star_rating, h.city, h.chain_name
                FROM Room r
                JOIN Hotel h ON r.hotel_name = h.hotel_name
                WHERE r.status = 'available'
                AND NOT EXISTS (
                    SELECT 1
                    FROM Booking b
                    WHERE b.hotel_name = r.hotel_name
                    AND b.room_number = r.room_number
                    AND (
                        (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                        OR (b.CheckInDate <= ? AND b.CheckOutDate >= ?)
                        OR (b.CheckInDate >= ? AND b.CheckOutDate <= ?)
                    )
                )
                AND NOT EXISTS (
                    SELECT 1
                    FROM Renting rt
                    WHERE rt.hotel_name = r.hotel_name
                    AND rt.room_number = r.room_number
                    AND (
                        (rt.checkin_date <= ? AND rt.checkout_date >= ?)
                        OR (rt.checkin_date <= ? AND rt.checkout_date >= ?)
                        OR (rt.checkin_date >= ? AND rt.checkout_date <= ?)
                    )
                )
            """);

            List<Object> params = new ArrayList<>();


            params.add(checkoutDate); // b.CheckInDate <= ? AND b.CheckOutDate >= ?
            params.add(checkinDate);  // b.CheckInDate <= ? AND b.CheckOutDate >= ?
            params.add(checkoutDate); // b.CheckInDate <= ? AND b.CheckOutDate >= ?
            params.add(checkinDate);  // b.CheckInDate <= ? AND b.CheckOutDate >= ?
            params.add(checkinDate);  // b.CheckInDate >= ? AND b.CheckOutDate <= ?
            params.add(checkoutDate); // b.CheckInDate >= ? AND b.CheckOutDate <= ?


            params.add(checkoutDate);
            params.add(checkinDate);
            params.add(checkoutDate);
            params.add(checkinDate);
            params.add(checkinDate);
            params.add(checkoutDate);


            if (minPrice != null && !minPrice.isEmpty()) {
                sql.append(" AND r.price >= ?");
                params.add(Double.parseDouble(minPrice));
            }
            if (maxPrice != null && !maxPrice.isEmpty()) {
                sql.append(" AND r.price <= ?");
                params.add(Double.parseDouble(maxPrice));
            }
            if (capacity != null && !capacity.isEmpty() && !capacity.equals("-- Any --")) {
                sql.append(" AND r.capacity = ?");
                params.add(capacity);
            }
            if (view != null && !view.isEmpty() && !view.equals("-- Any --")) {
                sql.append(" AND r.view_type = ?");
                params.add(view);
            }
            if (minStar != null && !minStar.isEmpty() && !minStar.equals("-- Any --")) {
                sql.append(" AND h.star_rating >= ?");
                params.add(Integer.parseInt(minStar));
            }
            if (chain != null && !chain.isEmpty() && !chain.equals("-- Any --")) {
                sql.append(" AND h.chain_name = ?");
                params.add(chain);
            }
            if (city != null && !city.isEmpty() && !city.equals("-- Any --")) {
                sql.append(" AND h.city = ?");
                params.add(city);
            }


            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Map<String, String> room = new HashMap<>();
                room.put("room_number", rs.getString("room_number"));
                room.put("hotel_name", rs.getString("hotel_name"));
                room.put("price", rs.getString("price"));
                room.put("capacity", rs.getString("capacity"));
                room.put("view_type", rs.getString("view_type"));
                room.put("status", rs.getString("status"));
                room.put("star_rating", rs.getString("star_rating"));
                room.put("city", rs.getString("city"));
                room.put("chain_name", rs.getString("chain_name"));
                results.add(room);
            }


            request.setAttribute("rooms", results);
            request.setAttribute("checkin_date", checkin);
            request.setAttribute("checkout_date", checkout);
            request.getRequestDispatcher("makeBooking.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("makeBooking.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("customer_id") == null) {
            response.sendRedirect("index.jsp");
            return;
        }


        doGet(request, response);
    }
}