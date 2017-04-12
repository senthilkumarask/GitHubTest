package com.bbb.order.bean;

import java.io.Serializable;
import java.util.Date;

public class SalesDataInfoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date startDate;
	private Date endDate;
	private Date lastStartDate;
	private Date lastEndDate;
	private boolean isIncrement;
	private boolean isDecrement;
	private int batchSize;
	private String rqlQuery;
	private int queryCount = 0;
	private String orderStates;
	private int totalQueryCount;
	private boolean isCleanUpData;
	private String rqlQueryWithRange;
	
	
	/**
	 * @return the rqlQueryWithRange
	 */
	public String getRqlQueryWithRange() {
	    return this.rqlQueryWithRange;
	}
	
	/**
	 * @param rqlQueryWithRange the rqlQueryWithRange to set
	 */
	public void setRqlQueryWithRange(String rqlQueryWithRange) {
	    this.rqlQueryWithRange = rqlQueryWithRange;
	}

	/**
	 * isCleanUpData flag
	 * @return
	 */
	public boolean getIsCleanUpData() {
	    return this.isCleanUpData;
	}
	
	/**
	 * @param isCleanUpData
	 */
	public void setIsCleanUpData(boolean isCleanUpData) {
	    this.isCleanUpData = isCleanUpData;
	}
	/**
	 * @return the totalQueryCount
	 */
	public int getTotalQueryCount() {
		return this.totalQueryCount;
	}
	/**
	 * @param totalQueryCount the totalQueryCount to set
	 */
	public void setTotalQueryCount(int totalQueryCount) {
		this.totalQueryCount = totalQueryCount;
	}
	/**
	 * @return the orderStates
	 */
	public String getOrderStates() {
		return this.orderStates;
	}
	/**
	 * @param orderStates the orderStates to set
	 */
	public void setOrderStates(String orderStates) {
		this.orderStates = orderStates;
	}
	/**
	 * @return the rqlQuery
	 */
	public String getRqlQuery() {
		return this.rqlQuery;
	}
	/**
	 * @param rqlQuery the rqlQuery to set
	 */
	public void setRqlQuery(String rqlQuery) {
		this.rqlQuery = rqlQuery;
	}

	/**
	 * @return the queryCount
	 */
	public int getQueryCount() {
		return this.queryCount;
	}
	/**
	 * @param queryCount the queryCount to set
	 */
	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

	/**
	 * @return the batchSize
	 */
	public int getBatchSize() {
		return this.batchSize;
	}
	/**
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return this.startDate;
	}
	/**
	 * @param startDate2 the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return this.endDate;
	}
	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	
	
	/**
	 * @return the lastStartDate
	 */
	public Date getLastStartDate() {
		return this.lastStartDate;
	}
	/**
	 * @param lastStartDate the lastStartDate to set
	 */
	public void setLastStartDate(Date lastStartDate) {
		this.lastStartDate = lastStartDate;
	}
	/**
	 * @return the lastEndDate
	 */
	public Date getLastEndDate() {
		return this.lastEndDate;
	}
	/**
	 * @param lastEndDate the lastEndDate to set
	 */
	public void setLastEndDate(Date lastEndDate) {
		this.lastEndDate = lastEndDate;
	}
	/**
	 * @return the isIncrement
	 */
	public boolean getIsIncrement() {
		return this.isIncrement;
	}
	/**
	 * @param isIncrement the isIncrement to set
	 */
	public void setIsIncrement(boolean isIncrement) {
		this.isIncrement = isIncrement;
	}
	/**
	 * @return the isDecrement
	 */
	public boolean getIsDecrement() {
		return this.isDecrement;
	}
	/**
	 * @param isDecrement the isDecrement to set
	 */
	public void setIsDecrement(boolean isDecrement) {
		this.isDecrement = isDecrement;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
	    
	    return new StringBuffer(
		    "startDate:" + this.startDate + "/n" + 
		    "endDate:" + this.endDate + "/n" +
		    "lastStartDate:" + this.lastStartDate + "/n" +
		    "lastEndDate:" + this.lastEndDate + "/n" +
		    "isIncrement:" + this.isIncrement + "/n" +
		    "isDecrement:" + this.isDecrement + "/n" +
		    "batchSize:" + this.batchSize + "/n" +
		    "rqlQuery:" + this.rqlQuery + "/n" +
		    "queryCount:" + this.queryCount + "/n" +
		    "orderStates:" + this.orderStates + "/n" +
		    "totalQueryCount:" + this.totalQueryCount + "/n" +
		    "isCleanUpData:" + this.isCleanUpData + "/n" +
		    "rqlQueryWithRange:" + this.rqlQueryWithRange + "/n"
		).toString();
	}
	
}
