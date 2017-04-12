package com.bbb.rest.catalog.vo;

import java.util.List;
import java.util.Map;
import java.io.Serializable;
import com.bbb.selfservice.vo.SchoolVO;
/**
 * THis VO holds information about College/ School details, its merchandize details and weblink promotion details.
 * @author agoe21
 *
 */
public class CollegeDetailVO implements Serializable{ 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SchoolVO schoolVO;
	private List<ProductPLPVO> productVOList;
	private Map<String,Object> promoMap;
	
	public Map<String, Object> getPromoMap() {
		return promoMap;
	}
	public void setPromoMap(Map<String, Object> promoMap) {
		this.promoMap = promoMap;
	}
	public SchoolVO getSchoolVO() {
		return schoolVO;
	}
	public void setSchoolVO(SchoolVO schoolVO) {
		this.schoolVO = schoolVO;
	}
	public List<ProductPLPVO> getProductVOList() {
		return productVOList;
	}
	public void setProductVOList(List<ProductPLPVO> productVOList) {
		this.productVOList = productVOList;
	}
}
