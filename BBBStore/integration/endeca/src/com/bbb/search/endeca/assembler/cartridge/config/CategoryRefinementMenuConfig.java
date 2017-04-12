package com.bbb.search.endeca.assembler.cartridge.config;

import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.RefinementMenuConfig;

public class CategoryRefinementMenuConfig extends RefinementMenuConfig {

	private static final long serialVersionUID = 7280565591672184600L;
	
	public CategoryRefinementMenuConfig(String pType)
	{
	  super(pType);
	}

	public CategoryRefinementMenuConfig(ContentItem pContentItem)
	{
	  super(pContentItem);
	}
	
	public String getTreeStructureL2ID() {
		if(null != getTypedProperty(BBBEndecaConstants.TREE_STRUCTURE_L2_ID)) {
			return (String)getTypedProperty(BBBEndecaConstants.TREE_STRUCTURE_L2_ID);			
		}
		return null;
	}
	
	public void setTreeStructureL2ID(String pTreeStructureL2ID) {
		put(BBBEndecaConstants.TREE_STRUCTURE_L2_ID,pTreeStructureL2ID);
	}

}
