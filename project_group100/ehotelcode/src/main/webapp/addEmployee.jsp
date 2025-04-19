<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Add Employee</title>
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
    <script>
        function validateForm() {
            const ssn = document.forms["employeeForm"]["SSN"].value;
            const firstName = document.forms["employeeForm"]["first_name"].value;
            const lastName = document.forms["employeeForm"]["last_name"].value;
            const role = document.forms["employeeForm"]["role"].value;

            if (!ssn || ssn.trim() === "") {
                alert("SSN is required.");
                return false;
            }
            if (!firstName || firstName.trim() === "") {
                alert("First Name is required.");
                return false;
            }
            if (!lastName || lastName.trim() === "") {
                alert("Last Name is required.");
                return false;
            }
            if (!role || role.trim() === "") {
                alert("Role is required.");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<div class="container">
    <h2>Add Employee</h2>
    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>
    <form name="employeeForm" action="AddEmployeeServlet" method="post" onsubmit="return validateForm()">
        <input type="hidden" name="hotel_name" value="<%= request.getParameter("hotel_name") %>">
        <label>SSN:</label>
        <input type="text" name="SSN" required>
        <label>First Name:</label>
        <input type="text" name="first_name" required>
        <label>Middle Name:</label>
        <input type="text" name="mid_name">
        <label>Last Name:</label>
        <input type="text" name="last_name" required>
        <label>Country:</label>
        <input type="text" name="country">
        <label>City:</label>
        <input type="text" name="city">
        <label>Street Number:</label>
        <input type="text" name="street_number">
        <label>Unit Number:</label>
        <input type="text" name="unit_number">
        <label>Zip Code:</label>
        <input type="text" name="zip_code">
        <label>Role:</label>
        <select name="role" required>
            <option value="">Select Role</option>
            <option value="manager">Manager</option>
            <option value="staff">Staff</option>
        </select>
        <label>Manager SSN (Optional):</label>
        <input type="text" name="manager_SSN">
        <button type="submit">Add Employee</button>
    </form>
</div>
</body>
</html>