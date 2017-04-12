package com.bbb.search.endeca.assembler.cartridge.config;

import java.io.Serializable;

import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.ContentSlotConfig;

/**
 * Custom content item type created before calling invokeAssembler
 * This is used to indicate which handler to invoke by assembler API
 * 
 * @author sc0054
 *
 */
public class ThreeColumnPageContentItem extends ContentSlotConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ThreeColumnPageContentItem(ContentItem pContentItem)
	{
		super(pContentItem);
	}
	
	public ThreeColumnPageContentItem(String type) {
		super(type);
	}

}
