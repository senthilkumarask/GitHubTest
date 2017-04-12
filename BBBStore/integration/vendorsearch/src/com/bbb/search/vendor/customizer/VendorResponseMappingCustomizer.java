package com.bbb.search.vendor.customizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

public class VendorResponseMappingCustomizer extends BBBGenericService implements
		VendorResponseCustomizer {

	private static final String ACTIVE = "active";
	private static final String ATTRIBUTES_KEY = "Attributes";
	private static final String DIM_DISPLAY_CONFIGTYPE = "DimDisplayConfig";
	private static final String DEPARTMENT_FACET = "DEPARTMENT";
	private static final String RECORD_TYPE_FACET = "RECORD TYPE";
	private static final String BRAND_KEY = "Brand";
	private static final String COLORGROUP_KEY = "ColorGroup";
	private static final String PRICE_RANGE_FACET = "PRICE RANGE";
	private static final String CLASS_NAME = "VendorResponseMappingCustomizer";
	/**
	 * This is overridden method to customize the Vendor Response. This method calls the method to 
	 * create facet-descriptor mapping, create facet List from descriptors in case facet list is 
	 * empty, set refined string for facets, descriptors and facet refinements name.
	 * 
	 * @param browseSearchVO
	 * @param pSearchQuery
	 * @throws None
	 */
	@Override
	public void customizeResponse(final SearchResults browseSearchVO, final SearchQuery pSearchQuery){
		BBBPerformanceMonitor.start(CLASS_NAME,
				"customizeResponse");
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseMappingCustomizer.customizeResponse() method. Parameters :: Search Keyword -" 
				+ pSearchQuery.getKeyWord());
		}
		int facetSize = 0;
		String facetName;
		String refineFacetName;
		int facetCountForRefTypeParam = 0;
		String allTabList = BBBCoreConstants.BLANK;
		String rootQuery = BBBCoreConstants.BLANK;
		String catalogRefId = BBBCoreConstants.BLANK;
		boolean deptAvail = false;
		boolean emptyBox = true;
		boolean emptyFacets = false;
		
		final List<CurrentDescriptorVO> descriptorVOs = browseSearchVO.getDescriptors();
		final String currentCatName = browseSearchVO.getCurrentCatName();
		if(null != browseSearchVO.getCategoryHeader()){
			rootQuery = browseSearchVO.getCategoryHeader().getQuery();
		}
		final Map<String, String> catalogRefMap = pSearchQuery.getCatalogRef();
		if(null != catalogRefMap){
			catalogRefId = catalogRefMap.get(BBBCoreConstants.CATALOG_REF_ID);
		}
		if(isLoggingDebug()){
			logDebug("Catalof Ref Id :: " + catalogRefId + " Root Query : " + rootQuery + ", currentCatName : " + currentCatName);
		}
		
		// Set these variables only for browse PLP -- the code under this if condition is not required as this is for category landing page --- check for facet search - TO-DO - With endpoint integration testing
		if(null != descriptorVOs && BBBUtility.isNotEmpty(catalogRefId)){
			String descRootName;
			String descName;
			final Iterator<CurrentDescriptorVO> descIterator = descriptorVOs.iterator();
			while(descIterator.hasNext()){
				final CurrentDescriptorVO descVO = descIterator.next();
				descRootName = descVO.getRootName();
				descName = descVO.getName();
				
				//Variable to set the class of 'All' Link in Category Refinement
				if(descName.equals(currentCatName) && rootQuery.equals(catalogRefId)){
					if(isLoggingDebug()){
						logDebug("Set the AllTabList property as Active for Descriptor :" + descName);
					}
					allTabList = ACTIVE;
				}
				
				//BBBSL-6423 | variable to contain total count of sws filters and facet filters
				if(!descRootName.equals(DEPARTMENT_FACET) && !descRootName.equals(RECORD_TYPE_FACET)){
					facetCountForRefTypeParam++ ;
				}
			}
		}
		
		final List<FacetParentVO> facetParentVOs = browseSearchVO.getFacets();
		if (!BBBUtility.isListEmpty(facetParentVOs)) {
		
			final Iterator<FacetParentVO> facetItr = facetParentVOs.iterator();
			while(facetItr.hasNext()){
				
				final FacetParentVO facetVO = facetItr.next();
				facetName = facetVO.getName();
				
				//Set the refined facet name
				refineFacetName = facetName.replaceAll(BBBCoreConstants.SPACE, BBBCoreConstants.UNDERSCORE);
				facetVO.setFacetRefinedName(refineFacetName);
				if(isLoggingDebug()){
					logDebug("Set the refined Facet name for facet : " + facetName + " as : " + refineFacetName); 
				}
				//Set the flag to signify if department facet exists in Facets List
				if(facetName.equals(DEPARTMENT_FACET)){
					deptAvail = true;
				}
				//Create a mapping of facets and their corresponding descriptor to display selected descriptors on JSP
				if(null != descriptorVOs && facetVO.isMultiSelect()){
					createFacetDescriptorMapping(descriptorVOs, facetVO, facetName);
				}
			}
		}
			
			//Create facet List from descriptors in case facet list is empty as we 
			//need to display only descriptors when facet list is empty.
			if (facetParentVOs != null) {
				facetSize = facetParentVOs.size();
			}
			
			if((facetParentVOs == null || facetParentVOs.isEmpty())|| (facetSize == 1 && deptAvail)){
				emptyFacets = true;
				emptyBox = createEmptyFacetList(descriptorVOs, browseSearchVO);
			}
			
			if(isLoggingDebug()){
				logDebug("The variable values are - deptAvail : " + deptAvail + ",emptyFacets : " + emptyFacets + ", emptyBox : " 
					+ emptyBox +", allTabList :" + allTabList + ", facetCountForRefTypeParam :" + facetCountForRefTypeParam + ", deptAvail : " + deptAvail );
			}
			browseSearchVO.setEmptyFacets(emptyFacets);
			browseSearchVO.setEmptyBox(emptyBox);
			browseSearchVO.setAllTabList(allTabList);
			browseSearchVO.setFacetCountForRefTypeParam(facetCountForRefTypeParam);
			browseSearchVO.setDeptAvail(deptAvail);
			if(isLoggingDebug()){
				logDebug("[END] VendorResponseMappingCustomizer.customizeResponse() method");
			}
			BBBPerformanceMonitor.start(CLASS_NAME,
					"customizeResponse");
	}
	
	/**
	 * This method is used to create facet List from descriptors in case facet list is empty
	 * 
	 * @param emptyFacets
	 * @param emptyBox
	 * @param descriptorVOs
	 * @param browseSearchVO
	 * @throws None
	 */
	private boolean createEmptyFacetList(final List<CurrentDescriptorVO> descriptorVOs, final SearchResults browseSearchVO)
	{	
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseMappingCustomizer.createEmptyFacetList() method");
		}
		final List<FacetParentVO> emptyFacetParentVOs = new ArrayList<>();
		String facetName ;
		boolean emptyBox = false;
		final String attributeName = BBBConfigRepoUtils.getStringValue(DIM_DISPLAY_CONFIGTYPE , ATTRIBUTES_KEY);
		String colorGroupFacet = BBBConfigRepoUtils.getStringValue(DIM_DISPLAY_CONFIGTYPE, COLORGROUP_KEY);
		String brandFacet = BBBConfigRepoUtils.getStringValue(DIM_DISPLAY_CONFIGTYPE, BRAND_KEY);
		
		if(null != descriptorVOs){
			final Iterator<CurrentDescriptorVO> descItr = descriptorVOs.iterator();
			final int descSize = descriptorVOs.size();
			while(descItr.hasNext()){
				
				boolean isFacetExist = false;
				final CurrentDescriptorVO descVO = descItr.next();
				final String descRootName = descVO.getRootName();
				
				//For the descriptors with multi select root facets,  create a new facet list 
				//from the descriptors list as the facet list is empty 
				if(!RECORD_TYPE_FACET.equals(descVO.getRootName()) && descVO.isMultiSelect()){
					if(isLoggingDebug()){
						logDebug("Creating Facet List For descriptor : " + descVO.getName() + " with facet root name : " + descRootName);
					}
					//Check if facets have already been created for these descriptors 
					final Iterator<FacetParentVO> emptyfacetsItr = emptyFacetParentVOs.iterator();
					while(emptyfacetsItr.hasNext()){
						final FacetParentVO itrFacetVO = emptyfacetsItr.next();
						facetName = itrFacetVO.getName();
						if(descRootName.equals(facetName)){
							isFacetExist = true;
							if(isLoggingDebug()){
								logDebug("Facet List has already been created for descriptor : " + descVO.getName() + " with root name : " + descRootName);
							}
							break;
						}
					}
					
					//Create the facet list only if facets have not been created for these descriptors
					if(!isFacetExist){
						final FacetParentVO emptyFacetVO = new FacetParentVO();
						final List<CurrentDescriptorVO> finalDescriptorVOs = new ArrayList<>();
						final List<CurrentDescriptorVO> allDescriptorVOs = browseSearchVO.getDescriptors();
						final Iterator<CurrentDescriptorVO> descVOItr = allDescriptorVOs.iterator();
						while(descVOItr.hasNext()){
							final CurrentDescriptorVO finalDescVO = descVOItr.next();
							final String finalDescRootName = finalDescVO.getRootName();
							if(finalDescRootName.equals(descRootName)){
								if(isLoggingDebug()){
									logDebug("Adding Descriptor " + finalDescVO.getName() + " in list for facet : " + finalDescRootName);
								}
								finalDescriptorVOs.add(finalDescVO);
								
							}
						}
						emptyFacetVO.setName(descRootName);
						emptyFacetVO.setFacetDescriptors(finalDescriptorVOs);
						emptyFacetParentVOs.add(emptyFacetVO);
					}
				}
				
				//Set the emptyBox flag if descriptor root name is in brand, color or price Range
				if(descSize > 1 && (descRootName.equals(brandFacet) || descRootName.equals(colorGroupFacet) || descRootName.equals(PRICE_RANGE_FACET) 
						|| descRootName.equals(attributeName))){
					logDebug("Set the emptyBox parameter as true");
					emptyBox = true;
				}
			}
		}

		browseSearchVO.setEmptyFacetsList(emptyFacetParentVOs);
		if(isLoggingDebug()){
			logDebug("[END] VendorResponseMappingCustomizer.createEmptyFacetList() method");
		}
		return emptyBox;
	}
	
	/**
	 * This method is used to create the facet-descriptor mapping to be used directly while rendering descriptors
	 * 
	 * @param descriptorVOs
	 * @param facetVO
	 * @param facetName
	 * @throws None
	 */
	private void createFacetDescriptorMapping(final List<CurrentDescriptorVO> descriptorVOs, final FacetParentVO facetVO, final String facetName){
		if(isLoggingDebug()){
			logDebug("[START] VendorResponseMappingCustomizer.createFacetDescriptorMapping() method");
		}
		
		final List<CurrentDescriptorVO> facetdescVOs = new ArrayList<>();
		final Iterator<CurrentDescriptorVO> descItrerator = descriptorVOs.iterator();
		while(descItrerator.hasNext()){
			final CurrentDescriptorVO descVO = descItrerator.next();
			final String descName = descVO.getName();
			
			//Set Refined descriptor name
			final String refinedDescName = StringEscapeUtils.escapeXml(BBBUtility.refineFacetString(descName));
			descVO.setRefinedName(refinedDescName);
			if(isLoggingDebug()){
				logDebug("Set the refined name for descriptor : " + descName + " as : " + refinedDescName);
			}
			
			//Create a mapping of facets and their corresponding descriptors only if
			//facet is multiselect as we display descriptors of multi select facets only 
			final String descRootName = descVO.getRootName();
			if(descRootName.equals(facetName)){
				if(isLoggingDebug()){
					logDebug("Adding Descriptor : " + descName + " in facet : " + facetName);
				}
				facetdescVOs.add(descVO);
			}
		}
		facetVO.setFacetDescriptors(facetdescVOs);
		if(isLoggingDebug()){
			logDebug("[END] VendorResponseMappingCustomizer.createFacetDescriptorMapping() method");
		}
	}

}
