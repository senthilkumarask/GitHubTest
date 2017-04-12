
package com.bbb.email.manager;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;

import atg.core.util.StringUtils;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.email.TemplateEmailException;
import atg.userprofiling.email.TemplateEmailInfoImpl;

import com.bbb.cms.EmailTemplateVO;
import com.bbb.cms.GSEmailVO;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBStoreRestConstants;
import com.bbb.email.BBBEmailHelper;
import com.bbb.email.BBBTemplateEmailSender;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.store.catalog.BBBGSCatalogToolsImpl;
import com.bbb.store.catalog.vo.ProductGSVO;
import com.bbb.store.catalog.vo.SkuGSVO;
import com.bbb.utils.BBBUtility;

public class BBBGSEmailSender extends BBBGenericService {

	private String feedbackEmailType;
	private String pdpEmailType;
	private String compareEmailType;
	private String tableCheckListEmailType;
	private String templateUrl;
	private BBBTemplateEmailSender emailSender;
	private SiteContext siteContext;
	private TemplateEmailInfoImpl emailInfo;
	private BBBCatalogTools catalogTools;
	private BBBGSCatalogToolsImpl catalogGSTools;
	private EmailTemplateVO emailTemplateVO = new EmailTemplateVO();
	private String templateUrlName;
	private String senderNameParamName;
	private String senderEmailParamName;
	private String recipientNameParamName;
	private String recipientName;
	private String recipientEmailParamName;
	private String messageParamName;
	private String subject;
	private String subjectParamName;
	private String storeIdProperty;
	private String channelIdProperty;
	private String serverNameProperty;
	private String contextPathProperty;
	private String siteIdParamName;
	private String serverName;
	private String contextPath;
	private String defaultLocale;
	

	/**
	 * @return defaultLocale
	 */
	public final String getDefaultLocale() {
		return this.defaultLocale;
	}

