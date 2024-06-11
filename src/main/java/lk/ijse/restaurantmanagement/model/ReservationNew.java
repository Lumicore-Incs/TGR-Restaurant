package lk.ijse.restaurantmanagement.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class ReservationNew {
    private String reservationId;
    private String tableId;
    private int noOfTables;
    private String date;
}
