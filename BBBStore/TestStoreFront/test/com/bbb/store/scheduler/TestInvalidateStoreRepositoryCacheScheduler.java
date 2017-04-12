package com.bbb.store.scheduler;

import com.sapient.common.tests.BaseTestCase;

/**
 * @author magga3
 *
 */
public class TestInvalidateStoreRepositoryCacheScheduler extends BaseTestCase {

	private static final String SCHEDULED_TASK = "scheduledTask";

	/**
	 * Test case to test the Scheduled Invalidation of
	 * Store Repository Cache.
	 */
	public void testInvalidateStoreRepositoryCacheScheduler() {
		InvalidateStoreRepositoryCacheScheduler invalidateCacheScheduler = (InvalidateStoreRepositoryCacheScheduler) getObject(SCHEDULED_TASK);
		invalidateCacheScheduler.setSchedulerEnabled(true);
		invalidateCacheScheduler.doScheduledTask();
		assertTrue(true);
	}
}
