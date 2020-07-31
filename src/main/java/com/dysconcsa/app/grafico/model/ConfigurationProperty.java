package com.dysconcsa.app.grafico.model;

import javafx.beans.property.*;

public class ConfigurationProperty {

    private IntegerProperty id;
    private StringProperty imagen;
    private StringProperty nombreEmpresa;
    private BooleanProperty selected;

    public ConfigurationProperty(Integer id, String nombreEmpresa, String imagen, Boolean selected) {
        this.id = new SimpleIntegerProperty(id);
        this.imagen = new SimpleStringProperty(imagen);
        this.nombreEmpresa = new SimpleStringProperty(nombreEmpresa);
        this.selected = new SimpleBooleanProperty(selected);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getImagen() {
        return imagen.get();
    }

    public StringProperty imagenProperty() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen.set(imagen);
    }

    public String getNombreEmpresa() {
        return nombreEmpresa.get();
    }

    public StringProperty nombreEmpresaProperty() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa.set(nombreEmpresa);
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    @Override
    public String toString() {
        return this.nombreEmpresa.get();
    }
}
