package com.bbb.ecommerce.order;

import java.util.Map;

import com.bbb.commerce.order.TBSShippingInfo;

import atg.commerce.order.ChangedProperties;
import atg.commerce.order.ShippingGroupImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;

/** This class is extended for Store Shipping group item implementation
 *
 * @author jpadhi
 * @version $Revision: #1 $ */
public class BBBStoreShippingGroup extends ShippingGroupImpl implements BBBShippingGroup {

    private static final long serialVersionUID = 1L;
    private static final String STORE_ID = "storeId";
    private static final String ECO_FEE_MAP = "ecoFeeItemMap";
    
    private static final String TBS_SHIP_INFO = "tbsShipInfo";
    
    private TBSShippingInfo mTBSShipInfo;

	public TBSShippingInfo getTbsShipInfo() {
		return mTBSShipInfo;
	}

	public void setTbsShipInfo(TBSShippingInfo pTBSShipInfo) {
		mTBSShipInfo = pTBSShipInfo;
		MutableRepositoryItem repItem = ((ChangedProperties) pTBSShipInfo).getRepositoryItem();
		setTBShipInfoRepositoryItem(repItem);
	}
    
	/**
	 * @return the tBSItemInfoRepositoryItem
	 */
	public MutableRepositoryItem getTBSShipInfoRepositoryItem() {
		return (MutableRepositoryItem) getPropertyValue(TBS_SHIP_INFO);
	}

	/**
	 * @param pTBSItemInfoRepositoryItem the tBSItemInfoRepositoryItem to set
	 */
	public void setTBShipInfoRepositoryItem(RepositoryItem pTBSShipInfoRepositoryItem) {
		setPropertyValue(TBS_SHIP_INFO, pTBSShipInfoRepositoryItem);
	}


    /** Gets the value of freeShippingMethod. */
    public  String getStoreId() {
        return (String) this.getPropertyValue(STORE_ID);
    }


    /** Sets the value to parentCommerceItem. */
    public final void setStoreId(final String pStoreId) {
        this.setPropertyValue(STORE_ID, pStoreId);
    }

    @Override
    @SuppressWarnings ("unchecked")
    public  Map<String, String> getEcoFeeItemMap() {
        return (Map<String, String>) this.getPropertyValue(ECO_FEE_MAP);
    }

    @Override
    public final void setEcoFeeItemMap(final Map<String, String> pEcoFeeItemMap) {
        this.setPropertyValue(ECO_FEE_MAP, pEcoFeeItemMap);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("BBBStoreShippingGroup [getStoreId()=").append(this.getStoreId())
                        .append(", getEcoFeeItemMap()=").append(this.getEcoFeeItemMap())
                        .append(", getShippingGroupClassType()=").append(this.getShippingGroupClassType())
                        .append(", getShippingMethod()=").append(this.getShippingMethod())
                        .append(", getDescription()=").append(this.getDescription()).append(", getState()=")
                        .append(this.getState()).append(", getPriceInfo()=").append(this.getPriceInfo())
                        .append(", getShipOnDate()=").append(this.getShipOnDate()).append(", getActualShipDate()=")
                        .append(this.getActualShipDate()).append(", getSubmittedDate()=")
                        .append(this.getSubmittedDate()).append(", getCommerceItemRelationships()=")
                        .append(this.getCommerceItemRelationships()).append(", getCommerceItemRelationshipCount()=")
                        .append(this.getCommerceItemRelationshipCount()).append(", getPaymentGroupRelationships()=")
                        .append(this.getPaymentGroupRelationships()).append(", getPaymentGroupRelationshipCount()=")
                        .append(this.getPaymentGroupRelationshipCount()).append(", getId()=").append(this.getId())
                        .append("]");
        return builder.toString();
    }
}
