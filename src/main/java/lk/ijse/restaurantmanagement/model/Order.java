package lk.ijse.restaurantmanagement.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class Order {
    private String orderId;
    private String orderType;
    private String cusId;
    private String date;
    private double total;
}
