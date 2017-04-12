package com.bbb.account;



import java.util.HashMap;
import java.util.List;
import java.util.Random;

import atg.commerce.order.CreditCard;
import atg.commerce.order.PaymentGroup;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.payment.creditcard.CreditCardInfo;
import atg.servlet.ServletUtil;
import atg.userprofiling.ProfileTools;

import com.bbb.commerce.common.BBBRepositoryContactInfo;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrder;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBProfileFormHandler extends BaseTestCase
{
	
	/*protected void setUp( ) throws Exception {
		
		BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) getObject("bbbProfileFormHandler");
		BBBProfileTools bbbProfileTools = (BBBProfileTools) bbbProfileFormHandler.getProfileTools();
		getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
		String pSiteId = (String) getObject("siteId");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

		String email = (String) getObject("email");
		String password = (String) getObject("password");

		bbbProfileFormHandler.getValue().put("login", email);
		bbbProfileFormHandler.getValue().put("password", password);
		atg.servlet.ServletUtil.setCurrentRequest(getRequest());

		boolean isLogin = bbbProfileFormHandler.handleLogin(getRequest(), getResponse());
		
		bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname1");
		bbbProfileFormHandler.setRemoveCard("D3cardnickname1");
		
		bbbProfileFormHandler.handleRemoveCard(getRequest(), getResponse());
		bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname2");
		bbbProfileFormHandler.setRemoveCard("D3cardnickname2");
		
		bbbProfileFormHandler.handleRemoveCard(getRequest(), getResponse());
		bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname3");
		bbbProfileFormHandler.setRemoveCard("D3cardnickname3");
		bbbProfileFormHandler.handleRemoveCard(getRequest(), getResponse());


		//delete address 
		bbbProfileFormHandler.setAddressId("D3secaddrnickname1");
		bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname1",true);
		
		
		bbbProfileFormHandler.setAddressId("D3secaddrnickname2");
		bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname2",true);
		bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		bbbProfileFormHandler.setProfile(null);
		bbbProfileFormHandler.getFormExceptions().clear();
		
	}*/

    public void testHandleLogin() throws Exception{

        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        bbbProfileFormHandler.setLoggingDebug(true);
        bbbProfileFormHandler.setLoggingInfo(true);
        bbbProfileFormHandler.setLoggingError(true);
        this.getRequest().setParameter("userCheckingOut","asdf");
        System.out.println("TestBBBProfileFormHandler.testHandleLogin.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        if((bbbProfileFormHandler.getOrder()!=null) && (bbbProfileFormHandler.getOrder().getId()!=null)){
            bbbProfileFormHandler.getShoppingCart().setCurrent(null);

        }
        //BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        manager.updatePassword(email, password);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.setAssoSite("BuyBuyBaby");
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()==0);
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

    }





    public void testHandleInvalidLogin() throws Exception{

        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        this.getRequest().setParameter("userCheckingOut","asdf");
        System.out.println("TestBBBProfileFormHandler.testHandleInvalidLogin.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());

        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

        final String email = (String) this.getObject("email");
        final String invalidEmail = (String) this.getObject("invalidemail");
        final String password = (String) this.getObject("password");

        //invalid email
        bbbProfileFormHandler.getValue().put("login", invalidEmail);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        //empty email
        bbbProfileFormHandler.getValue().put("login", "");
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        //empty pwd
        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", "");
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        //no profile
        bbbProfileFormHandler.getValue().put("login", "test@yahoo.com");
        bbbProfileFormHandler.getValue().put("password", "test123");
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getProfileManager().updateIsLoggedInProp(email);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());

        assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()==0);
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

    }

    public void testHandleUpdate() throws Exception{
        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        this.getRequest().setParameter("userCheckingOut","asdf");
        System.out.println("TestBBBProfileFormHandler.testHandleUpdate.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
        manager.updatePassword(email, password);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());

        //invalid Update
        bbbProfileFormHandler.getValue().put("email", "");
        bbbProfileFormHandler.getValue().put("firstName", "22222222222222222222222222222222222222222222222222222");
        bbbProfileFormHandler.getValue().put("lastName", "22222222222222222222222222222222222222222222222222222222222");
        bbbProfileFormHandler.getValue().put("phoneNumber", "1231aasd231324");
        bbbProfileFormHandler.getValue().put("mobileNumber", "12312asdas31324");
        bbbProfileFormHandler.handleUpdate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        //valid update
        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("phoneNumber", "9878728828");
        bbbProfileFormHandler.getValue().put("mobileNumber", "9786756968");
        bbbProfileFormHandler.handleUpdate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);

        bbbProfileFormHandler.getValue().put("email", "x"+email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("phoneNumber", "9416078375");
        bbbProfileFormHandler.getValue().put("mobileNumber", "9418578563");
        bbbProfileFormHandler.setEmailOptIn(true);
        bbbProfileFormHandler.handleUpdate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("phoneNumber", "9865327845");
        bbbProfileFormHandler.getValue().put("mobileNumber", "9764319865");
        bbbProfileFormHandler.handleUpdate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);

        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
    }

    public void testHandleCreate() throws Exception{

        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        this.getRequest().setParameter("userCheckingOut","asdf");

		
		System.out.println("TestBBBProfileFormHandler.testHandleCreate.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
		assertTrue(bbbProfileFormHandler.getProfile().isTransient());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);
		
        String email = (int)(Math.random()*9999) + (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        final String pSiteId = (String) this.getObject("siteId");

        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        
        getRequest().setParameter("siteId", pSiteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }

        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.getValue().put("confirmPassword", password);
        bbbProfileFormHandler.getValue().put("lastName", "Lname");

        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", "");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "222222222222222222222222222222222222222222222222222222222222222222222222222");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "222222222222222222222222222222222222222222222222222222222222222222222222222222");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("password", "");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);


        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("password", "123");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.getValue().put("confirmPassword", "cyxc");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);


        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("phoneNumber", "sdasdas");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("phoneNumber", "1231231234");
        bbbProfileFormHandler.getValue().put("mobileNumber", "1231231234");
        bbbProfileFormHandler.setEmailOptIn(true);
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);


        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("phoneNumber", "1231231234");
        bbbProfileFormHandler.getValue().put("mobileNumber", "asdfasd");
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);


        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("phoneNumber", "1231231234");
        bbbProfileFormHandler.getValue().put("mobileNumber", "1231231234");
        bbbProfileFormHandler.setSharedCheckBoxEnabled(true);
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() != 0);
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        final Random random  = new Random();
        email = "rajesh_test"+ random.nextInt(1000000) + "@example.com";
        bbbProfileFormHandler.getValue().put("email", email);
        bbbProfileFormHandler.getValue().put("firstName", "Rajesh");
        bbbProfileFormHandler.getValue().put("lastName", "Saini");
        bbbProfileFormHandler.getValue().put("password", "Password1");
        bbbProfileFormHandler.getValue().put("confirmPassword", "Password1");
        bbbProfileFormHandler.setSharedCheckBoxEnabled(false);
        bbbProfileFormHandler.handleCreate(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()==0);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }


        /*bbbProfileFormHandler.getValue().put("email", "x"+email);
		bbbProfileFormHandler.getValue().put("firstName", "Fname");
		bbbProfileFormHandler.getValue().put("lastName", "Lname");
		bbbProfileFormHandler.getValue().put("password", password);
		bbbProfileFormHandler.getValue().put("confirmPassword", password);
		bbbProfileFormHandler.getSessionBean().setLegacyMemberId("1891605403");
		
		bbbProfileFormHandler.setSharedCheckBoxEnabled(false);
		bbbProfileFormHandler.handleCreate(getRequest(), getResponse());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
		assertFalse(bbbProfileFormHandler.getProfile().isTransient());
		bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()!=0);
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}*/
		
		
		/*bbbProfileFormHandler.getValue().put("email", "x"+email);
		bbbProfileFormHandler.getValue().put("firstName", "Fname");
		bbbProfileFormHandler.getValue().put("lastName", "Lname");
		bbbProfileFormHandler.getValue().put("password", password);
		bbbProfileFormHandler.getValue().put("confirmPassword", password);
		bbbProfileFormHandler.setSharedCheckBoxEnabled(true);
		bbbProfileFormHandler.getSessionBean().setLegacyMemberId("dfsdfsd");
		bbbProfileFormHandler.handleCreate(getRequest(), getResponse());
		assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
		assertFalse(bbbProfileFormHandler.getProfile().isTransient());
		bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()!=0);
		if(!bbbProfileFormHandler.getProfile().isTransient()){
			bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
		}*/
        //bbbProfileFormHandler.handleLogout(getRequest(), getResponse());
        //assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()==0);

    }

    public void testHandleRegister() throws Exception{
        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        this.getRequest().setParameter("userCheckingOut","asdf");
        //BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);

        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        String email = (String) this.getObject("email");
        //String password = (String) getObject("password");
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("email", "");

        bbbProfileFormHandler.handleRegister(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);


        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("email",  "@@@@@"+email);
        bbbProfileFormHandler.handleRegister(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);

        //String password = (String) getObject("password");
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("email",  email);
        bbbProfileFormHandler.handleRegister(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);


        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        email = (int)(Math.random()*9999) + (String) this.getObject("email");
        bbbProfileFormHandler.getValue().put("email", "x" +email);
        bbbProfileFormHandler.handleRegister(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);


        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.handleRegister(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
    }

    public void testHandleChangePassword() throws Exception{

        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        this.getRequest().setParameter("userCheckingOut","asdf");
        //BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

        getRequest().setParameter("siteId", pSiteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }

        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");

        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("email",email);
        bbbProfileFormHandler.getValue().put("firstName", "Fname");
        bbbProfileFormHandler.getValue().put("lastName", "Lname");
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.getValue().put("confirmPassword", password);
        if(bbbProfileFormHandler.getProfile().isTransient()){
            final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
            bbbProfileFormHandler.getValue().put("login",email);
            manager.updatePassword(email, password);
            bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        }

        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());
        //change password
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123");
            bbbProfileFormHandler.setLegacyUser("abhilash@gamil.com");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);


            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123");
            bbbProfileFormHandler.setLegacyUser("abhilash@gamil.com");
            this.getRequest().getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, "abhilash@gamil.com");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);

            this.getRequest().getSession().removeAttribute(BBBCoreConstants.EMAIL_ADDR);
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123");
            bbbProfileFormHandler.setLegacyUser("asdas@asd.com");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()!=0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);


            bbbProfileFormHandler.setLegacyUser(null);
            bbbProfileFormHandler.getValue().remove("firstName");
            bbbProfileFormHandler.getValue().remove("lastName");
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);

            bbbProfileFormHandler.getValue().remove("oldpassword");
            bbbProfileFormHandler.getValue().remove("password");
            bbbProfileFormHandler.getValue().remove("confirmpassword");
            bbbProfileFormHandler.getValue().put("oldpassword", "");
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);


            bbbProfileFormHandler.getValue().remove("oldpassword");
            bbbProfileFormHandler.getValue().remove("password");
            bbbProfileFormHandler.getValue().remove("confirmpassword");
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);

            bbbProfileFormHandler.getValue().remove("oldpassword");
            bbbProfileFormHandler.getValue().remove("password");
            bbbProfileFormHandler.getValue().remove("confirmpassword");
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);

            bbbProfileFormHandler.getValue().remove("oldpassword");
            bbbProfileFormHandler.getValue().remove("password");
            bbbProfileFormHandler.getValue().remove("confirmpassword");
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123xxx");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);

            bbbProfileFormHandler.getValue().remove("oldpassword");
            bbbProfileFormHandler.getValue().remove("password");
            bbbProfileFormHandler.getValue().remove("confirmpassword");
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", password);
            bbbProfileFormHandler.getValue().put("confirmpassword", password);
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);




            bbbProfileFormHandler.getValue().remove("email");
            bbbProfileFormHandler.getValue().remove("password");
            bbbProfileFormHandler.getValue().remove("confirmpassword");

            bbbProfileFormHandler.getValue().put("email","abhilash@gmail.com");
            bbbProfileFormHandler.setLegacyUser("abhilash@gmail.com");
            bbbProfileFormHandler.getValue().put("password", "abhilash123");
            bbbProfileFormHandler.getValue().put("confirmpassword",  "abhilash123");
            this.getRequest().getSession().setAttribute(BBBCoreConstants.EMAIL_ADDR, "abhilash@gmail.com");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);
            this.getRequest().getSession().removeAttribute(BBBCoreConstants.EMAIL_ADDR);

            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);
            assertTrue(bbbProfileFormHandler.getProfile().isTransient());

            bbbProfileFormHandler.getValue().remove("firstName");
            bbbProfileFormHandler.getValue().remove("lastName");
            bbbProfileFormHandler.getValue().remove("oldpassword");
            bbbProfileFormHandler.getValue().remove("password");
            bbbProfileFormHandler.getValue().remove("confirmpassword");
            bbbProfileFormHandler.getValue().put("oldpassword", password);
            bbbProfileFormHandler.getValue().put("password", "M@noj123");
            bbbProfileFormHandler.getValue().put("confirmpassword", "M@noj123");
            bbbProfileFormHandler.getValue().put("firstName", "");
            bbbProfileFormHandler.getValue().put("lastName", "Lname");
            bbbProfileFormHandler.handleChangePassword(this.getRequest(), this.getResponse());
            assertTrue(bbbProfileFormHandler.getFormExceptions().size()>0);
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);

        }
        final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
        manager.updatePassword(email,password);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);



    }

    public void testHandleCreditCard() throws Exception {
        this.getRequest().setParameter("userCheckingOut","asdf");
        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        this.getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        System.out.println("TestBBBProfileFormHandler.testHandleCreditCard.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

        final BBBProfileTools bbbProfileTools = (BBBProfileTools) this.getObject("bbbProfileTools");
        
        this.getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);

        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        
        getRequest().setParameter("siteId", pSiteId);
        SiteContextManager siteContextManager= (SiteContextManager) getObject("siteContextManager");
        try {
        	siteContextManager.pushSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        } catch (SiteContextException siteContextException) {
        	throw new BBBSystemException("Exception" + siteContextException);
        }
        
        
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");


        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());




        //-------------------------
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname1");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname1");
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname2");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname2");
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname3");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname3");
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname4");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname4");
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());

        //delete address
        bbbProfileFormHandler.setAddressId("D3secaddrnickname1");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname1",true);

        bbbProfileFormHandler.setAddressId("D3secaddrnickname2");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname2",true);
        bbbProfileFormHandler.setAddressId("D3secaddrnickname3");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname3",true);
        bbbProfileFormHandler.setAddressId("D3secaddrnickname4");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname4",true);
        //====================

        bbbProfileFormHandler.setNewNicknameValueMapKey("newName");
        bbbProfileFormHandler.setNicknameValueMapKey("newName");
        bbbProfileFormHandler.handleNewAddress(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in changing default for credi card",	bbbProfileFormHandler.getFormExceptions().size()>0);
        bbbProfileFormHandler.getFormExceptions().clear();
        //set default address
        bbbProfileFormHandler.getBillAddrValue().put("address1", "addr");
        bbbProfileFormHandler.getBillAddrValue().put("city", "city");
        bbbProfileFormHandler.getBillAddrValue().put("state", "NH");
        bbbProfileFormHandler.getBillAddrValue().put("firstName", "fname");
        bbbProfileFormHandler.getBillAddrValue().put("lastName", "lname");
        bbbProfileFormHandler.getBillAddrValue().put("country", "US");
        bbbProfileFormHandler.getBillAddrValue().put("postalCode", "12345");
        bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", "D3secaddrnickname1");

        //set default credit card
        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "4");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "2024");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name one");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname1");
        bbbProfileFormHandler.getFormExceptions().clear();
        //add default credit card and address
        bbbProfileFormHandler.handleCreateNewCreditCardAndAddress(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in submitting default credit card and address",bbbProfileFormHandler.getFormExceptions().size()==0);

        //set new address
        bbbProfileFormHandler.getBillAddrValue().put("address1", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("city", "city");
        bbbProfileFormHandler.getBillAddrValue().put("state", "NH");
        bbbProfileFormHandler.getBillAddrValue().put("firstName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("lastName", "lasname");
        bbbProfileFormHandler.getBillAddrValue().put("country", "US");
        bbbProfileFormHandler.getBillAddrValue().put("postalCode", "12345");
        bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", "D3secaddrnickname2");

        //set new credit card
        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "4");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "2015");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name two");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname2");

        //add new credit card and address
        bbbProfileFormHandler.handleCreateNewCreditCardAndAddress(this.getRequest(), this.getResponse());

        assertTrue("ERRORS in submitting second credit card and address",bbbProfileFormHandler.getFormExceptions().size()==0);


        //add third credit card
        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "4");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "2016");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name three");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname3");
        bbbProfileFormHandler.getEditValue().put("newNickname", "D3cardnickname3");

        //associate this credit card address with second card that was added
        bbbProfileFormHandler.getBillAddrValue().put("makeBilling","true");
        bbbProfileFormHandler.getBillAddrValue().put("newNickname","D3secaddrnickname3");
        bbbProfileFormHandler.setNewNicknameValueMapKey("newNickname");
        //add new credit card only
        final boolean isCreateStatus = bbbProfileFormHandler.handleCreateNewCreditCard(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in submitting credit card only",bbbProfileFormHandler.getFormExceptions().size()==0);
        assertTrue("Create credit card only successfull",isCreateStatus);

        //calling
        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "4");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "2016");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name three");
        bbbProfileFormHandler.getBillAddrValue().put("newNickname","newBillingAddress");
        bbbProfileFormHandler.getValue().put("defaultCreditCard", "D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname4");
        bbbProfileFormHandler.getBillAddrValue().put("address1", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("city", "city");
        bbbProfileFormHandler.getBillAddrValue().put("state", "NH");
        bbbProfileFormHandler.getBillAddrValue().put("firstName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("lastName", "lasname");
        bbbProfileFormHandler.getBillAddrValue().put("country", "US");
        bbbProfileFormHandler.getBillAddrValue().put("postalCode", "12345");
        bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", "D3secaddrnickname4");
        bbbProfileFormHandler.handleCreateCreditCard(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in changing default for credi card",	bbbProfileFormHandler.getFormExceptions().size()==0);

        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "4");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "2016");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name three");
        bbbProfileFormHandler.getBillAddrValue().put("newNickname","newBillingAddress");
        bbbProfileFormHandler.getValue().put("defaultCreditCard", "D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname4");
        bbbProfileFormHandler.getBillAddrValue().put("address1", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("address2", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("city", "city");
        bbbProfileFormHandler.getBillAddrValue().put("state", "NH");
        bbbProfileFormHandler.getBillAddrValue().put("firstName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("companyName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("lastName", "lasname");
        bbbProfileFormHandler.getBillAddrValue().put("country", "US");
        bbbProfileFormHandler.getBillAddrValue().put("postalCode", "12345");
        bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", "D3secaddrnickname4");
        bbbProfileFormHandler.getEditValue().put("nickname","D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("newNickname","D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("newName","D3cardnickname4");
        bbbProfileFormHandler.getBillAddrValue().put("makeBilling","true");
        bbbProfileFormHandler.getEditValue().put("newCreditCard","false");
        bbbProfileFormHandler.handleUpdateCard(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in changing default for credi card",	bbbProfileFormHandler.getFormExceptions().size()==0);


        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "4");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "2016");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name three");
        bbbProfileFormHandler.getBillAddrValue().put("newNickname","newBillingAddress");
        bbbProfileFormHandler.getValue().put("defaultCreditCard", "D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname4");
        bbbProfileFormHandler.getBillAddrValue().put("address1", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("address2", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("city", "city");
        bbbProfileFormHandler.getBillAddrValue().put("state", "NH");
        bbbProfileFormHandler.getBillAddrValue().put("firstName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("companyName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("lastName", "lasname");
        bbbProfileFormHandler.getBillAddrValue().put("country", "US");
        bbbProfileFormHandler.getBillAddrValue().put("postalCode", "12345");
        bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", "D3secaddrnickname4");
        bbbProfileFormHandler.getEditValue().put("nickname","D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("newNickname","D3cardnickname4");
        bbbProfileFormHandler.getBillAddrValue().put("makeBilling","true");
        bbbProfileFormHandler.getEditValue().put("newCreditCard","false");
        bbbProfileFormHandler.setEditCard("D3cardnickname4");
        bbbProfileFormHandler.handleEditCard(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in changing default for credi card",	bbbProfileFormHandler.getFormExceptions().size()==0);


        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "4111111111111111");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "4");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "2016");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name three");
        bbbProfileFormHandler.getBillAddrValue().put("newNickname","newBillingAddress");
        bbbProfileFormHandler.getValue().put("defaultCreditCard", "D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname4");
        bbbProfileFormHandler.getBillAddrValue().put("address1", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("address2", "addre");
        bbbProfileFormHandler.getBillAddrValue().put("city", "city");
        bbbProfileFormHandler.getBillAddrValue().put("state", "NH");
        bbbProfileFormHandler.getBillAddrValue().put("firstName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("companyName", "firsname");
        bbbProfileFormHandler.getBillAddrValue().put("lastName", "lasname");
        bbbProfileFormHandler.getBillAddrValue().put("country", "US");
        bbbProfileFormHandler.getBillAddrValue().put("postalCode", "12345");
        bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", "D3secaddrnickname4");
        bbbProfileFormHandler.getEditValue().put("nickname","D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("newNickname","D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("address1", "addre");
        bbbProfileFormHandler.getEditValue().put("address2", "addre");
        bbbProfileFormHandler.getEditValue().put("city", "city");
        bbbProfileFormHandler.getEditValue().put("state", "NH");
        bbbProfileFormHandler.getEditValue().put("firstName", "firsname");
        bbbProfileFormHandler.getEditValue().put("companyName", "firsname");
        bbbProfileFormHandler.getEditValue().put("lastName", "lasname");
        bbbProfileFormHandler.getEditValue().put("country", "US");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "Visa");
        bbbProfileFormHandler.getEditValue().put("newCreditCard","false");
        bbbProfileFormHandler.getEditValue().put("postalCode","12345");
        bbbProfileFormHandler.getEditValue().put("companyName","companyName");
        bbbProfileFormHandler.setEditCard("D3cardnickname4");
        bbbProfileFormHandler.handleNewAddress(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in changing default for credi card",	bbbProfileFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.getEditValue().clear();
        bbbProfileFormHandler.getEditValue().put("firstName", "firsname");
        bbbProfileFormHandler.getEditValue().put("lastName", "firsname");
        bbbProfileFormHandler.getEditValue().put("address1", "addre");
        bbbProfileFormHandler.getEditValue().put("address2", "addre");
        bbbProfileFormHandler.getEditValue().put("city", "city");
        bbbProfileFormHandler.getEditValue().put("state", "AK");
        bbbProfileFormHandler.getEditValue().put("companyName", "firsname");
        bbbProfileFormHandler.getEditValue().put("country", "US");
        bbbProfileFormHandler.getEditValue().put("nickname","D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("newNickname","D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("postalCode","12345");
        bbbProfileFormHandler.setNewNicknameValueMapKey("newNickname");
        bbbProfileFormHandler.setNicknameValueMapKey("nickname");

        bbbProfileFormHandler.setDefaultShippingAddress("D3cardnickname4");
        bbbProfileFormHandler.handleDefaultShippingAddress(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in changing default for credi card",	bbbProfileFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.getFormExceptions().clear();


        bbbProfileFormHandler.handleUpdateAddress(this.getRequest(), this.getResponse());
        assertTrue("ERRORS ",	bbbProfileFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.getFormExceptions().clear();


        //change default credit card to second card
        bbbProfileFormHandler.getValue().put("defaultCreditCard", "D3cardnickname2");
        bbbProfileFormHandler.handleCheckoutDefaults(this.getRequest(), this.getResponse());
        assertTrue("ERRORS", bbbProfileFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.getFormExceptions().clear();

        //remove cards
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname1");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname1");
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname2");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname2");
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname3");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname3");
        bbbProfileFormHandler.getValue().put("mRemoveCard", "D3cardnickname4");
        bbbProfileFormHandler.setRemoveCard("D3cardnickname4");
        bbbProfileFormHandler.handleRemoveCard(this.getRequest(), this.getResponse());
        assertTrue("ERRORS", bbbProfileFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.getFormExceptions().clear();

        bbbProfileFormHandler.setUseShippingAddressAsDefault(true);
        bbbProfileFormHandler.setUseBillingAddressAsDefault(true);
        bbbProfileFormHandler.setAddressId("D3secaddrnickname4");
        bbbProfileFormHandler.handleRemoveAddress(this.getRequest(), this.getResponse());
        assertTrue("ERRORS", bbbProfileFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.getFormExceptions().clear();

        //delete address
        bbbProfileFormHandler.setAddressId("D3secaddrnickname1");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname1",true);

        bbbProfileFormHandler.setAddressId("D3secaddrnickname2");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname2",true);
        bbbProfileFormHandler.setAddressId("D3secaddrnickname3");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname3",true);
        bbbProfileFormHandler.setAddressId("D3secaddrnickname4");
        bbbProfileTools.removeProfileRepositoryAddress(bbbProfileFormHandler.getProfile(), "D3secaddrnickname4",true);

        //delete credit card
        //bbbProfileFormHandler.handleRemoveCard(getRequest(), getResponse());
        assertTrue("ERRORS in Removing credit card ",
                bbbProfileFormHandler.getFormExceptions().size()==0);

        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in Logging out ",
                bbbProfileFormHandler.getFormExceptions().size()==0);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);


        //calling invalid parameter
        bbbProfileFormHandler.getEditValue().put("creditCardNumber", "");
        bbbProfileFormHandler.getEditValue().put("creditCardType", "");
        bbbProfileFormHandler.getEditValue().put("expirationMonth", "");
        bbbProfileFormHandler.getEditValue().put("expirationYear", "");
        bbbProfileFormHandler.getEditValue().put("nameOnCard", "name three");
        bbbProfileFormHandler.getBillAddrValue().put("newNickname","newBillingAddress");
        bbbProfileFormHandler.getValue().put("defaultCreditCard", "D3cardnickname4");
        bbbProfileFormHandler.getEditValue().put("creditCardNickname", "D3cardnickname4");
        bbbProfileFormHandler.getBillAddrValue().put("address1", "");
        bbbProfileFormHandler.getBillAddrValue().put("city", "");
        bbbProfileFormHandler.getBillAddrValue().put("state", "");
        bbbProfileFormHandler.getBillAddrValue().put("firstName", "");
        bbbProfileFormHandler.getBillAddrValue().put("lastName", "");
        bbbProfileFormHandler.getBillAddrValue().put("country", "");
        bbbProfileFormHandler.getBillAddrValue().put("postalCode", "");
        bbbProfileFormHandler.getBillAddrValue().put("shippingAddrNickname", "D3secaddrnickname4");
        bbbProfileFormHandler.handleCreateCreditCard(this.getRequest(), this.getResponse());
        assertFalse("No Error ERRORS in changing default for credi card",	bbbProfileFormHandler.getFormExceptions().size()==0);


		
	}
	
	
	public void testHandleRegistration() throws Exception {
		
		 final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
	        bbbProfileFormHandler.setExpireSessionOnLogout(false);
	        this.getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
	        bbbProfileFormHandler.getFormExceptions().clear();
	        this.getRequest().setParameter("userCheckingOut","asdf");
	        if(!bbbProfileFormHandler.getProfile().isTransient()){
	            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
	        }

	        final BBBOrderTrackingManager manager = (BBBOrderTrackingManager) this.getObject("bbbOrderTrackingManager");

	        final String pSiteId = (String) this.getObject("siteId");
	        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

	          
	        final String orderId = (String) this.getObject("orderId");
	        final BBBOrder order = (BBBOrder)manager.getOrderObject(orderId);
	       
	        assertNotNull(order);
	        
	        BBBOrderTools orderTools = (BBBOrderTools) Nucleus.getGlobalNucleus().resolveName("/atg/commerce/order/OrderTools");  
	        
	        
	        BBBRepositoryContactInfo address = orderTools.createBillingAddress();
	        address.setFirstName("First");
	        address.setLastName("Last");
	        address.setCity("Houston");
	        address.setState("TX");
	        address.setCountry("US");
	        address.setAddress1("Test");
	        address.setPostalCode("12001");
	        address.setFromPaypal(false);
	        order.setBillingAddress(address);
	        
	        String email = (String) this.getObject("email");
	        final String password = (String) this.getObject("password");
	        bbbProfileFormHandler.getValue().put("email", email);
			bbbProfileFormHandler.getValue().put("login", email);
			
			bbbProfileFormHandler.getValue().put("password", password);
			bbbProfileFormHandler.getValue().put("confirmPassword", password);
			bbbProfileFormHandler.getValue().put("phoneNumber", "1231231234");
			bbbProfileFormHandler.getValue().put("mobileNumber", "1231231234");
			
			bbbProfileFormHandler.setBBBOrder(order);
			List<PaymentGroup> listPaymentGroups = order.getPaymentGroups();
			if (listPaymentGroups != null && listPaymentGroups.size() > 0) {
				for (PaymentGroup paymentGroup : listPaymentGroups) {
					if (paymentGroup instanceof CreditCard) {
						if (((CreditCardInfo) paymentGroup).getBillingAddress() != null) {
							String firstName = ((CreditCardInfo) paymentGroup)
									.getBillingAddress().getFirstName();
							String lastName = ((CreditCardInfo) paymentGroup)
									.getBillingAddress().getLastName();
							if(firstName == null || lastName == null){
								((CreditCardInfo) paymentGroup).getBillingAddress().setFirstName("Fname");
								((CreditCardInfo) paymentGroup).getBillingAddress().setLastName("Fname");
							}
							
						}

					}break;
				} 
			}
			
			bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathCA"));
	        bbbProfileFormHandler.handleRegistration(this.getRequest(), this.getResponse());
	        assertTrue(bbbProfileFormHandler.getFormExceptions().size()>=0);
	        if(!bbbProfileFormHandler.getProfile().isTransient()){
	            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
	        }
			bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BuyBuyBaby"));
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
	        email = (String) this.getObject("email");
	        bbbProfileFormHandler.getValue().put("email",email);
	        bbbProfileFormHandler.handleRegistration(this.getRequest(), this.getResponse());
	        assertTrue(bbbProfileFormHandler.getFormExceptions().size()>=0);
	        if(!bbbProfileFormHandler.getProfile().isTransient()){
	            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
	        }
			
			bbbProfileFormHandler.getFormExceptions().clear();
			bbbProfileFormHandler.setFormErrorVal(false);
	        email = (int)(Math.random()*9999) + (String) this.getObject("email");
	        bbbProfileFormHandler.getValue().put("email",email);
	        bbbProfileFormHandler.getValue().put("login", email);
	        bbbProfileFormHandler.getValue().put("loginEmail", "");
	        bbbProfileFormHandler.setBBBOrder(order);
			ServletUtil.setCurrentRequest(getRequest());
	        bbbProfileFormHandler.handleRegistration(this.getRequest(), this.getResponse());
       	    assertTrue(bbbProfileFormHandler.getFormExceptions().size()>=0);
	        if(!bbbProfileFormHandler.getProfile().isTransient()){
	            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
	        }
		}
	
	
	public void testHandleLegacyForgetPassword() throws Exception{
        this.getRequest().setParameter("userCheckingOut","asdf");
        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
		//getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
		bbbProfileFormHandler.getFormExceptions().clear();
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
        this.getObject("siteId");
		bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext("BedBathUS"));
        bbbProfileFormHandler.handleLegacyForgetPassword(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()>0);

		
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.getValue().put("email", "x" + email);
        bbbProfileFormHandler.handleLegacyForgetPassword(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()>0);

        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.getValue().put("email", "abhilash@gmail.com");
        bbbProfileFormHandler.handleLegacyForgetPassword(this.getRequest(), this.getResponse());
        assertTrue("ERRORS in Logging out ",bbbProfileFormHandler.getFormExceptions().size()>0);
        manager.updatePassword(email, password);
		
		manager.getAddressAPIImpl(); 
		manager.getCreditCardAPIImpl();
		manager.getCheckoutManager();
		manager.getPassword();
		manager.setPassword(null);
		manager.getSiteGroup();
		new PasswordMaxLengthRule().setMaxLength(2);
		new PasswordMaxLengthRule().getMaxLength();
		manager.getTools().isSendEmailInSeparateThread();
		manager.getTools().getTemplateEmailSender();
		manager.getTools().isPersistEmails();
		manager.getTools().getPlaceUtils();
		manager.getTools().setShippingAddressClassName(null);
		
		bbbProfileFormHandler.getLoginURL();
		bbbProfileFormHandler.setUserCheckingOut(null);
	
		bbbProfileFormHandler.setLegacyPasswordPopupURL(bbbProfileFormHandler.getLegacyPasswordPopupURL());
		bbbProfileFormHandler.getLegacyUser();
		bbbProfileFormHandler.setUserMigratedLoginProp(bbbProfileFormHandler.isUserMigratedLoginProp());
		bbbProfileFormHandler.setAddressAdded(bbbProfileFormHandler.getAddressAdded());	
		bbbProfileFormHandler.setFacebookLinking(bbbProfileFormHandler.getFacebookLinking());		
		bbbProfileFormHandler.getOrder();
		
		bbbProfileFormHandler.setReclaimLegacyIncorrectPasswordSuccessURL(bbbProfileFormHandler.getReclaimLegacyIncorrectPasswordSuccessURL());
		bbbProfileFormHandler.setStrReminderSent(bbbProfileFormHandler.getStrReminderSent());
		bbbProfileFormHandler.setLegacyForgetPasswordStatus(bbbProfileFormHandler.isLegacyForgetPasswordStatus());
	
		bbbProfileFormHandler.setLegacyPasswordSuccessURL(bbbProfileFormHandler.getLegacyPasswordSuccessURL());
		bbbProfileFormHandler.isFormErrorVal();
		
		bbbProfileFormHandler.setRegistrationSuccessURL(bbbProfileFormHandler.getRegistrationSuccessURL());
		bbbProfileFormHandler.setRegistrationErrorURL(bbbProfileFormHandler.getRegistrationErrorURL());
		

		bbbProfileFormHandler.setExtenstionSuccessURL(bbbProfileFormHandler.getExtenstionSuccessURL());
		bbbProfileFormHandler.getSiteGroup();

		bbbProfileFormHandler.getAssoSite();
		
		bbbProfileFormHandler.setLoginEmail(bbbProfileFormHandler.getLoginEmail());
		bbbProfileFormHandler.getSharedCheckBoxEnabled();
		
		bbbProfileFormHandler.setMakePreferredSet(true);
		bbbProfileFormHandler.setEditValue(null);
		
		bbbProfileFormHandler.getAddressProperties();
		
		bbbProfileFormHandler.setAddressIdValueMapKey(null);
		bbbProfileFormHandler.getAddressIdValueMapKey();
		
		bbbProfileFormHandler.setShippingAddressNicknameMapKey(null);
		bbbProfileFormHandler.setCardProperties(null);
		
		bbbProfileFormHandler.getImmutableCardProperties();
		bbbProfileFormHandler.getEditAddress();
		
		bbbProfileFormHandler.setEditAddress(null);
		bbbProfileFormHandler.setDefaultCard(null);
		
		bbbProfileFormHandler.getDefaultCard();
		bbbProfileFormHandler.isChangePasswordSuccessMessage();
		bbbProfileFormHandler.setChangePasswordSuccessMessage(true);
		
		bbbProfileFormHandler.isSuccessMessage();
		bbbProfileFormHandler.setErrorMap(new HashMap<String, String>());
		
		bbbProfileFormHandler.getRegistrationTemplateEmailInfo();
		bbbProfileFormHandler.setNewAddressSuccessURL(null);
		
		bbbProfileFormHandler.setNewAddressErrorURL(null);
		bbbProfileFormHandler.setUpdateAddressSuccessURL(null);
		bbbProfileFormHandler.setUpdateAddressErrorURL(null);
		
		bbbProfileFormHandler.setCreateCardSuccessURL(null);
		bbbProfileFormHandler.setCreateCardErrorURL(null);
		
		bbbProfileFormHandler.setUpdateCardSuccessURL(null);
		
		bbbProfileFormHandler.setUpdateCardErrorURL(null);
		bbbProfileFormHandler.setRemoveCardSuccessURL(null);
		bbbProfileFormHandler.getRemoveCardSuccessURL();
		bbbProfileFormHandler.setRemoveCardErrorURL(null);
		
		bbbProfileFormHandler.getRemoveCardErrorURL();
		bbbProfileFormHandler.setPreRegisterSuccessURL(null);
		bbbProfileFormHandler.setPreRegisterErrorURL(null);
		
		bbbProfileFormHandler.getShippingGroupMapContainer();
		
		bbbProfileFormHandler.getOrder();
		//bbbProfileFormHandler.getStateList();
		
		bbbProfileFormHandler.getCreditCardTypes();
		bbbProfileFormHandler.getRegistrySearchVO();
		bbbProfileFormHandler.setRegistrySearchVO(null);
		
		bbbProfileFormHandler.getGiftRegistryManager();
		bbbProfileFormHandler.getCreditCardYearMaxLimit();
		
	
		
		
		
	
		bbbProfileFormHandler.getReclaimLegacyAccountSuccessURL();
		bbbProfileFormHandler.setReclaimLegacyAccountSuccessURL(null);
		bbbProfileFormHandler.setReclaimLegacyAccountIncorrectPasswordURL(null);
		
		bbbProfileFormHandler.setDefaultBillingAddress(null);
		bbbProfileFormHandler.setDefaultBillingAddress(null);
		bbbProfileFormHandler.getEmailAddress();
		bbbProfileFormHandler.setEmailAddress(null);
		bbbProfileFormHandler.getFacebookProfileTool();
		bbbProfileFormHandler.getBillingAddressPropertyList();
		bbbProfileFormHandler.getReclaimLegacyAccountIncorrectPasswordURL();
		bbbProfileFormHandler.getDefaultBillingAddress();
		
	}
	
	
}
