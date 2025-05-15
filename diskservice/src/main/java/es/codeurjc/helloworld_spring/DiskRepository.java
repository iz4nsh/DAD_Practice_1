package es.codeurjc.helloworld_spring;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DiskRepository extends JpaRepository<Disk, Long> {

}
