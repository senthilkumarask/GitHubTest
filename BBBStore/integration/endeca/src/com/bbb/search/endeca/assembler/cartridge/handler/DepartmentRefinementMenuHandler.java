package com.bbb.search.endeca.assembler.cartridge.handler;

import static com.bbb.search.endeca.EndecaSearchUtil.isCategoryPageRequest;
import static com.bbb.search.endeca.EndecaSearchUtil.isHeaderSearchRequest;
import static com.bbb.search.endeca.EndecaSearchUtil.isKeywordSearchRequest;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.BBBABY_TYPE_AHEAD_VALUE;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.BBB_TYPE_AHEAD_VALUE;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.BBCA_TYPE_AHEAD_VALUE;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CATA_REF_ID;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_ALLCAPS_DGRAPH_BINS;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_DIMENSION_ID;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_DIMENSION_NAME;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_NAME;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_NODE_TYPE;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_NUM_REFINEMENTS;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_SORT;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_SORT_VALUE_DEFAULT;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_SORT_VALUE_DYNRANK;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.CI_PROP_SORT_VALUE_STATIC;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.DGRAPH_BINS;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.NODE_ID;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.RECORD_TYPE;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.SITE_ID;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.TYPE_AHEAD;
import static com.bbb.search.endeca.constants.BBBEndecaConstants.TYPE_AHEAD_CI_NAME;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.nucleus.logging.VariableArgumentApplicationLogging;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.CategoryRefinementVO;
import com.bbb.search.bean.result.CurrentDescriptorVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.bean.result.FacetRefinementVO;
import com.bbb.search.endeca.EndecaClient;
import com.bbb.search.endeca.EndecaSearch;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.search.endeca.assembler.cartridge.config.CategoryRefinementMenuConfig;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.bbb.utils.BBBUtility;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.cartridge.RefinementMenuConfig;
import com.endeca.infront.cartridge.support.RefinementBuilder;
import com.endeca.infront.navigation.model.EqlFilter;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.infront.navigation.request.RefinementMdexQuery;
import com.endeca.infront.navigation.request.support.NavigationRequest;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValIdList;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.Dimension;
import com.endeca.navigation.DimensionList;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ESearchAutoSuggestion;
import com.endeca.navigation.ESearchDYMSuggestion;
import com.endeca.navigation.ESearchReport;
import com.endeca.navigation.Navigation;
import com.endeca.navigation.PropertyMap;
import com.endeca.soleng.urlformatter.UrlFormatException;

/**
 * Department Refinement Handler class that handles Department Refinement Menu (Tree Structure) on Search Results Page and 
 * Department Refinement Menu (Flat list) on Category Pages based on the input context.
 * 
 * @author tg0055
 *
 */
public class DepartmentRefinementMenuHandler extends NavigationCartridgeHandler<CategoryRefinementMenuConfig, CategoryRefinementMenu> {

	private MdexRequest mMdexRequest;
	private EndecaClient mEndecaClient;
	private EndecaSearch mEndecaSearch;
	
	private static final String TRUE = "true";
	private EndecaSearchUtil mEndecaSearchUtil;

	
	private VariableArgumentApplicationLogging logger = AssemblerTools.getApplicationLogging();
	
	@Override
	protected CategoryRefinementMenuConfig wrapConfig(ContentItem pContentItem) {
		return new CategoryRefinementMenuConfig(pContentItem);
	}
	
