/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  ProfileVO.java
 *
 *  DESCRIPTION: Migrated profile details VO. 
 *   	
 *  HISTORY:
 *  26/03/12 Initial version
 *  	
 */
package com.bbb.account.profile.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author hbandl
 *
 */
/**
 * @author sjai70
 *
 */
public class ProfileEDWInfoVO implements Serializable {
	
	
	private static final long serialVersionUID = 1L;

	public String ATGProfileID;
	
	public String email;
	
	private int repoRetryCount;
	
	private String edwDataJsonObject;
	
	private boolean edwDataStale;
	
	private boolean sessionTIBCOcall;
	
	private String dataCentre;
	
	private String userToken;
	
	private boolean maxRepoRetryFlag;
	
	public boolean isMaxRepoRetryFlag() {
		return maxRepoRetryFlag;
	}

	public void setMaxRepoRetryFlag(boolean maxRepoRetryFlag) {
		this.maxRepoRetryFlag = maxRepoRetryFlag;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getDataCentre() {
		return dataCentre;
	}

	public void setDataCentre(String dataCentre) {
		this.dataCentre = dataCentre;
	}

	public boolean isSessionTIBCOcall() {
		return sessionTIBCOcall;
	}

	public void setSessionTIBCOcall(boolean sessionTIBCOcall) {
		this.sessionTIBCOcall = sessionTIBCOcall;
	}

	public boolean isEdwDataStale() {
		return edwDataStale;
	}

	public void setEdwDataStale(boolean edwDataStale) {
		this.edwDataStale = edwDataStale;
	}

	public String getEdwDataJsonObject() {
		return edwDataJsonObject;
	}

	public void setEdwDataJsonObject(String edwDataJsonObject) {
		this.edwDataJsonObject = edwDataJsonObject;
	}

	private List<String> edwProfileAttributes;

	public String getATGProfileID() {
		return ATGProfileID;
	}

	public void setATGProfileID(String aTGProfileID) {
		ATGProfileID = aTGProfileID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getRepoRetryCount() {
		return repoRetryCount;
	}

	public void setRepoRetryCount(int repoRetryCount) {
		this.repoRetryCount = repoRetryCount + 1;
	}

	public List<String> getEdwProfileAttributes() {
		return edwProfileAttributes;
	}

	public void setEdwProfileAttributes(List<String> edwProfileAttributes) {
		this.edwProfileAttributes = edwProfileAttributes;
	}
		
	}