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

    public void requestInstance(Instance instance){
        Map<String, Object> message = new HashMap<>();
        message.put("diskId", instance.getDisk().getId());
        message.put("name", instance.getName());
        message.put("memory", instance.getMemory());
        message.put("cores", instance.getCores());
        rabbitTemplate.convertAndSend("instance-requests", msg);
    }
}
