package musicalreservation.infra;

import java.util.List;
import musicalreservation.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "myPages", path = "myPages")
public interface MyPageRepository
    extends PagingAndSortingRepository<MyPage, Long> {
    List<MyPage> findByReservationId(Long reservationId);

    void deleteByReservationId(Long reservationId);
}
