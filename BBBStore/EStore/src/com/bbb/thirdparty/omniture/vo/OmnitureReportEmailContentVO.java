package com.bbb.thirdparty.omniture.vo;

import java.util.Date;

/**
 * Omniture Alert Email Content VO
 * @author Sapient
 *
 */
public class OmnitureReportEmailContentVO {
	
	/**
	 * Omniture Report ID
	 */
	private String reportId;
	
	/**
	 * Omniture Report Status [Success/Failed/Queued]
	 */
	private String opStatus;
	
	/**
	 * No of records updated
	 */
	private int recordsUpdated;
	
	/**
	 * No of search Terms
	 */
	private int noOfSearchTerms;
	/**
	 * Exception details in the case of operation failure
	 */
	private String exceptionDetails;
	/**
	 * Omniture Report type
	 */
	private String reportType;
	
	private String concept;
	
	
	private Date queuedDate;
	
	
	private String batchId;
	
	
	private String batchSeq;
	private String userId;
	private String reportSuiteId;
	private int recordsMoved;
	/**
	 * @return reportId
	 */
	public String getReportId() {
		return reportId;
	}
	
	/**
	 * Setter for reportId
	 * @param reportId
	 */
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	
	/**
	 * @return opStatus
	 */
	public String getOpStatus() {
		return opStatus;
	}
	
	/**
	 * Setter for opStatus
	 * @param opStatus
	 */
	public void setOpStatus(String opStatus) {
		this.opStatus = opStatus;
	}
	
	/**
	 * @return recordsUpdated
	 */
	public int getRecordsUpdated() {
		return recordsUpdated;
	}
	
	/**
	 * Setter for recordsUpdated
	 * @param recordsUpdated
	 */
	public void setRecordsUpdated(int recordsUpdated) {
		this.recordsUpdated = recordsUpdated;
	}
	
	/**
	 * @return exceptionDetails
	 */
	public String getExceptionDetails() {
		return exceptionDetails;
	}
	
	/**
	 * Setter for exceptionDetails
	 * @param exceptionDetails
	 */
	public void setExceptionDetails(String exceptionDetails) {
		this.exceptionDetails = exceptionDetails;
	}
	
	/**
	 * @return reportType
	 */
	public String getReportType() {
		return reportType;
	}

	/**
	 * Setter for omniture Report type
	 * @param reportType
	 */
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public Date getQueuedDate() {
		return queuedDate;
	}

	public void setQueuedDate(Date queuedDate) {
		this.queuedDate = queuedDate;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getBatchSeq() {
		return batchSeq;
	}

	public void setBatchSeq(String batchSeq) {
		this.batchSeq = batchSeq;
	}


	

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getNoOfSearchTerms() {
		return noOfSearchTerms;
	}

	public void setNoOfSearchTerms(int noOfSearchTerms) {
		this.noOfSearchTerms = noOfSearchTerms;
	}

	public String getReportSuiteId() {
		return reportSuiteId;
	}

	public void setReportSuiteId(String reportSuiteId) {
		this.reportSuiteId = reportSuiteId;
	}

	public int getRecordsMoved() {
		return recordsMoved;
	}

	public void setRecordsMoved(int recordsMoved) {
		this.recordsMoved = recordsMoved;
	}
}
