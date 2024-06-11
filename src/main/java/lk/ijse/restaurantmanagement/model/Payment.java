package lk.ijse.restaurantmanagement.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Payment {
    private String paymentId;
    private String  cusId;
    private String orderId;
    private String payMethod;
    private Double amount;
   // private Button payButton;
}
