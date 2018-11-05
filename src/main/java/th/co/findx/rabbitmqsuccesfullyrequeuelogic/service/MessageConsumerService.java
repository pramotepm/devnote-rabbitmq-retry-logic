package th.co.findx.rabbitmqsuccesfullyrequeuelogic.service;

import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumerService {

    @RabbitListener(queues = "some-queue-name")
    public void process(@Payload String text) {
        throw new AmqpRejectAndDontRequeueException("try to reject");
    }
}
