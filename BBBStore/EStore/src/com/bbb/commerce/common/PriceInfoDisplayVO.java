package com.bbb.commerce.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bbb.constants.BBBCoreConstants;

import atg.repository.RepositoryItem;

public class PriceInfoDisplayVO{
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean mFreeShipping;

	private double onlineRawTotal;

	private double promoDiscountAmount;

	private double rawAmount;

	private double totalAmount;	

	private double storeAmount;

	private double totalSavedAmount;

	private double totalSavedPercentage;

	private double totalDiscountShare;

	private int itemCount;

	private double totalTax;

	private double totalShippingAmount;

	private double unitListPrice;

	private double unitSalePrice;

	private double unitSavedAmount;

	private double unitSavedPercentage;

	private double totalSurcharge;
	
	private double surchargeSavings;
	
	private double shippingSavings;

	private double onlineTotal;
	
	private double orderPreTaxAmount;

	private double giftWrapPrice;

	private int hardgoodShippingGroupItemCount;

	private int storePickupShippingGroupItemCount;
	
	private List itemPromotionVOList;
	
	private long undiscountedItemsCount;
	
	private Map<RepositoryItem, Double> shippingAdjustments;
	
	private Map<RepositoryItem, Double> itemAdjustments;
	
	private Map<RepositoryItem, Double> orderAdjustments;
	
	private double rawShippingTotal;
	
	private double ecoFeeTotal;
	
	private double giftWrapTotal;
	
	private double shippingLevelTax;
	
	private double shippingGroupItemsTotal;
	
	private double shippingGroupItemTotal;
	
	private List<PriceBeanDisplayVO> priceBeans;
	
	private double onlineEcoFeeTotal;
	
	private double storeEcoFeeTotal;

    private double onlinePurchaseTotal;
    
    private double totalPreTaxAmount;
    
    private double shippingCountyLevelTax;
    
	private double shippingStateLevelTax;
	//R 2.2 remaining amonunt after giftcard 
	private double amountWithoutGiftCard;
	
	private double totalGiftCardAmt;
    //LTL Start
    private double deliverySurcharge;
    
    private double deliverySurchargeProrated;
   
    private double assemblyFee;
    
    private double totalDeliverySurcharge;
    
    private double totalAssemblyFee;
    
    private double deliverySurchargeSaving;
    
    private boolean maxDeliverySurchargeReached;
    
    private double maxDeliverySurcharge;
    
    private int promotionCount;
    
    //LTL End
    
    private Map<String , List<PriceBeanDisplayVO>> promoPriceBeansDisplayVO;
    
    private double finalShippingCharge;
    
    private double prevPrice;
    //for Order  History
    private String unformattedTotalAmount;
    
	public String getUnformattedTotalAmount() {
		return BBBCoreConstants.BLANK+this.getTotalAmount();
	}

	public void setUnformattedTotalAmount(String unformattedTotalAmount) {
		this.unformattedTotalAmount = unformattedTotalAmount;
	}

	/**
	 * @return the shippingCountyLevelTax
	 */
	public double getShippingCountyLevelTax() {
		return shippingCountyLevelTax;
	}

	/**
	 * @param shippingCountyLevelTax the shippingCountyLevelTax to set
	 */
	public void setShippingCountyLevelTax(double shippingCountyLevelTax) {
		this.shippingCountyLevelTax = shippingCountyLevelTax;
	}

	/**
	 * @return the shippingStateLevelTax
	 */
	public double getShippingStateLevelTax() {
		return shippingStateLevelTax;
	}

	/**
	 * @param shippingStateLevelTax the shippingStateLevelTax to set
	 */
	public void setShippingStateLevelTax(double shippingStateLevelTax) {
		this.shippingStateLevelTax = shippingStateLevelTax;
	}

    /**
	 * @return the totalPreTaxAmount
	 */
	public double getTotalPreTaxAmount() {
		return totalPreTaxAmount;
	}

