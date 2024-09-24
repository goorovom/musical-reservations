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

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString) {}

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='ReservationCompleted'"
    )
    public void wheneverReservationCompleted_NotificateToUser(
        @Payload ReservationCompleted reservationCompleted
    ) {
        ReservationCompleted event = reservationCompleted;
        System.out.println(
            "\n\n##### listener NotificateToUser : " +
            reservationCompleted +
            "\n\n"
        );
        // Sample Logic //

    }

    @StreamListener(
        value = KafkaProcessor.INPUT,
        condition = "headers['type']=='ReservationCancelCompleted'"
    )
    public void wheneverReservationCancelCompleted_NotificateToUser(
        @Payload ReservationCancelCompleted reservationCancelCompleted
    ) {
        ReservationCancelCompleted event = reservationCancelCompleted;
        System.out.println(
            "\n\n##### listener NotificateToUser : " +
            reservationCancelCompleted +
            "\n\n"
        );
        // Sample Logic //

    }
}
//>>> Clean Arch / Inbound Adaptor
