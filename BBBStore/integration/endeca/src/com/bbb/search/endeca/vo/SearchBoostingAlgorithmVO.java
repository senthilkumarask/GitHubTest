/**
 * 
 */
package com.bbb.search.endeca.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author pku104
 *
 */
public class SearchBoostingAlgorithmVO implements Serializable {

	private static final long serialVersionUID = 7714048819912744162L;
	private String entryId;
	private String siteSpectCode;
	private Boolean omnitureEventRequired;
	private boolean randomizationRequired;
	private Set<String> sites;
	private List<AlgorithmComponentVO> algorithmComponents;
	private Timestamp lastModifiedDate;
	private int totalBoostedProducts;
	private Map<String, Double> boostPercentageMap;
	private int pageType;
	private String omnitureDescription;

	/**
	 * @return the omnitureDescription
	 */
	public String getOmnitureDescription() {
		return omnitureDescription;
	}

	/**
	 * @param omnitureDescription the omnitureDescription to set
	 */
	public void setOmnitureDescription(String omnitureDescription) {
		this.omnitureDescription = omnitureDescription;
	}

	/**
	 * @return the entryId
	 */
	public String getEntryId() {
		return entryId;
	}

	/**
	 * @param entryId the entryId to set
	 */
	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	/**
	 * @return the siteSpectCode
	 */
	public String getSiteSpectCode() {
		return siteSpectCode;
	}

	/**
	 * @param siteSpectCode the siteSpectCode to set
	 */
	public void setSiteSpectCode(String siteSpectCode) {
		this.siteSpectCode = siteSpectCode;
	}

	/**
	 * @return the omnitureEventRequired
	 */
	public Boolean getOmnitureEventRequired() {
		return omnitureEventRequired;
	}

	/**
	 * @param omnitureEventRequired the omnitureEventRequired to set
	 */
	public void setOmnitureEventRequired(Boolean omnitureEventRequired) {
		this.omnitureEventRequired = omnitureEventRequired;
	}

	/**
	 * @return the sites
	 */
	public Set<String> getSites() {
		return sites;
	}

	/**
	 * @param sites the sites to set
	 */
	public void setSites(Set<String> sites) {
		this.sites = sites;
	}

	/**
	 * @return the algorithms
	 */
	public List<AlgorithmComponentVO> getAlgorithmComponents() {
		return algorithmComponents;
	}

	/**
	 * @param algorithms the algorithms to set
	 */
	public void setAlgorithmComponents(List<AlgorithmComponentVO> algorithmComponents) {
		this.algorithmComponents = algorithmComponents;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public Timestamp getLastModifiedDate() {
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Timestamp lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the totalBoostedProducts
	 */
	public int getTotalBoostedProducts() {
		return totalBoostedProducts;
	}

	/**
	 * @param totalBoostedProducts the totalBoostedProducts to set
	 */
	public void setTotalBoostedProducts(int totalBoostedProducts) {
		this.totalBoostedProducts = totalBoostedProducts;
	}

	/**
	 * @return the boostPercentage
	 */
	public Map<String, Double> getBoostPercentageMap() {
		return boostPercentageMap;
	}

	/**
	 * @param boostPercentage the boostPercentage to set
	 */
	public void setBoostPercentageMap(Map<String, Double> boostPercentageMap) {
		this.boostPercentageMap = boostPercentageMap;
	}

	/**
	 * @return the pageType
	 */
	public int getPageType() {
		return pageType;
	}

	/**
	 * @param pageType the pageType to set
	 */
	public void setPageType(int pageType) {
		this.pageType = pageType;
	}

	/**
	 * @return the randomizationRequired
	 */
	public boolean isRandomizationRequired() {
		return randomizationRequired;
	}

	/**
	 * @param randomizationRequired the randomizationRequired to set
	 */
	public void setRandomizationRequired(boolean randomizationRequired) {
		this.randomizationRequired = randomizationRequired;
	}

}
