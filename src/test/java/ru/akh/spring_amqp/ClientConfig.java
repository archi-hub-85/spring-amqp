package ru.akh.spring_amqp;

import java.io.IOException;

import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;

import ru.akh.spring_amqp.sender.BookServiceClient;

@TestConfiguration
public class ClientConfig {

    @Bean
    public ConnectionFactory connectionFactory() throws IOException {
        ConnectionFactory factory = Mockito.mock(ConnectionFactory.class);
        Connection connection = Mockito.mock(Connection.class);
        Channel channel = Mockito.mock(Channel.class);

        Mockito.when(factory.createConnection()).thenReturn(connection);
        Mockito.when(connection.createChannel(Mockito.anyBoolean())).thenReturn(channel);
        Mockito.when(channel.isOpen()).thenReturn(true);

        Queue.DeclareOk declareOk = Mockito.mock(Queue.DeclareOk.class);
        Mockito.when(channel.queueDeclare(Mockito.anyString(), Mockito.anyBoolean(), Mockito.anyBoolean(),
                Mockito.anyBoolean(), Mockito.any())).thenReturn(declareOk);

        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, RabbitTemplateConfigurer configurer) {
        RabbitTemplate template = new TestRabbitTemplate(connectionFactory);
        configurer.configure(template, connectionFactory);
        return template;
    }

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate template) {
        return new RabbitAdmin(template);
    }

    @Bean
    public BookServiceClient bookServiceClient(RabbitTemplate template) {
        return new BookServiceClient(template);
    }

}
