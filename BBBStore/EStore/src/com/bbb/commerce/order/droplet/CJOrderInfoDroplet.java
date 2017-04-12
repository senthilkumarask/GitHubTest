package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderImpl;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.OrderInfoVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;

/**
 * @author hkapo1 rsain4
 * @version $Revision: #1 $
 */
public class CJOrderInfoDroplet extends BBBDynamoServlet {

	private BBBCatalogTools mCatalogTools;
	private BBBPricingTools mPricingTools = null;


	private static final String QTY_CAPITAL = "QTY";
	private static final String AMT = "AMT";
	private static final String ITEM = "ITEM";


	private static final String DISPLAYNAME = "displayName";

	/**
	 * @return the catalogTools
	 */
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
		
		final Object order = pRequest
				.getObjectParameter(BBBCoreConstants.ORDER);
		final StringBuilder productIds = new StringBuilder();
		final StringBuilder itemIds = new StringBuilder();
		final StringBuilder itemQtys = new StringBuilder();
		final StringBuilder itemprices = new StringBuilder();
		final StringBuilder itemAmounts = new StringBuilder();
		
		final StringBuilder itemSkuIds = new StringBuilder();
		final StringBuilder itemQuantities = new StringBuilder();
		final StringBuilder itemAmts = new StringBuilder();
		final StringBuilder cItemIds = new StringBuilder();
		final StringBuilder itemSkuNames = new StringBuilder();
			
		final StringBuilder itemNames = new StringBuilder();
		final StringBuilder registryIds = new StringBuilder();
		final StringBuilder storeIds = new StringBuilder();
		double grandTotal = 0.0; 
		DecimalFormat df = new DecimalFormat("#.##");
				
