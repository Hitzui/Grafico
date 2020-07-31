package com.dysconcsa.app.grafico.util;

import org.apache.poi.ss.usermodel.FillPatternType;

public class ItemPattern {

    private String name;
    private FillPatternType patternType;

    public ItemPattern(String name, FillPatternType patternType) {
        this.name = name;
        this.patternType = patternType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FillPatternType getPatternType() {
        return patternType;
    }

    public void setPatternType(FillPatternType patternType) {
        this.patternType = patternType;
    }
}
