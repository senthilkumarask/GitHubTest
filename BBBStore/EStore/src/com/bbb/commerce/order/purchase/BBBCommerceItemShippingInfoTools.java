/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order.purchase/BBBCommerceItemShippingInfoTools.java.BBBCommerceItemShippingInfoTools $$
 * @updated $DateTime: Jan 4, 2012 4:21:26 PM
 */
package com.bbb.commerce.order.purchase;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderTools;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer;
import atg.commerce.order.purchase.CommerceItemShippingInfoTools;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.repository.RepositoryException;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;

import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;

/**
 * @author
 *
 */
public class BBBCommerceItemShippingInfoTools extends
		CommerceItemShippingInfoTools {
	
	private BBBCatalogTools catalogTools;
	private OrderTools orderTools;
	private PurchaseProcessHelper purchaseProcessHelper;

    /** @return */
    public final BBBCatalogTools getCatalogUtil() {
        return this.catalogTools;
    }

    /** @param catalogUtil */
    public final void setCatalogUtil(final BBBCatalogTools catalogUtil) {
        this.catalogTools = catalogUtil;
    }
	/**
	 * @return the orderTools
	 */
	public OrderTools getOrderTools() {
		return orderTools;
	}

	/**
	 * @param orderTools the orderTools to set
	 */
	public void setOrderTools(OrderTools orderTools) {
		this.orderTools = orderTools;
	}

	/**
	 * @return the purchaseProcessHelper
	 */
	public PurchaseProcessHelper getPurchaseProcessHelper() {
		return purchaseProcessHelper;
	}

	/**
	 * @param purchaseProcessHelper the purchaseProcessHelper to set
	 */
	public void setPurchaseProcessHelper(PurchaseProcessHelper purchaseProcessHelper) {
		this.purchaseProcessHelper = purchaseProcessHelper;
	}

	/**
	 * override the method to add ShippingGroupName in setShippingGroupName
	 * instead of adding splitShippingGroupName
	 */
	@Override
	public CommerceItemShippingInfo splitCommerceItemShippingInfoByQuantity(
			CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
			CommerceItemShippingInfo pCommerceItemShippingInfo,
			long pSplitQuantity) throws CommerceException {
		
		if(isLoggingDebug()){
			logDebug(new StringBuilder()
					.append("splitCommerceItemShippingInfoByQuantity: started - item: ")
					.append(pCommerceItemShippingInfo.getCommerceItem().getId())
					.append(" quantity: ").append(pSplitQuantity).toString());
		}
		if(pCommerceItemShippingInfo.getQuantity() < pSplitQuantity){
			throw new CommerceException("Item Qty can not be less than split Qty");
		}	
		
		final CommerceItemShippingInfo newInfo = callSuperSplitCommerceItemShippingInfoByQuantity(pCommerceItemShippingInfoContainer, pCommerceItemShippingInfo,
				pSplitQuantity);
		newInfo.setShippingGroupName(pCommerceItemShippingInfo.getShippingGroupName());
		
		if(isLoggingDebug()){
			logDebug(new StringBuilder()
				.append("splitCommerceItemShippingInfoByQuantity: ended - item: ")
				.append(pCommerceItemShippingInfo.getCommerceItem().getId())
				.toString());
		}
		return newInfo;
	}
    
	/**
	 * Method use to call super split commerce item shipping info method
	 * @param pCommerceItemShippingInfoContainer - container
	 * @param pCommerceItemShippingInfo - item and shipping info object
	 * @param pSplitQuantity - the split quantity
	 * @return - commerceItemShippingInfo object
	 * @throws CommerceException
	 */
	protected CommerceItemShippingInfo callSuperSplitCommerceItemShippingInfoByQuantity(
			CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
			CommerceItemShippingInfo pCommerceItemShippingInfo, long pSplitQuantity) throws CommerceException {
		return super.splitCommerceItemShippingInfoByQuantity(pCommerceItemShippingInfoContainer, pCommerceItemShippingInfo, pSplitQuantity);
	}
	
	/**
	 * This method is used to split LTL items
	 * @param pCommerceItemShippingInfoContainer
	 * @param pCommerceItemShippingInfo
	 * @param pSplitQuantity
	 * @param pOrder
	 * @return CommerceItemShippingInfo newInfo
	 * @throws CommerceException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @throws RepositoryException
	 */
	public CommerceItemShippingInfo splitCommerceItemShippingInfoForLTLByQuantity(
			CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
			CommerceItemShippingInfo pCommerceItemShippingInfo,
			long pSplitQuantity, Order pOrder) throws CommerceException, BBBSystemException, BBBBusinessException{
		
		BBBCommerceItem currentCommItem = (BBBCommerceItem) pCommerceItemShippingInfo.getCommerceItem();
		String sgId = ((BBBShippingGroupCommerceItemRelationship)currentCommItem.getShippingGroupRelationships().get(0)).getShippingGroup().getId();
		String prodId = currentCommItem.getAuxiliaryData().getProductId();
		String skuId = currentCommItem.getCatalogRefId();
		final CommerceItem item = this.getCommerceItemManager().createCommerceItem(
						((BBBOrderTools) this.getOrderTools()).getDefaultCommerceItemType(),skuId,prodId, pSplitQuantity);
		String newAssemblyCommId = "";
		String newDeliveryCommId = "";
		String currentDeliveryCommId = currentCommItem.getDeliveryItemId();
		String currentAssemblyCommId = currentCommItem.getAssemblyItemId();
		if (item != null) {
			((BBBCommerceItem)item).setLtlItem(true);
			this.logDebug("Delivery Surcharge item created: " + item);
			this.getCommerceItemManager().addAsSeparateItemToOrder(pOrder, item);
			this.getCommerceItemManager().addItemQuantityToShippingGroup(pOrder,item.getId(), sgId,pSplitQuantity);
			if(BBBUtility.isNotEmpty(currentDeliveryCommId)){
				try {
					if(currentCommItem.getWhiteGloveAssembly()!= null && 
							currentCommItem.getWhiteGloveAssembly().equalsIgnoreCase(BBBCatalogConstants.TRUE)){
						newAssemblyCommId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLAssemblyFeeSku(pOrder, pOrder.getShippingGroup(sgId), pOrder.getSiteId(), item);
						((BBBCommerceItem)item).setWhiteGloveAssembly(BBBCatalogConstants.TRUE);
					}
					newDeliveryCommId = ((BBBCommerceItemManager)getCommerceItemManager()).addLTLDeliveryChargeSku(pOrder, pOrder.getShippingGroup(sgId), pOrder.getSiteId(), item);
					((BBBCommerceItem)item).setDeliveryItemId(newDeliveryCommId);
					((BBBCommerceItem)item).setAssemblyItemId(newAssemblyCommId);
				} catch (RepositoryException e) {
					logError("Repository exception occurred while creating assembly and delivery items.");
				}
				
			}
			
		}
		
		CommerceItemShippingInfo currentInfo = pCommerceItemShippingInfo;
		CommerceItemShippingInfo newInfo = new CommerceItemShippingInfo();
		long newCurrentQuantity = currentInfo.getQuantity() - pSplitQuantity;
		
		createNewInfoForBBBCommerceItem(pSplitQuantity, pOrder, item, currentInfo, newInfo, newCurrentQuantity);
		
		//add commerce item shipping info for delivery item.
		createNewInforForDeliveryItem(pCommerceItemShippingInfoContainer, pSplitQuantity, pOrder, sgId,
				newDeliveryCommId, newCurrentQuantity, currentDeliveryCommId);
		
		//add commerce item shipping info for assembly item.
		createNewInfoForAssemblyItem(pCommerceItemShippingInfoContainer, pSplitQuantity, pOrder, sgId,
				newAssemblyCommId, newCurrentQuantity, currentAssemblyCommId);
		

		adjustHandlingInstructionsForSplit(currentInfo, newInfo, pSplitQuantity);
		
		copyCommerceItemProperties(currentCommItem, (BBBCommerceItem)item);
		String id = null;
        if(newInfo.getCommerceItem() != null){
        	 id = newInfo.getCommerceItem().getId();
        }
		pCommerceItemShippingInfoContainer.addCommerceItemShippingInfo(id, newInfo);
		debug("splitCommerceItemShippingInfoByQuantity: ended - item: " + pCommerceItemShippingInfo.getCommerceItem().getId());
		return newInfo;
		
		
	
	}

	/**
	 * This method copies all the properties form current commerce items to new commerce item created.
	 * @param currentCommItem
	 * @param item
	 */
	private void copyCommerceItemProperties(BBBCommerceItem currentCommItem,
			final BBBCommerceItem newCommItem) {
		if(newCommItem != null){
			newCommItem.setCommerceItemMoved(currentCommItem.getCommerceItemMoved());
			newCommItem.setStoreId(currentCommItem.getStoreId());
			newCommItem.setRegistryId(currentCommItem.getRegistryId());
			newCommItem.setItemMoved(currentCommItem.isItemMoved());
			newCommItem.setRegistryInfo(currentCommItem.getRegistryInfo());	
			newCommItem.setBts( currentCommItem.getBts());
			newCommItem.setVdcInd(currentCommItem.isVdcInd());
			newCommItem.setFreeShippingMethod(currentCommItem.getFreeShippingMethod());
			newCommItem.setSkuSurcharge(currentCommItem.getSkuSurcharge());
			newCommItem.setLastModifiedDate(currentCommItem.getLastModifiedDate());
			newCommItem.setIsEcoFeeEligible(currentCommItem.getIsEcoFeeEligible());
			newCommItem.setLtlShipMethod(currentCommItem.getLtlShipMethod());
		}
	}

	/**
	 * @param pCommerceItemShippingInfoContainer
	 * @param pSplitQuantity
	 * @param pOrder
	 * @param sgId
	 * @param assemblyCommId
	 * @param newCurrentQuantity
	 * @param currentLtlItemAssoc
	 * @throws ShippingGroupNotFoundException
	 * @throws CommerceException
	 */
	private void createNewInfoForAssemblyItem(
			CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
			long pSplitQuantity, Order pOrder, String sgId,
			String newAssemblyCommId, long newCurrentQuantity, String currentAssemblyCommId)
			throws ShippingGroupNotFoundException, CommerceException {
		if(BBBUtility.isNotEmpty(newAssemblyCommId)){
			CommerceItemShippingInfo currentAssemblyInfo = (CommerceItemShippingInfo) pCommerceItemShippingInfoContainer.getCommerceItemShippingInfos(currentAssemblyCommId).get(0);
			CommerceItemShippingInfo newAssemblyInfo = new CommerceItemShippingInfo();
			newAssemblyInfo.setCommerceItem(pOrder.getCommerceItem(newAssemblyCommId));
			newAssemblyInfo.setRelationshipType(newAssemblyInfo.getQuantityType());
			newAssemblyInfo.setShippingMethod(currentAssemblyInfo.getShippingMethod());
			newAssemblyInfo.setShippingGroupName(currentAssemblyInfo.getSplitShippingGroupName());
			newAssemblyInfo.setQuantity(pSplitQuantity);
			currentAssemblyInfo.setQuantity(newCurrentQuantity);
			currentAssemblyInfo.setSplitShippingGroupName(null);
			currentAssemblyInfo.setSplitQuantity(0L);
			this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(pOrder,currentAssemblyInfo.getCommerceItem(),newCurrentQuantity);
			currentAssemblyInfo.getCommerceItem().setQuantity(newCurrentQuantity);
			String assId = newAssemblyInfo.getCommerceItem().getId();
			pCommerceItemShippingInfoContainer.addCommerceItemShippingInfo(assId, newAssemblyInfo);
		}
	}

	/**
	 * @param pCommerceItemShippingInfoContainer
	 * @param pSplitQuantity
	 * @param pOrder
	 * @param sgId
	 * @param deliveryCommId
	 * @param newCurrentQuantity
	 * @param currentLtlItemAssoc
	 * @throws ShippingGroupNotFoundException
	 * @throws CommerceException
	 */
	private void createNewInforForDeliveryItem(
			CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer,
			long pSplitQuantity, Order pOrder, String sgId,
			String newDeliveryCommId, long newCurrentQuantity, String currentDeliveryCommId)
			throws ShippingGroupNotFoundException, CommerceException {
		if(BBBUtility.isNotEmpty(newDeliveryCommId)){
			CommerceItemShippingInfo currentDeliveryInfo = (CommerceItemShippingInfo) pCommerceItemShippingInfoContainer.getCommerceItemShippingInfos(currentDeliveryCommId).get(0);
			CommerceItemShippingInfo newDeliveryInfo = new CommerceItemShippingInfo();
			newDeliveryInfo.setCommerceItem(pOrder.getCommerceItem(newDeliveryCommId));
			newDeliveryInfo.setRelationshipType(newDeliveryInfo.getQuantityType());
			newDeliveryInfo.setShippingMethod(currentDeliveryInfo.getShippingMethod());
			newDeliveryInfo.setShippingGroupName(currentDeliveryInfo.getSplitShippingGroupName());
			newDeliveryInfo.setQuantity(pSplitQuantity);
			currentDeliveryInfo.setQuantity(newCurrentQuantity);
			currentDeliveryInfo.setSplitShippingGroupName(null);
			currentDeliveryInfo.setSplitQuantity(0L);
			this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(pOrder,currentDeliveryInfo.getCommerceItem(),newCurrentQuantity);
			currentDeliveryInfo.getCommerceItem().setQuantity(newCurrentQuantity);
			String delId = newDeliveryInfo.getCommerceItem().getId();
			pCommerceItemShippingInfoContainer.addCommerceItemShippingInfo(delId, newDeliveryInfo);
		}
	}

	/**
	 * @param pSplitQuantity
	 * @param pOrder
	 * @param item
	 * @param currentInfo
	 * @param newInfo
	 * @param newCurrentQuantity
	 * @throws CommerceException
	 */
	private void createNewInfoForBBBCommerceItem(long pSplitQuantity,
			Order pOrder, final CommerceItem item,
			CommerceItemShippingInfo currentInfo,
			CommerceItemShippingInfo newInfo, long newCurrentQuantity)
			throws CommerceException {
		newInfo.setCommerceItem(item);
		newInfo.setRelationshipType(newInfo.getQuantityType());
		newInfo.setShippingMethod(currentInfo.getShippingMethod());
		newInfo.setShippingGroupName(currentInfo.getSplitShippingGroupName());
		newInfo.setQuantity(pSplitQuantity);
		newInfo.setShippingGroupName(currentInfo.getShippingGroupName());
		currentInfo.setQuantity(newCurrentQuantity);
		currentInfo.setSplitShippingGroupName(null);
		currentInfo.setSplitQuantity(0L);
		this.getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(pOrder,currentInfo.getCommerceItem(),newCurrentQuantity);
		currentInfo.getCommerceItem().setQuantity(newCurrentQuantity);
	}
	
	
	@Override
	public void applyCommerceItemShippingInfo(
			CommerceItemShippingInfo pCommerceItemShippingInfo, Order pOrder,
			ShippingGroupMapContainer pShippingGroupMapContainer) throws CommerceException {
		try {
				pOrder.getCommerceItem(pCommerceItemShippingInfo.getCommerceItem().getId());
		} catch (CommerceItemNotFoundException e) {
			logError("CommerceItem not found while applying CommerItemShippingInfo");
			return;
		} catch (InvalidParameterException e) {
			logError("Ivalid CommerceItem found while applying CommerItemShippingInfo");
			return;
		}
		super.applyCommerceItemShippingInfo(pCommerceItemShippingInfo, pOrder,pShippingGroupMapContainer);
	}

	public void consolidateLTLShippingInfos(CommerceItemShippingInfoContainer pCommerceItemShippingGroupContainer)
		throws CommerceException{
		super.consolidateShippingInfos(pCommerceItemShippingGroupContainer);
}
}
