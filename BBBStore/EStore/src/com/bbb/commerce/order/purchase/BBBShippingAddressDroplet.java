package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.HardgoodShippingGroup;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.commerce.common.BBBAddressContainer;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.vo.BeddingShipAddrVO;
import com.bbb.utils.BBBUtility;

/**
 * @author nagarg
 *
 */
public class BBBShippingAddressDroplet extends BBBDynamoServlet {
    private static final String SCHOOL_COOKIE = "SchoolCookie";
	private static final String STATES = "states";
    private static final String KEYS = "keys";
    protected static final String SELECTED_ADDRESS = "selectedAddress";
    protected static final String GROUP_ADDRESSES = "groupAddresses";
    private static final String REGISTRIES_ADDRESSES = "registriesAddresses";
    protected static final String ADDRESS_CONTAINER = "addressContainer";
    protected static final String SHIPPING_GROUP = "shippingGroup";
    protected static final String END = "end";
    
    //
    /**
	 * Variable created for API to get the current order
	 */
	private BBBOrder mOrder;
	
    private BBBCheckoutManager mCheckoutMgr;

    private BBBCatalogTools mCatalogTools;
    /**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}

	@SuppressWarnings("unchecked")
    @Override
    /**
     * Service method takes profile, order, addresscontainer and 
     * outputs list for addresses for profile, registry and shippinggroup.
     */
    public void service(final DynamoHttpServletRequest req,
            final DynamoHttpServletResponse res) throws ServletException, IOException {

        BBBPerformanceMonitor.start("BBBShippingAddressDroplet", "service");        
       
        Boolean isPackHold; 
        String collegeIdValue = BBBCoreConstants.BLANK;
        final BBBAddressContainer addressContainer = (BBBAddressContainer) req.getObjectParameter(ADDRESS_CONTAINER);
        //String pSelectedAddressKey = req.getParameter("selectedAddress");
        final Profile profile = (Profile) req.getObjectParameter(BBBCoreConstants.PROFILE);
        final BBBOrder order = (BBBOrder) req.getObjectParameter(BBBCoreConstants.ORDER);
        collegeIdValue =  req.getCookieParameter(SCHOOL_COOKIE);
        /* for multi shipping address page, items will have separate 
         * dropdowns for addresses, each should pass the shippingroup */
        HardgoodShippingGroup shippingGroup = 
                (HardgoodShippingGroup) req.getObjectParameter(SHIPPING_GROUP);           
        
        Address shippingGroupAddress = null;
        String addressSelected = null;
        shippingGroup = orderShippingGroup(order, shippingGroup);
        if(shippingGroup!= null && !StringUtils.isEmpty(shippingGroup.getShippingAddress().getAddress1())) {
            addressSelected = shippingGroup.getId();
            shippingGroupAddress = shippingGroup.getShippingAddress();
        }
        logDebug("shippingGroup Address is  " + addressSelected);
       
        req.setParameter("shippingGroup", shippingGroup);
        
        final String siteId = order.getSiteId();
        setDefaultAddressFromProfile(req,res,profile, siteId);
        
        final List<StateVO> stateList = getCheckoutMgr().getStates(siteId,true, null);
        sortBasedStateName(stateList);
        
        req.setParameter(STATES, stateList);
        if(stateList != null) {
            logDebug("States count " + stateList.size());
        }
        
        final List<BBBAddress> allRegistryAddresses = getCheckoutMgr().getRegistryShippingAddress(siteId, order);
        
        String selectedAddress = addAddressToContainer(allRegistryAddresses, addressContainer, shippingGroupAddress);
        if(selectedAddress != null) {
            addressSelected = selectedAddress;
        }
        logDebug("Registry address count " + allRegistryAddresses.size());
        
        final List<BBBAddress> allShippingAddresses = new ArrayList<BBBAddress>();
        
        try {
            @SuppressWarnings("rawtypes")
            final List profileShippingAddresses = getCheckoutMgr().getProfileShippingAddress(profile, siteId);
            allShippingAddresses.addAll(profileShippingAddresses);
            selectedAddress = addAddressToContainer(allShippingAddresses, addressContainer, shippingGroupAddress);
            if(selectedAddress != null) {
                addressSelected = selectedAddress;
            }
            
        } catch (Exception e) {
            logError(LogMessageFormatter.formatMessage(req, "Error getting all the shipping addresses from profile"), e);
            req.setParameter("errMsg","addressError:1023");
        }
            
            
        addressSelected = getDefaultAddressId(req, profile,
                addressSelected, siteId);            
        
        if(addressSelected == null && allRegistryAddresses.size() > 0){
        	
       		 selectedAddress = allRegistryAddresses.get( allRegistryAddresses.size() - 1).getIdentifier();
       		 addressSelected = selectedAddress;
       	   
        }
        req.setParameter("profileAddresses", allShippingAddresses);
        logDebug("profile adddress count is " + allShippingAddresses.size());   
        
        final List<BBBAddress> shippinggroupAddresses = getCheckoutMgr().getShippinggroupAddresses(order);
        
       
        
         //BBBSL-971 starts
         if(req.getObjectParameter("isPackHold") != null){
         	  isPackHold =  true;
         }else{
         	isPackHold = false;
          } 
         
         // making the company name null in case company name is college id
         if(!isPackHold){
        	 for(BBBAddress address : shippinggroupAddresses){
        		 if(null!=collegeIdValue && collegeIdValue.equals(address.getCompanyName())){
        			 address.setCompanyName(BBBCoreConstants.BLANK);
        		 }
        	 }
         }
         addAddressToContainer(shippinggroupAddresses, addressContainer);
         
         
        if(order.getShippingGroups() !=null && isPackHold && collegeIdValue != null){
        	BeddingShipAddrVO beddingShipAddrVO = getCatalogTools().validateBedingKitAtt(order.getShippingGroups(), collegeIdValue);
        	if(beddingShipAddrVO !=null){
	        		SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
	        		Date date = new Date();
	        		String currentDate = dateformat.format(date);
	        	try {
					boolean	flagBeddingShip = false;
	        		if(!BBBUtility.isEmpty(beddingShipAddrVO.getShippingEndDate())){
					flagBeddingShip = getCatalogTools().validateBeddingAttDate(beddingShipAddrVO.getShippingEndDate(), currentDate);
	        		}
					if(flagBeddingShip){
						addressSelected="";
						req.setParameter("collegeAddress", beddingShipAddrVO);
					}
				} catch (ParseException e) {
					logError(LogMessageFormatter.formatMessage(req, "Error getting while date parsing"), e);	 
				}	
	         }        	 	
        } 
        // BBBSL-971 ends
        setParameters(req, res, addressContainer, addressSelected,
                allRegistryAddresses, shippinggroupAddresses);
        BBBPerformanceMonitor.end("BBBShippingAddressDroplet", "service");
    }
    
