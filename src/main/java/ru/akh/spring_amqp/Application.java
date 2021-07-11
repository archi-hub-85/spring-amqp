package ru.akh.spring_amqp;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.MarshallingMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.util.MimeTypeUtils;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    // @EnableRabbit
    public static class AmqpConfig {

        @Autowired
        private ApplicationProperties appProperties;

        @Bean
        public Queue queue() {
            return new Queue(appProperties.getQueue());
        }

        @Bean
        public DirectExchange directExchange() {
            return new DirectExchange(appProperties.getDirectExchange());
        }

        @Bean
        public Binding binding(Queue queue, DirectExchange directExchange) {
            return BindingBuilder.bind(queue).to(directExchange).with(appProperties.getRoutingKey());
        }

        @Bean
        public MessageConverter messageConverter(@Value("classpath:book.xsd") Resource schemaResource) {
            Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
            marshaller.setContextPath("ru.akh.spring_amqp.schema");
            marshaller.setSchema(schemaResource);

            MarshallingMessageConverter messageConverter = new MarshallingMessageConverter(marshaller);
            messageConverter.setContentType(MimeTypeUtils.TEXT_XML_VALUE);
            return messageConverter;
        }

    }

}
