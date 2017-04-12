package com.bbb.account;


import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.sapient.common.tests.BaseTestCase;

public class TestValidateOldAccount extends BaseTestCase
{
	

	public void testIsOldAccountValid() throws BBBBusinessException, BBBSystemException{
		String email = (String) getObject("email");
        String password = (String) getObject("password");
        String siteId = (String) getObject("siteId");
        BBBProfileManager manager = (BBBProfileManager) getObject("bbbProfileManager");
        boolean check = false;
        check = manager.isOldAccountValid(email, siteId, password);
        assertEquals(check, true);
    }
	
	public void testUpdatePassword() throws BBBBusinessException, BBBSystemException{
		String email = (String) getObject("email");
        String password = (String) getObject("password");
        BBBProfileManager manager = (BBBProfileManager) getObject("bbbProfileManager");
        boolean check = false;
        check = manager.updatePassword(email,  password);
        assertEquals(check, true);
    }
	
	public void testUpdateIsLoggedInProp() throws BBBBusinessException, BBBSystemException{
		String email = (String) getObject("email");
        BBBProfileManager manager = (BBBProfileManager) getObject("bbbProfileManager");
        boolean check = false;
        check = manager.updateIsLoggedInProp(email);
        assertEquals(check, true);
    }
}
