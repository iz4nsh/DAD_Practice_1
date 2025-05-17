@Service
public class MessagingService {
    @Autowired private RabbitTemplate rabbitTemplate;

    public void sendDiskRequest(Long instanceId, int size, String type){
        Map<String, Object> message = new HashMap<>();
        message.put("instanceId", instanceId);
        message.put("size", size);
        message.put("type", type);
        rabbitTemplate.convertAndSend("disk-requests", message);
    }

    public void sendInstanceRequest(Long instanceId, Long diskId, String name, int memory, int cores) {
        Map<String, Object> message = new HashMap<>();
        message.put("id", instanceId);
        message.put("diskId", diskId);
        message.put("name", name);
        message.put("memory", memory);
        message.put("cores", cores);
        rabbitTemplate.convertAndSend("instance-requests", message);
    }
}
