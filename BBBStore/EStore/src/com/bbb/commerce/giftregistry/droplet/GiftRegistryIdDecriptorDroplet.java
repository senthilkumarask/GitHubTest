//--------------------------------------------------------------------------------
//Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
//
//Reproduction or use of this file without explicit 
//written consent is prohibited.
//
//Created by: Prashanth Bhoomula
//
//Created on: 06-June-2013
//--------------------------------------------------------------------------------

package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
 * The Class GiftRegistryIdDecriptorDroplet.
 */
public class GiftRegistryIdDecriptorDroplet extends BBBPresentationDroplet {
	
	public static final String INPUT_REGISTRY_ID = "registryEncriptedId";
	public static final String OUTPUT_REGISTRY_ID = "registryId";
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response) throws ServletException,
			IOException {

		logDebug(" GiftRegistryIdDecriptorDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		String encStr = request.getParameter(INPUT_REGISTRY_ID);
		long registryDecId =0;
		if(!StringUtils.isEmpty(encStr)) {
			if(encStr.startsWith("%")){
				encStr = URLDecoder.decode(encStr, "UTF-8");
			}
			long registryEncId = Long.parseLong(encStr);
			if(registryEncId<0) {
				registryEncId = registryEncId*-1;
			}
			long key = registryEncId^0xBEB0BA;
			registryDecId = ((key&0x7C000000)>>26) + ((key<<5)&0x7FFFFFFF);
			
		}
		request.setParameter(OUTPUT_REGISTRY_ID, registryDecId);
		request.serviceLocalParameter(OPARAM_OUTPUT, request, response);
		logDebug(" GiftRegistryIdDecriptorDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
	}
}
