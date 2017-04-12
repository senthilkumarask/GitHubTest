package com.bbb.social.facebook.api.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author manohar
 * @version 1.0
 */
public class FBUserVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	private String id;

	/**
	 * 
	 */
	private String name;

	/**
	 * 
	 */
	@SerializedName("first_name")
	private String firstName;

	/**
	 * 
	 */
	@SerializedName("middle_name")
	private String middleName;

	/**
	 * 
	 */
	@SerializedName("last_name")
	private String lastName;

	/**
	 * 
	 */
	private String gender;

	/**
	 * 
	 */
	private String verified;

	/**
	 * 
	 */
	private Date birthDate;

	/**
	 * 
	 */
	private String offlineAccessToken;

	/**
	 * 
	 */
	private List<FBEducationVO> education;

	
	/**
	 * 
	 */
	private String email;
	
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * 
	 * @return
	 */
	public String getMiddleName() {
		return middleName;
	}

	/**
	 * 
	 * @param middleName
	 */
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	/**
	 * 
	 * @return
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * 
	 * @return
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * 
	 * @param gender
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * 
	 * @return
	 */
	public Date getBirthDate() {
		return new Date(birthDate.getTime());
	}

	/**
	 * 
	 * @param birthDate
	 */
	public void setBirthDate(Date birthDate) {
		this.birthDate = new Date(birthDate.getTime());
	}

	/**
	 * 
	 * @return
	 */
	public String getOfflineAccessToken() {
		return offlineAccessToken;
	}

	/**
	 * 
	 * @param offlineAccessToken
	 */
	public void setOfflineAccessToken(String offlineAccessToken) {
		this.offlineAccessToken = offlineAccessToken;
	}

	/**
	 * 
	 * @return
	 */
	public List<FBEducationVO> getEducation() {
		return education;
	}

	/**
	 * 
	 * @param schools
	 */
	public void setEducation(List<FBEducationVO> education) {
		this.education = education;
	}

	/**
	 * 
	 * @return
	 */
	public String getVerified() {
		return verified;
	}

	/**
	 * 
	 * @param verified
	 */
	public void setVerified(String verified) {
		this.verified = verified;
	}
	
	public boolean isVerified(){
		return Boolean.parseBoolean(this.verified);
	}

}
