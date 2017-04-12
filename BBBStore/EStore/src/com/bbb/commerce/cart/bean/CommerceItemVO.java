package com.bbb.commerce.cart.bean;

import java.io.Serializable;
import java.util.Comparator;

import com.bbb.commerce.cart.PricingMessageVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.VendorInfoVO;
import com.bbb.order.bean.BBBCommerceItem;

public class CommerceItemVO implements Comparator<CommerceItemVO>,Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mStockAvailability;
	private BBBCommerceItem mBBBCommerceItem;
	private SKUDetailVO mSkuDetailVO;
	private PricingMessageVO mPriceMessageVO;
	private String ltlShipMethodDesc;
	private String sddAvailabilityStatus;
	private VendorInfoVO vendorInfoVO;

	
	public VendorInfoVO getVendorInfoVO() {
		return vendorInfoVO;
	}

	public void setVendorInfoVO(VendorInfoVO vendorInfoVO) {
		this.vendorInfoVO = vendorInfoVO;
	}

	public String getLtlShipMethodDesc() {
		return ltlShipMethodDesc;
	}

	public void setLtlShipMethodDesc(String ltlShipMethodDesc) {
		this.ltlShipMethodDesc = ltlShipMethodDesc;
	}

	/**
	 * @return the priceMessageVO
	 */
	public PricingMessageVO getPriceMessageVO() {
		return mPriceMessageVO;
	}

	/**
	 * @param pPriceMessageVO the priceMessageVO to set
	 */
	public void setPriceMessageVO(PricingMessageVO pPriceMessageVO) {
		mPriceMessageVO = pPriceMessageVO;
	}

	public BBBCommerceItem getBBBCommerceItem() {
		return mBBBCommerceItem;
	}

	public void setBBBCommerceItem(BBBCommerceItem pBBBCommerceItem) {
		this.mBBBCommerceItem = pBBBCommerceItem;
	}

	public SKUDetailVO getSkuDetailVO() {
		return mSkuDetailVO;
	}

	public void setSkuDetailVO(SKUDetailVO pSkuDetailVO) {
		this.mSkuDetailVO = pSkuDetailVO;
	}

	public int getStockAvailability() {
		return mStockAvailability;
	}

	public void setStockAvailability(int pStockAvailability) {
		this.mStockAvailability = pStockAvailability;
	}

	@Override
	public int compare(CommerceItemVO pParamT1, CommerceItemVO pParamT2) {
		Integer p1 = pParamT1.getBBBCommerceItem().getSeqNumber();
		Integer p2 = pParamT2.getBBBCommerceItem().getSeqNumber();

		  if (p1 > p2 ){
		   return 1;
		  }
		  else if (p1 < p2){
		   return -1;
		  }
		  else
		   return 0;
	 }

	public String getSddAvailabilityStatus() {
		return sddAvailabilityStatus;
	}

	public void setSddAvailabilityStatus(String sddAvailabilityStatus) {
		this.sddAvailabilityStatus = sddAvailabilityStatus;
	}
	
	

}
