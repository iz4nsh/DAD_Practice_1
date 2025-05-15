package es.codeurjc.helloworld_spring;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
	
	@Bean
    public Queue diskRequestsQueue() {
        return new Queue("disk-requests", true);
    }

    @Bean
    public Queue diskStatusesQueue() {
        return new Queue("disk-statuses", true);
    }

}
