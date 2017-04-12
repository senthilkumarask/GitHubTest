package com.bbb.rest.catalog.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.AttributeVO;


public class CatalogItemAttributesVO implements Serializable {

	 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<AttributeVO> mAttributeVOsList;
	
	public List<AttributeVO> getAttributeVOsList() {
		return mAttributeVOsList;
	}
	public void setAttributeVOsList(List<AttributeVO> pAttributeVOsList) {
		
		this.mAttributeVOsList = pAttributeVOsList;
	}
	
}
