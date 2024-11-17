package com.example.coursework;

import javafx.animation.PauseTransition;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class dashboardController implements Initializable {

    @FXML
    private ComboBox<String> add_product_catalog;

    @FXML
    private TableColumn<getProductsData, String> add_product_col_catalog;

    @FXML
    private TableColumn<getProductsData, Integer> add_product_col_id;

    @FXML
    private TableColumn<getProductsData, String> add_product_col_manufacturer;

    @FXML
    private TableColumn<getProductsData, String> add_product_col_name;

    @FXML
    private TableColumn<getProductsData, Integer> add_product_col_price;

    @FXML
    private TableColumn<getProductsData, String> add_product_col_provider;

    @FXML
    private TableColumn<getProductsData, Integer> add_product_col_quantity;

    @FXML
    private TableView<getProductsData> add_product_tableview;

    @FXML
    private AnchorPane add_product_form;

    @FXML
    private ImageView add_product_image;

    @FXML
    private ComboBox<String> add_product_manufacturer;

    @FXML
    private TextField add_product_id;

    @FXML
    private TextField add_product_name;

    @FXML
    private TextField add_product_price;

    @FXML
    private ComboBox<String> add_product_provider;

    @FXML
    private TextField add_product_quantity;

    @FXML
    private TextField add_product_search;

    @FXML
    private Button add_products_btn;

    @FXML
    private Button home_btn;

    @FXML
    private AreaChart<String, Number> home_chart;

    @FXML
    private AnchorPane home_form;

    @FXML
    private Label home_totalOrderCancelled;

    @FXML
    private Label home_totalOrderCompleted;

    @FXML
    private Label home_totalOrderProcessing;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane main_form;

    @FXML
    private Button order_btn;

    @FXML
    private TableColumn<getOrderData, Integer> orders_col_id;

    @FXML
    private TableColumn<getOrderData, String> orders_col_pick_up_point;

    @FXML
    private TableColumn<getOrderData, String> orders_col_product;

    @FXML
    private TableColumn<getOrderData, Integer> orders_col_quantity;

    @FXML
    private TableColumn<getOrderData, String> orders_col_status;

    @FXML
    private TableColumn<getOrderData, String> orders_col_user;

    @FXML
    private AnchorPane orders_form;

    @FXML
    private TextField orders_id;

    @FXML
    private ComboBox<String> orders_pick_up_point;

    @FXML
    private ComboBox<String> orders_product;

    @FXML
    private TextField orders_quantity;

    @FXML
    private TextField orders_search;

    @FXML
    private ComboBox<String> orders_status;

    @FXML
    private TableView<getOrderData> orders_tableview;

    @FXML
    private ComboBox<String> orders_user;

    @FXML
    private Label username;

    // Штучки для базы данных
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    private Image image;
    private double x = 0;
    private double y = 0;

    public void ordersReset(){
        orders_id.setText("");
        orders_user.getSelectionModel().clearSelection();
        orders_pick_up_point.getSelectionModel().clearSelection();
        orders_status.getSelectionModel().clearSelection();
        orders_product.getSelectionModel().clearSelection();
        orders_quantity.setText("");
    }

    public void loadChartForProcessing(MouseEvent event) {
        String sql = "select date(od.order_details_date) as order_date, count(o.id_status) as order_count from orders o, order_details od, order_status os where o.id_status = os.id_status and o.id_order = od.id_order and os.name_status = 'В обработке' group by order_date";
        loadChart(sql);
    }

    public void loadChartForCompleted(MouseEvent event) {
        String sql = "select date(od.order_details_date) as order_date, count(o.id_status) as order_count from orders o, order_details od, order_status os where o.id_status = os.id_status and o.id_order = od.id_order and os.name_status = 'Выполнен' group by order_date";
        loadChart(sql);
    }

    public void loadChartForCancelled(MouseEvent event) {
        String sql = "select date(od.order_details_date) as order_date, count(o.id_status) as order_count from orders o, order_details od, order_status os where o.id_status = os.id_status and o.id_order = od.id_order and os.name_status = 'Отменен' group by order_date";
        loadChart(sql);
    }

    public void loadChart(String sql) {
        home_chart.getData().clear();

        XYChart.Series<String, Number> orderCountSeries = new XYChart.Series<>();
        orderCountSeries.setName("Количество заказов");

        XYChart.Series<String, Number> orderDateSeries = new XYChart.Series<>();
        orderDateSeries.setName("Дата заказа");

        try {
            PreparedStatement preparedStatement = connect.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                orderCountSeries.getData().add(new XYChart.Data<>(resultSet.getString("order_date"), resultSet.getInt("order_count")));
                orderDateSeries.getData().add(new XYChart.Data<>(resultSet.getString("order_date"), resultSet.getInt("order_count")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Добавление серий в график
        home_chart.getData().addAll(orderCountSeries, orderDateSeries);
    }

    public void getOrderStatusCounts() {
        String sql = "SELECT * FROM get_order_status_counts()";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int home_totalOrderProcessing = resultSet.getInt("processing_count");
                int home_totalOrderCompleted = resultSet.getInt("completed_count");
                int home_totalOrderCancelled = resultSet.getInt("cancelled_count");

                this.home_totalOrderProcessing.setText(Integer.toString(home_totalOrderProcessing));
                this.home_totalOrderCompleted.setText(Integer.toString(home_totalOrderCompleted));
                this.home_totalOrderCancelled.setText(Integer.toString(home_totalOrderCancelled));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOrder() {
        String orderIdText = orders_id.getText();
        String orderUser = orders_user.getValue();
        String orderPickUpPoint = orders_pick_up_point.getValue();
        String orderProduct = orders_product.getValue();
        String orderStatus = orders_status.getValue();
        String orderQuantityText = orders_quantity.getText();

        if (orderIdText.isEmpty() || orderUser.isEmpty() || orderPickUpPoint.isEmpty() || orderProduct.isEmpty() || orderStatus.isEmpty() || orderQuantityText.isEmpty()) {
            return;
        }

        int orderId;
        int orderQuantity;

        // Проверяем, являются ли введенные значения целыми числами
        try {
            orderId = Integer.parseInt(orderIdText);
            orderQuantity = Integer.parseInt(orderQuantityText);
        } catch (NumberFormatException e) {
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation Dialog");
        confirmationAlert.setHeaderText(null);
        confirmationAlert.setContentText("Are you sure you want to add this order?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.get() != ButtonType.OK){
            return;
        }

        String sqlUser = "SELECT id_user FROM users WHERE name_user = ?";
        String sqlPickUpPoint = "SELECT id_pick_up_point FROM pick_up_points WHERE name_pick_up_point = ?";
        String sqlProduct = "SELECT id_product FROM products WHERE name_product = ?";
        String sqlOrderStatus = "SELECT id_status FROM order_status WHERE name_status = ?";

        int userId = getIdFromDatabase(sqlUser, orderUser);
        int pickUpPointId = getIdFromDatabase(sqlPickUpPoint, orderPickUpPoint);
        int productId = getIdFromDatabase(sqlProduct, orderProduct);
        int orderStatusId = getIdFromDatabase(sqlOrderStatus, orderStatus);

        if (orderExists(orderId)) {
            String sqlOrderDetailsDate = "SELECT order_details_date FROM order_details WHERE id_order = ?";
            Date orderDetailsDate = getDateFromDatabase(sqlOrderDetailsDate, orderId);

            String sqlOrderDetails = "INSERT INTO order_details (id_order, order_details_date, order_details_id_product, order_details_quantity_product) VALUES (?, ?, ?, ?)";
            insertIntoDatabase(sqlOrderDetails, orderId, orderDetailsDate, productId, orderQuantity);
        } else {
            String sqlOrder = "INSERT INTO orders (id_order, id_user, id_pick_up_point, id_status) VALUES (?, ?, ?, ?)";
            insertIntoDatabase(sqlOrder, orderId, userId, pickUpPointId, orderStatusId);

            String sqlOrderDetails = "INSERT INTO order_details (id_order, order_details_date, order_details_id_product, order_details_quantity_product) VALUES (?, current_date, ?, ?)";
            insertIntoDatabase(sqlOrderDetails, orderId, productId, orderQuantity);
        }

        ordersShowListData();

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success Dialog");
        successAlert.setHeaderText(null);
        successAlert.setContentText("The order has been added successfully.");
        successAlert.showAndWait();

        ordersReset();
    }

    private java.sql.Date getDateFromDatabase(String sql, int orderId) {
        java.sql.Date orderDetailsDate = null;
        connect = database.connectDb();
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setInt(1, orderId);
            result = prepare.executeQuery();
            if (result.next()) {
                orderDetailsDate = result.getDate("order_details_date");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return orderDetailsDate;
    }

    public boolean orderExists(int orderId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE id_order = ?";
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setInt(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1) != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void ordersUpdate() {
        getOrderData getOrderD = orders_tableview.getSelectionModel().getSelectedItem();
        int num = orders_tableview.getSelectionModel().getSelectedIndex();

        if(getOrderD == null || (num - 1) < - 1) {
            return;
        }

        if((num - 1) < - 1) {
            return;
        }

        int orderId = getOrderD.getOrderId();
        String orderUser = orders_user.getValue();
        String orderPickUpPoint = orders_pick_up_point.getValue();
        String orderProduct = orders_product.getValue();
        String orderStatus = orders_status.getValue();
        int orderQuantity = Integer.parseInt(orders_quantity.getText());

        // Получаем id пользователя, точки выдачи и продукта
        String sqlUser = "SELECT id_user FROM users WHERE name_user = ?";
        String sqlPickUpPoint = "SELECT id_pick_up_point FROM pick_up_points WHERE name_pick_up_point = ?";
        String sqlProduct = "SELECT id_product FROM products WHERE name_product = ?";
        String sqlOrderStatus = "SELECT id_status FROM order_status WHERE name_status = ?";

        int userId = getIdFromDatabase(sqlUser, orderUser);
        int pickUpPointId = getIdFromDatabase(sqlPickUpPoint, orderPickUpPoint);
        int productId = getIdFromDatabase(sqlProduct, orderProduct);
        int orderStatusId = getIdFromDatabase(sqlOrderStatus, orderStatus);

        // Show confirmation alert
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to update this order?");
        confirmationAlert.setContentText("Order details:\n" +
                "Order ID: " + orderId + "\n" +
                "User: " + orderUser + "\n" +
                "Pick-up point: " + orderPickUpPoint + "\n" +
                "Product: " + orderProduct + "\n" +
                "Status: " + orderStatus + "\n" +
                "Quantity: " + orderQuantity);
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.get() != ButtonType.OK){
            return;
        }

        // Обновляем запись в таблице "order"
        String sqlOrder = "UPDATE orders SET id_user = ?, id_pick_up_point = ?, id_status = ? WHERE id_order = ?";
        insertIntoDatabase(sqlOrder, userId, pickUpPointId, orderStatusId, orderId);

        // Обновляем запись в таблице order_details
        String sqlOrderDetails = "UPDATE order_details SET order_details_id_product = ?, order_details_quantity_product = ? WHERE id_order = ?";
        insertIntoDatabase(sqlOrderDetails, productId, orderQuantity, orderId);

        ordersShowListData();

        // Show success alert
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        successAlert.setContentText("The order has been changed.");
        successAlert.showAndWait();

        ordersReset();
    }

    public void ordersSelect() {
        getOrderData getOrderD = orders_tableview.getSelectionModel().getSelectedItem();

        if(getOrderD == null) {
            return;
        }

        orders_id.setText(String.valueOf(getOrderD.getOrderId()));
        orders_user.setValue(getOrderD.getOrderUserName());
        orders_pick_up_point.setValue(getOrderD.getAddressPickUpPoint());
        orders_product.setValue(getOrderD.getNameProduct());
        orders_status.setValue(getOrderD.getOrderStatusName());
        orders_quantity.setText(String.valueOf(getOrderD.getOrderQuantity()));
    }

    public void addOrderStatusData() {
        ObservableList<String> listData = FXCollections.observableArrayList();
        String sql = "SELECT name_status FROM order_status";

        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()){
                listData.add(result.getString("name_status"));
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        orders_status.setItems(listData);
    }

    public ObservableList<getOrderData> ordersListData(){
        ObservableList<getOrderData> listData = FXCollections.observableArrayList();
        String sql = "select od2.id_order, od2.order_details_date, u2.name_user, pup.name_pick_up_point, os.name_status, p.name_product, od2.order_details_quantity_product from order_details od2, orders o, products p, pick_up_points pup, users u2, order_status os where od2.id_order = o.id_order and od2.order_details_id_product = p.id_product and o.id_user = u2.id_user and o.id_pick_up_point = pup.id_pick_up_point and o.id_status = os.id_status";

        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();
            getOrderData getOrderD;

            while (result.next()){
                getOrderD = new getOrderData(
                        result.getInt("id_order"),
                        result.getString("name_user"),
                        result.getString("name_pick_up_point"),
                        result.getString("name_status"), // Измените это на имя статуса
                        result.getString("name_product"),
                        result.getInt("order_details_quantity_product")
                );

                listData.add(getOrderD);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return listData;
    }

    public void ordersShowListData() {
        orders_tableview.setItems(ordersListData());

        orders_col_id.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orders_col_user.setCellValueFactory(new PropertyValueFactory<>("orderUserName"));
        orders_col_pick_up_point.setCellValueFactory(new PropertyValueFactory<>("addressPickUpPoint"));
        orders_col_status.setCellValueFactory(new PropertyValueFactory<>("orderStatusName")); // Измените это на имя статуса
        orders_col_product.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
        orders_col_quantity.setCellValueFactory(new PropertyValueFactory<>("orderQuantity"));
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

    private void openAddWindow(String windowName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(windowName + ".fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            scene.setOnMousePressed((MouseEvent event) -> {
                x = event.getSceneX();
                y = event.getSceneY();
            });

            scene.setOnMouseDragged((MouseEvent event) -> {
                stage.setX(event.getScreenX() - x);
                stage.setY(event.getScreenY() - y);
                stage.setOpacity(.8);
            });

            scene.setOnMouseReleased((MouseEvent event) -> {
                stage.setOpacity(1);
            });

            stage.setOnHidden(event -> {
                // Вызов методов, которые загружают данные для ComboBoxes
                addShowListData(add_product_manufacturer, "manufacturers");
                addShowListData(add_product_provider, "providers");
                addShowListData(add_product_catalog, "catalogs");
                addShowListData(orders_user, "users");
                addShowListData(orders_pick_up_point, "pick_up_points");
                addShowListData(orders_product, "products");
            });

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void addListener(ComboBox<String> comboBox, String item, String windowName) {
        comboBox.getItems().add(item);
        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (item.equals(newValue)) {
                if (windowName.equals("add_product_form")) {
                    add_products_btn.fire();
                } else {
                    openAddWindow(windowName);
                }
            }
        });
    }

    private <T> ObservableList<String> addListData(String tableName) {
        ObservableList<String> listdata = FXCollections.observableArrayList();
        String columnName = tableName.substring(0, tableName.length() - 1);
        String sql = "SELECT name_" + columnName + " FROM " + tableName;

        connect = database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                listdata.add(result.getString("name_" + columnName));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listdata;
    }

    public void addShowListData(ComboBox<String> comboBox, String tableName) {
        ObservableList<String> list = addListData(tableName);
        String columnName = tableName.substring(0, tableName.length() - 1);
        list.add("Add new " + columnName);
        comboBox.setItems(FXCollections.emptyObservableList());
        comboBox.setItems(list);
    }

    public void addProductsInsertImage(){
        FileChooser open = new FileChooser();
        File file = open.showOpenDialog(main_form.getScene().getWindow());

        if(file == null){
            return;
        }

        getUsersData.path = file.getAbsolutePath();

        image = new Image(file.toURI().toString(), 157, 157, false, true);
        add_product_image.setImage(image);
    }

    public ObservableList<getProductsData> addProductsListData(){
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

    private ObservableList<getProductsData> addProductsList;

    public void addProductsShowListData() {
        addProductsList = addProductsListData();

        add_product_col_id.setCellValueFactory(new PropertyValueFactory<>("productsId"));
        add_product_col_name.setCellValueFactory(new PropertyValueFactory<>("productsName"));
        add_product_col_quantity.setCellValueFactory(new PropertyValueFactory<>("productsQuantity"));
        add_product_col_manufacturer.setCellValueFactory(new PropertyValueFactory<>("nameManufacturer"));
        add_product_col_provider.setCellValueFactory(new PropertyValueFactory<>("nameProvider"));
        add_product_col_catalog.setCellValueFactory(new PropertyValueFactory<>("nameCatalog"));
        add_product_col_price.setCellValueFactory(new PropertyValueFactory<>("productsPrice"));

        add_product_tableview.setItems(addProductsList);
    }

    public void addProductSelect(){
        getProductsData getProductsD = add_product_tableview.getSelectionModel().getSelectedItem();

        if(getProductsD == null) {
            return;
        }

        add_product_id.setText(String.valueOf(getProductsD.getProductsId()));
        add_product_name.setText(getProductsD.getProductsName());
        add_product_manufacturer.setValue(getProductsD.getNameManufacturer());
        add_product_provider.setValue(getProductsD.getNameProvider());
        add_product_catalog.setValue(getProductsD.getNameCatalog());
        add_product_quantity.setText(String.valueOf(getProductsD.getProductsQuantity()));
        add_product_price.setText(String.valueOf(getProductsD.getProductsPrice()));

        String os = System.getProperty("os.name").toLowerCase();
        String uri;

        if (os.contains("win")) {
            uri = "file:" + getProductsD.getProductsImage();
        } else {
            uri = "file//" + getProductsD.getProductsImage();
        }

        image = new Image(uri,157, 157, false, true);
        add_product_image.setImage(image);
    }

    public void addProductsAdd() {
        String productName = add_product_name.getText();
        String productQuantityText = add_product_quantity.getText();
        String productPriceText = add_product_price.getText();
        String productImage = getUsersData.path;

        String manufacturerName = add_product_manufacturer.getValue();
        String providerName = add_product_provider.getValue();
        String catalogName = add_product_catalog.getValue();

        if (productName.isEmpty() || productQuantityText.isEmpty() || productPriceText.isEmpty() ||
                manufacturerName.isEmpty() || providerName.isEmpty() || catalogName.isEmpty()) {
            return;
        }

        int productQuantity;
        int productPrice;

        try {
            productQuantity = Integer.parseInt(add_product_quantity.getText());
            productPrice = Integer.parseInt(add_product_price.getText());
        } catch (NumberFormatException e) {
            return;
        }

        String sqlManufacturer = "SELECT id_manufacturer FROM manufacturers WHERE name_manufacturer = ?";
        String sqlProvider = "SELECT id_provider FROM providers WHERE name_provider = ?";
        String sqlCatalog = "SELECT id_catalog FROM catalogs WHERE name_catalog = ?";

        int manufacturerId = getIdFromDatabase(sqlManufacturer, manufacturerName);
        int providerId = getIdFromDatabase(sqlProvider, providerName);
        int catalogId = getIdFromDatabase(sqlCatalog, catalogName);

        String sqlMaxId = "SELECT MAX(id_product) FROM products";
        int maxId = getMaxIdFromDatabase(sqlMaxId);
        int productId = maxId + 1;

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to add this product?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.get() != ButtonType.OK){
            return;
        }

        String sqlProduct = "INSERT INTO products (id_product, name_product, quantity_product, id_manufacturer, id_provider, id_catalog, price, image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        insertIntoDatabase(sqlProduct, productId, productName, productQuantity, manufacturerId, providerId, catalogId, productPrice, productImage);

        addProductsShowListData();

        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(null);
        successAlert.setContentText("The product has been added.");
        successAlert.showAndWait();

        addProductsReset();
    }

    public void addProductUpdate() {
        getProductsData getProductsD = add_product_tableview.getSelectionModel().getSelectedItem();
        int num = add_product_tableview.getSelectionModel().getSelectedIndex();

        if(getProductsD == null || (num - 1) < - 1) {
            return;
        }

        if((num - 1) < - 1) {
            return;
        }

        int productId = getProductsD.getProductsId();
        String productName = add_product_name.getText();
        int productQuantity = Integer.parseInt(add_product_quantity.getText());
        int productPrice = Integer.parseInt(add_product_price.getText());

        String manufacturerName = add_product_manufacturer.getValue();
        String providerName = add_product_provider.getValue();
        String catalogName = add_product_catalog.getValue();

        String sqlManufacturer = "SELECT id_manufacturer FROM manufacturers WHERE name_manufacturer = ?";
        String sqlProvider = "SELECT id_provider FROM providers WHERE name_provider = ?";
        String sqlCatalog = "SELECT id_catalog FROM catalogs WHERE name_catalog = ?";

        int manufacturerId = getIdFromDatabase(sqlManufacturer, manufacturerName);
        int providerId = getIdFromDatabase(sqlProvider, providerName);
        int catalogId = getIdFromDatabase(sqlCatalog, catalogName);

        String currentImagePath = getProductsD.getProductsImage();

        String productImage = getUsersData.path;
        if (productImage == null || productImage.isEmpty()) {
            productImage = currentImagePath;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to update this product?");
        confirmationAlert.setContentText("Product details:\n" +
                "Product ID: " + productId + "\n" +
                "Product Name: " + productName + "\n" +
                "Quantity: " + productQuantity + "\n" +
                "Price: " + productPrice + "\n" +
                "Manufacturer: " + manufacturerName + "\n" +
                "Provider: " + providerName + "\n" +
                "Catalog: " + catalogName + "\n" +
                "Image: " + productImage);

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.get() == ButtonType.OK){
            // Обновляем запись в таблице products
            String sqlProduct = "UPDATE products SET name_product = ?, quantity_product = ?, id_manufacturer = ?, id_provider = ?, id_catalog = ?, price = ?, image = ? WHERE id_product = ?";
            insertIntoDatabase(sqlProduct, productName, productQuantity, manufacturerId, providerId, catalogId, productPrice, productImage, productId);

            addProductsShowListData();

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The product has been changed.");
            successAlert.showAndWait();

            addProductsReset();
        }
    }

    public void addProductDelete() {
        getProductsData getProductsD = add_product_tableview.getSelectionModel().getSelectedItem();
        int num = add_product_tableview.getSelectionModel().getSelectedIndex();

        if (getProductsD == null || (num - 1) < -1) {
            return;
        }

        int productId = getProductsD.getProductsId();

        String productName = getProductsD.getProductsName();

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Are you sure you want to delete this product?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.get() == ButtonType.OK){
            String sqlDelete = "DELETE FROM products WHERE id_product = ?";
            connect = database.connectDb();
            try {
                prepare = connect.prepareStatement(sqlDelete);
                prepare.setInt(1, productId);
                prepare.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }

            addProductsShowListData();

            // Создаем экземпляр Alert с типом INFORMATION
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("The product has been removed.");
            successAlert.showAndWait();

            addProductsReset();
        }
    }

    public void addProductsReset(){
        add_product_id.setText("");
        add_product_name.setText("");
        add_product_quantity.setText("");
        add_product_manufacturer.getSelectionModel().clearSelection();
        add_product_provider.getSelectionModel().clearSelection();
        add_product_catalog.getSelectionModel().clearSelection();
        add_product_price.setText("");
        add_product_image.setImage(null);
        getUsersData.path = "";
    }

    public void switchForm(ActionEvent event){
        Map<Node, String> buttonStyles = new HashMap<>();
        buttonStyles.put(home_btn, "-fx-background-color: #fce6dd; -fx-background-radius: 10;");
        buttonStyles.put(add_products_btn, "-fx-background-color: #fce6dd; -fx-background-radius: 10;");
        buttonStyles.put(order_btn, "-fx-background-color: #fce6dd; -fx-background-radius: 10;");

        Node source = (Node) event.getSource();
        home_form.setVisible(source == home_btn);
        add_product_form.setVisible(source == add_products_btn);
        orders_form.setVisible(source == order_btn);

        for (Node button : buttonStyles.keySet()) {
            String style = buttonStyles.get(button);
            if (button == source) {
                style += " -fx-background-radius: 15; -fx-border-color: #C41E3A; -fx-border-radius: 15; -fx-border-width: 2";
            }
            button.setStyle(style);
        }

        if (source == add_products_btn) {
            addProductsShowListData();
            SearchListener(add_product_tableview, add_product_search, add_product_tableview.getItems());
        } else if (source == order_btn) {
            ordersShowListData();
            addOrderStatusData();
            SearchListener(orders_tableview, orders_search, orders_tableview.getItems());
        } else if (source == home_btn) {
            getOrderStatusCounts();
        }
    }

    public void displayUsername(){
        username.setText(getUsersData.username + " " + getUsersData.usersurname);
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

    public void close(){
        System.exit(0);
    }

    public void minimize(){
        Stage stage = (Stage)main_form.getScene().getWindow();
        stage.setIconified(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayUsername();

        add_product_tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        orders_tableview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addListener(add_product_manufacturer, "Add new manufacturer", "addManufacturer");
        addListener(add_product_provider, "Add new provider", "addProvider");
        addListener(add_product_catalog, "Add new catalog", "addCatalog");
        addListener(orders_user, "Add new user", "addUser");
        addListener(orders_pick_up_point, "Add new pick_up_point", "addPick_up_point");
        addListener(orders_product, "Add new product", "add_product_form");

        addShowListData(add_product_manufacturer, "manufacturers");
        addShowListData(add_product_provider, "providers");
        addShowListData(add_product_catalog, "catalogs");
        addShowListData(orders_user, "users");
        addShowListData(orders_pick_up_point, "pick_up_points");
        addShowListData(orders_product, "products");
    }
}