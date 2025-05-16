package es.codeurjc.diskservice.model;

public class DiskStatus {
	
	    private long id;
	    private float size;
		private String type;
		private String status;

	    public DiskStatus() {}

	    public DiskStatus(long id, float size, String type, String status) {
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

		public void setId(long id) {
			this.id = id;
		}
		public void setSize(float size) {
			this.size = size;
		}
		public void setType(String type) {
			this.type = type;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		
}
