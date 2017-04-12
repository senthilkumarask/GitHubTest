package com.bbb.repository.util;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;

import atg.nucleus.GenericService;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.repository.nucleus.RepositoryRegistryService;

public class BBBCacheLoadingService extends GenericService {
	
	private static final String RANGE =" RANGE ";
	
	public 	Repository[] onDemandRepositories = null;	
	public 	Repository[] initialRepository = null;
	private int mRangeCount = 0;
	private int mPoolSize;
	private ExecutorService threadPool;
	
	@Override
	public void doStartService() throws ServiceException {
		super.doStartService();
		threadPool = Executors.newFixedThreadPool(mPoolSize);
		startRepositoryCaching();
	}
		
	public void startRepositoryCaching() {
		
		logInfo("BBBCacheLoadingService.startRepositoryCaching");
		IRepositoryWrapper rWrapper = null;
		if(initialRepository != null) {
			for(Repository repository: initialRepository) {

				try {
					if(((atg.adapter.gsa.GSARepository)repository).getCacheLocality() == null
							|| !((atg.adapter.gsa.GSARepository)repository).getCacheLocality().equals("external")) {
						logDebug("Skipping Cache Load for  : "+repository.getRepositoryName());
						continue;
					}
				}
				catch(Exception ex) {
					logInfo("Caught Exception while checking cacheLocality on  "+repository.getRepositoryName());
					continue;
				}
				cacheRepository(repository);
			}
		}
		logInfo("BBBCacheLoadingService.startRepositoryCaching");
	}
	
	public void cacheOnDemandRepositories() {
		logInfo("BBBCacheLoadingService.startRepositoryCaching");
		IRepositoryWrapper rWrapper = null;
		if(onDemandRepositories != null) {
			for(Repository repository: onDemandRepositories) {
				try {
					if(((atg.adapter.gsa.GSARepository)repository).getCacheLocality() == null
							|| !((atg.adapter.gsa.GSARepository)repository).getCacheLocality().equals("external")) {
						logDebug("Skipping Cache Load for  : "+repository.getRepositoryName());
						continue;
					}
				}
				catch(Exception ex) {
					logInfo("Caught Exception while checking cacheLocality on  "+repository.getRepositoryName());
					continue;
				}
				cacheRepository(repository);
			}
		}
		logInfo("BBBCacheLoadingService.startRepositoryCaching");
	}

	/**
	 * @param repository
	 * @return
	 */
	private void cacheRepository(Repository repository) {
		
		try {
				logInfo("Starting cache load for : "+repository.getRepositoryName());
				logDebug("Memorey before loading  : "+repository.getRepositoryName()+" :"+getMemoryUsed());
				
				String[] itemDescirptors = repository.getItemDescriptorNames();
				for(String descriptorName: itemDescirptors) {

					loadItemDesciptorData(repository, descriptorName);
				}
		}
		catch(Exception ex) {
			logInfo("Caught Exception while loading "+repository.getRepositoryName());
		}
		finally {
			logDebug("Memorey after loading  : "+repository.getRepositoryName()+" data:"+getMemoryUsed());
			logInfo("Ending cache load for : "+repository.getRepositoryName());
		}
	}
	
	private void loadItemDesciptorData(final Repository repository, final String descriptorName) {
		
		

		final IRepositoryWrapper rWrapper = new RepositoryWrapperImpl(repository);
		Runnable newQueryThread = new Runnable(){
			public void run(){
				int totalCount = 0;
				String rqlQueryRange="all" + RANGE+ totalCount + "+" + getRangeCount();
				totalCount=exeuteQuery(rqlQueryRange);
				int count=totalCount;
				logDebug("Start:"+repository.getRepositoryName()+":"+descriptorName);				
				while(count==getRangeCount()){
					rqlQueryRange="all" + RANGE + totalCount + "+" + getRangeCount();
					count=exeuteQuery(rqlQueryRange);
					totalCount = totalCount +count;
				}
				logDebug("End:"+repository.getRepositoryName()+":"+descriptorName+":"+totalCount);
			}
			
			private int exeuteQuery(String query){
				int itemCount=0;
				RepositoryItem[] items = null;
				try {
					items = rWrapper.queryRepositoryItems(descriptorName, query, new Object[1]);
				} catch(Exception e) {
					logError("Caught Exception while loading "+repository.getRepositoryName()+":"+descriptorName);
				}
				if(items!=null && items.length>=0){
					logDebug("Num of items of type "
								+ descriptorName + " cached are "
								+ items.length);
					itemCount = items.length;
				}else{
					logDebug("No records fetch for items type "+ descriptorName);
				}
				return itemCount;
			}
		};

		threadPool.execute(newQueryThread);

	}
	
	 private static long getMemoryUsed(){
		    long totalMemory = Runtime.getRuntime().totalMemory();
		    long freeMemory = Runtime.getRuntime().freeMemory();
		    return (totalMemory - freeMemory);
	 }	
	
	/**
	 * @return the mRangeCount
	 */
	public int getRangeCount() {
		return mRangeCount;
	}

	/**
	 * @param mRangeCount the mRangeCount to set
	 */
	public void setRangeCount(int pRangeCount) {
		this.mRangeCount = pRangeCount;
	}

	/**
	 * @return the mPoolSize
	 */
	public int getPoolSize() {
		return mPoolSize;
	}

	/**
	 * @param mPoolSize the mPoolSize to set
	 */
	public void setPoolSize(int pPoolSize) {
		this.mPoolSize = pPoolSize;
	}

	public Repository[] getOnDemandRepositories() {
		return onDemandRepositories;
	}

	public void setOnDemandRepositories(Repository[] onDemandRepositories) {
		this.onDemandRepositories = onDemandRepositories;
	}

	public Repository[] getInitialRepository() {
		return initialRepository;
	}

	public void setInitialRepository(Repository[] initialRepository) {
		this.initialRepository = initialRepository;
	}
}
