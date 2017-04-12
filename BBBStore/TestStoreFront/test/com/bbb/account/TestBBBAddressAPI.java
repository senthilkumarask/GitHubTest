package com.bbb.account;



import java.util.Iterator;
import java.util.Map;

import atg.repository.RepositoryItem;

import com.bbb.account.api.BBBAddressAPI;
import com.bbb.account.api.BBBAddressAPIConstants;
import com.bbb.account.api.BBBAddressAPIImpl;
import com.bbb.account.api.BBBAddressVO;
import com.bbb.account.api.BBBCreditCardAPI;
import com.bbb.account.api.BBBCreditCardAPIConstants;
import com.bbb.account.api.BBBCreditCardAPIImpl;
import com.bbb.commerce.common.BasicBBBCreditCardInfo;
import com.bbb.framework.BBBSiteContext;
import com.sapient.common.tests.BaseTestCase;

public class TestBBBAddressAPI extends BaseTestCase
{
    public void testBBBAddressAPI() throws Exception{

        final BBBProfileFormHandler bbbProfileFormHandler = (BBBProfileFormHandler) this.getObject("bbbProfileFormHandler");
        bbbProfileFormHandler.setExpireSessionOnLogout(false);
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        if(!bbbProfileFormHandler.getProfile().isTransient()){
            bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());
        }
        System.out.println("TestBBBProfileFormHandler.testHandleLogin.getFormExceptions()" + bbbProfileFormHandler.getFormExceptions());
        assertTrue(bbbProfileFormHandler.getProfile().isTransient());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size() == 0);

        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);

        //BBBProfileTools bbbProfileTools = (BBBProfileTools) getObject("bbbProfileTools");
        //getRequest().setParameter("BBBProfileFormHandler", bbbProfileFormHandler);
        final BBBProfileManager manager = (BBBProfileManager)this.getObject("bbbProfileManager");
        final String pSiteId = (String) this.getObject("siteId");
        bbbProfileFormHandler.setSiteContext(BBBSiteContext.getBBBSiteContext(pSiteId));
        atg.servlet.ServletUtil.setCurrentRequest(this.getRequest());
        final String email = (String) this.getObject("email");
        final String password = (String) this.getObject("password");
        manager.updatePassword(email, "S@pient1");
        bbbProfileFormHandler.getErrorMap().clear();
        bbbProfileFormHandler.getFormExceptions().clear();
        bbbProfileFormHandler.setFormErrorVal(false);
        bbbProfileFormHandler.getValue().put("login", email);
        bbbProfileFormHandler.getValue().put("password", password);
        bbbProfileFormHandler.handleLogin(this.getRequest(), this.getResponse());
        assertTrue(bbbProfileFormHandler.getFormExceptions().size()==0);
        assertFalse(bbbProfileFormHandler.getProfile().isTransient());
        bbbProfileFormHandler.getProfileTools().setLoggingDebug(true);
        bbbProfileFormHandler.getProfileManager().setLoggingDebug(true);
        final BBBCreditCardAPI creditCardAPIImpl = (BBBCreditCardAPIImpl)this.getObject("bbbCreditCardAPIImpl");
        final BasicBBBCreditCardInfo cardInfo = new BasicBBBCreditCardInfo();
        cardInfo.setCreditCardNumber("4111111111111111");
        cardInfo.setCreditCardType("MasterCard");
        cardInfo.setNameOnCard("visa");
        cardInfo.setExpirationYear("2015");
        cardInfo.setExpirationMonth("11");
        final BBBAddressAPI bbbAddressAPIImpl = (BBBAddressAPIImpl)this.getObject("bbbAddressAPIImpl");
        final BBBAddressVO addressVO = new BBBAddressVO();
        addressVO.setFirstName("John");
        addressVO.setLastName("pandit");
        addressVO.setAddress1("420 japan");
        addressVO.setAddress2("Indonasia");

        addressVO.setCity("Los angles");
        addressVO.setState("California");
        addressVO.setCountry("US");
        addressVO.setPostalCode("07063");
        new BBBAddressAPIConstants();
        new BBBCreditCardAPIConstants();
        try{
            assertNotNull(creditCardAPIImpl.addNewCreditCard(bbbProfileFormHandler.getProfile(), cardInfo, pSiteId));
            assertNotNull(creditCardAPIImpl.getUserCreditCardWallet(bbbProfileFormHandler.getProfile(), pSiteId));
            assertNotNull(creditCardAPIImpl.getDefaultCreditCard(bbbProfileFormHandler.getProfile(), pSiteId));
            assertNotNull(bbbAddressAPIImpl.getDefaultShippingAddress(bbbProfileFormHandler.getProfile(), pSiteId));
            assertNotNull(bbbAddressAPIImpl.getDefaultBillingAddress(bbbProfileFormHandler.getProfile(), pSiteId));
            assertNotNull(bbbAddressAPIImpl.getShippingAddress(bbbProfileFormHandler.getProfile(), pSiteId));
            assertNotNull(bbbAddressAPIImpl.getBillingAddress(bbbProfileFormHandler.getProfile(), pSiteId));
            assertNotNull(bbbAddressAPIImpl.addNewShippingAddress(bbbProfileFormHandler.getProfile(), addressVO, pSiteId));
            assertNotNull(bbbAddressAPIImpl.addNewShippingAddress(bbbProfileFormHandler.getProfile(), addressVO, pSiteId, false,false));
            assertNotNull(bbbAddressAPIImpl.addNewShippingAddress(bbbProfileFormHandler.getProfile(), addressVO, pSiteId, true, true));
            assertNotNull(bbbAddressAPIImpl.addNewBillingAddress(bbbProfileFormHandler.getProfile(), addressVO, pSiteId,true));

            final Map addresses = (Map)bbbProfileFormHandler.getProfile().getPropertyValue("secondaryAddresses");
            boolean x= true;
            if(addresses!=null){
                final Iterator it = addresses.entrySet().iterator();
                while(it.hasNext() && x){

                    final Map.Entry entry = (Map.Entry) it.next();

                    final RepositoryItem addressItem =(RepositoryItem)entry.getValue();

                    assertNotNull(bbbAddressAPIImpl.fetchAddress(bbbProfileFormHandler.getProfile(), pSiteId, addressItem.getRepositoryId()));

                    x= false;
                }
            }

            addressVO.setFirstName(null);
            addressVO.setLastName("pandit");
            addressVO.setAddress1("420 japan");
            addressVO.setAddress2("Indonasia");

            addressVO.setCity("Los angles");
            addressVO.setState("California");
            addressVO.setCountry("US");
            addressVO.setPostalCode("07063");
            assertTrue(BBBAddressTools.compare(addressVO,addressVO));
            assertFalse(BBBAddressTools.compare(null,addressVO));
            assertFalse(BBBAddressTools.compare(addressVO,null));
            assertTrue(BBBAddressTools.compare(null,null));

            final BBBAddressVO AddressVO1 = new BBBAddressVO();
            final BBBAddressVO AddressVO2 = new BBBAddressVO();
            final BBBAddressVO AddressVO3 = new BBBAddressVO();

            addressVO.setFirstName("Rajesh");
            addressVO.setLastName("pandit");
            addressVO.setAddress1("420 japan");
            addressVO.setAddress2("Indonasia");

            addressVO.setCity("Los angles");
            addressVO.setState("California");
            addressVO.setCountry("US");
            addressVO.setPostalCode("07063");

            BBBAddressTools.copyBBBAddress(addressVO, AddressVO1);

            AddressVO2.setFirstName(null);
            AddressVO2.setLastName(null);
            AddressVO2.setAddress1(null);
            AddressVO2.setAddress2(null);

            AddressVO2.setCity(null);
            AddressVO2.setState(null);
            AddressVO2.setCountry(null);
            AddressVO2.setPostalCode(null);
            BBBAddressTools.copyBBBAddress(AddressVO2,AddressVO3);

        } catch (final Exception e) {
            assertFalse(true);
        }
        bbbProfileFormHandler.handleLogout(this.getRequest(), this.getResponse());


    }


}
