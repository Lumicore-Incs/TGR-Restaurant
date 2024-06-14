package lk.ijse.restaurantmanagement.model.tm;

import com.jfoenix.controls.JFXButton;
import lk.ijse.restaurantmanagement.model.Cart;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class CartTm extends Cart {
    private String id;
    private String name;
    private int qty;
    private double unitPrice;
    private double total;
    private String date;
    private JFXButton btnRemove;

    public CartTm(String id, String name, double unitPrice) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
    }
}
