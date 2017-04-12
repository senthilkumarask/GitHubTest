/**
 * 
 */
package com.bbb.commerce.droplet;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryView;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

public class TBSItemExclusionDroplet extends BBBDynamoServlet {
	
	public static final String CONST_EXCLUSION_ITEM = "cartItemExclusion";
	public static final String PROP_SKUID = "skuId";
	public static final String PROP_REASON_CODE = "reasonCode";
	public static final String PROP_START_DT = "startDate";
	public static final String PROP_END_DT = "endDate";
	public static final String PROP_SITE_FLAG = "siteFlag";
	public static final String PARAM_SITEID="siteId";
	public static final String VALID_EXCLUDED_SKU="validItemExcludedSku";
	public static final String PROP_SKUID_LIST = "upcItems";
	public static final String CA_DISABLED = "caDisabled";
	
	private List ecomCAInclusionList;
	
	private BBBCatalogTools catalogTools;
	Repository itemExclusionRepository;
	
	/**
	 * @return the itemExclusionRepository
	 */
	public Repository getItemExclusionRepository() {
		return itemExclusionRepository;
	}

	/**
	 * @param pItemExclusionRepository the itemExclusionRepository to set
	 */
	public void setItemExclusionRepository(Repository pItemExclusionRepository) {
		itemExclusionRepository = pItemExclusionRepository;
	}
	
