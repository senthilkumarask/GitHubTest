package com.bbb.commerce.exim.bean;

import java.io.Serializable;
import java.util.List;


/**
 * @author sanam jain
 * The Class EximImagesVO.
 */
public class EximImagesVO implements Serializable {
	
	/** The id. */
	private String id;
	
	/** The description. */
	private String description;
	
	/** The previews. */
	private List<EximImagePreviewVO> previews;
	
	 /* (non-Javadoc)
 	 * @see java.lang.Object#toString()
 	 */
 	public String toString() {
	        final StringBuilder builder = new StringBuilder();
	        builder.append("EximImagesVO [id=").append(this.id)
	                        .append(", description=").append(this.description)
	                         .append(", previews=").append(this.getPreviews().toString())
	                        .append("]");
			return builder.toString();
	    }
	
	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Sets the description.
	 *
	 * @param description the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the previews.
	 *
	 * @return the previews
	 */
	public List<EximImagePreviewVO> getPreviews() {
		return previews;
	}
	
	/**
	 * Sets the previews.
	 *
	 * @param previews the new previews
	 */
	public void setPreviews(List<EximImagePreviewVO> previews) {
		this.previews = previews;
	}
}
