package com.example.coursework;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class addCatalogController {
    @FXML
    private Button closeBtn;

    @FXML
    private TextField nameCatalogField;

    // Штучки для базы данных
    private Connection connect;
    private PreparedStatement prepare;

    public addCatalogController() throws IOException {
    }

    public void addCatalog(){
        String sql = "INSERT INTO catalogs (name_catalog) VALUES (?)";

        connect = database.connectDb();

        try {

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, nameCatalogField.getText());
            prepare.executeUpdate();
            close();

        }catch (Exception e) {e.printStackTrace();}
    }

    public void close(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}