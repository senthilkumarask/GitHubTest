package com.bbb.cms.droplet;

import java.util.List;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import com.bbb.common.BBBDynamoServlet;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CollectionProductVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.logging.LogMessageFormatter;
/**
 * CollegeCollectionsDroplet retrieves the college categories based on categoryId from CatalogAPI
 * @author kshah
 *
 */
public class CollegeCollectionsDroplet extends BBBDynamoServlet {


	private BBBCatalogTools catalogTools = null;
	private String mSiteId;
	private String dormRoomCollectionCatId;
	private String configType;

	public String getDormRoomCollectionCatId() {
		return dormRoomCollectionCatId;
	}

	public void setDormRoomCollectionCatId(String dormRoomCollectionCatId) {
		this.dormRoomCollectionCatId = dormRoomCollectionCatId;
	}

	public String getConfigType() {
		return configType;
	}

	public void setConfigType(String configType) {
		this.configType = configType;
	}

	public String getSiteId() {
		if(mSiteId != null)
		{
			return mSiteId;
		}
		else
		{
			return getCurrentSiteId();
		}
	}

	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	public void setSiteId(String pSiteId) {
		this.mSiteId = pSiteId;
	}



	/**
	 * Return CatalogTools
	 * @return
	 */
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	/**
	 * Setting CatalogTools
	 * @param catalogTools
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	/**
	 * This method gets the value of site id from the page and fetches the states
	 * greater than the current date and site id.
	 */
	public void service(final DynamoHttpServletRequest request, final DynamoHttpServletResponse response)
			throws javax.servlet.ServletException, java.io.IOException {

		
			logDebug("starting method CollegeCollectionsDroplet");
		
		final String EMPTY = "empty";
		final String COLLEGE_OUTPUT = "output";
		final String LIST_COLLECTION_VO="listCollectionVo";
		final List<CollectionProductVO> listCollectionVo;
		//Request College sub-categories

		try{
			String keyDormCollection=getDormRoomCollectionCatId()+getSiteId();
			String dormRoomId=this.getCatalogTools().getAllValuesForKey(getConfigType(), keyDormCollection).get(0);
			logDebug("Calling getDormRoomCollections for Category : "+dormRoomId+" and site:"+getSiteId());
			listCollectionVo = getCatalogTools().getDormRoomCollections(getSiteId(), dormRoomId);
			logDebug("COLLEGE_COLLECTIONS will be created : ");
			request.setParameter(LIST_COLLECTION_VO,listCollectionVo);
			if(StringUtils.isEmpty(dormRoomId) ) {
				request.serviceParameter(EMPTY, request, response);
			}
			else{
				request.serviceParameter(COLLEGE_OUTPUT, request, response);
			}
		}catch(BBBBusinessException be){
			logError(LogMessageFormatter.formatMessage(request, "Collegecollectiondroplet|service|BBBBusinessException","catalog_1032"),be);

			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		} catch(BBBSystemException bs){
			logError(LogMessageFormatter.formatMessage(request, "Collegecollectiondroplet|service|BBBSystemException","catalog_1033"),bs);

			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		}

		logDebug("Exiting method CollegeCollectionsDroplet");
	}
}