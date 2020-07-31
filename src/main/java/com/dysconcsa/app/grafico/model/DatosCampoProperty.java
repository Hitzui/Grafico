package com.dysconcsa.app.grafico.model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DatosCampoProperty extends RecursiveTreeObject<DatosCampoProperty> {

    private DoubleProperty profundidadInicial;
    private DoubleProperty profundidadFinal;
    private IntegerProperty recobro;
    private IntegerProperty golpe1;
    private IntegerProperty golpe2;
    private IntegerProperty golpe3;

    public DatosCampoProperty() {
        this.profundidadInicial = new SimpleDoubleProperty(0.0);
        this.profundidadFinal = new SimpleDoubleProperty(1.5);
        this.recobro = new SimpleIntegerProperty(0);
        this.golpe1 = new SimpleIntegerProperty(0);
        this.golpe2 = new SimpleIntegerProperty(0);
        this.golpe3 = new SimpleIntegerProperty(0);
    }

    public DatosCampoProperty(Double profundidadInicial, Double profundidadFinal,
                              Integer recobro, Integer golpe1,
                              Integer golpe2, Integer golpe3) {
        this.profundidadInicial = new SimpleDoubleProperty(profundidadInicial);
        this.profundidadFinal = new SimpleDoubleProperty(profundidadFinal);
        this.recobro = new SimpleIntegerProperty(recobro);
        this.golpe1 = new SimpleIntegerProperty(golpe1);
        this.golpe2 = new SimpleIntegerProperty(golpe2);
        this.golpe3 = new SimpleIntegerProperty(golpe3);
    }

    public double getProfundidadInicial() {
        return profundidadInicial.get();
    }

    public DoubleProperty profundidadInicialProperty() {
        return profundidadInicial;
    }

    public void setProfundidadInicial(Number profundidadInicial) {
        this.profundidadInicial.set((Double) profundidadInicial);
    }

    public double getProfundidadFinal() {
        return profundidadFinal.get();
    }

    public DoubleProperty profundidadFinalProperty() {
        return profundidadFinal;
    }

    public void setProfundidadFinal(Number profundidadFinal) {
        this.profundidadFinal.set((Double) profundidadFinal);
    }

    public int getRecobro() {
        return recobro.get();
    }

    public IntegerProperty recobroProperty() {
        return recobro;
    }

    public void setRecobro(Number recobro) {
        this.recobro.set((Integer) recobro);
    }

    public int getGolpe1() {
        return golpe1.get();
    }

    public IntegerProperty golpe1Property() {
        return golpe1;
    }

    public void setGolpe1(Number golpe1) {
        this.golpe1.set((Integer) golpe1);
    }

    public int getGolpe2() {
        return golpe2.get();
    }

    public IntegerProperty golpe2Property() {
        return golpe2;
    }

    public void setGolpe2(Number golpe2) {
        this.golpe2.set((Integer) golpe2);
    }

    public int getGolpe3() {
        return golpe3.get();
    }

    public IntegerProperty golpe3Property() {
        return golpe3;
    }

    public void setGolpe3(Number golpe3) {
        this.golpe3.set((Integer) golpe3);
    }

    @Override
    public String toString() {
        return "DatosCampoProperty{" +
                "profundidadInicial=" + profundidadInicial +
                ", profundidadFinal=" + profundidadFinal +
                ", recobro=" + recobro +
                '}';
    }
}
