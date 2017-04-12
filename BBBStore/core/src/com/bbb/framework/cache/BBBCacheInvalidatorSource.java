package com.bbb.framework.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.transaction.TransactionManager;

import org.apache.axis2.java.security.AccessController;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.vo.MobileNearCacheVO;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.utils.BBBUtility;
import com.tangosol.net.CacheFactory;
import com.tangosol.net.cache.NearCache;

import atg.dms.patchbay.MessageSource;
import atg.dms.patchbay.MessageSourceContext;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import weblogic.management.provider.ManagementService;
import weblogic.security.acl.internal.AuthenticatedSubject;
import weblogic.security.service.PrivilegedActions;

/**
 * @author BBB
 *
 */
public class BBBCacheInvalidatorSource extends BBBGenericService implements MessageSource {
	
    MessageSourceContext mMessageSourceContext;
    boolean mSendingMessages;
    TransactionManager mTransactionManager;
    String mPortName;
	private static final String LOCALHOST = "http://localhost";
	private static final String CACHE_NAME_KEY_PARAM = "cacheName";
	private static final String CACHE_MENU_CONTENTS = "/store/_includes/header/elements/menu_contents.jsp###";
	private static final String CACHE_HEADER_CONTENTS = "/store/_includes/header/header.jsp###";
	private static final String CACHE_NAVIGATION_CONTENT = "/store/_includes/header/header_top_nav.jsp###";
	private static final String CONFIG_CACHE = "Config_Cache";
	private static final String LABEL_CACHE = "Label_Cache";
	private static final String CACHE_CLP_CONTENTS = "/store/browse/custom_landing.jsp###";
	private static final String ALL_CACHE = "AllCache";
	private static final String ERR_MSG_SIZE_NEAR_CACHE = "Cannot get size of cache Region - ";
	private String cacheMsgPort;
	private static final String CACHE_FOOTER_CONTENTS = "/store/_includes/footer/footer.jsp###";
	private static final String CACHE_L1_CONTENTS = "/store/browse/category_landing.jsp###";
	private String collectionChildRelnCachePortName;
	private String categoryRedirectURLCachePortName;
	private String nearCacheMsgPort;
	private String nearMobileCacheMsgPort;
	private HTTPCallInvoker httpCallInvoker;
	private BBBCatalogTools catalogTools;
	private String cacheSizeServiceUrl;
	private String serverPort;
	private String interactiveChecklistCachePortName;
	private static final String INTERACTIVE_CHECKLIST_CACHE ="Interactive_Checklist_Cache";
	private String dynamicPriceCachePort;
	private static final String REGISTRY_ITEMS_GUEST_C1 = "/store/giftregistry/frags/registry_items_guest_view.jsp###";
	private static final String REGISTRY_OVERLAY_STATIC_C1C2C3 = "/store/_includes/header/elements/registryOverlay.jsp###";
	private static final String STATIC_CHECKLIST_TYPES = "/store/_includes/header/elements/staticCheckListTypes.jsp###";
	private static final String STATIC_CHECKLIST_FULL_VIEW = "/store/_includes/header/elements/checklistStaticFullView.jsp###";
	private String tbsCacheMenuContentsFile;
	private String interactiveRepositoryPortName;

	
	/**
	 * @return the serverPort
	 */
	public String getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort the serverPort to set
	 */
	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}
	
	/**
	 * @return the cacheRefreshServiceUrl
	 */
	public String getCacheSizeServiceUrl() {
		return cacheSizeServiceUrl;
	}

	/**
	 * @param cacheRefreshServiceUrl the cacheRefreshServiceUrl to set
	 */
	public void setCacheSizeServiceUrl(String cacheSizeServiceUrl) {
		this.cacheSizeServiceUrl = cacheSizeServiceUrl;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	/**
	 * @return the httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {
		return httpCallInvoker;
	}

	/**
	 * @param httpCallInvoker the httpCallInvoker to set
	 */
	public void setHttpCallInvoker(HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}
	
	/**
	 * @return
	 */
	public String getNearMobileCacheMsgPort() {
		return nearMobileCacheMsgPort;
	}

	/**
	 * @param nearCacheMsgPort
	 */
	public void setNearMobileCacheMsgPort(String nearMobileCacheMsgPort) {
		this.nearMobileCacheMsgPort = nearMobileCacheMsgPort;
	}
	

	/**
	 * @return
	 */
	public String getNearCacheMsgPort() {
		return nearCacheMsgPort;
	}

	/**
	 * @param nearCacheMsgPort
	 */
	public void setNearCacheMsgPort(String nearCacheMsgPort) {
		this.nearCacheMsgPort = nearCacheMsgPort;
	}

	/**
	 * @return the cacheMsgPort
	 */
	public String getCacheMsgPort() {
		return this.cacheMsgPort;
	}

	/**
	 * @param cacheMsgPort the cacheMsgPort to set
	 */
	public void setCacheMsgPort(String cacheMsgPort) {
		this.cacheMsgPort = cacheMsgPort;
	}

	/**
	 * @return the categoryRedirectURLCachePortName
	 */
	public String getCategoryRedirectURLCachePortName() {
		return categoryRedirectURLCachePortName;
	}
	

	/**
	 * @param categoryRedirectURLCachePortName the categoryRedirectURLCachePortName to set
	 */
	public void setCategoryRedirectURLCachePortName(String categoryRedirectURLCachePortName) {
		this.categoryRedirectURLCachePortName = categoryRedirectURLCachePortName;
	}
	

	/**
	 *
	 */
	public void fireNavDropletCacheInvalidationMessage() {
		CacheInvalidationMessage message1 = new CacheInvalidationMessage(CACHE_MENU_CONTENTS, true);
		CacheInvalidationMessage message2 = new CacheInvalidationMessage(CACHE_HEADER_CONTENTS, true);
		CacheInvalidationMessage message3 = new CacheInvalidationMessage(CACHE_FOOTER_CONTENTS, true);
		CacheInvalidationMessage message4 = new CacheInvalidationMessage(CACHE_NAVIGATION_CONTENT, true);


		fireDropletCacheInvalidationMessage(message1);
		fireDropletCacheInvalidationMessage(message2);
		fireDropletCacheInvalidationMessage(message3);
		fireDropletCacheInvalidationMessage(message4);

	}

	/**
	 * 
	 */
	public void fireTBSNavDropletCacheInvMessage() {
		CacheInvalidationMessage menuContentsMessage = new CacheInvalidationMessage(getTbsCacheMenuContentsFile(), true);

		fireDropletCacheInvalidationMessage(menuContentsMessage);
	}

	/**
	 *
	 */
	public void fireL1CategoryCacheInvalidationMessage() {
		CacheInvalidationMessage message1 = new CacheInvalidationMessage(CACHE_L1_CONTENTS, true);

		fireDropletCacheInvalidationMessage(message1);

	}


	/**
	 * This method invalidates CLP cache
	 */
	public void fireCLPDropletCacheInvalidationMessage() {
		CacheInvalidationMessage message = new CacheInvalidationMessage(CACHE_CLP_CONTENTS, true);
		fireDropletCacheInvalidationMessage(message);
		logDebug("fireCLPDropletCacheInvalidationMessage() is invoked and cache is cleared");
	}
	/**
	 *
	 */
	public void fireConfigCacheContainerInvalidationMessage() {
		LocalCacheInvalidationMessage msg = new LocalCacheInvalidationMessage(CONFIG_CACHE, true, ALL_CACHE);
		fireObjectMessage(msg, getCacheMsgPort(), null);
	}

	/**
	 *
	 */
	public void fireLocalLabelCacheContainerInvalidationMessage() {
		LocalCacheInvalidationMessage msg = new LocalCacheInvalidationMessage(LABEL_CACHE, true, ALL_CACHE);
		fireObjectMessage(msg, getCacheMsgPort(), null);
	}

    /**
     * @param msg
     * @param portName
     * @param jmsType
     */
    public void fireDropletCacheInvalidationMessage(CacheInvalidationMessage msg,
			String portName, String jmsType) {
		fireObjectMessage(msg, portName, jmsType);
	}
	/**
	 * @param msg
	 */
	public void fireDropletCacheInvalidationMessage(CacheInvalidationMessage msg) {
		fireObjectMessage(msg, getPortName(), null);
	}

	/**
	 * @param productCatalogCache
	 *
	 */
	public void fireCollectionChildRelnCacheInvalidationMessage(String productCatalogCache) {
		CollectionChildRelnCacheInvalidationMessage message = new CollectionChildRelnCacheInvalidationMessage(productCatalogCache);
		fireObjectMessage(message, getCollectionChildRelnCachePortName(), null);
	}

	public void fireCategoryredirectURLCacheInvalidationMessage(String categoryRedirectURLCache) {
		CategoryRedirectURLCacheInvalidationMessage message = new CategoryRedirectURLCacheInvalidationMessage(categoryRedirectURLCache);
		fireObjectMessage(message, getCategoryRedirectURLCachePortName(), null);
	}
	
	protected void fireObjectMessage(Serializable messageBean, String portName,
			String jmsType) {
		fireObjectMessage(messageBean, portName, jmsType, -1L);
	}

	protected void fireObjectMessage(Serializable messageBean, String portName,
			String jmsType, long pDeliveryDate) {
		if (this.mMessageSourceContext == null) {
				logDebug("source context is null");
			return;
		}
		if (this.mTransactionManager == null) {
			if (isLoggingWarning())
				logWarning("transaction manager is null");
			return;
		}
		if (!this.mSendingMessages) {
				logDebug("not sending messages");
			return;
		}
		logDebug((new StringBuilder())
					.append("firing JMS message of type ")
					.append(jmsType)
					.append(" to output port ")
					.append(portName)
					.append(", message = ")
					.append(messageBean)
					.append(pDeliveryDate != -1L ? (new StringBuilder())
							.append(", delivery time ").append(pDeliveryDate)
							.toString() : "").toString());
		TransactionDemarcation td = null;
		try {
			td = new TransactionDemarcation();
			td.begin(this.mTransactionManager, 3);
			ObjectMessage message = null;
			if(portName != null && !portName.equals("")) {
				message = this.mMessageSourceContext.createObjectMessage(portName);
			} else {
				message = this.mMessageSourceContext.createObjectMessage();
			}
			if(jmsType != null && !jmsType.equals("")) {
				message.setJMSType(jmsType);
			}
			message.setObject(messageBean);
			if (pDeliveryDate != -1L) {
				message.setObjectProperty("ATG_JMS_DELIVERY_DATE", Long.valueOf(pDeliveryDate));
			}
			if(portName != null && !portName.equals("")) {
				this.mMessageSourceContext.sendMessage(portName, message);
			} else {
				this.mMessageSourceContext.sendMessage(message);
			}
		} catch (JMSException jmse) {
				logError(jmse);
		} catch (TransactionDemarcationException exc) {
				logError(exc);
		} finally {
			try {
				if (td != null) {
					td.end();
				}
			} catch (TransactionDemarcationException exc) {
				logError(exc);
			}
		}
	}
	
    public void fireDynamicPriceCacheMessage(){
    	CacheInvalidationMessage message = new CacheInvalidationMessage(BBBCoreConstants.BLANK);
    	logDebug("Message fired for caching after Dynamic Price Store Procedure Execution");
    	fireObjectMessage(message, getDynamicPriceCachePort(), null);
		
	}

    @Override
	public void setMessageSourceContext(MessageSourceContext pContext) {
		this.mMessageSourceContext = pContext;
	}

	/**
	 * @return mMessageSourceContext
	 */
	public MessageSourceContext getMessageSourceContext() {
		return this.mMessageSourceContext;
	}

	@Override
	public void startMessageSource() {
		this.mSendingMessages = true;
	}

	@Override
	public void stopMessageSource() {
		this.mSendingMessages = false;
	}

	/**
	 * @param pTransactionManager
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		this.mTransactionManager = pTransactionManager;
	}

	/**
	 * @return mTransactionManager
	 */
	public TransactionManager getTransactionManager() {
		return this.mTransactionManager;
	}
	/**
	 * @return mPortName
	 */
	public String getPortName() {
		return this.mPortName;
	}
	/**
	 * @param portName
	 */
	public void setPortName(String portName) {
		this.mPortName = portName;
	}

	/**
	 * @return the collectionChildRelnCachePortName
	 */
	public String getCollectionChildRelnCachePortName() {
		return collectionChildRelnCachePortName;
	}

	/**
	 * @param collectionChildRelnCachePortName the collectionChildRelnCachePortName to set
	 */
	public void setCollectionChildRelnCachePortName(
			String collectionChildRelnCachePortName) {
		this.collectionChildRelnCachePortName = collectionChildRelnCachePortName;
	}
	
	// Near Cache Implementation
	/**
	 * @param cacheName
	 * @return
	 */
	public NearCache getNearCache(final String cacheName) {
        NearCache nCache = (NearCache) CacheFactory.getCache(cacheName);
        return nCache;
	}
	
	/**
	 * @param cacheName
	 * @return
	 */
	public boolean clearNearCache(final String cacheName) {
		NearCache nCache = getNearCache(cacheName);
		if (nCache != null) {
			nCache.getFrontMap().clear();
			CacheInvalidationMessage message = new CacheInvalidationMessage(
					cacheName);
			fireObjectMessage(message, getNearCacheMsgPort(), null);
			logInfo("Clearing Near Cache: " + cacheName);
			return true;
		}
		return false;
	}
	
	/**
	 * @param cacheName
	 * @return
	 */
	public boolean clearNearCache(final String cacheName, final List<String> keys, final boolean isMobile) {
		String portName = getNearCacheMsgPort();
		NearCache nCache = getNearCache(cacheName);
		if (nCache != null) {
			CacheInvalidationMessage message = new CacheInvalidationMessage(
					cacheName, keys);
			if(isMobile){
				portName = getNearMobileCacheMsgPort();
			}
			fireObjectMessage(message, portName, null);
			logInfo("Clearing Near Cache: " + cacheName);
			return true;
		}
		return false;
	}
	
	/**
	 * @param pCacheName
	 * @return
	 */
	public int getNearCacheSize(String pCacheName) {
		NearCache nCache = getNearCache(pCacheName);
		if (nCache != null && nCache.getFrontMap() != null) {
			return nCache.size();
		}
		return 0;
	}
	
	/**
	 * This method get the size of the near mobile cache by calling the service which calls the spring controller
	 * 
	 * @param pCacheName
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getNearMobileCacheSize(String pCacheName) {
		
		logDebug("BBBCacheInvalidatorSource.getNearMobileCacheSize [Start] cacheName = " + pCacheName);
		
		//Get the kernel identity and then get the listen port number of the current server
		if(BBBUtility.isEmpty(this.getServerPort())){
			AuthenticatedSubject kernelId = (AuthenticatedSubject)AccessController.doPrivileged(PrivilegedActions.getKernelIdentityAction());
			int portNumber = ManagementService.getRuntimeAccess(kernelId).getServer().getListenPort();
			this.setServerPort(String.valueOf(portNumber));
		}
		
		try {
			HashMap<String, String> headerParam = new HashMap<String, String>();
			String serviceURI = getCacheSizeServiceUrl();
			String completeUrl = (new StringBuffer()).append(LOCALHOST).append(BBBCoreConstants.COLON).append(this.getServerPort()).append(
					serviceURI).append(BBBCoreConstants.QUESTION_MARK).append(CACHE_NAME_KEY_PARAM).append(BBBCoreConstants.EQUAL).append(pCacheName).toString();
			
			//Call the service which invokes mobile spring controller to get the details
			logDebug("Calling the mobile service with URL = " + completeUrl);
			MobileNearCacheVO mobileNearCacheVO = getHttpCallInvoker().invokeToGetJson(MobileNearCacheVO.class, headerParam, completeUrl, null, BBBCoreConstants.GET);
			logDebug("Response after Calling the service with URL = " + completeUrl + " is : " + mobileNearCacheVO);
			
			//return the size if error does not exist
			if(null != mobileNearCacheVO && (BBBCoreConstants.TRUE).equals(mobileNearCacheVO.getErrorExists())){
				logError(ERR_MSG_SIZE_NEAR_CACHE + pCacheName  + mobileNearCacheVO.getErrorMessage());
				return mobileNearCacheVO.getErrorMessage();
			}else if (null != mobileNearCacheVO){
				return mobileNearCacheVO.getSize();
			}else{ 
				logError(ERR_MSG_SIZE_NEAR_CACHE + pCacheName);
				return ERR_MSG_SIZE_NEAR_CACHE + pCacheName;
			}
		}catch (BBBBusinessException e) {
			logError("BBBBusinessException occurred while getting size of near cache " + pCacheName + e);
			return ERR_MSG_SIZE_NEAR_CACHE + pCacheName;
		} catch (BBBSystemException e) {
			logError("BBBSystemException occurred while getting size of near cache " + pCacheName + e);
			return ERR_MSG_SIZE_NEAR_CACHE + pCacheName;
		}
	}

	/**
	 * @return the dynamicPriceCachePort
	 */
	public final String getDynamicPriceCachePort() {
		return dynamicPriceCachePort;
	}

	/**
	 * @param dynamicPriceCachePort the dynamicPriceCachePort to set
	 */
	public final void setDynamicPriceCachePort(String dynamicPriceCachePort) {
		this.dynamicPriceCachePort = dynamicPriceCachePort;
	}
	
	/**
	 * Fire interactive checklist droplet caches
	 */
	public void fireRegistryChecklistCacheDropletInvalidationMessage() {
		CacheInvalidationMessage registryItemGuestC1Msg = new CacheInvalidationMessage(REGISTRY_ITEMS_GUEST_C1, true);
		CacheInvalidationMessage registryOverlayStatic = new CacheInvalidationMessage(REGISTRY_OVERLAY_STATIC_C1C2C3, true);
		CacheInvalidationMessage staticChecklistTypes = new CacheInvalidationMessage(STATIC_CHECKLIST_TYPES, true);
		CacheInvalidationMessage staticChecklistFullView = new CacheInvalidationMessage(STATIC_CHECKLIST_FULL_VIEW, true);

		fireDropletCacheInvalidationMessage(registryItemGuestC1Msg);
		fireDropletCacheInvalidationMessage(registryOverlayStatic);
		fireDropletCacheInvalidationMessage(staticChecklistTypes);
		fireDropletCacheInvalidationMessage(staticChecklistFullView);

	}
	
	public String getInteractiveChecklistCachePortName() {
		return interactiveChecklistCachePortName;
	}

	public void setInteractiveChecklistCachePortName(
			String interactiveChecklistCachePortName) {
		this.interactiveChecklistCachePortName = interactiveChecklistCachePortName;
	}

	/**
	 * @return the tbsCacheMenuContentsFile
	 */
	public String getTbsCacheMenuContentsFile() {
		return tbsCacheMenuContentsFile;
	}

	/**
	 * @param tbsCacheMenuContentsFile the tbsCacheMenuContentsFile to set
	 */
	public void setTbsCacheMenuContentsFile(String tbsCacheMenuContentsFile) {
		this.tbsCacheMenuContentsFile = tbsCacheMenuContentsFile;
	}	
	 /* Fire Interactive checklist Repository caches/admin repo caches.
	 */
	public void fireInteractiveInvalidationMessage() {
		CacheInvalidationMessage message = new CacheInvalidationMessage(BBBCoreConstants.INTERACTIVE_CHECKLIST_REPOSITORY_CACHE);
		fireObjectMessage(message, getInteractiveRepositoryPortName(), null);
	}

	
	public String getInteractiveRepositoryPortName() {
		return interactiveRepositoryPortName;
	}

	public void setInteractiveRepositoryPortName(
			String interactiveRepositoryPortName) {
		this.interactiveRepositoryPortName = interactiveRepositoryPortName;
	}

}
