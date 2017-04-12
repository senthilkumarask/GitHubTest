/**
 * @author jpadhi
 * @version Id: //com.bbb.commerce.order.purchase/TestBBBPurchaseProcessHelper.java.TestBBBPurchaseProcessHelper $$
 * @updated $DateTime: Nov 5, 2011 9:59:29 AM
 */
package com.bbb.commerce.order.purchase.pipeline;

import java.util.ArrayList;
import java.util.List;

import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.order.purchase.BBBCartFormhandler;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author jpadhi
 *
 */
public class TestProcAddOrRemoveEcoFeeItems extends BaseTestCase {
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public void testRunProcess() throws Exception {
		BBBCartFormhandler formHandler = (BBBCartFormhandler)getObject("bbbCartFormHandler");
		DynamoHttpServletRequest pRequest =  getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		//Add all parametrs in profile level
		MutableRepositoryItem profileItem = (MutableRepositoryItem)formHandler.getProfile();		
		ProfileTools profileTool = (ProfileTools) getRequest().resolveName("/atg/userprofiling/ProfileTools");
        
		//MutableRepositoryItem profileItem = (MutableRepositoryItem) profileTool.getItemFromEmail("andrew@example.com");
        
		PriceListManager priceListManager = (PriceListManager)getObject("bbbPriceListManager");
		String listPriceId = (String)getObject("listPriceId");
		String salePriceId = (String)getObject("salePriceId");
		RepositoryItem listPriceListItem = priceListManager.getPriceList(listPriceId);
		RepositoryItem salePriceListItem = priceListManager.getPriceList(salePriceId);
		profileItem.setPropertyValue("priceList", listPriceListItem);
		profileItem.setPropertyValue("salePriceList", salePriceListItem);
		
		
		/*CustomCatalogTools catalogTools = (CustomCatalogTools)getObject("bbbCatalogTools");
		List catalogIds = new ArrayList();
		String catalogId = (String)getObject("catalogId");
		catalogIds.add(catalogId);
		if(catalogTools.getCatalogsForRepository(null, catalogIds) != null){
			RepositoryItem catalogItem = catalogTools.getCatalogsForRepository(null, catalogIds)[0];
			profileItem.setPropertyValue("catalog", catalogItem);
			
		}
		formHandler.setProfile(profileItem);*/
		formHandler.setSiteId("BedBathUS");

		//Test logic
		formHandler.setAddItemCount(1);		
		
		
		// Test Scenario 1
		// Add one online product with quantity 2
		
		String pCatalogRefId = (String)getObject("catalogRefId");	
		String pProductId = (String)getObject("productId");		
		long pQuantity = 2;		
		
		
		
		if(formHandler.getItems() != null && formHandler.getItems()[0] != null){
			formHandler.getItems()[0].setProductId(pProductId);
			formHandler.getItems()[0].setCatalogRefId(pCatalogRefId);
			formHandler.getItems()[0].setQuantity(pQuantity);
			
		}
		boolean result = formHandler.handleAddItemToOrder(pRequest, pResponse);
		
		
		if( formHandler.getOrder().getShippingGroups() != null){
			if( formHandler.getOrder().getShippingGroups().get(0) != null && formHandler.getOrder().getShippingGroups().get(0) instanceof HardgoodShippingGroup);
			HardgoodShippingGroup hgSG = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);
			
			assertEquals("There should be onbly one CI relationship ##  ", 1, hgSG.getCommerceItemRelationshipCount());
			if( hgSG.getCommerceItemRelationships() != null && hgSG.getCommerceItemRelationships().get(0) != null){
				CommerceItemRelationship ciRel = (CommerceItemRelationship)hgSG.getCommerceItemRelationships().get(0);				
				assertEquals("CI relationship quantity should be 2", 2, ciRel.getQuantity()); 
			}
			
			
		}		
		
		String ecoCatalogRefId = (String)getObject("ecoCatalogRefId");	
		String ecoProductId = (String)getObject("ecoProductId");		
		long ecoQuantity = 2;	
		
		if(formHandler.getItems() != null && formHandler.getItems()[0] != null){
			formHandler.getItems()[0].setProductId(ecoProductId);
			formHandler.getItems()[0].setCatalogRefId(ecoCatalogRefId);
			formHandler.getItems()[0].setQuantity(ecoQuantity);
			
			//setting the address state as AK
			if( formHandler.getOrder().getShippingGroups().get(0) != null && formHandler.getOrder().getShippingGroups().get(0) instanceof HardgoodShippingGroup){			
				((HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0)).getShippingAddress().setState("AK");
			}
		}
		
		formHandler.handleAddItemToOrder(pRequest, pResponse);
				
		if( formHandler.getOrder().getShippingGroups() != null){
			if( formHandler.getOrder().getShippingGroups().get(0) != null && formHandler.getOrder().getShippingGroups().get(0) instanceof HardgoodShippingGroup);
			HardgoodShippingGroup hgSG = (HardgoodShippingGroup)formHandler.getOrder().getShippingGroups().get(0);
			
			assertEquals("There should be 2 CI relationship ##  ", 2, hgSG.getCommerceItemRelationshipCount());
			if( hgSG.getCommerceItemRelationships() != null){
				for(int index=0; index<hgSG.getCommerceItemRelationshipCount(); index++){
				CommerceItemRelationship ciRel = (CommerceItemRelationship)hgSG.getCommerceItemRelationships().get(index);		
				System.out.println("@@@@@@@@@@@@@@@@@@ SkuId added to SG : "+ciRel.getCommerceItem().getCatalogRefId());
				//assertEquals("CI relationship quantity should be 2", 2, ciRel.getQuantity()); 
				}
			}
		}
		
		formHandler.getOrder().removeAllCommerceItems();
		if(formHandler.getOrder().getShippingGroupCount() > 0){
			ShippingGroup sg = (ShippingGroup) (formHandler.getOrder().getShippingGroups().get(formHandler.getOrder().getShippingGroupCount() - 1));
			sg.removeAllCommerceItemRelationships();
		}
	}		
	
}
