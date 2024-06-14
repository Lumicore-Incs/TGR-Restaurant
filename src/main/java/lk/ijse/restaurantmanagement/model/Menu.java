package lk.ijse.restaurantmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Menu {
    private String id;
    private String name;
    private String size;
    private String unitPrice;
    private String status;
}