   /** @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws ServletException 
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
			vlogDebug("TBSItemExclusionDroplet :: service() method :: START");
			
			String skuId = pRequest.getParameter(PROP_SKUID);
			String siteId = pRequest.getParameter(PARAM_SITEID);
			String upcItems = pRequest.getParameter(PROP_SKUID_LIST);
			boolean isItemExcluded = Boolean.FALSE;
			boolean caDisabled = Boolean.FALSE;
			RepositoryItem skuRepositoryItem = null;
			
			if(!StringUtils.isEmpty(skuId) && !StringUtils.isEmpty(siteId)){
				vlogDebug("TBSItemExclusionDroplet :: service() :: skuId ::"+skuId+"& siteId ::"+siteId);
				if(isItemExclusion(skuId,siteId,pRequest, pResponse)) {
					isItemExcluded = Boolean.TRUE;
				}
			}
			
			if (!StringUtils.isEmpty(upcItems)) {
				if (upcItems.contains(BBBCoreConstants.COMMA)) {
					vlogDebug("TBSItemExclusionDroplet :: service() :: upcItems ::"+upcItems+"& siteId ::"+siteId);
					String[] splittedSkuIds = upcItems.split(BBBCoreConstants.COMMA);
					for (String skuUpcId : splittedSkuIds) {
						skuUpcId = skuUpcId.trim();
						if(skuUpcId.startsWith("0")){
							skuUpcId = skuUpcId.substring(1);
						}
						//Fetch SkuID for UPS Code
						skuRepositoryItem = getCatalogTools().getSKUForUPCSearch(skuUpcId);
						if(skuRepositoryItem == null || isItemExclusion(skuRepositoryItem.getRepositoryId(),siteId,pRequest, pResponse)) {
							isItemExcluded = Boolean.TRUE;
							break;
						}
					}
				} else {
						upcItems = upcItems.trim();
						if(upcItems.startsWith("0")){
							upcItems = upcItems.substring(1);
						}
						//Fetch SkuID for UPS Code
						skuRepositoryItem = getCatalogTools().getSKUForUPCSearch(upcItems);
						if(skuRepositoryItem == null || isItemExclusion(skuRepositoryItem.getRepositoryId(),siteId,pRequest, pResponse)) {
							isItemExcluded = Boolean.TRUE;
						}
				}
			}
			
			boolean exceptionOccured = false;
			
			try {
				if (!isItemExcluded && siteId.equals(TBSConstants.SITE_TBS_BAB_CA)) {
					if (BBBUtility.isNotEmpty(upcItems)) {
						caDisabled = checkSKUCADisabled(upcItems);
					} else if (BBBUtility.isNotEmpty(skuId)) {
						caDisabled = checkSKUCADisabled(skuId);
					}
				}	
			} catch (BBBSystemException bbbSystemException) {
				logError("System exception occured while querying sku item repository ::", bbbSystemException);
				pRequest.setParameter(BBBCoreConstants.ERRORMESSAGE, bbbSystemException.getErrorCode() + bbbSystemException.getMessage());
				pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
				caDisabled = true;
				exceptionOccured = true;
			} catch (BBBBusinessException bbbBusinessException) {
				vlogError("Business exception occured while querying sku item repository ::", bbbBusinessException);
				pRequest.setParameter(BBBCoreConstants.ERRORMESSAGE, bbbBusinessException.getErrorCode() + bbbBusinessException.getMessage());
				pRequest.serviceLocalParameter(TBSConstants.ERROR, pRequest, pResponse);
				caDisabled = true;
				exceptionOccured = true;
			}
			
			vlogDebug("TBSItemExclusionDroplet :: service() :: inputted invalid skuId ::"+skuId+" & siteId ::"+siteId);
			pRequest.setParameter(VALID_EXCLUDED_SKU, isItemExcluded);
			pRequest.setParameter(CA_DISABLED, caDisabled);
			if (!exceptionOccured)
				pRequest.serviceLocalParameter(TBSConstants.OUTPUT, pRequest, pResponse);
			vlogDebug("TBSItemExclusionDroplet :: service() method :: END");
	}

	private boolean checkSKUCADisabled(String upcItems) 
			throws BBBBusinessException, BBBSystemException {
		logDebug("TBSItemExclusionDroplet :: checkSKUCADisabled() method :: START");
		boolean caDisabled = Boolean.FALSE;
		if (upcItems.contains(BBBCoreConstants.COMMA)) {
			vlogDebug("TBSItemExclusionDroplet :: checkSKUCADisabled() :: upcItems ::"+upcItems);
			String[] splittedSkuIds = upcItems.split(BBBCoreConstants.COMMA);
			if(isItemCADisabled(splittedSkuIds))
					caDisabled = Boolean.TRUE;
		} else {
			if(isItemCADisabled(upcItems))
				caDisabled = Boolean.TRUE;
		}
		logDebug("TBSItemExclusionDroplet :: checkSKUCADisabled() method :: END");
		return caDisabled;
	}
	
	private boolean isItemCADisabled (String... skuIds) throws BBBBusinessException, BBBSystemException {
			logDebug("TBSItemExclusionDroplet :: isItemCADisabled() method :: START");
        	final RepositoryItem[] skuRepositoryItems = this.getCatalogTools().getMultipleSkuRepositoryItems(skuIds);
        	String ecomFullFillment = null;
        	for (RepositoryItem skuRepository : skuRepositoryItems) {
        		ecomFullFillment = null;
        		if (skuRepository.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME) != null) {
            		ecomFullFillment = ((String) skuRepository.getPropertyValue(BBBCatalogConstants.ECOM_FULFILLMENT_SKU_PROPERTY_NAME));
            	}
        		if (!BBBUtility.isListEmpty(getEcomCAInclusionList()) && !getEcomCAInclusionList().contains(ecomFullFillment)) {
        			return true;
        		}
        	}
        	logDebug("TBSItemExclusionDroplet :: isItemCADisabled() method :: END");
        	return false;	
	}
	
	/**
	 * Validates if the input skuId is valid or not.
	 * 
	 * @param skuId
	 *            SKU ID
	 * @param siteId
	 *            Site ID
	 * @param pRequest
	 *            DynamoHttpServletRequest
	 * @param pResponse
	 *            DynamoHttpServletResponse
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public boolean isItemExclusion(String skuId, String siteId,
			final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		vlogDebug("TBSItemExclusionDroplet :: isItemExclusion () :: START");

		RepositoryItem[] queryResult = null;
		RepositoryItemDescriptor exclusionItem = null;
		String reasonCode = "", siteFlag = null;
		Timestamp startDate = null, endDate = null;
		Timestamp currentDate;
		boolean validSite = Boolean.FALSE;
		boolean validDate = Boolean.FALSE;
		boolean isItemExcluded = Boolean.FALSE;


			try {
				exclusionItem = getItemExclusionRepository().getItemDescriptor(
						CONST_EXCLUSION_ITEM);

				RepositoryView exclusionView = exclusionItem
						.getRepositoryView();

				QueryBuilder exclusionQueryBuilder = exclusionView
						.getQueryBuilder();

				QueryExpression propQE = exclusionQueryBuilder
						.createPropertyQueryExpression(PROP_SKUID);

				QueryExpression constQE = exclusionQueryBuilder
						.createConstantQueryExpression(skuId);

				Query skuQuery = exclusionQueryBuilder.createComparisonQuery(
						propQE, constQE, QueryBuilder.EQUALS);

				queryResult = exclusionView.executeQuery(skuQuery);

			} catch (RepositoryException e) {
				vlogError("Inside Exception: Exception occured while querying ItemExclusion repository ::" + e);
			}

			Date date = new Date();
			currentDate = new Timestamp(date.getTime());
			
			if (null != queryResult && queryResult.length > TBSConstants.ZERO) {

				for (RepositoryItem itemExclusionRepoItem : queryResult) {

					if (isLoggingDebug()) {
						logDebug("Repository Item :: for sku ::" + skuId
								+ ":: is" + itemExclusionRepoItem);
					}

					reasonCode = itemExclusionRepoItem.getPropertyValue(
							PROP_REASON_CODE).toString();
					startDate = (Timestamp) itemExclusionRepoItem
							.getPropertyValue(PROP_START_DT);
					startDate.getTime();
					endDate = (Timestamp) itemExclusionRepoItem
							.getPropertyValue(PROP_END_DT);
					siteFlag = itemExclusionRepoItem.getPropertyValue(
							PROP_SITE_FLAG).toString();

					if (isLoggingDebug()) {
						logDebug("Repository Items :: reasonCode::" + reasonCode
								+ "::startDate::" + startDate + "::endDate::"
								+ endDate);
					}
					
					validSite = validateSite(siteId, siteFlag);

					if (validSite) {
						validDate = validateDate(currentDate, startDate, endDate);
						if (validDate) {
							if (null != reasonCode) {
								vlogDebug("Exclusion Exception occurred :: "
										+ reasonCode);
								isItemExcluded = Boolean.TRUE;
								pRequest.setParameter(PROP_REASON_CODE, reasonCode);
								break;
							} else {
								vlogError("Exclusion Exception occurred :: "
										+ reasonCode);
							}
						}
					}
				}
			}
			return isItemExcluded;
			
	}


	/**
	 * Validates and checks whether the sku start date and date are valid.
	 * 
	 * @param currentDate
	 * @param startDate
	 * @param endDate
	 */

