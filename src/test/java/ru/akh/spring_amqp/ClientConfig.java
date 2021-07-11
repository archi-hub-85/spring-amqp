package ru.akh.spring_amqp;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.qpid.server.SystemLauncher;
import org.apache.qpid.server.model.SystemConfig;
import org.apache.qpid.server.store.MemorySystemConfigImpl;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AbstractDependsOnBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.rabbitmq.client.AMQP.Queue;
import com.rabbitmq.client.Channel;

import ru.akh.spring_amqp.sender.BookServiceClient;

@TestConfiguration
public class ClientConfig {

    @TestConfiguration
    @Profile("default")
    public static class DefaultConfig {

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

    }

    @TestConfiguration
    @Import(EmbeddedConfig.EmbeddedConnectionFactoryDependsOnBeanFactoryPostProcessor.class)
    @Profile("embedded")
    public static class EmbeddedConfig {

        @Bean(initMethod = "start", destroyMethod = "shutdown")
        public EmbeddedAMQPBroker embeddedAMQPBroker(@Value("classpath:qpid-config.json") URL configResource) {
            return new EmbeddedAMQPBroker(configResource.toExternalForm());
        }

        public static class EmbeddedAMQPBroker {

            private final SystemLauncher broker = new SystemLauncher();

            private final String configLocation;

            public EmbeddedAMQPBroker(String configLocation) {
                this.configLocation = configLocation;
            }

            public void start() throws Exception {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put(SystemConfig.TYPE, MemorySystemConfigImpl.SYSTEM_CONFIG_TYPE);
                attributes.put(SystemConfig.INITIAL_CONFIGURATION_LOCATION, configLocation);
                broker.startup(attributes);
            }

            public void shutdown() {
                broker.shutdown();
            }

        }

        @Order(Ordered.LOWEST_PRECEDENCE)
        public static class EmbeddedConnectionFactoryDependsOnBeanFactoryPostProcessor
                extends AbstractDependsOnBeanFactoryPostProcessor {

            public EmbeddedConnectionFactoryDependsOnBeanFactoryPostProcessor() {
                super(ConnectionFactory.class, EmbeddedAMQPBroker.class);
            }

        }

    }

    @Bean
    public BookServiceClient bookServiceClient(RabbitTemplate template) {
        return new BookServiceClient(template);
    }

}
