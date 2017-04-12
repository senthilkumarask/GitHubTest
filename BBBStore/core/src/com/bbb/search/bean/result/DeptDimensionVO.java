/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;

/**
 * This class is a simple pojo used to set order of departments in 
 * type ahead search in mobile
 */

public class DeptDimensionVO implements Serializable{

	private static final long serialVersionUID = 1L;
	private String dimensionUrl;
	private String deptName;

	/**
	 * @return the dimensionUrl
	 */
	public String getDimensionUrl() {
		return dimensionUrl;
	}
	
	/**
	 * @param dimensionUrl the dimensionUrl to set
	 */
	public void setDimensionUrl(String dimensionUrl) {
		this.dimensionUrl = dimensionUrl;
	}
	
	/**
	 * @return the deptName
	 */
	public String getDeptName() {
		return deptName;
	}
	
	/**
	 * @param deptName the deptName to set
	 */
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
}
