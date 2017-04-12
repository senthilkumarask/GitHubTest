package com.bbb.config;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.map.HashedMap;
import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryException;

import com.bbb.common.BBBGenericService;

/**
 * Class to fetch configure keys from cache from configure repository to filter
 * the unused keys.
 * 
 * 
 * 
 */

public class BBBCachedConfigureKeys extends BBBGenericService {

	private static final String CONFIGUREKEYS = "/com/bbb/configurekeys/ConfigureKeys/";
	Map<String, List<String>> mCachedConfiguredKeys;

	/**
	 * Displays the cached configure keys in dyn/admin 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean getCachedConfigureKeys() {
		mCachedConfiguredKeys = new HashedMap();
		GSARepository repo = (GSARepository) Nucleus.getGlobalNucleus()
				.resolveName(CONFIGUREKEYS);
		String[] descriptorNames = repo.getItemDescriptorNames();
		for (int j = 0; j < descriptorNames.length; ++j) {
			GSAItemDescriptor desc;
			try {
				desc = (GSAItemDescriptor) repo
						.getItemDescriptor(descriptorNames[j]);
			} catch (RepositoryException exc) {
				desc = null;
			}
			if (desc != null) {
				List<String> itemCache = (List<String>) desc.getCachedItemIds();
				mCachedConfiguredKeys.put(descriptorNames[j], itemCache);
				Iterator<String> it = itemCache.iterator();
				if (isLoggingDebug()) {
					while (it.hasNext()) {
						logDebug(descriptorNames[j] + "::" + it.next());
					}
				}
			}
		}
		return true;
	}

	public Map<String, List<String>> getCachedConfiguredKeys() {
		return mCachedConfiguredKeys;
	}

	public void setCachedConfiguredKeys(
			Map<String, List<String>> cachedConfiguredKeys) {
		this.mCachedConfiguredKeys = cachedConfiguredKeys;
	}
}
