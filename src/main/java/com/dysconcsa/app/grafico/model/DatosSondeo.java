package com.dysconcsa.app.grafico.model;

import com.dysconcsa.app.grafico.util.DateUtil;

import static java.time.LocalDate.now;

public class DatosSondeo {
    private String sondeoNumero;
    private Double profundidadMinima;
    private Double profundidadMaxima;
    private String lugar;
    private String observaciones;
    private String operador;
    private String archivo;
    private String nivelFreatico;
    private Double elevacion;
    private String fecha;

    public DatosSondeo() {
        this.profundidadMaxima = 0.0;
        this.profundidadMinima = 0.0;
        this.elevacion = 0.0;
        fecha = DateUtil.format(now());
    }

    /*
     * @param sondeoNumero numero de sondeo
     * @param profundidadMinima profundidad minima del sondeo
     * @param profundidadMaxima profundidad maxima del sondeo
     * @param lugar lugar que se realizo el sondeo
     * @param observaciones observaciones que se encontraron
     * @param operador operador que realizo el sondeo
     * @param archivo numero de archivo
     * @param nivelFreatico nivel freatico
     * @param elevacion elevacion del sondeo
     */
    public DatosSondeo(String sondeoNumero, Double profundidadMinima, Double profundidadMaxima,
                       String lugar, String observaciones, String operador, String archivo,
                       String nivelFreatico, Double elevacion, String fecha) {
        this.sondeoNumero = sondeoNumero;
        this.profundidadMinima = profundidadMinima;
        this.profundidadMaxima = profundidadMaxima;
        this.lugar = lugar;
        this.observaciones = observaciones;
        this.operador = operador;
        this.archivo = archivo;
        this.nivelFreatico = nivelFreatico;
        this.elevacion = elevacion;
        this.fecha = fecha;
    }

    public String getSondeoNumero() {
        return sondeoNumero;
    }

    public void setSondeoNumero(String sondeoNumero) {
        this.sondeoNumero = sondeoNumero;
    }

    public Double getProfundidadMinima() {
        return profundidadMinima;
    }

    public void setProfundidadMinima(Double profundidadMinima) {
        this.profundidadMinima = profundidadMinima;
    }

    public Double getProfundidadMaxima() {
        return profundidadMaxima;
    }

    public void setProfundidadMaxima(Double profundidadMaxima) {
        this.profundidadMaxima = profundidadMaxima;
    }

    public Double getElevacion() {
        return elevacion;
    }

    public void setElevacion(Double elevacion) {
        this.elevacion = elevacion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getOperador() {
        return operador;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public String getArchivo() {
        return archivo;
    }

    public void setArchivo(String archivo) {
        this.archivo = archivo;
    }

    public String getNivelFreatico() {
        return nivelFreatico;
    }

    public void setNivelFreatico(String nivelFreatico) {
        this.nivelFreatico = nivelFreatico;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "ProfundidadSondeo{" +
                "sondeoNumero='" + sondeoNumero + '\'' +
                ", profundidadMinima=" + profundidadMinima +
                ", profundidadMaxima=" + profundidadMaxima +
                ", lugar='" + lugar + '\'' +
                ", observaciones='" + observaciones + '\'' +
                ", operador='" + operador + '\'' +
                ", archivo='" + archivo + '\'' +
                ", nivelFreatico='" + nivelFreatico + '\'' +
                ", elevacion=" + elevacion +
                '}';
    }
}