	public void preprocess(CategoryRefinementMenuConfig cartridgeConfig) throws CartridgeHandlerException {
		
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
		SearchQuery searchQuery = (SearchQuery)dynamoRequest.getAttribute(RQST_PARAM_NAME_SEARCH_QUERY_VO);
		
		if(searchQuery == null){
			throw new CartridgeHandlerException("Required Parameters null. dynamo request and/or searchQueryVO.");
		}
	    RefinementMdexQuery refinement = new RefinementMdexQuery();
	    refinement.setDimensionId(cartridgeConfig.getDimensionId());
	    refinement.setNumRefinements(Integer.valueOf(cartridgeConfig.getLimit()));
	    refinement.setBoostedDvals(cartridgeConfig.getBoostRefinements());
	    refinement.setBuriedDvals(cartridgeConfig.getBuryRefinements());
	    refinement.setWhyPrecedenceRuleFiredEnabled(cartridgeConfig.isWhyPrecedenceRuleFired());
	    updateSortConfig(cartridgeConfig, refinement);//Mostly for Type-Ahead Refinement
	    
	    if(isHeaderSearchRequest(searchQuery)){
	    	//create siteID filter only for TOP NAV Query.
			FilterState filters = new FilterState();
			filters = getEndecaSearchUtil().addSiteIdFilter(searchQuery, filters);
	    	mMdexRequest = createMdexRequest(filters, refinement);
	    }else{
	    	mMdexRequest = createMdexRequest(getNavigationState().getFilterState(), refinement);
	    }
	    //keyword search scenario
	    if(isKeywordSearchRequest(searchQuery)){
	    	//add type-ahead config item.
	    	CategoryRefinementMenuConfig newContentItem = null;
			String inDimName = (String)cartridgeConfig.get(CI_PROP_DIMENSION_NAME);
			if (inDimName.equalsIgnoreCase(getEndecaSearch().getConfigUtil().getDepartmentConfig().get(searchQuery.getSiteId()))) {
				newContentItem = new CategoryRefinementMenuConfig(cartridgeConfig.getType());
				newContentItem.put(CI_PROP_DIMENSION_NAME, inDimName+TYPE_AHEAD);
				newContentItem.put(CI_PROP_NAME, inDimName+TYPE_AHEAD);
				//newContentItem.put(CI_PROP_DIMENSION_ID, BBBEndecaConstants.TYPE_AHEAD_REF_DIM_MAP.get(inDimName+BBBEndecaConstants.TYPE_AHEAD));
				try {
					String dimensionId = getEndecaSearch().getSearchUtil().getCatalogId(inDimName+TYPE_AHEAD, null);
					if(null != dimensionId) {
						newContentItem.put(CI_PROP_DIMENSION_ID, dimensionId);
						newContentItem.put(CI_PROP_NUM_REFINEMENTS, "1");
						newContentItem.put(CI_PROP_SORT, CI_PROP_SORT_VALUE_DYNRANK);
						cartridgeConfig.put(TYPE_AHEAD_CI_NAME, newContentItem);
					}
				} catch (BBBBusinessException e) {
					logger.logError(e);
					throw new CartridgeHandlerException("Error retrieving Type_ahead dimension id - " + inDimName+TYPE_AHEAD + " - while creating keyword search request. : " + e.getMessage());
				} catch (BBBSystemException e) {
					logger.logError(e);
					throw new CartridgeHandlerException("Error retrieving Type_ahead dimension id - " + inDimName+TYPE_AHEAD + " - while creating keyword search request. : " + e.getMessage());
				}
			}
	    }
		/* PSI 6  || BPS-798 || Changes Start || Implemente department as tree*/
//		logDebug("fectchCategoryTreeStrucutre : " +fectchCategoryTreeStrucutre + "keyword :" +pSearchQuery.getKeyWord());
		/* PSI 6  || BPS-798 || Changes end || Implemente department as tree Structure*/
	}
	
	public CategoryRefinementMenu process(CategoryRefinementMenuConfig cartridgeConfig) throws CartridgeHandlerException {

		ENEQueryResults results = executeMdexRequest(mMdexRequest);
		
		CategoryRefinementMenu response = new CategoryRefinementMenu(cartridgeConfig);
		
		DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
		SearchQuery searchQuery = (SearchQuery)dynamoRequest.getAttribute(RQST_PARAM_NAME_SEARCH_QUERY_VO);
		
		if(isHeaderSearchRequest(searchQuery)){
			try {
				//EndecaQueryVO endecaQueryVO = (EndecaQueryVO)dynamoRequest.getAttribute(RQST_PARAM_NAME_ENDECA_QUERY_VO);
				List<FacetParentVO> facetsList = getEndecaSearch().getL1Categories(searchQuery, results);
				response.setFacetParentVOs(facetsList);
			} catch (BBBSystemException e) {
				throw new CartridgeHandlerException(e);
			}
		} else if(isCategoryPageRequest(searchQuery)){
			//category page
			CategoryParentVO categoryParent = createCategorySiblingRefinementForSelectedCategory(cartridgeConfig, results, searchQuery);
			response.setCatList(categoryParent);
		}else if(isKeywordSearchRequest(searchQuery)){
			//if keyword (invokes tree logic) search
			try {
				FacetParentVO facetParentVO = createCategoryTreeRefinement(searchQuery, results, cartridgeConfig);
				if(null != facetParentVO) {
					response.setFacetParentVOs(Arrays.asList(facetParentVO));	
				}
			} catch (BBBSystemException e) {
				logger.logError(e);
				throw new CartridgeHandlerException(e.getMessage());
			} catch (BBBBusinessException e) {
				logger.logError(e);
				throw new CartridgeHandlerException(e.getMessage());
			}
		}else {
			//could be topnav, brand page or any other page with default behavior. (tree logic will not be invoked since type-ahead dimension was not added.) 
			try {
				FacetParentVO facetParentVO = createCategoryTreeRefinement(searchQuery, results, cartridgeConfig);
				if(null != facetParentVO) {
					response.setFacetParentVOs(Arrays.asList(facetParentVO));	
				}
			} catch (BBBSystemException e) {
				logger.logError(e);
				throw new CartridgeHandlerException(e.getMessage());
			} catch (BBBBusinessException e) {
				logger.logError(e);
				throw new CartridgeHandlerException(e.getMessage());
			}
			
		}
		//logDebug("Exit process method.");
		return response;
	}
	
