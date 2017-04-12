package com.bbb.social.facebook.api.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class FBEducationVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private FBSchoolVO school;

	/**
	 * 
	 */
	private String type;

	/**
	 * 
	 */
	private List<FBWithVO> with;

	/**
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 * 
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * 
	 * @return
	 */
	public List<FBWithVO> getWith() {
		return with;
	}

	/**
	 * 
	 * @param with
	 */
	public void setWith(List<FBWithVO> with) {
		this.with = with;
	}

	/**
	 * 
	 * @return
	 */
	public FBSchoolVO getSchool() {
		return school;
	}

	/**
	 * 
	 * @param school
	 */
	public void setSchool(FBSchoolVO school) {
		this.school = school;
	}

}
