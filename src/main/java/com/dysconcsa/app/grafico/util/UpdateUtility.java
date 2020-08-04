package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.model.DatosCampoProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Actualizacion del archivo que maneja los datos para generar el excel con su grafico</p>
 * <p>Esta actualizacion se da ya que el usuario desea iniciar el valor de su sondeo desde un punto que no es
 * totalmente compatible al pie y medio, ejemplo, podria iniciar en 4 y no 3.5</p>
 */
@Component
public class UpdateUtility {

    Logger logger = LoggerFactory.getLogger(getClass());
    List<Double> yValues = new ArrayList<>();
    List<Integer> xValues = new ArrayList<>();
    public UpdateUtility() {
    }

    public Map<Integer, Map<List<Integer>, List<Double>>> genearXY(@org.jetbrains.annotations.NotNull List<DatosCampoProperty> datosCampoProperties) {
        double paso = 0.5;
        int aux = 0;

        Map<Integer, Map<List<Integer>, List<Double>>> valores = new HashMap<>();
        for (DatosCampoProperty dato : datosCampoProperties) {
            //Optional<ClasificacionSucsProperty> first = clasificacionSucsProperties.stream().filter(c -> c.getProfundidad() == dato.getProfundidadFinal()).findFirst();
            double profundidadInicial = dato.getProfundidadInicial();
            double profundidadFinal = dato.getProfundidadFinal();
            //celda inicial donde inicia el valor del recobro
            double getFirstCellRecobro = profundidadInicial * 2;
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
