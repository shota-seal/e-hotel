<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Employee</title>
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
        input, select {
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
    <h2>Edit Employee</h2>
    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>
    <form action="EditEmployeeServlet" method="post">
        <input type="hidden" name="hotel_name" value="<%= request.getAttribute("hotel_name") %>">
        <input type="hidden" name="SSN" value="<%= request.getAttribute("SSN") %>">
        <label>SSN:</label>
        <input type="text" value="<%= request.getAttribute("SSN") %>" disabled>
        <label>First Name:</label>
        <input type="text" name="first_name" value="<%= request.getAttribute("first_name") %>" required>
        <label>Last Name:</label>
        <input type="text" name="last_name" value="<%= request.getAttribute("last_name") %>" required>
        <label>Role:</label>
        <select name="role" required>
            <option value="manager" <%= "manager".equals(request.getAttribute("role")) ? "selected" : "" %>>Manager</option>
            <option value="staff" <%= "staff".equals(request.getAttribute("role")) ? "selected" : "" %>>Staff</option>
        </select>
        <button type="submit">Save</button>
    </form>
</div>
</body>
</html>