package com.bbb.thirdparty.omniture.vo;

import java.io.Serializable;
import java.util.List;

public class OmnitureReportSearchVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3923400802578245643L;
	private String type;
	private List<String> keywords ; 

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	@Override
	public String toString() {
		return "OmnitureReportSearchVO [type=" + type + ", keywords=" + keywords + "]";
	}
}
