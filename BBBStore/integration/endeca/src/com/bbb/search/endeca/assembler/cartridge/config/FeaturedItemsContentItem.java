package com.bbb.search.endeca.assembler.cartridge.config;

import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;

/**
 * Custom featured items content item used for storing all image related cartridge config
 * 
 * @author sc0054
 *
 */
public class FeaturedItemsContentItem extends BasicContentItem {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public FeaturedItemsContentItem(ContentItem pContentItem) {
		super(pContentItem);
		
	}

	public String getImageSrc() {
		return (String)getTypedProperty(BBBEndecaConstants.IMAGE_SRC);
	}
	
	public void setImageSrc(String pImageSrc) {
		put(BBBEndecaConstants.IMAGE_SRC,pImageSrc);
	}

	public String getImageHref() {
		return (String)getTypedProperty(BBBEndecaConstants.IMAGE_HREF);
	}

	public void setImageHref(String pImageHref) {
		put(BBBEndecaConstants.IMAGE_HREF,pImageHref);
	}

	public String getImageAlt() {
		return (String)getTypedProperty(BBBEndecaConstants.IMAGE_ALT);
	}
	
	public void setImageAlt(String pImageAlt) {
		put(BBBEndecaConstants.IMAGE_ALT,pImageAlt);
	}
	
	public String getPromoText() {
		return (String)getTypedProperty(BBBEndecaConstants.PROMO_TEXT);
	}
	
	public void setPromoText(String pPromoText) {
		put(BBBEndecaConstants.PROMO_TEXT,pPromoText);
	}
	
	public String getFooterText() {
		return (String)getTypedProperty(BBBEndecaConstants.FOOTER_TEXT);
	}
	
	public void setFooterText(String pFooterText) {
		put(BBBEndecaConstants.FOOTER_TEXT,pFooterText);
	}
	
	public String getLinkReference() {
		return (String)getTypedProperty(BBBEndecaConstants.LINK_REFERENCE);
	}
	
	public void setLinkReference(String pLinkReference) {
		put(BBBEndecaConstants.LINK_REFERENCE,pLinkReference);
	}
	
	public String getImage() {
		return (String)getTypedProperty(BBBEndecaConstants.IMAGE);
	}
	
	public void setImage(String pImage) {
		put(BBBEndecaConstants.IMAGE,pImage);
	}
	
	public String getCaption() {
		return (String)getTypedProperty(BBBEndecaConstants.CAPTION);
	}
	
	public void setCaption(String pCaption) {
		put(BBBEndecaConstants.CAPTION,pCaption);
	}
	
	public String getMobileImageSrc() {
		return (String)getTypedProperty(BBBEndecaConstants.MOBILE_IMAGE_SRC);
	}
	
	public void setMobileImageSrc(String pMobileImageSrc) {
		put(BBBEndecaConstants.MOBILE_IMAGE_SRC,pMobileImageSrc);
	}

	public String getMobileImageHref() {
		return (String)getTypedProperty(BBBEndecaConstants.MOBILE_IMAGE_HREF);
	}

	public void setMobileImageHref(String pMobileImageHref) {
		put(BBBEndecaConstants.MOBILE_IMAGE_HREF,pMobileImageHref);
	}

	public String getMobileImageAlt() {
		return (String)getTypedProperty(BBBEndecaConstants.MOBILE_IMAGE_ALT);
	}
	
	public void setMobileImageAlt(String pMobileImageAlt) {
		put(BBBEndecaConstants.MOBILE_IMAGE_ALT,pMobileImageAlt);
	}
	
	public String getRelatedSearches() {
		return (String)getTypedProperty(BBBEndecaConstants.RELATED_SEARCHES);
	}
	
	public void setRelatedSearches(String pRelatedSearches) {
		put(BBBEndecaConstants.RELATED_SEARCHES,pRelatedSearches);
	}


}
