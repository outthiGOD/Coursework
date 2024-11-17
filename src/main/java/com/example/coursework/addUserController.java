package com.example.coursework;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class addUserController {
    @FXML
    private Button closeBtn;

    @FXML
    private TextField loginUserField;

    @FXML
    private TextField nameUserField;

    @FXML
    private TextField passwordUserField;

    @FXML
    private Button saveCatalogBtn;

    @FXML
    private TextField surnameUserField;

    // Штучки для базы данных
    private Connection connect;
    private PreparedStatement prepare;

    public addUserController() throws IOException {
    }

    public void addCatalog(){
        String sql = "INSERT INTO users (name_user, surname_user, login, password, id_role) VALUES (?, ?, ?, ?, 2)";

        connect = database.connectDb();

        try {

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, nameUserField.getText());
            prepare.setString(2, surnameUserField.getText());
            prepare.setString(3, loginUserField.getText());
            prepare.setString(4, passwordUserField.getText());
            prepare.executeUpdate();
            close();

        }catch (Exception e) {e.printStackTrace();}
    }

    public void close(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}