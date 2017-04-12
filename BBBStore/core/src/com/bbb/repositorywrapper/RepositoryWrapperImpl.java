package com.bbb.repositorywrapper;

import java.util.Arrays;

import atg.adapter.gsa.LoadingStrategyContext;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.NamedQueryView;
import atg.repository.Query;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.common.BBBGenericService;
import com.bbb.utils.BBBUtility;

/**
* The repository wrapper impl class to encapsulate the creation, read, update, deletion
* and query operations.
*
* @author Prashanth K Bhoomula
* @Version: 1.0
* @Modified Date: June 19, 2012
*/

public class RepositoryWrapperImpl extends BBBGenericService implements IRepositoryWrapper {
	private Repository	wrappedRepository	= null;



	public RepositoryWrapperImpl(final Repository wrappedRepository) {
		this.wrappedRepository = wrappedRepository;
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#getOriginalRepository()
	 */
	@Override
	public Repository getOriginalRepository() {
		return this.wrappedRepository;
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#createItem(java.lang.String,
	 *      com.bbb.repositorywrapper.CreateRepositoryItemCallback)
	 */
	@Override
	public RepositoryItemWrapper createItem(final String descriptorName, final ICreateRepositoryItemCallback callbackHandler) {
		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}

		if (!MutableRepository.class.isInstance(this.wrappedRepository)) {
			throw (new CustomRepositoryException("The wrappedRepository " + this.wrappedRepository
					+ " is not the instance of class \"atg.repository.MutableRepository\"."));
		}

		MutableRepositoryItem createdItem = null;
		try {
			createdItem = ((MutableRepository) this.wrappedRepository).createItem(descriptorName);
			if (callbackHandler != null) {
				callbackHandler.setCreatedItemProperties(createdItem);
			}
			((MutableRepository) this.wrappedRepository).addItem(createdItem);
		} catch (final RepositoryException e) {
			throw (new CustomRepositoryException("Can not create a new repository item whose descriptor name is "
					+ descriptorName + ".", e));
		}

		
		this.logDebug("The created item is " + createdItem + ".");
		

		return (new RepositoryItemWrapper(createdItem));
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#getItem(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public RepositoryItemWrapper getItem(final String itemId, final String descriptorName) {
		if (BBBUtility.isEmpty(itemId)) {
			throw (new CustomRepositoryException("The parameter \"itemId\" can not be empty."));
		}
		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}

		RepositoryItem item = null;
		try {
			item = this.wrappedRepository.getItem(itemId, descriptorName);
		} catch (final RepositoryException e) {
			throw (new CustomRepositoryException("Can not get repository item whose id is " + itemId
					+ " and descriptor name is " + descriptorName + ".", e));
		}

		
		this.logDebug("Get repository item successfully whose id is " + itemId + " and descriptor name is "
					+ descriptorName + ".");
		

		if (item != null) {
			return (new RepositoryItemWrapper(item));
		} else {
			return null;
		}
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#updateItem(atg.repository.RepositoryItem)
	 */
	@Override
	public void updateItem(final RepositoryItem updatedItem) {
		if (updatedItem == null) {
			throw (new CustomRepositoryException("The parameter \"updatedItem\" can not be null."));
		}
		if (!MutableRepositoryItem.class.isInstance(updatedItem)) {
			throw (new CustomRepositoryException("The repository item " + updatedItem
					+ " is not the instance of class \"atg.repository.MutableRepositoryItem\"."));
		}

		if (!MutableRepository.class.isInstance(this.wrappedRepository)) {
			throw (new CustomRepositoryException("The wrappedRepository " + this.wrappedRepository
					+ " is not the instance of class \"atg.repository.MutableRepository\"."));
		}

		try {
			((MutableRepository) this.wrappedRepository).updateItem((MutableRepositoryItem) updatedItem);
		} catch (final RepositoryException e) {
			throw (new CustomRepositoryException("Can not update repository item " + updatedItem + ".", e));
		}

		
		this.logDebug("Update repository item " + updatedItem + " successfully.");
		
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#deleteItem(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public void deleteItem(final String itemId, final String descriptorName) {
		if (BBBUtility.isEmpty(itemId)) {
			throw (new CustomRepositoryException("The parameter \"itemId\" can not be empty."));
		}
		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}

		if (!MutableRepository.class.isInstance(this.wrappedRepository)) {
			throw (new CustomRepositoryException("The wrappedRepository " + this.wrappedRepository
					+ " is not the instance of class \"atg.repository.MutableRepository\"."));
		}

