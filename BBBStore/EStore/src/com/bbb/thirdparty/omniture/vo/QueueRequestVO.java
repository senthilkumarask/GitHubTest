package com.bbb.thirdparty.omniture.vo;

public class QueueRequestVO {
	private String batchId;
	private Integer count;
	private int reportIndex;
	private int startingWith;
	private int top;
	private String concept;
	private String reportType;
	private String methodType;
	private boolean batchRequest;
	private Integer batchSize;	
	private String dateFrom;
	private String dateTo;
	
	private OmnitureReportConfigVo omnitureReportConfigVo;
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
	 * @return the reportIndex
	 */
	public int getReportIndex() {
		return reportIndex;
	}
	/**
	 * @param reportIndex the reportIndex to set
	 */
	public void setReportIndex(int reportIndex) {
		this.reportIndex = reportIndex;
	}
	/**
	 * @return the startingWith
	 */
	public int getStartingWith() {
		return startingWith;
	}
	/**
	 * @param startingWith the startingWith to set
	 */
	public void setStartingWith(int startingWith) {
		this.startingWith = startingWith;
	}
	/**
	 * @return the top
	 */
	public int getTop() {
		return top;
	}
	/**
	 * @param top the top to set
	 */
	public void setTop(int top) {
		this.top = top;
	}
	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}
	/**
	 * @return the reportType
	 */
	public String getReportType() {
		return reportType;
	}
	/**
	 * @param reportType the reportType to set
	 */
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	/**
	 * @return the methodType
	 */
	public String getMethodType() {
		return methodType;
	}
	/**
	 * @param methodType the methodType to set
	 */
	public void setMethodType(String methodType) {
		this.methodType = methodType;
	}
	/**
	 * @return the batchRequest
	 */
	public boolean isBatchRequest() {
		return batchRequest;
	}
	/**
	 * @param batchRequest the batchRequest to set
	 */
	public void setBatchRequest(boolean batchRequest) {
		this.batchRequest = batchRequest;
	}
	/**
	 * @return the omnitureReportConfigVo
	 */
	public OmnitureReportConfigVo getOmnitureReportConfigVo() {
		return omnitureReportConfigVo;
	}
	/**
	 * @param omnitureReportConfigVo the omnitureReportConfigVo to set
	 */
	public void setOmnitureReportConfigVo(
			OmnitureReportConfigVo omnitureReportConfigVo) {
		this.omnitureReportConfigVo = omnitureReportConfigVo;
	}
	
	
	/**
	 * @return the batchSize
	 */
	public Integer getBatchSize() {
		return batchSize;
	}
	/**
	 * @param batchSize the batchSize to set
	 */
	public void setBatchSize(Integer batchSize) {
		this.batchSize = batchSize;
	}
	
	public String getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}
	public String getDateTo() {
		return dateTo;
	}
	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}
	@Override
	public String toString() {
		return "QueueRequestVO [batchId=" + batchId + ", count=" + count
				+ ", reportIndex=" + reportIndex + ", startingWith="
				+ startingWith + ", top=" + top + ", concept=" + concept
				+ ", reportType=" + reportType + ", methodType=" + methodType
				+ ", batchRequest=" + batchRequest + ", batchSize=" + batchSize
				+ ", dateFrom=" + dateFrom + ", dateTo=" + dateTo
				+ ", omnitureReportConfigVo=" + omnitureReportConfigVo + "]";
	}
	 
	
	
	
}
