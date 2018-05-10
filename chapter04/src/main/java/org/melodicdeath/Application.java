package org.melodicdeath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

//RabbitListenerConfigurer
@EnableRabbit
@ComponentScan
public class Application {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        System.out.println("===start up======");
        RabbitTemplate rabbitTemplate = context.getBean("rabbitTemplate", RabbitTemplate.class);
        rabbitTemplate.convertAndSend("", "queue.test", "test", new CorrelationData("queue.test"));
        rabbitTemplate.convertAndSend("", "queue.direct.test2", "test2", new CorrelationData("queue.direct.test2"));
        rabbitTemplate.convertAndSend("", "queue.test3", "test", new CorrelationData("queue.test3"));

        Order order = new Order();
        order.setId(1);
        order.setUserId(1000);
        order.setAmout(88d);
        order.setTime(LocalDateTime.now().toString());

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(order);
//        System.out.println(json);

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType("application/json");
        Message message = new Message(json.getBytes(), messageProperties);
        rabbitTemplate.send("", "queue.test2", message, new CorrelationData("queue.test2"));
//        rabbitTemplate.convertAndSend("queue.test2",order);

        TimeUnit.SECONDS.sleep(60);

        context.close();
    }
}
