package com.example.coursework;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class addPickUpPointController {
    @FXML
    private Button closeBtn;

    @FXML
    private TextField namePickUpPointField;

    // Штучки для базы данных
    private Connection connect;
    private PreparedStatement prepare;

    public addPickUpPointController() throws IOException {
    }

    public void addPickUpPoint(){
        String sql = "INSERT INTO pick_up_points (name_pick_up_point) VALUES (?)";

        connect = database.connectDb();

        try {

            prepare = connect.prepareStatement(sql);
            prepare.setString(1, namePickUpPointField.getText());
            prepare.executeUpdate();
            close();

        }catch (Exception e) {e.printStackTrace();}
    }

    public void close(){
        Stage stage = (Stage) closeBtn.getScene().getWindow();
        stage.close();
    }
}