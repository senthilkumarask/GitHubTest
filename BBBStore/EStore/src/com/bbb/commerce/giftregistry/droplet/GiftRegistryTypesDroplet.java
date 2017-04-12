package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

// TODO: Auto-generated Javadoc
/**
 * The class is the extension of the ATG DynamoServlet. The class is responsible
 * for rendering the content for Gift Registry Flyouts in reg_flyout.jsp. The
 * class presents content based on whether the user is authenticated and number
 * of registries the user owns
 * 
 * @author sku134
 * 
 */
public class GiftRegistryTypesDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	public static final String SITE_ID = "siteId";

	/**
	 * Fetch Registry Types for the dropdown to select a registry type and
	 * create a registry.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws ServletException
	 *             the servlet exception
	 */
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws IOException,
			ServletException {
	    BBBPerformanceMonitor.start("GiftRegistryTypesDroplet", "service");
		logDebug(" GiftRegistryTypesDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		String siteId = pRequest.getParameter(SITE_ID);

		try {
			List<RegistryTypeVO> registryTypesList = getGiftRegistryManager()
					.fetchRegistryTypes(siteId);
			SortedMap<Integer,RegistryTypeVO> regTypeMap = new TreeMap<Integer,RegistryTypeVO>();
			for(RegistryTypeVO registryTypeVO:registryTypesList){
				regTypeMap.put(registryTypeVO.getRegistryIndex(),registryTypeVO);
			}
			List<RegistryTypeVO> sortedRegTypeList =  new ArrayList<RegistryTypeVO>();
			Set<Integer> keySet = regTypeMap.keySet();
			for(Integer key:keySet){
				sortedRegTypeList.add(regTypeMap.get(key));
			}
			pRequest.setParameter("registryTypes", sortedRegTypeList);

			logDebug("set output to the display page");
			pRequest.serviceLocalParameter("output", pRequest, pResponse);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "BBBBusinessException from service of GiftRegistryTypeDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_biz_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (BBBSystemException e) {

			logError(LogMessageFormatter.formatMessage(pRequest, "BBBSystemException from service of GiftRegistryTypeDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_sys_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		} catch (RepositoryException e) {
			logError(LogMessageFormatter.formatMessage(pRequest, "Repository Exception from service of GiftRegistryTypesDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1001), e);
			pRequest.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_repo_exception");
			pRequest.serviceLocalParameter(OPARAM_ERROR, pRequest, pResponse);
		}
		logDebug(" GiftRegistryTypesDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - end");
		BBBPerformanceMonitor.end("GiftRegistryTypesDroplet", "service");
	}

	/**
	 * Gets the gift registry manager.
	 * 
	 * @return the giftRegistryManager
	 */
	public GiftRegistryManager getGiftRegistryManager() {
		return mGiftRegistryManager;
	}

	/**
	 * Sets the gift registry manager.
	 * 
	 * @param pGiftRegistryManager
	 *            the giftRegistryManager to set
	 */
	public void setGiftRegistryManager(GiftRegistryManager pGiftRegistryManager) {
		mGiftRegistryManager = pGiftRegistryManager;
	}

}
