package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class Variables {

    private static Variables instance;
    public File file;
    public EmpresaProperty empresaProperty;
    public DatosSondeo datosSondeo;
    public SuelosProperty suelosProperty;
    public ConfigurationProperty selectedConfiguration;
    public ObservableList<ClasificacionSucsProperty> clasificacionSucsProperties = FXCollections.observableArrayList();
    public ObservableList<AdemeProperty> ademeProperties = FXCollections.observableArrayList();
    public ObservableList<DatosCampoProperty> datosCampoProperties = FXCollections.observableArrayList();
    public ObservableList<TrepanoProperty> trepanoProperties = FXCollections.observableArrayList();
    public ObservableList<HumedadProperty> humedadProperties = FXCollections.observableArrayList();
    public ObservableList<EmpresaProperty> empresaProperties = FXCollections.observableArrayList();
    public ObservableList<SuelosProperty> suelosProperties = FXCollections.observableArrayList();

    private Variables() {
    }

    public static Variables getInstance() {
        if (instance == null) {
            instance = new Variables();
        }
        return instance;
    }

    public void destroy() {
        this.file = null;
        this.datosCampoProperties.clear();
        this.clasificacionSucsProperties.clear();
        this.trepanoProperties.clear();
        this.humedadProperties.clear();
        this.ademeProperties.clear();
        this.datosSondeo = null;
    }
}
