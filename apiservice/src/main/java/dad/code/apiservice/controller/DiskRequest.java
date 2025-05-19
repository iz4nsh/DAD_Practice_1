package dad.code.apiservice.controller;

public class DiskRequest {

    private float size;
    private String type;
    private long instanceId;

    public DiskRequest() {}

    public DiskRequest(float size, String type, long instanceId) {
        this.size = size;
        this.type = type;
        this.instanceId = instanceId;
    }

    public float getSize() { return size; }
    public void setSize(float size) { this.size = size; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getInstanceId() { return instanceId; }
    public void setInstanceId(long instanceId) { this.instanceId = instanceId; }
}