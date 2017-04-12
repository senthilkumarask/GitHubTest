package com.bbb.thirdparty.omniture.vo;

public class ArchiveDataVO {
	
	private String storeProcQuery;
	private String reportType;
	private int chkRecordsFlag;
	private String concept;
	private long recordsMoved;
	private boolean isArchiveProc;
	
	public String getStoreProcQuery() {
		return storeProcQuery;
	}
	public void setStoreProcQuery(String storeProcQuery) {
		this.storeProcQuery = storeProcQuery;
	}

	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public int getChkRecordsFlag() {
		return chkRecordsFlag;
	}
	public void setChkRecordsFlag(int chkRecordsFlag) {
		this.chkRecordsFlag = chkRecordsFlag;
	}
	public String getConcept() {
		return concept;
	}
	public void setConcept(String concept) {
		this.concept = concept;
	}
	public long getRecordsMoved() {
		return recordsMoved;
	}
	public void setRecordsMoved(long recordsMoved) {
		this.recordsMoved = recordsMoved;
	}
	public boolean isArchiveProc() {
		return isArchiveProc;
	}
	public void setArchiveProc(boolean isArchiveProc) {
		this.isArchiveProc = isArchiveProc;
	}
	

}
