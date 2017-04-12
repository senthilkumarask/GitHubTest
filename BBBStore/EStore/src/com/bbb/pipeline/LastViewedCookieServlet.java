package com.bbb.pipeline;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * The class is a servlet that takes care of persisting 15 last viewed
 * items by user in the form of a cookie
 * @author njai13(Neha Jain)
 *
 */
public class LastViewedCookieServlet extends InsertableServletImpl {

	private SiteContextManager siteContextManager;
	private String cookieLastName;
	private int cookieMaxAge;


	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}

	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}



	public String getCookieLastName() {
		return cookieLastName;
	}

	public void setCookieLastName(String cookieLastName) {
		this.cookieLastName = cookieLastName;
	}


	public int getCookieMaxAge() {
		return cookieMaxAge;
	}

	public void setCookieMaxAge(int cookieMaxAge) {
		this.cookieMaxAge = cookieMaxAge;
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

		Cookie cookie = null;
		//the cookie  name is <siteId><cookieLastName>
		final String siteId = SiteContextManager.getCurrentSiteId();
		final String cookieName=siteId+getCookieLastName();
		if(isLoggingDebug()){
			logDebug(" LastViewedCookieServlet service method last viewed cookie name  "+cookieName);
		}
		final BBBSessionBean sessionBean =
				(BBBSessionBean)pRequest.resolveName("/com/bbb/profile/session/SessionBean");
		final HashMap  sessionMap=sessionBean.getValues();
		final List <String>lvPrdtListFrmSession=(List<String>)sessionMap.get(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST);
		//get all cookies from request and set cookie to the lastviewed cookie
		final Cookie[] cookies = pRequest.getCookies();

		if(cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(cookieName)) {
					cookie = cookies[i];
					break;
				}
			}
		}
		if(isLoggingDebug()){
			logDebug("last viewed cookie value "+ cookie);
		}

		/*
		 * if cookie is already set for the user then get the list of last viewed product list from session bean
		 * if the list is not empty set the value of the cookie to product list in session bean delimited by : delimiter 
		 * if cookie is not set that means cookie has expired or its user's first session or cookie is deleted by user 
		 * In this case get product list from session bean if any and create a new cookie with cookie value as : delimited list of 
		 * last viewed products id.
		 */
		if(cookie!=null){

			if(isLoggingDebug()){
				logDebug("Cookie is already present with the user last.list of last  viewed products from session bean is "+lvPrdtListFrmSession);
			}
			if(lvPrdtListFrmSession!=null && !lvPrdtListFrmSession.isEmpty()){
				final String cookieValue=this.getCookieValueFrmSession(lvPrdtListFrmSession);
				if(isLoggingDebug()){
					logDebug(" list of last  viewed products from session bean is not null adding the list as" +
							" cookie value String delimited by : Cookie value to set from session bean is "+cookieValue);
				}
				cookie=this.getLVCookie(cookie, cookieValue, cookieName, pRequest);
				BBBUtility.addCookie(pResponse, cookie, true);
				//pResponse.addCookie(cookie);
			}
			/*
			 * if list of last  viewed products in session is empty that means its a fresh session.
			 * So add value of cookie into session bean as list of product ids last viewed by user
			 */
			else{
				final List <String>lastViewedPrdtListInCookie=getLVPrdtListFrmCookie( cookie);
				if(lastViewedPrdtListInCookie!=null && !lastViewedPrdtListInCookie.isEmpty()){
					if(isLoggingDebug()){
						logDebug(" Last viewed products list stored in cookie is Not empty ,adding the list in session bean value in cookie "+lastViewedPrdtListInCookie);
					}
					sessionBean.getValues().put(BBBSearchBrowseConstants.LAST_VIEWED_PRODUCT_ID_LIST, lastViewedPrdtListInCookie);
				}
				else if(isLoggingDebug()){
					logDebug(" Last viewed products list stored in cookie is empty Not adding the list in session bean");
				}
			}
		}
		/*
		 * if cookie is null that means either user has deleted cookie or is visiting for first time or visiting after last cookie has expired.
		 * add new cookie and if something is there in session (like if the user has deleted cookie while he is still browsing through the site) add the
		 * list of product id  as value of cookie
		 */
		else{
			if(isLoggingDebug()){
				logDebug("Cookie is not set with the user last Need to set new cookie..list of last  viewed products from session bean is "+lvPrdtListFrmSession);
			}
			final String cookieValue=this.getCookieValueFrmSession(lvPrdtListFrmSession);
			cookie=this.getLVCookie(cookie, cookieValue, cookieName, pRequest);
			BBBUtility.addCookie(pResponse, cookie, true);
		}
		passRequest(pRequest, pResponse);

	}
	/**
	 * the value in cookie is a String of last viewed  product ids delimited by : 
	 * The  method converts the String into list of product Ids to be added 
	 * to session Bean 
	 * @param lastViewedCookie
	 * @return
	 */
	private List<String> getLVPrdtListFrmCookie(Cookie lastViewedCookie){
		final String lvListFrmCookie= lastViewedCookie.getValue();
		if(!StringUtils.isEmpty(lvListFrmCookie)){
			final String[] lvPrdts=lvListFrmCookie.split(":");

			return Arrays.asList(lvPrdts);
		}
		return null;

	}
	/**
	 * The session bean stores last viewed items as list of product ids
	 * The method converts the list into a String of Product Ids delimited by :
	 * to be stored as value in cookie
	 * @param lvPrdtListFrmSession
	 * @return
	 */
	private String getCookieValueFrmSession(List<String> lvPrdtListFrmSession){
		final StringBuffer cookieValue=new StringBuffer("");
		if(lvPrdtListFrmSession!=null && !lvPrdtListFrmSession.isEmpty()){
			for(String prdtId:lvPrdtListFrmSession){
				cookieValue.append(prdtId).append(":");
			}
			cookieValue.deleteCharAt(cookieValue.lastIndexOf(":"));
		}
		return cookieValue.toString();
	}
	/**
	 * The method creates a unique cookie having value as 
	 * last viewed products is delimited by :
	 * The cookie is set separately for each site
	 * this is taken care by cookie name that has site is appended to it
	 * @param cookie
	 * @param cookieValue
	 * @param cookieName
	 * @param pRequest
	 * @return
	 */
	private Cookie getLVCookie(Cookie cookie,String cookieValue,String cookieName,DynamoHttpServletRequest pRequest ){
		if(cookie ==null){
			cookie = new Cookie(cookieName, cookieValue);
		}
		else{
			cookie.setValue(cookieValue);
		}

		String path= pRequest.getParameter("path");		 
		String domain= pRequest.getParameter("domain");
		if(BBBUtility.isEmpty(domain)){
			if(pRequest.getServerName()!=null)
			{
				domain=pRequest.getServerName();
			}
			else
			{
				domain="";
			}
		}
		if(BBBUtility.isEmpty(path)){
			path="/";
		}

		if(isLoggingDebug()){
			final StringBuffer debug=new StringBuffer(50);
			debug.append(" Cookie set with followinmg parameters cookie Name ").append(cookieName).append(" cookie Value ").append(cookieValue)
			.append(" max age of cookie ").append(getCookieMaxAge()).append(" domain " ).append(domain).append(" path ").append(path);
			logDebug(debug.toString());
		}
		cookie = new Cookie(cookieName, cookieValue);
		cookie.setMaxAge(getCookieMaxAge());
		cookie.setDomain(domain);
		cookie.setPath(path);
		return cookie;

	}
}
