package com.bbb.framework.httpquery.vo;

import java.util.Map;

public interface HTTPServiceRequestIF {
	
	String getServiceName();
	Map<String,String> getParamsValuesMap();
	String getSiteId();
	String getServiceType();
}
