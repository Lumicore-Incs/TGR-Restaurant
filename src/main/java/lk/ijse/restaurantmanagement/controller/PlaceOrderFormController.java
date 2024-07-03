package lk.ijse.restaurantmanagement.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import lk.ijse.restaurantmanagement.db.DbConnection;
import lk.ijse.restaurantmanagement.model.Menu;
import lk.ijse.restaurantmanagement.model.*;
import lk.ijse.restaurantmanagement.model.tm.CartTm;
import lk.ijse.restaurantmanagement.repository.*;
import lk.ijse.restaurantmanagement.util.Regex;
import lk.ijse.restaurantmanagement.util.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class PlaceOrderFormController {
    private final ObservableList<CartTm> cartList = FXCollections.observableArrayList();
    private final String[] typeList = {"takeAway", "dineIn"};
    public Pane menuPane;
    public TableView<CartTm> tblMenuItem;
    public TableColumn<?, ?> colName;
    public JFXTextField txtName;
    public JFXTextField txtCustomerId;
    public JFXTextField txtServiceCharge;
    public JFXTextField txtTableNo;
    public JFXButton btnPlaceOrder;
    public JFXButton btnDelete;
    public JFXTextField txtSize;
    public TableColumn<?, ?> colSize;

    @FXML
    private TableColumn<?, ?> colAction;
    @FXML
    private TableColumn<?, ?> colDescription;
    @FXML
    private TableColumn<?, ?> colItemCode;
    @FXML
    private TableColumn<?, ?> colQty;
    @FXML
    private TableColumn<?, ?> colTotal;
    @FXML
    private TableColumn<?, ?> colUnitPrice;
    @FXML
    private TableColumn<?, ?> colDate;
    @FXML
    private TableView<CartTm> tblOrderCart;
    @FXML
    private JFXTextField txtContact;
    @FXML
    private JFXTextField txtCustomerName;
    @FXML
    private JFXTextField txtDate;
    @FXML
    private JFXTextField txtId;
    @FXML
    private JFXTextField txtNetTotal;
    @FXML
    private JFXTextField txtOrderId;
    @FXML
    private JFXTextField txtQty;
    @FXML
    private JFXTextField txtUnitPrice;
    @FXML
    private ComboBox<String> cmbOrderType;

    public void initialize() {
        setCellValueFactory();
        setDate();
        loadTable();
        getOrderList();
        menuPane.setVisible(false);
        try {
            autoGenerateId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        btnPlaceOrder.setText("Place Order");
        btnDelete.setVisible(false);
    }

    public void getOrderList() {
        List<String> typelist = new ArrayList<>();
        Collections.addAll(typelist, typeList);
        ObservableList<String> obList = FXCollections.observableArrayList(typelist);
        cmbOrderType.setItems(obList);
    }

    private void loadTable() {
        ObservableList<CartTm> tmList = FXCollections.observableArrayList();
        for (CartTm cart : cartList) {
            CartTm cartTm = new CartTm(
                    cart.getId(),
                    cart.getName(),
                    cart.getQty(),
                    cart.getUnitPrice(),
                    cart.getTotal(),
                    cart.getDate(),
                    cart.getBtnRemove()
            );
            tmList.add(cartTm);
        }
        tblOrderCart.setItems(tmList);
        tblOrderCart.getSelectionModel().getSelectedItem();
    }

    private void setDate() {
        String now = String.valueOf(LocalDate.now());
        txtDate.setText(now);
    }

    private void setCellValueFactory() {
        colItemCode.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAction.setCellValueFactory(new PropertyValueFactory<>("btnRemove"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
    }

    @FXML
    void btnAddToCartOnAction() {
        if (isValidate()) {
            if (txtQty.getText() != null && cmbOrderType.getValue() != null && txtId.getText() != null) {
                String id = txtId.getText();
                String description = txtName.getText();
                double unitPrice = Double.parseDouble(txtUnitPrice.getText());
                String date = txtDate.getText();

                int qty = Integer.parseInt(txtQty.getText());
                double total = qty * unitPrice;

                // Check if the item is already in the cart
                for (int i = 0; i < tblOrderCart.getItems().size(); i++) {
                    if (id.equals(colItemCode.getCellData(i))) {
                        qty += cartList.get(i).getQty();
                        total = unitPrice * qty;
                        cartList.get(i).setQty(qty);
                        cartList.get(i).setTotal(total);

                        tblOrderCart.refresh();
                        calculateNetTotal();
                        txtQty.setText("");
                        clear();
                        return;
                    }
                }

                // Create a new remove button for this cart item
                JFXButton btnRemove = new JFXButton("remove");
                btnRemove.setStyle("-fx-background-radius:10px; -fx-background-color: grey");
                btnRemove.setCursor(Cursor.HAND);
                btnRemove.setOnAction(e -> {
                    ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
                    ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

                    Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();

                    if (type.orElse(no) == yes) {
                        CartTm selectedItem = tblOrderCart.getSelectionModel().getSelectedItem();
                        cartList.remove(selectedItem);
                        tblOrderCart.getItems().clear();
                        loadTable();
                        calculateNetTotal();
                    }
                });

                // Add the new item to the cart
                CartTm cartTm = new CartTm(id, description, qty, unitPrice, total, date, btnRemove);
                cartList.add(cartTm);
                tblOrderCart.setItems(cartList);
                txtQty.setText("");
                clear();
                calculateNetTotal();
            } else {
                new Alert(Alert.AlertType.ERROR, "Enter all required data!").show();
            }

        }

    }

    private void calculateNetTotal() {
        double fullTotal = 0;
        for (int i = 0; i < tblOrderCart.getItems().size(); i++) {
            fullTotal += (double) colTotal.getCellData(i);
        }
        txtNetTotal.setText(String.valueOf(fullTotal));
    }

    @FXML
    void btnPlaceOrderOnAction(){
        if (isValidate()) {
            String orderId = txtOrderId.getText();
            String orderType = String.valueOf(cmbOrderType.getValue());
            String cusId = txtCustomerId.getText();
            String date = String.valueOf(Date.valueOf(LocalDate.now()));
            int tableNo = 0;
            if (txtTableNo.getText() != null) {
                tableNo = Integer.parseInt(txtTableNo.getText());
            }
            String service = txtServiceCharge.getText();

            if (service != null) {
                double serviceCharge = Double.parseDouble(service);
                double value = Double.parseDouble(txtNetTotal.getText()) + serviceCharge;
                var order = new Order(orderId, orderType, cusId, date, value, tableNo, serviceCharge);

                List<OrderDetail> odList = new ArrayList<>();
                for (int i = 0; i < tblOrderCart.getItems().size(); i++) {
                    CartTm tm = cartList.get(i);

                    OrderDetail od = new OrderDetail(
                            orderId,
                            tm.getId(),
                            tm.getQty(),
                            tm.getUnitPrice()
                    );
                    odList.add(od);
                }

                PlaceOrder po = new PlaceOrder(order, odList);
                try {
                    if (btnPlaceOrder.getText().equals("Place Order")){
                        boolean isPlaced = PlaceOrderRepo.placeOrder(po);
                        if (isPlaced) {
                            new Alert(Alert.AlertType.CONFIRMATION, "order placed!").show();
                            tblOrderCart.getItems().clear();
                            btnReceiptOnAction();
                            clearFields();
                            autoGenerateId();
                        } else {
                            new Alert(Alert.AlertType.WARNING, "order not placed!").show();
                        }
                    }else {
                        boolean isPlaced = PlaceOrderRepo.placeOrderUpdate(po);
                        if (isPlaced) {
                            new Alert(Alert.AlertType.CONFIRMATION, "order Update!").show();
                            tblOrderCart.getItems().clear();
                            btnReceiptOnAction();
                            clearFields();
                            autoGenerateId();
                        } else {
                            new Alert(Alert.AlertType.WARNING, "order not placed!").show();
                        }
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Enter Service Charge..!").show();
            }
        }
    }

    @FXML
    void txtQtyOnAction() {
        Regex.setTextColor(TextField.QTY, txtQty);
        btnAddToCartOnAction();
    }

    public void btnSearchOnAction() {
        String contact = txtContact.getText();

        try {
            Customer customer = CustomerRepo.searchByContact(contact);
            if (customer != null) {
                txtCustomerId.setText(customer.getCusId());
                txtCustomerName.setText(customer.getName());
            } else {
                new Alert(Alert.AlertType.ERROR, "No Customer..!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        initialize();
    }

    @FXML
    private void autoGenerateId() throws SQLException {
        txtOrderId.setText(new OrderRepo().autoGenerateOrderId());
    }

    @FXML
    public void btnClearOnAction() {
        if (isValidate()) {
            clearFields();
        }
    }

    private void clearFields() {
        cmbOrderType.setValue("");
        txtId.setText("");
        txtName.setText("");
        txtUnitPrice.setText("");
        txtContact.setText("");
        txtId.setText("");
        txtCustomerName.setText("");
        txtNetTotal.setText("");
        txtServiceCharge.setText("");
        txtTableNo.setText("");
        initialize();
    }

    private void clear() {
        txtId.setText("");
        txtName.setText("");
        txtUnitPrice.setText("");
        txtQty.clear();
        txtCustomerId.clear();
        txtContact.clear();
        txtContact.clear();
    }

    public void btnReceiptOnAction() throws SQLException {
        String orderId = txtOrderId.getText();
        Order order = OrderRepo.searchById(orderId);
        if (order != null) {
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("OrderId", orderId); // Ensure the key matches the parameter name in the report

                JasperDesign load = JRXmlLoader.load(this.getClass().getResourceAsStream("/reports/newReport.jrxml"));
                JasperReport jasperReport = JasperCompileManager.compileReport(load);
                Connection connection = DbConnection.getInstance().getConnection();


                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, data, connection);

                // Check if the report contains any pages
                if (jasperPrint.getPages().isEmpty()) {
                    new Alert(Alert.AlertType.INFORMATION, "No data found for the given Order ID.").show();
                } else {
                    JasperViewer.viewReport(jasperPrint, false);
                }
            } catch (JRException e) {
                new Alert(Alert.AlertType.INFORMATION, e.getMessage()).show();
            }
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Order not found.").show();
        }
    }

    public void txtAContactOnKeyReleased() {
        Regex.setTextColor(TextField.CONTACT, txtContact);
    }

    public boolean isValidate() {
        Regex.setTextColor(TextField.CONTACT, txtContact);
        Regex.setTextColor(TextField.QTY, txtQty);
        return true;
    }

    public void searchOnMenuOnAction() {
        menuPane.setVisible(true);
        tblMenuItem.getItems().clear();
        String name = txtName.getText();

        if (name != null) {
            try {
                List<Menu> menuList2 = MenuRepo.searchByMenuName(name);
                ObservableList<CartTm> tmList = FXCollections.observableArrayList();
                for (Menu cart : menuList2) {
                    CartTm cartTm = new CartTm(
                            cart.getId(),
                            cart.getName(),
                            cart.getSize(),
                            Double.parseDouble(cart.getUnitPrice())
                    );
                    tmList.add(cartTm);
                }
                tblMenuItem.setItems(tmList);
                CartTm selectedItem = tblMenuItem.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    txtId.setText(selectedItem.getId());
                    txtName.setText(selectedItem.getName());
                    menuPane.setVisible(false);
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
            clear();
        } else {
            initialize();
        }
    }

    public void tblClickOnAction() {
        CartTm selectedItem = tblMenuItem.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            txtId.setText(selectedItem.getId());
            txtName.setText(selectedItem.getName());
            txtSize.setText(selectedItem.getSize());
            txtUnitPrice.setText(String.valueOf(selectedItem.getUnitPrice()));
            menuPane.setVisible(false);
        }
    }

    public void txtSearchOrderOnKeyEvent(KeyEvent keyEvent) throws SQLException {
        JFXButton btnRemove = new JFXButton("remove");
        if (keyEvent.getCode() == KeyCode.ENTER) {
            Order order = OrderRepo.searchById(txtOrderId.getText());
            if (order != null) {
                txtTableNo.setText(String.valueOf(order.getTableNo()));
                txtDate.setText(order.getDate());
                cmbOrderType.setValue(order.getOrderType());
                txtCustomerId.setText(order.getCusId());
                txtNetTotal.setText(String.valueOf(order.getTotal()));
                txtServiceCharge.setText(String.valueOf(order.getServiceCharge()));
                List<OrderDetail> list = OrderDetailRepo.searchByOrderid(order.getOrderId());

                for (OrderDetail dto : list) {
                    Menu menu = MenuRepo.serchByMenuId(dto.getItemId());
                    if (menu!=null){
                        CartTm cartTm = new CartTm(
                                dto.getItemId(),
                                menu.getName(),
                                dto.getQty(),
                                dto.getUnitPrice(),
                                dto.getQty() * dto.getUnitPrice(),
                                order.getDate(),
                                btnRemove
                        );
                        cartList.add(cartTm);
                        tblOrderCart.setItems(cartList);
                    }
                }

                btnRemove.setOnAction(e -> {
                    ButtonType yes = new ButtonType("yes", ButtonBar.ButtonData.OK_DONE);
                    ButtonType no = new ButtonType("no", ButtonBar.ButtonData.CANCEL_CLOSE);

                    Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();

                    if (type.orElse(no) == yes) {
                        CartTm selectedItem = tblOrderCart.getSelectionModel().getSelectedItem();
                        cartList.remove(selectedItem);
                        tblOrderCart.refresh();
                        loadTable();
                        calculateNetTotal();
                    }
                });
            } else {
                new Alert(Alert.AlertType.ERROR, "Empty Data..!").show();
            }
        }
        btnPlaceOrder.setText("Update Order");
        btnDelete.setVisible(true);
    }

    public void btnDeleteOnAction() throws SQLException {
        boolean isDelete=PlaceOrderRepo.deletePlaceOrder(txtOrderId.getText());
        if (isDelete){
            new Alert(Alert.AlertType.CONFIRMATION,"Order Delete..!").show();
        }else {
            new Alert(Alert.AlertType.ERROR,"Something Wrong..!").show();
        }
    }
}
