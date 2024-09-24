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
public class MypageViewHandler {

    //<<< DDD / CQRS
    @Autowired
    private MypageRepository mypageRepository;
    //>>> DDD / CQRS
}