	protected FacetParentVO createCategoryTreeRefinement(SearchQuery pSearchQuery, ENEQueryResults pResults, CategoryRefinementMenuConfig cartridgeConfig) throws BBBSystemException, BBBBusinessException{
		/*
		 * Below condition is added fetch the L2 department wchich has highest
		 * product count, L3 under the L2 and its ancestor category
		 */
		Navigation navigation = pResults.getNavigation();
		if(navigation == null){
			return null;
		}
		
		DimensionList refinementDims = navigation.getRefinementDimensions();
		Dimension rootDimensionSiteID = refinementDims.getDimension(Long.parseLong(cartridgeConfig.getDimensionId()));
		if(rootDimensionSiteID == null){
			return null;
		}
		
		FacetParentVO facetParentVO = null;
		//PROCESS ONLY MATCHING SITE DIMENSION AND NO OTHER - INCLUDING TYPE-AHEAD.
		if((rootDimensionSiteID.getName().equalsIgnoreCase(getEndecaSearch().getConfigUtil().getDepartmentConfig().get(pSearchQuery.getSiteId())))) {
			
			//Note above If condition: convertToFacetParentVO happens only for the requesting site and not also for it's type ahead created below.
			facetParentVO = convertToFacetParentVO(rootDimensionSiteID, pSearchQuery);

			final Dimension typeAHeadDimension = refinementDims.getDimension(cartridgeConfig.get(CI_PROP_DIMENSION_NAME)+TYPE_AHEAD);

			if(null == facetParentVO || null == typeAHeadDimension){
				return facetParentVO;//return null or L1s - since it could be brand page request if typeAHeadDimension is null
			}

			//CHECK FOR CONDITION THAT NO FILTERS ARE SELECTED AND null == pSearchQuery.getNarrowDown()
			if(hasUserSelectedFilters(pSearchQuery, navigation)){
				return facetParentVO;//return L1s.
			}
			
			//get EQL filter from request's filter state which will contain negative matching criteria
			//this can also be fetched from NegativeMatchingHandler but requires referencing another handler 
			String eqlFilterExpression = "";
			
			if(null != mMdexRequest && mMdexRequest instanceof NavigationRequest 
						&& null != ((NavigationRequest)mMdexRequest).getFilterState()) {
				EqlFilter eqlFilter = ((NavigationRequest)mMdexRequest).getFilterState().getEqlFilter();
				if(null != eqlFilter) {
					eqlFilterExpression = eqlFilter.getExpression();
				}
			}
			
			String endecaSiteRootDimension=String.valueOf(rootDimensionSiteID.getId());
			
			String dimID=null;
			FacetRefinementVO facetRefinementL2 = null;
			String ancestercategory=null;
			String omnitureBoostedL2Id = null;
			
			if (BBBUtility.isEmpty(cartridgeConfig.getTreeStructureL2ID())) {
			     omnitureBoostedL2Id = getEndecaSearchUtil().fetchOmnitureBoostedL2ID(navigation, pSearchQuery);
			}
			
			if(null != cartridgeConfig.getTreeStructureL2ID() && !cartridgeConfig.getTreeStructureL2ID().equals("")) {
				dimID = cartridgeConfig.getTreeStructureL2ID();
				facetRefinementL2 = new FacetRefinementVO();
				facetRefinementL2.setCatalogId(dimID);
				ancestercategory=getEndecaSearch().getDepartmentsForTreeNavigation(dimID,endecaSiteRootDimension,pSearchQuery,facetRefinementL2,eqlFilterExpression);
			} else if (!BBBUtility.isEmpty(omnitureBoostedL2Id)) {
				facetRefinementL2 = new FacetRefinementVO();
				facetRefinementL2.setCatalogId(omnitureBoostedL2Id);
				ancestercategory=getEndecaSearch().getDepartmentsForTreeNavigation(omnitureBoostedL2Id,endecaSiteRootDimension,pSearchQuery,facetRefinementL2,eqlFilterExpression);
			} else {	
				//logDebug("typeAHeadDimension : "+typeAHeadDimension+ "|| For Keyword :"+pSearchQuery.getKeyWord());
				
				//logDebug("fetching L2 and L3 category details for Department tree structure for keyword :"+pSearchQuery.getKeyWord());
               	final DimValList childDimList = typeAHeadDimension.getRefinements();
				@SuppressWarnings("unchecked")
				final ListIterator<DimVal> childDimIter =  childDimList.listIterator();
				
				DimVal childDimVal = null;
					
					while(childDimIter.hasNext()){
						childDimVal = childDimIter.next();
						facetRefinementL2 = new FacetRefinementVO();
						facetRefinementL2.setName(childDimVal.getName());
						facetRefinementL2.setDimensionName(childDimVal.getDimensionName());
						if(null != childDimVal.getProperties()){
							facetRefinementL2.setSize((String) childDimVal.getProperties().get(DGRAPH_BINS));
							dimID=String.valueOf(childDimVal.getProperties().get(NODE_ID));
						}
						//logDebug("L2 Department : "+dimID);
						facetRefinementL2.setCatalogId(dimID);
						ancestercategory=getEndecaSearch().getDepartmentsForTreeNavigation(dimID,endecaSiteRootDimension,pSearchQuery,facetRefinementL2,eqlFilterExpression);
						//logDebug("Parent Category for L2:"+childDimVal.getProperties().get(NODE_ID)+" is :"+ancestercategory);
						if(null!=ancestercategory){
							break;
						}
					}
					
			}

			//}
			/* Below Condition is to map l2 Department to ancestor category(L1 department)*/
			if(ancestercategory!=null) {
				//logDebug("size of Facet Refinements : "+fpVO.getFacetRefinement()!=null?String.valueOf(fpVO.getFacetRefinement().size()):null);
				for(FacetRefinementVO frVO:facetParentVO.getFacetRefinement()) {
					//logDebug("frVO.getCatalogId() : "+frVO.getCatalogId() +" ancestercategory: "+ancestercategory);
					if(frVO.getCatalogId().equals(ancestercategory)) {
						//logDebug("Ancestor Category Matched with L1 category:"+ancestercategory);
						frVO.setFacetsRefinementVOs(new ArrayList<FacetRefinementVO>());
						frVO.getFacetsRefinementVOs().add(facetRefinementL2);
						// calling again below method to get all the l2 departments for Ancestor category i.e L1 Department
						getEndecaSearch().getDepartmentsForTreeNavigation(ancestercategory,endecaSiteRootDimension,pSearchQuery,frVO,eqlFilterExpression);
						ancestercategory=null;
						break;
					}
				}
			}
		}

	/* PSI 6  || BPS-798 || Implemente department as tree || Changes End*/

//		logDebug("Facet List: " +facetParentList);
//		logDebug("Exit EndecaSearch.getFacets method.");

//		long EndTime1 = System.currentTimeMillis();
//		logDebug(" getFacets ends at  :"	+ EndTime1 + " Total Time Taken="+(EndTime1 -startTime1));
		return facetParentVO;
		
	}
	
