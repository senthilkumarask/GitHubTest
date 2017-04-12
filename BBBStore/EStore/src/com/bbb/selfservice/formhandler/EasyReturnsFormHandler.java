package com.bbb.selfservice.formhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;

import org.apache.commons.collections.map.HashedMap;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import atg.droplet.DropletException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericFormHandler;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.selfservice.manager.FedexShipService;
import com.bbb.utils.BBBUtility;
import com.fedex.ws.ship.v15.Notification;

public class EasyReturnsFormHandler extends BBBGenericFormHandler { 
	
	private HTTPCallInvoker httpCallInvoker;
	private String firstName;
	private String lastName;
	private String company;
	private String address1;
	private String address2;
	private String shipfromcity;
	private String state;
	private String postalcode;
	private String basePhoneERF1;
	private String basePhoneERF2;
	private String basePhoneERF3;
	private String phone;
	private String emailaddress;
	private String shiptorma;
	private String rslevel;
	private int numboxes;
	private BBBCatalogTools catalogTools;
	private LblTxtTemplateManager lblTxtTemplateManager;
	private SiteContext siteContext;
	
    // Adding constants for Fedex
	
	public static final String FEDEX_SUCCESS = "success";
	public static final String  FEDEX_TRACKINGNUMBER = "trackingNumber";
	public static final String  FEDEX_FAILURE = "failure";
	public static final String  FEDEX_ERROR = "failure";
	public static final String  FEDEX_URL  = "Url";
	public static final String  FEDEX_APP_PDF = "application/pdf";	
	private String ERROR_CODE = "err_easy_returns_fedex_webservice_call";
	private String URL = "Url";
	private String successURL = "easy_returns_email.jsp";
	private String message;
	
	/**
	 * to get message
	 * @return message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * to set message
	 * @param message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * to get siteContext
	 * @return siteContext
	 */
	public SiteContext getSiteContext() {
		return siteContext;
	}

	/**
	 * to set siteContext
	 * @param siteContext
	 */
	public void setSiteContext(SiteContext siteContext) {
		this.siteContext = siteContext;
	}

	/**
	 * To get fedexShipService
	 * @return fedexShipService
	 */
	public FedexShipService getFedexShipService() {
		return fedexShipService;
	}

	/**
	 * To set FedexShipService
	 * @param fedexShipService
	 */
	public void setFedexShipService(FedexShipService fedexShipService) {
		this.fedexShipService = fedexShipService;
	}
	
	/**
	 * To hold FedexShipService
	 */
	private FedexShipService fedexShipService;
	
	/**
	 * @return the httpCallInvoker
	 */
	public final HTTPCallInvoker getHttpCallInvoker() {
		return this.httpCallInvoker;
	}

	/**
	 * @param httpCallInvoker the httpCallInvoker to set
	 */
	public final void setHttpCallInvoker(final HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}

