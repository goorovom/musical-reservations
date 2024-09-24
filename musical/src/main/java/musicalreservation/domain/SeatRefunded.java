package musicalreservation.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class SeatRefunded extends AbstractEvent {

    private Long id;
    private Long musicalId;
    private Boolean isSold;
    private Long reservationId;

    public SeatRefunded(Seat aggregate) {
        super(aggregate);
    }

    public SeatRefunded() {
        super();
    }
}
//>>> DDD / Domain Event
