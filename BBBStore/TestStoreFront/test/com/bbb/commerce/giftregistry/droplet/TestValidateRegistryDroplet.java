package com.bbb.commerce.giftregistry.droplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.profile.session.BBBSessionBean;
import com.sapient.common.tests.BaseTestCase;

public class TestValidateRegistryDroplet extends BaseTestCase {
	public void testService() throws Exception {

		ValidateRegistryDroplet validateRegistryDroplet = (ValidateRegistryDroplet) getObject("validateRegistryDroplet");
		String validRegistryId = (String) getObject("validRegistryId");
		String invalidRegistryId = (String) getObject("invalidRegistryId");

		List<String> pListUserRegIds = new ArrayList<String>();
		pListUserRegIds.add("400002");
		pListUserRegIds.add("400003");
		pListUserRegIds.add("400004");
		
		getRequest().setParameter(BBBGiftRegistryConstants.REGISTRY_ID,	validRegistryId);
		
		BBBSessionBean sessionBean = (BBBSessionBean) getRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
		final HashMap sessionMap = sessionBean.getValues();
		sessionMap.put(BBBGiftRegistryConstants.USER_REGISTRIES_LIST, pListUserRegIds);
		
		validateRegistryDroplet.service(getRequest(), getResponse());
		
		getRequest().setParameter(BBBGiftRegistryConstants.REGISTRY_ID,	invalidRegistryId);
		validateRegistryDroplet.service(getRequest(), getResponse());
	}
}
