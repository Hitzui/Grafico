package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoConfiguration;
import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.ConfigurationProperty;
import com.dysconcsa.app.grafico.model.SuelosProperty;
import com.dysconcsa.app.grafico.util.Utility;
import com.dysconcsa.app.grafico.util.Variables;
import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class ConfigurationController {

    private String pathImagen = "";
    private final String _title = "Configuracion";
    int aux;
    private ObservableList<SuelosProperty> suelosProperties = FXCollections.observableArrayList();
    private final ObservableList<ConfigurationProperty> configurationProperties = FXCollections.observableArrayList();
    ConfigurationProperty selectedConfiguration;

    @FXML
    public JFXRadioButton radSelected;

    @FXML
    public JFXButton btnEditar;

    @FXML
    private JFXTextField txtNombreEmpresa;

    @FXML
    private JFXButton btnBuscarImagen;

    @FXML
    private ImageView imagen;

    @FXML
    private JFXButton btnNew;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnDelete;

    @FXML
    private JFXButton btnSelection;

    @FXML
    private JFXButton btnCancelar;
    @FXML
    private JFXListView<ConfigurationProperty> listEmpresas;

    @FXML
    private JFXComboBox<SuelosProperty> cmbSuelosBaseSubBase;

    @FXML
    private void initialize() {
        fillSuelosProperties();
        cmbSuelosBaseSubBase.setItems(suelosProperties);
        txtNombreEmpresa.setDisable(true);
        btnBuscarImagen.setDisable(true);
        enableButtons(false, false, true, true, true);
        Utility utility = new Utility();
        loadData();
        DaoConfiguration daoConfiguration = new DaoConfiguration();
        //imageLoader.updateImageView(imagen, String.valueOf(getClass().getResource("/image/logo.jpg").toURI()));
        btnNew.setOnAction(event -> {
            Variables.getInstance().selectedConfiguration = null;
            txtNombreEmpresa.clear();
            enableButtons(true, true, false, true, false);
            txtNombreEmpresa.setDisable(false);
            btnBuscarImagen.setDisable(false);
            pathImagen = "";
            imagen.setImage(null);
            txtNombreEmpresa.requestFocus();
            aux = 1;
        });
        btnEditar.setOnAction(event -> {
            txtNombreEmpresa.setDisable(false);
            btnBuscarImagen.setDisable(false);
            enableButtons(true, true, false, true, false);
            aux = 2;
            txtNombreEmpresa.requestFocus();
        });
        btnDelete.setOnAction(event -> {
            if (selectedConfiguration == null) {
                Utility.dialog(_title, "", "Debe especificar un valor para poder continuar.");
                return;
            }
            txtNombreEmpresa.setDisable(true);
            btnBuscarImagen.setDisable(true);
            aux = 3;
            enableButtons(true, true, false, true, false);

        });
        btnSelection.setOnAction(event -> {
            Variables.getInstance().selectedConfiguration = listEmpresas.getSelectionModel().getSelectedItem();
            selectedConfiguration = Variables.getInstance().selectedConfiguration;
            daoConfiguration.updateSelected(selectedConfiguration);
            radSelected.setSelected(true);
            if (daoConfiguration.get_error() != null) {
                daoConfiguration.get_error().printStackTrace();
            } else {
                Utility.dialog("Selccionar",
                        "Seleccionar configuracion",
                        "Se ha seleccionado la siguiente configuracion: "
                                + selectedConfiguration.getNombreEmpresa() +
                                ", como valor predeterminado para esta sesion.");
                loadData();
            }
        });
        btnCancelar.setOnAction(e -> {
            txtNombreEmpresa.clear();
            txtNombreEmpresa.setDisable(true);
            btnBuscarImagen.setDisable(true);
            aux = 0;
            enableButtons(false, false, true, true, true);
        });
        btnSave.setOnAction(e -> {
            if (txtNombreEmpresa.getText().length() <= 0) {
                Utility.dialog(_title, "", "Debe especificar el nombre de la empresa para aplicar los cambios.");
                txtNombreEmpresa.requestFocus();
                return;
            }
            int existSelectedConfiguration = (int) configurationProperties.stream().filter(ConfigurationProperty::isSelected).count();
            String headerText = "Configuracion: ";
            headerText = (selectedConfiguration != null) ? headerText.concat(selectedConfiguration.getNombreEmpresa()) : headerText.concat(txtNombreEmpresa.getText());
            ButtonType result = Utility.resultDialog("Guardar", headerText,
                    "¿Seguro desea aplicar los cambios seleccionado? Esta acción no se puede revertir.");
            if (result == ButtonType.CANCEL) return;
            switch (aux) {
                case 1:
                    if (pathImagen.length() <= 0) {
                        Utility.dialog(_title, "", "Debe especificar una imagen de la empresa para poder continuar.");
                        return;
                    }
                    ConfigurationProperty newConfig = new ConfigurationProperty(0, txtNombreEmpresa.getText(), pathImagen, false);
                    if (existSelectedConfiguration <= 0) newConfig.setSelected(radSelected.isSelected());
                    else newConfig.setSelected(false);
                    daoConfiguration.save(newConfig);
                    configurationProperties.add(newConfig);
                    break;
                case 2:
                    if (pathImagen.length() <= 0) {
                        pathImagen = selectedConfiguration.getImagen();
                    }
                    if (existSelectedConfiguration <= 0) selectedConfiguration.setSelected(radSelected.isSelected());
                    else selectedConfiguration.setSelected(false);
                    selectedConfiguration.setNombreEmpresa(txtNombreEmpresa.getText());
                    selectedConfiguration.setImagen(pathImagen);
                    daoConfiguration.update(selectedConfiguration);
                    loadData();
                    break;
                case 3:
                    ButtonType response = Utility.resultDialog("Eliminar valor", "",
                            "Seguro desea elminar el valor seleccionado?, esta accion no se puede deshacer.");
                    if (response.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        daoConfiguration.delete(selectedConfiguration);
                        if (daoConfiguration.get_error() != null) {
                            daoConfiguration.get_error().printStackTrace();
                        } else {
                            configurationProperties.remove(Variables.getInstance().selectedConfiguration);
                            Utility.dialog("Eliminar valor", "", "Se ha eliminado el valor seleccionado");
                            Variables.getInstance().selectedConfiguration = null;
                            txtNombreEmpresa.clear();
                        }
                    }
                    break;
            }
            if (daoConfiguration.get_error() != null) {
                daoConfiguration.get_error().printStackTrace();
            } else {
                Utility.dialog("Configuracion general", "", "Se han apalicado los cambios de forma correcta.");
                txtNombreEmpresa.clear();
                txtNombreEmpresa.setDisable(true);
                btnBuscarImagen.setDisable(true);
                enableButtons(false, false, true, false, true);
            }
        });
        btnBuscarImagen.setOnAction(e -> pathImagen = utility.openImage((Stage) btnBuscarImagen.getScene().getWindow(), imagen));
        listEmpresas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                txtNombreEmpresa.setText(newValue.getNombreEmpresa());
                imagen.setImage(new Image("file:" + newValue.getImagen()));
                this.selectedConfiguration = newValue;
                radSelected.setSelected(selectedConfiguration.isSelected());
            }
        });
    }

    void loadData() {
        if (configurationProperties.size() > 0) {
            configurationProperties.clear();
            listEmpresas.getItems().clear();
        }
        DaoConfiguration daoConfiguration = new DaoConfiguration();
        configurationProperties.addAll(daoConfiguration.findAll());
        listEmpresas.getItems().addAll(configurationProperties);
        listEmpresas.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    void enableButtons(boolean nuevo, boolean edit, boolean save, boolean delete, boolean cancel) {
        btnNew.setDisable(nuevo);
        btnEditar.setDisable(edit);
        btnSave.setDisable(save);
        btnDelete.setDisable(delete);
        btnCancelar.setDisable(cancel);
    }
    void fillSuelosProperties(){
        DaoSuelos daoSuelos = new DaoSuelos();
        try {
            suelosProperties.addAll(daoSuelos.findAll());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
