package musicalreservation.domain;

import musicalreservation.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "seats", path = "seats")
public interface SeatRepository
    extends PagingAndSortingRepository<Seat, Long> {}
