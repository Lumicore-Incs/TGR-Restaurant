package lk.ijse.restaurantmanagement.model;

import lombok.*;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlaceOrder {
    private Order order;
    private List<OrderDetail> odList;


}
