package com.bbb.mobile.component;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.paypal.vo.PayPalAddressVerifyVO;

/**
 * This class is used to map a jsessionId between mobile server with jsessionId
 * of service server.
 *
 * @author ssha53
 *
 */
@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class ServiceSession implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//service session id
	private String serverSessionId;

	//This is session confirmation number that is set for each session in ATG.
	private String dynSession;

	private boolean login;

	private String targetURL;

	private String siteIdHeader;

	private String cartCookie;

	private String sflCookie;

	private String supressHeader;

	private String wishListParam;

	private String registryParam;

	private String moveToWishlistParam;

	private String applicationType = null;

	private String registryId = null;

	private String registryName = null;

	private String atgProfileIdCookie;

	private PayPalAddressVerifyVO payPalAddress;

	private  String orderTotal = null;

	//BPS-1145: added for mobile app multi scan functionality
	private String scanMode = null;

	private String domainVsReferer = null;
	private String requestURL = null;

	private List<RegistrySummaryVO> registrySummaryVOList;

	public String getDomainVsReferer() {
		return domainVsReferer;
	}
	public void setDomainVsReferer(String domainVsReferer) {
		this.domainVsReferer = domainVsReferer;
	}
	public String getRequestURL() {
		return requestURL;
	}
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}





	//R2.2 Start -Story AP- Order.xml changes
	private String addressStatus;
	//R2.2 END

	//BSL-2562 : start

	private String latitude;
	private String longitude;

	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}



	//BSL-2562 : Ends


//	R2.2 Start : Mobile App Auto Login Cookie Parameters

	private String mobileAppRequest = "false";

	private String dynUserConfirmCookie;

	private String dynUserIdConfirmCookie;
