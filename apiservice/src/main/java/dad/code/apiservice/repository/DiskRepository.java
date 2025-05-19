package dad.code.apiservice.repository;

import dad.code.apiservice.model.Disk;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiskRepository extends JpaRepository<Disk, Long> {
    Optional<Disk> findByType(String type);
    // Agrega más métodos según tus necesidades, por ejemplo:
    // Optional<Disk> findByStatus(String status);
}