	/**
	 * @return the firstName
	 */
	public final String getFirstName() {
		return this.firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public final void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public final String getLastName() {
		return this.lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public final void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the company
	 */
	public final String getCompany() {
		return this.company;
	}

	/**
	 * @param company the company to set
	 */
	public final void setCompany(final String company) {
		this.company = company;
	}

	/**
	 * @return the address1
	 */
	public final String getAddress1() {
		return this.address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public final void setAddress1(final String address1) {
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public final String getAddress2() {
		return this.address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public final void setAddress2(final String address2) {
		this.address2 = address2;
	}

	/**
	 * @return the shipfromcity
	 */
	public final String getShipfromcity() {
		return this.shipfromcity;
	}

	/**
	 * @param shipfromcity the shipfromcity to set
	 */
	public final void setShipfromcity(final String shipfromcity) {
		this.shipfromcity = shipfromcity;
	}

	/**
	 * @return the state
	 */
	public final String getState() {
		return this.state;
	}

	/**
	 * @param state the state to set
	 */
	public final void setState(final String state) {
		this.state = state;
	}

	/**
	 * @return the postalcode
	 */
	public final String getPostalcode() {
		return this.postalcode;
	}

	/**
	 * @param postalcode the postalcode to set
	 */
	public final void setPostalcode(final String postalcode) {
		this.postalcode = postalcode;
	}

	/**
	 * @return the basePhoneERF1
	 */
	public final String getBasePhoneERF1() {
		return this.basePhoneERF1;
	}

	/**
	 * @param basePhoneERF1 the basePhoneERF1 to set
	 */
	public final void setBasePhoneERF1(final String basePhoneERF1) {
		this.basePhoneERF1 = basePhoneERF1;
	}

	/**
	 * @return the basePhoneERF2
	 */
	public final String getBasePhoneERF2() {
		return this.basePhoneERF2;
	}

	/**
	 * @param basePhoneERF2 the basePhoneERF2 to set
	 */
	public final void setBasePhoneERF2(final String basePhoneERF2) {
		this.basePhoneERF2 = basePhoneERF2;
	}

	/**
	 * @return the basePhoneERF3
	 */
	public final String getBasePhoneERF3() {
		return this.basePhoneERF3;
	}

	/**
	 * @param basePhoneERF3 the basePhoneERF3 to set
	 */
	public final void setBasePhoneERF3(final String basePhoneERF3) {
		this.basePhoneERF3 = basePhoneERF3;
	}

	/**
	 * @return the phone
	 */
	public final String getPhone() {
		return this.phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public final void setPhone(final String phone) {
		this.phone = phone;
	}

	/**
	 * @return the emailaddress
	 */
	public final String getEmailaddress() {
		return this.emailaddress;
	}

	/**
	 * @param emailaddress the emailaddress to set
	 */
	public final void setEmailaddress(final String emailaddress) {
		this.emailaddress = emailaddress;
	}

	/**
	 * @return the shiptorma
	 */
	public final String getShiptorma() {
		return this.shiptorma;
	}

	/**
	 * @param shiptorma the shiptorma to set
	 */
	public final void setShiptorma(final String shiptorma) {
		this.shiptorma = shiptorma;
	}

	/**
	 * @return the rslevel
	 */
	public final String getRslevel() {
		return this.rslevel;
	}

	/**
	 * @param rslevel the rslevel to set
	 */
	public final void setRslevel(final String rslevel) {
		this.rslevel = rslevel;
	}

	/**
	 * @return the numboxes
	 */
	public final int getNumboxes() {
		return this.numboxes;
	}

	/**
	 * @param numboxes the numboxes to set
	 */
	public final void setNumboxes(final int numboxes) {
		this.numboxes = numboxes;
	}

	/**
	 * @return the mCatalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return the lblTxtTemplateManager
	 */
	public final LblTxtTemplateManager getLblTxtTemplateManager() {
		return this.lblTxtTemplateManager;
	}

	/**
	 * @param lblTxtTemplateManager the lblTxtTemplateManager to set
	 */
	public final void setLblTxtTemplateManager(final LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * This method is customized to validate the different form fields to match.
	 * the business rules
	 * 
	 * @param pRequest - Dynamo Servlet Request
	 * 
	 */
	private void validateFormInputs(final DynamoHttpServletRequest pRequest) {
			
			String errorMessage = null;
			
			logDebug("EasyReturnsFormHandler.validateRequestInfo() method started"); //$NON-NLS-1$
			
			if (!BBBUtility.isValidFirstName(this.getFirstName())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_FIRSTNAME_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_FIRSTNAME_INVALID));
			}
			
			if (!BBBUtility.isValidLastName(this.getLastName())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_LASTNAME_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_LASTNAME_INVALID));
			}
			if("undefined".equalsIgnoreCase(this.getCompany())){
            	this.setCompany(null);
            }
			if (BBBUtility.isNotEmpty(this.getCompany()) && !BBBUtility.isValidCompanyName(this.getCompany())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
							BBBCoreErrorConstants.ERROR_EASY_RETURNS_COMPANYNAME_INVALID,
							pRequest.getLocale().getLanguage(), null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_COMPANYNAME_INVALID));
			}
			
			if (!BBBUtility.isValidAddressLine1(this.getAddress1())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_ADDRESS1_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_ADDRESS1_INVALID));
			}
			
