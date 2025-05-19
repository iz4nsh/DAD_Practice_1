package dad.code.apiservice.model;

import jakarta.persistence.*;

@Entity
public class Instance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int memory;
    private int cores;
    private String ip;
    private String status;

    @ManyToOne
    @JoinColumn(name = "disk_id")
    private Disk disk;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMemory() { return memory; }
    public void setMemory(int memory) { this.memory = memory; }

    public int getCores() { return cores; }
    public void setCores(int cores) { this.cores = cores; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Disk getDisk() { return disk; }
    public void setDisk(Disk disk) { this.disk = disk; }
}
