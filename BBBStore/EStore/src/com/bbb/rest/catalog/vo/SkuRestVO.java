package com.bbb.rest.catalog.vo;

import java.io.Serializable;

import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.exim.bean.EximKatoriResponseVO;
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.vo.wishlist.LTLWishListDslVO;

/**
 * @author rsain4
 *
 */
public class SkuRestVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SKUDetailVO skuVO;
	private double listPrice;
	private double salePrice;
	private int inventoryStatus;
	private CatalogItemAttributesVO skuAllAttributeVO ;
	private boolean isErrorExist;
	private String errorMsg;
	private String listPriceTBD;
	private String salePriceTBD;
	private boolean pdpHeartIconFilled;
	private EximKatoriResponseVO eximKatoriResponse;
	private String formattedListPrice;
	private String formattedSalePrice;
	private LTLWishListDslVO savedDsl;
	
	public EximKatoriResponseVO getEximKatoriResponse() {
		return eximKatoriResponse;
	}

	public void setEximKatoriResponse(EximKatoriResponseVO eximKatoriResponse) {
		this.eximKatoriResponse = eximKatoriResponse;
	}

	/**
	 * @return skuAllAttributeVO
	 */
	public CatalogItemAttributesVO getSkuAllAttributeVO() {
		return this.skuAllAttributeVO;
	}

	/**
	 * @param skuAttributeVO
	 */
	public void setSkuAllAttributeVO(final CatalogItemAttributesVO skuAttributeVO) {
		this.skuAllAttributeVO = skuAttributeVO;
	}

	/**
	 * @return inventoryStatus
	 */
	public int getInventoryStatus() {
		return this.inventoryStatus;
	}

	/**
	 * @param inventoryStatus
	 */
	public void setInventoryStatus(final int inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	/**
	 * @return skuVO
	 */
	public SKUDetailVO getSkuVO() {
		return this.skuVO;
	}

	/**
	 * @param skuVO
	 */
	public void setSkuVO(final SKUDetailVO skuVO) {
		this.skuVO = skuVO;
	}

	/**
	 * @return listPrice
	 */
	public double getListPrice() {
		return this.listPrice;
	}

	/**
	 * @param listPrice
	 */
	public void setListPrice(final double listPrice) {
		this.listPrice = listPrice;
	}

	/**
	 * @return salePrice
	 */
	public double getSalePrice() {
		return this.salePrice;
	}

	/**
	 * @param salePrice
	 */
	public void setSalePrice(final double salePrice) {
		this.salePrice = salePrice;
	}

	public boolean isErrorExist() {
		return isErrorExist;
	}

	public void setErrorExist(boolean isErrorExist) {
		this.isErrorExist = isErrorExist;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	/**Getter for listPriceTBD
	 * @return the listPriceTBD
	 */
	public String getListPriceTBD() {
		return listPriceTBD;
	}

	/**Setter for listPriceTBD
	 * @param listPriceTBD the listPriceTBD to set
	 */
	public void setListPriceTBD(String listPriceTBD) {
		this.listPriceTBD = listPriceTBD;
	}

	/**Getter for salePriceTBD
	 * @return the salePriceTBD
	 */
	public String getSalePriceTBD() {
		return salePriceTBD;
	}

	/**Setter for salePriceTBD
	 * @param salePriceTBD the salePriceTBD to set
	 */
	public void setSalePriceTBD(String salePriceTBD) {
		this.salePriceTBD = salePriceTBD;
	}

	public boolean isPdpHeartIconFilled() {
		return pdpHeartIconFilled;
	}

	public void setPdpHeartIconFilled(boolean pdpHeartIconFilled) {
		this.pdpHeartIconFilled = pdpHeartIconFilled;
	}

	public String getFormattedListPrice() {
		return formattedListPrice;
	}

	public void setFormattedListPrice(String formattedListPrice) {
		this.formattedListPrice = formattedListPrice;
	}

	public String getFormattedSalePrice() {
		return formattedSalePrice;
	}

	public void setFormattedSalePrice(String formattedSalePrice) {
		this.formattedSalePrice = formattedSalePrice;
	}

	public LTLWishListDslVO getSavedDsl() {
		return savedDsl;
	}

	public void setSavedDsl(LTLWishListDslVO savedDsl) {
		this.savedDsl = savedDsl;
	}	

}
