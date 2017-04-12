package com.bbb.selfservice.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogToolsImpl;
import com.bbb.exception.BBBSystemException;
import com.bbb.selfservice.manager.ScheduleAppointmentManager;
import com.bbb.common.BBBDynamoServlet;


public class ScheduleAppointmentDroplet extends BBBDynamoServlet{

	private ScheduleAppointmentManager mScheduleAppointmentManager;
	private BBBCatalogToolsImpl mCatalogTools;
	
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws ServletException, IOException {
		long startTime = System.currentTimeMillis();
		String favouriteStoreId = (String) request.getLocalParameter("favouriteStoreId");
		String appointmentType = (String) request.getLocalParameter("appointmentType");
		String siteId = (String) request.getLocalParameter("siteId");
		boolean isScheduleAppointment = false;
		boolean mapFavStoreAppointment = false;
		boolean errorOnModal =false;
		isScheduleAppointment = isScheduleAppointment(siteId);
			if(isScheduleAppointment){				
				if(isFavouriteStoreIdValid(favouriteStoreId,appointmentType) ){	
					try {
						mapFavStoreAppointment = this.getScheduleAppointmentManager().isApponitmentTypeValidForStore(favouriteStoreId,appointmentType);
					} catch (BBBSystemException e) {
						//catch and ignore
						logError(e);
					}
					if(!mapFavStoreAppointment){
						errorOnModal = true;
					}					
				}			
			}
			request.setParameter("isScheduleAppointment", isScheduleAppointment);	
			request.setParameter("errorOnModal", errorOnModal);	
			request.setParameter("directSkedgeMe", mapFavStoreAppointment);			
			
			request.serviceParameter("output", request, response);
			long endTime = System.currentTimeMillis();
		logDebug("[ScheduleAppointmentDroplet] - Total Time taken : " + (endTime - startTime) + "ms");
	}
	public boolean isScheduleAppointment(String siteId){
		
		return this.getScheduleAppointmentManager().canScheduleAppointmentForSiteId(siteId);
	}
	
	public boolean isFavouriteStoreIdValid(String favouriteStoreId,String appointmentType){
		
		return  ((favouriteStoreId!= null) && !StringUtils.isEmpty(favouriteStoreId) && (appointmentType!= null) && !StringUtils.isEmpty(appointmentType)) ;
	}
	
	public boolean isAppointmentValid(String appointmentType){
		
		return ((appointmentType!= null) && !StringUtils.isEmpty(appointmentType));
	}
	
	public ScheduleAppointmentManager getScheduleAppointmentManager() {
		return mScheduleAppointmentManager;
	}
	public void setScheduleAppointmentManager(
			ScheduleAppointmentManager pScheduleAppointmentManager) {
		mScheduleAppointmentManager = pScheduleAppointmentManager;
	}
	public BBBCatalogToolsImpl getCatalogTools() {
		return mCatalogTools;
	}
	public void setCatalogTools(BBBCatalogToolsImpl pCatalogTools) {
		mCatalogTools = pCatalogTools;
	}
	
	/*public boolean isScheduleAppointmentEnabled(){
		List<String> scheduleAppointment=null;
		Boolean isScheduleAppointment=false;
		try {
			scheduleAppointment = getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS,
					BBBCmsConstants.APPOINTMENT_SCHEDULER_KEY);
			if(null!=scheduleAppointment && !scheduleAppointment.isEmpty()){
				isScheduleAppointment=Boolean.valueOf(scheduleAppointment.get(0));
				logDebug(" is Schedule Appointment on ? "+isScheduleAppointment);
				return isScheduleAppointment;
			}
		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of isScheduleAppointment : checkScheduleAppointment"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of isScheduleAppointment : checkScheduleAppointment"), e);

		}	
		return isScheduleAppointment;
	}*/
	
}
