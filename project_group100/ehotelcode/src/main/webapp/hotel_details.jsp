<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hotel Details</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 40px;
            background-color: #f5f5f5;
        }

        h1 {
            color: #333;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 30px;
            background-color: #fff;
            border-radius: 8px;
            overflow: hidden;
        }

        th, td {
            padding: 12px 15px;
            border-bottom: 1px solid #ddd;
            text-align: left;
        }

        th {
            background-color: #f0f0f0;
            font-weight: bold;
        }

        h2 {
            margin-top: 50px;
            color: #444;
        }

        tr:hover {
            background-color: #f9f9f9;
        }

        button {
            margin-top: 10px;
            padding: 10px 15px;
            font-size: 14px;
            cursor: pointer;
        }

        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            padding-top: 80px;
            left: 0; top: 0;
            width: 100%; height: 100%;
            overflow: auto;
            background-color: rgba(0,0,0,0.4);
        }

        .modal-content {
            background-color: #fff;
            margin: auto;
            padding: 30px;
            border-radius: 10px;
            width: 400px;
            box-shadow: 0 8px 16px rgba(0,0,0,0.2);
            position: relative;
        }

        .modal-content h3 {
            margin-bottom: 20px;
            color: #222;
            font-size: 22px;
        }

        .modal-content label {
            display: block;
            margin-top: 10px;
            font-weight: 500;
            color: #333;
        }

        .modal-content input, .modal-content select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            border: 1px solid #ccc;
            border-radius: 6px;
            box-sizing: border-box;
        }

        .modal-content button[type="submit"] {
            margin-top: 20px;
            width: 100%;
            background-color: #4CAF50;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 6px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .modal-content button[type="submit"]:hover {
            background-color: #45a049;
        }

        .close {
            position: absolute;
            top: 15px;
            right: 20px;
            font-size: 24px;
            color: #999;
            cursor: pointer;
        }

        .close:hover {
            color: #333;
        }
    </style>


</head>
<body>
    <h1>Hotel Details</h1>

    <h2>Rooms</h2>
    <button onclick="document.getElementById('addRoomModal').style.display='block'">Add Room</button>
    <table>
        <tr>
            <th>Room Number</th>
            <th>Price</th>
            <th>Capacity</th>
            <th>View</th>
            <th>Problems</th>
            <th>Extendable</th>
            <th>Actions</th>
        </tr>
        <%
            List<Map<String, String>> rooms = (List<Map<String, String>>) request.getAttribute("rooms");
            if (rooms != null && !rooms.isEmpty()) {
                for (Map<String, String> room : rooms) {
        %>
        <tr>
            <td><%= room.get("room_number") %></td>
            <td>$<%= room.get("price") %></td>
            <td><%= room.get("capacity") %></td>
            <td><%= room.get("view_type") %></td>
            <td><%= room.get("problems") %></td>
            <td><%= room.get("extendable") %></td>
            <td>
                <form action="EditRoomServlet" method="post" style="display:inline">
                    <input type="hidden" name="room_number" value="<%= room.get("room_number") %>">
                    <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>">
                    <button type="submit">Edit</button>
                </form>
                <form action="DeleteRoomServlet" method="post" style="display:inline">
                    <input type="hidden" name="room_number" value="<%= room.get("room_number") %>">
                    <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>">
                    <button type="submit">Delete</button>
                </form>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr><td colspan="7">No rooms available for this hotel.</td></tr>
        <%
            }
        %>
    </table>

    <h2>Employees</h2>
    <button onclick="document.getElementById('addEmpModal').style.display='block'">Add Employee</button>
    <table>
        <tr>
            <th>Employee ID</th>
            <th>Name</th>
            <th>Role</th>
            <th>Email</th>
            <th>Actions</th>
        </tr>
        <%
            List<Map<String, String>> employees = (List<Map<String, String>>) request.getAttribute("employees");
            if (employees != null && !employees.isEmpty()) {
                for (Map<String, String> emp : employees) {
        %>
        <tr>
            <td><%= emp.get("employee_id") %></td>
            <td><%= emp.get("name") %></td>
            <td><%= emp.get("role") %></td>
            <td><%= emp.get("email") %></td>
            <td>
                <form action="EditEmployeeServlet" method="post" style="display:inline">
                    <input type="hidden" name="ssn" value="<%= emp.get("employee_id") %>">
                    <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>">
                    <button type="submit">Edit</button>
                </form>
                <form action="DeleteEmployeeServlet" method="post" style="display:inline">
                    <input type="hidden" name="ssn" value="<%= emp.get("employee_id") %>">
                    <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>">
                    <button type="submit">Delete</button>
                </form>
            </td>
        </tr>
        <%
                }
            } else {
        %>
        <tr><td colspan="5">No employees found for this hotel.</td></tr>
        <%
            }
        %>
    </table>



<!-- Room Modal -->
<div id="addRoomModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="document.getElementById('addRoomModal').style.display='none'">&times;</span>
        <h3>Add New Room</h3>
        <form action="AddRoomServlet" method="post">
            <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>" />

            <label>Room Number:</label>
            <input type="number" name="room_number" required />

            <label>Price:</label>
            <input type="text" name="price" required />

            <label>Capacity:</label>
            <select name="capacity" required>
                <option value="single">single</option>
                <option value="double">double</option>
                <option value="triple">triple</option>
                <option value="family">family</option>
            </select>

            <label>View Type:</label>
            <select name="view_type" required>
                <option value="sea">sea</option>
                <option value="mountain">mountain</option>
                <option value="none">none</option>
            </select>

            <label>Problems:</label>
            <input type="text" name="problems" />

            <label>Extendable:</label>
            <select name="extendable" required>
                <option value="true">Yes</option>
                <option value="false">No</option>
            </select>

            <button type="submit">Submit</button>
        </form>
    </div>
</div>

<!-- Employee Modal -->
<div id="addEmpModal" class="modal">
    <div class="modal-content">
        <span class="close" onclick="document.getElementById('addEmpModal').style.display='none'">&times;</span>
        <h3>Add New Employee</h3>
        <form action="AddEmployeeServlet" method="post">
            <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>" />

            <label>SSN:</label>
            <input type="text" name="ssn" required />

            <label>First Name:</label>
            <input type="text" name="first_name" required />

            <label>Last Name:</label>
            <input type="text" name="last_name" required />

            <label>Role:</label>
            <select name="role">
                <option value="staff">Staff</option>
                <option value="manager">Manager</option>
            </select>

            <button type="submit">Submit</button>
        </form>
    </div>
</div>


<script>
    window.onclick = function(event) {
        const modals = ['addRoomModal', 'addEmpModal'];
        modals.forEach(id => {
            const modal = document.getElementById(id);
            if (event.target === modal) modal.style.display = "none";
        });
    }
</script>
</body>
</html>


