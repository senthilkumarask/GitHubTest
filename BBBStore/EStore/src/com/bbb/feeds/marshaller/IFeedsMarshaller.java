package com.bbb.feeds.marshaller;

import java.sql.Timestamp;

/**
 * Marshalls the feed and writes that to feed file
 * 
 * @author skuma7
 *
 */
public interface IFeedsMarshaller  {
	
	void marshall(boolean isFullDataFeed, Timestamp schStartDate);
	
}
