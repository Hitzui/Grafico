package com.dysconcsa.app.grafico.controller;

import com.dysconcsa.app.grafico.dao.DaoEmpresa;
import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.*;
import com.dysconcsa.app.grafico.util.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MainController {

    final Utility utility;

    private String lastVisitedDirectory = System.getProperty("user.dir");
    private final ApplicationContext applicationContext;
    private EmpresaProperty empresaProperty;
    private SuelosProperty suelosProperty;

    @FXML
    public Button btnConfigurar;

    @FXML
    public AnchorPane mainPane;

    @FXML
    public Button btnClientes;

    @FXML
    public Button btnOpen;

    @FXML
    public Button btnEdit;

    @FXML
    public AnchorPane centerPane;

    @FXML
    public Button btnValores;

    @FXML
    public Button btnEstratigrafia;

    @FXML
    public Button btnSave;

    /*@FXML
    public Button btnSaveAs;*/

    @FXML
    public Button btnDelete;

    @FXML
    public Button btnNew;

    @FXML
    public Button btnClose;

    @FXML
    public Button btnSuelos;

    @FXML
    public Button btnDatosSondeos;

    @FXML
    public Button btnGenerar;

    @Value("classpath:/values.fxml")
    private Resource valuesResource;

    @Value("classpath:/clientes.fxml")
    private Resource clientesResource;

    @Value("classpath:/formCliente.fxml")
    private Resource formCliente;

    @Value("classpath:/datosSondeo.fxml")
    private Resource formDatosSondeos;

    @Value("classpath:/suelos.fxml")
    private Resource formSuelos;

    @Value("classpath:/formSuelos.fxml")
    private Resource formEditSuelos;

    @Value("classpath:/estratigrafia.fxml")
    private Resource formEstratigrafia;

    @Value("classpath:/configuration.fxml")
    private Resource formConfiguration;

    @Value("classpath:css/table.css")
    private Resource cssTable;

    @Value("classpath:css/base.css")
    private Resource cssBase;

    private File fileExcel;
    HostServices hostServices;
    String applicationTitle;

    public void setGetHostController(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public MainController(ApplicationContext applicationContext,
                          Utility utility, @Value("${application.title}") String applicationTitle) {
        super();
        this.applicationContext = applicationContext;
        this.utility = utility;
        this.applicationTitle = applicationTitle;
    }

    @FXML
    public void initialize() {
        loadSxml(Variables.getInstance().file);
        btnValores.setOnAction(action -> load(valuesResource, "values"));
        btnClientes.setOnAction(action -> load(clientesResource, "clientes"));
        btnDatosSondeos.setOnAction(action -> showDatosSondeoDialog());
        btnSuelos.setOnAction(action -> load(formSuelos, "suelos"));
        btnEstratigrafia.setOnAction(action -> load(formEstratigrafia, "estratigrafia"));
        btnConfigurar.setOnAction(action -> load(formConfiguration, "configuration"));
        btnOpen.setOnAction(action -> {
            try {
                //ArchivoXml archivoXml = new ArchivoXml();
                String readDirectory = utility.readLastDirectory();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Cargar daatos");
                System.out.println(readDirectory);
                if (readDirectory == null) readDirectory = "c:\\";
                File openDirectory = new File(readDirectory);
                if (!openDirectory.isDirectory() || !openDirectory.exists()) {
                    openDirectory = new File("c:\\");
                }
                fileChooser.setInitialDirectory(openDirectory);
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Sondeos files (*.sxml)", "*.sxml");
                fileChooser.getExtensionFilters().add(extFilter);
                Stage stage = (Stage) mainPane.getScene().getWindow();
                Variables.getInstance().file = fileChooser.showOpenDialog(stage);
                if (Variables.getInstance().file != null) {
                    loadSxml(Variables.getInstance().file);
                    ((Stage) centerPane.getScene().getWindow()).setTitle("Aplicacion de Sondeos - " + Variables.getInstance().file.getName());
                    //lastVisitedDirectory = (Variables.getInstance().file != null) ? Variables.getInstance().file.getParent() : System.getProperty("user.home");
                    utility.writeLastDirectory(Variables.getInstance().file.getParent());
                    load(valuesResource, "values");
                }
            } catch (Exception ex) {
                AlertError.showAlert(ex);
                ex.printStackTrace();
            }
        });
        btnSave.setOnAction(action -> centerPane.getChildren().forEach(node -> {
            if (node.isVisible()) {
                switch (node.getId()) {
                    case "values":
                    case "estratigrafia":
                        saveFile();
                        break;
                    case "clientes":
                    case "suelos":
                        break;
                }
            }
        }));
        btnNew.setOnAction(action -> centerPane.getChildren().forEach(node -> {
            if (node.isVisible()) {
                switch (node.getId()) {
                    case "clientes":
                        showEmpresaEditDialog(null);
                        break;
                    case "suelos":
                        showSuelosEditDialog(null);
                        break;
                }
            }
        }));
        btnEdit.setOnAction(action -> centerPane.getChildren().forEach(node -> {
            if (node.isVisible()) {
                switch (node.getId()) {
                    case "clientes":
                        empresaProperty = Variables.getInstance().empresaProperty;
                        showEmpresaEditDialog(empresaProperty);
                        break;
                    case "suelos":
                        suelosProperty = Variables.getInstance().suelosProperty;
                        showSuelosEditDialog(suelosProperty);
                        break;
                }
            }
        }));
        btnDelete.setOnAction(action -> centerPane.getChildren().forEach(node -> {
            if (node.isVisible()) {
                switch (node.getId()) {
                    case "clientes":
                        deleteCliente();
                        break;
                    case "suelos":
                        deleteSuelos();
                        break;
                }
            }
        }));
        btnGenerar.setOnAction(action -> {
            try {
                if (Variables.getInstance().empresaProperty == null) {
                    Utility.dialog("Error", "", "No se ha especificado un cliente, por favor especifique un cliente para continuar.");
                    return;
                }
                ArchivoExcel archivoExcel = new ArchivoExcel();
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Generar Gráfico");
                fileChooser.setInitialDirectory(new File(lastVisitedDirectory));
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Excel files (*.xlsx)", "*.xlsx");
                fileChooser.getExtensionFilters().addAll(extFilter);
                Stage stage = (Stage) centerPane.getScene().getWindow();
                // Show save file dialog
                fileExcel = fileChooser.showSaveDialog(stage);
                if (fileExcel != null) {
                    prepararDatos();
                    archivoExcel.setClasificacionSucsProperties(Variables.getInstance().clasificacionSucsProperties);
                    archivoExcel.setDatosCampoProperties(Variables.getInstance().datosCampoProperties);
                    archivoExcel.setHumedadProperties(Variables.getInstance().humedadProperties);
                    List<DatosSondeo> profundidadSondeos = new ArrayList<>();
                    profundidadSondeos.add(Variables.getInstance().datosSondeo);
                    archivoExcel.setDatosSondeos(profundidadSondeos);
                    archivoExcel.setTrepanoProperties(Variables.getInstance().trepanoProperties);
                    archivoExcel.crearArchivo(mainPane, fileExcel, Variables.getInstance().empresaProperty, Variables.getInstance().datosSondeo, Variables.getInstance().ademeProperties, hostServices);
                }
                lastVisitedDirectory = (Variables.getInstance().file != null) ? Variables.getInstance().file.getParent() : System.getProperty("user.home");
                utility.writeLastDirectory(lastVisitedDirectory);
            } catch (Exception ex) {
                ex.printStackTrace();
                //Utility.dialog("Error", "Error al generar", "No se pudo generar el archivo de sondeo, intente nuevamente." + ex.getMessage());
            }
        });
    }

    private void prepararDatos() {
        double prof = 0.0;
        ObservableList<DatosCampoProperty> agregar = FXCollections.observableArrayList();
        ObservableList<DatosCampoProperty> remover = FXCollections.observableArrayList();
        for (DatosCampoProperty datos : Variables.getInstance().datosCampoProperties) {
            if (prof != 0) {
                if (prof != datos.getProfundidadFinal()) {
                    double aux = datos.getProfundidadInicial();
                    while (aux < datos.getProfundidadFinal()) {
                        agregar.add(new DatosCampoProperty(aux, aux + 1.5, datos.getRecobro(), datos.getGolpe1(), datos.getGolpe2(), datos.getGolpe3()));
                        aux += 1.5;
                    }
                    remover.add(datos);
                }
            }
            prof = datos.getProfundidadFinal() + 1.5;
        }
        Variables.getInstance().datosCampoProperties.removeAll(remover);
        Variables.getInstance().datosCampoProperties.addAll(agregar);
        Variables.getInstance().datosCampoProperties.sort((x, y) -> {
            final Integer value = (int) (x.getProfundidadInicial() + 1.5) * 2;
            final Integer value2 = (int) (y.getProfundidadFinal()) * 2;
            return value.compareTo(value2);
        });
    }

    private void loadSxml(File file) {
        if (file != null) {
            ArchivoXml archivoXml = new ArchivoXml();
            Variables.getInstance().datosCampoProperties.clear();
            Variables.getInstance().datosCampoProperties.addAll(archivoXml.cargarDatosCampo(file));
            Variables.getInstance().clasificacionSucsProperties.clear();
            Variables.getInstance().clasificacionSucsProperties.addAll(archivoXml.cargarDatosClasificacion(file));
            Variables.getInstance().humedadProperties.clear();
            Variables.getInstance().humedadProperties.addAll(archivoXml.cargarDatosHumedad(file));
            Variables.getInstance().ademeProperties.clear();
            Variables.getInstance().ademeProperties.addAll(archivoXml.cargarDatosAdeme(file));
            Variables.getInstance().trepanoProperties.clear();
            ObservableList<TrepanoProperty> datos = archivoXml.cargarDatosTrepano(file);
            if (datos.size() > 0) {
                Variables.getInstance().trepanoProperties.addAll(datos);
            }
            List<DatosSondeo> list = archivoXml.cargarDatosIniciales(file);
            Variables.getInstance().datosSondeo = list.get(0);
        }
    }

    private void load(Resource resource, String accessibleText) {
        try {
            Node find = centerPane.lookup("#" + accessibleText);
            centerPane.getChildren().forEach(node -> node.setVisible(false));
            if (find != null) {
                find.setVisible(true);
            } else {
                FXMLLoader fxmlLoader = new FXMLLoader(resource.getURL());
                fxmlLoader.setControllerFactory(applicationContext::getBean);
                AnchorPane pane = fxmlLoader.load();
                pane.setId(accessibleText);
                AnchorPane.setBottomAnchor(pane, 0d);
                AnchorPane.setLeftAnchor(pane, 0d);
                AnchorPane.setTopAnchor(pane, 0d);
                AnchorPane.setRightAnchor(pane, 0d);
                pane.setVisible(true);
                pane.getStylesheets().add(String.valueOf(cssTable.getURL()));
                pane.getStylesheets().add(String.valueOf(cssBase.getURL()));
                centerPane.getChildren().add(pane);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param empresaProperty the person object to be edited
     */
    boolean showEmpresaEditDialog(EmpresaProperty empresaProperty) {
        FXMLLoader loader;
        try {
            Stage dialogStage = new Stage();
            loader = loadDialog(dialogStage, formCliente, "Editar cliente");
            FormClienteController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEmpresaProperty(empresaProperty);
            dialogStage.showAndWait();
            return controller.isOk();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showDatosSondeoDialog() {
        FXMLLoader loader;
        try {
            Stage dialogStage = new Stage();
            loader = loadDialog(dialogStage, formDatosSondeos, "Datos del Sondeo");
            DatosSondeoController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setLoadDatosSondeo(Variables.getInstance().datosSondeo);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    boolean showSuelosEditDialog(SuelosProperty suelosProperty) {
        Stage dialogStage = new Stage();
        try {
            FXMLLoader loader = loadDialog(dialogStage, formEditSuelos, "Editar Suelos");
            FormSuelosController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setSuelosProperty(suelosProperty);
            dialogStage.showAndWait();
            return controller.isOK();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private FXMLLoader loadDialog(Stage dialogStage, Resource resource, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(resource.getURL());
        AnchorPane anchorPane = loader.load();
        anchorPane.getStylesheets().add(String.valueOf(cssBase.getURL()));
        // Create the dialog Stage.
        utility.showDialogStage(dialogStage, anchorPane, title);
        return loader;
    }


    public void handle_btnClose() {
        centerPane.getChildren().clear();
        ((Stage) mainPane.getScene().getWindow()).setTitle(applicationTitle);
        clearVariablesAndFile();
    }

    private void clearVariablesAndFile() {
        Variables.getInstance().destroy();
    }

    boolean deleteCliente() {
        DaoEmpresa daoEmpresa = new DaoEmpresa();
        ButtonType result = Utility.resultDialog("Eliminar",
                "Cliente: " + Variables.getInstance().empresaProperty.getCliente(),
                "¿Eliminar el cliente selecionado? Esta accion no se puede revertir.");
        if (result == ButtonType.CANCEL) return false;
        daoEmpresa.delete(Variables.getInstance().empresaProperty.getId());
        if (daoEmpresa.get_error() != null) {
            AlertError.showAlert(daoEmpresa.get_error());
            return false;
        } else {
            Utility.dialog("Eliminar", "Eliminar cliente", "Se ha eliminado el cliente de forma correcta.");
            Variables.getInstance().empresaProperties.remove(Variables.getInstance().empresaProperty);
            return true;
        }
    }

    boolean deleteSuelos() {
        DaoSuelos daoSuelos = new DaoSuelos();
        JFXButton buttonAccept = new JFXButton("Aceptar");
        JFXButton buttonCancel = new JFXButton("Cancelar");
        JFXDialog dialog = Utility.showDialog(centerPane, "Eliminar",
                "Seguro desea eliminar el tipo de suelo seleccionado? Esta accion no se puede deshacer.",
                buttonAccept, buttonCancel);
        buttonAccept.setOnAction(event -> {
            daoSuelos.delete(Variables.getInstance().suelosProperty.getID());
            if (daoSuelos.get_error() != null) {
                dialog.close();
                Utility.dialog("Eliminar", "Eliminar suelo",
                        "No se pudo eliminar el tipo de suelo seleccionado, intente nuevamente o " +
                                "pongase en contacto con soporte tecnico. \nError: " + daoSuelos.get_error().getMessage());
            } else {
                Utility.dialog("Eliminar", "Eliminar suelos", "Se ha eliminado el tipo de suelo de forma correcta.");
                Variables.getInstance().suelosProperties.remove(Variables.getInstance().suelosProperty);
                dialog.close();
            }
        });
        buttonCancel.setOnAction(event -> dialog.close());
        dialog.show();
        return daoSuelos.get_error() == null;
    }

    private void saveFile() {
        if (Variables.getInstance().datosSondeo == null) {
            Utility.dialog("Guardar", "Guardar archivo", "No se puede guardar el archivo, no hay datos del sondeo a guardar, intente nuevamente");
            showDatosSondeoDialog();
            return;
        }
        try {
            File file = Variables.getInstance().file;
            FileChooser fileChooser = new FileChooser();
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Sondeos Files (*.sxml)",
                    "*.sxml");
            fileChooser.getExtensionFilters().add(extFilter);
            Stage stage = (Stage) centerPane.getScene().getWindow();
            // Show save file dialog
            if (file == null) {
                file = fileChooser.showSaveDialog(stage);
            }
            if (file != null) {
                ArchivoXml archivoXml = new ArchivoXml();
                archivoXml.setDocument();
                archivoXml.prepararElementosDatos(Variables.getInstance().datosCampoProperties, centerPane);
                archivoXml.prepararElementosClasificacion(Variables.getInstance().clasificacionSucsProperties, centerPane);
                archivoXml.prepararElementHumedad(Variables.getInstance().humedadProperties, centerPane);
                archivoXml.prepararElementosProfundidad(Variables.getInstance().datosSondeo);
                archivoXml.prepararElementosAdeme(Variables.getInstance().ademeProperties, centerPane);
                archivoXml.prepararElementosTrepano(Variables.getInstance().trepanoProperties, centerPane);
                archivoXml.guardarArchivoXml(file);
                Utility.dialog("Guardar", "Guardar Archivo sondeo", "Se ha guardado el archivo de forma correcta.");
                lastVisitedDirectory = file.getParent();
                utility.writeLastDirectory(lastVisitedDirectory);
            }
        } catch (Exception ex) {
            AlertError.showAlert(ex);
        }
    }
}
