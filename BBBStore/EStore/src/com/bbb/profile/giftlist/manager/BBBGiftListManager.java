package com.bbb.profile.giftlist.manager;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;



/** 
 * Extensions to the atg.commerce.gifts.GiftlistFormHandler.
 *
 * @see atg.commerce.gifts.GiftlistFormHandler
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.2/EStore/src/atg/projects/store/gifts/StoreGiftlistFormHandler.java#2 $
 * @updated $DateTime: 2011/02/09 11:14:12 $$Author: rbarbier $
 */
public class BBBGiftListManager extends GiftlistManager {

	
	private static final String MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY = "UNABLE TO RETRIEVE DATA FROM REPOSITORY";
	private static final String MSG_PARENT_PRODUCT_NOT_FOUND = "ATG_ISSUE:PARENT PRODUCT NOT FOUND FOR SKU";
	private static final String PROFILE_WISH_LIST_MIGRATION = "Profile-WishListMigration:";
	private static final String STORE_SKU = "storeSKU";
	private static final String MSG_ERROR_PROFILE_NOT_MIGRATED = "ATG_ISSUE:PROFILE NOT MIGRATED";
	private static final String MSG_ERROR_INVALID_SKU_IN_FEED = "FEED_DATA_ISSUE:SKU NOT VALID IN FEED";
	private static final String MSG_ERROR_STORE_SKU_IN_FEED = "FEED_DATA_ISSUE:STORE_SKU_IN_ATG";
	//private static final String MSG_ERROR_INVALID_PRODUCT_IN_FEED = "FEED_DATA_ISSUE:PRODUCT NOT VALID IN FEED";
	private static final String MSG_ERROR_GIFTLIST_ITEM_ALREADY_EXIST = "ATG_ISSUE:SKIP ITEM MERGE AS GIFT LIST ALREADY EXIST";
	private static final String MSG_ERROR_NEW_GIFTLIST_EXIST_QTYINVALID = "FEED_DATA_ISSUE:WISHLIST MERGE IS ALLOWED BUT QTY IS 0";
	private static final String MSG_ERROR_INVALID_QTY = "FEED_DATA_ISSUE:QUANTITY INVALID IN FEED";
	private static final String MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM = "ATG_ISSUE:Error in creating new gift list item";
	private BBBCatalogTools mBBBCatalogTools;
	
	/**
	 * Overload OOB method for two reasons 
	 * 	<li>OOB method merge skus - increase quantity. But in Migration we have to skip merge for existing WishLists</li>
	 * 	<li>For future extension, we can control merge using isMergeWishList boolean </li>
	 * @param pSkuId
	 * @param pProductId
	 * @param pGiftlistId
	 * @param pSiteId
	 * @param pQuantity
	 * @param isMergeWishList
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 * @throws CommerceException
	 * @throws RepositoryException
	 */
    public String addCatalogItemToGiftlist(String pSkuId, String pProductId, String pGiftlistId, 
    	String pSiteId, long pQuantity, boolean isMergeWishList) 
    {

		String displayName = null;
        String description = null;
        String productId = null;
        String skuId = null;
        String giftId = null;
        RepositoryItem sku = null;
        RepositoryItem product = null;
    	
		String errorMsg = null;

		if (pGiftlistId == null) {
        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_PROFILE_NOT_MIGRATED);
			return MSG_ERROR_PROFILE_NOT_MIGRATED;
		}
        
        try
        {
            sku = getCatalogTools().findSKU(pSkuId);
            if(sku == null){
            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_INVALID_SKU_IN_FEED);
            	return MSG_ERROR_INVALID_SKU_IN_FEED;
            }
        }  catch(RepositoryException exc)    {
        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_INVALID_SKU_IN_FEED);
        	return MSG_ERROR_INVALID_SKU_IN_FEED;
        }
        boolean isStoreSku=(Boolean)sku.getPropertyValue(STORE_SKU);
        if(isStoreSku){
        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_STORE_SKU_IN_FEED);
        	return MSG_ERROR_STORE_SKU_IN_FEED;
        }
        skuId = sku.getRepositoryId();
        
        try {
			productId=getBBBCatalogTools().getParentProductForSku(skuId,true);
			if(BBBUtility.isEmpty(productId)){
				logError(PROFILE_WISH_LIST_MIGRATION+MSG_PARENT_PRODUCT_NOT_FOUND);
	        	return MSG_PARENT_PRODUCT_NOT_FOUND;
			}			
		} catch (BBBBusinessException e) {
			logError(PROFILE_WISH_LIST_MIGRATION+MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
        	return MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY;
		} catch (BBBSystemException e) {
			logError(PROFILE_WISH_LIST_MIGRATION+MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY);
        	return MSG_UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY;
		}
        
        try{
        		product = getCatalogTools().findProduct(productId);
        		
        	}catch (RepositoryException exc){
        		logError("Profile-WishListMigration:"+MSG_PARENT_PRODUCT_NOT_FOUND);
        		return MSG_PARENT_PRODUCT_NOT_FOUND;
        	}

        if(product != null)
        {
            productId = product.getRepositoryId();
            displayName = (String)product.getPropertyValue(getGiftlistTools().getDisplayNameProperty());
            description = (String)product.getPropertyValue(getGiftlistTools().getDescriptionProperty());
        }
        giftId = getGiftlistItemId(pGiftlistId, skuId, productId, pSiteId);
        
        //Merge only when allowed
        if(giftId != null && isMergeWishList){
        	//also update only when quantity is greater than 0L
        	if(pQuantity > 0L) {
        		increaseGiftlistItemQuantityDesired(pGiftlistId, giftId, pQuantity);
        	} else{
        		//log Error that quantity is OL so skipping
            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_NEW_GIFTLIST_EXIST_QTYINVALID);
        		errorMsg =MSG_ERROR_NEW_GIFTLIST_EXIST_QTYINVALID;
        	}
        	
        } else if(giftId != null){
        	//Merge is not allowed
        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_GIFTLIST_ITEM_ALREADY_EXIST);
        	errorMsg = MSG_ERROR_GIFTLIST_ITEM_ALREADY_EXIST;
        	
        }
        else if(pQuantity > 0L)
        {
            try{
            	String itemId = createGiftlistItem(skuId, sku, productId, product, pQuantity, displayName, description, pSiteId);	
            	addItemToGiftlist(pGiftlistId, itemId);
            	
            } catch(CommerceException comExcep){
            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM);
            	errorMsg = MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM;
            } catch(RepositoryException repExcep) {
            	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM);
            	errorMsg = MSG_ERROR_CREATE_NEW_GIFTLIST_ITEM;
            }
        } else if( pQuantity <=0L){
        	logError(PROFILE_WISH_LIST_MIGRATION+MSG_ERROR_INVALID_QTY);
        	errorMsg = MSG_ERROR_INVALID_QTY;
        }            
        return errorMsg;
    }

	/**
	 * @return the mBBBCatalogTools
	 */
	public BBBCatalogTools getBBBCatalogTools() {
		return mBBBCatalogTools;
	}

	/**
	 * @param mBBBCatalogTools the mBBBCatalogTools to set
	 */
	public void setBBBCatalogTools(BBBCatalogTools mBBBCatalogTools) {
		this.mBBBCatalogTools = mBBBCatalogTools;
	}
}
