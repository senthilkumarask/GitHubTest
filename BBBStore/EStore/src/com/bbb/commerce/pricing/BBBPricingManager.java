package com.bbb.commerce.pricing;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.pricing.UnitPriceBean;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import atg.commerce.pricing.priceLists.PriceDroplet;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.core.util.Range;
import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.pricing.bean.ItemPriceVO;
import com.bbb.commerce.pricing.bean.PriceInfoVO;
import com.bbb.common.BBBGenericService;
import com.bbb.common.vo.PromotionVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.order.bean.EcoFeeCommerceItem;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.utils.BBBUtility;
import com.bbb.commerce.order.BBBCommerceItemManager;
import com.bbb.commerce.order.BBBShippingGroupCommerceItemRelationship;



/** @author sjatas */
public class BBBPricingManager extends BBBGenericService {

    private static final String EXCEPTION_OCCURRED_WHILE_FETCHING_PRICE = "Exception occurred while fetching price";
    private BBBPropertyManager propertyManager;
    private PriceListManager priceListManager;
    private SiteManager siteManager;
    private BBBPricingTools pricingTools;
	private BBBCommerceItemManager commerceItemManager;
	
	/**
	 * @return the commerceItemManager
	 */
	public BBBCommerceItemManager getCommerceItemManager() {
		return this.commerceItemManager;
	}

	/**
	 * @param commerceItemManager the commerceItemManager to set
	 */
	public void setCommerceItemManager(BBBCommerceItemManager commerceItemManager) {
		this.commerceItemManager = commerceItemManager;
	}
	
    /** Default Constructor. */
    public BBBPricingManager() {
        super();
    }

    /** @return Site Manager*/
    public final SiteManager getSiteManager() {
        return this.siteManager;
    }

    /** @param msiteManager the msiteManager to set */
    public final void setSiteManager(final SiteManager msiteManager) {
        this.siteManager = msiteManager;
    }

    /** @return the BBB profile property Manager */
    public final BBBPropertyManager getBbbProfilePropertyManager() {
        return this.propertyManager;
    }

    /** @param pBBBProfilePropertyManager the BBB profile property Manager to set */
    public final void setBbbProfilePropertyManager(final BBBPropertyManager pBBBProfilePropertyManager) {
        this.propertyManager = pBBBProfilePropertyManager;
    }

    /** @param pPriceListManager */
    public final void setPriceListManager(final PriceListManager pPriceListManager) {
        this.priceListManager = pPriceListManager;
    }

    /** @return */
    public final PriceListManager getPriceListManager() {
        return this.priceListManager;
    }
    
    /**
	 * @return the pricingTools
	 */
	public BBBPricingTools getPricingTools() {
		return pricingTools;
	}

	/**
	 * @param pricingTools the pricingTools to set
	 */
	public void setPricingTools(BBBPricingTools pricingTools) {
		this.pricingTools = pricingTools;
	}

	/** Method to fetch price related details of items.
     *
     * @param items list of items whose price details needs to be fetched
     * @return list of price details
     * @throws BBBBusinessException exception occurs in case any issue with input parameters
     * @throws BBBSystemException exception occurs in case of unexpected error occurred */
    public final List<ItemPriceVO> getPriceItems(final Map<String, String> items)
                    throws BBBBusinessException, BBBSystemException {

        this.logDebug("BBBPricingManager.getPriceItems : START");
        List<ItemPriceVO> output;

        if ((items != null) && !items.isEmpty()) {

            final PriceDroplet priceDroplet = (PriceDroplet) ServletUtil.getCurrentRequest().resolveName(
                            "/atg/commerce/pricing/priceLists/PriceDroplet");
            final DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
            final DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
            output = new ArrayList<ItemPriceVO>();
            this.logDebug("Start Iterating for items");
            RepositoryItem listPriceRepositoryItem = null;
            RepositoryItem salePriceRepositoryItem = null;

            for (final String key : items.keySet()) {
                this.logDebug("Fetching price details of item :" + key);
                final ItemPriceVO price = new ItemPriceVO();
                double listPrice = 0.0;
                double salePrice = 0.0;
                request.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, null);
                request.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, null);

                try {
                    request.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, key);
                    this.logDebug("Calling price droplet for fetching list price of :" + key);
                    priceDroplet.service(request, response);

                    price.setSkuId(key);

                    if (request.getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE) != null) {
                        this.logDebug("Setting list price details");
                        listPriceRepositoryItem = (RepositoryItem) request
                                        .getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE);
                        listPrice = ((Double) listPriceRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
                                        .doubleValue();
                        price.setListPrice(listPrice);
                    }

