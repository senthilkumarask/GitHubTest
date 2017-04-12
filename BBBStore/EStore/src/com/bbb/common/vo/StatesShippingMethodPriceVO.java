/**
 * 
 */
package com.bbb.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.bbb.constants.BBBCmsConstants;


/**
 * @author vagra4
 * 
 */
public class StatesShippingMethodPriceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal mMinOrderPrice;
	private BigDecimal mMaxOrderPrice;	
	private String mStateName;
	
	private List<AppShipMethodPriceVO> mAppShipMethodPriceVO;
	
	private String mDisplayRange;
	
	
	
	public BigDecimal getMinOrderPrice() {
		return mMinOrderPrice;
	}


	public void setMinOrderPrice(final BigDecimal minOrderPrice) {
		this.mMinOrderPrice = minOrderPrice;
	}


	public BigDecimal getMaxOrderPrice() {
		return mMaxOrderPrice;
	}


	public void setMaxOrderPrice(final BigDecimal pMaxOrderPrice) {
		this.mMaxOrderPrice = pMaxOrderPrice;
	}


	public String getStateName() {
		return mStateName;
	}


	public void setStateName(final String pStateName) {
		this.mStateName = pStateName;
	}


	public List<AppShipMethodPriceVO> getAppShipMethodPriceVO() {
		return mAppShipMethodPriceVO;
	}


	public void setAppShipMethodPriceVO(final 
			List<AppShipMethodPriceVO> pAppShipMethodPriceVO) {
		this.mAppShipMethodPriceVO = pAppShipMethodPriceVO;
	}

	

	public String getDisplayRange() {		
		
		if(this.getMinOrderPrice().floatValue() <= 0.0){
			mDisplayRange = "Up to "+ (BBBCmsConstants.DOLLOR + this.getMaxOrderPrice());
		}else{
			mDisplayRange = BBBCmsConstants.DOLLOR + this.getMinOrderPrice() 
								+ " - " + BBBCmsConstants.DOLLOR + this.getMaxOrderPrice();
		}
		
		return mDisplayRange;
	}
	
     public String getDisplayRangeMax() {		
	    mDisplayRange = BBBCmsConstants.DOLLOR + this.getMinOrderPrice() 
								+ " - and above";
	 	return mDisplayRange;
	}


	public void setDisplayRange(final String pDisplayRange) {
		this.mDisplayRange = pDisplayRange;
	}


	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("StatesShippingMethodPriceVO [mMinOrderPrice=");
		builder.append(mMinOrderPrice);
		builder.append(", mMaxOrderPrice=");
		builder.append(mMaxOrderPrice);
		builder.append(", mStateName=");
		builder.append(mStateName);
		builder.append(", mAppShipMethodPriceVO=");
		
		if(mAppShipMethodPriceVO != null){
			for(AppShipMethodPriceVO current : mAppShipMethodPriceVO){
				builder.append(current);
			}
		}		
		
		builder.append("]");
		return builder.toString();
	}
	
	
	
	
}
