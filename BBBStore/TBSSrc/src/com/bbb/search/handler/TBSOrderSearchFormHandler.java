package com.bbb.search.handler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringEscapeUtils;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.GenericFormHandler;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.commerce.vo.OrderVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.validation.BBBValidationRules;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.search.bean.TBSAdvancedOrderSearchBean;
import com.bbb.search.bean.TBSAdvancedOrderSearchResultsCacheBean;
import com.bbb.search.bean.TBSOrderSearchSessionBean;
import com.bbb.utils.BBBUtility;

public class TBSOrderSearchFormHandler extends GenericFormHandler {
	
	private LinkedHashMap<String, String> mAvailableSearchTypes;
	
	private String mSearchType;
	
	private String mSearchTerm;
	
	private BBBOrderManager mOrderManager;
	
	private String mSuccessURL;
	
	private String mFailureURL;
	
	private Map<String, String> mValue = new HashMap<String, String>();
	
	private BBBValidationRules mValidationRules;
	
	private String mAdvanceOrderSearchSucessURL;
	
	private String mAdvanceOrderSearchErrorURL;
	
	private String mBasicOrderSearchSucessURL;
	
	private String mBasicOrderSearchErrorURL;
	
	private String mOrderResults;
	
	private TBSOrderSearchSessionBean mOrderSessionBean; 
	
	private Repository mStoreRepository;
	
	private String babyItems;
	
	private TBSAdvancedOrderSearchBean advancedOrderSearchBean;
	
	private TBSAdvancedOrderSearchResultsCacheBean advancedOrderSearchCache;
	
	private BBBCatalogTools mCatalogTools;
	
	private int mDefaultNumResultsPerPage;
	
	private int mDefaultResultSetSize;
	
	
	public TBSAdvancedOrderSearchResultsCacheBean getAdvancedOrderSearchCache() {
		return advancedOrderSearchCache;
	}

	public void setAdvancedOrderSearchCache(
			TBSAdvancedOrderSearchResultsCacheBean advancedOrderSearchCache) {
		this.advancedOrderSearchCache = advancedOrderSearchCache;
	}
	
	public int getDefaultResultSetSize() {
		return mDefaultResultSetSize;
	}

	public void setDefaultResultSetSize(int pDefaultResultSetSize) {
		mDefaultResultSetSize = pDefaultResultSetSize;
	}

	public int getDefaultNumResultsPerPage() {
		return mDefaultNumResultsPerPage;
	}

	public void setDefaultNumResultsPerPage(int pDefaultNumResultsPerPage) {
		mDefaultNumResultsPerPage = pDefaultNumResultsPerPage;
	}

	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	public TBSAdvancedOrderSearchBean getAdvancedOrderSearchBean() {
		return advancedOrderSearchBean;
	}

	public void setAdvancedOrderSearchBean(
			TBSAdvancedOrderSearchBean advancedOrderSearchBean) {
		this.advancedOrderSearchBean = advancedOrderSearchBean;
	}

	/**
	 * @return the babyItems
	 */
	public String getBabyItems() {
		return babyItems;
	}

	/**
	 * @param pBabyItems the babyItems to set
	 */
	public void setBabyItems(String pBabyItems) {
		babyItems = pBabyItems;
	}

	/**
	 * @return the storeRepository
	 */
	public Repository getStoreRepository() {
		return mStoreRepository;
	}

	/**
	 * @param pStoreRepository the storeRepository to set
	 */
	public void setStoreRepository(Repository pStoreRepository) {
		mStoreRepository = pStoreRepository;
	}

	/**
	 * @return the availableSearchTypes
	 */
	public LinkedHashMap<String, String> getAvailableSearchTypes() {
		return mAvailableSearchTypes;
	}

	/**
	 * @param pAvailableSearchTypes the availableSearchTypes to set
	 */
	public void setAvailableSearchTypes(LinkedHashMap<String, String> pAvailableSearchTypes) {
		mAvailableSearchTypes = pAvailableSearchTypes;
	}

	/**
	 * @return the searchType
	 */
	public String getSearchType() {
		return mSearchType;
	}

