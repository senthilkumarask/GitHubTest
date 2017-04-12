package com.bbb.commerce.order.purchase;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.commerce.checkout.manager.BBBCheckoutManager;
import com.bbb.constants.BBBCoreConstants;

/**
 * @author jpadhi
 *
 */
public class BBBListStatesDroplet extends BBBDynamoServlet {
    	
	public static final String ELEMENT = "element";	
	public static final String STATES = "states";
	public static final String SITE_ID = "siteId";
	public static final String SHOW_MILITARY_STATES = "showMilitaryStates";
	
	private BBBCheckoutManager mCheckoutMgr;
	    
    public final BBBCheckoutManager getCheckoutMgr() {
        return mCheckoutMgr;
    }

    public final void setCheckoutMgr(BBBCheckoutManager pCheckoutMgr) {
        this.mCheckoutMgr = pCheckoutMgr;
    } 
	    
	    
    @Override
    /**
     * Service method takes comma separated shippable states.
     * creates a list out of that and set as output
     */
    public void service(DynamoHttpServletRequest req,
            DynamoHttpServletResponse res) throws ServletException, IOException {

        
        String siteId = req.getParameter(SITE_ID);
        boolean showMilitaryStates = true;

        
        if(("FALSE").equalsIgnoreCase(req.getParameter(SHOW_MILITARY_STATES))){
        	showMilitaryStates = false;
        }
        List<StateVO> stateList = getCheckoutMgr().getStates(siteId,showMilitaryStates,null);
        if(stateList != null) {
            logDebug("States count " + stateList.size());
        }
        
        if( StringUtils.isEmpty(siteId) || stateList == null){
        	req.serviceLocalParameter(BBBCoreConstants.ERROR, req, res);
        }else{         	
        	req.serviceLocalParameter(BBBCoreConstants.OUTPUT, req, res);
        	req.setParameter(STATES, stateList);            
        }
        
    }    
    
}
