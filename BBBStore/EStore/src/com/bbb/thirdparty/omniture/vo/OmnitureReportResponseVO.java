package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

public class OmnitureReportResponseVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2475925432162264628L;
	private OmnitureReportVO report;
	private float waitSeconds;
	private float runSeconds;
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
	@Override
	public String toString() {
		return "OmnitureReportResponseVO [report=" + report + ", waitSeconds="
				+ waitSeconds + ", runSeconds=" + runSeconds + "]";
	}
	
}
