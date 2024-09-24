package musicalreservation.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class ItemCreated extends AbstractEvent {

    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;

    public ItemCreated(Item aggregate) {
        super(aggregate);
    }

    public ItemCreated() {
        super();
    }
}
//>>> DDD / Domain Event
