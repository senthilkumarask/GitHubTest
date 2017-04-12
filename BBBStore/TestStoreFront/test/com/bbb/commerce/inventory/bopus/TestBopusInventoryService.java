package com.bbb.commerce.inventory.bopus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.inventory.BopusInventoryService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestBopusInventoryService extends BaseTestCase {

	public void testGetInventoryForBopusItem() throws BBBBusinessException, BBBSystemException {
		BopusInventoryService bopusService = (BopusInventoryService) getObject("bopusService");

		List<String> storeids = new ArrayList<String>();

		storeids.add((String) getObject("storeID"));

		Map<String, Integer> inventories = bopusService.getInventoryForBopusItem(
				(String) getObject("skuID"), storeids, true);

		assertNotNull(inventories);

	}

}
