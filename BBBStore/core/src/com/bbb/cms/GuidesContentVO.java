package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

public class GuidesContentVO  implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<GuidesTemplateVO> guidesContentList;

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

	 
}
