package th.co.findx.rabbitmqsuccesfullyrequeuelogic.config.queue;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.HashMap;
import java.util.Map;

@EnableRabbit
@Configuration
public class RabbitMQConfig implements RabbitListenerConfigurer {
    private final String exchange = "some-exchange";
    private final String queueName = "some-queue-name";
    private final String routingKey = "some-routing-key";

    private final String deadExchange = "some-exchange-dead";
    private final String deadQueueName = "some-dead-queue-name";
    private final int deadTtl = 10000; // 10 seconds


    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPublisherConfirms(true);
        return factory;
    }

    @Bean
    public DirectExchange exchange(RabbitAdmin rabbitAdmin) {
        // Default argument of queue
        Map<String, Object> args1 = new HashMap<>();
        args1.put("x-dead-letter-exchange", "");
        args1.put("x-dead-letter-routing-key", deadQueueName);

        Map<String, Object> args2 = new HashMap<>();
        args2.put("x-dead-letter-exchange", exchange);
        args2.put("x-dead-letter-routing-key", routingKey);
        args2.put("x-message-ttl", deadTtl);

        // Create exchange(s)
        DirectExchange directExchange = new DirectExchange(exchange);
        rabbitAdmin.declareExchange(directExchange);

        DirectExchange deadDirectExchange = new DirectExchange(deadExchange);
        rabbitAdmin.declareExchange(deadDirectExchange);

        // Declare queue(s)
        Queue queue1 = new Queue(queueName, true, false, false, args1);
        rabbitAdmin.declareQueue(queue1);

        Queue queue2 = new Queue(deadQueueName, true, false, false, args2);
        rabbitAdmin.declareQueue(queue2);

        // Bind queue(s)
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue1).to(directExchange).with(routingKey));

        return directExchange;
    }

    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(myHandlerMethodFactory());
    }

    @Bean
    public DefaultMessageHandlerMethodFactory myHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }
}
