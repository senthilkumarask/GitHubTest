/**
 * 
 */
package com.bbb.search.endeca.vo;

import java.io.Serializable;
import java.util.List;


public class BBBQueryResults implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private List<BBBDimension> dimList;
	private boolean containsNavigation;
	
	public List<BBBDimension> getDimList() {
		return this.dimList;
	}
	public void setDimList(List<BBBDimension> dimList) {
		this.dimList = dimList;
	}
	
	public boolean isContainsNavigation() {
		return this.containsNavigation;
	}
	public void setContainsNavigation(boolean containsNavigation) {
		this.containsNavigation = containsNavigation;
	}
	
	
	
	
}