	protected CategoryParentVO createCategorySiblingRefinementForSelectedCategory(CategoryRefinementMenuConfig cartridgeConfig, ENEQueryResults results, SearchQuery searchQuery) throws CartridgeHandlerException {
		final CategoryParentVO categoryParent = new CategoryParentVO();
		CategoryRefinementVO categoryRefines = null;
		final List<CategoryRefinementVO> catRefineList = new ArrayList<CategoryRefinementVO>();
		
		
		if(results == null) { return null;}

		final List<CurrentDescriptorVO> listDesc = getEndecaSearch().getDescriptors(results.getNavigation(), "", searchQuery);
		
		final Dimension dim = results.getNavigation().getDescriptorDimensions().getDimension(Long.parseLong(cartridgeConfig.getDimensionId()));
		
		if(dim == null){ return null;}
		
		if((dim.getName().equalsIgnoreCase(getEndecaSearch().getConfigUtil().getDepartmentConfig().get(searchQuery.getSiteId())))){
			final DimValList dimvalList = dim.getRefinements();
			int loopSize = 0;

			if(null == dimvalList || dimvalList.isEmpty()){
				if(!BBBUtility.isEmpty(searchQuery.getRefType()))
				{
					getEndecaSearch().setCategoryParentForSingleCategoryResult(searchQuery,categoryParent, catRefineList, listDesc, dim);//BAU
				}
				else
				{
					final DimValList dimvalList1 = dim.getAncestors();
					if(dimvalList1 != null && !dimvalList1.isEmpty()){
						final DimVal dimVal=(DimVal) dimvalList1.get(dimvalList1.size()-1);
						final long dimValId = dimVal.getId();
						final String name = dimVal.getName();
						loopSize = getEndecaSearch().getMaxCatTabs();
						categoryRefines = getSiblings(dimValId,searchQuery,categoryParent,catRefineList,name,loopSize);
				}
			  }
			}
			else{
				if(getEndecaSearch().getMaxCatTabs() >= dimvalList.size()){
					loopSize = dimvalList.size();
				}
				else{
					loopSize = getEndecaSearch().getMaxCatTabs();
				}

				for(int j=0;j<loopSize;j++){
					categoryRefines = new CategoryRefinementVO();
					final DimVal dimVal=(DimVal) dimvalList.get(j);
					final PropertyMap dimValuePropertyMap = dimVal.getProperties();
					categoryRefines.setName(dimVal.getName());

					categoryRefines.setQuery(String.valueOf(dimVal.getId()));
					if(getEndecaSearch().getNodeType().equalsIgnoreCase((String)dimValuePropertyMap.get(getEndecaSearch().getDimPropertyMap().get("NODE_TYPE")))){
						categoryRefines.setIsPortraitEligible(TRUE);
					}
					categoryRefines.setTotalSize((String)dimValuePropertyMap.get(getEndecaSearch().getDimPropertyMap().get("DGRAPH.BINS")));
					catRefineList.add(categoryRefines);
				}
				categoryParent.setName(((DimVal)dim.getCompletePath().get(dim.getCompletePath().size()-1)).getName());
				categoryParent.setQuery(String.valueOf(((DimVal)dim.getCompletePath().get(dim.getCompletePath().size()-1)).getId()));
				categoryParent.setCategoryRefinement(catRefineList);
				if(getEndecaSearch().getNodeType().equalsIgnoreCase((String)(((DimVal)dim.getCompletePath().get(dim.getCompletePath().size()-1)).getProperties().get(getEndecaSearch().getDimPropertyMap().get("NODE_TYPE"))))){
					categoryParent.setIsPortraitEligible(TRUE);
				}
			}
		}
		
		categoryParent.setCategoryTree(getBreadCrumbs(results.getNavigation()));

		return categoryParent;
	}
	
