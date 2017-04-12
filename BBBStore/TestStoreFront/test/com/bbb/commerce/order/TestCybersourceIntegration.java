package com.bbb.commerce.order;

import java.math.BigInteger;

import atg.core.util.Address;
import atg.integrations.cybersourcesoap.CyberSourceConfiguration;
import atg.integrations.cybersourcesoap.CyberSourceConnection;
import atg.integrations.cybersourcesoap.CyberSourceUtils;

import com.cybersource.stub.BillTo;
import com.cybersource.stub.CCAuthService;
import com.cybersource.stub.Card;
import com.cybersource.stub.Item;
import com.cybersource.stub.PurchaseTotals;
import com.cybersource.stub.ReplyMessage;
import com.cybersource.stub.RequestMessage;
import com.cybersource.stub.ShipTo;
import com.cybersource.stub.TaxService;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author alakra
 *
 */
public class TestCybersourceIntegration  extends BaseTestCase{
	
	public void testCybersourceCreditCard() throws Exception {
		CyberSourceConnection csConnection = (CyberSourceConnection) getObject("csConnection");
		CyberSourceConfiguration config = csConnection.getConfiguration();
		String siteName = "BuyBuyBaby";
		RequestMessage request = csConnection.getClientRequest(siteName);
		
		Address address = new Address();
		address.setFirstName("TEST");
		address.setLastName("TEST");
		address.setAddress1("3000 Sunrise Valley Drive");
		address.setCity("Reston");
		address.setState("VA");
		address.setCountry("US");
		address.setPostalCode("20190");
		
		BillTo billTo = new BillTo();
		CyberSourceUtils.fillBillTo(billTo, address, csConnection, true, false, siteName);
		billTo.setFirstName("TEST");
		billTo.setLastName("TEST");
		billTo.setEmail("test@example.com");
		request.setBillTo(billTo);
		
		ShipTo shipTo = new ShipTo();
		CyberSourceUtils.fillShipTo(shipTo, address, csConnection, true, false, siteName);
		shipTo.setFirstName("TEST");
		shipTo.setLastName("TEST");
		shipTo.setEmail("test@example.com");
		request.setShipTo(shipTo);
		
		Card card = new Card();
		card.setCardType("001"); //Visa
		card.setAccountNumber("4111111111111111");
		card.setExpirationMonth(BigInteger.valueOf(12));
		card.setExpirationYear(BigInteger.valueOf(2011));
		request.setCard(card);
		
		PurchaseTotals purchase = new PurchaseTotals();
		purchase.setCurrency("USD");
		//purchase.setGrandTotalAmount("540");
		request.setPurchaseTotals(purchase);
		
		Item item = new Item();
		item.setUnitPrice("140");
		Item[] items = {item};
		request.setItem(items);
		
		request.setMerchantID(config.getMerchantId(siteName));
		request.setMerchantReferenceCode("10101010");
		
		CCAuthService auth = new CCAuthService();
		auth.setRun("true");
		request.setCcAuthService(auth);
		
		ReplyMessage response = csConnection.sendRequest(request, siteName);
		getRequest().logInfo("Credit Auth Response:\n" + response);
	}
	
	public void testCybersourceTax() throws Exception {
		String siteName = "BuyBuyBaby";
		CyberSourceConnection csConnection = (CyberSourceConnection) getObject("csConnection");
		CyberSourceConfiguration config = csConnection.getConfiguration();
		RequestMessage request = csConnection.getClientRequest(siteName);
		
		Address address = new Address();
		address.setFirstName("TEST");
		address.setLastName("TEST");
		address.setAddress1("3000 Sunrise Valley Drive");
		address.setCity("Reston");
		address.setState("VA");
		address.setCountry("US");
		address.setPostalCode("20190");
		
		BillTo billTo = new BillTo();
		CyberSourceUtils.fillBillTo(billTo, address, csConnection, true, false, siteName);
		billTo.setFirstName("TEST");
		billTo.setLastName("TEST");
		billTo.setEmail("test@example.com");
		request.setBillTo(billTo);
		
		ShipTo shipTo = new ShipTo();
		CyberSourceUtils.fillShipTo(shipTo, address, csConnection, true, false, siteName);
		shipTo.setFirstName("TEST");
		shipTo.setLastName("TEST");
		shipTo.setEmail("test@example.com");
		request.setShipTo(shipTo);
		
		Card card = new Card();
		card.setCardType("001"); //Visa
		card.setAccountNumber("4111111111111111");
		card.setExpirationMonth(BigInteger.valueOf(12));
		card.setExpirationYear(BigInteger.valueOf(2011));
		request.setCard(card);
		
		PurchaseTotals purchase = new PurchaseTotals();
		purchase.setCurrency("USD");
		request.setPurchaseTotals(purchase);
		
		Item item = new Item();
		item.setUnitPrice("140");
		Item[] items = {item};
		request.setItem(items);
		
		request.setMerchantID(config.getMerchantId(siteName));
		request.setMerchantReferenceCode("10101010");
		
		TaxService taxService = new TaxService();
		taxService.setRun("true");
		
		request.setTaxService(taxService);
		
		ReplyMessage response = csConnection.sendRequest(request, siteName);
		getRequest().logInfo("Tax Response:\n" + response);
	}
}