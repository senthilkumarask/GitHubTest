package com.bbb.valuelink;

import net.datawire.vxn3.SelfRegistration;
import net.datawire.vxn3.VXNException;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;

import com.bbb.valuelink.encryption.EncryptionUtil;

public class BBBAppRegistration {
	private static final String  module = BBBAppRegistration.class.getName();
	private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClassName(module);
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		
        String mid = null;
        String tid = null;
        String svcid = null;
        String appID = null;

        java.util.List sdUrls = new java.util.ArrayList();
        logger.logDebug("Time-" + EncryptionUtil.getDateString("MM/DD/yyyy HH:mm:ss.sss"));
    
		if (args != null) {
			if (args.length >= 0) {
		        mid = "99032159997"; //Configured value, provided by FD
		        tid = "00000000651"; //Configured value, provided by FD
		        svcid = "104"; //Configured value, provided by FD
		        appID = "BEDBATHGIFTJAVA2";
		        
		        String mode = "-dev"; //args[0];
				if("-dev".equalsIgnoreCase(mode)) {
					sdUrls.add("https://staging1.datawire.net/sd");
			        sdUrls.add("https://staging2.datawire.net/sd");
				} else if("-prod".equalsIgnoreCase(mode)) {
			        sdUrls.add("https://vxn.datawire.net/sd"); 
			        sdUrls.add("https://vxn1.datawire.net/sd");
				} else {
					usage();
		        	return;
				}
			}
			
			if (args.length > 1) {
				mid = args[1];
			}

			if (args.length > 2) {
				tid = args[2];
			}
		      
			if (args.length > 3) {
				svcid = args[3];
			}
			
			if (args.length > 4) {
				appID = args[4];
			}
			
			if (args.length > 5) {
				String allUrls = args[5];
				if(!isEmpty(allUrls)) {
					for(String s : allUrls.split(",")) {
			        	sdUrls.add(s);
			        }
				}
			}

	        if (isEmpty(mid) ||  isEmpty(tid) || isEmpty(svcid) || isEmpty(appID)) {
	        	usage();
	        	return;
	        }
		} else {
			usage();
			return;
		}

        
        //Production URLs
        //sdUrls.add("https://vxn.datawire.net/sd"); 
        //sdUrls.add("https://vxn1.datawire.net/sd");


		try {
			SelfRegistration srs = new SelfRegistration(sdUrls, mid, tid, svcid, appID);
			srs.setMaxRegisterAttempts(3);
            srs.setRegisterAttemptsWaitMilliseconds(30000);
            
            // Call registerMerchant(). This will return a DID for the application to use.
            String did = srs.registerMerchant();
            
            logger.logDebug("NOTE this DID:" + did + ":");
            
            // Call activateMerchant().
            srs.activateMerchant();
            
		} catch (VXNException e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
			logger.logError(e.getMessage());
		}
	}

	private static boolean isEmpty(String str) {
		if (str == null || str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	private static void usage() {
		logger.logDebug("java BBBAppRegistration {-dev|-prod} [{MID} {TID} {Service ID} {Application ID} {Secure Transaport URLs comma separated}]");		
	}
}
