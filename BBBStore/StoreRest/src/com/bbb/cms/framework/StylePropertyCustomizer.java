package com.bbb.cms.framework;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.rest.filtering.RestPropertyCustomizer;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.search.integration.SearchManager;

/**
 * 
 * 
 */
public class StylePropertyCustomizer extends BBBGenericService implements
		RestPropertyCustomizer {
	
	private final String CHANNEL_THEME_HEADER = "X-bbb-channel-theme";
	private final String FACET_PROPERTY_NAME = "facet";
	private final String DESCRIPTION_PROPERTY_NAME = "description";

	public Object getPropertyValue(String pPropertyName, Object pResource) {

		logDebug("Entering StylePropertyCustomizer.getPropertyValue");
		String value = null;
		final String styleName = "styleName";
		final String styleItemDescriptor = "Style";
		String style = null;
		SearchManager searchManager = null;
		Repository styleRepository = null;
		BBBCatalogTools catalogTools = null;
		
		if (null != ServletUtil.getCurrentRequest()) {
			searchManager = (SearchManager) ServletUtil.getCurrentRequest()
					.resolveName("/com/bbb/search/integration/SearchManager");
			styleRepository = (Repository) ServletUtil.getCurrentRequest()
					.resolveName("/com/bbb/stofu/CommonRepository");
			catalogTools = (BBBCatalogTools) ServletUtil.getCurrentRequest()
					.resolveName("/com/bbb/commerce/catalog/BBBCatalogTools");
		}
		RepositoryItem styleItem = null;
		String styleEndecaId = null;
		String styleNameValue = "";
		String channelTheme = ServletUtil.getCurrentRequest().getHeader(CHANNEL_THEME_HEADER);
		try {
			value = (String) DynamicBeans.getSubPropertyValue(pResource, pPropertyName);
			if (null != styleRepository && null != searchManager && null != catalogTools) {
				if(channelTheme!=null && !channelTheme.isEmpty()){
					style=getFacetForChannelTheme(channelTheme);
					styleItem = styleRepository.getItem(value, styleItemDescriptor);
				}
				if (null != styleItem) {
					styleNameValue = (String) styleItem.getPropertyValue(styleName);
					styleEndecaId = searchManager.getCatalogId(style,
							styleNameValue);
				}
			}
			logDebug("StylePropertyCustomizer::: styleName=" + styleNameValue);
			logDebug("StylePropertyCustomizer::: value=" + value);
			logDebug("StylePropertyCustomizer::: styleEndecaId="
					+ styleEndecaId);

		} catch (PropertyNotFoundException e) {
			logError("Property Not Found Exception in StylePropertyCustomizer", e);
		} catch (RepositoryException e) {
			logError("Repsoitory Exception while fetching style item in StylePropertyCustomizer", e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException in StylePropertyCustomizer", e);
		} catch (BBBSystemException e) {
			logError("BBBSystemException in StylePropertyCustomizer", e);
		}
		logDebug("Exiting StylePropertyCustomizer.getPropertyValue");
		return styleEndecaId;
	}

	@Override
	public void setPropertyValue(String arg0, Object arg1, Object arg2) {
		throw new UnsupportedOperationException("Not implemented");
	}
	
	private String getFacetForChannelTheme(String channelTheme){
		String facetName = null;
		RepositoryItem[] items = null;
		Repository channelThemeRepository = (Repository) ServletUtil.getCurrentRequest().resolveName("/com/bbb/channelTheme/ChannelThemeRepository");
		try {
			RepositoryView view = channelThemeRepository.getView("channelThemeInfo");
			QueryBuilder queryBuilder = view.getQueryBuilder();
			String[] id = new String[1];
			id[0] = channelTheme;
			Query query = queryBuilder.createIdMatchingQuery(id);
			items = view.executeQuery(query);
		} catch (RepositoryException e) {
			logError("Error Occured while fetching facets for a particular ChannelTheme in getAllFacetsForChannelTheme in StylePropertyCustomizer", e);
		}
		if(items!=null){
			if(items.length==1){
				RepositoryItem facet = (RepositoryItem)(items[0].getPropertyValue(FACET_PROPERTY_NAME));
				if(facet != null){
					facetName = (((String)(facet).getPropertyValue(DESCRIPTION_PROPERTY_NAME)));
				}else{
					logError("Facet not found for channelTheme:" + channelTheme );
				}
			}else{
				logError("The number of facets for a particular ChannelTheme in "
						+ "getAllFacetsForChannelTheme in StylePropertyCustomizer are "
						+ "not equal to one. Method:: getFacetForChannelTheme");
			}
		}
		return facetName;
	}
}
