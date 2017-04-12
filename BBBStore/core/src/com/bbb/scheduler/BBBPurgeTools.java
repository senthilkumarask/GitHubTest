package com.bbb.scheduler;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

import atg.nucleus.ServiceMap;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

/**
 *
 * @author Ayush
 * @story Purge_Scheduler
 * @created 19/4/2013
 */

public class BBBPurgeTools extends BBBGenericService{

	private ServiceMap listOfRepository;
    private String mIdColumnName;


	/**
	 * @return the listOfRepository
	 */
	public ServiceMap getListOfRepository() {
		return this.listOfRepository;
	}

	/**
	 * @param listOfRepository the listOfRepository to set
	 */
	public void setListOfRepository(final ServiceMap plistOfRepository) {
		this.listOfRepository = plistOfRepository;
	}

	/**
	 * @return the mIdColumnName
	 */
	public String getIdColumnName() {
		return this.mIdColumnName;
	}

	/**
	 * @param mIdColumnName the mIdColumnName to set
	 */
	public void setIdColumnName(final String pIdColumnName) {
		this.mIdColumnName = pIdColumnName;
	}

	/**
	 * method returns the milliseconds value of no of days
	 * @return
	 */
	public long convDaysToMilliSeconds(final int pDays) {
		return pDays*BBBCoreConstants.MILLISECONDS_IN_ONE_DAY;
	}


	public MutableRepository getPurgeRepository (){
		return (MutableRepository) this.getListOfRepository().get((BBBCoreConstants.PURGE_REPOSITORY).toString());
	}

	public MutableRepositoryItem[] getPurgeSchedulerItems(){
		final MutableRepository purgeRepository = this.getPurgeRepository();
		Query purgeInfoQuery;
		MutableRepositoryItem[] purgeSchedulerItems= null;

		RepositoryView purgeView;
		try {
			purgeView = purgeRepository.getItemDescriptor(BBBCoreConstants.PURGE_DESCRIPTOR).getRepositoryView();
			purgeInfoQuery = purgeView.getQueryBuilder().createUnconstrainedQuery();
			purgeSchedulerItems = (MutableRepositoryItem[]) purgeView.executeQuery(purgeInfoQuery);
		} catch (final RepositoryException e) {
			this.logError("Repository Exception in getPurgeSchedulerItems:",e);
		}
		return purgeSchedulerItems;
	}


	/**
 	 * The method returns the items to be purged after executing query
	 * @param item
	 * @return RepositoryItem[]
	 */

	@SuppressWarnings("unchecked")
	public RepositoryItem[] findPurgeItems(final RepositoryItem item){
		
		this.logDebug("Entry findPurgeItems");
		

		Map <String,String> purgeParamMap = null;
		RepositoryItem[] purgeItems= null;
		final java.util.Date date= new java.util.Date();
		final StringBuffer filterQuery = new StringBuffer();

		final int interval = ((Integer) item.getPropertyValue(BBBCoreConstants.PROPERTY_INTERVAL)).intValue();
		final String itemDescriptor = (String) item.getPropertyValue(BBBCoreConstants.PROPERTY_ITEM_DESCRIPTOR);

		if(item.getPropertyValue(BBBCoreConstants.PROPERTY_PURGE_PARAM) != null){
			purgeParamMap = (Map<String, String>) item.getPropertyValue(BBBCoreConstants.PROPERTY_PURGE_PARAM);
		} else {
			
			this.logDebug("No Parameters defined for this Purge");			

			return null;
		}

		final Set<String> keySet = purgeParamMap.keySet();

		for(final String key:keySet){
			if(!(key.contains(BBBCoreConstants.COLUMN_NAME))) {
				filterQuery.append(key).append(" = ").append("\"").append(purgeParamMap.get(key).toString()).append("\"").append(" AND ");
			}
		}
		if(purgeParamMap.get(BBBCoreConstants.COLUMN_NAME_DATE) != null){
			filterQuery.append(purgeParamMap.get(BBBCoreConstants.COLUMN_NAME_DATE).toString()).append(" <=?0");
		} else {
			
				this.logDebug("Parameter Create Date not defined for this Purge");
			
		}

		if(purgeParamMap.get(BBBCoreConstants.COLUMN_NAME_ID) != null){
			this.setIdColumnName(purgeParamMap.get(BBBCoreConstants.COLUMN_NAME_ID).toString());
		} else {
			
				this.logDebug("Parameter Id not defined for this Purge");
			
		}

		final MutableRepository Repository = (MutableRepository) this.getListOfRepository().get(item.getPropertyValue(BBBCoreConstants.PROPERTY_REPOSITORY).toString());

		final Object params[] = new Object[1];
		params[0] = new Timestamp(date.getTime() - this.convDaysToMilliSeconds(interval));

		try {
			purgeItems = this.executeRQLQuery(filterQuery.toString(), params,itemDescriptor,Repository);
		} catch (final RepositoryException e) {
			this.logError("Repository Exception in findPurgeItems:",e);
		} catch (final BBBSystemException e) {
			this.logError("BBBSystem Exception in findPurgeItems:",e);
		} catch (final BBBBusinessException e) {
			this.logError("BBBBusiness Exception in findPurgeItems:",e);
		}
		
		this.logDebug("Exit findPurgeItems");
		
		return purgeItems;
	}

	public void removeItem(final MutableRepositoryItem purgeItem, final MutableRepositoryItem item) {
		final MutableRepository Repository = (MutableRepository) this.getListOfRepository().get(item.getPropertyValue(BBBCoreConstants.PROPERTY_REPOSITORY).toString());
		try {
			Repository.removeItem((String) purgeItem.getPropertyValue(this.getIdColumnName()),(String) item.getPropertyValue(BBBCoreConstants.PROPERTY_ITEM_DESCRIPTOR));
		} catch (final RepositoryException e) {
			this.logError("Repository Exception in removeItem:",e);
		}


	}

	private RepositoryItem[] executeRQLQuery(final String rqlQuery, final Object[] params,
			final String viewName,final MutableRepository repository) throws RepositoryException, BBBSystemException, BBBBusinessException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					final RepositoryView view = repository.getView(viewName);
					if (view == null) {
						this.logError("View "+viewName+" is null");
					}

					queryResult = statement.executeQuery(view, params);
					if ( queryResult == null) {
						
						this.logDebug("No results returned for query ["+rqlQuery+"]");
						
					}

				} catch (final RepositoryException e) {
					this.logError("Unable to retrieve data",e);
				}
			} else {
				this.logError("Repository has no data");
			}
		} else {
			this.logError("Query String is null");
		}
		return queryResult;
	}



}