	/**
	 * @param defaultLocale defaultLocale
	 */
	public final void setDefaultLocale(final String defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * @return the feedbackEmailType
	 */
	public final String getFeedbackEmailType() {
		return this.feedbackEmailType;
	}

	/**
	 * @param feedbackEmailType
	 *            the feedbackEmailType to set
	 */
	public final void setFeedbackEmailType(final String feedbackEmailType) {
		this.feedbackEmailType = feedbackEmailType;
	}

	/**
	 * @return the pdpEmailType
	 */
	public final String getPdpEmailType() {
		return this.pdpEmailType;
	}

	/**
	 * @param pdpEmailType
	 *            the pdpEmailType to set
	 */
	public final void setPdpEmailType(final String pdpEmailType) {
		this.pdpEmailType = pdpEmailType;
	}

	/**
	 * @return the compareEmailType
	 */
	public final String getCompareEmailType() {
		return this.compareEmailType;
	}

	/**
	 * @param compareEmailType
	 *            the compareEmailType to set
	 */
	public final void setCompareEmailType(final String compareEmailType) {
		this.compareEmailType = compareEmailType;
	}

	/**
	 * @return the tableCheckListEmailType
	 */
	public final String getTableCheckListEmailType() {
		return this.tableCheckListEmailType;
	}

	/**
	 * @param tableCheckListEmailType
	 *            the tableCheckListEmailType to set
	 */
	public final void setTableCheckListEmailType(final String tableCheckListEmailType) {
		this.tableCheckListEmailType = tableCheckListEmailType;
	}

	/**
	 * @return the emailInfo
	 */
	public final TemplateEmailInfoImpl getEmailInfo() {
		return this.emailInfo;
	}

	/**
	 * @param emailInfo
	 *            the emailInfo to set
	 */
	public final void setEmailInfo(final TemplateEmailInfoImpl emailInfo) {
		this.emailInfo = emailInfo;
	}

	/**
	 * @return the emailTemplateVO
	 */
	public final EmailTemplateVO getEmailTemplateVO() {
		return this.emailTemplateVO;
	}

	/**
	 * @param emailTemplateVO
	 *            the emailTemplateVO to set
	 */
	public final void setEmailTemplateVO(final EmailTemplateVO emailTemplateVO) {
		this.emailTemplateVO = emailTemplateVO;
	}

	/**
	 * @return the templateUrlName
	 */
	public final String getTemplateUrlName() {
		return this.templateUrlName;
	}

	/**
	 * @param templateUrlName
	 *            the templateUrlName to set
	 */
	public final void setTemplateUrlName(final String templateUrlName) {
		this.templateUrlName = templateUrlName;
	}

	/**
	 * @return the senderNameParamName
	 */
	public final String getSenderNameParamName() {
		return this.senderNameParamName;
	}

	/**
	 * @param senderNameParamName
	 *            the senderNameParamName to set
	 */
	public final void setSenderNameParamName(final String senderNameParamName) {
		this.senderNameParamName = senderNameParamName;
	}

	/**
	 * @return the senderEmailParamName
	 */
	public final String getSenderEmailParamName() {
		return this.senderEmailParamName;
	}

	/**
	 * @param senderEmailParamName
	 *            the senderEmailParamName to set
	 */
	public final void setSenderEmailParamName(final String senderEmailParamName) {
		this.senderEmailParamName = senderEmailParamName;
	}

	/**
	 * @return the recipientNameParamName
	 */
	public final String getRecipientNameParamName() {
		return this.recipientNameParamName;
	}

	/**
	 * @param recipientNameParamName
	 *            the recipientNameParamName to set
	 */
	public final void setRecipientNameParamName(final String recipientNameParamName) {
		this.recipientNameParamName = recipientNameParamName;
	}

	/**
	 * @return the recipientName
	 */
	public final String getRecipientName() {
		return this.recipientName;
	}

	/**
	 * @param recipientName
	 *            the recipientName to set
	 */
	public final void setRecipientName(final String recipientName) {
		this.recipientName = recipientName;
	}

	/**
	 * @return the recipientEmailParamName
	 */
	public final String getRecipientEmailParamName() {
		return this.recipientEmailParamName;
	}

	/**
	 * @param recipientEmailParamName
	 *            the recipientEmailParamName to set
	 */
	public final void setRecipientEmailParamName(final String recipientEmailParamName) {
		this.recipientEmailParamName = recipientEmailParamName;
	}

	/**
	 * @return the messageParamName
	 */
	public final String getMessageParamName() {
		return this.messageParamName;
	}

	/**
	 * @param messageParamName
	 *            the messageParamName to set
	 */
	public final void setMessageParamName(final String messageParamName) {
		this.messageParamName = messageParamName;
	}

	/**
	 * @return the subject
	 */
	public final String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject
	 *            the subject to set
	 */
	public final void setSubject(final String subject) {
		this.subject = subject;
	}

	/**
	 * @return the subjectParamName
	 */
	public final String getSubjectParamName() {
		return this.subjectParamName;
	}

	/**
	 * @param subjectParamName
	 *            the subjectParamName to set
	 */
	public final void setSubjectParamName(final String subjectParamName) {
		this.subjectParamName = subjectParamName;
	}

	/**
	 * @return the storeIdProperty
	 */
	public final String getStoreIdProperty() {
		return this.storeIdProperty;
	}

	/**
	 * @param storeIdProperty
	 *            the storeIdProperty to set
	 */
	public final void setStoreIdProperty(final String storeIdProperty) {
		this.storeIdProperty = storeIdProperty;
	}

	/**
	 * @return the channelIdProperty
	 */
	public final String getChannelIdProperty() {
		return this.channelIdProperty;
	}

	/**
	 * @param channelIdProperty
	 *            the channelIdProperty to set
	 */
	public final void setChannelIdProperty(final String channelIdProperty) {
		this.channelIdProperty = channelIdProperty;
	}

	/**
	 * @return the serverNameProperty
	 */
	public final String getServerNameProperty() {
		return this.serverNameProperty;
	}

	/**
	 * @param serverNameProperty
	 *            the serverNameProperty to set
	 */
	public final void setServerNameProperty(final String serverNameProperty) {
		this.serverNameProperty = serverNameProperty;
	}

	/**
	 * @return the contextPathProperty
	 */
	public final String getContextPathProperty() {
		return this.contextPathProperty;
	}

	/**
	 * @param contextPathProperty
	 *            the contextPathProperty to set
	 */
	public final void setContextPathProperty(final String contextPathProperty) {
		this.contextPathProperty = contextPathProperty;
	}

	/**
	 * @return the siteIdParamName
	 */
	public final String getSiteIdParamName() {
		return this.siteIdParamName;
	}

	/**
	 * @param siteIdParamName
	 *            the siteIdParamName to set
	 */
	public final void setSiteIdParamName(final String siteIdParamName) {
		this.siteIdParamName = siteIdParamName;
	}

	/**
	 * @return the serverName
	 */
	public final String getServerName() {
		return serverName;
	}

	/**
	 * @param serverName
	 *            the serverName to set
	 */
	public final void setServerName(final String serverName) {
		this.serverName = serverName;
	}

	/**
	 * @return the contextPath
	 */
	public final String getContextPath() {
		return this.contextPath;
	}

	/**
	 * @param contextPath
	 *            the contextPath to set
	 */
	public final void setContextPath(final String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * @return the templateUrl
	 */
	public final String getTemplateUrl() {
		return this.templateUrl;
	}

	/**
	 * @param templateUrl
	 *            the templateUrl to set
	 */
	public final void setTemplateUrl(final String templateUrl) {
		this.templateUrl = templateUrl;
	}

	/**
	 * @return the mEmailSender
	 */
	public final BBBTemplateEmailSender getEmailSender() {
		return this.emailSender;
	}

	/**
	 * @param emailSender
	 *            the mEmailSender to set
	 */
	public final void setEmailSender(final BBBTemplateEmailSender emailSender) {
		this.emailSender = emailSender;
	}

	/**
	 * @return the siteContext
	 */
	public final SiteContext getSiteContext() {
		return this.siteContext;
	}

	/**
	 * @param siteContext
	 *            the siteContext to set
	 */
	public final void setSiteContext(final SiteContext siteContext) {
		this.siteContext = siteContext;
	}

	/**
	 * @return catalogTools
	 */
	public final BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	/**
	 * @param catalogTools catalogTools
	 */
	public final void setCatalogTools(final BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * @return catalogGSTools
	 */
	public final BBBGSCatalogToolsImpl getCatalogGSTools() {
		return this.catalogGSTools;
	}

	/**
	 * @param catalogGSTools catalogGSTools
	 */
	public final void setCatalogGSTools(final BBBGSCatalogToolsImpl catalogGSTools) {
		this.catalogGSTools = catalogGSTools;
	}

	
	/**
	 * @param productId
	 * @param productAvailabilityFlag
	 * @param skuId
	 * @param recipientList
	 * @param reviewRating
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public final boolean sendPDPEmail(final String productId,
			final String productAvailabilityFlag, final String skuId,
			final String recipientList, final String reviewRating)
			throws BBBSystemException, BBBBusinessException {

		logDebug("BBBGSEmailSender sendPDPEmail:Inside Method");
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEND_PDP_EMAIL,
				this.getPdpEmailType());
		if (BBBUtility.isEmpty(productId)) {
			logDebug("productId is empty");
			logError(BBBStoreRestConstants.PRODUCT_ID_INVALID_MSG);
			throw new BBBBusinessException(
					BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
					BBBStoreRestConstants.PRODUCT_ID_INVALID_MSG);
		}
		String skuLargeImage = null;
		String productLargeImage = null;
		logDebug("BBBGSEmailSender sendPDPEmail: ProductId is: " + productId);
		logDebug("BBBGSEmailSender sendPDPEmail: SkuId is: " + skuId);
		logDebug("BBBGSEmailSender sendPDPEmail: Product Avialability Flag : " + productAvailabilityFlag);
		validateRecipientList(recipientList);
			try {
				final String storeId = ServletUtil.getCurrentRequest().getHeader(
						this.getStoreIdProperty());
				final String siteId = SiteContextManager.getCurrentSiteId();
				final String channelId = ServletUtil.getCurrentRequest().getHeader(
						this.getChannelIdProperty());
				validateStoreSiteChannel(storeId, siteId, channelId);
				final ProductGSVO productGSVo = this.getCatalogGSTools()
						.getGSProductDetails(productId, "");
				final GSEmailVO productEmailVo = new GSEmailVO();
				productEmailVo.setProductId(productId);
				Boolean isActive = false;
				Boolean isWebStockAvailable = false;
				isActive = this.catalogGSTools.validateWebActiveProduct(productId, siteId);
				productEmailVo.setName(productGSVo.getProductRestVO().getProductVO().getName());
				if (BBBUtility.isNotEmpty(skuId)) {
					final SkuGSVO skuGSVO = this.getCatalogGSTools().getGSSkuDetails(skuId);
					if (null != skuGSVO.getSkuImageMap()) {
						skuLargeImage = skuGSVO.getSkuImageMap().get(BBBStoreRestConstants.GSP);
					}
					if (BBBUtility.isEmpty(skuLargeImage)) {
						skuLargeImage = skuGSVO.getSkuRestVO().getSkuVO().getSkuImages().getBasicImage();
					}
				if (null != skuGSVO.getSkuInventory()
						&& skuGSVO.getSkuInventory().get(
								BBBStoreRestConstants.SKU_WEB_STOCK) > 0) {
						isWebStockAvailable = true;
					}
					isActive = this.catalogGSTools.validateWebActiveSku(skuId, siteId);
					final String upc = skuGSVO.getSkuRestVO().getSkuVO().getUpc();
					productEmailVo.setUpc(upc);
					productEmailVo.setSkuId(skuId);
					if (BBBUtility.isNotEmpty(skuGSVO.getSkuRestVO().getSkuVO().getDisplayName())) {
						productEmailVo.setName(skuGSVO.getSkuRestVO().getSkuVO().getDisplayName());
					}
					setSkuPriceVO(skuGSVO, productEmailVo);
					
				} else {
					setProductPriceVO(productGSVo, productEmailVo);
				}
				
				Map emailParams = collectParams(recipientList, siteId);
				final HashMap<String, Object> placeHolderValues = new HashMap<String, Object>();
				final StoreVO storeVO = this.getCatalogTools().getStoreDetails(storeId);
				getPlaceHolderValues(storeVO, placeHolderValues, siteId, this.getPdpEmailType());
				if (null != productGSVo.getProductImageMap()) {
					productLargeImage = productGSVo.getProductImageMap().get(BBBStoreRestConstants.GSP);
				}
				
				if (BBBUtility.isNotEmpty(skuLargeImage)) {
					productEmailVo.setLargeImage(skuLargeImage);
				} else if (BBBUtility.isNotEmpty(productLargeImage)) {
					productEmailVo.setLargeImage(productLargeImage);
				}
				 
				productEmailVo.setLongDescription(productGSVo
						.getProductRestVO().getProductVO().getShortDescription() + productGSVo
						.getProductRestVO().getProductVO().getLongDescription());
			if (BBBUtility.isNotEmpty(reviewRating)
					&& !reviewRating.equalsIgnoreCase(BBBCoreConstants.NULL_VALUE)
					&& Float.parseFloat(reviewRating) > 0) {
				productEmailVo
						.setReviewRating(BBBStoreRestConstants.BAZAAR_VOICE_START_STAR
								+ Float.parseFloat(reviewRating)
								+ BBBStoreRestConstants.BAZAAR_VOICE_END_STAR);
			}
				productEmailVo.setIsActive(isActive.toString());
				productEmailVo.setWebStockAvailable(isWebStockAvailable.toString());
				productEmailVo.setProductAvailabilityFlag(productAvailabilityFlag);
				emailParams.put(BBBStoreRestConstants.PLACE_HOLDER_VALUES, placeHolderValues);
				emailParams.put(BBBStoreRestConstants.PRODUCT_EMAIL_VO, productEmailVo);
				emailParams.put(BBBCoreConstants.STOREVO, storeVO);
				BBBEmailHelper.sendEmail(emailParams, this.getEmailSender(), this.getEmailInfo());
			} catch (TemplateEmailException e) {
				logError(BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_PDP_EMAIL,
						this.getPdpEmailType());
				throw new BBBBusinessException(
						BBBStoreRestConstants.EMAIL_SEND_FAIL_CODE,
						BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
			}
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_PDP_EMAIL,
				this.getPdpEmailType());
		return true;
	}


	
	/**
	 * @param jsonResultString
	 * @param recipientList
	 * @param emailType
	 * @param orderId
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public final boolean sendTableRegistryCartEmail(final String jsonResultString,
			final String recipientList, final String emailType, final String orderId) throws BBBSystemException,
			BBBBusinessException {

		logDebug("Enter method - sendTableRegistryCartEmail");
		
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.SEND_TABLE_REGISTRY_CART_EMAIL,
				emailType);
		validateRecipientList(recipientList);
		if (BBBUtility.isEmpty(emailType)) {
			logDebug("emailType is empty");
			logError(BBBStoreRestConstants.EMAIL_TYPE_EMPTY_MSG);
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.SEND_TABLE_REGISTRY_CART_EMAIL,
					emailType);
			throw new BBBBusinessException(
					BBBStoreRestConstants.EMAIL_TYPE_EMPTY_CODE,
					BBBStoreRestConstants.EMAIL_TYPE_EMPTY_MSG);
		}
		logDebug("emailType is: " + emailType);
		
		if (BBBUtility.isEmpty(jsonResultString)) {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.SEND_TABLE_REGISTRY_CART_EMAIL,
					emailType);
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
		}
		logDebug("jsonResultString is: " + jsonResultString);
			try {
				final String storeId = ServletUtil.getCurrentRequest().getHeader(
						this.getStoreIdProperty());
				final String siteId = SiteContextManager.getCurrentSiteId();
				final String channelId = ServletUtil.getCurrentRequest().getHeader(
						this.getChannelIdProperty());
				validateStoreSiteChannel(storeId, siteId, channelId);
				JSONObject jsonObject = null;
				jsonObject = (JSONObject) JSONSerializer
						.toJSON(jsonResultString);
				final DynaBean JSONResultbean = (DynaBean) JSONSerializer
						.toJava(jsonObject);
				final List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
				final Map<String, List<GSEmailVO>> tableRegistryCartMap = new LinkedHashMap<String, List<GSEmailVO>>();
				if (dynaBeanProperties
						.contains(BBBStoreRestConstants.TABLE_REGISTRY_CART)) {
					@SuppressWarnings("unchecked")
					final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
							.get(BBBStoreRestConstants.TABLE_REGISTRY_CART);
					
					if ((emailType.equals(BBBStoreRestConstants.EMAIL_CART))
							&& (channelId.equals(BBBStoreRestConstants.CHANNEL_FF2))) {
						final List<GSEmailVO> storeWebCartArray = new ArrayList<GSEmailVO>();
						final List<GSEmailVO> storeCartArray = new ArrayList<GSEmailVO>();
						final List<GSEmailVO> webCartArray = new ArrayList<GSEmailVO>();
						tableRegistryCartMap.put(BBBStoreRestConstants.STORE_WEB_AVAILABLE, storeWebCartArray);
						tableRegistryCartMap.put(BBBStoreRestConstants.ONLY_STORE_AVAILABLE, storeCartArray);
						tableRegistryCartMap.put(BBBStoreRestConstants.ONLY_WEB_AVAILABLE, webCartArray);
					}

					for (DynaBean item : itemArray) {
						setTableRegistryCartMap(channelId, emailType, siteId,
								tableRegistryCartMap, item);
					}
					
					if ((emailType.equals(BBBStoreRestConstants.EMAIL_CART))
							&& (channelId.equals(BBBStoreRestConstants.CHANNEL_FF2))) {
						Iterator it = tableRegistryCartMap.entrySet().iterator();
						while (it.hasNext())
						   {
						      final Entry<String, List<GSEmailVO>> item = (Entry) it.next();
						      if (item.getValue().isEmpty()) {
						    	  it.remove();
						      }
						   }
					}
					
				} else {
					logError(BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
					BBBPerformanceMonitor.end(
							BBBPerformanceConstants.SEND_TABLE_REGISTRY_CART_EMAIL,
							emailType);
					throw new BBBBusinessException(
							BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_CODE,
							BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
				}

				final StoreVO storeVO = this.getCatalogTools().getStoreDetails(storeId);
				final Map<String, Serializable> emailParams = collectParams(recipientList, siteId);
				final HashMap<String, Object> placeHolderValues = new HashMap<String, Object>();
				getPlaceHolderValues(storeVO, placeHolderValues, siteId, emailType);
				placeHolderValues.put(BBBCoreConstants.TABLE_REGISTRY_CART_MAP, tableRegistryCartMap);
				placeHolderValues.put(BBBCoreConstants.CHANNEL_ID, channelId);
				placeHolderValues.put(BBBStoreRestConstants.FRMDATA_SITEID, siteId);
				placeHolderValues.put(BBBStoreRestConstants.FRMDATA_ORDER_ID, orderId);
				placeHolderValues.put(BBBCoreConstants.STOREVO, storeVO);
				emailParams.put(BBBStoreRestConstants.PLACE_HOLDER_VALUES, placeHolderValues);
				BBBEmailHelper.sendEmail(emailParams, this.getEmailSender(), this.getEmailInfo());
			} catch (TemplateEmailException e) {
				logError(BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.SEND_TABLE_REGISTRY_CART_EMAIL,
						emailType);
				throw new BBBBusinessException(
						BBBStoreRestConstants.EMAIL_SEND_FAIL_CODE,
						BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
			}
		
		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.SEND_TABLE_REGISTRY_CART_EMAIL,
				emailType);
		return true;
	}

	/**
	 * @param jsonResultString
	 * @param recipientList
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean sendCompareEmail(final String jsonResultString,
			final String recipientList) throws BBBSystemException,
			BBBBusinessException {

		logDebug("Enter method - sendCompareEmail");
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.SEND_COMPARE_EMAIL,
				this.getCompareEmailType());
		validateRecipientList(recipientList);
		if (BBBUtility.isEmpty(jsonResultString)) {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_COMPARE_EMAIL,
					this.getCompareEmailType());
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
		}

		try {
				final String storeId = ServletUtil.getCurrentRequest().getHeader(
						this.getStoreIdProperty());
				final String siteId = SiteContextManager.getCurrentSiteId();
				final String channelId = ServletUtil.getCurrentRequest().getHeader(
						this.getChannelIdProperty());
				validateStoreSiteChannel(storeId, siteId, channelId);
				JSONObject jsonObject = null;
				jsonObject = (JSONObject) JSONSerializer
						.toJSON(jsonResultString);
				final DynaBean JSONResultbean = (DynaBean) JSONSerializer
						.toJava(jsonObject);
				final List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
				final List<GSEmailVO> compareArray = new ArrayList<GSEmailVO>();

				if (dynaBeanProperties.contains(BBBStoreRestConstants.COMPARE)) {
					@SuppressWarnings("unchecked")
					final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
							.get(BBBStoreRestConstants.COMPARE);
				if (itemArray.size() > BBBStoreRestConstants.NUMERIC_THREE
						|| itemArray.size() < BBBStoreRestConstants.NUMERIC_ONE) {
						logError(BBBStoreRestConstants.INVALID_NUMBER_OF_COMPARE_ITEMS_MSG);
						throw new BBBBusinessException(
								BBBStoreRestConstants.INVALID_NUMBER_OF_COMPARE_ITEMS_CODE,
								BBBStoreRestConstants.INVALID_NUMBER_OF_COMPARE_ITEMS_MSG);
					}
					for (DynaBean item : itemArray) {
						setCompareArray(siteId, compareArray, item);
					}
					
					if (null != channelId && channelId.equals(BBBStoreRestConstants.CHANNEL_FF2)) {
						int minNameLength = 0;
						int minDescpLength = 0;
						int count = 0;
						for (GSEmailVO compareProductArray : compareArray) {
							final int tempNameLength = compareProductArray.getName().length();
							final int tempDescpLength = compareProductArray.getLongDescription().length();
							if (count == 0) {
								minNameLength = tempNameLength;
								minDescpLength = tempDescpLength;
							} else {
								if (minNameLength > tempNameLength) {
									minNameLength = tempNameLength;
								} 
								if (minDescpLength > tempDescpLength) {
									minDescpLength = tempDescpLength;
								}
							}
							count++;
						}
						
						for (GSEmailVO compareProductArray : compareArray) {
							
							compareProductArray.setName(compareProductArray.getName().substring(
													BBBCoreConstants.ZERO, compareProductArray.getName().length() 
													== minNameLength ? minNameLength 
															: minNameLength - BBBCoreConstants.THREE)
											+ (compareProductArray.getName().length() > minNameLength ? "..."
													: ""));
							
							compareProductArray.setLongDescription(compareProductArray.getLongDescription().substring(
													BBBCoreConstants.ZERO, compareProductArray.getLongDescription().length() 
													== minDescpLength ? minDescpLength 
															: minDescpLength - BBBCoreConstants.THREE)
											+ (compareProductArray.getLongDescription().length() > minDescpLength ? "..."
													: ""));
						}
					}
				} else {
					logError(BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
					BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_COMPARE_EMAIL,
							this.getCompareEmailType());
					throw new BBBBusinessException(
							BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_CODE,
							BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
				}
				final StoreVO storeVO = this.getCatalogTools().getStoreDetails(storeId);
				final Map<String, Serializable> emailParams = collectParams(
						recipientList, siteId);
				final HashMap<String, Object> placeHolderValues = new HashMap<String, Object>();
				getPlaceHolderValues(storeVO, placeHolderValues, siteId, this.getCompareEmailType());
				placeHolderValues.put(BBBCoreConstants.CHANNEL_ID, channelId);
				placeHolderValues.put(BBBCoreConstants.STOREVO, storeVO);
				placeHolderValues.put(BBBCoreConstants.COMPARE_ARRAY,
						compareArray);
				emailParams.put(BBBStoreRestConstants.PLACE_HOLDER_VALUES,
						placeHolderValues);

				BBBEmailHelper.sendEmail(emailParams, this.getEmailSender(),
						this.getEmailInfo());

			} catch (TemplateEmailException e) {
				logError(BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_COMPARE_EMAIL,
						this.getCompareEmailType());
				throw new BBBBusinessException(
						BBBStoreRestConstants.EMAIL_SEND_FAIL_CODE,
						BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
			}
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_COMPARE_EMAIL,
				this.getCompareEmailType());
		return true;
	}

	/**
	 * @param jsonResultString
	 * @param recipientList
	 * @param tableName
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean sendTableCheckListEmail(String jsonResultString,
			final String recipientList, String tableName) throws BBBSystemException,
			BBBBusinessException {

		logDebug("Enter method - sendTableCheckListEmail");
		
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.SEND_TABLE_CHECKLIST_EMAIL,
				this.getTableCheckListEmailType());
		validateRecipientList(recipientList);
		if (BBBUtility.isEmpty(jsonResultString)) {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.SEND_TABLE_CHECKLIST_EMAIL,
					this.getTableCheckListEmailType());
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
		}
		if (BBBUtility.isEmpty(tableName)) {
			logError(BBBStoreRestConstants.TABLE_NAME_INVALID_MSG);
			BBBPerformanceMonitor.end(
					BBBPerformanceConstants.SEND_TABLE_CHECKLIST_EMAIL,
					this.getTableCheckListEmailType());
			throw new BBBBusinessException(
					BBBStoreRestConstants.TABLE_NAME_INVALID_CODE,
					BBBStoreRestConstants.TABLE_NAME_INVALID_MSG);
		}
			try {
				final String storeId = ServletUtil.getCurrentRequest().getHeader(
						this.getStoreIdProperty());
				final String siteId = SiteContextManager.getCurrentSiteId();
				final String channelId = ServletUtil.getCurrentRequest().getHeader(
						this.getChannelIdProperty());
				validateStoreSiteChannel(storeId, siteId, channelId);
				JSONObject jsonObject = null;
				jsonObject = (JSONObject) JSONSerializer
						.toJSON(jsonResultString);
				final DynaBean JSONResultbean = (DynaBean) JSONSerializer
						.toJava(jsonObject);
				final List<String> dynaBeanProperties = (ArrayList<String>) getPropertyNames(JSONResultbean);
				final Map<String, Map<String, List<GSEmailVO>>> tableCheckListMapOuter = new HashMap<String, Map<String, List<GSEmailVO>>>();
				//HashMap<String, String> primaryCategoryCountMap = new HashMap<String, String>();

				if (dynaBeanProperties
						.contains(BBBStoreRestConstants.TABLE_CHECKLIST)) {
					@SuppressWarnings("unchecked")
					final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
							.get(BBBStoreRestConstants.TABLE_CHECKLIST);
					
					for (DynaBean item : itemArray) {
					final Map<String, List<GSEmailVO>> tableCheckListMapInner = new HashMap<String, List<GSEmailVO>>();
						setTableCheckListMapOuter(tableCheckListMapOuter,
								//primaryCategoryCountMap,
								tableCheckListMapInner, item);
					}
				} else {
					logError(BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
					BBBPerformanceMonitor.end(
							BBBPerformanceConstants.SEND_TABLE_CHECKLIST_EMAIL,
							this.getTableCheckListEmailType());
					throw new BBBBusinessException(
							BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_CODE,
							BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
				}
				final StoreVO storeVO = this.getCatalogTools().getStoreDetails(storeId);
				final Map<String, Serializable> emailParams = collectParams(
						recipientList, siteId);
				final HashMap<String, Object> placeHolderValues = new HashMap<String, Object>();
				getPlaceHolderValues(storeVO, placeHolderValues,siteId,getTableCheckListEmailType());
				placeHolderValues.put(BBBCoreConstants.CHANNEL_ID, channelId);
				placeHolderValues.put(BBBCoreConstants.STOREVO, storeVO);
				placeHolderValues.put(
						BBBCoreConstants.TABLE_CHECKLIST_MAP_OUTER,
						tableCheckListMapOuter);
				//placeHolderValues.put(BBBCoreConstants.PRIMARY_CATEGORY_COUNT_MAP,primaryCategoryCountMap);
				placeHolderValues.put(BBBCoreConstants.TABLE_NAME, tableName);
				emailParams.put(BBBStoreRestConstants.PLACE_HOLDER_VALUES,
						placeHolderValues);

				BBBEmailHelper.sendEmail(emailParams, this.getEmailSender(),
						this.getEmailInfo());
			} catch (TemplateEmailException e) {
				logError(BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
				BBBPerformanceMonitor.end(
						BBBPerformanceConstants.SEND_TABLE_CHECKLIST_EMAIL,
						this.getTableCheckListEmailType());
				throw new BBBBusinessException(
						BBBStoreRestConstants.EMAIL_SEND_FAIL_CODE,
						BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
			}
		
		BBBPerformanceMonitor.end(
				BBBPerformanceConstants.SEND_TABLE_CHECKLIST_EMAIL,
				this.getTableCheckListEmailType());
		return true;
	}

	/**
	 * @param recipientList
	 * @param message
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public boolean sendFeedbackEmail(final String recipientList, String message)
			throws BBBSystemException, BBBBusinessException {

		logDebug("Enter method - sendFeedbackEmail");
		
		BBBPerformanceMonitor.start(
				BBBPerformanceConstants.SEND_FEEDBACK_EMAIL,
				this.getFeedbackEmailType());
		validateRecipientList(recipientList);
		if (BBBUtility.isEmpty(message)) {
			logError(BBBStoreRestConstants.EMAIL_MESSAGE_INVALID_MSG);
			BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_FEEDBACK_EMAIL,
					this.getFeedbackEmailType());
			throw new BBBBusinessException(
					BBBStoreRestConstants.EMAIL_MESSAGE_INVALID_CODE,
					BBBStoreRestConstants.EMAIL_MESSAGE_INVALID_MSG);
		}
		try {
				final String siteId = SiteContextManager.getCurrentSiteId();
				final Map<String, Serializable> emailParams = collectParams(
						recipientList, siteId);
				emailParams.put(this.getMessageParamName(), message);
				final HashMap<String, Object> placeHolderValues = new HashMap<String, Object>();
				placeHolderValues.put(
						BBBStoreRestConstants.FRMDATA_COMMENT_MESSAGE, message);
				placeHolderValues.put(BBBStoreRestConstants.FRMDATA_SITEID,
						siteId);
				placeHolderValues.put(BBBStoreRestConstants.EMAIL_TYPE,
						this.getFeedbackEmailType());
				placeHolderValues.put(BBBCoreConstants.STOFU_GS_EMAIL,
						BBBCoreConstants.STOFU_GS_EMAIL);
				emailParams.put(BBBStoreRestConstants.PLACE_HOLDER_VALUES,
						placeHolderValues);
				BBBEmailHelper.sendEmail(emailParams, this.getEmailSender(),
						this.getEmailInfo());

			} catch (TemplateEmailException e) {
				logError(BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
				BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_FEEDBACK_EMAIL,
						this.getFeedbackEmailType());
				throw new BBBBusinessException(
						BBBStoreRestConstants.EMAIL_SEND_FAIL_CODE,
						BBBStoreRestConstants.EMAIL_SEND_FAIL_MSG);
			}
		
		BBBPerformanceMonitor.end(BBBPerformanceConstants.SEND_FEEDBACK_EMAIL,
				this.getFeedbackEmailType());
		return true;
	}

	/**
	 * @param storeId
	 * @param siteId
	 * @param channelId
	 * @throws BBBBusinessException
	 */
	private void validateStoreSiteChannel(final String storeId,
			final String siteId, final String channelId)
			throws BBBBusinessException {
		if (BBBUtility.isEmpty(storeId)) {
			logDebug(BBBStoreRestConstants.STORE_ID_EMPTY_MSG);
			logError(BBBStoreRestConstants.STORE_ID_EMPTY_MSG);
			throw new BBBBusinessException(
					BBBCatalogErrorCodes.STORE_NOT_AVAILABLE_IN_REPOSITORY,
					BBBStoreRestConstants.STORE_ID_EMPTY_MSG);
		}
		logDebug("Inside validateStoreSiteChannel method : store id is: " + storeId);
		
		if (BBBUtility.isEmpty(siteId)) {
			logDebug(BBBStoreRestConstants.SITE_ID_EMPTY_MSG);
			logError(BBBStoreRestConstants.SITE_ID_EMPTY_MSG);
			throw new BBBBusinessException(
					BBBCmsConstants.ERROR_EMPTY_SITE_ID,
					BBBStoreRestConstants.SITE_ID_EMPTY_MSG);
		}
		logDebug("Inside validateStoreSiteChannel method : site id is: " + siteId);
		if (BBBUtility.isEmpty(channelId)) {
			logDebug(BBBStoreRestConstants.CHANNEL_EMPTY_MSG);
			logError(BBBStoreRestConstants.CHANNEL_EMPTY_MSG);
			throw new BBBBusinessException(
					BBBCoreErrorConstants.ERROR_HEADER_CHANNEL,
					BBBStoreRestConstants.CHANNEL_EMPTY_MSG);
		}
		logDebug("Inside validateStoreSiteChannel method : channel id is: " + channelId);
	}

	/**
	 * @param channelId
	 * @param emailType
	 * @param siteId
	 * @param tableRegistryCartMap
	 * @param item
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void setTableRegistryCartMap(final String channelId,
			final String emailType, final String siteId,
			final Map<String, List<GSEmailVO>> tableRegistryCartMap,
			final DynaBean item) throws BBBBusinessException,
			BBBSystemException {
		String productId = null;
		String skuId = null;
		String categoryId = null;
		String qty = null;
		String reviewRating = null;
		String inputTableName = null;
		String productAvailability = null;
		Boolean isWebStockAvailable = false;
		String skuImage = null;
		String productImage = null;
		SkuGSVO skuGSVO = null;
		
		final List<String> itemBeanProperties = (ArrayList<String>) getPropertyNames(item);
		if (itemBeanProperties.contains(BBBStoreRestConstants.PRODUCT_ID)
				&& null != item.get(BBBStoreRestConstants.PRODUCT_ID)) {
			productId = item.get(BBBStoreRestConstants.PRODUCT_ID).toString();
		}
		if (itemBeanProperties.contains(BBBStoreRestConstants.SKU_ID)
				&& null != item.get(BBBStoreRestConstants.SKU_ID)) {
			skuId = item.get(BBBStoreRestConstants.SKU_ID).toString();
		}
		if (itemBeanProperties.contains(BBBStoreRestConstants.CATEGORY_ID)
				&& null != item.get(BBBStoreRestConstants.CATEGORY_ID)) {
			categoryId = item.get(BBBStoreRestConstants.CATEGORY_ID).toString();
		}
		if (itemBeanProperties.contains(BBBStoreRestConstants.QUANTITY)
				&& null != item.get(BBBStoreRestConstants.QUANTITY)) {
			qty = item.get(BBBStoreRestConstants.QUANTITY).toString();
		}
		if (itemBeanProperties.contains(BBBStoreRestConstants.REVIEW_RATING)
				&& null != item.get(BBBStoreRestConstants.REVIEW_RATING)) {
			reviewRating = item.get(BBBStoreRestConstants.REVIEW_RATING).toString();
		}
		if (itemBeanProperties.contains(BBBStoreRestConstants.TABLE_NAME)
				&& null != item.get(BBBStoreRestConstants.TABLE_NAME)) {
			inputTableName = item.get(BBBStoreRestConstants.TABLE_NAME).toString();
		}
		if (itemBeanProperties
				.contains(BBBStoreRestConstants.PRODUCT_AVAILABILITY_FLAG)
				&& null != item
						.get(BBBStoreRestConstants.PRODUCT_AVAILABILITY_FLAG)) {
			productAvailability = item.get(BBBStoreRestConstants.PRODUCT_AVAILABILITY_FLAG).toString();
		}
	
		if (BBBUtility.isEmpty(productId)) {
			logDebug("productId is empty");
			logError(BBBStoreRestConstants.PRODUCT_ID_INVALID_MSG);
			throw new BBBBusinessException(
					BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
					BBBStoreRestConstants.PRODUCT_ID_INVALID_MSG);
		}
		logDebug("Inside setTableRegistryCartMap : productId is: " + productId);
		logDebug("Inside setTableRegistryCartMap : skuId is: " + skuId);
		logDebug("Inside setTableRegistryCartMap : categoryId is: " + categoryId);
		logDebug("Inside setTableRegistryCartMap : reviewRating is: " + reviewRating);
		logDebug("Inside setTableRegistryCartMap : inputTableName is: " + inputTableName);
		logDebug("Inside setTableRegistryCartMap : productAvailability is: " + productAvailability);
		
		Boolean isActive = false;
		
		isActive = this.catalogGSTools.validateWebActiveProduct(productId, siteId);
		
		final String categoryName = this.getCatalogGSTools().getCategoryName(productId,
				categoryId);
		final ProductGSVO productGSVo = this.getCatalogGSTools().getGSProductDetails(
				productId, categoryId);
		final GSEmailVO tableRegistryCartVO = new GSEmailVO();
		
		tableRegistryCartVO.setProductId(productId);
		tableRegistryCartVO.setName(productGSVo.getProductRestVO()
				.getProductVO().getName());
		
		
		if (null != productGSVo.getProductImageMap()) {
			 productImage = productGSVo.getProductImageMap().get(BBBStoreRestConstants.GSP);
		}
			
		final int quantity = Integer.parseInt(qty);
		tableRegistryCartVO.setQuantity(quantity);
		
		if (BBBUtility.isNotEmpty(skuId)) {
			skuGSVO = this.getCatalogGSTools().getGSSkuDetails(skuId);
			if (null != skuGSVO.getSkuImageMap()) {
				skuImage = skuGSVO.getSkuImageMap().get(BBBStoreRestConstants.GSP);
			}
			if (BBBUtility.isEmpty(skuImage)) {
				skuImage = skuGSVO.getSkuRestVO().getSkuVO().getSkuImages().getBasicImage();
			}
			if (null != skuGSVO.getSkuInventory()
					&& skuGSVO.getSkuInventory().get(
							BBBStoreRestConstants.SKU_WEB_STOCK) > 0) {
				isWebStockAvailable = true;
			}
			isActive = this.catalogGSTools.validateWebActiveSku(skuId, siteId);
			tableRegistryCartVO.setSkuId(skuId);
			tableRegistryCartVO.setUpc(skuGSVO.getSkuRestVO().getSkuVO()
					.getUpc());
			if (BBBUtility.isNotEmpty(skuGSVO.getSkuRestVO().getSkuVO().getDisplayName())) {
				tableRegistryCartVO.setName(skuGSVO.getSkuRestVO().getSkuVO().getDisplayName());
			}
			final Double salePrice = skuGSVO.getSkuRestVO().getSalePrice();
			final Double listPrice = skuGSVO.getSkuRestVO().getListPrice();
			
			if (salePrice > BBBStoreRestConstants.NUMERIC_ZERO) {
				final Double subTotal = salePrice * quantity;
				final String[] subTotalPrice = getPrice(formatCurrency(subTotal)).split(BBBStoreRestConstants.FULLSTOP);
				tableRegistryCartVO.setSubTotalPrice(subTotalPrice[0]);
				tableRegistryCartVO.setSubTotalDecimalPrice(subTotalPrice[1]);
				
			} else if (listPrice > BBBStoreRestConstants.NUMERIC_ZERO) {
				final Double subTotal = listPrice * quantity;
				final String[] subTotalPrice = this.getPrice(
						formatCurrency(subTotal)).split(
						BBBStoreRestConstants.FULLSTOP);
				tableRegistryCartVO.setSubTotalPrice(subTotalPrice[0]);
				tableRegistryCartVO.setSubTotalDecimalPrice(subTotalPrice[1]);
			}
			setSkuPriceVO(skuGSVO, tableRegistryCartVO);
		} else {
			setProductPriceVO(productGSVo, tableRegistryCartVO);
		}
		if (BBBUtility.isNotEmpty(skuImage)) {
			tableRegistryCartVO.setThumbnailImage(skuImage);
			tableRegistryCartVO.setSmallImage(skuImage);
		} else if (BBBUtility.isNotEmpty(productImage)) {
			tableRegistryCartVO.setThumbnailImage(productImage);
			tableRegistryCartVO.setSmallImage(productImage);
		}
		
		tableRegistryCartVO.setIsActive(isActive.toString());
		tableRegistryCartVO.setWebStockAvailable(isWebStockAvailable.toString());
		
		if (BBBUtility.isNotEmpty(reviewRating)
				&& !reviewRating.equalsIgnoreCase(BBBCoreConstants.NULL_VALUE)
				&& Float.parseFloat(reviewRating) > 0) {
			tableRegistryCartVO
					.setReviewRating(BBBStoreRestConstants.BAZAAR_VOICE_START_STAR
							+ Float.parseFloat(reviewRating)
							+ BBBStoreRestConstants.BAZAAR_VOICE_END_STAR);
		}
		if (null != item.get(BBBStoreRestConstants.REGISTRY_ID)) {
			tableRegistryCartVO.setRegistryID(item.get(BBBStoreRestConstants.REGISTRY_ID).toString());
		}
		if (BBBUtility.isNotEmpty(inputTableName)
				&& BBBUtility.isNotEmpty(productAvailability)) {
			tableRegistryCartVO.setTableName(inputTableName);
			tableRegistryCartVO.setProductAvailabilityFlag(productAvailability);
		} else {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
		}
		if (((emailType.equals(BBBStoreRestConstants.EMAIL_CART)) || (emailType
				.equals(BBBStoreRestConstants.EMAIL_REGISTRY)))
				&& (channelId.equals(BBBStoreRestConstants.CHANNEL_FF1))) {
			if (tableRegistryCartMap.containsKey(categoryName)) {
				List<GSEmailVO> tableRegistryCartArray = new ArrayList<GSEmailVO>();
				tableRegistryCartArray = tableRegistryCartMap.get(categoryName);
				tableRegistryCartArray.add(tableRegistryCartVO);
				tableRegistryCartMap.put(categoryName, tableRegistryCartArray);
			} else {
				final List<GSEmailVO> tableRegistryCartArray = new ArrayList<GSEmailVO>();
				tableRegistryCartArray.add(tableRegistryCartVO);
				tableRegistryCartMap.put(categoryName, tableRegistryCartArray);
			}
		}
		if ((emailType.equals(BBBStoreRestConstants.EMAIL_CART))
				&& (channelId.equals(BBBStoreRestConstants.CHANNEL_FF2))) {
			if (tableRegistryCartMap.containsKey(productAvailability)) {
					List<GSEmailVO> tableRegistryCartArray = null;
					tableRegistryCartArray = tableRegistryCartMap
							.get(productAvailability);
					tableRegistryCartArray.add(tableRegistryCartVO);
				}
			}
		
		if (emailType.equals(BBBStoreRestConstants.EMAIL_TABLE)) {
			if (null != skuGSVO && null != skuGSVO.getSkuInventory()) {
				if (skuGSVO.getSkuInventory().get(BBBInventoryManager.STORE).equals(BBBCoreConstants.ZERO)) {
					tableRegistryCartVO.setProductAvailabilityFlag(BBBCoreConstants.TRUE);
				} else {
					tableRegistryCartVO.setProductAvailabilityFlag(BBBCoreConstants.FALSE);
				}
			}
			if (tableRegistryCartMap.containsKey(inputTableName)) {
				List<GSEmailVO> tableRegistryCartArray = new ArrayList<GSEmailVO>();
				tableRegistryCartArray = tableRegistryCartMap
						.get(inputTableName);
				tableRegistryCartArray.add(tableRegistryCartVO);
				tableRegistryCartMap
						.put(inputTableName, tableRegistryCartArray);
			} else {
				final List<GSEmailVO> tableRegistryCartArray = new ArrayList<GSEmailVO>();
				tableRegistryCartArray.add(tableRegistryCartVO);
				tableRegistryCartMap
						.put(inputTableName, tableRegistryCartArray);
			}
		}
	}

	/**
	 * @param recipientList
	 * @param isError
	 * @return
	 * @throws BBBBusinessException
	 */
	private void validateRecipientList(final String recipientList)
			throws BBBBusinessException {
		String[] emailIds = null;
		String recipientEmailList = null;
		recipientEmailList = StringUtils.removeWhiteSpace(recipientList);
		if (!BBBUtility.isEmpty(recipientList)) {
			emailIds = recipientEmailList.split(BBBCoreConstants.SEMICOLON);
			for (String email : emailIds) {
				if (!BBBUtility.isValidEmail(email)) {
						logDebug(BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_MSG);
						logError(BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_MSG);
					throw new BBBBusinessException(
							BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_CODE,
							BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_MSG);
				}
			}
		} else {
			logDebug(BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_MSG);
			logError(BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_MSG);
			throw new BBBBusinessException(
					BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_CODE,
					BBBStoreRestConstants.RECIPIENT_EMAIL_INVALID_MSG);
		}
		logDebug("recipient email is: " + recipientList);
	}

	/**
	 * @param siteId
	 * @param compareArray
	 * @param item
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	private void setCompareArray(final String siteId, final List<GSEmailVO> compareArray,
			final DynaBean item) throws BBBBusinessException, BBBSystemException {
		@SuppressWarnings("unused")
		final GSEmailVO compareVO = new GSEmailVO();
		String productId = null;
		String skuId = null;
		String reviewRating = null;
		String productAvailability = null;
		String categoryId = null;
		String skuImage = null;
		String productImage = null;
		Boolean isWebStockAvailable = false;
		final List<String> itemBeanProperties = (ArrayList<String>) getPropertyNames(item);
		if (itemBeanProperties.contains(BBBStoreRestConstants.PRODUCT_ID)
				&& null != item.get(BBBStoreRestConstants.PRODUCT_ID)) {
			productId = item.get(BBBStoreRestConstants.PRODUCT_ID)
				.toString();
		}
		if (itemBeanProperties.contains(BBBStoreRestConstants.SKU_ID)
				&& null != item.get(BBBStoreRestConstants.SKU_ID)) {
			skuId = item.get(BBBStoreRestConstants.SKU_ID).toString();
		}
		if (itemBeanProperties.contains(BBBStoreRestConstants.REVIEW_RATING)
				&& null != item.get(BBBStoreRestConstants.REVIEW_RATING)) {
			reviewRating = item.get(BBBStoreRestConstants.REVIEW_RATING).toString();
		}
		if (itemBeanProperties
				.contains(BBBStoreRestConstants.PRODUCT_AVAILABILITY_FLAG)
				&& null != item
						.get(BBBStoreRestConstants.PRODUCT_AVAILABILITY_FLAG)) {
			productAvailability = item.get(BBBStoreRestConstants.PRODUCT_AVAILABILITY_FLAG).toString();
		}
		
		if (itemBeanProperties.contains(BBBStoreRestConstants.CATEGORY_ID)
				&& null != item.get(BBBStoreRestConstants.CATEGORY_ID)) {
			categoryId = item.get(BBBStoreRestConstants.CATEGORY_ID).toString();
		}
		if (BBBUtility.isEmpty(productId)) {
			logDebug(BBBStoreRestConstants.PRODUCT_ID_INVALID_MSG);
			logError(BBBStoreRestConstants.PRODUCT_ID_INVALID_MSG);
			throw new BBBBusinessException(
					BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY,
					BBBStoreRestConstants.PRODUCT_ID_INVALID_MSG);
		}
		logDebug("Inside setCompareArray method : Product Id is: " + productId);
		logDebug("Inside setCompareArray method : Category Id is: " + categoryId);
		logDebug("Inside setCompareArray method : Product Availability is: " + productAvailability);
		logDebug("Inside setCompareArray method : Review Rating is: " + reviewRating);
		Boolean isActive = false;
		isActive = this.catalogGSTools.validateWebActiveProduct(productId, siteId);
		compareVO.setProductId(productId);
		final ProductGSVO productGSVo = this.getCatalogGSTools().getGSProductDetails(
				productId, categoryId);
		if (null != productGSVo.getProductImageMap()) {
			 productImage = productGSVo.getProductImageMap().get(BBBStoreRestConstants.GSP);
		}
		
		compareVO.setName(productGSVo.getProductRestVO().getProductVO()
				.getName());
		compareVO.setLongDescription(productGSVo.getProductRestVO()
				.getProductVO().getLongDescription());
		List siblingProducts = productGSVo.getSiblingProducts();
		// GS-157 defect. Sort Good To Knows in the order defined in BCC.
		final List<String> goodToKnow = productGSVo.getGoodToKnow();
		compareVO.setSiblingProducts(siblingProducts);
		compareVO.setGoodToKnow(goodToKnow);
		
		if (BBBUtility.isNotEmpty(skuId)) {
			isActive = this.catalogGSTools.validateWebActiveSku(skuId, siteId);
			final SkuGSVO skuGSVO = this.getCatalogGSTools().getGSSkuDetails(skuId);
			if (null != skuGSVO.getSkuImageMap()) {
				skuImage = skuGSVO.getSkuImageMap().get(BBBStoreRestConstants.GSP);
			}
			if (BBBUtility.isEmpty(skuImage)) {
				skuImage = skuGSVO.getSkuRestVO().getSkuVO().getSkuImages().getBasicImage();
			}
			if (null != skuGSVO.getSkuInventory()
					&& skuGSVO.getSkuInventory().get(
							BBBStoreRestConstants.SKU_WEB_STOCK) > 0) {
				isWebStockAvailable = true;
			}
			compareVO.setUpc(skuGSVO.getSkuRestVO().getSkuVO().getUpc());
			compareVO.setSkuId(skuId);
			if (BBBUtility.isNotEmpty(skuGSVO.getSkuRestVO().getSkuVO().getDisplayName())) {
				compareVO.setName(skuGSVO.getSkuRestVO().getSkuVO().getDisplayName());
			}
			setSkuPriceVO(skuGSVO, compareVO);
		} else {
			setProductPriceVO(productGSVo, compareVO);
		}
		
		if (BBBUtility.isNotEmpty(skuImage)) {
			compareVO.setThumbnailImage(skuImage);
			compareVO.setSmallImage(skuImage);
		} else if (BBBUtility.isNotEmpty(productImage)) {
			compareVO.setThumbnailImage(productImage);
			compareVO.setSmallImage(productImage);
		}
		if (BBBUtility.isNotEmpty(reviewRating)
				&& !reviewRating.equalsIgnoreCase(BBBCoreConstants.NULL_VALUE)
				&& Float.parseFloat(reviewRating) > 0) {
			compareVO
					.setReviewRating(BBBStoreRestConstants.BAZAAR_VOICE_START_STAR
							+ Float.parseFloat(reviewRating)
							+ BBBStoreRestConstants.BAZAAR_VOICE_END_STAR);
		}
		if (!BBBUtility.isEmpty(productAvailability)) {
			compareVO.setProductAvailabilityFlag(productAvailability);
		} else {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
		}
		compareVO.setIsActive(isActive.toString());
		compareVO.setWebStockAvailable(isWebStockAvailable.toString());
		compareArray.add(compareVO);
	}

	/**
	 * @param tableCheckListMapOuter
	 * @param primaryCategoryCountMap
	 * @param tableCheckListMapInner
	 * @param item
	 * @throws BBBBusinessException
	 */
	private void setTableCheckListMapOuter(
			final Map<String, Map<String, List<GSEmailVO>>> tableCheckListMapOuter,
			final Map<String, List<GSEmailVO>> tableCheckListMapInner, final DynaBean item)
			throws BBBBusinessException {
		JSONObject jsonObject1 = null;
		jsonObject1 = (JSONObject) JSONSerializer.toJSON(item);
		final DynaBean JSONResultbean1 = (DynaBean) JSONSerializer
				.toJava(jsonObject1);
		final List<String> dynaBeanProperties1 = (ArrayList<String>) getPropertyNames(JSONResultbean1);
		if (dynaBeanProperties1
				.contains(BBBStoreRestConstants.FEATURED_CATEGORIES)) {
			@SuppressWarnings("unchecked")
			final List<DynaBean> itemArray1 = (ArrayList<DynaBean>) JSONResultbean1
					.get(BBBStoreRestConstants.FEATURED_CATEGORIES);
			final List<GSEmailVO> tableCheckListArray = new ArrayList<GSEmailVO>();
			for (DynaBean item1 : itemArray1) {
				String featuredCategoryName = null;
				if (null != item1
						.get(BBBStoreRestConstants.FEATURED_CATEGORY_NAME)) {
					featuredCategoryName = item1.get(
							BBBStoreRestConstants.FEATURED_CATEGORY_NAME)
							.toString();
				}
				String isPresent = null;
				if (null != item1.get(BBBStoreRestConstants.IS_PRESENT)) {
					isPresent = item1.get(BBBStoreRestConstants.IS_PRESENT)
							.toString();
				}
				final GSEmailVO tableCheckListVO = new GSEmailVO();
				tableCheckListVO.setCategoryName(featuredCategoryName);
				tableCheckListVO.setIsPresent(isPresent);
				tableCheckListArray.add(tableCheckListVO);
			}
			tableCheckListMapInner.put(
					BBBStoreRestConstants.FEATURED_CATEGORIES,
					tableCheckListArray);
		} else {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
		}
		if (dynaBeanProperties1.contains(BBBStoreRestConstants.PLUS_CATEGORIES)) {
			@SuppressWarnings("unchecked")
			final List<DynaBean> itemArray1 = (ArrayList<DynaBean>) JSONResultbean1
					.get(BBBStoreRestConstants.PLUS_CATEGORIES);
			final List<GSEmailVO> tableCheckListArray = new ArrayList<GSEmailVO>();
			for (DynaBean item1 : itemArray1) {
				String plusCategoryName = null;
				if (null != item1.get(BBBStoreRestConstants.PLUS_CATEGORY_NAME)) {
					plusCategoryName = item1.get(
							BBBStoreRestConstants.PLUS_CATEGORY_NAME)
							.toString();
				}
				String isPresent = null;
				if (null != item1.get(BBBStoreRestConstants.IS_PRESENT)) {
					isPresent = item1.get(BBBStoreRestConstants.IS_PRESENT)
							.toString();
				}
				final GSEmailVO tableCheckListVO = new GSEmailVO();
				tableCheckListVO.setCategoryName(plusCategoryName);
				tableCheckListVO.setIsPresent(isPresent);
				tableCheckListArray.add(tableCheckListVO);
			}
			tableCheckListMapInner.put(BBBStoreRestConstants.PLUS_CATEGORIES,
					tableCheckListArray);
		} else {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_KEY_INVALID_MSG);
		}
		final String primaryCategoryName = item.get(
				BBBStoreRestConstants.PRIMARY_CATEGORY_NAME).toString();
		//String count = item.get(BBBStoreRestConstants.COUNT).toString();
		if (BBBUtility.isNotEmpty(primaryCategoryName)) {
			//primaryCategoryCountMap.put(primaryCategoryName, count);
			tableCheckListMapOuter.put(primaryCategoryName,
					tableCheckListMapInner);
		} else {
			logError(BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
			throw new BBBBusinessException(
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_CODE,
					BBBStoreRestConstants.JSON_RESULT_STRING_INVALID_MSG);
		}
	}

	/**
	 * @param recipientList
	 * @param siteId
	 * @return
	 */
	private Map<String, Serializable> collectParams(final String recipientList,
			final String siteId) {
		final Map<String, Serializable> emailParams = new HashMap<String, Serializable>();
		emailParams.put(this.getTemplateUrlName(), this.getTemplateUrl());
		emailParams.put(this.getRecipientNameParamName(), this.getRecipientName());
		emailParams.put(this.getRecipientEmailParamName(), recipientList);
		emailParams.put(this.getSubjectParamName(), this.getSubject());
		emailParams.put(this.getSiteIdParamName(), siteId);
		return emailParams;
	}

	/**
	 * @param pDynaBean
	 * @return
	 */
	private List<String> getPropertyNames(final DynaBean pDynaBean) {
		logDebug("GiftRegistryTools.getPropertyNames() method start");

		final DynaClass dynaClass = pDynaBean.getDynaClass();
		final DynaProperty properties[] = dynaClass.getDynaProperties();
		final List<String> propertyNames = new ArrayList<String>();
		for (int i = 0; i < properties.length; i++) {
			final String name = properties[i].getName();
			propertyNames.add(name);
		}
		logDebug("GiftRegistryTools.getPropertyNames() method ends");

		return propertyNames;
	}
	
	/**
	 * @param price
	 * @return
	 */
	private String getPrice(final String price) {
		return price
				.substring(
						price.indexOf(BBBStoreRestConstants.Dollar) + BBBStoreRestConstants.NUMERIC_ONE,
						price.indexOf(".") + BBBStoreRestConstants.NUMERIC_THREE);
	}
	
	/**
	 * @param storeId
	 * @param placeHolderValues
	 * @param siteId
	 * @param emailType
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * Method to set Common Placeholder values
	 */
	private void getPlaceHolderValues(final StoreVO storeVO,
			final Map<String, Object> placeHolderValues, final String siteId,
			final String emailType) throws BBBBusinessException, BBBSystemException {
		placeHolderValues.put(BBBStoreRestConstants.FRMDATA_SITEID,
				siteId);
		placeHolderValues.put(BBBStoreRestConstants.EMAIL_TYPE,
				emailType);
		placeHolderValues.put(BBBStoreRestConstants.FRMDATA_STORE_NAME,
					storeVO.getStoreName());
		placeHolderValues.put(BBBStoreRestConstants.FRMDATA_ADDRESS,
					storeVO.getAddress());
		placeHolderValues.put(BBBStoreRestConstants.FRMDATA_CITY,
					storeVO.getCity());
		placeHolderValues.put(BBBStoreRestConstants.FRMDATA_STATE,
					storeVO.getState());
		placeHolderValues.put(BBBStoreRestConstants.FRMDATA_POSTAL_CODE,
					storeVO.getPostalCode());
		placeHolderValues.put(BBBCoreConstants.STOFU_GS_EMAIL,
				BBBCoreConstants.STOFU_GS_EMAIL);
		
	}
	
	/**
	 * @param productGSVo
	 * @param productEmailVo
	 */
	private void setProductPriceVO(final ProductGSVO productGSVo, final GSEmailVO productEmailVo) {
		final String highPrice = productGSVo.getProductHighPrice();
		final String lowPrice = productGSVo.getProductLowPrice();
		final String highSalePrice = productGSVo.getProductHighSalePrice();
		final String lowSalePrice = productGSVo.getProductLowSalePrice();
		if (BBBUtility.isNotEmpty(highPrice)) {
			final String[] price = this.getPrice(highPrice).split(BBBStoreRestConstants.FULLSTOP);
			productEmailVo.setProductHighPrice(price[0]);
			productEmailVo.setProductHighDecimalPrice(price[1]);
		}
		if (BBBUtility.isNotEmpty(lowPrice)) {
			final String[] price = this.getPrice(lowPrice).split(BBBStoreRestConstants.FULLSTOP);
			productEmailVo.setProductLowPrice(price[0]);
			productEmailVo.setProductLowDecimalPrice(price[1]);
		}
		if (BBBUtility.isNotEmpty(highSalePrice)) {
			final String[] price = this.getPrice(highSalePrice).split(BBBStoreRestConstants.FULLSTOP);
			productEmailVo.setProductHighSalePrice(price[0]);
			productEmailVo.setProductHighDecimalSalePrice(price[1]);
		}
		if (BBBUtility.isNotEmpty(lowSalePrice)) {
			final String[] price = this.getPrice(lowSalePrice).split(BBBStoreRestConstants.FULLSTOP);
			productEmailVo.setProductLowSalePrice(price[0]);
			productEmailVo.setProductLowDecimalSalePrice(price[1]);
		}
	}
	
	/**
	 * @param productGSVo
	 * @param productEmailVo
	 * Method to set Sku Prices
	 */
	private void setSkuPriceVO(final SkuGSVO skuGSVO, final GSEmailVO productEmailVo) {
		
		Double lowPrice = new Double("0.00");
		lowPrice = skuGSVO.getSkuRestVO().getListPrice();
		Double lowSalePrice = new Double("0.00");
		lowSalePrice = skuGSVO.getSkuRestVO().getSalePrice();
		
		productEmailVo.setProductHighPrice("");
		productEmailVo.setProductHighDecimalPrice("");
		productEmailVo.setProductHighSalePrice("");
		productEmailVo.setProductHighDecimalSalePrice("");
		
		if (Double.valueOf(lowPrice.toString()) > 0 && BBBUtility.isNotEmpty(lowPrice.toString())) {
			final String[] price = this.getPrice(formatCurrency(lowPrice)).split(BBBStoreRestConstants.FULLSTOP);
			productEmailVo.setProductLowPrice(price[0]);
			productEmailVo.setProductLowDecimalPrice(price[1]);
		}
		if (Double.valueOf(lowSalePrice) > 0 && BBBUtility.isNotEmpty(lowSalePrice.toString())) {
			final String[] price = this.getPrice(formatCurrency(lowSalePrice)).split(BBBStoreRestConstants.FULLSTOP);
			productEmailVo.setProductLowSalePrice(price[0]);
			productEmailVo.setProductLowDecimalSalePrice(price[1]);
		}
	}
	
	/**
	 * @param listPrice
	 * @return
	 */
	private String formatCurrency(final Object listPrice) {
		final String locale = this.getDefaultLocale();
		final Locale localeObj = RequestLocale.getCachedLocale(locale);
		logDebug("LocaleObj : " + locale + " LocaleObj : " + localeObj);
		final NumberFormat formatter = NumberFormat.getCurrencyInstance(localeObj);
		return formatter.format(listPrice);
	}

}
