package com.bbb.search.endeca.assembler.cartridge.config;

import java.util.List;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.cartridge.RecordSpotlight;
import com.endeca.infront.cartridge.RecordSpotlightConfig;
import com.endeca.infront.cartridge.model.Record;
import com.endeca.infront.util.express.PropertyMapView;

/**
 * This class extends RecordSpotlight and stores different type of featured products 
 * based on cartridge type 
 * 
 * @author sc0054
 *
 */
public class FeaturedProducts extends RecordSpotlight {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String NEWEST_RECORDS = "newest_records";
	private static final String POPULAR_RECORDS = "popular_records";
	private static final String TOP_RATED_RECORDS = "top_rated_records";
	private static final String TRENDING_RECORDS = "trending_records";
	private static final String SPONSORED_RECORDS = "sponsored_records";

	public FeaturedProducts(RecordSpotlightConfig cartridgeConfig) {
		super(cartridgeConfig);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Record> getNewestRecords()
	{
		return (List)getTypedProperty(NEWEST_RECORDS);
	}

	public void setNewestRecords(List<Record> newestRecords)
	{
		put(NEWEST_RECORDS, newestRecords);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Record> getPopularRecords()
	{
		return (List)getTypedProperty(POPULAR_RECORDS);
	}

	public void setPopularRecords(List<Record> popularRecords)
	{
		put(POPULAR_RECORDS, popularRecords);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Record> getTopRatedRecords()
	{
		return (List)getTypedProperty(TOP_RATED_RECORDS);
	}

	public void setTopRatedRecords(List<Record> topRatedRecords)
	{
		put(TOP_RATED_RECORDS, topRatedRecords);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Record> getTrendingRecords()
	{
		return (List)getTypedProperty(TRENDING_RECORDS);
	}

	public void setTrendingRecords(List<Record> trendingRecords)
	{
		put(TRENDING_RECORDS, trendingRecords);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Record> getSponsoredRecords()
	{
		return (List)getTypedProperty(SPONSORED_RECORDS);
	}

	public void setSponsoredRecords(List<Record> sponsoredRecords)
	{
		put(SPONSORED_RECORDS, sponsoredRecords);
	}


}
