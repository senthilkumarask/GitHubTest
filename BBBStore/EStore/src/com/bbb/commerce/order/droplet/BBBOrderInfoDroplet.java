package com.bbb.commerce.order.droplet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.PricingAdjustment;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.pricing.BBBPricingTools;
import com.bbb.commerce.pricing.bean.OrderInfoVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.utils.BBBUtility;

/**
 * @author hkapo1 rsain4
 * @version $Revision: #1 $
 */
public class BBBOrderInfoDroplet extends BBBDynamoServlet {

	private BBBCatalogTools mCatalogTools;
	private BBBPricingTools mPricingTools = null;

	private static final String ITEM_COUNT = "itemCount";
	private static final String ITEMPRICES2 = "itemprices";
	private static final String ITEM_AMOUNTS = "itemAmounts";
	private static final String ITEM_QTYS = "itemQtys";
	private static final String ITEM_IDS = "itemIds";
	private static final String ORDER_LIST = "orderList";
	private static final String QTY_CAPITAL = "QTY";
	private static final String AMT = "AMT";
	private static final String ITEM = "ITEM";
	private static final String PRC = "prc";
	private static final String QTY_SMALL = "qty";
	private static final String SKU = "sku";
	private static final String DSC = "desc";	
	private static final String REG = "reg";
	private static final String LINE = "line";
	private static final String ZMAS = "1";
	private static final String ITEM_SKU_IDS = "itemSkuIds";
	private static final String ITEM_QUANTITIES = "itemQuantities";
	private static final String ITEM_AMTS = "itemAmts";
	private static final String ITEM_SKU_NAMES = "itemSkuNames";
	private static final String CITEM_IDS = "cItemIds";

	// RKG Constants
	private static final String RKG_URL_PARAM_MID = "mid";
	private static final String RKG_URL_PARAM_OID = "oid";
	private static final String RKG_URL_PARAM_LID = "lid";
	private static final String RKG_URL_PARAM_CID = "cid";
	private static final String RKG_URL_PARAM_IID = "iid";
	private static final String RKG_URL_PARAM_TS = "ts";
	private static final String RKG_URL_PARAM_ICENT = "icent";
	private static final String RKG_URL_PARAM_IQTY = "iqty";
	private static final String RKG_URL_PARAM_INAME = "iname";
	
	//RKG Comparison Shopping tracking Constants
	
	private static final String RKG_URL_PARAM_ZMAM = "zmam";
	private static final String RKG_URL_PARAM_ZMAS = "zmas";
	private static final String RKG_URL_PARAM_QUANTITY = "quantity";
	private static final String RKG_URL_PARAM_PCODE = "pcode";
	private static final String RKG_URL_PARAM_ZMAN = "zman";
	private static final String RKG_URL_PARAM_ZMAT = "zmat";

	// TellAPart Constants
	private static final String PROMOCODE = "PromoCode";
	private static final String PROMOAMOUNT = "PromoAmount";
	private static final String DISPLAYNAME = "displayName";

	//For certona
	private static final String PURCHASE_CONFIRMATION = "purchase+confirmation";
	private static final String RESX_EVENT_TYPE = "resxEventType"; 
	
