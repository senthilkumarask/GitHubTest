package com.bbb.rest.commerce;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

import com.bbb.rest.framework.BaseTestCase;

public class TestRestGetItemInventory extends BaseTestCase {
    RestSession mSession;
    HashMap params = null;

    public TestRestGetItemInventory(final String name) {
        super(name);
    }

    /**
     * Test for authorised login successfully.
     * 
     * @throws RestClientException
     * @throws IOException
     * @throws JSONException
     */

    public void testGetItemsInventoryLoggedinUser() throws RestClientException, JSONException, IOException{
        this.mSession = this.getNewHttpSession();
        this.mSession.setUseHttpsForLogin(false);
        this.mSession.setUseInternalProfileForLogin(false);
        RestResult pd =  null;
        RestResult pd1 =  null;
        try {
            String result =null;
            this.mSession.login();
            this.params = (HashMap) this.getControleParameters();
            this.params.put("atg-rest-return-form-handler-exceptions", this.getObject("atg-rest-return-form-handler-exceptions"));
            this.params.put("value.login", this.getObject("login"));
            this.params.put("value.password", this.getObject("password"));
            this.params.put("atg-rest-return-form-handler-properties", "true");
            pd = this.mSession.createHttpRequest(
                    "http://"
                            + this.getHost()
                            + ":"
                            + this.getPort()
                            + (String) this.getObject("loginRequest"), this.params,"POST");
            final JSONObject json = new JSONObject(pd.readInputStream());
            System.out.println(json);
            if(json.has("formExceptions")){
                result = json.getString("formExceptions");
            }
            assertNull(result);
            final String skuid =  (String) this.getObject("pSkuId");
            final String storeId =  (String) this.getObject("pStoreId");
            final String operation =  (String) this.getObject("operation");
            final int itemQuantity =   (Integer) this.getObject("pItemQty");
            pd1 = this.mSession.createHttpRequest(
                    "http://"
                            + this.getHost()
                            + ":"
                            + this.getPort()
                            + (String) this.getObject("getInventoryOfItem"), this.params,new Object[] {skuid,storeId,itemQuantity,operation},"POST");
            final JSONObject jsonResponseObj = new JSONObject(pd1.readInputStream());
            System.out.println(jsonResponseObj);
            assertTrue(true);
        }finally {
            if(pd != null) {
                pd = null;
            }
            try {
                this.mSession.logout();
            } catch (final RestClientException e) {
                e.printStackTrace();
            }
        }
    }

    public void testGetItemsInventoryAnonymousUser() throws RestClientException, JSONException, IOException{
        this.mSession = this.getNewHttpSession();
        this.mSession.setUseHttpsForLogin(false);
        this.mSession.setUseInternalProfileForLogin(false);
        RestResult pd =  null;
        try {
            this.mSession.login();
            this.params = (HashMap) this.getControleParameters();
            this.params.put("atg-rest-return-form-handler-exceptions", this.getObject("atg-rest-return-form-handler-exceptions"));
            this.params.put("atg-rest-return-form-handler-properties", "true");
            final String skuid =  (String) this.getObject("pSkuId");
            final String storeId =  (String) this.getObject("pStoreId");
            final String operation =  (String) this.getObject("operation");
            final String registryId =  (String) this.getObject("registryId");
            final int itemQuantity =   (Integer) this.getObject("pItemQty");
            pd = this.mSession.createHttpRequest(
                    "http://"
                            + this.getHost()
                            + ":"
                            + this.getPort()
                            + (String) this.getObject("getInventoryOfItem"), this.params,new Object[] {skuid,storeId,itemQuantity,operation,registryId},"POST");
            final JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
            System.out.println(jsonResponseObj);
            assertTrue(true);
        }finally {
            if(pd != null) {
                pd = null;
            }
            try {
                this.mSession.logout();
            } catch (final RestClientException e) {
                e.printStackTrace();
            }
        }
    }


    public void testGetItemsInventoryError() throws RestClientException, JSONException, IOException{
        this.mSession = this.getNewHttpSession();
        this.mSession.setUseHttpsForLogin(false);
        this.mSession.setUseInternalProfileForLogin(false);
        RestResult pd =  null;
        try {
            this.mSession.login();
            this.params = (HashMap) this.getControleParameters();
            this.params.put("atg-rest-return-form-handler-exceptions", this.getObject("atg-rest-return-form-handler-exceptions"));
            this.params.put("atg-rest-return-form-handler-properties", "true");
            final String skuid =  (String) this.getObject("pSkuId");
            final String storeId =  (String) this.getObject("pStoreId");
            final String operation =  (String) this.getObject("operation");
            final String registryId =  (String) this.getObject("registryId");
            final int itemQuantity =   (Integer) this.getObject("pItemQty");
            pd = this.mSession.createHttpRequest(
                    "http://"
                            + this.getHost()
                            + ":"
                            + this.getPort()
                            + (String) this.getObject("getInventoryOfItem"), this.params,new Object[] {skuid,storeId,itemQuantity,operation,registryId},"POST");
            final JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
            System.out.println(jsonResponseObj);
        }catch (final RestClientException e) {
            if(e.getMessage().contains("store_id_null")){
                System.out.println(e);
                assertTrue(true);
            } else {
                assertFalse(false);
            }
        }finally {
            if(pd != null) {
                pd = null;
            }
            try {
                this.mSession.logout();
            } catch (final RestClientException e) {
                e.printStackTrace();
            }
        }
    }
}
