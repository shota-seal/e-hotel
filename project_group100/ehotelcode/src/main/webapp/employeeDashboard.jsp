<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List, java.util.Map" %>
<%
    Map<String, String> employee = (Map<String, String>) request.getAttribute("employee");
    List<Map<String, String>> bookings = (List<Map<String, String>>) request.getAttribute("bookings");
    String contextPath = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
    <title>Employee Dashboard</title>
    <style>
        body {
            font-family: 'Segoe UI', sans-serif;
            background: #f8f8f8;
            padding: 30px;
        }

        .container {
            max-width: 1100px;
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

        .info, .bookings {
            margin-top: 30px;
        }

        .info div {
            margin: 5px 0;
        }

        label {
            font-weight: bold;
            margin-right: 8px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th, td {
            border: 1px solid #ccc;
            padding: 12px;
            text-align: center;
        }

        th {
            background-color: #f0f0f0;
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

        .actions form {
            display: inline;
        }

        .section-title {
            margin-top: 40px;
            font-size: 20px;
            font-weight: bold;
            color: #444;
        }

        .error {
            color: white;
            background-color: #ff3333;
            padding: 10px;
            border-radius: 5px;
            text-align: center;
            margin-bottom: 20px;
        }
    </style>
    <script>
        function updateRoomList() {
            const hotelName = document.querySelector('input[name="hotel_name"]').value;
            let checkinDate = document.querySelector('input[name="checkin_date"]').value;
            let checkoutDate = document.querySelector('input[name="checkout_date"]').value;
            const roomSelect = document.querySelector('select[name="room_number"]');


            if (checkinDate.includes('/')) {
                checkinDate = checkinDate.replace(/\//g, '-');
            }
            if (checkoutDate.includes('/')) {
                checkoutDate = checkoutDate.replace(/\//g, '-');
            }

            if (hotelName && checkinDate && checkoutDate) {

                const url = "<%= contextPath %>/WalkInRentalServlet?hotel_name=" + encodeURIComponent(hotelName) +
                            "&checkin_date=" + checkinDate +
                            "&checkout_date=" + checkoutDate;

                fetch(url, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok: ' + response.statusText);
                    }
                    return response.json();
                })
                .then(data => {
                    if (data.error) {
                        alert(data.error);
                        roomSelect.innerHTML = '<option value="">No rooms available</option>';
                    } else {
                        roomSelect.innerHTML = '';
                        if (data.length === 0) {
                            roomSelect.innerHTML = '<option value="">No rooms available</option>';
                        } else {
                            data.forEach(room => {
                                const option = document.createElement('option');
                                option.value = room.room_number;
                                option.textContent = room.room_number;
                                roomSelect.appendChild(option);
                            });
                        }
                    }
                })
                .catch(error => {
                    console.error('Error fetching rooms:', error);
                    alert('Failed to load available rooms: ' + error.message);
                    roomSelect.innerHTML = '<option value="">Error loading rooms</option>';
                });
            }
        }
    </script>
</head>
<body>

<div class="container">
    <h2>Employee Dashboard</h2>

    <% if (request.getAttribute("error") != null) { %>
        <p class="error"><%= request.getAttribute("error") %></p>
    <% } %>

    <div class="info">
        <div><label>Name:</label><%= employee.get("first_name") %> <%= employee.get("last_name") %></div>
        <div><label>Role:</label><%= employee.get("role") %></div>
        <div><label>Hotel:</label><%= employee.get("hotel_name") %></div>
    </div>

    <div class="bookings">
        <div class="section-title">Customer Bookings</div>

        <% if (bookings != null && !bookings.isEmpty()) { %>
            <table>
                <tr>
                    <th>Booking ID</th>
                    <th>Customer ID</th>
                    <th>Room</th>
                    <th>Check-in</th>
                    <th>Check-out</th>
                    <th>Action</th>
                </tr>
                <% for (Map<String, String> b : bookings) { %>
                <tr>
                    <td><%= b.get("booking_number") %></td>
                    <td><%= b.get("customer_id") %></td>
                    <td><%= b.get("room_number") %></td>
                    <td><%= b.get("check_in") %></td>
                    <td><%= b.get("check_out") %></td>
                    <td class="actions">
                        <form action="ConvertToRentalServlet" method="post">
                            <input type="hidden" name="booking_number" value="<%= b.get("booking_number") %>">
                            <button class="btn" type="submit">Convert to Rental</button>
                        </form>
                    </td>
                </tr>
                <% } %>
            </table>
        <% } else { %>
            <p style="text-align:center; font-style:italic; color: #777;">No bookings found.</p>
        <% } %>
    </div>

    <div class="section-title">Current Rentals</div>
    <%
        List<Map<String, String>> rentings = (List<Map<String, String>>) request.getAttribute("rentings");
        if (rentings != null && !rentings.isEmpty()) {
    %>
        <table>
            <tr>
                <th>Rental ID</th>
                <th>Customer</th>
                <th>Room</th>
                <th>Hotel</th>
                <th>Rent Date</th>
                <th>Check-in</th>
                <th>Check-out</th>
            </tr>
            <% for (Map<String, String> r : rentings) { %>
            <tr>
                <td><%= r.get("renting_number") %></td>
                <td><%= r.get("first_name") %> <%= r.get("last_name") %></td>
                <td><%= r.get("room_number") %></td>
                <td><%= r.get("hotel_name") %></td>
                <td><%= r.get("rent_date") %></td>
                <td><%= r.get("checkin_date") %></td>
                <td><%= r.get("checkout_date") %></td>
            </tr>
            <% } %>
        </table>
    <% } else { %>
        <p style="text-align: center; font-style: italic;">No rentals found.</p>
    <% } %>

    <div class="section-title">Walk-in Rental</div>
    <form action="WalkInRentalServlet" method="post">
        <label>Customer ID:</label>
        <input type="text" name="customer_id" required>
        <label>Room Number:</label>
        <select name="room_number" required>
            <%
                List<Map<String, String>> availableRooms = (List<Map<String, String>>) request.getAttribute("available_rooms");
                if (availableRooms != null && !availableRooms.isEmpty()) {
                    for (Map<String, String> room : availableRooms) {
            %>
                        <option value="<%= room.get("room_number") %>"><%= room.get("room_number") %></option>
            <%
                    }
                } else {
            %>
                        <option value="">No rooms available</option>
            <%
                }
            %>
        </select>
        <label>Hotel Name:</label>
        <input type="text" name="hotel_name" value="<%= employee.get("hotel_name") %>" readonly required>
        <label>Check-in:</label>
        <input type="date" name="checkin_date" required onblur="updateRoomList()">
        <label>Check-out:</label>
        <input type="date" name="checkout_date" required onblur="updateRoomList()">
        <button class="btn" type="submit">Create Rental</button>
    </form>

    <div class="section-title">Register Payment</div>
    <form action="RegisterPaymentServlet" method="get">
        <label>Rental ID:</label>
        <input type="text" name="rental_id" required>
        <button class="btn" type="submit">Calculate Total</button>
    </form>

    <div style="text-align: right; margin-bottom: 10px;">
        <form action="LogoutServlet" method="get" style="display: inline;">
            <button type="submit" class="btn" style="background-color: #dc3545;">Logout</button>
        </form>
    </div>
</div>

</body>
</html>