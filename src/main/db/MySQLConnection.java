package db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MySQLConnection {

    private String url;
    private String user;
    private String password;
    private Connection connection;

    public MySQLConnection() {
        // Automatically load the database credentials from application.properties
        loadDatabaseCredentials();
    }

    private void loadDatabaseCredentials() {
        try {
            // ✅ Hardcoded the path to the application.properties file
            FileInputStream fis = new FileInputStream("bin/resources/application.properties");
            Properties properties = new Properties();
            properties.load(fis);

            // Assign properties to variables
            this.url = properties.getProperty("db.url");
            this.user = properties.getProperty("db.username");
            this.password = properties.getProperty("db.password");

        } catch (FileNotFoundException e) {
            System.out.println("application.properties file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Failed to load database credentials.");
            e.printStackTrace();
        }
    }

    public Connection connect() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // System.out.println("Connecting to MySQL database...");

            // Create connection
            connection = DriverManager.getConnection(url, user, password);
            // System.out.println("✅ Connected to MySQL database successfully!");

        } catch (ClassNotFoundException e) {
            System.out.println("❌ MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("❌ Failed to connect to MySQL database.");
            e.printStackTrace();
        }

        return connection;
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Disconnected from MySQL database.");
            } catch (SQLException e) {
                System.out.println("❌ Failed to disconnect from MySQL database.");
                e.printStackTrace();
            }
        }
    }
}
