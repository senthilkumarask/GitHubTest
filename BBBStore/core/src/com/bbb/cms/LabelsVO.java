package com.bbb.cms;

import java.io.Serializable;
import java.util.Map;

public class LabelsVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public LabelsVO(Map<String, String> labels, Map<String, String> labelTextArea, Map<String, String> errorMsg) {
		super();
		this.labels = labels;
		this.labelTextArea = labelTextArea;
		this.errorMsg = errorMsg;
	}
	private Map<String,String> labels;
	private Map<String,String> labelTextArea;
	private Map<String,String> errorMsg;
	/**
	 * @return the labels
	 */
	public Map<String, String> getLabels() {
		return labels;
	}
	/**
	 * @param labels the labels to set
	 */
	public void setLabels(Map<String, String> labels) {
		this.labels = labels;
	}
	/**
	 * @return the labelTextArea
	 */
	public Map<String, String> getLabelTextArea() {
		return labelTextArea;
	}
	/**
	 * @param labelTextArea the labelTextArea to set
	 */
	public void setLabelTextArea(Map<String, String> labelTextArea) {
		this.labelTextArea = labelTextArea;
	}
	/**
	 * @return the errorMsg
	 */
	public Map<String, String> getErrorMsg() {
		return errorMsg;
	}
	/**
	 * @param errorMsg the errorMsg to set
	 */
	public void setErrorMsg(Map<String, String> errorMsg) {
		this.errorMsg = errorMsg;
	}
 
}

