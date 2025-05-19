package dad.code.apiservice.controller;

import dad.code.apiservice.model.Disk;
import dad.code.apiservice.model.Instance;
import dad.code.apiservice.repository.DiskRepository;
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

    private final DiskRepository diskRepository;

    @Autowired
    private InstanceRepository instanceRepo;
    @Autowired
    private MessagingService messagingService;

    InstanceController(DiskRepository diskRepository) {
        this.diskRepository = diskRepository;
    }

    // Mostrar todos los servidores creados (paginado)
    @GetMapping
    public Page<Instance> getAll(Pageable pageable) {
        return instanceRepo.findAll(pageable);
    }

    // Mostrar una instancia por id usando query param ?id=x
    @GetMapping(params = "id")
    public ResponseEntity<Instance> getById(@RequestParam("id") Long id) {
        return instanceRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Crear instancia (solo envía la solicitud al broker, no guarda en la base de
    // datos)
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

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("id") Long id) {
        Optional<Instance> instanceOpt = instanceRepo.findById(id);
        if (instanceOpt.isEmpty()) {
            System.out.println("Intento de borrar instancia no existente con id: " + id);
            return ResponseEntity.notFound().build();
        }

        Instance instance = instanceOpt.get();
        Disk disk = instance.getDisk();

        if (disk != null) {
            System.out.println("Desasignando disco " + disk.getId() + " de la instancia " + id);
            disk.setStatus("UNASSIGNED");
            instance.setDisk(null);
            this.diskRepository.save(disk);
            System.out.println("Disco " + disk.getId() + " puesto en estado UNASSIGNED");
        } else {
            System.out.println("La instancia " + id + " no tiene disco asociado.");
        }

        instanceRepo.delete(instance);
        System.out.println("Instancia " + id + " eliminada correctamente.");

        return ResponseEntity.noContent().build();
    }

}
