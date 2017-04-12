/**
 * 
 */
package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.CommerceItemShippingInfoContainer;
import atg.commerce.order.purchase.CommerceItemShippingInfoTools;
import atg.commerce.order.purchase.PurchaseProcessConfiguration;
import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.nucleus.ServiceMap;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.utils.BBBUtility;

/**
 * This class is used to provide operation to be performed for multi-shipping
 * functionality like cleaning the shipping  
 *
 */
public class BBBMultiShippingManager extends BBBGenericService {
	
	private PurchaseProcessConfiguration purchaseProcessorConfiguration;
	private Profile mProfile;
	private OrderHolder cart;
	private CommerceItemShippingInfoTools mCommerceItemShippingInfoTools;
	
	 private BBBCatalogTools catalogTools;
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * 
	 * @param catalogTools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * 
	 * @param shippingGroupTypes
	 * @param initShippingGroups
	 * @param initShippingInfos
	 * @param initBasedOnOrder
	 * @param clearShippingGroups
	 * @param clearShippingInfos
	 * @param clearAll
	 * @param createOneInfoPerUnit
	 * @return
	 */
	public boolean reInitializeShippingContainers(String shippingGroupTypes, boolean initShippingGroups, boolean initShippingInfos, 
			boolean initBasedOnOrder, boolean clearShippingGroups, boolean clearShippingInfos, boolean clearAll, boolean createOneInfoPerUnit){
		
		logDebug("BBBMultiShippingManager.clearShippingContainers: START");
		ServiceMap mShippingGroupInitializers = new ServiceMap();
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		mShippingGroupInitializers.put("electronicShippingGroup", pRequest.resolveName("/atg/commerce/order/purchase/ElectronicShippingGroupInitializer"));
		mShippingGroupInitializers.put("hardgoodShippingGroup", pRequest.resolveName("/atg/commerce/order/purchase/HardgoodShippingGroupInitializer"));
		
		if (getCart().getClass() == null) {
			logError("BBBMultiShippingManager.clearShippingContainers: Order is null");
			logError("BBBMultiShippingManager.clearShippingContainers: END");
		    return false;
	    }
	    
		if (getProfile() == null) {
			logError("BBBMultiShippingManager.clearShippingContainers: Profile is null");
			logError("BBBMultiShippingManager.clearShippingContainers: END");
		    return false;
		}
		    
		if (clearAll || clearShippingGroups) {
			getCommerceItemShippingInfoTools().clearShippingGroups(getPurchaseProcessorConfiguration().getCommerceItemShippingInfoContainer(),
											getPurchaseProcessorConfiguration().getShippingGroupMapContainer());
			logDebug("removing all ShippingGroups");
		}

	    if (clearAll || clearShippingInfos) {
	       getCommerceItemShippingInfoTools().clearCommerceItemShippingInfos(getPurchaseProcessorConfiguration().getCommerceItemShippingInfoContainer(),
	    		   							getPurchaseProcessorConfiguration().getShippingGroupMapContainer());
	        logDebug("removing all ShippingInfos");
	    }
		    
	    if(initShippingGroups){
	    	String shippingGroups = StringUtils.removeWhiteSpace(shippingGroupTypes);
	    	String [] types = StringUtils.splitStringAtCharacter(shippingGroups,','); 
	    	getCommerceItemShippingInfoTools().initializeUserShippingMethods(getPurchaseProcessorConfiguration().getShippingGroupMapContainer(), getProfile(), 
	    									types, mShippingGroupInitializers);
	    }
      
	    getCommerceItemShippingInfoTools().removeDeletedShippingGroups(getPurchaseProcessorConfiguration().getShippingGroupMapContainer());
	    
	    if (initBasedOnOrder) 
	    {
	    	try{
	    		getCommerceItemShippingInfoTools().initializeBasedOnOrder(getCart().getCurrent(),
		        		 	getPurchaseProcessorConfiguration().getCommerceItemShippingInfoContainer(),getPurchaseProcessorConfiguration().getShippingGroupMapContainer(),
		        		 	mShippingGroupInitializers.values(), createOneInfoPerUnit);
	    		
	    	}catch (CommerceException e){
	    		logError("BBBMultiShippingManager.clearShippingContainers: Error occured while initializing based on Order", e);
	    	}
	      
	    	if (!isAnyShippingInfoExistsInContainter()) {
	    		logDebug("CommerceItemShippingInfoContainer does not have any cisi.");
	    		getCommerceItemShippingInfoTools().initializeCommerceItemShippingInfosToDefaultShipping(getCart().getCurrent(),
	    											getPurchaseProcessorConfiguration().getCommerceItemShippingInfoContainer(),
	    											getPurchaseProcessorConfiguration().getShippingGroupMapContainer(),
	    											createOneInfoPerUnit);
	    	}
	    }else {
	  //  	if (true)
	    		getCommerceItemShippingInfoTools().initializeCommerceItemShippingInfosToDefaultShipping(getCart().getCurrent(),
						getPurchaseProcessorConfiguration().getCommerceItemShippingInfoContainer(),
						getPurchaseProcessorConfiguration().getShippingGroupMapContainer(),
						createOneInfoPerUnit);
	    }
	    
	    
	    logDebug("BBBMultiShippingManager.clearShippingContainers: END");    
		return true;
		
	}
	
	 
    /**
     * method to get all commerce item shipping info object from CommerceItemShippingInfoContainer 
     * @param reinitialize This value indicates whether recreate CommerceItemShippingInfo or return the existing CommerceItemShippingInfo objects. If 
     * 	true, it will destroy the multi-split data and refresh the containers.
     * @return commerceItemShippingInfos list of commerce item shipping info object in container.
     */
    public  Map<String, CommerceItemShippingInfo> getCommerceItemShippingInfos(boolean reinitialize) {
    	
    	if (reinitialize) {
	       this.reInitializeShippingContainers("hardgoodShippingGroup,storeShippingGroup", false, false, 
					true, false, false, true, false);
	    }
        final ArrayList<CommerceItemShippingInfo> commerceItemShippingInfos = new ArrayList<CommerceItemShippingInfo>();
        //Multi shipping LTL story mobile
        List<CommerceItem> allCommItems;
		allCommItems = this.getCart().getCurrent().getCommerceItems();
		if(allCommItems!=null){
 				 for(CommerceItem commItem : allCommItems){
					 if (commItem instanceof BBBCommerceItem) {
						 BBBCommerceItem bbbCommItem = (BBBCommerceItem) commItem;
				 String lTLHighestShipMethodId = "";
				 boolean ltlItemFlag = bbbCommItem.isLtlItem();
				 if(ltlItemFlag){
					 this.logDebug("LTL item found");
					 this.logDebug("lTLEligibleShippingMethods getCatalogRefId:"+bbbCommItem.getCatalogRefId());
					 this.logDebug("lTLEligibleShippingMethods getCurrentSiteId:"+SiteContextManager.getCurrentSiteId());
					 List<ShipMethodVO> lTLEligibleShippingMethods=null;
					try {
						lTLEligibleShippingMethods = getCatalogTools().getLTLEligibleShippingMethods(bbbCommItem.getCatalogRefId(), SiteContextManager.getCurrentSiteId(),
								((BBBProfileTools)(this.getProfile().getProfileTools())).getUserLocale(ServletUtil.getCurrentRequest(), ServletUtil.getCurrentResponse()).getLanguage());
					} catch (BBBSystemException e) {
						logError("BBBMultiShippingManager.getCommerceItemShippingInfos: Error occured while lTLEligibleShippingMethods based on item", e);
					} catch (BBBBusinessException e) {
						logError("BBBMultiShippingManager.getCommerceItemShippingInfos: Error occured while lTLEligibleShippingMethods based on item", e);
					} catch (ServletException e) {
						logError("BBBMultiShippingManager.getCommerceItemShippingInfos: Error occured while lTLEligibleShippingMethods based on item", e);
					} catch (IOException e) {
						logError("BBBMultiShippingManager.getCommerceItemShippingInfos: Error occured while lTLEligibleShippingMethods based on item", e);
					}
					 boolean foundRegistrantMethod=false;
					 boolean foundAddedRegistryMethod=false;

					if(lTLEligibleShippingMethods != null){
						 this.logDebug("lTLEligibleShippingMethods size"+lTLEligibleShippingMethods.size());
						 logDebug("lTLEligibleShippingMethods : " + lTLEligibleShippingMethods);
						 for(ShipMethodVO methodVO:lTLEligibleShippingMethods){
							 if(methodVO.getShipMethodId().equalsIgnoreCase(bbbCommItem.getRegistrantShipMethod()) || methodVO.getShipMethodId().equalsIgnoreCase(bbbCommItem.getRegisryShipMethod())){
								 if(methodVO.getShipMethodId().equalsIgnoreCase(bbbCommItem.getRegistrantShipMethod())){
									 foundRegistrantMethod=true;
								 }
								 if(methodVO.getShipMethodId().equalsIgnoreCase(bbbCommItem.getRegisryShipMethod())){
									 foundAddedRegistryMethod=true;
								 }
							 }
						 }
					}
					 if (lTLEligibleShippingMethods != null && !lTLEligibleShippingMethods.isEmpty()) {
						 ShipMethodVO lTLHighestShipMethod = lTLEligibleShippingMethods.get(lTLEligibleShippingMethods.size()-1);
						 lTLHighestShipMethodId = lTLHighestShipMethod.getShipMethodId();
						 bbbCommItem.setHighestshipMethod(lTLHighestShipMethodId);
						 if(foundRegistrantMethod){
							 bbbCommItem.setRegisryShipMethod(bbbCommItem.getRegistrantShipMethod());
						 }else if(!foundAddedRegistryMethod){
							 bbbCommItem.setRegisryShipMethod("");
						 }
					 }
					 
				 }
					 }
 				 
			 }	
		}
        for (final Object cisiObj : getPurchaseProcessorConfiguration().getCommerceItemShippingInfoContainer().getAllCommerceItemShippingInfos()) {
            if (((CommerceItemShippingInfo) cisiObj).getCommerceItem() instanceof BBBCommerceItem) {
                commerceItemShippingInfos.add((CommerceItemShippingInfo) cisiObj);
            }
        }
        
        final Map<String, CommerceItemShippingInfo> commerceItemShippingInfoMap = new HashMap<String, CommerceItemShippingInfo>();
        for(int index = 0; index < commerceItemShippingInfos.size(); index++) {
            commerceItemShippingInfoMap.put(String.valueOf(index), commerceItemShippingInfos.get(index));
        }
        return commerceItemShippingInfoMap;
    }
    
