/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBEncryptionDroplet.java
 *
 *  DESCRIPTION: BBBEncryptionDroplet encrypts and decrypts the given string 
 *  			 using DES algorithm.
 *  			 
 *  HISTORY:
 *  02/16/2012 Initial version
 *	02/20/2012 Moved constants to BBBAccountConstants.java file
 *
 */

package com.bbb.account.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.account.BBBDesEncryptionTools;
import static com.bbb.constants.BBBAccountConstants.*;

public class BBBEncryptionDroplet extends BBBDynamoServlet {
	
	private BBBDesEncryptionTools mBBBDesEncryptionTools;


	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		final String operation= pRequest.getParameter(PARAM_ENC_OPERATION);		 
		final String inputvalue= pRequest.getParameter(PARAM_ENC_IN_VALUE);
		String outputValue=null;
			logDebug("operation is:"+operation);
			logDebug("input value is:"+inputvalue);
		if(OPR_ENCRYPT.equalsIgnoreCase(operation)){
			outputValue = getBBBDesEncryptionTools().encrypt(inputvalue);
		}
		else if(OPR_DECRYPT.equalsIgnoreCase(operation)){
			outputValue = getBBBDesEncryptionTools().decrypt(inputvalue);
		}else{
				logDebug("Error. operation is invalid.");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
			return;
		}
			logDebug("output value is:"+outputValue);
		pRequest.setParameter(PARAM_ENC_OUT_VALUE,outputValue);
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);
	}

	/**
	 * @return the mBBBDesEncryptionTools
	 */
	public BBBDesEncryptionTools getBBBDesEncryptionTools() {
		return mBBBDesEncryptionTools;
	}

	/**
	 * @param mBBBDesEncryptionTools the mBBBDesEncryptionTools to set
	 */
	public void setBBBDesEncryptionTools(
			BBBDesEncryptionTools pBBBDesEncryptionTools) {
		this.mBBBDesEncryptionTools = pBBBDesEncryptionTools;
	}

}
