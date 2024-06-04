package lk.ijse.restaurantManagement.model.tm;

import com.jfoenix.controls.JFXButton;
import lk.ijse.restaurantManagement.model.Cart;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class CartTm extends Cart {
    private String id;
    private String description;
    private int qty;
    private double unitPrice;
    private double total;
    private String date;
    private JFXButton btnRemove;
}
