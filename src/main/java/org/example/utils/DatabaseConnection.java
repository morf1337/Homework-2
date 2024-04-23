package org.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties properties = new Properties();
            try {
                InputStream fis = DatabaseConnection.class.getClassLoader().getResourceAsStream("config.properties");
                properties.load(fis);
                String url = properties.getProperty("url");
                String user = properties.getProperty("user");
                String password = properties.getProperty("password");
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, password);

            } catch (IOException e) {
                throw new SQLException("Failed to read application properties file");
            } catch (SQLException e) {
                throw new SQLException("Failed to establish a database connection");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
        return connection;
    }
}
