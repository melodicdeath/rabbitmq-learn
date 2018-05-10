package org.melodicdeath;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MessageHandler {
    //没有设置默认的处理方法的时候，方法名是handleMessage
    public void handleMessage(String message) {
        System.out.println("---------handleMessage-------------");
        System.out.println(message);
    }

    @RabbitListener(queues = "queue.test")
    public void handleMessage2(String message) {
        System.out.println("handleMessage2");
        System.out.println(new String(message));
    }

    @RabbitListener(bindings = {@QueueBinding(value = @Queue(value = "queue.test2", durable = "true"),
            exchange = @Exchange(value = "exchange.test"), key = "test2")},containerFactory = "rabbitListenerContainerFactory2")
    public void handleMessage3(Order message, Channel channel) throws IOException {
        System.out.println("handleMessage3");
        System.out.println(message);

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().deliveryMode(2).
                contentEncoding("UTF-8").contentType("json").build();
        channel.basicPublish("","queue.test",properties,"123412".getBytes());
    }

}
