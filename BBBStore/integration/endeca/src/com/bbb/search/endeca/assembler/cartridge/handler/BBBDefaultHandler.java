package com.bbb.search.endeca.assembler.cartridge.handler;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.bean.query.SearchQuery;
import com.bbb.search.bean.result.SearchResults;
import com.bbb.search.endeca.EndecaContentResponseParser;
import com.bbb.search.endeca.constants.BBBEndecaConstants;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.ContentSlotHandler;
import com.endeca.infront.content.ContentException;
import com.endeca.infront.content.MdexContentRequestBroker;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

/**
 * DefaultHandler that gets called for custom ThreeColumnContentItem
 * Parser is called from process of this handler and converted SearchResults VO is returned back
 * 
 * @author sc0054
 *
 */
public class BBBDefaultHandler extends ContentSlotHandler {
	
	private EndecaContentResponseParser contentParser;
	
	public static final String CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO = "searchResults";
	public static final String CONTENT_ITEM_PARAM_NAME_REDIRECTS_VO = "redirects";
	
	@Override
	public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException
	{
		//call parser here to avoid including ENEQueryResults in content item
		try {
			//lookup SearchQuery in current request 
			//this would be null for getBrands query - NOT ANY MORE
			DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
			SearchQuery searchQuery = (SearchQuery)dynamoRequest.getAttribute(BBBEndecaConstants.RQST_PARAM_NAME_SEARCH_QUERY_VO);
			
			SearchResults searchResults = getContentParser().extractContentResponse(pContentItem, searchQuery);
			
			if(contentBroker instanceof MdexContentRequestBroker) {
				//fetch redirects from Endeca and parse them for splitting on delimiter
				String keywordRedirectFromEndeca = ((MdexContentRequestBroker)contentBroker).getRedirectPath();
				searchResults.setRedirUrl(getContentParser().getRedirectUrl(keywordRedirectFromEndeca, searchQuery));
			}
			pContentItem.put(CONTENT_ITEM_PARAM_NAME_SEARCH_RESULTS_VO,searchResults);
			
		} catch (BBBSystemException | BBBBusinessException e) {
			throw new CartridgeHandlerException("Exception thrown in DefaultHandler " +
					"while trying to extract content response in process method - "+e.getMessage(),e);
		} catch (ContentException e) {
			throw new CartridgeHandlerException("Exception thrown in DefaultHandler " +
					"while trying to fetch redirects using contentBroker in process method - "+e.getMessage(),e);
		}
		return pContentItem;
	}

	/**
	 * @return the contentParser
	 */
	public EndecaContentResponseParser getContentParser() {
		return this.contentParser;
	}

	/**
	 * @param contentParser the contentParser to set
	 */
	public void setContentParser(EndecaContentResponseParser contentParser) {
		this.contentParser = contentParser;
	}

}
