package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.ClasificacionSucsProperty;
import com.dysconcsa.app.grafico.model.DatosCampoProperty;
import com.dysconcsa.app.grafico.model.SuelosProperty;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.*;

/**
 * <p>Actualizacion del archivo que maneja los datos para generar el excel con su grafico</p>
 * <p>Esta actualizacion se da ya que el usuario desea iniciar el valor de su sondeo desde un punto que no es
 * totalmente compatible al pie y medio, ejemplo, podria iniciar en 4 y no 3.5</p>
 */
@Component
public class UpdateUtility {

    private final PropertiesFile propertiesFile;
    Utility utility = new Utility();
    Logger logger = LoggerFactory.getLogger(getClass());
    List<Double> yValues = new ArrayList<>();
    List<Integer> xValues = new ArrayList<>();

    public UpdateUtility() {
        propertiesFile = new PropertiesFile();
    }

    public void clasificacion(XSSFSheet sheet, ObservableList<ClasificacionSucsProperty> clasificacionSucsProperties, Double elevacion) throws SQLException {
        if (clasificacionSucsProperties.size() <= 0) {
            return;
        }
        DaoSuelos daoSuelos = new DaoSuelos();
        XSSFWorkbook wb = sheet.getWorkbook();
        DataFormat format = wb.createDataFormat();
        double profundidadInicial = 0d;
        int numCeldaAnterior = utility.initRow;
        double espesor;
        double acumProf = 0.0;
        double acum_espesor = 0d;
        CellStyle style;
        CellStyle styleFormat = utility.customCellStyle(wb, HorizontalAlignment.CENTER, (short) 22);
        XSSFCellStyle cellStyle = utility.customCellStyle(wb, HorizontalAlignment.CENTER, (short) 22);
        XSSFCellStyle cellStyleBottom = utility.customCellStyle(wb);
        Optional<SuelosProperty> rotado = daoSuelos.findAll().stream().filter(suelo -> suelo.getNombre().equals("rotado")).findAny();
        double profundidadInicialSuelos = 0d;
        int cbsb = Integer.parseInt(propertiesFile.getProperty("cbsb"));
        for (ClasificacionSucsProperty clasificacion : clasificacionSucsProperties) {
            //ejemplo de rango de celdas para los valores de cotas, profundidad y estrato
            double profundidad = clasificacion.getProfundidad();
            double difProfundidad = profundidad - profundidadInicial;
            int cantCelda = (int) (difProfundidad * 2);
            int valorActual = numCeldaAnterior + cantCelda - 1;
            espesor = Math.abs(profundidad - acumProf) * 0.3048;
            acum_espesor += espesor;
            elevacion -= espesor;
            //rango de celda donde van los valores
            //rango de celda para el valor de Cota
            XSSFRow row = getRow(sheet, numCeldaAnterior);
            // cota
            Cell cell = row.createCell(0);
            sheet.setColumnWidth(cell.getColumnIndex(), 3000);
            cell.setCellValue(elevacion);
            cell.setCellStyle(cellStyleBottom);
            // profundidad
            cell = row.createCell(1);
            cell.setCellValue(acum_espesor);
            cell.setCellStyle(cellStyleBottom);
            // estrato
            cell = row.createCell(2);
            cell.setCellValue(espesor);
            cell.setCellStyle(cellStyle);
            SuelosProperty suelo = daoSuelos.findById(clasificacion.getTipoSuelo());
            if (rotado.isPresent()) {
                if (rotado.get().getID() == suelo.getID()) {
                    cell = row.getCell(16);
                    if (cell == null) cell = row.createCell(16);
                    cell.setCellValue("R O T A D O");
                    cell.setCellStyle(cellStyle);
                    sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, numCeldaAnterior, 16, 19));
                }
            }
            // ingreso de las imagenes del tipo de suelo
            XSSFCell cellSucs = row.createCell(5);
            style = utility.createBackgroundColorXSSFCellStyle(wb, clasificacion.getColor(), clasificacion.getPattern());
            cellSucs.setCellStyle(style);
            cellSucs.setCellValue(suelo.getSimbolo().toUpperCase());
            cell = row.createCell(6);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(clasificacion.getDescripcion() + "\n(" + suelo.getSimbolo().toUpperCase() + ")");
            cellStyle.setWrapText(true);
            cell.setCellStyle(cellStyle);
            Cell cellLimite = row.createCell(7);
            if (clasificacion.getLimiteLiquido() == 0) {
                cellLimite.setCellValue("");
                //sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 11, 11));
            } else {
                styleFormat.setDataFormat(format.getFormat("0"));
                cellLimite.setCellValue(clasificacion.getLimiteLiquido());
            }
            cellLimite.setCellStyle(styleFormat);
            Cell cellIndice = row.createCell(8);
            if (clasificacion.getIndicePlasticidad() == 0) {
                cellIndice.setCellValue("");
            } else {
                cellIndice.setCellValue(clasificacion.getIndicePlasticidad());
            }
            cellIndice.setCellStyle(styleFormat);
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 2, 2));
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 5, 5));
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 6, 6));
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 7, 7));
            sheet.addMergedRegion(new CellRangeAddress(numCeldaAnterior, valorActual, 8, 8));
            acumProf = clasificacion.getProfundidad();
            profundidadInicial = profundidad;
            numCeldaAnterior = valorActual + 1;
        }
    }

    private XSSFRow getRow(XSSFSheet sheet, int index) {
        XSSFRow temp = sheet.getRow(index);
        if (temp == null) {
            temp = sheet.createRow(index);
        }
        return temp;
    }

    public Map<Integer, Map<List<Integer>, List<Double>>> genearXY(List<DatosCampoProperty> datosCampoProperties) {
        double paso = 0.5;
        int aux = 0;
        Map<Integer, Map<List<Integer>, List<Double>>> valores = new HashMap<>();
        for (DatosCampoProperty dato : datosCampoProperties) {
            double profundidadInicial = dato.getProfundidadInicial();
            double profundidadFinal = dato.getProfundidadFinal();
            //celda inicial donde inicia el valor del recobro
            //celda final donde termina el valor recobro
            double getLastCellRecrobro = profundidadFinal * 2;
            double dif = profundidadFinal - profundidadInicial;
            int multi = dato.getGolpe1() * 2;
            int suma = dato.getGolpe2() + dato.getGolpe3();
            if (dato.getGolpe1() > 0) {
                yValues.add(profundidadInicial);
                xValues.add(multi);
                profundidadInicial += paso;
                yValues.add(profundidadInicial);
                xValues.add(multi);
            } else {
                profundidadInicial += paso;
                if (!xValues.isEmpty() && !yValues.isEmpty()) {
                    Map<List<Integer>, List<Double>> xyDatos = addSerieMap(xValues, yValues);
                    valores.put(aux, xyDatos);
                    aux += 1;
                }
            }
            if (dato.getGolpe2() > 0) {
                yValues.add(profundidadInicial);
                xValues.add(suma);
                profundidadInicial += paso;
                yValues.add(profundidadInicial);
                xValues.add(suma);
            } else {
                profundidadInicial += paso;
                if (!xValues.isEmpty() && !yValues.isEmpty()) {
                    Map<List<Integer>, List<Double>> xyDatos = addSerieMap(xValues, yValues);
                    valores.put(aux, xyDatos);
                    aux += 1;
                }
            }
            if (dato.getGolpe3() > 0) {
                profundidadInicial += paso;
                yValues.add(profundidadInicial);
                xValues.add(suma);
            } else {
                if (!xValues.isEmpty() && !yValues.isEmpty()) {
                    Map<List<Integer>, List<Double>> xyDatos = addSerieMap(xValues, yValues);
                    valores.put(aux, xyDatos);
                    aux += 1;

                }
            }
        }
        if (valores.isEmpty()) {
            Map<List<Integer>, List<Double>> xyDatos = addSerieMap(xValues, yValues);
            xyDatos.put(xValues, yValues);
            valores.put(aux, xyDatos);
        }
        return valores;
    }

    private Map<List<Integer>, List<Double>> addSerieMap(List<Integer> x, List<Double> y) {
        Map<List<Integer>, List<Double>> xyDatos = new HashMap<>();
        List<Integer> auxX = new ArrayList<>(x);
        List<Double> auxY = new ArrayList<>(y);
        double firstY = y.get(0);
        //el valor de (x1,y1)
        auxX.add(0, 0);
        auxY.add(0, firstY);
        //el valor de (xn,yn), es decir se a;aden en el ultimo puesto de la lista
        auxX.add(0);
        auxY.add(y.get(y.size() - 1));
        auxX.add(0);
        auxY.add(firstY);
        //despues de reordenar la lista con sus valores respectivos
        //los añadimos para crear la serie correspondiente
        xyDatos.put(auxX, auxY);
        xValues.clear();
        yValues.clear();
        return xyDatos;
    }

}
