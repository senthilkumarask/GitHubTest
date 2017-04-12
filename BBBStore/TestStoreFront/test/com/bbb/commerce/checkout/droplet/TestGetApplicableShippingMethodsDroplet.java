package com.bbb.commerce.checkout.droplet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import atg.beans.DynamicBeanDescriptor;
import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicPropertyDescriptor;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.FilteredCommerceItem;
import atg.commerce.pricing.PricingContext;
import atg.commerce.pricing.PricingContextFactory;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryPropertyDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.bbb.commerce.pricing.BBBCIAttributesBasedFilter;
import com.bbb.commerce.pricing.BBBGiftCardFilter;
import com.bbb.constants.BBBCoreConstants;
import com.sapient.common.tests.BaseTestCase;

/**
 * To test GetApplicableShippingMethodsDroplet's service method.
 * 
 * @author msiddi
 * @story UC_MDZ_Shipping_Methods
 * @version 1.0
 */

public class TestGetApplicableShippingMethodsDroplet extends BaseTestCase {

	private static final String DROPLET = "bbbGetApplicableShippingMethodsDroplet";

	/**
	 * To test the perOrder flow of shipping method Droplet - Single shipping
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testServiceForShippingMethodsPerOrder() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		ServletUtil.setCurrentRequest(pRequest);
		GetApplicableShippingMethodsDroplet shippingMethodDroplet = 
			(GetApplicableShippingMethodsDroplet) getObject(DROPLET);
		BBBCartFormhandler formHandler = createorderAndAdditem(pRequest,
				pResponse);
		
		String operationParam = (String) getObject(BBBCoreConstants.PER_ORDER);

		// Setting request parameters
		getRequest().setParameter(BBBCoreConstants.ORDER,formHandler.getOrder());
		getRequest().setParameter(BBBCoreConstants.OPERATION, operationParam);

		// Calling droplet service method
		shippingMethodDroplet.service(pRequest, pResponse);

		// Getting shipping methods from request
		List<ShipMethodVO> shipMethodVOList = (List<ShipMethodVO>) pRequest
				.getObjectParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST);

		//assertTrue("Number of shipping methods not matching", shipMethodVOList.size() == 3);

	}

	/**
	 * To test the perSKU flow of shipping method Droplet - multi shipping.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testServiceForShippingMethodsPerSku() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		GetApplicableShippingMethodsDroplet shippingMethodDroplet = 
			(GetApplicableShippingMethodsDroplet) getObject(DROPLET);

		String operationParam = (String) getObject(BBBCoreConstants.PER_SKU);
		String pCatalogRefId = (String) getObject("catalogRefId");

		// create an order and add an item
		BBBCartFormhandler formHandler = createorderAndAdditem(pRequest,
				pResponse);
		
		/* Start -  code to test BBBGiftCardFilter */
		BBBGiftCardFilter bbbGiftCardFilter = (BBBGiftCardFilter) getObject("bbbGiftCardFilter");
		List<FilteredCommerceItem> filterItemList = new ArrayList<FilteredCommerceItem>();
		List<CommerceItem> commerceItemList = formHandler.getOrder().getCommerceItems();
		
		//add item to filter commerce list
		for (CommerceItem commerceItem : commerceItemList) {
			FilteredCommerceItem filteredItem = new FilteredCommerceItem(commerceItem);
			filterItemList.add(filteredItem);
		}
		//assertEquals("CommerceItem count before filtering should be 2 ", 2, filterItemList.size());
		
		//create pricing context
		PricingContext pricingContext = getPricingContext();
		pricingContext.setOrder(formHandler.getOrder());
		
		//call filter to filter-out gift card items
		bbbGiftCardFilter.filterItems(1, pricingContext, null, null, null, filterItemList);
		
        //assertEquals("The filtered items should not have giftcard items", 1, filterItemList.size());
		
        /*  End  - code to test BBBGiftCardFilter */
        
		// Setting request parameters
		getRequest().setParameter(BBBCoreConstants.ORDER,
				formHandler.getOrder());
		getRequest().setParameter(BBBCoreConstants.OPERATION, operationParam);

		// Calling droplet service method
		shippingMethodDroplet.service(pRequest, pResponse);

		// Getting shipping methods from request
		//HashMap<String, List<ShipMethodVO>> shipMethodVOMap = (HashMap<String, List<ShipMethodVO>>) pRequest.getObjectParameter(BBBCoreConstants.SKU_MEHOD_MAP);

