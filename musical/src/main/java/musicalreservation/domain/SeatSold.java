package musicalreservation.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class SeatSold extends AbstractEvent {

    private Long id;
    private Long musicalId;
    private Boolean isSold;
    private Long userId;
    private Long reservationId;

    public SeatSold(Seat aggregate) {
        super(aggregate);
    }

    public SeatSold() {
        super();
    }
}
//>>> DDD / Domain Event
