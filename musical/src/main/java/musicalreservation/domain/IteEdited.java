package musicalreservation.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import musicalreservation.domain.*;
import musicalreservation.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class IteEdited extends AbstractEvent {

    private Long id;
    private String name;
    private Date startDate;
    private Date endDate;

    public IteEdited(Item aggregate) {
        super(aggregate);
    }

    public IteEdited() {
        super();
    }
}
//>>> DDD / Domain Event
