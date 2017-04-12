package com.bbb.commerce.catalog.vo;

import java.io.Serializable;

public class CollegeVO implements Comparable<CollegeVO>,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mCollegeId;
	private String mCollegeName;
	private String mCollegeLogo;
	private volatile int hashCode = 0;

	public CollegeVO() {
		// TODO Auto-generated constructor stub
	}

	public CollegeVO(final String pCollegeId,final String pCollegeName,final String pCollegeLogo) {
		this.mCollegeId = pCollegeId;
		this.mCollegeName = pCollegeName;
		this.mCollegeLogo = pCollegeLogo;
	}

	/**
	 * @return the collegeId
	 */
	public String getCollegeId() {
		return this.mCollegeId;
	}

	/**
	 * @param pCollegeId
	 *            the collegeId to set
	 */
	public void setCollegeId(final String pCollegeId) {
		this.mCollegeId = pCollegeId;
	}

	/**
	 * @return the collegeName
	 */
	public String getCollegeName() {
		return this.mCollegeName;
	}

	/**
	 * @param pCollegeName
	 *            the collegeName to set
	 */
	public void setCollegeName(final String pCollegeName) {
		this.mCollegeName = pCollegeName;
	}

	/**
	 * @return the collegeLogo
	 */
	public String getCollegeLogo() {
		return this.mCollegeLogo;
	}

	/**
	 * @param pCollegeLogo
	 *            the collegeLogo to set
	 */
	public void setCollegeLogo(final String pCollegeLogo) {
		this.mCollegeLogo = pCollegeLogo;
	}

	@Override
	public String toString() {
		final StringBuffer toString = new StringBuffer(" College VO Details \n ");
		toString.append("College ID  ").append(this.mCollegeId).append("\n").append(" College Name  ").append(this.mCollegeName).append("\n").append(" College Logo ")
				.append(this.mCollegeLogo).append("\n");
		return toString.toString();
	}

	@Override
	public int compareTo(final CollegeVO pCollegeVo) {
		final int compareResult = this.mCollegeName.compareTo(pCollegeVo.getCollegeName());
		return compareResult;
	}

	@Override
	public boolean equals(final Object pObj) {

		if (pObj instanceof CollegeVO) {
			final CollegeVO collegeVO = (CollegeVO) pObj;
			if (this.mCollegeId.equals(collegeVO.getCollegeId()) && this.mCollegeName.equals(collegeVO.getCollegeName())
					&& this.mCollegeLogo.equals(collegeVO.getCollegeLogo())) {
				return true;
			}
		}
		return false;

	}
	@Override
	public int hashCode() {
		final int multiplier = 23;
		if (this.hashCode == 0) {
			int code = 133;
			code = (multiplier * code) + this.mCollegeId.hashCode();
			code = (multiplier * code) + this.mCollegeName.hashCode();
			code =  (multiplier * code) + this.mCollegeLogo.hashCode();
			this.hashCode = code;
		}
		return this.hashCode;

	}

}
