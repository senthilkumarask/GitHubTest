package com.bbb.search.endeca.assembler.cartridge.handler;

import static com.bbb.search.endeca.EndecaSearchUtil.isKeywordSearchRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.endeca.EndecaSearchUtil;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.NavigationContainer;
import com.endeca.infront.cartridge.NavigationContainerConfig;
import com.endeca.infront.cartridge.NavigationContainerHandler;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.content.support.XmlContentItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * This handler inserts additional refinementmenu items into navigation list 
 * these are required for supporting additional functionality 
 * @author sc0054
 *
 */
public class GuidedNavigationHandler extends NavigationContainerHandler {
	
	//property used for retrieving existing content configuration
	private String navigationContentName;

	//properties used for setting new refinements
	private String dimensionNameProperty;
	private String dimensionIdProperty;
	private String refinementNameProperty;
	
	//this property is set as handler type so that refinement handler is called
	private String refinementTemplateId;
	
	//price range property dimension name
	private String priceRangeDimensionName;
	
	//price range mx dimension name
	private String priceRangeMxDimensionName;
	
	//record type dimension name
	private String recordTypeDimensionName;
	
	private EndecaSearchUtil endecaSearchUtil;
	
	@Override
	public void preprocess(NavigationContainerConfig pContentItem)
			throws CartridgeHandlerException {

		if (null != pContentItem && null != pContentItem.get(getNavigationContentName())) {
			if (pContentItem.get(getNavigationContentName()) instanceof List<?>) {
				List<XmlContentItem> sourceRefinementContentItems = (List<XmlContentItem>) pContentItem.get(getNavigationContentName());
				List<Object> updatedRefinementContentItems = new ArrayList<Object>();
				ListIterator<XmlContentItem> refinementIter = sourceRefinementContentItems.listIterator();
				while (refinementIter.hasNext()) {
					XmlContentItem refinement = refinementIter.next();
					updatedRefinementContentItems.add(refinement);
					if (null != refinement.get(getDimensionNameProperty())) {
						if (refinement.get(getDimensionNameProperty()).equals(
								getPriceRangeDimensionName())) {


							
							BasicContentItem newContentItem = new BasicContentItem(
									refinement.getType());
							newContentItem.put(getDimensionNameProperty(), getPriceRangeMxDimensionName());
							newContentItem.put(getRefinementNameProperty(), getPriceRangeMxDimensionName());
							try {
								//retireve dimension id from Endeca
								String dimensionId = getEndecaSearchUtil().getCatalogId(getPriceRangeMxDimensionName(),null);
								if(null != dimensionId) {
									newContentItem.put(getDimensionIdProperty(), dimensionId);
									updatedRefinementContentItems.add(newContentItem);
								}
							} catch (BBBBusinessException | BBBSystemException e) {
								throw new CartridgeHandlerException("Exception thrown in GuidedNavigationHandler " +
										"while trying to extract dimension id using search util in preprocess method - "+e.getMessage(),e);
							}
						}
					}
				}
				
				DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
				SearchQuery searchQuery = (SearchQuery)dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO);
				
				if(isKeywordSearchRequest(searchQuery)){
					
					BasicContentItem newContentItem = new BasicContentItem(getRefinementTemplateId());
					newContentItem.put(getDimensionNameProperty(), getRecordTypeDimensionName());
					newContentItem.put(getRefinementNameProperty(), getRecordTypeDimensionName());
					try {
						//retireve dimension id from Endeca
						String dimensionId = getEndecaSearchUtil().getCatalogId(getRecordTypeDimensionName(),null);
						if(null != dimensionId) {
							newContentItem.put(getDimensionIdProperty(), dimensionId);
							updatedRefinementContentItems.add(newContentItem);
						}
					} catch (BBBBusinessException | BBBSystemException e) {
						throw new CartridgeHandlerException("Exception thrown in GuidedNavigationHandler " +
								"while trying to extract dimension id using search util in preprocess method - "+e.getMessage(),e);
					}
				}

				pContentItem.put(getNavigationContentName(),updatedRefinementContentItems);
			}
		}

