package com.dysconcsa.app.grafico.util;

import com.dysconcsa.app.grafico.model.AdemeProperty;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;

public class AdemeConverter extends StringConverter<AdemeProperty> {
    private final ListCell<AdemeProperty> cell;

    public AdemeConverter(ListCell<AdemeProperty> cell) {
        this.cell = cell;
    }

    @Override
    public String toString(AdemeProperty object) {
        return "Se ademo hasta " + object.getProfundidad();
    }

    @Override
    public AdemeProperty fromString(String string) {
        AdemeProperty ademeProperty = cell.getItem();
        ademeProperty.setProfundidad(Double.parseDouble(string));
        ademeProperty.setDescripcion("Se ademo hasta " + string);
        return ademeProperty;
    }
}
