package dad.code.apiservice.service;

import dad.code.apiservice.model.Disk;
import dad.code.apiservice.model.Instance;
import dad.code.apiservice.repository.DiskRepository;
import dad.code.apiservice.repository.InstanceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ListenerService {

    @Autowired private DiskRepository diskRepo;
    @Autowired private InstanceRepository instanceRepo;
    @Autowired private MessagingService messagingService;

    @RabbitListener(queues = "disk-statuses")
    public void handleDiskStatus(Map<String, Object> msg){
        Long instanceId = ((Number) msg.get("instanceId")).longValue();
        Long id = ((Number) msg.get("id")).longValue();
        String status = (String) msg.get("status");
        Disk disk = diskRepo.findById(diskId).orElse(new Disk());

        disk.setId(diskId);
        disk.setStatus(status);
        disk.setSize((Integer) msg.get("size"));
        disk.setType((String) msg.get("type"));
        diskRepo.save(disk);

        if("ASSIGNED".equals(status)){
            Instance instance = instanceRepo.findById(instanceId).orElseThrow();
            instance.setDisk(disk);
            instanceRepo.save(instance);
            messagingService.sendInstanceRequest(instance.getId(), diskId, instance.getName(), instance.getMemory(), instance.getCores());
        }
    }

    @RabbitListener(queues = "instance-statuses")
    public void handleInstanceStatus(Map<String, Object> msg){
        String name = ((String) msg.get("name"));
        String status = (String) msg.get("status");
        String ip = (String) msg.getOrDefault("ip", null);

        Instance instance = instanceRepo.findByName(name).orElseThrow(()-> new RuntimeException("Instance not found with name: " + name));
        instance.setStatus(status);

        if (ip != null) instance.setIp(ip);
        instanceRepo.save(instance);
    }
}
