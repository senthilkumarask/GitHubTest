package com.bbb.scheduler;

import com.sapient.common.tests.BaseTestCase;

public class TestBBBPurgingScheduler extends BaseTestCase{
	
	public void testPurgeProcess() throws Exception {
		BBBPurgingScheduler purgeScheduler = (BBBPurgingScheduler)getObject("bbbpurgingscheduler");
		purgeScheduler.setLoggingDebug(true);
		purgeScheduler.setLoggingTrace(true);
		purgeScheduler.setLoggingInfo(true);
		
		if(purgeScheduler.isSchedulerEnabled())
			assertTrue(purgeScheduler.processPurge());
	}
}
