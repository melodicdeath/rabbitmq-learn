package melodicdeath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@ComponentScan
@EnableRabbit
public class Application {
    public static void main(String[] args) throws InterruptedException, JsonProcessingException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Application.class);
        System.out.println("===start up======");
        RabbitTemplate rabbitTemplate = context.getBean("rabbitTemplate", RabbitTemplate.class);
        rabbitTemplate.convertAndSend("", "queue.test", "test", new CorrelationData("queue.test"));
        rabbitTemplate.convertAndSend("queue.test2", "test2");

        TimeUnit.SECONDS.sleep(60);

        context.close();
    }
}
