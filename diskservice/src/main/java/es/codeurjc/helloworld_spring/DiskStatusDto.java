package es.codeurjc.helloworld_spring;

public class DiskStatusDto {
	
	    private long id;
	    private float size;
		private String type;
		private String status;

	    public DiskStatusDto() {}

	    public DiskStatusDto(long id, float size, String type, String status) {
	        this.id = id;
	        this.size = size;
	        this.type = type;
	        this.status = status;
	    }

	    public long getId() {
	        return id;
	    }

	    public float getSize() {
	        return size;
	    }

	    public String getType() {
	        return type;
	    }

	    public String getStatus() {
	        return status;
	    }
}