	@SuppressWarnings("unchecked")
	public CategoryRefinementVO getSiblings(final long dimValId,final SearchQuery pSearchQuery,final CategoryParentVO categoryParent,
			final List<CategoryRefinementVO> catRefineList, final String name,final int loopSize) throws CartridgeHandlerException{
		
		CategoryRefinementVO categoryRefine = null;
		final CategoryRefinementVO catRefine = new CategoryRefinementVO();
			final DimValIdList dvalIds = new DimValIdList();
	        dvalIds.addDimValueId(0);
			
		    RefinementMdexQuery refinement = new RefinementMdexQuery();
		    refinement.setDimensionId(""+dimValId);
		    //refinement.setNumRefinements(1000);

		    MdexRequest siblingsRequest = createMdexRequest(new FilterState(), refinement);
		    ENEQueryResults siblingsResults = executeMdexRequest(siblingsRequest);
		    
		    final DimensionList dims = siblingsResults.getNavigation().getRefinementDimensions();
		    
			final ListIterator<Dimension> iter =  dims.listIterator();
			String catName = null;
			long catId = 0;
			while(iter.hasNext()){
				final Dimension dim = iter.next();
				if((dim.getName().equalsIgnoreCase(getEndecaSearch().getConfigUtil().getDepartmentConfig().get(pSearchQuery.getSiteId())))){
					final DimValList dimvalList = dim.getRefinements();
					final ListIterator<DimVal> iterDim =  dimvalList.listIterator();
					while(iterDim.hasNext()){
						//get the details for selected category Id
						final DimVal dimVal = iterDim.next();
						if(pSearchQuery.getCatalogRef().get(CATA_REF_ID).equalsIgnoreCase(String.valueOf(dimVal.getId()))){
							catName = dimVal.getName();
							catId = dimVal.getId();
							catRefine.setName(catName);
							catRefine.setQuery(String.valueOf(catId));
							PropertyMap dimPropertyMap = dimVal.getProperties();
							if(getEndecaSearch().getNodeType().equalsIgnoreCase((String)dimPropertyMap.get(getEndecaSearch().getDimPropertyMap().get(CI_PROP_NODE_TYPE)))){
								catRefine.setIsPortraitEligible(TRUE);
							}
							catRefine.setTotalSize((String)dimPropertyMap.get(getEndecaSearch().getDimPropertyMap().get(CI_PROP_ALLCAPS_DGRAPH_BINS)));
							break;
						}
					}
				}
			}
			final ListIterator<Dimension> iter1 =  dims.listIterator();
			while(iter1.hasNext()){
				final Dimension dim = iter1.next();
				if((dim.getName().equalsIgnoreCase(getEndecaSearch().getConfigUtil().getDepartmentConfig().get(pSearchQuery.getSiteId())))){
					final DimValList dimvalList = dim.getRefinements();
					final ListIterator<DimVal> iterDim =  dimvalList.listIterator();
					while(iterDim.hasNext()){
						DimVal dimVal = iterDim.next();
						PropertyMap dimPropertyMap = dimVal.getProperties();
						categoryRefine = new CategoryRefinementVO();
						categoryRefine.setName(dimVal.getName());
						categoryRefine.setQuery(String.valueOf(dimVal.getId()));
						if(getEndecaSearch().getNodeType().equalsIgnoreCase((String)dimPropertyMap.get(getEndecaSearch().getDimPropertyMap().get(CI_PROP_NODE_TYPE)))){
							categoryRefine.setIsPortraitEligible(TRUE);
						}
						categoryRefine.setTotalSize((String)dimPropertyMap.get(getEndecaSearch().getDimPropertyMap().get(CI_PROP_ALLCAPS_DGRAPH_BINS)));
						catRefineList.add(categoryRefine);
						if(iterDim.nextIndex() == loopSize){
							boolean present =false;
							// Iterate through the list of VO's to check if the selected category is present in the list.
							for(CategoryRefinementVO cVo : catRefineList){
								if(String.valueOf(catId).equalsIgnoreCase(cVo.getQuery())){
									present = true;
								}
							}
							//If not present, replace last VO in list with the seelcted category VO.
							if(!present){
								catRefineList.set(catRefineList.size()-1, catRefine);
							}
							break;
		                }
					}
					categoryParent.setCategoryRefinement(catRefineList);
					categoryParent.setName(name);
					categoryParent.setQuery(String.valueOf(dimValId));
				}
        }
		return categoryRefine;
	}
	
