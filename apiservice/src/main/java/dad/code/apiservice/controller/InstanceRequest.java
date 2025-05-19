package dad.code.apiservice.controller;

public class InstanceRequest {
    private String name;
    private int memory;
    private int cores;
    private Integer diskSize;
    private String diskType;

    // Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMemory() { return memory; }
    public void setMemory(int memory) { this.memory = memory; }

    public int getCores() { return cores; }
    public void setCores(int cores) { this.cores = cores; }

    public Integer getDiskSize() { return diskSize; }
    public void setDiskSize(int diskSize) { this.diskSize = diskSize; }

    public String getDiskType() { return diskType; }
    public void setDiskType(String diskType) { this.diskType = diskType; }
}