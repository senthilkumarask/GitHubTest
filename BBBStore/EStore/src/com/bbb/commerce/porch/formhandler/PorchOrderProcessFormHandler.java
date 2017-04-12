package com.bbb.commerce.porch.formhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContextException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class PorchOrderProcessFormHandler extends BBBGenericFormHandler {
	
	private String orderIds;
	private String porchLoginSuccessURL;
	private String porchLoginFailureURL;
	private String ordersSuccessURL;
	private String ordersFailureURL;
	private String adminName;
	private String adminPassword;
	private Map<String,List<CommerceItem>> porchProjectIdList;
	
	private static final String ERR_MSG_REQUIRED_NAME = "Please enter porch orders";
	private static final String VALID_DATA = "Thanks !!";
	private static final String ERR_MSG_NOT_VALID_NAME = "Please provide valid admin name";
	private static final String ERR_MSG_REQUIRED_PASSWORD = "Please provide password";
	private static final String ERR_MSG_NOT_VALID_PASSWORD = "Please provide valid password";
	
	private BBBCatalogTools catalogTools;
	private PorchServiceManager porchServiceManager;
	
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
		this.logDebug("handleValidateUser method start. ");
		this.validateAdminUser();
		if (!this.getFormError()) {
			this.logDebug("User is Porch Authenticated User");
			pRequest.getSession().setAttribute("UserAuthenticated","true");
		}
		this.logDebug("handleValidateUser method End. ");
		return this.checkFormRedirect(this.getPorchLoginSuccessURL(),
				this.getPorchLoginFailureURL(), pRequest, pResponse);
	}
	
	public void validateAdminUser() throws BBBSystemException, BBBBusinessException {
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

	/**
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	
	public boolean handleValidateOrderId(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException, BBBSystemException, BBBBusinessException, SiteContextException {
		this.logDebug("handleValidateUser method start. ");
		this.validateData();
		if (!this.getFormError()) {
			String commaseperatedOrders = this.getOrderIds();
			if(!StringUtils.isBlank(commaseperatedOrders)){
			List<String> list =new ArrayList<String>(Arrays.asList(commaseperatedOrders.split(",")));// new ArrayList<String>(Arrays.asList(commaseperatedOrders));
			
			final Map<String,List<CommerceItem>> projectIdMap = this.getPorchServiceManager().processQASPorchOrders(list);			
			setPorchProjectIdList(projectIdMap);
		}
			pRequest.getSession().setAttribute("UserAuthenticated","true");
		}
		this.logDebug("handleValidateUser method End. ");
		return this.checkFormRedirect(this.getOrdersSuccessURL(),
				this.getOrdersFailureURL(), pRequest, pResponse);
	}
	
	public void validateData() throws BBBSystemException, BBBBusinessException {
		this.logDebug("validateAdminUser method start");
		if (BBBUtility.isEmpty(this.getOrderIds())) {
			this.addFormException(new DropletException(
					ERR_MSG_REQUIRED_NAME));
		}
		this.logDebug("validateAdminUser method end");
	}

 

	public String getPorchLoginSuccessURL() {
		return porchLoginSuccessURL;
	}

	public void setPorchLoginSuccessURL(String porchLoginSuccessURL) {
		this.porchLoginSuccessURL = porchLoginSuccessURL;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

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

	public String getPorchLoginFailureURL() {
		return porchLoginFailureURL;
	}

	public void setPorchLoginFailureURL(String porchLoginFailureURL) {
		this.porchLoginFailureURL = porchLoginFailureURL;
	}

	public String getOrdersSuccessURL() {
		return ordersSuccessURL;
	}

	public void setOrdersSuccessURL(String ordersSuccessURL) {
		this.ordersSuccessURL = ordersSuccessURL;
	}

	public String getOrdersFailureURL() {
		return ordersFailureURL;
	}

	public void setOrdersFailureURL(String ordersFailureURL) {
		this.ordersFailureURL = ordersFailureURL;
	}

	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}

	public Map<String,List<CommerceItem>> getPorchProjectIdList() {
		return porchProjectIdList;
	}

	public void setPorchProjectIdList(Map<String,List<CommerceItem>> porchProjectIdList) {
		this.porchProjectIdList = porchProjectIdList;
	}

	/**
	 * @return the orderIds
	 */
	public String getOrderIds() {
		return orderIds;
	}

	/**
	 * @param orderIds the orderIds to set
	 */
	public void setOrderIds(String orderIds) {
		this.orderIds = orderIds;
	}

}
