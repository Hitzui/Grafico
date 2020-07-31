package com.dysconcsa.app.grafico.dao;

import com.dysconcsa.app.grafico.model.EmpresaProperty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DaoEmpresa {

    private Exception _error;
    private DataConnection connection;
    private int idAfterSave;

    public int getIdAfterSave() {
        return idAfterSave;
    }

    public DaoEmpresa() {
        connection = new DataConnection();
        try {
            Connection conn = connection.getConnection();
            String sql = "CREATE TABLE IF NOT EXISTS `empresa` ( `ID` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `cliente` TEXT, `proyecto` TEXT, `fecha` TEXT )";
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (SQLException ex) {
            _error = ex;
        }
    }

    public void save(EmpresaProperty empresaProperty) {
        try {
            String sql = "INSERT INTO `empresa`(`cliente`,`proyecto`,`fecha`) VALUES (?,?,?)";
            Connection cnn = connection.getConnection();
            PreparedStatement preparedStatement = cnn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            parameters(preparedStatement, empresaProperty);
            int getId = preparedStatement.executeUpdate();
            if (getId != 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        this.idAfterSave = generatedKeys.getInt(1);
                    } else {
                        this.idAfterSave = 0;
                    }
                }
            }
        } catch (SQLException ex) {
            _error = ex;
        }
    }

    private void parameters(PreparedStatement preparedStatement, EmpresaProperty empresaProperty) throws SQLException {
        preparedStatement.setString(1, empresaProperty.getCliente());
        preparedStatement.setString(2, empresaProperty.getProyecto());
        preparedStatement.setString(3, empresaProperty.getFecha());
    }

    public void update(EmpresaProperty empresaProperty) {
        try {
            String sql = "UPDATE empresa set cliente = ?, proyecto = ?,fecha = ? WHERE id = ?";
            Connection cnn = connection.getConnection();
            PreparedStatement preparedStatement = cnn.prepareStatement(sql);
            parameters(preparedStatement, empresaProperty);
            preparedStatement.setInt(4, empresaProperty.getId());
            this.idAfterSave = preparedStatement.executeUpdate();
        } catch (Exception ex) {
            _error = ex;
        }
    }

    public void delete(Integer id) {
        try {
            String sql = "DELETE FROM empresa WHERE id = ?";
            Connection cnn = connection.getConnection();
            PreparedStatement preparedStatement = cnn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } catch (Exception ex) {
            _error = ex;
        }
    }

    public Exception get_error() {
        return _error;
    }

    public List<EmpresaProperty> findAll() {
        List<EmpresaProperty> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM empresa";
            Connection cnn = connection.getConnection();
            PreparedStatement preparedStatement = cnn.prepareStatement(sql);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                EmpresaProperty empresa = new EmpresaProperty(result.getString("cliente"), result.getString("proyecto"),
                        result.getString("fecha"));
                empresa.setId(result.getInt("id"));
                list.add(empresa);
            }
        } catch (Exception ex) {
            _error = ex;
        }
        return list;
    }

    public EmpresaProperty find(int getIdEmpresa) {
        EmpresaProperty empresa = null;
        try {
            String sql = "select * from empresa where id = ?";
            Connection cnn = connection.getConnection();
            PreparedStatement preparedStatement = cnn.prepareStatement(sql);
            preparedStatement.setInt(1, getIdEmpresa);
            ResultSet result = preparedStatement.executeQuery();
            while (result.next()) {
                empresa = new EmpresaProperty(result.getString("cliente"), result.getString("proyecto"),
                        result.getString("fecha"));
                empresa.setId(result.getInt("id"));
                System.out.println("Id: " + result.getInt("id"));
            }
            return empresa;
        } catch (SQLException e) {
            _error = e;
        }
        return null;
    }
}
