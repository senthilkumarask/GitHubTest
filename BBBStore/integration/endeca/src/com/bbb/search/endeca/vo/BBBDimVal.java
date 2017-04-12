/**
 * 
 */
package com.bbb.search.endeca.vo;

import java.io.Serializable;
//import java.util.List;
//import java.util.Map;

//import com.endeca.navigation.DimensionList;


public class BBBDimVal implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String dimValName;
	private long dimValId;
	
	public String getDimValName() {
		return this.dimValName;
	}
	public void setDimValName(String dimValName) {
		this.dimValName = dimValName;
	}
	
	public long getDimValId() {
		return this.dimValId;
	}
	public void setDimValId(long dimValId) {
		this.dimValId = dimValId;
	}

	
	
	
	
}