	private boolean validateDate(Timestamp currentDate, Timestamp startDate,
			Timestamp endDate) {

		vlogDebug("TBSItemExclusionDroplet.isItemExclusion (). validateDate():: START");

		if (startDate.before(currentDate)
				&& (endDate.after(currentDate) || endDate.equals(currentDate))) {
			return true;
		}

		vlogDebug("TBSItemExclusionDroplet.isItemExclusion (). validateDate():: END");

		return false;
	}

	/**
	 * Validates for the valid site Id
	 * 
	 * @param siteId
	 *            Site ID
	 * @param siteFlag
	 */
	private boolean validateSite(String currentSiteId, String siteFlag) {

		vlogDebug("TBSItemExclusionDroplet.isItemExclusion (). validateSite():: START");
		if (!BBBUtility.isEmpty(currentSiteId) && !BBBUtility.isEmpty(siteFlag)) {
		if ((siteFlag.contains(TBSConstants.SITE_TBS_BAB_US_VALUE) && currentSiteId.equals(TBSConstants.SITE_TBS_BAB_US)) ||
		(siteFlag.contains(TBSConstants.SITE_TBS_BBB_VALUE) && currentSiteId.equals(TBSConstants.SITE_TBS_BBB)) ||
		(siteFlag.contains(TBSConstants.SITE_TBS_BAB_CA_VALUE) && currentSiteId.equals(TBSConstants.SITE_TBS_BAB_CA))){
		return true;
		}
		}
		vlogDebug("TBSItemExclusionDroplet.isItemExclusion (). validateSite():: END");
		return false;
	}

	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public List getEcomCAInclusionList() {
		return ecomCAInclusionList;
	}

	public void setEcomCAInclusionList(List ecomCAInclusionList) {
		this.ecomCAInclusionList = ecomCAInclusionList;
	}


}
