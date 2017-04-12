package com.bbb.commerce.order.purchase.pipeline;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import atg.beans.DynamicBeans;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceIdentifier;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PipelineConstants;
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

import com.bbb.constants.TBSConstants;
import com.bbb.order.bean.GiftWrapCommerceItem;
import com.bbb.order.bean.LTLAssemblyFeeCommerceItem;
import com.bbb.order.bean.LTLDeliveryChargeCommerceItem;
import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;

public class ProcLoadTBSItemInfo extends LoadProperties implements
PipelineProcessor {

	static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

	/** Resource Bundle **/
	private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle
			.getBundle(MY_RESOURCE_NAME,
					atg.service.dynamo.LangLicense.getLicensedDefault());

	private final int SUCCESS = 1;

	private String mCommerceItemsProperty = "commerceItems";

	private String mTbsItemInfoProperty = "tBSItemInfo";
	
	/**
	 * Returns the valid return codes 1 - The processor completed.
	 * 
	 * @return an integer array of the valid return codes.
	 */
	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}

	@Override
	public int runProcess(Object pParamObject,
			PipelineResult pParamPipelineResult) throws Exception {
		HashMap map = (HashMap) pParamObject;
		Order order = (Order) map.get(PipelineConstants.ORDER);
		MutableRepositoryItem orderItem = (MutableRepositoryItem) map
				.get(PipelineConstants.ORDERREPOSITORYITEM);
		OrderManager orderManager = (OrderManager) map
				.get(PipelineConstants.ORDERMANAGER);
		Boolean invalidateCache = (Boolean) map
				.get(PipelineConstants.INVALIDATECACHE);
		
		
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
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidOrderParameter", MY_RESOURCE_NAME, sResourceBundle));
		if (orderItem == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidRepositoryItemParameter", MY_RESOURCE_NAME,
					sResourceBundle));
		if (orderManager == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource(
					"InvalidOrderManagerParameter", MY_RESOURCE_NAME,
					sResourceBundle));
		if (invalidateCache == null)
			invalidateCache = Boolean.FALSE;
		
		String orderSiteId = order.getSiteId();
		//Added for order update message
		if(null!=orderSiteId){
			if (!(orderSiteId.equals(TBSConstants.SITE_TBS_BAB_US) || orderSiteId.equals(TBSConstants.SITE_TBS_BBB) || orderSiteId
					.equals(TBSConstants.SITE_TBS_BAB_CA))) {
				return SUCCESS;
			}
		}

		OrderTools orderTools = orderManager.getOrderTools();

		CommerceIdentifier ci;
		MutableRepositoryItem ciMutItem;
		CommerceItem cItem;
		TBSCommerceItem tbsCommItem;
		NonMerchandiseCommerceItem nonMercItem;
		List commerceItems;
		Object tbsItemInfo;
		Iterator iter, innerIter;
		GiftWrapCommerceItem gfitItem = null;
		
		

		commerceItems = (List) orderItem
				.getPropertyValue(getCommerceItemsProperty());
		iter = commerceItems.iterator();
		while (iter.hasNext()) {
			ciMutItem = (MutableRepositoryItem) iter.next();
			cItem = order.getCommerceItem(ciMutItem.getRepositoryId());

			if (cItem instanceof TBSCommerceItem) {

				tbsCommItem = (TBSCommerceItem) cItem;

				tbsItemInfo = ciMutItem
						.getPropertyValue(getTbsItemInfoProperty());
				if (tbsItemInfo != null) {
					ci = createTBSInfo(tbsItemInfo, invalidateCache, orderTools, order);

					tbsCommItem.setTBSItemInfo((TBSItemInfo) ci);
				} // if
				if (tbsCommItem instanceof ChangedProperties)
					((ChangedProperties) tbsCommItem).clearChangedProperties();
			} else if(cItem instanceof LTLAssemblyFeeCommerceItem || cItem instanceof LTLDeliveryChargeCommerceItem){
				nonMercItem = (NonMerchandiseCommerceItem) cItem;

				tbsItemInfo = ciMutItem
						.getPropertyValue(getTbsItemInfoProperty());
				if (tbsItemInfo != null) {
					ci = createTBSInfo(tbsItemInfo, invalidateCache, orderTools, order);

					nonMercItem.setTBSItemInfo((TBSItemInfo) ci);
				} // if
				if (nonMercItem instanceof ChangedProperties)
					((ChangedProperties) nonMercItem).clearChangedProperties();
			} else if (cItem instanceof GiftWrapCommerceItem) {

				gfitItem = (GiftWrapCommerceItem) cItem;

				tbsItemInfo = ciMutItem.getPropertyValue(getTbsItemInfoProperty());
				if (tbsItemInfo != null) {
					ci = createTBSInfo(tbsItemInfo, invalidateCache, orderTools, order);

					gfitItem.setTBSItemInfo((TBSItemInfo) ci);
				} // if
				if (gfitItem instanceof ChangedProperties)
					((ChangedProperties) gfitItem).clearChangedProperties();
			} 
		} // for

		return SUCCESS;
	}
	
	
	protected CommerceIdentifier createTBSInfo(Object pTbsItemInfo, Boolean pInvalidateCache, OrderTools pOrderTools, Order pOrder) throws Exception{
		MutableRepositoryItem mutItem;
		RepositoryItemDescriptor desc;
		String className, mappedPropName;
		CommerceIdentifier ci;
		String[] loadProperties = getLoadProperties();
		Object value;
		
		mutItem = (MutableRepositoryItem) pTbsItemInfo;
		desc = mutItem.getItemDescriptor();

		if (pInvalidateCache.booleanValue())
			invalidateCache((ItemDescriptorImpl) desc, mutItem);

		className = pOrderTools.getMappedBeanName(desc
				.getItemDescriptorName());
		ci = (CommerceIdentifier) Class.forName(className)
				.newInstance();

		DynamicBeans.setPropertyValue(ci, "id",
				mutItem.getRepositoryId());
		if (ci instanceof ChangedProperties)
			((ChangedProperties) ci).setRepositoryItem(mutItem);

		// this is where the properties are loaded from the
		// repository into the order
		for (int i = 0; i < loadProperties.length; i++) {
			mappedPropName = getMappedPropertyName(loadProperties[i]);
			if (desc.hasProperty(loadProperties[i])) {
				value = mutItem.getPropertyValue(loadProperties[i]);
				if (isLoggingDebug())
					logDebug("load property[" + loadProperties[i]
							+ ":" + value + ":"
							+ ci.getClass().getName() + ":"
							+ ci.getId() + "]");
				OrderRepositoryUtils.setPropertyValue(pOrder, ci,
						mappedPropName, value);
			}
		}

		if (ci instanceof ChangedProperties)
			((ChangedProperties) ci).clearChangedProperties();
		return ci;
	}

	/**
	 * This method invalidates the item from the cache if invalidateCache is
	 * true
	 */
	protected void invalidateCache(ItemDescriptorImpl desc,
			MutableRepositoryItem mutItem) {
		try {
			desc.removeItemFromCache(mutItem.getRepositoryId());
		} catch (RepositoryException e) {
			if (isLoggingWarning())
				logWarning("Unable to invalidate item descriptor "
						+ desc.getItemDescriptorName() + ":"
						+ mutItem.getRepositoryId());
		}
	}

	public String getCommerceItemsProperty() {
		return mCommerceItemsProperty;
	}

	public void setCommerceItemsProperty(String pCommerceItemsProperty) {
		mCommerceItemsProperty = pCommerceItemsProperty;
	}

	public String getTbsItemInfoProperty() {
		return mTbsItemInfoProperty;
	}

	public void setTbsItemInfoProperty(String pTbsItemInfoProperty) {
		mTbsItemInfoProperty = pTbsItemInfoProperty;
	}
}
