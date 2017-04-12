package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.profile.CommerceProfileTools;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.droplet.BBBPromotionTools;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;

/**
 * @author hkapo1 rsain4
 * @version $Revision: #1 $
 */
public class BBBOrderRKGInfo extends BBBDynamoServlet {

	private BBBCatalogTools mCatalogTools;
	private BBBPricingTools mPricingTools = null;
	private Profile profile;

	private static final String COMMA = ",";
	private static final String BBBCOUPONS = "bbbCoupons";
	private static final String DISPLAYNAME = "displayName";
	
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}
	
	/**
	 * @return the pricingTools
	 */
	public final BBBPricingTools getPricingTools() {
		return mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public final void setPricingTools(BBBPricingTools pPricingTools) {
		mPricingTools = pPricingTools;
	}
	
	/**
	 * @return the profile
	 */
	public Profile getProfile() {
		return profile;
	}

	/**
	 * @param profile the profile to set
	 */
	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	/**
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@SuppressWarnings("unchecked")
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException { 
		
		BBBPerformanceMonitor.start("BBBOrderInfoDroplet", "service");
		logDebug("CLS=[BBBOrderInfoDroplet] MTHD=[Service starts]");
		ProductVO prodVO=null;
		Collection<CategoryVO> categoryVO=null;
		final Object order = pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		final StringBuilder productIds = new StringBuilder();
		final StringBuilder categoryNamesL1 = new StringBuilder();
		final StringBuilder categoryNamesL2 = new StringBuilder();
		final StringBuilder categoryNamesL3 = new StringBuilder();
		final StringBuilder categoryIdsL1 = new StringBuilder();
		final StringBuilder itemNames = new StringBuilder();
		int skuQuantity=0;
		String prodCategoryId =null;
		String prodCategoryName = null;
		
		//double grandTotal = 0.0;
		if (order != null) {
			
			final String siteId = ((BBBOrder) order).getSiteId();
			final int listSize = ((BBBOrder) order).getCommerceItemCount();
			String promoStr = createPromoStr(order, getProfile());
			Set<RepositoryItem> parentCategorySet =null;
			int count=1;
			for (ShippingGroup sg : (List<ShippingGroup>) ((BBBOrder) order)
					.getShippingGroups()) {

				for (ShippingGroupCommerceItemRelationship cisiRel : (List<ShippingGroupCommerceItemRelationship>) sg
						.getCommerceItemRelationships()) {
					
					List<String> catList=new ArrayList();
					CommerceItem commerceItem = cisiRel.getCommerceItem();
					if (!(commerceItem instanceof BBBCommerceItem)) {
						continue;
					}
					final String itemName = (String) ((RepositoryItem) commerceItem
							.getAuxiliaryData().getCatalogRef())
							.getPropertyValue(DISPLAYNAME);
					itemNames.append(itemName);
					String productId = commerceItem.getAuxiliaryData()
							.getProductId();
					productIds.append(productId);
					skuQuantity= (int) (skuQuantity+commerceItem.getQuantity());
					if (productId != null) {
						try {
							RepositoryItem productRepositoryItem = ((BBBCatalogToolsImpl) getCatalogTools())
									.getCatalogRepository()
									.getItem(
											productId,
											BBBCatalogConstants.PRODUCT_ITEM_DESCRIPTOR);
							if (productRepositoryItem != null) {
								logDebug("productRepositoryItem is not null for product id "
										+ productId);
								parentCategorySet = (Set<RepositoryItem>) productRepositoryItem
										.getPropertyValue(BBBCatalogConstants.PARENT_CATEGORIES_PROPERTY_NAME);
							}
						} catch (RepositoryException e1) {
							// TODO Auto-generated catch block
							//e1.printStackTrace();
							logError(e1.getMessage(),e1);
						}
						if (parentCategorySet != null && !parentCategorySet.isEmpty()) {
							try {
								Map<String, CategoryVO> parents = getCatalogTools()
										.getParentCategoryForProduct(productId,
												siteId);
								if (!parents.isEmpty()) {
									Set<String> key = parents.keySet();
									int cat1 = 1;
									for (Iterator i = key.iterator(); i
											.hasNext();) {
										String keyValue = (String) i.next();
										if (cat1 == 1) {
											prodCategoryId = parents.get(
													keyValue).getCategoryId();
											categoryIdsL1
													.append(prodCategoryId);
											cat1++;
										}
									}
									for (Iterator i = key.iterator(); i
											.hasNext();) {
										String keyValue = (String) i.next();
										prodCategoryName = parents
												.get(keyValue)
												.getCategoryName();
										catList.add(prodCategoryName);
									}
									for (int i = 0; i < 3; i++) {
										if (i == 0) {
											if (i < catList.size()) {
												categoryNamesL1.append(catList
														.get(i));
											} else {
												categoryNamesL1.append("null");
											}
										}
										if (i == 1) {
											if (i < catList.size()) {
												categoryNamesL2.append(catList
														.get(i));
											} else {
												categoryNamesL2.append("null");
											}
										}
										if (i == 2) {
											if (i < catList.size()) {
												categoryNamesL3.append(catList
														.get(i));
											} else {
												categoryNamesL3.append("null");
											}
										}
									}
								}
							} catch (BBBSystemException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								logError(e.getMessage(),e);
							} catch (BBBBusinessException e) {
								// TODO Auto-generated catch block
								//e.printStackTrace();
								logError(e.getMessage(),e);
							}
						}else{
							categoryIdsL1.append("null");
							categoryNamesL1.append("null");
							categoryNamesL2.append("null");
							categoryNamesL3.append("null");	
							}
						}
					if (count < listSize) {
						itemNames.append(BBBCoreConstants.COMMA);
						productIds.append(BBBCoreConstants.COMMA);
						if (categoryNamesL1.length() > 0) {
							categoryNamesL1.append(BBBCoreConstants.COMMA);
							categoryIdsL1.append(BBBCoreConstants.COMMA);
						}
						if (categoryNamesL2.length() > 0) {
							categoryNamesL2.append(BBBCoreConstants.COMMA);
						}
						if (categoryNamesL3.length() > 0) {
							categoryNamesL3.append(BBBCoreConstants.COMMA);
						}
						count++;
					}

				}
			}
			if(((BBBOrder) order).getPriceInfo()!=null)
			{
				double val=((BBBOrder) order).getPriceInfo().getRawSubtotal();
				BigDecimal number = BigDecimal.valueOf(val);
				number = number.setScale(2, RoundingMode.DOWN);
				String pretax=number.toString();
				pretax=pretax.replace(",", "");
				pRequest.setParameter(BBBCoreConstants.RKG_PRETAX_TOTAL, pretax);
			}
			BBBPerformanceMonitor.end("BBBOrderInfoDroplet", "service");
			pRequest.setParameter(BBBCoreConstants.RKG_PRODUCT_NAMES, itemNames.toString());
			pRequest.setParameter(BBBCoreConstants.RKG_PRODUCT_IDS, productIds.toString());
			pRequest.setParameter(BBBCoreConstants.RKG_PRODUCT_COUNT, skuQuantity);
			pRequest.setParameter(BBBCoreConstants.RKG_PROMOTIONS, promoStr);
			pRequest.setParameter(BBBCoreConstants.RKG_PROD_CATEGORYID_L1, categoryIdsL1.toString());
			pRequest.setParameter(BBBCoreConstants.RKG_PROD_CATEGORY_NAME_L1, categoryNamesL1.toString());
			pRequest.setParameter(BBBCoreConstants.RKG_PROD_CATEGORY_NAME_L2, categoryNamesL2.toString());
			pRequest.setParameter(BBBCoreConstants.RKG_PROD_CATEGORY_NAME_L3, categoryNamesL3.toString());
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest, pResponse);
			}
			
		}
	
	
/*	private String getCurrentSiteId(final DynamoHttpServletRequest pRequest) {
		String siteId = SiteContextManager.getCurrentSiteId();
		if (siteId == null) {
			siteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
		}
		logDebug("siteId: " + siteId);
		return siteId;
	}*/
	
	
	
	private String createPromoStr(final Object order, final Profile profile) {

		List<String> promotionStringList = new ArrayList<String>();
		final StringBuilder itemPromotionString = new StringBuilder();
		final StringBuilder orderPromotionString = new StringBuilder();
		final StringBuilder shipPromotionString = new StringBuilder();
		
		/*BBBH-3026 Code Change for fetching promotions from claimable repository not from BBBCoupons Property*/
		List<RepositoryItem>  couponRepositoryItem = this.getPromotionTools().getCouponListFromOrder((BBBOrderImpl)order);
		boolean firstItem = true , firstItemOrder = true , firstItemShip = true;


		for (RepositoryItem repositoryItem : couponRepositoryItem) {
				try {
				RepositoryItem []	currentPromos =  this.getCatalogTools().getPromotions(repositoryItem.getRepositoryId());//(RepositoryItem) ((Set) repositoryItem.getPropertyValue("promotions")).iterator().next();//this.getClaimableManager().getClaimableTools().fetchCouponPromotions(coupon);
				RepositoryItem currentPromo = null;
				if(currentPromos != null && currentPromos.length >0){
					currentPromo= currentPromos[0];
				}
			if(currentPromo!= null)
				{
				logDebug("Current Promotions:" + currentPromo);
				
						if(currentPromo.getItemDescriptor().getItemDescriptorName()
						.equalsIgnoreCase(BBBCoreConstants.ITEM_DISCOUNT)){
							if(!firstItem){
								itemPromotionString.append(COMMA);
							}
							itemPromotionString.append(String.valueOf(currentPromo
									.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME)));
							firstItem = false;
						}
	                  else if(currentPromo.getItemDescriptor().getItemDescriptorName()
						.equalsIgnoreCase(BBBCoreConstants.ORDER_DISCOUNT)){
						if(!firstItemOrder){
							orderPromotionString.append(COMMA);
						}
						orderPromotionString.append(String.valueOf(currentPromo
								.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME)));
						firstItemOrder = false;
					}
					else if(currentPromo.getItemDescriptor().getItemDescriptorName()
					.equalsIgnoreCase(BBBCoreConstants.SHIP_DISCOUNT))
					{
						if(!firstItemShip){
							orderPromotionString.append(COMMA);
						}
						shipPromotionString.append(String.valueOf(currentPromo
								.getPropertyValue(BBBCoreConstants.PROMO_DISPLAY_NAME)));
						firstItemShip = false;
					}
					}
				} catch (RepositoryException e) {
					
					logError("Error occured while fetching coupons in omniture",e);
				}
				catch (BBBSystemException e) {
					
					logError("BBBSystem Exception occured while fetching coupons in omniture",e);
				} catch (BBBBusinessException e) {
					
					logError("BBBBusiness Exception occured while fetching coupons in omniture",e);
				}
		
	}

	if(!firstItem && orderPromotionString.length()>0){
		 orderPromotionString.insert(0, COMMA);
	}
	if(!firstItemOrder && shipPromotionString.length()>0){
		 orderPromotionString.insert(0, COMMA);
	}
	promotionStringList.add(itemPromotionString.toString());
	promotionStringList.add(orderPromotionString.toString());
	promotionStringList.add(shipPromotionString.toString());
	String promotionString = promotionStringList.get(0) + promotionStringList.get(1) + promotionStringList.get(2);
	return promotionString;
	}


private BBBPromotionTools mPromotionTools;

public BBBPromotionTools getPromotionTools() {
	return mPromotionTools;
}

public void setPromotionTools(BBBPromotionTools pPromotionTools) {
	this.mPromotionTools = pPromotionTools;
}

}
