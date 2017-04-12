package com.bbb.commerce.browse.category;

import java.util.List;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;



public interface IProdToutHelper {

	

	List<ProductVO> getProducts(String siteId, String categoryId) throws BBBSystemException, BBBBusinessException;
}
