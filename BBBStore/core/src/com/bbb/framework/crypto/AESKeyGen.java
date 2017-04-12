/*
 *
 * File  : AESKeyGen.java
 * Project:     BBB
 */
package com.bbb.framework.crypto;



/**
 * 
 */
public final class AESKeyGen  {
	//private Constructor
	private AESKeyGen(){
		
	}
    
    public static String asHex(final byte[] pByteArray) {
		final StringBuffer strbuf = new StringBuffer(pByteArray.length * 2);
		int i;
		for (i = 0; i < pByteArray.length; i++) {
		    if ((pByteArray[i] & 0xff) < 0x10) {
			strbuf.append("0");
		    }
		    strbuf.append(Long.toString(pByteArray[i] & 0xff, 16));
		}
		return strbuf.toString();
    }
    
    public static void main(final String[] pArgs) {/*

    	try {
    	    // Get the KeyGenerator
    	    final KeyGenerator kgen = KeyGenerator.getInstance("AES");
    	    kgen.init(128); // 192 and 128 bits may be available
    	    // Generate the secret key specs.
    	    final SecretKey skey = kgen.generateKey();
    	    final byte[] raw = skey.getEncoded();
    	    String base64Key = Base64.encodeToString(raw);


    	} catch (final NoSuchAlgorithmException e) {
    		   ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClass(AESKeyGen.class);
    		   logger.logError("Error while encryption "+ e.getMessage());
    	}
        */}

}
