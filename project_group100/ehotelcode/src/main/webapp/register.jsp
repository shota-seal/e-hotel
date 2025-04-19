<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Customer Registration</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f5f7fa;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }
        .container {
            background-color: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.1);
            width: 400px;
        }
        h2 {
            text-align: center;
            margin-bottom: 25px;
        }
        label {
            font-weight: bold;
        }
        input[type="text"], input[type="number"], select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        input[type="submit"] {
            width: 100%;
            padding: 10px;
            background-color: #28a745;
            color: white;
            font-weight: bold;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }
        input[type="submit"]:hover {
            background-color: #218838;
        }
        .error {
            color: red;
            text-align: center;
            margin-bottom: 15px;
        }
        .back-link {
            text-align: center;
            margin-top: 20px;
        }
        .back-link a {
            color: #007bff;
            text-decoration: none;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Customer Registration</h2>

    <% if (request.getAttribute("error") != null) { %>
        <div class="error">Error: <%= request.getAttribute("error") %></div>
    <% } %>

    <form action="RegisterCustomerServlet" method="post">
        <label>User ID (numbers only):</label>
        <input type="number" name="customer_id" required>

        <label>First Name:</label>
        <input type="text" name="first_name" required>

        <label>Mid Name:</label>
        <input type="text" name="mid_name">

        <label>Last Name:</label>
        <input type="text" name="last_name">

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

        <label>ID Type:</label>
        <select name="id_type" required>
            <option value="">-- Select ID Type --</option>
            <option value="SSN">SSN</option>
            <option value="SIN">SIN</option>
            <option value="driving_license">driving_license</option>
        </select>

        <input type="submit" value="Register">
    </form>

    <div class="back-link">
        <a href="index.jsp">&larr; Back to Login</a>
    </div>
</div>
</body>
</html>
