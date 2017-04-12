package com.bbb.integration;

import java.io.IOException;

import com.qas.www.web_2010_04.Fault;

//import com.qas.newmedia.internet.ondemand.product.proweb.QasException;

import atg.server.drp.DrpServletRequest;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.tbsqas.QasAddressVerification;

public class TestQAS {
	public static void main(String[] args) throws IOException {
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
        try {
			qasaddressverification.doSearch(req, new DynamoHttpServletResponse());
		} catch (Fault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //System.out.println(qasaddressverification.getAddressLines());
        //System.out.println(qasaddressverification.getVerificationLevel());
        //System.out.println(qasaddressverification.getDpvStatus());
        //System.out.println(qasaddressverification.getPicklistItems());
        //System.out.println(qasaddressverification.getFullMoniker());
	}
}
