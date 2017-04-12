package com.bbb.cms.manager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryImpl;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.ServletUtil;

import com.bbb.cms.LabelsVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.framework.cache.BBBWebCacheIF;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;


/**
 * This class is responsible for getting label value/text area value/Error
 * Message it would interact with repository and get the value.
 * 
 * @author ajosh8
 * 
 */
public class LblTxtTemplateManager extends BBBGenericService {

	private static final String LABEL_DESCRIPTOR = "labelTextArea";
	private static final String LABEL_TRANSLATIONS = "translations";
	private static final String LABEL_VALUE = "labelValue";
	private static final String LABEL_DEFAULT_VALUE = "labelValueDefault";
	private static final String TEXTAREA_VALUE = "textAreaValue";
	private static final String TEXTAREA_DEFAULT_VALUE = "textAreaValueDefault";
	private static final String LBL_TXT_KEY = "key";
	private static final String ERR_MSG = "errorMsg";
	private static final String ERR_MSG_DEFAULT = "errorMsgDefault";
	private static final String TEMPLATE_TYPE = "templateType";
	private static final String LABEL_VALUE_RQL = "key = ?0 and channel = ?1";
	private static final String ALL_LABELS_RQL = "channel = ?0";
	private static final String LABEL = "Label";
	private static final String TEXT_AREA = "TextArea";
	private static final String ERROR = "Error";
	private Repository mLabelTemplate;
	private BBBWebCacheIF labelCacheContainer;
	private String cacheName;
	private long cacheTimeout;
	
	/**
	 * @return the cacheTimeout
	 */
	public long getCacheTimeout() {
		return this.cacheTimeout;
	}

