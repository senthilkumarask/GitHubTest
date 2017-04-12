package com.bbb.certona.vo;

import java.sql.Timestamp;
import java.util.List;

import com.bbb.commerce.catalog.vo.CategoryVO;

public class CertonaCategoryVO extends CategoryVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<String> subCatIds;
	private List<String> assocSites;
	private String nodeType;
	private Timestamp creationDate;
	private String thumbNailPath;
	private String shortDesc;
	private String longDesc;
	private boolean isActive=false;
	/**
	 * @return the subCatIds
	 */
	public List<String> getSubCatIds() {
		return subCatIds;
	}
	/**
	 * @param subCatIds the subCatIds to set
	 */
	public void setSubCatIds(List<String> subCatIds) {
		this.subCatIds = subCatIds;
	}
	/**
	 * @return the assocSites
	 */
	public List<String> getAssocSites() {
		return assocSites;
	}
	/**
	 * @param assocSites the assocSites to set
	 */
	public void setAssocSites(List<String> assocSites) {
		this.assocSites = assocSites;
	}
	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}
	/**
	 * @param nodeType the nodeType to set
	 */
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	/**
	 * @return the thumbNailPath
	 */
	public String getThumbNailPath() {
		return thumbNailPath;
	}
	/**
	 * @param thumbNailPath the thumbNailPath to set
	 */
	public void setThumbNailPath(String thumbNailPath) {
		this.thumbNailPath = thumbNailPath;
	}
	/**
	 * @return the shortDesc
	 */
	public String getShortDesc() {
		return shortDesc;
	}
	/**
	 * @param shortDesc the shortDesc to set
	 */
	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}
	/**
	 * @return the longDesc
	 */
	public String getLongDesc() {
		return longDesc;
	}
	/**
	 * @param longDesc the longDesc to set
	 */
	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}
	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}
	/**
	 * @param isActive the isActive to set
	 */
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	/**
	 * @return the creationDate
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}
	
}
