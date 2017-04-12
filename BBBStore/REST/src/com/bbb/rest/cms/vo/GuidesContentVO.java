package com.bbb.rest.cms.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.cms.GuidesTemplateVO;

public class GuidesContentVO{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<GuidesTemplateVO> guidesContentList;
	 private boolean error=true;
	 private List<String> errorCode=null;
	/**
	 * @return the guidesContentList
	 */
	public List<GuidesTemplateVO> getGuidesContentList() {
		return guidesContentList;
	}
	/**
	 * @param guidesContentList the guidesContentList to set
	 */
	public void setGuidesContentList(List<GuidesTemplateVO> guidesContentList) {
		this.guidesContentList = guidesContentList;
	}
	/**
	 * @return the error
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(boolean error) {
		this.error = error;
	}
	/**
	 * @return the errorCode
	 */
	public List<String> getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(List<String> errorCode) {
		this.errorCode = errorCode;
	}
	 
}
