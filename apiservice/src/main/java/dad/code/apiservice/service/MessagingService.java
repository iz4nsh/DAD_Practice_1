package dad.code.apiservice.service;

import dad.code.apiservice.controller.DiskRequest;
import dad.code.apiservice.model.Instance;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class MessagingService {
    @Autowired private RabbitTemplate rabbitTemplate;

public void sendDiskRequest(Long instanceId, int size, String type){
    if (type == null || type.isBlank()) throw new IllegalArgumentException("El tipo de disco no puede ser null o vacío");
    if (instanceId == null) throw new IllegalArgumentException("El instanceId no puede ser null");

    DiskRequest request = new DiskRequest(size, type, instanceId);
    rabbitTemplate.convertAndSend("disk-requests", request);
}


    public void requestInstance(Instance instance){
        Map<String, Object> message = new HashMap<>();
        message.put("id", instance.getId());
        message.put("diskId", instance.getDisk().getId());
        message.put("name", instance.getName());
        message.put("memory", instance.getMemory());
        message.put("cores", instance.getCores());
        rabbitTemplate.convertAndSend("instance-requests", message);
    }
}
