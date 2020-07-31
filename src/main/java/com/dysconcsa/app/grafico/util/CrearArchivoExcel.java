package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.dao.DaoSuelos;
import com.dysconcsa.app.grafico.model.ClasificacionSucsProperty;
import com.dysconcsa.app.grafico.model.DatosCampoProperty;
import com.dysconcsa.app.grafico.model.SuelosProperty;
import javafx.collections.ObservableList;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * <p><b>Actualizacion</b> del codigo para crear el <i>Archivo de Excel</i></p>
 * <p>Antes se usaba 1.5' para crear el grafico,se seguira usando, pero ahora
 * el usuario puede editar eso, y de esa forma graficar segun sus valores</p>
 */
@Component
public class CrearArchivoExcel {

    private XSSFWorkbook wb;
    @Autowired
    private Utility utility;
    int initRow =13;

    public void setWb(XSSFWorkbook wb) {
        this.wb = wb;
    }

    public CrearArchivoExcel() {
    }

    void generateSeriesX(ObservableList<DatosCampoProperty> datosCampoProperties) {
        try {

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
        for (int j = 1; j <= size; j++) {
            double profundidadInicial = datosCampoProperties.get(j).getProfundidadInicial();
            double profundidadFinal = datosCampoProperties.get(j).getProfundidadFinal();
            double difProfundidad = profundidadFinal-profundidadInicial;
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

}