		//List<ShipMethodVO> shipMethodVOListForSku = (List<ShipMethodVO>) shipMethodVOMap.get(pCatalogRefId);

		//assertTrue("Number of shipping methods not matching",shipMethodVOListForSku.size() == 3);

	}

	/**
	 * To test the error scenario - order is null.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testServiceForShippingMethodsErrOrder() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		GetApplicableShippingMethodsDroplet shippingMethodDroplet = 
			(GetApplicableShippingMethodsDroplet) getObject(DROPLET);

		// Setting request parameters
		getRequest().setParameter(BBBCoreConstants.ORDER, null);

		// Calling droplet service method
		shippingMethodDroplet.service(pRequest, pResponse);

		// Getting shipping methods from request
		HashMap<String, List<ShipMethodVO>> shipMethodVOMap = (HashMap<String, List<ShipMethodVO>>) pRequest
				.getObjectParameter(BBBCoreConstants.SKU_MEHOD_MAP);

		//assertNull("No shipping methods found", shipMethodVOMap);

	}
	
		
	/**
	 * To test the error scenario - wrong operation string.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testServiceForShippingMethodsErrOperation() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();

		GetApplicableShippingMethodsDroplet shippingMethodDroplet = 
			(GetApplicableShippingMethodsDroplet) getObject(DROPLET);

		
		// create an order and add an item
		BBBCartFormhandler formHandler = createorderAndAdditem(pRequest,
				pResponse);

		// Setting request parameters
		getRequest().setParameter(BBBCoreConstants.ORDER,
				formHandler.getOrder());
		
		// setting wrong operation param
		getRequest().setParameter(BBBCoreConstants.OPERATION, BBBCoreConstants.OPERATION);

		// Calling droplet service method
		shippingMethodDroplet.service(pRequest, pResponse);

		
		// Getting shipping methods from request
		List<ShipMethodVO> shipMethodVOList = (List<ShipMethodVO>) pRequest
				.getObjectParameter(BBBCoreConstants.SHIP_METHOD_VO_LIST);

		//assertNull("No shipping methods found", shipMethodVOList);

	}
	
	/**
	 * 
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testCIAttributesBasedFilterSuccess() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
	
		// create an order and add an item
		BBBCartFormhandler formHandler = createorderAndAdditem(pRequest,
				pResponse);
		
		/* Start -  code to test BBBGiftCardFilter */
		BBBCIAttributesBasedFilter bbbCIAttributesBasedFilter = (BBBCIAttributesBasedFilter) getObject("bbbCIAttributesBasedFilter");
		List<FilteredCommerceItem> filterItemList = new ArrayList<FilteredCommerceItem>();
		List<CommerceItem> commerceItemList = formHandler.getOrder().getCommerceItems();
		
		//add item to filter commerce list
		for (CommerceItem commerceItem : commerceItemList) {
			FilteredCommerceItem filteredItem = new FilteredCommerceItem(commerceItem);
			filterItemList.add(filteredItem);
		}
		
		//create pricing context
		PricingContext pricingContext = getPricingContext();
		pricingContext.setOrder(formHandler.getOrder());
		
		//call filter to filter-out gift card items
		bbbCIAttributesBasedFilter.filterItems(1, pricingContext, null, null, null, filterItemList);
		
        //assertEquals("The filtered items count should be 2", 2, filterItemList.size());
		
        /*  End  - code to test BBBGiftCardFilter */
		
	}
	/**
	 * To test the perOrder flow of shipping method Droplet - Single shipping
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void testNoItemForCIAttributesBasedFilter() throws Exception {

		BBBCIAttributesBasedFilter bbbCIAttributesBasedFilter = (BBBCIAttributesBasedFilter) getObject("bbbCIAttributesBasedFilter");
		List<FilteredCommerceItem> filterItemList = new ArrayList<FilteredCommerceItem>();
		
		//create pricing context
		PricingContext pricingContext = getPricingContext();
		
		try{
		//call filter to filter-out gift card items
		bbbCIAttributesBasedFilter.filterItems(1, pricingContext, null, null, null, filterItemList);
		
		}catch(Exception e){
			//assertEquals("Exception occured while filtering items for promotions", true, true);
		}
      
	}

	/**
	 * 
	 * This method creates an order and add a commerce item to it.
	 * 
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @return BBBCartFormhandler
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private BBBCartFormhandler createorderAndAdditem(
			DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws Exception {

		BBBCartFormhandler formHandler = (BBBCartFormhandler) getObject("bbbCartFormHandler");

		// Add all parameters in profile level
		MutableRepositoryItem profileItem = (MutableRepositoryItem) formHandler
				.getProfile();

		PriceListManager priceListManager = (PriceListManager) getObject("bbbPriceListManager");
		String listPriceId = (String) getObject("listPriceId");
		String salePriceId = (String) getObject("salePriceId");
		RepositoryItem listPriceListItem = priceListManager
				.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager
				.getPriceList(salePriceId);
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);

		CustomCatalogTools catalogTools = (CustomCatalogTools) getObject("bbbCatalogTools");
		List catalogIds = new ArrayList();
		String catalogId = (String) getObject("catalogId");
		catalogIds.add(catalogId);
		if (catalogTools.getCatalogsForRepository(null, catalogIds) != null) {
			RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(
					null, catalogIds)[0];
			profileItem.setPropertyValue("catalog", catalogItem);

		}
		formHandler.setProfile(profileItem);
		formHandler.setSiteId("BedBathUS");

		// Test logic
		formHandler.setAddItemCount(2);

		// Add one online product with quantity 2

		String pCatalogRefId = (String) getObject("catalogRefId");
		String pProductId = (String) getObject("productId");
		long pQuantity = 2;

		if (formHandler.getItems() != null && formHandler.getItems()[0] != null) {
			formHandler.getItems()[0].setProductId(pProductId);
			formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			formHandler.getItems()[0].setQuantity(pQuantity);
		}
		
		String pCatalogRefIdGiftCard = (String) getObject("catalogRefIdGiftCard");
		String pProductIdGiftCard = (String) getObject("productIdGiftCard");
		 
		if (formHandler.getItems() != null && formHandler.getItems()[1] != null) {
			formHandler.getItems()[1].setProductId(pProductIdGiftCard);
			formHandler.getItems()[1].setCatalogRefId(pCatalogRefIdGiftCard);
			formHandler.getItems()[1].setQuantity(1);
		}

		// add item to the order
		formHandler.handleAddItemToOrder(pRequest, pResponse);
		
		
		return formHandler;
	}
	
	/**
	 * Create Pricing Context.
	 * 
	 * @return pricingContext
	 */
	private PricingContext getPricingContext(){
		
		PricingContextFactory pcFactory = new PricingContextFactory();
		PricingContext pricingContext = pcFactory.createPricingContext();
		
		pricingContext.setPricingModel(new RepositoryItem() {
			
			@Override
			public String getItemDisplayName() {
				return null;
			}
			
			@Override
			public boolean isTransient() {
				return false;
			}
			
			@Override
			public String getRepositoryId() {
				return null;
			}
			
			@Override
			public Repository getRepository() {
				return null;
			}
			
			@Override
			public Object getPropertyValue(String s) {
				return "bbbCoupons";
			}
			
			@Override
			public RepositoryItemDescriptor getItemDescriptor()
					throws RepositoryException {
				RepositoryItemDescriptor repoItem = new RepositoryItemDescriptor() {
					
					@Override
					public boolean isInstance(Object obj) {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public boolean hasProperty(String s) {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public String[] getPropertyNames() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public DynamicPropertyDescriptor[] getPropertyDescriptors() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public DynamicPropertyDescriptor getPropertyDescriptor(String s) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public DynamicBeanDescriptor getBeanDescriptor() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public boolean areInstances(DynamicBeanInfo dynamicbeaninfo) {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public void updatePropertyDescriptor(RepositoryPropertyDescriptor repositorypropertydescriptor) throws RepositoryException {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void removePropertyDescriptor(String s) throws RepositoryException {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public boolean isContextMembershipEnabled() {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public boolean hasCompositeKey() {
						// TODO Auto-generated method stub
						return false;
					}
					
					@Override
					public RepositoryView getRepositoryView() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public Repository getRepository() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getItemDescriptorName() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public RepositoryPropertyDescriptor getDisplayNameProperty() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public RepositoryPropertyDescriptor getContextMembershipProperty() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String encodeCompositeKey(String[] as) throws RepositoryException {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public void addPropertyDescriptor(RepositoryPropertyDescriptor repositorypropertydescriptor) throws RepositoryException {
						// TODO Auto-generated method stub
						
					}
				};
				return repoItem;
			}
			
			@Override
			public Collection<String> getContextMemberships()
					throws RepositoryException {
				return null;
			}
		});
		
		
		return pricingContext;
	}

}