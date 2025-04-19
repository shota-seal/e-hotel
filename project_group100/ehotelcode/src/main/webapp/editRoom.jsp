<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Room</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        .container {
            max-width: 600px;
            margin: auto;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        label {
            display: block;
            margin: 10px 0 5px;
        }
        input, select, textarea {
            width: 100%;
            padding: 8px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        button {
            padding: 10px 20px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #0056b3;
        }
        .error {
            color: red;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Edit Room</h2>
    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>
    <form action="EditRoomServlet" method="post">
        <input type="hidden" name="hotel_name" value="<%= request.getAttribute("hotel_name") %>">
        <input type="hidden" name="room_number" value="<%= request.getAttribute("room_number") %>">
        <label>Room Number:</label>
        <input type="text" value="<%= request.getAttribute("room_number") %>" disabled>
        <label>Price:</label>
        <input type="number" name="price" value="<%= request.getAttribute("price") %>" step="0.01" required>
        <label>Amenities:</label>
        <textarea name="amenities"><%= request.getAttribute("amenities") != null ? request.getAttribute("amenities") : "" %></textarea>
        <label>Capacity:</label>
        <select name="capacity" required>
            <option value="single" <%= "single".equals(request.getAttribute("capacity")) ? "selected" : "" %>>Single</option>
            <option value="double" <%= "double".equals(request.getAttribute("capacity")) ? "selected" : "" %>>Double</option>
            <option value="triple" <%= "triple".equals(request.getAttribute("capacity")) ? "selected" : "" %>>Triple</option>
            <option value="family" <%= "family".equals(request.getAttribute("capacity")) ? "selected" : "" %>>Family</option>
        </select>
        <label>View Type:</label>
        <select name="view_type" required>
            <option value="sea" <%= "sea".equals(request.getAttribute("view_type")) ? "selected" : "" %>>Sea</option>
            <option value="mountain" <%= "mountain".equals(request.getAttribute("view_type")) ? "selected" : "" %>>Mountain</option>
            <option value="none" <%= "none".equals(request.getAttribute("view_type")) ? "selected" : "" %>>None</option>
        </select>
        <label>Expandable:</label>
        <select name="expandable" required>
            <option value="Yes" <%= "true".equals(String.valueOf(request.getAttribute("expandable"))) ? "selected" : "" %>>Yes</option>
            <option value="No" <%= "false".equals(String.valueOf(request.getAttribute("expandable"))) ? "selected" : "" %>>No</option>
        </select>
        <label>Issues:</label>
        <textarea name="issues"><%= request.getAttribute("issues") != null ? request.getAttribute("issues") : "" %></textarea>
        <label>Status:</label>
        <select name="status" required>
            <option value="available" <%= "available".equals(request.getAttribute("status")) ? "selected" : "" %>>Available</option>
            <option value="booked" <%= "booked".equals(request.getAttribute("status")) ? "selected" : "" %>>Booked</option>
            <option value="rented" <%= "rented".equals(request.getAttribute("status")) ? "selected" : "" %>>Rented</option>
            <option value="maintenance" <%= "maintenance".equals(request.getAttribute("status")) ? "selected" : "" %>>Maintenance</option>
        </select>
        <button type="submit">Save</button>
    </form>
</div>
</body>
</html>