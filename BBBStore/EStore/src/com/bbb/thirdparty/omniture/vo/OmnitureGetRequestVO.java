package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureGetRequestVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2475925432162264628L;
	private String reportID;
	public String getReportID() {
		return reportID;
	}
	public void setReportID(String reportID) {
		this.reportID = reportID;
	}
	@Override
	public String toString() {
		return "OmnitureGETRequestVO [reportID=" + reportID + "]";
	}
	
}
