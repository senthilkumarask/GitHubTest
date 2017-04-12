package com.bbb.valuelink.test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import com.bbb.valuelink.encryption.EncryptionUtil;
import com.bbb.valuelink.encryption.ValueLinkEncryptor;

import net.datawire.vxn3.SimpleTransaction;
import net.datawire.vxn3.VXN;
import net.datawire.vxn3.VXNException;

//==========================================================
/**
 * Sample class to make Balance API call.
 */
@SuppressWarnings("unchecked")
public class CheckBalanceSampleThread {

	private static VXN vxn = null;
	private static char FS = (char) 0x1C;
	private final static String BALANCE_TRAN = "2400";
	private final static String REDEEM_TRAN = "2202";
	private final static String REDEEM_VOID_TRAN = "2800";
	private final static String stMID = "99032159997";
	private final static String stTID = "00000000651";
	private final static String stDID = "00010766123432271633";
	private final static String svcid = "104";
	private final static String AppID = "BEDBATHGIFTJAVA2";
	private final static String version = "V100000";

	@SuppressWarnings("rawtypes")
	private final static List sdList = new java.util.ArrayList(2);

	static {
		sdList.add("https://staging1.datawire.net/sd");
		sdList.add("https://staging2.datawire.net/sd");
	}

	// ------------------------------------------------------
	public static void main(String args[]) throws InterruptedException {
		String mid = "99032159997";
		String gcNumber = "7777060829214424";
		String pin = "01788105";
		String workingKeyId = "0312";

		ValueLinkEncryptor vle = new ValueLinkEncryptor();
		String encryptedPin = vle.encryptPin(pin);

		System.out.println();// NOPMD - this is test class

		Thread th = new Thread();

		TimeZone usTimeZone = TimeZone.getTimeZone("America/Denver");
		// TimeZone usTimeZone = TimeZone.getTimeZone("America/Los_Angeles");
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeZone(usTimeZone);
		calendar.setTime(new Date());

		System.out.println("DATE[MM/DD/YYYY] " + "0" // NOPMD - this is test class
				+ calendar.get(Calendar.MONTH) + "/"
				+ calendar.get(Calendar.DATE) + "/"
				+ calendar.get(Calendar.YEAR));

		System.out.println();// NOPMD - this is test class

		String AM_PM = "AM";
		if (calendar.get(Calendar.AM_PM) == 1) {
			AM_PM = "PM";
		} else if (calendar.get(Calendar.AM_PM) == 2) {
			AM_PM = "AM";
		}
		System.out.println("[START TIME, TimeZone:America/Denver  HH:mm:ss] "// NOPMD - this is test class
				+ calendar.get(Calendar.HOUR) + ":"
				+ calendar.get(Calendar.MINUTE) + ":"
				+ calendar.get(Calendar.SECOND) + " " + AM_PM); 

		System.out.println();// NOPMD - this is test class

		for (int i = 1; i <= 100; i++) {
			System.out.println("********** Call: "+ i);// NOPMD - this is test class
			checkBalance(mid, gcNumber, encryptedPin, workingKeyId);
			th.sleep(1000);
		}

		System.out.println("[END TIME, TimeZone:America/Denver HH:mm:ss] " // NOPMD - this is test class
				+ calendar.get(Calendar.HOUR) + ":"
				+ calendar.get(Calendar.MINUTE) + ":"
				+ calendar.get(Calendar.SECOND) + " " + AM_PM);
		calendar.setTime(new Date());

		// redeem(mid, gcNumber, encryptedPin, workingKeyId, "10000");
		// redeemVoid(mid, gcNumber, encryptedPin, workingKeyId, "10000");
		// System.out.println(generateUniqueClientRef());

	}

	private static String generateUniqueClientRef() {
		Calendar cal = Calendar.getInstance();
		int i = cal.get(Calendar.HOUR_OF_DAY) + 65;
		int j = cal.get(Calendar.MINUTE) + 65;
		j = (j < 91 || (j > 96 && j < 123)) ? j : j - 6;
		String s = "" + (char) i + (char) j + cal.get(Calendar.SECOND)
				+ cal.get(Calendar.MILLISECOND);
		return s;
	}

