/**
 * 
 */
package com.bbb.search.bean.result;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @author agoe21
 *
 */
public class FacetQueryResult implements Serializable{

	private static final long serialVersionUID = 1L;

	private String mFacetName;
	
	private Map<String,String> mMatches;
	
	private List<DeptDimensionVO> orderedMatches;


	/**
	 * @return the facetName
	 */
	public String getFacetName() {
		return mFacetName;
	}

	/**
	 * @param pFacetName the facetName to set
	 */
	public void setFacetName(final String pFacetName) {
		this.mFacetName = pFacetName;
	}

	/**
	 * @return the matches
	 */
	public Map<String, String> getMatches() {
		return mMatches;
	}

	/**
	 * @param pMatches the matches to set
	 */
	public void setMatches(final Map<String, String> pMatches) {
		if(null!=pMatches){
			List<DeptDimensionVO> ordMatches = new ArrayList<DeptDimensionVO>();
			Iterator<Map.Entry<String,String>> match = (Iterator<Map.Entry<String,String>>)pMatches.entrySet().iterator();
			while (match.hasNext()){
				Map.Entry<String,String> entry = (Map.Entry<String,String>)match.next();
				DeptDimensionVO ordMatch = new DeptDimensionVO();
				ordMatch.setDimensionUrl(entry.getKey());
				ordMatch.setDeptName(entry.getValue());
				ordMatches.add(ordMatch);
			}
			this.setOrderedMatches(ordMatches);
		}
		mMatches = pMatches;
	}


	/**
	 * @return the orderedMatches
	 */
	public List<DeptDimensionVO> getOrderedMatches() {
		return orderedMatches;
	}
	

	/**
	 * @param orderedMatches the orderedMatches to set
	 */
	public void setOrderedMatches(List<DeptDimensionVO> orderedMatches) {
		this.orderedMatches = orderedMatches;
	}
	
	

}
