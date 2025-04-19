<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>Hotel List</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            padding: 40px;
            background-color: #f9f9f9;
        }

        h2 {
            color: #333;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background-color: #fff;
            border-radius: 8px;
            overflow: hidden;
        }

        th, td {
            padding: 12px 15px;
            border-bottom: 1px solid #ccc;
            text-align: left;
        }

        th {
            background-color: #f0f0f0;
            font-weight: bold;
        }

        tr:hover {
            background-color: #f5f5f5;
        }

        a {
            color: #007bff;
            text-decoration: none;
        }

        a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <h2>Hotels under selected chain</h2>

    <%
        List<Map<String, String>> hotels = (List<Map<String, String>>) request.getAttribute("hotels");
        if (hotels == null || hotels.isEmpty()) {
    %>
        <p>No hotels found for this chain.</p>
    <%
        } else {
    %>
        <table>
            <tr>
                <th>Hotel Name</th>
                <th>City</th>
                <th>Country</th>
                <th>Star Rating</th>
            </tr>
            <%
                for (Map<String, String> hotel : hotels) {
            %>
            <tr>
                <td>
                    <a href="HotelDetailsServlet?hotel_name=<%= hotel.get("hotel_name") %>">
                        <%= hotel.get("hotel_name") %>
                    </a>
                </td>
                <td><%= hotel.get("city") %></td>
                <td><%= hotel.get("country") != null ? hotel.get("country") : "-" %></td>
                <td><%= hotel.get("star_rating") %></td>
            </tr>
            <%
                }
            %>
        </table>
    <%
        }
    %>
</body>
</html>
