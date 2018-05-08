package org.melodicdeath;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Publisher {
    public static void main(String[] args) throws Exception{
        ConnectionFactory connectionFactory = new ConnectionFactory();
//        connectionFactory.setUri("amqp://test:test@139.199.65.115:9672/test");
        connectionFactory.setHost("139.199.65.115");
        connectionFactory.setPort(9672);
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        connectionFactory.setVirtualHost("/test");
        Connection connection = connectionFactory.newConnection();

        Channel channel = connection.createChannel();

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder().deliveryMode(2).
                contentEncoding("UTF-8").build();

        //第一个参数是exchange参数，如果是为空字符串，那么就会发送到(AMQP default)默认的exchange，而routingKey便是所要发送到的队列名
        channel.basicPublish("","queue.test",properties,"中文中文中文中文1".getBytes());
        channel.basicPublish("","queue.test",properties,"中文中文中文中文2".getBytes());

        //direct类型的exchange类型的exchange，zhihao.miao.order绑定zhihao.info.miao队列，route key是order
        channel.basicPublish("exchange.test","test",properties,"中文中文中文中文3".getBytes());

        channel.close();
        connection.close();
    }
}
