package lk.ijse.restaurantmanagement.controller;

import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.restaurantmanagement.db.DbConnection;
import lk.ijse.restaurantmanagement.model.Employee;
import lk.ijse.restaurantmanagement.model.Salary;
import lk.ijse.restaurantmanagement.model.tm.SalaryTm;
import lk.ijse.restaurantmanagement.repository.EmployeeRepo;
import lk.ijse.restaurantmanagement.repository.SalaryRepo;
import lk.ijse.restaurantmanagement.util.Regex;
import lk.ijse.restaurantmanagement.util.TextField;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class SalaryFormController {

    public Label lblEmployeeName;
    public TableColumn<?, ?> colEmployeeName;
    @FXML
    private TableColumn<?, ?> colAmount;

    @FXML
    private TableColumn<?, ?> colDate;

    @FXML
    private TableColumn<?, ?> colEmployeeId;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableView<SalaryTm> tblSalary;

    @FXML
    private ComboBox<String> cmbEmployeeId;

    @FXML
    private JFXTextField txtAmount;

    @FXML
    private DatePicker txtDate;

    @FXML
    private JFXTextField txtSalaryId;


    @FXML
    private AnchorPane root;


    private List<Salary> salaryList = new ArrayList<>();

    public void initialize() throws SQLException {
        try {
            autoGenerateId();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

        this.salaryList = getAllSalaries();
        setCellValueFactory();
        loadCustomerTable();
        getEmployeeIdList();

    }

    private void getEmployeeIdList() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> idList = EmployeeRepo.getIds();
            obList.addAll(idList);
            cmbEmployeeId.setItems(obList);
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    private List<Salary> getAllSalaries() {
        List<Salary> salaryList = null;
        try {
            salaryList = SalaryRepo.getAll();
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        return salaryList;
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("salaryId"));
        colEmployeeId.setCellValueFactory(new PropertyValueFactory<>("employeeId"));
        colEmployeeName.setCellValueFactory(new PropertyValueFactory<>("employeeName"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
    }

    private void loadCustomerTable() throws SQLException {
        ObservableList<SalaryTm> tmList = FXCollections.observableArrayList();

        for (Salary salary : salaryList) {
            Employee employee = EmployeeRepo.searchById(salary.getEmployeeId());
            SalaryTm salaryTm = new SalaryTm(
                    salary.getSalaryId(),
                    salary.getEmployeeId(),
                    employee.getName(),
                    salary.getAmount(),
                    salary.getDate()
            );
            tmList.add(salaryTm);
        }
        tblSalary.setItems(tmList);
    }

    public void tblOnClickAction() {
        SalaryTm selectedItem = tblSalary.getSelectionModel().getSelectedItem();
        if (selectedItem!=null){
            txtSalaryId.setText(selectedItem.getSalaryId());
            cmbEmployeeId.setValue(selectedItem.getEmployeeId());
            txtAmount.setText(String.valueOf(selectedItem.getAmount()));
            txtDate.setValue(LocalDate.parse(selectedItem.getDate()));
        }
    }

    @FXML
    public void btnClearOnAction() {
        if (isValidate()) {
            clearFields();
        }
    }

    @FXML
    private void clearFields() {
        txtSalaryId.setText("");
        txtAmount.setText("");
        cmbEmployeeId.getSelectionModel().clearSelection();
        lblEmployeeName.setText("");
        txtDate.setValue(LocalDate.now());
    }

    @FXML
    void btnSaveOnAction() throws SQLException {
        if (isValidate()) {
            String salaryId = txtSalaryId.getText();
            String employeeId = cmbEmployeeId.getValue();
            double amount = Double.parseDouble(txtAmount.getText());
            String date = String.valueOf(txtDate.getValue());

            Salary salary = new Salary(salaryId, employeeId, amount, date);

            try {
                boolean isSaved = SalaryRepo.save(salary);
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Salary paid!").show();
                    clearFields();
                    autoGenerateId();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
            clearFields();
            initialize();
        }
    }

    @FXML
    public void btnBackOnAction() throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/main_form.fxml")));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Dashboard Form");
        stage.centerOnScreen();
    }

    @FXML
    private void autoGenerateId() throws SQLException {
        txtSalaryId.setText(new SalaryRepo().autoGenarateSalaryId());
    }

    public void cmbEmployeeIdOnAction() throws SQLException {
        String employeeId = cmbEmployeeId.getValue();
        if (employeeId!=null){
            Employee employee = EmployeeRepo.searchById(employeeId);
            lblEmployeeName.setText(employee.getName());
        }
    }

    public void btnReceiptOnAction() throws JRException, SQLException {
        if (isValidate()) {
            JasperDesign jasperDesign =
                    JRXmlLoader.load("reports/salaryPayments.jrxml");
            JasperReport jasperReport =
                    JasperCompileManager.compileReport(jasperDesign);

            Map<String, Object> data = new HashMap<>();
            data.put("salaryId", txtSalaryId.getText());
            data.put("employeeId", cmbEmployeeId.getValue());

            JasperPrint jasperPrint =
                    JasperFillManager.fillReport(
                            jasperReport,
                            data,
                            DbConnection.getInstance().getConnection());

            JasperViewer.viewReport(jasperPrint, false);
        }
    }

    public void txtSalaryOnKeyReleased() {
        Regex.setTextColor(TextField.SALARY, txtAmount);
    }

    public boolean isValidate() {
        return Regex.setTextColor(TextField.AMOUNT, txtAmount);
    }

    public void btnUpdateOnAction() throws SQLException {
        if (isValidate()) {
            String salaryId = txtSalaryId.getText();
            String employeeId = cmbEmployeeId.getValue();
            double amount = Double.parseDouble(txtAmount.getText());
            String date = String.valueOf(txtDate.getValue());

            Salary salary = new Salary(salaryId, employeeId, amount, date);

            try {
                boolean isSaved = SalaryRepo.update(salary);
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "Update Data!").show();
                    clearFields();
                    autoGenerateId();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
            clearFields();
            initialize();
        }
    }

    public void btnDeleteOnAction() throws SQLException {
        String id = txtSalaryId.getText();
        if (SalaryRepo.deleteData(id)){
            new Alert(Alert.AlertType.CONFIRMATION,"Delete this "+id+" Date..!").show();
            clearFields();
            autoGenerateId();
            initialize();
        }else {
            new Alert(Alert.AlertType.ERROR,"Something Wrong..!").show();
        }
    }

    public void btnSearchOnAction() throws SQLException {
        List<Salary> allSalaryEmployee = SalaryRepo.findAllSalaryEmployee(cmbEmployeeId.getValue());

        ObservableList<SalaryTm> tmList = FXCollections.observableArrayList();
        for (Salary salary : allSalaryEmployee) {
            Employee employee = EmployeeRepo.searchById(salary.getEmployeeId());
            SalaryTm salaryTm = new SalaryTm(
                    salary.getSalaryId(),
                    salary.getEmployeeId(),
                    employee.getName(),
                    salary.getAmount(),
                    salary.getDate()
            );
            tmList.add(salaryTm);
        }
        tblSalary.setItems(tmList);
    }
}
