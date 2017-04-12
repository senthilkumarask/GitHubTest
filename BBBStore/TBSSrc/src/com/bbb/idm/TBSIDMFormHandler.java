package com.bbb.idm;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.TBSConstants;
import com.bbb.integration.idm.IDMAuthService;

/**
 * Handles the associate authentication via IDM 
 *
 */
public class TBSIDMFormHandler extends BBBGenericFormHandler {

	private String username;
	private String password;
	private IDMAuthService IDMService;
	private String successURL="";
	private String errorURL="";
    private Map<String,String> successUrlMap;
    private Map<String,String> errorUrlMap;
    private String fromPage;// Page Name that will be set from JSP
	private static final String USERNAME_EMPTY = "No username found";
	private static final String PASSWORD_EMPTY = "No password found";
	private static final String INVALID_AUTH = "Authorization failed";
	private static final String APPROVER_NOT_DIFFERENT = "Approver ID is not different from current associate";

	/**
	 * Handler method for associate authentication 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public final boolean handleAssocAuth(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath() + getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath() + getErrorUrlMap().get(getFromPage()));
			
		}
		
		if(!preAssocAuth(pRequest, pResponse)){
			return false;
		}
        // Actually attempt the auth...
		boolean success = IDMService.authenticateAssociate(getUsername(), getPassword());
		vlogDebug("IDM service status :: "+success);
		if( success ) {
			// setting the sessionAttribute for the associate or approver
        	AssociateVO associateVO = new AssociateVO(getUsername(),new Date());
	        pRequest.getSession().setAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME1, associateVO);
		}
		else {
			this.addFormException(new DropletException(INVALID_AUTH));
		}
		return this.checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
	}

	private Boolean preAssocAuth(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		Object associateObj = pRequest.getSession().getAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME1);
		if(associateObj != null){
			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
		}
		String username = getUsername();
        if( username == null || username.isEmpty() ) {
			this.addFormException(new DropletException(USERNAME_EMPTY));
			return this.checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
		}
		
        String password = getPassword();
       	if( password == null || password.isEmpty() ) {
			this.addFormException(new DropletException(PASSWORD_EMPTY));
			return this.checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
		}

       	AssociateVO approverVO = (AssociateVO)pRequest.getSession().getAttribute(TBSConstants.ASSOCIATE_ATTRIBUTE_NAME2);
       	if(approverVO != null && approverVO.getAssociateId().equalsIgnoreCase(username)) {
			addFormException(new DropletException(APPROVER_NOT_DIFFERENT)); 
			vlogError("Approver Id and Associate Id are same");
			return checkFormRedirect(getSuccessURL(), getErrorURL(), pRequest, pResponse);
    	}
		return true;
	}	
	
	public IDMAuthService getIDMService() {
		return IDMService;
	}
	public void setIDMService(IDMAuthService IDMService) {
		this.IDMService = IDMService;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username.trim();
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSuccessURL() {
		return successURL;
	}
	public void setSuccessURL(String successURL) {
		this.successURL = successURL;
	}
	public String getErrorURL() {
		return errorURL;
	}
	public void setErrorURL(String errorURL) {
		this.errorURL = errorURL;
	}

	public Map<String,String> getSuccessUrlMap() {
		return successUrlMap;
	}

	public void setSuccessUrlMap(Map<String,String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}

	public Map<String,String> getErrorUrlMap() {
		return errorUrlMap;
	}

	public void setErrorUrlMap(Map<String,String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
}
