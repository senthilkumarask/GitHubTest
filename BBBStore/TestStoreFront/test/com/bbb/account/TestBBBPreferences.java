package com.bbb.account;

import java.util.Map;

import atg.commerce.order.OrderManager;
import atg.nucleus.Nucleus;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;

import com.bbb.account.service.profile.BBBGetProfileInfoWebService;
import com.bbb.account.service.profile.ProfileBasicVO;
import com.bbb.account.service.profile.RequestVO;
import com.bbb.account.service.profile.ResponseVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBPreferences extends BaseTestCase{
	public void testPreferences() throws Exception{
		DynamoHttpServletRequest req = getRequest();
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		
		bbbProfileFormHandler.setExpireSessionOnLogout(false);
		req.setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
		
		req.setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
		
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(req, getResponse());
		}
		
		
		OrderManager manager = (OrderManager) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/order/OrderManager");
		
		BBBOrder bbbOrder = (BBBOrder) manager.createOrder("134255626");
		bbbProfileFormHandler.setOrder(bbbOrder);			
		
		getRequest().setParameter("userCheckingOut","asdf");	
		if(bbbProfileFormHandler.getOrder()!=null && bbbProfileFormHandler.getOrder().getId()!=null){
			bbbProfileFormHandler.getShoppingCart().setCurrent(null);
			bbbProfileFormHandler.getShoppingCart().getCurrent();
			//bbbProfileFormHandler.getOrder().setProfileId("sadf");	
		}
		System.out.println("TestBBBPreferences.testPreferences.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
		assertTrue(bbbProfileFormHandler.getProfile().isTransient());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);
		
		String pSiteId = (String) getObject("currSite");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

		String email = (String) getObject("email");
		String password = (String) getObject("password");

		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		atg.servlet.ServletUtil.setCurrentRequest(req);

		boolean isLogin = bbbProfileFormHandler.handleLogin(req, getResponse());
		assertFalse(bbbProfileFormHandler.getProfile().isTransient());
		
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			BBBStoreSessionDroplet droplet = (BBBStoreSessionDroplet)req.resolveName("/com/bbb/account/BBBStoreSessionDroplet");
			String currSite = (String) getObject("currSite");
			req.setParameter("currSite", currSite);
			droplet.service(req,getResponse());
			String obj = (String)req.getObjectParameter(BBBCoreConstants.URL);
			if(obj!=null){
				assertNotNull(obj);
				BBBGetProfileInfoWebService webservice = (BBBGetProfileInfoWebService)Nucleus.getGlobalNucleus().resolveName("/com/bbb/webservices/BBBGetProfileInfoWebService");
				BBBProfileTools tools = (BBBProfileTools)req.resolveName("/atg/userprofiling/ProfileTools");
				RepositoryItem item = tools.getItemFromEmail(email);
				Map site = (Map)item.getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
				RepositoryItem siteItem = (RepositoryItem)site.get(pSiteId);
				RequestVO reqvo = new RequestVO(); 
				reqvo.setIpAddress((String)siteItem.getPropertyValue(BBBCoreConstants.IP_ADDRESS));
				reqvo.setSiteId((String)siteItem.getPropertyValue(BBBCoreConstants.SITE_ID));
				reqvo.setToken((String)siteItem.getPropertyValue(BBBCoreConstants.TOKEN));
				ResponseVO res = webservice.getResponseVO(reqvo);
				assertNotNull(res);
				webservice.getQuery();
				webservice.getProfileTool();
				boolean status = false;
				Map map = (Map) tools.getItemFromEmail(email).getPropertyValue(BBBCoreConstants.USER_SITE_REPOSITORY_ITEM);
				MutableRepositoryItem items = (MutableRepositoryItem) map.get(currSite);
				if (map != null && !map.isEmpty()) {
					reqvo.setIpAddress("123.123.123.123");
					reqvo.setSiteId("BuyBuyBaby");
					reqvo.setToken((String)items.getPropertyValue(BBBCoreConstants.TOKEN));
					status = true;
				}
				res = webservice.getResponseVO(reqvo);
				assertNotNull(res);
				reqvo.setToken("asdfasdfasdf");
				res = webservice.getResponseVO(reqvo);
				reqvo.setToken(null);
				res = webservice.getResponseVO(reqvo);
				webservice.getTimeOutMinutes();
				reqvo.getSource();
				reqvo.setSource(null);
				reqvo.getRequestType();
				reqvo.setRequestType(0);
				res.isAuthorzied();
				res.getProfileBasicVO();
				res.getMessage();
				res.getResponseType();
			} else {
				assertNotNull(req.getObjectParameter("fail"));
			}
		}
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}
		bbbProfileFormHandler.getErrorMap().clear();
		bbbProfileFormHandler.getFormExceptions().clear();
		bbbProfileFormHandler.setFormErrorVal(false);
		
		//manager.removeOrder(bbbProfileFormHandler.getOrder().getId());
		
	}

}
