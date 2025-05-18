package dad.code.apiservice.repository;

import dad.code.apiservice.model.Disk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiskRepository extends JpaRepository<Disk, Long> { 
}
