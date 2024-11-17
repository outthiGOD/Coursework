package com.example.coursework;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class addManufacturerController {
    @FXML
    private Button closeBtn;

    @FXML
    private TextField nameManufacturerField;

    // Штучки для базы данных
    private Connection connect;
    private PreparedStatement prepare;

    public addManufacturerController() throws IOException {
    }

    public void addManufacturer(){
        String sql = "INSERT INTO manufacturers (name_manufacturer) VALUES (?)";

        connect = database.connectDb();

        try {

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, nameManufacturerField.getText());
            prepare.executeUpdate();
            close();

        }catch (Exception e) {e.printStackTrace();}
    }

    public void close(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}