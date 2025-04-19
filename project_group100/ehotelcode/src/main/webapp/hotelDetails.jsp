<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hotel Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        h2 {
            margin-top: 20px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f2f2f2;
        }
        button {
            padding: 5px 10px;
            margin-right: 5px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .edit-btn {
            background-color: #007bff;
            color: white;
        }
        .delete-btn {
            background-color: #dc3545;
            color: white;
        }
        .logout-btn {
            background-color: #6c757d;
            color: white;
        }
        .error {
            color: red;
            margin-bottom: 10px;
        }
        .nav-buttons {
            margin-bottom: 20px;
        }
    </style>
    <script>
        function confirmDelete(message) {
            return confirm(message);
        }
        function confirmLogout() {
            return confirm("Are you sure you want to logout?");
        }
    </script>
</head>
<body>
<div class="nav-buttons">
    <button class="logout-btn" onclick="if(confirmLogout()) location.href='LogoutServlet'">Logout</button>
</div>

<h2>Rooms</h2>
<button onclick="location.href='addRoom.jsp?hotel_name=<%= request.getAttribute("hotel_name") %>'">Add Room</button>
<% if (request.getAttribute("error") != null) { %>
    <p class="error"><%= request.getAttribute("error") %></p>
<% } %>
<table>
    <tr>
        <th>Room Number</th>
        <th>Price</th>
        <th>Amenities</th>
        <th>Capacity</th>
        <th>View Type</th>
        <th>Expandable</th>
        <th>Issues</th>
        <th>Status</th>
        <th>Actions</th>
    </tr>
    <%
        List<Map<String, String>> rooms = (List<Map<String, String>>) request.getAttribute("rooms");
        if (rooms != null) {
            for (Map<String, String> room : rooms) {
    %>
    <tr>
        <td><%= room.get("room_number") %></td>
        <td><%= room.get("price") %></td>
        <td><%= room.get("amenities") %></td>
        <td><%= room.get("capacity") %></td>
        <td><%= room.get("view_type") %></td>
        <td><%= room.get("expandable") %></td>
        <td><%= room.get("issues") %></td>
        <td><%= room.get("status") %></td>
        <td>
            <button class="edit-btn" onclick="location.href='EditRoomServlet?hotel_name=<%= request.getAttribute("hotel_name") %>&room_number=<%= room.get("room_number") %>'">Edit</button>
            <form action="DeleteRoomServlet" method="post" style="display:inline;" onsubmit="return confirmDelete('Are you sure you want to delete this room?');">
                <input type="hidden" name="hotel_name" value="<%= request.getAttribute("hotel_name") %>">
                <input type="hidden" name="room_number" value="<%= room.get("room_number") %>">
                <button type="submit" class="delete-btn">Delete</button>
            </form>
        </td>
    </tr>
    <%      }
        }
    %>
</table>

<h2>Employees</h2>
<button onclick="location.href='addEmployee.jsp?hotel_name=<%= request.getAttribute("hotel_name") %>'">Add Employee</button>
<table>
    <tr>
        <th>SSN</th>
        <th>Name</th>
        <th>Role</th>
        <th>Actions</th>
    </tr>
    <%
        List<Map<String, String>> employees = (List<Map<String, String>>) request.getAttribute("employees");
        if (employees != null) {
            for (Map<String, String> employee : employees) {
    %>
    <tr>
        <td><%= employee.get("SSN") %></td>
        <td><%= employee.get("name") %></td>
        <td><%= employee.get("role") %></td>
        <td>
            <button class="edit-btn" onclick="location.href='EditEmployeeServlet?hotel_name=<%= request.getAttribute("hotel_name") %>&SSN=<%= employee.get("SSN") %>'">Edit</button>
            <form action="DeleteEmployeeServlet" method="post" style="display:inline;" onsubmit="return confirmDelete('Are you sure you want to delete this employee?');">
                <input type="hidden" name="hotel_name" value="<%= request.getAttribute("hotel_name") %>">
                <input type="hidden" name="SSN" value="<%= employee.get("SSN") %>">
                <button type="submit" class="delete-btn">Delete</button>
            </form>
        </td>
    </tr>
    <%      }
        }
    %>
</table>
</body>
</html>