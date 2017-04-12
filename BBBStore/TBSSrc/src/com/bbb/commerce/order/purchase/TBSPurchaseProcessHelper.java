package com.bbb.commerce.order.purchase;

import java.util.Date;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.util.PipelineErrorHandler;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.cmo.vo.LineItemVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.order.TBSOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.kirsch.vo.LineItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;
import com.bbb.tbs.selfservice.manager.TBSSearchStoreManager;
import com.bbb.utils.BBBUtility;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;

public class TBSPurchaseProcessHelper extends BBBPurchaseProcessHelper{

	/**
	 * mCatalogRepository property to store CatalogRepository
	 */
	private Repository mCatalogRepository;
	
	/**
	 * mLabelRepository property to store LabelRepository
	 */
	private Repository mLabelRepository;
	
	
	/**
	 * @return the mLabelRepository
	 */
	public Repository getLabelRepository() {
		return mLabelRepository;
	}

	/**
	 * @param pLabelRepositoryp the mLabelRepository to set
	 */
	public void setLabelRepository(Repository pLabelRepositoryp) {
		mLabelRepository = pLabelRepositoryp;
	}

	/**
	 * Property to hold TBSSearchStoreManager reference.
	 */
	private String cartModifierFormHandler;
	
	public String getCartModifierFormHandler() {
		return cartModifierFormHandler;
	}

	public void setCartModifierFormHandler(String cartModifierFormHandler) {
		this.cartModifierFormHandler = cartModifierFormHandler;
	}

	private TBSSearchStoreManager mSearchStoreManager;
	
	/**
	 * @return the catalogRepository
	 */
	public Repository getCatalogRepository() {
		return mCatalogRepository;
	}

	/**
	 * @param pCatalogRepository the catalogRepository to set
	 */
	public void setCatalogRepository(Repository pCatalogRepository) {
		mCatalogRepository = pCatalogRepository;
	}
	
	
	/**
	 * @return the searchStoreManager
	 */
	public TBSSearchStoreManager getSearchStoreManager() {
		return mSearchStoreManager;
	}
	
	/**
	 * @param pSearchStoreManager the searchStoreManager to set
	 */
	public void setSearchStoreManager(TBSSearchStoreManager pSearchStoreManager) {
		mSearchStoreManager = pSearchStoreManager;
	}
	
