<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Room</title>
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
    <script>
        function validateForm() {
            const roomNumber = document.forms["roomForm"]["room_number"].value;
            const price = document.forms["roomForm"]["price"].value;
            const capacity = document.forms["roomForm"]["capacity"].value;
            const viewType = document.forms["roomForm"]["view_type"].value;
            const status = document.forms["roomForm"]["status"].value;

            if (!roomNumber || roomNumber.trim() === "") {
                alert("Room Number is required.");
                return false;
            }
            if (!price || price.trim() === "" || price < 0) {
                alert("Price is required and must be non-negative.");
                return false;
            }
            if (!capacity || capacity.trim() === "") {
                alert("Capacity is required.");
                return false;
            }
            if (!viewType || viewType.trim() === "") {
                alert("View Type is required.");
                return false;
            }
            if (!status || status.trim() === "") {
                alert("Status is required.");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<div class="container">
    <h2>Add Room</h2>
    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>
    <form name="roomForm" action="AddRoomServlet" method="post" onsubmit="return validateForm()">
        <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>">

        <label>Room Number:</label>
        <input type="number" name="room_number" required>

        <label>Price:</label>
        <input type="number" name="price" step="0.01" min="0" required>

        <label>Amenities (e.g., "TV, air condition, fridge"):</label>
        <textarea name="amenities"></textarea>

        <label>Capacity:</label>
        <select name="capacity" required>
            <option value="">Select Capacity</option>
            <option value="single">Single</option>
            <option value="double">Double</option>
            <option value="triple">Triple</option>
            <option value="family">Family</option>
        </select>

        <label>View Type:</label>
        <select name="view_type" required>
            <option value="">Select View Type</option>
            <option value="sea">Sea</option>
            <option value="mountain">Mountain</option>
            <option value="none">None</option>
        </select>

        <label>Expandable:</label>
        <select name="expandable" required>
            <option value="false">No</option>
            <option value="true">Yes</option>
        </select>

        <label>Issues (e.g., "broken window"):</label>
        <textarea name="issues"></textarea>

        <label>Status:</label>
        <select name="status" required>
            <option value="">Select Status</option>
            <option value="available">Available</option>
            <option value="booked">Booked</option>
            <option value="rented">Rented</option>
            <option value="maintenance">Maintenance</option>
        </select>

        <button type="submit">Add Room</button>
    </form>
</div>
</body>
</html>