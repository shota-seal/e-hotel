<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f4f6f8;
            margin: 0;
            padding: 30px;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: #fff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        }

        h1 {
            color: #333;
            margin-bottom: 20px;
        }

        form {
            margin-bottom: 30px;
        }

        label {
            font-weight: bold;
            font-size: 16px;
            margin-right: 10px;
        }

        select {
            padding: 8px 12px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input[type="submit"], button {
            background-color: #007bff;
            color: white;
            border: none;
            padding: 8px 16px;
            margin-left: 10px;
            border-radius: 6px;
            font-size: 14px;
            cursor: pointer;
        }

        input[type="submit"]:hover, button:hover {
            background-color: #0056b3;
        }

        .logout-btn {
            background-color: #6c757d;
            color: white;
        }

        .logout-btn:hover {
            background-color: #5a6268;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            font-size: 14px;
        }

        th, td {
            padding: 12px 15px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #f1f1f1;
            font-weight: bold;
        }

        tr:hover {
            background-color: #f9f9f9;
        }

        .hotel-title, .archive-title {
            margin-top: 40px;
            font-size: 22px;
            color: #444;
        }

        a {
            color: #007bff;
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }

        .nav-buttons {
            margin-bottom: 20px;
        }
    </style>
    <script>
        function confirmLogout() {
            return confirm("Are you sure you want to logout?");
        }
    </script>
</head>
<body>
    <div class="container">
        <h1>Welcome, Admin!</h1>

        <div class="nav-buttons">
            <button class="logout-btn" onclick="if(confirmLogout()) location.href='LogoutServlet'">Logout</button>
        </div>

        <form action="AdminChainServlet" method="post">
            <label>Select a Hotel Chain:</label>
            <select name="chain">
                <option value="Hilton">Hilton</option>
                <option value="Marriott">Marriott</option>
                <option value="Hyatt">Hyatt</option>
                <option value="Sheraton">Sheraton</option>
                <option value="Best Western">Best Western</option>
            </select>
            <input type="submit" value="Show Hotels">
        </form>

        <form action="ShowBookingArchiveServlet" method="post" style="display:inline;">
            <input type="submit" value="Show Booking Archive">
        </form>
        <form action="ShowRentingArchiveServlet" method="post" style="display:inline;">
            <input type="submit" value="Show Renting Archive">
        </form>

        <%
            List<Map<String, String>> hotels = (List<Map<String, String>>) request.getAttribute("hotels");
            String chainName = (String) request.getAttribute("chain");

            if (hotels != null && !hotels.isEmpty()) {
        %>
        <div class="hotel-title">Hotels in <%= chainName %>:</div>
        <table>
            <tr>
                <th>Name</th>
                <th>City</th>
                <th>Street</th>
                <th>Zip Code</th>
                <th>Stars</th>
                <th>Rooms</th>
                <th>Email</th>
                <th>Phone</th>
            </tr>
            <%
                for (Map<String, String> hotel : hotels) {
            %>
            <tr>
                <td>
                    <a href="HotelDetailsServlet?hotel_name=<%= hotel.get("hotel_name") %>">
                        <%= hotel.get("hotel_name") %>
                    </a>
                </td>
                <td><%= hotel.get("city") != null ? hotel.get("city") : "N/A" %></td>
                <td><%= hotel.get("street_number") != null ? hotel.get("street_number") : "" %> <%= hotel.get("unit_number") != null ? hotel.get("unit_number") : "" %></td>
                <td><%= hotel.get("zip_code") != null ? hotel.get("zip_code") : "N/A" %></td>
                <td><%= hotel.get("star_rating") %></td>
                <td><%= hotel.get("number_of_rooms") %></td>
                <td><%= hotel.get("contact_email") != null ? hotel.get("contact_email") : "N/A" %></td>
                <td><%= hotel.get("phone_number") != null ? hotel.get("phone_number") : "N/A" %></td>
            </tr>
            <% } %>
        </table>
        <% } %>

        <!-- Booking Archive Section -->
        <%
            List<Map<String, String>> bookingArchives = (List<Map<String, String>>) request.getAttribute("bookingArchives");
            if (bookingArchives != null && !bookingArchives.isEmpty()) {
        %>
        <div class="archive-title">Booking Archive:</div>
        <table>
            <tr>
                <th>Booking Number</th>
                <th>Booking Date</th>
                <th>Check-In Date</th>
                <th>Check-Out Date</th>
                <th>Customer ID</th>
                <th>Room Number</th>
                <th>Hotel Name</th>
                <th>Chain Name</th>
            </tr>
            <%
                for (Map<String, String> booking : bookingArchives) {
            %>
            <tr>
                <td><%= booking.get("booking_number") %></td>
                <td><%= booking.get("BookingDate") != null ? booking.get("BookingDate") : "N/A" %></td>
                <td><%= booking.get("CheckInDate") != null ? booking.get("CheckInDate") : "N/A" %></td>
                <td><%= booking.get("CheckOutDate") != null ? booking.get("CheckOutDate") : "N/A" %></td>
                <td><%= booking.get("customer_id") != null ? booking.get("customer_id") : "N/A" %></td>
                <td><%= booking.get("room_number") != null ? booking.get("room_number") : "N/A" %></td>
                <td><%= booking.get("hotel_name") != null ? booking.get("hotel_name") : "N/A" %></td>
                <td><%= booking.get("chain_name") != null ? booking.get("chain_name") : "N/A" %></td>
            </tr>
            <% } %>
        </table>
        <% } %>

        <!-- Renting Archive Section -->
        <%
            List<Map<String, String>> rentingArchives = (List<Map<String, String>>) request.getAttribute("rentingArchives");
            if (rentingArchives != null && !rentingArchives.isEmpty()) {
        %>
        <div class="archive-title">Renting Archive:</div>
        <table>
            <tr>
                <th>Renting Number</th>
                <th>Rent Date</th>
                <th>Check-In Date</th>
                <th>Check-Out Date</th>
                <th>Customer ID</th>
                <th>Room Number</th>
                <th>Hotel Name</th>
                <th>Chain Name</th>
                <th>Employee SSN</th>
            </tr>
            <%
                for (Map<String, String> renting : rentingArchives) {
            %>
            <tr>
                <td><%= renting.get("renting_number") %></td>
                <td><%= renting.get("rent_date") != null ? renting.get("rent_date") : "N/A" %></td>
                <td><%= renting.get("checkin_date") != null ? renting.get("checkin_date") : "N/A" %></td>
                <td><%= renting.get("checkout_date") != null ? renting.get("checkout_date") : "N/A" %></td>
                <td><%= renting.get("customer_id") != null ? renting.get("customer_id") : "N/A" %></td>
                <td><%= renting.get("room_number") != null ? renting.get("room_number") : "N/A" %></td>
                <td><%= renting.get("hotel_name") != null ? renting.get("hotel_name") : "N/A" %></td>
                <td><%= renting.get("chain_name") != null ? renting.get("chain_name") : "N/A" %></td>
                <td><%= renting.get("employee_SSN") != null ? renting.get("employee_SSN") : "N/A" %></td>
            </tr>
            <% } %>
        </table>
        <% } %>
    </div>
</body>
</html>