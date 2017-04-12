package com.bbb.order.bean;

import atg.commerce.order.ChangedProperties;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;


/**
 * Holder for non merchandise commerce item
 * 
 * @author msiddi
 * @story UC_Checkout_Item_Promo
 * @version 1.0
 */

public class NonMerchandiseCommerceItem extends BaseCommerceItemImpl {

    /**
     * 
     */
    private static final long serialVersionUID = -7595128449188114303L;
    
	public static final String TBSITEMINFO = "tBSItemInfo";

	private TBSItemInfo mTBSItemInfo;

	/**
	 * @return the tBSItemInfo
	 */
	public TBSItemInfo getTBSItemInfo() {
		return mTBSItemInfo;
	}

	/**
	 * @param pTBSItemInfo the tBSItemInfo to set
	 */
	public void setTBSItemInfo(TBSItemInfo pTBSItemInfo) {
		mTBSItemInfo = pTBSItemInfo;
		MutableRepositoryItem repItem = ((ChangedProperties) pTBSItemInfo).getRepositoryItem();
		setTBSItemInfoRepositoryItem(repItem);
	}

	/**
	 * @return the tBSItemInfoRepositoryItem
	 */
	public MutableRepositoryItem getTBSItemInfoRepositoryItem() {
		return (MutableRepositoryItem) getPropertyValue(TBSITEMINFO);
	}

	/**
	 * @param pTBSItemInfoRepositoryItem the tBSItemInfoRepositoryItem to set
	 */
	public void setTBSItemInfoRepositoryItem(RepositoryItem pTBSItemInfoRepositoryItem) {
		setPropertyValue(TBSITEMINFO, pTBSItemInfoRepositoryItem);
	}


}
