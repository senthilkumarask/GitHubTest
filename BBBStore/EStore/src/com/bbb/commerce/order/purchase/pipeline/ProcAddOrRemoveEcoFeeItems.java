package com.bbb.commerce.order.purchase.pipeline;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemImpl;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.processor.SavedProperties;
import atg.commerce.pricing.PricingConstants;
import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.EcoFeeCommerceItem;

public class ProcAddOrRemoveEcoFeeItems extends SavedProperties implements PipelineProcessor {

	/* ===================================================== *
	 	MEMBER VARIABLES
	 * ===================================================== */
	/**
	 * Property of the CatalogTools
	 */	
	private BBBCatalogToolsImpl mCatalogUtil;	
	
	/**
	 * Property of the PurchaseProcessHelper
	 */	
	private BBBPurchaseProcessHelper mPurchaseProcessHelper;
		
	/**
     * Final Static variable for success.
     */
	private static final int SUCCESS = 1;
	
	/**
     * Final Static variable for INITIAL.
     */
	private static final String INITIAL = "INITIAL";
	
	
	/**
	 * Property of the PurchaseProcessHelper
	 */	
	private OrderManager orderManager;	
	
	private String repriceOrderChainId;
	
	private Locale userLocale;


	
	
	/* ===================================================== *
	 	STANDARD METHODS
	 * ===================================================== */
	
	/** This is the pipeline link in the update order pipeline. It is responsible to 
	 *  Add or Remove eco Fee item.
	 *  
	 * @param	 Object, PipelineResult
	 * 
	 * @return	int
	 */	
	@SuppressWarnings("unchecked")
	public int runProcess(Object params, PipelineResult pipelineResult) 
			throws java.lang.Exception {
		String isFromOrderFeedListener=BBBCoreConstants.BLANK;
	
		
		Map<String, Object> requestInputMap = (Map<String, Object>) params;		
		BBBOrderImpl order = (BBBOrderImpl) requestInputMap.get(PipelineConstants.ORDER);
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		
		if (pRequest != null) {
			isFromOrderFeedListener = pRequest.getParameter(BBBCoreConstants.FROM_ORDER_FEED_LISTENER);
			
		} else if (order!=null) {
			if (isLoggingDebug()) {
				logDebug("The pRequest is null, Retrieve indicator for OrderUpdateMessageListener flow from order");
			}
			isFromOrderFeedListener = order.isOSUpdateListener()?BBBCoreConstants.TRUE:BBBCoreConstants.FALSE;
		}
		
		if (!StringUtils.isBlank(isFromOrderFeedListener) && isFromOrderFeedListener.equalsIgnoreCase(BBBCoreConstants.TRUE)) {
			if (isLoggingDebug()) {
				logDebug("The current request is from OrderFeedListener therefore skipping the AddOrRemoveEcoFeeItems flow with params:" + params);
			}
			return SUCCESS;
		}
		
		else {
			if (isLoggingDebug()) {
				logDebug("Entered  AddOrRemoveEcoFeeItems : " + params);
			}
		
			if (order != null && getPurchaseProcessHelper().isEcoFeeForCurrentSite(order)) {
					boolean repriceOrder = addOrUpdateEcoFeeItems(order);
					if (repriceOrder) {
						rePriceOrder(order);
					}
				}
					return SUCCESS;
			}
	}
	
	
	@SuppressWarnings("unchecked")
	private boolean addOrUpdateEcoFeeItems(Order pOrder) throws RepositoryException, BBBSystemException, CommerceException {		
		
		boolean repriceOrder = false;		
		List<ShippingGroup> shippingGroups = pOrder.getShippingGroups();
		
		for (ShippingGroup pShippingGroup : shippingGroups) {
			if (isLoggingDebug()){
			   logDebug("addOrUpdateEcoFeeItems called for SG -: " + pShippingGroup + ", & Order -: " + pOrder );
			}
			
			EcoFeeSKUVO ecoFeeSkuVO = null;
			String ecoFeeItemId = null;
			String stateId = null;
			List<CommerceItemRelationship> ciRels = new ArrayList<CommerceItemRelationship>();
			ciRels.addAll(pShippingGroup.getCommerceItemRelationships());
			
			repriceOrder = validateEcoFeeItems(pOrder, pShippingGroup, ciRels);
			
				
			try {
			    stateId = getStateId(pShippingGroup);
            } catch (BBBBusinessException e) {
                if(isLoggingDebug()) {
                    logDebug(LogMessageFormatter.formatMessage(null, "Error getting State Id"));
                }
            }
				
			boolean removeAllEcoFee = isStateEcoFeeFree(stateId);
			
            if(removeAllEcoFee) {
            	repriceOrder = removeAllEcoFeeItemsFromShippingGroup(pOrder, pShippingGroup, ciRels);           
                continue;
            }
                
			 
			for (CommerceItemRelationship commerceItemRelationship : ciRels) {
				
				CommerceItem commerceItem = commerceItemRelationship.getCommerceItem();
				try {
					if(commerceItem instanceof BBBCommerceItem) {
						ecoFeeSkuVO = getCatalogUtil().getEcoFeeSKUDetailForState(stateId, commerceItem.getCatalogRefId());
						
						ecoFeeItemId = ((BBBShippingGroup)pShippingGroup).getEcoFeeItemMap().get(commerceItem.getId());
						if(ecoFeeItemId == null){								
							getPurchaseProcessHelper().addEcoFeeItem(commerceItem, pOrder, pShippingGroup, commerceItemRelationship.getQuantity() ,ecoFeeSkuVO);
							repriceOrder = Boolean.TRUE;
						} else {							
							CommerceItemImpl onlineEcoFeeItem = null;
							try {
								onlineEcoFeeItem = (CommerceItemImpl) pOrder.getCommerceItem(ecoFeeItemId);
								if(!onlineEcoFeeItem.getCatalogRefId().equalsIgnoreCase(ecoFeeSkuVO.getFeeEcoSKUId()) || onlineEcoFeeItem.getQuantity() != commerceItemRelationship.getQuantity()) {
									repriceOrder = removeEcoFeeItemFromShippingGroup(pOrder, pShippingGroup, ecoFeeItemId, commerceItemRelationship.getCommerceItem().getId(), ciRels);								
									getPurchaseProcessHelper().addEcoFeeItem(commerceItem, pOrder, pShippingGroup, commerceItemRelationship.getQuantity() ,ecoFeeSkuVO);								
								}	
							} catch (CommerceItemNotFoundException e) {
								getPurchaseProcessHelper().addEcoFeeItem(commerceItem, pOrder, pShippingGroup, commerceItemRelationship.getQuantity() ,ecoFeeSkuVO);
								repriceOrder = Boolean.TRUE;
							}							
														
						}
					}					
				} catch (BBBBusinessException e) {
					ecoFeeItemId = ((BBBShippingGroup)pShippingGroup).getEcoFeeItemMap().get(commerceItem.getId());
					if (!StringUtils.isBlank(ecoFeeItemId)) {								
						repriceOrder = removeEcoFeeItemFromShippingGroup(pOrder, pShippingGroup, ecoFeeItemId, commerceItemRelationship.getCommerceItem().getId(), ciRels);						
					}						
				} catch (InvalidParameterException e) {
					((BBBShippingGroup)pShippingGroup).getEcoFeeItemMap().remove(commerceItem.getId());
				} 			
			}				
		}
		return repriceOrder;
	}


