package lk.ijse.restaurantmanagement.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.restaurantmanagement.model.Menu;
import lk.ijse.restaurantmanagement.model.tm.MenuTm;
import lk.ijse.restaurantmanagement.repository.MenuRepo;
import lk.ijse.restaurantmanagement.util.Regex;
import lk.ijse.restaurantmanagement.util.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MenuFormController {
    public ComboBox<String> txtQtyOnHand;
    public JFXTextField txtUnitPrice;
    public JFXTextField txtId;
    public TableColumn<?, ?> colStatus;
    public TableColumn<?, ?> colUnitPrice;
    public TableColumn<?, ?> colSize;
    public TableColumn<?, ?> colName;
    public TableColumn<?, ?> colId;
    public TableView<MenuTm> tblItem;
    public ComboBox<String> cmbStatus;
    public AnchorPane root;
    public JFXTextField txtName;

    private List<Menu> menuList = new ArrayList<>();
    private final String[] size = {"Full", "Harf", "Budget"};

    public void initialize() {
        try {
            autoGenerateId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

        this.menuList = getAllItems();
        setCellValueFactory();
        loadMenuTable();
        comboValueSet();
    }

    private void comboValueSet() {
        txtQtyOnHand.getItems().clear();
        cmbStatus.getItems().clear();
        txtQtyOnHand.getItems().addAll(size);
        cmbStatus.getItems().addAll("Available", "Unavailable");
    }

    private void loadMenuTable() {
        ObservableList<MenuTm> tmList = FXCollections.observableArrayList();

        for (Menu menu : menuList) {
            MenuTm menuTm = new MenuTm(
                    menu.getId(),
                    menu.getName(),
                    menu.getSize(),
                    menu.getUnitPrice(),
                    menu.getStatus()
            );
            tmList.add(menuTm);
        }
        tblItem.setItems(tmList);
        tblItem.getSelectionModel().getSelectedItem();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private List<Menu> getAllItems() {
        List<Menu> menuList = null;
        try {
            menuList = MenuRepo.getAll();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        return menuList;
    }

    private void autoGenerateId() throws SQLException {
        txtId.setText(new MenuRepo().autoGenerateItemCode());
    }

    public void tblClickOnAction() {
        MenuTm selectedItem = tblItem.getSelectionModel().getSelectedItem();
        txtId.setText(selectedItem.getId());
        txtName.setText(selectedItem.getName());
        txtQtyOnHand.setValue(selectedItem.getSize());
        txtUnitPrice.setText(selectedItem.getUnitPrice());
        cmbStatus.setValue(selectedItem.getStatus());
    }

    public void btnDeleteOnAction() {
        if (isValidate()) {
            String id = txtId.getText();

            try {
                boolean isDeleted = MenuRepo.delete(id);
                if (isDeleted) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Menu deleted!").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
            clearFields();
            initialize();
        }
    }

    public void btnClearOnAction() {
        if (isValidate()) {
            clearFields();
        }
    }

    public void btnUpdateOnAction() {
        if (isValidate()) {
            String id = txtId.getText();
            String name = txtName.getText();
            String qtyOnHand = String.valueOf(txtQtyOnHand.getValue());
            String unitPrice = txtUnitPrice.getText();
            String status = String.valueOf(cmbStatus.getValue());

            Menu menu = new Menu(id, name, qtyOnHand, unitPrice, status);
            try {
                boolean isUpdated = MenuRepo.update(menu);
                if (isUpdated) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Menu updated!").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
            clearFields();
            initialize();
        }
    }

    public void btnSaveOnAction() throws SQLException {
        if (isValidate()) {
            String id = txtId.getText();
            String name = txtName.getText();
            String qtyOnHand = String.valueOf(txtQtyOnHand.getValue());
            String unitPrice = txtUnitPrice.getText();
            String status = String.valueOf(cmbStatus.getValue());

            if (MenuRepo.isExit(name, qtyOnHand)==null) {
                Menu menu = new Menu(id, name, qtyOnHand, unitPrice, status);
                try {
                    boolean isSaved = MenuRepo.save(menu);
                    if (isSaved) {
                        new Alert(Alert.AlertType.CONFIRMATION, "Menu saved!").show();
                        clearFields();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
                clearFields();
                initialize();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "All Ready Exits..!").show();
            }
        }
    }

    public void btnBackOnAction() throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/main_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Dashboard Form");
        stage.centerOnScreen();
    }

    public void txtItemNameOnKeyReleased() {
        tblItem.getItems().clear();
        String name = txtName.getText();

        if (name != null) {
            try {
                List<Menu> menuList2 = MenuRepo.searchByMenuName(name);
                ObservableList<MenuTm> tmList = FXCollections.observableArrayList();
                for (Menu menu : menuList2) {
                    MenuTm menuTm = new MenuTm(
                            menu.getId(),
                            menu.getName(),
                            menu.getSize(),
                            menu.getUnitPrice(),
                            menu.getStatus()
                    );
                    tmList.add(menuTm);
                }
                tblItem.setItems(tmList);
                tblItem.getSelectionModel().getSelectedItem();
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        } else {
            initialize();
        }
    }

    public void txtItemUnitPriceOnKeyReleased() {
        Regex.setTextColor(TextField.UNITPRICE, txtUnitPrice);
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtQtyOnHand.setValue("");
        txtUnitPrice.setText("");
        cmbStatus.setValue("");
        initialize();
    }

    public boolean isValidate() {
        if (!Regex.setTextColor(TextField.UNITPRICE, txtUnitPrice)) return false;
        return Regex.setTextColor(TextField.NAME, txtName);
    }

}
