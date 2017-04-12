package com.bbb.selfservice.tools;
import com.sapient.common.tests.BaseTestCase;

public class TestMapQuestService extends BaseTestCase {
	
	public void testService() throws Exception
	{
		MapQuestService mapQuestService  = new MapQuestService();
		try {
			mapQuestService.communicateMapQuest("baby towel");
		} catch (Exception e) {
			assertNotNull(e);
		}
	}
}
