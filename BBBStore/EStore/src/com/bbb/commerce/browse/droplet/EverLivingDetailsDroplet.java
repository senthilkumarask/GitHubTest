//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Dhanashree Waghmare
//
//Created on: 3-March-2014
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

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.browse.manager.ProductManager;
import com.bbb.commerce.catalog.vo.AttributeVO;
//import com.bbb.commerce.catalog.vo.CategoryVO;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.commerce.catalog.vo.MediaVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.RollupTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.TabVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.utils.BBBUtility;

/**
 * This class is to populate the all info of product which is the part of
 * product VO.
 * 
 */
public class EverLivingDetailsDroplet extends BBBDynamoServlet {
	
	private static final String SIZE = "SIZE";
	private static final String IS_MAIN_PRODUCT = "isMainProduct";
		/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
		private ProductManager productManager;
		private BBBInventoryManager inventoryManager;
		private String providerID;

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
		public static final String CATEGORYID = "categoryId";
		public static final String PLACEHOLDER = "PDP";
		public static final String PRODUCT_ID_PARAMETER = "id";
		public final static String OPARAM_OUTPUT="output";
		public final static String OPARAM_ERROR="error";
		public final static String TABLOOKUP="tabLookUp";
		public final static String SKUID="skuId";
		public final static String REGISTRYID="registryId";
		public final static String INSTOCK="inStock";
		private static final String LINK_STRING = "linkStringNonRecproduct";
		private static final String RKGCOLLECTION_PROD_IDS = "rkgCollectionProdIds";
	
	
	/* ===================================================== *
 		GETTERS and SETTERS
	 * ===================================================== */
	
	
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
	public String getProviderID() {
		return providerID;
	}

