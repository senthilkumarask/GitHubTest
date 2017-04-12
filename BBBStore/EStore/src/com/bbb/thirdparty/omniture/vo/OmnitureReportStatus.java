/**
 * 
 */
package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;

/**
 * @author pku104
 *
 */
public class OmnitureReportStatus implements Serializable{
	private static final long serialVersionUID = -8762388888217238310L;

	private String reportID;
	private String type;
	private String queueTime;
	private String status;
	private String priority;
	private String estimate;
	private String reportSuiteID;
	private String user;
	private boolean cancel ;
	/**
	 * @return the reportID
	 */
	public String getReportID() {
		return reportID;
	}
	/**
	 * @param reportID the reportID to set
	 */
	public void setReportID(String reportID) {
		this.reportID = reportID;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the queueTime
	 */
	public String getQueueTime() {
		return queueTime;
	}
	/**
	 * @param queueTime the queueTime to set
	 */
	public void setQueueTime(String queueTime) {
		this.queueTime = queueTime;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the priority
	 */
	public String getPriority() {
		return priority;
	}
	/**
	 * @param priority the priority to set
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	/**
	 * @return the estimate
	 */
	public String getEstimate() {
		return estimate;
	}
	/**
	 * @param estimate the estimate to set
	 */
	public void setEstimate(String estimate) {
		this.estimate = estimate;
	}
	/**
	 * @return the reportSuiteID
	 */
	public String getReportSuiteID() {
		return reportSuiteID;
	}
	/**
	 * @param reportSuiteID the reportSuiteID to set
	 */
	public void setReportSuiteID(String reportSuiteID) {
		this.reportSuiteID = reportSuiteID;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OmnitureReportStatus [reportID=" + reportID + ", type="
				+ type + ", queueTime=" + queueTime + ", status=" + status
				+ ", priority=" + priority + ", estimate=" + estimate
				+ ", reportSuiteID=" + reportSuiteID + ", user=" + user + "]";
	}
	public boolean isCancel() {
		return cancel;
	}
	public void setCancel(boolean cancel) {
		this.cancel = cancel;
	}
}
