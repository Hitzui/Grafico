/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dysconcsa.app.grafico.model;

import javafx.beans.property.*;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Objects;

/**
 * @author jmiguelsevilla@hotmail.com
 */
public class SuelosProperty {
    private IntegerProperty ID;
    private StringProperty nombre;
    private StringProperty simbolo;
    private ObjectProperty<IndexedColors> color;
    private ObjectProperty<FillPatternType> pattern;

    /**
     * tipo de suelos en la base de datos, se guarda la imagne como texto
     *
     * @param ID     Llave primaria
     * @param name   Nombre del tipo de suelo
     * @param symbol Simbologia a usa
     * @return SuelosProperty
     */
    public SuelosProperty(Integer ID, String name, String symbol, IndexedColors colors, FillPatternType patternType) {
        this.ID = new SimpleIntegerProperty(ID);
        this.nombre = new SimpleStringProperty(name);
        this.simbolo = new SimpleStringProperty(symbol);
        this.color = new SimpleObjectProperty<>(colors);
        this.pattern = new SimpleObjectProperty<>(patternType);
    }

    public int getID() {
        return ID.get();
    }

    public IntegerProperty IDProperty() {
        return ID;
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    public String getNombre() {
        return nombre.get();
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getSimbolo() {
        return simbolo.get();
    }

    public StringProperty simboloProperty() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo.set(simbolo);
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
        return simbolo.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuelosProperty that = (SuelosProperty) o;
        return Objects.equals(ID, that.ID) &&
                Objects.equals(nombre, that.nombre) &&
                Objects.equals(simbolo, that.simbolo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, nombre, simbolo);
    }
}
