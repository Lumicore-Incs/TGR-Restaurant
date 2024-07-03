package lk.ijse.restaurantmanagement.model.tm;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class TablesTm {
    private String tableId;
    private String description;
    private int noOfTables;
    private int noOfSeats;
}
