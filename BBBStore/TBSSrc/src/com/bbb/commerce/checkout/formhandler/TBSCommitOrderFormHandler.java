package com.bbb.commerce.checkout.formhandler;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.common.TBSSessionComponent;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.idm.AssociateVO;
import com.bbb.integration.idm.IDMAuthService;
import com.bbb.tbs.account.TBSProfileFormHandler;

public class TBSCommitOrderFormHandler extends BBBCommitOrderFormHandler {
	
	private static final String APPROVER_NAME_EMPTY = "No approver name found";
	private static final String PASSWORD_EMPTY = "No password found";
	private static final String APPROVER_NOT_DIFFERENT = "Approver ID is not different from associate ID";
	private static final String INVALID_AUTH = "Authorization failed";
	
	private TBSProfileFormHandler mProfileformHandler;
	private TBSSessionComponent mTbsSessionComponent;
	
	private String mApproverId;
	private String mApproverPwd;
	private IDMAuthService mIdmService;
	private String mApprovalSuccessURL;
	private String mApprovalErrorURL;

	public TBSSessionComponent getTbsSessionComponent() {
		return mTbsSessionComponent;
	}

	public void setTbsSessionComponent(TBSSessionComponent mTbsSessionComponent) {
		this.mTbsSessionComponent = mTbsSessionComponent;
	}


	/**
	 * @return the profileformHandler
	 */
	public TBSProfileFormHandler getProfileformHandler() {
		return mProfileformHandler;
	}

	/**
	 * @param pProfileformHandler the profileformHandler to set
	 */
	public void setProfileformHandler(TBSProfileFormHandler pProfileformHandler) {
		mProfileformHandler = pProfileformHandler;
	}

	/**
	 * @return the approverId
	 */
	public String getApproverId() {
		return mApproverId;
	}

	/**
	 * @return the approverPwd
	 */
	public String getApproverPwd() {
		return mApproverPwd;
	}

	/**
	 * @param pApproverId the approverId to set
	 */
	public void setApproverId(String pApproverId) {
		mApproverId = pApproverId.trim();
	}

	/**
	 * @param pApproverPwd the approverPwd to set
	 */
	public void setApproverPwd(String pApproverPwd) {
		mApproverPwd = pApproverPwd;
	}

	/**
	 * @return the idmService
	 */
	public IDMAuthService getIdmService() {
		return mIdmService;
	}

	/**
	 * @param pIdmService the idmService to set
	 */
	public void setIdmService(IDMAuthService pIdmService) {
		mIdmService = pIdmService;
	}

	/**
	 * @return the approvalSuccessURL
	 */
	public String getApprovalSuccessURL() {
		return mApprovalSuccessURL;
	}

	/**
	 * @return the approvalErrorURL
	 */
	public String getApprovalErrorURL() {
		return mApprovalErrorURL;
	}

	/**
	 * @param pApprovalSuccessURL the approvalSuccessURL to set
	 */
	public void setApprovalSuccessURL(String pApprovalSuccessURL) {
		mApprovalSuccessURL = pApprovalSuccessURL;
	}

	/**
	 * @param pApprovalErrorURL the approvalErrorURL to set
	 */
	public void setApprovalErrorURL(String pApprovalErrorURL) {
		mApprovalErrorURL = pApprovalErrorURL;
	}

	/**
	 * This method is overridden to set the associateId, approverId and storeId to the order.
	 */
	@Override
	public void preCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		vlogDebug("TBSCommitOrderFormHandler :: preCommitOrder() method :: START");
		BBBOrder order = (BBBOrder) getOrder();
		super.preCommitOrder(pRequest, pResponse);
		
