package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.SuelosProperty;
import com.dysconcsa.app.grafico.util.Utility;
import com.dysconcsa.app.grafico.util.Variables;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class SuelosController {

    @FXML
    public AnchorPane centerPane;
    @Autowired
    private MainController mainController;
    private final ObservableList<IndexedColors> itemsColorPoperties = FXCollections.observableArrayList();
    private final ObservableList<FillPatternType> patternTypesProperties = FXCollections.observableArrayList();

    @FXML
    public TableView<SuelosProperty> tableSuelos;
    @FXML
    public TableColumn<SuelosProperty, String> colDescripcion;
    @FXML
    public TableColumn<SuelosProperty, String> colSimbolo;
    @FXML
    public TableColumn<SuelosProperty, IndexedColors> colColor;
    @FXML
    public TableColumn<SuelosProperty, FillPatternType> colPattern;

    private JFXPopup popup;

    @FXML
    public void initialize() {
        Utility utility = new Utility();
        loadColors();
        loadDatas();
        loadPattern();
        colDescripcion.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colSimbolo.setCellValueFactory(cellData -> cellData.getValue().simboloProperty());
        tableSuelos.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> Variables.getInstance().suelosProperty = newValue));
        tableSuelos.setRowFactory(event -> {
            final TableRow<SuelosProperty> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getButton().name().equals("SECONDARY") && (!row.isEmpty())) {
                    initPopup(row, e.getX(), e.getY());
                }
            });
            return row;
        });
        colColor.setCellFactory(e -> new TableCell<SuelosProperty, IndexedColors>() {
            @Override
            public void updateItem(IndexedColors item, boolean empty) {
                // Always invoke super constructor.
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setBackground(Background.EMPTY);
                    setText("");
                } else {
                    XSSFColor color = new XSSFColor(item, null);
                    String hexColor;
                    try {
                        hexColor = color.getARGBHex().substring(2);
                    } catch (Exception ex) {
                        hexColor = "";
                    }
                    this.setStyle(String.format("-fx-background-color: %s;", hexColor));
                    setText(item.name());
                }
            }
        });
        colColor.setCellValueFactory(value -> value.getValue().colorProperty());
        colPattern.setCellFactory(param -> utility.comboBoxPattern(patternTypesProperties));
        colPattern.setCellValueFactory(value -> value.getValue().patternProperty());
    }

    private void loadDatas() {
        DaoSuelos daoSuelos = new DaoSuelos();
        try {
            tableSuelos.getItems().clear();
            Variables.getInstance().suelosProperties.clear();
            Variables.getInstance().suelosProperties.addAll(daoSuelos.findAll());
            tableSuelos.setItems(Variables.getInstance().suelosProperties);
            tableSuelos.refresh();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadColors() {
        Utility utility = new Utility();
        utility.loadColors(itemsColorPoperties);
    }

    private void loadPattern() {
        patternTypesProperties.addAll(FillPatternType.values());
    }

    private void initPopup(TableRow<SuelosProperty> tableRow, double x, double y) {
        JFXListView<String> list = new JFXListView<>();
        list.getItems().addAll("Nuevo", "Editar", "Eliminar", "Actualizar Lista");
        list.setPrefHeight(150);
        list.setPrefWidth(140);
        list.getSelectionModel().selectedIndexProperty().addListener(((observable, oldValue, newValue) -> {
            switch ((int) newValue) {
                case 0:
                    if (mainController.showSuelosEditDialog(null)) {
                        loadDatas();
                    }
                    break;
                case 1:
                    if (mainController.showSuelosEditDialog(Variables.getInstance().suelosProperty)) {
                        loadDatas();
                    }
                    break;
                case 2:
                    if (mainController.deleteSuelos()) {
                        loadDatas();
                    }
                    break;
                case 3:
                    loadDatas();
                    break;
                default:
                    break;
            }
            popup.hide();
        }));
        popup = new JFXPopup(list);
        popup.show(tableRow, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, x, y);
    }
}
