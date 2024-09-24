package musicalreservation.infra;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import musicalreservation.config.kafka.KafkaProcessor;
import musicalreservation.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class MyPageViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCompleted_then_CREATE_1(
        @Payload ReservationCompleted reservationCompleted
    ) {
        try {
            if (!reservationCompleted.validate()) return;

            // view 객체 생성
            MyPage myPage = new MyPage();
            // view 객체에 이벤트의 Value 를 set 함
            myPage.setReservationId(reservationCompleted.getId());
            myPage.setMusicalId(reservationCompleted.getMusicalId());
            myPage.setMusicalName(reservationCompleted.getMusicalName());
            myPage.setStatus(reservationCompleted.getStatus());
            myPage.setReservedDt(reservationCompleted.getReservedDt());
            // view 레파지 토리에 save
            myPageRepository.save(myPage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCancelCompleted_then_UPDATE_1(
        @Payload ReservationCancelCompleted reservationCancelCompleted
    ) {
        try {
            if (!reservationCancelCompleted.validate()) return;
            // view 객체 조회

            List<MyPage> myPageList = myPageRepository.findByReservationId(
                reservationCancelCompleted.getId()
            );
            for (MyPage myPage : myPageList) {
                // view 객체에 이벤트의 eventDirectValue 를 set 함
                myPage.setReservationId(reservationCancelCompleted.getId());
                myPage.setMusicalId(reservationCancelCompleted.getMusicalId());
                myPage.setMusicalName(
                    reservationCancelCompleted.getMusicalName()
                );
                myPage.setStatus(reservationCancelCompleted.getStatus());
                myPage.setReservedDt(
                    reservationCancelCompleted.getReservedDt()
                );
                // view 레파지 토리에 save
                myPageRepository.save(myPage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenReservationCancelPlaced_then_DELETE_1(
        @Payload ReservationCancelPlaced reservationCancelPlaced
    ) {
        try {
            if (!reservationCancelPlaced.validate()) return;
            // view 레파지 토리에 삭제 쿼리
            myPageRepository.deleteByReservationId(
                reservationCancelPlaced.getId()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //>>> DDD / CQRS
}
