package com.bbb.valuelink;

import java.util.List;
import java.util.StringTokenizer;

import net.datawire.vxn3.SimpleTransaction;
import net.datawire.vxn3.VXN;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import com.bbb.valuelink.encryption.EncryptionUtil;

/** 
 * Class to assign working key....
 */
@SuppressWarnings("unchecked")
public class AssignWorkingKeyTransaction {

	private static char FS = (char)0x1C;
    private final static String ASSIGN_MERCHANT_KEY_TRAN = "2010";
    private final static String stMID = "99032159997";
    private final static String stTID = "00000000651";
    private final static String stDID = "00010766123432271633";
    private final static String svcid = "104";
    private final static String AppID = "BEDBATHGIFTJAVA2";
    private static final String  module = AssignWorkingKeyTransaction.class.getName();
	private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClassName(module);
    
	@SuppressWarnings("rawtypes")
	private final static List sdList = new java.util.ArrayList(2);
    
    static {
	    sdList.add("https://staging1.datawire.net/sd");
	    sdList.add("https://staging2.datawire.net/sd");
    }

    //------------------------------------------------------
    public static void main(String args[]) {
    	//Usage - AssignWorkingKeyTransaction <mid>, <Working Key ID>, <Encrypted Merchant Working Key>
    	if (args == null || args.length != 4) {
    		logger.logDebug("Usage - AssignWorkingKeyTransaction <mid>, <tid>, <Working Key ID>, <Encrypted Merchant Working Key>");
    		//return;
    		//String mid = "99032139997";//BuyBuyBaby
    		//String mid = "99032159997";//BedBathUS
    		String mid = "99032149997";//BedBathCanada
    		String workKeyID = "0312";//keeping it MMYY format, as it has to be numeric.
    		String encryptedWorkingKey = "37d7329c35811f9a527c64e9ed13632a2427256846651ac4f84b088ac37aa7c6e5f4c5ef8f93c689";
    		assignWorkingKey(mid, workKeyID, encryptedWorkingKey);
    	} else {
    		String mid = args[0];
    		String workKeyID = args[1];
    		String encryptedWorkingKey = args[2];
    		assignWorkingKey(mid, workKeyID, encryptedWorkingKey);
    	}
    }

    
	public static void assignWorkingKey(final String mid, final String workingKeyId, final String encryptedWorkingKey) {
        String version = "V100000";
       
        //2010 - Assign Merchant key Transaction Payload
        String strPayload = "SV." + mid + FS+"40" + ASSIGN_MERCHANT_KEY_TRAN +FS+"12" + EncryptionUtil.getDateString("HHmmss")+FS
        	+"13" + EncryptionUtil.getDateString("MMDDyyyy") +FS+ "42" + mid + "0000" + FS
        	+ "63" + encryptedWorkingKey + FS+"EA30" + FS + "F3" + workingKeyId;
        
        logger.logDebug("Request Payload:" + strPayload);

        char payload[] = strPayload.toCharArray();

        // This array is used to store response payload
        // returning by the tranasction execution.
        char  respPayload[];

        try {

            // Get the instance of the VXN object.
            // This will do a service discovery.
            VXN vxn = VXN.getInstance(sdList,stDID,stMID,stTID,svcid,AppID);

            String ttttttt = "1048331";
            SimpleTransaction tr = vxn.newSimpleTransaction(ttttttt + version);

            // Set payload for the transaction object.
            tr.setPayload(payload);
            //System.out.println("Request Payload:" + new String(payload));
            // Execute the transaction object.
            tr.executeXmlRequest();
            //System.out.println("Transaction Response Status Code:" + tr.getResponseStatusCode());
            // check the status and if it is "OK" then get the payload
            if(null != tr.getResponseStatusCode() && "OK".equals(tr.getResponseStatusCode())) {
            	// Get response payload.
                respPayload = tr.getPayload();
                //Expected response Payload for balance API
                // "SV DOT format";
                logger.logDebug("Response Payload Reference:" + respPayload);
                logger.logDebug("Response Payload:" + new String(respPayload));
                
                StringTokenizer strT = new StringTokenizer(new String(respPayload), new String (Character.toString(FS)));
                if (strT != null) {
                	while(strT.hasMoreTokens()) {
                		logger.logDebug(strT.nextToken());
                	}
                }
            }
        } catch (final net.datawire.vxn3.VXNException ex) {
            ex.printStackTrace();
        }
    }
}