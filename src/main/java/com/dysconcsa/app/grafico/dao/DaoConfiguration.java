package com.dysconcsa.app.grafico.dao;


import com.dysconcsa.app.grafico.model.ConfigurationProperty;
import com.dysconcsa.app.grafico.util.AlertError;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DaoConfiguration {

    private Connection connection;
    private Exception _error = null;

    public Exception get_error() {
        return _error;
    }

    public DaoConfiguration() {
        DataConnection dataConnection;
        try {
            String sql = "create table if not exists configuration(id INTEGER not null primary key autoincrement,nombreEmpresa TEXT not null,imagen  TEXT not null,selected bit default 0)";
            dataConnection = new DataConnection();
            connection = dataConnection.getConnection();
            connection.prepareStatement(sql).execute();
        } catch (Exception ex) {
            AlertError.showAlert(ex);
        }
    }

    public ConfigurationProperty findOne() {
        ConfigurationProperty configurationProperty = null;
        try {
            String sql = "select * from configuration where selected = true limit 1";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                configurationProperty = new ConfigurationProperty(rs.getInt("id"),
                        rs.getString("nombreEmpresa").toUpperCase(),
                        rs.getString("imagen"), rs.getBoolean("selected"));
            }
        } catch (Exception ex) {
            _error = ex;
        }
        return configurationProperty;
    }

    public void delete(ConfigurationProperty configurationProperty) {
        try {
            String sql = "delete from configuration where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, configurationProperty.getId());
            preparedStatement.execute();
        } catch (Exception ex) {
            _error = ex;
        }
    }

    public ObservableList<ConfigurationProperty> findAll() {
        ObservableList<ConfigurationProperty> configurationProperties = FXCollections.observableArrayList();
        try {
            String sql = "select * from configuration";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                configurationProperties
                        .add(new ConfigurationProperty(rs.getInt("id"),
                                rs.getString("nombreEmpresa"),
                                rs.getString("imagen"), rs.getBoolean("selected")));
            }
        } catch (Exception ex) {
            _error = ex;
        }
        return configurationProperties;
    }

    public void save(ConfigurationProperty configurationProperty) {
        try {
            String sql = "insert into configuration(nombreEmpresa, imagen,selected) values(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, configurationProperty.getNombreEmpresa().toUpperCase());
            preparedStatement.setString(2, configurationProperty.getImagen());
            preparedStatement.setBoolean(3, configurationProperty.isSelected());
            preparedStatement.execute();
        } catch (Exception ex) {
            _error = ex;
        }
    }

    public void update(ConfigurationProperty configurationProperty) {
        try {
            String sql = "update configuration set nombreEmpresa = ?, imagen = ?, selected= ? where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, configurationProperty.getNombreEmpresa().toUpperCase());
            preparedStatement.setString(2, configurationProperty.getImagen());
            preparedStatement.setBoolean(3, configurationProperty.isSelected());
            preparedStatement.setInt(4, configurationProperty.getId());
            preparedStatement.execute();
        } catch (Exception ex) {
            _error = ex;
        }
    }

    public void updateSelected(ConfigurationProperty configurationProperty) {
        try {
            String sql1 = "update configuration set selected = false";
            String sql2 = "update configuration set selected= true where id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql1);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(sql2);
            preparedStatement.setInt(1, configurationProperty.getId());
            preparedStatement.executeUpdate();
        } catch (Exception ex) {
            _error = ex;
        }
    }
}
