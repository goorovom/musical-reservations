package musicalreservation.infra;

import java.util.List;
import musicalreservation.domain.*;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(
    collectionResourceRel = "searchMusicals",
    path = "searchMusicals"
)
public interface SearchMusicalRepository
    extends PagingAndSortingRepository<SearchMusical, Long> {}
