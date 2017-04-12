package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.tool.GiftRegistryTools;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;


/**
 * This droplet is used to get print at home card details from repository.
 * 
 * This will be used in Print At Home landing and detail pages.
 */
public class PrintAtHomeDroplet extends BBBPresentationDroplet {
	
	/**
	 * to hold siteId String
	 */
	
	private String siteId ="siteId";
	
	/**
	 * to hold repoItemsKey String
	 */
	
	private String repoItemsKey = "repoItems";
	
	/**
	 * to hold output String
	 */
	
	private String outPut = "output";
	
	/**
	 * to hold id String
	 */
	private String repoId = "id";
	
	/**
	 * to hold Gift Registry Tool
	 */
	private GiftRegistryTools mGiftRegistryTools;
	
	/**
	 * @return mGiftRegistryTools
	 */
	public GiftRegistryTools getGiftRegistryTools() {
		return mGiftRegistryTools;
	}
	
	/**
	 * @param mGiftRegistryTools
	 */
	public void setGiftRegistryTools(GiftRegistryTools pGiftRegistryTools) {
		this.mGiftRegistryTools = pGiftRegistryTools;
	}
	
	/**
	 * To get list of available templates for card design specific to a site and also 
	 *   used to get thumbnail template details based on repository id.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws ServletException
	 *             , IOException
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		BBBPerformanceMonitor.start( PrintAtHomeDroplet.class.getName() + " : " + "service");
		
		logDebug("PrintAtHomeDroplet : service : START");
	
		String siteId = (String)pRequest.getParameter(this.siteId);
		String repositoryId = (String)pRequest.getParameter(repoId);
		
		// fetch card design template based on site Id.
		if(siteId!=null){
		RepositoryItem [] repoItems = getGiftRegistryTools().getPrintAtHomeCardTemplates(siteId);
		if(repoItems!=null){
			if(repoItems.length>1){
				pRequest.setParameter(repoItemsKey, repoItems[0]);
			}else{
		       pRequest.setParameter(repoItemsKey, repoItems);
			}
			pRequest.serviceLocalParameter(outPut, pRequest, pResponse);
		}
		}else if(repositoryId!=null){  // fetch thumb nail image details
			RepositoryItem [] repoItems = getGiftRegistryTools().getThumbnailTemplateDetails(repositoryId);
			 pRequest.setParameter(repoItemsKey, repoItems);
			 pRequest.serviceLocalParameter(outPut, pRequest, pResponse);
		}else{
			logError(LogMessageFormatter.formatMessage(pRequest,	"BBBBusinessException from SERVICE of PrintAtHomeDroplet.required site id or " +
					"thumbnail repository id parameter"));
		}
	   logDebug("PrintAtHomeDroplet : service : Exit");
		BBBPerformanceMonitor.end( PrintAtHomeDroplet.class.getName() + " : " + "service");
	}

}
