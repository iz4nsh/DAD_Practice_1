public interface InstanceRepository extends JpaRepository<Instance, Long> {
    Optional<Instance> findByName(String name);
}
