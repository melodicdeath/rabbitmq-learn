package org.melodicdeath;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Consumer {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("139.199.65.115");
        connectionFactory.setPort(9672);
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        connectionFactory.setVirtualHost("/test");

        //客户端的消费消息
        Map<String, Object> clientProperties = new HashMap<>();
        clientProperties.put("desc", "just test");
        clientProperties.put("author", "zt");
        clientProperties.put("user", "zt");

        connectionFactory.setClientProperties(clientProperties);

        //给客户端的connetction命名
        Connection connection = connectionFactory.newConnection("test队列的消费者");

        //给channel起个编号
        Channel channel = connection.createChannel(10);

        //返回consumerTag，也可以通过重载方法进行设置consumerTag
        String consumerTag = channel.basicConsume("queue.test", true, new SimpleConsumer(channel));
        System.out.println(consumerTag);

        TimeUnit.SECONDS.sleep(30);

        channel.close();
        connection.close();
    }

    static class SimpleConsumer extends DefaultConsumer {

        public SimpleConsumer(Channel channel) {
            super(channel);
        }

        @Override
        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
            System.out.println(consumerTag);
            System.out.println("properties：" + properties);
            System.out.println("body：" + new String(body));
            System.out.println("----------");
        }
    }
}
