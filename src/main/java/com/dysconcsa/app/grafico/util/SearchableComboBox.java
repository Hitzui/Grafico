package com.dysconcsa.app.grafico.util;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

/**
 * A simple extension of the {@link ComboBox} which shows a search field while the
 * popup is showing. The user can type any text into this search field to filter the
 * popup list.
 * <p>
 * The user can type multiple words. The popup list is filtered checking if the string
 * representation of an item contains all filter words (ignoring case).
 * <p>
 * After filtering the list, the user can select an entry by
 * <ul>
 *     <li>
 *         Pressing ENTER: the selected item is applied, the popup closes. If no item
 *         is selected, the first item is applied. To select another item the cursor
 *         keys can be used before pressing ENTER.
 *     </li>
 *     <li>
 *         Pressing TAB: Same as ENTER, but in addition the focus is transferred to the
 *         next control.
 *     </li>
 *     <li>
 *         Selecting an item using the mouse closes the popup.
 *     </li>
 * </ul>
 * <p>
 * When pressing ESCAPE while the popup is showing, the item that was selected when the
 * popup opened will be re-selected (even if the user did select another item using the
 * cursor keys.
 * <p>
 * Other than the {@link ComboBox}, the SearchableComboBox does open the Popup when using
 * the cursor keys (the {@link ComboBox} does only change the selected item without
 * opening the popup). This combined with the behavior of the ESCAPE key does allow to
 * go through the list of items with the cursor keys and than press ESCAPE to revert
 * the changes.
 *
 * <h3>Screenshot</h3>
 * To better describe what a SearchableComboBox is, please refer to the pictures below:
 * <center>
 *     <img src="searchable-combo-box-1.png" alt="Screenshot of SearchableComboBox"/>
 *     <img src="searchable-combo-box-2.png" alt="Screenshot of SearchableComboBox"/>
 *     <img src="searchable-combo-box-3.png" alt="Screenshot of SearchableComboBox"/>
 * </center>
 *
 * <h3>Example</h3>
 *
 * Let's look at an example to clarify this. The combo box offers the items
 * ["Berlin", "Bern", "Munich", "Paris", "New York", "Alberta"]. The user now types "ber" into
 * the search field. The combo box popup will only show ["Berlin", "Bern", "Alberta"].
 * <p>
 * To select the first item ("Berlin"), the user can now either just press ENTER or TAB,
 * or first select this item using the cursor DOWN key and press ENTER or TAB afterwards,
 * or select this item using the mouse.
 * <p>
 * To select the second or third item, the user either
 * must use the cursor keys first, use the mouse, or type more text until the searched item
 * is the first (or only) item in the list.
 * <p>
 * If you want to modify an existing {@link ComboBox} you can set the skin to
 * {@link SearchableComboBoxSkin} (e.g. using {@link ComboBox#setSkin(Skin)} or in CSS.
 *
 * @see SearchableComboBoxSkin
 */
public class SearchableComboBox<T> extends ComboBox<T> {

    public SearchableComboBox() {
        super();
    }

    SearchableComboBox(ObservableList<T> items) {
        super(items);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SearchableComboBoxSkin<>(this);
    }

}