package lk.ijse.restaurantmanagement.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import lk.ijse.restaurantmanagement.repository.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DashboardFormController {

    @FXML
    private Label lblCustomerCount;

    @FXML
    private Label lblEmployeeCount;

    @FXML
    private Label lblItemCount;
    private int customerCount;
    private int itemCount;
    private int employeeCount;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblTime;

    private volatile boolean stop = false;


    @FXML
    private BarChart<String, Number> barChartOrders;

    public void initialize() throws SQLException {
        timeNow();
        LocalDate date = LocalDate.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM dd");
        String formattedDate = date.format(dateFormatter);
        lblDate.setText(formattedDate);

        try {
            customerCount = CustomerRepo.getCustomerCount();
            employeeCount = EmployeeRepo.getEmployeeCount();
            itemCount = MenuRepo.getMenuCount();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
        setCustomerCount(customerCount);
        setItemCount(itemCount);
        setEmployeeCount(employeeCount);
         OrderRepo.ordersCount(barChartOrders);
    }


    public void timeNow(){
        Thread thread = new Thread(()->{
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            while (!stop){
                try {
                    Thread.sleep(1000);
                }catch (Exception e){
                    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
                }
               final String timenow = sdf.format(new Date());
                Platform.runLater(()->{
                  lblTime.setText(timenow);
                });
            }
        });
        thread.start();
    }

    private void setItemCount(int itemCount) {
        lblItemCount.setText(String.valueOf(itemCount));
    }

    private void setCustomerCount(int customerCount) {
        lblCustomerCount.setText(String.valueOf(customerCount));
    }


    private void setEmployeeCount(int employeeCount) {
        lblEmployeeCount.setText(String.valueOf(employeeCount));
    }
}