                    final RepositoryItem userProfile = ServletUtil.getCurrentUserProfile();
                    request.setParameter(BBBCatalogConstants.SKU_ATTRIBUTE, key);
                    request.setParameter(BBBCatalogConstants.PRICE_LIST_ATTRIBUTE, userProfile
                                    .getPropertyValue(BBBCatalogConstants.SALE_PRICE_LIST_PRICING_PROPERTY_NAME));
                    this.logDebug("Calling price droplet for fetching sale price of :" + key);
                    priceDroplet.service(request, response);

                    if (request.getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE) != null) {
                        this.logDebug("Setting sale price details");
                        salePriceRepositoryItem = (RepositoryItem) request
                                        .getObjectParameter(BBBCatalogConstants.PRICE_ATTRIBUTE);
                        salePrice = ((Double) salePriceRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
                                        .doubleValue();
                        price.setSalePrice(salePrice);
                    }

                    if (!BBBUtility.isEmpty(items.get(key))) {
                        this.logDebug("Calculating total price");
                        int quantity = 0;
                        try {
                            quantity = Integer.parseInt(items.get(key));
                        } catch (final NumberFormatException nfe) {
                            throw new BBBBusinessException(BBBCatalogErrorCodes.PRICEING_EXCEPTION_DROPLET_INPUT_ERROR,
                                            "Error parsing quantity");
                        }
                        if (quantity < 0) {
                            quantity = 0;
                        }
                        price.setQuantity(quantity);
                        if (salePriceRepositoryItem != null) {
                            price.setTotalPrice(salePrice * quantity);
                        } else {
                            price.setTotalPrice(listPrice * quantity);
                        }
                    } else {
                        this.logDebug("Quantity is null");
                        price.setTotalPrice(0.0);
                    }

                    output.add(price);

                } catch (final ServletException e) {
                    throw new BBBSystemException(BBBCatalogErrorCodes.PRICEING_EXCEPTION_DROPLET_SERVLET_EXCEPTION,
                                    EXCEPTION_OCCURRED_WHILE_FETCHING_PRICE);
                } catch (final IOException e) {
                    throw new BBBSystemException(BBBCatalogErrorCodes.PRICEING_EXCEPTION_DROPLET_IO_EXCEPTION,
                                    EXCEPTION_OCCURRED_WHILE_FETCHING_PRICE);
                }

            }

        } else {
            throw new BBBBusinessException(BBBCatalogErrorCodes.PRICEING_EXCEPTION_DROPLET_INPUT_ERROR,
                            "Mandatory parameter missing");
        }

        this.logDebug("BBBPricingManager.getPriceItems : END");
        return output;

    }

    /** The method gets the List price of the SKU based on site price List.
     *
     * @param siteName Site Name
     * @param productId Product ID
     * @param skuId SKU ID
     * @return List Price
     * @throws BBBSystemException Exception */
    public double getListPriceBySite(final String siteName, final String productId, final String skuId)
                    throws BBBSystemException {

        this.logDebug("BVFeedTools Method : getListPriceBySite start");
        double listPrice = 0.0;
        RepositoryItem siteRepItem = null;
        // RepositoryItem profile = ServletUtil.getCurrentUserProfile();
        try {
            RepositoryItem priceList = null;
            siteRepItem = this.getSiteManager().getSite(siteName);
            try {
                priceList = this.getPriceListManager().getPriceListForSite(siteRepItem, "defaultListPriceList");
            } catch (final RepositoryException e) {
                this.logError("Price List not configured for site" + siteRepItem.getRepositoryId());
            }
            if (priceList != null) {
                final RepositoryItem price = this.getPriceListManager().getPrice(priceList, productId, skuId);
                if (price != null) {
                    listPrice = ((Double) price.getPropertyValue(BBBCatalogConstants.LIST_PRICE_PRICING_PROPERTY_NAME))
                                    .doubleValue();
                }
            }
        } catch ( PriceListException e) {

            throw new BBBSystemException(BBBCoreErrorConstants.FEED_PRICE_LIST_EXCEPTION,
                            "PriceListException while retrieving List Price " + e);
        } catch ( RepositoryException e) {
          //  e.printStackTrace();
        	logError(e.getMessage(),e);
        }
            this.logDebug("Listprice of SKU : " + skuId + " is : " + listPrice);
        this.logDebug("BVFeedTools Method : getListPriceBySite end");
        return listPrice;
    }
    
    /**
	 * This method takes the BBBCommerceItem and get its priceInfo object and
	 * sets into the PriceInfoVO.
	 * 
	 * @param BBBCommerceItem
	 * @return PriceInfoVO
	 */
	@SuppressWarnings("unchecked")
    public PriceInfoVO getItemPriceInfo(final BBBCommerceItem item) {
		
		
		PriceInfoVO priceInfoVO = new PriceInfoVO();
		long undiscountedItemsCount = 0;
		
		List<PromotionVO> itemPromotionVOList = priceInfoVO.getItemPromotionVOList();
		
		if (item != null) {
			
			double savedAmount = 0.0;
			double savedPercentage = 0.0;
			double savedUnitAmount = 0.0;
			double savedUnitPercentage = 0.0;
			
			priceInfoVO.setItemCount((int) item.getQuantity());
			ItemPriceInfo priceInfo = item.getPriceInfo();
			if(priceInfo == null) { 
			    return priceInfoVO;
			}
            priceInfoVO.setRawAmount(priceInfo.getRawTotalPrice());
			priceInfoVO.setTotalAmount(priceInfo.getAmount());
			priceInfoVO.setUnitListPrice(priceInfo.getListPrice());
			
			if (priceInfo.getSalePrice() > 0) {
				
				savedAmount = BigDecimal.valueOf(priceInfo.getListPrice()).multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue() - priceInfo.getAmount();				
				priceInfoVO.setTotalSavedAmount(savedAmount);
				priceInfoVO.setUnitSalePrice(priceInfo.getSalePrice());
				savedUnitAmount = priceInfo.getListPrice() - priceInfo.getSalePrice();
				priceInfoVO.setUnitSavedAmount(savedUnitAmount);
			}
			if (savedAmount > 0) {
				
				savedPercentage  = getPricingTools().round(BigDecimal.valueOf(savedAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / BigDecimal.valueOf(priceInfo.getListPrice()).multiply(BigDecimal.valueOf(item.getQuantity())).doubleValue(), 2);
				priceInfoVO.setTotalSavedPercentage(savedPercentage);
				savedUnitPercentage = getPricingTools().round(BigDecimal.valueOf(savedUnitAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / priceInfo.getListPrice(), 2);
				priceInfoVO.setUnitSavedPercentage(savedUnitPercentage);
			}

			priceInfoVO.setTotalDiscountShare(priceInfo.getOrderDiscountShare());			
		
			
			List<UnitPriceBean> priceBeans= getPricingTools().generatePriceBeans(priceInfo.getCurrentPriceDetails());
			for(UnitPriceBean unitPriceBean : priceBeans) {
				if (unitPriceBean.getPricingModels() == null || unitPriceBean.getPricingModels().isEmpty()) {
					undiscountedItemsCount += unitPriceBean.getQuantity();
				}
			}
			priceInfoVO.setUndiscountedItemsCount(undiscountedItemsCount);
			priceInfoVO.setPriceBeans(priceBeans);		

			
			priceInfoVO.setItemPromotionVOList(itemPromotionVOList);
			priceInfoVO.setUndiscountedItemsCount(undiscountedItemsCount);
		}
		return priceInfoVO;
	}
	
	/**
	 * This method takes the shipping group object and get its priceInfo object
	 * and sets into the PriceInfoVO. It sets the giftWrap price and will be
	 * used in order review page.
	 * 
	 * @param Order
	 * @return PriceInfoVO
	 */
	@SuppressWarnings("unchecked")
    public PriceInfoVO getShippingPriceInfo(final ShippingGroup shippingGroup, final OrderImpl pOrder) {
		final PriceInfoVO priceInfoVO = new PriceInfoVO();
		if (shippingGroup != null) {
			List<ShippingGroupCommerceItemRelationship> commerceItemRelationshipList = shippingGroup.getCommerceItemRelationships();
			double giftWrapPrice = 0.0;
			double ecoFee = 0.0;
			TaxPriceInfo shippingTaxInfo = null;
			long itemCount = 0;
			double shippingGroupItemsSavedAmount = 0.0;
			double shippingGroupItemsTotalListPrice = 0.0;
			double shippingGroupSavedPercentage = 0.0;
			double totalAmount = 0.0;
			double shippingGroupItemsTotal = 0.0;
			double surchargeSavings = 0.0;
			double shippingSavings = 0.0;
			
			for (ShippingGroupCommerceItemRelationship commerceItemRelationship : commerceItemRelationshipList) {
				CommerceItem commerceItem = commerceItemRelationship.getCommerceItem();
				if (commerceItem instanceof BBBCommerceItem && commerceItem.getPriceInfo() != null) {
					itemCount += commerceItemRelationship.getQuantity();
					Range relationshipRange = commerceItemRelationship.getRange();
					List<UnitPriceBean> priceBeans= getPricingTools().generatePriceBeans(commerceItemRelationship.
														getCommerceItem().getPriceInfo().getCurrentPriceDetailsForRange(relationshipRange));
					for(UnitPriceBean unitPriceBean : priceBeans) {
						shippingGroupItemsTotal += BigDecimal.valueOf(unitPriceBean.getUnitPrice()).multiply(BigDecimal.valueOf(unitPriceBean.getQuantity())).doubleValue();						
					}
					
					double itemSavedAmount = commerceItemRelationship.getRawtotalByAverage() - commerceItemRelationship.getAmountByAverage();
					shippingGroupItemsSavedAmount += itemSavedAmount;

					shippingGroupItemsTotalListPrice += BigDecimal.valueOf(commerceItem.getPriceInfo().getListPrice()).multiply(BigDecimal.valueOf(itemCount)).doubleValue() ;

				}
				if (commerceItem != null && commerceItem.getPriceInfo() != null && commerceItem instanceof GiftWrapCommerceItem) {
					giftWrapPrice += commerceItem.getPriceInfo().getAmount();

				}
				
				if (commerceItem != null && commerceItem.getPriceInfo() != null && commerceItem instanceof EcoFeeCommerceItem) {
					ecoFee = ecoFee + commerceItem.getPriceInfo().getAmount();
				}
			}
			
			if (pOrder != null && pOrder.getTaxPriceInfo() != null && pOrder.getTaxPriceInfo().getShippingItemsTaxPriceInfos() != null) {
				shippingTaxInfo = (TaxPriceInfo) pOrder.getTaxPriceInfo().getShippingItemsTaxPriceInfos().get(shippingGroup.getId());
				
				if (shippingTaxInfo != null) {
					priceInfoVO.setShippingLevelTax(shippingTaxInfo.getAmount());
					priceInfoVO.setShippingCountyLevelTax(shippingTaxInfo.getCountyTax());
					priceInfoVO.setShippingStateLevelTax(shippingTaxInfo.getStateTax());
					totalAmount += shippingTaxInfo.getAmount();
				}
			} else {
				priceInfoVO.setShippingCountyLevelTax(0);
				priceInfoVO.setShippingStateLevelTax(0);
				priceInfoVO.setShippingLevelTax(0);
			}
			
			getPricingTools().fillAdjustments(priceInfoVO, shippingGroup);
			
			if (shippingGroupItemsTotalListPrice > 0) {
				shippingGroupSavedPercentage = getPricingTools()
						.round(BigDecimal.valueOf(shippingGroupItemsSavedAmount).multiply(BigDecimal.valueOf(100)).doubleValue() / shippingGroupItemsTotalListPrice,
								2);
			}
			priceInfoVO.setTotalSavedPercentage(shippingGroupSavedPercentage);
			priceInfoVO.setGiftWrapTotal(giftWrapPrice);
			priceInfoVO.setEcoFeeTotal(ecoFee);
			BBBShippingPriceInfo shippingPriceInfo = (BBBShippingPriceInfo) shippingGroup.getPriceInfo();
			if (shippingPriceInfo != null) {
				priceInfoVO.setTotalSurcharge(shippingPriceInfo.getSurcharge());
				priceInfoVO.setTotalShippingAmount(shippingPriceInfo.getFinalShipping());
				priceInfoVO.setRawShippingTotal(shippingPriceInfo.getRawShipping());
				totalAmount += shippingPriceInfo.getFinalShipping() + shippingPriceInfo.getSurcharge();
//				surchargeSavings += shippingPriceInfo.getSurcharge() - shippingPriceInfo.getFinalSurcharge();
				shippingSavings+= shippingPriceInfo.getRawShipping() - shippingPriceInfo.getFinalShipping(); 
			}
			totalAmount += shippingGroupItemsTotal;
			totalAmount += giftWrapPrice;
			totalAmount += ecoFee;
			priceInfoVO.setItemCount((int) itemCount);
			priceInfoVO.setTotalSavedAmount(shippingGroupItemsSavedAmount);
			priceInfoVO.setShippingGroupItemsTotal(shippingGroupItemsTotal);
			priceInfoVO.setTotalAmount(totalAmount);
			priceInfoVO.setSurchargeSavings(surchargeSavings);
			priceInfoVO.setShippingSavings(shippingSavings);
			
            priceInfoVO.setFinalShippingCharge(priceInfoVO.getRawShippingTotal() - priceInfoVO.getShippingSavings());

			// LTL : Shipping level delivery, assembly and surcharge savings
			Double shippingDeliverySurcharge = 0.0;
			Double shippingDeliverySurchargeSaving = 0.0;
			Double shippingAssemblyFee = 0.0;
			if(shippingGroup instanceof BBBHardGoodShippingGroup){
				BBBHardGoodShippingGroup bbbShippingGroup = (BBBHardGoodShippingGroup) shippingGroup;
				//Retrieve relationship of LTL commerce item with its assembly and delivery surcharge commerce items.
				List<BBBShippingGroupCommerceItemRelationship> sgciRelList = shippingGroup.getCommerceItemRelationships();
				for(BBBShippingGroupCommerceItemRelationship sgciRel : sgciRelList){
					if(sgciRel.getCommerceItem() instanceof BBBCommerceItem){
						if(((BBBCommerceItem)sgciRel.getCommerceItem()).isLtlItem()){
							PriceInfoVO ciPriceInfoVo = new PriceInfoVO();
							String commerceItem = sgciRel.getCommerceItem().getId();
							ciPriceInfoVo = getCommerceItemManager().getLTLItemPriceInfo(commerceItem,ciPriceInfoVo, pOrder);
							shippingDeliverySurcharge+=ciPriceInfoVo.getDeliverySurcharge();
							shippingDeliverySurchargeSaving+=ciPriceInfoVo.getDeliverySurchargeSaving();
							shippingAssemblyFee+=ciPriceInfoVo.getAssemblyFee();
						}else{
							break;
						}
					}
				}
				totalAmount = totalAmount + shippingDeliverySurcharge - shippingDeliverySurchargeSaving + shippingAssemblyFee;
			}
			priceInfoVO.setTotalAmount(totalAmount);
			priceInfoVO.setTotalDeliverySurcharge(shippingDeliverySurcharge);
			priceInfoVO.setDeliverySurchargeSaving(shippingDeliverySurchargeSaving);
			priceInfoVO.setAssemblyFee(shippingAssemblyFee);
			
		}
		return priceInfoVO;
	}
	
	@SuppressWarnings("unchecked")
    public PriceInfoVO getShippingGroupCommerceItemPriceInfo(final ShippingGroupCommerceItemRelationship shippingGroupCommerceItemRelationship) {			
		PriceInfoVO priceInfoVO = new PriceInfoVO();
		long undiscountedItemsCount = 0;
		double shippingGroupItemTotal = 0;
		Range relationshipRange = shippingGroupCommerceItemRelationship.getRange();
		List<UnitPriceBean> priceBeans= getPricingTools().generatePriceBeans(shippingGroupCommerceItemRelationship.
											getCommerceItem().getPriceInfo().getCurrentPriceDetailsForRange(relationshipRange));
		for(UnitPriceBean unitPriceBean : priceBeans) {
			shippingGroupItemTotal += BigDecimal.valueOf(unitPriceBean.getUnitPrice()).multiply(BigDecimal.valueOf(unitPriceBean.getQuantity())).doubleValue();
			if (unitPriceBean.getPricingModels() == null || unitPriceBean.getPricingModels().isEmpty()) {
				undiscountedItemsCount += unitPriceBean.getQuantity();
			}
		}
		priceInfoVO.setUndiscountedItemsCount(undiscountedItemsCount);
		priceInfoVO.setPriceBeans(priceBeans);
		priceInfoVO.setShippingGroupItemTotal(shippingGroupItemTotal);
		return priceInfoVO;
	}
}
