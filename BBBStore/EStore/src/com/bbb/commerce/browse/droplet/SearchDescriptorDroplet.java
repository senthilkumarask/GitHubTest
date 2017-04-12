package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.servlet.ServletException;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/**
* The SearchDescriptorDroplet groups descriptors and returns to JSP
*
* @author  gtiwa7
* @version 1.0
* @since   2017-02-21 
*/
public class SearchDescriptorDroplet  extends BBBDynamoServlet{

	public static final  String OUTPUT = "output";
	public static final String DEPARTMENT="DEPARTMENT";

	@Override
	public void service(DynamoHttpServletRequest pReq , DynamoHttpServletResponse pRes) 
			throws ServletException, IOException{
		
		logDebug("Inside service() of SearchDescriptorDroplet"); 
		SearchResults searchResults=(SearchResults) pReq.getObjectParameter("searchResults");
		if(searchResults != null) {
			logDebug("searchResults found from request "); 
			Map<String, List<CurrentDescriptorVO>> descriptorMap = this.generateDescriptorMap(searchResults);
			 pReq.setParameter("descriptorMap", descriptorMap);
	    	 pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
		}
	}
	
	
	
	/**
	 * This method is used to modify the Descriptors output into an array , if
	 * descriptors has multiple ROOTNAME with the same name. It is used to
	 * display same root name descriptors with different child properties on
	 * front end.
	 * 
	 * @param SearchResults
	 *            searchResults
	 * @return Map<List,urrentDescriptorVO>>
	 */
	public Map<String, List<CurrentDescriptorVO>> generateDescriptorMap(SearchResults searchResults) {
		Map<String, List<CurrentDescriptorVO>> map = new LinkedHashMap<>();
		
		ListIterator<CurrentDescriptorVO> iterator = searchResults.getDescriptors().listIterator(searchResults.getDescriptors().size());

		while (iterator.hasPrevious()) {
			List<CurrentDescriptorVO> descriptorVOs = new ArrayList<>();
			CurrentDescriptorVO currentDescriptorVO = iterator.previous();
			if(currentDescriptorVO.getRootName().equals(DEPARTMENT)
					|| this.checkDescriptorExistInFacet(currentDescriptorVO, searchResults.getFacets())){
				logDebug("Descriptor name either department or  all filters are not checked of facets ");
				continue;
			}
			// checking if descriptor map contains descriptor name then get the list of items and add the current into it
			if (map.containsKey(currentDescriptorVO.getRootName())) {
				List<CurrentDescriptorVO> existList =  map.get(currentDescriptorVO.getRootName());
				existList.add(currentDescriptorVO);
				map.put(currentDescriptorVO.getRootName(), existList);
			} else {
				descriptorVOs.add(currentDescriptorVO);
				map.put(currentDescriptorVO.getRootName(), descriptorVOs);
				logDebug("Adding  "+currentDescriptorVO.getRootName()+" in descriptor map.");
			}

		}
		return map;
	}
	
	/**
	 * This method checks whether descriptor exists in facets or not , if exists then return true
	 * 
	 * @param currentDescriptorVO
	 *            CurrentDescriptorVO
	 * @param facets
	 *           List<FacetParentVO>         
	 * @return boolean
	 */
	public boolean checkDescriptorExistInFacet(CurrentDescriptorVO currentDescriptorVO , List<FacetParentVO> facets){
		boolean flag=false;
		if(!BBBUtility.isEmpty(facets) && null != currentDescriptorVO){
			for(FacetParentVO facetVO : facets){
				if(facetVO.getName().equals(currentDescriptorVO.getRootName())){
					logDebug("FacetName : "+facetVO.getName()+" found equal to Descriptor name : "+currentDescriptorVO.getRootName());
					flag=true;
				}
			}
		}
		return flag;
	}
	
}