package melodicdeath;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerEndpoint;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
        return rabbitTemplate;
    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        //默认的确认模式是AcknowledgeMode.AUTO
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);

        return factory;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames("queue.test");
        /**
         * 自动确认涉及到一个问题就是如果在消息消息的时候抛出异常，消息处理失败，但是因为自动确认而server将该消息删除了。
         * NONE表示自动确认
         */
        container.setAcknowledgeMode(AcknowledgeMode.NONE);
        container.setMessageListener(message -> {
            System.out.println("====接收到消息=====");
            System.out.println(new String(message.getBody()));

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //相当于自己的一些消费逻辑抛错误
            throw new NullPointerException("consumer fail");

        });

//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
//            System.out.println("====接收到消息=====");
//            System.out.println(new String(message.getBody()));
//            TimeUnit.SECONDS.sleep(1);
//            if (message.getMessageProperties().getHeaders().get("error") == null) {
//                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//                System.out.println("消息已经确认");
//            } else {
////                channel.basicNack(message.getMessageProperties().getDeliveryTag(),false,false);
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//                System.out.println("消息拒绝");
//            }
//
//        });


        /*
        * 如果消息成功被消费（成功的意思就是在消费的过程中没有抛出异常），则自动确认。
        1）当抛出AmqpRejectAndDontRequeueException异常的时候，则消息会被拒绝，且requeue=false（不重新入队列）
        2）当抛出ImmediateAcknowledgeAmqpException异常，则消费者会被确认
        3）其他的异常，则消息会被拒绝，且requeue=true（如果此时只有一个消费者监听该队列，则有发生死循环的风险，多消费端也会造成资源的极大浪费，这个在开发过程中一定要避免的）。可以通过setDefaultRequeueRejected（默认是true）去设置，
        * */

//        container.setAcknowledgeMode(AcknowledgeMode.AUTO);
//        container.setMessageListener((message) -> {
//            System.out.println("====接收到消息=====");
//            System.out.println(new String(message.getBody()));
//            //抛出NullPointerException异常则重新入队列
////            throw new NullPointerException("消息消费失败");
//            //当抛出的异常是AmqpRejectAndDontRequeueException异常的时候，则消息会被拒绝，且requeue=false
//            //throw new AmqpRejectAndDontRequeueException("消息消费失败");
//            //当抛出ImmediateAcknowledgeAmqpException异常，则消费者会被确认
//            throw new ImmediateAcknowledgeAmqpException("消息消费失败");
//
//        });

        return container;
    }
}
