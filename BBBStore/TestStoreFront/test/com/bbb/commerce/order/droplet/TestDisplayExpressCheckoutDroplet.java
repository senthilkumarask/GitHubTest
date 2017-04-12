package com.bbb.commerce.order.droplet;

import java.util.List;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileFormHandler;
import com.bbb.account.BBBProfileManager;
import com.bbb.commerce.catalog.vo.ShipMethodVO;
import com.bbb.commerce.common.ExpressCheckoutVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestDisplayExpressCheckoutDroplet extends BaseTestCase {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testExpressCheckoutDroplet() throws Exception {
		

		final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
		final String pSiteId = (String) this.getObject("siteId");
		
		
		//Login the user
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        bbbProfileFormHandler.setLoggingDebug(true);
        bbbProfileFormHandler.setLoggingInfo(true);
        bbbProfileFormHandler.setLoggingError(true);
        this.getRequest().setParameter("userCheckingOut","asdf");
        System.out.println("TestBBBProfileFormHandler.testHandleLogin.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

       
        if((bbbProfileFormHandler.getOrder()!=null) && (bbbProfileFormHandler.getOrder().getId()!=null)){
            bbbProfileFormHandler.getShoppingCart().setCurrent(null);

        }
        final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
        
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        ServletUtil.setCurrentResponse(this.getResponse());
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        manager.updatePassword(email, password);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());
		
		//Check for Express Checkout Condition
        final DynamoHttpServletRequest pRequest = this.getRequest();
        
        final Profile profile = bbbProfileFormHandler.getProfile();
        final MutableRepositoryItem defaultShippingAddress = (MutableRepositoryItem) profile.getPropertyValue("shippingAddress");
        final String sku = (String)this.getObject("sku");
        final String address1 = "Address1";
        defaultShippingAddress.setPropertyValue("address1", address1);
        pRequest.setRequestURI("/store/sdfasdfsd/asdfasd");
        ServletUtil.setCurrentRequest(pRequest);
        ServletUtil.setCurrentResponse(this.getResponse());
        profile.setPropertyValue("shippingAddress", defaultShippingAddress);

        final BBBOrder inSessionOrder = (BBBOrder) bbbProfileFormHandler.getOrder();
        inSessionOrder.setSiteId(pSiteId);
                
        pRequest.setParameter(BBBCoreConstants.PROFILE, profile);
		pRequest.setParameter(BBBCoreConstants.ORDER, inSessionOrder);
		pRequest.setParameter(BBBCoreConstants.SITE_ID, pSiteId);
		
		SiteContextManager siteContextManager = (SiteContextManager) getObject("siteContextManager");
		atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
		ServletUtil.setCurrentResponse(this.getResponse());
		try {
			siteContextManager.pushSiteContext(BBBSiteContext
					.getBBBSiteContext(pSiteId));
		} catch (SiteContextException siteContextException) {
			throw new BBBSystemException("Exception" + siteContextException);
		}


		DisplayExpressCheckoutDroplet displayExpressCheckoutDroplet = (DisplayExpressCheckoutDroplet) this.getObject("displayExpressCheckoutDroplet");
		ExpressCheckoutVO checkoutVO = displayExpressCheckoutDroplet
				.getExpressCheckoutCondition();
		assertNotNull(checkoutVO);	
		
		//Logout the user
		bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
		assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()==0);
	    assertTrue(bbbProfileFormHandler.getProfile().isTransient());
	    bbbProfileFormHandler.getErrorMap().clear();
	    bbbProfileFormHandler.getFormExceptions().clear();
	    bbbProfileFormHandler.setFormErrorVal(false);
       

	}
}
