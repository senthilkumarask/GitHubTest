package com.bbb.commerce.checkout.droplet;


import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.constants.BBBCheckoutConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * @author BBB
 *
 */
public class BBBPackNHoldDroplet extends BBBDynamoServlet {
    BBBCheckoutManager manager;
    private BBBCatalogTools mCatalogTools;
    private static final String SCHOOL_COOKIE = "SchoolCookie";
    private static final String HAS_SINGLE_COLLEGE_ITEM = "hasSingleCollegeItem";
    private static final String HAS_ALL_PACKNHOLD_ITEMS = "hasAllPackNHoldItems";
    private static final String IS_PACK_AND_HOLD_FLAG_ENABLED = "isPackAndHoldFlag";
    
    /**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param pCatalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

    @Override

    public void service(DynamoHttpServletRequest req,
            DynamoHttpServletResponse res) throws ServletException, IOException {
    	
    	BBBPerformanceMonitor.start("BBBPackNHoldDroplet", "service");
		logDebug("starting - service method BBBPackNHoldDroplet");
		
        String siteId = SiteContextManager.getCurrentSiteId();
        String collegeIdValue = req.getCookieParameter(SCHOOL_COOKIE);
    	BBBHardGoodShippingGroup sg = null;
        boolean isPackHold = false;
    	boolean hasSingleCollegeItem = false;
    	boolean hasAllPackNHoldItems = false;
		boolean isFromSPC = Boolean.parseBoolean(req.getParameter(BBBCoreConstants.IS_SINGLE_PAGE_CHECKOUT_ENABLED));
		boolean isPackAndHoldFlag =false;
		if(req.getObjectParameter(BBBCoreConstants.ORDER) !=null)
		{
			Order order = (Order) req.getObjectParameter(BBBCoreConstants.ORDER);
			logDebug("Order Details: "+order);
			
        //for pack and hold only one shipping group will be present;
		@SuppressWarnings("unchecked")
		List<ShippingGroup> shippingGrp = order.getShippingGroups();
		hasAllPackNHoldItems = getManager().hasAllPackNHoldItems(siteId, order, isFromSPC);
		hasSingleCollegeItem = getManager().hasEvenSingleCollegeItem(siteId, order);
		isPackAndHoldFlag = ((BBBOrderImpl)order).isPackAndHoldFlag();
        if(collegeIdValue == null){
        	//if collegeId value is null then read the collegeId from Order object 
			//as the college id attribute will be set in BBBCommitOrderFormhandler
			collegeIdValue = ((BBBOrderImpl)order).getCollegeId(); 
		}
        
        if (hasSingleCollegeItem) {
	        if (!BBBUtility.isEmpty(collegeIdValue) && hasAllPackNHoldItems) {
				isPackHold = true;
			} else {
				isPackHold = false;
				// if order has even a single college item then, pack n hold
				// check box will be visible and it will be editable 
				}
	        //BBBSL-8952 - duplicate code removed from BBBPackNHoldDroplet on both the conditions
	        
	        	if (!shippingGrp.isEmpty() && shippingGrp.size() == 1
						&& shippingGrp.get(0) instanceof BBBHardGoodShippingGroup
						&& ((BBBHardGoodShippingGroup) shippingGrp.get(0)).getShipOnDate() != null ) {
					
					sg = (BBBHardGoodShippingGroup) shippingGrp.get(0);
					Calendar pacKCal = Calendar.getInstance();					
					pacKCal.setTime(sg.getShipOnDate());
	
					String month = Integer.toString(pacKCal.get(Calendar.MONTH) + 1);
					if (month != null && month.length() == 1) {
						month = BBBCheckoutConstants.STRING_ZERO + month;
					}
	
					req.setParameter(BBBCheckoutConstants.PACKANDHOLDDATE,
							month + BBBCheckoutConstants.DATE_SEPARATOR
									+ pacKCal.get(Calendar.DATE)
									+ BBBCheckoutConstants.DATE_SEPARATOR
									+ pacKCal.get(Calendar.YEAR));
					
				}
				else{
					req.setParameter(BBBCheckoutConstants.PACKANDHOLDDATE, BBBCheckoutConstants.BLANK);
				}
	        req.setParameter(HAS_SINGLE_COLLEGE_ITEM, Boolean.valueOf(hasSingleCollegeItem));
	        req.setParameter(HAS_ALL_PACKNHOLD_ITEMS, Boolean.valueOf(hasAllPackNHoldItems));
	        req.setParameter(IS_PACK_AND_HOLD_FLAG_ENABLED, Boolean.valueOf(isPackAndHoldFlag));
	        req.setParameter(BBBCheckoutConstants.ISPACKHOLD, Boolean.valueOf(isPackHold));
			req.serviceParameter(BBBCoreConstants.OUTPUT, req, res);
    	}
		}
        logDebug("Exiting - service method BBBPackNHoldDroplet");		
		BBBPerformanceMonitor.end("BBBPackNHoldDroplet", "service");
    
    }

    /**
     * @param pCollegeId
     * @return Date
     */
    // BBBSL-9726 - this method overwritten the ShipOnDate() 
    
  /*  public Date getShipOnDateByCollegeId(String pCollegeId){
    	Date shippingEndDate = null;
    	SimpleDateFormat dateformat = new SimpleDateFormat(MM_DD_YYYY);
    	Date date = new Date();
    	String currentDate = dateformat.format(date);
    	boolean dateEligible = true;
    	if(null != pCollegeId){
    		this.beddingShipAddrVO = getCatalogTools().getBeddingShipAddrVO(pCollegeId);
    		try {
				dateEligible = getCatalogTools().validateBeddingAttDate(this.beddingShipAddrVO.getShippingEndDate(), currentDate);
			} catch (ParseException e) {
				logError(("Error while data parsing College Id : " + pCollegeId + "beddingShipAddrVO.getShippingEndDate() : " +
						this.beddingShipAddrVO.getShippingEndDate() + "currentDate : " + currentDate), e);
			}
    		if(null != this.beddingShipAddrVO && dateEligible){
    			DateFormat formatter = new SimpleDateFormat(MM_DD_YYYY);
    			try {
    				if(null != this.beddingShipAddrVO.getShippingEndDate()){
    					shippingEndDate = formatter.parse(this.beddingShipAddrVO.getShippingEndDate());
    				}
    			}catch (ParseException e) {
    				logError("BBBPackNHoldDroplet: Error in getShipOnDateByCollegeId Method", e);
    			}
    		}
    	}
    	return shippingEndDate;
    }*/

    /**
     * @return manager
     */
    public BBBCheckoutManager getManager() {
        return this.manager;
    }
    
    /**
     * @param pManager
     */
    public void setManager(BBBCheckoutManager pManager) {
        this.manager = pManager;
    }
}
