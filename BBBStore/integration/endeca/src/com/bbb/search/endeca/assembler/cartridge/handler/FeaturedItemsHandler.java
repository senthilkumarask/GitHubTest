package com.bbb.search.endeca.assembler.cartridge.handler;

import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;

import com.bbb.search.endeca.assembler.cartridge.config.FeaturedItemsContentItem;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * Passthrough handler for returning FeaturedItemsContentItem
 * @author sc0054
 *
 */
public class FeaturedItemsHandler extends
		NavigationCartridgeHandler<ContentItem, FeaturedItemsContentItem> {
	
	//template IDs looked up from properties file
	private Map<String, String> featuredItemsTemplateMap;

	@Override
	protected FeaturedItemsContentItem wrapConfig(ContentItem contentItem) {

		return new FeaturedItemsContentItem(contentItem);
	}
	
	public FeaturedItemsContentItem process(ContentItem pCartridgeConfig) throws CartridgeHandlerException {
		FeaturedItemsContentItem featuredItems = new FeaturedItemsContentItem(pCartridgeConfig);
		
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.LINK_REFERENCE))) {
			featuredItems.setLinkReference((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.LINK_REFERENCE)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE))) {
			featuredItems.setImage((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.CAPTION))) {
			featuredItems.setCaption((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.CAPTION)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.PROMO_TEXT))) {
			featuredItems.setPromoText((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.PROMO_TEXT)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE_HREF))) {
			featuredItems.setImageHref((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE_HREF)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE_SRC))) {
			featuredItems.setImageSrc((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE_SRC)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE_ALT))) {
			featuredItems.setImageAlt((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.IMAGE_ALT)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.MOBILE_IMAGE_HREF))) {
			featuredItems.setMobileImageHref((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.MOBILE_IMAGE_HREF)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.MOBILE_IMAGE_SRC))) {
			featuredItems.setMobileImageSrc((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.MOBILE_IMAGE_SRC)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.MOBILE_IMAGE_ALT))) {
			featuredItems.setMobileImageAlt((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.MOBILE_IMAGE_ALT)));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.FOOTER_TEXT))) {
			String footerText = (String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.FOOTER_TEXT));
			featuredItems.setFooterText(StringEscapeUtils.unescapeHtml(footerText));
		}
		if(null != pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.RELATED_SEARCHES))) {
			featuredItems.setRelatedSearches((String)pCartridgeConfig.get(getFeaturedItemsTemplateMap().get(BBBEndecaConstants.RELATED_SEARCHES)));
		}
		
		
		return featuredItems;
	}

	/**
	 * @return the featuredItemsTemplateMap
	 */
	public Map<String, String> getFeaturedItemsTemplateMap() {
		return featuredItemsTemplateMap;
	}

	/**
	 * @param featuredItemsTemplateMap the featuredItemsTemplateMap to set
	 */
	public void setFeaturedItemsTemplateMap(Map<String, String> featuredItemsTemplateMap) {
		this.featuredItemsTemplateMap = featuredItemsTemplateMap;
	}	

}
