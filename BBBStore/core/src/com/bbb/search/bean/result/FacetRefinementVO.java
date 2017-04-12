/**
 * 
 */
package com.bbb.search.bean.result;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.utils.BBBUtility;

/**
 * @author agupt8
 *
 */
public class FacetRefinementVO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private String dimensionName;
	private String query;
	private String size;
	private String catalogId;
	private String facetRefFilter;
	private String intlFlag;
	private String shopAllCatUrl;
	public String getIntlFlag() {
		return intlFlag;
	}

	public void setIntlFlag(String intlFlag) {
		this.intlFlag = intlFlag;
	}

	private List<FacetRefinementVO> facetsRefinementVOs;
	
	
	
	public String getDimensionName() {
		return dimensionName;
	}

	public void setDimensionName(String dimensionName) {
		this.dimensionName = dimensionName;
	}

	/**
	 * @return the facetRefFilter
	 */
	public String getFacetRefFilter() {
		return facetRefFilter;
	}

	/**
	 * @param facetRefFilter the facetRefFilter to set
	 */
	public void setFacetRefFilter(String facetRefFilter) {
		this.facetRefFilter = facetRefFilter;
	}

	/**
	 * @return the refinedName
	 */ 
	public String getRefinedName() {
		return StringEscapeUtils.escapeXml(BBBUtility.refineFacetString(this.name));
	}
	
	/**
	 * @return the name for US and Mexico countries and localize the price ranges for other countries
	 */
	public String getName() {
		String dimName =this.name;
		String country=null;
		//check for price  range facets and for mexico specific facets
		if (!BBBUtility.isEmpty(getDimensionName()) && (getDimensionName().equalsIgnoreCase(BBBInternationalShippingConstants.PRICE_RANGE_ATTR) || getDimensionName().equalsIgnoreCase(BBBInternationalShippingConstants.Bed_Bath___Beyond_Type_Ahead)) && !dimName.contains(BBBInternationalShippingConstants.CURRENCY_MEXICO)) {
			DynamoHttpServletRequest pRequest=ServletUtil.getCurrentRequest();
			if(pRequest!=null ){
				Profile profileFromReq = (Profile)pRequest.getAttribute(BBBInternationalShippingConstants.PROFILE);
				if(profileFromReq == null){
					profileFromReq = (Profile) pRequest.resolveName(BBBCoreConstants.ATG_PROFILE);
					pRequest.setAttribute(BBBInternationalShippingConstants.PROFILE, profileFromReq);
				}
				if(profileFromReq!=null){
					country=(String) profileFromReq.getPropertyValue(BBBInternationalShippingConstants.COUNTRY_CODE);
				}
			}
			//check for country is not US or blank,tehn only localize the prices
			if(BBBUtility.isNotEmpty(country) && !country.equalsIgnoreCase(BBBInternationalShippingConstants.DEFAULT_COUNTRY) ){
				Properties prop = new Properties();
				prop.setProperty(BBBInternationalShippingConstants.ROUND,BBBInternationalShippingConstants.DOWN);
				Pattern pattern = Pattern.compile(BBBCoreConstants.PATTERN_FORMAT);
				Matcher matcher = null;
				
				matcher = pattern.matcher(dimName);
				int i = 0;
				String lowPrice =BBBCoreConstants.BLANK, highPrice = BBBCoreConstants.BLANK;
				while (matcher.find()) {
					if (i == 0) {
						lowPrice = matcher.group();
						lowPrice=lowPrice.substring(1);
						i++;
					} else if (i == 1){
						highPrice = matcher.group();
						highPrice=highPrice.substring(1);
					}
	
				}
				 dimName=BBBUtility.convertToInternationalPrice(dimName, lowPrice, highPrice, prop);
		 }
		}

		return dimName;
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
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(final String size) {
		this.size = size;
	}
	/**
	 * @return the catalogId
	 */
	public String getCatalogId() {
		return catalogId;
	}
	/**
	 * @param catalogId the catalogId to set
	 */
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
  
	/* PSI 6  || BPS-798 || Changes Start || Implemented department as tree*/
   public List<FacetRefinementVO> getFacetsRefinementVOs() {
	return facetsRefinementVOs;
   }

   public void setFacetsRefinementVOs(List<FacetRefinementVO> facetsRefinementVOs) {
	this.facetsRefinementVOs = facetsRefinementVOs;
   }

	@Override
	public String toString() {
		return "FacetRefinementVO [name=" + name + ", query=" + query + ", size="
				+ size + ", catalogId=" + catalogId + ", facetRefFilter="
				+ facetRefFilter + ", facetsRefinementVOs=" + facetsRefinementVOs
				+ "]";
	}
	/* PSI 6  || BPS-798 || Changes end || Implemented department as tree*/

	public String getShopAllCatUrl() {
		return shopAllCatUrl;
	}

	public void setShopAllCatUrl(String shopAllCatUrl) {
		this.shopAllCatUrl = shopAllCatUrl;
	}
}
