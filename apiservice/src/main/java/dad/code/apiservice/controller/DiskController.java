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
