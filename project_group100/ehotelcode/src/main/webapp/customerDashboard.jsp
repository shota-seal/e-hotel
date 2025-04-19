<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    String firstName = (String) request.getAttribute("first_name");
    if (firstName == null) {
        firstName = "Customer";
    }

    List<Map<String, String>> bookings = (List<Map<String, String>>) request.getAttribute("bookings");
    String success = (String) session.getAttribute("success");
    if (success != null) {
        session.removeAttribute("success");
    }
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Dashboard</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f8f8f8;
            padding: 0;
            margin: 0;
        }

        .container {
            max-width: 900px;
            margin: 50px auto;
            background: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
        }

        h1 {
            text-align: center;
            color: #333;
        }

        .actions {
            text-align: center;
            margin: 20px 0;
        }

        .actions a {
            padding: 10px 20px;
            background-color: #28a745;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 16px;
        }

        .actions a:hover {
            background-color: #218838;
        }

        table {
            width: 100%;
            margin-top: 30px;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            border: 1px solid #ddd;
            text-align: center;
        }

        th {
            background-color: #f1f1f1;
        }

        .no-booking {
            text-align: center;
            font-style: italic;
            margin-top: 20px;
            color: #888;
        }

        .logout-link {
            text-align: center;
            margin-top: 30px;
        }

        .logout-link a {
            color: #007bff;
            text-decoration: none;
        }

        .logout-link a:hover {
            text-decoration: underline;
        }

        .success-message {
            color: green;
            text-align: center;
            margin-bottom: 20px;
        }

        .error-message {
            color: red;
            text-align: center;
            margin-bottom: 20px;
        }

        .delete-btn {
            background-color: #dc3545;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 4px;
            cursor: pointer;
        }

        .delete-btn:hover {
            background-color: #c82333;
        }
    </style>
</head>
<body>

<div class="container">
    <h1>Welcome, <%= firstName %>!</h1>

    <% if (success != null) { %>
        <p class="success-message"><%= success %></p>
    <% } %>
    <% if (error != null) { %>
        <p class="error-message"><%= error %></p>
    <% } %>

    <div class="actions">
        <a href="MakeBookingServlet">➕ Make a Booking</a>
    </div>

    <h2>Your Bookings</h2>

    <table>
        <tr>
            <th>Booking ID</th>
            <th>Hotel</th>
            <th>Room</th>
            <th>Check-in</th>
            <th>Check-out</th>
            <th>Action</th>
        </tr>
        <% if (bookings != null && !bookings.isEmpty()) { %>
            <% for (Map<String, String> booking : bookings) { %>
                <tr>
                    <td><%= booking.get("booking_id") %></td>
                    <td><%= booking.get("hotel_name") %></td>
                    <td><%= booking.get("room_number") %></td>
                    <td><%= booking.get("checkin_date") %></td>
                    <td><%= booking.get("checkout_date") %></td>
                    <td>
                        <form action="DeleteBookingServlet" method="post" onsubmit="return confirm('Are you sure you want to delete this booking?');">
                            <input type="hidden" name="booking_id" value="<%= booking.get("booking_id") %>">
                            <button type="submit" class="delete-btn">Delete</button>
                        </form>
                    </td>
                </tr>
            <% } %>
        <% } else { %>
            <tr>
                <td colspan="6" class="no-booking">You have no bookings yet.</td>
            </tr>
        <% } %>
    </table>

    <div class="logout-link">
        <a href="LogoutServlet">← Logout</a>
    </div>
</div>

</body>
</html>