package dad.code.apiservice.controller;

import dad.code.apiservice.model.Instance;
import dad.code.apiservice.repository.InstanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;




@RestController
@RequestMapping("/api/instances")
public class InstanceController {

    @Autowired private InstanceRepository instanceRepo;

    @GetMapping
    public Page<Instance> getAll(Pageable pageable) {
        return instanceRepo.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Instance> getById(@PathVariable Long id) {
        return instanceRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Instance> create(@RequestBody Instance instance) {
        Instance saved = instanceRepo.save(instance);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Instance> update(@PathVariable Long id, @RequestBody Instance instance) {
        return instanceRepo.findById(id).map(existing -> {
            instance.setId(id);
            return ResponseEntity.ok(instanceRepo.save(instance));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return instanceRepo.findById(id).map(instance -> {
            instanceRepo.delete(instance);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}