	/**
	 * @param pSearchType the searchType to set
	 */
	public void setSearchType(String pSearchType) {
		mSearchType = pSearchType;
	}

	/**
	 * @return the searchTerm
	 */
	public String getSearchTerm() {
		return mSearchTerm;
	}

	/**
	 * @param pSearchTerm the searchTerm to set
	 */
	public void setSearchTerm(String pSearchTerm) {
		mSearchTerm = pSearchTerm;
	}
	
	/**
	 * @return the orderManager
	 */
	public BBBOrderManager getOrderManager() {
		return mOrderManager;
	}

	/**
	 * @param pOrderManager the orderManager to set
	 */
	public void setOrderManager(BBBOrderManager pOrderManager) {
		mOrderManager = pOrderManager;
	}

	/**
	 * @return the successURL
	 */
	public String getSuccessURL() {
		return mSuccessURL;
	}

	/**
	 * @return the failureURL
	 */
	public String getFailureURL() {
		return mFailureURL;
	}

	/**
	 * @param pSuccessURL the successURL to set
	 */
	public void setSuccessURL(String pSuccessURL) {
		mSuccessURL = pSuccessURL;
	}

	/**
	 * @param pFailureURL the failureURL to set
	 */
	public void setFailureURL(String pFailureURL) {
		mFailureURL = pFailureURL;
	}

	/**
	 * @return the value
	 */
	public Map<String, String> getValue() {
		return mValue;
	}

	/**
	 * @param pValue the value to set
	 */
	public void setValue(Map<String, String> pValue) {
		mValue = pValue;
	}

	/**
	 * @return the validationRules
	 */
	public BBBValidationRules getValidationRules() {
		return mValidationRules;
	}

	/**
	 * @param pValidationRules the validationRules to set
	 */
	public void setValidationRules(BBBValidationRules pValidationRules) {
		mValidationRules = pValidationRules;
	}

	/**
	 * @return the advanceOrderSearchSucessURL
	 */
	public String getAdvanceOrderSearchSucessURL() {
		return mAdvanceOrderSearchSucessURL;
	}

	/**
	 * @return the advanceOrderSearchErrorURL
	 */
	public String getAdvanceOrderSearchErrorURL() {
		return mAdvanceOrderSearchErrorURL;
	}

	/**
	 * @param pAdvanceOrderSearchSucessURL the advanceOrderSearchSucessURL to set
	 */
	public void setAdvanceOrderSearchSucessURL(String pAdvanceOrderSearchSucessURL) {
		mAdvanceOrderSearchSucessURL = pAdvanceOrderSearchSucessURL;
	}

	/**
	 * @param pAdvanceOrderSearchErrorURL the advanceOrderSearchErrorURL to set
	 */
	public void setAdvanceOrderSearchErrorURL(String pAdvanceOrderSearchErrorURL) {
		mAdvanceOrderSearchErrorURL = pAdvanceOrderSearchErrorURL;
	}

	/**
	 * @return the basicOrderSearchSucessURL
	 */
	public String getBasicOrderSearchSucessURL() {
		return mBasicOrderSearchSucessURL;
	}

	/**
	 * @return the basicOrderSearchErrorURL
	 */
	public String getBasicOrderSearchErrorURL() {
		return mBasicOrderSearchErrorURL;
	}

	/**
	 * @param pBasicOrderSearchSucessURL the basicOrderSearchSucessURL to set
	 */
	public void setBasicOrderSearchSucessURL(String pBasicOrderSearchSucessURL) {
		mBasicOrderSearchSucessURL = pBasicOrderSearchSucessURL;
	}

	/**
	 * @param pBasicOrderSearchErrorURL the basicOrderSearchErrorURL to set
	 */
	public void setBasicOrderSearchErrorURL(String pBasicOrderSearchErrorURL) {
		mBasicOrderSearchErrorURL = pBasicOrderSearchErrorURL;
	}

	/**
	 * @return the orderResults
	 */
	public String getOrderResults() {
		return mOrderResults;
	}

	/**
	 * @param pOrderResults the orderResults to set
	 */
	public void setOrderResults(String pOrderResults) {
		mOrderResults = pOrderResults;
	}

