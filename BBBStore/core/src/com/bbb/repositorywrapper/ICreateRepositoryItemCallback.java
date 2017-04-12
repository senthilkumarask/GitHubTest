package com.bbb.repositorywrapper;

import atg.repository.MutableRepositoryItem;

/**
* The custom code handler after created an new repository item. 
* @author Prashanth K Bhoomula
* @Version: 1.0
* @Modified Date: June 19, 2012
*/
public interface ICreateRepositoryItemCallback {

	/**
	 * 
	 * Set the properties of created repository item.
	 * 
	 * @param createdItem
	 */
	public abstract void setCreatedItemProperties(MutableRepositoryItem createdItem);	
}
