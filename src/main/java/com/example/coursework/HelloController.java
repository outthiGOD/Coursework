package com.example.coursework;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HelloController {

    @FXML
    private Button loginbutton;

    @FXML
    private TextField logintext;

    @FXML
    private PasswordField passwordtext;

    @FXML
    private Button skipBtn;

    // Штучки для базы данных
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private double x = 0;
    private double y = 0;
    public void Login(){
        String sql = "SELECT name_user, surname_user, id_role FROM users WHERE login = ? and password = ?";
        connect = database.connectDb();

        try{
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, logintext.getText());
            prepare.setString(2, passwordtext.getText());

            result = prepare.executeQuery();

            if(logintext.getText().isEmpty() || passwordtext.getText().isEmpty()){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText("Fields are empty");
                alert.setContentText("Please fill in both fields (login and password)");
                alert.showAndWait();
            } else {
                if(result.next()) {
                    getUsersData.username = result.getString("name_user");
                    getUsersData.usersurname = result.getString("surname_user");
                    int role = result.getInt("id_role");

                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("Successful login");
                    alert.setContentText("You have successfully logged in");
                    alert.showAndWait();

                    loginbutton.getScene().getWindow().hide();
                    Parent root;
                    if (role == 1) {
                        root = FXMLLoader.load(getClass().getResource("dashboard.fxml"));
                    } else {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboardForUser.fxml"));
                        root = loader.load();
                        dashboardForUserController controller = loader.getController();
                        controller.displayUsername();
                        controller.hideButtons2();
                    }

                    Stage stage = new Stage();
                    Scene scene = new Scene(root);

                    scene.setOnMousePressed((MouseEvent event) ->{
                        x = event.getSceneX();
                        y = event.getSceneY();
                    });

                    scene.setOnMouseDragged((MouseEvent event) ->{
                        stage.setX(event.getScreenX() - x);
                        stage.setY(event.getScreenY() - y);
                        stage.setOpacity(.8);
                    });

                    scene.setOnMouseReleased((MouseEvent event) ->{
                        stage.setOpacity(1);
                    });

                    stage.initStyle(StageStyle.TRANSPARENT);
                    stage.setScene(scene);
                    stage.show();
                } else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error Dialog");
                    alert.setHeaderText("Incorrect login or password");
                    alert.setContentText("The username or password is not correct");
                    alert.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openAddUserWindow() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("addUser.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            root.setOnMousePressed((MouseEvent event) ->{
                x = event.getSceneX();
                y = event.getSceneY();
            });

            scene.setOnMouseDragged((MouseEvent event) ->{
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
                stage.setOpacity(.8);
            });

            scene.setOnMouseReleased((MouseEvent event) ->{
                stage.setOpacity(1);
            });

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openDashboardForUser() {
        try {
            Stage currentStage = (Stage) loginbutton.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboardForUser.fxml"));
            Parent root = loader.load();
            dashboardForUserController controller = loader.getController();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            scene.setOnMousePressed((MouseEvent event) ->{
                x = event.getSceneX();
                y = event.getSceneY();
            });

            scene.setOnMouseDragged((MouseEvent event) ->{
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
                stage.setOpacity(.8);
            });

            scene.setOnMouseReleased((MouseEvent event) ->{
                stage.setOpacity(1);
            });

            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);

            controller.hideButtons();

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void close(){
        System.exit(0);
    }
}