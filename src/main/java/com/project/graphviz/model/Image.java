package com.project.graphviz.model;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Image extends FileDb {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4185494982104811455L;
	private String parameters;
	@ManyToOne
	@JoinColumn(name = "graphviz_id", nullable = false)
	private GraphvizFile graphvizFile;

	public Image() {
		super();
	}

	public Image(String id, String name, String type, Long size, LocalDate createdAt, String parameters,
			GraphvizFile graphvizFile) {
		super(id, name, type, size, createdAt);
		this.parameters = parameters;
		this.graphvizFile = graphvizFile;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public GraphvizFile getGraphvizFile() {
		return graphvizFile;
	}

	public void setGraphvizFile(GraphvizFile graphvizFile) {
		this.graphvizFile = graphvizFile;
	}

}
