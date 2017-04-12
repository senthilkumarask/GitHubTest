package com.bbb.account;

import java.util.Iterator;

import atg.core.util.StringUtils;
import atg.droplet.DropletException;

import com.bbb.account.vo.reclaim.ForgetPasswordResponseVO;
import com.bbb.account.vo.reclaim.GetAccountInfoResponseVO;
import com.bbb.account.vo.reclaim.ReclaimAccountResponseVO;
import com.bbb.account.vo.reclaim.VerifyAccountResponseVO;
import com.bbb.framework.webservices.vo.ValidationError;
import com.bbb.utils.BBBUtility;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBReclaimLegacyAccount extends BaseTestCase{
	
	/*public void testVerifyAccount() throws Exception {
		
		VerifyAccountResponseVO responseVO = new VerifyAccountResponseVO();
		BBBProfileManager manager = (BBBProfileManager) getObject("bbbProfileManager");
		String pEmailId = (String) getObject("emailId");
		String pSiteId = (String) getObject("siteId");
		String pPassword = (String) getObject("password");
		String invalidEmailId = (String) getObject("invalidEmailId");
		String invalidSiteId = (String) getObject("invalidSiteId");
		
		//Case when site Id is valid but email is invalid
		responseVO = manager.verifyAccountResponseVO(pEmailId, pSiteId);
		testVerifyAccountDetails(responseVO,pEmailId, pSiteId, pPassword);
		
		//Case when site Id is valid but email is invalid
		responseVO = manager.verifyAccountResponseVO(invalidEmailId, pSiteId);
		testVerifyAccountDetails(responseVO,pEmailId, pSiteId, pPassword);
		
		//Case when site Id is valid but email is not associated 
		//responseVO = manager.verifyAccountResponseVO(pEmailId, invalidSiteId);
		//testVerifyAccountDetails(responseVO,pEmailId, pSiteId, pPassword);
		
		//Case when siteid and the emailid is invalid
		//responseVO = manager.verifyAccountResponseVO(invalidEmailId, invalidSiteId);
		//testVerifyAccountDetails(responseVO,pEmailId, pSiteId, pPassword);
	
	}
	
	*/
	
	public void testVerifyAccountDetails(VerifyAccountResponseVO pResponseVO, String pEmailId, String pSiteId, String pPassword) throws Exception 	{
		
		assertFalse(pResponseVO == null);
		if(pResponseVO.getErrorStatus().isErrorExists()) { 
			//If error received from webservice	
			assertTrue(!StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage())
					|| !StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()) 
					||!pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
		} else {
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage()));
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()));
			assertTrue(pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
		}
		testGetAccountInfo(pEmailId, pSiteId, pPassword);
	}
	
	
	public void testGetAccountInfo(String pEmailId, String pSiteId, String pPassword) throws Exception {
		
		GetAccountInfoResponseVO responseVO = new GetAccountInfoResponseVO();
		BBBProfileManager manager = (BBBProfileManager) getObject("bbbProfileManager");
		
		responseVO = manager.getAccountInfoResponseVO(pEmailId, pPassword, pSiteId);//Case when site Id is valid but email is invalid
		testGetAccountInfo(responseVO, pEmailId, pPassword, pSiteId);
	
	}
	
	public void testGetAccountInfo(GetAccountInfoResponseVO pResponseVO, String pEmailId, String pPassword, String pSiteId) throws Exception 	{
		
		assertFalse(pResponseVO == null);
		if(pResponseVO.getErrorStatus().isErrorExists()) {
			//If error received from webservice
			assertTrue(!StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage())
					|| !StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()) 
					||!pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
			if(!StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage())
					|| !StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage())){
				testForgetPassword(pEmailId,pSiteId);
			
			} else if(!StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage())){
				Iterator<ValidationError> valErrorIterator = pResponseVO.getErrorStatus().getValidationErrors().iterator();
				while(valErrorIterator.hasNext()) {
					ValidationError valError = valErrorIterator.next();
					if(!BBBUtility.isEmpty(valError.getKey()) && !BBBUtility.isEmpty(valError.getValue())) {
						if(valError.getKey().toLowerCase().contains("password")) {
							//call fp ws	
							testForgetPassword(pEmailId,pSiteId);
						}
					}
				}
			}
		} else {
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage()));
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()));
			assertTrue(pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
			BBBProfileFormHandler profileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
			profileFormHandler.handleCreate(getRequest(), getResponse());
			testReclaimAccount(pEmailId, pSiteId, pResponseVO.getMemberToken());
		}
		testForgetPassword(pEmailId,pSiteId);
		BBBProfileFormHandler profileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		profileFormHandler.handleCreate(getRequest(), getResponse());
		testReclaimAccount(pEmailId, pSiteId, pResponseVO.getMemberToken());
	}	
	
	public void testReclaimAccount(String pProfileId, String pSiteId, String pMemberToken) throws Exception {
		
		ReclaimAccountResponseVO responseVO = new ReclaimAccountResponseVO();
		BBBProfileManager manager = (BBBProfileManager) getObject("bbbProfileManager");
		
		responseVO = manager.reclaimAccountResponseVO(pProfileId, pSiteId, pMemberToken);//Case when site Id is valid but email is invalid
		testReclaimAccountDetails(responseVO);
			
	}
	
	public void testReclaimAccountDetails(ReclaimAccountResponseVO pResponseVO) throws Exception 	{
		
		assertFalse(pResponseVO == null);
		if(pResponseVO.getErrorStatus().isErrorExists()) { 
			//If error received from webservice
			assertTrue(!StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage())
					|| !StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()) 
					||!pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
		} else {
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage()));
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()));
			assertTrue(pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
		}
	}
	
	public void testForgetPassword(String pEmailId, String pSiteId) throws Exception {
		
		ForgetPasswordResponseVO responseVO = new ForgetPasswordResponseVO();
		BBBProfileManager manager = (BBBProfileManager) getObject("bbbProfileManager");
		
		responseVO = manager.forgetPasswordResponseVO(pEmailId, pSiteId);//Case when site Id is valid but email is invalid
		testForgetPasswordDetails(responseVO);
			
	}
	
	public void testForgetPasswordDetails(ForgetPasswordResponseVO pResponseVO) throws Exception 	{
		
		assertFalse(pResponseVO == null);
		if(pResponseVO.getErrorStatus().isErrorExists()) { 
			//If error received from webservice
			assertTrue(!StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage())
					|| !StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()) 
					||!pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
		} else {
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getDisplayMessage()));
			assertTrue(StringUtils.isEmpty(pResponseVO.getErrorStatus().getErrorMessage()));
			assertTrue(pResponseVO.getErrorStatus().getValidationErrors().isEmpty());
		}
	}	
	
	/*public void WStestHandleCreate() throws Exception {
		
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		String pEmailId = (String) getObject("emailId");
		String pSiteId = (String) getObject("siteId");
		String pPassword = (String) getObject("password");
		String pConfirmPassword = (String) getObject("password");
		String pFirstName = (String) getObject("firstName");
		String pLastName = (String) getObject("lastName");
		String pMemberId = (String) getObject("memberId");
		
		bbbProfileFormHandler.getValue().put("login", pEmailId);
		bbbProfileFormHandler.getValue().put("password", pPassword);
		bbbProfileFormHandler.getValue().put("siteId", pSiteId);
		bbbProfileFormHandler.getValue().put("confirmPassword", pConfirmPassword);
		bbbProfileFormHandler.getValue().put("firstName", pFirstName);
		bbbProfileFormHandler.getValue().put("lastName", pLastName);
		bbbProfileFormHandler.getValue().put("memberId", pMemberId);
		
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());

		boolean isCreate = bbbProfileFormHandler.handleCreate(getRequest(), getResponse());
		
		
	
	}*/
}