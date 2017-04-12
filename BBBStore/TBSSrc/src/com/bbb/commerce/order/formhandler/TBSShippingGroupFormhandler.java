/**
 * 
 */
package com.bbb.commerce.order.formhandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.order.bean.BBBShippingPriceInfo;
import com.bbb.account.BBBAddressTools;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressImpl;
import com.bbb.commerce.order.BBBShippingGroupManager;
import com.bbb.commerce.order.TBSOrderManager;
import com.bbb.commerce.order.TBSOrderTools;
import com.bbb.commerce.order.TBSShippingGroupManager;
import com.bbb.commerce.order.TBSShippingInfo;
import com.bbb.commerce.order.purchase.BBBPurchaseProcessHelper;
import com.bbb.commerce.order.purchase.CheckoutProgressStates;
import com.bbb.commerce.order.purchase.TBSPurchaseProcessHelper;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBPayPalConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.ecommerce.order.BBBStoreShippingGroup;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.order.bean.TBSCommerceItem;
import com.bbb.order.bean.TBSItemInfo;
import com.bbb.utils.BBBUtility;
import com.bbb.commerce.order.TBSOrder;

/**
 * @author pperikiti
 *
 */
public class TBSShippingGroupFormhandler extends BBBShippingGroupFormhandler{
	
	private static final String ERROR_MULTIPLE_SHIPPING = "err_shipping_multiple_shipping";
	private static final String NICKNAME_SEPARATOR = ";;";
    private static final String ERROR_SAVING_TO_PROFILE = "err_shipping_save_to_profile";
    
    private String editShipAddressID;
    private boolean updateAddress =false;
    private boolean isShippingOverrideRemovalRequired = false;
    
    public boolean isShippingOverrideRemovalRequired() {
		return isShippingOverrideRemovalRequired;
	}

	public void setShippingOverrideRemovalRequired(
			boolean isShippingOverrideRemovalRequired) {
		this.isShippingOverrideRemovalRequired = isShippingOverrideRemovalRequired;
	}
    
    /** Validates the new address. Check for required properties, and make sure to throw an error 
     * if street address contains state as AA,AE, AP for Krisch items
     * AFO/FPO.
     *
     * @param pAddress - address
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs */
	@SuppressWarnings("unchecked")
	@Override
	protected void validateShippingAddress(Address pAddress, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) 
			throws IOException, ServletException {
			vlogDebug("TBSShippingGroupFormhandler :: validateShippingAddress() method :: START");
		 	List<CommerceItem> commerceItems = getOrder().getCommerceItems();
	        Set<RepositoryItem> skuAttrRelation = null;
	        RepositoryItem skuAttribute = null;
	        String state = pAddress.getState();
	        String skuAttrId = null;
	        TBSCommerceItem tbsItem = null;
	        RepositoryItem skuItem = null;
	        if(commerceItems == null || commerceItems.isEmpty()){
	        	vlogDebug("There are no commerceItems in the order ");
	        	return;
	        }
			 if(state.equals(TBSConstants.STATE_AF_AA) || state.equals(TBSConstants.STATE_AF_AE) || state.equals(TBSConstants.STATE_AF_AP)){
		        for (CommerceItem commerceItem : commerceItems) {
		        	if(commerceItem instanceof TBSCommerceItem) {
		        		tbsItem = (TBSCommerceItem) commerceItem;
		        		skuItem = (RepositoryItem) tbsItem.getAuxiliaryData().getCatalogRef();
		        		if(skuItem != null){
		        			skuAttrRelation = (Set<RepositoryItem>) skuItem.getPropertyValue(TBSConstants.SKU_ATTRIBUTE_RELATION);
		        		}
		        		if(skuAttrRelation != null && skuAttrRelation.size() > TBSConstants.ZERO ){
		        			vlogDebug("skuAttrRelation :: "+skuAttrRelation );
		        			for (RepositoryItem skuAttrReln : skuAttrRelation) {
		        				skuAttribute = (RepositoryItem) skuAttrReln.getPropertyValue(TBSConstants.SKU_ATTRIBUTE);
		        				if(skuAttribute != null){
		        					skuAttrId = skuAttribute.getRepositoryId();
		        				}
		        				if(!StringUtils.isBlank(skuAttrId) && (skuAttrId.equals(TBSConstants.KIRSCH_SKU_ATTRIBUTE))){
		        					addFormException(new DropletException("custom commerce items cannot be shipped to APO/FO addresses"));
		        				}
		        			}
		        		}
		        	}
				}
			}  
	    vlogDebug("TBSShippingGroupFormhandler :: validateShippingAddress() method :: END");
		super.validateShippingAddress(pAddress, pRequest, pResponse);
	}
	
