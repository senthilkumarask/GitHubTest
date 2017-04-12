package com.bbb.commerce.order.purchase;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.BaseTestCase;
import com.bbb.order.bean.BBBCommerceItem;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.CommonConfiguration;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.SimpleOrderManager;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
public class TestBBBCartFormHandler extends BaseTestCase {
	@Spy BBBCartFormhandler cartFormhandler = new BBBCartFormhandler();
	@Mock GiftRegistryManager giftRegistryManager;
	@Mock BBBSessionBean mockSessionBean;
	@Mock DynamoHttpServletRequest dynamoHttpServletRequest;
	@Mock DynamoHttpServletResponse dynamoHttpServletResponse;
	@Mock RegistrySummaryVO registrySummaryVO;
	@Mock CommonConfiguration commonConfiguration;
	@Mock SiteContextManager siteContextManager;
	@Mock BBBCatalogTools catalogTools;
	@Mock AddressVO addressVO;
	@Mock BBBOrderImpl order;
	@Mock Map<String, RegistrySummaryVO> registryMap = new HashMap<String, RegistrySummaryVO>();
	@Mock BBBCommerceItem commerceItem = new BBBCommerceItem(); 
	@Mock BBBCommerceItem bbbCommerceItem;
	@Mock SimpleOrderManager orderManager;
	
	@Override
	public void setUp(){
		super.setUp();
		registrySummaryVO.setRegistryId("52024793");
		mockSessionBean.setBuyoffStartBrowsingSummaryVO(registrySummaryVO);
		registrySummaryVO.setShippingAddress(addressVO);
		cartFormhandler.setCommonConfiguration(commonConfiguration);
		commonConfiguration.setLoggingDebugForRequestScopedComponents(true);
		cartFormhandler.setCatalogUtil(catalogTools);
		order.setRegistryMap(registryMap);
		//commerceItem.add(bbbCommerceItem);
		cartFormhandler.setRegistryManager(giftRegistryManager);
	}
	
	@Test
	public void testAssociateRegistryContextWithCart() throws BBBSystemException, ServletException, IOException, BBBBusinessException, CommerceItemNotFoundException, InvalidParameterException  {		
		doReturn("16746231").when(cartFormhandler).getBuyOffAssociationSkuId();
		doReturn(registrySummaryVO).when(mockSessionBean).getBuyoffStartBrowsingSummaryVO();		
		doReturn("52024793").when(registrySummaryVO).getRegistryId();
		//String pSiteId = "BedBathUS";
		when(cartFormhandler.getCatalogUtil().getDefaultCountryForSite(anyString())).thenReturn("usa");
		doReturn(mockSessionBean).when(dynamoHttpServletRequest).resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		doReturn(addressVO).when(registrySummaryVO).getShippingAddress();
		doReturn(order).when(cartFormhandler).getOrder();
		when(order.getCommerceItem(anyString())).thenReturn(commerceItem);
		when(cartFormhandler.getRegistryManager().getRegistryInfo(anyString(), anyString())).thenReturn(registrySummaryVO);
		//when(commerceItem.get(0)).thenReturn(bbbCommerceItem);
		doReturn(orderManager).when(cartFormhandler).getOrderManager();
		assertEquals(true,cartFormhandler.handleAssociateRegistryContextWithCart(dynamoHttpServletRequest, dynamoHttpServletResponse));			
		//scartFormhandler.handleAssociateRegistryContextWithCart(dynamoHttpServletRequest, dynamoHttpServletResponse);		
	}
}