	/**
	 * @param totalPreTaxAmount the totalPreTaxAmount to set
	 */
	public void setTotalPreTaxAmount(double totalPreTaxAmount) {
		this.totalPreTaxAmount = totalPreTaxAmount;
	}

	private String estimatedShippingMethod;
    
    
    

	/**
	 * @return the prevPrice
	 */
	public double getPrevPrice() {
		return prevPrice;
	}

	/**
	 * @param pPrevPrice the prevPrice to set
	 */
	public void setPrevPrice(double pPrevPrice) {
		this.prevPrice = pPrevPrice;
	}

	/**
	 * @return the shippingGroupItemsTotal
	 */
	public double getShippingGroupItemsTotal() {
		return shippingGroupItemsTotal;
	}

	/**
	 * @param shippingGroupItemsTotal the shippingGroupItemsTotal to set
	 */
	public void setShippingGroupItemsTotal(double shippingGroupItemsTotal) {
		this.shippingGroupItemsTotal = shippingGroupItemsTotal;
	}

	/**
	 * @return the shippingGroupItemTotal
	 */
	public double getShippingGroupItemTotal() {
		return shippingGroupItemTotal;
	}

	/**
	 * @param shippingGroupItemTotal the shippingGroupItemTotal to set
	 */
	public void setShippingGroupItemTotal(double shippingGroupItemTotal) {
		this.shippingGroupItemTotal = shippingGroupItemTotal;
	}

	/**
	 * @return the giftWrapPrice
	 */
	public double getGiftWrapPrice() {
		return giftWrapPrice;
	}

	/**
	 * @param giftWrapPrice
	 *            the giftWrapPrice to set
	 */
	public void setGiftWrapPrice(double giftWrapPrice) {
		this.giftWrapPrice = giftWrapPrice;
	}

	/**
	 * @return the rawAmount
	 */
	public double getRawAmount() {
		return rawAmount;
	}

	/**
	 * @param rawAmount
	 *            the rawAmount to set
	 */
	public void setRawAmount(double rawAmount) {
		this.rawAmount = rawAmount;
	}

