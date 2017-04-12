//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Naveen Kumar
//
//Created on: 29-November-2011
//--------------------------------------------------------------------------------

package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import javax.servlet.ServletException;
import com.bbb.wishlist.GiftListVO;
import org.apache.commons.lang.StringUtils;
import atg.servlet.ServletUtil;
import atg.commerce.gifts.GiftlistManager;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.commerce.catalog.vo.AttributeVO;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.MediaVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.TabVO;
import com.bbb.commerce.checklist.tools.CheckListTools;
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBStoreInventoryManager;
import com.bbb.commerce.porch.service.PorchServiceManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.rest.catalog.vo.KatoriPriceRestVO;
import com.bbb.rest.output.BBBCustomTagComponent;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;

/**
 * This class is to populate the all info of product which is the part of
 * product VO.
 * 
 */
public class ProductDetailDroplet extends BBBDynamoServlet {
	
		private static final String SHOW_INCART_PRICE = "showIncartPrice";
		private static final String IS_MAIN_PRODUCT = "isMainProduct";
		private static final String SIZE = "SIZE";
		private static final String IS_DEFAULT_SKU = "isDefaultSku";
		private static final String COLOR_IN_QUERY_PARAMS_TBS = "colorInQueryParamsTBS";
		/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
		private ProductManager productManager;
		private BBBInventoryManager inventoryManager;
		private List<String> providerIdList;
		private BBBCatalogTools mCatalogTools;
		private BBBStoreInventoryManager storeInventoryManager;
		private SearchStoreManager searchStoreManager;
		private LblTxtTemplateManager lblTxtTemplateManager;
		private GiftlistManager giftlistManager;
		RepositoryItem mProfile;
		private PorchServiceManager porchServiceManager;
		
		/* ===================================================== *
 		CONSTANTS
	 * ===================================================== */	
		public static final String SKUDETAILVO = "pSKUDetailVO";
		public static final String MEDIAVO = "mediaVO";
		public static final String PRODUCTVO = "productVO";
		public static final String COLLECTIONVO = "collectionVO";
		public static final String DEFAULTCHILDSKU = "pDefaultChildSku";
		public static final String FIRSTATTRIBUTSVOLIST = "pFirstAttributsVOList";
		public static final String FIRSTCHILDSKU = "pFirstChildSKU";
		public static final String PRODUCTTAB = "productTabs";
		public static final String ZOOMQUERY = "zoomQuery";
		public static final String PLACEHOLDER = "PDP";
		public static final String PRODUCT_ID_PARAMETER = "id";
		public final static String OPARAM_OUTPUT="output";
		public final static String OPARAM_ERROR="error";
		public final static String TABLOOKUP="tabLookUp";
		public final static String SKUID="skuId";
		public static final String CHECK_INVENTORY = "checkInventory";
		public final static String REGISTRYID="registryId";
		public final static String INSTOCK="inStock";
		private static final String LINK_STRING = "linkStringNonRecproduct";
		private static final String RKGCOLLECTION_PROD_IDS = "rkgCollectionProdIds";
		private static final String POC = "poc";
		private static final int DEFAULT_HEIGHT = 400;
		private static final int DEFAULT_WIDTH = 400;
		private static final String HEIGHT_PARAM = "hei=";
		private static final String WIDTH_PARAM = "&wid=";
		private static final String QUALITY_PARAM = "&qlt=50,1";
		//DSK | VDC messaging - combine cart and PDP | offset message
		public final static String OPARAM_VDC_MSG ="vdcMsg";
		public final static String STORE_DETAILS ="storeDetails";
		public final static String FAV_STORE_STOCK_STATUS = "favStoreStockStatus";
		public final static String SHOW_FAV_STORE ="showFavStore";
		public static final String REMOVE_PERSONALIZATION_COOKIE = "removePersonalization";
		public static final String LTL_DSL = "ltlDsl";
		public final static String SOPT="sopt";

	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
		public BBBCatalogTools getCatalogTools() {
			return mCatalogTools;
		}

