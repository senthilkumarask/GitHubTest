package com.bbb.certona.vo;

import java.io.Serializable;

public class CollectionChildProductAttr implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String childProductId;
	private String childRollUpType;
	private boolean likeUnlike=false;
	/**
	 * @return the childProductId
	 */
	public String getChildProductId() {
		return childProductId;
	}
	/**
	 * @param childProductId the childProductId to set
	 */
	public void setChildProductId(String childProductId) {
		this.childProductId = childProductId;
	}
	/**
	 * @return the childRollUpType
	 */
	public String getChildRollUpType() {
		return childRollUpType;
	}
	/**
	 * @param childRollUpType the childRollUpType to set
	 */
	public void setChildRollUpType(String childRollUpType) {
		this.childRollUpType = childRollUpType;
	}
	/**
	 * @return the likeUnlike
	 */
	public boolean getLikeUnlike() {
		return likeUnlike;
	}
	/**
	 * @param likeUnlike the likeUnlike to set
	 */
	public void setLikeUnlike(boolean likeUnlike) {
		this.likeUnlike = likeUnlike;
	}
	
	public String toString(){
		StringBuffer toString=new StringBuffer();
		toString.append("Collection child product Id details ").append(this.childProductId).append(" \n")
		.append("childRollUpType ").append(this.childRollUpType).append(" \n").append("likeUnlike ").append(likeUnlike);
		return toString.toString();
	}
}
