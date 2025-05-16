package es.codeurjc.diskservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.codeurjc.diskservice.model.DiskRequest;
import es.codeurjc.diskservice.model.DiskStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DiskRequestListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public DiskRequestListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "disk-requests")
    public void handleDiskRequest(String json) {
        try {
            DiskRequest req = mapper.readValue(json, DiskRequest.class);

            scheduleStatus(req, "REQUESTED", 0);
            scheduleStatus(req, "INITIALIZING", 5);
            scheduleStatus(req, "ASSIGNED", 15);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleStatus(DiskRequest req, String status, int delaySeconds) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            try {
                DiskStatus diskStatus = new DiskStatus();
                diskStatus.setId(req.getId());
                diskStatus.setSize(req.getSize());
                diskStatus.setType(req.getType().toUpperCase());
                diskStatus.setStatus(status);

                String msg = mapper.writeValueAsString(diskStatus);
                rabbitTemplate.convertAndSend("disk-statuses", msg);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }
}
