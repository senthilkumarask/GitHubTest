package com.bbb.search.endeca.assembler.cartridge.handler;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * Passthrough handler for returning FlyoutFeaturedItemsContentItem
 * @author tg0055
 *
 */
public class FlyoutFeaturedItemsHandler extends
		NavigationCartridgeHandler<FlyoutFeaturedItemContent, FlyoutFeaturedItemContent> {

	@Override
	protected FlyoutFeaturedItemContent wrapConfig(ContentItem contentItem) {

		return new FlyoutFeaturedItemContent(contentItem);
	}
	
	public FlyoutFeaturedItemContent process(FlyoutFeaturedItemContent pCartridgeConfig) throws CartridgeHandlerException {
		return pCartridgeConfig;
	}	

}
