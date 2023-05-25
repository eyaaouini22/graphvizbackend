package com.project.graphviz.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class GraphvizFile extends FileDb {

	private static final long serialVersionUID = 5155074018041220323L;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "graphvizFile")
	private List<Image> imageList;

	public GraphvizFile() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GraphvizFile(String id, String name, String type, Long size, LocalDate createdAt) {
		super(id, name, type, size, createdAt);
	}

	public List<Image> getImageList() {
		return imageList;
	}

	public void setImageList(List<Image> imageList) {
		this.imageList = imageList;
	}

}
