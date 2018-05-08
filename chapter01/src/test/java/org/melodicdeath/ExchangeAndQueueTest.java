package org.melodicdeath;

import com.rabbitmq.client.*;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ExchangeAndQueueTest {
    @Test
    public void testExchange() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("139.199.65.115");
        connectionFactory.setPort(9672);
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        connectionFactory.setVirtualHost("/test");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare("exchange.test", BuiltinExchangeType.DIRECT, true, false, null);

        channel.close();
        connection.close();
    }

    @Test
    public void testQueue() throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("139.199.65.115");
        connectionFactory.setPort(9672);
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        connectionFactory.setVirtualHost("/test");

        Connection connection = connectionFactory.newConnection();
        Channel channel = connection.createChannel();

        AMQP.Queue.DeclareOk ok = channel.queueDeclare("queue.test",true,false,false,null);
        System.out.println(ok);

        channel.queueBind("queue.test","exchange.test","test");

        channel.close();
        connection.close();
    }
}
