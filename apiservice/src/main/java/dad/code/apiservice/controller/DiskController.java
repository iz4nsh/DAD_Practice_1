package dad.code.apiservice.controller;

import dad.code.apiservice.model.Disk;
import dad.code.apiservice.repository.DiskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/disks")
public class DiskController {

    @Autowired private DiskRepository diskRepo;

    @GetMapping
    public Page<Disk> getAll(Pageable pageable) {
        return diskRepo.findAll(pageable);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        Disk disk = diskRepo.findById(id).orElseThrow();
        if(!"UNASSIGNED".equals(disk.getStatus())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Disk must be UNASSIGNED to delete.");
        }
        
        diskRepo.delete(disk);
        return ResponseEntity.noContent().build();
    }
}
