package com.bbb.account;

import java.util.Random;

import atg.userprofiling.Profile;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.BBBSiteContext;
import com.bbb.profile.BBBPropertyManager;
import com.sapient.common.tests.BaseTestCase;

public class TestProfileSync extends BaseTestCase {
    @SuppressWarnings("unchecked")
    public void testProfileSync() throws Exception {

        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        System.out.println("TestProfileSync.testProfileSync.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

        this.getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        final BBBProfileManager manger = (BBBProfileManager) this.getObject("bbbProfileManager");

        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));

        final String siteId = bbbProfileFormHandler.getSiteContext().getSite().getId();
        System.out.println("siteId : " + siteId);

        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        manger.updatePassword(email, password);
        final Profile profile = bbbProfileFormHandler.getProfile();
        final BBBPropertyManager propertyManager = bbbProfileFormHandler.getPropertyManager();

        if (profile.isTransient()) {
            bbbProfileFormHandler.getValue().put("login", email);
            bbbProfileFormHandler.getValue().put("password", password);
            atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
            final boolean isLogin = bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
            assertFalse(bbbProfileFormHandler.getProfile().isTransient());
            if (isLogin) {
                final String firstNameProperty = propertyManager.getFirstNamePropertyName();
                final String lastNameProperty = propertyManager.getLastNamePropertyName();
                final String mobileNumberProperty = propertyManager.getMobileNumberPropertyName();
                final String phoneNumberProperty = propertyManager.getPhoneNumberPropertyName();
                final String emailAddressProperty = propertyManager.getEmailAddressPropertyName();

                final String emailAddress = (String) profile.getPropertyValue(emailAddressProperty);
                final String firstName = (String) profile.getPropertyValue(firstNameProperty);
                final String lastName = (String) profile.getPropertyValue(lastNameProperty) + BBBCoreConstants.BLANK + new Random().nextInt(10);
                String mobileNumber = (String) profile.getPropertyValue(mobileNumberProperty);
                String phoneNumber = (String) profile.getPropertyValue(phoneNumberProperty);
                if((mobileNumber==null) || mobileNumber.equals("")){
                    mobileNumber = "1234567890";
                }
                if((phoneNumber == null) || phoneNumber.equals("")){
                    phoneNumber = "1234567890";
                }
                bbbProfileFormHandler.getValue().put(emailAddressProperty, emailAddress);
                bbbProfileFormHandler.getValue().put(firstNameProperty,	firstName);
                bbbProfileFormHandler.getValue().put(lastNameProperty, lastName);
                bbbProfileFormHandler.getValue().put(mobileNumberProperty, mobileNumber);
                bbbProfileFormHandler.getValue().put(phoneNumberProperty, phoneNumber);

                assertEquals(true, bbbProfileFormHandler.updateRegistry());
            }
            if(!bbbProfileFormHandler.getProfile().isTransient()){
                bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
            }
            bbbProfileFormHandler.getErrorMap().clear();
            bbbProfileFormHandler.getFormExceptions().clear();
            bbbProfileFormHandler.setFormErrorVal(false);
        }

    }
}
