package lk.ijse.restaurantmanagement.model.tm;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class SalaryTm {
    private String salaryId;
    private String employeeId;
    private String employeeName;
    private double amount;
    private String date;
}
