package com.bbb.cms.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.repository.RepositoryItem;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.ServletUtil;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBStoreRestConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.rest.search.RestSearchManager;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.SearchResults;

/**
 * 
 * 
 */
public class SolutionFacetPropertyCustomizer extends BBBGenericService implements
		RestPropertyCustomizer {

	public Object getPropertyValue(String pPropertyName, Object pResource) {

		logDebug("Entering SolutionFacetPropertyCustomizer.getPropertyValue");
		String facetEndecaId = "";
		String solutionFacetDesc = "";
		RestSearchManager restSearchManager = null;
		String channelID = "";
		Map<String, String> queryParams = new HashMap<String, String>();
		String channelFlag = "x-bbb-channel"; 
		JSONObject obj = new JSONObject();
		try {
			Set<RepositoryItem> solutionFacetsSetRepositoryItem = (Set<RepositoryItem>)DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			for(RepositoryItem solutionFacetsRepositoryItem:solutionFacetsSetRepositoryItem){
			if (null != ServletUtil.getCurrentRequest()) {
					channelID = ServletUtil.getCurrentRequest().getHeader(channelFlag);
					logDebug("Inside SolutionFacetPropertyCustomizer.getPropertyValue : Channel Id: "+channelID);
					restSearchManager = (RestSearchManager)ServletUtil.getCurrentRequest().resolveName("/com/bbb/rest/search/RestSearchManager");
				}

			queryParams.put("channelID", channelID);
			if(null != solutionFacetsRepositoryItem.getPropertyValue(BBBStoreRestConstants.SKU_FACET_TYPE_DESCRIPTION)){
				solutionFacetDesc = solutionFacetsRepositoryItem.getPropertyValue(BBBStoreRestConstants.SKU_FACET_TYPE_DESCRIPTION).toString();
			}
			if(null != restSearchManager){
				SearchResults searchResults = restSearchManager.performSearch(queryParams);
				List<FacetParentVO> facets = searchResults.getFacets();
				for (FacetParentVO facet : facets) {
					if (facet != null && facet.getName().equalsIgnoreCase(solutionFacetDesc)) {
						facetEndecaId = facet.getFacetEndecaId();
						logDebug("Inside SolutionFacetPropertyCustomizer.getPropertyValue : FacetEndecaId: "+facetEndecaId);
					}
				}
			}
			obj.put("facetEndecaId", facetEndecaId);
			obj.put("description", solutionFacetsRepositoryItem.getPropertyValue("description"));
			obj.put("id", solutionFacetsRepositoryItem.getRepositoryId());
			obj.put("repositoryId", solutionFacetsRepositoryItem.getRepositoryId());
		}

		} catch (PropertyNotFoundException e) {
			logError("Property Not Found Exception in SolutionFacetPropertyCustomizer", e);
		} catch (BBBSystemException e) {
			logError("BBBSystemException in SolutionFacetPropertyCustomizer", e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException in SolutionFacetPropertyCustomizer", e);
		} 
		logDebug("Exiting SolutionFacetPropertyCustomizer.getPropertyValue");
		return obj;
	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}
}
