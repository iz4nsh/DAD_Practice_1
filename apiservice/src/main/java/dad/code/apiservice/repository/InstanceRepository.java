package dad.code.apiservice.repository;

import dad.code.apiservice.model.Instance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InstanceRepository extends JpaRepository<Instance, Long> {
    Optional<Instance> findByName(String name);
}
