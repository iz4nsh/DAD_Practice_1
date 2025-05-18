package dad.code.apiservice.controller;

import dad.code.apiservice.model.Instance;
import dad.code.apiservice.repository.InstanceRepository;
import dad.code.apiservice.service.MessagingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/instances")

public class InstanceController {

    @Autowired private InstanceRepository instanceRepo;
    @Autowired private MessagingService messagingService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody InstanceRequest req){
        Instance instance = new Instance();
        instance.setName(req.getName());
        instance.setMemory(req.getMemory());
        instance.setCores(req.getCores());
        instance.setStatus("DISK_REQUESTED");
        instanceRepo.save(instance);
        messagingService.requestDisk(instance.getId(), req.getDiskSize(), req.getDiskType());

        URI location = URI.create("/instances/" + instance.getId());
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Instance instance = instanceRepo.findById(id).orElseThrow();
        if (instance.getDisk() != null) {
            instance.getDisk().setStatus("UNASSIGNED");
            instanceRepo.save(instance);
        }
        instanceRepo.delete(instance);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public Page<Instance> getAll(Pageable pageable){
        return instanceRepo.findAll(pageable);
    }
}
