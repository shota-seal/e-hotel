<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    List<Map<String, String>> rooms = (List<Map<String, String>>) request.getAttribute("rooms");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Search Available Rooms</title>
    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background: #f2f2f2;
            margin: 0;
            padding: 20px;
        }

        .container {
            max-width: 1000px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 20px rgba(0,0,0,0.1);
        }

        h2 {
            text-align: center;
            color: #333;
        }

        form {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            justify-content: center;
            margin-bottom: 30px;
        }

        label {
            font-weight: bold;
            color: #444;
        }

        input[type="text"], select {
            padding: 8px;
            width: 180px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        input[type="date"] {
            padding: 8px;
            width: 180px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .btn-search {
            background-color: #007bff;
            color: white;
            padding: 10px 16px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            margin-top: 22px;
        }

        .btn-search:hover {
            background-color: #0056b3;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th, td {
            padding: 10px 12px;
            border: 1px solid #ccc;
            text-align: center;
        }

        th {
            background-color: #f8f8f8;
        }

        .no-results {
            text-align: center;
            font-style: italic;
            color: #888;
            margin-top: 20px;
        }

        .back-link {
            display: block;
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
    </style>
</head>
<body>

<div class="container">
    <h2>Search Available Rooms</h2>

    <form action="MakeBookingServlet" method="post">
        <div>
            <label>Min Price:</label><br>
            <input type="text" name="min_price">
        </div>
        <div>
            <label>Max Price:</label><br>
            <input type="text" name="max_price">
        </div>
        <div>
            <label>Capacity:</label><br>
            <select name="capacity">
                <option value="">-- Any --</option>
                <option value="single">Single</option>
                <option value="double">Double</option>
                <option value="triple">Triple</option>
                <option value="family">Family</option>
            </select>
        </div>
        <div>
            <label>View:</label><br>
            <select name="view_type">
                <option value="">-- Any --</option>
                <option value="sea">Sea</option>
                <option value="mountain">Mountain</option>
                <option value="none">None</option>
            </select>
        </div>
        <div>
            <label>Min Hotel Stars:</label><br>
            <select name="min_star">
                <option value="">-- Any --</option>
                <option value="1">★ 1+</option>
                <option value="2">★ 2+</option>
                <option value="3">★ 3+</option>
                <option value="4">★ 4+</option>
                <option value="5">★ 5</option>
            </select>
        </div>
        <div>
            <label>Check-in:</label><br>
            <input type="date" name="checkin_date" required>
        </div>
        <div>
            <label>Check-out:</label><br>
            <input type="date" name="checkout_date" required>
        </div>
        <div>
            <label>Hotel Chain:</label><br>
            <select name="hotel_chain">
                <option value="">-- Any --</option>
                <option value="Hilton">Hilton</option>
                <option value="Marriott">Marriott</option>
                <option value="Hyatt">Hyatt</option>
                <option value="Sheraton">Sheraton</option>
                <option value="Best Western">Best Western</option>
            </select>
        </div>
        <div>
            <label>City:</label><br>
            <select name="city">
                <option value="">-- Any --</option>
                <option value="New York">New York</option>
                <option value="Los Angeles">Los Angeles</option>
                <option value="Chicago">Chicago</option>
                <option value="Miami">Miami</option>
                <option value="Toronto">Toronto</option>
                <option value="Vancouver">Vancouver</option>
            </select>
        </div>
        <div>
            <br>
            <input type="submit" value="Search" class="btn-search">
        </div>
    </form>

    <% if (rooms != null && !rooms.isEmpty()) { %>
        <table>
            <tr>
                <th>Room Number</th>
                <th>Hotel</th>
                <th>Chain</th>
                <th>City</th>
                <th>Price</th>
                <th>Capacity</th>
                <th>View</th>
                <th>Star Rating</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            <% for (Map<String, String> room : rooms) { %>
                <tr>
                    <td><%= room.get("room_number") %></td>
                    <td><%= room.get("hotel_name") %></td>
                    <td><%= room.get("chain_name") %></td>
                    <td><%= room.get("city") %></td>
                    <td>$<%= room.get("price") %></td>
                    <td><%= room.get("capacity") %></td>
                    <td><%= room.get("view_type") %></td>
                    <td><%= room.get("star_rating") != null ? room.get("star_rating") : "N/A" %></td>
                    <td><%= room.get("status") %></td>
                    <td>
                       <form action="RoomDetailsServlet" method="get">
                            <input type="hidden" name="room_number" value="<%= room.get("room_number") %>">
                            <input type="hidden" name="hotel_name" value="<%= room.get("hotel_name") %>">
                            <button type="submit">Book</button>
                       </form>
                    </td>
                </tr>
            <% } %>
        </table>
    <% } else { %>
        <p class="no-results">No rooms found for selected criteria.</p>
    <% } %>

    <div class="back-link">
        <a href="CustomerDashboardServlet">← Back to Dashboard</a>
    </div>
</div>

</body>
</html>