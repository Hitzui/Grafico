package com.dysconcsa.app.grafico.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataConnection {
    Connection connection;

    public Connection getConnection() {
        try {
            this.connection = DriverManager.getConnection("jdbc:sqlite:sondeo.db");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return connection;
    }

    public DataConnection() {

    }
}
