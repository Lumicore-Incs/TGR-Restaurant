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
    private String size;
    private int qty;
    private double unitPrice;
    private double total;
    private String date;
    private JFXButton btnRemove;

    public CartTm(String id, String name,String size, double unitPrice) {
        this.id = id;
        this.name = name;
        this.size=size;
        this.unitPrice = unitPrice;
    }

    public CartTm(String id, String description, int qty, double unitPrice, double total, String date, JFXButton btnRemove) {
        this.id = id;
        this.name = description;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.total = total;
        this.date = date;
        this.btnRemove = btnRemove;
    }
}
