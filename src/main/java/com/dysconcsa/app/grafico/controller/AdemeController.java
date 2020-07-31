package com.dysconcsa.app.grafico.controller;


import com.dysconcsa.app.grafico.model.AdemeProperty;
import com.dysconcsa.app.grafico.util.AdemeConverter;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class AdemeController {

    private Stage dialogStage;
    public static ObservableList<AdemeProperty> ademeProperties;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private JFXButton btnSalir;

    @FXML
    private JFXButton btnIngresar;

    @FXML
    private JFXButton btnEliminar;

    @FXML
    private JFXButton btnLimpiarLista;

    @FXML
    private JFXTextField txtAdeme;

    @FXML
    private ListView<AdemeProperty> listView;

    @FXML
    public void initialize() {
        if (ademeProperties == null) {
            ademeProperties = FXCollections.observableArrayList();
        }
        listView.setEditable(true);
        txtAdeme.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([\\.]\\d{0,4})?")) {
                txtAdeme.setText(oldValue);
            }
        });
        txtAdeme.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                action_btnIngresar();
            }
        });
        listView.setItems(ademeProperties);
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                txtAdeme.setText("0");
            } else {
                txtAdeme.setText(String.valueOf(newValue.getProfundidad()));
            }
        });
        listView.setCellFactory(converter -> {
            TextFieldListCell<AdemeProperty> cell = new TextFieldListCell<>();
            cell.setConverter(new AdemeConverter(cell));
            return cell;
        });
        listView.setOnEditCommit(event -> {
            System.out.println(event.getNewValue());
        });
    }

    public void action_btnIngresar() {
        Double valor;
        if (txtAdeme.getText().trim().length() <= 0) {
            return;
        }
        valor = Double.parseDouble(txtAdeme.getText());
        AdemeProperty ademeProperty = new AdemeProperty(valor, "Se ademo hasta " + valor);
        listView.getItems().add(ademeProperty);
        txtAdeme.setText("");
        txtAdeme.requestFocus();
    }

    public void action_btnEliminar() {
        AdemeProperty selectedItem = listView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            listView.getItems().remove(selectedItem);
        }
    }

    public void action_btnSalir() {
        dialogStage.close();
    }

    public void action_btnLimpiarDatos() {
        ademeProperties.clear();
        listView.getItems().clear();
    }

    void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    ObservableList<AdemeProperty> getAdemeProperties() {
        return ademeProperties;
    }

    void setAdemeProperties(ObservableList<AdemeProperty> ademeProperties) {
        AdemeController.ademeProperties = ademeProperties;
    }
}
