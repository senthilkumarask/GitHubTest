package com.bbb.search.bean.result;

import java.io.Serializable;

/**
 * @author dgoel7
 *
 */
public class SWSTermsVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** The name. */
	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** The removal query. */
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	/** The removal query. */
	private String removalValue;

	public String getRemovalValue() {
		return removalValue;
	}

	public void setRemovalValue(String removalValue) {
		this.removalValue = removalValue;
	}


}
