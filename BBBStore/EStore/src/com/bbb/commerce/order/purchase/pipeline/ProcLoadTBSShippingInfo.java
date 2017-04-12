package com.bbb.commerce.order.purchase.pipeline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import atg.beans.DynamicBeans;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.processor.LoadProperties;
import atg.commerce.order.processor.OrderRepositoryUtils;
import atg.core.util.ResourceUtils;
import atg.multisite.SiteContextManager;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItemDescriptor;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;

/**
 * The Class ProcLoadTBSShippingInfo.
 */
public class ProcLoadTBSShippingInfo extends LoadProperties implements PipelineProcessor {

	/** The Constant MY_RESOURCE_NAME. */
	static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

	/**  Resource Bundle *. */
	private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
			atg.service.dynamo.LangLicense.getLicensedDefault());

	/** The success. */
	private final int SUCCESS = 1;

	/** The Shipping groups property. */
	private String mShippingGroupsProperty = "shippingGroups";

	/** The Tbs ship info property. */
	private String mTbsShipInfoProperty = "tbsshipinfo";

	/**
	 * Returns the valid return codes 1 - The processor completed.
	 * 
	 * @return an integer array of the valid return codes.
	 */
	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	/* (non-Javadoc)
	 * @see atg.service.pipeline.PipelineProcessor#runProcess(java.lang.Object, atg.service.pipeline.PipelineResult)
	 */
	@Override
	public int runProcess(Object pParamObject, PipelineResult pParamPipelineResult) throws Exception {
		HashMap map = (HashMap) pParamObject;
		Order order = (Order) map.get(PipelineConstants.ORDER);
		MutableRepositoryItem orderItem = (MutableRepositoryItem) map.get(PipelineConstants.ORDERREPOSITORYITEM);
		OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
		Boolean invalidateCache = (Boolean) map.get(PipelineConstants.INVALIDATECACHE);

		String currentSiteId = SiteContextManager.getCurrentSiteId();
		
		//Added for order update message
		if(null==currentSiteId){
			return SUCCESS;
		}

		
		if(null!=currentSiteId){
			if (!(currentSiteId.equals(TBSConstants.SITE_TBS_BAB_US) || currentSiteId.equals(TBSConstants.SITE_TBS_BBB) || currentSiteId
					.equals(TBSConstants.SITE_TBS_BAB_CA))) {
				return SUCCESS;
			}
		}

		// check for null parameters
		if (order == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
		if (orderItem == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryItemParameter", MY_RESOURCE_NAME,
					sResourceBundle));
		if (orderManager == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter", MY_RESOURCE_NAME,
					sResourceBundle));
		if (invalidateCache == null)
			invalidateCache = Boolean.FALSE;

		String orderSiteId = order.getSiteId();
		if(null!=orderSiteId){
			if (!(orderSiteId.equals(TBSConstants.SITE_TBS_BAB_US) || orderSiteId.equals(TBSConstants.SITE_TBS_BBB) || orderSiteId
					.equals(TBSConstants.SITE_TBS_BAB_CA))) {
				return SUCCESS;
			}
		}
		OrderTools orderTools = orderManager.getOrderTools();

		CommerceIdentifier ci;
		MutableRepositoryItem sgMutItem, mutItem;
		RepositoryItemDescriptor desc;
		ShippingGroup shipGrp;
		BBBHardGoodShippingGroup bbbShipGrp = null;
		BBBStoreShippingGroup bbbStoreShipGrp = null; 
		List shippingGroups;
		Object tbsShipInfo;
		Iterator iter, innerIter;
		String className, mappedPropName;
		String[] loadProperties = getLoadProperties();
		Object value;

		shippingGroups = (List) orderItem.getPropertyValue(getShippingGroupsProperty());
		iter = shippingGroups.iterator();
		while (iter.hasNext()) {
			sgMutItem = (MutableRepositoryItem) iter.next();
			shipGrp = order.getShippingGroup(sgMutItem.getRepositoryId());

			if (shipGrp instanceof BBBHardGoodShippingGroup) {

				bbbShipGrp = (BBBHardGoodShippingGroup) shipGrp;
			} else if (shipGrp instanceof BBBStoreShippingGroup) {
				bbbStoreShipGrp = (BBBStoreShippingGroup) shipGrp;
			}

			tbsShipInfo = sgMutItem.getPropertyValue(getTbsShipInfoProperty());
			if (tbsShipInfo != null) {
				mutItem = (MutableRepositoryItem) tbsShipInfo;
				desc = mutItem.getItemDescriptor();

				if (invalidateCache.booleanValue())
					invalidateCache((ItemDescriptorImpl) desc, mutItem);

				className = orderTools.getMappedBeanName(desc.getItemDescriptorName());
				ci = (CommerceIdentifier) Class.forName(className).newInstance();

				DynamicBeans.setPropertyValue(ci, "id", mutItem.getRepositoryId());
				if (ci instanceof ChangedProperties)
					((ChangedProperties) ci).setRepositoryItem(mutItem);

				// this is where the properties are loaded from the
				// repository into the order
				for (int i = 0; i < loadProperties.length; i++) {
					mappedPropName = getMappedPropertyName(loadProperties[i]);
					if (desc.hasProperty(loadProperties[i])) {
						value = mutItem.getPropertyValue(loadProperties[i]);
						if (isLoggingDebug())
							logDebug("load property[" + loadProperties[i] + ":" + value + ":" + ci.getClass().getName() + ":"
									+ ci.getId() + "]");
						OrderRepositoryUtils.setPropertyValue(order, ci, mappedPropName, value);
					}
				}

				if (ci instanceof ChangedProperties)
					((ChangedProperties) ci).clearChangedProperties();

				if(bbbShipGrp != null){
					bbbShipGrp.setTbsShipInfo((TBSShippingInfo) ci);
					// if
					if (bbbShipGrp instanceof ChangedProperties)
						((ChangedProperties) bbbShipGrp).clearChangedProperties();
				}
				if(bbbStoreShipGrp != null){
					bbbStoreShipGrp.setTbsShipInfo((TBSShippingInfo) ci);
					// if
					if (bbbStoreShipGrp instanceof ChangedProperties)
						((ChangedProperties) bbbStoreShipGrp).clearChangedProperties();
				}
			}// if
		} // for

		return SUCCESS;
	}

	/**
	 * This method invalidates the item from the cache if invalidateCache is
	 * true.
	 *
	 * @param desc the desc
	 * @param mutItem the mut item
	 */
	protected void invalidateCache(ItemDescriptorImpl desc, MutableRepositoryItem mutItem) {
		try {
			desc.removeItemFromCache(mutItem.getRepositoryId());
		} catch (RepositoryException e) {
			if (isLoggingWarning())
				logWarning("Unable to invalidate item descriptor " + desc.getItemDescriptorName() + ":" + mutItem.getRepositoryId());
		}
	}

	/**
	 * Gets the tbs ship info property.
	 *
	 * @return the tbsShipInfoProperty
	 */
	public String getTbsShipInfoProperty() {
		return mTbsShipInfoProperty;
	}

	/**
	 * Sets the tbs ship info property.
	 *
	 * @param pTbsShipInfoProperty            the tbsShipInfoProperty to set
	 */
	public void setTbsShipInfoProperty(String pTbsShipInfoProperty) {
		mTbsShipInfoProperty = pTbsShipInfoProperty;
	}

	/**
	 * Gets the shipping groups property.
	 *
	 * @return the shippingGroupsProperty
	 */
	public String getShippingGroupsProperty() {
		return mShippingGroupsProperty;
	}

	/**
	 * Sets the shipping groups property.
	 *
	 * @param pShippingGroupsProperty            the shippingGroupsProperty to set
	 */
	public void setShippingGroupsProperty(String pShippingGroupsProperty) {
		mShippingGroupsProperty = pShippingGroupsProperty;
	}

}
