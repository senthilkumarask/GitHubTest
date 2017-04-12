package com.bbb.search.endeca.assembler.cartridge.handler;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;

public class FlyoutFeaturedItemContent extends BasicContentItem {

	private static final long serialVersionUID = 3774032851073940601L;

	private static final String PROP_NAME = "name";
	private static final String PROP_L1_ID = "L1ID";
	
	public FlyoutFeaturedItemContent(ContentItem pCartridgeConfig){
		super(pCartridgeConfig);
	}
	
	public String getCategoryName() {
		return (String)getTypedProperty(PROP_NAME);
	}

	public String getCategoryId() {
		return (String)getTypedProperty(PROP_L1_ID);
	}
	
}