// End : : Mobile App Auto Login Cookie Parameters

	private boolean cookieAutoLogin;
	private String clientIP;
	private String internationalCookie;
	private String babyCA;
	private String babyCASourcePath;
	private String internationalFormExceptions;
	private List<String> restrictedList;
	private List<String> bopusList;
	private boolean wmSet=false;
	private String currencySymbol;
	private Integer currencyRoundingMethod;

	public String getCurrencySymbol() {
		return currencySymbol;
	}
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	public Integer getCurrencyRoundingMethod() {
		return currencyRoundingMethod;
	}
	public void setCurrencyRoundingMethod(Integer currencyRoundingMethod) {
		this.currencyRoundingMethod = currencyRoundingMethod;
	}
	public boolean isWmSet() {
		return wmSet;
	}
	public void setWmSet(boolean wmSet) {
		this.wmSet = wmSet;
	}
	public String getInternationalFormExceptions() {
		return internationalFormExceptions;
	}
	public void setInternationalFormExceptions(String internationalFormExceptions) {
		this.internationalFormExceptions = internationalFormExceptions;
	}
	public List<String> getRestrictedList() {
		return restrictedList;
	}
	public void setRestrictedList(List<String> restrictedList) {
		this.restrictedList = restrictedList;
	}
	public List<String> getBopusList() {
		return bopusList;
	}
	public void setBopusList(List<String> bopusList) {
		this.bopusList = bopusList;
	}
	public String getInternationalCookie() {
		return internationalCookie;
	}
	public void setInternationalCookie(String internationalCookie) {
		this.internationalCookie = internationalCookie;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	// Registry Kickstarters take me back URL after registry creation
	private String ksTakeMeBackURL;

	private String selectedShippingAddr;

	private String deviceSessionId;;

	private String payPalInvalidAddressMessage;

	private boolean copyRegistryEnabled;

	private String mobileHostName;

	private boolean createInternationalCookieFromIP;

	/**
	 * @return the createInternationalCookieFromIP
	 */
	public boolean isCreateInternationalCookieFromIP() {
		return createInternationalCookieFromIP;
	}
	/**
	 * @param createInternationalCookieFromIP the createInternationalCookieFromIP to set
	 */
	public void setCreateInternationalCookieFromIP(
			boolean createInternationalCookieFromIP) {
		this.createInternationalCookieFromIP = createInternationalCookieFromIP;
	}
	/**
	 * service session id
	 * @return the serverSessionId
	 */
	public String getServerSessionId() {
		return serverSessionId;
	}
	/**
	 * service session id
	 * @param serverSessionId the serverSessionId to set
	 */
	public void setServerSessionId(String serverSessionId) {
		this.serverSessionId = serverSessionId;
	}
	/**
	 * @return the dynSession
	 */
	public String getDynSession() {
		return dynSession;
	}
	/**
	 * @param dynSession the dynSession to set
	 */
	public void setDynSession(String dynSession) {
		this.dynSession = dynSession;
	}
	public boolean isLogin() {
		return login;
	}
	public void setLogin(boolean login) {
		this.login = login;
	}
	/**
	 * @return the targetURL
	 */
	public String getTargetURL() {
		return targetURL;
	}
	/**
	 * @param targetURL the targetURL to set
	 */
	public void setTargetURL(String targetURL) {
		this.targetURL = targetURL;
	}
	/**
	 * @return the siteIdHeader
	 */
	public String getSiteIdHeader() {
		return siteIdHeader;
	}
	/**
	 * @param siteIdHeader the siteIdHeader to set
	 */
	public void setSiteIdHeader(String siteIdHeader) {
		this.siteIdHeader = siteIdHeader;
	}
	/**
	 * @return the cartCookie
	 */
	public String getCartCookie() {
		return cartCookie;
	}
	/**
	 * @param cartCookie the cartCookie to set
	 */
	public void setCartCookie(String cartCookie) {
		this.cartCookie = cartCookie;
	}
	public String getSflCookie() {
		return sflCookie;
	}
	public void setSflCookie(String sflCookie) {
		this.sflCookie = sflCookie;
	}
	/**
	 *
	 * @return
	 */
	public String getSupressHeader() {
		return supressHeader;
	}
	/**
	 *
	 * @param supressHeader
	 */
	public void setSupressHeader(String supressHeader) {
		this.supressHeader = supressHeader;
	}
	/**
	 * @return the wishListParam
	 */
	public String getWishListParam() {
		return wishListParam;
	}
	/**
	 * @param wishListParam the wishListParam to set
	 */
	public void setWishListParam(String wishListParam) {
		this.wishListParam = wishListParam;
	}
	/**
	 * @return the registryParam
	 */
	public String getRegistryParam() {
		return registryParam;
	}
	/**
	 * @param registryParam the registryParam to set
	 */
	public void setRegistryParam(String registryParam) {
		this.registryParam = registryParam;
	}
	/**
	 * @return the applicationType
	 */
	public String getApplicationType() {
		return applicationType;
	}
	/**
	 * @param applicationType the applicationType to set
	 */
	public void setApplicationType(String applicationType) {
		this.applicationType = applicationType;
	}
	public String getBabyCA() {
		return babyCA;
	}
	public void setBabyCA(String babyCA) {
		this.babyCA = babyCA;
	}
	/**
	 * @return the registryId
	 */
	public String getRegistryId() {
		return registryId;
	}
	/**
	 * @param registryId the registryId to set
	 */
	public void setRegistryId(String registryId) {
		this.registryId = registryId;
	}
	/**
	 * @return the registryName
	 */
	public String getRegistryName() {
		return registryName;
	}
	/**
	 * @param registryName the registryName to set
	 */
	public void setRegistryName(String registryName) {
		this.registryName = registryName;
	}
	/**
	 * @return the moveToWishlistParam
	 */
	public String getMoveToWishlistParam() {
		return moveToWishlistParam;
	}
	/**
	 * @param moveToWishlistParam the moveToWishlistParam to set
	 */
	public void setMoveToWishlistParam(String moveToWishlistParam) {
		this.moveToWishlistParam = moveToWishlistParam;
	}
	public void setAtgProfileIdCookie(String atgProfileIdCookie) {
		this.atgProfileIdCookie = atgProfileIdCookie;
	}
	public String getAtgProfileIdCookie() {
		return this.atgProfileIdCookie;
	}
	/**
	 * @return the payPalAddress
	 */
	public PayPalAddressVerifyVO getPayPalAddress() {
		return payPalAddress;
	}
	/**
	 * @param payPalAddress the payPalAddress to set
	 */
	public void setPayPalAddress(PayPalAddressVerifyVO payPalAddress) {
		this.payPalAddress = payPalAddress;
	}

	/**
	 * @return the addressStatus
	 */
	public String getAddressStatus() {
		return addressStatus;
	}
	/**
	 * @param addressStatus the addressStatus to set
	 */
	public void setAddressStatus(String addressStatus) {
		this.addressStatus = addressStatus;
	}

	/**
	 * @return Cookie Loged In user in Session
	 */
	public boolean isCookieAutoLogin() {
		return cookieAutoLogin;
	}

	/**
	 * @param cookieAutoLogin
	 */
	public void setCookieAutoLogin(boolean cookieAutoLogin) {
		this.cookieAutoLogin = cookieAutoLogin;
	}

	/**
	 * @return Auto Login Cookie Received
	 */
	public String getDynUserConfirmCookie() {
		return dynUserConfirmCookie;
	}
	/**
	 * @param dynUserConfirmCookie
	 */
	public void setDynUserConfirmCookie(String dynUserConfirmCookie) {
		this.dynUserConfirmCookie = dynUserConfirmCookie;
	}
	/**
	 * @return Auto Login Cookie Received
	 */
	public String getDynUserIdConfirmCookie() {
		return dynUserIdConfirmCookie;
	}
	public void setDynUserIdConfirmCookie(String dynUserIdConfirm) {
		this.dynUserIdConfirmCookie = dynUserIdConfirm;
	}

	/**
	 * @return Request Coming From Mobile App
	 */
	public String getMobileAppRequest() {
		return mobileAppRequest;
	}
	/**
	 * @param mobileAppRequest
	 */
	public void setMobileAppRequest(String mobileAppRequest) {
		this.mobileAppRequest = mobileAppRequest;
	}

	/**
	 * @return the ksTakeMeBackURL
	 */
	public String getKsTakeMeBackURL() {
		return ksTakeMeBackURL;
	}

	/**
	 * @param ksTakeMeBackURL the ksTakeMeBackURL to set
	 */
	public void setKsTakeMeBackURL(String ksTakeMeBackURL) {
		this.ksTakeMeBackURL = ksTakeMeBackURL;
	}

	/**
	 * @return the selectedShippingAddr
	 */
	public String getSelectedShippingAddr() {
		return this.selectedShippingAddr;
	}
	/**
	 * @param selectedShippingAddr the selectedShippingAddr to set
	 */
	public void setSelectedShippingAddr(final String selectedShippingAddr) {
		this.selectedShippingAddr = selectedShippingAddr;
	}
	/**
	 * @return the deviceSessionId
	 */
	public String getDeviceSessionId() {
		return deviceSessionId;
	}
	/**
	 * @param deviceSessionId the deviceSessionId to set
	 */
	public void setDeviceSessionId(String deviceSessionId) {
		this.deviceSessionId = deviceSessionId;
	}

	/**
	 * @return the payPalInvalidAddressMessage
	 */
	public String getPayPalInvalidAddressMessage() {
		return payPalInvalidAddressMessage;
	}
	/**
	 * @param payPalInvalidAddressMessage the payPalInvalidAddressMessage to set
	 */
	public void setPayPalInvalidAddressMessage(
			String payPalInvalidAddressMessage) {
		this.payPalInvalidAddressMessage = payPalInvalidAddressMessage;
	}

	public String getBabyCASourcePath() {
		return babyCASourcePath;
	}
	public void setBabyCASourcePath(String babyCASourcePath) {
		this.babyCASourcePath = babyCASourcePath;
	}



	/**
	 * @return the copyRegistryEnabled
	 */
	public boolean isCopyRegistryEnabled() {
		return copyRegistryEnabled;
	}
	/**
	 * @param copyRegistryEnabled the copyRegistryEnabled to set
	 */
	public void setCopyRegistryEnabled(boolean copyRegistryEnabled) {
		this.copyRegistryEnabled = copyRegistryEnabled;
	}

	public String getMobileHostName() {
		return mobileHostName;
	}
	public void setMobileHostName(String mobileHostName) {
		this.mobileHostName = mobileHostName;
	}

	/**
	 * Getter for scan mode.
	 *
	 * @return the scanMode
	 */
	public String getScanMode() {
		return scanMode;
	}

	/**
	 * Setter for scan mode.
	 *
	 * @param scanMode the scanMode to set
	 */
	public void setScanMode(String scanMode) {
		this.scanMode = scanMode;
	}
	/**
	 * @return the orderTotal
	 */
	public String getOrderTotal() {
		return orderTotal;
	}
	/**
	 * @param orderTotal the orderTotal to set
	 */
	public void setOrderTotal(String orderTotal) {
		this.orderTotal = orderTotal;
	}

	public List<RegistrySummaryVO> getRegistrySummaryVOList() {
		return registrySummaryVOList;
	}

	public void setRegistrySummaryVOList(List<RegistrySummaryVO> registrySummaryVOList) {
		this.registrySummaryVOList = registrySummaryVOList;
	}

}
