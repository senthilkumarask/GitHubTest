package com.bbb.rest.giftregistry;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import atg.multisite.SiteContext;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.giftregistry.bean.GiftRegSessionBean;
import com.bbb.commerce.giftregistry.droplet.GiftRegistryPaginationDroplet;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 * This class is used to search registry information for REST module.
 * 
 * @author ssha53
 * 
 */
public class SearchRegistryByCriteriaAPI extends BBBGenericService {

	/** Constants for string literal site id. */
	private static final String SITE_ID = "siteId";

	public static final String OUTPUT_ERROR_MSG = "errorMsg";
	
	private final String FIRST_NAME = "firstName";
	
	private final String LAST_NAME = "lastName";
	
	private final String REGISTRY_ID = "registryId";
	
	private final String EMAIL = "email";
	
	private static final String Gift_REG_SESSION_BEAN = "/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean";
	
	/** The Registry info service name. */
	private String mRegistrySearchServiceName;

	/** The GiftRegistryPaginationDroplet instance */
	private GiftRegistryPaginationDroplet giftRegistryPaginationDroplet;
	
	/** The Catalog tools. */
	private BBBCatalogTools mCatalogTools;
	
	private SiteContext siteContext;
	
	/**
	 * Get Registry information from the droplet
	 * 
	 * @return RegistrySummaryVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public List searchRegistryByCriteria(Map<String,String> inputMap)
			throws BBBBusinessException, BBBSystemException {
		List<RegistrySummaryVO> pRegistryVO = null;

		logDebug(" RestSearchRegistryByCriteria searchRegistryByCriteria(Map<String,String> inputMap) - start");
		
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();

		Map<String,String> inputDataMap = inputMap;

		final String pSiteId = getSiteContext().getSite().getId();
		pRequest.setParameter(BBBGiftRegistryConstants.PAGE_NO, inputDataMap.get("pageNo"));
		pRequest.setParameter(BBBGiftRegistryConstants.PER_PAGE, inputDataMap.get("perPage"));
		pRequest.setParameter(BBBGiftRegistryConstants.SORT_PASS_STRING, inputDataMap.get("sortPassString"));
		pRequest.setParameter(SITE_ID, pSiteId);	
		
		RegistrySearchVO registrySearchVO = new RegistrySearchVO();
		
		
		if(inputDataMap.get(FIRST_NAME) != null && !"".equals(inputDataMap.get(FIRST_NAME))){
			registrySearchVO.setFirstName(inputDataMap.get(FIRST_NAME));
			registrySearchVO.setLastName(inputDataMap.get(LAST_NAME));
		}else if(inputDataMap.get(REGISTRY_ID) != null && !"".equals(inputDataMap.get(REGISTRY_ID))){
			registrySearchVO.setRegistryId(inputDataMap.get(REGISTRY_ID));
		}else if(inputDataMap.get(EMAIL) != null && !"".equals(inputDataMap.get(EMAIL))){
			registrySearchVO.setEmail(inputDataMap.get(EMAIL));
		}else{
			 throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_BUSSINESS_EXCEPTION, "Business Exception is raised to required fields’ validation failure while Search Registry by Criteria.");
		}
		
		
		registrySearchVO.setServiceName(getRegistrySearchServiceName());
		if (null != getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, pSiteId)) {
					registrySearchVO.setSiteId(getCatalogTools()
									.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,pSiteId).get(0));
				}
		registrySearchVO.setUserToken(getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,
						BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		registrySearchVO.setReturnLeagacyRegistries(false);
		registrySearchVO.setGiftGiver(true);
		//registrySearchVO.setFilterRegistriesInProfile(false);
		GiftRegSessionBean giftRegSessionBean = (GiftRegSessionBean) pRequest.resolveName(Gift_REG_SESSION_BEAN);
		
		giftRegSessionBean.setRequestVO(registrySearchVO);

		String errorMsg=null;
		try {
			getGiftRegistryPaginationDroplet().service(pRequest, response);
			pRegistryVO = (List<RegistrySummaryVO>) pRequest
					.getObjectParameter("registrySummaryResultList");
			 errorMsg=(String)pRequest
					.getObjectParameter(OUTPUT_ERROR_MSG); 
		 if(errorMsg!=null)
			{
			    logError(errorMsg);
				throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_SYSTEM_EXCEPTION, "System Exception raised while Search Registry by Criteria.");
			}
		 
		} catch (IOException ioException) {
			logError(ioException);
			throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_IO_EXCEPTION, "IO Exception raised while Search Registry by Criteria.");
		} catch (ServletException servletException) {
			logError(servletException);
		    throw new BBBSystemException(BBBCatalogErrorCodes.REGISTRY_SEARCH_EXCEPTION_SERVLET_EXCEPTION, "Servlet Exception raised while Search Registry by Criteria.");
		}
		
		logDebug("RestSearchRegistryByCriteria searchRegistryByCriteria(Map<String,String> inputMap) - ends");
		
		return pRegistryVO;

	}

	/**
	 * @return the giftRegistryPaginationDroplet
	 */
	public GiftRegistryPaginationDroplet getGiftRegistryPaginationDroplet() {
		return giftRegistryPaginationDroplet;
	}

	/**
	 * @param giftRegistryPaginationDroplet the giftRegistryPaginationDroplet to set
	 */
	public void setGiftRegistryPaginationDroplet(
			GiftRegistryPaginationDroplet giftRegistryPaginationDroplet) {
		this.giftRegistryPaginationDroplet = giftRegistryPaginationDroplet;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		mCatalogTools = catalogTools;
	}

	/**
	 * @return the registrySearchServiceName
	 */
	public String getRegistrySearchServiceName() {
		return mRegistrySearchServiceName;
	}

	/**
	 * @param registrySearchServiceName the registrySearchServiceName to set
	 */
	public void setRegistrySearchServiceName(String registrySearchServiceName) {
		mRegistrySearchServiceName = registrySearchServiceName;
	}

	public SiteContext getSiteContext() {
		return siteContext;
	}

	public void setSiteContext(SiteContext siteContext) {
		this.siteContext = siteContext;
	}

}
