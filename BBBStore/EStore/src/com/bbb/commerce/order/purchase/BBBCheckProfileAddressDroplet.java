package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.commerce.common.BBBAddress;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * @author jpadhi
 *
 */
public class BBBCheckProfileAddressDroplet extends BBBDynamoServlet {
        
    private BBBCheckoutManager mCheckoutMgr;

    
    
    @SuppressWarnings("unchecked")
    @Override
    /**
     * Service method takes profile and siteId
     * outputs if profile contains address.
     */
    public void service(final DynamoHttpServletRequest req,
            final DynamoHttpServletResponse res) throws ServletException, IOException {

        BBBPerformanceMonitor.start("BBBCheckProfileAddressDroplet", "service");
        final Profile profile = (Profile) req.getObjectParameter(BBBCoreConstants.PROFILE);
        final String siteId = (String) req.getObjectParameter(BBBCoreConstants.SITE_ID);
        final List<BBBAddress> allShippingAddresses = new ArrayList<BBBAddress>();
        
        try {
            @SuppressWarnings("rawtypes")
            final List profileShippingAddresses = getCheckoutMgr().getProfileShippingAddress(profile, siteId);
            allShippingAddresses.addAll(profileShippingAddresses);           
            
        } catch (Exception e) {
            logError(LogMessageFormatter.formatMessage(req, "Error getting all the shipping addresses from profile"), e);
        }         
        logDebug("profile adddress count is " + allShippingAddresses.size());
        req.serviceLocalParameter(BBBCoreConstants.OUTPUT, req, res);
        if(allShippingAddresses.size() > 0){
        	req.setParameter("profileAddresses", allShippingAddresses);       	   
        }else{
        	req.setParameter("profileAddresses", allShippingAddresses);  
        }
        
        BBBPerformanceMonitor.end("BBBShippingAddressDroplet", "service");
    }    
   
    public final BBBCheckoutManager getCheckoutMgr() {
        return mCheckoutMgr;
    }

    public final void setCheckoutMgr(final BBBCheckoutManager pCheckoutMgr) {
        this.mCheckoutMgr = pCheckoutMgr;
    }
    
    
}