	//for compare metrics
	private static final String STORE_ID ="storeId";
	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
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
		return this.mPricingTools;
	}

	/**
	 * @param pPricingTools the pricingTools to set
	 */
	public final void setPricingTools(BBBPricingTools pPricingTools) {
		this.mPricingTools = pPricingTools;
	}
	

	/**
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
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
		final StringBuilder promoCodeSB = new StringBuilder();
		final StringBuilder promoAmountSB = new StringBuilder();
		final StringBuilder storeId = new StringBuilder();
		double grandTotal = 0.0; 
		DecimalFormat df = new DecimalFormat("#.##");
		
		String resxEventType = "";
				
		if (order != null) {
 
			final String siteId = getCurrentSiteId(pRequest);
			final String rkgMerchantId = getRkgMerchantId(siteId);
			final String rkgZMAMId =  getRkgZMAMId(siteId);
			String orderId = null;
			
			String onlineOrderNumber = ((BBBOrder) order).getOnlineOrderNumber();
			
			if(null != onlineOrderNumber){
				orderId = onlineOrderNumber;
			}
		
			final String genOrderCode = ((BBBOrder) order).getOnlineOrderNumber() != null ? ((BBBOrder) order).getOnlineOrderNumber() : ((BBBOrder) order).getBopusOrderNumber();
			final Date orderDate = ((BBBOrder) order).getSubmittedDate();

			final int listSize = ((BBBOrder) order).getCommerceItemCount();
			int merchandizibleCommerceItemCount = 0 ;
			int count = 1;
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[orderId="+orderId
					+" orderDate="+orderDate
					+" commerceItemsSize ="+listSize);
			
			for (CommerceItem commerceItem : (List<CommerceItem>) ((BBBOrder) order)
					.getCommerceItems()) {

				//If EcoFee or GiftWrap items then skip
				if( !(commerceItem instanceof NonMerchandiseCommerceItem)){
				
					
					final String itemName = (String) ((RepositoryItem) commerceItem
							.getAuxiliaryData().getCatalogRef())
							.getPropertyValue(DISPLAYNAME);

					itemIds.append(commerceItem.getCatalogRefId());
					productIds.append(commerceItem.getAuxiliaryData()
							.getProductId());
					itemNames.append(itemName);
					itemSkuIds.append(commerceItem.getCatalogRefId());
					itemQuantities.append(commerceItem.getQuantity());
					cItemIds.append(commerceItem.getId());
					itemSkuNames.append(itemName);
					if (count < listSize) {
						itemIds.append(BBBCoreConstants.SEMICOLON);
						productIds.append(BBBCoreConstants.SEMICOLON);
						itemNames.append(BBBCoreConstants.DOUBLE_COLON_SYMBOL);
						itemSkuIds.append(BBBCoreConstants.PIPE_SYMBOL);
						itemQuantities.append(BBBCoreConstants.PIPE_SYMBOL);
						cItemIds.append(BBBCoreConstants.PIPE_SYMBOL);
						itemSkuNames.append(BBBCoreConstants.PIPE_SYMBOL);
					}
					itemQtys.append(commerceItem.getQuantity());
					if (count < listSize) {
						itemQtys.append(BBBCoreConstants.SEMICOLON);
					}
					if (commerceItem.getPriceInfo() != null && commerceItem.getPriceInfo().getSalePrice() > 0.0) {
						itemAmounts.append(df.format(commerceItem.getPriceInfo()
								.getSalePrice()*commerceItem.getQuantity()));
						/*itemprices.append(commerceItem.getPriceInfo()
								.getSalePrice());*/
					} else if (commerceItem.getPriceInfo() != null){
						itemAmounts.append(df.format(commerceItem.getPriceInfo()
								.getListPrice()*commerceItem.getQuantity()));
						/*itemprices.append(commerceItem.getPriceInfo()
								.getListPrice());*/
					}
					if(commerceItem.getPriceInfo() != null){
						itemprices.append(
								df.format(commerceItem.getPriceInfo().getAmount()/commerceItem.getQuantity()));
						itemAmts.append(
								df.format(commerceItem.getPriceInfo().getAmount()/commerceItem.getQuantity()));
						grandTotal += commerceItem.getPriceInfo().getAmount();
					}
					if (count < listSize) {
						itemprices.append(BBBCoreConstants.SEMICOLON);
						itemAmounts.append(BBBCoreConstants.SEMICOLON);
						itemAmts.append(BBBCoreConstants.PIPE_SYMBOL);
					}
					
					//registryIds
					String registryId = ((BBBCommerceItem)commerceItem).getRegistryId();

					if(!StringUtils.isEmpty(resxEventType)){
						resxEventType +=";";
					}
					if(registryId !=null){
						Map<String, RegistrySummaryVO> registryMap = ((BBBOrder)order).getRegistryMap();

						RegistrySummaryVO regSummVO = registryMap.get(registryId);
						if (regSummVO != null && regSummVO.getRegistryType() != null) {

							String registryType = regSummVO.getRegistryType()
									.getRegistryTypeDesc();
							resxEventType +=PURCHASE_CONFIRMATION+"+"+registryType;
						}
					} else{
						resxEventType += PURCHASE_CONFIRMATION;
					}
					
					registryIds.append(registryId);	
					if (count < listSize) {
						registryIds.append(BBBCoreConstants.SEMICOLON);
					}
					
					merchandizibleCommerceItemCount++;

					
				}
				
				count++;
			}
						
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[itemIds="+itemIds
					+" skuQty="+itemQtys
					+" skuPrice ="+itemprices
					+" skuName="+itemNames
					+" skuAmounts="+itemAmounts);			
			
			final String[] prodIds = productIds.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] skuIds = itemIds.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] skuQty = itemQtys.toString().split(
					BBBCoreConstants.SEMICOLON);
			final String[] skuPrice = itemprices.toString().split(
					BBBCoreConstants.SEMICOLON);
			
			final String[] skuName = itemNames.toString().split(
					BBBCoreConstants.DOUBLE_COLON_SYMBOL);
			final String[] regIds = registryIds.toString().split(
					BBBCoreConstants.SEMICOLON);
						
			final List<String> wcItemUrl = new ArrayList<String>();
			//TheBumps.com item URL
			final List<String> bpItemUrl = new ArrayList<String>();
			final List<String> cjItemUrl = new ArrayList<String>();
			final List<String> rkgItemUrl = new ArrayList<String>();
			final List<String> rkgComparisonItemUrl = new ArrayList<String>();
			final List<OrderInfoVO> orderList = new ArrayList<OrderInfoVO>();
			OrderInfoVO orderInfoVO = null;

			for (int i = 0; i < merchandizibleCommerceItemCount; i++) {
				String reg =null;
				String registryIdRKG = BBBCoreConstants.BLANK;
				if(regIds != null && i < regIds.length) {
					reg = regIds[i];
					if(regIds[i] != null){
					    registryIdRKG = regIds[i];
					}
				}
				final String wcAndBPUrl = createBPAndWCURL(i, skuIds[i], skuQty[i],
						skuPrice[i], reg,skuName[i]);
				final String rkgComparisonURL = createRKGComparisonURL(rkgZMAMId, ZMAS ,skuQty[i], skuIds[i], genOrderCode, grandTotal);
				
				final String cjUrl = createCJURL(i, skuIds[i], skuQty[i],
						skuPrice[i]);
				
				
				logDebug("skuIds " + i +" "+ skuIds[i] + " skuPrice " + i +" "+ skuPrice[i] + " skuQty " + i +" "+ skuQty[i] + " skuName " + i +" "+ skuName[i]);
				
				final String rkgUrl = createRKGURL(rkgMerchantId, genOrderCode, i,
					registryIdRKG, skuIds[i], orderDate,
						skuPrice[i], skuQty[i], skuName[i], pRequest.getLocale());
				
				
				logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[wcAndBPUrl="+wcAndBPUrl
						+" cjUrl ="+cjUrl
						+" rkgUrl="+rkgUrl+" rkgComparisonURL ="+rkgComparisonURL);
				
				wcItemUrl.add(wcAndBPUrl);
				bpItemUrl.add(wcAndBPUrl);
				cjItemUrl.add(cjUrl);
				rkgItemUrl.add(rkgUrl);
				rkgComparisonItemUrl.add(rkgComparisonURL);
				
				orderInfoVO = new OrderInfoVO();
				orderInfoVO.setSkuId(skuIds[i]);
				orderInfoVO.setPrice(skuPrice[i]);
				orderInfoVO.setItemCount(skuQty[i]);
				orderInfoVO.setProductId(prodIds[i]);
				
				try {
					ProductVO productVo = getCatalogTools().getProductDetails(siteId, prodIds[i]);
					
					
					if(productVo !=null){
						orderInfoVO.setProductURL(productVo.getProductPageUrl());
						orderInfoVO.setProductTitle(productVo.getName());
						orderInfoVO.setProductImageURL(productVo.getProductImages().getSmallImage());
						orderInfoVO.setProductLargeImageURL(productVo.getProductImages().getLargeImage());
						orderInfoVO.setProductDescription(productVo.getLongDescription());
						if (null != reg && reg.equals("null")) {
							reg = "";
						}
						orderInfoVO.setRegistry(reg);
					}
				} catch (BBBSystemException e) {
					logError("Some BBBSystemException occuered while fetching product details for product Id : "+prodIds[i]);
				} catch (BBBBusinessException e) {
					logError("Some BBBBusinessException occuered while fetching product details for product Id : "+prodIds[i]);
				}	
				orderList.add(orderInfoVO);
			}

			
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[wcItemUrl="+wcItemUrl
					+" bpItemUrl="+bpItemUrl
					+" cjItemUrl ="+cjItemUrl
					+" rkgItemUrl="+rkgItemUrl +" rkgComparisonItemUrl="+rkgComparisonItemUrl );
			
			// Added for tellApart
			createPromoStr(order, promoCodeSB, promoAmountSB,storeId);
			pRequest.setParameter(PROMOCODE, promoCodeSB.toString());
			pRequest.setParameter(PROMOAMOUNT, promoAmountSB.toString());
			pRequest.setParameter(STORE_ID, storeId.toString());

			
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[promoCodeSB="+promoCodeSB
					+" promoAmountSB="+promoAmountSB);
			
			pRequest.setParameter(ITEM_IDS, productIds);
			pRequest.setParameter(RESX_EVENT_TYPE, resxEventType);
			pRequest.setParameter(ITEM_QTYS, itemQtys);
			pRequest.setParameter(ITEMPRICES2, itemprices);
			pRequest.setParameter(ITEM_AMOUNTS, itemAmounts);
			pRequest.setParameter(ITEM_COUNT, listSize);
			pRequest.setParameter(ITEM_SKU_IDS, itemSkuIds);
			pRequest.setParameter(ITEM_AMTS, itemAmts);
			pRequest.setParameter(ITEM_QUANTITIES, itemQuantities);
			pRequest.setParameter(ITEM_SKU_NAMES, itemSkuNames);
			pRequest.setParameter(CITEM_IDS, cItemIds);
			pRequest.setParameter(BBBCoreConstants.WC_ITEM_URL, wcItemUrl);
			pRequest.setParameter(BBBCoreConstants.BP_ITEM_URL, bpItemUrl);
			pRequest.setParameter(BBBCoreConstants.CJ_ITEM_URL, cjItemUrl);
			pRequest.setParameter(BBBCoreConstants.RKG_ITEM_URL, rkgItemUrl);
			pRequest.setParameter(BBBCoreConstants.RKG_COMPARISON_ITEM_URL, rkgComparisonItemUrl);
			pRequest.setParameter(BBBCoreConstants.GRAND_ORDER_TOTAL, df.format(grandTotal));
			pRequest.setParameter(ORDER_LIST, orderList);
			pRequest.serviceParameter(BBBCoreConstants.OPARAM, pRequest,
					pResponse);
			
			pRequest.setParameter(BBBCoreConstants.OUTPUT_PARAM_PRICEINFOVO,
					getPricingTools().getOrderPriceInfo((OrderImpl) order));
			
		}

		logDebug("CLS=[BBBOrderInfoDroplet] MTHD=[Service ends]");
		BBBPerformanceMonitor.end("BBBOrderInfoDroplet", "service");
	}

	/**
	 * Create The Bumps (bp) or WeddingChannel (wc) formated URL
	 * 
	 * @param lineNumber
	 *            - line number of item on the Invoice (Required)
	 * @param skuId
	 *            - SkuId of Product (Required)
	 * @param price
	 *            - price of the SKU in cents.(Required)
	 * @param quantity
	 *            - SKU quantity (Required)
	 * @param registryId
	 *            - Registry Id quantity (Required)
	 * @param skuName
	 *            - Descriptive name of sku (Required)
	 * @return URL
	 */
	
	private String createBPAndWCURL(final int lineNumber, final String skuId,
			final String quantity, final String price, final String registryId, final String skuName) {
		final StringBuilder url = new StringBuilder();
		url.append(LINE);
		url.append(BBBCoreConstants.COLON);
		url.append(lineNumber + 1);
		url.append(BBBCoreConstants.PIPE_SYMBOL);
		
		if(registryId!=null &&  !registryId.equalsIgnoreCase("null") ){
			url.append(REG);
			url.append(BBBCoreConstants.COLON);
			url.append(registryId);
			url.append(BBBCoreConstants.PIPE_SYMBOL);
		}
		
		url.append(SKU);
		url.append(BBBCoreConstants.COLON);
		url.append(skuId);
		url.append(BBBCoreConstants.PIPE_SYMBOL);

		url.append(DSC);
		url.append(BBBCoreConstants.COLON);
		url.append(skuName);
		url.append(BBBCoreConstants.PIPE_SYMBOL);
		
		url.append(QTY_SMALL);
		url.append(BBBCoreConstants.COLON);
		url.append(quantity);
		url.append(BBBCoreConstants.PIPE_SYMBOL);
		url.append(PRC);
		url.append(BBBCoreConstants.COLON);
		url.append(price);

		return url.toString();
	}

	
	/**
	 * Create CommissionJunction(CJ) formated URL
	 * 
	 * @param lineNumber
	 *            - line number of item on the Invoice (Required)
	 * @param skuId
	 *            - SkuId of Product (Required)
	 * @param price
	 *            - price of the SKU in cents.(Required)
	 * @param quantity
	 *            - SKU quantity (Required)
	 * @return URL
	 */
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
	 * Create RKG formated URL
	 * 
	 * @param merchantId
	 *            - Site Specific Merchant Id (Required)
	 * @param orderId
	 *            - Order Id (Required)
	 * @param lineNumber
	 *            - line number of item on the Invoice (Required)
	 * @param customInfo
	 *            - Custom Information (Optional)
	 * @param skuId
	 *            - SkuId of Product (Required)
	 * @param timeStamp
	 *            - order placement time in YYYYMMDDHHMMSS format (Optional)
	 * @param price
	 *            - price of the SKU in cents.(Required)
	 * @param quantity
	 *            - SKU quantity (Required)
	 * @param skuName
	 *            - URI encoded SKU name (Required)
	 * @return URL
	 */
	private String createRKGURL(final String merchantId, final String orderId,
			final int lineNumber, final String customInfo, final String skuId,
			final Date timeStamp, final String price, final String quantity,
			final String skuName, final Locale pLocale) {

		final StringBuilder url = new StringBuilder();
		url.append(RKG_URL_PARAM_MID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(merchantId);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_OID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(orderId);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_LID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(lineNumber + 1);

		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_CID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(customInfo);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_IID);
		url.append(BBBCoreConstants.EQUAL);
		url.append(skuId);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_TS);
		url.append(BBBCoreConstants.EQUAL);
		url.append(getRkgFormattedDate(timeStamp, pLocale));
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_ICENT);
		url.append(BBBCoreConstants.EQUAL);
		url.append(getRkgFormattedPrice(price));
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_IQTY);
		url.append(BBBCoreConstants.EQUAL);
		url.append(quantity);
		url.append(BBBCoreConstants.AMPERSAND);

		url.append(RKG_URL_PARAM_INAME);
		url.append(BBBCoreConstants.EQUAL);
		url.append(getEncodedString(skuName));
		return url.toString();
	}
	
	private String createRKGComparisonURL(final String rkgZMAMId,final String ZMAS ,final String skuQty,final String skuId,final String orderId,final double grandTotal){
		final StringBuilder url = new StringBuilder();
		url.append(RKG_URL_PARAM_ZMAM);
		url.append(BBBCoreConstants.EQUAL);
		url.append(rkgZMAMId);
		url.append(BBBCoreConstants.AMPERSAND);
		
		url.append(RKG_URL_PARAM_ZMAS);
		url.append(BBBCoreConstants.EQUAL);
		url.append(ZMAS);
		url.append(BBBCoreConstants.AMPERSAND);
		
		url.append(RKG_URL_PARAM_QUANTITY);
		url.append(BBBCoreConstants.EQUAL);
		url.append(skuQty);
		url.append(BBBCoreConstants.AMPERSAND);
		
		url.append(RKG_URL_PARAM_PCODE);
		url.append(BBBCoreConstants.EQUAL);
		url.append(skuId);
		url.append(BBBCoreConstants.AMPERSAND);
		
		url.append(RKG_URL_PARAM_ZMAN);
		url.append(BBBCoreConstants.EQUAL);
		url.append(orderId);
		url.append(BBBCoreConstants.AMPERSAND);
		
		url.append(RKG_URL_PARAM_ZMAT);
		url.append(BBBCoreConstants.EQUAL);
		url.append(grandTotal);
		
		return url.toString();
	}

	/**
	 * This method return Site Specific RKG Merchant Id
	 * 
	 * @param siteId
	 *            - Site Id
	 * @return rkgMerchantId
	 */
	private String getRkgMerchantId(final String siteId) {

		String rkgMerchantId = null;
		List<String> rkgMerchantIds = null;
		try {
			rkgMerchantIds = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.RKG_CONFIG_TYPE, siteId);
			
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[rkgMerchantIds="+rkgMerchantIds);
			
		} catch (BBBSystemException e) {
			logError("Error in getting merchant Id ", e);
		} catch (BBBBusinessException e) {
			logError("Error in getting merchant Id ", e);
		}

		if (!BBBUtility.isListEmpty(rkgMerchantIds)) {
			rkgMerchantId = rkgMerchantIds.get(0);
		}
		return rkgMerchantId;

	}
	
	private String getRkgZMAMId(final String siteId) {

		String rkgZMAMId = null;
		List<String> rkgZMAMIds = null;
		String siteIdZMAM = siteId + "ZMAM";
		try {
			rkgZMAMIds = this.getCatalogTools().getAllValuesForKey(
					BBBCoreConstants.RKG_CONFIG_TYPE, siteIdZMAM);
			
			logDebug("CLS=[BBBOrderInfoDroplet]/MSG=[rkgZMAMIds="+rkgZMAMIds);
			
		} catch (BBBSystemException e) {
			logError("Error in getting rkgZMAM Id ", e);
		} catch (BBBBusinessException e) {
			logError("Error in getting rkgZMAM Id ", e);
		}

		if (!BBBUtility.isListEmpty(rkgZMAMIds)) { 	
			rkgZMAMId = rkgZMAMIds.get(0);
		}
		return rkgZMAMId;

	}

	/**
	 * This method return Current Site Id
	 * 
	 * @param pRequest
	 *            - DynamoHttpServletRequest
	 * @return siteId
	 */
	private String getCurrentSiteId(final DynamoHttpServletRequest pRequest) {
		String siteId = getCurrentSiteIdFromManager();
		if (siteId == null) {
			siteId = pRequest.getParameter(BBBCoreConstants.SITE_ID);
		}
		logDebug("siteId: " + siteId);
		return siteId;
	}

	protected String getCurrentSiteIdFromManager() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * This method take price in dollar and return RKG Formatted Price in Cents
	 * 
	 * @param itemPrice
	 * @return priceInCents
	 */
	private String getRkgFormattedPrice(String itemPrice) {
		double cents;
		String priceInCents = null;
		String itemPriceRKG = itemPrice;
		itemPriceRKG = itemPriceRKG.replaceAll(BBBCoreConstants.COMMA,
				BBBCoreConstants.BLANK);
		final double price = Double.parseDouble(itemPriceRKG);
		if (price > 0) {
			cents = price * 100;
	 		priceInCents = String.valueOf((int) Math.round(cents));
		} else {
			priceInCents = String.valueOf((int) price);
		}
		return priceInCents;
	}

	/**
	 * This method take date and return RKG Formatted Date in String
	 * 
	 * @param pDate
	 * @return rkgFormatedDate
	 */
	private String getRkgFormattedDate(final Date pDate, Locale pLocale) {
		final SimpleDateFormat format = new SimpleDateFormat(
				BBBCoreConstants.RKG_DATE_PATTERN, pLocale);
		String rkgFormatedDate = null;
		if (pDate != null) {
			rkgFormatedDate = format.format(pDate);
		} else {
			rkgFormatedDate = format.format(new Date());
		}
		return rkgFormatedDate;
	}

	/**
	 * This method take String value and return URI encoded String
	 * 
	 * @param str
	 * @return encodedString
	 */
	private String getEncodedString(final String str) {
		String encodedString = null;
		try {
			encodedString = java.net.URLEncoder.encode(str, BBBCoreConstants.UTF_8).replace(BBBCoreConstants.PLUS, BBBCoreConstants.PERCENT_TWENTY);
		} catch (UnsupportedEncodingException e) {
			encodedString = str;
		}
		return encodedString;
	}

	/**
	 * Extract all the promotions applied to the order
	 * 
	 * @param order
	 * @param profile
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void createPromoStr(final Object order,
			final StringBuilder promoCodeSB, final StringBuilder promoAmountSB,final StringBuilder storeId) {

		int index = 1;
		if( ((BBBOrder) order).getPriceInfo() != null &&
				((BBBOrder) order).getPriceInfo().getAdjustments() != null){
			for (PricingAdjustment adjustment : (List<PricingAdjustment>) ((BBBOrder) order)
					.getPriceInfo().getAdjustments()) {
				if (adjustment.getPricingModel() != null) {
					if (index < ((BBBOrder) order).getPriceInfo().getAdjustments()
							.size()) {
						promoCodeSB.append(adjustment.getPricingModel()
								.getPropertyValue(DISPLAYNAME));
						promoAmountSB.append(adjustment.getTotalAdjustment());
						promoCodeSB.append(BBBCoreConstants.COMMA);
						promoAmountSB.append(BBBCoreConstants.COMMA);
					} else {
						promoCodeSB.append(adjustment.getPricingModel()
								.getPropertyValue(DISPLAYNAME));
						promoAmountSB.append(adjustment.getTotalAdjustment());
					}
	
					index++;
				}
			}
		}
		for (CommerceItem item : (List<CommerceItem>) ((BBBOrder) order)
				.getCommerceItems()) {
			if (item instanceof BBBCommerceItem) {
				index = 1;
				if(item.getPriceInfo() != null &&
						item.getPriceInfo().getAdjustments() != null){
					for (PricingAdjustment adjustment : (List<PricingAdjustment>) item
							.getPriceInfo().getAdjustments()) {
						if (adjustment.getPricingModel() != null) {
							if (index < item.getPriceInfo().getAdjustments().size()) {
								promoCodeSB.append(adjustment.getPricingModel()
										.getPropertyValue(DISPLAYNAME));
								promoAmountSB.append(adjustment
										.getTotalAdjustment());
								promoCodeSB.append(BBBCoreConstants.COMMA);
								promoAmountSB.append(BBBCoreConstants.COMMA);
							} else {
								promoCodeSB.append(adjustment.getPricingModel()
										.getPropertyValue(DISPLAYNAME));
								promoAmountSB.append(adjustment
										.getTotalAdjustment());
							}
	
							index++;
						}
					}
				}
			}
		}

		for (ShippingGroup sg : (List<ShippingGroup>) ((BBBOrder) order)
				.getShippingGroups()) {
			if (sg instanceof HardgoodShippingGroup) {
				index = 1;
			
				if(sg.getPriceInfo() != null &&
						sg.getPriceInfo().getAdjustments() != null){
					for (PricingAdjustment adjustment : (List<PricingAdjustment>) sg
							.getPriceInfo().getAdjustments()) {
						if (adjustment.getPricingModel() != null) {
							if (index < sg.getPriceInfo().getAdjustments().size()) {
								promoCodeSB.append(adjustment.getPricingModel()
										.getPropertyValue(DISPLAYNAME));
								promoAmountSB.append(adjustment
										.getTotalAdjustment());
								promoCodeSB.append(BBBCoreConstants.COMMA);
								promoAmountSB.append(BBBCoreConstants.COMMA);
							} else {
								promoCodeSB.append(adjustment.getPricingModel()
										.getPropertyValue(DISPLAYNAME));
								promoAmountSB.append(adjustment
										.getTotalAdjustment());
							}
	
							index++;
						}
					}
				}
			}
			else if(sg instanceof BBBStoreShippingGroup){
				
				if(storeId.length()>0){
					storeId.append(BBBCoreConstants.COMMA);
				}
				storeId.append((((BBBStoreShippingGroup) sg).getStoreId()));
			}
		}
		

	}

}