	/**
	 * @param providerID the providerID to set
	 */
	public void setProviderID(String providerID) {
		this.providerID = providerID;
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
		String registryId = null;
		List<AttributeVO> pFirstAttributsVOList = null;
		List<TabVO> productTabs=null;
		List<MediaVO> media=null;
		CollectionProductVO collectionProductVO	= null;
		int inStockStatus = 0;
		boolean inStock = true;
		boolean liveClicker = true;
		final StringBuilder rkgProductIds = new StringBuilder();
		boolean isCollectionFlag= false;
		//boolean isLeadProduct= false;
		String color;
		boolean checkInventory= true;
		
		//Map<String, CategoryVO> categoryMap = null;
		//String categoryId =null;
		//int counter =1;
		
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
			boolean isMainProduct = false;
			if(pRequest.getObjectParameter(IS_MAIN_PRODUCT) != null){
				isMainProduct = Boolean.parseBoolean(pRequest.getParameter(IS_MAIN_PRODUCT));
			}
			registryId = pRequest.getParameter(REGISTRYID);
			/**
			 * siteId from the JSP page.if site id is null then get it from the
			 * SiteContextManager
			 */
			pSiteId = pRequest.getParameter(BBBCmsConstants.SITE_ID);
			if (pSiteId == null) {
				pSiteId = SiteContextManager.getCurrentSiteId();
			}
			
			if(null != pProductId){
				logDebug("pSiteId["+pSiteId+"]");
				logDebug("pProductId["+pProductId+"]");
				
				if(isMainProduct){
					productVO = getProductManager().getEverLivingMainProductDetails(pSiteId, pProductId);
				}
				else{
					productVO = getProductManager().getEverLivingProductDetails(pSiteId, pProductId);
				}
				media = getProductManager().getMediaDetails(pProductId, pSiteId);
				} else {
					logDebug("pProductId is NULL");
				}
				
			
			//added for R2 item 141
			color = pRequest.getParameter("color");
			
			if(null != productVO){
				
				//added for R2 item 141
				if(!BBBUtility.isEmpty(color)) {
					// if color is present,need to change rollupAttributes for size                             
					 if(!productVO.isCollection()){
						getColorSwatchDetail(productVO,color,isCollectionFlag);
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
					} 
				}
				
				if (pDefaultChildSku!=null) {
					boolean calculateAboveBelowLine = false;
					
//					if (!BBBUtility.isEmpty(registryId) ) {
//								calculateAboveBelowLine = true;
//		                        inStockStatus = getInventoryManager().getProductAvailability(pSiteId, pDefaultChildSku, BBBInventoryManager.ADD_ITEM_FROM_REG);
//		            //  }// else {
		            	  		calculateAboveBelowLine = true;
		            	  		checkInventory = Boolean.parseBoolean(pRequest.getParameter("checkInventory"));
		            	  		if(checkInventory){
		            	  			inStockStatus = getInventoryManager().getEverLivingProductAvailability(pSiteId, pDefaultChildSku, BBBInventoryManager.PRODUCT_DISPLAY);
		            	  		}
		            //  }
					pSKUDetailVO = getProductManager().getEverLivingSKUDetails(pSiteId,pDefaultChildSku, calculateAboveBelowLine);

					if(inStockStatus == BBBInventoryManager.NOT_AVAILABLE){
						inStock = false;
					}
					if(!BBBUtility.isEmpty(registryId) && pSKUDetailVO.isSkuBelowLine()){
						inStock = false;
					}
					logDebug("DefaultChildSku: "+pDefaultChildSku+" Instock: "+inStock);
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
						
						if (!BBBUtility.isEmpty(color) && !isMainProduct ) {
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
					}
					
					pRequest.setParameter(COLLECTIONVO, collectionProductVO);
					logDebug("LeadSKU["+collectionProductVO.getLeadSKU()+"]");
					
					if((null != collectionProductVO.getLeadSKU()) && (collectionProductVO.getLeadSKU())){
						pRequest.setParameter(TABLOOKUP, true);
						logDebug("LeadSKU["+collectionProductVO.getLeadSKU()+"]");
					} else {
						pRequest.setParameter(TABLOOKUP, false);
					}
					if(collectionProductVO !=null) {
						final List<ProductVO> childProducts = collectionProductVO.getChildProducts();
						int size = childProducts.size();
						int count =1;
						if(!BBBUtility.isListEmpty(childProducts)) {
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
					}
					pRequest.setParameter(RKGCOLLECTION_PROD_IDS, rkgProductIds);
				} else {
					pRequest.setParameter(TABLOOKUP, true);
				}
				
				if((null != media) && (!media.isEmpty())){
					final ListIterator<MediaVO> mediaIterator = media.listIterator();
					while(mediaIterator.hasNext()){
						final MediaVO mediaVO = mediaIterator.next();
						if(mediaVO.getProviderId().equals(providerID)){
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
				
				pRequest.setParameter(PRODUCTTAB, productTabs);
				pRequest.setParameter(INSTOCK, inStock);
				pRequest.setParameter(SKUDETAILVO, pSKUDetailVO);				
				pRequest.setParameter(PRODUCTVO, productVO);
				pRequest.setParameter(FIRSTATTRIBUTSVOLIST, pFirstAttributsVOList);
				if(productVO.getChildSKUs() != null){
					pRequest.setParameter(FIRSTCHILDSKU, productVO.getChildSKUs().get(0));
				}
				//Adding user token for bazaar voice 
				pRequest.setParameter(BBBCoreConstants.USER_TOKEN_BVRR, pRequest.getSession().getAttribute(BBBCoreConstants.USER_TOKEN_BVRR));
				
				
				//Below code to generate certona link string for non recomended product
				final StringBuilder linkString = new StringBuilder("");
					linkString.append(pProductId).append(';');
				if(collectionProductVO !=null) {
					final List<ProductVO> childProducts = collectionProductVO.getChildProducts();
					if(childProducts != null) {
						for(ProductVO items: childProducts) {
								linkString.append(items.getProductId()).append(';');
								
						}
					}
				}
				pRequest.setParameter(LINK_STRING, linkString); 
				pRequest.serviceParameter(OPARAM_OUTPUT, pRequest,
					pResponse);
			}else{
				pRequest.serviceParameter(OPARAM_ERROR, pRequest,
						pResponse);
			}

		} catch (BBBBusinessException bbbbEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Business Exception from service of ProductDetailDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1032),bbbbEx);
			pRequest.serviceParameter(OPARAM_ERROR, pRequest,
					pResponse);
		} catch (BBBSystemException bbbsEx) {
			logError(LogMessageFormatter.formatMessage(pRequest, "System Exception from service of ProductDetailDroplet for productId=" +pProductId +" |skuId="+pSKUId +" |SiteId="+pSiteId,BBBCoreErrorConstants.BROWSE_ERROR_1033),bbbsEx);
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

		rollUpList=getProductManager().getEverLivingRollupDetails(productVO.getProductId(), color, firstRollUpType, secondRollUpType);     
		if(rollUpList == null || rollUpList.isEmpty() ){
			firstRollUpType="finish"; 
			rollUpList=getProductManager().getEverLivingRollupDetails(productVO.getProductId(), color, firstRollUpType, secondRollUpType);  	
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

					productVO.getProductImages().setThumbnailImage(
							rollupTypeVO.getThumbnailImagePath());
					break; 
				}
			}
		} 
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
		if(collectionProductVO !=null)
		{
			if(null != collectionProductVO.getChildProducts()){
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
														List<RollupTypeVO> rollUpValues = getProductManager().getEverLivingRollupDetails(product.getProductId(), rollUpVO.getRollupAttribute(), rollupString, null);
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
		}
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
								List<RollupTypeVO> rollUpValues = getProductManager().getEverLivingRollupDetails(product.getProductId(), rollUp, rollupKey,rollUpKeys.toArray()[1].toString());
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

	
	
}



