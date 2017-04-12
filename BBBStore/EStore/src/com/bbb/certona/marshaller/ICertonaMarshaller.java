package com.bbb.certona.marshaller;

import java.sql.Timestamp;

/**
 * Marshalls the feed and writes that to feed file
 * 
 * @author skuma7
 *
 */
public interface ICertonaMarshaller  {
	
	void marshall(boolean isFullDataFeed, Timestamp schStartDate);
	
}
