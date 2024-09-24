package musicalreservation.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import musicalreservation.ReservationApplication;
import musicalreservation.domain.ReservationCancelCompleted;
import musicalreservation.domain.ReservationCancelPlaced;
import musicalreservation.domain.ReservationCompleted;
import musicalreservation.domain.ReservationPlaced;

@Entity
@Table(name = "Reservation_table")
@Data
//<<< DDD / Aggregate Root
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long userId;

    private Long musicalId;

    private String musicalName;

    private Long seatId;

    private Integer seatRow;

    private Integer seatCol;

    private Date reservedDt;

    private String status;

    @PostPersist
    public void onPostPersist() {
        ReservationPlaced reservationPlaced = new ReservationPlaced(this);
        reservationPlaced.publishAfterCommit();
    }

    @PostRemove
    public void onPostRemove() {
        ReservationCancelPlaced reservationCancelPlaced = new ReservationCancelPlaced(
            this
        );
        reservationCancelPlaced.publishAfterCommit();

        ReservationCompleted reservationCompleted = new ReservationCompleted(
            this
        );
        reservationCompleted.publishAfterCommit();

        ReservationCancelCompleted reservationCancelCompleted = new ReservationCancelCompleted(
            this
        );
        reservationCancelCompleted.publishAfterCommit();
    }

    @PreRemove
    public void onPreRemove() {}

    public static ReservationRepository repository() {
        ReservationRepository reservationRepository = ReservationApplication.applicationContext.getBean(
            ReservationRepository.class
        );
        return reservationRepository;
    }

    public void reserve() {
        //implement business logic here:

    }

    public void cancelReservation() {
        //implement business logic here:

    }

    //<<< Clean Arch / Port Method
    public static void updateStatusAlreadySold(
        SeatAlreadySold seatAlreadySold
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Reservation reservation = new Reservation();
        repository().save(reservation);

        */

        /** Example 2:  finding and process
        
        repository().findById(seatAlreadySold.get???()).ifPresent(reservation->{
            
            reservation // do something
            repository().save(reservation);


         });
        */

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
