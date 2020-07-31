package com.dysconcsa.app.grafico.util;

import javafx.scene.paint.Color;

public class Item {

    public Item(String value, Color color) {
        this.value = value;
        this.color = color;
    }

    private final String value;
    private final Color color;

    public String getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return value;
    }
}