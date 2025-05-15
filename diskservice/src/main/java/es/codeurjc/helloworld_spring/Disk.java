package es.codeurjc.helloworld_spring;

import jakarta.persistence.*;

@Entity
public class Disk {
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private float size;
	
	@Enumerated(EnumType.STRING)
	private diskType type;
	
	@Enumerated(EnumType.STRING)
	private diskStatus status;
	
	public enum diskType{
		HDD, SSD
	}
	
	public enum diskStatus{
		REQUESTED, INITIALIZING, ASSIGNED, UNASSIGNED
	}

	public Disk() {
		super();
	}

	public Disk(float size, diskType type, diskStatus status) {
		super();
		this.size = size;
		this.type = type;
		this.status = status;
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

	public diskType getType() {
		return type;
	}

	public void setType(diskType type) {
		this.type = type;
	}

	public diskStatus getStatus() {
		return status;
	}

	public void setStatus(diskStatus status) {
		this.status = status;
	}
}
