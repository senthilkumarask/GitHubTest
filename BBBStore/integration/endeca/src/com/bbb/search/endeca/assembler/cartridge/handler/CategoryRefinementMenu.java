package com.bbb.search.endeca.assembler.cartridge.handler;

import java.util.List;

import com.bbb.search.bean.result.CategoryParentVO;
import com.bbb.search.bean.result.FacetParentVO;
import com.bbb.search.endeca.assembler.cartridge.config.CategoryRefinementMenuConfig;
import com.endeca.infront.cartridge.RefinementMenu;

public class CategoryRefinementMenu extends RefinementMenu {

	private static final long serialVersionUID = -8534145534448219190L;

	//flat category structure
	private CategoryParentVO mCatList;
	//category tree structure
	private List<FacetParentVO> mFacetParentVOs;
	
	public CategoryRefinementMenu(CategoryRefinementMenuConfig pConfig) {
		 super(pConfig);
	}
	
	public CategoryParentVO getCatList() {
		return mCatList;
	}

	public void setCatList(CategoryParentVO pCatList) {
		this.mCatList = pCatList;
	}
	
	public List<FacetParentVO> getFacetParentVOs() {
		return mFacetParentVOs;
	}

	public void setFacetParentVOs(List<FacetParentVO> pFacetParentVO) {
		this.mFacetParentVOs = pFacetParentVO;
	}
}