	/**
	  *
	  *  This method checks to see if there is any shipping infos in the container. If there is any cisi in the container, this method
	  *  returns true otherwise false. The cisi could be in any level. The cisi could be in the Order, CommerceItem, ShippingGroup
	  *  or Tax level. If any cisi found this method returns true and if no cisi found, then this method returns false.
	  *
	  * @return true/false based on the the existance of any cisi in the container
	  *
	  */
	 public boolean isAnyShippingInfoExistsInContainter () {

	  logDebug ("Entering isAnyPaymentInfoExistsInContainter()");
	  
	  boolean shippingInfoExists = false;
	  CommerceItemShippingInfoContainer cisic = getPurchaseProcessorConfiguration().getCommerceItemShippingInfoContainer();
	  Map allcisi = cisic.getCommerceItemShippingInfoMap();

	  if (BBBUtility.isMapNullOrEmpty(allcisi)) {
	   logDebug ("CommerceItemShippingInfoContainer does not have any cisi.");
	   
	    return false;
	  }

	  Iterator cisii = allcisi.keySet().iterator();
	  while (cisii.hasNext()) {
	    String commerceItemId = (String) cisii.next();
	    List cisiList = cisic.getCommerceItemShippingInfos(commerceItemId);
	    if (cisiList != null
	        && cisiList.size() > 0) {
	        logDebug ("cisi list is found.");	     
	      shippingInfoExists = true;
	      break;
	    }
	  }//end of while
	  return shippingInfoExists;
	 }
	 
	 
  /**
   * Returns the tools component containing the API for modifying
   * CommerceItemShippingInfos 
   * @return
   */
	public CommerceItemShippingInfoTools getCommerceItemShippingInfoTools()
	{
		return mCommerceItemShippingInfoTools;
	}

