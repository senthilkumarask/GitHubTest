/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.Map;

/**
 * @author agoe21
 *
 */
public class SearchResult implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long mResultId;
	
	private String mTitle;
	
	private String mDescription;
	
	private Map<String,String> mFields;

	/**
	 * @return the resultId
	 */
	public long getResultId() {
		return mResultId;
	}

	/**
	 * @param pResultId the resultId to set
	 */
	public void setResultId(final long pResultId) {
		this.mResultId = pResultId;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param pTitle the title to set
	 */
	public void setTitle(final String pTitle) {
		this.mTitle = pTitle;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return mDescription;
	}

	/**
	 * @param pDescription the description to set
	 */
	public void setDescription(final String pDescription) {
		this.mDescription = pDescription;
	}

	/**
	 * @return the fields
	 */
	public Map<String, String> getFields() {
		return mFields;
	}

	/**
	 * @param pFields the fields to set
	 */
	public void setFields(final Map<String, String> pFields) {
		this.mFields = pFields;
	}
}
