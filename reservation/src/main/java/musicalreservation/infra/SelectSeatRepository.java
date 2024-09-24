package musicalreservation.infra;

import java.util.List;
import musicalreservation.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "selectSeats",
    path = "selectSeats"
)
public interface SelectSeatRepository
    extends PagingAndSortingRepository<SelectSeat, Long> {}
