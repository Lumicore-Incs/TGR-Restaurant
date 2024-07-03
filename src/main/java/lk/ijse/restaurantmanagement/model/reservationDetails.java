package lk.ijse.restaurantmanagement.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class reservationDetails {

 private String reservationId;
 private String tableId;
 private int reqTablesQty;
}
