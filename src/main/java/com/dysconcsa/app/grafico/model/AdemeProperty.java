package com.dysconcsa.app.grafico.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class AdemeProperty {
    private DoubleProperty profundidad;
    private StringProperty descripcion;

    public AdemeProperty() {
        this.profundidad = new SimpleDoubleProperty(0.0);
        this.descripcion = new SimpleStringProperty("Se ademo hasta 0.0");
    }

    /**
     * <p>Esto se usa para indicar hasta donde se ademo, ya que se usa como valor principal la profundida y no los golpes</p>
     *
     * @param profundidad       hasta donde se ademo
     * @param descripcion descripcion que se usa en el archivo de excel
     */
    public AdemeProperty(Double profundidad, String descripcion) {
        this.profundidad = new SimpleDoubleProperty(profundidad);
        this.descripcion = new SimpleStringProperty(descripcion);
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

    public String getDescripcion() {
        return descripcion.get();
    }

    public StringProperty descripcionProperty() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion.set(descripcion);
    }

    @Override
    public String toString() {
        return "Se ademo hasta " + profundidad.get();
    }
}
