package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;
import java.util.List;

public class ReportDescriptionVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4950211752256132869L;
	private String reportSuiteID;
	private String dateFrom;
	private String dateTo;
	private String sortBy;
//	private OmnitureReportMetricVO metrics;
	private List<OmnitureReportMetricVO> metrics;
	private List<OmnitureReportElementVO> elements;
	
	public String getReportSuiteID() {
		return reportSuiteID;
	}
	public void setReportSuiteID(String reportSuiteID) {
		this.reportSuiteID = reportSuiteID;
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
	public List<OmnitureReportMetricVO> getMetrics() {
		return metrics;
	}
	public void setMetrics(List<OmnitureReportMetricVO> metrics) {
		this.metrics = metrics;
	}
	public List<OmnitureReportElementVO> getElements() {
		return elements;
	}
	public void setElements(List<OmnitureReportElementVO> elements) {
		this.elements = elements;
	}
	
	@Override
	public String toString() {
		return "ReportDescriptionVO [reportSuiteID=" + reportSuiteID
				+ ", dateFrom=" + dateFrom + ", dateTo=" + dateTo
				+ ", metrics=" + metrics + ", elements=" + elements + ", sortBy=" +sortBy+ "]";
	}
	public String getSortBy() {
		return sortBy;
	}
	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}
	
}
