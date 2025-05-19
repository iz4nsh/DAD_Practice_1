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

    // Cambiado para usar ?id=x
    @GetMapping(params = "id")
    public ResponseEntity<Disk> getById(@RequestParam("id") Long id) {
        return diskRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DiskRequest> create(@RequestBody DiskRequest req) {
        if (req.getType() == null || req.getType().isBlank() || req.getSize() < 0 ) {
            return ResponseEntity.badRequest().body(null);
        }
        messagingService.sendDiskRequest(null, (int) req.getSize(), req.getType());
        // Devuelve el objeto recibido y 202 Accepted
        return ResponseEntity.accepted().body(req);
    }

    // Cambiado para usar ?id=x
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam("id") Long id) {
        return diskRepo.findById(id).map(disk -> {
            if (!"UNASSIGNED".equalsIgnoreCase(disk.getStatus())) {
                // Solo se puede eliminar si el disco está UNASSIGNED
                return ResponseEntity.status(409).body("Disk must be UNASSIGNED to be deleted.");
            }
            diskRepo.delete(disk);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
