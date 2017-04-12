package com.bbb.commerce.giftregistry.formhandler;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.BaseTestCase;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
public class TestGiftRegistryFormHandler extends BaseTestCase {
	@Spy GiftRegistryFormHandler giftRegistryFormHandler = new GiftRegistryFormHandler();
	@Mock GiftRegistryManager giftRegistryManager;
	@Mock BBBSessionBean mockSessionBean;
	@Mock DynamoHttpServletRequest dynamoHttpServletRequest;
	@Mock DynamoHttpServletResponse dynamoHttpServletResponse;
	
	@Override
	public void setUp(){
		super.setUp();
	}
	
	@Test
	public void testBuyOffStartBrowsing() throws BBBSystemException, BBBBusinessException, ServletException, IOException {
		giftRegistryFormHandler.setGiftRegistryManager(giftRegistryManager);
		doReturn(mockSessionBean).when(dynamoHttpServletRequest).resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		doReturn("BedBathUS").when(giftRegistryFormHandler).getCurrentSiteId();
		doReturn("12345").when(giftRegistryFormHandler).getRegistryId();
		doReturn("pageContext/page/College").when(giftRegistryFormHandler).getBuyoffStartBrowsingSuccessURL();
		doReturn("http://bedbathandbeyond.com:7003/store").when(giftRegistryFormHandler).getErrorURL();
		giftRegistryFormHandler.handleBuyOffStartBrowsing(dynamoHttpServletRequest, dynamoHttpServletResponse);
		verify(giftRegistryFormHandler,times(1)).handleBuyOffStartBrowsing(dynamoHttpServletRequest, dynamoHttpServletResponse);
	
	}
}
