package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.model.DatosSondeo;
import com.dysconcsa.app.grafico.util.DateUtil;
import com.dysconcsa.app.grafico.util.Utility;
import com.dysconcsa.app.grafico.util.Variables;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class DatosSondeoController extends HeaderPaneController {

    DatosSondeo loadDatosSondeo;

    void setLoadDatosSondeo(DatosSondeo loadDatosSondeo) {
        this.loadDatosSondeo = loadDatosSondeo;
        if (loadDatosSondeo != null) {
            txtSondeoNumero.setText(loadDatosSondeo.getSondeoNumero());
            txtProfundidadMinima.setText(String.valueOf(loadDatosSondeo.getProfundidadMinima()));
            txtProfundidadMaxima.setText(String.valueOf(loadDatosSondeo.getProfundidadMaxima()));
            txtElevacion.setText(String.valueOf(loadDatosSondeo.getElevacion()));
            txtNivelFreatico.setText(loadDatosSondeo.getNivelFreatico());
            txtLugar.setText(loadDatosSondeo.getLugar());
            txtOperador.setText(loadDatosSondeo.getOperador());
            txtObservaciones.setText(loadDatosSondeo.getObservaciones());
            txtArchivo.setText(loadDatosSondeo.getArchivo());
            txtFecha.setValue(DateUtil.parse(loadDatosSondeo.getFecha()));
        } else {
            this.loadDatosSondeo = new DatosSondeo();
        }
    }

    @FXML
    public JFXTextField txtSondeoNumero;
    @FXML
    public JFXTextField txtProfundidadMinima;
    @FXML
    public JFXTextField txtProfundidadMaxima;
    @FXML
    public JFXTextField txtElevacion;
    @FXML
    public JFXTextField txtNivelFreatico;
    @FXML
    public JFXTextField txtLugar;
    @FXML
    public JFXTextField txtObservaciones;
    @FXML
    public JFXTextField txtOperador;
    @FXML
    public JFXTextField txtArchivo;
    @FXML
    public JFXDatePicker txtFecha;
    @FXML
    public JFXButton btnGuardar;
    @FXML
    public JFXButton btnCancelar;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        header.setOnMousePressed(Utility::mousePressed);
        header.setOnMouseDragged(event -> Utility.mouseDragged(event, dialogStage));
        btnCancelar.setOnAction(action -> dialogStage.close());
        btnGuardar.setOnAction(event -> {
            loadDatosSondeo.setSondeoNumero(txtSondeoNumero.getText());
            loadDatosSondeo.setArchivo(txtArchivo.getText());
            loadDatosSondeo.setElevacion(Double.valueOf(txtElevacion.getText()));
            loadDatosSondeo.setLugar(txtLugar.getText());
            loadDatosSondeo.setNivelFreatico(txtNivelFreatico.getText());
            loadDatosSondeo.setObservaciones(txtObservaciones.getText());
            loadDatosSondeo.setFecha(DateUtil.format(txtFecha.getValue()));
            loadDatosSondeo.setOperador(txtOperador.getText());
            loadDatosSondeo.setProfundidadMaxima(Double.valueOf(txtProfundidadMaxima.getText()));
            loadDatosSondeo.setProfundidadMinima(Double.valueOf(txtProfundidadMinima.getText()));
            Variables.getInstance().datosSondeo = loadDatosSondeo;
            dialogStage.close();
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
