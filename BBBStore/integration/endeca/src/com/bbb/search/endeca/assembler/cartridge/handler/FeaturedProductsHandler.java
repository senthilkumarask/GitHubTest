package com.bbb.search.endeca.assembler.cartridge.handler;

import java.util.List;

import com.bbb.search.endeca.assembler.cartridge.config.FeaturedProducts;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RecordSpotlight;
import com.endeca.infront.cartridge.RecordSpotlightConfig;
import com.endeca.infront.cartridge.RecordSpotlightHandler;
import com.endeca.infront.cartridge.RecordSpotlightSelection;
import com.endeca.infront.navigation.model.SortOption;
import com.endeca.infront.navigation.model.SortSpec;

/**
 * Handler used for converting RecordSpotlight into FeaturedProducts
 * Based on type of cartridgeconfig corresponding featured products are set in response 
 * @author sc0054
 *
 */
public class FeaturedProductsHandler extends RecordSpotlightHandler {
	
	//Template IDs retrieved from properties
	private String newestFeaturedProductsTemplateID;
	private String popularFeaturedProductsTemplateID;
	private String topRatedFeaturedProductsTemplateID;
	private String trendingFeaturedProductsTemplateID;
	private String sponsoredFeaturedProductsTemplateID;
	
	@Override
	public void preprocess(RecordSpotlightConfig cartridgeConfig) throws CartridgeHandlerException {
		
		if (cartridgeConfig != null 
				&& cartridgeConfig.getRecordSelection() != null) {
			RecordSpotlightSelection recordSelection = cartridgeConfig.getRecordSelection();
			SortOption sortOption = recordSelection.getSortOption();
			
			if ((sortOption != null) && (sortOption.getSorts() != null) && (!sortOption.getSorts().isEmpty())) {
				List<SortSpec> sortSpecs = sortOption.getSorts();
				
				for(SortSpec sort : sortSpecs) {
					if(sort.getKey().equalsIgnoreCase("P_Ratings")) {
						SortSpec numRatingsSort = new SortSpec();
						numRatingsSort.setKey("P_Num_Ratings");
						numRatingsSort.setDescending(true);
						sortSpecs.add(numRatingsSort);
						break;
					}
				}
			}
		}
		
		super.preprocess(cartridgeConfig);
	}
	
	
	@Override
	public RecordSpotlight process(RecordSpotlightConfig cartridgeConfig) throws CartridgeHandlerException 
	{
		RecordSpotlight recordSpotlight = null;
		try {
			recordSpotlight = super.process(cartridgeConfig);	
		} catch(NullPointerException npe) {
			//try setting isAugment as false and test
			//OutOfBox code is throwing NPE when augment is true with L3 record spotlight
			cartridgeConfig.getRecordSelection().setAugment(false);
			recordSpotlight = super.process(cartridgeConfig);
		}
		
		if(recordSpotlight != null) {
			FeaturedProducts featuredProducts = new FeaturedProducts(cartridgeConfig);
			if(recordSpotlight.getSeeAllLink() != null)  featuredProducts.setSeeAllLink(recordSpotlight.getSeeAllLink());
			
			if (cartridgeConfig.getType() != null 
					&& cartridgeConfig.getType().equalsIgnoreCase(getNewestFeaturedProductsTemplateID())) {
				featuredProducts.setNewestRecords(recordSpotlight.getRecords());
			} else if (cartridgeConfig.getType() != null 
					&& cartridgeConfig.getType().equalsIgnoreCase(getPopularFeaturedProductsTemplateID())) {
				featuredProducts.setPopularRecords(recordSpotlight.getRecords());
			} else if (cartridgeConfig.getType() != null 
					&& cartridgeConfig.getType().equalsIgnoreCase(getTopRatedFeaturedProductsTemplateID())) {
				featuredProducts.setTopRatedRecords(recordSpotlight.getRecords());
			} else if (cartridgeConfig.getType() != null 
					&& cartridgeConfig.getType().equalsIgnoreCase(getTrendingFeaturedProductsTemplateID())) {
				featuredProducts.setTrendingRecords(recordSpotlight.getRecords());
			} else if (cartridgeConfig.getType() != null 
					&& cartridgeConfig.getType().equalsIgnoreCase(getSponsoredFeaturedProductsTemplateID())) {
				featuredProducts.setSponsoredRecords(recordSpotlight.getRecords());
			} 
			
			return featuredProducts;
		} 
		
		return null;
	}


	/**
	 * @return the newestFeaturedProductsTemplateID
	 */
	public String getNewestFeaturedProductsTemplateID() {
		return newestFeaturedProductsTemplateID;
	}


	/**
	 * @param newestFeaturedProductsTemplateID the newestFeaturedProductsTemplateID to set
	 */
	public void setNewestFeaturedProductsTemplateID(
			String newestFeaturedProductsTemplateID) {
		this.newestFeaturedProductsTemplateID = newestFeaturedProductsTemplateID;
	}


	/**
	 * @return the popularFeaturedProductsTemplateID
	 */
	public String getPopularFeaturedProductsTemplateID() {
		return popularFeaturedProductsTemplateID;
	}


	/**
	 * @param popularFeaturedProductsTemplateID the popularFeaturedProductsTemplateID to set
	 */
	public void setPopularFeaturedProductsTemplateID(
			String popularFeaturedProductsTemplateID) {
		this.popularFeaturedProductsTemplateID = popularFeaturedProductsTemplateID;
	}


	/**
	 * @return the topRatedFeaturedProductsTemplateID
	 */
	public String getTopRatedFeaturedProductsTemplateID() {
		return topRatedFeaturedProductsTemplateID;
	}


	/**
	 * @param topRatedFeaturedProductsTemplateID the topRatedFeaturedProductsTemplateID to set
	 */
	public void setTopRatedFeaturedProductsTemplateID(
			String topRatedFeaturedProductsTemplateID) {
		this.topRatedFeaturedProductsTemplateID = topRatedFeaturedProductsTemplateID;
	}


	/**
	 * @return the trendingFeaturedProductsTemplateID
	 */
	public String getTrendingFeaturedProductsTemplateID() {
		return trendingFeaturedProductsTemplateID;
	}


	/**
	 * @param trendingFeaturedProductsTemplateID the trendingFeaturedProductsTemplateID to set
	 */
	public void setTrendingFeaturedProductsTemplateID(
			String trendingFeaturedProductsTemplateID) {
		this.trendingFeaturedProductsTemplateID = trendingFeaturedProductsTemplateID;
	}


	/**
	 * @return the sponsoredFeaturedProductsTemplateID
	 */
	public String getSponsoredFeaturedProductsTemplateID() {
		return sponsoredFeaturedProductsTemplateID;
	}


	/**
	 * @param sponsoredFeaturedProductsTemplateID the sponsoredFeaturedProductsTemplateID to set
	 */
	public void setSponsoredFeaturedProductsTemplateID(
			String sponsoredFeaturedProductsTemplateID) {
		this.sponsoredFeaturedProductsTemplateID = sponsoredFeaturedProductsTemplateID;
	}
	
	
	
}

