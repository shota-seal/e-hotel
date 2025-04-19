<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Login Page</title>
    <style>
        body {
            background: #f5f5f5;
            font-family: "Helvetica Neue", sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .login-container {
            background-color: #ffffff;
            padding: 40px 30px;
            border-radius: 12px;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
            width: 350px;
        }

        .login-container h2 {
            margin-bottom: 25px;
            color: #333333;
        }

        label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
            color: #555555;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            border-radius: 6px;
            border: 1px solid #cccccc;
        }

        input[type="submit"] {
            background-color: #007bff;
            color: white;
            border: none;
            margin-top: 20px;
            cursor: pointer;
            transition: background-color 0.3s ease;
        }

        input[type="submit"]:hover {
            background-color: #0056b3;
        }

        .footer {
            margin-top: 20px;
            text-align: center;
            font-size: 13px;
            color: #999999;
        }

        .register-link {
            margin-top: 15px;
            text-align: center;
        }

        .register-link a {
            color: #007bff;
            text-decoration: none;
        }

        .register-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h2>Login Page</h2>

        <% if (error != null) { %>
            <p style="color: red;"><%= error %></p>
        <% } %>

        <form action="LoginServlet" method="post">
            <label>Select login type:</label>
            <select name="role">
                <option value="admin">👑 Admin</option>
                <option value="employee">👔 Employee</option>
                <option value="customer">👤 Customer</option>
            </select>

            <label>Username:</label>
            <input type="text" name="username" required>

            <label>Password:</label>
            <input type="password" name="password" required>

            <input type="submit" value="Login">
        </form>

        <div class="register-link">
            <p>New here? <a href="register.jsp">Create a new customer account</a></p>
        </div>

        <div class="footer">
            &copy; 2025 e-Hotels
        </div>
    </div>
</body>
</html>