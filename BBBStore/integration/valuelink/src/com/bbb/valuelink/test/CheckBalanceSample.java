package com.bbb.valuelink.test;

import java.util.Calendar;
//import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.bbb.valuelink.encryption.EncryptionUtil;
import com.bbb.valuelink.encryption.ValueLinkEncryptor;

import net.datawire.vxn3.SimpleTransaction;
import net.datawire.vxn3.VXN;
import net.datawire.vxn3.VXNException;


//==========================================================
/** Sample class to make Balance API call.
 */
@SuppressWarnings("unchecked")
public class CheckBalanceSample {

	private static char FS = (char)0x1C;
    private final static String BALANCE_TRAN = "2400";
    private final static String REDEEM_TRAN = "2202";
    private final static String REDEEM_VOID_TRAN = "2800";
    private final static String stMID = "99032159997";
    private final static String stTID = "00000000651";
    private final static String stDID = "00010766123432271633";//"1.07661234322716E+16";
    private final static String svcid = "104";
    private final static String AppID = "BEDBATHGIFTJAVA2";
    private final static String version = "V100000";

    @SuppressWarnings("rawtypes")
	private final static List sdList = new java.util.ArrayList(2);
    
    static {
	    sdList.add("https://staging1.datawire.net/sd");
	    sdList.add("https://staging2.datawire.net/sd");
    }
	
    //------------------------------------------------------
    public static void main(String args[]) throws InterruptedException {
    	String mid = "99032159997";
    	String gcNumber = "7777060829226907";
    	String pin = "13473915";
    	String workingKeyId = "0312";
    	
    	ValueLinkEncryptor vle = new ValueLinkEncryptor();
    	String encryptedPin = vle.encryptPin(pin);
         
       	checkBalance(mid, gcNumber, encryptedPin, workingKeyId);
    	//redeem(mid, gcNumber, encryptedPin, workingKeyId, "10000");
    	//redeemVoid(mid, gcNumber, encryptedPin, workingKeyId, "10000");
		//System.out.println(generateUniqueClientRef());
    	
    }
    
    private static String generateUniqueClientRef() {
		Calendar cal = Calendar.getInstance();
		int i = cal.get(Calendar.HOUR_OF_DAY) + 65;
		int j = cal.get(Calendar.MINUTE) + 65;
		j = (j < 91 || (j > 96 && j < 123)) ? j : j - 6;
		String s = ""+ (char) i + (char)j +cal.get(Calendar.SECOND)+cal.get(Calendar.MILLISECOND);
		return s;
	}

	public static void checkBalance(String pMid, String pGCNumber, String pPin, String workingKeyId) {
        
        String balanceAPIPayload = 
        		"SV." + pMid +
        		FS+"40" + BALANCE_TRAN +
        		FS+"12" + EncryptionUtil.getDateString("HHmmss")+
        		FS+"13" + EncryptionUtil.getDateString("MMddyyyy") +
        		FS + "42" + pMid + "0000" +
        		FS+"EA30" + 
        		FS+"70" + pGCNumber + 
        		FS + "34" + pPin + 
        		FS + "C0" + "840"+
        		FS + "F3" + workingKeyId;

        try {
			invokeVL(balanceAPIPayload);
		} catch (VXNException e) {
			e.printStackTrace();
		}
	}
	
	public static void redeem(String pMid, String pGCNumber, String pPin, String workingKeyId, String amount) {
        // Check Balance Payload
        String redeemPayload = "SV." + pMid +FS+"40" + REDEEM_TRAN + FS + "04" + amount +FS+"12" + EncryptionUtil.getDateString("HHmmss")
        + FS +"13" + EncryptionUtil.getDateString("MMddyyyy") +FS+"70" + pGCNumber + FS + "34" + pPin + FS + "42" + pMid + "0000" 
        + FS+"EA30" + FS + "F3" + workingKeyId + FS + "C0" + "840" + FS+ "15X4443316f36313730";
        
        try {
			invokeVL(redeemPayload);
		} catch (VXNException e) {
			System.out.println("***********Exception in redeemVoid - Stack trace starts...");// NOPMD - this is test class
			e.printStackTrace();
			System.out.println("***********Exception in redeemVoid - Stack trace ends...");// NOPMD - this is test class
			timeReversal(redeemPayload);
		}
	}
	
	private static void timeReversal(String origPayload) {
		try {
			String timeReversalPayload = EncryptionUtil.getTimeReversalPayload(origPayload);
			invokeVL(timeReversalPayload);
		} catch (VXNException e) {
			e.printStackTrace();
		}
	}

	public static void redeemVoid(String pMid, String pGCNumber, String pPin, String workingKeyId, String amount) {
        // Check Balance Payload
        String redeemPayload = "SV." + pMid +FS+"40" + REDEEM_VOID_TRAN + FS + "04" + amount +FS+"12" + EncryptionUtil.getDateString("HHmmss")
        + FS +"13" + EncryptionUtil.getDateString("MMddyyyy") +FS+"70" + pGCNumber + FS + "34" + pPin + FS + "42" + pMid + "0000" 
        + FS+"EA30" + FS + "F3" + workingKeyId + FS + "C0" + "840";

        try {
			invokeVL(redeemPayload);
		} catch (VXNException e) {
			System.out.println("***********Exception in redeemVoid - Stack trace starts...");// NOPMD - this is test class
			e.printStackTrace();
			System.out.println("***********Exception in redeemVoid - Stack trace ends...");// NOPMD - this is test class
			timeReversal(redeemPayload);
		}
	}
	
	public static void invokeVL(String payloadStr) throws VXNException {
		System.out.println("Request Payload:" + payloadStr);// NOPMD - this is test class
		
        char payload[] = payloadStr.toCharArray();

        // This array is used to store response payload
        // returning by the transaction execution.
        char  respPayload[];

        // Get the instance of the VXN object.
        // This will do a service discovery.
        VXN vxn = VXN.getInstance(sdList,stDID,stMID,stTID,svcid,AppID);

        String ttttttt = generateUniqueClientRef();
        SimpleTransaction tr = vxn.newSimpleTransaction(ttttttt + version);

        // Set payload for the transaction object.
        tr.setPayload(payload);
        // Execute the transaction object.
        tr.executeXmlRequest();
        //System.out.println("Transaction Response Status Code:" + tr.getResponseStatusCode());
        // check the status and if it is "OK" then get the payload
        if(null != tr.getResponseStatusCode() && "OK".equals(tr.getResponseStatusCode())) {
        	// Get response payload.
            respPayload = tr.getPayload();
            //Expected response Payload for balance API
            // "SV DOT format";
            //System.out.println("Response Payload Reference:" + respPayload);
            System.out.println("Response Payload:" + new String(respPayload));// NOPMD - this is test class
            
            StringTokenizer strT = new StringTokenizer(new String(respPayload), new String (Character.toString(FS)));
            if (strT != null) {
            	while(strT.hasMoreTokens()) {
            		System.out.println(strT.nextToken());// NOPMD - this is test class
            	}
            }
        }
    }
}