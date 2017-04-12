package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;
import java.util.List;

// 
/**
 * @author simra2
 * The Class OmnitureReportVO.
 */
public class OmnitureReportVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2820166831682181493L;
	private String type;
	private ReportSuiteVO reportSuite;
	private List<OmnitureReportElementVO> elements;
	private String period;
	private List<OmnitureReportMetricVO> metrics;
	private List<OmnitureReportSegmentVO> segments;
	private List<OmnitureReportDataVO> data;
	private double totals;
	private String version;

	public ReportSuiteVO getReportSuite() {
		return reportSuite;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public void setReportSuite(ReportSuiteVO reportSuite) {
		this.reportSuite = reportSuite;
	}


	public List<OmnitureReportElementVO> getElements() {
		return elements;
	}


	public void setElements(List<OmnitureReportElementVO> elements) {
		this.elements = elements;
	}


	public List<OmnitureReportMetricVO> getMetrics() {
		return metrics;
	}


	public void setMetrics(List<OmnitureReportMetricVO> metrics) {
		this.metrics = metrics;
	}


	public List<OmnitureReportSegmentVO> getSegments() {
		return segments;
	}


	public void setSegments(List<OmnitureReportSegmentVO> segments) {
		this.segments = segments;
	}


	public List<OmnitureReportDataVO> getData() {
		return data;
	}


	public void setData(List<OmnitureReportDataVO> data) {
		this.data = data;
	}


	public double getTotals() {
		return totals;
	}


	public void setTotals(double totals) {
		this.totals = totals;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	@Override
	public String toString() {
		return "OmnitureReportVO [type=" + type + ", reportSuite="
				+ reportSuite + ", elements=" + elements + ", metrics="
				+ metrics + ", segments=" + segments + ", data=" + data
				+ ", totals=" + totals + ", version=" + version + "]";
	}


	public String getPeriod() {
		return period;
	}


	public void setPeriod(String period) {
		this.period = period;
	}
	

}