/**
 * 
 */
package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;

import atg.commerce.gifts.GiftlistManager;
import atg.commerce.inventory.InventoryException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileManager;
import com.bbb.browse.webservice.manager.BBBEximManager;
import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.commerce.inventory.BBBStoreInventoryManager;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSavedItemsSessionBean;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.selfservice.manager.SearchStoreManager;
import com.bbb.utils.BBBUtility;
import com.bbb.wishlist.GiftListVO;
import com.bbb.rest.catalog.vo.KatoriPriceRestVO;


/**
 * @author
 *
 */
public class SKURollUpListDroplet extends BBBDynamoServlet {
	
	/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
		private ProductManager productManager;
		private BBBInventoryManager inventoryManager;
		private BBBStoreInventoryManager storeInventoryManager;
		private SearchStoreManager searchStoreManager;
		private GiftlistManager giftlistManager;
		RepositoryItem mProfile;
	/* ===================================================== *
 		CONSTANTS
	 * ===================================================== */	
		public final static String  OPARAM_OUTPUT="output";
		public final static String  OPARAM_ERROR="error";
		public final static String  COLOR_PARAM="Color";
		public final static String  SIZE_PARAM="Size";
		public final static String  FINISH_PARAM="Finish";
		public final static String SKUDETAILS_VO="SKUDetailsVO";
		public final static String INSTOCK="inStock";
		public final static String REGISTRYID="registryId";
		public final static String FAV_STORE_STOCK_STATUS = "favStoreStockStatus";
		public final static String  LTL_DSL = "ltlDsl";
		public final static String SOPT="sopt";
		
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
		// LTL 
		private BBBShippingGroupManager mShippingGroupManager;
		
		private BBBCatalogTools mCatalogTools;
		
		
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

	public void setProductManager(ProductManager productManager) {
		this.productManager = productManager;
	}
	
	public BBBInventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

		/**
	 * Getter for BBBShippingGroupManager.
	 * 
	 * @return mShippingGroupManager.
	 */
	public BBBShippingGroupManager getShippingGroupManager() {
		return mShippingGroupManager;
	}
	
	/**
	 * Setter for BBBShippingGroupManager.
	 * 
	 * @param pShipingGrpMgr
	 *            BBB Shipping Group Manager
	 */
	public void setShippingGroupManager(
			final BBBShippingGroupManager pShipingGrpMgr) {
		this.mShippingGroupManager = pShipingGrpMgr;
	}

	private BBBEximManager eximManager;
	
	public BBBEximManager getEximManager() {
		return eximManager;
	}