		super.preprocess(pContentItem);

	}

	@Override
	public NavigationContainer process(NavigationContainerConfig pContentItem)
			throws CartridgeHandlerException {
		// build refinement menu using values from
		// pContentItem.get("navigation")
		// Add type-ahead, price range & record type

		if (null != pContentItem.get(getNavigationContentName())
				&& pContentItem.get(getNavigationContentName()) instanceof List<?>) {
			List<Object> refinements = (List<Object>) pContentItem.get(getNavigationContentName());
			
			List<RefinementMenu> navigationRefinements = new ArrayList<RefinementMenu>();

			for (Object refinement : refinements) {
				
				if(refinement instanceof RefinementMenu) {
					navigationRefinements.add((RefinementMenu)refinement);					
				} else if(refinement instanceof XmlContentItem) {
					//indicates dynamic content slot for refinement menu
					if( ((XmlContentItem)refinement).get(BBBEndecaConstants.CI_CONTENTS_NAME) != null) {
						Object internalContentItem = ((XmlContentItem)refinement).get(BBBEndecaConstants.CI_CONTENTS_NAME);
						//"contents" would be of type List and RefinementMenu/CategoryRefinementMenu would be one of the objects
						if(internalContentItem instanceof List<?>) {
							for(Object internalContent : (List)internalContentItem) {
								if(internalContent instanceof RefinementMenu || internalContent instanceof CategoryRefinementMenu) {
									navigationRefinements.add((RefinementMenu)internalContent);
								}
							}
						}
						
					}
				}
			}

			NavigationContainer outputModel = new NavigationContainer(
					pContentItem);
			outputModel.setNavigation(navigationRefinements);
			return outputModel;
		}
		return null;
	}

	/**
	 * @return the navigationContentName
	 */
	public String getNavigationContentName() {
		return navigationContentName;
	}

	/**
	 * @param navigationContentName the navigationContentName to set
	 */
	public void setNavigationContentName(String navigationContentName) {
		this.navigationContentName = navigationContentName;
	}

	/**
	 * @return the dimensionNameProperty
	 */
	public String getDimensionNameProperty() {
		return dimensionNameProperty;
	}

	/**
	 * @param dimensionNameProperty the dimensionNameProperty to set
	 */
	public void setDimensionNameProperty(String dimensionNameProperty) {
		this.dimensionNameProperty = dimensionNameProperty;
	}

	/**
	 * @return the dimensionIdProperty
	 */
	public String getDimensionIdProperty() {
		return dimensionIdProperty;
	}

	/**
	 * @param dimensionIdProperty the dimensionIdProperty to set
	 */
	public void setDimensionIdProperty(String dimensionIdProperty) {
		this.dimensionIdProperty = dimensionIdProperty;
	}

	/**
	 * @return the refinementNameProperty
	 */
	public String getRefinementNameProperty() {
		return refinementNameProperty;
	}

	/**
	 * @param refinementNameProperty the refinementNameProperty to set
	 */
	public void setRefinementNameProperty(String refinementNameProperty) {
		this.refinementNameProperty = refinementNameProperty;
	}

	/**
	 * @return the refinementTemplateId
	 */
	public String getRefinementTemplateId() {
		return refinementTemplateId;
	}

	/**
	 * @param refinementTemplateId the refinementTemplateId to set
	 */
	public void setRefinementTemplateId(String refinementTemplateId) {
		this.refinementTemplateId = refinementTemplateId;
	}

	/**
	 * @return the priceRangeDimensionName
	 */
	public String getPriceRangeDimensionName() {
		return priceRangeDimensionName;
	}

	/**
	 * @param priceRangeDimensionName the priceRangeDimensionName to set
	 */
	public void setPriceRangeDimensionName(String priceRangeDimensionName) {
		this.priceRangeDimensionName = priceRangeDimensionName;
	}

	/**
	 * @return the priceRangeMxDimensionName
	 */
	public String getPriceRangeMxDimensionName() {
		return priceRangeMxDimensionName;
	}

	/**
	 * @param priceRangeMxDimensionName the priceRangeMxDimensionName to set
	 */
	public void setPriceRangeMxDimensionName(String priceRangeMxDimensionName) {
		this.priceRangeMxDimensionName = priceRangeMxDimensionName;
	}

	/**
	 * @return the recordTypeDimensionName
	 */
	public String getRecordTypeDimensionName() {
		return recordTypeDimensionName;
	}

	/**
	 * @param recordTypeDimensionName the recordTypeDimensionName to set
	 */
	public void setRecordTypeDimensionName(String recordTypeDimensionName) {
		this.recordTypeDimensionName = recordTypeDimensionName;
	}

	/**
	 * @return the endecaSearchUtil
	 */
	public EndecaSearchUtil getEndecaSearchUtil() {
		return endecaSearchUtil;
	}

	/**
	 * @param endecaSearchUtil the endecaSearchUtil to set
	 */
	public void setEndecaSearchUtil(EndecaSearchUtil endecaSearchUtil) {
		this.endecaSearchUtil = endecaSearchUtil;
	}

}
