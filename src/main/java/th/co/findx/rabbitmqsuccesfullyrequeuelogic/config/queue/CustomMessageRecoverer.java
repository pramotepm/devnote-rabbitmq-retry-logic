package th.co.findx.rabbitmqsuccesfullyrequeuelogic.config.queue;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;

import java.util.HashMap;
import java.util.Map;

public class CustomMessageRecoverer extends RepublishMessageRecoverer {

    private int expireSecond;

    public CustomMessageRecoverer(AmqpTemplate errorTemplate, String errorExchange) {
        super(errorTemplate, errorExchange);
    }

    @Override
    protected Map<? extends String, ? extends Object> additionalHeaders(Message message, Throwable cause) {
        Map headers = new HashMap<>();
        headers.put("x-header-expire-milli", getExpireSecond());
        message.getMessageProperties().setExpiration(String.valueOf(getExpireSecond() * 60 * 1000));
        return headers;
    }

    public int getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(int expireSecond) {
        this.expireSecond = expireSecond;
    }
}