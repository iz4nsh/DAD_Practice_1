package dad.code.diskservice.listener;

import dad.code.diskservice.model.DiskRequest;
import dad.code.diskservice.model.DiskStatus;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class DiskRequestListener {

    private final RabbitTemplate rabbitTemplate;

    public DiskRequestListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "disk-requests")
    public void handleDiskRequest(DiskRequest req) {
        long id = req.getInstanceId();
        
        scheduleStatus(id, req, "REQUESTED", 0);
        scheduleStatus(id, req, "INITIALIZING", 5);
        scheduleStatus(id, req, "ASSIGNED", 15);
    }

    private void scheduleStatus(long id, DiskRequest req, String status, int delaySeconds) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            try {
                DiskStatus diskStatus = new DiskStatus();
                diskStatus.setId(id);
                diskStatus.setSize(req.getSize());
                diskStatus.setType(req.getType().toUpperCase());
                diskStatus.setStatus(status);
                diskStatus.setInstanceId(req.getInstanceId()); 

                rabbitTemplate.convertAndSend("disk-statuses", diskStatus);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

}
