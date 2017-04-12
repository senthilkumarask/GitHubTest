package com.bbb.commerce.order.purchase.pipeline;

import java.util.HashMap;
import java.util.Iterator;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.processor.OrderRepositoryUtils;
import atg.commerce.order.processor.SavedProperties;
import atg.core.util.ResourceUtils;
import atg.repository.ConcurrentUpdateException;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

import com.bbb.order.bean.NonMerchandiseCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;

public class ProcSaveTBSItemInfo extends SavedProperties implements PipelineProcessor{

	static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

	/** Resource Bundle **/
	private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

	private final int SUCCESS = 1;


	/**
	 * Returns the valid return codes 1 - The processor completed
	 * 
	 * @return an integer array of the valid return codes.
	 */
	@Override
	public int[] getRetCodes() {
		int[] ret = { SUCCESS };
		return ret;
	}


	//-------------------------------------
	// property: LoggingIdentifier
	String mLoggingIdentifier = "ProcSaveTBSItemInfo";

	/**
	 * Sets property LoggingIdentifier
	 **/
	public void setLoggingIdentifier(String pLoggingIdentifier) {
		mLoggingIdentifier = pLoggingIdentifier;
	}

	/**
	 * Returns property LoggingIdentifier
	 **/
	public String getLoggingIdentifier() {
		return mLoggingIdentifier;
	}

	@Override
	public int runProcess(Object pParamObject,
			PipelineResult pParamPipelineResult) throws Exception {
		HashMap map = (HashMap) pParamObject;
		Order order = (Order) map.get(PipelineConstants.ORDER);
		OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
		Repository repository = (Repository) map.get(PipelineConstants.ORDERREPOSITORY);

		MutableRepository mutRep;
		Iterator iter, innerIter;
		boolean changed = false;

		// check for null parameters
		if (order == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
					MY_RESOURCE_NAME, sResourceBundle));
		if (repository == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
					MY_RESOURCE_NAME, sResourceBundle));
		if (orderManager == null)
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
					MY_RESOURCE_NAME, sResourceBundle));

		OrderTools orderTools = orderManager.getOrderTools();

		try {
			mutRep = (MutableRepository) repository;
		}
		catch (ClassCastException e) {
			throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidRepositoryParameter",
					MY_RESOURCE_NAME, sResourceBundle), e);
		}

		// this is where all the code to store the properties to the repository goes
		iter = order.getCommerceItems().iterator();
		for (CommerceItem cItem = null; iter.hasNext(); ) {
			cItem = (CommerceItem) iter.next();

			if (cItem instanceof TBSCommerceItem) {
				TBSCommerceItem tbsCommItem = (TBSCommerceItem) cItem;
				
				
				TBSItemInfo tbsItemInfo = tbsCommItem.getTBSItemInfo();
				if(tbsItemInfo != null){
					
					changed = saveTBSItemInfo(tbsItemInfo, orderTools, mutRep,order);
					
					if (tbsItemInfo instanceof ChangedProperties) {
						ChangedProperties cp = (ChangedProperties) tbsItemInfo;
						if (cp.isChanged())
							changed = true;
						cp.clearChangedProperties();
						cp.setSaveAllProperties(false);
						cp.setChanged(false);
					}
				} //if
			} else if(cItem instanceof NonMerchandiseCommerceItem){
				NonMerchandiseCommerceItem nonMercItem = (NonMerchandiseCommerceItem) cItem;
				
				TBSItemInfo tbsItemInfo = nonMercItem.getTBSItemInfo();
				if(tbsItemInfo != null){
					
					changed = saveTBSItemInfo(tbsItemInfo, orderTools, mutRep,order);
					
					if (tbsItemInfo instanceof ChangedProperties) {
						ChangedProperties cp = (ChangedProperties) tbsItemInfo;
						if (cp.isChanged())
							changed = true;
						cp.clearChangedProperties();
						cp.setSaveAllProperties(false);
						cp.setChanged(false);
					}
				}
				
			}
		} // for

		if (changed) {
			map.put(PipelineConstants.CHANGED, Boolean.TRUE);
			if (isLoggingDebug())
				logDebug("Set changed flag to true in ProcSaveTBSItemInfo");
		}

		return SUCCESS;
	}
	
	protected boolean saveTBSItemInfo(TBSItemInfo pTbsItemInfo, OrderTools pOrderTools, MutableRepository pMutRep, Order pOrder) throws Exception{
		MutableRepositoryItem mutItem = null;
		Object value;
		Object[] savedProperties;
		String className, mappedPropName;
		boolean changed = false;
		
		
		if (getSaveChangedPropertiesOnly()
				&& pTbsItemInfo instanceof ChangedProperties
				&& (! ((ChangedProperties) pTbsItemInfo).getSaveAllProperties())) {
			savedProperties =
					((ChangedProperties) pTbsItemInfo).getChangedProperties().toArray();
		}
		else {
			savedProperties = getSavedProperties();
		}

		mutItem = null;
		if (pTbsItemInfo instanceof ChangedProperties)
			mutItem = ((ChangedProperties) pTbsItemInfo).getRepositoryItem();

		if (mutItem == null) {
			mutItem = pMutRep.getItemForUpdate(pTbsItemInfo.getId(),
					pOrderTools.getMappedItemDescriptorName(pTbsItemInfo.getClass().getName()));
			if (pTbsItemInfo instanceof ChangedProperties)
				((ChangedProperties) pTbsItemInfo).setRepositoryItem(mutItem);
		}


		if( savedProperties != null ) {
		for (int i = 0; i < savedProperties.length; i++) {
			mappedPropName = getMappedPropertyName((String) savedProperties[i]);

			if (! OrderRepositoryUtils.hasProperty(pOrder, pTbsItemInfo, mappedPropName))
				continue;

			try {
				value = OrderRepositoryUtils.getPropertyValue(pOrder, pTbsItemInfo, mappedPropName);
			}
			catch (PropertyNotFoundException e) {
				continue; // should never happen because we already checked for existence
			}

			if (isLoggingDebug())
				logDebug("save property[" + (String) savedProperties[i] + ":" + value + ":" +
						pTbsItemInfo.getClass().getName() + ":" + pTbsItemInfo.getId() + "]");
			OrderRepositoryUtils.saveRepositoryItem(pMutRep, mutItem, (String) savedProperties[i], value, pOrderTools);
			changed = true;
		} // for
		}
		
		if ((! pOrder.isTransient()) && mutItem.isTransient()) {
			if (isLoggingDebug())
				logDebug("Adding TBSItemInfo to Repository: " + mutItem.getItemDescriptor().getItemDescriptorName());
			pMutRep.addItem(mutItem);
		}

		try {
			pMutRep.updateItem(mutItem);
		}
		catch (ConcurrentUpdateException e) {
			String[] msgArgs = { pOrder.getId(), mutItem.getItemDescriptor().getItemDescriptorName() };
			throw new CommerceException(ResourceUtils.getMsgResource("ConcurrentUpdateAttempt",
					MY_RESOURCE_NAME, sResourceBundle, msgArgs), e);
		}
		
		return changed;

	}

}
