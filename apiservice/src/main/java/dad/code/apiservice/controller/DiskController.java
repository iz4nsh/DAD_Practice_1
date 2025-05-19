package dad.code.apiservice.controller;

import dad.code.apiservice.model.Disk;
import dad.code.apiservice.repository.DiskRepository;
import dad.code.apiservice.service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/disks")
public class DiskController {

    @Autowired private DiskRepository diskRepo;
    @Autowired private MessagingService messagingService;

    @GetMapping
    public Page<Disk> getAll(Pageable pageable) {
        return diskRepo.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Disk> getById(@PathVariable Long id) {
        return diskRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // SOLO ENVÍA LA SOLICITUD AL BROKER, NO GUARDA EN LA BASE DE DATOS
    @PostMapping
    public ResponseEntity<DiskRequest> create(@RequestBody DiskRequest req) {
        messagingService.sendDiskRequest(null, (int) req.getSize(), req.getType());
        // Devuelve el objeto recibido y 202 Accepted
        return ResponseEntity.accepted().body(req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return diskRepo.findById(id).map(disk -> {
            if ("ASSIGNED".equalsIgnoreCase(disk.getStatus())) {
                return ResponseEntity.status(409).build(); // Conflict: Disk is assigned to an instance
            }
            diskRepo.delete(disk);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