	/**
     * override the BBBPurchaseProcessHelper class method to check whether to create the BBBCOmmerceItem or TBSCommerceItem -
     *
     * @param pItemInfo
     * @param pCatalogKey
     * @param pOrder
     *
     */
	@SuppressWarnings("unchecked")
	@Override
    protected CommerceItem createCommerceItem(final AddCommerceItemInfo pItemInfo, final String pCatalogKey, final Order pOrder)
    		throws CommerceException {
	 
	 vlogDebug("TBSPurchaseProcessHelper :: createCommerceItem() :: START " );
	 CommerceItem cItem = null;
	 if(pItemInfo.getValue().get("pagetype") != null){
		 pItemInfo.getValue().remove("pagetype");
	 }
	 CommerceItemManager cimgr = getCommerceItemManager();
	 String upccode = null;
	 try {
			RepositoryItem skuItem = getCatalogRepository().getItem(pItemInfo.getCatalogRefId(), TBSConstants.SKU);
			Set<RepositoryItem> ParentCats = null;
			CategoryVO categoryVO = null;
			if(skuItem != null){
				upccode = (String) skuItem.getPropertyValue(TBSConstants.UPC);
			}
			//Creating BBBCommerceItem and it will behave like type casting the BBBCommerceItem to TBSCommerceItem. 
			//Creating the regular commerce item and type casting it to TBSCommerce item.
	        this.logDebug(new StringBuilder().append("createCommerceItem() : creating CommerceItem for product id ").append(pItemInfo.getProductId()).toString() );
	        final String siteId = ( StringUtils.isBlank(pItemInfo.getSiteId())) ? SiteContextManager.getCurrentSiteId() : pItemInfo.getSiteId();

	        RepositoryItem prodItem = getCatalogRepository().getItem(pItemInfo.getProductId(),"product");
	        if(prodItem != null){
	        	ParentCats = (Set<RepositoryItem>) prodItem.getPropertyValue("parentCategories");
	        }
	        if(ParentCats != null && !ParentCats.isEmpty()){
	        	Boolean isCollege = false;
	        	for (RepositoryItem catItem : ParentCats) {
	        		if(catItem.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME) != null){
	        			isCollege = (Boolean)catItem.getPropertyValue(BBBCatalogConstants.IS_COLLEGE_CATEGORY_PROPERTY_NAME);
	        			if(isCollege){
	        				pItemInfo.getValue().put(TBSConstants.BTS, "true");
	        				break;
	        			}
	        		}
	        	}
	        }
	        TBSCommerceItem ci  = (TBSCommerceItem)cimgr.createCommerceItem(
	                pItemInfo.getCommerceItemType(), pItemInfo.getCatalogRefId(),
	                null, pItemInfo.getProductId(), null, pItemInfo.getQuantity(),
	                pCatalogKey, null, siteId, null);

	        this.logDebug("TbsPurchaseProcessHelper.createCommerceItem:: BBBCommerceItem before addition of BBBProperties " + ci.toString());
	        
	        ci = addTbsBBBProperties(pItemInfo, ci, pOrder, siteId);
	        SKUDetailVO skuVo = null;
	        try {
				skuVo = getCatalogTools().getSKUDetails(siteId, pItemInfo.getCatalogRefId());
			} catch (BBBSystemException e) {
				vlogError("BBBSystemException occurred while getting the sku details ::"+e);
			} catch (BBBBusinessException e) {
				vlogError("BBBBusinessException occurred while getting the sku details ::"+e);
			}
	        String shipTime = null;
	        if(skuVo != null && skuVo.isVdcSku() && !skuVo.isLtlItem()){
	        	if(skuItem.getPropertyValue("vdcSkuMessage") != null){
	        		shipTime = (String)skuItem.getPropertyValue("vdcSkuMessage");
	        		ci.setShipTime(shipTime);
	        	}
	        } else if(skuVo != null && skuVo.isLtlItem()){
	        	shipTime = null;
	        } else {
	        	shipTime = (String) pItemInfo.getValue().get(TBSConstants.SHIP_TIME);
	        	if (BBBUtility.isEmpty(shipTime)) {
	        		shipTime = updateCommerceItemShipTime(pItemInfo, siteId);
	        	}
	        	ci.setShipTime(shipTime);
	        }
	        if(StringUtils.isBlank(shipTime)){
	        	ci.setShipTime(getLtlShipTime("lbl_ltl_ship_time"));
	        }
	        
	        this.logDebug("TbsPurchaseProcessHelper.createCommerceItem:: BBBCommerceItem after addition of BBBProperties " + ci.toString());
	        this.logDebug("createCommerceItem() : Added extra BBB properties into CommerceItem Created " );
	        cItem = cimgr.addItemToOrder(pOrder, ci);
	        if(!StringUtils.isBlank(upccode)){
	        	((TBSCommerceItem)cItem).setPdpUrlflag(false);
			}
		} catch (RepositoryException e) {
        	vlogError("RepositoryException occured while creating the commerceItem "+e);
        }
		vlogDebug("TBSPurchaseProcessHelper :: createCommerceItem() :: END " );
		return cItem;
	 }
	 
    private String getLtlShipTime(String pKey) {
    	RepositoryItemDescriptor labelTextDescriptor;
    	RepositoryItem[] labels = null;
    	String shiptime = null;
		try {
			labelTextDescriptor = getLabelRepository().getItemDescriptor("labelTextArea");
			RepositoryView labelView = labelTextDescriptor.getRepositoryView();
			QueryBuilder labelQueryBuilder = labelView.getQueryBuilder();
			
			QueryExpression keyProperty = labelQueryBuilder.createPropertyQueryExpression("key");
			QueryExpression keyValue = labelQueryBuilder.createConstantQueryExpression(pKey);
			Query labelQuery = labelQueryBuilder.createComparisonQuery(keyProperty, keyValue, QueryBuilder.EQUALS);
			
			logDebug("label query : " + labelQuery);
			
			labels = labelView.executeQuery(labelQuery);
			if(labels != null && labels.length > TBSConstants.ZERO ){
				shiptime = (String) labels[TBSConstants.ZERO].getPropertyValue("labelValueDefault");
			}
			
		} catch (RepositoryException e) {
			vlogError("RepositoryException occurred while getting the label item "+e);
		}
		
		return shiptime;
	}

	/**
     * @param pItemInfo
     * @param pCommerceItem
     * @param pOrder
     * @param pSiteId
     * @return BBBCommerceItem
     * @throws CommerceException
     */
    public TBSCommerceItem addTbsBBBProperties(final AddCommerceItemInfo pItemInfo, final TBSCommerceItem pCommerceItem,
            final Order pOrder, final String pSiteId)
                    throws CommerceException{

        SKUDetailVO skuDetailVO = null;
        @SuppressWarnings ("unchecked")
        final Dictionary<String, String> value = pItemInfo.getValue();
        RegistrySummaryVO registrySummaryVO = null;
        final TBSCommerceItem tempItem = pCommerceItem;
        if (value != null) {
            tempItem.setRegistryId(value.get(TBSConstants.REGISTRY_ID));
            final BBBOrderImpl bbbOrder = (BBBOrderImpl) pOrder;
            if( (value.get(TBSConstants.REGISTRY_ID) != null) && !StringUtils.isEmpty(value.get(TBSConstants.REGISTRY_ID).toString())){
                registrySummaryVO = (RegistrySummaryVO) bbbOrder.getRegistryMap().get(
                        value.get(TBSConstants.REGISTRY_ID));
                if(registrySummaryVO == null){
                    try{
                        registrySummaryVO = this.getGiftRegistryManager().getRegistryInfo( value.get(TBSConstants.REGISTRY_ID), pSiteId);
                    }catch (final BBBBusinessException e) {
                        throw new CommerceException(e);
                    }catch (final BBBSystemException e) {
                        throw new CommerceException(e);
                    }
                }
                if(null != registrySummaryVO){
                    tempItem.setRegistryInfo( getRegistryInfo(registrySummaryVO));
                }

            }

            tempItem.setReferenceNumber(value.get("referenceNumber"));
            tempItem.setEximErrorExists(Boolean.parseBoolean(value.get("eximErrorExists")));

            if(BBBUtility.isNotEmpty(value.get("eximPricingReq"))) {
            	tempItem.setEximPricingReq(Boolean.parseBoolean(value.get("eximPricingReq")));
            } else if(BBBUtility.isNotEmpty(value.get("referenceNumber"))){
            	tempItem.setEximPricingReq(true);
            }
            tempItem.setEximErrorExists(Boolean.parseBoolean(value.get("eximErrorExists")));
            
            tempItem.setStoreId(value.get(TBSConstants.STORE_ID));
            final String prevPrice = value.get(BBBCoreConstants.PREVPRICE);
            if(!StringUtils.isEmpty(prevPrice)){
                tempItem.setPrevPrice(Double.parseDouble(prevPrice));
            }
            tempItem.setLtlShipMethod(value.get(TBSConstants.LTL_SHIP_METHOD));
            tempItem.setWhiteGloveAssembly(value.get(BBBCatalogConstants.WHITE_GLOVE_ASSEMLBY));
            tempItem.setBts(Boolean.parseBoolean( value.get(TBSConstants.BTS)));
            final String check = value.get(BBBCoreConstants.OOS);
            if(!StringUtils.isEmpty(check)) {
                tempItem.setMsgShownOOS(Boolean.parseBoolean(check));
            } else {
                tempItem.setMsgShownOOS(true);
            }
            pItemInfo.getValue().remove(BBBCoreConstants.OOS);
            //set the current Time as lastmodifieddate. which is used in mini cart to sort the items
            tempItem.setLastModifiedDate(new Date());
            try{
                skuDetailVO = this.getCatalogTools().getSKUDetails(pSiteId, pItemInfo.getCatalogRefId(), false, true, true);
            }catch (final BBBSystemException e) {
                this.logError("System Exception Occourred while getting SKUVo from catalog", e);
            }catch (final BBBBusinessException e) {
                this.logError("Business Exception Occourred while getting SKUVo from catalog", e);
            }
            //Set flag indicating CommenceItem is of store SKU
            if(skuDetailVO != null){
                tempItem.setStoreSKU(skuDetailVO.isStoreSKU());
            }
        }
        try {
            skuDetailVO = this.getCatalogTools().getSKUDetails(pSiteId, pItemInfo.getCatalogRefId(), false, true, true);
            if (null != skuDetailVO) {
                tempItem.setVdcInd(skuDetailVO.isVdcSku());
                if (!skuDetailVO.isVdcSku() && (value != null)) {
                    tempItem.setBts(Boolean.parseBoolean(value.get(TBSConstants.BTS)));
                }
                if(skuDetailVO.getFreeShipMethods() != null) {
                    tempItem.setFreeShippingMethod( getFreeShippingMethodInfo(skuDetailVO.getFreeShipMethods()));
                }
                tempItem.setSkuSurcharge(skuDetailVO.getShippingSurcharge());
                tempItem.setIsEcoFeeEligible(skuDetailVO.getIsEcoFeeEligible());
            }
        } catch (final BBBSystemException e) {
            throw new CommerceException("BBBSystem Exception from createCommerceItem in BBBPurchaseProcessHelper", e);
        } catch (final BBBBusinessException e) {
            throw new CommerceException("BBBSystem Exception from createCommerceItem in BBBPurchaseProcessHelper", e);
        }

        return tempItem;
    }
    
    
    /**
     * This method is used to get the shiptime
     * @param pItemInfo
     * @param pSiteId
     * 
     * @return lShipTime
     * @throws CommerceException
     */
    protected String updateCommerceItemShipTime(AddCommerceItemInfo pItemInfo, String pSiteId) throws CommerceException{
    	vlogDebug("TBSPurchaseProcessHelper :: updateCommerceItemShipTime() :: START " );
    	String lSkuId = pItemInfo.getCatalogRefId();
    	String lSiteId = pSiteId;
		String lShipTime = getSearchStoreManager().getShipTime(lSkuId, pItemInfo.getQuantity(), lSiteId, null);
		vlogDebug("TBSPurchaseProcessHelper :: updateCommerceItemShipTime() :: END " );
		return lShipTime;
    }

    
    /**
	 * This method gets the LineItems from the Kirsch Response and creates the
	 * kirsch commerce items.
	 * 
	 * @param pResponseVO
	 *            the response vo
	 * @return true, if successful
	 * @throws RepositoryException
	 * @throws CommerceException
	 */
	public void createKirschCommerceItems(List<LineItem> pLineItems, Order pOrder) throws RepositoryException, CommerceException {

		CommerceItemManager commItemMngr = getCommerceItemManager();

		boolean isRollBack = false;
		vlogDebug("Creating the Kirsch commerce items");
		for (LineItem lineItem : pLineItems) {

			RepositoryItem skuItem = null;
			String skuId = lineItem.getSku();

			try {
				skuItem = getOrderManager().getCatalogTools().findSKU(skuId);

				if (skuItem != null) {
					Set parentProducts = (Set) skuItem.getPropertyValue("parentProducts");

					if (parentProducts != null && !parentProducts.isEmpty()) {
						RepositoryItem prodItem = (RepositoryItem) parentProducts.toArray()[0];

						if (prodItem != null) {
							String productId = prodItem.getRepositoryId();

							vlogDebug("Creating the Kirsch commerce item for the SKU id :: " + lineItem.getSku());

							TBSCommerceItem tbsCItem = (TBSCommerceItem) commItemMngr.createCommerceItem("tbsCommerceItem",
									lineItem.getSku(), productId, lineItem.getQuantity());

							// creating TBSItemInfo object
							vlogDebug("creating TBSItemInfo object");
							TBSItemInfo tbsItemInfo =  ((TBSOrderTools) getOrderManager().getOrderTools()).createTBSItemInfo();
							tbsItemInfo.setConfigId(lineItem.getConfigId());
							tbsItemInfo.setProductDesc(lineItem.getProductDesc());
							tbsItemInfo.setProductImage(lineItem.getProductImage());
							tbsItemInfo.setRetailPrice(Double.valueOf(lineItem.getRetailPrice()));
							tbsItemInfo.setCost(Double.valueOf(lineItem.getCost()));
							tbsItemInfo.setPriceOveride(false);
							tbsItemInfo.setSaveAllProperties(false);
							tbsCItem.setTBSItemInfo(tbsItemInfo);
							tbsCItem.setPdpUrlflag(false);
							/* Added for RM #39309 -- start */
							tbsCItem.setVdcInd(true);
							/* Added for RM #39309 -- end */
							
							/* Added for RM # 33071 --- Start*/
							RepositoryItem lSkuItem = (RepositoryItem) tbsCItem.getAuxiliaryData().getCatalogRef();
							RepositoryItem jdaDeptItem=(RepositoryItem)lSkuItem.getPropertyValue(TBSConstants.SKU_JDA_DEPT);
							String ljdaDeptId= "";
							if(null!=jdaDeptItem)
							{
								ljdaDeptId=(String) jdaDeptItem.getPropertyValue(TBSConstants.ID);
							}
							RepositoryItem jdaSubDeptItem=(RepositoryItem)lSkuItem.getPropertyValue(TBSConstants.SKU_JDA_SUB_DEPT);
							String ljdaSubDept= "";
							if(null!=jdaSubDeptItem)
							{
								ljdaSubDept= (String) jdaSubDeptItem.getPropertyValue(TBSConstants.ID);
							}
							String ljdaClass= (String)lSkuItem.getPropertyValue(TBSConstants.SKU_JDA_CLASS);

							if(ljdaDeptId.equals(TBSConstants.KIRSCH_SKU_JDA_DEPT_ID)  &&  ljdaSubDept.equals(TBSConstants.KIRSCH_SKU_JDA_SUB_DEPT_ID) && ljdaClass.equals(TBSConstants.KIRSCH_SKU_JDA_CLASS)) 
							{
								String lShipTime = TBSConstants.KIRSCH_SHIPTIME1;
								tbsCItem.setShipTime(lShipTime);
							}
							else
							{
								String lShipTime = TBSConstants.KIRSCH_SHIPTIME2;
								tbsCItem.setShipTime(lShipTime);
							}
							/* Added for RM # 33071 --- End*/
							ShippingGroup sg = getShippingGroup(pOrder);
							CommerceItem item = commItemMngr.addItemToOrder(pOrder, tbsCItem);
							long lUnassignedQuantity = commItemMngr.getUnassignedQuantityForCommerceItem(item);
							commItemMngr.addItemQuantityToShippingGroup(pOrder, item.getId(), sg.getId(), lUnassignedQuantity);

						}
					}
				}
			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(e);
				}
				throw e;
			} catch (CommerceException e) {
				if (isLoggingError()) {
					logError(e);
				}
				throw e;
			}
		}
	}

	/**
	 * This method gets the LineItems from the CMO Response and creates the CMO
	 * commerce items.
	 * 
	 * @param pResponseVO
	 *            the response vo
	 * @return true, if successful
	 * @throws RepositoryException
	 * @throws CommerceException
	 */
	public void createCMOCommerceItems(List<LineItemVO> pLineItems, Order pOrder) throws RepositoryException, CommerceException {

		CommerceItemManager commItemMngr = getCommerceItemManager();

		for (LineItemVO lineItemVO : pLineItems) {

			RepositoryItem skuItem;
			try {
				skuItem = getOrderManager().getCatalogTools().findSKU(lineItemVO.getSku());

				if (skuItem != null) {
					Set parentProducts = (Set) skuItem.getPropertyValue("parentProducts");

					if (parentProducts != null && !parentProducts.isEmpty()) {
						RepositoryItem prodItem = (RepositoryItem) parentProducts.toArray()[0];

						if (prodItem != null) {
							String productId = prodItem.getRepositoryId();

							vlogDebug("Creating the CMO commerce item for the SKU id :: " + lineItemVO.getSku() + " and ConfigId :: " + lineItemVO.getConfigId() );
							TBSCommerceItem tbsCItem = (TBSCommerceItem) commItemMngr.createCommerceItem("tbsCommerceItem",
									lineItemVO.getSku(), productId, lineItemVO.getQuantity());

							// creating TBSItemInfo object
							vlogDebug("creating TBSItemInfo object");
							TBSItemInfo tbsItemInfo = ((TBSOrderTools) getOrderManager().getOrderTools()).createTBSItemInfo();
							tbsItemInfo.setConfigId(lineItemVO.getConfigId());
							tbsItemInfo.setProductDesc(lineItemVO.getProductDescription());
							tbsItemInfo.setRetailPrice(lineItemVO.getRetailPrice());
							tbsItemInfo.setCost(lineItemVO.getCost());
							tbsCItem.setTBSItemInfo(tbsItemInfo);
							tbsCItem.setPdpUrlflag(false);
							tbsCItem.setShipTime(lineItemVO.getVendorLeadTime());
							
							/* Added for RM #39309 -- start */
							tbsCItem.setVdcInd(true);
							/* Added for RM #39309 -- end */
							vlogDebug("TBSItemInfo object created is : " + tbsCItem );
							ShippingGroup sg = getShippingGroup(pOrder);
							CommerceItem item = commItemMngr.addItemToOrder(pOrder, tbsCItem);
							long lUnassignedQuantity = commItemMngr.getUnassignedQuantityForCommerceItem(item);
							commItemMngr.addItemQuantityToShippingGroup(pOrder, item.getId(), sg.getId(), lUnassignedQuantity);

						}
					}
				}

			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError(e);
				}
				throw e;
			} catch (CommerceException e) {
				if (isLoggingError()) {
					logError(e);
				}
				throw e;
			}
		}
	}

	public void addBBBProperties(CommerceItem pTbsExistItem, CommerceItem pTbsnewItem) {
        
		if(pTbsExistItem instanceof TBSCommerceItem){
			TBSCommerceItem existItem = (TBSCommerceItem) pTbsExistItem;
			TBSCommerceItem newItem = (TBSCommerceItem) pTbsnewItem;
			newItem.setRegistryId(existItem.getRegistryId());
			newItem.setRegistryInfo(existItem.getRegistryInfo());
			
			newItem.setStoreId(existItem.getStoreId());
			newItem.setPrevPrice(existItem.getPrevPrice());
			newItem.setLtlShipMethod(existItem.getLtlShipMethod());
			newItem.setWhiteGloveAssembly(existItem.getWhiteGloveAssembly());
			newItem.setBts(existItem.getBts());
			newItem.setMsgShownOOS(existItem.isMsgShownOOS());
			newItem.setStoreSKU(existItem.isStoreSKU());
			newItem.setLtlItem(existItem.isLtlItem());
			
			newItem.setLastModifiedDate(new Date());
			newItem.setVdcInd(existItem.isVdcInd());
			newItem.setFreeShippingMethod(existItem.getFreeShippingMethod());
			newItem.setSkuSurcharge(existItem.getSkuSurcharge());
			newItem.setIsEcoFeeEligible(existItem.getIsEcoFeeEligible());
			newItem.setShipTime(existItem.getShipTime());
			
			//set PersonalizedDetails
			if(!BBBUtility.isEmpty(existItem.getReferenceNumber())) {
				newItem.setReferenceNumber(existItem.getReferenceNumber());
				newItem.setPersonalizationDetails(existItem.getPersonalizationDetails());
				newItem.setPersonalizationOptions(existItem.getPersonalizationOptions());
				newItem.setPersonalizeCost(existItem.getPersonalizeCost());
				newItem.setFullImagePath(existItem.getFullImagePath());
				newItem.setThumbnailImagePath(existItem.getThumbnailImagePath());
				newItem.setEximErrorExists(existItem.isEximErrorExists());
				newItem.setEximPricingReq(existItem.isEximPricingReq());
				newItem.setPersonalizationOptionsDisplay(existItem.getPersonalizationOptionsDisplay());
				newItem.setPersonalizePrice(existItem.getPersonalizePrice());
				newItem.setMetaDataFlag(existItem.getMetaDataFlag());
				newItem.setMetaDataUrl(existItem.getMetaDataUrl());
				newItem.setModerationFlag(existItem.getModerationFlag());
				newItem.setModerationUrl(existItem.getModerationUrl());
				newItem.setMobileFullImagePath(existItem.getMobileFullImagePath());
				newItem.setMobileThumbnailImagePath(existItem.getMobileThumbnailImagePath());
			}
		}
		
		
		if(pTbsExistItem instanceof LTLDeliveryChargeCommerceItem){
			LTLDeliveryChargeCommerceItem existItem = (LTLDeliveryChargeCommerceItem) pTbsExistItem;
			LTLDeliveryChargeCommerceItem newItem = (LTLDeliveryChargeCommerceItem) pTbsnewItem;
			
			newItem.setLtlCommerceItemRelation(existItem.getLtlCommerceItemRelation());
			newItem.setStoreId(existItem.getStoreId());
		}
		if(pTbsExistItem instanceof LTLAssemblyFeeCommerceItem){
			LTLAssemblyFeeCommerceItem existItem = (LTLAssemblyFeeCommerceItem) pTbsExistItem;
			LTLAssemblyFeeCommerceItem newItem = (LTLAssemblyFeeCommerceItem) pTbsnewItem;
			
			newItem.setLtlCommerceItemRelation(existItem.getLtlCommerceItemRelation());
			newItem.setStoreId(existItem.getStoreId());
		}
	}
	
	public ShippingGroup getShippingGroup(Order pOrder) {
	    ShippingGroup shippingGroup = null;
	    if ((pOrder.getShippingGroups() != null) && (pOrder.getShippingGroups().size() > 0))
	      shippingGroup = (ShippingGroup)pOrder.getShippingGroups().get(0);
	    else {
	      try {
	        pOrder.addShippingGroup(getShippingGroupManager().createShippingGroup());
	        shippingGroup = (ShippingGroup)pOrder.getShippingGroups().get(0);
	      } catch (CommerceException ce) {
	        vlogError("CommerceException occurred " + ce, new Object[0]);
	        return null;
	      }
	    }
	    return shippingGroup;
	  }
	
	@SuppressWarnings("rawtypes")
	@Override
	public void postAddItemsToOrder(List pItemsAdded, Order pOrder,
			PricingModelHolder pUserPricingModels, Locale pUserLocale,
			RepositoryItem pProfile, PipelineErrorHandler pErrorHandler,
			AddCommerceItemInfo[] pItemInfos, boolean pGiftWithPurchase)
			throws CommerceException{
		super.postAddItemsToOrder(pItemsAdded, pOrder, pUserPricingModels, pUserLocale, pProfile, pErrorHandler, pItemInfos, pGiftWithPurchase);
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		BBBCartFormhandler cmHandler = (BBBCartFormhandler) request.resolveName(getCartModifierFormHandler());
		if(cmHandler.isFromPipelineFlag()){
		((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(pOrder,null);
		}
	}
}