	/** This method returns the Category tree in List of strings.
	 * @param nav
	 * @return List<String>
	 */
	@SuppressWarnings({ "rawtypes"})
	private List<String> getBreadCrumbs(final Navigation nav) {

		DimensionList dimensions = null;
		Dimension dimension = null;
		ListIterator iterDimensions = null;
		DimValList ancestors = null;
		DimVal ancestor = null;
		DimVal descriptor = null;
		ListIterator iterAncestors = null;
		final List<String> breadCrumb = new ArrayList<String>();
		String name = null;

		dimensions = nav.getDescriptorDimensions();
		iterDimensions = dimensions.listIterator();
		while (iterDimensions.hasNext()) {
			dimension = (Dimension) iterDimensions.next();
			name = dimension.getName();
			ancestors = dimension.getAncestors();
			descriptor = dimension.getDescriptor();
			iterAncestors = ancestors.listIterator();
			while (iterAncestors.hasNext()) {
				ancestor = (DimVal) iterAncestors.next();
				name = ancestor.getName();
				breadCrumb.add(name);
			}
			name = descriptor.getName();
			breadCrumb.add(name);
		}
		return breadCrumb;
	}
	
	/**
	 * This method will iterate over 'Department' refinement and create FacetParentVO object with available refinements
	 * @param xmlContentItem
	 * @return
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	private FacetParentVO convertToFacetParentVO(Dimension rootDimensionSiteID, SearchQuery pSearchQuery) throws BBBSystemException, BBBBusinessException {
//		List<FacetParentVO> facetParentList = new ArrayList<FacetParentVO>();
		
		final Map<String, String> dimensionMap = getEndecaSearch().getConfigUtil().getDimensionMap();
		final List<String> typeAheadDimensionNames = new ArrayList<String>(
				Arrays.asList(BBB_TYPE_AHEAD_VALUE, BBCA_TYPE_AHEAD_VALUE, BBBABY_TYPE_AHEAD_VALUE, RECORD_TYPE));
		
		
		//SiteContextManager.getCurrentSiteId() would return TBS_ when used in TBS EAR
		final String siteId = pSearchQuery.getSiteId();
		final List<String> catIDstobeSupressed = getEndecaSearch().getConfigUtil().getCatIdsToBeSuppressed(siteId);
		final Collection<String> facets = getEndecaSearch().getConfigUtil().getFacets(siteId);
		String attributes = getEndecaSearch().getConfigUtil().getCatalogTools().getConfigValueByconfigType(getEndecaSearch().getConfigUtil().getDimDisplayMapConfig()).get("Attributes");
		List<String> pList = getEndecaSearch().getConfigUtil().sanitizedAttributeValues(siteId);
		
		Map<String,String> attributeMap=getEndecaSearch().getConfigUtil().getAttributeInfo();
		
		FacetParentVO facetParent = new FacetParentVO();
		List<FacetRefinementVO> facetRefinements = new ArrayList<FacetRefinementVO>();
		
		facetParent.setName(dimensionMap.get(rootDimensionSiteID.getName()));
		
		//ignore current facet if this is not in facets list defined for site
		if(!facets.contains(facetParent.getName()) && !typeAheadDimensionNames.contains(facetParent.getName())) {
			return null;
		}
		
		// This condition added for mobile multiselect requirement
		if(rootDimensionSiteID.getRoot() != null) {
			facetParent.setMultiSelect(rootDimensionSiteID.getRoot().isMultiSelectOr());
	    }
		
//		if(rootDimensionSiteID.getExpandAction() != null) 
//			facetParent.setQuery(rootDimensionSiteID.getExpandAction().getNavigationState());
		
		
//		CustomRefinementMenu customRefinementMenu = null;
//		boolean hasCustomBoost = false;
//		boolean hasCustomBury = false;
//		if(refinementMenu instanceof CustomRefinementMenu) {
//			customRefinementMenu = (CustomRefinementMenu)refinementMenu;
//			hasCustomBoost = (null != customRefinementMenu.getCustomBoostedDvals() 
//					&& customRefinementMenu.getCustomBoostedDvals().size() > 0) ? true : false;
//			hasCustomBury = (null != customRefinementMenu.getCustomBuriedDvals() 
//					&& customRefinementMenu.getCustomBuriedDvals().size() > 0) ? true : false;
//
//			
//		}
		
		FacetRefinementVO facetRefinement = null;
		DimValList dimValList = rootDimensionSiteID.getRefinements();
		
		if(dimValList != null){
		
		for (Object object : dimValList) {
			DimVal refinement = (DimVal)object;
			
			facetRefinement = new FacetRefinementVO();
			facetRefinement.setName(refinement.getName());
			//dimensionname required for intl attributes & pricing
			facetRefinement.setDimensionName(rootDimensionSiteID.getName());
			facetRefinement.setSize(Integer.toString(RefinementBuilder.getCount(refinement)));
			
			//Adding sort string to refinement so that search PLP would get sorted after refining
			if(null != pSearchQuery && !BBBUtility.isEmpty(pSearchQuery.getSortString())) {
				facetRefinement.setQuery(BBBEndecaConstants.SORT_FIELD+"="+pSearchQuery.getSortString());
			}
			
			//facetRefinement.query is not going to be in the same format as before. Needs further processing??
			//facetRefinement.setQuery(refinement.getNavigationState());
//					logDebug("refinement getNavigationState - "+refinement.getNavigationState());
//					logDebug("count - "+refinement.getCount()+" label - "+
//									refinement.getLabel()+" siterootpath - "+refinement.getSiteRootPath()
//									+"properties map - "+refinement.getProperties()+" sitestate - "+refinement.getSiteState()
//									+"contentpath - "+refinement.getContentPath());
			
			boolean addRefinementValue; 
			try {
				addRefinementValue = EndecaSearchUtil.assignFacetRefFilterAndCatalogId(facetRefinement,getNavigationState().selectNavigationFilter(String.valueOf(refinement.getId())).toString(), pSearchQuery);
			} catch(UrlFormatException urlFormatExe) {
				addRefinementValue = false;
			}
			
			//continue incase facet ref filter and catalog id cannot be retrieved
			if(!addRefinementValue) {
				continue;
			}
			
			//add to facetRefinements only as per custom dimension logic
//			if(null != customRefinementMenu) {
//				if(hasCustomBoost) {
//					if(customRefinementMenu.getCustomBoostedDvals().contains(facetRefinement.getCatalogId())) {
//						addRefinementValue = true;
//					}
//				} else if(hasCustomBury) {
//					if(!customRefinementMenu.getCustomBuriedDvals().contains(facetRefinement.getCatalogId())) {
//						addRefinementValue = true;
//					}
//				}
//			} else {
//				addRefinementValue = true;
//			}
			
			if(!catIDstobeSupressed.contains(facetRefinement.getCatalogId())) {
				if(facetRefinement.getName().equals(attributes) && null != pList && !pList.isEmpty()) {
					if(pList.contains(facetRefinement.getName().toLowerCase())){
						facetRefinement.setIntlFlag(BBBCoreConstants.YES_CHAR);
						for (Map.Entry<String,String> entry : attributeMap.entrySet()) {
							if(entry.getKey().contains(facetRefinement.getName())){
								facetRefinement.setIntlFlag(entry.getValue());
						    }
						}
						if(addRefinementValue) {
							facetRefinements.add(facetRefinement);
						}
					} 
				} else {
					 if(addRefinementValue) {
						 facetRefinements.add(facetRefinement);
					 }
				}
			}
			
		}
		}

		facetParent.setFacetRefinement(facetRefinements);

		return facetParent;
	}
	
	/**
	 * 
	 * @param pMdexRequest
	 * @param pSearchQuery
	 * @return
	 */
	protected boolean hasUserSelectedFilters(SearchQuery pSearchQuery, Navigation pNavigation){
		/* PSI 6  || BPS-798 || Changes Start || Implemente department as tree*/
//	logDebug("fectchCategoryTreeStrucutre : " +fectchCategoryTreeStrucutre + "keyword :" +pSearchQuery.getKeyWord());
		/* PSI 6  || BPS-798 || Changes end || Implemente department as tree Structure*/
		if(StringUtils.isNotEmpty(pSearchQuery.getNarrowDown()) || hasUserSelectedFacets(pNavigation, pSearchQuery)){
			return true;
		}
		return false;
	}
	
