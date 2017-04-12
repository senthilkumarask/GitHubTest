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
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
/**
 * CollegeCollectionsDroplet retrieves the college categories based on categoryId from CatalogAPI
 * @author kshah
 *
 */
public class NextCollegeCollectionDroplet extends BBBDynamoServlet {


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
			return SiteContextManager.getCurrentSiteId();
		}
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
	
		logDebug("starting method NextCollegeCollectionDroplet");
		
		String methodName = "NextCollegeCollectionDroplet_service";
        BBBPerformanceMonitor.start(methodName);
		
		final String PRODUCTID = "productId";
		final String EMPTY = "empty";
		final String COLLEGE_OUTPUT = "output";
		final String NEXT_COLLECTION_VO="nextCollectionVo";
		final List<CollectionProductVO> listCollectionVo;
		String productId =null;
		CollectionProductVO nextCollectionProduct =null;

		if(request.getLocalParameter(PRODUCTID) !=null){
			productId = (String)request.getLocalParameter(PRODUCTID);
		}
		
		try{
			String dormRoomId=this.getCatalogTools().getAllValuesForKey(getConfigType(), getDormRoomCollectionCatId()).get(0);
			logDebug("Calling getDormRoomCollections for Category : "+dormRoomId+" and site:"+getSiteId());
			listCollectionVo = getCatalogTools().getDormRoomCollections(getSiteId(), dormRoomId);
			int matchFlag=0;
			int index=0;
			for (CollectionProductVO collectionProductVO : listCollectionVo) {
				if(collectionProductVO.getProductId().equalsIgnoreCase(productId))
				{
					matchFlag=1;
				}
				if(matchFlag==1)
				{
					if(index==(listCollectionVo.size()-1))
					{
						nextCollectionProduct= listCollectionVo.get(0);
						break;
					}
					else
					{
						nextCollectionProduct =listCollectionVo.get(index+1);
						break;
					}
				}
				index++;
			}
			
			logDebug("COLLEGE_COLLECTIONS will be created : ");
			
			request.setParameter(NEXT_COLLECTION_VO,nextCollectionProduct);
			request.setParameter("listCollectionVo",listCollectionVo);
			if(StringUtils.isEmpty(dormRoomId) ) {
				request.serviceParameter(EMPTY, request, response);
			}
			else
			{
				request.serviceParameter(COLLEGE_OUTPUT, request, response);
			}
		}catch(BBBBusinessException be){
			
		  logError(LogMessageFormatter.formatMessage(request, "NextcollegeCollectionDroplet|service()|BBBBusinessException","catalog_1046"),be);
		  
		   request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		} catch(BBBSystemException bs){
			
		    logError(LogMessageFormatter.formatMessage(request, "NextcollegeCollectionDroplet|service()|BBBSystemException","catalog_1047"),bs);
		          
		
			request.serviceParameter(BBBCoreConstants.ERROR_OPARAM,request, response);
		}

		
			logDebug("Exiting method NextCollegeCollectionDroplet");
		
		BBBPerformanceMonitor.end(methodName);

	}
}