package com.dysconcsa.app.grafico.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class HumedadProperty {
    private final DoubleProperty profundidadInicial;
    private final DoubleProperty profundidadFinal;
    private final DoubleProperty humedad;

    public HumedadProperty() {
        this.profundidadInicial = new SimpleDoubleProperty(0.0);
        this.profundidadFinal = new SimpleDoubleProperty(1.5);
        this.humedad = new SimpleDoubleProperty(0.0);
    }

    public HumedadProperty(Double profundidadInicial, Double profundidadFinal, Double humedad) {
        this.profundidadInicial = new SimpleDoubleProperty(profundidadInicial);
        this.profundidadFinal = new SimpleDoubleProperty(profundidadFinal);
        this.humedad = new SimpleDoubleProperty(humedad);
    }

    public double getProfundidadInicial() {
        return profundidadInicial.get();
    }

    public DoubleProperty profundidadInicialProperty() {
        return profundidadInicial;
    }

    public void setProfundidadInicial(double profundidadInicial) {
        this.profundidadInicial.set(profundidadInicial);
    }

    public double getProfundidadFinal() {
        return profundidadFinal.get();
    }

    public DoubleProperty profundidadFinalProperty() {
        return profundidadFinal;
    }

    public void setProfundidadFinal(double profundidadFinal) {
        this.profundidadFinal.set(profundidadFinal);
    }

    public double getHumedad() {
        return humedad.get();
    }

    public DoubleProperty humedadProperty() {
        return humedad;
    }

    public void setHumedad(double humedad) {
        this.humedad.set(humedad);
    }
}
