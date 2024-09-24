package musicalreservation.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

//<<< DDD / Domain Event
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

    public ReservationCompleted(Reservation aggregate) {
        super(aggregate);
    }

    public ReservationCompleted() {
        super();
    }
}
//>>> DDD / Domain Event
