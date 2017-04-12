package com.bbb.commerce.order;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceIdentifierImpl;
import atg.commerce.order.Constants;
import atg.repository.MutableRepositoryItem;

import com.bbb.constants.TBSConstants;

public class TBSShippingInfo extends CommerceIdentifierImpl implements ChangedProperties {


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
		 * @return the isShipPriceOverride
		 */
		public boolean isShipPriceOverride() {
			Boolean isOverride = ((Boolean) getPropertyValue(TBSConstants.IS_SHIP_PRICE_OVERRIDE));
			return( (isOverride == null) ? false : isOverride.booleanValue() );
		}

		public void setShipPriceOverride(boolean override) {
			this.setPropertyValue(TBSConstants.IS_SHIP_PRICE_OVERRIDE,  Boolean.valueOf(override));
		}

		/**
		 * @return the shipPriceValue
		 */
		public double getShipPriceValue() {
			Double price = ((Double) getPropertyValue(TBSConstants.SHIP_PRICE_VALUE));
			return ((price == null) ? 0.0 : price.doubleValue());
		}

		public void setShipPriceValue(double shipValue) {
			this.setPropertyValue(TBSConstants.SHIP_PRICE_VALUE, Double.valueOf(shipValue));
		}

		/**
		 * @return the shipPriceReason
		 */
		public String getShipPriceReason() {
			String reason = ((String) getPropertyValue(TBSConstants.SHIP_PRICE_REASON));
			return reason;
		}

		public void setShipPriceReason(String reason) {
			this.setPropertyValue(TBSConstants.SHIP_PRICE_REASON, reason);
		}

		/**
		 * @return the isSurchargeOverride
		 */
		public boolean isSurchargeOverride() {
			Boolean isOverride = ((Boolean) getPropertyValue(TBSConstants.IS_SURCHARGE_OVERRIDE));
			return( (isOverride == null) ? false : isOverride.booleanValue() );
		}

		public void setSurchargeOverride(boolean override) {
			this.setPropertyValue(TBSConstants.IS_SURCHARGE_OVERRIDE, Boolean.valueOf(override));
		}

		/**
		 * @return the surchargeValue
		 */
		public double getSurchargeValue() {
			Double price = ((Double) getPropertyValue(TBSConstants.SURCHARGE_VALUE));
			return ((price == null) ? 0.0 : price.doubleValue());
		}

		public void setSurchargeValue(double surcharge) {
			this.setPropertyValue(TBSConstants.SURCHARGE_VALUE, Double.valueOf(surcharge));
		}

		/**
		 * @return the surchargeReason
		 */
		public String getSurchargeReason() {
			return ((String) getPropertyValue(TBSConstants.SURCHARGE_REASON));
		}
		
		public void setSurchargeReason(String reason) {
			this.setPropertyValue(TBSConstants.SURCHARGE_REASON, reason);
		}

		/**
		 * @return the isTaxOverride
		 */
		public boolean isTaxOverride() {
			Boolean isOverride = ((Boolean) getPropertyValue(TBSConstants.IS_TAX_OVERRIDE));
			return( (isOverride == null) ? false : isOverride.booleanValue() );
		}

		public void setTaxOverride(boolean override) {
			this.setPropertyValue(TBSConstants.IS_TAX_OVERRIDE, Boolean.valueOf(override));
		}

		/**
		 * @return the taxOverrideType
		 */
		public int getTaxOverrideType() {
			Integer reason = ((Integer) getPropertyValue(TBSConstants.TAX_OVERRIDE_TYPE));
			return ((reason == null) ? 0 : reason.intValue());
		}

		public void setTaxOverrideType(int overrideType) {
			this.setPropertyValue(TBSConstants.TAX_OVERRIDE_TYPE, Integer.valueOf(overrideType));
		}

		/**
		 * @return the taxValue
		 */
		public double getTaxValue() {
			Double price = ((Double) getPropertyValue(TBSConstants.TAX_VALUE));
			return ((price == null) ? 0.0 : price.doubleValue());
		}

		public void setTaxValue(double taxValue) {
			this.setPropertyValue(TBSConstants.TAX_VALUE, Double.valueOf(taxValue));
		}

		/**
		 * @return the surchargeReason
		 */
		public String getTaxReason() {
			return ((String) getPropertyValue(TBSConstants.TAX_REASON));
		}

		public void setTaxReason(String reason) {
			this.setPropertyValue(TBSConstants.TAX_REASON, reason);
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
		 * @param pIsPriceOveride the isPriceOveride to set
		 */
		public void setPriceOveride(boolean pIsPriceOveride) {
			setPropertyValue(TBSConstants.IS_PRICE_OVERIDE, Boolean.valueOf(pIsPriceOveride));
		}
		
		/**
		 * @param pTaxExemptId the taxExemptId to set
		 */
		public void setTaxExemptId(String pTaxExemptId) {
			setPropertyValue(TBSConstants.TAX_EXEMPT_ID, pTaxExemptId);
		}
		
		/**
		 * @return the surchargeReason
		 */
		public String getTaxExemptId() {
			return ((String) getPropertyValue(TBSConstants.TAX_EXEMPT_ID));
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
	}
