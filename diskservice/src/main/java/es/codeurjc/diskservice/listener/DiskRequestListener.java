package es.codeurjc.diskservice.listener;

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

    public DiskRequestListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Ejemplo de generación de id en el listener

    @RabbitListener(queues = "disk-requests")
    public void handleDiskRequest(DiskRequest req) {
        long id = req.getInstanceId();
            // Simula los cambios de estado del disco
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
                diskStatus.setInstanceId(req.getInstanceId()); // ✅ este es el fix

                rabbitTemplate.convertAndSend("disk-statuses", diskStatus);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

}
