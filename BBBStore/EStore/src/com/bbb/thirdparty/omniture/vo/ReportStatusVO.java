package com.bbb.thirdparty.omniture.vo;

import java.sql.Timestamp;
import java.util.Date;

public class ReportStatusVO {
	
	private Long id;
	private String reportId;
	private String methodType;
	private String reportType;
	private String concept;
	private Date queuedDate;
	private Timestamp reportGetTime1;
	private Timestamp reportGetTime2;
	private Double reportExecutionTime;
	private Double reportProcessingFinishTime;
	private String reportOperationStatus;
	private String errorCode;
	private String errorDescription;
	private Integer accessAttempts;
	private String batchId;
	private Integer batchSeq;
	private Integer count;
	private Integer rangeFrom;
	private Integer rangeTo;
	private Integer attempts;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public String getMethodType() {
		return methodType;
	}
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	public String getReportType() {
		return reportType;
	}
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
	public Timestamp getReportGetTime1() {
		return reportGetTime1;
	}
	public void setReportGetTime1(Timestamp reportGetTime1) {
		this.reportGetTime1 = reportGetTime1;
	}
	public Timestamp getReportGetTime2() {
		return reportGetTime2;
	}
	public void setReportGetTime2(Timestamp reportGetTime2) {
		this.reportGetTime2 = reportGetTime2;
	}
	public Double getReportExecutionTime() {
		return reportExecutionTime;
	}
	public void setReportExecutionTime(Double reportExecutionTime) {
		this.reportExecutionTime = reportExecutionTime;
	}
	public Double getReportProcessingFinishTime() {
		return reportProcessingFinishTime;
	}
	public void setReportProcessingFinishTime(Double reportProcessingFinishTime) {
		this.reportProcessingFinishTime = reportProcessingFinishTime;
	}
	public String getReportOperationStatus() {
		return reportOperationStatus;
	}
	public void setReportOperationStatus(String reportOperationStatus) {
		this.reportOperationStatus = reportOperationStatus;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public Integer getAccessAttempts() {
		return accessAttempts;
	}
	public void setAccessAttempts(Integer accessAttempts) {
		this.accessAttempts = accessAttempts;
	}
	/**
	 * @return the batchId
	 */
	public String getBatchId() {
		return batchId;
	}
	/**
	 * @param batchId the batchId to set
	 */
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	/**
	 * @return the batchSeq
	 */
	public Integer getBatchSeq() {
		return batchSeq;
	}
	/**
	 * @param batchSeq the batchSeq to set
	 */
	public void setBatchSeq(Integer batchSeq) {
		this.batchSeq = batchSeq;
	}
	/**
	 * @return the count
	 */
	public Integer getCount() {
		return count;
	}
	/**
	 * @param count the count to set
	 */
	public void setCount(Integer count) {
		this.count = count;
	}
	/**
	 * @return the rangeFrom
	 */
	public Integer getRangeFrom() {
		return rangeFrom;
	}
	/**
	 * @param rangeFrom the rangeFrom to set
	 */
	public void setRangeFrom(Integer rangeFrom) {
		this.rangeFrom = rangeFrom;
	}
	/**
	 * @return the rangeTo
	 */
	public Integer getRangeTo() {
		return rangeTo;
	}
	/**
	 * @param rangeTo the rangeTo to set
	 */
	public void setRangeTo(Integer rangeTo) {
		this.rangeTo = rangeTo;
	}
	/**
	 * @return the attempts
	 */
	public Integer getAttempts() {
		return attempts;
	}
	/**
	 * @param attempts the attempts to set
	 */
	public void setAttempts(Integer attempts) {
		this.attempts = attempts;
	}
	
	
	
	
	

}