		AssociateVO approverVO = (AssociateVO) pRequest.getSession().getAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME2);
		if(order.isTBSApprovalRequired() && approverVO == null){
			addFormException(new DropletException("Please approve the override details"));
		}
		if(pRequest.getSession().getAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME1) != null){
			AssociateVO associateVO = (AssociateVO) pRequest.getSession().getAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME1);
			order.setTBSAssociateID(associateVO.getAssociateId());
		}
		if(approverVO != null){
			order.setTBSApproverID(approverVO.getAssociateId());
		}
		String storeNum = (String)pRequest.getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		if(StringUtils.isBlank(storeNum)){
			storeNum = storeNumFromCookie(pRequest);
		}
		order.setTbsStoreNo(storeNum);
		String sucessUrl = getCommitOrderSuccessURL();
		if(StringUtils.isBlank(sucessUrl)){
			sucessUrl = pRequest.getContextPath() + getCheckoutState().getSuccessURL((Profile) getProfile());
		} else if(!sucessUrl.contains("_DARGS")) {
			sucessUrl = pRequest.getContextPath() + getCheckoutState().getSuccessURL((Profile) getProfile());
		}
		/* Code change for JIRA Ticket #TBXPS-1470 starts */
		
		String siteId = getCurrentSiteId();
		if(!siteId.isEmpty() && (TBSConstants.SITE_TBS_BAB_US.equals(siteId) || TBSConstants.SITE_TBS_BBB.equals(siteId) || TBSConstants.SITE_TBS_BAB_CA.equals(siteId)) && !((Profile) this.getProfile()).isTransient()){
			sucessUrl = sucessUrl+"?orderId="+order.getId() + "&loggedin=true";
		}else{
			sucessUrl = sucessUrl+"?orderId="+order.getId();
		}
		
		/* Code change for JIRA Ticket #TBXPS-1470 ends */
		
		setCommitOrderSuccessURL(sucessUrl);
		vlogDebug("TBSCommitOrderFormHandler :: preCommitOrder() method :: END");
	}
	
	/**
	 * This method is overriden to logout the user on successful order submission. 
	 */
	public void postCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		vlogDebug("TBSCommitOrderFormHandler :: postCommitOrder() method :: START");
		super.postCommitOrder(pRequest, pResponse);
		
		if (!getProfile().isTransient()){
			getCheckoutState().setIsTBSOrderSubmitted(true);
			getProfileformHandler().postLogoutUser(pRequest, pResponse);		 
	    } else {
	    	getProfileformHandler().removeOrderCookie(pRequest, pResponse);
	    	getTbsSessionComponent().setEmailId(null);
			getTbsSessionComponent().setMobileNumber(null);
	    }
		// This check is to fix cart page redirection issue after placing the order.
		pRequest.getSession().isNew();
		vlogDebug("TBSCommitOrderFormHandler :: postCommitOrder() method :: END");
	}
	
	/**
	 * Handler method for approver authentication 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean handleApproverAuth(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		vlogDebug("TBSCommitOrderFormHandler :: handleApproverAuth() method :: START");
		
        if( StringUtils.isBlank(getApproverId())) {
			addFormException(new DropletException(APPROVER_NAME_EMPTY));
			vlogError("Approver Id is empty");
			return checkFormRedirect("", "", pRequest, pResponse);
		}
		if(StringUtils.isBlank(getApproverPwd())) {
			addFormException(new DropletException(PASSWORD_EMPTY));
			vlogError("Approver password is empty");
			return checkFormRedirect(getApprovalSuccessURL(), getApprovalErrorURL(), pRequest, pResponse);
		}
    	AssociateVO associateVO = (AssociateVO)pRequest.getSession().getAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME1);
    	if(associateVO != null && associateVO.getAssociateId().equalsIgnoreCase(getApproverId())) {
			addFormException(new DropletException(APPROVER_NOT_DIFFERENT)); 
			vlogError("Approver Id and Associate Id are same");
			return checkFormRedirect(getApprovalSuccessURL(), getApprovalErrorURL(), pRequest, pResponse);
    	}
        // Actually attempt the auth...
		boolean success = getIdmService().authenticateAssociate(getApproverId(), getApproverPwd());
		
		if(success) {
			// setting the sessionAttribute for the approver
        	associateVO = new AssociateVO(getApproverId(), new Date());
	        pRequest.getSession().setAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME2, associateVO);
		}
		else {
			this.addFormException(new DropletException(INVALID_AUTH));
			vlogError("Approver authentication failed");
		}
		vlogDebug("TBSCommitOrderFormHandler :: handleApproverAuth() method :: END");
		return checkFormRedirect(getApprovalSuccessURL(), getApprovalErrorURL(), pRequest, pResponse);
	}
	
	public boolean handleCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		//setting state to REVIEW
		getCheckoutState().setCurrentLevel(BBBPayPalConstants.REVIEW);
		return super.handleCommitOrder(pRequest, pResponse);
	}
	
	/**
	 * This is used to get the store number from cookie
	 * @param pRequest
	 * @return
	 */
	private String storeNumFromCookie(HttpServletRequest pRequest) {
		Cookie cookies[] = pRequest.getCookies();
		String cookieValue = null;
		if (cookies != null)
			for (int i = 0; i < cookies.length; i++) {
				String name = cookies[i].getName();
				if (TBSConstants.STORE_NUMBER_COOKIE.equals(name)) {
					cookieValue = cookies[i].getValue();
					break;
				}
			}
		return cookieValue;
	}
}
