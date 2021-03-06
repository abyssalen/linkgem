package com.github.shaigem.linkgem.ui.search;

import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.controlsfx.control.textfield.CustomTextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The presenter to handle the searcher.
 *
 * @author Ronnie Tran
 */
public class SearchPresenter implements Initializable {

    @FXML
    CustomTextField searchTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchTextField.setRight(GlyphsDude.createIcon(MaterialDesignIcon.MAGNIFY, "1.8em"));
    }

    public void resetText() {
        searchTextField.setText("");
    }

    public String getText() {
        return searchTextField.getText();
    }

    public StringProperty textProperty() {
        return searchTextField.textProperty();
    }

    public CustomTextField getSearchTextField() {
        return searchTextField;
    }
}
