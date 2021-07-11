package ru.akh.spring_amqp;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ru.akh.spring-amqp")
@ConstructorBinding
@Validated
public class ApplicationProperties {

    @NotEmpty
    private final String queue;

    private final String directExchange;

    private final String routingKey;

    public ApplicationProperties(String queue, String directExchange, String routingKey) {
        this.queue = queue;
        this.directExchange = directExchange;
        this.routingKey = routingKey;
    }

    public String getQueue() {
        return queue;
    }

    public String getDirectExchange() {
        return directExchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

}
