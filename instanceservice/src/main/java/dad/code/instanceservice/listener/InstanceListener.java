package dad.code.instanceservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import dad.code.instanceservice.model.InstanceRequest;
import dad.code.instanceservice.model.InstanceStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class InstanceListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public InstanceListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }



    private void scheduleStatus(String name, String status, String ip, int delaySeconds) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            try {
                InstanceStatus update = new InstanceStatus();
                update.setName(name);
                update.setStatus(status);
                update.setIp(ip);

                String msg = mapper.writeValueAsString(update);
                rabbitTemplate.convertAndSend("instance-statuses", msg);

                System.out.println("Published status '" + status + "' for instance '" + name + "'");

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    private String generateRandomIp() {
        Random r = new Random();
        return "192.168.1." + (2 + r.nextInt(253));
    }
}