			if (BBBUtility.isNotEmpty(this.getAddress2()) && !BBBUtility.isValidAddressLine2(this.getAddress2())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_ADDRESS2_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_ADDRESS2_INVALID));
			}
			
			if (!BBBUtility.isValidCity(this.getShipfromcity())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_CITY_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_CITY_INVALID));
			}
			
			if (BBBUtility.isEmpty(this.getState())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_STATE_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_STATE_INVALID));
			}
			
			if (!BBBUtility.isValidZip(this.getPostalcode())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_POSTALCODE_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_POSTALCODE_INVALID));
			}
			
			
			if (BBBUtility.isEmpty(this.getEmailaddress())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_EMAIL_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_EMAIL_INVALID));
			}
			
			if (BBBUtility.isEmpty(this.getBasePhoneERF1()) || BBBUtility.isEmpty(this.getBasePhoneERF2()) || BBBUtility.isEmpty(this.getBasePhoneERF3()) ) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_PHONENUMBER_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_PHONENUMBER_INVALID));
			}
			
			if ((BBBUtility.isNotEmpty(this.getBasePhoneERF1())
					|| BBBUtility.isNotEmpty(this.getBasePhoneERF2()) 
					|| BBBUtility.isNotEmpty(this.getBasePhoneERF3()))
					&& ((this.getBasePhoneERF1().length() < BBBCoreConstants.THREE
					|| !BBBUtility.isValidNumber(this.getBasePhoneERF1())) 
					|| (this.getBasePhoneERF2().length() < BBBCoreConstants.THREE
					|| !BBBUtility.isValidNumber(this.getBasePhoneERF2()))
					|| (this.getBasePhoneERF3().length() < BBBCoreConstants.FOUR
					|| !BBBUtility.isValidNumber(this.getBasePhoneERF3())))) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
							BBBCoreErrorConstants.ERROR_EASY_RETURNS_PHONENUMBER_INVALID,
							pRequest.getLocale().getLanguage(), null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_PHONENUMBER_INVALID));
			}
			
			if (!BBBUtility.isValidOrderNumber(this.getShiptorma())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_ORDER_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_ORDER_INVALID));
			}
			
			if (BBBUtility.isNotEmpty(this.getEmailaddress()) && !BBBUtility.isValidEmail(this.getEmailaddress())) {
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_EMAIL_INVALID, pRequest.getLocale().getLanguage(),
						null, null);
			addFormException(new DropletException(errorMessage,
					BBBCoreErrorConstants.ERROR_EASY_RETURNS_EMAIL_INVALID));
			}

			logDebug("EasyReturnsFormHandler.validateRequestInfo() method ends"); //$NON-NLS-1$
	}

	/**
	 *  This method is used to Generate Label
	 *  
	 * @param pRequest - Dynamo Servlet Request
	 * @param pResponse - Dynamo Servlet Response
	 * @throws ServletException  - Servlet Response
	 * @throws IOException - Io Exceptions
	 */

	public final void handleGenerateLabel(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		try {
			List<String> fedexEnabled = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.FEDEX_ENABLED);
		    if(fedexEnabled!=null){
		    	String string = fedexEnabled.get(0);
		    	boolean isFedexEnabled = Boolean.parseBoolean(string);
		    	if(isFedexEnabled){
		    		if(this.getRslevel()!=null && this.getRslevel().equals("4wrl")){
			    		FedexLabelTagService(pRequest,pResponse);	
			    		/*	
				    	 * TBXPS-1699 | IllegalStateException
				    	 */
				    		checkFormRedirect("selfservice/easy_returns_form.jsp", "", pRequest, pResponse);
		    		}else if (this.getRslevel()!=null && this.getRslevel().equals("4erl")){
		    			FedexEmailTagService(pRequest,pResponse);
		    		}else {
		    			FedexEmailTagService(pRequest,pResponse);
		    		}
		    	}else{
		    		UPSService(pRequest,pResponse);
		    		/*	
			    	 * TBXPS-1699 | IllegalStateException
			    	 */
			    		checkFormRedirect("selfservice/easy_returns_form.jsp", "", pRequest, pResponse);
		    	}		    	
		    }
		} catch (BBBSystemException e) {
			logError(e.getMessage(),e);
		} catch (BBBBusinessException e) {
			logError(e.getMessage(),e);
		}
		
		
		
	}
	/**
	 * This method is used to generate email label
	 * @param pRequest - Dynamo request
	 * @param pResponse - Dynamo Response
	 * @return 
	 */
	private final boolean  FedexEmailTagService(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse){
		Map<String , String> pMap  = setHashMap();
		Map<String , Object> hMap = getFedexShipService().sendEmailLabel(pMap , this.getCatalogTools(),this.getSiteContext());
		if(hMap!=null && hMap.get(URL)!=null){
				String str = (String) hMap.get(URL);		
				String strencoded = pResponse.encodeURL(str);
				//strencoded.replaceAll(" ", ""); //TODO				
				   try {
						List<String>  message = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_SUCCESS_URL_EMAIL_MESSAGE);
						String messageString = message.get(0).replace("<url>", strencoded.trim());
						setMessage(messageString);
						return this.checkFormRedirect("./"+successURL, pRequest.getRequestURL().toString(),pRequest , pResponse);				
					   } catch (BBBSystemException e) {
						logError(e.getMessage(),e);
					} catch (BBBBusinessException e) {
						logError(e.getMessage(),e);
					} catch (ServletException e) {
					logError(e.getMessage(),e);
				} catch (IOException e) {
					logError(e.getMessage(),e);
				}
			
		}else if(hMap!=null && hMap.get(FEDEX_FAILURE)!=null){
			com.fedex.ws.openship.v7.Notification[] nfs = (com.fedex.ws.openship.v7.Notification[]) hMap.get(FEDEX_FAILURE);
			StringBuffer bfe = new StringBuffer();
			for(int i=0; i<nfs.length ; i++){
				com.fedex.ws.openship.v7.Notification nf = nfs[i];
				bfe.append(nf.getMessage() + "<br/>");
				
			}
			addFormException(new DropletException(bfe.toString(),ERROR_CODE));
		}else{	
			addFormException(new DropletException(getErrorMessage(this.getCatalogTools()).get(0),ERROR_CODE));
		}
		return true;
	
		
	}
	
	/**
	 * Return user details
	 * 
	 * @return map - return user details
	 */
	private Map<String , String> setHashMap(){
		final Map<String , String> pMap = new HashedMap();		
		pMap.put(BBBCoreConstants.FIRST_NAME.toLowerCase(), this.getFirstName());
		pMap.put(BBBCoreConstants.LAST_NAME.toLowerCase(), this.getLastName());
		pMap.put(BBBCoreConstants.CC_COMPANY, this.getCompany());
		pMap.put(BBBCoreConstants.CC_ADDRESS1, this.getAddress1());
		pMap.put(BBBCoreConstants.CC_ADDRESS2, this.getAddress2());
		pMap.put(BBBCoreConstants.SHIP_FROM_CITY.toLowerCase(), this.getShipfromcity());
		pMap.put(BBBCoreConstants.CC_STATE, this.getState());
		pMap.put(BBBCoreConstants.CC_POSTAL_CODE.toLowerCase(), this.getPostalcode());		
		pMap.put(BBBCoreConstants.PHONE , this.getBasePhoneERF1()+this.getBasePhoneERF2()+this.getBasePhoneERF3());
		pMap.put(BBBCoreConstants.EMAIL_ADDRESS, this.getEmailaddress());
		pMap.put(BBBCoreConstants.NUM_BOXES, String.valueOf(this.getNumboxes()));
		pMap.put(BBBCoreConstants.WEIGHT1, String.valueOf(BBBCoreConstants.TEN));
		pMap.put(BBBCoreConstants.WEIGHT2, BBBCoreConstants.BLANK);
		pMap.put(BBBCoreConstants.WEIGHT3, BBBCoreConstants.BLANK);
		pMap.put(BBBCoreConstants.SHIPTORMA, this.getShiptorma());
		return pMap;
	}
	
	/**
	 * This method is used to generate Label
	 * @param pRequest
	 * @param pResponse
	 */
	private final void  FedexLabelTagService(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse){
		Map<String , String> pMap  = setHashMap();
		Map<String , Object> hMap = getFedexShipService().getLabel(pMap,this.getCatalogTools(),this.getSiteContext());
		
		if(hMap!=null && hMap.get(FEDEX_SUCCESS)!=null){
			byte[] bytesPDF = (byte[]) hMap.get(FEDEX_SUCCESS);
			String trackingNummber = (String) hMap.get(FEDEX_TRACKINGNUMBER);
			ServletOutputStream sOutPut;
			try {
				sOutPut = pResponse.getOutputStream();
				pResponse.setContentType(FEDEX_APP_PDF);
				pResponse.setHeader("Content-Disposition", "inline; filename='" + trackingNummber + "'");
				sOutPut.write(bytesPDF);
				sOutPut.flush();
				sOutPut.close();
			} catch (IOException e) {
				logError(e.getMessage(),e);
			}
		}else{
			if(hMap!=null && hMap.get(FEDEX_FAILURE)!=null){
				Notification[] nfs = (Notification[]) hMap.get(FEDEX_FAILURE);
				StringBuffer bfe = new StringBuffer();
				for(int i=0; i<nfs.length ; i++){
					Notification nf = nfs[i];
					bfe.append(nf.getMessage() + "<br/>");
				
				}
				addFormException(new DropletException(bfe.toString(),ERROR_CODE));
			}else{	
				addFormException(new DropletException(getErrorMessage(this.getCatalogTools()).get(0),ERROR_CODE));
			}
		}
	}
	/**
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	private final void  UPSService(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)throws ServletException,
			IOException {


		logDebug("EasyReturnsFormHandler.handleRequestInfo() method started"); //$NON-NLS-1$
		String response = null;
		String targetURL = null;
		String loginId = null;
		String loginPassword = null;
		String uom = null;
		String packagetype = null;
		String errorMessage = null;
		this.validateFormInputs(pRequest);
		
		String siteId = SiteContextManager.getCurrentSiteId();
		
		if (!getFormError()) {
			try {
				targetURL = this.getCatalogTools().getAllValuesForKey(
						BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.UPS_EASY_RETURNS_URL).get(0);
				loginId = this.getCatalogTools().getAllValuesForKey(
						BBBCoreConstants.EASY_RETURNS_KEY, BBBCoreConstants.EASY_RETURNS_LOGIN_ID).get(0);
				loginPassword = this.getCatalogTools().getAllValuesForKey(
						BBBCoreConstants.EASY_RETURNS_KEY, BBBCoreConstants.EASY_RETURNS_LOGIN_PASSWORD).get(0);
				
				uom = this.getCatalogTools().getAllValuesForKey(
						BBBCoreConstants.EASY_RETURNS_KEY, BBBCoreConstants.EASY_RETURNS_UOM).get(0);
				
				packagetype = this.getCatalogTools().getAllValuesForKey(
						BBBCoreConstants.EASY_RETURNS_KEY, BBBCoreConstants.EASY_RETURNS_PACKAGE_TYPE).get(0);
				
				errorMessage = this.getLblTxtTemplateManager().getErrMsg(
						BBBCoreErrorConstants.ERROR_EASY_RETURNS_HTTP_CALL, pRequest.getLocale().getLanguage(),
						null, null);
				logDebug("Inside EasyReturnsFormHandler.handleRequestInfo() : targetURL" + targetURL); //$NON-NLS-1$
				logDebug("Inside EasyReturnsFormHandler.handleRequestInfo() : loginId" + loginId); //$NON-NLS-1$
				
				} catch (BBBSystemException systemException) {
					logError(systemException.getMessage(), systemException);
					addFormException(new DropletException(errorMessage,
							BBBCoreErrorConstants.ERROR_EASY_RETURNS_HTTP_CALL));
					return;
				} catch (BBBBusinessException businessException) {
					logError(businessException.getMessage(), businessException);
					addFormException(new DropletException(errorMessage,
							BBBCoreErrorConstants.ERROR_EASY_RETURNS_HTTP_CALL));
					return;
				}
				
				final List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair(BBBCoreConstants.FIRST_NAME.toLowerCase(), this.getFirstName()));
				params.add(new BasicNameValuePair(BBBCoreConstants.LAST_NAME.toLowerCase(), this.getLastName()));
				params.add(new BasicNameValuePair(BBBCoreConstants.CC_COMPANY, this.getCompany()));
				params.add(new BasicNameValuePair(BBBCoreConstants.CC_ADDRESS1, this.getAddress1()));
				params.add(new BasicNameValuePair(BBBCoreConstants.CC_ADDRESS2, this.getAddress2()));
				params.add(new BasicNameValuePair(BBBCoreConstants.SHIP_FROM_CITY.toLowerCase(), this.getShipfromcity()));
				params.add(new BasicNameValuePair(BBBCoreConstants.CC_STATE, this.getState()));
				params.add(new BasicNameValuePair(BBBCoreConstants.CC_POSTAL_CODE.toLowerCase(), this.getPostalcode()));
				params.add(new BasicNameValuePair(BBBCoreConstants.BASE_PHONE_ERF1, this.getBasePhoneERF1()));
				params.add(new BasicNameValuePair(BBBCoreConstants.BASE_PHONE_ERF2, this.getBasePhoneERF2()));
				params.add(new BasicNameValuePair(BBBCoreConstants.BASE_PHONE_ERF3, this.getBasePhoneERF3()));
				params.add(new BasicNameValuePair(BBBCoreConstants.PHONE, this.getPhone()));
				params.add(new BasicNameValuePair(BBBCoreConstants.EMAIL_ADDRESS, this.getEmailaddress()));
				params.add(new BasicNameValuePair(BBBCoreConstants.SHIPTORMA, this.getShiptorma()));
				params.add(new BasicNameValuePair(BBBCoreConstants.NUM_BOXES, String.valueOf(this.getNumboxes())));
				params.add(new BasicNameValuePair(BBBCoreConstants.RS_LEVEL, this.getRslevel()));
				params.add(new BasicNameValuePair(BBBCoreConstants.EASY_RETURNS_LOGIN_ID, loginId));
				params.add(new BasicNameValuePair(BBBCoreConstants.EASY_RETURNS_LOGIN_PASSWORD, loginPassword));
				params.add(new BasicNameValuePair(BBBCoreConstants.WEIGHT1, String.valueOf(BBBCoreConstants.TEN)));
				params.add(new BasicNameValuePair(BBBCoreConstants.WEIGHT2, BBBCoreConstants.BLANK));
				params.add(new BasicNameValuePair(BBBCoreConstants.WEIGHT3, BBBCoreConstants.BLANK));
				params.add(new BasicNameValuePair(BBBCoreConstants.EASY_RETURNS_SERVICE,BBBCoreConstants.EASY_RETURNS_SERVICE_CODE));
				
				if(BBBCoreConstants.SITE_BAB_CA.equals(siteId)){
					params.add(new BasicNameValuePair(BBBCoreConstants.UOM, uom));
					params.add(new BasicNameValuePair(BBBCoreConstants.PACKAGE_TYPE, packagetype));
					
				}
				try {
					response = this.getHttpCallInvoker().invoke(targetURL, params);
				} catch (BBBBusinessException businessException) {
					logError(businessException.getMessage(), businessException);
					addFormException(new DropletException(errorMessage,
							BBBCoreErrorConstants.ERROR_EASY_RETURNS_HTTP_CALL));
					return;
				} catch (BBBSystemException systemException) {
					logError(BBBCoreErrorConstants.ERROR_EASY_RETURNS_HTTP_CALL,
							systemException);
					addFormException(new DropletException(errorMessage,
							BBBCoreErrorConstants.ERROR_EASY_RETURNS_HTTP_CALL));
					return;
				}
				if (BBBUtility.isNotEmpty(response)) {
					pResponse.setContentType("text/html"); //$NON-NLS-1$
					pResponse.getWriter().print(response);
					pResponse.getWriter().flush();
				} else {
					addFormException(new DropletException(errorMessage,
							BBBCoreErrorConstants.ERROR_EASY_RETURNS_HTTP_CALL));
					return;
				}
		} 
		
		logDebug("EasyReturnsFormHandler.handleRequestInfo() method ends"); //$NON-NLS-1$
	
	}
	
	/**
	 * This method is used to get error message.
	 * @param pBBBCatalogTools
	 * @return return error message
	 */
	private List<String> getErrorMessage(BBBCatalogTools pBBBCatalogTools){
		List<String> error = null;
	    try {
	    	error = pBBBCatalogTools.getAllValuesForKey(BBBCoreConstants.WSDL_FEDEX,BBBCoreConstants.FEDEX_SHIPSERVICE_CMN_ERROR);
		  }catch (BBBSystemException e) {
		   logError(e.getMessage(),e);
		} catch (BBBBusinessException e) {
			logError(e.getMessage(),e);
		}
		return error;
		
	}
}
