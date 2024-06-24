package lk.ijse.restaurantmanagement.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lk.ijse.restaurantmanagement.db.DbConnection;
import lk.ijse.restaurantmanagement.model.*;
import lk.ijse.restaurantmanagement.model.Menu;
import lk.ijse.restaurantmanagement.model.tm.CartTm;
import lk.ijse.restaurantmanagement.repository.*;
import lk.ijse.restaurantmanagement.util.Regex;
import lk.ijse.restaurantmanagement.util.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class PlaceOrderFormController {
    public Pane menuPane;
    public TableView<CartTm> tblMenuItem;
    public TableColumn<?, ?> colName;
    public JFXTextField txtName;
    public JFXTextField txtCustomerId;
    public JFXTextField txtServiceCharge;
    public JFXTextField txtTableNo;
    @FXML
    private AnchorPane root;

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


    private final ObservableList<CartTm> cartList = FXCollections.observableArrayList();
    private final String[] typeList = {"takeAway", "dineIn"};
    private double netTotal = 0;


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
    }

    @FXML
    void btnAddToCartOnAction() {
        if (isValidate()) {
            if (txtQty.getText()!=null && cmbOrderType.getValue()!=null && txtId.getText()!=null){
                String id = txtId.getText();
                String description = txtName.getText();
                String qty2 = txtQty.getText();
                double unitPrice = Double.parseDouble(txtUnitPrice.getText());
                String date = txtDate.getText();
                JFXButton btnRemove = new JFXButton("remove");
                btnRemove.setStyle("-fx-background-radius:10px; -fx-background-color: grey");
                btnRemove.setCursor(Cursor.HAND);
                int qty= Integer.parseInt(qty2);
                double total = qty * unitPrice;
                btnRemove.setOnAction(e -> {
                    ButtonType yes = new ButtonType("yes", ButtonBar.ButtonData.OK_DONE);
                    ButtonType no = new ButtonType("no", ButtonBar.ButtonData.CANCEL_CLOSE);

                    Optional<ButtonType> type = new Alert(Alert.AlertType.INFORMATION, "Are you sure to remove?", yes, no).showAndWait();

                    if (type.orElse(no) == yes) {
                        CartTm selectedItem = tblOrderCart.getSelectionModel().getSelectedItem();
                        cartList.remove(selectedItem);
                        tblOrderCart.getItems().clear();
                        loadTable();
                        calculateNetTotal();
                    }
                });

                for (int i = 0; i < tblOrderCart.getItems().size(); i++) {
                    if (id.equals(colItemCode.getCellData(i))) {
                        qty += cartList.get(i).getQty();
                        total = unitPrice * qty;

                        cartList.get(i).setQty(qty);
                        cartList.get(i).setTotal(total);

                        tblOrderCart.refresh();
                        calculateNetTotal();
                        txtQty.setText("");
                        return;
                    }
                    clear();
                    clearFields();
                }

                CartTm cartTm = new CartTm(id, description, qty, unitPrice, total, date, btnRemove);

                cartList.add(cartTm);

                tblOrderCart.setItems(cartList);
                txtQty.setText("");
                calculateNetTotal();
            }else {
                new Alert(Alert.AlertType.ERROR,"enter another data.!").show();
            }
        }

    }

    private void calculateNetTotal() {
        for (int i = 0; i < tblOrderCart.getItems().size(); i++) {
            netTotal += (double) colTotal.getCellData(i);
        }
        txtNetTotal.setText(String.valueOf(netTotal));
    }

    @FXML
    void btnBackOnAction() throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/main_form.fxml")));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Dashboard Form");
        stage.centerOnScreen();
    }

    @FXML
    void btnPlaceOrderOnAction() {
        if (isValidate()) {
            String orderId = txtOrderId.getText();
            String orderType = String.valueOf(cmbOrderType.getValue());
            String cusId = txtCustomerId.getText();
            String date = String.valueOf(Date.valueOf(LocalDate.now()));
            int tableNo = Integer.parseInt(txtTableNo.getText());
            double serviceCharge = Double.parseDouble(txtServiceCharge.getText());

            var order = new Order(orderId, orderType, cusId, date, netTotal+serviceCharge, tableNo, serviceCharge);

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
                boolean isPlaced = PlaceOrderRepo.placeOrder(po);
                if (isPlaced) {
                    new Alert(Alert.AlertType.CONFIRMATION, "order placed!").show();
                    autoGenerateId();
                    clearFields();
                } else {
                    new Alert(Alert.AlertType.WARNING, "order not placed!").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
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
            if (customer!=null){
                txtCustomerId.setText(customer.getCusId());
                txtCustomerName.setText(customer.getName());
            }else {
                new Alert(Alert.AlertType.ERROR,"No Customer..!").show();
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
    }

    private void clear() {
        txtId.setText("");
        txtName.setText("");
        txtUnitPrice.setText("");
    }

    public void btnReceiptOnAction() throws JRException, SQLException {
        if (isValidate()) {
            System.out.println("1");
            try {
                JasperDesign load = JRXmlLoader.load(this.getClass().getResourceAsStream("/reports/Blank_A4_1.jrxml"));
                JasperReport jasperReport = JasperCompileManager.compileReport(load);
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, null, DbConnection.getInstance().getConnection());
                JasperViewer.viewReport(jasperPrint, false);
            } catch (JRException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btnGetReceipt() throws JRException, SQLException {
        if (isValidate()) {
            JasperDesign jasperDesign =
                    JRXmlLoader.load("/reports/CustomerReceipt.jrxml");
            JasperReport jasperReport =
                    JasperCompileManager.compileReport(jasperDesign);


            Map<String, Object> data = new HashMap<>();
            data.put("orderId", txtOrderId.getText());
            data.put("qty", txtQty.getText());

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            data,
                            DbConnection.getInstance().getConnection());

            JasperViewer.viewReport(jasperPrint, false);
        }
    }


    public void txtAContactOnKeyReleased() {
        Regex.setTextColor(TextField.CONTACT, txtContact);
    }
    public boolean isValidate(){
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
                            Double.parseDouble(cart.getUnitPrice())
                    );
                    tmList.add(cartTm);
                }
                tblMenuItem.setItems(tmList);
                CartTm selectedItem = tblMenuItem.getSelectionModel().getSelectedItem();
                if (selectedItem!=null){
                    txtId.setText(selectedItem.getId());
                    txtName.setText(selectedItem.getName());
                    menuPane.setVisible(false);
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        } else {
            initialize();
        }
    }

    public void tblClickOnAction() {
        CartTm selectedItem = tblMenuItem.getSelectionModel().getSelectedItem();
        if (selectedItem!=null){
            txtId.setText(selectedItem.getId());
            txtName.setText(selectedItem.getName());
            txtUnitPrice.setText(String.valueOf(selectedItem.getUnitPrice()));
            menuPane.setVisible(false);
        }
    }
}
