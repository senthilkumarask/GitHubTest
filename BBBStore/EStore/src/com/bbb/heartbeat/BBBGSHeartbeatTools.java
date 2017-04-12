package com.bbb.heartbeat;

import java.util.List;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.rest.stofu.vo.HeartbeatMonitoringVO;

public interface BBBGSHeartbeatTools {
	public List<HeartbeatMonitoringVO> fetchHeartbeatMonitoringData(String filterStoreId, String filterAppId, String filterHealth) throws BBBSystemException, BBBBusinessException;
}
