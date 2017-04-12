package com.bbb.thirdparty.omniture.vo;

import java.sql.Timestamp;

public class OmnitureReportConfigVo implements Comparable<OmnitureReportConfigVo> {
	
	private String reportConfigId;
	
	private String methodType;
	
	private String reportType;
	
	private String concept;
	
	private String freq;
	
	private String cancelAllowed;
	
	private Integer priority;
	
	private Integer count;
	
	private Integer batchCount;
	
	private Integer numOfThread;
	
	private Integer thresholdDays;
	
	private Integer reportTimeRange;
	
	private Integer reportTimeRangeAdjustment;
	
	private Integer productCount;
	private Integer startWith;

	

	private Timestamp lastSuccessRunDate;
	
	private Timestamp lastModifiedDate;
	
	public int compareTo(OmnitureReportConfigVo  OmnitureReportConfigVo) {
		int comparePriority=0;
		int lPriority= 0;
		
		if(OmnitureReportConfigVo.getPriority() != null)
		{
			comparePriority =   OmnitureReportConfigVo.getPriority();
		}
		if(this.priority != null)
		{
			lPriority =   this.priority;
		}
		
		return lPriority - comparePriority;
	}

	public String getReportConfigId() {
		return reportConfigId;
	}

	public void setReportConfigId(String reportConfigId) {
		this.reportConfigId = reportConfigId;
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

	public String getFreq() {
		return freq;
	}

	public void setFreq(String freq) {
		this.freq = freq;
	}

	public String getCancelAllowed() {
		return cancelAllowed;
	}

	public void setCancelAllowed(String cancelAllowed) {
		this.cancelAllowed = cancelAllowed;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getBatchCount() {
		if (batchCount == null)
			return 0;
		return batchCount;
	}

	public void setBatchCount(Integer batchCount) {
		this.batchCount = batchCount;
	}

	public Integer getNumOfThread() {
		return numOfThread;
	}

	public void setNumOfThread(Integer numOfThread) {
		this.numOfThread = numOfThread;
	}

	public Integer getThresholdDays() {
		return thresholdDays;
	}

	public void setThresholdDays(Integer thresholdDays) {
		this.thresholdDays = thresholdDays;
	}

	public Integer getReportTimeRange() {
		if (reportTimeRange == null)
			return 0;
		return reportTimeRange;
	}

	public void setReportTimeRange(Integer reportTimeRange) {
		this.reportTimeRange = reportTimeRange;
	}

	public Integer getReportTimeRangeAdjustment() {
		if (reportTimeRangeAdjustment == null)
			return 0;
		return reportTimeRangeAdjustment;
	}

	public void setReportTimeRangeAdjustment(Integer reportTimeRangeAdjustment) {
		this.reportTimeRangeAdjustment = reportTimeRangeAdjustment;
	}

	public Integer getProductCount() {
		return productCount;
	}

	public void setProductCount(Integer productCount) {
		this.productCount = productCount;
	}

	public Timestamp getLastSuccessRunDate() {
		return lastSuccessRunDate;
	}

	public void setLastSuccessRunDate(Timestamp lastSuccessRunDate) {
		this.lastSuccessRunDate = lastSuccessRunDate;
	}

	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	public Integer getStartWith() {
		return startWith;
	}

	public void setStartWith(Integer startWith) {
		this.startWith = startWith;
	}	

}
