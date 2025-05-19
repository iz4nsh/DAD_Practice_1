package dad.code.instanceservice.model;

import lombok.Data;

@Data
public class InstanceStatus {
    private Long id;
    private String name;
    private String status;
    private String ip;
}
