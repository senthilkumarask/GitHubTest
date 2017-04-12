package com.bbb.rest.stofu.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.heartbeat.BBBGSHeartbeatTools;
import com.bbb.rest.stofu.vo.HeartbeatMonitoringVO;

public class HeartbeatMonitoringDroplet extends BBBDynamoServlet {

	private final String STOREID = "storeId";
	private final String APPID = "appId";
	private final String HEALTH = "health";
	private BBBGSHeartbeatTools heartbeatTools;

	public BBBGSHeartbeatTools getHeartbeatTools() {
		return heartbeatTools;
	}

	public void setHeartbeatTools(BBBGSHeartbeatTools heartbeatTools) {
		this.heartbeatTools = heartbeatTools;
	}

	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		logDebug("HeartbeatMonitoringDroplet Class service method started");
		String filterStoreId = pRequest.getParameter(STOREID);
		String filterAppId = pRequest.getParameter(APPID);
		String filterHealth = pRequest.getParameter(HEALTH);
		List<HeartbeatMonitoringVO> listHeartbeatMonitoringVO;
		try {
			listHeartbeatMonitoringVO = this.getHeartbeatTools()
					.fetchHeartbeatMonitoringData(filterStoreId, filterAppId,
							filterHealth);
			pRequest.setParameter("listHeartbeatMonitoringVO",
					listHeartbeatMonitoringVO);
			pRequest.serviceParameter("output", pRequest, pResponse);
			logDebug("HeartbeatMonitoringDroplet Class service method exited");
		} catch (BBBSystemException e) {
			logError("Error while fetching config key" + e);
			e.printStackTrace();
		} catch (BBBBusinessException e) {
			logError("Error while fetching config key" + e);
		}

	}
}
