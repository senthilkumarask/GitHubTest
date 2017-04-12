package com.bbb.pipeline;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ComponentName;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.AccessControlServlet;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.affiliates.tags.MOM365TagUtils;
import com.bbb.browse.BazaarVoiceUtil;
import com.bbb.commerce.giftregistry.droplet.RegistryEventDateComparator;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.commerce.order.paypal.BBBPayPalSessionBean;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * DESCRIPTION: A pipeline servlet handled the redirection logic for
 * unauthorizes user
 * 
 * @author akhaju
 */
public class BBBAccessController extends AccessControlServlet {

	private static final String QUESTION_MARK = "?";
	private static final String KIRSCH_REDIRECT_URL = "kirschRedirectUrl";
	private Map<String, String> mRedirectUrl;
	private ProfileTools mTools;
	private Map<String, String> mNotAllowedUrl;
	private Map<String, String> cookieLoginNotAllowedUrl;
	private String mGlobalPage;
	private List<String> mSecureURL;
	private boolean mSecureEnable;
	private String mViewKickStartersPage = "/kickstarters";
	private String mMyKickStarterPage;
	private List<String> mNoCachePages;
	private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";
	private static final String GIFT_REG_SESSIONBEAN ="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean";
	private static final Integer COOKIE_LOGIN_SECURITY_STATUS = Integer.valueOf(2);
	private static final String BBB_BUSINESS = "BBBBusiness";
	private static final String EXCEPTION_CHECK_USER_TOKEN_BVRR = "Exception - BBBProfileFormHandler.checkUserTokenBVRR()";
	/**
	 * @return the mNoCachePages
	 */
	public List<String> getNoCachePages() {
		return mNoCachePages;
	}

	/**
	 * @param mNoCachePages the mNoCachePages to set
	 */
	public void setNoCachePages(List<String> pNoCachePages) {
		mNoCachePages = pNoCachePages;
	}

	/**
	 * @return the secureEnable
	 */
	public boolean isSecureEnable() {
		return mSecureEnable;
	}

	/**
	 * @param pSecureEnable
	 *            the secureEnable to set
	 */
	public void setSecureEnable(boolean pSecureEnable) {
		mSecureEnable = pSecureEnable;
	}

	/**
	 * @return the secureURL
	 */
	public List<String> getSecureURL() {
		return mSecureURL;
	}

	/**
	 * @param pSecureURL
	 *            the secureURL to set
	 */
	public void setSecureURL(List<String> pSecureURL) {
		mSecureURL = pSecureURL;
	}
	/**
	 * to hold my kick starter jsp page name
	 * 
	 * @return mMyKickStarterPage
	 */
	public String getMyKickStarterPage() {
		return mMyKickStarterPage;
	}

	/**
	 *  to hold my kick starter jsp page name
	 *  
	 * @param pMyKickStarterPage
	 */
	public void setMyKickStarterPage(String pMyKickStarterPage) {
		mMyKickStarterPage = pMyKickStarterPage;
	}
	/**
	 * to return jsp page name of view all kick starters
	 * @return mViewKickStarterPage
	 */
	public String getViewKickStartersPage() {
		return mViewKickStartersPage;
	}

	/**
	 *  to hold view all kick starter jsp page name
	 *  
	 * @param pViewKickStarterPage
	 */
	public void setViewKickStartersPage(String pViewKickStartersPage) {
		mViewKickStartersPage = pViewKickStartersPage;
	}
	/**
	 * @return mGlobalPage
	 */
	public String getGlobalPage() {
		return mGlobalPage;
	}

	/**
	 * @param pGlobalPage
	 */
	public void setGlobalPage(String pGlobalPage) {
		mGlobalPage = pGlobalPage;
	}

	private ComponentName mProfileComponentName;

	/**
	 * @return mNotAllowedUrl
	 */
	public Map<String, String> getNotAllowedUrl() {
		return mNotAllowedUrl;
	}

	/**
	 * @param pNotAllowedUrl
	 */
	public void setNotAllowedUrl(Map<String, String> pNotAllowedUrl) {
		mNotAllowedUrl = pNotAllowedUrl;
	}

	/**
	 * @return mRedirectUrl
	 */
	public Map<String, String> getRedirectUrl() {
		return mRedirectUrl;
	}

	/**
	 * @param pRedirectUrl
	 */
	public void setRedirectUrl(Map<String, String> pRedirectUrl) {
		mRedirectUrl = pRedirectUrl;
	}

	/**
	 * @return mTools
	 */
	public ProfileTools getTools() {
		return mTools;
	}

