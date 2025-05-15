package es.codeurjc.diskservice.model;

public class DiskRequest {
	
		private long id;
	    private float size;
	    private String type;

	    public DiskRequest() {}

	    public DiskRequest(long id, float size, String type) {
	    	this.id = id;
	        this.size = size;
	        this.type = type;
	    }

	    public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
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
