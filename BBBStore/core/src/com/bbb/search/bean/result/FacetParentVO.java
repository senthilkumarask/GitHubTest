/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;

/**
 * @author agupt8
 *
 */
public class FacetParentVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String query;
	private List<FacetRefinementVO> facetRefinement;
	private boolean multiSelect;
	
	private String facetFilter;
    private String facetEndecaId;
    private List<CurrentDescriptorVO> facetDescriptors;
    private String facetRefinedName;
    private DisplayPropertiesVO displayProperties;
    
	/**
	 * @return the displayProperties
	 */
	public DisplayPropertiesVO getDisplayProperties() {
		return displayProperties;
	}
	/**
	 * @param displayProperties the displayProperties to set
	 */
	public void setDisplayProperties(DisplayPropertiesVO displayProperties) {
		this.displayProperties = displayProperties;
	}
	/**
	 * @return the facetDescriptors
	 */
	public List<CurrentDescriptorVO> getFacetDescriptors() {
		return facetDescriptors;
	}
	/**
	 * @param facetDescriptors the facetDescriptors to set
	 */
	public void setFacetDescriptors(List<CurrentDescriptorVO> facetDescriptors) {
		this.facetDescriptors = facetDescriptors;
	}
	/**
	 * @return the facetRefinedName
	 */
	public String getFacetRefinedName() {
		return facetRefinedName;
	}
	/**
	 * @param facetRefinedName the facetRefinedName to set
	 */
	public void setFacetRefinedName(String facetRefinedName) {
		this.facetRefinedName = facetRefinedName;
	}
	/**
	 * @return the facetFilter
	 */
	public String getFacetFilter() {
		return facetFilter;
	}
	/**
	 * @param facetFilter the facetFilter to set
	 */
	public void setFacetFilter(String facetFilter) {
		this.facetFilter = facetFilter;
	}
	 
     /**
	 * @return the facetEndecaId
	 */
	public String getFacetEndecaId() {
		return facetEndecaId;
	}
	/**
	 * @param facetEndecaId the facetEndecaId to set
	 */
	public void setFacetEndecaId(String facetEndecaId) {
		this.facetEndecaId = facetEndecaId;
	} 

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
	/**
	 * @return the query
	 */
	public String getQuery() {
		return query;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(final String query) {
		this.query = query;
	}
	/**
	 * @return the facetRefinement
	 */
	public List<FacetRefinementVO> getFacetRefinement() {
		return facetRefinement;
	}
	/**
	 * @param facetRefinement the facetRefinement to set
	 */
	public void setFacetRefinement(final List<FacetRefinementVO> facetRefinement) {
		this.facetRefinement = facetRefinement;
	}
	/**
	 * @return the isMultiSelect
	 */
	public boolean isMultiSelect() {
		return multiSelect;
	}
	/**
	 * @param isMultiSelect the isMultiSelect to set
	 */
	public void setMultiSelect(final boolean multiSelect) {
		this.multiSelect = multiSelect;
	}
	
	@Override
	public String toString() {
		return "FacetRefinementVO [name "+name+" facetRefinement "+facetRefinement+" ]";
	}
	}
