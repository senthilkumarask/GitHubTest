package com.bbb.cms.droplet;


import java.util.Iterator;
import java.util.List;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;

/**
 * This Droplet would interact with config repository to get the video ids.
 * @author ajosh8
 *
 */

public class GuidesVideosDroplet extends BBBDynamoServlet {

	private static final String ERROR_MSG = "errorMsg";
	private static final String ERROR = "error";
	private static final String OUTPUT = "output";
	private static final String FINAL_VIDEO_LIST = "finalVideoList";
	private BBBCatalogTools catalogTools;
	private  String configKey;


	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}


	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}


	public String getConfigKey() {
		return configKey;
	}


	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}

/**
 * This would interact with catalog api to get video id from the config repository.
 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		StringBuilder finalVideoList=new StringBuilder("");
		String updatedVideosList=null;
		try {
			List<String> videoList = (List<String>)getCatalogTools().getContentCatalogConfigration(getConfigKey());
			 
			if (null!=videoList && videoList.size()>0){
			Iterator<String> it=videoList.iterator();
			while(it.hasNext()){
				finalVideoList=finalVideoList.append(it.next()+',');
			}

			if(finalVideoList.length()>0) {
				updatedVideosList=finalVideoList.substring(0, finalVideoList.length()-1);
				request.setParameter(FINAL_VIDEO_LIST, updatedVideosList); 
			}
			
			


			request.serviceParameter(OUTPUT, request, response);
			}
			else{
				request.serviceParameter("empty", request, response);
			 
			}
			

		}catch (BBBBusinessException e) {
			
		     logError(LogMessageFormatter.formatMessage(request, "GuidesVideoDroplet|service()|BBBBusinessException","catalog_1036"),e);
		     
			request.setParameter(ERROR_MSG,"err_guides_config_exception");
			request.serviceLocalParameter(ERROR, request,
					response);
		} catch (BBBSystemException e) {
			 
		    logError(LogMessageFormatter.formatMessage(request, "GuidesVideoDroplet|service()|BBBSystemException","catalog_1037"),e);
		      
			request.setParameter(ERROR_MSG,"err_guides_config_exception");
			request.serviceLocalParameter(ERROR, request,
					response);
		} 

	}

}










