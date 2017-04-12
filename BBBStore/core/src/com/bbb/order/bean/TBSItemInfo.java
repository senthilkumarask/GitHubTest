package com.bbb.order.bean;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceIdentifierImpl;
import atg.commerce.order.Constants;
import atg.repository.MutableRepositoryItem;

import com.bbb.constants.TBSConstants;

public class TBSItemInfo extends CommerceIdentifierImpl implements ChangedProperties{

	private static final long serialVersionUID = 1L;

	private MutableRepositoryItem mRepositoryItem;
	
	private boolean mSaveAllProperties = false;

	private boolean mChanged = false;

	private HashSet mChangedProperties = null;
	
	public MutableRepositoryItem getRepositoryItem() {
		return this.mRepositoryItem;
	}

	public void setRepositoryItem(MutableRepositoryItem pRepositoryItem) {
		this.mRepositoryItem = pRepositoryItem;
	}
	
	/**
	 * @return the configId
	 */
	public String getConfigId() {
		return ((String) getPropertyValue(TBSConstants.CONFIG_ID));
	}

	/**
	 * @return the productDesc
	 */
	public String getProductDesc() {
		return ((String) getPropertyValue(TBSConstants.PRODUCT_DESC));
	}

	/**
	 * @return the productImage
	 */
	public String getProductImage() {
		return ((String) getPropertyValue(TBSConstants.PRODUCT_IMG));
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return ((String) getPropertyValue(TBSConstants.ERROR_CODE));
	}

	/**
	 * @return the errorName
	 */
	public String getErrorName() {
		return ((String) getPropertyValue(TBSConstants.ERROR_NAME));
	}

	/**
	 * @return the retailPrice
	 */
	public double getRetailPrice() {
		Double price = ((Double) getPropertyValue(TBSConstants.RETAIL_PRICE));
		return ((price == null) ? 0.0 : price.doubleValue());
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		Double price = ((Double) getPropertyValue(TBSConstants.COST));
		return ((price == null) ? 0.0 : price.doubleValue());
	}

	/**
	 * @return the overridePrice
	 */
	public double getOverridePrice() {
		Double price = ((Double) getPropertyValue(TBSConstants.OVERRIDE_PRICE));
		return ((price == null) ? 0.0 : price.doubleValue());
	}

	/**
	 * @return the overrideQuantity
	 */
	public int getOverrideQuantity() {
		Integer quantity = ((Integer) getPropertyValue(TBSConstants.OVERRIDE_QUANTITY));
		return ((quantity == null) ? 0 : quantity.intValue());
	}

	/**
	 * @return the isPriceOveride
	 */
	public boolean isPriceOveride() {
		Boolean overrideflag = ((Boolean) getPropertyValue(TBSConstants.IS_PRICE_OVERIDE));
		return ((overrideflag == null) ? false : overrideflag.booleanValue());
	}

	public String getOverideReason() {
		String reason = (String)getPropertyValue(TBSConstants.OVERRIDE_REASON);
		return reason;
	}

	public String getCompetitor() {
		String competitor = (String)getPropertyValue(TBSConstants.COMPETITOR);
		return competitor;
	}

	/**
	 * @param pChangedProperties the changedProperties to set
	 */
	public void setChangedProperties(HashSet pChangedProperties) {
		mChangedProperties = pChangedProperties;
	}

	/**
	 * @param pConfigId the configId to set
	 */
	public void setConfigId(String pConfigId) {
		setPropertyValue(TBSConstants.CONFIG_ID, pConfigId);
	}

	/**
	 * @param pProductDesc the productDesc to set
	 */
	public void setProductDesc(String pProductDesc) {
		setPropertyValue(TBSConstants.PRODUCT_DESC, pProductDesc);
	}

	/**
	 * @param pProductImage the productImage to set
	 */
	public void setProductImage(String pProductImage) {
		setPropertyValue(TBSConstants.PRODUCT_IMG, pProductImage);
	}

	/**
	 * @param pErrorCode the errorCode to set
	 */
	public void setErrorCode(String pErrorCode) {
		setPropertyValue(TBSConstants.ERROR_CODE, pErrorCode);
	}

