package com.bbb.ecommerce.order;

import java.util.Map;

public interface BBBShippingGroup {
	
	public abstract Map<String, String> getEcoFeeItemMap();

	public abstract void setEcoFeeItemMap(Map<String, String> pEcoFeeItemMap);
}
