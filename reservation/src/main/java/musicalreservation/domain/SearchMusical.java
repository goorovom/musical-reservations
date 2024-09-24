package musicalreservation.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "SearchMusical_table")
@Data
public class SearchMusical {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
}
