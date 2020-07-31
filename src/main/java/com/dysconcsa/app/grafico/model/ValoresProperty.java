package com.dysconcsa.app.grafico.model;

import javafx.beans.property.*;

import java.util.Objects;

public class ValoresProperty {
    private IntegerProperty id;
    private DoubleProperty espesor;
    private StringProperty trepano;
    private IntegerProperty tipoSuelo;
    private StringProperty limiteLiquido;
    private StringProperty indicePlasticidad;
    private DoubleProperty indiceHumedad;
    private IntegerProperty recobro;
    private IntegerProperty golpes;
    private IntegerProperty idEmpresa;
    private DoubleProperty elevacion;

    public ValoresProperty() {
    }

    /**
     * <p>Valores a establecer para generar el grafico</p>
     *
     * @param espesor
     * @param trepano
     * @param tipoSuelo
     * @param limiteLiquido
     * @param indicePlasticidad
     * @param indiceHumedad
     * @param recobro
     * @param golpes
     * @param empresa
     */
    public ValoresProperty(double espesor, String trepano,
                           int tipoSuelo, String limiteLiquido, String indicePlasticidad,
                           double indiceHumedad, int recobro, int golpes, int empresa) {
        this.espesor = new SimpleDoubleProperty(espesor);
        this.trepano = new SimpleStringProperty(trepano);
        this.tipoSuelo = new SimpleIntegerProperty(tipoSuelo);
        this.limiteLiquido = new SimpleStringProperty(limiteLiquido);
        this.indicePlasticidad = new SimpleStringProperty(indicePlasticidad);
        this.indiceHumedad = new SimpleDoubleProperty(indiceHumedad);
        this.recobro = new SimpleIntegerProperty(recobro);
        this.golpes = new SimpleIntegerProperty(golpes);
        this.idEmpresa = new SimpleIntegerProperty(empresa);
        this.elevacion = new SimpleDoubleProperty(0d);
    }

    public ValoresProperty(double espesor, String trepano, int tipoSuelo, String limiteLiquido, String indicePlasticidad, double indiceHumedad, int recobro, int golpes, int empresa, double elevacion) {
        this.espesor = new SimpleDoubleProperty(espesor);
        this.trepano = new SimpleStringProperty(trepano);
        this.tipoSuelo = new SimpleIntegerProperty(tipoSuelo);
        this.limiteLiquido = new SimpleStringProperty(limiteLiquido);
        this.indicePlasticidad = new SimpleStringProperty(indicePlasticidad);
        this.indiceHumedad = new SimpleDoubleProperty(indiceHumedad);
        this.recobro = new SimpleIntegerProperty(recobro);
        this.golpes = new SimpleIntegerProperty(golpes);
        this.idEmpresa = new SimpleIntegerProperty(empresa);
        this.elevacion = new SimpleDoubleProperty(elevacion);
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

    public double getEspesor() {
        return espesor.get();
    }

    public DoubleProperty espesorProperty() {
        return espesor;
    }

    public void setEspesor(double espesor) {
        this.espesor.set(espesor);
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

    public int getTipoSuelo() {
        return tipoSuelo.get();
    }

    public IntegerProperty tipoSueloProperty() {
        return tipoSuelo;
    }

    public void setTipoSuelo(int tipoSuelo) {
        this.tipoSuelo.set(tipoSuelo);
    }

    public String getLimiteLiquido() {
        return limiteLiquido.get();
    }

    public StringProperty limiteLiquidoProperty() {
        return limiteLiquido;
    }

    public void setLimiteLiquido(String limiteLiquido) {
        this.limiteLiquido.set(limiteLiquido);
    }

    public String getIndicePlasticidad() {
        return indicePlasticidad.get();
    }

    public StringProperty indicePlasticidadProperty() {
        return indicePlasticidad;
    }

    public void setIndicePlasticidad(String indicePlasticidad) {
        this.indicePlasticidad.set(indicePlasticidad);
    }

    public double getIndiceHumedad() {
        return indiceHumedad.get();
    }

    public DoubleProperty indiceHumedadProperty() {
        return indiceHumedad;
    }

    public void setIndiceHumedad(double indiceHumedad) {
        this.indiceHumedad.set(indiceHumedad);
    }

    public int getRecobro() {
        return recobro.get();
    }

    public IntegerProperty recobroProperty() {
        return recobro;
    }

    public void setRecobro(int recobro) {
        this.recobro.set(recobro);
    }

    public int getGolpes() {
        return golpes.get();
    }

    public IntegerProperty golpesProperty() {
        return golpes;
    }

    public void setGolpes(int golpes) {
        this.golpes.set(golpes);
    }

    public int getIdEmpresa() {
        return idEmpresa.get();
    }

    public IntegerProperty idEmpresaProperty() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa.set(idEmpresa);
    }

    public double getElevacion() {
        return elevacion.get();
    }

    public DoubleProperty elevacionProperty() {
        return elevacion;
    }

    public void setElevacion(double elevacion) {
        this.elevacion.set(elevacion);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValoresProperty that = (ValoresProperty) o;
        return Objects.equals(recobro, that.recobro);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recobro);
    }

    @Override
    public String toString() {
        return "ValoresProperty{" +
                "id=" + id +
                ", espesor=" + espesor +
                ", trepano=" + trepano +
                ", tipoSuelo=" + tipoSuelo +
                ", limiteLiquido=" + limiteLiquido +
                ", indicePlasticidad=" + indicePlasticidad +
                ", indiceHumedad=" + indiceHumedad +
                ", recobro=" + recobro +
                ", golpes=" + golpes +
                ", idEmpresa=" + idEmpresa +
                ", elevacion=" + elevacion +
                '}';
    }
}
