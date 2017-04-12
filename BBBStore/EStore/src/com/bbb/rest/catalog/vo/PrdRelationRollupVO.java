package com.bbb.rest.catalog.vo;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.RollupTypeVO;

public class PrdRelationRollupVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rollupTypeKey;
	private List<RollupTypeVO> rollupTypeVO;

	public String getRollupTypeKey() {
		return rollupTypeKey;
	}
	public void setRollupTypeKey(String rollupTypeKey) {
		this.rollupTypeKey = rollupTypeKey;
	}
	public List<RollupTypeVO> getRollupTypeVO() {
		return rollupTypeVO;
	}
	public void setRollupTypeVO(List<RollupTypeVO> rollupTypeVO) {
		this.rollupTypeVO = rollupTypeVO;
	}




}
