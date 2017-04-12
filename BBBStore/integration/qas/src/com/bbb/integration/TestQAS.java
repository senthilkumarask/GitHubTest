package com.bbb.integration;

import java.io.IOException;

import com.qas.newmedia.internet.ondemand.product.proweb.QasException;

import atg.qas.QasAddressVerification;
import atg.server.drp.DrpServletRequest;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

public class TestQAS {
	public static void main(String[] args) throws QasException,IOException {
        String s = "USA";
        String s1 = "12000 sunrise valley dr||herndon|TX|20170";
        String s2 = "ATG";
        QasAddressVerification qasaddressverification = new QasAddressVerification();
        qasaddressverification.setOnDemandUsername("ws_1414_ext");
        qasaddressverification.setOnDemandPassword("June2011");
        qasaddressverification.setOnDemandUrl("https://ws2.ondemand.qas.com/ProOnDemand/V3/ProOnDemandService.asmx?WSDL");
        qasaddressverification.setOnDemandLayout(s2);
        qasaddressverification.setCountry(s);
        qasaddressverification.setSearchString(s1);
        DynamoHttpServletRequest req = new DynamoHttpServletRequest();
        req.setRequest(new DrpServletRequest());
        qasaddressverification.doSearch(req, new DynamoHttpServletResponse());
        //System.out.println(qasaddressverification.getAddressLines());
        //System.out.println(qasaddressverification.getVerificationLevel());
        //System.out.println(qasaddressverification.getDpvStatus());
        //System.out.println(qasaddressverification.getPicklistItems());
        //System.out.println(qasaddressverification.getFullMoniker());
	}
}