		/**
		 * @param catalogTools the catalogTools to set
		 */
		public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
			this.mCatalogTools = pCatalogTools;
		}
	
	public ProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(final ProductManager productManager) {
		this.productManager = productManager;
	}
	
	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(final BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	/**
	 * @return the providerID
	 */
	public List<String> getProviderIdList() {
		return providerIdList;
	}

	/**
	 * @param providerID the providerID to set
	 */
	public void setProviderIdList(List<String> providerIdList) {
		this.providerIdList = providerIdList;
	}
	
	private BBBEximManager bbbEximManager;
	
	public BBBEximManager getBbbEximManager() {
		return bbbEximManager;
	}

	public void setBbbEximManager(BBBEximManager bbbEximManager) {
		this.bbbEximManager = bbbEximManager;
	}
	
	private BBBCustomTagComponent customTagComponent; 

	public BBBCustomTagComponent getCustomTagComponent() {
		return customTagComponent;
	}

	public void setCustomTagComponent(BBBCustomTagComponent customTagComponent) {
		this.customTagComponent = customTagComponent;
	}
	
	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return lblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(LblTxtTemplateManager lblTxtTemplateManager) {
		this.lblTxtTemplateManager = lblTxtTemplateManager;
	}

	/**
	 * This method get the product id and site id from the jsp and pass these
	 * value to manager class and get the productVo from manager class
	 * 
	 * @param DynamoHttpServletRequest
	 *            , DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
	    BBBPerformanceMonitor.start("ProductDetailDroplet", "service");
		/* ===================================================== *
		   MEMBER VARIABLES
	    * ===================================================== */
		ProductVO productVO = null;
		SKUDetailVO pSKUDetailVO = null;
		String pSiteId = null;
		List<String> childSKUs;
		String pDefaultChildSku = null;
		String pProductId = null;
		String pSKUId = null;
		boolean checkInventory=true;
		String registryId = null;
		List<AttributeVO> pFirstAttributsVOList = null;
		List<TabVO> productTabs=null;
		List<MediaVO> media=null;
		CollectionProductVO collectionProductVO	= null;
		int inStockStatus=0;
		boolean inStock = true;
		boolean liveClicker = true;
		final StringBuilder rkgProductIds = new StringBuilder();
		boolean isCollectionFlag= false;
		//boolean isLeadProduct= false;
		String color;
		String collectionProductId= null;
		String poc=null;
		String pSopt = null;
		try {

			/**
			 * Product id from the JSP page.
			 */
			pProductId = pRequest
					.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
			/**
			 * SKU id from the JSP page.
			 */
			pSKUId = pRequest.getParameter(SKUID);
			pSopt = pRequest.getParameter(SOPT);
			String indexStr = (String) pRequest.getParameter("startIndex");
			int startIndex = BBBCoreConstants.ZERO;
			if (!BBBUtility.isEmpty(indexStr)) {
				startIndex = Integer.parseInt(indexStr);
			}
			
			/**
			 * Flag to check inventory, passed from TBS Search page
			 */
			if(!(BBBUtility.isBlank(pRequest.getParameter(CHECK_INVENTORY))) && 
					((String)pRequest.getParameter(CHECK_INVENTORY)).equalsIgnoreCase(BBBCoreConstants.FALSE))
			{
				checkInventory = false;
			}
			
			boolean isMainProduct = false;
			boolean isDefaultSku = false;
			boolean colorInQueryParamsTBS = false;
			if(pRequest.getObjectParameter(IS_MAIN_PRODUCT) != null){
				isMainProduct = Boolean.parseBoolean(pRequest.getParameter(IS_MAIN_PRODUCT));
			}
			
			if(pRequest.getObjectParameter(IS_DEFAULT_SKU) != null){
				isDefaultSku = Boolean.parseBoolean(pRequest.getParameter(IS_DEFAULT_SKU));
			}
			
			if(pRequest.getObjectParameter(COLOR_IN_QUERY_PARAMS_TBS) != null){
				colorInQueryParamsTBS = Boolean.parseBoolean(pRequest.getParameter(COLOR_IN_QUERY_PARAMS_TBS));
			}
			registryId = pRequest.getParameter(REGISTRYID);
			poc = pRequest.getParameter(POC);
			Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
			/**
			 * siteId from the JSP page.if site id is null then get it from the
			 * SiteContextManager
			 */
			pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.
					resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			if (pSiteId == null) {
				pSiteId = SiteContextManager.getCurrentSiteId();
			}
			 
			 
			if(null != pProductId){
				logDebug("pSiteId["+pSiteId+"]");
				logDebug("pProductId["+pProductId+"]");
			if(Boolean.parseBoolean(pRequest.getParameter("fromTBSPDP")))  {
				productVO=getProductManager().getProductDetailsForLazyLoading(pSiteId, pProductId, startIndex);
			} else if(isMainProduct){
				productVO = getProductManager().getMainProductDetails(pSiteId, pProductId);
				pRequest.setParameter("nextIndex", BBBCoreConstants.ZERO);
			} else{
				productVO = getProductManager().getProductDetails(pSiteId, pProductId);
			}
				media = getProductManager().getMediaDetails(pProductId, pSiteId);
			} else {
				logDebug("pProduct is NULL");
			}
			
			
			// fetching proch service family details.
			getPorchServiceManager().getPorchServiceFamilyCodes(pProductId,productVO);			
			
			//added for R2 item 141
			color = pRequest.getParameter("color");
			
			if(null != productVO){
				StoreDetails storeDetails = null;
				//added for R2 item 141
				if(!BBBUtility.isEmpty(color)) {
					// if color is present,need to change rollupAttributes for size                             
					 if(!productVO.isCollection() && !colorInQueryParamsTBS){
						getColorSwatchDetail(productVO,color,isCollectionFlag);
                    } else {
                    	pSKUId = getSkuIDForColor(productVO, color, pSiteId, pProductId);
                    	if (!BBBUtility.isEmpty(pRequest.getParameter(SKUID)) && pRequest.getParameter(SKUID).equalsIgnoreCase(pSKUId)) {
							checkInventory = true;
							pRequest.setParameter("selectedColorFound", true);
						}
                    }
				 } 
				
				// to get the child SKUs of this product
				// this code will  be modify for multiple skus.
				childSKUs = productVO.getChildSKUs();
				
				if (childSKUs != null && childSKUs.size() == 1) {					
					pDefaultChildSku = childSKUs.get(0);					
				} else {
					if((null != childSKUs) &&(null != pSKUId) && (childSKUs.contains(pSKUId))){
						pDefaultChildSku = pSKUId;
				}  //Below code is added by TBS team to fix RM 33120. But this is not required as registry button should be enabled in case of LTL VDC items.
				   else if(null != childSKUs && isDefaultSku) {
				   	pDefaultChildSku = childSKUs.get(0);	
				   }
				}
				
					//storeDetails = fetchFavStoreDetails(pRequest, pSiteId, profile, sessionBean , pResponse);
					
				if (pDefaultChildSku!=null) {
					
					boolean calculateAboveBelowLine = false;
					
					if(checkInventory)
					{
						if (!BBBUtility.isEmpty(registryId) ) {
									calculateAboveBelowLine = true;
			                        inStockStatus = getInventoryManager().getProductAvailability(pSiteId, pDefaultChildSku, BBBInventoryManager.ADD_ITEM_FROM_REG,0);
			              } else {
			            	  		calculateAboveBelowLine = false;
			                        inStockStatus = getInventoryManager().getProductAvailability(pSiteId, pDefaultChildSku, BBBInventoryManager.PRODUCT_DISPLAY,0);
			              }
					}
					pSKUDetailVO = getProductManager().getSKUDetails(pSiteId,pDefaultChildSku, calculateAboveBelowLine);
					//BBBH-4958
					if(pRequest.getParameter(SHOW_INCART_PRICE)!=null && pRequest.getParameter(SHOW_INCART_PRICE).equalsIgnoreCase("true")){
						getCatalogTools().updateShippingMessageFlag(pSKUDetailVO, pSKUDetailVO.isInCartFlag());
					}
					
				/*	if (!profile.isTransient() && storeDetails != null &&  
							checkSkuBopusEligible(pSKUDetailVO , sessionBean)) {
						try {
							pRequest.setParameter(FAV_STORE_STOCK_STATUS, getStoreInventoryManager().
									getFavStoreInventory(pDefaultChildSku, pSiteId, storeDetails.getStoreId() , true));
						} catch (InventoryException invExc) {
							logError("Error fetching inventory status", invExc);
						}
					}
					*/
					if(inStockStatus == BBBInventoryManager.NOT_AVAILABLE){
						inStock = false;
					}
					if(!BBBUtility.isEmpty(registryId) && pSKUDetailVO.isSkuBelowLine()){
						inStock = false;
					}
					logDebug("DefaultCldSku: "+pDefaultChildSku+" Instock: "+inStock);
					if(null != pSKUDetailVO){
						productVO.setName(pSKUDetailVO.getDisplayName());
						productVO.setShortDescription(pSKUDetailVO.getDescription());
						productVO.setAttributesList(pSKUDetailVO.getSkuAttributes());
						productVO.setProductImages(pSKUDetailVO.getSkuImages());
						productVO.setZoomAvailable(pSKUDetailVO.isZoomAvailable());
						pRequest.setParameter("color", pSKUDetailVO.getColor());
						if(color != null && color.equalsIgnoreCase(pSKUDetailVO.getColor()) ) {
							pRequest.setParameter("selected", pSKUDetailVO.getSkuId());
						}
						pRequest.setParameter("size", pSKUDetailVO.getSize());
						pRequest.setParameter("upc", pSKUDetailVO.getUpc());
						pRequest.setParameter("finish", pSKUDetailVO.getColor());
					}
					//Navigate Away Functionality starts
					EximSessionBean eximResponse = getPersonalizedSkuFromSession(pDefaultChildSku, sessionBean);
					boolean removePersonalization = isRemovePersonaliationRequest(pRequest, pDefaultChildSku);
					pRequest.setParameter(BBBCoreConstants.REMOVE_PERSONALIZATION, removePersonalization);
					String removePers = pRequest.getParameter(BBBCoreConstants.REMOVE_PERSONALIZATION);
					if(removePers !=null && removePers.equalsIgnoreCase(BBBCoreConstants.TRUE))
						removePersonalization = true;						
					if(eximResponse!=null && !removePersonalization){
						pRequest.setAttribute(BBBCoreConstants.PERSONALIZED_SKU, eximResponse);
						//BBBH-2889 - check for incart flag for handling incart price on tbs
						KatoriPriceRestVO katoriPriceVO = getBbbEximManager().getPriceByRefKatori(eximResponse.getRefnum(), eximResponse.getSkuId(), eximResponse.getSiteId(), eximResponse.getEximResponse(), null, false, pSKUDetailVO.isInCartFlag(), pSKUDetailVO.getParentProdId());
						pRequest.setAttribute(BBBEximConstants.EXIM_ITEM_PRICE, katoriPriceVO.getKatoriItemPrice());
					    pRequest.setAttribute(BBBEximConstants.EXIM_PERSONALISED_PRICE,katoriPriceVO.getKatoriPersonlizedPrice());
					    pRequest.setAttribute(BBBInternationalShippingConstants.SHOPPERCURRENCY, katoriPriceVO.getCurrencySymbol());
					}else if(eximResponse!=null && removePersonalization && sessionBean.getPersonalizedSkus().containsKey(pDefaultChildSku)){
						sessionBean.getPersonalizedSkus().remove(pDefaultChildSku);
					}else if(eximResponse!=null && removePersonalization && !sessionBean.getPersonalizedSkus().containsKey(pDefaultChildSku)){
						logError("Error occured while removing personalized object from Session.  Unable to remove item from session. SkuId:-" + pDefaultChildSku);
					}else if(eximResponse == null && removePersonalization){
						logError("EximResponse from session is null.Item Personalization details are not available. SkuId :- " + pDefaultChildSku);
					}
					pRequest.setParameter(BBBCoreConstants.IS_ITEM_IN_WISHLIST, this.isItemInWishlist(pDefaultChildSku,pSKUDetailVO.isLtlItem(),pRequest,pSKUDetailVO.getPersonalizationType(),pSopt));		
					//Navigate Away Functionality ends
				}
				// * getting list of first attributes VO
				if(productVO.getAttributesList() != null){	
					pFirstAttributsVOList = productVO.getAttributesList().get(PLACEHOLDER);
				}
				// Setting Objects in request
				if(!productVO.isCollection()){
					if((null != productVO.getRollupAttributes()) && (productVO.getRollupAttributes().containsKey("NONE"))){
						Map<String, List<RollupTypeVO>> rollUp = productVO.getRollupAttributes();
						rollUp.remove("NONE");
						productVO.setRollupAttributes(rollUp);
					}
				}
				// getting product tab list
				productTabs = productVO.getProductTabs();
				
				if(productVO.isCollection()){
					collectionProductVO = (CollectionProductVO) productVO;
//					final List<String> collectionRollUp = collectionProductVO.getCollectionRollUp();
										
					//check for explode collection
//					if((null != collectionProductVO.getLeadSKU()) && (!collectionProductVO.getLeadSKU())){
//						if((null != collectionRollUp) && !(collectionProductVO.getLeadSKU()) 
//								&& !(collectionRollUp.isEmpty()) && (collectionRollUp.size() == 1)){
//						collectionProductVO = createChildProducts(collectionProductVO, collectionRollUp, pSiteId);
//						} else {
//							if((null != collectionRollUp) && (collectionRollUp.isEmpty())){
//								collectionProductVO = createChildProductsSkuLevel(collectionProductVO, pSiteId);
//							}
//						}
//					}
					
					collectionProductVO = createChildProducts(collectionProductVO, pSiteId);
					collectionProductVO = createChildProductsSkuLevel(collectionProductVO, pSiteId);
					//added for R2 item 141
					if((collectionProductVO.getLeadSKU() == null) || (!collectionProductVO.getLeadSKU())){
						Iterator<ProductVO> lstIterator;
						
						if (!BBBUtility.isEmpty(color) && !isMainProduct) {
							List<ProductVO> lstProduct = collectionProductVO.getChildProducts();
							lstIterator = lstProduct.iterator();
							while (lstIterator.hasNext()) {
								isCollectionFlag = true;
								getColorSwatchDetail(lstIterator.next(), color,isCollectionFlag);
							}
						}
					}else{
						isCollectionFlag=false;
						getColorSwatchDetail(collectionProductVO, color,isCollectionFlag);
						
						//BBBSL-4825 Check for color attributes for accessories  products START
						if(!isMainProduct){
							Iterator<ProductVO> lstIterator;
							List<ProductVO> lstProduct = collectionProductVO.getChildProducts();
							lstIterator = lstProduct.iterator();
							while (lstIterator.hasNext()) {
								getColorSwatchDetail(lstIterator.next(), color,isCollectionFlag);
							}
						}
						//BBBSL-4825 Check for color attributes for accessories START
					}
					
					pRequest.setParameter(COLLECTIONVO, collectionProductVO);
					logDebug("LeadSKU["+collectionProductVO.getLeadSKU()+"]");
					
					if((null != collectionProductVO.getLeadSKU()) && (collectionProductVO.getLeadSKU())){
						pRequest.setParameter(TABLOOKUP, true);
						logDebug("LeadSKU["+collectionProductVO.getLeadSKU()+"]");
					} else {
						pRequest.setParameter(TABLOOKUP, false);
					}
						final List<ProductVO> childProducts = collectionProductVO.getChildProducts();
						if(!BBBUtility.isListEmpty(childProducts)) {
						int size = childProducts.size();
						int count =1;
							for(Iterator<ProductVO> i=childProducts.iterator();i.hasNext();) {
								if(count!=size) {
									rkgProductIds.append(i.next().getProductId());
									rkgProductIds.append(',');
									count++;
								}else{
									rkgProductIds.append(i.next().getProductId());
								}
							}
						}
					pRequest.setParameter(RKGCOLLECTION_PROD_IDS, rkgProductIds.toString());
				} else {
						pRequest.setParameter(TABLOOKUP, true);
				}
				
				if(!BBBUtility.isListEmpty(media)){
					final ListIterator<MediaVO> mediaIterator = media.listIterator();
					while(mediaIterator.hasNext()){
						final MediaVO mediaVO = mediaIterator.next();
						logDebug("Product ID:"+ pProductId+" mediaVO Provider ID:"+mediaVO.getProviderId());
						if(getProviderIdList().contains(mediaVO.getProviderId())){
							liveClicker = true;
							break;
						} else {
							liveClicker = false;
						}
					}
					
				}	
				
				if(!liveClicker){
					pRequest.setParameter(MEDIAVO, media.get(0));
				} 	
				
				//Below code to generate certona link string for non recommended product
				final StringBuilder linkString = new StringBuilder("");
				if(pProductId != null) {
					linkString.append(pProductId).append(';');
				}
				
				if(collectionProductVO !=null && !Boolean.parseBoolean(pRequest.getParameter("fromTBSPDP"))) {
					final List<ProductVO> childProducts = collectionProductVO.getChildProducts();
					if(childProducts != null) {
						for(ProductVO items: childProducts) {
							if(items != null ) {
								linkString.append(items.getProductId()).append(';');
								
							}
						}
					}
				} /*else if (collectionProductVO !=null && Boolean.parseBoolean(pRequest.getParameter("fromTBSPDP")) 
						&& !BBBUtility.isListEmpty(productVO.getActiveChildProductIds()) && startIndex == 0) {
					String omnitureCollectionEvar29= "";
					for (String childProdId : productVO.getActiveChildProductIds()) {
						omnitureCollectionEvar29+=";"+childProdId+";;;;eVar29="+collectionProductVO.getProductId()+",";
						linkString.append(childProdId).append(';');
					}
					collectionProductVO.setOmnitureCollectionEvar29(omnitureCollectionEvar29);
				}*/
				pRequest.setParameter(PRODUCTTAB, productTabs);
				pRequest.setParameter(INSTOCK, inStock);
				pRequest.setParameter(SKUDETAILVO, pSKUDetailVO);				
				pRequest.setParameter(PRODUCTVO, productVO);
				pRequest.setParameter(FIRSTATTRIBUTSVOLIST, pFirstAttributsVOList);
				// pRequest.setParameter("activeChildProductList",productVO.getActiveChildProductIds());
				if(productVO.getChildSKUs() != null){
					pRequest.setParameter(FIRSTCHILDSKU, productVO.getChildSKUs().get(0));
				}
				//Adding user token for bazaar voice 
				pRequest.setParameter(BBBCoreConstants.USER_TOKEN_BVRR, pRequest.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR));
				
				
				if (!Boolean.parseBoolean(pRequest.getParameter("fromTBSPDP")) || ((Boolean.parseBoolean(pRequest.getParameter("fromTBSPDP"))) && startIndex == 0 )) {
					int zoomIndex;
					if(poc==null){
						zoomIndex = this.getCatalogTools().getZoomIndex(productVO.getProductId(),pSiteId);
					}
					else{
						zoomIndex = this.getCatalogTools().getZoomIndex(poc,pSiteId);
					}
					String zoomQuery = HEIGHT_PARAM+DEFAULT_HEIGHT*zoomIndex+WIDTH_PARAM+DEFAULT_WIDTH*zoomIndex+QUALITY_PARAM;
					pRequest.setParameter(ZOOMQUERY, zoomQuery);
				}
				//DSK | VDC messaging - combine cart and PDP | offset message
				if(pSKUDetailVO!=null && !StringUtils.isEmpty(pSKUDetailVO.getSkuId()) && pSKUDetailVO.isVdcSku()){
				try {
					String offsetDateVDC = getCatalogTools().getActualOffsetDate(pSiteId, pSKUDetailVO.getSkuId());
					if (!StringUtils.isEmpty(offsetDateVDC)){					
						pRequest.setParameter(BBBCoreConstants.OFFSET_DATE_VDC,offsetDateVDC);
						pRequest.serviceLocalParameter(OPARAM_VDC_MSG,pRequest,pResponse);
					}
					} catch (BBBBusinessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (BBBSystemException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				 }
				//get Akamai's header 
				
				
				getAkamaiLatLng(pRequest, pResponse);
				pRequest.setParameter(LINK_STRING, linkString.toString()); 
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
					pResponse);
			}else{
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}

		} catch (BBBBusinessException bbbbEx) {
			// BSL-2776 | Performance Changes | Changing logs status from error to debug in case product is disabled or not in repository
			if(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY.equalsIgnoreCase(bbbbEx.getErrorCode()) || BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY.equalsIgnoreCase(bbbbEx.getErrorCode())) {
				logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of ProductDetailDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1032));
			} else {
				logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of ProductDetailDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1032),bbbbEx);
			}
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (BBBSystemException bbbsEx) {
			// BSL-2776 | Performance Changes | Changing logs status from error to debug in case product is disabled or not in repository
			if(BBBCatalogErrorCodes.PRODUCT_IS_DISABLED_NO_LONGER_AVAILABLE_REPOSITORY.equalsIgnoreCase(bbbsEx.getErrorCode()) || BBBCatalogErrorCodes.PRODUCT_NOT_AVAILABLE_IN_REPOSITORY.equalsIgnoreCase(bbbsEx.getErrorCode())) {
				logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of ProductDetailDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1032));
			} else {
				logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of ProductDetailDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1033),bbbsEx);
			}
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}
		BBBPerformanceMonitor.end("ProductDetailDroplet", "service");
	}

	
	
	/**
	 * This method will replace size list it rollup attribute map and large/thumnail image to product vo.
	 * This is added for R2-item141 
	 * @param productVO
	 * @param color
	 * @param isCollectionFlag
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public void getColorSwatchDetail(ProductVO productVO,String color, boolean isCollectionFlag) throws BBBBusinessException,BBBSystemException{	

		List<RollupTypeVO> rollUpList = null;		
		String firstRollUpType="color";
		String secondRollUpType="size";  	 

		rollUpList=getProductManager().getRollupDetails(productVO.getProductId(), color, firstRollUpType, secondRollUpType);     
		productVO.setColorMatched(true);
		if(BBBUtility.isListEmpty(rollUpList)){
			productVO.setColorMatched(false);
			firstRollUpType="finish"; 
			rollUpList=getProductManager().getRollupDetails(productVO.getProductId(), color, firstRollUpType, secondRollUpType);  	
		} 		
		if(productVO.getRollupAttributes() != null){
			Map<String,List<RollupTypeVO>> rollupAttributes= productVO.getRollupAttributes();		 
			List<RollupTypeVO> lstRollupType = rollupAttributes.get(SIZE);
			if(!BBBUtility.isListEmpty(rollUpList)){			
				if(lstRollupType != null){
					rollupAttributes.put(SIZE,rollUpList);
				}
			}
		}	 
		if (isCollectionFlag == false ) {
			Iterator<RollupTypeVO> itRollupLst = rollUpList.iterator();
			while (itRollupLst.hasNext()) {
				RollupTypeVO rollupTypeVO = itRollupLst.next();
				if (color.equalsIgnoreCase(rollupTypeVO.getFirstRollUp())) {
					productVO.getProductImages().setThumbnailImage(
							rollupTypeVO.getLargeImagePath() + BBBCoreConstants.QUESTION_MARK
							+ BBBCoreConstants.DOLLOR + BBBCatalogConstants.PRODUCT_THUMBNAIL_IMAGE_SIZE 
							+ BBBCoreConstants.DOLLOR);
					
					productVO.getProductImages().setLargeImage(
							rollupTypeVO.getLargeImagePath());
					break;
				}
			}
		}
		if (isCollectionFlag == true) {
			Iterator<RollupTypeVO> itRollupLst = rollUpList.iterator();
			while (itRollupLst.hasNext()) {
				RollupTypeVO rollupTypeVO = itRollupLst.next();
				if (color.equalsIgnoreCase(rollupTypeVO.getFirstRollUp())) {
					// BBBSL-3383 -  Blurry images showing
					productVO.getProductImages().setThumbnailImage(
							rollupTypeVO.getLargeImagePath() + BBBCoreConstants.QUESTION_MARK
							+ BBBCoreConstants.DOLLOR + BBBCatalogConstants.PRODUCT_THUMBNAIL_IMAGE_SIZE 
							+ BBBCoreConstants.DOLLOR );
					break; 
				}
			}
		} 
	}
 
	/**
	 * This method will fetch skuId based on rollup attribute color or sku 
	 * 	and second rollup attribute size in case of TBS from PDP and Quick View Pages.
	 * This is added for TBXPS-708
	 * @param productVO
	 * @param colorValue
	 * @param siteId
	 * @param productId
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public String getSkuIDForColor(ProductVO productVO,String colorValue, String siteId, String productId) 
			throws BBBBusinessException,BBBSystemException {	
		String skuId = null;
		List<RollupTypeVO> rollUpList = null;		
		String firstRollUpType = "color";
		String secondRollUpType = "size";  	 

		rollUpList = getProductManager().getRollupDetails(productVO.getProductId(), colorValue, firstRollUpType, secondRollUpType);  
		
		if(BBBUtility.isListEmpty(rollUpList)){
			productVO.setColorMatched(false);
			firstRollUpType = "finish"; 
			rollUpList=getProductManager().getRollupDetails(productVO.getProductId(), colorValue, firstRollUpType, secondRollUpType);  	
		} 		
		if(rollUpList != null) {
		Iterator<RollupTypeVO> itRollupLst = rollUpList.iterator();
		while (itRollupLst.hasNext()) {
			RollupTypeVO rollupTypeVO = itRollupLst.next();
			if (colorValue.equalsIgnoreCase(rollupTypeVO.getFirstRollUp())) {
				Map<String,String> rollUpTypeValueMap = new HashMap<String, String>();
				rollUpTypeValueMap.put(firstRollUpType, colorValue);
				rollUpTypeValueMap.put(secondRollUpType, rollupTypeVO.getRollupAttribute());
				boolean everLivingProduct = getProductManager().getProductStatus(siteId, productId);
				if(everLivingProduct){
					skuId = getProductManager().getEverLivingSKUId(siteId, productId, rollUpTypeValueMap);
				} else{
					skuId = getProductManager().getSKUId(siteId, productId, rollUpTypeValueMap);
				}
				break;
			}
		}
		}
		return skuId;
	}
	
	
	
	/**
	 * This method explodes the Child Products of a collection based on the
	 * rollup attributes of the collection and child products
	 * 
	 * return CollectionProductVO
	 **/
	
	public CollectionProductVO createChildProducts(CollectionProductVO collectionProductVO, String siteId){
		
		CollectionProductVO collection = new CollectionProductVO();
		final List<ProductVO> ProductVOList = new ArrayList<ProductVO>();
		ProductVO collectionChildProduct = null;		
		collection = collectionProductVO;
		if(collectionProductVO !=null && collectionProductVO.getChildProducts()!=null)
		{
			//if(null != collectionProductVO.getChildProducts()){
			final ListIterator<ProductVO> iterator = collectionProductVO.getChildProducts().listIterator();
			while(iterator.hasNext()){
				final ProductVO product = iterator.next();
				Map<String,List<RollupTypeVO>> productRollUp = product.getRollupAttributes();
				Map<String,List<RollupTypeVO>> populateRollUp = new HashMap<String, List<RollupTypeVO>>();
				Map<String,List<RollupTypeVO>> prdRelationRollup = product.getPrdRelationRollup();
				// collection rollup is not present in the Child Product roll up when child product roll up size is 2
//				if((rollUp.size() == 1) && (productRollUp.size() == 2) && !(productRollUp.containsKey(rollUp.get(0)))){
//					collectionChildProduct = new ProductVO();
//					populateRollUp = productRollUp;
//					ProductVOList.add(setChildProductsAttributes(collectionChildProduct, product, populateRollUp, null));
//				} else {
				
				if((null != prdRelationRollup) && (null != productRollUp) && (prdRelationRollup.keySet().size() != 2) 
						&& (!prdRelationRollup.keySet().equals(productRollUp.keySet())) && ((prdRelationRollup.containsKey("COLOR") && (productRollUp.containsKey("COLOR"))) 
						|| (prdRelationRollup.containsKey("FINISH") && (productRollUp.containsKey("FINISH"))) 
						|| (prdRelationRollup.containsKey("SIZE") && (productRollUp.containsKey("SIZE"))))){
					
					final List<String> rollUp = new ArrayList<String> (product.getPrdRelationRollup().keySet());
				 
					for(String rollupString:rollUp){
						if(productRollUp.containsKey(rollupString)){
							// collection rollup is same as the Child Product roll up
//								if(productRollUp.size() == 1){
//									populateRollUp = productRollUp;
//									collectionChildProduct = new ProductVO();
//									ProductVOList.add(setChildProductsAttributes(collectionChildProduct, product, populateRollUp, null));
//								} else {
									// Child product roll up contains collection roll up but both are not equal
									populateRollUp.put(rollupString, productRollUp.get(rollupString));
									productRollUp.remove(rollupString);
									Set<String> rollUpKeys = productRollUp.keySet();
									if(rollUpKeys !=null)
									{
										for(String rollupKey:rollUpKeys){									
											List<RollupTypeVO> rollupList =  productRollUp.get(rollupKey);
											if(rollupList!=null)
											{
												for(RollupTypeVO rollUpVO:rollupList){
													collectionChildProduct = new ProductVO();
													StringBuilder name = new StringBuilder(product.getName());
													name.append(" - "+rollUpVO.getRollupAttribute());
													String concatName = name.toString();
													try {
														List<RollupTypeVO> rollUpValues = getProductManager().getRollupDetails(product.getProductId(), rollUpVO.getRollupAttribute(), rollupString, null);
														if(rollUpValues!=null && !rollUpValues.isEmpty()){
															populateRollUp.put(rollupString, rollUpValues);
														}
													} catch (BBBBusinessException bbbbEx) {
														logError(BBBCoreErrorConstants.BROWSE_ERROR_1032 +" Business Exception from createChildProducts of ProductDetailDroplet ",bbbbEx);
													} catch (BBBSystemException bbbsEx) {
														logError(BBBCoreErrorConstants.BROWSE_ERROR_1033 +" System Exception from createChildProducts of ProductDetailDroplet ",bbbsEx);
													}	
													ProductVOList.add(setChildProductsAttributes(collectionChildProduct, product, populateRollUp, concatName));
												}
											}
										}
									}
								}
//						}
//						else {	
//							// collection rollup is not present in the Child Product roll up when child product roll up size is 1
//							if((productRollUp.size() == 1) && !(productRollUp.containsKey(rollUp.get(0)))){
//								Set<String> rollUpKeys = productRollUp.keySet();
//								for(String rollupKey:rollUpKeys){									
//									List<RollupTypeVO> rollupList =  productRollUp.get(rollupKey); 
//									for(RollupTypeVO rollUpVO:rollupList){
//										collectionChildProduct = new ProductVO();
//										List<String> skuIdList = new ArrayList<String>();
//										Map<String,String> rollUpTypeValueMap = new HashMap<String, String>();
//										StringBuilder name = new StringBuilder(product.getName());
//										name.append(" - "+rollUpVO.getRollupAttribute());
//										String concatName = name.toString();
//										if(productRollUp.containsKey(rollupString)){
//											productRollUp.remove(rollupString);
//										}
//										rollUpTypeValueMap.put(rollupKey, rollUpVO.getRollupAttribute());
//										try {
//											List<RollupTypeVO> rollUpValues = getProductManager().getRollupDetails(product.getProductId(), rollUpVO.getRollupAttribute(), rollupString, null);
//											skuIdList.add(getProductManager().getSKUId(siteId, product.getProductId(), rollUpTypeValueMap));
//											if(!rollUpValues.isEmpty()){
//												populateRollUp.put(rollupString, rollUpValues);
//											}
//										} catch (BBBBusinessException bbbbEx) {
//											logError(BBBCoreErrorConstants.BROWSE_ERROR_1032 +" Business Exception [empty rollup] from createChildProducts of ProductDetailDroplet ",bbbbEx);
//										} catch (BBBSystemException bbbsEx) {
//											logError(BBBCoreErrorConstants.BROWSE_ERROR_1033 +" System Exception [empty rollup] from createChildProducts of ProductDetailDroplet ",bbbsEx);
//										}	
//										// set SKU as Child SKU of the product
//										collectionChildProduct.setChildSKUs(skuIdList);
//										ProductVOList.add(setChildProductsAttributes(collectionChildProduct, product, populateRollUp, concatName));
//									}
//								}
//							}
//						}					
					}
				} else {
					ProductVOList.add(product);
				}
//				}
			}
//		}
//			collection.setLeadSKU(false);
			collection.setChildProducts(ProductVOList);
		}
		return collection;
	}
	
	
	/**
	 * This method sets the values of the child products called
	 * during exploding the child products based on roll up attributes.
	 * 
	 * return ProductVO
	 **/
	
	public ProductVO setChildProductsAttributes(ProductVO collectionChildProduct, ProductVO product,
			Map<String,List<RollupTypeVO>> populateRollUp, String name){
		
		// set child product attributes
		if(null == name){
			collectionChildProduct.setName(product.getName());
		} else {
			collectionChildProduct.setName(name);
		}
		
		
		collectionChildProduct.setPriceRangeDescription(product.getPriceRangeDescription());
		if((null == collectionChildProduct.getChildSKUs()) || (collectionChildProduct.getChildSKUs().isEmpty())){
			collectionChildProduct.setChildSKUs(product.getChildSKUs());
		}		
		collectionChildProduct.setRollupAttributes(populateRollUp);
		collectionChildProduct.setProductId(product.getProductId());
		collectionChildProduct.setProductImages(product.getProductImages());
		return collectionChildProduct;
	}
	
	
	/**
	 * This method explodes the Child Products of a collection to SKU level based on the
	 * rollup attributes of the child products
	 * 
	 * return CollectionProductVO
	 **/
	
	public CollectionProductVO createChildProductsSkuLevel(CollectionProductVO collectionProductVO, String siteId){
		
		CollectionProductVO collection = new CollectionProductVO();
		final List<ProductVO> ProductVOList = new ArrayList<ProductVO>();
		if(null != collectionProductVO.getChildProducts()){
		final ListIterator<ProductVO> iterator = collectionProductVO.getChildProducts().listIterator();
		Map<String,List<RollupTypeVO>> populateRollUp = new HashMap<String, List<RollupTypeVO>>();
		collection = collectionProductVO;
		ProductVO collectionChildProduct = null;	
		String concatName = null;
		while(iterator.hasNext()){
			final ProductVO product = iterator.next();
			if((null != product.getPrdRelationRollup()) && (null != product.getRollupAttributes()) 
					&& (product.getPrdRelationRollup().containsKey("NONE")) && (!product.getRollupAttributes().containsKey("NONE"))){
				Map<String,List<RollupTypeVO>> productRollUp = product.getRollupAttributes();
				if(productRollUp.size() == 1){
					Set<String> rollUpKeys = productRollUp.keySet();
					for(String rollupKey:rollUpKeys){		
						List<RollupTypeVO> rollupList =  productRollUp.get(rollupKey); 
						for(RollupTypeVO rollUpVO:rollupList){
							collectionChildProduct = new ProductVO();
							List<String> skuIdList = new ArrayList<String>();
							Map<String,String> rollUpTypeValueMap = new HashMap<String, String>();
							StringBuilder name = new StringBuilder(product.getName());
							name.append(" - "+rollUpVO.getRollupAttribute());
							concatName = name.toString();
							
							rollUpTypeValueMap.put(rollupKey, rollUpVO.getRollupAttribute());
							try {
								skuIdList.add(getProductManager().getSKUId(siteId, product.getProductId(), rollUpTypeValueMap));
							} catch (BBBBusinessException bbbbEx) {
								logError(BBBCoreErrorConstants.BROWSE_ERROR_1032 +" Business Exception from createChildProductsSkuLevel of ProductDetailDroplet ",bbbbEx);
							} catch (BBBSystemException bbbsEx) {
								logError(BBBCoreErrorConstants.BROWSE_ERROR_1033 +" System Exception from createChildProductsSkuLevel of ProductDetailDroplet ",bbbsEx);
							}	
							// set SKU as Child SKU of the product
							collectionChildProduct.setChildSKUs(skuIdList);
							ProductVOList.add(setChildProductsAttributes(collectionChildProduct, product, populateRollUp, concatName));
						}
					}				
				} else {
					Set<String> rollUpKeys = productRollUp.keySet();
					Iterator<String> rollUpIterator = rollUpKeys.iterator();
					if(rollUpIterator.hasNext()){
						String rollupKey = rollUpIterator.next();
						List<RollupTypeVO> rollupList =  productRollUp.get(rollupKey);
						for(RollupTypeVO rollUpVO:rollupList){
							
							Map<String,String> rollUpTypeValueMap = new HashMap<String, String>();
							String rollUp = rollUpVO.getRollupAttribute();
							rollUpTypeValueMap.put(rollupKey, rollUp);
							
							try {
								List<RollupTypeVO> rollUpValues = getProductManager().getRollupDetails(product.getProductId(), rollUp, rollupKey,rollUpKeys.toArray()[1].toString());
								for(RollupTypeVO rollUpTypeVO: rollUpValues){
									List<String> skuIdList = new ArrayList<String>();
									collectionChildProduct = new ProductVO();
									rollUpTypeValueMap.put(rollUpKeys.toArray()[1].toString(), rollUpTypeVO.getRollupAttribute());
									StringBuilder name = new StringBuilder(product.getName());
									name.append(" - ");
									name.append(rollUp);
									name.append(" - ");
									name.append(rollUpTypeVO.getRollupAttribute());
									concatName = name.toString();
									skuIdList.add(getProductManager().getSKUId(siteId, product.getProductId(), rollUpTypeValueMap));
									collectionChildProduct.setChildSKUs(skuIdList);
									ProductVOList.add(setChildProductsAttributes(collectionChildProduct, product, populateRollUp, concatName));
								}
							} catch (BBBBusinessException bbbbEx) {
								logError(BBBCoreErrorConstants.BROWSE_ERROR_1032 +" Business Exception [productRollUp is not one] from createChildProductsSkuLevel of ProductDetailDroplet ",bbbbEx);
							} catch (BBBSystemException bbbsEx) {
								logError(BBBCoreErrorConstants.BROWSE_ERROR_1033 +" System Exception [productRollUp is not one] from createChildProductsSkuLevel of ProductDetailDroplet ",bbbsEx);
							}	
							
						}
					}
				}
			} else {
				ProductVOList.add(product);
			}
		}
//		collection.setLeadSKU(false);
		collection.setChildProducts(ProductVOList);
		
		return collection;
		}
		else
		{
			collectionProductVO.setChildProducts(ProductVOList);
			return collectionProductVO;
		}
	}


	/**
	 * This method sets favorite store details.
	 * @param req
	 * @param siteId
	 * @param profile
	 * @return
	 * @throws Exception 
	 */
	/*public StoreDetails fetchFavStoreDetails(
			final DynamoHttpServletRequest req, final String siteId,
			final Profile profile, final BBBSessionBean sessionBean , DynamoHttpServletResponse res)
			throws Exception {
		logDebug("ProductDetailDroplet.fetchFavStoreDetails() - start with params request: "
				+ req + "siteId: " + siteId + "profile: " + profile);
		StoreDetails storeDetails = null;
		if (!profile.isTransient()
				&& !sessionBean.isInternationalShippingContext()) {
			String favoriteStoreId = getSearchStoreManager()
					.fetchFavoriteStoreId(siteId, profile);
			if (!BBBUtility.isEmpty(favoriteStoreId)) {
				storeDetails = getSearchStoreManager().fetchFavStoreDetailsForPDP(
						favoriteStoreId, siteId , req , res);

			} else {
				List<StoreDetails> lStoreDetails = getSearchStoreManager()
						.searchStoreByLatLng(
								req.getParameter(SelfServiceConstants.LATITUDE),
								SelfServiceConstants.LONGITUDE);
				if (!BBBUtility.isListEmpty(lStoreDetails)) {
					storeDetails = lStoreDetails.get(0);
				}
			}
			if (storeDetails != null)
				req.setParameter(STORE_DETAILS, storeDetails);
		}

		logDebug("ProductDetailDroplet.fetchFavStoreDetails() - end");
		return storeDetails;
	}*/

	/**
	 * This method checks given sku is bopus eligible or not.
	 * @param pSKUDetailVO
	 * @param profile
	 * @param sessionBean
	 * @return
	 */
	public boolean checkSkuBopusEligible(final SKUDetailVO pSKUDetailVO, 
			final BBBSessionBean sessionBean) {
		logDebug("ProductDetailDroplet.checkSkuBopusEligible() - start");
		return !sessionBean.isInternationalShippingContext() 
				&& pSKUDetailVO!=null && !pSKUDetailVO.isLtlItem() 
				&& !pSKUDetailVO.isBopusAllowed() && !pSKUDetailVO.isCustomizableRequired();
	}

	/**
	 * 
	 * @return the BBBStoreInventoryManager
	 */
	public BBBStoreInventoryManager getStoreInventoryManager() {
		return storeInventoryManager;
	}

	/**
	 * 
	 * @param the storeInventoryManager to set
	 */
	public void setStoreInventoryManager(
			BBBStoreInventoryManager storeInventoryManager) {
		this.storeInventoryManager = storeInventoryManager;
	}
	
	/**
	 * 
	 * @return the SearchStoreManager
	 */
	public SearchStoreManager getSearchStoreManager() {
		return searchStoreManager;
	}

	/**
	 * 
	 * @param the searchStoreManager to set
	 */
	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}
	public EximSessionBean getPersonalizedSkuFromSession(String skuId, BBBSessionBean sessionBean){
		return sessionBean.getPersonalizedSkus().get(skuId);
	}
	
	private boolean isRemovePersonaliationRequest(DynamoHttpServletRequest request, String skuId){
		String cookieName = ProductDetailDroplet.REMOVE_PERSONALIZATION_COOKIE;
		String cookieValue = BBBUtility.getCookie(request, cookieName);
		if(!BBBUtility.isEmpty(cookieValue)){
			String[] skuIds = cookieValue.split(BBBCoreConstants.COMMA);
			for(String sku:skuIds){
				if(sku.equalsIgnoreCase(skuId)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**This method fetches lat/lng from Akamai's header
	 * @param pRequest
	 * @param pResponse
	 */
	public void getAkamaiLatLng(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) {
		String headerValue = (String) pRequest.getHeader("X-Akamai-Edgescape");
		if (headerValue != null) {
			
			//Calling utility method which will fetch the values from header and populate them in a map.
			Map<String, String> map = BBBUtility.getAkamaiHeaderValueMap(headerValue);
			
			String latitude = map.get(SelfServiceConstants.LOCATIONLAT);
			String longitude = map.get(SelfServiceConstants.LONG);
			pRequest.setParameter(SelfServiceConstants.LATITUDE, latitude);
			pRequest.setParameter(SelfServiceConstants.LONGITUDE, longitude);
		}
	}

	/**This method is used to set heart icon filled/unfilled on PDP 
	 * @param pRequest
	 * @param isLTLItem
	 * @param skuId
	 * @param personalizationType
	 * @param pSopt
	 */
	public boolean isItemInWishlist(String skuId,boolean isLTLItem,DynamoHttpServletRequest pRequest,String personalizationType,String pSopt) {
		List items;
		logDebug("SkuId passed to isItemInWishlist.."+skuId);
		List<String> ltlDsl = new ArrayList<String>();
		boolean normalSku = false;
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		if(profile.isTransient())
		{
			List<GiftListVO> giftListVO = null;
			giftListVO = ((BBBSavedItemsSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean")).getSaveItems(true);				
			if(null != giftListVO){
			for(GiftListVO giftListVOItem:giftListVO){
				if(null != skuId && !StringUtils.isEmpty(skuId) && skuId.equalsIgnoreCase(giftListVOItem.getSkuID())){
					if(null != giftListVOItem.getReferenceNumber() && null !=  pRequest.getAttribute(BBBCoreConstants.PERSONALIZED_SKU)){
						logDebug("Returning true for In progress personalized product");
						return true;
					}
					else if(null ==  pRequest.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) && !StringUtils.isEmpty(personalizationType)){						
							for (GiftListVO giftListVOItem1:giftListVO){
                               if(skuId.equalsIgnoreCase(giftListVOItem1.getSkuID()) && null == giftListVOItem1.getReferenceNumber()){
                                      normalSku =  true;
                                      break;
                               }                                                      
                        }
                        if(normalSku == true){
                               return true;
                        }
                        else{
                        logDebug("Returning false for completely personalized product");
                        }
                        return false;                
					}
					if(!isLTLItem){
						logDebug("Returning true for normal products");
						return true;
						}
					else if(!StringUtils.isEmpty(pSopt) && isLTLItem && pSopt.equalsIgnoreCase(giftListVOItem.getLtlShipMethod())){
						ltlDsl.add(giftListVOItem.getLtlShipMethod());	
						pRequest.setParameter(LTL_DSL, ltlDsl);
						return true;
					}
					else if(isLTLItem){
						ltlDsl.add(giftListVOItem.getLtlShipMethod());	
						pRequest.setParameter(LTL_DSL, ltlDsl);
					}
					}
				}
			}
		}
		else{
		items = this.getGiftlistManager().getGiftlistItems(((RepositoryItem) profile.getPropertyValue(BBBCoreConstants.WISH_LIST)).getRepositoryId());
		if ((items != null) && (items.size() > 0)) {
			for (int i = 0; i < items.size(); i++) {
				String itemId;
				RepositoryItem item = (RepositoryItem) items.get(i);
				//To check if the sku is saved in wishlist
				if(null != skuId && !StringUtils.isEmpty(skuId) && skuId.equalsIgnoreCase((String) (item).getPropertyValue(BBBCoreConstants.SKU_ID))){
					if(null != (String) (item).getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) && null !=  pRequest.getAttribute(BBBCoreConstants.PERSONALIZED_SKU)){
						logDebug("Returning true for In progress personalized product");
						return true;
					}
					else if(null ==  pRequest.getAttribute(BBBCoreConstants.PERSONALIZED_SKU)  && !StringUtils.isEmpty(personalizationType)){       
                        if(pRequest.getParameter("itemPersonalized") != null && pRequest.getParameter("itemPersonalized").equalsIgnoreCase("true")){
                        	return false;
                        }
						for (int j = 0; j < items.size(); j++){
                              RepositoryItem item1 = (RepositoryItem) items.get(j);
                               if(skuId.equalsIgnoreCase((String) (item1).getPropertyValue(BBBCoreConstants.SKU_ID)) && null == (String) item1.getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER)){
                                      normalSku =  true;
                                      break;
                               }                                                      
                        }
                        if(normalSku == true){
                               return true;
                        }
                        else{
                        logDebug("Returning false for completely personalized product");                      
                        return false;
                        }
                 }
				 else if(!isLTLItem){
						logDebug("Returning true for normal products");
						return true;
					}	//For filling heart icon when coming from cart page in case of ltl products
					else if(!StringUtils.isEmpty(pSopt) && isLTLItem && pSopt.equalsIgnoreCase((String) (item).getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD))){
						ltlDsl.add((String) item.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD));	
						pRequest.setParameter(LTL_DSL, ltlDsl);
						return true;
					}
					else if(isLTLItem){
						ltlDsl.add((String) item.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD));	
						pRequest.setParameter(LTL_DSL, ltlDsl);
					}
				}
			}
		}
		}
		return false;
	}
	
	public GiftlistManager getGiftlistManager() {
		return giftlistManager;
	}

	public void setGiftlistManager(GiftlistManager giftlistManager) {
		this.giftlistManager = giftlistManager;
	}

	public RepositoryItem getProfile() {
		return mProfile;
	}

	public void setProfile(RepositoryItem profile) {
		mProfile = profile;
	}

 

	/**
	 * @return the porchServiceManager
	 */
	public PorchServiceManager getPorchServiceManager() {
		return porchServiceManager;
	}

	/**
	 * @param porchServiceManager the porchServiceManager to set
	 */
	public void setPorchServiceManager(PorchServiceManager porchServiceManager) {
		this.porchServiceManager = porchServiceManager;
	}
	
}