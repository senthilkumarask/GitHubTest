package com.bbb.importprocess.vo;

import atg.core.util.StringUtils;

public class StofuImagesVO {

	String mOperationFlag;
	String mScene7URL;
	String mShotType;

	public String getScene7URL() {
		return mScene7URL;
	}

	public void setScene7URL(String mScene7URL) {
		this.mScene7URL = mScene7URL;
	}

	public String getShotType() {
		return mShotType;
	}

	public void setShotType(String mShotType) {
		this.mShotType = mShotType;
	}

	public String getOperationFlag() {
		if (!StringUtils.isEmpty(mOperationFlag)) {

		      return mOperationFlag.trim();
		    }
		return mOperationFlag;
	}

	public void setOperationFlag(String mOperationFlag) {
		this.mOperationFlag = mOperationFlag;
	}

}
