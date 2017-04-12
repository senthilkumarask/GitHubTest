package com.bbb.rest.catalog.vo;

import java.util.Map;
import java.util.Set;

import com.bbb.commerce.catalog.vo.CollegeVO;
/**
 * 
 * @author agoe21
 *
 */
public class CollegeMerchandizeVO{ 
	/**
	 * 
	 */
	private Set<String> collegeBucket;
	private Map<String,Map<String, CollegeVO>> alphabetCollegeListMap;
	
	public Map<String, Map<String, CollegeVO>> getAlphabetCollegeListMap() {
		return alphabetCollegeListMap;
	}
	public void setAlphabetCollegeListMap(
			Map<String, Map<String, CollegeVO>> alphabetCollegeListMap) {
		this.alphabetCollegeListMap = alphabetCollegeListMap;
	}
	public Set<String> getCollegeBucket() {
		return collegeBucket;
	}
	public void setCollegeBucket(Set<String> collegeBucket) {
		this.collegeBucket = collegeBucket;
	}
}
