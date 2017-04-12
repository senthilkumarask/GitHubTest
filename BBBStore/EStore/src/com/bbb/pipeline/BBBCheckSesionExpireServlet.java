package com.bbb.pipeline;

import java.io.IOException;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import atg.droplet.DropletConstants;
import atg.naming.NameContext;
import atg.nucleus.naming.ComponentName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class BBBCheckSesionExpireServlet extends InsertableServletImpl {

	private String cartPage;
	private String loginPage;
	private LblTxtTemplateManager mMsgHandler;
	private static final String COLON = ":";
	private static final String BLANK = "";
	private static final Integer COOKIE_LOGIN_SECURITY_STATUS = Integer.valueOf(2);
	
	public String getLoginPage() {
		return loginPage;
	}

	public void setLoginPage(String loginPage) {
		this.loginPage = loginPage;
	}
	
	public String getCartPage() {
		return cartPage;
	}

	public void setCartPage(String cartPage) {
		this.cartPage = cartPage;
	}
	
	/**
	 * returns the CMS message handler to add form exceptions
	 * 
	 * @return
	 */
	public LblTxtTemplateManager getMsgHandler() {
		return mMsgHandler;
	}

	/**
	 * Sets the CMS message handler to add form exceptions
	 * 
	 * @param pMsgHandler
	 */
	public void setMsgHandler(LblTxtTemplateManager pMsgHandler) {
		mMsgHandler = pMsgHandler;
	}
	

	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		if (isLoggingDebug()) {
			logDebug("BBBCheckSesionExpireServlet.start : URI -" + pRequest.getRequestURI() + " & Context Path :: " + pRequest.getContextPath());
		}
		 ComponentName mProfileComponentName = ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE);
		Profile profile = (Profile) pRequest.resolveName(mProfileComponentName);
		Integer currentSecurityCode = (Integer) profile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		String ajaxCall =  pRequest.getParameter("fromAjax");
		String isAjaxCall= pRequest.getHeader("BBB-ajax-request");
		 boolean spcAjaxCall = Boolean.parseBoolean(isAjaxCall);
		 if ((pRequest.getRequestURI().indexOf("/checkout/checkout_single.jsp") > -1 || pRequest.getRequestURI().indexOf("/checkout/singlePage") > -1 )&& pRequest.getSession().isNew() && null == ajaxCall && spcAjaxCall) {			 
				
					logInfo("Session Expired during Checkout flow");
				
				pResponse.setHeader("BBB-ajax-redirect-url", getHost() + pRequest.getContextPath() + getCartPage());				
				clearExpceptionMsg(pRequest);
				return;
				
			}else if ((pRequest.getRequestURI().indexOf("/giftregistry/view_registry_owner.jsp") > -1)&& pRequest.getSession().isNew() && null == ajaxCall && spcAjaxCall) {			 
				
					logInfo("Session Expired during registry flow");
				
				pResponse.setHeader("BBB-ajax-redirect-url", getHost() + pRequest.getContextPath() + getLoginPage());				
				clearExpceptionMsg(pRequest);
				return;
				
			}else
		if ((pRequest.getRequestURI().indexOf("/singlePage/preview/frag") > -1 )&& pRequest.getSession().getAttribute("sessionId")==null) {
			
				logInfo("Session Expired during SPC preview flow");
			
			pResponse.setHeader("BBB-ajax-redirect-url", getHost() + pRequest.getContextPath() + getCartPage());
			pResponse.sendLocalRedirect(pRequest.getContextPath() + getCartPage(), pRequest);
			clearExpceptionMsg(pRequest);
			return;

		}else if (((pRequest.getRequestURI().indexOf("/checkout") > -1 && !COOKIE_LOGIN_SECURITY_STATUS.equals(currentSecurityCode)) || pRequest.getRequestURI().indexOf("/cart") > -1 )&& pRequest.getSession().isNew() && null == ajaxCall) {
			
				logInfo("Session Expired during Checkout flow");
			
			pResponse.setHeader("BBB-ajax-redirect-url", getHost() + pRequest.getContextPath() + getCartPage());
			pResponse.sendLocalRedirect(pRequest.getContextPath() + getCartPage(), pRequest);
			clearExpceptionMsg(pRequest);
			return;
			
		}else if (pRequest.getRequestURI().indexOf("/cart.jsp") > -1 && !pRequest.getSession().isNew()){
			
			pResponse.setHeader("BBB-ajax-redirect-url", getHost() + pRequest.getContextPath() + getCartPage());
			passRequest(pRequest, pResponse);
		} else {
			if (isLoggingDebug()) {
				logDebug("No interception due to session expiry- Pass the request to next servlet");
			}
			if(!pRequest.getRequestURI().equalsIgnoreCase("/store/TealeafTarget.jsp")){
			HttpSession session = pRequest.getSession();
	        session.setAttribute("sessionId", pRequest.getSession().getId());
		}
			passRequest(pRequest, pResponse);
		}

	}

	/**
	 * @param pRequest
	 */
	@SuppressWarnings("unchecked")
	private void clearExpceptionMsg(DynamoHttpServletRequest pRequest) {
		if (pRequest != null) {
		      NameContext ctx = pRequest.getRequestScope();
		      if (ctx != null) {
		        Vector expVector = (Vector) ctx.getElement(DropletConstants.DROPLET_EXCEPTIONS_ATTRIBUTE);
		        if(expVector != null){
		        	expVector.clear();
		        }
		      }
		    }
	}
	
	/**
	 * This method prepares host path.
	 * 
	 * @param hostKey
	 * @param pSiteId
	 * @param pRequest
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getHost()  {

		if (isLoggingDebug()) {
			logDebug("[Start]: getHost()");
		}

		String hostpath = null;
		String hostStr = null;
		StringBuilder splitStr = new StringBuilder();

		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();

		if (pRequest != null) {
			String url = pRequest.getRequestURL().toString();
			String contextPath = pRequest.getContextPath();
			String serverPort = pRequest.getServerPort() + BLANK;

			hostStr = url.split(contextPath)[0];

			if (isLoggingDebug()) {
				logDebug("url: " + url);
				logDebug("contextPath: " + contextPath);
				logDebug("hostpath after context path split: " + hostStr);
				logDebug("serverPort: " + serverPort);
			}

			splitStr.append(COLON).append(serverPort);

			if (isLoggingDebug()) {
				logDebug("SplitStr: " + splitStr.toString());
			}

			if (hostStr.contains(splitStr.toString())) {
				if (isLoggingDebug()) {
					logDebug("port found in URL");
				}
				hostpath = hostStr.split(splitStr.toString())[0];
			} else {
				if (isLoggingDebug()) {
					logDebug("NO port in URL");
				}
				hostpath = hostStr;
			}

		} else {
			if (isLoggingWarning()) {
				logWarning("Request object is null from ServletUtil.getCurrentRequest()");
			}
		}

		if (isLoggingDebug()) {
			logDebug("hostpath: " + hostpath);
			logDebug("[End]: getHost()");
		}

		return hostpath;
	}

}
