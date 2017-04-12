package com.bbb.feeds.marketing.vo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;

public class MarketingFeedVO implements Cloneable {

	Map<String,String> feedDataMap = null;

	public String toString(final List<String> feedHeaders, final String fieldDelimiter) {

		String feedData = "", tempStr = null;
		if(feedHeaders != null) {
			final Iterator<String> iterator = feedHeaders.iterator();
			while(iterator.hasNext()) {
				tempStr = this.feedDataMap.get(iterator.next());
				if(!StringUtils.isEmpty(tempStr)) {
					feedData += tempStr.replaceAll("/(\r|\n)+/g", " ");
				}

				feedData = feedData +fieldDelimiter;
			}
		}
		return feedData;
	}
	
	// Changed method name to deepClone from Clone since it's creating deep clone, 
	// also it does not call super method, so removing override annotation. Fixed as part of PMD.
	public Object deepClone() {

		final MarketingFeedVO obj = new MarketingFeedVO();
		final Map<String,String> feedDataMapClone = new HashMap<String, String>();
		feedDataMapClone.putAll(this.feedDataMap);
		obj.setFeedDataMap(feedDataMapClone);
		return obj;
	}


	public Map<String, String> getFeedDataMap() {
		return this.feedDataMap;
	}

	public void setFeedDataMap(final Map<String, String> feedData) {
		this.feedDataMap = feedData;
	}

	public void addFeedDataMap(final String key, final String value) {
		if(this.feedDataMap == null) {
			this.feedDataMap = new HashMap<String, String>();
		}
		this.feedDataMap.put(key, value);
	}

	public String get(final String key) {

		if((this.feedDataMap == null)
				|| StringUtils.isEmpty(key)) {
			return null;
		}
		return this.feedDataMap.get(key);
	}

}