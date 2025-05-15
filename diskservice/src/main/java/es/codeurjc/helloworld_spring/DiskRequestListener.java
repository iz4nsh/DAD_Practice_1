package es.codeurjc.helloworld_spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Component
public class DiskRequestListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Long, Disk> diskStore = new ConcurrentHashMap<>();
    private long currentId = 1;

    public DiskRequestListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "disk-requests")
    public void handle(DiskRequestDto dto) {
        long id = generateId();
        Disk disk = new Disk(dto.getSize(),
                Disk.diskType.valueOf(dto.getType().toUpperCase()),
                Disk.diskStatus.REQUESTED);
        setDiskId(disk, id);  

        diskStore.put(id, disk);
        sendStatus(disk);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        executor.schedule(() -> {
            disk.setStatus(Disk.diskStatus.INITIALIZING);
            sendStatus(disk);
        }, 5, TimeUnit.SECONDS);

        executor.schedule(() -> {
            disk.setStatus(Disk.diskStatus.ASSIGNED);
            sendStatus(disk);
        }, 15, TimeUnit.SECONDS);
    }

    private synchronized long generateId() {
        return currentId++;
    }
  
    private void setDiskId(Disk disk, long id) {
        try {
            var field = Disk.class.getDeclaredField("id");
            field.setAccessible(true);
            field.setLong(disk, id);
        } catch (Exception e) {
            throw new RuntimeException("No se pudo asignar ID manualmente al Disk", e);
        }
    }

    private void sendStatus(Disk disk) {
        try {
            String message = objectMapper.writeValueAsString(Map.of(
                    "id", disk.getId(),
                    "size", disk.getSize(),
                    "type", disk.getType().toString(),
                    "status", disk.getStatus().toString()
            ));
            rabbitTemplate.convertAndSend("disk-statuses", message);
        } catch (JsonProcessingException e) {
            System.err.println("Error al serializar mensaje: " + e.getMessage());
        }
    }
}
