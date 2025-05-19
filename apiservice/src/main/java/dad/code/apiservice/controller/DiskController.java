package dad.code.apiservice.controller;

import dad.code.apiservice.model.Disk;
import dad.code.apiservice.repository.DiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/disks")
public class DiskController {

    @Autowired private DiskRepository diskRepo;

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

    @PostMapping
    public ResponseEntity<Disk> create(@RequestBody Disk disk) {
        Disk saved = diskRepo.save(disk);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return diskRepo.findById(id).map(disk -> {
            // Verifica si el disco está asignado a alguna instancia sin usar getInstance()
            if (disk.getStatus() != null && disk.getStatus().equalsIgnoreCase("ASSIGNED")) {
                return ResponseEntity.status(409).build(); // Conflict: Disk is assigned to an instance
            }
            diskRepo.delete(disk);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
