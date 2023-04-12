package com.project.graphviz.model;

import java.io.Serializable;

public class FileResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5045127561622843268L;

	private String id;

	private String type;

	private String name;
	private String url;

	public FileResponse() {

	}

	public FileResponse(String id, String type, String name, String url) {
		super();
		this.id = id;
		this.type = type;
		this.setUrl(url);
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
