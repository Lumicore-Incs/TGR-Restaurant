package lk.ijse.restaurantmanagement.controller;

import javafx.scene.chart.BarChart;
import javafx.scene.control.DatePicker;
import lk.ijse.restaurantmanagement.repository.OrderRepo;

import java.sql.SQLException;
import java.time.LocalDate;

public class IncomeReportForm {
    public BarChart<String, Number> barChartOrders;
    public DatePicker txtStartDate;
    public DatePicker txtEndDate;

    public void getReportOnAction() throws SQLException {
        LocalDate startDate = txtStartDate.getValue();
        LocalDate endDate = txtEndDate.getValue();
        OrderRepo.ordersReport(barChartOrders,String.valueOf(startDate),String.valueOf(endDate));
    }
}