	/**
	 * @param pCommerceItemShippingInfoTools
	 */
	public void setCommerceItemShippingInfoTools(CommerceItemShippingInfoTools pCommerceItemShippingInfoTools)
	{
		mCommerceItemShippingInfoTools = pCommerceItemShippingInfoTools;
	}

	/**
	 * get cart property
	 * 
	 * @return
	 */
	public OrderHolder getCart() {
		return cart;
	}

	/**
	 * set cart property
	 * 
	 * @param cart
	 */
	public void setCart(OrderHolder cart) {
		this.cart = cart;
	}
	
	/**
	 * Set the Profile property.
	 * @param pProfile a <code>Profile</code> value
	 */
	public void setProfile(Profile pProfile) {
		mProfile = pProfile;
	}

	/**
	 * Return the Profile property.
	 * @return a <code>Profile</code> value
	 */
	public Profile getProfile() {
		return mProfile;
	}
	  
	/**
	 * @return the purchaseProcessorConfiguration
	 */
	public PurchaseProcessConfiguration getPurchaseProcessorConfiguration() {
		return purchaseProcessorConfiguration;
	}


	/**
	 * @param purchaseProcessorConfiguration the purchaseProcessorConfiguration to set
	 */
	public void setPurchaseProcessorConfiguration(
			PurchaseProcessConfiguration purchaseProcessorConfiguration) {
		this.purchaseProcessorConfiguration = purchaseProcessorConfiguration;
	}
	
}
