package com.bbb.repositorywrapper;

import atg.repository.Repository;
import atg.repository.RepositoryItem;

/**
 * The repository wrapper to encapsulate the creation, read, update, deletion
 * and query operations.
 * 
 * @author Prashanth K Bhoomula
 * @Version: 1.0
 * @Modified Date: June 19, 2012
 */
public interface IRepositoryWrapper {

	/**
	 * Get the wrapped repository to supply the operations that owned by it.
	 * 
	 * @return
	 */
	public abstract Repository getOriginalRepository();

	/**
	 * 
	 * Create a new repository item whose descriptor name is descriptorName and
	 * its id generated automatically.
	 * 
	 * @param descriptorName
	 * @param callbackHandler
	 * @return
	 */
	public abstract RepositoryItemWrapper createItem(String descriptorName,
			ICreateRepositoryItemCallback callbackHandler);

	/**
	 * Get the repository item whose id is itemId and descriptor name is
	 * descriptorName.
	 * 
	 * @param itemId
	 * @param descriptorName
	 * @return
	 */
	public abstract RepositoryItemWrapper getItem(String itemId,
			String descriptorName);

	/**
	 * Update the properties of repository item updatedItem to repository.
	 * 
	 * @param updatedItem
	 */
	public abstract void updateItem(RepositoryItem updatedItem);

	/**
	 * Delete the repository item whose id is itemId and descriptor name is
	 * descriptorName.
	 * 
	 * @param itemId
	 * @param descriptorName
	 */
	public abstract void deleteItem(String itemId, String descriptorName);

	/**
	 * Execute the specified RQL "rql" with parameters "params" and return the
	 * RepositoryItem array.<br>
	 * And the RepositoryItem array contains the repository items whose
	 * descriptor name are "descriptorName".
	 * 
	 * @param descriptorName
	 * @param rql
	 * @param params
	 * @return
	 */
	public abstract RepositoryItem[] queryRepositoryItems(
			String descriptorName, String rql, Object[] params);

	public abstract RepositoryItem[] queryRepositoryItems(
			String descriptorName, String rql, Object[] params, boolean lazyLoad);	
	/**
	 * Execute the specified RQL "rql" with parameters "params" and return the
	 * RepositoryItemWrapper array.<br>
	 * And the RepositoryItemWrapper array contains the repository items whose
	 * descriptor name are "descriptorName".
	 * 
	 * @param descriptorName
	 * @param rql
	 * @param params
	 * @return
	 */
	public abstract RepositoryItemWrapper[] queryRepositoryWrapperItems(
			String descriptorName, String rql, Object[] params);

	/**
	 * 
	 * 
	 * 
	 * @param descriptorName
	 * @param rql
	 * @param params
	 * @return
	 */
	public abstract RepositoryItem queryFirstRepositoryItem(
			String descriptorName, String rql, Object[] params);

	/**
	 * 
	 * 
	 * 
	 * @param descriptorName
	 * @param rql
	 * @param params
	 * @return
	 */
	public abstract RepositoryItem queryLastRepositoryItem(
			String descriptorName, String rql, Object[] params);
	
	public RepositoryItem[] executeNamedQuery(String descriptorName, String sqlQueryName);

}
