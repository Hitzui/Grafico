package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.*;
import com.dysconcsa.app.grafico.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class ValuesController implements Initializable {

    public static File file = null;

    @FXML
    public TableView<DatosCampoProperty> tableDatos;
    @FXML
    public TableColumn<DatosCampoProperty, Double> colProfundidadInicial;
    @FXML
    public TableColumn<DatosCampoProperty, Double> colProfundidadFinal;
    @FXML
    public TableColumn<DatosCampoProperty, Integer> colRecobro;
    @FXML
    public TableColumn<DatosCampoProperty, Integer> colGolpe1;
    @FXML
    public TableColumn<DatosCampoProperty, Integer> colGolpe2;
    @FXML
    public TableColumn<DatosCampoProperty, Integer> colGolpe3;
    @FXML
    public TableView<TrepanoProperty> tableTrepano;
    @FXML
    public TableColumn<TrepanoProperty, Double> colProfundidadTrepano;
    @FXML
    public TableColumn<TrepanoProperty, String> colTrepano;
    private ObservableList<IndexedColors> itemsColorPoperties = FXCollections.observableArrayList();
    private ObservableList<FillPatternType> patternTypesProperties = FXCollections.observableArrayList();
    private ObservableList<SuelosProperty> suelosProperties = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSxml(file);
        loadColors();
        loadPattern();
        listSuelosProperties();
        if (Variables.getInstance().trepanoProperties.size() <= 0) {
            Variables.getInstance().trepanoProperties.add(new TrepanoProperty(0.0, ""));
        }
        if (Variables.getInstance().datosCampoProperties.size() <= 0) {
            Variables.getInstance().datosCampoProperties.add(new DatosCampoProperty(0d, 1.5, 0, 0, 0, 0));
        }
        tableDatos.setItems(Variables.getInstance().datosCampoProperties);
        tableTrepano.setItems(Variables.getInstance().trepanoProperties);
        setupGolpesColumn();
        setupTrepanoColumn();
        setTableEditTrepano();
        setTableEditableDatos();
    }

    private void setupTrepanoColumn() {
        ObservableList<String> trepanoList = FXCollections.observableArrayList();
        trepanoList.addAll("AW - Nw Ex", "AX", "BX", "NX", "T", "D", "Do", "CP", "CN", "PD");
        colProfundidadTrepano.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colProfundidadTrepano.setCellValueFactory(value -> value.getValue().profundidadProperty().asObject());
        colProfundidadTrepano.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setProfundidad(value);
            tableTrepano.getSelectionModel().select(event.getTablePosition().getRow(), colTrepano);
            event.consume();
        });
        colTrepano.setCellFactory(cell -> {
            ComboBoxTableCell<TrepanoProperty, String> comboBoxTableCell = new ComboBoxTableCell<>();
            comboBoxTableCell.getItems().addAll(trepanoList);
            comboBoxTableCell.setComboBoxEditable(true);
            AutoCompleteComboBoxListener.autoCompleteComboBoxPlus(comboBoxTableCell.getComboBox(), (typedText, itemToCompare) -> itemToCompare.toLowerCase().contains(typedText.toLowerCase()) || itemToCompare.equals(typedText));
            return comboBoxTableCell;
        });
        colTrepano.setCellValueFactory(value -> value.getValue().trepanoProperty());
        colTrepano.setOnEditCommit(event -> {
            try {
                final String value = !event.getNewValue().equals(event.getOldValue()) ? event.getNewValue() : event.getOldValue();
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setTrepano(value.toUpperCase());
                event.consume();
            } catch (Exception ex) {
                event.getTableView().getItems().get(event.getTablePosition().getRow()).setTrepano(event.getOldValue().toUpperCase());
            }
        });
    }

    @SuppressWarnings("unchecked")
	private void setTableEditTrepano() {
        tableTrepano.setEditable(true);
        tableTrepano.getSelectionModel().cellSelectionEnabledProperty().set(true);
        tableTrepano.setOnKeyPressed(event -> {
            TablePosition<TrepanoProperty, ?> pos = tableTrepano.getFocusModel().getFocusedCell();
            if (event.getCode().isDigitKey() || event.getCode().isLetterKey()) {
                editFocuedCellTrepano();
            } else if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (pos.getColumn() == 1) {
                    if (pos.getRow() == (tableTrepano.getItems().size() - 1)) {
                        Variables.getInstance().trepanoProperties.add(new TrepanoProperty(0.0, ""));
                        event.consume();
                    }
                    tableTrepano.getSelectionModel().selectNext();
                } else {
                    tableTrepano.getSelectionModel().selectRightCell();
                }
            } else if (event.getCode() == KeyCode.DELETE) {
                if (tableTrepano.getItems().size() > 1) {
                    Variables.getInstance().trepanoProperties.remove(tableTrepano.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    @SuppressWarnings("unused")
	public void action_btnEliminar() {
        if (Variables.getInstance().datosCampoProperties.size() <= 1) {
            return;
        }
        int index = tableDatos.getSelectionModel().getSelectedIndex();
        DatosCampoProperty datosCampoProperty = tableDatos.getSelectionModel().getSelectedItem();
        Variables.getInstance().datosCampoProperties.remove(datosCampoProperty);
    }

    public void action_btnInsertar() {
        DatosCampoProperty datosCampoProperty = tableDatos.getSelectionModel().getSelectedItem();
        int index = tableDatos.getSelectionModel().getSelectedIndex();
        index += 1;
        Double profInicial = datosCampoProperty.getProfundidadInicial() + 1.5;
        Double profFinal = datosCampoProperty.getProfundidadFinal() + 1.5;
        DatosCampoProperty addDatos = new DatosCampoProperty(profInicial, profFinal,
                datosCampoProperty.getRecobro(), datosCampoProperty.getGolpe1(), datosCampoProperty.getGolpe2(),
                datosCampoProperty.getGolpe3());
        for (int i = index; i < Variables.getInstance().datosCampoProperties.size(); i++) {
            DatosCampoProperty datos = Variables.getInstance().datosCampoProperties.get(i);
            datos.setProfundidadInicial(datos.getProfundidadInicial() + 1.5);
            datos.setProfundidadFinal(datos.getProfundidadFinal() + 1.5);
        }
        tableDatos.getItems().add(index, addDatos);
    }

    private void setupGolpesColumn() {
        colProfundidadInicial.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colProfundidadInicial.setCellValueFactory(value -> value.getValue().profundidadInicialProperty().asObject());
        colProfundidadInicial.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setProfundidadInicial(value);
            tableDatos.getSelectionModel().select(event.getTablePosition().getRow(), colProfundidadFinal);
            event.consume();
        });
        colProfundidadFinal.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colProfundidadFinal.setCellValueFactory(value -> value.getValue().profundidadFinalProperty().asObject());
        colProfundidadFinal.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setProfundidadFinal(value);
            event.consume();
        });
        colRecobro.setCellFactory(EditCell.forTableColumn(new IntegerStringConverter()));
        colRecobro.setCellValueFactory(value -> value.getValue().recobroProperty().asObject());
        colRecobro.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setRecobro(value);
            tableDatos.getSelectionModel().select(event.getTablePosition().getRow(), colGolpe1);
            event.consume();
        });
        colGolpe1.setCellFactory(EditCell.forTableColumn(new IntegerStringConverter()));
        colGolpe1.setCellValueFactory(value -> value.getValue().golpe1Property().asObject());
        colGolpe1.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setGolpe1(value);
            tableDatos.getSelectionModel().select(event.getTablePosition().getRow(), colGolpe2);
            event.consume();
        });
        colGolpe2.setCellFactory(EditCell.forTableColumn(new IntegerStringConverter()));
        colGolpe2.setCellValueFactory(value -> value.getValue().golpe2Property().asObject());
        colGolpe2.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setGolpe2(value);
            tableDatos.getSelectionModel().select(event.getTablePosition().getRow(), colGolpe3);
            event.consume();
        });
        colGolpe3.setCellFactory(EditCell.forTableColumn(new IntegerStringConverter()));
        colGolpe3.setCellValueFactory(value -> value.getValue().golpe3Property().asObject());
        colGolpe3.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setGolpe3(value);
            event.consume();
        });
    }

    @SuppressWarnings("unchecked")
    private void setTableEditableDatos() {
        tableDatos.setEditable(true);
        tableDatos.getSelectionModel().cellSelectionEnabledProperty().set(true);
        tableDatos.setOnKeyPressed(event -> {
            TablePosition<DatosCampoProperty, ?> pos = tableDatos.getFocusModel().getFocusedCell();
            if (event.getCode().isDigitKey()) {
                editFocusedCellDatos();
            } else if (event.getCode() == KeyCode.DELETE) {
                action_btnEliminar();
            } else if (event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ENTER) {
                if (pos.getColumn() == 5) {
                    if (pos.getRow() == (tableDatos.getItems().size() - 1)) {
                        DatosCampoProperty datosCampoProperty = tableDatos.getItems().get(pos.getRow());
                        Double profIni = datosCampoProperty.getProfundidadFinal();
                        Double profFinal = datosCampoProperty.getProfundidadFinal() + 1.5;
                        Variables.getInstance().datosCampoProperties.add(new DatosCampoProperty(profIni, profFinal, 0, 0, 0, 0));
                        tableDatos.getSelectionModel().select(pos.getRow() + 1, colRecobro);
                        event.consume();
                    } else {
                        tableDatos.getSelectionModel().selectNext();
                    }
                } else {
                    tableDatos.getSelectionModel().selectNext();
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void editFocuedCellTrepano() {
        final TablePosition<TrepanoProperty, ?> focusedCell = tableTrepano.focusModelProperty().get().focusedCellProperty()
                .get();
        tableTrepano.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    @SuppressWarnings("unchecked")
    private void editFocusedCellDatos() {
        final TablePosition<DatosCampoProperty, ?> focusedCell = tableDatos.focusModelProperty().get()
                .focusedCellProperty().get();
        tableDatos.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    private void loadSxml(File file) {
        if (file != null) {
            ArchivoXml archivoXml = new ArchivoXml();
            Variables.getInstance().datosCampoProperties.clear();
            Variables.getInstance().datosCampoProperties.addAll(archivoXml.cargarDatosCampo(file));
            Variables.getInstance().trepanoProperties.clear();
            ObservableList<TrepanoProperty> datos = archivoXml.cargarDatosTrepano(file);
            if (datos.size() > 0) {
                Variables.getInstance().trepanoProperties.addAll(datos);
            }
            List<DatosSondeo> list = archivoXml.cargarDatosIniciales(file);
            list.get(0);
        }
    }

    private void loadColors() {
        Utility utility = new Utility();
        utility.loadColors(itemsColorPoperties);
    }

    private void loadPattern() {
        patternTypesProperties.addAll(FillPatternType.values());
    }

    private void listSuelosProperties() {
        DaoSuelos daoSuelos = new DaoSuelos();
        suelosProperties.clear();
        try {
            suelosProperties.addAll(daoSuelos.findAll());
        } catch (SQLException e) {
            AlertError.showAlert(e);
        }
    }
}
