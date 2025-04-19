<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Confirm Payment</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f8f8f8;
            padding: 30px;
        }
        .container {
            max-width: 600px;
            margin: auto;
            background: #fff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
        }
        h2 {
            color: #333;
            text-align: center;
        }
        .info div {
            margin: 10px 0;
        }
        label {
            font-weight: bold;
            margin-right: 8px;
        }
        .btn {
            padding: 6px 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #0056b3;
        }
        .error {
            color: red;
            text-align: center;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>Confirm Payment</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>

    <div class="info">
        <div><label>Rental ID:</label><%= request.getAttribute("rental_id") %></div>
        <div><label>Total Days:</label><%= request.getAttribute("total_days") %></div>
        <div><label>Price per Day:</label>$<%= request.getAttribute("price_per_day") %></div>
        <div><label>Total Amount:</label>$<%= request.getAttribute("total_amount") %></div>
    </div>

    <form action="RegisterPaymentServlet" method="post">
        <input type="hidden" name="rental_id" value="<%= request.getAttribute("rental_id") %>">
        <label>Amount:</label>
        <input type="number" step="0.01" name="amount" required>
        <button class="btn" type="submit">Confirm Payment</button>
    </form>
</div>
</body>
</html>