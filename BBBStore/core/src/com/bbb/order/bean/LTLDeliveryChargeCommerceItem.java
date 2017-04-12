package com.bbb.order.bean;

import java.util.Date;



/**
 * Holder for LTLDeliveryChargeCommerceItem.
 * @author ATG
 * @version $Revision: #1 $
 */
public class LTLDeliveryChargeCommerceItem extends NonMerchandiseCommerceItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5638460658620769245L;
	
	private static final String LTL_ASSOC_COMMERCE_ITEM_ID = "ltlCommerceItemRelation";
	private static final String LAST_MODIFIED_DATE = "lastModifiedDate";
	
	 /**
	 * @return the ltlAssocCommerceItemId
	 */
	public String getLtlCommerceItemRelation() {
		if (this.getPropertyValue(LTL_ASSOC_COMMERCE_ITEM_ID) != null) {
            return ((String) this.getPropertyValue(LTL_ASSOC_COMMERCE_ITEM_ID));
        }
		return null;
	}
	
	/**
	 * @param ltlAssocCommerceItemId
	 */
	public void setLtlCommerceItemRelation(final String ltlAssocCommerceItemId) {
		this.setPropertyValue(LTL_ASSOC_COMMERCE_ITEM_ID, ltlAssocCommerceItemId);
	}
	
	/** Gets the value of lastModifiedDate.
    *
    * @return Last Modified Date */
   public Date getLastModifiedDate() {
       return (Date) this.getPropertyValue(LAST_MODIFIED_DATE);
   }

   /** Sets the value to lastModifiedDate.
    *
    * @param pLastModifiedDate */
   public void setLastModifiedDate(final Date pLastModifiedDate) {
       this.setPropertyValue(LAST_MODIFIED_DATE, pLastModifiedDate);
   }
	
}
