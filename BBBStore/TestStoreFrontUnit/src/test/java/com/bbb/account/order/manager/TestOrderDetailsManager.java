/**
 * 
 */
package com.bbb.account.order.manager;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;

import atg.core.util.Address;

import com.bbb.commerce.cart.bean.CommerceItemDisplayVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.common.BBBOrderVO;
import com.bbb.ecommerce.order.BBBHardGoodShippingGroup;
import com.bbb.ecommerce.order.BBBOrderImpl;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BaseTestCase;

/**
 * @author pku104
 * 
 */
public class TestOrderDetailsManager extends BaseTestCase {
	@Spy
	OrderDetailsManager spyOrderDetailsManager = new OrderDetailsManager();
	@Spy
	CommerceItemDisplayVO spyCommerceItemDisplayVO = new CommerceItemDisplayVO();
	@Mock
	BBBOrderImpl mockBBBOrderImpl;
	@Mock
	BBBOrderVO mockBBBOrderVO;
	@Mock
	BBBHardGoodShippingGroup mockBBBHardGoodShippingGroup;
	@Mock
	BBBCatalogTools mockBBBCatalogTools;
	Address mockAddress = new Address();
	List<BBBHardGoodShippingGroup> mockList = new ArrayList<BBBHardGoodShippingGroup>();

	@Override
	public void setUp() {
		super.setUp();
		spyOrderDetailsManager.setCatalogTools(mockBBBCatalogTools);
		mockAddress.setFirstName("Test");
		mockAddress.setLastName("User");
		mockAddress.setAddress1("Fulton Street");
		mockAddress.setState("New York");
		mockList.add(mockBBBHardGoodShippingGroup);
	}

	@Test
	public void testSetItemLevelExpectedDeliveryDate()
			throws BBBBusinessException, BBBSystemException {
		final boolean isVdcSku = false;
		final String skuId = "106192";
		final boolean fromAtgOrderDetails = true;
		final String orderDate = "5/7/2015";
		final String shippingMethod = "Standard";
		doReturn(mockList).when(mockBBBOrderImpl).getShippingGroups();
		doReturn(mockAddress).when(mockBBBHardGoodShippingGroup)
				.getShippingAddress();
		doReturn(orderDate).when(mockBBBOrderVO).getSubmittedDate();
		doReturn(shippingMethod).when(mockBBBHardGoodShippingGroup)
				.getShippingMethod();
		doReturn("5/10-5/15").when(mockBBBCatalogTools).getExpectedDeliveryDate(
				anyString(), anyString(), anyString(), (Date) anyObject());

		verify(mockBBBHardGoodShippingGroup, times(0)).getShippingAddress();
		verify(mockBBBOrderImpl, times(0)).getShippingGroups();
		verify(mockBBBOrderVO, times(0)).getSubmittedDate();
		verify(mockBBBCatalogTools, times(0)).getExpectedDeliveryDate(
				anyString(), anyString(), anyString(), (Date) anyObject());
		spyOrderDetailsManager.setItemLevelExpectedDeliveryDate(
				spyCommerceItemDisplayVO, mockBBBOrderVO,
				isVdcSku, skuId, fromAtgOrderDetails, shippingMethod, new Date(), mockBBBHardGoodShippingGroup);
	}

	@Test
	public void testSetItemLevelExpectedDeliveryDateForVDC()
			throws BBBBusinessException, BBBSystemException {
		final boolean isVdcSku = true;
		final String skuId = "106192";
		final boolean fromAtgOrderDetails = true;
		final String orderDate = "5/7/2015";
		final String shippingMethod = "Standard";
		doReturn(mockList).when(mockBBBOrderImpl).getShippingGroups();
		doReturn(mockAddress).when(mockBBBHardGoodShippingGroup)
				.getShippingAddress();
		doReturn(orderDate).when(mockBBBOrderVO).getSubmittedDate();
		doReturn(shippingMethod).when(mockBBBHardGoodShippingGroup)
				.getShippingMethod();
		doReturn("5/10-5/15").when(mockBBBCatalogTools).getExpectedDeliveryDate(
				anyString(), anyString(), anyString(), (Date) anyObject());

		verify(mockBBBHardGoodShippingGroup, times(0)).getShippingAddress();
		verify(mockBBBOrderImpl, times(0)).getShippingGroups();
		verify(mockBBBOrderVO, times(0)).getSubmittedDate();
		verify(mockBBBCatalogTools, times(0)).getExpectedDeliveryDate(anyString(), anyString(), anyString(), (Date) anyObject());
		spyOrderDetailsManager.setItemLevelExpectedDeliveryDate(spyCommerceItemDisplayVO, mockBBBOrderVO,
				isVdcSku, skuId, fromAtgOrderDetails, shippingMethod, new Date(), mockBBBHardGoodShippingGroup);
	}

