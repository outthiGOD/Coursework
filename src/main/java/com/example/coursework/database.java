package com.example.coursework;

import java.sql.*;

public class database {
    public static Connection connectDb() {
        Connection connect = null;
        try {
            Class.forName("org.postgresql.Driver");
            connect = DriverManager.getConnection("jdbc:postgresql://localhost:15432/postgres", "postgres", "123321");
            connect.setSchema("public");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connect;
    }
}