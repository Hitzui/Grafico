package com.dysconcsa.app.grafico.model;

import javafx.beans.property.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ClasificacionSucsProperty {
    private DoubleProperty profundidad;
    private IntegerProperty limiteLiquido;
    private IntegerProperty indicePlasticidad;
    private IntegerProperty tipoSuelo;
    private StringProperty descripcion;
    private ObjectProperty<IndexedColors> color;
    private ObjectProperty<FillPatternType> pattern;

    public ClasificacionSucsProperty(Double profundidad, Integer limiteLiquido,
                                     Integer indicePlasticidad, Integer tipoSuelo,
                                     String descripcion, IndexedColors color,FillPatternType patternType) {
        this.profundidad = new SimpleDoubleProperty(profundidad);
        this.limiteLiquido = new SimpleIntegerProperty(limiteLiquido);
        this.indicePlasticidad = new SimpleIntegerProperty(indicePlasticidad);
        this.tipoSuelo = new SimpleIntegerProperty(tipoSuelo);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.color = new SimpleObjectProperty<>(color);
        this.pattern = new SimpleObjectProperty<>(patternType);
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

    public double getProfundidad() {
        return profundidad.get();
    }

    public DoubleProperty profundidadProperty() {
        return profundidad;
    }

    public void setProfundidad(double profundidad) {
        this.profundidad.set(profundidad);
    }

    public int getLimiteLiquido() {
        return limiteLiquido.get();
    }

    public IntegerProperty limiteLiquidoProperty() {
        return limiteLiquido;
    }

    public void setLimiteLiquido(int limiteLiquido) {
        this.limiteLiquido.set(limiteLiquido);
    }

    public int getIndicePlasticidad() {
        return indicePlasticidad.get();
    }

    public IntegerProperty indicePlasticidadProperty() {
        return indicePlasticidad;
    }

    public void setIndicePlasticidad(int indicePlasticidad) {
        this.indicePlasticidad.set(indicePlasticidad);
    }

    public int getTipoSuelo() {
        return tipoSuelo.get();
    }

    public IntegerProperty tipoSueloProperty() {
        return tipoSuelo;
    }

    public void setTipoSuelo(int tipoSuelo) {
        this.tipoSuelo.set(tipoSuelo);
    }

    public IndexedColors getColor() {
        return color.get();
    }

    public ObjectProperty<IndexedColors> colorProperty() {
        return color;
    }

    public void setColor(IndexedColors color) {
        this.color.set(color);
    }

    public FillPatternType getPattern() {
        return pattern.get();
    }

    public ObjectProperty<FillPatternType> patternProperty() {
        return pattern;
    }

    public void setPattern(FillPatternType pattern) {
        this.pattern.set(pattern);
    }

    @Override
    public String toString() {
        return "ClasificacionSucsProperty{" +
                "profundidad=" + profundidad +
                ", limiteLiquido=" + limiteLiquido +
                ", indicePlasticidad=" + indicePlasticidad +
                ", descripcion=" + descripcion +
                '}';
    }
}