	/** This handler method will validate shipping address and apply the shipping groups to the order.
    *
    * @param pRequest a <code>DynamoHttpServletRequest</code> value
    * @param pResponse a <code>DynamoHttpServletResponse</code> value
    * @return a <code>boolean</code> value
    * @exception ServletException if an error occurs
    * @exception IOException if an error occurs */
   public boolean handleAddShipping(final DynamoHttpServletRequest pRequest,
                   final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
       final String contextPath = pRequest.getContextPath();
       String failureURL = null;
       if (!BBBUtility.isEmpty(getCheckoutProgressStates().getFailureURL())
                       && !getCheckoutProgressStates().getFailureURL()
                                       .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
           failureURL = contextPath + getCheckoutProgressStates().getFailureURL();
       }
       
       String lRequestURI = pRequest.getRequestURI();

       String successURL = failureURL;

       Transaction tr = null;
       final String myHandleMethod = "BBBShippingGroupFormHandler.handleAddShipping";
       final RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();

       if ((rrm == null) || rrm.isUniqueRequestEntry(myHandleMethod)) {
           BBBPerformanceMonitor.start(BBBPerformanceConstants.ADD_SINGLE_SHIPPING, myHandleMethod);
           try {
               tr = ensureTransaction();

               if (!checkFormRedirect(null, failureURL, pRequest, pResponse)) {
                   return false;
               }
                                          
               final BBBOrderImpl order = (BBBOrderImpl) this.getOrder();
               this.logInfo(myHandleMethod + " start :: orderId : " + order.getId());
               if(null != order.getCouponListVo() && order.getCouponListVo().size() > 0){
           			this.logInfo(" available coupons in order : " + order.getCouponListVo().get(0).getmPromoId());
           		}
               synchronized (getOrder()) {
                   try {
                       preAddShipping(pRequest, pResponse);

                       if (getFormError()) {
                           if (isLoggingDebug()) {
                               logDebug("Redirecting due to form error in preMoveToBilling.");
                           }
                           setNewShipToAddressName(null);
                           return checkFormRedirect(null, failureURL, pRequest, pResponse);
                       }

                       addShipping(pRequest, pResponse);

                       if (getFormError()) {
                           if (isLoggingDebug()) {
                               logDebug("Redirecting due to form error in moveToBilling");
                           }

                           return checkFormRedirect(null, failureURL, pRequest, pResponse);
                       }

                       if (isOrderContainRestrictedSKU(getOrder(),null,"")) {
                           // Redirect request to error url
                           addFormException(new DropletException(getMsgHandler().getErrMsg(
                                           BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU, LOCALE_EN, null),
                                           BBBCoreConstants.SHIP_ZIP_CODE_RESTRICTED_FOR_SKU));
                           if (!BBBUtility.isEmpty(failureURL)) {
                               failureURL = failureURL + SHIPPING_RESTRICTION_TRUE;
                           }
                           return checkFormRedirect(failureURL, failureURL, pRequest, pResponse);
                       }

                       runProcessValidateShippingGroups(getOrder(), getUserPricingModels(),
                                       getUserLocale(pRequest, pResponse), getProfile(), null);
                   } catch (final Exception exc) {
                       setNewShipToAddressName(null);
                       addFormException(new DropletException(getMsgHandler().getErrMsg(
                                       ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                       if (isLoggingError()) {
                           logError("Exception occured", exc);
                       }
                   }

                   if (getFormError()) {
                       if (isLoggingDebug()) {
                           logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
                       }

                       return checkFormRedirect(null, failureURL, pRequest, pResponse);
                   }
                   
                   if (!this.getStoreSGMethodName().equalsIgnoreCase(getShippingOption())) {

                	   giftMessaging();
                   }

                   if (getFormError()) {
                       if (isLoggingDebug()) {
                           logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
                       }
                       return checkFormRedirect(null, failureURL, pRequest, pResponse);
                   }

                   postAddShipping(pRequest, pResponse);

                   if(isShippingOverrideRemovalRequired())
            	   {
                   try {
                	   		String siteId =  SiteContextManager.getCurrentSiteId();
							if(siteId.contains(TBSConstants.BED_BATH_US)){
								siteId = TBSConstants.BED_BATH_US;
							} else if(siteId.contains(TBSConstants.BED_BATH_CA)){
								siteId = TBSConstants.BED_BATH_CA;
							} else if(siteId.contains(TBSConstants.BUY_BUY_BABY)){
								siteId = TBSConstants.BUY_BUY_BABY;
							}
                		   
                			   	Map<String, Boolean> pricemap = ((TBSOrder)this.getOrder()).getOverridePriceMap();
                			   	if(!BBBUtility.isMapNullOrEmpty(pricemap) && pricemap.containsKey(((BBBHardGoodShippingGroup)this.getOrder().getShippingGroups().get(0)).getId())){
                			   		pricemap.remove(((BBBHardGoodShippingGroup)this.getOrder().getShippingGroups().get(0)).getId());
                			   	}
                			   	if(BBBUtility.isMapNullOrEmpty(pricemap))
                		   		{
                			   		TBSOrder tbsOrder = (TBSOrder)this.getOrder();
                			   		tbsOrder.setTBSApprovalRequired(false);
                			   	}
                			   	((TBSOrder)this.getOrder()).setOverridePriceMap(pricemap);
                		   		TBSShippingInfo shipInfo = ((BBBHardGoodShippingGroup)((TBSOrder)this.getOrder()).getShippingGroups().get(0)).getTbsShipInfo();
                		   		if(shipInfo == null) 
                		   		{
                			   		if( isLoggingDebug() ) logDebug("Creating new shipInfo");
       		    					shipInfo = ((TBSOrderTools)this.getOrderManager().getOrderTools()).createTBSShippingInfo();
       		    					((BBBHardGoodShippingGroup)((TBSOrder)this.getOrder()).getShippingGroups().get(0)).setTbsShipInfo(shipInfo);
                		   		}
                		   		shipInfo.setShipPriceOverride(false);
                		   		shipInfo.setShipPriceReason(null);
                		   		double shipPriceValue=super.getCatalogUtil().getShippingFee(siteId, this.getShippingOption(), this.getAddress().getState(), this.getOrder().getPriceInfo().getRawSubtotal(),null);
                		   		shipInfo.setShipPriceValue(shipPriceValue);
                		   		((BBBShippingPriceInfo)((BBBHardGoodShippingGroup)this.getOrder().getShippingGroups().get(0)).getPriceInfo()).setRawShipping(shipPriceValue);
                		   		((BBBShippingPriceInfo)((BBBHardGoodShippingGroup)this.getOrder().getShippingGroups().get(0)).getPriceInfo()).setFinalShipping(shipPriceValue);
                	     
                	   }catch(Exception exc){
                	    	 if (isLoggingError()) {
                	    		 logError("Exception occured after postAddShipping in TBSShippingGroupFormhandler.handleAddShipping() ",
                                           exc);
                	    	 	}
                	    	 addFormException(new DropletException(getMsgHandler().getErrMsg(
                                       ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                	     	}
            	   	}
                   
                   try {
                       getOrderManager().updateOrder(getOrder());
                       // If NO form errors are found, redirect to the success URL.
                       // If form errors are found, redirect to the error URL.
                       
                       // Added as Part of Story 83-N: Start
                       //BBBOrderImpl order = (BBBOrderImpl)getOrder();
                       if(order.isPayPalOrder()  && !getFormError()){
                       	logDebug("Order is of type Paypal");
                       	//getPayPalSessionBean().setPayPalShipAddValidated(true);
                       	getCheckoutProgressStates().setCurrentLevel(
                                   CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
                       	if (!BBBUtility.isEmpty(getCheckoutProgressStates().getFailureURL())
                                   && !getCheckoutProgressStates().getFailureURL()
                                                   .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
	                        	if(getPayPalSessionBean().isFromPayPalPreview()){
	                        		logDebug("Coming from preview page , so redirect to review page");
	                        		successURL = contextPath + getCheckoutProgressStates().getCheckoutFailureURLs().get(CheckoutProgressStates.DEFAULT_STATES.REVIEW.toString());
	                        	} else if (lRequestURI.contains("checkoutType.jsp")){
	                        		successURL = lRequestURI;
	                        	}
	                        	else{
	                        		logDebug("Coming from validation page , so redirect to validation page for further validations");
				                    successURL = contextPath + BBBPayPalConstants.PAYPAL_REDIRECT_PATH;
	                        	}
                       	}
                       }
                       // Added as Part of Story 83-N: End
                       else{
		                    getCheckoutProgressStates().setCurrentLevel(
		                                    CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
		                    if (!BBBUtility.isEmpty(getCheckoutProgressStates().getFailureURL())
		                                    && !getCheckoutProgressStates().getFailureURL()
		                                                    .equals(BBBCoreConstants.ATG_REST_IGNORE_REDIRECT)) {
		                        successURL = contextPath + getCheckoutProgressStates().getFailureURL();
		                    }
		
		                    if (collegeAddress) {
		                        successURL = successURL + "?colg=true";
		                    }
                       }

                   } catch (final Exception exc) {
                       if (isLoggingError()) {
                           logError("Exception occured while updateorder during BBBShippingGroupFormhandler.handleAddShipping() ",
                                           exc);
                       }
                       addFormException(new DropletException(getMsgHandler().getErrMsg(
                                       ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
                   }
               } // synchronized
               
               this.logInfo(myHandleMethod + " end :: orderId : " + order.getId());
               if(null != order.getCouponListVo() && order.getCouponListVo().size() > 0){
           			this.logInfo(" available coupons in order : " + order.getCouponListVo().get(0).getmPromoId());
           		}

               return checkFormRedirect(successURL, failureURL, pRequest, pResponse);
           } finally {
               if (tr != null) {
                   commitTransaction(tr);
               }
               BBBPerformanceMonitor.end(BBBPerformanceConstants.ADD_SINGLE_SHIPPING, myHandleMethod);
               if (rrm != null) {
                   rrm.removeRequestEntry(myHandleMethod);
               }
           }
       }
       return false;
   }
   
   @Override
   protected void preShipToAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
   IOException {
	   if (this.getStoreSGMethodName().equalsIgnoreCase(getShippingOption())) {
		   String storeId =  (String) pRequest.getSession().getAttribute("storenumber");

		   if (!StringUtils.isEmpty(storeId) ) {
			   try {
				   final ShippingGroup storePickupShippingGroup = this.getManager().getStorePickupShippingGroup(
						   storeId, this.getOrder());
				   
			   } catch (final CommerceException e) {
				   this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(
						   ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null), ERROR_SHIPPING_GENERIC_ERROR));
			   }
		   }
		   return;
	   }
	   super.preShipToAddress(pRequest, pResponse);
   }
   
   @Override
   public void preAddShipping(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
           throws ServletException, IOException {
		if (isUpdateProfileAdress()) {
		   if(this.getPoBoxStatus()!=null && this.getPoBoxStatus().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXSTATUS))
			   ((BBBAddress)this.getAddress()).setQasValidated(true);
		   if(this.getPoBoxFlag()!=null && this.getPoBoxFlag().equalsIgnoreCase(BBBCoreConstants.QAS_POBOXFLAG))
		   		((BBBAddress)this.getAddress()).setPoBoxAddress(true);
		   setNewAddress(false);
		   ((BBBAddress) getAddress()).setId(getEditShipAddressID());
		   setShipToAddressName(getEditShipAddressID());
		   updateProfileAddress(pRequest, pResponse);
		   this.preShipToAddress(pRequest, pResponse);
		} else {
		   super.preAddShipping(pRequest, pResponse);
		}
		if(((TBSOrder)this.getOrder())!=null)
		   {
			   this.setShippingOverrideRemovalRequired(false);
			   if(!BBBUtility.isListEmpty(this.getOrder().getShippingGroups()) && this.getOrder().getShippingGroups().get(0)!=null && !BBBUtility.isEmpty(this.getShippingOption()))
			   {
				   if(!(this.getShippingOption().equalsIgnoreCase(((BBBHardGoodShippingGroup)this.getOrder().getShippingGroups().get(0)).getShippingMethod()))){
					   TBSShippingInfo shipInfo = ((BBBHardGoodShippingGroup)((TBSOrder)this.getOrder()).getShippingGroups().get(0)).getTbsShipInfo();
					   if(shipInfo!=null && shipInfo.isShipPriceOverride()){
					   		this.setShippingOverrideRemovalRequired(true);
   }
				   }
			   }
		   }
   }
   
   @Override
	public void addShipping(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	   	this.logInfo("TBSShippingGroupFormhandler.addShipping() :: profileID : "+this.getProfile().getRepositoryId() + " OrderID : " + this.getOrder().getId());
	   
	   	if (this.getStoreSGMethodName().equalsIgnoreCase(getShippingOption())) {

			final BBBAddress addressFromContainer = this.getAddressContainer().getAddressFromContainer(this.getShipToAddressName());
			// addressFromContainer.setSource("shipping");
			
			final HardgoodShippingGroup firstNonGiftHardgoodShippingGroupWithRels = this.getShippingGroupManager()
					.getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder());
			BBBStoreShippingGroup storeSg = null;
			List<ShippingGroup> shippingGroupList = getOrder().getShippingGroups();
			for (final ShippingGroup sg : shippingGroupList) {
				if (((BBBShippingGroupManager)getShippingGroupManager()).getStoreShippingGroupType().equalsIgnoreCase(
						sg.getShippingGroupClassType())) {
					storeSg = (BBBStoreShippingGroup) sg;
				}
			}
			
			List<CommerceItem> commerceItems = getOrder().getCommerceItems();
			
			try {
				if(firstNonGiftHardgoodShippingGroupWithRels != null) {
					for (CommerceItem commerceItem : commerceItems) {

						getCommerceItemManager().removeItemQuantityFromShippingGroup(getOrder(), commerceItem.getId(), firstNonGiftHardgoodShippingGroupWithRels.getId(), commerceItem.getQuantity());

						getCommerceItemManager().addItemQuantityToShippingGroup(getOrder(), commerceItem.getId(), storeSg.getId(), commerceItem.getQuantity());

						storeSg.setShippingMethod(getShippingOption());
					}
				}
			} catch (CommerceException e1) {
				if (this.isLoggingError()) {
					this.logError(" some error occured while updating shipping address", e1);
				}
				this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null),
						ERROR_SHIPPING_GENERIC_ERROR));
			}
			
			final Map registryMap = ((BBBOrderImpl) this.getOrder()).getRegistryMap();
			this.setRegistryInfo(addressFromContainer, registryMap);

			try {
				/*((BBBShippingGroupManager) this.getShippingGroupManager()).shipToAddress(this.getBBBShippingInfoBean(),
						(BBBHardGoodShippingGroup) firstNonGiftHardgoodShippingGroupWithRels);*/
				// LTL RM-27711 | Remove empty SG | In case LTL item is removed
				// on cart, LTL SG persists in Order with no
				// relationship, hence need to remove all empty SG.
				this.getManager().removeEmptyShippingGroups(this.getOrder());
			} catch (CommerceException e) {
				if (this.isLoggingError()) {
					this.logError(" some error occured while updating shipping address", e);
				}
				this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SHIPPING_GENERIC_ERROR, LOCALE_EN, null),
						ERROR_SHIPPING_GENERIC_ERROR));
			}

			if (this.getFormError()) {
				return;
			}
			return;
		}
		super.addShipping(pRequest, pResponse);
	}
   
   
   @Override
   protected void postShipToNewAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
   IOException {
	   if (this.getStoreSGMethodName().equalsIgnoreCase(getShippingOption())) {
		   return;
	   }
	   if (isUpdateProfileAdress()) {		   
           final BBBHardGoodShippingGroup defaultHGSG = (BBBHardGoodShippingGroup) this.getShippingGroupManager()
                           .getFirstNonGiftHardgoodShippingGroupWithRels(this.getOrder());
           defaultHGSG.setSourceId(Long.toString(new Date().getTime()));
	   } else {
		   super.postShipToNewAddress(pRequest, pResponse);
	   }
   }

	private boolean isUpdateProfileAdress() {

		return this.isUpdateAddress() && BBBUtility.isNotEmpty(getEditShipAddressID()) 
					&& (BBBCoreConstants.TRUE.equals(this.getShipToAddressName()) || BBBCoreConstants.COLLEGE.equals(this.getShipToAddressName()) || 
							getEditShipAddressID().equals(getShipToAddressName()))
					&& this.isSingleShippingGroupCheckout() && this.getSaveShippingAddress()
	                && !this.getProfile().isTransient() && !this.getShipToAddressName().contains(REGISTRY_TEXT);
	}
   
   /** 
    * This method is update to avoid the creation of the new shipping group, in case of only one shipping group for multishipping group.
    * @param pRequest a <code>DynamoHttpServletRequest</code> value
    * @param pResponse a <code>DynamoHttpServletResponse</code> value
    * @exception ServletException if an error occurs
    * @exception IOException if an error occurs */
   @SuppressWarnings({ "unchecked", "rawtypes" })
public void multipleShipping(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException, CommerceException {
       List cisiItemsList = getCisiItems();
       CommerceItemShippingInfo cisi = null;
       BBBHardGoodShippingGroup hgsg = null;
       // Setting it to false. If there is exception in multipleShipping() then page will again initialize the
       // CISIContainer
       setClearContainer(true);
       try {

       	//Start: ltl specific code start
       	
       	List<ShippingGroup> sglist = getOrder().getShippingGroups();
       	
       	//create map of commerce items in a shipping group for which shipping method is empty.
           Map<String, List<String>> sgciMap = getSgCiMapForEmptyShipMethod(sglist);
           
           //create assembly and delivery items and CISI for commerce items present in sgciMap.
           if(!sgciMap.isEmpty()){
           	getManager().createDelAssItemForSGCIMap(sgciMap, getOrder(), getShippingGroupMapContainer(), getCommerceItemShippingInfoContainer());
           }
           
           //create list of commerce items for which assembly is false and for which assembly is true in all shipping group
           Map<String, List<String>> sgciMapForWgWithAss = new HashMap<String, List<String>>();
           Map<String, List<String>> sgciMapForWgWithoutAss = new HashMap<String, List<String>>();
           createSGCIMapForWithAndWithoutAss(sglist, sgciMapForWgWithAss, sgciMapForWgWithoutAss);
           
           //add remove assembly for commerceIdListWithAssembly and commerceIdListWithoutAssembly 
           getManager().addRemoveAssForCommerceItems(sgciMapForWgWithAss, sgciMapForWgWithoutAss,
           		getOrder(), getShippingGroupMapContainer(), getCommerceItemShippingInfoContainer());

           //End: ltl specific code end
           
           for (int index = 0; index < cisiItemsList.size(); index++) {
               cisi = (CommerceItemShippingInfo) cisiItemsList.get(index);
               if (getStoreSGMethodName().equalsIgnoreCase(cisi.getShippingMethod())) {
                   continue;
               }
               if(cisiItemsList.size() == 1){
            	   hgsg = ((TBSShippingGroupManager)getManager()).getSGForIdAndMethod(getOrder(), getMultiShippingAddrCont(),
                           cisi.getShippingGroupName(), cisi.getShippingMethod());
               } else {
            	   hgsg = getManager().getMatchingSGForIdAndMethod(getOrder(), getMultiShippingAddrCont(),
            			   cisi.getShippingGroupName(), cisi.getShippingMethod());
               }
               final String inputShipGroupMethodKey = cisi.getShippingGroupName() + NICKNAME_SEPARATOR+ cisi.getShippingMethod();
               getShippingGroupContainerService().addShippingGroup(inputShipGroupMethodKey, hgsg);
               cisi.setShippingGroupName(inputShipGroupMethodKey);
               
               //Start: LTL code to set ShippingGroupName and ShippingMethod for CISI of associated delivery and assembly item if commerce item is ltl.
               if(((BBBCommerceItem)cisi.getCommerceItem()).isLtlItem()){
               	updateCISIForDelAndAssItems(cisi, inputShipGroupMethodKey);
               }
               //End: LTL code to set ShippingGroupName and ShippingMethod for CISI of associated delivery and assembly item if commerce item is ltl.
           }

           // add registrant Email Here
           applyRegistrantEmailToSG();

           applyShippingGroups(pRequest, pResponse);
           
           //apply LTLItemAssocMap to newly created shipping groups for ltl items.
           
           getManager().applyLTLItemAssocMap(sglist, getOrder());
           
           // merge cisi when shipping method is same for ltl items
           sglist = getOrder().getShippingGroups();
           for(ShippingGroup sgAfterApply : sglist){
           	//create map of commerce items with same sku in a shipping group.
           	Map<String, List<String>> skuCommItemListMap = createSkuToCommItemsMap(sgAfterApply);
           	getManager().mergeCISIforSameLTLShipMethod(sgAfterApply, skuCommItemListMap, getOrder(),
           			getPurchaseProcessHelper(), getCommerceItemShippingInfoContainer());
           }
           
           getManager().removeEmptyShippingGroups(getOrder());
           
           if (!isOrderIncludesGifts()) {
        	   List<HardgoodShippingGroup> hardgoodShippingGroupList = getShippingGroupManager()
						.getHardgoodShippingGroups(getOrder());
        	   for (HardgoodShippingGroup hardgoodShippingGroupItem : hardgoodShippingGroupList) {
        		    	((BBBPurchaseProcessHelper) this.getPurchaseProcessHelper()).removeAllGiftWrapFromShippingGroup(
                                this.getOrder(), hardgoodShippingGroupItem, SiteContextManager.getCurrentSiteId());
   			   }
           }
       } catch (CommerceException e) {
           vlogError(LogMessageFormatter.formatMessage(pRequest, ERROR_MULTIPLE_SHIPPING), e);
           addFormException(new DropletException(getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                           LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
           throw new CommerceException(e);
       }  catch (BBBSystemException e) {
       		vlogError("System Exception occurred while checking if sku is ltl in multipleShipping method::BBBShippingGroupFormHandler",e);
       		addFormException(new DropletException(getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                   LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
		} catch (BBBBusinessException e) {
			vlogError("Business Exception occurred while checking if sku is ltl in multipleShipping method::BBBShippingGroupFormHandler",e);
			addFormException(new DropletException(getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                   LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
		} catch (RepositoryException e) {
			vlogError("Error while creating Delivery Commerce Item",e);
			addFormException(new DropletException(getMsgHandler().getErrMsg(ERROR_MULTIPLE_SHIPPING,
                   LOCALE_EN, null), ERROR_MULTIPLE_SHIPPING));
		}

   }
   
   
   /** Applies the address to new HardGoodShippingGroup in order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs */
  public void addNewAddress(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
                  throws ServletException, IOException {
      String name = null;
      try {

          if (this.getSaveShippingAddress()) {
              final BBBAddressImpl newAddress = this.getCheckoutManager().saveAddressToProfile(
                              (Profile) this.getProfile(), this.getAddress(), this.getOrder().getSiteId());
              name = newAddress.getIdentifier();
          } else {
              name = Long.toString(new Date().getTime());
          }

          BBBAddressImpl bbbAddress = new BBBAddressImpl();
          bbbAddress = (BBBAddressImpl) BBBAddressTools.copyBBBAddress((BBBAddress) this.getAddress(),
                          (BBBAddress) bbbAddress);
          if(!StringUtils.isBlank(bbbAddress.getEmail()) && !getUserProfile().isTransient()){
        	  List<BBBAddressVO> profileShippingAddresses = getCheckoutManager().getProfileShippingAddress(getUserProfile(), getOrder().getSiteId());
        	  Map<String, BBBAddress> addressMap = new HashMap<String, BBBAddress>();
        	  for (BBBAddressVO bbbAddressVO : profileShippingAddresses) {	
      			addressMap.put(bbbAddressVO.getIdentifier(), bbbAddressVO);	
      		  }
        	  
        	  Set<String> addressKeySet = addressMap.keySet();
        	  BBBAddress tempAddress = null;
        	  boolean duplicateFound = false;
        	  for (String key : addressKeySet) {
        		tempAddress = addressMap.get(key);
      			if(bbbAddress instanceof Address && tempAddress instanceof Address && (tempAddress.getRegistryId()==null || tempAddress.getRegistryId().isEmpty())){
      				duplicateFound = BBBAddressTools.compare((Address)tempAddress, (Address)bbbAddress);
      				if(duplicateFound){
      					tempAddress.setEmail(bbbAddress.getEmail());
      					tempAddress.setPhoneNumber(bbbAddress.getPhoneNumber());
      					tempAddress.setAlternatePhoneNumber(bbbAddress.getAlternatePhoneNumber());
      					name = key;
      					break;
      				}
      			}
        	  }
          }
          this.getShippingAddressContainer().addNewAddressToContainer(name, bbbAddress);
          this.getShippingAddressContainer().setNewAddressKey(name);
          final int index = Integer.parseInt(this.getCisiIndex());
          if (index >= 0 && index < this.getCisiItems().size()) {
              final List commerceItemShippingInfoList = this.getCisiItems();
              final CommerceItemShippingInfo currentCISI = (CommerceItemShippingInfo) commerceItemShippingInfoList
                              .get(index);
              if (this.isLoggingDebug()) {
                  this.logDebug("CISI ShippingGroupName before adding new Address:::::::: "
                                  + currentCISI.getShippingGroupName());
                  this.logDebug("New ShippingGroupName in CISI :::: " + name);
              }
              currentCISI.setShippingGroupName(name);
          }

          pRequest.setParameter("newAddressKey", name);

      } catch (final BBBSystemException e) {
          if (this.isLoggingError()) {
              this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SAVING_TO_PROFILE), e);
          }
          this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SAVING_TO_PROFILE,
                          LOCALE_EN, null), ERROR_SAVING_TO_PROFILE));
      } catch (final BBBBusinessException e) {
          if (this.isLoggingError()) {
              this.logError(LogMessageFormatter.formatMessage(pRequest, ERROR_SAVING_TO_PROFILE), e);
          }
          this.addFormException(new DropletException(this.getMsgHandler().getErrMsg(ERROR_SAVING_TO_PROFILE,
                          LOCALE_EN, null), ERROR_SAVING_TO_PROFILE));
      }

  }
  
  	/** This method is overridden to create the new commerce items based on the shipping groups
	  *
	  * @param pRequest a <code>DynamoHttpServletRequest</code> value
	  * @param pResponse a <code>DynamoHttpServletResponse</code> value
	  * @exception ServletException if an error occurs
	  * @exception IOException if an error occurs
	*/
	 public void postMultipleShipping(final DynamoHttpServletRequest pRequest,
	                 final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
	     if (!this.getFormError()) {
	    	 try {
	    		 List<String> removeItems = new ArrayList<String>();
	    		 Order order = getOrder();
	    		 
	    		 //creating commerce items based on the relations
		    	 createItemsBasedOnRelation(pRequest, pResponse);
	            
	            //removing the split sequence number
	            List<CommerceItem> citems2 = order.getCommerceItems();
	            for (CommerceItem commerceItem : citems2) {
					if(commerceItem instanceof TBSCommerceItem){
						((TBSCommerceItem)commerceItem).setSplitSequence(0);
					}
				}
		        
		     // merging the items based on the shipping group
		        mergeItemsBasedOnShipGroup(pRequest, pResponse);
	
	        } catch (CommerceException e) {
	        	vlogError("CommerceException occurred while spliting items "+e);
	        }
	        super.postMultipleShipping(pRequest, pResponse); 
	     }
	
	 }

	 /**
	  * THis method is used to create the commerceitems based on the shippingGroupCommerceItem relations
	  * @param pRequest
	  * @param pResponse
	  * @throws CommerceException
	  * @throws ServletException
	  * @throws IOException
	  */
	private void createItemsBasedOnRelation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws CommerceException, ServletException, IOException {
		
		Order order = getOrder();
		List<String> removeItems = new ArrayList<String>();
		 Map<String, List<ShippingGroupCommerceItemRelationship>> relationMap = new HashMap<String, List<ShippingGroupCommerceItemRelationship>>();
		 List relations = order.getRelationships();
		 ShippingGroupCommerceItemRelationship spCIrelation = null;
		 String citemId = null;
		 List<ShippingGroupCommerceItemRelationship> relationsList = null;
		 // preparing the Map with commerceId and relationship
		 for (Object object : relations) {
			 if(object instanceof ShippingGroupCommerceItemRelationship){
				 spCIrelation = (ShippingGroupCommerceItemRelationship) object;
				 citemId = spCIrelation.getCommerceItem().getId();
				 if(relationMap.containsKey(citemId)){
					 relationsList = relationMap.get(citemId);
					 relationsList.add(spCIrelation);
				 } else {
					 relationsList = new ArrayList<ShippingGroupCommerceItemRelationship>();
					 relationsList.add(spCIrelation);
					 relationMap.put(citemId, relationsList);
				 }
			 }
		}
		Set<String> citems = relationMap.keySet();
		int count = 1;
	      
		for (String cId : citems) {
			List<ShippingGroupCommerceItemRelationship> citemRealtion = relationMap.get(cId);
			if(citemRealtion.size() > 1){
				//preparing the list of commerce items to remove
				removeItems.add(cId);
				CommerceItem citem = null;
				for (ShippingGroupCommerceItemRelationship shipCommerceRelation : citemRealtion) {
					citem = shipCommerceRelation.getCommerceItem();
					if(citem instanceof TBSCommerceItem){
						
						TBSCommerceItem newTbsItem = (TBSCommerceItem)getCommerceItemManager().createCommerceItem("default",
								citem.getCatalogRefId(), citem.getAuxiliaryData().getProductId(), shipCommerceRelation.getQuantity());
						((TBSPurchaseProcessHelper)getPurchaseProcessHelper()).addBBBProperties(citem, newTbsItem);
						
						if(((TBSCommerceItem)citem).getTBSItemInfo() != null){
							TBSItemInfo existinginfo = ((TBSCommerceItem)citem).getTBSItemInfo();
							TBSItemInfo tbsItemInfo =  ((TBSOrderTools) getOrderManager().getOrderTools()).createTBSItemInfo();
							
							tbsItemInfo.setPriceOveride(true);
							tbsItemInfo.setOverridePrice(existinginfo.getOverridePrice());
							tbsItemInfo.setOverrideQuantity((int)shipCommerceRelation.getQuantity());
							tbsItemInfo.setOverideReason(existinginfo.getOverideReason());
							tbsItemInfo.setCompetitor(existinginfo.getCompetitor());
							newTbsItem.setTBSItemInfo(tbsItemInfo);
						}
						newTbsItem.setSplitSequence(count);
						ShippingGroup sg = shipCommerceRelation.getShippingGroup();
						CommerceItem item = getCommerceItemManager().addItemToOrder(order, newTbsItem);
						long lUnassignedQuantity = getCommerceItemManager().getUnassignedQuantityForCommerceItem(item);
						getCommerceItemManager().addItemQuantityToShippingGroup(order, item.getId(), sg.getId(), lUnassignedQuantity);
						count++;
					}
				}
			}
		}
		
	    // removing the existing commerce items
		if(!removeItems.isEmpty()){
			for (String removeId : removeItems) {
				getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, removeId);
		        getCommerceItemManager().removeItemFromOrder(order, removeId);
			}
		}
		removeItems.clear();
		
		getManager().removeEmptyShippingGroups(order);
		getOrderManager().updateOrder(order);
		repriceOrder(pRequest, pResponse);
	}

	/**
	 * This method is used to merge the items based on the shipping groups
	 * @param pRequest
	 * @param pResponse
	 * @param removeItems
	 * @throws CommerceException
	 * @throws ServletException
	 * @throws IOException
	 */
	private void mergeItemsBasedOnShipGroup(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
			throws CommerceException, ServletException, IOException {
		
			List<String> removeItems = new ArrayList<String>();
			Order order = getOrder();
			List relations2 = order.getRelationships();
			Map<String, List<TBSCommerceItem>> shipItemsMap = new HashMap<String, List<TBSCommerceItem>>();
			ShippingGroupCommerceItemRelationship spCIrelation2 = null;
			TBSCommerceItem citem = null;
			String shipId = null;
			List<TBSCommerceItem> citemsList2 = null;
			 
			for (Object object : relations2) {
				if(object instanceof ShippingGroupCommerceItemRelationship){
					spCIrelation2 = (ShippingGroupCommerceItemRelationship)object;
					if(spCIrelation2.getCommerceItem() instanceof TBSCommerceItem){
						citem = (TBSCommerceItem) spCIrelation2.getCommerceItem();
						shipId = spCIrelation2.getShippingGroup().getId();
						if(shipItemsMap.containsKey(shipId)){
							citemsList2 = shipItemsMap.get(shipId);
							citemsList2.add(citem);
						} else {
							citemsList2 = new ArrayList<TBSCommerceItem>();
							citemsList2.add(citem);
							shipItemsMap.put(shipId, citemsList2);
						}
					}
				}
			}
			Set<String> shipSet = shipItemsMap.keySet();
			List<TBSCommerceItem> citemsList3 = null;
			Map<String, TBSCommerceItem> updateItemsMap = new HashMap<String, TBSCommerceItem>();
			for (String shipId2 : shipSet) {
				citemsList3 = shipItemsMap.get(shipId2);
				if(citemsList3.size() > 1){
					long existQty = 0;
					CommerceItem existItem = null;
					String registryId = "0";
					String override = "0";
					String refnum = "";
					String sku = null;
					String combinedKey = null;
					for (TBSCommerceItem commerceItem : citemsList3) {
						
						registryId = commerceItem.getRegistryId();
						refnum = commerceItem.getReferenceNumber();
						
						if(commerceItem.getTBSItemInfo() != null){
							override = Double.toString(commerceItem.getTBSItemInfo().getOverridePrice());
						}
						sku = commerceItem.getCatalogRefId();
						combinedKey = sku+"_"+registryId+"_"+refnum+"_"+override+"_"+shipId2;
						
						//updating the exist item qty and deleting new item based on catalogRefId
						if(updateItemsMap.containsKey(combinedKey)){
							existItem = updateItemsMap.get(combinedKey);
							existQty = existItem.getQuantity();
							getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(
									order, existItem, existQty + commerceItem.getQuantity());
							//change the Commerce Item quantity:
							existItem.setQuantity(existQty + commerceItem.getQuantity());
							removeItems.add(commerceItem.getId());
						} else {
							updateItemsMap.put(combinedKey, commerceItem);
						}
					}
				}
			}
			
			// removing the existing commerce items
			if(!removeItems.isEmpty()){
				for (String removeId : removeItems) {
					getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, removeId);
			        getCommerceItemManager().removeItemFromOrder(order, removeId);
				}
			}
			getManager().removeEmptyShippingGroups(order);
			getOrderManager().updateOrder(order);
			repriceOrder(pRequest, pResponse);
		}
	
	public boolean handleMultipleShipping(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		if(getFormError()){
			getFormExceptions().clear();
		}
		
		super.handleMultipleShipping(pRequest, pResponse);
		vlogDebug("call TBSShippingGroupFM: reintializeAndCopyLineItemDetails:");
		((TBSOrderTools) getOrderManager().getOrderTools()).reInitializeAndCopyLineItemDetails(getOrder());
		
		
		
		return true; 
	}

	@Override
	public boolean postChangeToShipOnline(final DynamoHttpServletRequest pRequest,
            final DynamoHttpServletResponse pResponse) throws CommerceException {
		((TBSOrderManager)getOrderManager()).getAutoWaiveShipDetails(getOrder(),getCommerceItemId());
		return super.postChangeToShipOnline(pRequest, pResponse);
	}

	public String getEditShipAddressID() {
		return editShipAddressID;
	}

	public void setEditShipAddressID(String editShipAddressID) {
		this.editShipAddressID = editShipAddressID;
	}

	public boolean isUpdateAddress() {
		return updateAddress;
	}

	public void setUpdateAddress(boolean updateAddress) {
		this.updateAddress = updateAddress;
	}
	
}

