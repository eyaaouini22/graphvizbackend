package com.project.graphviz.model;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class FileDb implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6411527560746655850L;
	@Id
	private String id;
	private String name;
	private String type;
	private Long size;
	private LocalDate createdAt;

	public FileDb() {
	}

	public FileDb(String id, String name, String type, Long size, LocalDate createdAt) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.size = size;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public LocalDate getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}

}
