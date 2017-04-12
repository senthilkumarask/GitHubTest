package com.bbb.commerce.order;

import atg.core.util.StringUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;

/**
 *
 * @author alakra
 */
public class BBBOrderPropertyManager extends ApplicationLoggingImpl {
	
	private String mOrderName = null;
	private String mSubstatusName = null;
	private String mSubmittedDateName = null;
	private String mOrderXMLName = null;
	private String mCreatedByOrderId=null;
	private String salesChannel = null;

	public String getSalesChannel() {
		return salesChannel;
	}

	public void setSalesChannel(String salesChannel) {
		this.salesChannel = salesChannel;
	}

	public String getLoggingIdentifier() {
		return "OrderPropertyManager";
	}

	public boolean hasProperty(String pPropertyName, RepositoryItem pItem) {
		if (pItem == null) {
			return false;
		}
		try {
			return hasProperty(pPropertyName, pItem.getItemDescriptor());
		} catch (RepositoryException exc) {
			if (isLoggingError()) {
				if (exc.getSourceException() != null) {
					logError(exc.toString(), exc.getSourceException());
				} else {
					logError(exc);
				}
			}
		}
		return false;
	}

	public boolean hasProperty(String pPropertyName, RepositoryItemDescriptor pItemDescriptor) {
		if (pItemDescriptor == null) {
			return false;
		}
		if (pPropertyName == null) {
			return false;
		}
		String propertyNames[] = StringUtils.splitStringAtCharacter(pPropertyName, '.');
		RepositoryItemDescriptor rid = pItemDescriptor;
		for (int i = 0; i < propertyNames.length; i++) {
			if (rid == null || !rid.hasProperty(propertyNames[i])) {
				return false;
			}
			RepositoryPropertyDescriptor pd = (RepositoryPropertyDescriptor) rid
					.getPropertyDescriptor(propertyNames[i]);
			rid = pd.getPropertyItemDescriptor();
		}

		return true;
	}

	/**
	 * @return the orderName
	 */
	public String getOrderName() {
		return mOrderName;
	}

	/**
	 * @param pOrderName the orderName to set
	 */
	public void setOrderName(String pOrderName) {
		mOrderName = pOrderName;
	}

	/**
	 * @return the substatusName
	 */
	public String getSubstatusName() {
		return mSubstatusName;
	}

	/**
	 * @param pSubstatusName the substatusName to set
	 */
	public void setSubstatusName(String pSubstatusName) {
		mSubstatusName = pSubstatusName;
	}

	/**
	 * @return the submittedDateName
	 */
	public String getSubmittedDateName() {
		return mSubmittedDateName;
	}

	/**
	 * @param pSubmittedDateName the submittedDateName to set
	 */
	public void setSubmittedDateName(String pSubmittedDateName) {
		mSubmittedDateName = pSubmittedDateName;
	}

	/**
	 * @return the orderXMLName
	 */
	public final String getOrderXMLName() {
		return mOrderXMLName;
	}

	/**
	 * @param pOrderXMLName the orderXMLName to set
	 */
	public final void setOrderXMLName(String pOrderXMLName) {
		mOrderXMLName = pOrderXMLName;
	}

	/**
	 * @return the mCreatedByOrderId
	 */
	public String getCreatedByOrderIdName() {
		return mCreatedByOrderId;
	}

	/**
	 * @param mCreatedByOrderId the mCreatedByOrderId to set
	 */
	public void setCreatedByOrderIdName(String mCreatedByOrderId) {
		this.mCreatedByOrderId = mCreatedByOrderId;
	}
	
}