	/**
	 * @param pMdexRequest
	 * @param pSearchQuery
	 * @return
	 */
	protected boolean hasUserSelectedFacets(Navigation pNavigation, SearchQuery pSearchQuery){
		final String siteId = pSearchQuery.getSiteId();
		final Collection<String> facets = getEndecaSearch().getConfigUtil().getFacets(siteId);
		final Map<String, String> dimensionMap = getEndecaSearch().getConfigUtil().getDimensionMap();


		DimensionList descDims = pNavigation.getDescriptorDimensions();
		if(descDims != null && descDims.size() > 0){
			for (Object obj : descDims) {
				Dimension dim = (Dimension) obj;
				if(SITE_ID.equalsIgnoreCase(dim.getName())) {
					continue;
				}
				if(RECORD_TYPE.equalsIgnoreCase(dim.getName())) {
					continue;
				}
				if(facets.contains(dimensionMap.get(dim.getName()))) {
					return true;
				}
			}
		}
		return false;
	}

   private static void updateSortConfig(RefinementMenuConfig cartridgeConfig, RefinementMdexQuery dimConfig)
     throws CartridgeHandlerException
   {
		RefinementMdexQuery.RefinementSortType sort = RefinementMdexQuery.RefinementSortType.DISABLED;
		if (cartridgeConfig.getSort() != null) {
			String configuredSort = cartridgeConfig.getSort().toUpperCase();
		if (CI_PROP_SORT_VALUE_DYNRANK.equalsIgnoreCase(configuredSort)) {
			sort = RefinementMdexQuery.RefinementSortType.DYNAMIC;
		} else if (CI_PROP_SORT_VALUE_STATIC.equalsIgnoreCase(configuredSort)) {
			sort = RefinementMdexQuery.RefinementSortType.STATIC;
		} else if (!CI_PROP_SORT_VALUE_DEFAULT.equalsIgnoreCase(configuredSort)) {
			throw new CartridgeHandlerException("sort configuration: " + configuredSort + " is invalid.");
		}
		}
		dimConfig.setSortType(sort);
   }
     
   
    
	public EndecaClient getEndecaClient() {
		return mEndecaClient;
	}

	public void setEndecaClient(EndecaClient pEndecaClient) {
		this.mEndecaClient = pEndecaClient;
	}

	public EndecaSearch getEndecaSearch() {
		return mEndecaSearch;
	}

	public void setEndecaSearch(EndecaSearch pEndecaSearch) {
		this.mEndecaSearch = pEndecaSearch;
	}
	public EndecaSearchUtil getEndecaSearchUtil() {
		return mEndecaSearchUtil;
	}

	public void setEndecaSearchUtil(EndecaSearchUtil pEndecaSearchUtil) {
		this.mEndecaSearchUtil = pEndecaSearchUtil;
	}

}
