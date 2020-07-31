package com.dysconcsa.app.grafico.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EmpresaProperty extends RecursiveTreeObject<EmpresaProperty> {
    private IntegerProperty id;
    StringProperty cliente;
    StringProperty proyecto;
    StringProperty fecha;

    public EmpresaProperty(String cliente, String proyecto, String fecha) {
        this.id = new SimpleIntegerProperty(0);
        this.cliente = new SimpleStringProperty(cliente);
        this.proyecto = new SimpleStringProperty(proyecto);
        this.fecha = new SimpleStringProperty(fecha);
    }

    public EmpresaProperty() {
        this.id = new SimpleIntegerProperty(0);
        this.cliente = new SimpleStringProperty("");
        this.proyecto = new SimpleStringProperty("");
        this.fecha = new SimpleStringProperty("");
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

    public String getCliente() {
        return cliente.get();
    }

    public StringProperty clienteProperty() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente.set(cliente);
    }

    public String getProyecto() {
        return proyecto.get();
    }

    public StringProperty proyectoProperty() {
        return proyecto;
    }

    public void setProyecto(String proyecto) {
        this.proyecto.set(proyecto);
    }

    public String getFecha() {
        return fecha.get();
    }

    public StringProperty fechaProperty() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha.set(fecha);
    }

    @Override
    public String toString() {
        return "Cliente: " + id.get() + " - " + cliente.get();
    }
}