	@Test
	public void testSetItemLevelExpectedDeliveryDateForShipOnDate()
			throws BBBBusinessException, BBBSystemException {
		final boolean isVdcSku = true;
		final String skuId = "106192";
		final boolean fromAtgOrderDetails = true;
		final String orderDate = "5/7/2015";
		final String shippingMethod = "Standard";
		doReturn(mockList).when(mockBBBOrderImpl).getShippingGroups();
		doReturn(mockAddress).when(mockBBBHardGoodShippingGroup)
				.getShippingAddress();
		doReturn(orderDate).when(mockBBBOrderVO).getSubmittedDate();
		doReturn(shippingMethod).when(mockBBBHardGoodShippingGroup)
				.getShippingMethod();
		doReturn(new Date()).when(mockBBBHardGoodShippingGroup).getShipOnDate();
		doReturn("5/10-5/15").when(mockBBBCatalogTools).getExpectedDeliveryDate(
				anyString(), anyString(), anyString(), (Date) anyObject());

		verify(mockBBBHardGoodShippingGroup, times(0)).getShippingAddress();
		verify(mockBBBOrderImpl, times(0)).getShippingGroups();
		verify(mockBBBOrderVO, times(0)).getSubmittedDate();
		verify(mockBBBCatalogTools, times(0)).getExpectedDeliveryDate(
				anyString(), anyString(), anyString(), (Date) anyObject());
		spyOrderDetailsManager.setItemLevelExpectedDeliveryDate(
				spyCommerceItemDisplayVO, mockBBBOrderVO,
				isVdcSku, skuId, fromAtgOrderDetails, "STANDARD", new Date(), mockBBBHardGoodShippingGroup);
	}

	@Test
	public void testSetItemLevelExpectedDeliveryDateForShipOnDateNonVDC()
			throws BBBBusinessException, BBBSystemException {
		final boolean isVdcSku = false;
		final String skuId = "106192";
		final boolean fromAtgOrderDetails = true;
		final String orderDate = "InValidDate";
		final String shippingMethod = "Standard";
		doReturn(mockList).when(mockBBBOrderImpl).getShippingGroups();
		doReturn(mockAddress).when(mockBBBHardGoodShippingGroup)
				.getShippingAddress();
		doReturn(orderDate).when(mockBBBOrderVO).getSubmittedDate();
		doReturn(shippingMethod).when(mockBBBHardGoodShippingGroup)
				.getShippingMethod();
		doReturn(new Date()).when(mockBBBHardGoodShippingGroup).getShipOnDate();
		doReturn("5/10-5/15").when(mockBBBCatalogTools).getExpectedDeliveryDate(
				anyString(), anyString(), anyString(), (Date) anyObject());

		verify(mockBBBHardGoodShippingGroup, times(0)).getShippingAddress();
		verify(mockBBBOrderImpl, times(0)).getShippingGroups();
		verify(mockBBBOrderVO, times(0)).getSubmittedDate();
		verify(mockBBBCatalogTools, times(0)).getExpectedDeliveryDate(
				anyString(), anyString(), anyString(), (Date) anyObject());
		spyOrderDetailsManager.setItemLevelExpectedDeliveryDate(
				spyCommerceItemDisplayVO, mockBBBOrderVO,
				isVdcSku, skuId, fromAtgOrderDetails, "STANDARD", new Date(), mockBBBHardGoodShippingGroup);
	}

}
