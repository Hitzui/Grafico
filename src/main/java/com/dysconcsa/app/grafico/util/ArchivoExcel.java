package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.dao.DaoConfiguration;
import com.dysconcsa.app.grafico.dao.DaoEmpresa;
import com.dysconcsa.app.grafico.model.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTabPane;
import javafx.application.HostServices;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xddf.usermodel.*;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dysconcsa.app.grafico.util.Utility.showDialog;

@Component
public class ArchivoExcel {

    Logger logger = LoggerFactory.getLogger(getClass());
    private ObservableList<AdemeProperty> ademeProperties;
    private ObservableList<DatosCampoProperty> datosCampoProperties;
    private ObservableList<ClasificacionSucsProperty> clasificacionSucsProperties;
    private ObservableList<HumedadProperty> humedadProperties;
    private List<DatosSondeo> datosSondeos;
    private ObservableList<TrepanoProperty> trepanoProperties;
    private final Utility utility;
    private final int lastRow = 29;
    private final Map<Integer, Integer> seriesGrafico = new HashMap<>();
    UpdateUtility updateUtility = new UpdateUtility();

    public void setTabPane(JFXTabPane tabPane) {
    }

    public void setTrepanoProperties(ObservableList<TrepanoProperty> trepanoProperties) {
        this.trepanoProperties = trepanoProperties;
    }

    public void setDatosCampoProperties(ObservableList<DatosCampoProperty> datosCampoProperties) {
        this.datosCampoProperties = datosCampoProperties;
    }

    public void setClasificacionSucsProperties(ObservableList<ClasificacionSucsProperty> clasificacionSucsProperties) {
        this.clasificacionSucsProperties = clasificacionSucsProperties;
    }

    public void setHumedadProperties(ObservableList<HumedadProperty> humedadProperties) {
        this.humedadProperties = humedadProperties;
    }

    public void setDatosSondeos(List<DatosSondeo> datosSondeos) {
        this.datosSondeos = datosSondeos;
    }

    public ArchivoExcel() {
        utility = new Utility();
    }

