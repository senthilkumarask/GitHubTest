package com.bbb.importprocess.vo;

import java.util.List;

public class StofuImageParentVO {
	boolean mProductFlag;
	List<StofuImagesVO> mStofuImagesVOList;

	public boolean isProductFlag() {
		return mProductFlag;
	}

	public void setProductFlag(boolean mProductFlag) {
		this.mProductFlag = mProductFlag;
	}

	public List<StofuImagesVO> getStofuImagesVOList() {
		return mStofuImagesVOList;
	}

	public void setStofuImagesVOList(List<StofuImagesVO> mStofuImagesVOList) {
		this.mStofuImagesVOList = mStofuImagesVOList;
	}

}
