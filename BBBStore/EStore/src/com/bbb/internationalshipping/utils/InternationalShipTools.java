/*
 * 
 */
package com.bbb.internationalshipping.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.internationalshipping.vo.BBBInternationalContextVO;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

/**
 * The class has utility methods for international shipping 
 * All the business logic resides in this class.
 *
 * 
 */
public class InternationalShipTools extends BBBGenericService {

	/** The catalog tools. */
	private BBBCatalogTools catalogTools;
	
	/** The default country. */
	private String defaultCountry;
	
	/** The site context manager. */
	private SiteContextManager siteContextManager;
	
	/** The international shipping builder. */
	private BBBInternationalShippingBuilder internationalShippingBuilder;

	/** The cookie last name. */
	private String cookieLastName;

	/**
	 * Gets the default country.
	 *
	 * @return the default country
	 */
	public String getDefaultCountry() {
		return defaultCountry;
	}
	
	
	/**
	 * Sets the default country.
	 *
	 * @param defaultCountry the new default country
	 */
	public void setDefaultCountry(String defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	/**
	 * Gets the cookie last name.
	 *
	 * @return the cookie last name
	 */
	public String getCookieLastName() {
		return cookieLastName;
	}
	
	
	/**
	 * Sets the cookie last name.
	 *
	 * @param cookieLastName the new cookie last name
	 */
	public void setCookieLastName(String cookieLastName) {
		this.cookieLastName = cookieLastName;
	}
	
	
	
	/**
	 * Gets the site context manager.
	 *
	 * @return the site context manager
	 */
	public SiteContextManager getSiteContextManager() {
		return siteContextManager;
	}
	
	
	/**
	 * Sets the site context manager.
	 *
	 * @param siteContextManager the new site context manager
	 */
	public void setSiteContextManager(SiteContextManager siteContextManager) {
		this.siteContextManager = siteContextManager;
	}
	

	/**
	 * Gets the catalog tools.
	 *
	 * @return the catalog tools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	
	/**
	 * Sets the catalog tools.
	 *
	 * @param catalogTools the new catalog tools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	/**
	 * Gets the international shipping builder.
	 *
	 * @return the international shipping builder
	 */
	public BBBInternationalShippingBuilder getInternationalShippingBuilder() {
		return internationalShippingBuilder;
	}
	
	/**
	 * Sets the international shipping builder.
	 *
	 * @param internationalShippingBuilder the new international shipping builder
	 */
	public void setInternationalShippingBuilder(
			BBBInternationalShippingBuilder internationalShippingBuilder) {
		this.internationalShippingBuilder = internationalShippingBuilder;
	}
	
	/**
	 * The method gets the value of country currency from already existing cookie
	 * the value in cookie is the last selected country and currency delimited by : 
	 * The  method converts the String into list of String storing the country
	 * and the other the currency.
	 *
	 * @param internatShipCookie the internat ship cookie
	 * @return List<String>
	 */
	public List<String> getDefaultFrmCookie(Cookie internatShipCookie){
		if(internatShipCookie!=null){
			final String valuesFrmCookie= internatShipCookie.getValue();
			logDebug((new StringBuffer(" the international ship cookie ").append(internatShipCookie.getPath())
						.append(internatShipCookie.getDomain()).append( internatShipCookie.getName()).append("has value : ").append(valuesFrmCookie).toString())) ;
			if(!StringUtils.isEmpty(valuesFrmCookie)){
				final String[] defaultValue=valuesFrmCookie.split(BBBCoreConstants.COLON);

				return Arrays.asList(defaultValue);
			}
		}
		return null;

	}
	
	/**
	 * The method sets the value in the international cookie
	 * the value in cookie is the last selected country and currency delimited by : .
	 *
	 * @param defaultCountry the default country
	 * @param defaultCurrency the default currency
	 * @return String
	 */

	public String getCookieValueFrmSession(String defaultCountry,String defaultCurrency){
		final StringBuffer cookieValue=new StringBuffer("");
		if(!StringUtils.isEmpty(defaultCountry) && !StringUtils.isEmpty(defaultCurrency)){
			cookieValue.append(defaultCountry).append(BBBCoreConstants.COLON).append(defaultCurrency);
			logDebug(" Value to be set in the international cookie is "+cookieValue.toString());
		}
		return cookieValue.toString();
	}

	/**
	 * The method creates the name of the international cookie.
	 *
	 * @return the cookie full name
	 */

	public String getCookieFullName(){

		//the cookie  name is <siteId><cookieLastName>
		final String siteId = SiteContextManager.getCurrentSiteId();
		return siteId+getCookieLastName();
	}

	/**
	 * The method is used to set the international cookie with all the details.
	 *
	 * @param defaultCountry the default country
	 * @param defaultCurrency the default currency
	 * @param pRequest the request
	 * @return Cookie
	 */
	public Cookie setInternationalShipCookie(String defaultCountry,String defaultCurrency,DynamoHttpServletRequest pRequest ){
		logDebug(" Enter method: setInternationalShipCookie with parameters defaultCountry: "+defaultCountry +" defaultCurrency: "+defaultCurrency);
		String cookieValue=this.getCookieValueFrmSession(defaultCountry, defaultCurrency);

		final String cookieName=getCookieFullName();
		final Cookie cookie = new Cookie(cookieName, cookieValue);

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

		
		
		List<String> intShipCookieValue=null;
		int cookieMaxAge=0;

		try {
			intShipCookieValue = this.getCatalogTools().getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_COOKIE_MAX_AGE);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InternationalShipTools : cookieMaxAge"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of InternationalShipTools : cookieMaxAge"), e);

		}

		if(intShipCookieValue!=null && !intShipCookieValue.isEmpty()){
			cookieMaxAge=Integer.valueOf(intShipCookieValue.get(0));
		}
		final StringBuffer debug=new StringBuffer(50);
		debug.append(" Cookie set with followinmg parameters cookie Name ").append(cookieName).append(" cookie Value ").append(cookieValue)
		.append(" max age of cookie ").append(cookieMaxAge).append(" domain " ).append(domain).append(" path ").append(path);
		logDebug(debug.toString());

		logDebug(" cookieMaxAge .. "+cookieMaxAge);
		cookie.setMaxAge(cookieMaxAge);
		cookie.setDomain(domain);
		cookie.setPath(path);
		return cookie;

	}
	
	/**
	 * The method checks if international shipping feature is switched on .
	 *
	 * @return the boolean
	 */
	public Boolean  checkIntSwitch(){
		List<String> intShipSwitchValue=null;
		Boolean isIntShipOn=false;
		try {
			intShipSwitchValue = this.getCatalogTools().getAllValuesForKey(BBBInternationalShippingConstants.INTERNATIONAL_SHIPPING,
					BBBInternationalShippingConstants.CONFIG_KEY_INTERNATIONAL_SHIP_SWITCH);
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InternationalShipTools : checkIntSwitch"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of InternationalShipTools : checkIntSwitch"), e);

		}

		if(intShipSwitchValue!=null && !intShipSwitchValue.isEmpty()){
			isIntShipOn=Boolean.valueOf(intShipSwitchValue.get(0));
		}
		logDebug(" is international shipping on ? "+isIntShipOn);
		return isIntShipOn;
	}

	/**
	 * The method gets the country code of the country to which the user IP belongs.
	 *
	 * @param ipAddress the ip address
	 * @return the country code from vo
	 */

	public String getCountryCodeFromVO(String ipAddress){
		logDebug(" enter method getCountryCodeFromVO :IpAddress "+ipAddress);
		try {
			final BBBInternationalContextVO contextVO=this.getInternationalShippingBuilder().buildContextFromIP(ipAddress);
			if(contextVO!=null && contextVO.getShippingLocation()!=null){
				final BBBSessionBean sessionBean =
						(BBBSessionBean)ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
				sessionBean.getValues().put(BBBInternationalShippingConstants.KEY_FOR_SHIPPING_ENABLED, contextVO.getShippingLocation().isShippingEnabled());
				String countryFromIP=contextVO.getShippingLocation().getCountryCode();
				logDebug(" Country code of the Ip address is :"+countryFromIP);
				return countryFromIP;
			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InternationalShipTools : getCountryCodeFromVO"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of InternationalShipTools : getCountryCodeFromVO"), e);
		}
		logDebug(" returning default value of country code :"+this.getDefaultCountry());
		return this.getDefaultCountry();
	}

	/**
	 * The method builds the context from ambassador schema .
	 *
	 * @return the list
	 */
	public List<BBBInternationalContextVO>  buildAllContext(){
		List<BBBInternationalContextVO> contextVO=null;

		try {
			contextVO=this.getInternationalShippingBuilder().buildContextAll();
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of InternationalShipTools :buildAllContext "), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"Business Exception from service of InternationalShipTools:buildAllContext  "), e);

		}

		return contextVO;
	}
	
	/**
	 * The method checks if the international cookie is already set for the user.
	 *
	 * @param pRequest the request
	 * @return cookie
	 */

	public Cookie getIntShipCookieAvailable(final DynamoHttpServletRequest pRequest){
		logDebug(" Enter method : getIntShipCookieAvailable");
		Cookie cookie = null;

		final String cookieName=getCookieFullName();

		final Cookie[] cookies = pRequest.getCookies();

		if(cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(cookieName)) {
					cookie = cookies[i];
					logDebug(" international cookie is set for the user with value :"+cookie.getValue());
					break;
				}
			}
		}
		logDebug(" no international cookie available for the user");
		return cookie;
	}

	/**
	 * Gets the context vo.
	 *
	 * @param countryCode the country code
	 * @return the context vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public BBBInternationalContextVO getContextVO(String countryCode) throws BBBSystemException, BBBBusinessException{
		BBBInternationalContextVO contextVO=this.getInternationalShippingBuilder().buildContextBasedOnCountryCode(countryCode);

		return contextVO;
	}
	
	/**
	 * Gets the country code from session.
	 *
	 * @param pRequest the request
	 * @return the country code from session
	 */

	public String getCountryCodeFromSession(DynamoHttpServletRequest pRequest){
		logDebug(" enter method getCountryCodeFromSession ");
		String countryCode=this.getDefaultCountry();
		final BBBSessionBean sessionBean =
				(BBBSessionBean)pRequest.resolveName("/com/bbb/profile/session/SessionBean");
		@SuppressWarnings("rawtypes")
		final HashMap  sessionMap=sessionBean.getValues();
		if(sessionMap!=null && !sessionMap.isEmpty()){
			countryCode=(String)sessionMap.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
			logDebug("session bean with session id "+pRequest.getRequestedSessionId()+" has country code "+countryCode);
		}
		return countryCode;

	}
	
	/**
	 * Get user Ip address.
	 *
	 * @param pRequest the request
	 * @return the user ip address
	 */

	public String getUserIpAddress(final DynamoHttpServletRequest pRequest){
		final String ipHeaderName=BBBConfigRepoUtils.getStringValue( BBBInternationalShippingConstants.CONFIG_TYPE_FOR_IP_ADDRESS, BBBInternationalShippingConstants.HEADER_PARAMETER_FOR_IP_ADDRESS);
		String ipAddress = pRequest.getHeader(ipHeaderName);
		if (isLoggingDebug()) {
		    logDebug("IP From [TRUE_IP_HEADER] :: " + ipAddress );
		}
		
		if ((StringUtils.isEmpty(ipAddress))) {
			ipAddress = pRequest.getRemoteAddr();
			if (isLoggingDebug()) {
				logDebug("IP From [TRUE_IP_HEADER] is empty. Fetched remote IP address of client" );
			    logDebug("Remote IP Address :: " + ipAddress );
			}
		}
		if (StringUtils.isEmpty(ipAddress) || (ipAddress != null && ipAddress.contains(BBBCoreConstants.COLON))) {
			ipAddress = null;
		}
		if (isLoggingDebug()) {
		    logDebug("Returning IP Address ::" + ipAddress );
		}
		return ipAddress;
	}
	//Rest call 
	/**
	 * Gets the country code from session mobile.
	 *
	 * @return the country code from session mobile
	 */
	public String getCountryCodeFromSessionMobile(){
		logDebug(" enter method getCountryCodeFromSessionMobile ");
		String shippingEnabled=BBBCoreConstants.BLANK;
		String selectedCountryCode = this.getDefaultCountry();
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		final BBBSessionBean sessionBean =
				(BBBSessionBean)ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/SessionBean");
		@SuppressWarnings("rawtypes")
		final HashMap  sessionMap=sessionBean.getValues();
		if(sessionMap!=null && !sessionMap.isEmpty()){
			selectedCountryCode = (String)sessionMap.get(BBBInternationalShippingConstants.KEY_FOR_SESSION_VARIABLE_TO_STORE_DEFAULT_CONTEXT);
			if(sessionMap.get(BBBInternationalShippingConstants.KEY_FOR_SHIPPING_ENABLED)!=null)
			{
				shippingEnabled=sessionMap.get(BBBInternationalShippingConstants.KEY_FOR_SHIPPING_ENABLED).toString();
			}
			logDebug("session bean with session id "+request.getRequestedSessionId()+" has country code "+selectedCountryCode);
		}
		
		if(BBBUtility.isEmpty(shippingEnabled) || (!BBBUtility.isEmpty(shippingEnabled) && shippingEnabled.equalsIgnoreCase("true")))
		{
			return selectedCountryCode;
		}
		else
		{
			return BBBInternationalShippingConstants.DEFAULT_COUNTRY;
		}
	}
	
	/**
	 * The method is used to reset the international cookie.
	 *
	 * @param defaultCountry the default country
	 * @param defaultCurrency the default currency
	 * @param pRequest the request
	 * @return Cookie
	 */
	public Cookie resetInternationalShipCookie(String defaultCountry,String defaultCurrency,DynamoHttpServletRequest pRequest ){
		logDebug(" Enter method: setInternationalShipCookie with parameters defaultCountry: "+defaultCountry +" defaultCurrency: "+defaultCurrency);
		String cookieValue=this.getCookieValueFrmSession(defaultCountry, defaultCurrency);

		final String cookieName=getCookieFullName();
		final Cookie cookie = new Cookie(cookieName, cookieValue);

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
		
		int cookieMaxAge=0;

		logDebug(" cookieMaxAge .. "+cookieMaxAge);
		cookie.setMaxAge(cookieMaxAge);
		cookie.setDomain(domain);
		cookie.setPath(path);
		return cookie;

	}
	
}