	/**
	 * @param pErrorName the errorName to set
	 */
	public void setErrorName(String pErrorName) {
		setPropertyValue(TBSConstants.ERROR_NAME, pErrorName);
	}

	/**
	 * @param pRetailPrice the retailPrice to set
	 */
	public void setRetailPrice(double pRetailPrice) {
		setPropertyValue(TBSConstants.RETAIL_PRICE, Double.valueOf(pRetailPrice));
	}

	/**
	 * @param pCost the cost to set
	 */
	public void setCost(double pCost) {
		setPropertyValue(TBSConstants.COST, Double.valueOf(pCost));
	}

	/**
	 * @param pOverridePrice the overridePrice to set
	 */
	public void setOverridePrice(double pOverridePrice) {
		setPropertyValue(TBSConstants.OVERRIDE_PRICE, Double.valueOf(pOverridePrice));
	}

	/**
	 * @param pOverrideQuantity the overrideQuantity to set
	 */
	public void setOverrideQuantity(int pOverrideQuantity) {
		setPropertyValue(TBSConstants.OVERRIDE_QUANTITY, Integer.valueOf(pOverrideQuantity));
	}

	/**
	 * @param pIsPriceOveride the isPriceOveride to set
	 */
	public void setPriceOveride(boolean pIsPriceOveride) {
		setPropertyValue(TBSConstants.IS_PRICE_OVERIDE, Boolean.valueOf(pIsPriceOveride));
	}

	public void setOverideReason(String pOverideReason) {
		setPropertyValue(TBSConstants.OVERRIDE_REASON, pOverideReason);
	}

	public void setCompetitor(String pCompetitor) {
		setPropertyValue(TBSConstants.COMPETITOR, pCompetitor);
	}

	public Object getPropertyValue(String pPropertyName) {
		MutableRepositoryItem mutItem = getRepositoryItem();
		if (mutItem == null) {
			throw new RuntimeException(MessageFormat.format(
					Constants.NULL_REPITEM_IN_COMMERCEITEM,
					new Object[] {  }));
		}
		return mutItem.getPropertyValue(pPropertyName);
	}

	public void setPropertyValue(String pPropertyName, Object pPropertyValue) {
		MutableRepositoryItem mutItem = getRepositoryItem();

		if (mutItem == null) {
			throw new RuntimeException(MessageFormat.format(
					Constants.NULL_REPITEM_IN_COMMERCEITEM,
					new Object[] {  }));
		}
		mutItem.setPropertyValue(pPropertyName, pPropertyValue);
		setChanged(true);
	}

	@Override
	public void update(Observable pO, Object pArg) {
		if (pArg instanceof String)
			addChangedProperty((String) pArg);
		else
			throw new RuntimeException("Observable update for "
					+ super.getClass().getName()
					+ " was received with arg type " + pArg.getClass().getName()
					+ ":" + pArg);
		
	}

	@Override
	public boolean getSaveAllProperties() {
		return this.mSaveAllProperties;
	}

	@Override
	public void setSaveAllProperties(boolean pSaveAllProperties) {
		this.mSaveAllProperties = pSaveAllProperties;
	}

	@Override
	public boolean isChanged() {
		return ((this.mChanged) || ((this.mChangedProperties != null) && (!(getChangedProperties()
				.isEmpty()))));
	}

	@Override
	public void setChanged(boolean pChanged) {
		this.mChanged = pChanged;
		
	}

	@Override
	public Set getChangedProperties() {
		if (this.mChangedProperties == null)
			this.mChangedProperties = new HashSet(7);
		return this.mChangedProperties;
	}

	@Override
	public void addChangedProperty(String pPropertyName) {
		getChangedProperties().add(pPropertyName);
	}

	@Override
	public void clearChangedProperties() {
		if (this.mChangedProperties == null)
			return;
		this.mChangedProperties.clear();
	}
	
	public String toString(){
		
	return "TBSItemInfo [mRepositoryItem= " + mRepositoryItem + " mSaveAllProperties= " + mSaveAllProperties + " mChanged "
	+ mChanged + " mChangedProperties " + " Configid " + getConfigId();
	
	}
}