	public void setEximManager(BBBEximManager eximManager) {
		this.eximManager = eximManager;
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
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		/* ===================================================== *
		   MEMBER VARIABLES
	    * ===================================================== */
		String pSiteId = null;
		String pProductId = null;
		SKUDetailVO pSKUDetailVO = null;
		String skuId = null;
		String firstRollUpValue = null;
		String firstRollUpType = null;
		String secondRollUpType = null;
		String secondRollUpValue = null;
		String registryId = null;
		String prodColor = null;
		String prodSize = null;
		String prodFinish = null;
		Map<String,String> rollUpTypeValueMap = new HashMap<String, String>();
		int inStockStatus;
		boolean inStock = true;
		String pSopt = null;
		//final String locale=  pRequest.getLocale().getLanguage();
		
		try {

			/**
			 * Product id from the JSP page.
			 */
			pProductId = pRequest
					.getParameter(BBBSearchBrowseConstants.PRODUCT_ID_PARAMETER);
			
			prodColor = pRequest
					.getParameter("prodColor");
			
			prodSize = pRequest
					.getParameter("prodSize");
			
			prodFinish = pRequest
					.getParameter("prodFinish");
			pSopt = pRequest.getParameter(SOPT);
			
			registryId = pRequest.getParameter(REGISTRYID);
			BBBSessionBean sessionBean = (BBBSessionBean) pRequest.
					resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
			if((!BBBUtility.isEmpty(prodColor) && !(prodColor.equals("null"))) && !BBBUtility.isEmpty(prodSize) && !(prodSize.equals("null"))){
				firstRollUpType = COLOR_PARAM;
				firstRollUpValue = prodColor;
				secondRollUpType = SIZE_PARAM;
				secondRollUpValue = prodSize;
			} else {
				if((!BBBUtility.isEmpty(prodColor) && !(("null").equals(prodFinish))) && !BBBUtility.isEmpty(prodSize) && !(("null").equals(prodSize))){
					firstRollUpType = FINISH_PARAM;
					firstRollUpValue = prodFinish;
					secondRollUpType = SIZE_PARAM;
					secondRollUpValue = prodSize;
				} else {
					if(!BBBUtility.isEmpty(prodColor) && !(("null").equals(prodColor))){
						firstRollUpType = COLOR_PARAM;
						firstRollUpValue = prodColor;
						secondRollUpType = null;
						secondRollUpValue = null;
					} else {
						if(!BBBUtility.isEmpty(prodSize) && !(("null").equals(prodSize))){
							firstRollUpType = SIZE_PARAM;
							firstRollUpValue = prodSize;
							secondRollUpType = null;
							secondRollUpValue = null;
						} else {
							firstRollUpType = FINISH_PARAM;
							firstRollUpValue = prodFinish;
							secondRollUpType = null;
							secondRollUpValue = null;
						}
					}
				}
			}

			/**
			 * siteId from the JSP page.if site id is null then get it from the
			 * SiteContextManager
			 */
			pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
			if (pSiteId == null) {
				pSiteId = currentSiteId();
			}
			Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
			
			boolean everLivingProduct= false;
			everLivingProduct = getProductManager().getProductStatus(pSiteId, pProductId);
			Boolean ltlproduct=false;
			logDebug("pSiteId["+pSiteId+"]");
			logDebug("pProductId["+pProductId+"]");
			
			if((null != pProductId) && (((null!=firstRollUpType) && (null!=firstRollUpValue)))){
					
					rollUpTypeValueMap.put(firstRollUpType, firstRollUpValue);
					
					if(((null!=secondRollUpType) && (null!=secondRollUpValue))){
						rollUpTypeValueMap.put(secondRollUpType, secondRollUpValue);
					}
					if(everLivingProduct){
						skuId = getProductManager().getEverLivingSKUId(pSiteId, pProductId, rollUpTypeValueMap);
						if (null != skuId) {
							pSKUDetailVO = getProductManager().getEverLivingSKUDetails(pSiteId, skuId, false);
							if ((!BBBUtility.isEmpty(registryId) && !(("null").equals(registryId))) ) {
		                        inStockStatus = getInventoryManager().getEverLivingProductAvailability(pSiteId, skuId, BBBInventoryManager.ADD_ITEM_FROM_REG);
							} else {
		                        inStockStatus = getInventoryManager().getEverLivingProductAvailability(pSiteId, skuId, BBBInventoryManager.PRODUCT_DISPLAY);
							}
					
							if(inStockStatus == BBBInventoryManager.NOT_AVAILABLE){
								inStock = false;
							}
						}
					}else{ // BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message 
						logDebug("First rollup value is "+firstRollUpValue);
					skuId = getProductManager().getSKUId(pSiteId, pProductId, rollUpTypeValueMap);
					if (null != skuId) {
						pSKUDetailVO = getProductManager().getSKUDetails(pSiteId, skuId, false);
							if(pSKUDetailVO.isVdcSku()){
								String vdcOffsetFlag = mCatalogTools.getConfigValueByconfigType(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS).get(BBBCoreConstants.VDC_OFFSET_FLAG);
								if(!StringUtils.isEmpty(vdcOffsetFlag) &&  vdcOffsetFlag.equalsIgnoreCase(BBBCatalogConstants.TRUE)){
								String	actualOffsetDate = mCatalogTools.getActualOffsetDate(pSiteId, skuId);
								if(!StringUtils.isEmpty(actualOffsetDate)){
									pRequest.setParameter(BBBCoreConstants.ACTUAL_OFF_SET_DATE, actualOffsetDate);
								}
								}
							}
						// BPSI - 1940 DSK | VDC messaging - combine cart and PDP | offset message
						if ((!BBBUtility.isEmpty(registryId) && !(("null").equals(registryId))) ) {
	                        inStockStatus = getInventoryManager().getProductAvailability(pSiteId, skuId, BBBInventoryManager.ADD_ITEM_FROM_REG, 0);
						} else {
	                        inStockStatus = getInventoryManager().getProductAvailability(pSiteId, skuId, BBBInventoryManager.PRODUCT_DISPLAY, 0);
						}
				
						if(inStockStatus == BBBInventoryManager.NOT_AVAILABLE){
							inStock = false;
						}
					}
					}
				}
			logDebug("Sku Id is "+skuId);
			
			// LTL | JS call change from PDP and Quick View
			List<ShipMethodVO> shipMethodVOList = new ArrayList<ShipMethodVO>();
			if(pSKUDetailVO!=null && pSKUDetailVO.isLtlItem()) {
				pRequest.setParameter(BBBCatalogConstants.IS_LTL_SKU,true);
				//shipMethodVOList = pSKUDetailVO.getEligibleShipMethods();
			} else {
				pRequest.setParameter(BBBCatalogConstants.IS_LTL_SKU,false);
			}
			//pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,shipMethodVOList);
			if(!everLivingProduct){
				ltlproduct = getProductManager().getProductDetails(pSiteId, pProductId).isLtlProduct();
			}
			if(null != ltlproduct) {
				pRequest.setParameter(BBBCatalogConstants.IS_LTL_PRODUCT,ltlproduct);
			} else {
				pRequest.setParameter(BBBCatalogConstants.IS_LTL_PRODUCT,false);
			}
			
			fetchFavStoreInventory(pRequest, pSiteId, pSKUDetailVO, skuId,
					sessionBean, profile);
			
			if(null != skuId){
				logDebug("skuId["+skuId+"]");				
				pRequest.setParameter(SKUDETAILS_VO, pSKUDetailVO);
				
				pRequest.setParameter(INSTOCK, inStock);
				EximSessionBean eximResponse = this.setPersonalizedItemFromSession(skuId, pRequest);
				if(eximResponse!=null){
					//BBBH-2889 - check for incart flag for handling incart price on tbs
					if(pSKUDetailVO != null) {
						KatoriPriceRestVO price = getEximManager().getPriceByRefKatori(eximResponse.getRefnum(), eximResponse.getSkuId(), eximResponse.getSiteId(), eximResponse.getEximResponse(), null, false, pSKUDetailVO.isInCartFlag() , pProductId);
						pRequest.setAttribute(BBBEximConstants.EXIM_ITEM_PRICE, price.getKatoriItemPrice());
						pRequest.setAttribute(BBBEximConstants.EXIM_PERSONALISED_PRICE, price.getKatoriPersonlizedPrice());
						pRequest.setAttribute(BBBInternationalShippingConstants.SHOPPERCURRENCY, price.getCurrencySymbol());
					}
				}
				pRequest.setParameter(BBBCoreConstants.IS_ITEM_IN_WISHLIST, this.isItemInWishlist(skuId,pSKUDetailVO,pRequest,eximResponse,pSopt));
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
						pResponse);
							
			} else {
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}
			
		}	catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception  from service of SKURollUpListDroplet for productId=" +pProductId 
					+" |prodColor="+prodColor +" |prodSize="+prodSize
					+ " |prodFinish="+prodFinish+ " |pSiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1036),bbbbEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (BBBSystemException bbbsEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception  from service of SKURollUpListDroplet for productId=" +pProductId 
					+" |prodColor="+prodColor +" |prodSize="+prodSize
					+ " |prodFinish="+prodFinish+ " |pSiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1037),bbbsEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		}
		
	}

	protected String currentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/**
	 * This method fills/unfills heart icon for multi-sku on PDP.
	 * @param pRequest
	 * @param skuId
	 * @param skuDetailVO
	 */
	public boolean isItemInWishlist(String skuId,SKUDetailVO skuDetailVO,DynamoHttpServletRequest pRequest,EximSessionBean eximResponse,String pSopt) {
		List items;
		List<ShipMethodVO> shipMethodList = new ArrayList<ShipMethodVO>();
		boolean normalSku  = false;
		Profile profile = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
		if(profile.isTransient())
		{
			List<GiftListVO> giftListVO = null;
			giftListVO = ((BBBSavedItemsSessionBean) ServletUtil.getCurrentRequest().resolveName("/com/bbb/profile/session/BBBSavedItemsSessionBean")).getSaveItems(true);				
			if(null != giftListVO){
			for(GiftListVO giftListVOItem:giftListVO){
				if(!BBBUtility.isEmpty(skuId) && skuId.equalsIgnoreCase(giftListVOItem.getSkuID())){
					if(null != giftListVOItem.getReferenceNumber() && null !=  eximResponse){
						logDebug("Returning true for In progress personalized product");
						return true;
					}
					else if(null ==  eximResponse && !BBBUtility.isEmpty(skuDetailVO.getPersonalizationType())){						
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
					if(skuDetailVO!=null && !skuDetailVO.isLtlItem()){
						logDebug("Returning true for normal products");
						return true;
						}
					else if((!BBBUtility.isEmpty(pSopt) && skuDetailVO.isLtlItem() && pSopt.equalsIgnoreCase(giftListVOItem.getLtlShipMethod())) || (skuDetailVO!=null && skuDetailVO.isLtlItem())){
						for(ShipMethodVO shipMethodVo:skuDetailVO.getEligibleShipMethods()){							
							if(shipMethodVo.getShipMethodId().equalsIgnoreCase(giftListVOItem.getLtlShipMethod())){
							shipMethodVo.setSavedInWishlist("true");
							}	
						else{
							shipMethodVo.setSavedInWishlist("false");
							}
						shipMethodList.add(shipMethodVo);
						}
						skuDetailVO.setEligibleShipMethods(shipMethodList);					
						pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,shipMethodList);
						if(!BBBUtility.isEmpty(pSopt)){
							return true;
						}
					}
					/*else if(skuDetailVO!=null && skuDetailVO.isLtlItem()){
						for(ShipMethodVO shipMethodVo:skuDetailVO.getEligibleShipMethods()){							
							if(shipMethodVo.getShipMethodId().equalsIgnoreCase(giftListVOItem.getLtlShipMethod())){
							shipMethodVo.setSavedInWishlist("true");
							}	
						else{
							shipMethodVo.setSavedInWishlist("false");
							}
						shipMethodList.add(shipMethodVo);
						}
						skuDetailVO.setEligibleShipMethods(shipMethodList);					
						pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,shipMethodList);
					}*/
					}
				}
			}
			if(shipMethodList.size() == 0){
				if(skuDetailVO!=null)
				pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,skuDetailVO.getEligibleShipMethods());
			}
		}
		else{			 
		items = this.getGiftlistManager().getGiftlistItems(((RepositoryItem) profile.getPropertyValue(BBBCoreConstants.WISH_LIST)).getRepositoryId());
		if ((items != null) && (items.size() > 0)) {
			for (int i = 0; i < items.size(); i++) {
				String itemId;
				RepositoryItem item = (RepositoryItem) items.get(i);
				//To check if the sku is saved in wishlist
				if(BBBUtility.isEmpty(skuId) && skuId.equalsIgnoreCase((String) (item).getPropertyValue(BBBCoreConstants.SKU_ID))){
					logDebug("Item is in wishlist");
					if(skuDetailVO!=null && null != (String) (item).getPropertyValue(BBBCoreConstants.REFERENCE_NUMBER) && null !=  pRequest.getAttribute(BBBCoreConstants.PERSONALIZED_SKU)){
						logDebug("Returning true for In progress personalized product");
						return true;
					}
					else if(skuDetailVO!=null && null ==  pRequest.getAttribute(BBBCoreConstants.PERSONALIZED_SKU) && !BBBUtility.isEmpty(skuDetailVO.getPersonalizationType())){												       
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
                        }
                        return false;
                 
					}					
					else if(skuDetailVO!=null && !skuDetailVO.isLtlItem()){
						logDebug("Returning true for normal product");
						return true;
					}					
					else if(skuDetailVO!=null && skuDetailVO.isLtlItem()){
						for(ShipMethodVO shipMethodVo:skuDetailVO.getEligibleShipMethods()){							
							if(shipMethodVo.getShipMethodId().equalsIgnoreCase((String) item.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD))){
							shipMethodVo.setSavedInWishlist("true");
							}	
						else{
							shipMethodVo.setSavedInWishlist("false");
							}
						shipMethodList.add(shipMethodVo);
						}
						skuDetailVO.setEligibleShipMethods(shipMethodList);					
						pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,shipMethodList);
						}
				}
			}
		}
		if(shipMethodList.size() == 0){
			if(skuDetailVO!=null)
			pRequest.setParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST,skuDetailVO.getEligibleShipMethods());
		}
	}
	return false;
	}
	
	
	/**
	 * This method sets fav store inventory.
	 * @param pRequest
	 * @param pSiteId
	 * @param pSKUDetailVO
	 * @param skuId
	 * @param sessionBean
	 * @param profile
	 * @throws BBBBusinessException
	 * @throws BBBSystemException 
	 */
	public void fetchFavStoreInventory(final DynamoHttpServletRequest pRequest,
			final String pSiteId, final SKUDetailVO pSKUDetailVO, final String skuId,
			final BBBSessionBean sessionBean, final Profile profile)
			throws BBBBusinessException, BBBSystemException {
		logDebug("SKURollUpListDroplet.fetchFavStoreInventory() - start");
		if (!profile.isTransient() && checkSkuBopusEligible(pSKUDetailVO, sessionBean)) {
			String favoriteStoreId = getSearchStoreManager().
					fetchFavoriteStoreId(pSiteId, profile);
			try {
				if(BBBUtility.isEmpty(favoriteStoreId))
				{
					logDebug("Favorite Store ID is not set for User "+profile.getRepositoryId());
				}
				else
				{
				pRequest.setParameter(FAV_STORE_STOCK_STATUS, getStoreInventoryManager().
						getFavStoreInventory(skuId, pSiteId, favoriteStoreId , false));
				}
			} catch (InventoryException invExc) {
				if(isLoggingDebug()){
				logDebug("Error fetching inventory status", invExc);
				}
			}
		}
		logDebug("SKURollUpListDroplet.fetchFavStoreInventory() - end");
	}


	/**
	 * This method checks given sku is bopus eligible or not.
	 * @param pSKUDetailVO
	 * @param profile
	 * @param sessionBean
	 * @return
	 */
	public boolean checkSkuBopusEligible(final SKUDetailVO pSKUDetailVO,
			final BBBSessionBean sessionBean) {
		logDebug("SKURollUpListDroplet.checkSkuBopusEligible() - start");
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
	 * @param the searchStoreManager set
	 */
	public void setSearchStoreManager(SearchStoreManager searchStoreManager) {
		this.searchStoreManager = searchStoreManager;
	}
	public EximSessionBean setPersonalizedItemFromSession(String skuId, DynamoHttpServletRequest request){
		//BBBSessionBean sessionBean = (BBBSessionBean)request.resolveName(BBBCoreConstants.SESSION_BEAN);
		BBBSessionBean sessionBean = resolveSessionBean(request);
		
		if(sessionBean.getPersonalizedSkus().containsKey(skuId)){
			EximSessionBean eximVO = sessionBean.getPersonalizedSkus().get(skuId);
			request.setAttribute(BBBCoreConstants.PERSONALIZED_SKU, eximVO);
			return eximVO;
		}
		else{
			logDebug("Personalized Item not found in session. skuId :- " + skuId);
		}
		return null;
	}

	protected BBBSessionBean resolveSessionBean(DynamoHttpServletRequest request) {
		return BBBProfileManager.resolveSessionBean(request);
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

	
}