    private boolean isStateEcoFeeFree(String stateId) throws BBBSystemException {
        boolean removeAllEcoFee = false;
        try {
            
            if(stateId == null || INITIAL.equalsIgnoreCase(stateId) || !getCatalogUtil().isEcoFeeEligibleForState(stateId)){								
                removeAllEcoFee = true;                    
            }
        } catch (BBBBusinessException e1) {
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "Error getting isEcoFeeEligibleForState"));
            }
            removeAllEcoFee = true;
        } catch (BBBSystemException e1) {
            if(isLoggingDebug()) {
                logDebug(LogMessageFormatter.formatMessage(null, "Error getting isEcoFeeEligibleForState"));
            }
            removeAllEcoFee = true;
        }
        return removeAllEcoFee;
    }
	
	
	private String getStateId(ShippingGroup pShippingGroup) throws BBBBusinessException, BBBSystemException {
		String stateId = null;
		if(pShippingGroup instanceof BBBHardGoodShippingGroup && ((BBBHardGoodShippingGroup) pShippingGroup).getShippingAddress() != null) {
			stateId = ((BBBHardGoodShippingGroup) pShippingGroup).getShippingAddress().getState();
		} else if (pShippingGroup instanceof BBBStoreShippingGroup) {
			stateId = getCatalogUtil().getStoreDetails(((BBBStoreShippingGroup) pShippingGroup).getStoreId()).getState();
		}
		return stateId;
	}


	private boolean validateEcoFeeItems(Order pOrder, ShippingGroup pShippingGroup, List<CommerceItemRelationship> ciRels) {
		
		Map<String,String> ecoFeemap = new HashMap<String, String>();
		ecoFeemap.putAll(((BBBShippingGroup)pShippingGroup).getEcoFeeItemMap());
		Set<String> commerceItemIds = ecoFeemap.keySet();
		
		boolean repriceOrder = false;
				
		for (String commerceItemId : commerceItemIds) {

			boolean matchFound = false;
			for (CommerceItemRelationship ciRel : ciRels) {
				if (ciRel.getCommerceItem().getId().equals(commerceItemId)) {
					matchFound = true;
				}
			}
			if (!matchFound) {				
				repriceOrder = removeEcoFeeItemFromShippingGroup(pOrder, pShippingGroup,
						ecoFeemap.get(commerceItemId), commerceItemId,
						ciRels);
			}

		}
		return repriceOrder;
	}


	private void rePriceOrder (Order order) throws RunProcessException, RepositoryException {
		
		HashMap<String, Object> paramsReprice = new HashMap<String, Object>();
		
		paramsReprice.put(PricingConstants.PRICING_OPERATION_PARAM,  PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING);
		paramsReprice.put(PricingConstants.ORDER_PARAM, order);
		paramsReprice.put(PricingConstants.LOCALE_PARAM, getUserLocale());
		RepositoryItem profileForOrder = getOrderManager().getOrderTools().getProfileTools().getProfileForOrder(order);
		paramsReprice.put(PricingConstants.PROFILE_PARAM, profileForOrder);
		paramsReprice.put(PipelineConstants.ORDERMANAGER, getOrderManager());
		
		getOrderManager().getPipelineManager().runProcess(getRepriceOrderChainId(), paramsReprice);
	}
	
	private void removeShippingGroupCommerceItemRelationByCommereceItemId (final String commerceItemId, 
			ShippingGroup pShippingGroup, List<CommerceItemRelationship> ciRels) throws RelationshipNotFoundException, InvalidParameterException {	
		
		for (CommerceItemRelationship ciRelation : ciRels) {
			if (ciRelation.getCommerceItem().getId().equalsIgnoreCase(commerceItemId)) {
				pShippingGroup.removeCommerceItemRelationship(ciRelation.getId());
			}
		}
	}
	
	private boolean removeEcoFeeItemFromShippingGroup (Order pOrder, ShippingGroup pShippingGroup, String ecoFeeItemId, 
			String commerceItemId, List<CommerceItemRelationship> ciRels) {
		((BBBShippingGroup)pShippingGroup).getEcoFeeItemMap().remove(commerceItemId);
		try {
			removeShippingGroupCommerceItemRelationByCommereceItemId(ecoFeeItemId, pShippingGroup, ciRels);
			pOrder.removeCommerceItem(ecoFeeItemId);
			
		} catch (RelationshipNotFoundException e1) {
			logError("The relationship is not part of order for commerce item " + ecoFeeItemId);
			return false;
		} catch (InvalidParameterException e1) {
			logError("The Invalid Parameter for commerce item " + ecoFeeItemId);
			return false;
		} catch (CommerceItemNotFoundException e1) {
			logError("The commerce item not found in order" + ecoFeeItemId);
			return false;
		}
		return Boolean.TRUE;
	}
	
	
	private boolean removeAllEcoFeeItemsFromShippingGroup (Order pOrder, ShippingGroup pShippingGroup,
			List<CommerceItemRelationship> ciRels) {		
		
		((BBBShippingGroup)pShippingGroup).getEcoFeeItemMap().clear();	
		boolean results = false;
		for (CommerceItemRelationship ciRelation : ciRels) {
			if (ciRelation.getCommerceItem() instanceof EcoFeeCommerceItem) {
				try {
					pShippingGroup.removeCommerceItemRelationship(ciRelation.getId());
					pOrder.removeCommerceItem(ciRelation.getCommerceItem().getId());
					results = true;
				} catch (RelationshipNotFoundException e) {
					logError("The relationship is not part of order" + ciRelation.getId());
				} catch (InvalidParameterException e) {
					logError("The relationship is not part of order" + ciRelation.getId());
				} catch (CommerceItemNotFoundException e) {
					logError("The commerceitem is not part of order" + ciRelation.getCommerceItem().getId());
				}
			}
		}
		return results;
	}
	
	/* ===================================================== *
	 	GETTERS and SETTERS
	 * ===================================================== */
	
	/**
	 *Overriden method of PipelineProcessor which indicated the return code for run process method.
	 *
	 * @return Int: Array of int .
	 */
	public int[] getRetCodes() {
	    int[] retn = {SUCCESS};
	    return retn;
	}
	
	
	public BBBCatalogToolsImpl getCatalogUtil() {
		return mCatalogUtil;
	}
	public void setCatalogUtil(final BBBCatalogToolsImpl catalogUtil) {
		this.mCatalogUtil = catalogUtil;
	}


	public BBBPurchaseProcessHelper getPurchaseProcessHelper() {
		return mPurchaseProcessHelper;
	}


	public void setPurchaseProcessHelper(
			BBBPurchaseProcessHelper pPurchaseProcessHelper) {
		this.mPurchaseProcessHelper = pPurchaseProcessHelper;
	}


	/**
	 * @return the orderManager
	 */
	public OrderManager getOrderManager() {
		return orderManager;
	}


	/**
	 * @param orderManager the orderManager to set
	 */
	public void setOrderManager(OrderManager orderManager) {
		this.orderManager = orderManager;
	}


	/**
	 * @return the repriceOrderChainId
	 */
	public String getRepriceOrderChainId() {
		return repriceOrderChainId;
	}


	/**
	 * @param repriceOrderChainId the repriceOrderChainId to set
	 */
	public void setRepriceOrderChainId(String repriceOrderChainId) {
		this.repriceOrderChainId = repriceOrderChainId;
	}

	/**
	 * @return the userLocale
	 */
	public Locale getUserLocale() {
		return userLocale;
	}


	/**
	 * @param userLocale the userLocale to set
	 */
	public void setUserLocale(Locale userLocale) {
		this.userLocale = userLocale;
	}
	
	
}
