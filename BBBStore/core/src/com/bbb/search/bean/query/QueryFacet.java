/**
 * 
 */
package com.bbb.search.bean.query;

import java.io.Serializable;
import java.util.List;


/**
 * @author agoe21
 *
 */
public class QueryFacet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String mName;
	
	private List<String> mSelections;

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
	 * @return the selections
	 */
	public List<String> getSelections() {
		return mSelections;
	}

	/**
	 * @param pSelections the selections to set
	 */
	public void setSelections(final List<String> pSelections) {
		this.mSelections = pSelections;
	}
}
