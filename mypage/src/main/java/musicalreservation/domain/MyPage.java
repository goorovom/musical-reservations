package musicalreservation.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "MyPage_table")
@Data
public class MyPage {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private Long musicalId;
    private String musicalName;
    private Long reservationId;
    private String status;
    private Date reservedDt;
    private Long userId;
}
