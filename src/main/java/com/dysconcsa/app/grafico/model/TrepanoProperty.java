package com.dysconcsa.app.grafico.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TrepanoProperty {

    private DoubleProperty profundidad;
    private StringProperty trepano;

    public TrepanoProperty() {
        this.profundidad = new SimpleDoubleProperty(0.0);
        this.trepano = new SimpleStringProperty("");
    }

    public TrepanoProperty(Double profundidad, String trepano) {
        this.profundidad = new SimpleDoubleProperty(profundidad);
        this.trepano = new SimpleStringProperty(trepano);
    }

    public double getProfundidad() {
        return profundidad.get();
    }

    public DoubleProperty profundidadProperty() {
        return profundidad;
    }

    public void setProfundidad(double profundidad) {
        this.profundidad.set(profundidad);
    }

    public String getTrepano() {
        return trepano.get();
    }

    public StringProperty trepanoProperty() {
        return trepano;
    }

    public void setTrepano(String trepano) {
        this.trepano.set(trepano);
    }
}
