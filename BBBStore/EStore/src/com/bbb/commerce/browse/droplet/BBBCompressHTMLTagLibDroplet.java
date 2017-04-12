package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

public class BBBCompressHTMLTagLibDroplet extends BBBDynamoServlet {

	private BBBCatalogTools catalogTools;
	private static final String USER_AGENT = "User-Agent";
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}
	
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse)
					throws ServletException, IOException
					{

		logDebug("BBBCompressHTMLTagLibServlet.service() method called");
		
		
		BBBSessionBean sessionBean =(BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
	
		logDebug("Compress HTML Tag Lib flag Value ["+sessionBean.getCompressHtmlTagLib()+"]");
		
		
		try {
			List<String>  compressHTMLTagLibUserAgent = this.getCatalogTools().getAllValuesForKey(BBBCatalogConstants.CONTENT_CATALOG_KEYS,
					BBBCoreConstants.COMPRESS_HTML_TAGLIB_USER_AGENTS);
			
			logDebug("Config key value for User Agents for which HTML to be compressed: "+compressHTMLTagLibUserAgent);
			
			sessionBean.setCompressHtmlTagLib("false");
			if(!BBBUtility.isListEmpty(compressHTMLTagLibUserAgent)){
				boolean isCompressed = false;
				for(String pUserAgent: compressHTMLTagLibUserAgent){
					if(BBBUtility.isStringPatternValid(pUserAgent, pRequest.getHeader(USER_AGENT))){
						if(isCompressed){
							break;
						}
						sessionBean.setCompressHtmlTagLib("true");
						isCompressed = true;
					}
				}
			}
			else{
				sessionBean.setCompressHtmlTagLib("true");
			}
		} catch (BBBSystemException e) {
			logError("Exception occurred while retrieving config keys", e);
			sessionBean.setCompressHtmlTagLib("true");
		} catch (BBBBusinessException e) {
			logError("Exception occurred while retrieving config keys", e);
			sessionBean.setCompressHtmlTagLib("true");
		} catch(Exception e){
			logError("Exception occurred while retrieving config keys", e);
			sessionBean.setCompressHtmlTagLib("true");
		}
	}
}