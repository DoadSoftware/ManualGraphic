package com.manual.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="container")
@XmlAccessorType(XmlAccessType.FIELD)
public class Container implements Comparable<Container> {
	
	@XmlElement(name = "container_id")
	private int container_id;
	
	@XmlElement(name = "container_key")
	private String container_key;
	  
	@XmlElement(name = "container_value")
	private String container_value;

	public Container(int container_id, String container_key, String container_value) {
		super();
		this.container_id = container_id;
		this.container_value = container_value;
		this.container_key = container_key;
	}
	
	public Container() {
		super();
	}

	public int getContainer_id() {
		return container_id;
	}

	public void setContainer_id(int container_id) {
		this.container_id = container_id;
	}

	public String getContainer_key() {
		return container_key;
	}

	public void setContainer_key(String container_key) {
		this.container_key = container_key;
	}

	public String getContainer_value() {
		return container_value;
	}

	public void setContainer_value(String container_value) {
		this.container_value = container_value;
	}
	
	@Override
	public int compareTo(Container con) {
		return (int) (this.getContainer_id()-con.getContainer_id());
	}
}