    public void crearArchivo(AnchorPane anchorPane, File file, EmpresaProperty selectedEmpresa, DatosSondeo datosSondeo,
                             ObservableList<AdemeProperty> ademeProperties, HostServices hostServices) {
        this.ademeProperties = ademeProperties;
        if (selectedEmpresa == null || selectedEmpresa.getId() == 0) {
            Utility.dialog("Error", "", "No se ha especificado un cliente, por favor especifique un cliente para continuar.");
            return;
        }
        // create a new workbook
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            DaoEmpresa daoEmpresa = new DaoEmpresa();
            EmpresaProperty empresaProperty = daoEmpresa.find(selectedEmpresa.getId());
            DaoConfiguration daoConfiguration = new DaoConfiguration();
            if (Variables.getInstance().selectedConfiguration == null) {
                Variables.getInstance().selectedConfiguration = daoConfiguration.findOne();
            }
            ConfigurationProperty configurationProperty = Variables.getInstance().selectedConfiguration;
            String pathImage = "";
            if (configurationProperty == null) {
                pathImage = String.valueOf(getClass().getResource("/image/logo.jpg"));
            } else {
                pathImage = configurationProperty.getImagen();
            }
            XSSFSheet sheet = wb.createSheet();
            XSSFPrintSetup ps = sheet.getPrintSetup();
            ps.setLandscape(true);
            //sheet.setAutobreaks(true);
            sheet.setHorizontallyCenter(true);
            ps.setFitHeight((short) 0);
            ps.setFitWidth((short) 1);
            insertImage(wb, sheet, pathImage);
            // datos del cliente
            Font fontBold = wb.createFont();
            fontBold.setFontHeight((short) 22);
            //fontBold.setFontName("Bahnschrift Condensed");
            fontBold.setBold(true);
            CellStyle cellStyleRight = wb.createCellStyle();
            CellStyle cellStyleLeft = wb.createCellStyle();
            cellStyleLeft.setFont(fontBold);
            cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
            //cellStyleLeft.setWrapText(true);
            cellStyleLeft.setVerticalAlignment(VerticalAlignment.TOP);
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFont(fontBold);
            cellStyleRight.setFont(fontBold);
            cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
            cellStyleRight.setVerticalAlignment(VerticalAlignment.TOP);
            datosCliente(sheet, empresaProperty, datosSondeo);
            data(sheet, datosSondeo, cellStyle, wb, fontBold);
            //ancho de la fila 0, donde esta la cota, no se ve el valor, por eso lo ampliamos
            sheet.setColumnWidth(0, 4000);
            heightCell(sheet, datosCampoProperties);
            // esto es para darle borde a todo
            PropertyTemplate pt = new PropertyTemplate();
            //nombre del cliente
            pt.drawBorders(new CellRangeAddress(0, 0, 7, lastRow), BorderStyle.THIN, BorderExtent.BOTTOM);
            //proyecto
            pt.drawBorders(new CellRangeAddress(1, 1, 7, lastRow), BorderStyle.THIN, BorderExtent.BOTTOM);
            pt.drawBorders(new CellRangeAddress(2, 2, 7, 23), BorderStyle.THIN, BorderExtent.BOTTOM);
            //sondeo numero
            pt.drawBorders(new CellRangeAddress(2, 2, 27, lastRow), BorderStyle.THIN, BorderExtent.BOTTOM);
            //lugar
            pt.drawBorders(new CellRangeAddress(3, 3, 7, lastRow), BorderStyle.THIN, BorderExtent.BOTTOM);
            //cuadro de datos del sondeo
            pt.drawBorders(new CellRangeAddress(5, 9, 10, lastRow), BorderStyle.THIN, BorderExtent.OUTSIDE);
            //elevacion
            pt.drawBorders(new CellRangeAddress(10, 11, 0, 9), BorderStyle.THIN, BorderExtent.OUTSIDE);
            //Recobro
            pt.drawBorders(new CellRangeAddress(10, 11, 10, 10), BorderStyle.THIN, BorderExtent.OUTSIDE);
            //Golpes
            pt.drawBorders(new CellRangeAddress(10, 11, 11, 11), BorderStyle.THIN, BorderExtent.OUTSIDE);
            //Golpes pie
            pt.drawBorders(new CellRangeAddress(10, 11, 12, 12), BorderStyle.THIN, BorderExtent.OUTSIDE);
            //profundidad
            pt.drawBorders(new CellRangeAddress(10, 11, 13, 13), BorderStyle.THIN, BorderExtent.OUTSIDE);
            //grafico
            pt.drawBorders(new CellRangeAddress(10, 11, 14, lastRow), BorderStyle.THIN, BorderExtent.OUTSIDE);
            //cuadro de titulos
            pt.drawBorders(new CellRangeAddress(5, 9, 0, 9), BorderStyle.THIN, BorderExtent.ALL);
            double profundidadFinal = datosCampoProperties.get(datosCampoProperties.size() - 1).getProfundidadFinal();
            //logger.info("Profundidad final del ultimo golpe: " + profundidadFinal);
            int size = (int) (profundidadFinal * 2);
            pt.drawBorders(new CellRangeAddress(12, size + 11, 0, 13), BorderStyle.THIN, BorderExtent.ALL);
            //borde exterior completo
            pt.drawBorders(new CellRangeAddress(5, size + 11, 0, lastRow), BorderStyle.MEDIUM, BorderExtent.OUTSIDE);
            //operador
            pt.drawBorders(new CellRangeAddress(6, 6, 13, 21), BorderStyle.THIN, BorderExtent.BOTTOM);
            //nivel freatico
            pt.drawBorders(new CellRangeAddress(6, 6, 25, lastRow - 1), BorderStyle.THIN, BorderExtent.BOTTOM);
            //observaciones
            pt.drawBorders(new CellRangeAddress(7, 7, 13, lastRow - 1), BorderStyle.THIN, BorderExtent.BOTTOM);
            //archivo
            pt.drawBorders(new CellRangeAddress(8, 8, 13, 23), BorderStyle.THIN, BorderExtent.BOTTOM);
            //fecha
            pt.drawBorders(new CellRangeAddress(8, 8, 26, lastRow - 1), BorderStyle.THIN, BorderExtent.BOTTOM);
            pt.applyBorders(sheet);
            // String archivo = "sondeos.xlsx";
            XSSFPrintSetup printSetup = sheet.getPrintSetup();
            printSetup.setLandscape(true);
            printSetup.setPaperSize(PrintSetup.LETTER_PAPERSIZE);
            // sheet.createFreezePane(0, 4);
            sheet.setFitToPage(true);
            //sheet.setAutobreaks(true);
            // printSetup.setFooterMargin(0.25);
            Footer footer = sheet.getFooter();
            String footerText = "\"Clave: AW - Nw EX, AX, BX, nx - Diametro Standard. T = Tungsteno, D = Diamante, Do = Doble, CP = Cola de Pescado, CN = Cuchara Normal, PD = Tubo de Pared Delgada.\"";
            footer.setLeft(HeaderFooter.startBold() + HeaderFooter.fontSize((short) 20) + footerText.toUpperCase());
            footer.setRight(HeaderFooter.fontSize((short) 20) + HeaderFooter.page().toUpperCase() + " De " + HeaderFooter.numPages().toUpperCase());
            sheet.setMargin(Sheet.LeftMargin, 0.25);
            sheet.setMargin(Sheet.RightMargin, 0.25);
            sheet.setMargin(Sheet.TopMargin, 0.25);
            sheet.setMargin(Sheet.BottomMargin, 0.5);
            // aca repetimos el encabezado por cada hoja a imprimir
            sheet.setRepeatingRows(new CellRangeAddress(0, 11, 0, lastRow));
            //LINEA DEL FIN DE SONDEO
            Row endRow = sheet.createRow(size + 13);
            Cell endCell = endRow.createCell(0);
            CellStyle endStyle = wb.createCellStyle();
            endStyle.setFont(fontBold);
            endStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            endStyle.setAlignment(HorizontalAlignment.CENTER);
            endCell.setCellValue("Fin del Sondeo No. " + datosSondeo.getSondeoNumero());
            endCell.setCellStyle(endStyle);
            //pie de pagina, indicando fil del sondeo
            sheet.addMergedRegion(new CellRangeAddress(size + 13, size + 13, 0, lastRow));
            try (OutputStream fileOut = new FileOutputStream(file)) {
                wb.write(fileOut);
                wb.close();
                JFXButton btnAceptar = new JFXButton("Aceptar");
                JFXButton btnCancelar = new JFXButton("Cancelar");
                JFXDialog dialog = showDialog(anchorPane, "Informacion",
                        "Se ha generado el archivo de forma correcta, ¿Desea abrir el archivo?", btnAceptar,
                        btnCancelar);
                btnCancelar.setOnAction(e -> dialog.close());
                btnAceptar.setOnAction(e -> {
                    EventQueue.invokeLater(() -> {
                        hostServices.showDocument(String.valueOf(file.toURI()));
                    });
                    dialog.close();
                });
                dialog.show();
            } catch (Exception e) {
                Utility.dialog("Error", "Generar Archivo", "No se pudo generar el archivo del grafico, revise si esta abierto o los datos ingresados.");
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void datosCliente(XSSFSheet sheet, EmpresaProperty empresaProperty, DatosSondeo datosSondeo) {
        XSSFWorkbook wb = sheet.getWorkbook();
        CellStyle cellStyleRight = wb.createCellStyle();
        CellStyle cellStyleLeft = wb.createCellStyle();
        Font fontBold = wb.createFont();
        fontBold.setBold(false);
        fontBold.setFontHeightInPoints((short) 22);
        cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleLeft.setFont(fontBold);
        cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        cellStyleRight.setFont(fontBold);
        // datos del cliente
        XSSFRow row = sheet.createRow(0);
        Cell cell = row.createCell(6);
        cell.setCellStyle(cellStyleRight);
        cell.setCellValue("Cliente:");
        sheet.setColumnWidth(cell.getColumnIndex(), 700);
        cell = row.createCell(7);
        cell.setCellStyle(cellStyleLeft);
        cell.setCellValue(empresaProperty.getCliente());
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, lastRow));
        // proyecto
        int lengthProyecto = empresaProperty.getProyecto().length();
        row = sheet.createRow(1);
        Cell cellProyecto = row.createCell(6);
        cellProyecto.setCellStyle(cellStyleRight);
        cellProyecto.setCellValue("Proyecto:");
        cellProyecto = row.createCell(7);
        cellProyecto.setCellStyle(cellStyleLeft);
        String cortarProyecto1;
        String cortarProyecto2 = "";
        if (lengthProyecto > 130) {
            cortarProyecto1 = utility.truncate(empresaProperty.getProyecto(), 0, 130);
            cortarProyecto2 = utility.truncate(empresaProperty.getProyecto(), cortarProyecto1.length(), lengthProyecto - 1);
        } else {
            cortarProyecto1 = empresaProperty.getProyecto();
        }

        cellProyecto.setCellValue(cortarProyecto1);
        //sheet.addMergedRegion(new CellRangeAddress(1, 2, 7, 21));
        // sondeo numero
        row = sheet.createRow(2);
        if (cortarProyecto2.length() > 0) {
            cellProyecto = row.createCell(7);
            cellProyecto.setCellValue(cortarProyecto2);
            cellProyecto.setCellStyle(cellStyleLeft);
        }
        Cell cellSondeo = row.createCell(24);
        CellStyle cellStyleSondeoNumero = wb.createCellStyle();
        Font fontSondeo = wb.createFont();
        fontSondeo.setFontHeightInPoints((short) 28);
        fontSondeo.setBold(true);
        cellStyleSondeoNumero.setFont(fontSondeo);
        cellStyleSondeoNumero.setAlignment(HorizontalAlignment.CENTER);
        cellSondeo.setCellStyle(cellStyleSondeoNumero);
        cellSondeo.setCellValue("Sondeo No:");
        cellSondeo.setCellStyle(cellStyleSondeoNumero);
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 24, 26));
        sheet.addMergedRegion(new CellRangeAddress(2, 2, 27, lastRow));
        cell = row.createCell(27);
        cell.setCellStyle(cellStyleSondeoNumero);
        cell.setCellValue(datosSondeo.getSondeoNumero());
        // lugar
        row = sheet.createRow(3);
        cell = row.createCell(6);
        cell.setCellValue("Lugar:");
        cell.setCellStyle(cellStyleRight);
        cell = row.createCell(7);
        cell.setCellValue(datosSondeo.getLugar());
        cell.setCellStyle(cellStyleLeft);
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 7, lastRow));
    }

    private void data(XSSFSheet sheet, DatosSondeo datosSondeo, CellStyle cellStyle, XSSFWorkbook wb,
                      Font fontBold) {
        CellStyle cellStyleLeft = wb.createCellStyle();
        cellStyleLeft.setAlignment(HorizontalAlignment.LEFT);
        cellStyleLeft.setFont(fontBold);
        cellStyleLeft.setVerticalAlignment(VerticalAlignment.CENTER);
        CellStyle cellStyleRight = wb.createCellStyle();
        cellStyleRight.setAlignment(HorizontalAlignment.RIGHT);
        cellStyleRight.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyleRight.setFont(fontBold);
        // ********************* operador **********************************/
        XSSFRow rowVacia = sheet.createRow(6);
        Cell cellLugar = rowVacia.createCell(10);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 10, 12));
        cellLugar.setCellValue("Operador: ");
        cellLugar.setCellStyle(cellStyleRight);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 13, 21));
        cellLugar = rowVacia.createCell(13);
        cellLugar.setCellValue(datosSondeo.getOperador());
        cellLugar.setCellStyle(cellStyleLeft);
        // ********************** nivel friatico *************************/
        cellLugar = rowVacia.createCell(22);
        //sheet.setColumnWidth(21, 3500);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 22, 24));
        cellLugar.setCellValue("Nivel Freatico:");
        cellLugar.setCellStyle(cellStyleRight);
        sheet.addMergedRegion(new CellRangeAddress(6, 6, 25, lastRow - 1));
        cellLugar = rowVacia.createCell(25);
        cellLugar.setCellValue(datosSondeo.getNivelFreatico());
        cellLugar.setCellStyle(cellStyleLeft);
        // ***************** observaciones *******************/
        rowVacia = sheet.createRow(7);
        cellLugar = rowVacia.createCell(10);
        cellLugar.setCellValue("Observaciones:");
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 10, 12));
        cellLugar.setCellStyle(cellStyleRight);
        sheet.addMergedRegion(new CellRangeAddress(7, 7, 13, lastRow - 1));
        cellLugar = rowVacia.createCell(13);
        cellLugar.setCellValue(datosSondeo.getObservaciones());
        cellLugar.setCellStyle(cellStyleLeft);
        // *****************************************************/
        rowVacia = sheet.createRow(8);
        // **************** archivo ****************************/
        cellLugar = rowVacia.createCell(10);
        cellLugar.setCellValue("Archivo: ");
        cellLugar.setCellStyle(cellStyleRight);
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 10, 12));
        cellLugar = rowVacia.createCell(13);
        cellLugar.setCellValue(datosSondeo.getArchivo());
        cellLugar.setCellStyle(cellStyleLeft);
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 13, 23));
        // ************** fecha *******************************/
        cellLugar = rowVacia.createCell(24);
        cellLugar.setCellValue("Fecha: ");
        cellLugar.setCellStyle(cellStyleRight);
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 24, 25));
        cellLugar = rowVacia.createCell(26);
        cellLugar.setCellValue(datosSondeo.getFecha());
        sheet.addMergedRegion(new CellRangeAddress(8, 8, 26, lastRow - 1));
        cellLugar.setCellStyle(cellStyleLeft);
        // ****************************************************/
        rowVacia = sheet.createRow(5);
        rowVacia.setHeight((short) 600);
        // **************************************************/
        // rowVacia.setHeight((short) 1500);
        XSSFCell cellCota = rowVacia.createCell(0);
        cellCota.setCellValue("Cota en \nMetros");
        borderCellRotate(cellCota, wb, fontBold, (short) 90);
        XSSFCell cellProfundidadM = rowVacia.createCell(1);
        cellProfundidadM.setCellValue("Profundidad \nen metros");
        borderCellRotate(cellProfundidadM, wb, fontBold, (short) 90);
        XSSFCell cellEspesor = rowVacia.createCell(2);
        cellEspesor.setCellValue("Espesor \nEstratos \nen metros");
        borderCellRotate(cellEspesor, wb, fontBold, (short) 90);
        sheet.setColumnWidth(cellEspesor.getColumnIndex(), 5000);
        XSSFCell cellAdeme = rowVacia.createCell(3);
        cellAdeme.setCellValue("Ademe");
        borderCellRotate(cellAdeme, wb, fontBold, (short) 90);
        XSSFCell cellTrepano = rowVacia.createCell(4);
        cellTrepano.setCellValue("Trepano");
        borderCellRotate(cellTrepano, wb, fontBold, (short) 90);
        XSSFCell cellSucs = rowVacia.createCell(5);
        cellSucs.setCellValue("Clasificacion \nS.U.C.S");
        borderCellRotate(cellSucs, wb, fontBold, (short) 90);
        XSSFCell cellDescripcion = rowVacia.createCell(6);
        XSSFRichTextString richTextString = new XSSFRichTextString("Descripcion Geologica y Clasificacion\n Del Material Encontrado");
        cellDescripcion.setCellValue(richTextString);
        borderCellRotate(cellDescripcion, wb, fontBold, (short) 0);
        //sheet.addMergedRegion(new CellRangeAddress(5, 9, 6, 6));
        sheet.autoSizeColumn(6, true);
        //borderCell(cell, cellStyleDatos, fontBold);
        XSSFCell cellLimite = rowVacia.createCell(7);
        cellLimite.setCellValue("Limite \nLiquido");
        borderCellRotate(cellLimite, wb, fontBold, (short) 90);
        XSSFCell cell = rowVacia.createCell(8);
        cell.setCellValue("Indice de \nPlasticidad");
        borderCellRotate(cell, wb, fontBold, (short) 90);
        cell = rowVacia.createCell(9);
        cell.setCellValue("Humedad \nnatural");
        borderCellRotate(cell, wb, fontBold, (short) 90);
        // *************** DATOS ********************************************/
        CellStyle _cellStyle = wb.createCellStyle();
        _cellStyle.setShrinkToFit(true);
        _cellStyle.setWrapText(true);
        _cellStyle.setAlignment(HorizontalAlignment.CENTER);
        _cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFFont font = wb.createFont();
        font.setBold(true);
        //font.setFontName("Bahnschrift Condensed");
        font.setFontHeightInPoints((short) 16);
        _cellStyle.setFont(font);
        XSSFRow rowDatos = sheet.createRow(10);
        sheet.addMergedRegion(new CellRangeAddress(10, 11, 0, 9));
        RegionUtil.setBorderBottom(BorderStyle.MEDIUM, new CellRangeAddress(10, 11, 0, 9), sheet);
        cell = rowDatos.createCell(0);
        cell.setCellValue("Elevacion en metros: " + datosSondeos.get(0).getElevacion());
        cell.setCellStyle(cellStyleLeft);
        rowDatos.setHeightInPoints(45);
        Row xRow = sheet.getRow(11);
        if (xRow == null) xRow = sheet.createRow(11);
        xRow.setHeightInPoints(45);
        cell = rowDatos.createCell(10);
        sheet.addMergedRegion(new CellRangeAddress(10, 11, 10, 10));
        cell.setCellValue("Recobro");
        sheet.setColumnWidth(cell.getColumnIndex(), 3500);
        cell.setCellStyle(_cellStyle);
        cell = rowDatos.createCell(11);
        sheet.addMergedRegion(new CellRangeAddress(10, 11, 11, 11));
        cell.setCellValue("Golpes");
        sheet.setColumnWidth(cell.getColumnIndex(), 3500);
        cell.setCellStyle(_cellStyle);
        cell = rowDatos.createCell(12);
        sheet.addMergedRegion(new CellRangeAddress(10, 11, 12, 12));
        cell.setCellValue("Golpes\n Por Pie");
        sheet.setColumnWidth(cell.getColumnIndex(), 3200);
        cell.setCellStyle(_cellStyle);
        Cell cellProfundidad = rowDatos.createCell(13);
        XSSFCellStyle cellStyleProfundidad = wb.createCellStyle();
        XSSFFont fontProfundidad = wb.createFont();
        fontProfundidad.setFontHeightInPoints((short) 16);
        fontProfundidad.setBold(true);
        //fontProfundidad.setFontName("Bahnschrift Condensed");
        cellStyleProfundidad.setFont(fontProfundidad);
        cellStyleProfundidad.setAlignment(HorizontalAlignment.CENTER);
        cellStyleProfundidad.setVerticalAlignment(VerticalAlignment.CENTER);
        sheet.addMergedRegion(new CellRangeAddress(10, 11, 13, 13));
        cellProfundidad.setCellValue("Profundidad");
        cellProfundidad.setCellStyle(cellStyleProfundidad);
        sheet.setColumnWidth(cellProfundidad.getColumnIndex(), 4200);
        sheet.addMergedRegion(new CellRangeAddress(10, 11, 14, lastRow));
        try {
            int _initRow;
            //int size = datosCampoProperties.size() * 3;
            int size = (int) (datosCampoProperties.get(datosCampoProperties.size() - 1).getProfundidadFinal() * 2);
            utility.setWb(wb);
            utility.crearDatosCampo(sheet, datosCampoProperties, clasificacionSucsProperties);
            updateUtility.clasificacion(sheet, clasificacionSucsProperties, datosSondeos.get(0).getElevacion());
            utility.datosHumedad(sheet, humedadProperties, size);
            //utility.generateSeriesX(datosCampoProperties);
            utility.datosTrepano(sheet, trepanoProperties, size);
            valoresGrafico(datosCampoProperties, wb);
            // Ingreso del ademe
            _initRow = 12;
            double _auxAdeme = 0d;
            for (AdemeProperty ademeProperty : this.ademeProperties) {
                if (ademeProperty.getProfundidad() == 0d) {
                    sheet.addMergedRegion(new CellRangeAddress(12, size + 11, 3, 3));
                    _auxAdeme = size;
                    break;
                } else {
                    CellStyle style = wb.createCellStyle();
                    style.setWrapText(true);
                    style.setRotation((short) 90);
                    style.setFont(fontBold);
                    style.setAlignment(HorizontalAlignment.CENTER);
                    style.setVerticalAlignment(VerticalAlignment.CENTER);
                    Row xrow = sheet.getRow(_initRow);
                    if (xrow == null)
                        xrow = sheet.createRow(_initRow);
                    Cell _cell = xrow.getCell(3);
                    if (_cell == null)
                        _cell = xrow.createCell(3);
                    _cell.setCellStyle(style);
                    double merged = (ademeProperty.getProfundidad() * 2);
                    int mergedAdeme = (int) Math.abs(_auxAdeme - merged) - 1;
                    if (merged > size) {
                        sheet.addMergedRegion(new CellRangeAddress(_initRow, size + 11, 3, 3));
                    } else {
                        sheet.addMergedRegion(new CellRangeAddress(_initRow, mergedAdeme + _initRow, 3, 3));
                    }
                    _cell.setCellValue(ademeProperty.getDescripcion());
                    _initRow = _initRow + mergedAdeme + 1;
                    _auxAdeme = merged;
                }
            }
            if (_auxAdeme < size) {
                int merged = (int) Math.abs(size - _auxAdeme) + _initRow - 1;
                sheet.addMergedRegion(new CellRangeAddress(_initRow, merged, 3, 3));
            }
            cellStyle.setRotation((short) 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * <p>Genera los valores que se usaran en el grafico, por cada serie se genera un par (x,y), respectivamente
     * en la filas (n,m), ejemplo: (cell 0, cell 1), para cada row, segun serie n</p>
     * @param datosCampoProperties
     * @param wb
     */
    private void valoresGrafico(ObservableList<DatosCampoProperty> datosCampoProperties, Workbook wb) {
        Map<Integer, Map<List<Integer>, List<Double>>> listOfPuntosXY = updateUtility.genearXY(datosCampoProperties);
        //logger.info("Size of list: " + listOfPuntosXY.size());
        Utility utility = new Utility();
        XSSFSheet sheet = (XSSFSheet) wb.createSheet("Datos");
        for (Map.Entry<Integer, Map<List<Integer>, List<Double>>> series : listOfPuntosXY.entrySet()) {
            Map<List<Integer>, List<Double>> value = series.getValue();
            int i = series.getKey();
            int size = 0;
            for (Map.Entry<List<Integer>, List<Double>> puntos : value.entrySet()) {
                if (i > 0) i += 1;
                List<Integer> x = puntos.getKey();
                int max = x.stream().mapToInt(a -> a).max().orElse(0);
                List<Double> y = puntos.getValue();
                if (x.size() > 0) {
                    size = x.size();
                    for (int j = 0; j < size; j++) {
                        Row row = sheet.getRow(j);
                        if (row == null) row = sheet.createRow(j);
                        Cell cell = row.createCell(i);
                        cell.setCellValue(x.get(j));
                        cell = row.createCell(1 + i);
                        cell.setCellValue(y.get(j));
                    }
                }
            }
            //logger.info("" + series.getKey());
            seriesGrafico.put(series.getKey(), size);
        }
        createChart((XSSFSheet) wb.getSheetAt(0), sheet, datosCampoProperties, utility);
    }

    private void borderCellRotate(Cell cell, Workbook wb, Font fontBold, short rotation) {
        CellStyle cellStyleDatos = wb.createCellStyle();
        cellStyleDatos.setWrapText(true);
        cellStyleDatos.setRotation(rotation);
        fontBold.setFontHeightInPoints((short) 24);
        borderCell(cell, cellStyleDatos, fontBold);
        Sheet sheet = cell.getSheet();
        int columnIndex = cell.getColumnIndex();
        cell.getRow().setHeightInPoints(40);
        sheet.setColumnWidth(columnIndex, 35 * 100);
        sheet.addMergedRegion(new CellRangeAddress(5, 9, columnIndex, columnIndex));
    }

    private void borderCell(Cell cell, CellStyle cellStyleDatos, Font fontBold) {
        cellStyleDatos.setAlignment(HorizontalAlignment.CENTER);
        cellStyleDatos.setFont(fontBold);
        cellStyleDatos.setBorderBottom(BorderStyle.MEDIUM);
        cellStyleDatos.setBorderTop(BorderStyle.MEDIUM);
        cellStyleDatos.setBorderRight(BorderStyle.MEDIUM);
        cellStyleDatos.setBorderLeft(BorderStyle.MEDIUM);
        cellStyleDatos.setAlignment(HorizontalAlignment.CENTER);
        cellStyleDatos.setVerticalAlignment(VerticalAlignment.CENTER);
        cell.setCellStyle(cellStyleDatos);
    }


    public void setAxisTitle(XSSFChart chart, String title) {
        CTTextCharacterProperties ctTextCharacterProperties;
        CTChart ctChart = chart.getCTChart();
        CTTitle ctTitle = ctChart.addNewTitle();
        ctTitle.addNewOverlay().setVal(false);
        CTTx tx = ctTitle.addNewTx();
        CTTextBody rich = tx.addNewRich();
        rich.addNewBodyPr();  // body properties must exist, but can be empty
        CTTextParagraph para = rich.addNewP();
        CTRegularTextRun r = para.addNewR();
        ctTextCharacterProperties = ctChart.getTitle().getTx().getRich().addNewP().addNewPPr().addNewDefRPr();
        ctTextCharacterProperties.setSz(1600);
        ctTextCharacterProperties.setB(true);
        r.setRPr(ctTextCharacterProperties);
        r.setT(title);
    }

    private void createChart(XSSFSheet sheet, XSSFSheet sheet2, ObservableList<DatosCampoProperty> datosCampoProperties,
                             Utility utility) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        DatosCampoProperty datosCampoProperty = datosCampoProperties.get(datosCampoProperties.size() - 1);
        int num_rows = (int) ((datosCampoProperty.getProfundidadFinal() * 2) + 13);
        //List<Double> yList = utility.yValues(datosCampoProperties);
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 14, 10, lastRow + 1, num_rows);
        //Map<Integer, List<Integer>> mapRotadoX = utility.mapRotadosX;
        XSSFChart chart = drawing.createChart(anchor);
        //chart.setTitleText("N = Golpes / Pie");
        chart.getCTChartSpace().addNewRoundedCorners();
        chart.getCTChartSpace().getRoundedCorners().setVal(false);
        chart.getCTChartSpace().addNewSpPr();
        if (chart.getCTChartSpace().getSpPr().getSolidFill() == null) {
            chart.getCTChartSpace().getSpPr().addNewNoFill();
        }
        if (chart.getCTChartSpace().getSpPr().getLn() == null) {
            chart.getCTChartSpace().getSpPr().addNewLn();
            chart.getCTChartSpace().getSpPr().getLn().addNewNoFill();
        }
        CTChart ctChart = chart.getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        if (ctPlotArea.getSpPr() == null) {
            ctPlotArea.addNewSpPr();
        }
        ctPlotArea.getSpPr().addNewNoFill();
        //ctPlotArea.getCatAxArray()[0].addNewMajorGridlines();
        //ctPlotArea.getValAxArray()[0].addNewMajorGridlines();
        XDDFValueAxis bottomAxis = chart.createValueAxis(AxisPosition.BOTTOM);
        //bottomAxis.setTitle("N = Golpes / Pie");
        CTValAx ctValAx = ctPlotArea.getValAxArray((int) bottomAxis.getId());
        ctValAx.addNewMajorGridlines();
        CTTextBody ctTextBody = ctValAx.addNewTxPr();
        ctTextBody.addNewBodyPr(); //body properties
        CTTextCharacterProperties ctTextCharacterProperties = ctTextBody.addNewP().addNewPPr().addNewDefRPr(); //character properties
        ctTextCharacterProperties.setSz(20 * 100); //size in 100th of a point
        setAxisTitle(chart, "N = Golpes / Pie");
        // https://stackoverflow.com/questions/32010765
        bottomAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        bottomAxis.setMajorUnit(10);
        bottomAxis.setMaximum(100.0);
        bottomAxis.setMinorUnit(0.0);
        //bottomAxis.getOrAddMajorGridProperties();
        bottomAxis.setVisible(true);
        XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
        CTValAx ctLeftAxis = ctPlotArea.getValAxArray((int) leftAxis.getId());
        ctLeftAxis.addNewMajorGridlines();
        // leftAxis.setTitle("f(x)");
        leftAxis.crossAxis(bottomAxis);
        // leftAxis.getOrAddMajorGridProperties();
        leftAxis.setCrosses(AxisCrosses.AUTO_ZERO);
        leftAxis.setOrientation(AxisOrientation.MAX_MIN);
        double maxValue = datosCampoProperty.getProfundidadFinal();
        leftAxis.setMajorUnit(0.5);
        //leftAxis.setMajorUnit(maxValue);
        leftAxis.setMinorUnit(0);
        leftAxis.setMaximum(maxValue);
        leftAxis.setMinimum(0);
        leftAxis.setVisible(false);
        XDDFScatterChartData data = (XDDFScatterChartData) chart.createData(ChartTypes.SCATTER, bottomAxis, leftAxis);
        logger.info(seriesGrafico.size() + "");
        for (Map.Entry<Integer, Integer> map : seriesGrafico.entrySet()) {
            int i = map.getKey();
            if (i > 0) i += 1;
            int columns = i;
            //logger.info("Columns value: " + i);
            //logger.info("Map getValue: " + map.getValue());
            int rows = map.getValue();
            logger.info("XS Values: " + (rows - 1) + ", columns: " + columns + " - " + (columns + 1));
            XDDFNumericalDataSource<Double> xs = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(0, rows - 1, columns, columns));
            //logger.info("Values: " + (rows - 1) + ", culumns: " + (columns + 1) + " - " + (columns + 1));
            XDDFNumericalDataSource<Double> ys = XDDFDataSourcesFactory.fromNumericCellRange(sheet2,
                    new CellRangeAddress(0, rows - 1, columns + 1, columns + 1));
            XDDFScatterChartData.Series series = (XDDFScatterChartData.Series) data.addSeries(xs, ys);
            series.setSmooth(false);
            series.setMarkerStyle(MarkerStyle.NONE);
        }
        chart.plot(data);
        solidLineSeries(data);
    }

    private void insertImage(Workbook wb, XSSFSheet sheet,
                             String path) {
        try {
            CreationHelper helper = wb.getCreationHelper();
            // add a picture in this workbook.
            //InputStream is = new FileInputStream(getClass().getResource("/image/logo.jpg").getPath());
            InputStream is = new FileInputStream(path);
            byte[] bytes = IOUtils.toByteArray(is);
            is.close();
            int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
            // create drawing
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            // add a picture shape
            ClientAnchor anchor = helper.createClientAnchor();
            anchor.setCol1(0);
            anchor.setRow1(0);
            Picture pict = drawing.createPicture(anchor, pictureIdx);
            // auto-size picture
            pict.resize(6, 3);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void solidLineSeries(XDDFChartData data) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.BLACK));
        XDDFFillProperties fillProperties = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.RED));
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        line.setWidth(Units.pixelToEMU(6));
        for (XDDFChartData.Series values : data.getSeries()) {
            XDDFShapeProperties properties = values.getShapeProperties();
            if (properties == null) {
                properties = new XDDFShapeProperties();
            }
            properties.setFillProperties(fillProperties);
            properties.setLineProperties(line);
            values.setShapeProperties(properties);
        }
    }

    private void solidLineSeries(XDDFChartData data, int index) {
        XDDFSolidFillProperties fill = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.BLACK));
        XDDFFillProperties fillProperties = new XDDFSolidFillProperties(XDDFColor.from(PresetColor.RED));
        XDDFLineProperties line = new XDDFLineProperties();
        line.setFillProperties(fill);
        line.setWidth(Units.pixelToEMU(6));
        XDDFChartData.Series series = data.getSeries().get(index);
        XDDFShapeProperties properties = series.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setFillProperties(fillProperties);
        properties.setLineProperties(line);
        series.setShapeProperties(properties);
    }

    /***
     * Alto de las filas
     * @param sheet
     * @param datosCampoProperties
     */
    private void heightCell(XSSFSheet sheet, ObservableList<DatosCampoProperty> datosCampoProperties) {
        for (int i = 0; i < 4; i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                row = sheet.createRow(i);
            row.setHeightInPoints(50);
        }
        int size = datosCampoProperties.size() * 3;
        for (int i = 12; i < size + 12; i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                row = sheet.createRow(i);
            row.setHeightInPoints(20);
        }
    }
}
