package musicalreservation.infra;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.naming.NameParser;
import javax.naming.NameParser;
import javax.transaction.Transactional;
import musicalreservation.config.kafka.KafkaProcessor;
import musicalreservation.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

//<<< Clean Arch / Inbound Adaptor
@Service
@Transactional
public class PolicyHandler {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    SeatRepository seatRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='ReservationCancelPlaced'"
    )
    public void wheneverReservationCancelPlaced_RefundSeat(
        @Payload ReservationCancelPlaced reservationCancelPlaced
    ) {
        ReservationCancelPlaced event = reservationCancelPlaced;
        System.out.println(
            "\n\n##### listener RefundSeat : " +
            reservationCancelPlaced +
            "\n\n"
        );

        // Sample Logic //
        Seat.refundSeat(event);
    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='ReservationPlaced'"
    )
    public void wheneverReservationPlaced_SellSeat(
        @Payload ReservationPlaced reservationPlaced
    ) {
        ReservationPlaced event = reservationPlaced;
        System.out.println(
            "\n\n##### listener SellSeat : " + reservationPlaced + "\n\n"
        );

        // Sample Logic //
        Seat.sellSeat(event);
    }
}
//>>> Clean Arch / Inbound Adaptor
