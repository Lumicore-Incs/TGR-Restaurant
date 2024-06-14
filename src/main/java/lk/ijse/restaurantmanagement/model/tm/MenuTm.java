package lk.ijse.restaurantmanagement.model.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class MenuTm {
    private String id;
    private String name;
    private String size;
    private String unitPrice;
    private String status;
}
