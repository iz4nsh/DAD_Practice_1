package es.codeurjc.diskservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.codeurjc.diskservice.model.DiskRequest;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class DiskRequestListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public DiskRequestListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "disk-requests")
    public void handle(DiskRequest dto) {
        sendStatus(dto.getId(), dto.getSize(), dto.getType(), "REQUESTED");

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(() -> {
            sendStatus(dto.getId(), dto.getSize(), dto.getType(), "INITIALIZING");
        }, 5, TimeUnit.SECONDS);

        executor.schedule(() -> {
            sendStatus(dto.getId(), dto.getSize(), dto.getType(), "ASSIGNED");
        }, 15, TimeUnit.SECONDS);
    }

    private void sendStatus(Long id, float size, String type, String status) {
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                    "id", id,
                    "size", size,
                    "type", type.toUpperCase(),
                    "status", status));
            rabbitTemplate.convertAndSend("disk-statuses", message);
        } catch (Exception e) {
            System.err.println("Error al serializar estado del disco: " + e.getMessage());
        }
    }
}
