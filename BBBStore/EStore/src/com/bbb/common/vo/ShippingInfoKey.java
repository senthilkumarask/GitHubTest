package com.bbb.common.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ShippingInfoKey implements Comparable<ShippingInfoKey>,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal minOrderPrice;
	private BigDecimal maxOrderPrice;
 	public BigDecimal getMinOrderPrice() {
		return minOrderPrice;
	}

	public void setMinOrderPrice(BigDecimal minOrderPrice) {
		this.minOrderPrice = minOrderPrice;
	}

	public BigDecimal getMaxOrderPrice() {
		return maxOrderPrice;
	}

	public void setMaxOrderPrice(BigDecimal maxOrderPrice) {
		this.maxOrderPrice = maxOrderPrice;
	}

	public int compareTo(final ShippingInfoKey shippingInfoKey) {
		
		int compare = 1;
		
		if(this.minOrderPrice == shippingInfoKey.getMinOrderPrice()
				&& this.maxOrderPrice == shippingInfoKey.getMaxOrderPrice()){
			compare = 0;
		}else if(this.minOrderPrice.floatValue() < shippingInfoKey.getMinOrderPrice().floatValue()){
			compare = -1;
		}
		
		return compare;		
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((maxOrderPrice == null) ? 0 : maxOrderPrice.hashCode());
		result = prime * result
				+ ((minOrderPrice == null) ? 0 : minOrderPrice.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		final ShippingInfoKey other = (ShippingInfoKey) obj;
		if (maxOrderPrice == null) {
			if (other.maxOrderPrice != null){
				return false;
			}
		} else if (!maxOrderPrice.equals(other.maxOrderPrice)){
			return false;
		}
		if (minOrderPrice == null) {
			if (other.minOrderPrice != null){
				return false;
			}
		} else if (!minOrderPrice.equals(other.minOrderPrice)){
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("ShippingInfoKey [mMinOrderPrice=");
		builder.append(minOrderPrice);
		builder.append(", mMaxOrderPrice=");
		builder.append(maxOrderPrice);
		builder.append("]");
		return builder.toString();
	}
	
}
