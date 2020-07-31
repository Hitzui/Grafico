package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.SuelosProperty;
import com.dysconcsa.app.grafico.util.AlertError;
import com.dysconcsa.app.grafico.util.Utility;
import com.dysconcsa.app.grafico.util.Variables;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.stereotype.Component;

@Component
public class FormSuelosController extends HeaderPaneController {

    private boolean isOK;
    private Stage dialogStage;
    private SuelosProperty suelosProperty;
    private final ObservableList<IndexedColors> itemsColorPoperties = FXCollections.observableArrayList();
    private final ObservableList<FillPatternType> patternTypesProperties = FXCollections.observableArrayList();
    private int aux;

    @FXML
    public JFXComboBox<IndexedColors> cmbColor;
    @FXML
    public JFXComboBox<FillPatternType> cmbPattern;
    @FXML
    public JFXTextField txtNombre;
    @FXML
    public JFXTextField txtSimbologia;
    @FXML
    public JFXButton btnSave;
    @FXML
    public JFXButton btnCancel;

    @FXML
    public void initialize() {
        header.setOnMousePressed(Utility::mousePressed);
        header.setOnMouseDragged(event -> Utility.mouseDragged(event, dialogStage));
        btnSave.setOnAction(actionEvent -> action_btnGuardarSuelo());
        btnCancel.setOnAction(event -> {
            dialogStage.close();
            isOK = false;
        });
        loadColors();
        loadPattern();
        cmbPattern.getItems().addAll(patternTypesProperties);
        cmbColor.getItems().addAll(itemsColorPoperties);
        cmbColor.valueProperty().addListener((observable, oldValue, newValue) -> {
            XSSFColor color = new XSSFColor(newValue, null);
            String hexColor;
            try {
                hexColor = color.getARGBHex().substring(2);
            } catch (Exception ex) {
                hexColor = "";
            }
            cmbColor.setStyle(String.format("-fx-background-color: #%s", hexColor));
        });
        cmbColor.setCellFactory(lv -> new ListCell<IndexedColors>() {
                    @Override
                    protected void updateItem(IndexedColors item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setBackground(Background.EMPTY);
                            setText("");
                        } else {
                            XSSFColor color = new XSSFColor(item, null);
                            String textColor = color.getARGBHex().substring(2);
                            setBackground(new Background(new BackgroundFill(Color.valueOf(textColor),
                                    CornerRadii.EMPTY,
                                    Insets.EMPTY)));
                            setText(item.name());
                        }
                    }
                }
        );
    }

    void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    void setSuelosProperty(SuelosProperty suelosProperty) {
        this.suelosProperty = suelosProperty;
        if (suelosProperty != null) {
            txtNombre.setText(suelosProperty.getNombre());
            txtSimbologia.setText(suelosProperty.getSimbolo());
            cmbColor.setValue(suelosProperty.getColor());
            cmbPattern.setValue(suelosProperty.getPattern());
            aux = 1;
        } else {
            aux = 0;
        }
    }

    private void action_btnGuardarSuelo() {
        try {
            if (txtNombre.getText().length() <= 0) {
                txtNombre.requestFocus();
                return;
            }
            if (txtSimbologia.getText().length() <= 0) {
                txtSimbologia.requestFocus();
                return;
            }
            DaoSuelos daoSuelos = new DaoSuelos();
            if (Utility.resultDialog("Guardar", "", "¿Seguro desea guardar los datos del tipo de suelo?") == ButtonType.CANCEL) {
                return;
            }
            IndexedColors colors = cmbColor.getValue();
            FillPatternType patternType = cmbPattern.getValue();
            switch (aux) {
                //nuevo registro
                case 0:
                    SuelosProperty suelo = new SuelosProperty(0, txtNombre.getText().toUpperCase(), txtSimbologia.getText().toUpperCase(), colors, patternType);
                    daoSuelos.save(suelo);
                    Variables.getInstance().suelosProperties.add(suelo);
                    break;
                //editar datos
                case 1:
                    suelosProperty = new SuelosProperty(suelosProperty.getID(), txtNombre.getText(), txtSimbologia.getText(), colors, patternType);
                    daoSuelos.update(suelosProperty);
                    break;
            }
            if (daoSuelos.get_error() != null) {
                daoSuelos.get_error().printStackTrace();
                isOK = false;
            } else {
                isOK = true;
                dialog();
                clear();
                dialogStage.close();
            }
        } catch (Exception ex) {
            AlertError.showAlert(ex);
        }
    }

    private void dialog() {
        Utility.dialog("Guardar", "Tipo de suelo", "Se han ingresado los datos de forma correcta.");
    }

    private void clear() {
        txtNombre.clear();
        txtSimbologia.clear();
        cmbColor.setValue(IndexedColors.WHITE);
        cmbPattern.setValue(FillPatternType.NO_FILL);
    }

    private void loadColors() {
        Utility utility = new Utility();
        utility.loadColors(itemsColorPoperties);
    }

    private void loadPattern() {
        patternTypesProperties.addAll(FillPatternType.values());
    }

    public boolean isOK() {
        return isOK;
    }
}
