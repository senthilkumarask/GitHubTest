package com.bbb.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.CreditCardTypeVO;
import com.bbb.commerce.catalog.vo.GiftWrapVO;
import com.bbb.commerce.catalog.vo.LTLAssemblyFeeVO;
import com.bbb.commerce.catalog.vo.LTLDeliveryChargeVO;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.catalog.vo.SiteChatAttributesVO;
import com.bbb.commerce.catalog.vo.SiteVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.result.SortOptionVO;
import com.bbb.search.bean.result.SortOptionsVO;
import com.bbb.utils.BBBUtility;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * @author Velmurugan Moorthy
 * This class is created to refactor the code in BBBCatalogToolsImpl
 * Methods accessing site repository are placed in this class(from BBBCatalogToolsImpl)
 *  
 */
public class BBBSiteRepositoryTools extends BBBConfigToolsImpl {
	
	private MutableRepository siteRepository;
	private MutableRepository shippingRepository;
	private String bopusEligibleStateQuery;	
	private String brandIdQuery;
	private String brandNameQuery;
	private String sddShipMethodId;	
	private MutableRepository catalogRepository;
	private Repository managedCatalogRepository;
	
	/** if ((for the given site packNHoldEnabled) and (date is within start/end date)) then return true, else false
    *
    * @param siteId
    * @param date
    * @return is Pack & Hold Window
    * @throws BBBSystemException
    * @throws BBBBusinessException */

   
   public final boolean isPackNHoldWindow(final String siteId, final Date date)
                   throws BBBSystemException, BBBBusinessException {


       boolean isPackNHoldWindow = false;
       this.logDebug("Catalog API Method Name [isPackNHoldWindow] siteId [" + siteId + "] date [" + date + "]");

       if (BBBUtility.isEmpty(siteId) || (date == null)) {
           throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                           BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
       }

       try {
           BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isPackNHoldWindow");
           final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
                           BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
           if (siteConfiguration != null) {
               boolean isPackNHold = false;
               Date packNHoldStart = null;
               Date packNHoldEnd = null;
               if (siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME) != null) {
                   isPackNHold = ((Boolean) siteConfiguration
                                   .getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_SITE_PROPERTY_NAME))
                                   .booleanValue();
                   this.logTrace(isPackNHold + " isPackNHold value for siteId " + siteId);
               }
               if (siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME) != null) {
                   packNHoldStart = (Date) siteConfiguration
                                   .getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_START_DATE_SITE_PROPERTY_NAME);
                   this.logTrace(packNHoldStart + " packNHoldStart value for siteId " + siteId);
               }
               if (siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) != null) {
                   packNHoldEnd = (Date) siteConfiguration
                                   .getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME);
                   this.logTrace(packNHoldEnd + " packNHoldEnd value for siteId " + siteId);
               }
               if ((isPackNHold) && ((packNHoldStart != null) && date.after(packNHoldStart))
                               && ((packNHoldEnd != null) && date.before(packNHoldEnd))) {
                   this.logTrace(" pack N Hold window is true for siteId " + siteId);
                   isPackNHoldWindow = true;
               }
           } else {
           	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isPackNHoldWindow");
               throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                               BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
           }
       } catch (final RepositoryException e) {
           this.logError("Catalog API Method Name [isPackNHoldWindow]: RepositoryException ");
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isPackNHoldWindow");
           throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                           BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
       } finally {
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isPackNHoldWindow");
       }
       this.logTrace(isPackNHoldWindow + " pack N Hold window value for siteId " + siteId);
       this.logDebug("Catalog API Method Name [isPackNHoldWindow] siteId [" + siteId + "] date [" + date + "] Exit");
       return isPackNHoldWindow;
   }

   /** 
    * The API method reads the registry type applicable for the site and returns the corresponding RegistryTypeVO 
    * */
   public final List<RegistryTypeVO> getRegistryTypes(final String siteId)
                   throws BBBSystemException, BBBBusinessException {
       this.logDebug("Catalog API Method Name [getRegistryTypes] siteId[" + siteId + "]");
       try {
           BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypes");
           final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
                           BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
           final List<RegistryTypeVO> registryTypeVOList = new ArrayList<RegistryTypeVO>();
           if (siteConfiguration != null) {
               @SuppressWarnings ("unchecked")
               final Set<RepositoryItem> registryRepositoryItem = (Set<RepositoryItem>) siteConfiguration
                               .getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME);
               if ((registryRepositoryItem != null) && !registryRepositoryItem.isEmpty()) {
                   for (final RepositoryItem registryRepoItem : registryRepositoryItem) {
                       this.logTrace("add registry id [" + registryRepoItem.getRepositoryId()
                                       + "] to registry type list");
                       registryTypeVOList.add(new RegistryTypeVO(registryRepoItem));
                   }
               }
           } else {
           	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypes");
               throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_NOT_AVAILABLE_IN_REPOSITORY,
                               BBBCatalogErrorCodes.REGISTRY_NOT_AVAILABLE_IN_REPOSITORY);
           }
           return registryTypeVOList;
       } catch (final RepositoryException e) {
           this.logError("Catalog API Method Name [getRegistryTypes]: RepositoryException ");
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypes");
           throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                           BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
       } finally {
       	this.logDebug("Catalog API Method Name [getRegistryTypes] siteId[" + siteId + "] Exit");
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypes");
       }
   }

   /**
    *  If the default shipping method is not defined for a site then the method returns BBBBusinessException with an
    * error code indicating that there is no default shipping method configured for the site The Shipping Method
    * details are retrieved from the Repository Item Cache and returned in the method response.
    *  
    * */
    public final ShipMethodVO getDefaultShippingMethod(final String siteId)
                   throws BBBBusinessException, BBBSystemException {
 
           this.logDebug("Catalog API Method Name [getDefaultShippingMethod] siteId " + siteId);
 
       try {
           BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultShippingMethod");
           final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
                           BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

           if (siteConfiguration != null) {
               final RepositoryItem shippingMethodRepositoryItem = (RepositoryItem) siteConfiguration
                               .getPropertyValue(BBBCatalogConstants.DEFAULT_SHIP_METHOD_PROPERTY_NAME);

               if (shippingMethodRepositoryItem == null) {
                   this.logTrace("Default shipping method is not present for the site");
                   BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultShippingMethod");
                   throw new BBBBusinessException(
                                   BBBCatalogErrorCodes.DEFAULT_SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY,
                                   BBBCatalogErrorCodes.DEFAULT_SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY);
               }
               this.logTrace("Default shipping method id value [" + shippingMethodRepositoryItem.getRepositoryId()
                               + "]");
               return new ShipMethodVO(shippingMethodRepositoryItem);
           }
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultShippingMethod");
           throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                           BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
       } catch (final RepositoryException e) {
           this.logError("Catalog API Method Name [getDefaultShippingMethod]: RepositoryException ");
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultShippingMethod");
           throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                           BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
       } finally {
       	this.logDebug("Catalog API Method Name [getDefaultShippingMethod] siteId " + siteId + " Exit");
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultShippingMethod");
       }
   }

    /** If the stateid does not exist in the state table then method returns BBBBusinessException with an error code
     * indicating that the state does not exist The state details are read from the Site Repository Item Cache. */

    public boolean isNexusState(final String siteId, final String stateId)
                    throws BBBBusinessException, BBBSystemException {

            final StringBuffer debug = new StringBuffer(30);
            debug.append("Catalog API Method Name [isNexusState] siteId[").append(siteId).append("] stateId [")
                            .append(stateId);
            this.logDebug(debug.toString());

        RepositoryItem siteConfiguration = null;
        if ((stateId != null) && (siteId != null) && !StringUtils.isEmpty(siteId) && !StringUtils.isEmpty(stateId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " isNexusState");
                siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
                if (siteConfiguration == null) {
                    this.logTrace("Site repository item is null!");
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isNexusState");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
                }

                @SuppressWarnings ("unchecked")
                final Set<RepositoryItem> nexusStateSet = (Set<RepositoryItem>) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.NEXUS_STATE_SITE_PROPERTY_NAME);
                	if (!BBBUtility.isCollectionEmpty(nexusStateSet)) {
                    for (final RepositoryItem nexusStateRepositoryItem : nexusStateSet) {
                        final String nexusStateId = nexusStateRepositoryItem.getRepositoryId();
                        this.logTrace("nexusStateId id value [" + nexusStateId + "] Input stateId value " + stateId);
                        if (nexusStateId.equalsIgnoreCase(stateId)) {
                            return true;
                        }
                    }
                }
                return false;
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [isNexusState]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isNexusState");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " isNexusState");
            }
        }

        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }


    /** If there are no credit card types configured for a site the method throws a BBBBusinessException with an error
     * code indicating that no credit card types configured If the credit card types are configured, the attribute is
     * read from the Site Repository Item Cache and returned in the method response.
     *
     * @param siteId
     * @return Credit Card Types
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    @SuppressWarnings ("unchecked")
    public final List<CreditCardTypeVO> getCreditCardTypes(final String siteId)
                    throws BBBBusinessException, BBBSystemException {
            this.logDebug("Catalog API Method Name [getCreditCardTypes] siteId " + siteId);
        RepositoryItem siteConfiguration = null;
        final List<CreditCardTypeVO> creditCardVOList = new ArrayList<CreditCardTypeVO>();
        List<RepositoryItem> paymentCardRepositoryItemList = null;
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCreditCardTypes");
            siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            if (siteConfiguration != null) {
                paymentCardRepositoryItemList = (List<RepositoryItem>) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.PAYMENT_CARDS_PROPERTY_NAME);
                if ((paymentCardRepositoryItemList != null) && !paymentCardRepositoryItemList.isEmpty()) {
                    for (final RepositoryItem paymentCardRepoItem : paymentCardRepositoryItemList) {
                        this.logTrace("Payment card id value [" + paymentCardRepoItem.getRepositoryId() + "] ");
                        creditCardVOList.add(new CreditCardTypeVO(paymentCardRepoItem));
                    }
                } else {
                	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCreditCardTypes");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.CREDIT_CARD_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.CREDIT_CARD_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } else {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCreditCardTypes");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getCreditCardTypes]: RepositoryException ");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCreditCardTypes");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [getCreditCardTypes] siteId " + siteId + " Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCreditCardTypes");
        }
        return creditCardVOList;

    }

    /** If there are no states configured for a particular site the method throws a BBBBusinessException with an error
     * code indicating that states are not configured The states list returned will include the AFO/FPO states If the
     * states property of the SiteRepository is not null, the list is read from the Site Repository Item Cache
     *
     * @param siteId
     * @showMilitaryStates
     * @throws BBBSystemException */
   public final List<StateVO> getStates(final String siteId, final boolean showMilitaryStates,  String noShowPage)
                    throws BBBBusinessException, BBBSystemException {
	   this.logDebug("Catalog API Method Name [getStates] siteid " + siteId);
       RepositoryItem siteConfiguration = null;
       final List<StateVO> stateVOList = new ArrayList<StateVO>();
       try {
           BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getStates");
           siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
           if (siteConfiguration == null) {
               this.logTrace("Site repository item is null in getStates!");
               BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStates");
               throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                               BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
           }
           @SuppressWarnings ("unchecked")
           final Set<RepositoryItem> stateRepItems = (Set<RepositoryItem>) siteConfiguration
                           .getPropertyValue(BBBCatalogConstants.STATES_PROPERTY_NAME);
            if (!BBBUtility.isCollectionEmpty(stateRepItems)) {
               for (final RepositoryItem stateRepoitem : stateRepItems) {
               	
               	if(!StringUtils.isEmpty(noShowPage) && noShowPage.equalsIgnoreCase(BBBCatalogConstants.NO_SHOW_REGISTRY) && stateRepoitem.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES)!=null && ((Boolean)stateRepoitem.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES)==false)){
                   	continue;
                   }else if(!StringUtils.isEmpty(noShowPage) && noShowPage.equalsIgnoreCase(BBBCatalogConstants.NO_SHOW_SHIPPING) && stateRepoitem.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES)!=null && ((Boolean)stateRepoitem.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES)==false)){
                   	continue;
                   }else if(!StringUtils.isEmpty(noShowPage) && noShowPage.equalsIgnoreCase(BBBCatalogConstants.NO_SHOW_BILLING) && stateRepoitem.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES)!=null && ((Boolean)stateRepoitem.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES)==false)){
                   	continue;
                   }
                   // In case need to pass the military states too, then only get those specific states and add to
                   // List.
                   if (showMilitaryStates) {
                   	if (!((BBBCoreConstants.SITEBAB_CA_TBS.equalsIgnoreCase(SiteContextManager.getCurrentSiteId()) || BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(SiteContextManager.getCurrentSiteId())) && BBBCoreConstants.STATE_CODE_QC.equalsIgnoreCase(stateRepoitem.getRepositoryId()))) {
                       stateVOList.add(this.getStateVO(siteId, stateRepoitem));
                   	}
                   } else {
                       // Else Only add Non Military States by Default.
                       boolean isMilitaryState = false;
                       if (stateRepoitem.getPropertyValue(BBBCatalogConstants.IS_MILITARY_STATE) != null) {
                           isMilitaryState = ((Boolean) stateRepoitem
                                           .getPropertyValue(BBBCatalogConstants.IS_MILITARY_STATE)).booleanValue();
                       }
                       if (!isMilitaryState) {
                       	if (!((BBBCoreConstants.SITEBAB_CA_TBS.equalsIgnoreCase(SiteContextManager.getCurrentSiteId()) || BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(SiteContextManager.getCurrentSiteId())) && BBBCoreConstants.STATE_CODE_QC.equalsIgnoreCase(stateRepoitem.getRepositoryId()))) {
                           stateVOList.add(this.getStateVO(siteId, stateRepoitem));
                       	}
                       }
                   }
                   
               }
           } else {
           	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStates");
               throw new BBBBusinessException(BBBCatalogErrorCodes.STATE_NOT_AVAILABLE_IN_REPOSITORY,
                               BBBCatalogErrorCodes.STATE_NOT_AVAILABLE_IN_REPOSITORY);
           }
       } catch (final RepositoryException e) {
           this.logError("Catalog API Method Name [getStates]: RepositoryException ");
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStates");
           throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                           BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
       } finally {
       	this.logDebug("Catalog API Method Name [getStates] siteid " + siteId + " Exit");
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStates");
       }
       Collections.sort(stateVOList);

       return stateVOList;
    }

	private StateVO getStateVO(final String pSiteId,
			final RepositoryItem statesRepositoryItem)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("Catalog API Method Name [getStateVO]Parameter categoryRepositoryItem["
				+ statesRepositoryItem + "] Parameter pSiteId[" + pSiteId + "]");
		if (statesRepositoryItem != null) {
			final boolean isNexusState = this.isNexusState(pSiteId,
					statesRepositoryItem.getRepositoryId());
			boolean isMilitaryState = false;
			boolean isShowOnReg = true;
			boolean isShowOnShipping = true;
			boolean isShowOnBilling = true;
			if (statesRepositoryItem
					.getPropertyValue(BBBCatalogConstants.IS_MILITARY_STATE) != null) {
				isMilitaryState = ((Boolean) statesRepositoryItem
						.getPropertyValue(BBBCatalogConstants.IS_MILITARY_STATE))
						.booleanValue();
			}
			if (statesRepositoryItem
					.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES) != null) {
				isShowOnReg = ((Boolean) statesRepositoryItem
						.getPropertyValue(BBBCatalogConstants.SHOW_ON_REGISTRY_PAGES))
						.booleanValue();
			}
			if (statesRepositoryItem
					.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES) != null) {
				isShowOnShipping = ((Boolean) statesRepositoryItem
						.getPropertyValue(BBBCatalogConstants.SHOW_ON_SHIPPING_PAGES))
						.booleanValue();
			}
			if (statesRepositoryItem
					.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES) != null) {
				isShowOnBilling = ((Boolean) statesRepositoryItem
						.getPropertyValue(BBBCatalogConstants.SHOW_ON_BILLING_PAGES))
						.booleanValue();
			}
			final String stateName = statesRepositoryItem
					.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME) != null ? (String) statesRepositoryItem
					.getPropertyValue(BBBCatalogConstants.DESCRIPTION_STATE_PROPERTY_NAME)
					: "";
			return new StateVO(statesRepositoryItem.getRepositoryId(),
					stateName, isNexusState, isMilitaryState, isShowOnReg,
					isShowOnShipping, isShowOnBilling);
		}
		return null;
	}
    
	
	/** The API method returns the details of gift wrap sku corresponding to a site if the site id passed is not present
     * in the repository a BBBBusinessException is thrown indicating the error code
     *
     * @param siteId
     * @return Gift Wrap VO
     * @throws BBBBusinessException
     * @throws BBBSystemException
     * */
	
    public final GiftWrapVO getWrapSkuDetails(final String siteId) throws BBBBusinessException, BBBSystemException {

            this.logDebug("Catalog API Method Name [getWrapSku] siteId " + siteId);
    
        RepositoryItem siteConfiguration = null;
        GiftWrapVO giftWrapVO = null;
        double wrapPrice = 0.0;
        RepositoryItem giftWrapSkuItem = null;
        RepositoryItem giftWrapProductItem = null;
        String giftWrapSku = "";
        String giftWrapProduct = "";
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getWrapSku");
            siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            if (siteConfiguration == null) {
                this.logTrace("Site repository item is null in getWrapSkuDetails! " + siteId);
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRICE_SITE_PROPERTY_NAME) != null) {
                wrapPrice = ((Double) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRICE_SITE_PROPERTY_NAME))
                                .doubleValue();
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_SKU_SITE_PROPERTY_NAME) != null) {
                giftWrapSkuItem = (RepositoryItem) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.GIFT_WRAP_SKU_SITE_PROPERTY_NAME);
                giftWrapSku = giftWrapSkuItem.getRepositoryId();
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME) != null) {
                giftWrapProductItem = (RepositoryItem) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.GIFT_WRAP_PRODUCT_SITE_PROPERTY_NAME);
                giftWrapProduct = giftWrapProductItem.getRepositoryId();
            }
            giftWrapVO = new GiftWrapVO(giftWrapSku, giftWrapProduct, wrapPrice);
            this.logTrace(giftWrapVO.toString());
            return giftWrapVO;
        } catch (final RepositoryException e) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getWrapSku");
            this.logError("Catalog API Method Name [getWrapSku]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [getWrapSku] siteId " + siteId + " Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getWrapSku");
        }

    }

    /** The API method returns the map of Greetings corresponding to a site if the site id passed is not present in the
     * repository a BBBBusinessException is thrown indicating the error code
     *
     * @param siteId
     * @return Common Greetings
     * @throws BBBBusinessException
     * @throws BBBSystemException */
    @SuppressWarnings ("unchecked")
    public final Map<String, String> getCommonGreetings(final String siteId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getCommonGreetings] siteId " + siteId);
        RepositoryItem siteConfiguration = null;
        Map<String, String> commonGreetingsMap = new HashMap<String, String>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCommonGreetings");
            siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            if (siteConfiguration == null) {
                this.logTrace("Site repository item is null in getCommonGreetings! site id used is :" + siteId);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCommonGreetings");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            }

            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.COMMON_GREETINGS_SITE_PROPERTY_NAME) != null) {
                commonGreetingsMap = (Map<String, String>) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.COMMON_GREETINGS_SITE_PROPERTY_NAME);
            }
        } catch (final RepositoryException e) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCommonGreetings");
            this.logError("Catalog API Method Name [getCommonGreetings]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	this.logDebug("Catalog API Method Name [getCommonGreetings] siteId " + siteId + " Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCommonGreetings");
        }
        return commonGreetingsMap;
    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getLTLAssemblyFeeSkuDetails(java.lang.String)
     */
    public final LTLAssemblyFeeVO getLTLAssemblyFeeSkuDetails(final String siteId) throws BBBBusinessException, BBBSystemException {

            this.logDebug("Catalog API Method Name [getLTLAssemblyFeeSkuDetails] siteId " + siteId);
      
        RepositoryItem siteConfiguration = null;
        LTLAssemblyFeeVO ltlAssemblyFeeVO = null;
        double ltlAssemblySkuPrice = 0.0;
        String ltlAssemblySku = "";
        String ltlAssemblyProduct = "";
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLAssemblyFeeSkuDetails");
            siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            if (siteConfiguration == null) {
                this.logTrace("Site repository item is null in getLTLAssemblyFeeSkuDetails! " + siteId);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLAssemblyFeeSkuDetails");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
           }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_PRICE_SITE_PROPERTY_NAME) != null) {
              ltlAssemblySkuPrice = ((Double) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_PRICE_SITE_PROPERTY_NAME))
                                .doubleValue();
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_SITE_PROPERTY_NAME) != null) {
            	ltlAssemblySku = (String) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_SKU_SITE_PROPERTY_NAME);
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_PRODUCT_SITE_PROPERTY_NAME) != null) {
            	ltlAssemblyProduct = (String) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.LTL_ASSEMBLY_PRODUCT_SITE_PROPERTY_NAME);
            }
            ltlAssemblyFeeVO = new LTLAssemblyFeeVO(ltlAssemblySku, ltlAssemblyProduct, ltlAssemblySkuPrice);
            this.logTrace(ltlAssemblyFeeVO.toString());
            return ltlAssemblyFeeVO;
        } catch (final RepositoryException re) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLAssemblyFeeSkuDetails");
            this.logError("Catalog API Method Name [getLTLAssemblyFeeSkuDetails]: RepositoryException" + re.getMessage());
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, re);
        } finally {
        	this.logDebug("Catalog API Method Name [getLTLAssemblyFeeSkuDetails] siteId " + siteId + " Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLAssemblyFeeSkuDetails");
        }

    }

    /* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getLTLDeliveryChargeSkuDetails(java.lang.String)
     */
    public final LTLDeliveryChargeVO getLTLDeliveryChargeSkuDetails(final String siteId) throws BBBBusinessException, BBBSystemException {
            this.logDebug("Catalog API Method Name [getLTLDeliveryChargeSkuDetails] siteId " + siteId);
        RepositoryItem siteConfiguration = null;
        LTLDeliveryChargeVO ltlDeliveryChargeVO = null;
        double ltlDeliveryChargeSkuPrice = 0.0;
        String ltlDeliveryChargeSku = "";
        String ltlDeliveryChargeProduct = "";
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLDeliveryChargeSkuDetails");
            siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            if (siteConfiguration == null) {
                this.logError("Site repository item is null in getLTLDeliveryChargeSkuDetails " + siteId);
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLDeliveryChargeSkuDetails");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_PRICE_SITE_PROPERTY_NAME) != null) {
              ltlDeliveryChargeSkuPrice = ((Double) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_PRICE_SITE_PROPERTY_NAME))
                                .doubleValue();
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_SITE_PROPERTY_NAME) != null) {
            	ltlDeliveryChargeSku = (String) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_SKU_SITE_PROPERTY_NAME);
            }
            if (siteConfiguration.getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_PRODUCT_SITE_PROPERTY_NAME) != null) {
            	ltlDeliveryChargeProduct = (String) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.LTL_DELIVERY_CHARGE_PRODUCT_SITE_PROPERTY_NAME);
            }
            ltlDeliveryChargeVO = new LTLDeliveryChargeVO(ltlDeliveryChargeSku, ltlDeliveryChargeProduct, ltlDeliveryChargeSkuPrice);
     
            	this.logTrace(ltlDeliveryChargeVO.toString());
          
            return ltlDeliveryChargeVO;
        } catch (final RepositoryException re) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLDeliveryChargeSkuDetails");
            this.logError("Catalog API Method Name [getLTLDeliveryChargeSkuDetails]: RepositoryException " + re.getMessage());
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, re);
        } finally {
        	this.logDebug("Catalog API Method Name [getLTLDeliveryChargeSkuDetails] siteId " + siteId + " Exit");
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getLTLDeliveryChargeSkuDetails");
        }

    }
    
    
    /** If the SKU does not exist in the system then the method will throw a BBBBusinessException with an error code
     * indicating that SKU does not exist If the SKU exist and no shipping methods are configured for the SKU, the
     * method will return all the shipping methods applicable for a site If the SKU contains the shipping methods and
     * the site in which the SKU belongs contains less no of applicable ship methods then the SKU shipping methods are
     * filtered matching against the Site Shipping Methods. Site shipping methods takes precedence over SKU's eligible
     * shipping methods The Shipping Method details are retrieved from the Repository Item Cache and returned as part of
     * the response
     *
     * @throws BBBSystemException */
    public  List<ShipMethodVO> getShippingMethodsForSku(final String siteId, final String skuId, boolean sameDayDeliveryFlag)
                    throws BBBBusinessException, BBBSystemException {
        final StringBuffer logDebug = new StringBuffer(50);
            logDebug.append("Catalog API Method Name [getShippingMethodsForSku] Parameter siteId[").append(siteId)
                            .append("] Parameter skuId[").append(skuId);
            this.logDebug(logDebug.toString());

        final List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingMethodsForSku");
            //final RepositoryItem skuRepositoryItem = null;
            //TODO To add the following code after the confirmation - whether to include catalogRepo or not - Vel
            final RepositoryItem skuRepositoryItem = this.getCatalogRepository().getItem(skuId,BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
            if (skuRepositoryItem == null) {
                throw new BBBBusinessException(BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SKU_NOT_AVAILABLE_IN_REPOSITORY);
            }
            
            Boolean isLtlSku = false;
            if(null != skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU))
            {
            	isLtlSku = (Boolean)skuRepositoryItem.getPropertyValue(BBBCatalogConstants.IS_LTL_SKU);
            }
            final List<String> shipIdList = new ArrayList<String>(); 
            final List<ShipMethodVO> shipMethodVOListForSite = new ArrayList<ShipMethodVO>();
            final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
                            BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
            
            if (siteConfiguration != null) {
                @SuppressWarnings ("unchecked")
                final Set<RepositoryItem> applicableShipMethodSet = (Set<RepositoryItem>) siteConfiguration
                                .getPropertyValue(BBBCatalogConstants.APPLICABLE_SHIPMETHODS_SITE_PROPERTY_NAME);
                this.logTrace("Set of applicable Shipping methods [" + applicableShipMethodSet + "] ");
                if ((applicableShipMethodSet != null) && !applicableShipMethodSet.isEmpty()) {
                    for (final RepositoryItem applicableShipMethod : applicableShipMethodSet) {
                    	Boolean isLTLShippingMethod = false;
                    	if(null != applicableShipMethod.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD)){
                    		isLTLShippingMethod = (Boolean)applicableShipMethod.getPropertyValue(BBBCatalogConstants.IS_LTL_SHIPPING_METHOD);
                    	}
                    	 
                    	/* if(null != isLtlSku && null != isLTLShippingMethod && isLtlSku && isLTLShippingMethod)
                    	{
	                        shipIdList.add(applicableShipMethod.getRepositoryId());
	                        ShipMethodVO shipMethodVO=new ShipMethodVO(applicableShipMethod);
	                        shipMethodVO.setLtlShipMethod(isLTLShippingMethod);
	                        shipMethodVOListForSite.add(shipMethodVO);
                    	}
                    	else*/ 
                    	/*
                    	 * The following condition is commented while refactoring as this condition will not be covered
                    	 * isLtlSku & isLTLShippingMethod are initialized locally & will never be null
                    	 *if(null != isLtlSku && null != isLTLShippingMethod && !isLtlSku && !isLTLShippingMethod) 
                    	 */
                    	
                    		if(!isLtlSku && !isLTLShippingMethod)	
                    	{
	                        shipIdList.add(applicableShipMethod.getRepositoryId());
	                        if(applicableShipMethod.getRepositoryId().equals(getSddShipMethodId())){
	                        	// BBBH-5255 - SPC off case defect fixed
	                        	if(sameDayDeliveryFlag){
	                        		 shipMethodVOListForSite.add(new ShipMethodVO(applicableShipMethod));
	            				}
	                        } else{
	                        	shipMethodVOListForSite.add(new ShipMethodVO(applicableShipMethod));
	                        }
	                        
                    	}
                    }
                } else {
                	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingMethodsForSku");
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SHIPPING_METHOD_NOT_CONFIGURED_FOR_SITE_IN_REPOSITORY);
                }
            } else {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingMethodsForSku");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            }
            @SuppressWarnings ("unchecked")
            final Set<RepositoryItem> eligibleShipMethodSet = (Set<RepositoryItem>) skuRepositoryItem
                            .getPropertyValue(BBBCatalogConstants.ELIGIBLE_SHIP_METHODS_PROPERTY_NAME);

            if (!BBBUtility.isCollectionEmpty(eligibleShipMethodSet)) {
                this.logTrace("Retrieving Shipping methods for SKU id [" + skuId + "] ");
                this.logTrace("Set of applicable Shipping methods [" + eligibleShipMethodSet + "] ");
                for (final RepositoryItem eligibleShipMethods : eligibleShipMethodSet) {
                    if (shipIdList.contains(eligibleShipMethods.getRepositoryId()) && !isLtlSku) {
                    	ShipMethodVO shipMethodVO=new ShipMethodVO(eligibleShipMethods);
	                    shipMethodVO.setEligibleShipMethod(true);
                        shipMethodVOList.add(shipMethodVO);
                    }
                    else if(isLtlSku) {
                    	ShipMethodVO shipMethodVO=new ShipMethodVO(eligibleShipMethods);
	                    shipMethodVO.setEligibleShipMethod(true);
                        shipMethodVOList.add(shipMethodVO);
                    }
                }
                // BBBH-2379 - Shipping page changes (MPC)
                if(!isLtlSku){
                
					if(sameDayDeliveryFlag && !(null!=siteId && (siteId.equals(TBSConstants.SITE_TBS_BAB_US) || siteId.equals(TBSConstants.SITE_TBS_BAB_CA) || siteId.equals(TBSConstants.SITE_TBS_BBB)))){
					updateListWithSddShipMethod(shipMethodVOList);
				}
                }
                return shipMethodVOList;
            }

            this.logTrace("No Shipping Methods for SKU id [" + skuId + "] Retrieving Shipping methods from Site :"
                            + shipMethodVOListForSite);
            return shipMethodVOListForSite;
        } catch (final RepositoryException e) {
        	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingMethodsForSku");
            this.logError("Catalog API Method Name [getShippingMethodsForSku]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        } finally {
        	logDebug.append(" Exit");
        	this.logDebug(logDebug.toString());
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getShippingMethodsForSku");
        }
    }

    /** This method returns the default country for a given site id If the default country is not present it returns
     * null.
     *
     * @param siteId
     * @return Default Country for Site
     * @throws BBBBusinessException
     * @throws BBBSystemException 
     * 
     * */
     public final String getDefaultCountryForSite(final String siteId) throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getDefaultCountryForSite] siteId " + siteId);
        if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultCountryForSite");
                final RepositoryItem siteItem = this.getSiteRepository().getItem(siteId,
                                BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
                this.logTrace("site Repository Item value [" + siteItem + "]");

                if (siteItem != null) {
                    if (siteItem.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) != null) {
                        this.logTrace("default country value ["
                                        + siteItem.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME)
                                        + "]");
                        return (String) siteItem
                                        .getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME);
                    }

                    this.logTrace("Default country is null");
                    return null;
                }
                this.logTrace("Input site is not valid");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultCountryForSite");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getDefaultCountryForSite]: RepositoryException ");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultCountryForSite");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
            	this.logDebug("Catalog API Method Name [getDefaultCountryForSite] siteId " + siteId + " Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getDefaultCountryForSite");
            }
        }
        this.logDebug("Input parameter siteId is null");
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

     public SiteChatAttributesVO getSiteChatAttributes(final String siteId) throws BBBSystemException {
         RepositoryItem siteConfiguration = null;
         final SiteChatAttributesVO siteChatAttributesVO = new SiteChatAttributesVO();
         this.logDebug("Start of method getSiteChatAttributes");

         boolean siteChatOnOffFlag = false;
         boolean pdpChatOnOffFlag = false;
         boolean pdpChatOverrideFlag = false;
         boolean pdpDaasOnOffFlag = false;
         boolean pdpDaasOverrideFlag = false;

         try {
             siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

             if (siteConfiguration == null) {
                 throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1007,
                                 "no rows fetched for given site id");
             }
             this.logDebug("siteConfiguration object---" + siteConfiguration);

             if (siteConfiguration.getPropertyValue(BBBCatalogConstants.CHAT_GLOBAL_PROPERTY_NAME) != null) {
                 siteChatOnOffFlag = ((Boolean) siteConfiguration
                                 .getPropertyValue(BBBCatalogConstants.CHAT_GLOBAL_PROPERTY_NAME)).booleanValue();
             }
             if (siteConfiguration.getPropertyValue(BBBCatalogConstants.CHAT_PDP_PROPERTY_NAME) != null) {
                 pdpChatOnOffFlag = ((Boolean) siteConfiguration
                                 .getPropertyValue(BBBCatalogConstants.CHAT_PDP_PROPERTY_NAME)).booleanValue();
             }
             if (siteConfiguration.getPropertyValue(BBBCatalogConstants.CHAT_PDP_OVERRIDE_PROPERTY_NAME) != null) {
                 pdpChatOverrideFlag = ((Boolean) siteConfiguration
                                 .getPropertyValue(BBBCatalogConstants.CHAT_PDP_OVERRIDE_PROPERTY_NAME)).booleanValue();
             }
             if (siteConfiguration.getPropertyValue(BBBCatalogConstants.DAAS_PDP_PROPERTY_NAME) != null) {
                 pdpDaasOnOffFlag = ((Boolean) siteConfiguration
                                 .getPropertyValue(BBBCatalogConstants.DAAS_PDP_PROPERTY_NAME)).booleanValue();
             }
             if (siteConfiguration.getPropertyValue(BBBCatalogConstants.DAAS_PDP_OVERRIDE_PROPERTY_NAME) != null) {
                 pdpDaasOverrideFlag = ((Boolean) siteConfiguration
                                 .getPropertyValue(BBBCatalogConstants.DAAS_PDP_OVERRIDE_PROPERTY_NAME)).booleanValue();
             }

             siteChatAttributesVO.setChatURL((String) siteConfiguration
                             .getPropertyValue(BBBCatalogConstants.CHAT_URL_PROPERTY_NAME));
             siteChatAttributesVO.setOnOffFlag(siteChatOnOffFlag);
             siteChatAttributesVO.setWeekDayOpenTime((Date) siteConfiguration
                             .getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_OPEN_TIME_PROPERTY_NAME));
             siteChatAttributesVO.setWeekDayCloseTime((Date) siteConfiguration
                             .getPropertyValue(BBBCatalogConstants.CHAT_WEEKDAY_CLOSE_TIME_PROPERTY_NAME));
             siteChatAttributesVO.setWeekEndOpenTime((Date) siteConfiguration
                             .getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_OPEN_TIME_PROPERTY_NAME));
             siteChatAttributesVO.setWeekEndCloseTime((Date) siteConfiguration
                             .getPropertyValue(BBBCatalogConstants.CHAT_WEEKEND_CLOSE_TIME_PROPERTY_NAME));
             siteChatAttributesVO.setChatFlagPDP(pdpChatOnOffFlag);
             siteChatAttributesVO.setChatOverrideFlagPDP(pdpChatOverrideFlag);
             siteChatAttributesVO.setDaasFlagPDP(pdpDaasOnOffFlag);
             siteChatAttributesVO.setDaasOverrideFlagPDP(pdpDaasOverrideFlag);
         } catch (final RepositoryException e) {
             this.logError("Catalog API Method Name [getSiteChatAttributes]: RepositoryException ");
             throw new BBBSystemException(BBBCoreErrorConstants.CATALOG_ERROR_1008,
                             "Repository Exception while getting chat attributes", e);
         }
         this.logDebug("End of method getSiteChatAttributes");
         return siteChatAttributesVO;
     }

 	// Added for 117-A5 Story
 	public String getGridListAttributes(String siteId)	throws BBBSystemException {
 		RepositoryItem siteConfiguration = null;
 		String siteGridListDefaultValue = null;
 		try {
 			siteConfiguration = this.getSiteRepository().getItem(siteId,
 					BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
 			if (siteConfiguration == null) {
 				throw new BBBSystemException(
 						BBBCoreErrorConstants.CATALOG_ERROR_1007,
 						"no rows fetched for given site id");
 			}
 			if (siteConfiguration
 					.getPropertyValue(BBBCatalogConstants.GRID_LIST_PROPERTY_NAME) != null) {
 				siteGridListDefaultValue = (String) (siteConfiguration
 						.getPropertyValue(BBBCatalogConstants.GRID_LIST_PROPERTY_NAME));

 			}

 		} catch (RepositoryException e) {
 			// TODO Auto-generated catch block
 			//e.printStackTrace();
 			logError(e.getMessage(),e);
 		}

 		// TODO Auto-generated method stub
 		return siteGridListDefaultValue;
 	}

 	/** Returns a list of state Codes that are bopus eligible. If input parameter is null then BBBBusinessException is
     * thrown */
    @SuppressWarnings ("unchecked")
    public final List<String> getBopusEligibleStates(final String siteId)
                    throws BBBBusinessException, BBBSystemException {
        this.logDebug("Catalog API Method Name [getBopusEligibleStates] siteid " + siteId);
        if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getBopusEligibleStates");

                List<RepositoryItem> allStateRepoItemsList = null;
                List<RepositoryItem> allBopusExcludedStateItemsList = null;
                Set<RepositoryItem> allBopusExcludedStates = null;
                List<String> allBopusStateItemIdList = null;

                final RepositoryItem[] allStateRepoItems = this.executeRQLQuery(this.getBopusEligibleStateQuery(),
                                BBBCatalogConstants.STATE_ITEM_DESCRIPTOR, this.getShippingRepository());

                final RepositoryItem siteRepositoryItem = this.getSiteRepository().getItem(siteId,
                                BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

                if (siteRepositoryItem != null) {
                    allBopusExcludedStates = (Set<RepositoryItem>) siteRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.BOPUS_EXCLUDED_STATE_SITE_PROPERTY_NAME);
                }

                this.logDebug("allStateRepoItems value " + Arrays.toString(allStateRepoItems));
                this.logDebug("allBopusExcludedStateRepoItems value " + allBopusExcludedStates);

                if (allStateRepoItems != null) {
                    allStateRepoItemsList = new ArrayList<RepositoryItem>();
                    for (final RepositoryItem allStateRepoItem : allStateRepoItems) {
                        this.logDebug("State code of  state " + allStateRepoItem.getRepositoryId());
                        allStateRepoItemsList.add(allStateRepoItem);
                    }
                } else {
                    throw new BBBBusinessException(BBBCatalogErrorCodes.STATE_NOT_AVAILABLE_IN_REPOSITORY,
                                    "No state data found in the repository");
                }
                if (!BBBUtility.isCollectionEmpty(allBopusExcludedStates)) {
                    allBopusExcludedStateItemsList = new ArrayList<RepositoryItem>(allBopusExcludedStates);
                    allStateRepoItemsList.removeAll(allBopusExcludedStateItemsList);
                }
                if (allStateRepoItemsList.isEmpty()) {
                    throw new BBBBusinessException(
                                    BBBCatalogErrorCodes.NO_BOPUS_ELIGIBLE_STATE_AVAILABLE_IN_REPOSITORY,
                                    "No bopus eligible state found in the repository");
                }
                allBopusStateItemIdList = new ArrayList<String>();
                for (final RepositoryItem bopusStateRepoItem : allStateRepoItemsList) {
                    this.logDebug("State code of bopus excluded state " + bopusStateRepoItem.getRepositoryId());
                    allBopusStateItemIdList.add(bopusStateRepoItem.getRepositoryId());
                }
                return allBopusStateItemIdList;
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getBopusEligibleStates]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBopusEligibleStates");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);

    }

    /** This method returns the site description and default country code for a site
    *
    * @param storeId
    * @return Site Details
    * @throws BBBBusinessException
    * @throws BBBSystemException */
   public final SiteVO getSiteDetailFromSiteId(final String siteId) throws BBBBusinessException, BBBSystemException {
       this.logDebug("Catalog API Method Name [getSiteDetailFromSiteId] for site " + siteId);
       final SiteVO siteVO = new SiteVO();

       if ((siteId != null) && !StringUtils.isEmpty(siteId)) {
           try {

               final RepositoryItem siteRepositoryItem = this.getSiteRepository().getItem(siteId,
                               BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
               if (siteRepositoryItem != null) {

                   if (siteRepositoryItem.getPropertyValue(BBBCatalogConstants.SITE_NAME_SITE_PROPERTY_NAME) != null) {
                       siteVO.setSiteName((String) siteRepositoryItem
                                       .getPropertyValue(BBBCatalogConstants.SITE_NAME_SITE_PROPERTY_NAME));
                   }
                   if (siteRepositoryItem.getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME) != null) {
                       siteVO.setCountryCode((String) siteRepositoryItem
                                       .getPropertyValue(BBBCatalogConstants.DEFAULT_COUNTRY_SITE_PROPERTY_NAME));
                   }
               } else {
                   throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                                   BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
               }
           } catch (final RepositoryException e) {
               this.logError("Catalog API Method Name [getSiteDetailFromSiteId]: RepositoryException ");
               throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                               BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
           }
       } else {
           throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                           BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
       }
       this.logDebug(siteVO.toString());
       return siteVO;
   }

   /** The method gets the registry name corresponding to the registry code
   *
   * @param registryCode
   * @return Name of registry
   * @throws BBBSystemException
   * @throws BBBBusinessException */
   public final String getRegistryTypeName(final String registryCode, final String siteId)
                  throws BBBSystemException, BBBBusinessException {

      this.logDebug("Catalog API Method Name [getRegistryTypeName] registryCode[" + registryCode + "]");

      String registryTypeName = "";
      try {
          BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypeName");
          final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
                          BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
          if (siteConfiguration != null) {
              @SuppressWarnings ("unchecked")
              final Set<RepositoryItem> registryRepositoryItem = (Set<RepositoryItem>) siteConfiguration
                              .getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME);
              if (!BBBUtility.isCollectionEmpty(registryRepositoryItem)) {
                  for (final RepositoryItem registryRepoItem : registryRepositoryItem) {
                      if ((registryRepoItem.getPropertyValue("registryTypeCode") != null)
                                      && ((String) registryRepoItem.getPropertyValue("registryTypeCode"))
                                                      .equalsIgnoreCase(registryCode)) {

                          registryTypeName = (String) registryRepoItem
                                          .getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME);
                          break;
                      }
                  }
              }
          } else {
              throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_NOT_AVAILABLE_IN_REPOSITORY,
                              BBBCatalogErrorCodes.REGISTRY_NOT_AVAILABLE_IN_REPOSITORY);
          }
          return registryTypeName;
      } catch (final RepositoryException e) {
          this.logError("Catalog API Method Name [getRegistryTypes]: RepositoryException ");
          throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                          BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
      } finally {
          BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypeName");
      }
   }
   
 
   /** Part of Release 2.1 implementation This method gets the configured values for the chat link and sets the values
    * in the CategoryVO
    *
    * @param pCategoryVO
    * @throws BBBSystemException
    * @throws BBBBusinessException */
   public void getBccManagedCategory(final CategoryVO pCategoryVO)
                   throws BBBSystemException, BBBBusinessException {
       this.logDebug("Catalog API Method Name [getBccManagedCategory]Parameter CategoryVO[" + pCategoryVO + "]");
		if (pCategoryVO != null) {
			final List<SortOptionsVO> sortOptionList = new ArrayList<SortOptionsVO>();
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			String channel = null;
			if(pRequest!=null && pRequest.getHeader(BBBCoreConstants.CHANNEL)!=null){
				channel=pRequest.getHeader(BBBCoreConstants.CHANNEL);
			}
			String siteId = "";
			if (null != pRequest) {
				siteId = (String) pRequest.getAttribute(BBBCoreConstants.SITE_ID);
			}
			if (BBBUtility.isEmpty(siteId)){
				siteId = getCurrentSiteId();
			}
			if (BBBUtility.isEmpty(channel)) {
				channel = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
			}
				
			final SortOptionVO sortOptions = new SortOptionVO();
			SortOptionsVO sortOption = new SortOptionsVO();
			final Object[] params = new Object[2];
			params[0] = pCategoryVO.getCategoryId();
			try {
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getBccManagedCategory");
				final RepositoryItem[] bbbManagedCategoryDetails = this.executeRQLQuery("categoryId=?0", params,
						BBBCatalogConstants.BCC_MANAGED_CATEGORY, 
						(MutableRepository) getBbbManagedCatalogRepository());
				final RepositoryItem siteItem = this.getSiteRepository()
						.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
				if (siteItem != null) {
					final Map<String, RepositoryItem> defSiteSortMap = (Map<String, RepositoryItem>)
							siteItem.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION);
					final Map<String, List<RepositoryItem>> siteSortOptMap = (Map<String, List<RepositoryItem>>)
							siteItem.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST);
					RepositoryItem defSiteSortOpt = null;
					RepositoryItem siteSortOptItem = null;
					List<RepositoryItem> siteSortOptList = new ArrayList<RepositoryItem>();
					if (!BBBUtility.isMapNullOrEmpty(defSiteSortMap)) {
						defSiteSortOpt = (RepositoryItem) defSiteSortMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
		                if (defSiteSortMap.keySet().contains(channel)) {
							defSiteSortOpt = (RepositoryItem) defSiteSortMap.get(channel);
						}
					} else {
						logError("Catalog API Method Name[getBccManagedCategory]"
								+ ":Default Site Sort Option Map Null");					
					}
					if (!BBBUtility.isMapNullOrEmpty(siteSortOptMap)) {
						siteSortOptItem = (RepositoryItem) siteSortOptMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
						if (siteSortOptMap.keySet().contains(channel)) {
							siteSortOptItem = (RepositoryItem) siteSortOptMap.get(channel);
						}
						siteSortOptList = (List<RepositoryItem>) siteSortOptItem
								.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS);
					} else {
						logError("Catalog API Method Name [getBccManagedCategory]"
								+ ":Site Sort Options Map Null");					
					}
					if (bbbManagedCategoryDetails != null && bbbManagedCategoryDetails[0] != null) {
						final RepositoryItem categoryPromoContent = (RepositoryItem) bbbManagedCategoryDetails[0]
								.getPropertyValue(BBBCatalogConstants.CATEGORY_PROMO_ID);
						if (null != categoryPromoContent
								&& null != categoryPromoContent
										.getPropertyValue(BBBCatalogConstants.PROMO_CONTENT)) {
							pCategoryVO.setBannerContent(categoryPromoContent
									.getPropertyValue(
											BBBCatalogConstants.PROMO_CONTENT)
									.toString());
							if(null != categoryPromoContent.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH)){
							pCategoryVO.setCssFilePath(categoryPromoContent
									.getPropertyValue(
											BBBCatalogConstants.CSS_FILE_PATH)
									.toString());
							}
							if(null != categoryPromoContent.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH)){
							pCategoryVO.setJsFilePath(categoryPromoContent
									.getPropertyValue(
											BBBCatalogConstants.JS_FILE_PATH)
									.toString());
							}
							logTrace("Category Promo Content \n:"
									+ categoryPromoContent.getPropertyValue(
											BBBCatalogConstants.PROMO_CONTENT)
											.toString()
									+ "\n CSS File Path: "
									+ categoryPromoContent.getPropertyValue(
											BBBCatalogConstants.CSS_FILE_PATH)
											
									+ "\n JS File Path: "
									+ categoryPromoContent.getPropertyValue(
											BBBCatalogConstants.JS_FILE_PATH)
											);
						} else {
							logTrace("Category Banner Content is not available for category id: "
									+ pCategoryVO.getCategoryId());
						}
						
						final Boolean isChatEnable = (Boolean) bbbManagedCategoryDetails[0]
								.getPropertyValue("chatEnabled");
						if (isChatEnable != null && isChatEnable.booleanValue()) {
							pCategoryVO.setChatEnabled(Boolean.TRUE);
							pCategoryVO.setChatURL((String) bbbManagedCategoryDetails[0]
									.getPropertyValue(BBBCatalogConstants.CHAT_URL));
							pCategoryVO.setChatCode((String) bbbManagedCategoryDetails[0]
									.getPropertyValue(BBBCatalogConstants.CHAT_CODE));
							pCategoryVO.setChatLinkPlaceholder((String) siteItem
									.getPropertyValue(BBBCatalogConstants.CHAT_LINK_PLACEHOLDER));
						} else if (isChatEnable != null && !isChatEnable.booleanValue()) {
							pCategoryVO.setChatEnabled(Boolean.FALSE);
						} else {
							pCategoryVO.setChatEnabled(Boolean.FALSE);
						}
						final Map<String, RepositoryItem> defCatSortMap  = (Map<String, RepositoryItem>) 
								bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.DEFCATSORTOPTION);
						final Map<String, List<RepositoryItem>> catSortOptMap = (Map<String, List<RepositoryItem>>) 
								bbbManagedCategoryDetails[0].getPropertyValue(BBBCatalogConstants.CATSORTOPTIONLIST);
						RepositoryItem defCatSortOpt = null;
						RepositoryItem catSortOptItem = null;
						List<RepositoryItem> catSortOptList = null;
						  if (!BBBUtility.isMapNullOrEmpty(defCatSortMap)) {	
							defCatSortOpt = (RepositoryItem) defCatSortMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
			                if (defCatSortMap.keySet().contains(channel)) {
			                	defCatSortOpt = (RepositoryItem) defCatSortMap.get(channel);
							}
			            }
						if (!BBBUtility.isMapNullOrEmpty(catSortOptMap)) {
							catSortOptItem = (RepositoryItem) catSortOptMap
			                		.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
							if (catSortOptMap.keySet().contains(channel)) {
								catSortOptItem = (RepositoryItem) catSortOptMap.get(channel);
							}
							catSortOptList = (List<RepositoryItem>) catSortOptItem
									.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS);
						}
				        if (defCatSortOpt != null) {
							sortOption.setSortCode((String) defCatSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
							sortOption.setSortValue((String) defCatSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
							sortOption.setAscending((Integer) defCatSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
							sortOption.setSortUrlParam((String) defCatSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
							sortOption.setRepositoryId(defCatSortOpt.getRepositoryId());
							
						} else {
							if (defSiteSortOpt != null) {
								sortOption.setSortCode((String) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOption.setSortValue((String) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOption.setAscending((Integer) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOption.setSortUrlParam((String) defSiteSortOpt
										.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
							}
						}
						//if (catSortOptList != null && !catSortOptList.isEmpty()) {
						if (!BBBUtility.isCollectionEmpty(catSortOptList)) {
							for (RepositoryItem catSortItem :catSortOptList) {
								final SortOptionsVO sortOptionVO = new SortOptionsVO();
								sortOptionVO.setSortCode((String) catSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOptionVO.setSortValue((String) catSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOptionVO.setAscending((Integer) catSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOptionVO.setSortUrlParam((String) catSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOptionVO.setRepositoryId(catSortItem.getRepositoryId());
								sortOptionList.add(sortOptionVO);
							}
						} else {
							if (!BBBUtility.isCollectionEmpty(siteSortOptList)) {
								for (RepositoryItem siteSortItem :siteSortOptList) {
									final SortOptionsVO sortOptionVO = new SortOptionsVO();
									sortOptionVO.setSortCode((String) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
									sortOptionVO.setSortValue((String) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
									sortOptionVO.setAscending((Integer) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
									sortOptionVO.setSortUrlParam((String) siteSortItem
											.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
									sortOptionVO.setRepositoryId(siteSortItem.getRepositoryId());
									sortOptionList.add(sortOptionVO);
								}
							}
						}
						// BV Additional tag #216
						final String displayAskAndAnswer = (String) bbbManagedCategoryDetails[0]
								.getPropertyValue("displayaskandanswer");
						if (displayAskAndAnswer != null) {
						    pCategoryVO.setDisplayAskAndAnswer(displayAskAndAnswer);
						}
						// Additonal for Story 117-A5

						final String zoomValue = (String)bbbManagedCategoryDetails[0]
								.getPropertyValue("zoomValue");
						if(zoomValue!=null){
							pCategoryVO.setZoomValue(zoomValue);
						}
							
						
						final String defaultViewValue = (String) bbbManagedCategoryDetails[0]
								.getPropertyValue("defaultViewValue");
						if (defaultViewValue != null) {
							pCategoryVO.setDefaultViewValue(defaultViewValue);
						}
						
					} else {
						//Category is not BCC Managed
						if (!BBBUtility.isCollectionEmpty(siteSortOptList)) {
							for (RepositoryItem siteSortItem :siteSortOptList) {
								final SortOptionsVO sortOptionVO = new SortOptionsVO();
								sortOptionVO.setSortCode((String) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOptionVO.setSortValue((String) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOptionVO.setAscending((Integer) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOptionVO.setSortUrlParam((String) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOptionVO.setRepositoryId(siteSortItem.getRepositoryId());
								sortOptionList.add(sortOptionVO);
							}
						}
						if (defSiteSortOpt != null) {
							sortOption.setSortCode((String) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
							sortOption.setSortValue((String) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
							sortOption.setAscending((Integer) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
							sortOption.setSortUrlParam((String) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
							sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
						}
						pCategoryVO.setDisplayAskAndAnswer(BBBCatalogConstants.ASK_ANSWER_DEFAULT_VALUE);
						pCategoryVO.setChatEnabled(Boolean.FALSE);
					}
					boolean ifDefInList = false;
					if (!BBBUtility.isListEmpty(sortOptionList)) {
						if (BBBUtility.isNotEmpty(sortOption.getRepositoryId())) {
							for (SortOptionsVO sortOptionListItem :sortOptionList) {
								
								if (sortOptionListItem.getRepositoryId()
										.equalsIgnoreCase(sortOption.getRepositoryId())) {
									ifDefInList= true;
								}
								if(ifDefInList){
									break;
								}
							}
							if(!ifDefInList && defSiteSortOpt != null){
								final String defSiteSortOptRepId = defSiteSortOpt.getRepositoryId();
								for (SortOptionsVO sortOptionListItem :sortOptionList) {
									
									if (sortOptionListItem.getRepositoryId().equalsIgnoreCase(defSiteSortOptRepId)) {
										sortOption.setSortCode((String) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
										sortOption.setSortValue((String) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
										sortOption.setAscending((Integer) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
										sortOption.setSortUrlParam((String) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
										sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
										ifDefInList= true;
									}
									if(ifDefInList){
										break;
									}
								}
							}
						} else {
							sortOption = sortOptionList.get(0);
						}
						}
									
					sortOptions.setDefaultSortingOption(sortOption);
					sortOptions.setSortingOptions(sortOptionList);
					pCategoryVO.setSortOptionVO(sortOptions);
					pCategoryVO.setChatLinkPlaceholder((String) siteItem
							.getPropertyValue(BBBCatalogConstants.CHAT_LINK_PLACEHOLDER));
				} else {
					logError("Catalog API Method Name [getBccManagedCategory]:Site Item Null");
				}
			} catch (final RepositoryException e) {
				this.logError("Catalog API Method Name [getBccManagedCategory]:RepositoryException", e);
				throw new BBBSystemException("chat_repository_exception", 
						"RepositoryException while retriving Chat configuration data");
			} finally {
               BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBccManagedCategory");
           }
       } else {
           throw new BBBBusinessException("categoryVO_null", "CategoryVO is NULL");
       }
       this.logDebug("Catalog API Method Name [getBccManagedCategory] ends");
   }
   
   @SuppressWarnings("unchecked")
	public BrandVO getBccManagedBrand(String brandName, String channel) throws BBBSystemException, BBBBusinessException {
		
		logDebug("Catalog API Method Name [getBccManagedBrand]brandName["+brandName+"]");
		SortOptionVO sortOptions = new SortOptionVO();
		SortOptionsVO sortOption = new SortOptionsVO();
		BrandVO brandVO=new BrandVO();
		if(BBBUtility.isNotEmpty(brandName)){
			List<SortOptionsVO> sortOptionList = new ArrayList<SortOptionsVO>();
           final String siteId = getCurrentSiteId();
           if (BBBUtility.isEmpty(channel)) {
           	channel = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
           }

			try {
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL+" getBccManagedBrand");
				Object[] params = new Object[1];
				params[0]=brandName;
				RepositoryItem[] brandRepositoryItem=this.executeRQLQuery(getBrandNameQuery(),params, BBBCatalogConstants.BRANDS_ITEM_DESCRIPTOR, getCatalogRepository());
					if(!BBBUtility.isArrayEmpty(brandRepositoryItem)){
					String brandId=(String) brandRepositoryItem[0].getPropertyValue("id");
					Object[] param = new Object[1];
					param[0]=brandId;
					RepositoryItem[] bccManagedBrandRepositoryItem=this.executeRQLQuery(getBrandIdQuery(),param, BBBCatalogConstants.BCC_MANAGED_BRAND_ITEM_DESCRIPTOR, (MutableRepository) this.getBbbManagedCatalogRepository());
					final RepositoryItem siteItem=this.getSiteRepository().getItem(siteId,BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
					final Map<String, RepositoryItem> defSiteSortMap = (Map<String, RepositoryItem>)
							siteItem.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION);
					final Map<String, RepositoryItem> siteSortOptMap = (Map<String, RepositoryItem>)
							siteItem.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST);
					
					RepositoryItem siteDefSortOpt = null;
					RepositoryItem siteSortOptItem = null;
					List<RepositoryItem> sortOptList = new ArrayList<RepositoryItem>();
					if (defSiteSortMap != null && !defSiteSortMap.isEmpty()) {
						siteDefSortOpt = defSiteSortMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
		                if (defSiteSortMap.keySet().contains(channel)) {
							siteDefSortOpt = defSiteSortMap.get(channel);
						}
					} else {
						logError("Catalog API Method Name[getBccManagedCategory]"
								+ ":Default Site Sort Option Map Null");					
					}
		        	if (siteSortOptMap != null && !siteSortOptMap.isEmpty()) { 
						siteSortOptItem = (RepositoryItem) siteSortOptMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
						if (siteSortOptMap.keySet().contains(channel)) {
							siteSortOptItem = (RepositoryItem) siteSortOptMap.get(channel);
						}
						sortOptList = (List<RepositoryItem>) siteSortOptItem
								.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS);
					} else {
						logError("Catalog API Method Name [getBccManagedCategory]"
								+ ":Site Sort Options Map Null");					
					}
		        	
					if (bccManagedBrandRepositoryItem != null) {
					//R2.2 BRAND Promo container from BCC START
						final RepositoryItem brandPromoSet = (RepositoryItem) bccManagedBrandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.PROMO_ID);

						if (null != brandPromoSet) {
							if(null != brandPromoSet.getPropertyValue(BBBCatalogConstants.BRAND_CONTENT)){
								brandVO.setBrandContent(brandPromoSet.getPropertyValue(BBBCatalogConstants.BRAND_CONTENT).toString());
							}
							if(null != brandPromoSet.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH)){
								brandVO.setCssFilePath(brandPromoSet.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH).toString());
							}
							if(null != brandPromoSet.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH)){
								brandVO.setJsFilePath(brandPromoSet.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH).toString());
							}
						}
					//R2.2 BRAND Promo container from BCC END
						
						final Map<String, RepositoryItem> defBrandSortMap  = (Map<String, RepositoryItem>)bccManagedBrandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.DEF_BRAND_SORT_OPTION);
						final Map<String, RepositoryItem> BrandSortOptMap = (Map<String, RepositoryItem>)bccManagedBrandRepositoryItem[0].getPropertyValue(BBBCatalogConstants.BRAND_SORT_OPTION_LIST);
						RepositoryItem defBrandSortOpt = null;
						RepositoryItem brandSortOptItem = null;
						List<RepositoryItem> brandSortOptList = null;
						if (defBrandSortMap != null && !defBrandSortMap.isEmpty()) {
							defBrandSortOpt = defBrandSortMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
							if (defBrandSortMap.keySet().contains(channel)) {
								defBrandSortOpt = defBrandSortMap.get(channel);
							}
						}
						if (BrandSortOptMap != null && !BrandSortOptMap.isEmpty()) {
							brandSortOptItem = (RepositoryItem) BrandSortOptMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
							if (BrandSortOptMap.keySet().contains(channel)) {
								brandSortOptItem = (RepositoryItem) BrandSortOptMap.get(channel);
							}
							brandSortOptList = (List<RepositoryItem>) brandSortOptItem.getPropertyValue(BBBCatalogConstants.SORTING_OPTIONS);
						}
						
	
						if(defBrandSortOpt != null){
							sortOption.setSortCode((String)defBrandSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
							sortOption.setSortValue((String)defBrandSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
							sortOption.setAscending((Integer)defBrandSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
							sortOption.setSortUrlParam((String)defBrandSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
						}
						else{
							if (siteDefSortOpt!=null){
								sortOption.setSortCode((String)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOption.setSortValue((String)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOption.setAscending((Integer)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOption.setSortUrlParam((String)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
							}
						}
	
	
						if(!BBBUtility.isListEmpty(brandSortOptList)) {
							for(RepositoryItem brandSortItem :brandSortOptList){
								SortOptionsVO sortOptionVO = new SortOptionsVO();
								sortOptionVO.setSortCode((String)brandSortItem.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOptionVO.setSortValue((String)brandSortItem.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOptionVO.setAscending((Integer)brandSortItem.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOptionVO.setSortUrlParam((String)brandSortItem.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOptionList.add(sortOptionVO);
							}
						} else {
							if(sortOptList != null && !sortOptList.isEmpty())
								for(RepositoryItem sortItem :sortOptList){
									SortOptionsVO sortOptionVO = new SortOptionsVO();
									sortOptionVO.setSortCode((String)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
									sortOptionVO.setSortValue((String)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
									sortOptionVO.setAscending((Integer)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
									sortOptionVO.setSortUrlParam((String)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
									sortOptionList.add(sortOptionVO);
								}
						}
	
					} else {
					    if(!BBBUtility.isCollectionEmpty(sortOptList))
						{
							for(RepositoryItem sortItem :sortOptList){
								SortOptionsVO sortOptionVO = new SortOptionsVO();
								sortOptionVO.setSortCode((String)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOptionVO.setSortValue((String)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOptionVO.setAscending((Integer)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOptionVO.setSortUrlParam((String)sortItem.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOptionList.add(sortOptionVO);
							}
	
						}
						if (siteDefSortOpt!=null){
							sortOption.setSortCode((String)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
							sortOption.setSortValue((String)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
							sortOption.setAscending((Integer)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
							sortOption.setSortUrlParam((String)siteDefSortOpt.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
						}
						//Brand is not BCC Managed
						
					}
					sortOptions.setDefaultSortingOption(sortOption);
					sortOptions.setSortingOptions(sortOptionList);
				}
				else{
					logDebug("Catalog API Method Name [getBccManagedBrand]:Brand with name" +params[0]+" is not found in database");
				}
			}catch (RepositoryException e) {
				logError("Catalog API Method Name [getBccManagedBrand]:RepositoryException", e);
			}
		}
		else {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getBccManagedBrand");
			throw new BBBBusinessException("brandName_null", "BrandName is NULL");
		}
		logDebug("Catalog API Method Name [getBccManagedBrand] ends");
		brandVO.setSortOptionVO(sortOptions);
		BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL+" getBccManagedBrand");
		return brandVO;
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

   /* START R2.1 Added Site specific Attribute Values List to be shown Scope Item #213 */
   /** This is to return List of attrbutes values configured at site level if any to be shown as facets.. Return null if
    * nothing is configured in this Property at site level.
    *
    * @param pSearchQuery
    * @return List<String>
    * @throws BBBSystemException
    * @throws BBBBusinessException
    * 
    *  */
   @SuppressWarnings ("unchecked")
   public final List<String> siteAttributeValues(final String pSiteId) throws BBBBusinessException, BBBSystemException {

       this.logDebug("Entering API Method Name [siteAttributeValues].");

       List<String> pList = null;
       try {
           BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " siteAttributeValues");
           final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(pSiteId,
                           BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

           if (siteConfiguration != null) {

               // Check if the list of attribute values to be shown as facets is configured at site level OR not.
               /*Commenting this condition as it's the same as the following 'if' block
                * if ((null != siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST))
                               && !((Set<RepositoryItem>) siteConfiguration
                                               .getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST))
                                               .isEmpty()) {*/
                   this.logDebug("Attribute Values are configured at Site Repository Level for Site:" + pSiteId);
                   final Set<RepositoryItem> attrRepItems = (Set<RepositoryItem>) siteConfiguration
                                   .getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST);
                   if (!BBBUtility.isCollectionEmpty(attrRepItems)) {
                       pList = new ArrayList<String>();
                       for (final RepositoryItem attributeRepoItem : attrRepItems) {
                           final String pAttrValue = (String) attributeRepoItem
                                           .getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME);
                           pList.add(pAttrValue);
                       }
                  
               }
           } else {
               throw new BBBBusinessException(BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY,
                               BBBCatalogErrorCodes.SITE_NOT_AVAILABLE_IN_REPOSITORY);
           }
       } catch (final RepositoryException e) {
           this.logError("Catalog API Method Name [siteAttributeValues]: RepositoryException ");
           throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                           BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
       } finally {
           BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " siteAttributeValues");
       }

       this.logDebug("Exiting API Method Name [siteAttributeValues].");
       return pList;
   }

   /** R2.1.1 : Ask and Answer and Chat Story Changes This method returns a Set of Category Ids that are to be ignores
    * for Shop Similar Link for that Site.
    *
    * @param siteId
    * @return Set<String>
    * @throws BBBSystemException
    * @throws BBBBusinessException */
   public Set<String> getSimilarProductsIgnoreList(final String siteId) throws BBBSystemException {
       this.logDebug("Entering Catalog API Method Name [getSimilarProductsIgnoreList]");
       RepositoryItem siteConfiguration = null;
       final Set<String> similarProductsIgnoreList = new HashSet<String>();

       try {
           siteConfiguration = this.getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

           if (siteConfiguration == null) {
               this.logDebug("no rows fetched for given site id");
           } else {
               this.logDebug("siteConfiguration object---" + siteConfiguration);
               
				/*
				 * Removing this if condition because the following if block also does the same check
				 * if (siteConfiguration.getPropertyValue(BBBCatalogConstants.SIMILAR_PRODUCT_IGNORE_LIST_PROPERTY_NAME) != null) {
				 * 
				 */
                   @SuppressWarnings ("unchecked")
                   final Set<RepositoryItem> similarProductsIgnoreListRepItems = (Set<RepositoryItem>) siteConfiguration
                                   .getPropertyValue(BBBCatalogConstants.SIMILAR_PRODUCT_IGNORE_LIST_PROPERTY_NAME);
                   if (similarProductsIgnoreListRepItems != null) {
                       for (final RepositoryItem similarProductsIgnoreListRepItem : similarProductsIgnoreListRepItems) {
                           similarProductsIgnoreList.add(similarProductsIgnoreListRepItem.getRepositoryId());
                       }
                   } else {
                       this.logDebug("Similar Products Ignore List Repository Items Set is null");
                   }
               //}
           }
       } catch (final RepositoryException e) {
           this.logError("Catalog API Method Name [getSimilarProductsIgnoreList]: RepositoryException ");
       }
       this.logDebug("Exiting Catalog API Method Name [getSimilarProductsIgnoreList]");
       return similarProductsIgnoreList;
   }

   
   /**
	 * R2.2 Product Comparison Page. 178-A4
	 * This method is used to check for site specific promotional attributes.If found,
	 * then a map of promotional attributes as value and attribute id as keys applicable 
	 * for that site and product is returned.
	 * 
	 * @param pSiteId
	 * @param attributeNameRepoItemMap 
	 * @return List
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public Map<String,AttributeVO> getSiteLevelAttributes(final String pSiteId, Map<String, RepositoryItem> attributeNameRepoItemMap)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("Entering BBBCatalogTools.getSiteLevelAttributes() method");

		Map<String, AttributeVO> prodAttributesMap = null;
		try {
			BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSiteLevelAttributes");
			if(null != pSiteId){
			final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(pSiteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);

			if (siteConfiguration != null) {
				// Check if the list of attribute values to be shown on comparison page is configured at site level OR not.
				this.logDebug("Attribute Values are configured at Site Repository Level for Site:" + pSiteId);
				final Set<RepositoryItem> attrRepItems = (Set<RepositoryItem>) siteConfiguration.getPropertyValue(BBBCatalogConstants.SITE_ATTRIBUTE_VALUES_LIST);
				if (!BBBUtility.isCollectionEmpty(attrRepItems)) {
					prodAttributesMap = new HashMap<String, AttributeVO>();
					for (Map.Entry entry : attributeNameRepoItemMap.entrySet()) {
						if(attrRepItems.contains(entry.getValue())){
							AttributeVO compare=new AttributeVO();
							RepositoryItem attributeRepoItem = (RepositoryItem) entry.getValue();
								/*
								 * This check is an additional check since,
								 * compare variable is initialized before.
								 *  This condition will always be true. 
								 *  Hence only 1 branch will be covered in test cases
								 */
								final String attrValue = (String) attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME);
								final String actionURL=(String) attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME);			
								final String imageURl=(String) attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME);					
								compare.setActionURL(actionURL);
								compare.setImageURL(imageURl);
								compare.setAttributeDescrip(attrValue);
							prodAttributesMap.put(attributeRepoItem.getRepositoryId(), compare);
						}
						}
					}
				}
			}
			else{
				prodAttributesMap = new HashMap<String, AttributeVO>();
				if(!BBBUtility.isMapNullOrEmpty(attributeNameRepoItemMap)){
				for (Map.Entry entry : attributeNameRepoItemMap.entrySet()) {
					AttributeVO compare=new AttributeVO();
					RepositoryItem attributeRepoItem = (RepositoryItem) entry.getValue();
						final String attrValue = (String) attributeRepoItem.getPropertyValue(BBBCatalogConstants.DISPLAY_DESCRIPTION_ATTRIBUTE_PROPERTY_NAME);
						final String actionURL=(String) attributeRepoItem.getPropertyValue(BBBCatalogConstants.ACTION_URL_ATTRIBUTE_PROPERTY_NAME);			
						final String imageURl=(String) attributeRepoItem.getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME);					
						compare.setActionURL(actionURL);
						compare.setImageURL(imageURl);
						compare.setAttributeDescrip(attrValue);

					prodAttributesMap.put(attributeRepoItem.getRepositoryId(), compare);
				}
			  }	
			}
		} catch (final RepositoryException e) {
			this.logError("Catalog API Method Name [getSiteLevelAttributes]: RepositoryException ", e);

		} finally {
			BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSiteLevelAttributes");
		}

		this.logDebug("Exiting BBBCatalogTools.getSiteLevelAttributes() method");
		return prodAttributesMap;
	}
   
	// Getting Default View [STRAT] 
		public String getDefaultPLPView(String siteId) throws BBBSystemException
		{

				logDebug("getDefaultPLPView [START] siteId: " + siteId);
			String defaultView = null;
			RepositoryItem siteConfiguration = null;
			try {
				siteConfiguration = this.getSiteRepository().getItem(siteId,
						BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
				if (null != siteConfiguration) {
					defaultView = (String) siteConfiguration
							.getPropertyValue(BBBCatalogConstants.DEFAULT_VIEW_PLP);
				} else {
					throw new BBBSystemException(
							BBBCoreErrorConstants.CATALOG_ERROR_1007,
							"no rows fetched for given site id");

				}
			} catch (RepositoryException e) {
				throw new BBBSystemException(
						BBBCoreErrorConstants.CATALOG_ERROR_1007,
						"no rows fetched for given site id");
			}
			catch (IllegalArgumentException e) {
				throw new BBBSystemException(
						BBBCoreErrorConstants.CATALOG_ERROR_1007,
						"no such column defined for given siteId");
			}

				logDebug("getDefaultPLPView [START] defaultView :" + defaultView);
			
			return defaultView;

		}

		// Getting Default View [END]

		/** This method returns the tax override threshold for a site */
		public double getTaxOverrideThreshold(String siteId) throws BBBBusinessException, BBBSystemException {
			vlogDebug("Catalog API Method Name [getShippingOverrideThreshold] siteId " + siteId + " Entry");
			RepositoryItem siteConfiguration = null;
			Double threshold = new Double(0);
			try {
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL	+ " getTaxOverrideThreshold");
				siteConfiguration = getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
				if (siteConfiguration == null) {
					return threshold.doubleValue();
				}
				if (siteConfiguration.getPropertyValue(BBBCatalogConstants.TBS_TAX_OVERRIDE_THRESHOLD) != null) {
					threshold = (Double) siteConfiguration.getPropertyValue(BBBCatalogConstants.TBS_TAX_OVERRIDE_THRESHOLD);
					vlogDebug("getTaxOverrideThreshold [" + threshold + "]");
				}
			} catch (final RepositoryException e) {
				vlogError("Catalog API Method Name [getTaxOverrideThreshold]: RepositoryException ");
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						e);
			} finally {
				vlogDebug("Catalog API Method Name [getShippingOverrideThreshold] siteId " + siteId + " Exit");
				BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getTaxOverrideThreshold");
			}
			return threshold.doubleValue();
		}

	
	/** Return the sort options based on the site and channel
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	@SuppressWarnings("unchecked")
	public SortOptionVO getSortOptionsForSite()
                 throws BBBSystemException, BBBBusinessException {
     logDebug("Catalog API Method Name [getDefaultSortOptions]");
			final List<SortOptionsVO> sortOptionList = new ArrayList<SortOptionsVO>();
			DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
			String channel = null;
			if(pRequest!=null && pRequest.getHeader(BBBCoreConstants.CHANNEL)!=null){
				channel=pRequest.getHeader(BBBCoreConstants.CHANNEL);
			}
			String siteId = "";
			if (null != pRequest) {
				siteId = (String) pRequest.getAttribute(BBBCoreConstants.SITE_ID);
			}
			if (BBBUtility.isEmpty(siteId)){
				siteId = getCurrentSiteId();
			}
			if (BBBUtility.isEmpty(channel)) {
				channel = BBBCoreConstants.DEFAULT_CHANNEL_VALUE;
			}
				
			final SortOptionVO sortOptions = new SortOptionVO();
			SortOptionsVO sortOption = new SortOptionsVO();
			try {
				BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSortOptionsForSite");
				final RepositoryItem siteItem = this.getSiteRepository()
						.getItem(siteId, BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
				if (siteItem != null) {
					final Map<String, RepositoryItem> defSiteSortMap = (Map<String, RepositoryItem>)
							siteItem.getPropertyValue(BBBCatalogConstants.DEFSITESORTOPTION);
					final Map<String, List<RepositoryItem>> siteSortOptMap = (Map<String, List<RepositoryItem>>)
							siteItem.getPropertyValue(BBBCatalogConstants.SITESORTOPTIONLIST);
					RepositoryItem defSiteSortOpt = null;
					RepositoryItem siteSortOptItem = null;
					List<RepositoryItem> siteSortOptList = new ArrayList<RepositoryItem>();
					if (!BBBUtility.isMapNullOrEmpty(defSiteSortMap)) {
						defSiteSortOpt = (RepositoryItem) defSiteSortMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
		                if (defSiteSortMap.keySet().contains(channel)) {
							defSiteSortOpt = (RepositoryItem) defSiteSortMap.get(channel);
						}
					} else {
						logError("Catalog API Method Name[getSortOptionsForSite]"
								+ ":Default Site Sort Option Map Null");					
					}
					if (!BBBUtility.isMapNullOrEmpty(siteSortOptMap)) {
						siteSortOptItem = (RepositoryItem) siteSortOptMap.get(BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
						if (siteSortOptMap.keySet().contains(channel)) {
							siteSortOptItem = (RepositoryItem) siteSortOptMap.get(channel);
						}
						siteSortOptList = (List<RepositoryItem>) siteSortOptItem
								.getPropertyValue(BBBCatalogConstants.SORTINGOPTIONS);
					} else {
						logError("Catalog API Method Name [getSortOptionsForSite]"
								+ ":Site Sort Options Map Null");					
					}
						if (!BBBUtility.isCollectionEmpty(siteSortOptList)) {
							for (RepositoryItem siteSortItem :siteSortOptList) {
								final SortOptionsVO sortOptionVO = new SortOptionsVO();
								sortOptionVO.setSortCode((String) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
								sortOptionVO.setSortValue((String) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
								sortOptionVO.setAscending((Integer) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
								sortOptionVO.setSortUrlParam((String) siteSortItem
										.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
								sortOptionVO.setRepositoryId(siteSortItem.getRepositoryId());
								sortOptionList.add(sortOptionVO);
							}
						}
						if (defSiteSortOpt != null) {
							sortOption.setSortCode((String) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
							sortOption.setSortValue((String) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
							sortOption.setAscending((Integer) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
							sortOption.setSortUrlParam((String) defSiteSortOpt
									.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
							sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
						}
					boolean ifDefInList = false;
					if (!BBBUtility.isListEmpty(sortOptionList)) {
						if (BBBUtility.isNotEmpty(sortOption.getRepositoryId())) {
							for (SortOptionsVO sortOptionListItem :sortOptionList) {
								
								if (sortOptionListItem.getRepositoryId()
										.equalsIgnoreCase(sortOption.getRepositoryId())) {
									ifDefInList= true;
								}
								if(ifDefInList){
									break;
								}
							}
							if(!ifDefInList && defSiteSortOpt != null){
								final String defSiteSortOptRepId = defSiteSortOpt.getRepositoryId();
								for (SortOptionsVO sortOptionListItem :sortOptionList) {
									
									if (sortOptionListItem.getRepositoryId().equalsIgnoreCase(defSiteSortOptRepId)) {
										sortOption.setSortCode((String) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGCODE));
										sortOption.setSortValue((String) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGVALUE));
										sortOption.setAscending((Integer) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGORDER));
										sortOption.setSortUrlParam((String) defSiteSortOpt
												.getPropertyValue(BBBCatalogConstants.SORTINGURLPARAM));
										sortOption.setRepositoryId(defSiteSortOpt.getRepositoryId());
										ifDefInList= true;
									}
									if(ifDefInList){
										break;
									}
								}
							}
						} else {
							sortOption = sortOptionList.get(0);
						}
						}
									
					sortOptions.setDefaultSortingOption(sortOption);
					sortOptions.setSortingOptions(sortOptionList);
				} else {
					logError("Catalog API Method Name [getSortOptionsForSite]:Site Item Null");
				}
			} catch (final RepositoryException e) {
				this.logError("Catalog API Method Name [getSortOptionsForSite]:RepositoryException", e);
				throw new BBBSystemException("chat_repository_exception", 
						"RepositoryException while retriving Chat configuration data");
			} finally {
             BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBccManagedCategory");
         }
			  this.logDebug("Catalog API Method Name [getSortOptionsForSite] ends");
     return sortOptions;
   
 }
     
   /**
    * Getters & setters section
    */

	public String getBopusEligibleStateQuery() {
		return bopusEligibleStateQuery;
	}

	public void setBopusEligibleStateQuery(String bopusEligibleStateQuery) {
		this.bopusEligibleStateQuery = bopusEligibleStateQuery;
	}

	public MutableRepository getShippingRepository() {
		return shippingRepository;
	}

	public void setShippingRepository(MutableRepository shippingRepository) {
		this.shippingRepository = shippingRepository;
	}

	public String getBrandIdQuery() {
		return brandIdQuery;
	}

	public void setBrandIdQuery(String brandIdQuery) {
		this.brandIdQuery = brandIdQuery;
	}

	public String getBrandNameQuery() {
		return brandNameQuery;
	}

	public void setBrandNameQuery(String brandNameQuery) {
		this.brandNameQuery = brandNameQuery;
	}

	/**
	 * @return the sddShipMethodId
	 */
	public String getSddShipMethodId() {
		return sddShipMethodId;
	}

	/**
	 * @param sddShipMethodId the sddShipMethodId to set
	 */
	public void setSddShipMethodId(String sddShipMethodId) {
		this.sddShipMethodId = sddShipMethodId;
	}

	/**
	 * @return the catalogRepository
	 */
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	/**
	 * @param catalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
	
	/**
	 * Gets the bbb managed catalog repository.
	 * 
	 * @return the managedCatalogRepository
	 */
	public Repository getBbbManagedCatalogRepository() {
		return managedCatalogRepository;
	}

	/**
	 * Sets the bbb managed catalog repository.
	 * @param managedCatalogRepository the managedCatalogRepository to set
	 */
	public void setBbbManagedCatalogRepository(Repository managedCatalogRepository) {
		this.managedCatalogRepository = managedCatalogRepository;		
	}

	/**
	 * @return the siteRepository
	 */
	public MutableRepository getSiteRepository() {
		return siteRepository;
	}

	/**
	 * @param siteRepository the siteRepository to set
	 */
	public void setSiteRepository(MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}	
}