	/**
	 * @return the totalAmount
	 */
	public double getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount
	 *            the totalAmount to set
	 */
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}


	/**
	 * @return the storeAmount
	 */
	public double getStoreAmount() {
		return storeAmount;
	}

	/**
	 * @param storeAmount
	 *            the storeAmount to set
	 */
	public void setStoreAmount(double storeAmount) {
		this.storeAmount = storeAmount;
	}

	/**
	 * @return the totalSavedAmount
	 */
	public double getTotalSavedAmount() {
		return totalSavedAmount;
	}

	/**
	 * @param totalSavedAmount
	 *            the totalSavedAmount to set
	 */
	public void setTotalSavedAmount(double totalSavedAmount) {
		this.totalSavedAmount = totalSavedAmount;
	}

	/**
	 * @return the totalSavedPercentage
	 */
	public double getTotalSavedPercentage() {
		return totalSavedPercentage;
	}

	/**
	 * @param totalSavedPercentage
	 *            the totalSavedPercentage to set
	 */
	public void setTotalSavedPercentage(double totalSavedPercentage) {
		this.totalSavedPercentage = totalSavedPercentage;
	}

	/**
	 * @return the totalDiscountShare
	 */
	public double getTotalDiscountShare() {
		return totalDiscountShare;
	}

	/**
	 * @param totalDiscountShare
	 *            the totalDiscountShare to set
	 */
	public void setTotalDiscountShare(double totalDiscountShare) {
		this.totalDiscountShare = totalDiscountShare;
	}

	/**
	 * @return the itemCount
	 */
	public int getItemCount() {
		return itemCount;
	}

	/**
	 * @param itemCount
	 *            the itemCount to set
	 */
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * @return the totalTax
	 */
	public double getTotalTax() {
		return totalTax;
	}

	/**
	 * @param totalTax
	 *            the totalTax to set
	 */
	public void setTotalTax(double totalTax) {
		this.totalTax = totalTax;
	}

	/**
	 * @return the totalShippingAmount
	 */
	public double getTotalShippingAmount() {
		return totalShippingAmount;
	}

	/**
	 * @param totalShippingAmount
	 *            the totoalShippingAmount to set
	 */
	public void setTotalShippingAmount(double totoalShippingAmount) {
		this.totalShippingAmount = totoalShippingAmount;
	}

	/**
	 * @return the unitListPrice
	 */
	public double getUnitListPrice() {
		return unitListPrice;
	}

	/**
	 * @param unitListPrice
	 *            the unitListPrice to set
	 */
	public void setUnitListPrice(double unitListPrice) {
		this.unitListPrice = unitListPrice;
	}

	/**
	 * @return the unitSalePrice
	 */
	public double getUnitSalePrice() {
		return unitSalePrice;
	}

	/**
	 * @param unitSalePrice
	 *            the unitSalePrice to set
	 */
	public void setUnitSalePrice(double unitSalePrice) {
		this.unitSalePrice = unitSalePrice;
	}

	/**
	 * @return the unitSavedAmount
	 */
	public double getUnitSavedAmount() {
		return unitSavedAmount;
	}

	/**
	 * @param unitSavedAmount
	 *            the unitSavedAmount to set
	 */
	public void setUnitSavedAmount(double unitSavedAmount) {
		this.unitSavedAmount = unitSavedAmount;
	}

	/**
	 * @return the unitSavedPercentage
	 */
	public double getUnitSavedPercentage() {
		return unitSavedPercentage;
	}

	/**
	 * @param unitSavedPercentage
	 *            the unitSavedPercentage to set
	 */
	public void setUnitSavedPercentage(double unitSavedPercentage) {
		this.unitSavedPercentage = unitSavedPercentage;
	}

	/**
	 * @return the appliedAdjustmentsList
	 */
	/*public List<RepositoryItem> getAppliedAdjustmentsList() {

		if (appliedAdjustmentsList != null) {
			return appliedAdjustmentsList;
		} else {
			appliedAdjustmentsList = new ArrayList<RepositoryItem>();
			return appliedAdjustmentsList;
		}

	}

	*//**
	 * @param appliedAdjustmentsList
	 *            the appliedAdjustmentsList to set
	 *//*
	public void setAppliedAdjustmentsList(
			List<RepositoryItem> appliedAdjustmentsList) {
		this.appliedAdjustmentsList = appliedAdjustmentsList;
	}

	*//**
	 * @return the unAppliedAdjustmentsList
	 *//*
	public List<RepositoryItem> getUnAppliedAdjustmentsList() {

		if (unAppliedAdjustmentsList != null) {
			return unAppliedAdjustmentsList;
		} else {
			unAppliedAdjustmentsList = new ArrayList<RepositoryItem>();
			return unAppliedAdjustmentsList;
		}

	}

	*//**
	 * @param unAppliedAdjustmentsList
	 *            the unAppliedAdjustmentsList to set
	 *//*

	public void setUnAppliedAdjustmentsList(
			List<RepositoryItem> unAppliedAdjustmentsList) {
		this.unAppliedAdjustmentsList = unAppliedAdjustmentsList;
	}

	*//**
	 * @return the adjustmentsList
	 *//*
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public List getAdjustmentsList() {

		if (adjustmentsList != null) {
			return adjustmentsList;

		} else {
			adjustmentsList = new ArrayList();
			return adjustmentsList;

		}
	}

	*//**
	 * @param adjustmentsList
	 *            the adjustmentsList to set
	 *//*

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public void setAdjustmentsList(List adjustmentsList) {
		this.adjustmentsList = adjustmentsList;
	}*/

	/**
	 * @return the totalSurcharge
	 */
	public double getTotalSurcharge() {
		return totalSurcharge;
	}

	/**
	 * @param totalSurcharge
	 *            the totalSurcharge to set
	 */
	public void setTotalSurcharge(double totalSurcharge) {
		this.totalSurcharge = totalSurcharge;
	}

	/**
	 * @return the onlineTotal
	 */
	public double getOnlineTotal() {
		return onlineTotal;
	}

	/**
	 * @param onlineTotal the onlineTotal to set
	 */
	public void setOnlineTotal(double onlineTotal) {
		this.onlineTotal = onlineTotal;
	}

	public double getPreTaxAmount() {
		return getRawAmount() + getTotalShippingAmount() + getTotalSurcharge() + getGiftWrapTotal() + getEcoFeeTotal();
	}

	public int getHardgoodShippingGroupItemCount() {
		return hardgoodShippingGroupItemCount;
	}

	public void setHardgoodShippingGroupItemCount(
			int hardgoodShippingGroupItemCount) {
		this.hardgoodShippingGroupItemCount = hardgoodShippingGroupItemCount;
	}

	public int getStorePickupShippingGroupItemCount() {
		return storePickupShippingGroupItemCount;
	}

	public void setStorePickupShippingGroupItemCount(
			int storePickupShippingGroupItemCount) {
		this.storePickupShippingGroupItemCount = storePickupShippingGroupItemCount;
	}

	/**
	 * @return the shipOnlineOrderPreTaxAmount
	 */
	public double getOrderPreTaxAmount() {
		return orderPreTaxAmount;
	}

	/**
	 * @param orderPreTaxAmount the shipOnlineOrderPreTaxAmount to set
	 */
	public void setOrderPreTaxAmount(double orderPreTaxAmount) {
		this.orderPreTaxAmount = orderPreTaxAmount;
	}

	
	/**
	 * @return the undiscountedItemsCount
	 */
	public long getUndiscountedItemsCount() {
		return undiscountedItemsCount;
	}

	/**
	 * @param undiscountedItemsCount the undiscountedItemsCount to set
	 */
	public void setUndiscountedItemsCount(long undiscountedItemsCount) {
		this.undiscountedItemsCount = undiscountedItemsCount;
	}

	/**
	 * @return the rawShippingTotal
	 */
	public double getRawShippingTotal() {
		return rawShippingTotal;
	}

	/**
	 * @param rawShippingTotal the rawShippingTotal to set
	 */
	public void setRawShippingTotal(double rawShippingTotal) {
		this.rawShippingTotal = rawShippingTotal;
	}

	/**
	 * @return the shippingDiscount
	 */
	public double getShippingDiscount() {
		return getRawShippingTotal() - getTotalShippingAmount();
	}

	/**
	 * @return the ecoFeeTotal
	 */
	public double getEcoFeeTotal() {
		return ecoFeeTotal;
	}

	/**
	 * @param ecoFeeTotal the ecoFeeTotal to set
	 */
	public void setEcoFeeTotal(double ecoFeeTotal) {
		this.ecoFeeTotal = ecoFeeTotal;
	}

	/**
	 * @return the giftWrapTotal
	 */
	public double getGiftWrapTotal() {
		return giftWrapTotal;
	}

	/**
	 * @param giftWrapTotal the giftWrapTotal to set
	 */
	public void setGiftWrapTotal(double giftWrapTotal) {
		this.giftWrapTotal = giftWrapTotal;
	}

	/**
	 * @return the shippingLevelTax
	 */
	public double getShippingLevelTax() {
		return shippingLevelTax;
	}

	/**
	 * @param shippingLevelTax the shippingLevelTax to set
	 */
	public void setShippingLevelTax(double shippingLevelTax) {
		this.shippingLevelTax = shippingLevelTax;
	}

	/**
	 * @return the itemPromotionVOList
	 */
	public List getItemPromotionVOList() {
		if (itemPromotionVOList == null) {
				itemPromotionVOList = new ArrayList();
		}
		return itemPromotionVOList;
	}

	/**
	 * @param itemPromotionVOList the itemPromotionVOList to set
	 */
	public void setItemPromotionVOList(List itemPromotionVOList) {
		this.itemPromotionVOList = itemPromotionVOList;
	}

	/**
	 * @return the priceBeans
	 */
	public List<PriceBeanDisplayVO> getPriceBeans() {
		return priceBeans;
	}

	/**
	 * @param priceBeans the priceBeans to set
	 */
	public void setPriceBeans(List<PriceBeanDisplayVO> priceBeans) {
		this.priceBeans = priceBeans;
	}

	/**
	 * @return the shippingAdjustments
	 */
	public Map<RepositoryItem, Double> getShippingAdjustments() {
		if (shippingAdjustments == null) {
			shippingAdjustments = new HashMap<RepositoryItem, Double>();
		}
		return shippingAdjustments;
	}

	/**
	 * @param shippingAdjustments the shippingAdjustments to set
	 */
	public void setShippingAdjustments(
			Map<RepositoryItem, Double> shippingAdjustments) {
		this.shippingAdjustments = shippingAdjustments;
	}
		

	public Map<RepositoryItem, Double> getItemAdjustments() {
		return itemAdjustments;
	}

	public void setItemAdjustments(Map<RepositoryItem, Double> itemAdjustments) {
		this.itemAdjustments = itemAdjustments;
	}


	public Map<RepositoryItem, Double> getOrderAdjustments() {
		return orderAdjustments;
	}

	public void setOrderAdjustments(Map<RepositoryItem, Double> orderAdjustments) {
		this.orderAdjustments = orderAdjustments;
	}

	/**
	 * @return the onlineEcoFeeTotal
	 */
	public double getOnlineEcoFeeTotal() {
		return onlineEcoFeeTotal;
	}

	/**
	 * @param onlineEcoFeeTotal the onlineEcoFeeTotal to set
	 */
	public void setOnlineEcoFeeTotal(double onlineEcoFeeTotal) {
		this.onlineEcoFeeTotal = onlineEcoFeeTotal;
	}

	/**
	 * @return the storeEcoFeeTotal
	 */
	public double getStoreEcoFeeTotal() {
		return storeEcoFeeTotal;
	}

	/**
	 * @param storeEcoFeeTotal the storeEcoFeeTotal to set
	 */
	public void setStoreEcoFeeTotal(double storeEcoFeeTotal) {
		this.storeEcoFeeTotal = storeEcoFeeTotal;
	}

    public void setOnlinePurchaseTotal(double pOnlinePurchaseTotal) {
        onlinePurchaseTotal = pOnlinePurchaseTotal;
    }
    
    public double getOnlinePurchaseTotal() {
        return onlinePurchaseTotal;
    }

    public boolean getFreeShipping() {
        return mFreeShipping;
    }
    
    public boolean isFreeShipping() {
        return mFreeShipping;
    }

    public void setFreeShipping(boolean pFreeShipping) {
        this.mFreeShipping = pFreeShipping;
    }

	public double getSurchargeSavings() {
		return surchargeSavings;
	}

	public void setSurchargeSavings(double surchargeSavings) {
		this.surchargeSavings = surchargeSavings;
	}

	public double getShippingSavings() {
		return shippingSavings;
	}

	public void setShippingSavings(double shippingSavings) {
		this.shippingSavings = shippingSavings;
	}

	public String getEstimatedShippingMethod() {
		return estimatedShippingMethod;
	}

	public void setEstimatedShippingMethod(String estimatedShippingMethod) {
		this.estimatedShippingMethod = estimatedShippingMethod;
	}

	public double getAmountWithoutGiftCard() {
		return amountWithoutGiftCard;
	}

	public void setAmountWithoutGiftCard(double amountWithoutGiftCard) {
		this.amountWithoutGiftCard = amountWithoutGiftCard;
	}

	
	//LTL Changes Start
	public double getDeliverySurcharge() {
		return deliverySurcharge;
	}
    
	public void setDeliverySurcharge(double deliverySurcharge) {
		this.deliverySurcharge = deliverySurcharge;
}
	
	public double getDeliverySurchargeProrated() {
		return deliverySurchargeProrated;
	}

	public void setDeliverySurchargeProrated(double deliverySurchargeProrated) {
		this.deliverySurchargeProrated = deliverySurchargeProrated;
	}

	public double getAssemblyFee() {
		return assemblyFee;
	}

	public void setAssemblyFee(double assemblyFee) {
		this.assemblyFee = assemblyFee;
	}

	public double getTotalDeliverySurcharge() {
		return totalDeliverySurcharge;
	}

	public void setTotalDeliverySurcharge(double totalDeliverySurcharge) {
		this.totalDeliverySurcharge = totalDeliverySurcharge;
	}

	public double getTotalAssemblyFee() {
		return totalAssemblyFee;
	}

	public void setTotalAssemblyFee(double totalAssemblyFee) {
		this.totalAssemblyFee = totalAssemblyFee;
	}

	public double getDeliverySurchargeSaving() {
		return deliverySurchargeSaving;
	}

	public void setDeliverySurchargeSaving(double deliverySurchargeSaving) {
		this.deliverySurchargeSaving = deliverySurchargeSaving;
	}

	public boolean isMaxDeliverySurchargeReached() {
		return maxDeliverySurchargeReached;
	}

	public void setMaxDeliverySurchargeReached(boolean maxDeliverySurchargeReached) {
		this.maxDeliverySurchargeReached = maxDeliverySurchargeReached;
	}

	public double getMaxDeliverySurcharge() {
		return maxDeliverySurcharge;
	}

	public void setMaxDeliverySurcharge(double maxDeliverySurcharge) {
		this.maxDeliverySurcharge = maxDeliverySurcharge;
	}

	/**
	 * @return the finalShippingCharge
	 */
	public double getFinalShippingCharge() {
		return finalShippingCharge;
	}

	/**
	 * @param finalShippingCharge the finalShippingCharge to set
	 */
	public void setFinalShippingCharge(double finalShippingCharge) {
		this.finalShippingCharge = finalShippingCharge;
	}

	/**
	 * @return the promotionCount
	 */
	public int getPromotionCount() {
		return promotionCount;
	}

	/**
	 * @param promotionCount the promotionCount to set
	 */
	public void setPromotionCount(int promotionCount) {
		this.promotionCount = promotionCount;
	}

	/**
	 * @return the promoPriceBeansDisplayVO
	 */
	public Map<String, List<PriceBeanDisplayVO>> getPromoPriceBeansDisplayVO() {
		return promoPriceBeansDisplayVO;
	}

	/**
	 * @param promoPriceBeansDisplayVO the promoPriceBeansDisplayVO to set
	 */
	public void setPromoPriceBeansDisplayVO(
			Map<String, List<PriceBeanDisplayVO>> promoPriceBeansDisplayVO) {
		this.promoPriceBeansDisplayVO = promoPriceBeansDisplayVO;
	}


	//LTL Changes End
	
	/**
	 * 
	 * @return the totalGiftCardAmt
	 */
	public double getTotalGiftCardAmt() {
		return totalGiftCardAmt;
	}

	/**
	 * 
	 * @param the totalGiftCardAmt to set
	 */
	public void setTotalGiftCardAmt(double totalGiftCardAmt) {
		this.totalGiftCardAmt = totalGiftCardAmt;
	}

	/**
	 * @return the onlineRawTotal
	 */
	public double getOnlineRawTotal() {
		return onlineRawTotal;
	}

	/**
	 * @param onlineRawTotal the onlineRawTotal to set
	 */
	public void setOnlineRawTotal(double onlineRawTotal) {
		this.onlineRawTotal = onlineRawTotal;
	}

	/**
	 * @return the promoDiscountAmount
	 */
	public double getPromoDiscountAmount() {
		return promoDiscountAmount;
	}

	/**
	 * @param promoDiscountAmount the promoDiscountAmount to set
	 */
	public void setPromoDiscountAmount(double promoDiscountAmount) {
		this.promoDiscountAmount = promoDiscountAmount;
	}

}
