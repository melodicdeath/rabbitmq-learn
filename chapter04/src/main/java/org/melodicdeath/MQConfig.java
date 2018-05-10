package org.melodicdeath;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost("139.199.65.115");
        connectionFactory.setPort(9672);
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        connectionFactory.setVirtualHost("/test");

        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);

        return connectionFactory;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        return rabbitAdmin;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            System.out.println("============returnedMessage==method=========");
            System.out.println("replyCode: " + replyCode);
            System.out.println("replyText: " + replyText);
            System.out.println("exchange: " + exchange);
            System.out.println("routingKey: " + routingKey);
        });

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            System.out.println("=====消息进行消费了======");
            if (ack) {
                System.out.println("消息id为: " + correlationData + "的消息，已经被ack成功");
            } else {
                System.out.println("消息id为: " + correlationData + "的消息，消息nack，失败原因是：" + cause);
            }
        });
        return rabbitTemplate;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean("rabbitListenerContainerFactory2")
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory2(ConnectionFactory connectionFactory) {
        //SimpleRabbitListenerContainerFactory发现消息中有content_type有text就会默认将其转换成string类型的
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new MessageConverter() {
            @Override
            public Message toMessage(Object object, MessageProperties messageProperties) throws MessageConversionException {
//                ObjectMapper objectMapper = new ObjectMapper();
//                Message message = null;
//                try {
//                    message  = new Message(objectMapper.writeValueAsBytes(object), messageProperties);
//                    messageProperties.setContentType("json");
//                } catch (JsonProcessingException e) {
//                    e.printStackTrace();
//                }
//
//                return message;

                System.out.println("here");
                return null;
            }

            @Override
            public Object fromMessage(Message message) throws MessageConversionException {
                ObjectMapper objectMapper = new ObjectMapper();
                Order order = null;
                try {
                    order = objectMapper.readValue(new String(message.getBody()), Order.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return order;
            }
        });
        return factory;
    }

    @Bean
    public RabbitListenerConfigurer rabbitListenerConfigurer() {

        return registrar -> {

            //endpoint设置queue.direct.test2队列的消息处理逻辑
            SimpleRabbitListenerEndpoint endpoint = new SimpleRabbitListenerEndpoint();
            endpoint.setId("10");
            endpoint.setQueueNames("queue.direct.test2");
            endpoint.setMessageListener(new MessageListenerAdapter(new MessageHandler()));

            //注册endpoint
            registrar.registerEndpoint(endpoint);
        };
    }
}
