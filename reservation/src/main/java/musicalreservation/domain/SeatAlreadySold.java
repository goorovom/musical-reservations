package musicalreservation.domain;

import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

@Data
@ToString
public class SeatAlreadySold extends AbstractEvent {

    private Long id;
    private Long musicalId;
    private Long userId;
    private Long reservationId;
    private Boolean isSold;
}
