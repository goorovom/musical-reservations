package musicalreservation.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;
import musicalreservation.MusicalApplication;
import musicalreservation.domain.SeatAlreadySold;
import musicalreservation.domain.SeatRefunded;
import musicalreservation.domain.SeatSold;

@Entity
@Table(name = "Seat_table")
@Data
//<<< DDD / Aggregate Root
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long musicalId;

    private String grade;

    private Integer seatRow;

    private Integer seatCol;

    private Boolean isSold;

    private Long userId;

    @PostPersist
    public void onPostPersist() {
        SeatSold seatSold = new SeatSold(this);
        seatSold.publishAfterCommit();

        SeatAlreadySold seatAlreadySold = new SeatAlreadySold(this);
        seatAlreadySold.publishAfterCommit();
    }

    @PostUpdate
    public void onPostUpdate() {
        SeatRefunded seatRefunded = new SeatRefunded(this);
        seatRefunded.publishAfterCommit();
    }

    public static SeatRepository repository() {
        SeatRepository seatRepository = MusicalApplication.applicationContext.getBean(
            SeatRepository.class
        );
        return seatRepository;
    }

    //<<< Clean Arch / Port Method
    public static void refundSeat(
        ReservationCancelPlaced reservationCancelPlaced
    ) {
        //implement business logic here:

        /** Example 1:  new item 
        Seat seat = new Seat();
        repository().save(seat);

        SeatRefunded seatRefunded = new SeatRefunded(seat);
        seatRefunded.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(reservationCancelPlaced.get???()).ifPresent(seat->{
            
            seat // do something
            repository().save(seat);

            SeatRefunded seatRefunded = new SeatRefunded(seat);
            seatRefunded.publishAfterCommit();

         });
        */

    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void sellSeat(ReservationPlaced reservationPlaced) {
        //implement business logic here:

        /** Example 1:  new item 
        Seat seat = new Seat();
        repository().save(seat);

        SeatSold seatSold = new SeatSold(seat);
        seatSold.publishAfterCommit();
        SeatAlreadySold seatAlreadySold = new SeatAlreadySold(seat);
        seatAlreadySold.publishAfterCommit();
        */

        /** Example 2:  finding and process
        
        repository().findById(reservationPlaced.get???()).ifPresent(seat->{
            
            seat // do something
            repository().save(seat);

            SeatSold seatSold = new SeatSold(seat);
            seatSold.publishAfterCommit();
            SeatAlreadySold seatAlreadySold = new SeatAlreadySold(seat);
            seatAlreadySold.publishAfterCommit();

         });
        */

        repository().findById(reservationPlaced.getSeatId()).ifPresent(seat -> {
            // 이미 판매된 좌석일 때
            if(seat.getIsSold()){
                SeatAlreadySold seatAlreadySold = new SeatAlreadySold(seat);
                seatAlreadySold.setReservationId(reservationPlaced.getId());
                seatAlreadySold.publishAfterCommit();
            }else{
                // 예매 가능한 좌석일 때
                seat.setIsSold(true);
                repository().save(seat);
                SeatSold seatSold = new SeatSold(seat);
                seatSold.publishAfterCommit();
            }
        });

    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
