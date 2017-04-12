package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * This droplet is being used in pagination. This takes the response returned by
 * the websevice and gives to the jsp page, If it is the first call it dose not
 * call webservice, it takes result from the request.
 * 
 * @author apanw1
 * 
 */
public class GiftRegistryPaginationDroplet extends BBBPresentationDroplet {

	/** The Gift registry manager. */
	private GiftRegistryManager mGiftRegistryManager;

	/** The Constant DEFAULT_SORT_ORDER. */
	private static final String DEFAULT_SORT_ORDER = "ASCE";
	
	private static final String SITE_ID = "siteId";


	/**
	 * Takes registrySummaryVOList from request or webservice and sets required
	 * result based on parameters like SortSeq, startindx, perpage.
	 * 
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 * @throws ServletException
	 *             the servlet exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {
		logDebug(" GiftRegistryPaginationDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");
		  BBBPerformanceMonitor.start("GiftRegistryPaginationDroplet", "SearchWSPagination");
		try {

			String sortPassString = request
					.getParameter(BBBGiftRegistryConstants.SORT_PASS_STRING);
			String sortOrder = request
					.getParameter(BBBGiftRegistryConstants.SORTING_ORDER);
			GiftRegSessionBean registrySessionBean = (GiftRegSessionBean) request
					.resolveName("/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean");
			String pSiteId = request.getParameter(SITE_ID);
			String mx_registry_pagination = request
					.getParameter("mx_registry_pagination");
			
			logDebug("CLS=[GiftRegistryPaginationDroplet]/MSG=[Service method starts]");

			// Default values
			Integer pageNo = BBBGiftRegistryConstants.DEFAULT_TAB;
			Integer perPage = BBBGiftRegistryConstants.DEFAULT_PAGE_SIZE;

			List<RegistrySummaryVO> registrySummaryVOList = null;
			
			RegistrySearchVO registrySearchVO = (RegistrySearchVO) registrySessionBean.getRequestVO();

			if (registrySearchVO == null) {
				logError("registrySearchVO is null");
				request.setParameter(OUTPUT_ERROR_MSG,
						"err_regsearch_biz_exception");
				request.serviceParameter(OPARAM_ERROR, request, response);
				return;
			}

			if (request.getLocalParameter(BBBGiftRegistryConstants.PAGE_NO) != null) {
				pageNo = Integer.parseInt((String) request
						.getLocalParameter(BBBGiftRegistryConstants.PAGE_NO));
			}

			if (request.getLocalParameter(BBBGiftRegistryConstants.PER_PAGE) != null) {
				perPage = Integer.parseInt((String) request
						.getLocalParameter(BBBGiftRegistryConstants.PER_PAGE));
			}
			if (request.getLocalParameter("registryId") != null) {
				Object registryId = request.getLocalParameter("registryId");
				registrySearchVO.setRegistryId(registryId.toString());
			}

			int forStart = ((pageNo - 1) * perPage);

			registrySearchVO.setStartIdx(forStart);
			registrySearchVO.setBlkSize(perPage);

			if(mx_registry_pagination!=null && mx_registry_pagination.equals("mxregistry")){
				if (sortOrder != null) {
					registrySearchVO.setSortSeqOrder(sortOrder);
				} else {
					registrySearchVO.setSortSeqOrder(DEFAULT_SORT_ORDER);
				}

				if (sortPassString != null) {
					registrySearchVO.setSortSeq(sortPassString);
				} else {
					registrySearchVO.setSortSeq("NAME");
				}
			}

			// Call to web service
			RegSearchResVO searchResponseVO = getGiftRegistryManager()
					.searchRegistries(registrySearchVO, pSiteId);

			int totalResultSize = 0;

			if (searchResponseVO != null) {
				totalResultSize = searchResponseVO.getTotEntries();
				registrySummaryVOList = searchResponseVO
						.getListRegistrySummaryVO();
			}

			if (totalResultSize == 0) {
				request.serviceParameter(OPARAM_EMPTY, request, response);

			} else {

				request.setParameter(
						BBBGiftRegistryConstants.TOTAL_RESULT_COUNT,
						totalResultSize);
				request.setParameter(
						BBBGiftRegistryConstants.REGiSTRY_SEARCH_LIST,
						registrySummaryVOList);
				request.serviceParameter(OPARAM_OUTPUT, request, response);
			}

		} 
		
////SAP-345 start
        
        catch(SocketTimeoutException e)
        {
        	logError(LogMessageFormatter.formatMessage(request, "SocketTimeoutException from service of GiftRegistryPaginationDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004));

			request.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_biz_exception");
			request.serviceLocalParameter(OPARAM_ERROR, request, response);
        }
        
        //////SAP-345 end
		
		catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(request, "BBBBusinessException from service of GiftRegistryPaginationDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1004),e);

			request.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_biz_exception");
			request.serviceLocalParameter(OPARAM_ERROR, request, response);

		} catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(request, "BBBSystemException from service of GiftRegistryPaginationDroplet", BBBCoreErrorConstants.GIFTREGISTRY_ERROR_1005),e);
			request.setParameter(OUTPUT_ERROR_MSG,
					"err_regsearch_sys_exception");
			request.serviceLocalParameter(OPARAM_ERROR, request, response);
		}

		logDebug("CLS=[GiftRegistryPaginationDroplet]/MSG=[Service method exits]");
		BBBPerformanceMonitor.end("GiftRegistryPaginationDroplet", "SearchWSPagination");
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
