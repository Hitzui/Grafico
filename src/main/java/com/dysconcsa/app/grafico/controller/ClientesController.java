package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoEmpresa;
import com.dysconcsa.app.grafico.model.EmpresaProperty;
import com.dysconcsa.app.grafico.util.AlertError;
import com.dysconcsa.app.grafico.util.Variables;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ClientesController implements Initializable {

    @Autowired
    private MainController mainController;

    private void loadData() {
        try {
            DaoEmpresa daoEmpresa = new DaoEmpresa();
            tableClientes.getItems().clear();
            Variables.getInstance().empresaProperties.clear();
            Variables.getInstance().empresaProperties.addAll(daoEmpresa.findAll());
            tableClientes.setItems(Variables.getInstance().empresaProperties);
        } catch (Exception ex) {
            AlertError.showAlert(ex);
        }
    }

    private JFXPopup popup;

    @FXML
    public AnchorPane clientesPane;

    @FXML
    private TableView<EmpresaProperty> tableClientes;

    @FXML
    private TableColumn<EmpresaProperty, Integer> colCodigoCliente;

    @FXML
    private TableColumn<EmpresaProperty, String> colNombreCliente;

    @FXML
    private TableColumn<EmpresaProperty, String> colFecha;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
        colCodigoCliente.setCellValueFactory(param -> param.getValue().idProperty().asObject());
        colNombreCliente.setCellValueFactory(param -> param.getValue().clienteProperty());
        colFecha.setCellValueFactory(param -> param.getValue().fechaProperty());
        tableClientes.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> Variables.getInstance().empresaProperty = newValue);
        tableClientes.setRowFactory(event -> {
            final TableRow<EmpresaProperty> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getButton().name().equals("SECONDARY") && (!row.isEmpty())) {
                    initPopup(row, e.getX(), e.getY());
                }
            });
            return row;
        });
    }

    private void initPopup(TableRow<EmpresaProperty> row, double x, double y) {
        JFXListView<String> list = new JFXListView<>();
        list.getItems().addAll("Nuevo", "Editar", "Eliminar", "Seleccionar", "Actualizar lista");
        list.setPrefHeight(170);
        list.setPrefWidth(140);
        list.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            switch ((int) newValue) {
                case 0:
                    mainController.showEmpresaEditDialog(null);
                    break;
                case 1:
                    if (mainController.showEmpresaEditDialog(Variables.getInstance().empresaProperty)) {
                        loadData();
                    }
                    break;
                case 2:
                    if (mainController.deleteCliente()) loadData();
                    break;
                case 3:
                    tableClientes.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValueCliente, newValueCliente) -> Variables.getInstance().empresaProperty = newValueCliente);
                    break;
                case 4:
                    loadData();
                    break;
                default:
                    break;
            }
            popup.hide();
        }));
        popup = new JFXPopup(list);
        popup.show(row, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, x, y);
    }
}
