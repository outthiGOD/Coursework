package com.example.coursework;

import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
import java.util.*;

public class dashboardForUserController implements Initializable {

    @FXML
    private TextField cart_Price;

    @FXML
    private TableColumn<getProductsData, String> cart_col_catalog;

    @FXML
    private TableColumn<getProductsData, Integer> cart_col_id;

    @FXML
    private TableColumn<getProductsData, String> cart_col_manufacturer;

    @FXML
    private TableColumn<getProductsData, String> cart_col_name;

    @FXML
    private TableColumn<getProductsData, Integer> cart_col_price;

    @FXML
    private TableColumn<getProductsData, String> cart_col_provider;

    @FXML
    private TableColumn<getProductsData, Integer> cart_col_quantity;

    @FXML
    private AnchorPane cart_form;

    @FXML
    private ImageView cart_image;

    @FXML
    private TextField cart_search;

    @FXML
    private Button products_addBtn;

    @FXML
    private TableView<getProductsData> cart_tableview;

    @FXML
    private Button signIn;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button products_btn;

    @FXML
    private TableColumn<getProductsData, String> products_col_catalog;

    @FXML
    private TableColumn<getProductsData, Integer> products_col_id;

    @FXML
    private TableColumn<getProductsData, String> products_col_manufacturer;

    @FXML
    private TableColumn<getProductsData, String> products_col_name;

    @FXML
    private TableColumn<getProductsData, Integer> products_col_price;

    @FXML
    private TableColumn<getProductsData, String> products_col_provider;

    @FXML
    private TableColumn<getProductsData, String> products_col_quantity;

    @FXML
    private AnchorPane products_form;

    @FXML
    private ImageView products_image;

    @FXML
    private TextField products_price;

    @FXML
    private TextField products_search;

    @FXML
    private TableView<getProductsData> products_tableview;

    @FXML
    private ComboBox<String> cart_pick_up_point;

    @FXML
    private Label username;

    @FXML
    private Button сart_btn;

    // Штучки для базы данных
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Image image;
    private double x = 0;
    private double y = 0;

    public void addOrderFromCart() {
        String orderUser = username.getText();
        String orderPickUpPoint = cart_pick_up_point.getValue();

        if (orderPickUpPoint == null || orderPickUpPoint.isEmpty()) {
            showAlert("Error Dialog", "Please specify the pick-up point.");
            return;
        }

        String sqlUser = "SELECT id_user FROM users WHERE name_user = ?";
        int userId = getIdFromDatabase(sqlUser, orderUser);

        String sqlPickUpPoint = "SELECT id_pick_up_point FROM pick_up_points WHERE name_pick_up_point = ?";
        int pickUpPointId = getIdFromDatabase(sqlPickUpPoint, orderPickUpPoint);

        String sqlOrder = "SELECT MAX(id_order) FROM orders";
        int orderId = getMaxIdFromDatabase(sqlOrder) + 1;

        String sqlOrderDetails = "INSERT INTO orders (id_order, id_user, id_pick_up_point, id_status) VALUES (?, ?, ?, ?)";
        insertIntoDatabase(sqlOrderDetails, orderId, userId, pickUpPointId, 1);

        for (getProductsData product : cartItems) {
            String sqlProduct = "SELECT id_product FROM products WHERE name_product = ?";
            int productId = getIdFromDatabase(sqlProduct, product.getProductsName());

            String sqlOrderDetailsProduct = "INSERT INTO order_details (id_order, order_details_date, order_details_id_product, order_details_quantity_product) VALUES (?, current_date, ?, ?)";
            insertIntoDatabase(sqlOrderDetailsProduct, orderId, productId, product.getProductsQuantity());

            String sqlUpdateProduct = "UPDATE products SET quantity_product = quantity_product - ? WHERE id_product = ?";
            insertIntoDatabase(sqlUpdateProduct, product.getProductsQuantity(), productId);
        }

        cartItems.clear();
        cart_tableview.setItems(cartItems);
        cart_pick_up_point.getSelectionModel().clearSelection();

        showAlert("Success Dialog", "The order has been added successfully.");
    }