	public static void checkBalance(String pMid, String pGCNumber, String pPin,
			String workingKeyId) {

		String balanceAPIPayload = "SV." + pMid + FS + "40" + BALANCE_TRAN + FS
				+ "12" + EncryptionUtil.getDateString("HHmmss") + FS + "13"
				+ EncryptionUtil.getDateString("MMDDyyyy") + FS + "42" + pMid
				+ "0000" + FS + "EA30" + FS + "70" + pGCNumber + FS + "34"
				+ pPin + FS + "C0" + "840" + FS + "F3" + workingKeyId;

		try {
			invokeVL(balanceAPIPayload);
		} catch (VXNException e) {
			e.printStackTrace();
		}
	}

	public static void redeem(String pMid, String pGCNumber, String pPin,
			String workingKeyId, String amount) {
		// Check Balance Payload
		String redeemPayload = "SV." + pMid + FS + "40" + REDEEM_TRAN + FS
				+ "04" + amount + FS + "12"
				+ EncryptionUtil.getDateString("HHmmss") + FS + "13"
				+ EncryptionUtil.getDateString("MMDDyyyy") + FS + "70"
				+ pGCNumber + FS + "34" + pPin + FS + "42" + pMid + "0000" + FS
				+ "EA30" + FS + "F3" + workingKeyId + FS + "C0" + "840" + FS
				+ "15X4443316f36313730";

		try {
			invokeVL(redeemPayload);
		} catch (VXNException e) {
			System.out	// NOPMD - this is test class
					.println("***********Exception in redeemVoid - Stack trace starts...");
			e.printStackTrace();
			System.out	// NOPMD - this is test class
					.println("***********Exception in redeemVoid - Stack trace ends...");// NOPMD - this is test class
			timeReversal(redeemPayload);
		}
	}

	private static void timeReversal(String origPayload) {
		try {
			String timeReversalPayload = EncryptionUtil
					.getTimeReversalPayload(origPayload);
			invokeVL(timeReversalPayload);
		} catch (VXNException e) {
			e.printStackTrace();
		}
	}

	public static void redeemVoid(String pMid, String pGCNumber, String pPin,
			String workingKeyId, String amount) {
		// Check Balance Payload
		String redeemPayload = "SV." + pMid + FS + "40" + REDEEM_VOID_TRAN + FS
				+ "04" + amount + FS + "12"
				+ EncryptionUtil.getDateString("HHmmss") + FS + "13"
				+ EncryptionUtil.getDateString("MMDDyyyy") + FS + "70"
				+ pGCNumber + FS + "34" + pPin + FS + "42" + pMid + "0000" + FS
				+ "EA30" + FS + "F3" + workingKeyId + FS + "C0" + "840";

		try {
			invokeVL(redeemPayload);
		} catch (VXNException e) {
			System.out // NOPMD - this is test class
					.println("***********Exception in redeemVoid - Stack trace starts...");// NOPMD - this is test class
			e.printStackTrace(); // NOPMD - this is test class
			System.out // NOPMD - this is test class
					.println("***********Exception in redeemVoid - Stack trace ends...");// NOPMD - this is test class
			timeReversal(redeemPayload);
		}
	}

	public static void invokeVL(String payloadStr) throws VXNException {
		System.out.println("Request Payload:" + payloadStr);// NOPMD - this is test class

		char payload[] = payloadStr.toCharArray();

		// This array is used to store response payload
		// returning by the transaction execution.
		char respPayload[];

		// Get the instance of the VXN object.
		// This will do a service discovery.
		if(vxn == null){
			vxn = VXN.getInstance(sdList, stDID, stMID, stTID, svcid, AppID);	
		}

		/*if(vxn.equals(vxn)){
			System.out.println("********************* same instance ***************");
			
		}
		*/
		
		String ttttttt = generateUniqueClientRef();
		SimpleTransaction tr = vxn.newSimpleTransaction(ttttttt + version);

		// Set payload for the transaction object.
		tr.setPayload(payload);
		// Execute the transaction object.
		tr.executeXmlRequest();
		// System.out.println("Transaction Response Status Code:" +
		// tr.getResponseStatusCode());
		// check the status and if it is "OK" then get the payload
		if (null != tr.getResponseStatusCode()
				&& "OK".equals(tr.getResponseStatusCode())) {
			// Get response payload.
			respPayload = tr.getPayload();
			// Expected response Payload for balance API
			// "SV DOT format";
			// System.out.println("Response Payload Reference:" + respPayload);
			System.out.println("Response Payload:" + new String(respPayload));// NOPMD - this is test class

			StringTokenizer strT = new StringTokenizer(new String(respPayload),
					new String(Character.toString(FS)));
			if (strT != null) {
				while (strT.hasMoreTokens()) {
					System.out.println(strT.nextToken());// NOPMD - this is test class
				}
			}
		}
	}
}