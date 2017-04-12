package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureGetResponseVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2475925432162264628L;
	private OmnitureReportVO report;
	private float waitSeconds;
	private float runSeconds;
	private String error;
	private String error_description;
	private String error_uri;
	private int recordsMoved;
	public OmnitureReportVO getReport() {
		return report;
	}
	public void setReport(OmnitureReportVO report) {
		this.report = report;
	}
	public float getWaitSeconds() {
		return waitSeconds;
	}
	public void setWaitSeconds(float waitSeconds) {
		this.waitSeconds = waitSeconds;
	}
	public float getRunSeconds() {
		return runSeconds;
	}
	public void setRunSeconds(float runSeconds) {
		this.runSeconds = runSeconds;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getError_description() {
		return error_description;
	}
	public void setError_description(String error_description) {
		this.error_description = error_description;
	}
	public String getError_uri() {
		return error_uri;
	}
	public void setError_uri(String error_uri) {
		this.error_uri = error_uri;
	}
	@Override
	public String toString() {
		return "OmnitureGetResponseVO [report=" + report + ", waitSeconds="
				+ waitSeconds + ", runSeconds=" + runSeconds + ", error="
				+ error + ", error_description=" + error_description
				+ ", error_uri=" + error_uri + "]";
	}
	/**
	 * @return the recordsMoved
	 */
	public int getRecordsMoved() {
		return recordsMoved;
	}
	/**
	 * @param recordsMoved the recordsMoved to set
	 */
	public void setRecordsMoved(int recordsMoved) {
		this.recordsMoved = recordsMoved;
	}

	
}
