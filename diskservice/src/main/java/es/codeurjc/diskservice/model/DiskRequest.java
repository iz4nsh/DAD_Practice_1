package es.codeurjc.diskservice.model;

public class DiskRequest {

    private float size;
    private String type;

    public DiskRequest() {}

    public DiskRequest(float size, String type) {
        this.size = size;
        this.type = type;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