	/**
	 * @return the orderSessionBean
	 */
	public TBSOrderSearchSessionBean getOrderSessionBean() {
		return mOrderSessionBean;
	}

	/**
	 * @param pOrderSessionBean the orderSessionBean to set
	 */
	public void setOrderSessionBean(TBSOrderSearchSessionBean pOrderSessionBean) {
		mOrderSessionBean = pOrderSessionBean;
	}

	/**
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException 
	 */
	 public boolean handleSearch(DynamoHttpServletRequest pRequest,
	         DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		 vlogDebug("TBSOrderSearchFormHandler :: handleSearch() :: START");
		 String orderId = null;
		 if(getSearchType().equals(TBSConstants.SEARCH_TYPE_ORDER)){
			 vlogDebug("performing order search.......");
			 try {
				 orderId = ((TBSOrderTools)getOrderManager().getOrderTools()).orderSearch(getSearchTerm());
				 if(!StringUtils.isBlank(orderId)){
					 setSuccessURL(pRequest.getContextPath() + TBSConstants.ORDER_DETAILS_URL + orderId);
				 }
			} catch (RepositoryException e) {
				addFormException(new DropletException("Exception occurred"));
				vlogError("Exception occurred "+e);
			}
			if(StringUtils.isBlank(orderId)){
				setSuccessURL(pRequest.getContextPath() + TBSConstants.NO_SEARCH_RESULTS_URL + getSearchTerm());
			}
		 } else if(getSearchType().equals(TBSConstants.UPC)){
			 vlogDebug("Performing UPC search.......");
			 if(!StringUtils.isBlank(getSearchTerm())){
				 setSuccessURL(pRequest.getContextPath() + TBSConstants.UPC_SEARCH_URL+getSearchTerm()+"&type="+getSearchType());
			 }
		 } else if(getSearchType().equals(TBSConstants.PRODUCT)){
			 vlogDebug("Performing Product search.......");
			 if(!StringUtils.isBlank(getSearchTerm())){
				 String enteredSearchTerm = getSearchTerm().replaceAll("[\\W]|_", " ").replaceAll("( )+", " ").trim().replaceAll(" ", "-");
				 pRequest.getSession().setAttribute("origSearchTerm", getSearchTerm());
				 pRequest.getSession().removeAttribute("origSearchTermDisplay");
				 if(BBBUtility.isNotEmpty(enteredSearchTerm)){
					 enteredSearchTerm = StringEscapeUtils.unescapeHtml(enteredSearchTerm);
				 }
				 if(!StringUtils.isBlank(getBabyItems()) && getBabyItems().equals("true")){
					 setSuccessURL(pRequest.getContextPath() + "/s/"+enteredSearchTerm+"?babyItems="+getBabyItems());
				 } else {
					 setSuccessURL(pRequest.getContextPath() + "/s/"+enteredSearchTerm);
				 }
			 }
		 }
			//performing search if searchtype is selecteed as "store"
		 else if(getSearchType().equals(TBSConstants.STORE)){
			 vlogDebug("Performing search using type Store .......");
			 String errortype=null;
			 if(getSearchTerm().matches("[0-9]+") || getSearchTerm().matches("^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]{1}\\d{1}[A-Za-z]{1} *\\d{1}[A-Za-z]{1}\\d{1}$")){
				 if(!StringUtils.isBlank(getSearchTerm()) && getSearchTerm().length() >= 5 && isValidZip(getSearchTerm())){
					 
					 vlogDebug("Performing Store search using Zipcode......."+getSearchTerm());
					 setSuccessURL(pRequest.getContextPath() + TBSConstants.STORE_SEARCH_ZIP_URL+getSearchTerm()+"&type="+getSearchType());
					 
				 }else if(!StringUtils.isBlank(getSearchTerm()) && getSearchTerm().length() >= 1 && !isValidZip(getSearchTerm()) && isValidStore(getSearchTerm())) {
					 
					 vlogDebug("Performing Store search using Store ID......."+getSearchTerm());
					 setSuccessURL(pRequest.getContextPath() + TBSConstants.STORE_SEARCH_ID_URL+getSearchTerm()+"&type="+getSearchType());
					 
				 }else{
					 
					 errortype="invalidSearchInputForStore";
					 vlogDebug("Performing Store search ......."+getSearchTerm());
					 setSuccessURL(pRequest.getContextPath() + TBSConstants.NO_SEARCH_RESULTS_URL+getSearchTerm()+"&type="+getSearchType()+"&errortype="+errortype);
				 }
			 }else if(!StringUtils.isBlank(getSearchTerm()) && getSearchTerm().matches(".*[A-Za-z].*") && getSearchTerm().matches(".*[,].*") && isValidCityState(getSearchTerm())){
				 String[] lSearchtermArr = getSearchTerm().split(",");
				 String lCity = lSearchtermArr[0].toLowerCase().trim();
				 String lState = lSearchtermArr[1].toLowerCase().trim();
				 vlogDebug("Performing Store search using City and State......."+getSearchTerm());
				 
				 setSuccessURL(pRequest.getContextPath() + TBSConstants.STORE_SEARCH_ADDRESS_URL+lCity+","+lState+"&type="+getSearchType());
				 
			 }else if(!StringUtils.isBlank(getSearchTerm()) && getSearchTerm().matches(".*[A-Za-z].*") && !getSearchTerm().matches(".*[,].*") && !getSearchTerm().matches("[0-9]+")){
				 
				 errortype="invalidAddress";
				 vlogDebug("Performing Store search using invalid combination of City and State......."+getSearchTerm());
				 setSuccessURL(pRequest.getContextPath() + TBSConstants.NO_SEARCH_RESULTS_URL+getSearchTerm()+"&type="+getSearchType()+"&errortype="+errortype);
				 
			 }else{
				 
				 errortype="invalidSearchInputForStore";
				 vlogDebug("Performing Store search ......."+getSearchTerm());
				 setSuccessURL(pRequest.getContextPath() + TBSConstants.NO_SEARCH_RESULTS_URL+getSearchTerm()+"&type="+getSearchType()+"&errortype="+errortype);
			 }
			 		
		 }
		 else if(StringUtils.isBlank(getSearchTerm())){
			 addFormException(new DropletException("Please enter the valid search term"));
			 vlogDebug("No Search term found");
		 }
		 vlogDebug("TBSOrderSearchFormHandler :: handleSearch() :: END");
		 
		 return checkFormRedirect(getSuccessURL(), getFailureURL(), pRequest, pResponse);
	 }
	 
