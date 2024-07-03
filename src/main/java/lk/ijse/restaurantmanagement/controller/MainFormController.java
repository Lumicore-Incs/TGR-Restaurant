package lk.ijse.restaurantmanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainFormController {
    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane root;

    @FXML
    void btnDashboardOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void initialize() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void btnExitOnAction() throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/login_form.fxml")));
        Stage stage = (Stage) mainPane.getScene().getWindow();
        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Login Form");
        stage.centerOnScreen();
    }

    public void btnPlaceOrderOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/placeOrder_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void btnSalaryOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/salary_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void btnEmployeeOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/employee_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void btnItemOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/item_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void btnCustomerOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/customer_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void btnMenuOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/menu_form.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }

    public void btnReportOnAction() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/IncomeReportForm.fxml"));
        Pane registerPane = fxmlLoader.load();
        root.getChildren().clear();
        root.getChildren().add(registerPane);
    }
}