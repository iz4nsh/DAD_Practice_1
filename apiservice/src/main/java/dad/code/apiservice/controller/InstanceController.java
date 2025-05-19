package dad.code.apiservice.controller;

import dad.code.apiservice.model.Instance;
import dad.code.apiservice.repository.InstanceRepository;
import dad.code.apiservice.service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/instances")
public class InstanceController {

    @Autowired private InstanceRepository instanceRepo;
    @Autowired private MessagingService messagingService;

    // Mostrar todos los servidores creados (paginado)
    @GetMapping
    public Page<Instance> getAll(Pageable pageable) {
        return instanceRepo.findAll(pageable);
    }

    // Mostrar una instancia por id
    @GetMapping("/{id}")
    public ResponseEntity<Instance> getById(@PathVariable Long id) {
        return instanceRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear instancia (solo envía la solicitud al broker, no guarda en la base de datos)
    @PostMapping
    public ResponseEntity<Instance> create(@RequestBody InstanceRequest req) {
        if (req.getDiskType() == null || req.getDiskType().isBlank() || req.getDiskSize() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        Instance instance = new Instance();
        instance.setName(req.getName());
        instance.setMemory(req.getMemory());
        instance.setCores(req.getCores());
        instance.setStatus("BUILDING_DISK");
        instance = instanceRepo.save(instance); // Aquí se genera el id
        messagingService.sendDiskRequest(instance.getId(), req.getDiskSize(), req.getDiskType());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(instance.getId())
                .toUri();
        return ResponseEntity.created(location).body(instance);
    }

    // Eliminar instancia (solo envía la solicitud al broker, el ListenerService actualizará el disco)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Instance> instanceOpt = instanceRepo.findById(id);
        if (instanceOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        // Aquí podrías enviar un mensaje al broker para eliminar la instancia si tu arquitectura lo requiere
        // Por ahora, solo respondemos 202 Accepted para indicar que la operación es asíncrona
        return ResponseEntity.accepted().build();
    }
}