	 /**
	  * 
	  * @param pSearchTerm
	  * @return valid store id or not
	  */
		 public boolean isValidStore(String pSearchTerm) {
			 logDebug("TBSSearchFormHandler.isValidStore() method started"); 
			// TODO Auto-generated method stub
			 RepositoryItem lStoreRepositoryItem = null;
			 try {
				 lStoreRepositoryItem = getStoreRepository().getItem(pSearchTerm, "store");
				 if(lStoreRepositoryItem == null){
					 return false;
				 }
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logDebug("TBSSearchFormHandler.isValidStore() method end"); 
			return true;
		}

	/**
		 * 
		 * @param pSearchterm
		 * @return valid zip or not
		 */
		public boolean isValidZip(String pSearchterm) {		
			logDebug("TBSSearchFormHandler.isValidZip() method started"); 
				if(!BBBUtility.isEmpty(pSearchterm))
				{
					if(SiteContextManager.getCurrentSiteId().equals("TBS_BedBathCanada"))
					{
					if (!BBBUtility.isCanadaZipValid(pSearchterm) ) 
					{
						return false;
					}
					
				    }
					if (!BBBUtility.isValidZip(pSearchterm) ) 
					{
						return false;
					}
					return true;
				}
				logDebug("TBSSearchFormHandler.isValidZip() method end"); 
				return false;
			}
			
			/**
			 * 
			 * @param pSearchterm
			 * @return valid citystate or not
			 */
			public boolean isValidCityState(String pSearchterm) {		
				logDebug("TBSSearchFormHandler.isValidCityState() method started");  
				if(!BBBUtility.isEmpty(pSearchterm))
				{
					String[] lSearchtermArr = pSearchterm.split(",");
					String lCity = lSearchtermArr[0].toLowerCase().trim();
					String lState = lSearchtermArr[1].toLowerCase().trim();
					if (!BBBUtility.isEmpty(lCity)
							&& (!BBBUtility.isEmpty(lState) && !lState.equals(BBBCoreConstants.MINUS_ONE))) 
					{
						if (!BBBUtility.isValidCity(lCity) || (!BBBUtility.isAlphaOnly(lState))) 
						{
							return false;
						}
					}
					return true;
				}
				logDebug("TBSSearchFormHandler.isValidCityState() method end");  
				return false;
			} 
	 
	 /**
	  *This handle method is used for searching the orders based on OrderId or EmailId. 
	  * @param pRequest
	  * @param pResponse
	  * @return
	  * @throws ServletException
	  * @throws IOException
	  */
	 public boolean handleBasicOrderSearch(DynamoHttpServletRequest pRequest,
	         DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		 
		 vlogDebug("TBSSearchFormHandler :: handleBasicOrderSearch() method :: START");
		 getOrderSessionBean().clear();
		 pRequest.getSession().setAttribute(TBSConstants.DEFAULTTAB,TBSConstants.BASIC);
		 preBasicOrderSearch(pRequest, pResponse);
		 if(!getFormError()){
			 setOrderResults("true");
			 getOrderSessionBean().setEmail(mValue.get("email"));
			 getOrderSessionBean().setOrderId(mValue.get("orderId"));
		 }
		 vlogDebug("TBSSearchFormHandler :: handleBasicOrderSearch() method :: END");
		 return checkFormRedirect(getBasicOrderSearchSucessURL(), getBasicOrderSearchErrorURL(), pRequest, pResponse);
	 }
	 
	 /**
	  * This method is used for validating the user entered data of basic order search.
	  * @param pRequest
	  * @param pResponse
	  */
	 private void preBasicOrderSearch(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		 vlogDebug("TBSSearchFormHandler :: preBasicOrderSearch() method :: START");
		 if(StringUtils.isBlank(mValue.get("orderId")) && StringUtils.isBlank(mValue.get("email"))){
			 addFormException(new DropletException("Please enter valid OrderId or email Address"));
		 }

		 if(!StringUtils.isBlank(mValue.get("email"))){
			 boolean emailPattern = !BBBUtility.isValidEmail(mValue.get("email").trim());
			 if (emailPattern) {
				 addFormException(new DropletException("Please enter valid email Address"));
			 }
		 }
         vlogDebug("TBSSearchFormHandler :: preBasicOrderSearch() method :: END");
	}

	/**
	  *This handle method is used for searching the orders based on firstName, LastName, StartDate, EndDate, StoreId and RegistryId. 
	  * @param pRequest
	  * @param pResponse
	  * @return
	  * @throws ServletException
	  * @throws IOException
	  */
	 public boolean handleAdvanceOrderSearch(DynamoHttpServletRequest pRequest,
	         DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		 
		 vlogDebug("TBSSearchFormHandler :: handleAdvanceOrderSearch() method :: START");
		 getAdvancedOrderSearchBean().clear();
		 pRequest.getSession().setAttribute(TBSConstants.DEFAULTTAB,TBSConstants.ADVANCE);
		 
		 String mapDate = mValue.get("strtDate");
		 String[] ldateArr= new String[2];
		 String stDate = null,edDate = null;
		 
		 if(!mapDate.equals("") || !StringUtils.isEmpty(mapDate)){
			 ldateArr = mapDate.split(" - ");
			 stDate = ldateArr[0].trim();
			 edDate = ldateArr[1].trim();
			 
			 if(!getMinimalOrderFlag()){
				 stDate =  stDate.replace("/", "-"); 
				 edDate = edDate.replace("/", "-");
			 }
		 }
		 try {
			 preAdvanceOrderSearch(pRequest, pResponse);
			 if(!getFormError()){
				 setOrderResults("true");
				 String fNameFromDictionary = mValue.get("firstName");
				 String lNameFromDictionary = mValue.get("lastName");
				 String registryFromDictionary = mValue.get("registryId");
				 String storeNumberFromDictionary =	mValue.get("storeId");
				 
				 getAdvancedOrderSearchBean().setFirstName(fNameFromDictionary);
				 getAdvancedOrderSearchBean().setLastName(lNameFromDictionary);
				 getAdvancedOrderSearchBean().setStartDate(stDate);
				 getAdvancedOrderSearchBean().setEndDate(edDate);
				 getAdvancedOrderSearchBean().setRegistryNum(registryFromDictionary);
				 getAdvancedOrderSearchBean().setStoreNum(storeNumberFromDictionary);
				 String searchKey = fNameFromDictionary+lNameFromDictionary+stDate+edDate+storeNumberFromDictionary+registryFromDictionary;
				 
				 //Get resultSetThresholdSize from Config Keys: if null then get it from property file 
				 int numResultSetSize = getDefaultResultSetSize();
				 
				 List<String> resultSetSizeValueFromConfigKey = null;
				 
				 resultSetSizeValueFromConfigKey = getCatalogTools().getAllValuesForKey("AdvancedOrderInquiryKeys", "ResultSetSize");
				 				 
				 if (null != resultSetSizeValueFromConfigKey
							&& null != resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO)){
					 numResultSetSize = Integer.parseInt(resultSetSizeValueFromConfigKey.get(TBSConstants.ZERO));
				 } else {
					 numResultSetSize = getDefaultResultSetSize();
				 }
				 
				 vlogDebug("handleAdvanceOrderSearch() : ResultSetSize: "+numResultSetSize+" Search Term: "+searchKey);
				 advancedOrderSearch(numResultSetSize,searchKey);
			 } 
		 } catch (ParseException e) {
			 addFormException(new DropletException("An error occurred while searching the order"));
			 vlogError("Exception occurred "+e);
		} catch (BBBSystemException e) {
			vlogError("Exception occurred "+e);
		} catch (BBBBusinessException e) {
			vlogError("Exception occurred "+e);
		}
		 
		 vlogDebug("TBSSearchFormHandler :: handleAdvanceOrderSearch() method :: END");
		 return checkFormRedirect(getAdvanceOrderSearchSucessURL(), getAdvanceOrderSearchErrorURL(), pRequest, pResponse);
	 }
	 
