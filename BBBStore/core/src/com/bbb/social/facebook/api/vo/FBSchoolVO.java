package com.bbb.social.facebook.api.vo;

import java.io.Serializable;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class FBSchoolVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String id;

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	private String type;

	/**
	 * 
	 */
	private String year;

	/**
	 * 
	 */
	private String degree;

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public String getYear() {
		return year;
	}

	/**
	 * 
	 * @param year
	 */
	public void setYear(String year) {
		this.year = year;
	}

	/**
	 * 
	 * @return
	 */
	public String getDegree() {
		return degree;
	}

	/**
	 * 
	 * @param degree
	 */
	public void setDegree(String degree) {
		this.degree = degree;
	}

}
