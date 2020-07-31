package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoEmpresa;
import com.dysconcsa.app.grafico.model.EmpresaProperty;
import com.dysconcsa.app.grafico.util.DateUtil;
import com.dysconcsa.app.grafico.util.Utility;
import com.dysconcsa.app.grafico.util.Variables;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static com.dysconcsa.app.grafico.util.Utility.mouseDragged;
import static com.dysconcsa.app.grafico.util.Utility.resultDialog;

@Component
public class FormClienteController extends HeaderPaneController {

    @FXML
    public Label lblTitulo;
    @FXML
    public Button btnCancelar;
    @FXML
    public Button btnAceptar;
    @FXML
    public JFXTextField txtCliente;
    @FXML
    public JFXTextArea txtProyecto;
    @FXML
    public JFXDatePicker txtFecha;

    @Value("${cliente.edit.dialog}")
    private String titulo;

    private boolean isOk;
    private Stage dialogStage;
    private EmpresaProperty empresaProperty;

    boolean isOk() {
        return isOk;
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialogStage Stage of dialog
     */
    void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    void setEmpresaProperty(EmpresaProperty empresaProperty) {
        if (empresaProperty != null) {
            lblTitulo.setText("Editar Cliente " + empresaProperty.getId());
            this.empresaProperty = empresaProperty;
            txtCliente.setText(empresaProperty.getCliente());
            txtProyecto.setText(empresaProperty.getProyecto());
            txtFecha.setValue(DateUtil.parse(empresaProperty.getFecha()));
        } else {
            this.empresaProperty = null;
        }
    }

    @FXML
    public void initialize() {
        header.setOnMousePressed(Utility::mousePressed);
        header.setOnMouseDragged(event -> mouseDragged(event, dialogStage));
        btnCancelar.setOnAction(event -> dialogStage.close());
        btnAceptar.setOnAction(e -> {
            ButtonType choice = resultDialog("Editar cliente", "Guardar", "Seguro desea guardar los datos del cliente?");
            if (choice == ButtonType.CANCEL) {
                return;
            }
            DaoEmpresa daoEmpresa = new DaoEmpresa();
            String fecha;
            if (txtCliente.getText().length() <= 0) return;
            if (txtFecha.getValue() != null) {
                fecha = DateUtil.format(txtFecha.getValue());
            } else fecha = DateUtil.format(LocalDate.now());
            if (empresaProperty != null) {
                empresaProperty.setCliente(txtCliente.getText());
                empresaProperty.setProyecto(txtProyecto.getText());
                empresaProperty.setFecha(fecha);
                daoEmpresa.update(empresaProperty);
            } else {
                empresaProperty = new EmpresaProperty(txtCliente.getText(), txtProyecto.getText(), fecha);
                daoEmpresa.save(empresaProperty);
                Variables.getInstance().empresaProperties.add(empresaProperty);
                empresaProperty.setId(daoEmpresa.getIdAfterSave());
                System.out.println(daoEmpresa.getIdAfterSave());
            }
            if (daoEmpresa.get_error() != null) {
                daoEmpresa.get_error().printStackTrace();
            } else {
                isOk = true;
                dialogStage.close();
            }
        });
    }
}
