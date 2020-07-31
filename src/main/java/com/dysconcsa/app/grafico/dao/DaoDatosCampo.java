package com.dysconcsa.app.grafico.dao;

import com.dysconcsa.app.grafico.model.DatosCampoProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class DaoDatosCampo {

    Exception _error;

    public Exception get_error() {
        return _error;
    }

    private DataConnection connection;

    public DaoDatosCampo() {
        connection = new DataConnection();
        try {
            Connection conn = connection.getConnection();
            String sql = "CREATE TABLE IF NOT EXISTS datos_campo" +
                    "(" +
                    "    profundidad_inicial number " +
                    "        constraint datos_campo_pk " +
                    "            primary key, " +
                    "    profundidad_final   number, " +
                    "    recobro             int, " +
                    "    golpe1              int, " +
                    "    golpe2              int, " +
                    "    golpe3              int " +
                    ")";
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (Exception ex) {
            _error = ex;
        }
    }

    public ObservableList<DatosCampoProperty> findAll() throws SQLException {
        ObservableList<DatosCampoProperty> datosCampoProperties = FXCollections.observableArrayList();
        try (Connection cnn = connection.getConnection()) {
            String sql = "select * from datos_campo";
            PreparedStatement preparedStatement = cnn.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                DatosCampoProperty datosCampoProperty = new DatosCampoProperty(rs.getDouble("profundidad_inicial"),
                        rs.getDouble("profundidad_fianl"), rs.getInt("recobro"),
                        rs.getInt("golpe1"), rs.getInt("golpe2"), rs.getInt("golpe3"));
                datosCampoProperties.add(datosCampoProperty);
            }
        } catch (Exception ex) {
            _error = ex;
        }
        return datosCampoProperties;
    }
}