	 /**
	  * This method is used for validating the fields entered by the user for the advance order search.
	  * Following are the minimum criteria used for validation:
	  * 1. Name Date
	  * 2. Name Store
	  * 3. Date Store
	  * 4. Registry
	  *  
	  * @param pRequest
	  * @param pResponse
	  * @throws ServletException
	  * @throws IOException
	 * @throws ParseException 
	  */
	 public void preAdvanceOrderSearch(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
             throws ServletException, IOException, ParseException {
		 vlogDebug("TBSSearchFormHandler :: preAdvanceOrderSearch() method :: START");
		 boolean firstNameFlag = false;
		 boolean lastNameFlag = false;
		 boolean dateFlag = false;
		 boolean storeIdFlag = false;
		 boolean registryNumFlag = false;
		 boolean minCriteriaErrorFlag = true;
		 boolean fieldErrorFlag = false;
		 
		 String firstName = mValue.get("firstName");
		 String lastName = mValue.get("lastName");
		 String mapDate = mValue.get("strtDate");
		 String[] ldateArr= new String[2];
		 String stDate = null,edDate = null;
		 if(!mapDate.equals("") || !StringUtils.isEmpty(mapDate)){
			 ldateArr = mapDate.split(" - ");
			 stDate = ldateArr[0].trim();
			 stDate =  stDate.replace("/", "-");
			 edDate = ldateArr[1].trim();
			 edDate = edDate.replace("/", "-");
		 }
		 
		 // Validate start and end date
		 if(!StringUtils.isBlank(stDate) && !StringUtils.isBlank(edDate)) {
			 Date date = new Date();
			 DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
			 Date startDate = dateFormat.parse(stDate);
			 Date endDate = dateFormat.parse(edDate);
			 
			 dateFlag = true;
			 if(startDate.compareTo(date) > TBSConstants.ZERO){
				 fieldErrorFlag = true;
			 } else if(startDate.compareTo(endDate) > TBSConstants.ZERO){
				 fieldErrorFlag = true;
			 }
		 }
		 
		// Validate firstName min 1 char
		 if(!StringUtils.isBlank(firstName)) {
			 firstNameFlag = true;
		 }
		// Validate lastName minimum 2 char
		 if(!StringUtils.isBlank(lastName)) {
			 lastNameFlag = true;
			 if(lastName.length() < 2) {
				 fieldErrorFlag = true;
			 }
		 }
		// Validate storeID number only check
		 if(!StringUtils.isBlank(mValue.get("storeId"))) {
			 storeIdFlag = true;
			 if(!Pattern.matches("[0-9]+", mValue.get("storeId"))){
				 fieldErrorFlag = true;
			 }
		 }
		// Validate Registry number
		 if(!StringUtils.isBlank(mValue.get("registryId"))){
			 registryNumFlag = true;
			 if (!BBBUtility.isStringPatternValid(getValidationRules().getAlphaNumericPattern(), mValue.get("registryId"))) {
				 fieldErrorFlag = true;
            } else if (!BBBUtility.isStringLengthValid(mValue.get("registryId"), 1, TBSConstants.MAX_REGISTRY_LENGTH)) {
            	fieldErrorFlag = true;
            }
		 }
		 
		 vlogDebug("preAdvanceOrderSearch() : firstName: "+firstName+" lastName: "+lastName+" Start Date: "+stDate+" End Date: "+edDate+" Registry: "+mValue.get("registryId")+"Store Num: "+mValue.get("storeId"));
		 
		 //Check if minimum criteria met, if met then set the errorFlag as false
		 if(firstNameFlag && lastNameFlag && dateFlag) {
			 minCriteriaErrorFlag = false;
		 } else if(firstNameFlag && lastNameFlag && storeIdFlag) {
			 minCriteriaErrorFlag = false;
		 } else if(dateFlag && storeIdFlag) {
			 minCriteriaErrorFlag = false;
		 } else if(registryNumFlag) {
			 minCriteriaErrorFlag = false;
		 }
		// If individual field error or minimum criteria not met then display the generic error message
		 if(minCriteriaErrorFlag || fieldErrorFlag) {
			 getAdvancedOrderSearchBean().setFirstName(firstName);
			 getAdvancedOrderSearchBean().setLastName(lastName);
			 getAdvancedOrderSearchBean().setStartDate(stDate);
			 getAdvancedOrderSearchBean().setEndDate(edDate);
			 getAdvancedOrderSearchBean().setRegistryNum(mValue.get("registryId"));
			 getAdvancedOrderSearchBean().setStoreNum(mValue.get("storeId"));
			 addFormException(new DropletException("Minimum Search Criteria was not met")); 
		 }
		 

		 vlogDebug("TBSSearchFormHandler :: preAdvanceOrderSearch() method :: END");
	 }
	 /**
	  * This method is invoked inside handleAdvancedOrderSearch, it does the following:
	  * 1. Queries the database
	  * 2. If the resultSetSize is greater than a threshold then throw an exception
	  * 3. Store the key and resultSet in cache
	  * 
	  * @param numResultSetSize
	  * @param searchKey
	  */
	 private void advancedOrderSearch(int numResultSetSize, String searchKey){
		 vlogDebug("TBSSearchFormHandler :: advancedOrderSearch() method :: Start");
		 List<RepositoryItem> orders = null;
		 List<OrderVO> resultedOrders = new ArrayList<OrderVO>();
		 
			try {
				if(getMinimalOrderFlag())
				{
					resultedOrders = ((TBSOrderTools)getOrderManager().getOrderTools())
							.minimalAdvancedOrderSearch(getAdvancedOrderSearchBean().getFirstName(), getAdvancedOrderSearchBean().getLastName(), 
							getAdvancedOrderSearchBean().getStartDate(),getAdvancedOrderSearchBean().getEndDate(), getAdvancedOrderSearchBean().getRegistryNum(), 
							getAdvancedOrderSearchBean().getStoreNum(),numResultSetSize);
				}
				else
				{
				
					orders = ((TBSOrderTools)getOrderManager().getOrderTools())
								.advancedOrderSearch(getAdvancedOrderSearchBean().getFirstName(), getAdvancedOrderSearchBean().getLastName(), 
								getAdvancedOrderSearchBean().getStartDate(),getAdvancedOrderSearchBean().getEndDate(), getAdvancedOrderSearchBean().getRegistryNum(), 
								getAdvancedOrderSearchBean().getStoreNum(),numResultSetSize);
					//Check if the resultSetSize is greater then threshold, if yes then remove the orders beyond the threshold
					if(orders!=null && orders.size()>numResultSetSize){
						vlogDebug("advancedOrderSearch(): Result Set Size: "+orders.size());
						getAdvancedOrderSearchBean().setOrderThresholdExceeded(true);
						for(int orderToBeDeleted=numResultSetSize; orderToBeDeleted<orders.size();orderToBeDeleted++){
							orders.remove(orderToBeDeleted);
						}
					}
				}

				getAdvancedOrderSearchBean().setSearchTerm(searchKey);
				
				if(!getMinimalOrderFlag() && orders != null && orders.size() > TBSConstants.ZERO){
					resultedOrders = new ArrayList();
					HashMap orderItemVO = new HashMap();
					orderItemVO.put(orders, resultedOrders);
					int totalOrders = 0;	
					if(null!=orders) {
						 totalOrders = orders.size();
					}
					//Set the searchKey and totalOrders in session.
					getAdvancedOrderSearchBean().setTotalOrders(totalOrders);
					//Set the values in cache memory
					if(!getAdvancedOrderSearchCache().getOutputOrders().containsKey(searchKey))
						getAdvancedOrderSearchCache().getOutputOrders().put(searchKey, orderItemVO);
				}
				else if(getMinimalOrderFlag() && resultedOrders != null && resultedOrders.size() > TBSConstants.ZERO)
				{
					//HashMap orderItemVO = new HashMap();
					//orderItemVO.put(resultedOrders, minimalOrders);
					int totalOrders = 0;	
					if(null!=resultedOrders) {
						 totalOrders = resultedOrders.size();
					}
					//Set the searchKey and totalOrders in session.
					getAdvancedOrderSearchBean().setTotalOrders(totalOrders);
					if(totalOrders >=numResultSetSize){
						getAdvancedOrderSearchBean().setOrderThresholdExceeded(true);
					}
					//Set the values in cache memory
					if(!getAdvancedOrderSearchCache().getOutputMinimalOrders().containsKey(searchKey))
						getAdvancedOrderSearchCache().getOutputMinimalOrders().put(searchKey, resultedOrders);
				}
				
			} catch (RepositoryException e) {
				vlogError("Exception occurred "+e);
			} catch (ParseException e) {
				vlogError("ParseException occurred "+e);
			}
			vlogDebug("TBSSearchFormHandler :: advancedOrderSearch() method :: END");	
			
	 }

	 private boolean getMinimalOrderFlag()
	 {
		//Fetch key to check if we need to call Store Proc for Order Inquiry
			List<String> configKeyValues;
			try {
				configKeyValues = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "TBS_MINIMAL_ORDERDETAIL");
				String minimalOrderKey = null;
				boolean minimalOrderRequired=false;
				if(!BBBUtility.isListEmpty(configKeyValues))
				{
					minimalOrderKey = configKeyValues.get(0);
				}
				
				if(BBBUtility.isNotEmpty(minimalOrderKey) && minimalOrderKey.equalsIgnoreCase(BBBCoreConstants.TRUE))
				{
					minimalOrderRequired=true;
				}
				return minimalOrderRequired;
			} catch (BBBSystemException | BBBBusinessException e) {
				vlogError("Exception occurred while fetching key: [TBS_MINIMAL_ORDERDETAIL]", e);
			}		 
		 return false;
	 }
}
