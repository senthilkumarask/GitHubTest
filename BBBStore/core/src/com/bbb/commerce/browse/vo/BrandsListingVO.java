/*
 * --------------------------------------------------------------------------------
 * Copyright 2011 Bath & Beyond, Inc. All Rights Reserved.
 * 
 * Reproduction or use of this file without explicit written consent is prohibited.
 * 
 * Created by: Archit Goel
 * 
 * Created on: 13-October-2011
 * --------------------------------------------------------------------------------
 */

package com.bbb.commerce.browse.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.vo.BrandVO;

/**
 * Bean to hold the List of Brands and List of Featured Brands.
 * 
 * @author agoe21
 *
 */
public class BrandsListingVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	/* ===================================================== *
		MEMBER VARIABLES
	 * ===================================================== */
	private List<BrandVO> listBrands;
	private List<BrandVO> listFeaturedBrands;
	private Map<String, ArrayList<BrandVO>> alphabetBrandListMap;
	private Map<String, ArrayList<BrandVO>>  numericBrandListMap;
	/* ===================================================== *
		GETTERS and SETTERS
	 * ===================================================== */
	public List<BrandVO> getListBrands() {
		return listBrands;
	}

	public void setListBrands(final List<BrandVO> pListBrands) {
		listBrands = pListBrands;
	}

	public List<BrandVO> getListFeaturedBrands() {
		return listFeaturedBrands;
	}

	public void setListFeaturedBrands(final List<BrandVO> pListFeaturedBrands) {
		listFeaturedBrands = pListFeaturedBrands;
	}

	public Map<String, ArrayList<BrandVO>> getAlphabetBrandListMap() {
		return alphabetBrandListMap;
	}

	public void setAlphabetBrandListMap(Map<String, ArrayList<BrandVO>> alphabetBrandListMap) {
		this.alphabetBrandListMap = alphabetBrandListMap;
	}

	public Map<String, ArrayList<BrandVO>> getNumericBrandListMap() {
		return numericBrandListMap;
	}

	public void setNumericBrandListMap(Map<String, ArrayList<BrandVO>> numericBrandListMap) {
		this.numericBrandListMap = numericBrandListMap;
	}
}