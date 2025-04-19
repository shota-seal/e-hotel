<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.Map" %>
<%
    Map<String, String> room = (Map<String, String>) request.getAttribute("room");
    String error = (String) request.getAttribute("error");
    if (room == null) {

        if (error == null) {
            error = "Room details are unavailable.";
        }
%>
        <p style="color: red; text-align: center;"><%= error %></p>
        <p style="text-align: center;"><a href="makeBooking.jsp">Back to Booking</a></p>
<%
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Room Details</title>
    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background-color: #f9f9f9;
            padding: 40px;
        }

        .container {
            max-width: 600px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: #333;
        }

        .detail {
            margin: 15px 0;
        }

        .detail label {
            font-weight: bold;
            display: inline-block;
            width: 150px;
        }

        .detail span {
            color: #555;
        }

        .book-btn {
            display: block;
            width: 100%;
            margin-top: 30px;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            font-size: 16px;
            border-radius: 6px;
            cursor: pointer;
        }

        .book-btn:hover {
            background-color: #0056b3;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            font-weight: bold;
        }

        input[type="date"] {
            padding: 8px;
            width: 100%;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .back-link {
            text-align: center;
            margin-top: 30px;
        }

        .back-link a {
            color: #007bff;
            text-decoration: none;
        }

        .back-link a:hover {
            text-decoration: underline;
        }

        .error-message {
            color: red;
            text-align: center;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<div class="container">
    <h2>Room Details</h2>

    <% if (error != null) { %>
        <p class="error-message"><%= error %></p>
    <% } %>

    <div class="detail"><label>Room Number:</label><span><%= room.get("room_number") %></span></div>
    <div class="detail"><label>Hotel:</label><span><%= room.get("hotel_name") %></span></div>
    <div class="detail"><label>Price Per Day:</label><span>$<%= room.get("price") %></span></div>
    <div class="detail"><label>Capacity:</label><span><%= room.get("capacity") %></span></div>
    <div class="detail"><label>View:</label><span><%= room.get("view_type") %></span></div>
    <div class="detail"><label>Extendable:</label><span><%= room.get("expandable") %></span></div>
    <div class="detail"><label>Status:</label><span><%= room.get("status") %></span></div>
    <div class="detail"><label>Star Rating:</label><span><%= room.get("star_rating") %> ★</span></div>

    <form class="booking-form" action="BookingServlet" method="post">
        <input type="hidden" name="room_number" value="<%= room.get("room_number") %>">
        <input type="hidden" name="hotel_name" value="<%= room.get("hotel_name") %>">

        <div class="form-group">
            <label>Start Date:</label>
            <input type="date" name="start_date" required>
        </div>
        <div class="form-group">
            <label>End Date:</label>
            <input type="date" name="end_date" required>
        </div>

        <button class="book-btn" type="submit">Book Room</button>
    </form>

    <div class="back-link">
        <a href="CustomerDashboardServlet">← Back to Dashboard</a>
    </div>
</div>

</body>
</html>