package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

@Component
public class Utility {

    private XSSFWorkbook wb;
    Map<Integer, List<Integer>> mapRotadosX;
    Map<Integer, List<Double>> mapRotadosY;
    public int initRow = 12;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final PropertiesFile propertiesFile = new PropertiesFile();

    void setWb(Workbook wb) {
        this.wb = (XSSFWorkbook) wb;
    }

    public Utility() {
        this.mapRotadosX = new HashMap<>();
        this.mapRotadosY = new HashMap<>();
    }

    public static void dialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static ButtonType resultDialog(String title, String header, String content) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle(title);
        confirm.setContentText(content);
        confirm.setHeaderText(header);
        confirm.showAndWait();
        return confirm.getResult();
    }

    public static JFXDialog showDialog(AnchorPane anchorPane, String header, String message, JFXButton... button) {
        StackPane stackPane = new StackPane();
        anchorPane.getChildren().add(stackPane);
        stackPane.setLayoutX(anchorPane.getHeight() / 2.5);
        stackPane.setLayoutY(anchorPane.getWidth() / 4);
        stackPane.autosize();
        JFXDialogLayout content = new JFXDialogLayout();
        Text txtHeader = new Text(header);
        content.setHeading(txtHeader);
        Text txtMessage = new Text(message);
        content.setBody(txtMessage);
        JFXDialog dialog = new JFXDialog(stackPane, content, JFXDialog.DialogTransition.CENTER);
        content.setActions(button);
        return dialog;
    }

    public void showDialogStage(Stage dialogStage, Parent page, String title) {
        // Create the dialog Stage.
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.setResizable(false);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        dialogStage.getIcons()
                .add(new Image(getClass().getResourceAsStream("/image/icons8_File_32px_1.png")));
    }

    public String openImage(Stage stage, ImageView imagen) {
        String pathImagen = "";
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Resource File");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
            File selectedFile = fileChooser.showOpenDialog(stage);
            if (selectedFile != null) {
                pathImagen = selectedFile.getPath();
                Image fileImage = new Image(String.valueOf(selectedFile.toURI()));
                imagen.setImage(fileImage);
            }
        } catch (Exception ex) {
            AlertError.showAlert(ex);
        }
        return pathImagen;
    }

    String truncate(final String content, final int beginIndex, final int lastIndex) {
        String result = content.substring(beginIndex, lastIndex);
        if (content.charAt(lastIndex) != ' ') {
            result = result.substring(0, result.lastIndexOf(" "));
        }
        return result;
    }

    void crearDatosCampo(XSSFSheet sheet, ObservableList<DatosCampoProperty> datosCampoProperties, ObservableList<ClasificacionSucsProperty> clasificacionSucsProperties) throws SQLException {
        XSSFCellStyle cellStyleCenter = customCellStyle(wb, HorizontalAlignment.CENTER, (short) 22);
        CellStyle cellStyleLeft = customCellStyle(wb, HorizontalAlignment.LEFT, (short) 22);
        CellStyle cellStyleRight = customCellStyle(wb, HorizontalAlignment.RIGHT, (short) 22);
        CellStyle cellStyleCenter2 = customCellStyle(wb, HorizontalAlignment.CENTER, (short) 34);
        int numCeldaAnterior = initRow;
        int cbsb = Integer.parseInt(propertiesFile.getProperty("cbsb"));
        DaoSuelos daoSuelos = new DaoSuelos();
        double profundidadCBSB = 0d;
        XSSFDataFormat dataFormat = wb.createDataFormat();
        Optional<ClasificacionSucsProperty> any = clasificacionSucsProperties.stream()
                .filter(f -> f.getTipoSuelo() == cbsb).findAny();
        if (any.isPresent()) {
            SuelosProperty suelosProperty = daoSuelos.findById(cbsb);
            ClasificacionSucsProperty clasificacionSucsProperty = any.get();
            if (suelosProperty != null) {
                profundidadCBSB = clasificacionSucsProperty.getProfundidad();
                int dif = (int) (clasificacionSucsProperty.getProfundidad() * 2);
                logger.info("Tamano de cbsb: " + dif);
                sheet.addMergedRegion(new CellRangeAddress(initRow, initRow + dif - 1, 10, 10));
                sheet.addMergedRegion(new CellRangeAddress(initRow, initRow + dif - 1, 11, 11));
                sheet.addMergedRegion(new CellRangeAddress(initRow, initRow + dif - 1, 12, 12));
                numCeldaAnterior = initRow + dif;
            }
        }
        for (DatosCampoProperty dato : datosCampoProperties) {
            if (profundidadCBSB == dato.getProfundidadFinal()) continue;
            logger.info("Inicio de celda " + numCeldaAnterior);
            Row row = sheet.getRow(numCeldaAnterior);
            if (row == null) {
                row = sheet.createRow(numCeldaAnterior);
            }
            //----------------- Recobro ------------------------------
            Cell cell = row.createCell(10);
            if (dato.getRecobro() == 0) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(dato.getRecobro() + "\"");
            }
            cell.setCellStyle(cellStyleCenter);
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, numCeldaAnterior + 2, 10, 10));
            //--------------------Golpe 1 ----------------------------
            cell = row.createCell(11);
            if (dato.getGolpe1() == 0) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(dato.getGolpe1());
            }
            //----------------- Golpe 1 * 2 --------------------------
            cell.setCellStyle(cellStyleLeft);
            cell = row.createCell(12);
            int multi = dato.getGolpe1() * 2;
            if (multi == 0) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(multi);
            }
            cell.setCellStyle(cellStyleLeft);
            // next row + 1
            numCeldaAnterior += 1;
            row = sheet.getRow(numCeldaAnterior);
            if (row == null) {
                row = sheet.createRow(numCeldaAnterior);
            }
            cell = row.createCell(11);
            if (dato.getGolpe2() == 0) {
                cell = row.createCell(16);
                cell.setCellValue("");
                //cell.setCellValue("R O T A D O");
                //sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, numCeldaAnterior, 16, 19));
                cell.setCellStyle(cellStyleLeft);
            } else {
                cell.setCellValue(dato.getGolpe2());
            }
            cell.setCellStyle(cellStyleCenter);
            cell = row.createCell(12);
            int suma = dato.getGolpe2() + dato.getGolpe3();
            if (suma == 0) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(suma);
            }
            cell.setCellStyle(cellStyleCenter2);
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, numCeldaAnterior + 1, 12, 12));
            // next row + 1
            numCeldaAnterior += 1;
            row = sheet.getRow(numCeldaAnterior);
            if (row == null) {
                row = sheet.createRow(numCeldaAnterior);
            }
            cell = row.createCell(11);
            if (dato.getGolpe3() == 0) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(dato.getGolpe3());
            }
            cell.setCellStyle(cellStyleRight);
            numCeldaAnterior += 1;
        }
        // establecer datos de la celda profundidad
        numCeldaAnterior = initRow;
        int count = 1;
        int size = datosCampoProperties.size();
        int total = (int) (datosCampoProperties.get(size - 1).getProfundidadFinal() * 2);
        for (int j = 1; j <= total; j++) {
            Row row = sheet.getRow(numCeldaAnterior);
            if (row == null) {
                row = sheet.createRow(numCeldaAnterior);
            }
            //row.setHeight((short) 20);
            row.setHeightInPoints(20);
            Cell cell = row.createCell(13);
            if (j % 2 == 0) {
                cell.setCellValue(count + "'");
                count++;
            }

            cell.setCellStyle(cellStyleCenter);
            numCeldaAnterior += 1;
        }
    }

    void datosHumedad(XSSFSheet sheet, ObservableList<HumedadProperty> humedadProperties, int size) {
        if (humedadProperties.size() <= 0) {
            return;
        }
        int auxProfundidadInicial = 0;
        CellStyle cellStyle = customCellStyle(sheet.getWorkbook(), HorizontalAlignment.CENTER,
                (short) 18);
        int init = 0;
        for (HumedadProperty dato : humedadProperties) {
            int firstRow = (int) (dato.getProfundidadInicial() * 2);
            int lastRow = (int) (dato.getProfundidadFinal() * 2);
            Row row = sheet.getRow(firstRow + 12);
            if (row == null) {
                row = sheet.createRow(firstRow + 12);
            }
            if (init == 0) {
                if (auxProfundidadInicial != firstRow) {
                    sheet.addMergedRegion(new CellRangeAddress(12, firstRow + 11, 9, 9));
                }
            } else {
                if ((auxProfundidadInicial + 1) != firstRow) {
                    sheet.addMergedRegion(new CellRangeAddress(auxProfundidadInicial + 12, firstRow + 11, 9, 9));
                }
            }
            Cell cell = row.createCell(9);
            cell.setCellValue(dato.getHumedad());
            cell.setCellStyle(cellStyle);
            sheet.addMergedRegion(new CellRangeAddress(firstRow + 12, lastRow + 11, 9, 9));
            auxProfundidadInicial = lastRow;
            init += 1;
        }
        if (auxProfundidadInicial < size) {
            sheet.addMergedRegion(new CellRangeAddress(auxProfundidadInicial + 12, size + 11, 9, 9));
        }
    }

    void datosTrepano(XSSFSheet sheet, ObservableList<TrepanoProperty> trepanoProperties, int size) {
        if (trepanoProperties.size() <= 0) {
            return;
        }
        int _initRow = 12;
        double _auxAdeme = 0d;
        CellStyle cellStyle = customCellStyle(sheet.getWorkbook(), HorizontalAlignment.CENTER, (short) 16);
        for (TrepanoProperty dato : trepanoProperties) {
            Row row = sheet.getRow(_initRow);
            if (row == null) {
                row = sheet.createRow(_initRow);
            }
            Cell cell = row.createCell(4);
            cell.setCellStyle(cellStyle);
            double merged = (dato.getProfundidad() * 2);
            int mergedAdeme = (int) Math.abs(_auxAdeme - merged) - 1;
            if (merged > size) {
                sheet.addMergedRegion(new CellRangeAddress(_initRow, size + 10, 4, 4));
            } else {
                sheet.addMergedRegion(new CellRangeAddress(_initRow, mergedAdeme + _initRow, 4, 4));
            }
            cell.setCellValue(dato.getTrepano());
            _initRow = _initRow + mergedAdeme + 1;
            _auxAdeme = merged;
        }
        if (_auxAdeme < size) {
            int merged = (int) Math.abs(size - _auxAdeme) + _initRow - 1;
            sheet.addMergedRegion(new CellRangeAddress(_initRow, merged, 4, 4));
        }
    }

    public XSSFCellStyle customCellStyle(XSSFWorkbook wb, HorizontalAlignment horizontal, short fontSize) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints(fontSize);
        cellStyle.setFont(font);
        cellStyle.setAlignment(horizontal);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        return cellStyle;
    }

    public XSSFCellStyle customCellStyle(XSSFWorkbook wb) {
        XSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 22);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setShrinkToFit(true);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        return cellStyle;
    }

    void generateSeriesX(ObservableList<DatosCampoProperty> datosCampoProperties) {
        try {
            mapRotadosX.clear();
            mapRotadosY.clear();
            List<Integer> cantZeros = new ArrayList<>();
            List<Integer> cantValues = new ArrayList<>();
            List<Double> yLista = yValues(datosCampoProperties);
            List<Integer> xLista = xValues(datosCampoProperties);
            int indexZero = 0;
            int indexValue = 0;
            for (int i = 0; i < xLista.size(); i++) {
                int val = xLista.get(i);
                if (i == 0) {
                    if (val != 0) {
                        cantZeros.add(0);
                    }
                } else {
                    if (val == 0) {
                        indexZero += 1;
                        if (indexValue > 0) cantValues.add(indexValue);
                        indexValue = 0;
                    } else {
                        if (indexZero > 0) cantZeros.add(i);
                        indexValue = i;
                        indexZero = 0;
                    }
                }
                if (xLista.size() - 1 == i) {
                    if (cantValues.size() != cantZeros.size()) cantValues.add(xLista.size() - 1);
                }
            }
            if (cantValues.size() <= 0) {
                List<Integer> x = new ArrayList<>();
                List<Double> y = new ArrayList<>();
                x.add(0);
                y.add(0d);
                x.addAll(xLista);
                y.addAll(yLista);
                x.add(0);
                x.add(0);
                y.add(yLista.get(yLista.size() - 1));
                y.add(y.get(0));
                mapRotadosX.put(1, x);
                mapRotadosY.put(1, y);
            } else {
                for (int j = 0; j < cantZeros.size(); j++) {
                    List<Integer> x = new ArrayList<>();
                    List<Double> y = new ArrayList<>();
                    x.add(0);
                    int toIndex = cantValues.get(j) + 1;
                    int fromIndex = cantZeros.get(j);
                    if (cantZeros.get(j) == 0) {
                        y.add(0d);
                    } else {
                        y.add(yLista.get(cantZeros.get(j) - 1));
                    }
                    List<Double> tempList = yLista.subList(fromIndex, toIndex);
                    y.addAll(tempList);
                    List<Integer> tempList2 = xLista.subList(fromIndex, toIndex);
                    x.addAll(tempList2);
                    x.add(0);
                    x.add(0);
                    y.add(tempList.get(tempList.size() - 1));
                    y.add(tempList.get(0));
                    mapRotadosX.put(mapRotadosX.size() + 1, x);
                    mapRotadosY.put(mapRotadosY.size() + 1, y);
                }
                System.out.println(mapRotadosX + " <> " + mapRotadosY);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    List<Integer> xValues(ObservableList<DatosCampoProperty> datosCampoProperties) {
        List<Integer> listaValores = new ArrayList<>();
        for (DatosCampoProperty dato : datosCampoProperties) {
            Integer multi = dato.getGolpe1() * 2;
            Integer suma = dato.getGolpe2() + dato.getGolpe3();
            listaValores.add(multi);
            listaValores.add(multi);
            listaValores.add(suma);
            listaValores.add(suma);
        }
        return listaValores;
    }

    List<Double> yValues(ObservableList<DatosCampoProperty> datosCampoProperties) {
        double constantePies = 0.0;
        List<Integer> listaValores = this.xValues(datosCampoProperties);
        List<Double> listaConstante = new ArrayList<>();
        int size = datosCampoProperties.size() * 3;
        System.out.println("Size of array " + size);
        for (int j = 1; j <= size; j++) {
            listaConstante.add(constantePies);
            if (j % 2 == 0) {
                constantePies = constantePies + 1;
            } else {
                constantePies = constantePies + 0.5;
            }
            listaConstante.add(constantePies);
            if (listaConstante.size() == listaValores.size()) {
                break;
            }
        }
        return listaConstante;
    }

    @SuppressWarnings("unused")
    public CellStyle createBackgroundColorXSSFCellStyle(Workbook wb, IndexedColors color, FillPatternType foreGround) {
        CellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 18);
        cellStyle.setWrapText(true);
        cellStyle.setFont(font);
        cellStyle.setRotation((short) 90);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setFillPattern(foreGround);
        cellStyle.setFillBackgroundColor(IndexedColors.WHITE.index);
        cellStyle.setFillForegroundColor(color.index);
        return cellStyle;
    }

    public void loadColors(ObservableList<IndexedColors> itemsColorPoperties) {
        for (IndexedColors colors : IndexedColors.values()) {
            try {
                XSSFColor color = new XSSFColor(colors, null);
                if (!color.getARGBHex().equals("null")) {
                    itemsColorPoperties.add(colors);
                }
            } catch (Exception ignored) {
            }
        }

    }

    public <T> ComboBoxTableCell<T, IndexedColors> comboBoxColors(ObservableList<IndexedColors> itemsColorPoperties) {
        ComboBoxTableCell<T, IndexedColors> comboBoxTableCell = new ComboBoxTableCell<>();
        comboBoxTableCell.getItems().addAll(itemsColorPoperties);
        comboBoxTableCell.updateSelected(true);
        comboBoxTableCell.getComboBox().valueProperty().addListener((observable, oldValue, newValue) -> {
            XSSFColor color = new XSSFColor(newValue, null);
            comboBoxTableCell.setStyle(" -fx-background-color: #" + color.getARGBHex().substring(2));
        });
        comboBoxTableCell.updateSelected(true);
        return comboBoxTableCell;
    }

    public <T> ComboBoxTableCell<T, FillPatternType> comboBoxPattern(ObservableList<FillPatternType> patternTypesProperties) {
        ComboBoxTableCell<T, FillPatternType> comboBoxTableCell = new ComboBoxTableCell<>();
        comboBoxTableCell.getItems().addAll(patternTypesProperties);
        comboBoxTableCell.updateSelected(true);
        comboBoxTableCell.updateSelected(true);
        return comboBoxTableCell;
    }

    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void mousePressed(MouseEvent event) {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    public static void mouseDragged(MouseEvent event, Stage stage) {
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);

    }

    public void writeLastDirectory(String value) {
        PrintWriter fw;
        try {
            File checkfile = new File("c:\\config");
            if (!checkfile.isDirectory()) {
                checkfile.mkdir();
            }
            fw = new PrintWriter("c:\\config\\directory.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(value);
            bw.close();
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readLastDirectory() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("c:\\config\\directory.txt"));
            String line = reader.readLine();
            reader.close();
            if (line.equals("null")) return System.getProperty("user.hom");
            File file = new File(line);
            if (file.exists()) {
                return line;
            } else {
                return System.getProperty("user.hom");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return System.getProperty("user.home");
        }
    }
}