    public void PickUpPointsData() {
        ObservableList<String> listData = FXCollections.observableArrayList();
        String sql = "SELECT name_pick_up_point FROM pick_up_points";

        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()){
                listData.add(result.getString("name_pick_up_point"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        cart_pick_up_point.setItems(listData);
    }

    public void removeFromCart() {

        ObservableList<getProductsData> selectedProducts = cart_tableview.getSelectionModel().getSelectedItems();
        cartItems.removeAll(selectedProducts);
        cart_tableview.setItems(cartItems);

    }

    private ObservableList<getProductsData> cartItems = FXCollections.observableArrayList();

    public void addToCart() {
        ObservableList<getProductsData> selectedProducts = products_tableview.getSelectionModel().getSelectedItems();

        for (getProductsData product : selectedProducts) {
            try {
                if (cartItems.stream().anyMatch(item -> item.getProductsId().equals(product.getProductsId()))) {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Update Quantity");
                    dialog.setHeaderText("Enter new quantity for product: " + product.getProductsName());
                    Optional<String> result = dialog.showAndWait();

                    result.ifPresent(newQuantity -> {
                        int newQuantityInt = Integer.parseInt(newQuantity);
                        if (newQuantityInt <= 0) {
                            showAlert("Error", "Quantity must be greater than 0");
                            return;
                        }

                        if (newQuantityInt > product.getProductsQuantity()) {
                            showAlert("Error", "Quantity must not exceed available quantity in the database");
                            return;
                        }

                        getProductsData existingProduct = cartItems.stream()
                                .filter(item -> item.getProductsId().equals(product.getProductsId()))
                                .findFirst()
                                .orElse(null);

                        if (existingProduct != null) {
                            existingProduct.setProductsQuantity(newQuantityInt);
                            cart_tableview.refresh();
                        }
                    });
                } else {
                    TextInputDialog dialog = new TextInputDialog();
                    dialog.setTitle("Add to Cart");
                    dialog.setHeaderText("Enter quantity for product: " + product.getProductsName());
                    Optional<String> result = dialog.showAndWait();

                    result.ifPresent(quantity -> {
                        int quantityInt = Integer.parseInt(quantity);
                        if (quantityInt <= 0) {
                            showAlert("Error", "Quantity must be greater than 0");
                            return;
                        }

                        if (quantityInt > product.getProductsQuantity()) {
                            showAlert("Error", "Quantity must not exceed available quantity in the database");
                            return;
                        }

                        product.setProductsQuantity(quantityInt);
                        cartItems.add(product);
                        cart_tableview.setItems(cartItems);
                    });
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "You must enter a whole number");
            }
        }
    }

    public void CartShowListData() {

        cart_col_catalog.setCellValueFactory(new PropertyValueFactory<>("nameCatalog"));
        cart_col_id.setCellValueFactory(new PropertyValueFactory<>("productsId"));
        cart_col_manufacturer.setCellValueFactory(new PropertyValueFactory<>("nameManufacturer"));
        cart_col_name.setCellValueFactory(new PropertyValueFactory<>("productsName"));
        cart_col_price.setCellValueFactory(new PropertyValueFactory<>("productsPrice"));
        cart_col_provider.setCellValueFactory(new PropertyValueFactory<>("nameProvider"));
        cart_col_quantity.setCellValueFactory(new PropertyValueFactory<>("productsQuantity"));

    }

    public void updateProductsPrice() {
        products_tableview.getSelectionModel().getSelectedItems().addListener((ListChangeListener.Change<? extends getProductsData> change) -> {
            double totalPrice = 0;
            for (getProductsData product : change.getList()) {
                totalPrice += product.getProductsPrice();
            }
            products_price.setText(String.valueOf(totalPrice) + " руб.");
        });
    }

    public ObservableList<getProductsData> ProductsListData(){
        ObservableList<getProductsData> listData = FXCollections.observableArrayList();
        String sql = "select id_product, name_product, quantity_product, name_manufacturer, name_provider, name_catalog, price, image from products p, manufacturers m, providers p2, catalogs c where p.id_manufacturer = m.id_manufacturer and p.id_provider = p2.id_provider and p.id_catalog = c.id_catalog";

        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            getProductsData getProductsD;

            while (result.next()){
                getProductsD = new getProductsData(result.getInt("id_product"),
                        result.getString("name_product"),
                        result.getInt("quantity_product"),
                        result.getString("name_manufacturer"),
                        result.getString("name_provider"),
                        result.getString("name_catalog"),
                        result.getInt("price"),
                        result.getString("image")
                );

                listData.add(getProductsD);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        return listData;
    }

    private ObservableList<getProductsData> ProductsList;

    public void ProductsShowListData() {
        ProductsList = ProductsListData();

        products_col_id.setCellValueFactory(new PropertyValueFactory<>("productsId"));
        products_col_name.setCellValueFactory(new PropertyValueFactory<>("productsName"));
        products_col_quantity.setCellValueFactory(new PropertyValueFactory<>("productsQuantity"));
        products_col_manufacturer.setCellValueFactory(new PropertyValueFactory<>("nameManufacturer"));
        products_col_provider.setCellValueFactory(new PropertyValueFactory<>("nameProvider"));
        products_col_catalog.setCellValueFactory(new PropertyValueFactory<>("nameCatalog"));
        products_col_price.setCellValueFactory(new PropertyValueFactory<>("productsPrice"));

        products_tableview.setItems(ProductsList);
    }

    public void ProductSelect(){
        getProductsData getProductsD = products_tableview.getSelectionModel().getSelectedItem();

        if(getProductsD == null) {
            return;
        }

        String os = System.getProperty("os.name").toLowerCase();
        String uri;

        if (os.contains("win")) {
            uri = "file:" + getProductsD.getProductsImage();
        } else {
            uri = "file//" + getProductsD.getProductsImage();
        }

        image = new Image(uri,220, 185, false, true);

        products_image.setImage(image);
    }

    public void CartSelect(){
        getProductsData getProductsD = cart_tableview.getSelectionModel().getSelectedItem();

        if(getProductsD == null) {
            return;
        }

        String os = System.getProperty("os.name").toLowerCase();
        String uri;

        if (os.contains("win")) {
            uri = "file:" + getProductsD.getProductsImage();
        } else {
            uri = "file//" + getProductsD.getProductsImage();
        }

        image = new Image(uri,220, 185, false, true);

        cart_image.setImage(image);
    }

    private int getMaxIdFromDatabase(String sql) {
        int maxId = 0;
        connect = database.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            if (result.next()) {
                maxId = result.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maxId;
    }

    private int getIdFromDatabase(String sql, String name) {
        int id = 0;
        connect = database.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, name);
            result = prepare.executeQuery();
            if (result.next()) {
                id = result.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    private void insertIntoDatabase(String sql, Object... params) {
        connect = database.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                prepare.setObject(i + 1, params[i]);
            }
            prepare.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calculateCartTotalPrice() {
        double totalPrice = 0;
        for (getProductsData product : cartItems) {
            totalPrice += product.getProductsPrice() * product.getProductsQuantity();
        }
        cart_Price.setText(String.valueOf(totalPrice) + " руб.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public <T> void SearchListener(TableView<T> table, TextField searchField, ObservableList<T> data) {
        FilteredList<T> filteredData = new FilteredList<>(data, p -> true);
        SortedList<T> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedData);

        PauseTransition pause = new PauseTransition(Duration.millis(650)); // Задержка в 650 миллисекунд
        pause.setOnFinished(event -> {
            String value = searchField.getText().toLowerCase();
            if (value.equals("!")) {
                filteredData.setPredicate(null);
            } else {
                String[] values = value.split(";");
                filteredData.setPredicate(item -> {
                    boolean exclude = false;
                    for (String v : values) {
                        String searchValue = v.trim();
                        if (searchValue.startsWith("!")) {
                            searchValue = searchValue.substring(1);
                            exclude = true;
                        }
                        for (TableColumn<T, ?> column : table.getColumns()) {
                            String cellValue = column.getCellData(item).toString().toLowerCase();
                            if (cellValue.contains(searchValue)) {
                                return !exclude;
                            }
                        }
                    }
                    return exclude;
                });
            }
        });

        searchField.textProperty().addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
            pause.playFromStart();
        });
    }

    public void switchForm(ActionEvent event){
        Map<Node, String> buttonStyles = new HashMap<>();
        buttonStyles.put(products_btn, "-fx-background-color: #fce6dd; -fx-background-radius: 10;");
        buttonStyles.put(сart_btn, "-fx-background-color: #fce6dd; -fx-background-radius: 10;");

        Node source = (Node) event.getSource();
        products_form.setVisible(source == products_btn);
        cart_form.setVisible(source == сart_btn);

        for (Node button : buttonStyles.keySet()) {
            String style = buttonStyles.get(button);
            if (button == source) {
                style += " -fx-background-radius: 15; -fx-border-color: #C41E3A; -fx-border-radius: 15; -fx-border-width: 2";
            }
            button.setStyle(style);
        }

        if (source == products_btn) {
            ProductsShowListData();
            updateProductsPrice();
            SearchListener(products_tableview, products_search, products_tableview.getItems());
        } else if (source == сart_btn) {
            PickUpPointsData();
            SearchListener(cart_tableview, cart_search, cart_tableview.getItems());
            CartShowListData();
        }
    }

    public void displayUsername() {
        username.setText(getUsersData.username);
    }
    public void logout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to logout?");
        Optional<ButtonType> option = alert.showAndWait();

        try {
            if (option.get().equals(ButtonType.OK)) {

                logout.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("Authorization.fxml"));
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void SignIn() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Message");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to log in?");
        Optional<ButtonType> option = alert.showAndWait();

        try {
            if (option.get().equals(ButtonType.OK)) {

                logout.getScene().getWindow().hide();
                Parent root = FXMLLoader.load(getClass().getResource("Authorization.fxml"));
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close(){
        System.exit(0);
    }

    public void hideButtons() {
        logout.setVisible(false);
        сart_btn.setVisible(false);
        products_addBtn.setVisible(false);
    }

    public void hideButtons2() {
        signIn.setVisible(false);
    }

    public void minimize(){
        Stage stage = (Stage)main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CartShowListData();

        products_tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        cart_tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        cartItems.addListener((ListChangeListener.Change<? extends getProductsData> change) -> {
            calculateCartTotalPrice();
        });
    }
}