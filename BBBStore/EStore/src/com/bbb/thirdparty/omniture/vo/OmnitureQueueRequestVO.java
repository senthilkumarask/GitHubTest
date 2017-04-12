package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureQueueRequestVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5289297010076670409L;
	private ReportDescriptionVO reportDescription;
	public ReportDescriptionVO getReportDescription() {
		return reportDescription;
	}
	public void setReportDescription(ReportDescriptionVO reportDescription) {
		this.reportDescription = reportDescription;
	}
	@Override
	public String toString() {
		return "OmnitureQueueRequestVO [reportDescription=" + reportDescription
				+ "]";
	}
	
}
