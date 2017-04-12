package com.bbb.sdd.vo.response;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class DeliveryWindowSddVOResponse.
 *
 * @author 
 */
public class DeliveryWindowSddVOResponse implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	private String id = "";
    
    /** The starts_at. */
    private String starts_at = "";
    
    /** The ends_at. */
    private String ends_at = "";
    
    /** The expires_at. */
    private String expires_at = "";
     
    	 
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
	 * Gets the starts_at.
	 *
	 * @return the starts_at
	 */
	public String getStarts_at() {
		return starts_at;
	}
	
	/**
	 * Sets the starts_at.
	 *
	 * @param starts_at the new starts_at
	 */
	public void setStarts_at(String starts_at) {
		this.starts_at = starts_at;
	}
	
	/**
	 * Gets the ends_at.
	 *
	 * @return the ends_at
	 */
	public String getEnds_at() {
		return ends_at;
	}
	
	/**
	 * Sets the ends_at.
	 *
	 * @param ends_at the new ends_at
	 */
	public void setEnds_at(String ends_at) {
		this.ends_at = ends_at;
	}
	
	/**
	 * Gets the expires_at.
	 *
	 * @return the expires_at
	 */
	public String getExpires_at() {
		return expires_at;
	}
	
	/**
	 * Sets the expires_at.
	 *
	 * @param expires_at the new expires_at
	 */
	public void setExpires_at(String expires_at) {
		this.expires_at = expires_at;
	}
		
         /* (non-Javadoc)
          * @see java.lang.Object#toString()
          */
         public String toString(){
     		
     		StringBuffer deliveryWindowSddVOResponse = new StringBuffer("");
     		deliveryWindowSddVOResponse.append("id: "+this.id + "|");
     		deliveryWindowSddVOResponse.append("starts_at: "+this.starts_at + "|");
     		deliveryWindowSddVOResponse.append("ends_at: "+this.ends_at + "|");
     		deliveryWindowSddVOResponse.append("expires_at: "+this.expires_at + "|");
     		return deliveryWindowSddVOResponse.toString();
     		
     	}
         
         
        		 

}