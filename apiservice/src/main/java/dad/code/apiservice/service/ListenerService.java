package dad.code.apiservice.service;

import dad.code.apiservice.model.Disk;
import dad.code.apiservice.model.Instance;
import dad.code.apiservice.repository.DiskRepository;
import dad.code.apiservice.repository.InstanceRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class ListenerService {

    @Autowired
    private DiskRepository diskRepo;

    @Autowired
    private InstanceRepository instanceRepo;

    @Autowired
    private MessagingService messagingService;

    @RabbitListener(queues = "disk-statuses")
    public void handleDiskStatus(Map<String, Object> msg) {
        try {
            if (msg.get("id") == null || msg.get("status") == null) {
                System.err.println("Mensaje de disco inválido: " + msg);
                return;
            }

            Long diskId = ((Number) msg.get("id")).longValue();
            String status = (String) msg.get("status");
            Number sizeNum = (Number) msg.get("size");
            Float size = sizeNum != null ? sizeNum.floatValue() : null;
            String type = (String) msg.get("type");

            Disk disk;
            Optional<Disk> diskOpt = diskRepo.findById(diskId);
            if (diskOpt.isEmpty()) {
                // Crear el disco si no existe
                disk = new Disk();
                if (size != null) disk.setSize(size);
                if (type != null) disk.setType(type);
                disk.setStatus(status);
                try {
                    diskRepo.save(disk);
                    System.out.println("Disk creado: " + disk);
                } catch (Exception ex) {
                    System.err.println("No se pudo crear el disco (posible conflicto): " + ex.getMessage());
                    return;
                }
            } else {
                // Recarga el disco antes de modificarlo
                disk = diskRepo.findById(diskId).orElse(null);
                if (disk == null) {
                    System.err.println("El disco fue eliminado antes de poder actualizarlo: " + diskId);
                    return;
                }
                if (size != null) disk.setSize(size);
                if (type != null) disk.setType(type);
                disk.setStatus(status);
                try {
                    diskRepo.save(disk);
                    System.out.println("Disk actualizado: " + disk);
                } catch (Exception ex) {
                    System.err.println("No se pudo actualizar el disco (posible conflicto): " + ex.getMessage());
                    return;
                }
            }

            if ("ASSIGNED".equalsIgnoreCase(status)) {
                Object instanceIdObj = msg.get("instanceId");
                if (instanceIdObj == null) {
                    System.err.println("Falta instanceId para asignar el disco " + diskId);
                    return;
                }

                Long instanceId = ((Number) instanceIdObj).longValue();
                Optional<Instance> optionalInstance = instanceRepo.findById(instanceId);
                if (optionalInstance.isEmpty()) {
                    System.err.println("Instancia no encontrada con ID: " + instanceId);
                    return;
                }

                Instance instance = optionalInstance.get();
                instance.setDisk(disk); // Asocia el disco real
                instanceRepo.save(instance);

                // Ahora sí, solicita la creación de la instancia al instance-service
                messagingService.requestInstance(instance);
            }

        } catch (Exception e) {
            System.err.println("Error al procesar disk-status: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "instance-statuses")
    public void handleInstanceStatus(Map<String, Object> msg) {
        try {
            // Cambia a buscar por id, no por name
            Long id = msg.get("id") != null ? ((Number) msg.get("id")).longValue() : null;
            String status = (String) msg.get("status");
            String ip = (String) msg.getOrDefault("ip", null);

            if (id == null || status == null) {
                System.err.println("Mensaje de instancia inválido: " + msg);
                return;
            }

            Optional<Instance> optionalInstance = instanceRepo.findById(id);
            if (optionalInstance.isEmpty()) {
                System.err.println("Instancia no encontrada con id: " + id);
                return;
            }

            Instance instance = optionalInstance.get();
            instance.setStatus(status);
            if (ip != null) instance.setIp(ip);

            instanceRepo.save(instance);

            System.out.println("Instancia actualizada: " + id + " -> " + status + (ip != null ? " @ " + ip : ""));

        } catch (Exception e) {
            System.err.println("Error al procesar instance-status: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

