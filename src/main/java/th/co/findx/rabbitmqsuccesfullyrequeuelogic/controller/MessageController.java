package th.co.findx.rabbitmqsuccesfullyrequeuelogic.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    private RabbitTemplate template;

    @GetMapping("/test")
    public ResponseEntity texting(@RequestParam String text) {
        template.convertAndSend("some-exchange", "some-routing-key", text);
        return ResponseEntity.ok().build();
    }
}
