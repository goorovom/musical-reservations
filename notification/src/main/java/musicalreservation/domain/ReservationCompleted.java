package musicalreservation.domain;

import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

@Data
@ToString
public class ReservationCompleted extends AbstractEvent {

    private Long id;
    private Long userId;
    private Long musicalId;
    private String musicalName;
    private Long seatId;
    private Integer seatRow;
    private Integer seatCol;
    private String status;
    private Date reservedDt;
}