		if (order != null) {

			final String siteId = getCurrentSiteId(pRequest);
			final String orderId = ((BBBOrder) order).getId();
			final Date orderDate = ((BBBOrder) order).getSubmittedDate();

			int listSize = 0;
			int merchandizibleCommerceItemCount = 0 ;
			int count = 1;

			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[orderId="+orderId
					+" orderDate="+orderDate
					+" commerceItemsSize ="+listSize);
			for (CommerceItem commerceItem : (List<CommerceItem>) ((BBBOrder) order)
					.getCommerceItems()) {
				if(!(commerceItem instanceof NonMerchandiseCommerceItem)){
					String storeId= ((BBBCommerceItem) commerceItem).getStoreId();
					if(storeId == null){
						listSize ++;
						}
					}
			}
			for (CommerceItem commerceItem : (List<CommerceItem>) ((BBBOrder) order)
					.getCommerceItems()) {
				//If EcoFee or GiftWrap items then skip
				if(!(commerceItem instanceof NonMerchandiseCommerceItem)){
					String storeId= ((BBBCommerceItem) commerceItem).getStoreId();
					if(storeId == null){
					final String itemName = (String) ((RepositoryItem) commerceItem
							.getAuxiliaryData().getCatalogRef())
							.getPropertyValue(DISPLAYNAME);
					
					storeIds.append(storeId);
					itemIds.append(commerceItem.getCatalogRefId());
					productIds.append(commerceItem.getAuxiliaryData()
							.getProductId());
					itemNames.append(itemName);
					itemSkuIds.append(commerceItem.getCatalogRefId());
					itemQuantities.append(commerceItem.getQuantity());
					cItemIds.append(commerceItem.getId());
					itemSkuNames.append(itemName);
					if (count < listSize) {
						storeIds.append(BBBCoreConstants.SEMICOLON);
						itemIds.append(BBBCoreConstants.SEMICOLON);
						productIds.append(BBBCoreConstants.SEMICOLON);
						itemNames.append(BBBCoreConstants.SEMICOLON);
						itemSkuIds.append(BBBCoreConstants.PIPE_SYMBOL);
						itemQuantities.append(BBBCoreConstants.PIPE_SYMBOL);
						cItemIds.append(BBBCoreConstants.PIPE_SYMBOL);
						itemSkuNames.append(BBBCoreConstants.PIPE_SYMBOL);
					}
					itemQtys.append(commerceItem.getQuantity());
					if (count < listSize) {
						itemQtys.append(BBBCoreConstants.SEMICOLON);
					}
					if(commerceItem.getPriceInfo() != null) {
						if ( commerceItem.getPriceInfo().getSalePrice() > 0.0) {
							itemAmounts.append(df.format(commerceItem.getPriceInfo()
									.getSalePrice()*commerceItem.getQuantity()));
							/*itemprices.append(commerceItem.getPriceInfo()
									.getSalePrice());*/
						} else {
							itemAmounts.append(df.format(commerceItem.getPriceInfo()
									.getListPrice()*commerceItem.getQuantity()));
							/*itemprices.append(commerceItem.getPriceInfo()
									.getListPrice());*/
						}
						
						itemprices.append(
								df.format(commerceItem.getPriceInfo().getAmount()/commerceItem.getQuantity()));
						itemAmts.append(
								df.format(commerceItem.getPriceInfo().getAmount()/commerceItem.getQuantity()));
						
						if (count < listSize) {
							itemprices.append(BBBCoreConstants.SEMICOLON);
							itemAmounts.append(BBBCoreConstants.SEMICOLON);
							itemAmts.append(BBBCoreConstants.PIPE_SYMBOL);
						}
	

	
						grandTotal += commerceItem.getPriceInfo().getAmount();
					}
					merchandizibleCommerceItemCount++;
					count++;
				}
			}
				
			}
			
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[itemIds="+itemIds
					+" skuQty="+itemQtys
					+" skuPrice ="+itemprices
					+" skuName="+itemNames
					+" skuAmounts="+itemAmounts+"storeIds="+storeIds);
			
			
			final String[] prodIds = productIds.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] storeNums = storeIds.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] skuIds = itemIds.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] skuQty = itemQtys.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] skuPrice = itemprices.toString().split(
					BBBCoreConstants.SEMICOLON);
			
			final String[] skuName = itemNames.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] regIds = registryIds.toString().split(
					BBBCoreConstants.SEMICOLON);

			final List<String> cjItemUrl = new ArrayList<String>();
			final List<OrderInfoVO> orderList = new ArrayList<OrderInfoVO>();
			OrderInfoVO orderInfoVO = null;

			for (int i = 0; i < merchandizibleCommerceItemCount; i++) {
				String reg =null;
				if(i < regIds.length) {
					reg = regIds[i];
				}
				final String cjUrl = createCJURL(i, skuIds[i], skuQty[i],
						skuPrice[i]);

				logDebug("skuIds[i]" + skuIds[i]);
				logDebug("skuPrice[i]" + skuPrice[i]);
				logDebug("skuQty[i]" + skuQty[i]);
				logDebug("skuName[i]" + skuName[i]);

				cjItemUrl.add(cjUrl);	
			}
			
			if(cjItemUrl.size()>0){
				pRequest.setParameter(BBBCoreConstants.CJ_ITEM_URL, cjItemUrl);
				pRequest.setParameter(BBBCoreConstants.CJ_SKUIDS, itemSkuIds.toString());
				pRequest.setParameter(BBBCoreConstants.CJ_SKUPRICES, itemAmts.toString());
				pRequest.setParameter(BBBCoreConstants.CJ_SKUQTY, itemQuantities.toString());
				pRequest.setParameter(BBBCoreConstants.CJ_BOPUS_ONLY_ORDER, "false");
			}else{
				pRequest.setParameter(BBBCoreConstants.CJ_BOPUS_ONLY_ORDER, "true");
			}
			
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,
					pResponse);
			
			pRequest.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
					getPricingTools().getOrderPriceInfo((OrderImpl) order));
			
		}

		logDebug("CLS=[BBBOrderInfoDroplet] MTHD=[Service ends]");
		BBBPerformanceMonitor.end("BBBOrderInfoDroplet", "service");
	}

	
	private String createCJURL(final int lineNumber, final String skuId,
			final String quantity, final String price) {
		final StringBuilder url = new StringBuilder();
		url.append(ITEM);
		url.append(lineNumber + 1);
		url.append(BBBCoreConstants.EQUAL);
		url.append(skuId);
		url.append(BBBCoreConstants.AMPERSAND);
		url.append(AMT);
		url.append(lineNumber + 1);
		url.append(BBBCoreConstants.EQUAL);
		url.append(price);
		url.append(BBBCoreConstants.AMPERSAND);
		url.append(QTY_CAPITAL);
		url.append(lineNumber + 1);
		url.append(BBBCoreConstants.EQUAL);
		url.append(quantity);
		return url.toString();	
	}

	/**
	 * This method return Current Site Id
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @return siteId
	 */
	private String getCurrentSiteId(final DynamoHttpServletRequest pRequest) {
		String siteId = extractGetSiteId();
		if (siteId == null) {
			siteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
		}
		logDebug("siteId: " + siteId);
		return siteId;
	}
   
	/**
	 *  use to get the site id 
	 * @return site id
	 */
	protected String extractGetSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
}
