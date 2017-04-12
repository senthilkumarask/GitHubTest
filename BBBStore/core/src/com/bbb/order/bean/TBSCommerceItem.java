package com.bbb.order.bean;

import java.util.Set;

import com.bbb.constants.TBSConstants;

import atg.commerce.order.ChangedProperties;
import atg.core.util.StringUtils;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

public class TBSCommerceItem extends BBBCommerceItem {
	
private static final long serialVersionUID = 1L;
	
	public static final String TBSITEMINFO = "tBSItemInfo";
	
	private TBSItemInfo mTBSItemInfo;
	
	public static final String SHIPTIME = "ShipTime";
	
	private boolean pdpUrlflag = true;
	private int splitSequence;
	
	private static final String AUTO_WAIVE_FLAG = "autoWaiveFlag";
	private static final String AUTO_WAIVE_CLASSIFICATION = "autoWaiveClassification";
	private static final String STORE_ONHAND_QTY = "storeOnhandQty";
	
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

	/**
	 * @return the mShipTime
	 */
	public String getShipTime() {
		if (getPropertyValue(SHIPTIME) != null) {
			return (String) getPropertyValue(SHIPTIME);
		}
		return null;
	}

	/**
	 * @param pShipTime the mShipTime to set
	 */
	public void setShipTime(String pShipTime) {
		setPropertyValue(SHIPTIME, pShipTime);
	}

	/**
	 * @return the pdpUrlflag
	 */
	public boolean isPdpUrlflag() {
		return pdpUrlflag;
	}

	/**
	 * @param pPdpUrlflag the pdpUrlflag to set
	 */
	public void setPdpUrlflag(boolean pPdpUrlflag) {
		pdpUrlflag = pPdpUrlflag;
	}
	
	public String getGsOrderId() {
		return (String)this.getPropertyValue(TBSConstants.GS_ORDER_ID);
	}
	public void setGsOrderId(String pGsOrderId) {
		this.setPropertyValue(TBSConstants.GS_ORDER_ID, pGsOrderId);
	}
	
	public boolean isAutoWaiveFlag() {
		if(getPropertyValue(AUTO_WAIVE_FLAG) != null){
			return ((Boolean)getPropertyValue(AUTO_WAIVE_FLAG)).booleanValue();
		} else {
			return false;
		}
	}

	public void setAutoWaiveFlag(boolean autoWaiveFlag) {
		setPropertyValue(AUTO_WAIVE_FLAG, Boolean.valueOf(autoWaiveFlag));
	}

	public void setAutoWaiveClassification(String classification) {
		setPropertyValue(AUTO_WAIVE_CLASSIFICATION, classification);
	}

	public String getAutoWaiveClassification() {
		return (String)getPropertyValue(AUTO_WAIVE_CLASSIFICATION);
	}
	
	public void setStoreOnhandQty(int qty) {
		setPropertyValue(STORE_ONHAND_QTY, new Integer(qty));
	}

	public int getStoreOnhandQty() {
		Integer qty = (Integer)getPropertyValue(STORE_ONHAND_QTY);
		if( qty != null ) {
			return qty.intValue();
		}
		return 0;
	}

	/**
	 * Checks if commerce item is kirsch.
	 *
	 * @return true, if commerce item is kirsch
	 */
	public boolean isKirsch(){
		
		 Set<RepositoryItem> skuAttrRelation = null;
		 RepositoryItem skuAttribute = null;
		 String skuAttrId = null;
		
		RepositoryItem skuItem = (RepositoryItem) getAuxiliaryData().getCatalogRef();
		if(skuItem != null){
			skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
		}
		if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
			for (RepositoryItem skuAttrReln : skuAttrRelation) {
				skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
				if(skuAttribute != null){
					skuAttrId = skuAttribute.getRepositoryId();
				}
				if(!StringUtils.isBlank(skuAttrId) && skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Checks if commerce item is cmo.
	 *
	 * @return true, if commerce item is cmo
	 */
	public boolean isCMO(){
		
		 Set<RepositoryItem> skuAttrRelation = null;
		 RepositoryItem skuAttribute = null;
		 String skuAttrId = null;
		
		RepositoryItem skuItem = (RepositoryItem) getAuxiliaryData().getCatalogRef();
		if(skuItem != null){
			skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
		}
		if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
			for (RepositoryItem skuAttrReln : skuAttrRelation) {
				skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
				if(skuAttribute != null){
					skuAttrId = skuAttribute.getRepositoryId();
				}
				if(!StringUtils.isBlank(skuAttrId) && skuAttrId.equals(TBSConstants.CMO_SKU_ATTRIBUTE)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @return the splitSequence
	 */
	public int getSplitSequence() {
		return splitSequence;
	}

	/**
	 * @param pSplitSequence the splitSequence to set
	 */
	public void setSplitSequence(int pSplitSequence) {
		splitSequence = pSplitSequence;
	}

	
}
