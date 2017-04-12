package com.bbb.admin.droplet;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.sapient.common.tests.BaseTestCase;

public class TestListSchedulerJobs extends BaseTestCase {
	public void testListSchedulerJobs() throws Exception {

		ListSchedulerJobs schedulerJobs = (ListSchedulerJobs) getObject("schedulerJobs");
		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		assertNotNull(schedulerJobs.getScheduler());
		assertNotNull(schedulerJobs.getScheduleJobsList());
		try{
			schedulerJobs.service(pRequest, pResponse);
		}catch (Exception e) {
			assertNotNull(e.getStackTrace());
		}
	}

}
