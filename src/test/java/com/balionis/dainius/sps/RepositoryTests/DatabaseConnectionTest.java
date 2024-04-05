package com.balionis.dainius.sps.RepositoryTests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnectionTest {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/dainius_db";
        String username = "root";
        String password = "1234";

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection successful.");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Database connection failed:");
            e.printStackTrace();
        }
    }
}

