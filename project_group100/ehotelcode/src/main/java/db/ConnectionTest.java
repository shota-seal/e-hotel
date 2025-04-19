package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionTest {
    private static final String URL = "jdbc:postgresql://localhost:5432/ehotel";
    private static final String USER = "postgres";
    private static final String PASSWORD = "moti";

    public static Connection connect() {
        Connection conn = null;
        try {

            Class.forName("org.postgresql.Driver");

            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the PostgreSQL database successfully!");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }


    public static void main(String[] args) {
        connect();
    }
}
