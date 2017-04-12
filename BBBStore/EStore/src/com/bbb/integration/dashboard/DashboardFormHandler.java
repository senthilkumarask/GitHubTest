package com.bbb.integration.dashboard;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class DashboardFormHandler extends BBBGenericFormHandler {

	private String adminName;
	private String adminPassword;
	private BBBCatalogTools catalogTools;
	private String dashboardSuccessURL;
	private String dashboardErrorURL;
	
	public String getAdminName() {
		return adminName;
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	
	public String getAdminPassword() {
		return adminPassword;
	}
	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	public String getDashboardSuccessURL() {
		return dashboardSuccessURL;
	}
	public String getDashboardErrorURL() {
		return dashboardErrorURL;
	}
	public void setDashboardErrorURL(String dashboardErrorURL) {
		this.dashboardErrorURL = dashboardErrorURL;
	}
	public void setDashboardSuccessURL(String dashboardSuccessURL) {
		this.dashboardSuccessURL = dashboardSuccessURL;
	}
	
	
	
	private static final String ERR_MSG_REQUIRED_NAME = "Please provide admin name";
	private static final String ERR_MSG_NOT_VALID_NAME = "Please provide valid admin name";
	private static final String ERR_MSG_REQUIRED_PASSWORD = "Please provide password";
	private static final String ERR_MSG_NOT_VALID_PASSWORD = "Please provide valid password";
	
	
	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean handleValidateUser(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException {
		this.logDebug("HandleValidateUser method start. ");
		this.validateAdminUser();
		if (!this.getFormError()) {
			pRequest.getSession().setAttribute("userAuthenticated","true");
		}
		this.logDebug("handleValidateUser method End. ");
		return this.checkFormRedirect(this.getDashboardSuccessURL(),
				this.getDashboardErrorURL(), pRequest, pResponse);
	}
	
	private void validateAdminUser() throws BBBSystemException, BBBBusinessException {
		this.logDebug("validateAdminUser method start");
		List<String> adminUsers = null;
		List<String> listOfPasswords = null;
		try {
			adminUsers = this.getCatalogTools().getAllValuesForKey(
					"ContentCatalogKeys", "admin_name");
			listOfPasswords = this.getCatalogTools().getAllValuesForKey(
					"ContentCatalogKeys", "admin_password");
			if (BBBUtility.isEmpty(this.getAdminName())) {
				this.addFormException(new DropletException(
						ERR_MSG_REQUIRED_NAME));
			} else if (adminUsers != null && !adminUsers.isEmpty()
					&& adminUsers.contains(this.getAdminName())) {
				this.setAdminName(this.getAdminName());
			} else {
				this.addFormException(new DropletException(
						ERR_MSG_NOT_VALID_NAME));
			}
			if (BBBUtility.isEmpty(this.getAdminPassword())) {
				this.addFormException(new DropletException(
						ERR_MSG_REQUIRED_PASSWORD));
			} else if (listOfPasswords != null && !listOfPasswords.isEmpty()
					&& this.getAdminPassword().equals(listOfPasswords.get(0))) {
				this.setAdminPassword(this.getAdminPassword());
			} else {
				this.addFormException(new DropletException(
						ERR_MSG_NOT_VALID_PASSWORD));
			}
		} catch (final BBBSystemException e) {
			throw new BBBSystemException("BBBSystemException - admin_name/admin_password key not found for site");
		} catch (final BBBBusinessException e) {
			throw new BBBBusinessException("BBBSystemException - admin_name/admin_password key not found for site");
		}
		this.logDebug("validateAdminUser method end");
	}
	
}