		try {
			((MutableRepository) this.wrappedRepository).removeItem(itemId, descriptorName);
		} catch (final RepositoryException e) {
			throw (new CustomRepositoryException("Can not delete the repository item whose id is " + itemId
					+ " and descriptor name is " + descriptorName + ".", e));
		}

		
			this.logDebug("Delete repository item successfully whose id is " + itemId + " and descriptor name is "
					+ descriptorName + ".");
		
	}


	@Override
	public RepositoryItem[] queryRepositoryItems(final String descriptorName, final String rql, final Object[] params) {
		return this.queryRepositoryItems(descriptorName, rql, params, false);
	}

	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#queryRepositoryItems(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public RepositoryItem[] queryRepositoryItems(final String descriptorName, final String rql, Object[] params, final boolean lazyLoad) {
		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}
		if (BBBUtility.isEmpty(rql)) {
			throw (new CustomRepositoryException("The parameter \"rql\" can not be empty."));
		}
		if (params == null) {
			params = new Object[] {};
		}

		RepositoryItem[] items = null;
		try {
			if(lazyLoad) {
				LoadingStrategyContext.pushLoadStrategy("lazy");
			}
			final RepositoryView view = this.wrappedRepository.getView(descriptorName);
			final RqlStatement statement = RqlStatement.parseRqlStatement(rql);
			items = statement.executeQuery(view, params);
		} catch (final RepositoryException e) {
			throw (new CustomRepositoryException("Can not execute the RQL statement " + rql
					+ " to get repository items whose descriptor name are " + descriptorName + ".", e));
		}
		finally {
			if(lazyLoad) {
				LoadingStrategyContext.popLoadStrategy();
			}
		}

		if(null != items){
			this.logDebug("The result of executing the RQL " + rql + " is " + Arrays.toString(items) + ", and the length of result is "
					+ items.length + ".");
		}

		return items;
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#queryRepositoryWrapperItems(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public RepositoryItemWrapper[] queryRepositoryWrapperItems(final String descriptorName, final String rql, Object[] params) {
		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}
		if (BBBUtility.isEmpty(rql)) {
			throw (new CustomRepositoryException("The parameter \"rql\" can not be empty."));
		}
		if (params == null) {
			params = new Object[] {};
		}

		final RepositoryItem[] items = this.queryRepositoryItems(descriptorName, rql, params);

		if ((items == null) || (items.length == 0)) {
			
			this.logDebug("The result of executing the RQL " + rql + " is empty.");
			
			return null;
		} else {
			final RepositoryItemWrapper[] results = new RepositoryItemWrapper[items.length];
			for (int i = 0; i < items.length; i++) {
				if (items[i] != null) {
					results[i] = new RepositoryItemWrapper(items[i]);
				}
			}
			return results;
		}
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#queryFirstRepositoryItem(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public RepositoryItem queryFirstRepositoryItem(final String descriptorName, final String rql, Object[] params) {
		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}
		if (BBBUtility.isEmpty(rql)) {
			throw (new CustomRepositoryException("The parameter \"rql\" can not be empty."));
		}
		if (params == null) {
			params = new Object[] {};
		}

		final RepositoryItem[] items = this.queryRepositoryItems(descriptorName, rql, params);
		if ((items != null) && (items.length > 0)) {
			return items[0];
		}
		return null;
	}



	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#queryLastRepositoryItem(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public RepositoryItem queryLastRepositoryItem(final String descriptorName, final String rql, Object[] params) {
		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}
		if (BBBUtility.isEmpty(rql)) {
			throw (new CustomRepositoryException("The parameter \"rql\" can not be empty."));
		}
		if (params == null) {
			params = new Object[] {};
		}

		final RepositoryItem[] items = this.queryRepositoryItems(descriptorName, rql, params);
		if ((items != null) && (items.length > 0)) {
			return items[items.length - 1];
		}
		return null;
	}


	/**
	 *
	 * @see com.bbb.repositorywrapper.IRepositoryWrapper#queryLastRepositoryItem(java.lang.String,
	 *      java.lang.String, java.lang.Object[])
	 */
	@Override
	public RepositoryItem[] executeNamedQuery(final String descriptorName, final String sqlQueryName) {

		if (BBBUtility.isEmpty(descriptorName)) {
			throw (new CustomRepositoryException("The parameter \"descriptorName\" can not be empty."));
		}
		if (BBBUtility.isEmpty(sqlQueryName)) {
			throw (new CustomRepositoryException("The parameter \"rql\" can not be empty."));
		}
		try {
			final NamedQueryView  view = (NamedQueryView)this.wrappedRepository.getView(descriptorName);
			final Query namedQuery = view.getNamedQuery(sqlQueryName);
			final RepositoryItem[] items = view.executeQuery(namedQuery);
			return items;
		} catch (final RepositoryException e) {
			throw (new CustomRepositoryException("Can not create a new repository item whose descriptor name is "
					+ descriptorName + ".", e));
		}
	}


}