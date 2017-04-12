package com.bbb.repository.util;

import atg.adapter.gsa.GSAItemDescriptor;
import atg.adapter.gsa.GSARepository;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;

import com.bbb.common.BBBGenericService;

/** BPS-1394 Cache Invalidation of registry items from local repository
 * @author smis26
 *
 */
public class RepositoryInvalidatorSevice extends BBBGenericService {
	
			
	/** This method is used to clean invalidate Cached Item 
	 * @param pId
	 * @param itemDescriptor
	 * @param repository
	 */
	public void invalidateCachedItem(String pId, String itemDescriptor, MutableRepository repository){		
		try {
			this.logDebug("RepositoryInvalidatorSevice.invalidateCachedItem() method Start: pId - " + pId + " itemDescriptor- " + itemDescriptor);

			GSAItemDescriptor gsaItemDescriptor = (GSAItemDescriptor)(((GSARepository)repository).getItemDescriptor(itemDescriptor));
			gsaItemDescriptor.removeItemFromCache(pId);
			
		} catch (RepositoryException e) {
			logError("Error while removing inventory [" + pId + "] from cache");
		}
		
		this.logDebug("RepositoryInvalidatorSevice.invalidateCachedItem() method End");
	}
	
}
