package com.bbb.pipeline;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.formhandler.InternationalShipFormHandler;
import com.bbb.internationalshipping.utils.InternationalShipTools;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * The class is a servlet to check if the request is from google adds
 * 
 *
 */
public class InternationalReferrerUserDetectionServlet extends InsertableServletImpl {


	private InternationalShipTools tools;
	private boolean enableServlet;
	/**
	 * @return is this Servlet is enabled
	 */
	public boolean isEnableServlet() {
		return enableServlet;
	}

	/**
	 * @param boolean the enableServlet to set
	 */
	public void setEnableServlet(boolean enableServlet) {
		this.enableServlet = enableServlet;
	}

	/**
	 * @return the InternationalShipTools
	 */
	public InternationalShipTools getTools() {
		return tools;
	}

	/**
	 * @param InternationalShipTools the tools to set
	 */
	public void setTools(InternationalShipTools tools) {
		this.tools = tools;
	}

	/**
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException
	 *             if there was an error while executing the code
	 * @throws IOException
	 *             if there was an error with servlet io
	 * @return void
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {

		if (isLoggingDebug()) {
			logDebug(" International Referrer user Detection Servlet service method : Begin ");
		}
		Boolean isIntShipOn = this.getTools().checkIntSwitch();
		if(isEnableServlet() && isIntShipOn && (BBBCoreConstants.CONTEXT_STORE.equals(pRequest.getContextPath()) || 
				BBBCoreConstants.CONTEXT_REST.equals(pRequest.getContextPath())) && pRequest.getParameter(BBBInternationalShippingConstants.RESET_GOOGLE_ADDFLOW)==null){
			String mcid = pRequest
					.getParameter(BBBInternationalShippingConstants.MCID);
			String mcidRest = pRequest
					.getHeader(BBBInternationalShippingConstants.MCID);
			String googleFlowRest = pRequest
					.getHeader(BBBInternationalShippingConstants.GOOGLE_FLOW);
			if (((pRequest.getSession().getAttribute(BBBInternationalShippingConstants.GOOGLE_FLOW) != null && ((String) pRequest
					.getSession().getAttribute(BBBInternationalShippingConstants.GOOGLE_FLOW))
					.equalsIgnoreCase(BBBCoreConstants.TRUE)) || BBBUtility
					.isNotEmpty(googleFlowRest)) || ((BBBUtility.isNotEmpty(mcid) && mcid
							.contains(BBBInternationalShippingConstants.GOOGLE)) || (BBBUtility
							.isNotEmpty(mcidRest)))) {
				pRequest.getSession()
				.setAttribute(BBBInternationalShippingConstants.GOOGLE_FLOW,
						BBBCoreConstants.TRUE);
				/*BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.resolveName(BBBCoreConstants.SESSION_BEAN);*/
				
				BBBSessionBean sessionBean = (BBBSessionBean) pRequest
						.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME);
				if (sessionBean == null) {
					sessionBean = (BBBSessionBean)pRequest.resolveName(BBBCoreConstants.SESSION_BEAN);
					pRequest.setAttribute(BBBCoreConstants.SESSION_BEAN_NAME, sessionBean);
				}
				
				
				HashMap sessionMap = sessionBean.getValues();
				String previousCountry = (String) sessionMap
						.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
				if ((BBBUtility.isEmpty(previousCountry) || (BBBUtility
								.isNotEmpty(previousCountry) && !previousCountry
								.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY)))) {
					sessionMap
							.put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT,
									BBBInternationalShippingConstants.DEFAULT_COUNTRY);
					sessionMap
							.put(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CURRENCY,
									BBBInternationalShippingConstants.CURRENCY_USD);
					sessionBean.setInternationalShippingContext(false);
					Profile profile = (Profile) pRequest
							.resolveName(BBBCoreConstants.ATG_PROFILE);
					profile.setPropertyValue(
							BBBInternationalShippingConstants.COUNTRY_CODE,
							BBBInternationalShippingConstants.DEFAULT_COUNTRY);
					profile.setPropertyValue(
							BBBInternationalShippingConstants.CURRENCY_CODE,
							BBBInternationalShippingConstants.CURRENCY_USD);
					profile.setPropertyValue(
							BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT,
							false);
					if (isLoggingDebug()) {
						logDebug("Call from Google flow");
					}
					try {
						boolean reprice = updateInternationalCookie(pRequest,
								pResponse);
						if (reprice) {
							InternationalShipFormHandler isHandler = (InternationalShipFormHandler) pRequest
									.resolveName(BBBInternationalShippingConstants.INTERNATIONALSHIPFORMHANDLER);
							isHandler.repriceReffererOrder(pRequest, pResponse,previousCountry);
						}
					} catch (BBBSystemException e) {
						logError(
								LogMessageFormatter
										.formatMessage(
												null,
												"BBBSystemException from service of InternationalReferrerUserDetectionServlet :"),
								e);
					} catch (BBBBusinessException e) {
						logError(
								LogMessageFormatter
										.formatMessage(
												null,
												"BBBBusinessException from service of InternationalReferrerUserDetectionServlet :"),
								e);
					}
	
				}
			}
		}
		passRequest(pRequest, pResponse);
	}

		/**
		 * This method gets the default country selected by user from cookie.
		 *
		 * @param pRequest the new checkout param
		 * @return Cookie
		 * @throws BBBSystemException the BBB system exception
		 * @throws BBBBusinessException the BBB business exception
		 */
		public boolean updateInternationalCookie(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse ) throws BBBSystemException, BBBBusinessException {

			if(isLoggingDebug()){
				this.logDebug(LogMessageFormatter.formatMessage(null,
						"Inside  method of InternationalReferrerUserDetectionServlet : updateInternationalCookie"));
			}
			Cookie cookie=null;
			boolean reprice=true;
			cookie=this.getTools().getIntShipCookieAvailable(pRequest);
			if(cookie !=null){
				if(isLoggingDebug()){
					logDebug(" International cookie is set with value :"+cookie.getValue());
				}
				if(cookie.getValue().contains(BBBInternationalShippingConstants.DEFAULT_COUNTRY+BBBCoreConstants.COLON) && cookie.getValue().contains(BBBInternationalShippingConstants.CURRENCY_USD)){
					reprice=false;
				}
				else{
					cookie.setDomain(pRequest.getServerName());
					cookie.setPath("/");
					final StringBuffer cookieValue=new StringBuffer("");
					cookieValue.append(BBBInternationalShippingConstants.DEFAULT_COUNTRY).append(BBBCoreConstants.COLON).append(BBBInternationalShippingConstants.CURRENCY_USD);
					cookie.setMaxAge(0);
					cookie.setValue(cookieValue.toString());
					BBBUtility.addCookie(pResponse, cookie, false);
				}
			}
			else{
				reprice=false;
			}
			this.logDebug(LogMessageFormatter.formatMessage(null,
					"Exit  method of InternationalReferrerUserDetectionServlet : updateInternationalCookie"));
			
			return reprice;
		}

}
