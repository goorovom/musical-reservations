package musicalreservation.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class SeatAlreadySold extends AbstractEvent {

    private Long id;
    private Long musicalId;
    private Long userId;
    private Long reservationId;
    private Boolean isSold;

    public SeatAlreadySold(Seat aggregate) {
        super(aggregate);
    }

    public SeatAlreadySold() {
        super();
    }
}
//>>> DDD / Domain Event
