package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.AdemeProperty;
import com.dysconcsa.app.grafico.model.ClasificacionSucsProperty;
import com.dysconcsa.app.grafico.model.HumedadProperty;
import com.dysconcsa.app.grafico.model.SuelosProperty;
import com.dysconcsa.app.grafico.util.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class EstratigrafiaController {

    private ObservableList<SuelosProperty> suelosProperties = FXCollections.observableArrayList();
    private final ObservableList<IndexedColors> itemsColorPoperties = FXCollections.observableArrayList();
    private final ObservableList<FillPatternType> patternTypesProperties = FXCollections.observableArrayList();

    @FXML
    public TableView<ClasificacionSucsProperty> tableClasificacion;
    @FXML
    public TableColumn<ClasificacionSucsProperty, Double> colProfundidadSucs;
    @FXML
    public TableColumn<ClasificacionSucsProperty, Integer> colLimiteLiquido;
    @FXML
    public TableColumn<ClasificacionSucsProperty, Integer> colIndicePlasticidad;
    @FXML
    public TableColumn<ClasificacionSucsProperty, SuelosProperty> colTipoSuelo;
    @FXML
    public TableColumn<ClasificacionSucsProperty, String> colDescipcion;
    @FXML
    public TableColumn<ClasificacionSucsProperty, IndexedColors> colColor;
    @FXML
    public TableColumn<ClasificacionSucsProperty, FillPatternType> colPattern;
    @FXML
    public TableView<HumedadProperty> tableHumedad;
    @FXML
    public TableColumn<HumedadProperty, Double> colProfundidadInicialHumedad;
    @FXML
    public TableColumn<HumedadProperty, Double> colProfundidadFinalHumedad;
    @FXML
    public TableColumn<HumedadProperty, Double> colContenidoHumedad;
    @FXML
    public TableView<AdemeProperty> tableAdeme;
    @FXML
    public TableColumn<AdemeProperty, Double> colProfundidadAdeme;
    @FXML
    public TableColumn<AdemeProperty, String> colAdemeDescripion;

    @FXML
    public void initialize() {
        loadColors();
        loadPattern();
        loadSuelos();
        tableClasificacion.setItems(Variables.getInstance().clasificacionSucsProperties);
        tableHumedad.setItems(Variables.getInstance().humedadProperties);
        tableAdeme.setItems(Variables.getInstance().ademeProperties);
        if (Variables.getInstance().clasificacionSucsProperties.size() <= 0) {
            if (suelosProperties.size() <= 0) {
                Variables.getInstance().clasificacionSucsProperties.add(new ClasificacionSucsProperty(0.0, 0, 0, 0, "", IndexedColors.WHITE, FillPatternType.NO_FILL));
            } else {
                Variables.getInstance().clasificacionSucsProperties.add(new ClasificacionSucsProperty(0.0, 0, 0, suelosProperties.get(0).getID(), suelosProperties.get(0).getNombre(), IndexedColors.WHITE, FillPatternType.NO_FILL));
            }
        }
        if (Variables.getInstance().humedadProperties.size() <= 0) {
            Variables.getInstance().humedadProperties.add(new HumedadProperty(0.0, 1.5, 0.0));
        }
        if (Variables.getInstance().ademeProperties.size() <= 0) {
            Variables.getInstance().ademeProperties.add(new AdemeProperty(0.0, "Se Ademo hasta: 0.0"));
        }
        tableClasificacion.setItems(Variables.getInstance().clasificacionSucsProperties);
        tableHumedad.setItems(Variables.getInstance().humedadProperties);
        tableAdeme.setItems(Variables.getInstance().ademeProperties);
        setupClasificacionColumn();
        setupHumedadColumn();
        setupAdemeColumn();
    }

    private void setupAdemeColumn() {
        setTableEditableAdeme();
        colProfundidadAdeme.setCellValueFactory(value -> value.getValue().profundidadProperty().asObject());
        colProfundidadAdeme.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colProfundidadAdeme.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setProfundidad(value);
            event.getTableView().getItems().get(event.getTablePosition().getRow())
                    .setDescripcion("Se ademo hasta: " + value);
        });
        colAdemeDescripion.setCellValueFactory(value -> value.getValue().descripcionProperty());
    }

    private void setupHumedadColumn() {
        setTableEditableHumedad();
        colProfundidadInicialHumedad.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colProfundidadInicialHumedad
                .setCellValueFactory(value -> value.getValue().profundidadInicialProperty().asObject());
        colProfundidadInicialHumedad.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setProfundidadInicial(value);
            tableHumedad.getSelectionModel().select(event.getTablePosition().getRow(), colProfundidadFinalHumedad);
            event.consume();
        });
        colProfundidadFinalHumedad.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colProfundidadFinalHumedad.setCellValueFactory(value -> value.getValue().profundidadFinalProperty().asObject());
        colProfundidadFinalHumedad.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setProfundidadFinal(value);
            tableHumedad.getSelectionModel().select(event.getTablePosition().getRow(), colContenidoHumedad);
            event.consume();
        });
        colContenidoHumedad.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colContenidoHumedad.setCellValueFactory(value -> value.getValue().humedadProperty().asObject());
        colContenidoHumedad.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setHumedad(value);
            event.consume();
        });
    }

    @SuppressWarnings("rawtypes")
    private void setupClasificacionColumn() {
        DaoSuelos daoSuelos = new DaoSuelos();
        colProfundidadSucs.setCellValueFactory(value -> value.getValue().profundidadProperty().asObject());
        colProfundidadSucs.setCellFactory(EditCell.forTableColumn(new DoubleStringConverter()));
        colProfundidadSucs.setOnEditCommit(event -> {
            final Double value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setProfundidad(value);
            tableClasificacion.getSelectionModel().select(event.getTablePosition().getRow(), colLimiteLiquido);
            event.consume();
            //tableClasificacion.refresh();
        });
        colLimiteLiquido.setCellValueFactory(value -> value.getValue().limiteLiquidoProperty().asObject());
        colLimiteLiquido.setCellFactory(EditCell.forTableColumn(new IntegerStringConverter()));
        colLimiteLiquido.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setLimiteLiquido(value);
            tableClasificacion.getSelectionModel().select(event.getTablePosition().getRow(), colIndicePlasticidad);
            event.consume();
        });
        colIndicePlasticidad.setCellValueFactory(value -> value.getValue().indicePlasticidadProperty().asObject());
        colIndicePlasticidad.setCellFactory(EditCell.forTableColumn(new IntegerStringConverter()));
        colIndicePlasticidad.setOnEditCommit(event -> {
            final Integer value = event.getNewValue() != null ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setIndicePlasticidad(value);
            tableClasificacion.getSelectionModel().select(event.getTablePosition().getRow(), colTipoSuelo);
            event.consume();
        });
        colTipoSuelo.setCellFactory(param -> {
            ComboBoxTableCell<ClasificacionSucsProperty, SuelosProperty> comboBoxTableCell = new ComboBoxTableCell<>();
            comboBoxTableCell.getItems().addAll(suelosProperties);
            comboBoxTableCell.setComboBoxEditable(true);
            AutoCompleteComboBoxListener.autoCompleteComboBoxPlus(comboBoxTableCell.getComboBox(), (typedText, itemToCompare) -> itemToCompare.getNombre().toLowerCase().contains(typedText.toLowerCase()) || itemToCompare.getNombre().equals(typedText));
            comboBoxTableCell.updateSelected(true);
            return comboBoxTableCell;
        });
        colTipoSuelo.setCellValueFactory(value -> {
            SuelosProperty suelosProperty = daoSuelos.findById(value.getValue().getTipoSuelo());
            return new SimpleObjectProperty<>(suelosProperty);
        });
        colTipoSuelo.setOnEditCommit(event -> {
            ClasificacionSucsProperty clasificacionSucsProperty = event.getRowValue();
            SuelosProperty suelos = daoSuelos.findBYSimbolo(String.valueOf(event.getNewValue()));
            TablePosition pos = tableClasificacion.getFocusModel().getFocusedCell();
            //clasificacionSucsProperties.get(pos.getRow()).setDescripcion(suelos.getNombre().toUpperCase());
            clasificacionSucsProperty.setTipoSuelo(suelos.getID());
            Variables.getInstance().clasificacionSucsProperties.get(pos.getRow()).setColor(suelos.getColor());
            Variables.getInstance().clasificacionSucsProperties.get(pos.getRow()).setPattern(suelos.getPattern());
            event.consume();
        });
        colDescipcion.setCellValueFactory(value -> value.getValue().descripcionProperty());
        colDescipcion.setCellFactory(EditCell.forTableColumn(new DefaultStringConverter()));
        colDescipcion.setOnEditCommit(event -> {
            final String value = !event.getNewValue().isEmpty() ? event.getNewValue() : event.getOldValue();
            event.getTableView().getItems().get(event.getTablePosition().getRow()).setDescripcion(value.toUpperCase());
            event.consume();
        });
        colColor.setCellFactory(param -> comboBoxColors());
        colColor.setCellValueFactory(value -> value.getValue().colorProperty());
        colPattern.setCellFactory(param -> comboBoxPattern());
        colPattern.setCellValueFactory(value -> value.getValue().patternProperty());
        setTableEditableClasificacionSucs();
    }

    @SuppressWarnings("unchecked")
    private void setTableEditableClasificacionSucs() {
        tableClasificacion.setEditable(true);
        tableClasificacion.getSelectionModel().cellSelectionEnabledProperty().set(true);
        tableClasificacion.setOnKeyPressed(event -> {
            TablePosition<ClasificacionSucsProperty, ?> pos = tableClasificacion.getFocusModel().getFocusedCell();
            if (event.getCode().isDigitKey()) {
                editFocuedCellClasificacion();
            } else if (event.getCode() == KeyCode.DELETE) {
                if (Variables.getInstance().clasificacionSucsProperties.size() > 1) {
                    Variables.getInstance().clasificacionSucsProperties.remove(tableClasificacion.getSelectionModel().getSelectedItem());
                }
            } else if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (pos.getColumn() == 4) {
                    if (pos.getRow() == (tableClasificacion.getItems().size() - 1)) {
                        Variables.getInstance().clasificacionSucsProperties.add(new ClasificacionSucsProperty(0.0, 0, 0,
                                suelosProperties.get(0).getID(), suelosProperties.get(0).getNombre(), IndexedColors.WHITE, FillPatternType.NO_FILL));
                        tableClasificacion.getSelectionModel().select(pos.getRow() + 1, colProfundidadSucs);
                    } else {
                        tableClasificacion.getSelectionModel().selectNext();
                    }
                } else {
                    tableClasificacion.getSelectionModel().selectNext();
                }
                event.consume();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void setTableEditableAdeme() {
        tableAdeme.setEditable(true);
        tableAdeme.getSelectionModel().cellSelectionEnabledProperty().set(true);
        tableAdeme.setOnKeyPressed(event -> {
            TablePosition<AdemeProperty, ?> pos = tableAdeme.getFocusModel().getFocusedCell();
            if (event.getCode().isDigitKey()) {
                editFocuedCellAdeme();
            } else if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (pos.getColumn() == 1) {
                    if (pos.getRow() == (tableAdeme.getItems().size() - 1)) {
                        Variables.getInstance().ademeProperties.add(new AdemeProperty(0.0, "Se ademo hasta 0.0"));
                        event.consume();
                    }
                }
                tableAdeme.getSelectionModel().selectNext();
            } else if (event.getCode() == KeyCode.DELETE) {
                if (tableAdeme.getItems().size() > 1) {
                    Variables.getInstance().ademeProperties.remove(tableAdeme.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void setTableEditableHumedad() {
        tableHumedad.setEditable(true);
        tableHumedad.getSelectionModel().cellSelectionEnabledProperty().set(true);
        tableHumedad.setOnKeyPressed(event -> {
            TablePosition<HumedadProperty, ?> pos = tableHumedad.getFocusModel().getFocusedCell();
            if (event.getCode().isDigitKey()) {
                editFocuedCellHumedad();
            } else if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.TAB) {
                if (pos.getColumn() == 2) {
                    if (pos.getRow() == (tableHumedad.getItems().size() - 1)) {
                        Variables.getInstance().humedadProperties.add(new HumedadProperty(0.0, 0.0, 0.0));
                        event.consume();
                    }
                }
                tableHumedad.getSelectionModel().selectNext();
            } else if (event.getCode() == KeyCode.DELETE) {
                if (tableHumedad.getItems().size() > 1) {
                    Variables.getInstance().humedadProperties.remove(tableHumedad.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void editFocuedCellAdeme() {
        final TablePosition<AdemeProperty, ?> focusedCell = tableAdeme.focusModelProperty().get().focusedCellProperty()
                .get();
        tableAdeme.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    @SuppressWarnings("unchecked")
    private void editFocuedCellHumedad() {
        final TablePosition<HumedadProperty, ?> focusedCell = tableHumedad.focusModelProperty().get()
                .focusedCellProperty().get();
        tableHumedad.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    @SuppressWarnings("unchecked")
    private void editFocuedCellClasificacion() {
        final TablePosition<ClasificacionSucsProperty, ?> focusedCell = tableClasificacion.focusModelProperty().get()
                .focusedCellProperty().get();
        tableClasificacion.edit(focusedCell.getRow(), focusedCell.getTableColumn());
    }

    private void loadSuelos() {
        DaoSuelos daoSuelos = new DaoSuelos();
        suelosProperties.clear();
        try {
            suelosProperties.addAll(daoSuelos.findAll());
        } catch (SQLException e) {
            AlertError.showAlert(e);
        }
    }

    private void loadColors() {
        Utility utility = new Utility();
        utility.loadColors(itemsColorPoperties);
    }

    private void loadPattern() {
        patternTypesProperties.addAll(FillPatternType.values());
    }

    private ComboBoxTableCell<ClasificacionSucsProperty, IndexedColors> comboBoxColors() {
        Utility utility = new Utility();
        return utility.comboBoxColors(itemsColorPoperties);
    }

    private ComboBoxTableCell<ClasificacionSucsProperty, FillPatternType> comboBoxPattern() {
        Utility utility = new Utility();
        return utility.comboBoxPattern(patternTypesProperties);
    }
}