	/**
	 * @param pTools
	 */
	public void setTools(ProfileTools pTools) {
		mTools = pTools;
	}
	
	
	/**
	 * OverRideded method from ATG OOTB AccessControlerServlet and does
	 * redirection task according to the business rules i.e when the user not
	 * logged in and try to access the authorized page then it user will be
	 * redirect to the deniedUrl. *
	 * 
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
	public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws IOException, ServletException {
 		String methodName = BBBCoreConstants.SERVICE;
		BBBPerformanceMonitor.start(BBBPerformanceConstants.ACCESS_CONTROLLER, methodName);
			if (isLoggingDebug()) {
				logDebug("BBBAccessController.service() method started");
			}
			String contextPath = pRequest.getContextPath();
			String uri = pRequest.getRequestURI().toLowerCase();
			String originalURL = (String)pRequest.getAttribute(BBBCoreConstants.REQUESTURI_QUERY_STRING);
			String uriWithoutContextPath = null;
			String[] array = uri.split(BBBCoreConstants.SLASH, 3);
			boolean findStore = true;
			String ajaxCall= pRequest.getHeader("BBB-ajax-request");
			if(BBBUtility.isNotEmpty(originalURL)){
				if(originalURL.contains(BBBCoreConstants.FIND_STORE)){
					findStore = false;
				}
			}
            
            if(array!= null &&  array.length > 2){
            	uriWithoutContextPath = BBBCoreConstants.SLASH + array[2];
            }
            mProfileComponentName = ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE);
			Profile profile = (Profile) pRequest.resolveName(mProfileComponentName);
			Integer currentSecurityCode = profileSecurityStatus(profile);
			
			String deniedUrl = contextPath + getDeniedAccessURL();
			String notAllowed = mNotAllowedUrl.get(uriWithoutContextPath);
			String mapUrl = mRedirectUrl.get(uriWithoutContextPath);
			String pageName = pRequest.getParameter(BBBCoreConstants.PAGE_NAME);
			boolean noCachePage = mNoCachePages.contains(uriWithoutContextPath);
				
			boolean isSecure = pRequest.isSecure();
			if (isSecure && mSecureEnable) {
				pRequest.setScheme(BBBCoreConstants.HTTPS);
			}
			//set Mom365 cookie
			if(MOM365TagUtils.isMom365Enabled()){
				MOM365TagUtils.checkAndSetMom365Cookie(pRequest,pResponse);
			}
			String registry_Success_Back_Btn_Url_Flag = (String) pRequest.getSession().getAttribute(BBBCoreConstants.REGISTRY_SUCCESS_BACK_BTN_URL_FLAG);
			if(	registry_Success_Back_Btn_Url_Flag==null){
				registry_Success_Back_Btn_Url_Flag=BBBCoreConstants.REGISTRY_BACK_BTN_FLAG_REDIRECT_NONE;
			}
			//added no-cache for some pages.
			if(isSecure || noCachePage){
				pResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
				pResponse.setHeader( "Pragma", "no-cache" );
	
			}
			//Recognized User flow
			if(COOKIE_LOGIN_SECURITY_STATUS.equals(currentSecurityCode) && !contextPath.equalsIgnoreCase(BBBCoreConstants.CONTEXT_REST)
					&& !contextPath.equalsIgnoreCase(BBBCoreConstants.CONTEXT_TBS)) {
				if (isLoggingDebug()) {
					logDebug("BBBAccessController.service() Recognized user");
				}
				String userTokenBVRR = null;
				  try {
					  final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
                              .resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		           if(sessionBean != null && sessionBean.getValues().get("registrySkinnyVOList") == null){
		                  List<RegistrySkinnyVO> registrySkinnyVOList = getGiftRegistryManager()
		                                     .getAcceptableGiftRegistries(profile, SiteContextManager.getCurrentSiteId());
		                  if(registrySkinnyVOList !=  null && registrySkinnyVOList.size() > 0){
		                              RegistryEventDateComparator eventDateComparator = new RegistryEventDateComparator();
		                              eventDateComparator.setSortOrder(2);
		                              Collections.sort(registrySkinnyVOList, eventDateComparator);
		                              sessionBean.getValues().put("registrySkinnyVOList", registrySkinnyVOList);
		                              sessionBean.getValues().put("size", registrySkinnyVOList.size());
		                  }
		           }

					  BBBProfileFormHandler profileFormHandler=(BBBProfileFormHandler) pRequest.resolveName(BBBCoreConstants.PROFILE_FORM_HANDLER);
					  String emailId=(String) profile.getPropertyValue(BBBCoreConstants.EMAIL);
			            userTokenBVRR = BazaarVoiceUtil.createUserTokenBVRR(BazaarVoiceUtil.generateMD5((profileFormHandler.getEmailMD5HashPrefix())+emailId.toLowerCase()),
			            		profileFormHandler.getBazaarVoiceKey(),emailId);
			            if (userTokenBVRR.equalsIgnoreCase(BBBCoreConstants.BAZAAR_VOICE_ERROR)) {
			                throw new BBBBusinessException(BBBCoreErrorConstants.ACCOUNT_ERROR_1275,
			                        BBBCoreConstants.BAZAAR_VOICE_ERROR);
			            }
			        
					} catch (BBBBusinessException e) {
						 this.logError(
	                                LogMessageFormatter.formatMessage(pRequest, BBB_BUSINESS
	                                        + EXCEPTION_CHECK_USER_TOKEN_BVRR, BBBCoreErrorConstants.ACCOUNT_ERROR_1023), e);
	                    
					} catch (BBBSystemException e) {
						 this.logError(
	                                LogMessageFormatter.formatMessage(pRequest, BBB_BUSINESS
	                                        + EXCEPTION_CHECK_USER_TOKEN_BVRR, BBBCoreErrorConstants.ACCOUNT_ERROR_1023), e);
	                    
					} catch (RepositoryException e) {
						this.logError("GiftRegistryTools.getUserRegistryList() :: Repository exception while list the registry " + e);
					} finally {
			            BBBPerformanceMonitor.end(BBBPerformanceConstants.CHECKUSER_TOKEN_BVRR, methodName);
			        }
			    pRequest.getSession().setAttribute(BBBCoreConstants.USER_TOKEN_BVRR, userTokenBVRR);
				String redirectURL= getCookieLoginNotAllowedUrl().get(uriWithoutContextPath);
				DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
				boolean paypalOrder = false;
				if(null!= request){
					BBBPayPalSessionBean paypalSessionBean = (BBBPayPalSessionBean)request.resolveName(BBBPayPalConstants.PAYPAL_SESSION_BEAN_PATH);
					if(null!= paypalSessionBean && null != paypalSessionBean.getGetExpCheckoutResponse()){
						paypalOrder = true;
					}
				}
				if(!BBBUtility.isEmpty(uriWithoutContextPath))
				{
				if((BBBUtility.isNotEmpty(redirectURL) && findStore) || (uriWithoutContextPath.contains(BBBCoreConstants.CHECKOUT) && !uriWithoutContextPath.contains(BBBCoreConstants.ENVOY_CHECKOUT) && !paypalOrder && !uriWithoutContextPath.contains(BBBCoreConstants.SITE_SPECT_JSP))){
					uri = pRequest.getRequestURIWithQueryString();
					pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL, uri);
					final String favStoreId = pRequest.getParameter(BBBCoreConstants.MAKE_FAVOURITE_STORE_ID);
					if(!BBBUtility.isEmpty(favStoreId))
					{
						pRequest.getSession().setAttribute(BBBCoreConstants.MAKE_FAVOURITE_STORE_ID, favStoreId);
					}
					if(BBBUtility.isEmpty(redirectURL) && uriWithoutContextPath.contains(BBBCoreConstants.CHECKOUT)){
						redirectURL = BBBCoreConstants.LOGIN_JSP+QUESTION_MARK+"checkout=true";
					} else if(BBBUtility.isEmpty(redirectURL)){
						redirectURL=BBBCoreConstants.LOGIN_JSP;
					}
					if ((pRequest.getRequestURI().indexOf(BBBCoreConstants.VIEW_REGISTRY_OWNER_PAGE) > -1) || (pRequest.getRequestURI().indexOf(BBBCoreConstants.HARTE_HANKS_MODAL) > -1)) {
						if(!BBBUtility.isEmpty(getRedirectUrl().get(uriWithoutContextPath))){
							pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL, contextPath + getRedirectUrl().get(uriWithoutContextPath));
						}
						if (isLoggingDebug()) { 
							logDebug("Session Expired during registry flow");
						}
						pResponse.setHeader("BBB-ajax-redirect-url", contextPath+redirectURL);
						if(null == ajaxCall){
						    pResponse.sendRedirect(contextPath + redirectURL);
						}
						return;
						
					}
					pResponse.sendRedirect(contextPath + redirectURL);
				}
				}
			}
			else{
					if(uri!=null && uri.contains("simpleReg_creation_form.jsp") && BBBCoreConstants.REGISTRY_BACK_BTN_FLAG_REDIRECT_NONE.equals(registry_Success_Back_Btn_Url_Flag)){
					pRequest.getSession().setAttribute(BBBCoreConstants.REGISTRY_SUCCESS_BACK_BTN_URL, pRequest.getHeader(BBBCoreConstants.REFERRER));
					pRequest.getSession().setAttribute(BBBCoreConstants.REGISTRY_SUCCESS_BACK_BTN_URL_FLAG, BBBCoreConstants.REGISTRY_BACK_BTN_FLAG_REDIRECT_SET);
				}else if(!uri.contains("simpleReg_creation_form.jsp") &&  BBBCoreConstants.REGISTRY_BACK_BTN_FLAG_REDIRECT_ENABLED.equals(registry_Success_Back_Btn_Url_Flag) ){
					pRequest.getSession().setAttribute(BBBCoreConstants.REGISTRY_SUCCESS_BACK_BTN_URL_FLAG, BBBCoreConstants.REGISTRY_BACK_BTN_FLAG_REDIRECT_NONE);
				}
				if ((mapUrl != null && deniedUrl != null) || notAllowed != null) {
					if (profile != null) {
						if (!profile.isTransient() && notAllowed != null) {
							pResponse.sendRedirect(contextPath + notAllowed);
						} else if (profile.isTransient() && notAllowed == null) {
							uri = pRequest.getRequestURIWithQueryString();
							pRequest.getSession().setAttribute(BBBCoreConstants.REDIRECT_URL, uri);
							String kirschRedirectUrl = pRequest.getParameter(KIRSCH_REDIRECT_URL);
							if (!BBBUtility.isEmpty(kirschRedirectUrl)) {
								pRequest.getSession().setAttribute(KIRSCH_REDIRECT_URL, kirschRedirectUrl);
							}
							if (!BBBUtility.isEmpty(mapUrl)) {
								deniedUrl = contextPath + mapUrl;
							}
							if(!BBBUtility.isEmpty(pRequest.getQueryString())){
								deniedUrl=deniedUrl+QUESTION_MARK+pRequest.getQueryString();
									if(uriWithoutContextPath!=null && uriWithoutContextPath.contains(BBBCoreConstants.VIEW_REGISTRY_OWNER)){
									deniedUrl=deniedUrl+BBBCoreConstants.TARGET_PARAMETER;
							}
							}
							pResponse.sendRedirect(deniedUrl);
						}
					}
				} else {
	
					Object removeSes = pRequest.getParameter(BBBCoreConstants.REDIRECT_PARAM_NAME);
					if (removeSes != null && removeSes.toString().equalsIgnoreCase(BBBCoreConstants.REDIRECT_PARAM_VALUE)) {
						Object redirectURL = pRequest.getSession().getAttribute(BBBCoreConstants.REDIRECT_URL);
						if (redirectURL != null) {
							pRequest.getSession().removeAttribute(BBBCoreConstants.REDIRECT_URL);
							if (pRequest.getSession().getAttribute(KIRSCH_REDIRECT_URL) != null) {
								pRequest.getSession().removeAttribute(KIRSCH_REDIRECT_URL);
							}
						}
					}
				}
				// Checking if it is coming for bazaar voice write review from
				// product detail page
				if (BBBCoreConstants.YES.equalsIgnoreCase(pRequest.getParameter(BBBCoreConstants.BV_DO_LOGIN))) {
					pRequest.getSession().setAttribute(BBBCoreConstants.BV_WRITE_REVIEW_URL, pRequest.getRequestURIWithQueryString());
					pRequest.getSession().setAttribute(BBBCoreConstants.RETURN_PAGE, pRequest.getParameter(BBBCoreConstants.RETURN_PAGE));
					pRequest.getSession().setAttribute(BBBCoreConstants.PAGE_NAME, pRequest.getParameter(BBBCoreConstants.PAGE_NAME));
				}
			}
				
			
			if (isLoggingDebug()) {
				logDebug("BBBAccessController.service() method ends");
			}
			BBBPerformanceMonitor.end(BBBPerformanceConstants.ACCESS_CONTROLLER, methodName);
			super.service(pRequest, pResponse);
		

	}
	/**
	 * Gets the gift registry manager.
	 * 
	 * @return mGiftRegMgr
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegMgr;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegMgr
	 *            the new gift registry manager
	 */
	public void setGiftRegistryManager(final GiftRegistryManager pGiftRegMgr) {
		mGiftRegMgr = pGiftRegMgr;
	}
	/** to hold Gift Registry */
	private GiftRegistryManager mGiftRegMgr;
	 /**
		 * Return The profile Security Status
		 * @param profile
		 * @return
		 */
	private static Integer profileSecurityStatus(final Profile profile) {
		  final Integer securityStatus = (Integer) profile.getPropertyValue(BBBCheckoutConstants.SECURITY_STATUS);
		   return securityStatus;

	 }
	 /**
	 * Return The cookieLoginNotAllowedUrl
	 * Map of urls not allowed for recognized users
	 * @return
	 */
	public Map<String, String> getCookieLoginNotAllowedUrl() {
		return cookieLoginNotAllowedUrl;
	}
	/**
	 * Sets the cookieLoginNotAllowedUrl.
	 * 
	 * @param cookieLoginNotAllowedUrl
	 *            Map of urls not allowed for recognized users
	 */
	public void setCookieLoginNotAllowedUrl(
		Map<String, String> cookieLoginNotAllowedUrl) {
		this.cookieLoginNotAllowedUrl = cookieLoginNotAllowedUrl;
	}	
	
}