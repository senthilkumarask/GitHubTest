package com.bbb.email;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

import atg.repository.MutableRepository;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.email.TemplateEmailException;

import com.bbb.cms.EmailTemplateVO;
import com.bbb.cms.GSEmailVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class EmailTemplateManager extends BBBGenericService{

    private static final String ITEM_DESCRIPTOR_THEME = "channelThemeInfo";
	private static final String HOST_URL = "hostURL";
	private static final String FROMDATA_HOST_REQURL = "frmData_host_requestURL";
	private static final String EMAIL_FOOTER = "emailFooter";
	private static final String EMAIL_HEADER = "emailHeader";
	public static final String EMAIL_TYPE = "emailType";
	private static final String EMAIL_FROM = "emailFrom"; 
	private static final String EMAIL_SUBJECT = "emailSubject"; 
	private static final String EMAIL_BODY = "emailBody"; 
	private static final String HTTPS = "https:";

	private static final String STORE = "/store";

	private static final String EMAIL_FLAG = "emailFlag";
	//private static final String SHOW_VIEW_IN_BROWSER = "showViewInBrowser";
	
	private static final String SITE_NAME = "frmData_siteId";
	private static final String NO_IMG_PATH = "/_assets/global/images/no_image_available.jpg";
	private static final String FORWARD_SLASH = "/";
	private static final String TEMPLATE_DESC = "emailRepository";
	private static final String COLON = ":";
	private static final String BLANK = "";
	private static final String EMAIL_PERSIST_ID = "emailPersistId";
	private BBBEmailConfiguration mEmailConfiguration;
	private MutableRepository channelThemeRepository;
	public MutableRepository getChannelThemeRepository() {
		return channelThemeRepository;
	}
	public void setChannelThemeRepository(
			MutableRepository channelThemeRepository) {
		this.channelThemeRepository = channelThemeRepository;
	}
	//private BBBCatalogTools mBBBCatalogTools;
	private Repository mEmailTemplate;
	private BBBCatalogTools catalogTools;
	
	private Map<String, String> mTbsEmailSiteMap = new HashMap<String, String>();
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public Repository getEmailTemplate() {
		return mEmailTemplate;
	}

	public void setEmailTemplate(Repository pEmailTemplate) {
		this.mEmailTemplate = pEmailTemplate;
	}
	
	/**
	 * @return the emailConfiguration
	 */
	public BBBEmailConfiguration getEmailConfiguration() {
		return mEmailConfiguration;
	}

	/**
	 * @param pEmailConfiguration
	 *            the emailConfiguration to set
	 */
	public void setEmailConfiguration(BBBEmailConfiguration pEmailConfiguration) {
		mEmailConfiguration = pEmailConfiguration;
	}
	
	/**
	 * @return the tbsEmailSiteMap
	 */
	public Map<String, String> getTbsEmailSiteMap() {
		return mTbsEmailSiteMap;
	}

	/**
	 * @param pTbsEmailSiteMap the tbsEmailSiteMap to set
	 */
	public void setTbsEmailSiteMap(Map<String, String> pTbsEmailSiteMap) {
		mTbsEmailSiteMap = pTbsEmailSiteMap;
	}

	private String getImageHost(String pImgConfigURL, String pSiteId){
		
		StringBuffer image_host = new StringBuffer(30);
		
		image_host.append(pImgConfigURL);
		image_host.append("/_assets/emailtemplates/");
		if(BBBCoreConstants.SITE_BAB_US.equalsIgnoreCase(pSiteId) ){
			image_host.append(BBBCoreConstants.SITE_BBB_US_IMG_LOC);
		}else if ( BBBCoreConstants.SITE_BBB.equalsIgnoreCase(pSiteId)){
			image_host.append(BBBCoreConstants.SITE_BBB_IMG_LOC);
		}else if( BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(pSiteId)){
			image_host.append(BBBCoreConstants.SITE_BBB_CA_IMG_LOC);
		}
		
		return HTTPS+image_host.toString();
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
     * @throws RepositoryException
	 */
	private String getHost() throws BBBBusinessException, BBBSystemException, RepositoryException {
	
		logDebug("[Start]: getHost()");
		String hostpath = null;
		String hostStr = null;
		StringBuilder SplitStr = new StringBuilder();
		String tempChannelTheme = null;
		RepositoryItem channelThemeRepositoryItem = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();

		if (pRequest != null) {
			String url = pRequest.getRequestURL().toString();
			String contextPath = pRequest.getContextPath();
			String serverPort = pRequest.getServerPort() + BLANK;
			String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
			String channelTheme = null;
			if(channel!=null && channel.isEmpty()){
				channel = pRequest.getParameter(BBBCoreConstants.CHANNEL);
			}
			if(BBBUtility.isNotEmpty(channel) && (channel.equalsIgnoreCase(BBBCoreConstants.FF1) || channel.equalsIgnoreCase(BBBCoreConstants.FF2))){
				channelTheme = pRequest.getHeader(BBBCoreConstants.CHANNEL_THEME);
				if (BBBUtility.isNotEmpty(channelTheme)) {
					channelThemeRepositoryItem = this
							.getChannelThemeRepository().getItem(channelTheme,
									ITEM_DESCRIPTOR_THEME);
					if (null != channelThemeRepositoryItem) {
						tempChannelTheme = channelThemeRepositoryItem
								.getRepositoryId();
					} else {
						logError("Theme does not exist");
					}
				}
			}
			//Added this for STOFU where the email is theme specific
			if (BBBUtility.isNotEmpty(channelTheme) && null != tempChannelTheme
					&& tempChannelTheme.equalsIgnoreCase(channelTheme)) {
				String propertyHostUrl = (String) channelThemeRepositoryItem
						.getPropertyValue(HOST_URL);
				hostpath = BBBCoreConstants.HTTPS + BBBCoreConstants.CONSTANT_SLASH + propertyHostUrl;
			} else if ((pRequest
					.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER) != null && ((String) pRequest
					.getParameter(BBBCoreConstants.IS_FROM_SCHEDULER))
					.equalsIgnoreCase(BBBCoreConstants.TRUE))
					|| (BBBUtility.isNotEmpty(channel) && (channel
							.equalsIgnoreCase(BBBCoreConstants.MOBILEWEB)
							|| channel
									.equalsIgnoreCase(BBBCoreConstants.MOBILEAPP)
							|| channel.equalsIgnoreCase(BBBCoreConstants.FF1) || channel
								.equalsIgnoreCase(BBBCoreConstants.FF2)))) {
				try {

					List<String> configValue = getCatalogTools().getAllValuesForKey(BBBCoreConstants.MOBILEWEB_CONFIG_TYPE,BBBCoreConstants.REQUESTDOMAIN_CONFIGURE);
					if (configValue != null && configValue.size() > 0) {
						hostpath = BBBCoreConstants.HTTPS + BBBCoreConstants.CONSTANT_SLASH + configValue.get(0);
					}
				} catch (BBBSystemException e) {
					logError("EmailTemplateManager.getHost :: System Exception occured while fetching config value for config key "
							+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE
							+ "config type "
							+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
				} catch (BBBBusinessException e) {
					logError("EmailTemplateManager.getHost :: Business Exception occured while fetching config value for config key "
							+ BBBCoreConstants.REQUESTDOMAIN_CONFIGURE
							+ "config type "
							+ BBBCoreConstants.MOBILEWEB_CONFIG_TYPE + e);
				}
			} else {
			hostStr = url.split(contextPath)[0];
			logDebug("url: " + url);
			logDebug("contextPath: " + contextPath);
			logDebug("hostpath after context path split: " + hostStr);
			logDebug("serverPort: " + serverPort);
		
			SplitStr.append(COLON).append(serverPort);

			
			logDebug("SplitStr: " + SplitStr.toString());
	
			if (hostStr.contains(SplitStr.toString())) {
				logDebug("port found in URL");
				hostpath = hostStr.split(SplitStr.toString())[0];
			} else {
				logDebug("NO port in URL");
				hostpath = hostStr;
			}

		}
		}else {
			if (isLoggingWarning()) {
				logWarning("Request object is null from ServletUtil.getCurrentRequest()");
			}
		}

		logDebug("hostpath: " + hostpath);
		logDebug("[End]: getHost()");
	
		return hostpath;
	}

	@SuppressWarnings({ "rawtypes", "unchecked"}) 
	public EmailTemplateVO getEmailTemplateData(Map pPlaceHolderValues) throws TemplateEmailException
	{
		RepositoryView view = null;
		QueryBuilder queryBuilder = null;
		QueryExpression queryExpEmailType;
		QueryExpression queryEmailType;
		
		String pEmailType = null;
		String pSiteId = null;
		String pEmailPersistId = null;
		String pShowViewInBrowser = "";
		
		RepositoryItem item = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		String channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		EmailTemplateVO emailTemplateVO = new EmailTemplateVO();
		
		
	
		logDebug("EmailTemplateManager.getEmailTemplateData() Method Entering");
		
		if(pPlaceHolderValues != null && !pPlaceHolderValues.isEmpty()){
			if(pPlaceHolderValues.get(EMAIL_TYPE) != null){
				pEmailType = pPlaceHolderValues.get(EMAIL_TYPE).toString();
			}
			
			if(pPlaceHolderValues.get(SITE_NAME) != null){
				pSiteId = pPlaceHolderValues.get(SITE_NAME).toString();
			}
			
			if(pPlaceHolderValues.get(EMAIL_PERSIST_ID) != null){
				pEmailPersistId = pPlaceHolderValues.get(EMAIL_PERSIST_ID).toString();
			}
			if(pSiteId == null || pEmailType == null){
				logError("Invalid input Parameter : Either SiteId or EmailType is null in PlaceHolderValues");
				throw new TemplateEmailException("Invalid input Parameter : Either SiteId or EmailType is null in PlaceHolderValues");
			}
			
			logDebug("Values extracted from pPlaceHolderValues are : pSiteId : "+pSiteId+" & pEmailType : "+pEmailType);
			
		}else{			
			logError("Invalid input Parameter : pPlaceHolderValues Map is null");			
			throw new TemplateEmailException("Invalid input Parameter : pPlaceHolderValues Map is null");
		}
		try{
			
			view = getEmailTemplate().getView(TEMPLATE_DESC);
			
			queryBuilder = view.getQueryBuilder();
			
			queryExpEmailType = queryBuilder.createPropertyQueryExpression(EMAIL_TYPE);
			queryEmailType = queryBuilder.createConstantQueryExpression(pEmailType);
			
			final Query[] queries = new Query[1];
	        queries[0] = queryBuilder.createComparisonQuery(queryExpEmailType, queryEmailType, QueryBuilder.EQUALS);
	        
	        Query query = queryBuilder.createAndQuery(queries);
	        
	   
			logDebug("Executing Query to retrieve data : "+query);
		
	        
	        if(view.executeQuery(query) != null && view.executeQuery(query)[0] != null){
	        	
        		item = view.executeQuery(query)[0];			        	
	        	//Fetch imageHost URL from config Key
        		List<String> keysList = null;
				if (BBBUtility.isNotEmpty(channel)
						&& (channel.equalsIgnoreCase(BBBCoreConstants.FF1) || channel
								.equalsIgnoreCase(BBBCoreConstants.FF2))) {
					keysList = getEmailConfiguration().getAllValuesForKey(
							BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.IMAGE_HOST_GS);
				} else {
					keysList = getEmailConfiguration().getAllValuesForKey(
							BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.IMAGE_HOST);
				}
	        	//Fetch Host URL from config Key
	        	String host_Url = getHost();
	        	
	        	if (keysList != null && keysList.size() > BBBCoreConstants.ZERO) {
	        		pPlaceHolderValues.put(BBBCoreConstants.FRMDATA_IMAGE_URL, getImageHost(keysList.get(BBBCoreConstants.ZERO), pSiteId) );
	        	}
	        	String babyCanadaUrl =BBBConfigRepoUtils.getStringValue("ReferralControls", "BabyCanada_Source_URL");
	        	if(babyCanadaUrl!=null)
	        	{
	        		pPlaceHolderValues.put(BBBCoreConstants.FRMDATA_babyCanadaUrl, babyCanadaUrl);
	        	}
	        	
	        	if(host_Url != null){
	        		if(getTbsEmailSiteMap().get(pSiteId) != null){
	        			host_Url = getTbsEmailSiteMap().get(pSiteId);
	        			emailTemplateVO.setHostUrl(host_Url);
	        		}
	        		pPlaceHolderValues.put(BBBCoreConstants.FRMDATA_HOST_URL, host_Url);
	        	}
	        	logDebug("Placing current year in pPlaceHolderValues : frmData_currentYear : " + Calendar.getInstance().get(Calendar.YEAR));
	        	pPlaceHolderValues.put(BBBCoreConstants.FRM_DATA_CUR_YEAR, Calendar.getInstance().get(Calendar.YEAR));  
	        	emailTemplateVO.setSiteId(pSiteId);
	        	Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
	        	String InternationalFlag = null;
	        	String countryCode = null;
	        	String currencyCode = null;
	        	if(profile != null){
				     countryCode=	(String) profile.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
					 currencyCode=		(String) profile.getPropertyValue(BBBInternationalShippingConstants.CURRENCY_CODE);
					if(profile.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT) != null){
					 InternationalFlag=profile.getPropertyValue(BBBInternationalShippingConstants.INTERNATIONAL_CONTEXT).toString();
					}
	        	}
			    		emailTemplateVO.setCurrencyCode(currencyCode);
			    		emailTemplateVO.setCountryCode(countryCode);
			    		emailTemplateVO.setInternationalFlag(InternationalFlag);
	        		
	        	//stofu
	        	if(pPlaceHolderValues.containsKey(BBBCoreConstants.STOFU_GS_EMAIL)){
	        		List<String> scene7KeysList = getEmailConfiguration().getAllValuesForKey(
							BBBCoreConstants.THIRD_PARTY_URL, BBBCoreConstants.SCENE7_URL);
	        		String imagePath=HTTPS+keysList.get(0);
	        		String scene7Path=HTTPS+scene7KeysList.get(0);
	        		String hostStoreUrl=host_Url+STORE;
					if (pPlaceHolderValues.get(BBBCoreConstants.FRMDATA_LARGE_IMAGE) != null && BBBUtility.isNotEmpty(pPlaceHolderValues.get(BBBCoreConstants.FRMDATA_LARGE_IMAGE).toString())) {
						pPlaceHolderValues.put(BBBCoreConstants.FRMDATA_IMAGE,scene7Path+ FORWARD_SLASH+ pPlaceHolderValues.get(BBBCoreConstants.FRMDATA_LARGE_IMAGE));
					} else {
						pPlaceHolderValues.put(BBBCoreConstants.FRMDATA_IMAGE,imagePath + NO_IMG_PATH);
					}
	        		pPlaceHolderValues.put(BBBCoreConstants.FRMDATA_HOST_STORE_URL, hostStoreUrl);
		        	emailTemplateVO.setChannelId((String) pPlaceHolderValues.get(BBBCoreConstants.CHANNEL_ID));
					emailTemplateVO.setProductAvailabilityFlag((Boolean) pPlaceHolderValues.get(BBBCoreConstants.PRODUCT_AVAILABILITY_FLAG));
					emailTemplateVO.setProductId((String) pPlaceHolderValues.get(BBBCoreConstants.PRODUCT_ID));
					emailTemplateVO.setRecipientList((String) pPlaceHolderValues.get(BBBCoreConstants.RECIPIENT_LIST));
					emailTemplateVO.setReviewRating((String) pPlaceHolderValues.get(BBBCoreConstants.REVIEW_RATING));
					emailTemplateVO.setSkuId((String) pPlaceHolderValues.get(BBBCoreConstants.SKU_ID_STOFU));
					emailTemplateVO.setStoreId((String) pPlaceHolderValues.get(BBBCoreConstants.STORE_ID));
					emailTemplateVO.setStoreVO((StoreVO) pPlaceHolderValues.get(BBBCoreConstants.STOREVO));
					emailTemplateVO.setProductVo((ProductVO) pPlaceHolderValues.get(BBBCoreConstants.PRODUCTVO));
					emailTemplateVO.setPriceRangeDescription((String)pPlaceHolderValues.get(BBBCoreConstants.PRICE_RANGE_DESCRIPTION));
					emailTemplateVO.setSiblingProducts((List<ProductVO>)pPlaceHolderValues.get(BBBCoreConstants.SIBLING_PRODUCTS));
					emailTemplateVO.setGoodToKnow((Map<String,Boolean>)pPlaceHolderValues.get(BBBCoreConstants.GOOD_TO_KNOW));
					emailTemplateVO.setTableRegistryCartMap((Map<String, List<GSEmailVO>>) pPlaceHolderValues.get(BBBCoreConstants.TABLE_REGISTRY_CART_MAP));
					emailTemplateVO.setCompareArray((List<GSEmailVO>) pPlaceHolderValues.get(BBBCoreConstants.COMPARE_ARRAY));
					emailTemplateVO.setTableCheckListMapOuter((Map<String, Map<String, List<GSEmailVO>>>) pPlaceHolderValues.get(BBBCoreConstants.TABLE_CHECKLIST_MAP_OUTER));
					emailTemplateVO.setPrimaryCategoryCountMap((HashMap<String, String>) pPlaceHolderValues.get(BBBCoreConstants.PRIMARY_CATEGORY_COUNT_MAP));
					emailTemplateVO.setTableName((String) pPlaceHolderValues.get(BBBCoreConstants.TABLE_NAME));
					emailTemplateVO.setHostUrl(host_Url);
					
	        	}
				//stofu
				

	        	// Set Email Flag for Persistence #69
	        	if(item.getPropertyValue(EMAIL_FLAG) != null){	        	    
	        	    emailTemplateVO.setEmailFlag(item.getPropertyValue(EMAIL_FLAG).toString());
	        	    if(((String) item.getPropertyValue(EMAIL_FLAG)).equalsIgnoreCase("OFF")){
	        		 pShowViewInBrowser = "display: none;";
	        	    }else{
	        	    	String url = pRequest.getRequestURL().toString();
	        	    	String contextPath = pRequest.getContextPath();
	        			String serverPort = pRequest.getServerPort() + BLANK;
	        	    	String hostStr = url.split(contextPath)[0];
	        	    	StringBuilder SplitStr = new StringBuilder();

	        			SplitStr.append(COLON).append(serverPort);
	        			String requestHost="";
	        			
	        			logDebug("SplitStr: " + SplitStr.toString());
	        	
	        			if (hostStr.contains(SplitStr.toString())) {
	        				logDebug("port found in URL");
	        				requestHost = hostStr.split(SplitStr.toString())[0];
	        			} else {
	        				logDebug("NO port in URL");
	        				requestHost = hostStr;
	        			}
	        			logDebug("The parameter " + EmailTemplateManager.FROMDATA_HOST_REQURL + " is " + requestHost);
	        	    	pPlaceHolderValues.put(EmailTemplateManager.FROMDATA_HOST_REQURL, requestHost);
	        	    }
	        	}else{	        	    
	        	    pShowViewInBrowser = "display: none;";	        	    
	        	}
	        	if(pShowViewInBrowser != null){
	        	    //Replace dynamic values in Email Body #69
	        	    pPlaceHolderValues.put(BBBCoreConstants.SHOW_VIEW_IN_BROWSER , pShowViewInBrowser);
	        	    emailTemplateVO.setShowViewInBrowser(pShowViewInBrowser);
		        }
	        	if(item.getPropertyValue(EMAIL_TYPE) != null){
	        		emailTemplateVO.setEmailType(item.getPropertyValue(EMAIL_TYPE).toString());
	        	}
	        	if(item.getPropertyValue(EMAIL_FROM) != null){
	        		emailTemplateVO.setEmailFrom(item.getPropertyValue(EMAIL_FROM).toString());
	        	}
	        	if(item.getPropertyValue(EMAIL_SUBJECT) != null){
	        		emailTemplateVO.setEmailSubject(item.getPropertyValue(EMAIL_SUBJECT).toString());
	        	}
	        	if(pEmailPersistId != null){	        	  
	        	    	emailTemplateVO.setEmailPersistId(pEmailPersistId);
	        	}
	        	if(item.getPropertyValue(EMAIL_BODY) != null) {		        		
	        		String emailBodyVal = item.getPropertyValue(EMAIL_BODY).toString();
	        		String pageUrl = (String)pPlaceHolderValues.get(BBBGiftRegistryConstants.PAGE_URL);
	        		if(null != pageUrl && pageUrl.contains(BBBCoreConstants.QUESTION_MARK)){
	        			emailBodyVal = emailBodyVal.replace(BBBGiftRegistryConstants.PAGE_URL+BBBCoreConstants.QUESTION_MARK, BBBGiftRegistryConstants.PAGE_URL+BBBCoreConstants.AMPERSAND);
	        		}
	        		//Replace dynamic values in Email Body
		        	emailBodyVal = replaceDynamicValues(pPlaceHolderValues,
							emailBodyVal);
	        		emailTemplateVO.setEmailBody(emailBodyVal);
	        	}
	        	if(item.getPropertyValue(EMAIL_HEADER) != null){
	        		String emailHeader = item.getPropertyValue(EMAIL_HEADER).toString();
	        		//Replace dynamic values in Email Header
	        		emailHeader = replaceDynamicValues(pPlaceHolderValues,
	        				emailHeader);
	        		emailTemplateVO.setEmailHeader(emailHeader);
	        	}
	        	if(item.getPropertyValue(EMAIL_FOOTER) != null){
	        		String emailFooter = item.getPropertyValue(EMAIL_FOOTER).toString();
	        		//Replace dynamic values in Email Footer
	        		emailFooter = replaceDynamicValues(pPlaceHolderValues,
	        				emailFooter);
	        		emailTemplateVO.setEmailFooter(emailFooter);
	        	}
	        	if(pPlaceHolderValues.get(BBBCoreConstants.EMAILFROM) != null){
					emailTemplateVO.setEmailFromcart((String)pPlaceHolderValues.get(BBBCoreConstants.EMAILFROM));
				}
				if(pPlaceHolderValues.get(BBBCoreConstants.EMAIL_CART_MESSAGE) != null){
					emailTemplateVO.setMessageFromcart((String)pPlaceHolderValues.get(BBBCoreConstants.EMAIL_CART_MESSAGE));
				}
				if(pPlaceHolderValues.get(BBBCoreConstants.FIRST_NAME) != null){
					emailTemplateVO.setFirstName((String)pPlaceHolderValues.get(BBBCoreConstants.FIRST_NAME));
				}
	        }   
		}catch (RepositoryException re) {
			logError(LogMessageFormatter.formatMessage(null, "EmailTemplateManager.getEmailTemplateData() | RepositoryException "), re);			
		} catch (BBBSystemException se) {
			logError(LogMessageFormatter.formatMessage(null, "EmailTemplateManager.getEmailTemplateData() | BBBSystemException "), se);			
		} catch (BBBBusinessException be) {
			logError(LogMessageFormatter.formatMessage(null, "EmailTemplateManager.getEmailTemplateData() | BBBBusinessException "), be);			
		}
		
		logDebug("Existing method RegistryTemplateManager.getEmailTemplateData()");
		return emailTemplateVO;
	}

	/**
	 * @param pPlaceHolderValues
	 * @param pEmailSection
	 * @return
	 */
	private String replaceDynamicValues(Map pPlaceHolderValues,
			String pEmailSection) {
		
		
		logDebug("[START] replaceDynamicValues");
		logDebug("pEmailSection: "+ pEmailSection);
		
		
		if(pEmailSection!= null && !pPlaceHolderValues.isEmpty())
		{
			Set entrySet = pPlaceHolderValues.entrySet();
		    for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
		        Map.Entry value = (Map.Entry) iterator.next();
		        
		        if(value.getValue() != null)
		        {
		        	
		        	logDebug("REPLCING...." + (String) value.getKey() +" by " + value.getValue().toString());
		        	
		        	pEmailSection = pEmailSection.replaceAll((String) value.getKey(), Matcher.quoteReplacement(value.getValue().toString()));
		        }
		        
		    }
			
		}
		logDebug("[END] replaceDynamicValues");
		logDebug("pEmailSection: "+ pEmailSection);
		
		return pEmailSection;
	}
	
}