	/**
	 * @param cacheTimeout the cacheTimeout to set
	 */
	public void setCacheTimeout(long cacheTimeout) {
		this.cacheTimeout = cacheTimeout;
	}

	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return this.cacheName;
	}

	/**
	 * @param cacheName the cacheName to set
	 */
	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}


	/**
	 * @return the localLabelCacheContainer
	 */
	public BBBWebCacheIF getLabelCacheContainer() {
		return this.labelCacheContainer;
	}

	/**
	 * @param localLabelCacheContainer the localLabelCacheContainer to set
	 */
	public void setLabelCacheContainer(BBBWebCacheIF labelCacheContainer) {
		this.labelCacheContainer = labelCacheContainer;
	}

	/**
	 * @param mLabelTemplate
	 *          the mLabelTemplate to set
	 */
	public void setLabelTemplate(Repository pLabelTemplate) {
		this.mLabelTemplate = pLabelTemplate;
	}

	public Repository getLabelTemplate() {
		return this.mLabelTemplate;
	}



	/**
	 * This method will interact with repository and get the label Value based on
	 * Key
	 * 
	 * @param pKey
	 * @param pLanguage
	 * @param placeHolderMap
	 * @return
	 */
	public String getPageLabel(String pKey, String pLanguage, Map<String, String> placeHolderMap) {

		return getPageLabel(pKey, pLanguage, placeHolderMap, null);
	}


	/**
	 * This method will interact with repository and get the label Value based on
	 * Key and provided Site
	 * 
	 * @param pKey
	 * @param pLanguage
	 * @param placeHolderMap
	 * @param pSiteId
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public String getPageLabel(String pKey, String pLanguage, Map<String, String> placeHolderMap, String pSiteId) {

		RepositoryItem translationItem;
		Map<String, RepositoryItem> lblTranslation;
		String lblTxtValue = "";

	
		logDebug("LblTxtTemplateManager.getPageLblTxt() Method Entering");
	
		if (StringUtils.isEmpty(pSiteId)) {
			pSiteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}
		if (BBBUtility.isEmpty(pSiteId)){
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
		
		// firstly found the label key in the label cache container,
		// if not found , then do a repository call
		final String nameOfCache = getCacheName();
		final String cacheKey = getCacheKeyName(pKey,pSiteId,LABEL);
		long timeoutOfCache = getCacheTimeout();
		if (null != getLabelCacheContainer() && null != getLabelCacheContainer().get(cacheKey, nameOfCache)) {
			lblTxtValue =  (String) getLabelCacheContainer().get(cacheKey, nameOfCache);
		} 
		else {
			RepositoryItem[] items = getRepositoryContent(pKey);
			if (items != null && items.length != 0) {
				for (RepositoryItem item : items) {
					lblTranslation = (Map<String, RepositoryItem>) item.getPropertyValue(LABEL_TRANSLATIONS);
					translationItem = (RepositoryItem) lblTranslation.get(pSiteId);
					if (translationItem != null) {
						lblTxtValue = (String) translationItem.getPropertyValue(LABEL_VALUE);
					} else {
						lblTxtValue = (String) item.getPropertyValue(LABEL_DEFAULT_VALUE);
					}
				}
			} else {
				lblTxtValue = "";
			}
			
			// put the label key value in the label cache container
			if (null != getLabelCacheContainer()) {
				getLabelCacheContainer().put(cacheKey, lblTxtValue, nameOfCache, timeoutOfCache);
			}
		}
		
		lblTxtValue = replaceDynamicProperties(lblTxtValue, placeHolderMap);
		
		
		logDebug("LblTxtTemplateManager.getPageLblTxt() Method Ending");
		
		return lblTxtValue;
	}

	/**
	 * Method to replace the placeholder with dynamic data
	 * 
	 * @param properties
	 *          Map<String, String>
	 * @param value
	 *          String
	 * 
	 * @return String
	 */
	private String replaceDynamicProperties(String value,
			final Map<String, String> properties) {
		
		logDebug(this.getClass().getName() + " entering");
		
		if (!StringUtils.isBlank(value)
				&& (properties != null && !properties.isEmpty())) {
			Iterator<String> iterator = properties.keySet().iterator();

			while (iterator.hasNext()) {
				String key = iterator.next();
				String replaceString = "";
				if (properties.get(key) instanceof String) {
					replaceString = properties.get(key);
				} else {
					replaceString = String.valueOf(properties.get(key));
				}

				if (value.contains(key)) {
					value = value.replace("$" + key, replaceString);
				}
			}
		}
		logDebug(this.getClass().getName() + " exiting");
		return value;
	}

	/**
	 * This method will interact with repository and get the TextArea Value based
	 * on Key
	 * 
	 * @param pKey
	 * @param pLanguage
	 * @param pPlaceHolderMap
	 * @return
	 */

	public String getPageTextArea(String pKey, String pLanguage, Map<String, String> pPlaceHolderMap) {

		return getPageTextArea(pKey, pLanguage, pPlaceHolderMap, null);
	}

	/**
	 * This method will interact with repository and get the TextArea Value based
	 * on Key and Site
	 * 
	 * @param pKey
	 * @param pLanguage
	 * @param pPlaceHolderMap
	 * @param pSiteId
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public String getPageTextArea(String pKey, String pLanguage, Map<String, String> pPlaceHolderMap, String pSiteId) {
		RepositoryItem mTranslationItem;
		Map<String, RepositoryItem> mLblTranslation;
		String mlblTxtValue = "";

		logDebug("LblTxtTemplateManager.getPageTextArea() Method Entering");
		
		if (StringUtils.isEmpty(pSiteId)) {
			pSiteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}
		if (BBBUtility.isEmpty(pSiteId)){
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
		
		// firstly found the label textarea key in the label cache container,
		// if not found , then do a repository call
		final String nameOfCache = getCacheName();
		final String cacheKey = getCacheKeyName(pKey,pSiteId,TEXT_AREA);
		long timeoutOfCache = getCacheTimeout();
		if (null != getLabelCacheContainer() && null != getLabelCacheContainer().get(cacheKey, nameOfCache)) {
			mlblTxtValue =  (String) getLabelCacheContainer().get(cacheKey, nameOfCache);
		} 
		else {
			RepositoryItem[] items = getRepositoryContent(pKey);
	
			if (items != null && items.length != 0) {
	
				for (int i = 0; i < items.length; i++) {
	
					mLblTranslation = (Map<String, RepositoryItem>) items[i].getPropertyValue(LABEL_TRANSLATIONS);
					mTranslationItem = (RepositoryItem) mLblTranslation.get(pSiteId);
					if (mTranslationItem != null) {
	
						mlblTxtValue = (String) mTranslationItem.getPropertyValue(TEXTAREA_VALUE);
	
					} else {
						mlblTxtValue = (String) items[i].getPropertyValue(TEXTAREA_DEFAULT_VALUE);
					}
				}
			} else {
				mlblTxtValue = "";
			}
			// put the label textarea key value in the label cache container
			if (null != getLabelCacheContainer()) {
				getLabelCacheContainer().put(cacheKey, mlblTxtValue, nameOfCache, timeoutOfCache);
			}
		}
		
		mlblTxtValue = replaceDynamicProperties(mlblTxtValue, pPlaceHolderMap);
		
		logDebug("LblTxtTemplateManager.getPageTextArea() Method Ending");
		
		return mlblTxtValue;
	}

	/**
	 * This method will interact with repository and get the Error Message based
	 * on Key
	 * 
	 * @param pKey
	 * @param pLanguage
	 * @return String
	 */


	public String getErrMsg(String pKey, String pLanguage, Map<String, String> placeHolderMap) {

		return getErrMsg(pKey, pLanguage, placeHolderMap, null);
	}

	/**
	 * This method will interact with repository and get the Error Message based
	 * on Key and SiteId
	 * 
	 * @param pKey
	 * @param pLanguage
	 * @param placeHolderMap
	 * @param pSiteId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getErrMsg(String pKey, String pLanguage, Map<String, String> placeHolderMap, String pSiteId) {
		RepositoryItem mTranslationItem;
		Map<String, RepositoryItem> mLblTranslation;
		String mlblTxtValue = "";
		
		logDebug("LblTxtTemplateManager.getErrMsg() Method Entering");
		
		if (StringUtils.isEmpty(pSiteId)) {
			pSiteId = (String) ServletUtil.getCurrentRequest().getAttribute(BBBCoreConstants.SITE_ID);
		}
		if (BBBUtility.isEmpty(pSiteId)){
			pSiteId = SiteContextManager.getCurrentSiteId();
		}
		
		// firstly found the label error key in the label cache container,
		// if not found , then do a repository call
		final String nameOfCache = getCacheName();
		final String cacheKey = getCacheKeyName(pKey,pSiteId,ERROR);
		long timeoutOfCache = getCacheTimeout();
		if (null != getLabelCacheContainer() && null != getLabelCacheContainer().get(cacheKey, nameOfCache)) {
			mlblTxtValue =  (String) getLabelCacheContainer().get(cacheKey, nameOfCache);
		} 
		else {
			RepositoryItem[] items = getRepositoryContent(pKey);
			
			if (items != null && items.length != 0) {
				for (int i = 0; i < items.length; i++) {
					mLblTranslation = (Map<String, RepositoryItem>) items[i].getPropertyValue(LABEL_TRANSLATIONS);
					mTranslationItem = (RepositoryItem) mLblTranslation.get(pSiteId);
					if (mTranslationItem != null) {
	
						mlblTxtValue = (String) mTranslationItem.getPropertyValue(ERR_MSG);
	
					} else {
						mlblTxtValue = (String) items[i].getPropertyValue(ERR_MSG_DEFAULT);
					}
				}
			} else {
				mlblTxtValue = "";
			}
		
			// put the label error key value in the label cache container
			if (null != getLabelCacheContainer()) {
				getLabelCacheContainer().put(cacheKey, mlblTxtValue, nameOfCache, timeoutOfCache);
			}
		}
		
		mlblTxtValue = replaceDynamicProperties(mlblTxtValue, placeHolderMap);
		
		
		logDebug("LblTxtTemplateManager.getErrMsg() Method Ending");
		
		return mlblTxtValue;

	}

	/**
	 * This method will interact with repository based on key and get the instance
	 * of item descriptor
	 * 
	 * @param mKey
	 * @return RepositoryItem
	 */
	public RepositoryItem[] getRepositoryContent(String mKey) {
		RepositoryItem[] items = null;

	
		logDebug("LblTxtTemplateManager.getRepositoryContent() Method Start");
		

		TransactionManager transactionManager = getTransactionManager();
		TransactionDemarcation transactionDemarcation = new TransactionDemarcation();
		try {
			transactionDemarcation.begin(transactionManager, TransactionDemarcation.NOT_SUPPORTED);

			String channelId = BBBUtility.getChannel();
			//RM-27937 || MobileApp is coming as channel from Request while all labels are associated with MobileWeb
			if(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channelId)) {
				channelId = BBBCoreConstants.MOBILEWEB;
			}
			RepositoryView view =  getLabelTemplate().getView(LABEL_DESCRIPTOR);

			RqlStatement statement = RqlStatement.parseRqlStatement(LABEL_VALUE_RQL);

			Object params[] = new Object[2];
			params[0] = mKey;
			params[1] = channelId;
			
			items = statement.executeQuery(view, params);
			
		}  catch (TransactionDemarcationException e) {
			logError(LogMessageFormatter.formatMessage(	null, "TransactionDemarcationException - LblTxtTemplateManager.getRepositoryContent()"),e);
		} catch (RepositoryException e) {
			
			logError(LogMessageFormatter.formatMessage(null, "getRepositoryContent:","catalog_1063"),e);
			
		} catch (Exception e) {
			
				logError(LogMessageFormatter.formatMessage(null, "getRepositoryContent:","catalog_1064"),e);
			
		}finally{
			try {
					transactionDemarcation.end();
			} catch (TransactionDemarcationException e) {
				logInfo("Error occured while fetching Label from repository" + e.getMessage());
			}
		}
		
		logDebug("LblTxtTemplateManager.getRepositoryContent() Method Ending");
		
		return items;

	}
	
	private String getCacheKeyName(final String pKey , final String pSiteId ,final String type){
		String cacheKey = null;
		String channelId =null;
		if(null != ServletUtil.getCurrentRequest()) {
			channelId = ServletUtil.getCurrentRequest().getHeader(BBBCoreConstants.CHANNEL);
		}
		//RM-27937 || MobileApp is coming as channel from Request while all labels are associated with MobileWeb
		if(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channelId)) {
			channelId = BBBCoreConstants.MOBILEWEB;
		}
		if(BBBUtility.isNotEmpty(channelId)){		
			cacheKey = type + "_" + pKey + "_" + channelId + "_" + pSiteId;
		}else {
			cacheKey = type + "_" + pKey + "_" + pSiteId;
		}
		return cacheKey;
	}
	
	/**
	 * This method is used to get the instance of transaction manager for label repository.
	 * 
	 * @return
	 */
	protected TransactionManager getTransactionManager() 
	  {
	    Repository repository = getLabelTemplate();
	    if (repository instanceof RepositoryImpl) 
	      return ((RepositoryImpl) repository).getTransactionManager();
	    return null;
	  }
	

	@SuppressWarnings("unchecked")
	public LabelsVO getAllLabels(String pLanguage)
	{

		RepositoryItem[] items = null;
		String lblTxtValue = "";
		String lblTxtKey = "";
		Map<String,String> labels = new HashMap<String,String>();
		Map<String,String> labelTextArea = new HashMap<String,String>();
		Map<String,String> errorMsg = new HashMap<String,String>();
		LabelsVO labelsVO = new LabelsVO(labels, labelTextArea, errorMsg);
		try {
			String channelId = BBBUtility.getChannel();
			//RM-27937 || MobileApp is coming as channel from Request while all labels are associated with MobileWeb
			if(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(channelId)) {
				channelId = BBBCoreConstants.MOBILEWEB;
			}
			RepositoryView view =  getLabelTemplate().getView(LABEL_DESCRIPTOR);
			RqlStatement statement = RqlStatement.parseRqlStatement(ALL_LABELS_RQL);
			Object params[] = new Object[1];
			params[0] = channelId;
			items = statement.executeQuery(view, params);
			
			if (items != null && items.length != 0) {
				for (RepositoryItem item : items) {
					if(item != null)
					{
						lblTxtKey = (String) item.getPropertyValue(LBL_TXT_KEY);
						int templateType = -1;
						if(item.getPropertyValue(TEMPLATE_TYPE) != null && lblTxtKey != null){
							templateType = (Integer)item.getPropertyValue(TEMPLATE_TYPE);	
						}
						
						if(templateType >= 0 && templateType <= 2)
						{
							lblTxtValue=this.getAllLabel(templateType, lblTxtKey, pLanguage);
							if(templateType == 0)
								labels.put(lblTxtKey, lblTxtValue);
							else if(templateType == 1)
								labelTextArea.put(lblTxtKey, lblTxtValue);
							else if(templateType == 2)
								errorMsg.put(lblTxtKey, lblTxtValue);
						}
					}
				}
				labelsVO.setLabels(labels);
				labelsVO.setLabelTextArea(labelTextArea);
				labelsVO.setErrorMsg(errorMsg);
			}
					
		} catch (RepositoryException e) {
		
		 logError(LogMessageFormatter.formatMessage(null, "getAllLabels:","catalog_1063"),e);
		
		} catch (BBBBusinessException e) {
		
		logError(LogMessageFormatter.formatMessage(null, "getAllLabels:","catalog_1064"),e);
		
		}
		return labelsVO;
	}

	/**
	 * method to fetch label/error/text area data from Label template repository
	 * @param type type to identify what type of data needs to be fetched
	 * @param pKey key whose value needs to be fetched 
	 * @param pLanguage language for which needs to lookup the value
	 * @param placeHolderMap place holder for replacing any dynamic value in static content
	 * @return value of lable/error/text area fetched
	 * @throws BBBBusinessException exception in case any error occurred while fetching content
	 * 
	 */
	public String getAllLabel(int type,String pKey, String pLanguage)
			throws BBBBusinessException{
		logDebug("LblTxtTemplateManager.getAllLabel : START");
		if(StringUtils.isEmpty(pKey)||type<0){
			throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM,"required input parameter is null");
		}
		String value=null;
		logDebug("input parameters getAllLabel type: "+type +" pKey "+pKey+" pLanguage "+pLanguage);
		if(type==0){
			value= getPageLabel(pKey,pLanguage,null);
		}
		else if(type==1){

			value= getPageTextArea(pKey,pLanguage,null);
		}else if(type==2){
			value= getErrMsg(pKey,pLanguage,null);
		}

		logDebug("LblTxtTemplateManager.getAllLabel : END");
		return value;

	}

	/**
	 * This method is used to fetch label/error/text area data from Label template repository
	 * @param type type to identify what type of data needs to be fetched
	 * @param pKey key whose value needs to be fetched 
	 * @param pLanguage language for which needs to lookup the value
	 * @param placeHolderMap place holder for replacing any dynamic value in static content
	 * @return value of label/error/text area fetched
	 * @throws BBBBusinessException exception in case any error occurred while fetching content
	 * 
	 */
	public String getAllLabelForMobile(int type, String pKey, Map<String, String> placeHolderMap) throws BBBBusinessException{
		logDebug("LblTxtTemplateManager.getAllLabelForMobile : START");
		String pLanguage = null;
		if (StringUtils.isEmpty(pKey) || type < 0) {
			throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM, "required input parameter is null");
		}
		String value = null;
		logDebug("input parameters getAllLabelForMobile type: "+type +" pKey "+pKey+" pLanguage "+pLanguage);
		if (type == 0) {
			value = getPageLabel(pKey, pLanguage, placeHolderMap);
		} else if (type == 1) {
			value = getPageTextArea(pKey, pLanguage, placeHolderMap);
		} else if (type == 2) {
			value = getErrMsg(pKey, pLanguage, placeHolderMap);
		}

		logDebug("LblTxtTemplateManager.getAllLabelForMobile : END");
		return value;
	}

	/**
	 * method to fetch label/error/text area data from Label template repository
	 * for multiple keys
	 * @return Map<String,String> Map with key as of label/error/text area key and Map value as the value of the label/text area/error fetched
	 * @throws BBBBusinessException exception in case any error occurred while fetching content
	 * 
	 */
	public Map<String,String> getMultipleLabelValues(Map<String,String> labelKeyLabelTypeMap,String language)
			throws BBBBusinessException{

		Map<String,String> labelKeyLabelValMap=new HashMap<String,String> ();
		if(labelKeyLabelTypeMap!=null && !labelKeyLabelTypeMap.isEmpty()){
			Set<String> keySet=labelKeyLabelTypeMap.keySet();
			Integer labelType=1;
			String labelVal=null;
			for(String key:keySet){
				labelType=Integer.valueOf(labelKeyLabelTypeMap.get(key));
				try{
					labelVal=this.getAllLabel(labelType, key, language);
					labelKeyLabelValMap.put(key, labelVal);
				}catch(BBBBusinessException e){
					logError(" Ignoring Label key "+key +" error fetching value from repository "+e.getMessage());
				}
			}
		}
		else{
			throw new BBBBusinessException(BBBCmsConstants.ERROR_NULL_INPUT_PARAM,"required input parameter is null");
		}
		return labelKeyLabelValMap;
	}
	
	/**
	 * This method will interact with repository and get the TextArea Value based
	 * on Key when Request is from Scheduler
	 * 
	 * @param pKey
	 * @param pPlaceHolderMap
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	public String getPageTextArea(String pKey, Map<String, String> pPlaceHolderMap) {
		RepositoryItem mTranslationItem;
		Map<String, RepositoryItem> mLblTranslation;
		String mlblTxtValue = "";

		logDebug("LblTxtTemplateManager.getPageTextArea() Method Entering");
		
		String pSiteId = SiteContextManager.getCurrentSiteId();
		
		// firstly found the label textarea key in the label cache container,
		// if not found , then do a repository call
		final String nameOfCache = getCacheName();
		final String cacheKey = getCacheKeyName(pKey,pSiteId,TEXT_AREA);
		long timeoutOfCache = getCacheTimeout();
		if (null != getLabelCacheContainer() && null != getLabelCacheContainer().get(cacheKey, nameOfCache)) {
			mlblTxtValue =  (String) getLabelCacheContainer().get(cacheKey, nameOfCache);
		} 
		else {
			RepositoryItem[] items = getRepositoryContent(pKey);
	
			if (items != null && items.length != 0) {
	
				for (int i = 0; i < items.length; i++) {
	
					mLblTranslation = (Map<String, RepositoryItem>) items[i].getPropertyValue(LABEL_TRANSLATIONS);
					mTranslationItem = (RepositoryItem) mLblTranslation.get(pSiteId);
					if (mTranslationItem != null) {
	
						mlblTxtValue = (String) mTranslationItem.getPropertyValue(TEXTAREA_VALUE);
	
					} else {
						mlblTxtValue = (String) items[i].getPropertyValue(TEXTAREA_DEFAULT_VALUE);
					}
				}
			} else {
				mlblTxtValue = "";
			}
			// put the label textarea key value in the label cache container
			if (null != getLabelCacheContainer()) {
				getLabelCacheContainer().put(cacheKey, mlblTxtValue, nameOfCache, timeoutOfCache);
			}
		}
		
		mlblTxtValue = replaceDynamicProperties(mlblTxtValue, pPlaceHolderMap);
		
		logDebug("LblTxtTemplateManager.getPageTextArea() Method Ending");
		
		return mlblTxtValue;
	}

}