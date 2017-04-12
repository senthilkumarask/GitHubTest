/**
 * 
 */
package com.bbb.search.bean.query;

import java.io.Serializable;

/**
 * @author agoe21
 *
 */
public class AssetType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mName;
	private int mValue;

	/**
	 * @return the name
	 */
	public String getName() {
		return mName;
	}
	
	/**
	 * @param pName the name to set
	 */
	public void setName(final String pName) {
		this.mName = pName;
	}
	
	/**
	 * @return the value
	 */
	public int getValue() {
		return mValue;
	}
	
	/**
	 * @param pValue the value to set
	 */
	public void setValue(final int pValue) {
		this.mValue = pValue;
	}
}