    private void setDefaultAddressFromProfile(final DynamoHttpServletRequest req,
            final DynamoHttpServletResponse res,Profile profile, String siteId)
    {
    	try {
            logDebug("BBBShippingAddressDroplet.setDefaultAddressFromProfile starts ");   
    		
			BBBAddress defaultShippingAddress = (BBBAddress)getCheckoutMgr().getProfileAddressTool().getDefaultShippingAddress(profile, siteId);
			if(defaultShippingAddress.getAddress1() != null)
			{
				defaultShippingAddress.setIsNonPOBoxAddress(BBBUtility.isNonPOBoxAddress(defaultShippingAddress.getAddress1(), defaultShippingAddress.getAddress2()));
				req.setParameter("defaultShippingAddress", defaultShippingAddress);
			}
			else
				req.setParameter("defaultShippingAddress", null);
			BBBAddress defaultBillingAddress = (BBBAddress)getCheckoutMgr().getProfileAddressTool().getDefaultBillingAddress(profile, siteId);
			if(defaultBillingAddress != null && defaultBillingAddress.getAddress1() != null)
			{
				defaultBillingAddress.setIsNonPOBoxAddress(BBBUtility.isNonPOBoxAddress(defaultBillingAddress.getAddress1(), defaultBillingAddress.getAddress2()));
				req.setParameter("defaultBillingAddress", defaultBillingAddress);
			}
			else
				req.setParameter("defaultBillingAddress", null);
    	} catch (BBBSystemException e) {
    		logError(LogMessageFormatter.formatMessage(req, "Error getting default shipping and billing address in BBBShippingAddressDroplet.setDefaultAddressFromProfile"), e);
		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(req, "Error getting default shipping and billing address in BBBShippingAddressDroplet.setDefaultAddressFromProfile"), e);
		}
         logDebug("BBBShippingAddressDroplet.setDefaultAddressFromProfile ends ");

    }


    protected void addAddressToContainer(final List<BBBAddress> shippinggroupAddresses,
            final BBBAddressContainer addressContainer) {
        for (BBBAddress bbbAddress : shippinggroupAddresses) {
            final String addressKey = bbbAddress.getIdentifier();
            addressContainer.addAddressToContainer(addressKey, bbbAddress);
        }
        
    }


    private void setParameters(final DynamoHttpServletRequest req,
            final DynamoHttpServletResponse res,
            final BBBAddressContainer addressContainer, final String addressSelected,
            final List<BBBAddress> allRegistryAddresses,
            final List<BBBAddress> shippinggroupAddresses) throws ServletException,
            IOException {
        logDebug("shippingGroup address count is " + shippinggroupAddresses.size());     
        
        req.setParameter(REGISTRIES_ADDRESSES, allRegistryAddresses);
        
        req.setParameter(GROUP_ADDRESSES, shippinggroupAddresses);
        
        req.setParameter(ADDRESS_CONTAINER, addressContainer);
        req.setParameter(SELECTED_ADDRESS, addressSelected);
  
        logDebug("selected address is " + addressSelected);
        logDebug("address in container are " + addressContainer.getAddressMap().size());
    
        if(addressContainer.getAddressMap().size() > 0) { 
            req.setParameter(KEYS, addressContainer.getAddressMap().keySet());
            req.serviceLocalParameter(BBBCoreConstants.OUTPUT, req, res);
        } else {            
            req.serviceLocalParameter(BBBCoreConstants.EMPTY_OPARAM, req, res);            
        }
        req.serviceLocalParameter(END, req, res);
    }


    @SuppressWarnings("rawtypes")
	protected HardgoodShippingGroup orderShippingGroup(final BBBOrder order,
            HardgoodShippingGroup shippingGroup) {
        if(shippingGroup == null) {
            final List hardgoodShippingGroups = getCheckoutMgr().getShippingGroupManager().getHardgoodShippingGroups(order);
            if(hardgoodShippingGroups != null && hardgoodShippingGroups.size() > 0) {
                logDebug("shippingGroups found in order, total count  " + hardgoodShippingGroups.size());      

                shippingGroup = (HardgoodShippingGroup) hardgoodShippingGroups.get(0);//take first one            
            }
        }
        return shippingGroup;
    }


    private String getDefaultAddressId(final DynamoHttpServletRequest req,
            final Profile profile, final String addressSelected, final String siteId) {
        BBBAddress defaultShippingAddress;
        String defaultId = addressSelected;
        try {
            defaultShippingAddress = (BBBAddress)getCheckoutMgr().getProfileAddressTool().getDefaultShippingAddress(profile, siteId);
            if(defaultShippingAddress != null && 
                    !StringUtils.isEmpty(defaultShippingAddress.getAddress1()) &&
                    addressSelected == null) {
                defaultId = defaultShippingAddress.getIdentifier();                
            }
        } catch (Exception e) {
            logError(LogMessageFormatter.formatMessage(req, "Error getting default shipping address"), e);
        }
        return defaultId;
    }

       
    private String addAddressToContainer(final List<BBBAddress> nonShippinggroupAddresses,
            final BBBAddressContainer addressContainer, final Address shippingAddress) {
        String result = null;
        for (BBBAddress bbbAddress : nonShippinggroupAddresses) {
            final String addressKey = bbbAddress.getIdentifier();
            if(shippingAddress != null) {
                
                shippingAddress.setOwnerId(bbbAddress.getOwnerId());      // override the ownerId property value to skip checking while comparison
                
                if(StringUtils.isEmpty(bbbAddress.getAddress2()) && 
                        StringUtils.isEmpty(shippingAddress.getAddress2())) {//profile store null and sg store "" string, normalizing the objects
                    bbbAddress.setAddress2(shippingAddress.getAddress2());
                }
                if(StringUtils.isEmpty(bbbAddress.getMiddleName()) && 
                        StringUtils.isEmpty(shippingAddress.getMiddleName())) {//profile store null and sg store "" string, normalizing the objects
                    bbbAddress.setMiddleName(shippingAddress.getMiddleName());
                }
                if(((Address)bbbAddress).equals(shippingAddress)) {
                    result = addressKey;
                }
            } 
            addressContainer.addAddressToContainer(addressKey, bbbAddress);
                
        }
        return result;
    }

	/**
	 * This method sorts List<ShippingMethodPriceVO>
	 * 
	 * @param pListOfStateVO
	 */
	private void sortBasedStateName(final List<StateVO> pListOfStateVO) {

		if (!BBBUtility.isListEmpty(pListOfStateVO)) {

			Collections.sort(pListOfStateVO,
					new Comparator<StateVO>() {

						@Override
						public int compare(final StateVO obj1, final StateVO obj2) {

							int returnValue = 0;

							if (obj1.getStateName().compareTo(
									obj2.getStateName()) > 0) {
								returnValue = 1;
							} else {
								returnValue = -1;
							}

							return returnValue;
						}
					});
		}

	}
   
    public final BBBCheckoutManager getCheckoutMgr() {
        return mCheckoutMgr;
    }

    public final void setCheckoutMgr(final BBBCheckoutManager pCheckoutMgr) {
        this.mCheckoutMgr = pCheckoutMgr;
    }
    

    /**
	 * @return the mOrder
	 */
	public BBBOrder getOrder() {
		return mOrder;
	}

	/**
	 * @param mOrder the mOrder to set
	 */
	public void setOrder(BBBOrder pOrder) {
		this.mOrder = pOrder;
	}

}
