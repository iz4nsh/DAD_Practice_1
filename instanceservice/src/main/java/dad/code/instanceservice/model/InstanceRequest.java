package dad.code.instanceservice.model;

import lombok.Data;

@Data
public class InstanceRequest {
    private Long diskId;
    private String name;
    private int memory;
    private int cores;
}
