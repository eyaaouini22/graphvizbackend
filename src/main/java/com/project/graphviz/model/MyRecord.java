package com.project.graphviz.model;

import java.io.Serializable;

public class MyRecord implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String property1;
	private String property2;

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}
}
