package musicalreservation.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.Data;
import musicalreservation.infra.AbstractEvent;

@Data
public class ReservationCancelCompleted extends AbstractEvent {

    private Long id;
    private Long userId;
    private Long musicalId;
    private String musicalName;
    private Long seatId;
    private Integer seatRow;
    private Integer seatCol;
    private String status;
}
