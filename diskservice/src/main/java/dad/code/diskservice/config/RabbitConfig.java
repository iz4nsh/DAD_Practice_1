package dad.code.diskservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

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

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
