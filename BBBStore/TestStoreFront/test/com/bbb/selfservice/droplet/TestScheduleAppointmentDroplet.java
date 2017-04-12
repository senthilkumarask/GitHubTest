package com.bbb.selfservice.droplet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.cms.RecommendationLandingPageTemplateVO;
import com.bbb.commerce.catalog.vo.AppointmentVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.selfservice.common.SelfServiceConstants;
import com.bbb.selfservice.common.StoreDetailsWrapper;
import com.sapient.common.tests.BaseTestCase;

public class TestScheduleAppointmentDroplet extends BaseTestCase{

	public void testScheduleAppointment() throws Exception {

		DynamoHttpServletRequest pRequest = getRequest();
		DynamoHttpServletResponse pResponse = getResponse();
		
		ScheduleAppointmentDroplet scheduleDroplet = (ScheduleAppointmentDroplet) getObject("scheduleAppointment");
		pRequest.setParameter("siteId", (String) getObject("siteId"));
		pRequest.setParameter("favouriteStoreId", (String) getObject("favouriteStoreId"));
		pRequest.setParameter("appointmentType", (String) getObject("appointmentType"));
		scheduleDroplet.service(pRequest, pResponse);
		List<AppointmentVO> appointmentList = (List<AppointmentVO>)pRequest.getObjectParameter("appointmentList");
		
		System.out.println(appointmentList.toString());
    	assertNotNull(appointmentList);

}
}
