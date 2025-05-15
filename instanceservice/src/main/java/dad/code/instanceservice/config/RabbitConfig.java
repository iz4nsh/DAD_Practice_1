package dad.code.instanceservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue instanceRequestsQueue() {
        return new Queue("instance-requests", true); // durable = true
    }

    @Bean
    public Queue instanceStatusesQueue() {
        return new Queue("instance-statuses", true);
    }
}
