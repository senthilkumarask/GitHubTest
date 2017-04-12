package com.bbb.simplifyRegistry.tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.cart.utils.RepositoryPriorityComparator;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;

/**
 * This is a manager for kickStarters, which contains all the methods
 * to retrieve the data from kickStarter repository.
 * 
 * @author dwaghmare
 * 
 */

public class SimplifyRegistryTools extends BBBGenericService {

private Repository simplifyRegistryRepository;

public Repository getSimplifyRegistryRepository() {
	return simplifyRegistryRepository;
}




public void setSimplifyRegistryRepository(Repository simplifyRegistryRepository) {
	this.simplifyRegistryRepository = simplifyRegistryRepository;
}

	@SuppressWarnings({ "unchecked", "null" })
	public RepositoryItem getRegInputsByType(String registryType)
		throws RepositoryException {
		
		BBBPerformanceMonitor.start( SimplifyRegistryTools.class.getName() + " : " + "getInputFields(registryType)");
		
		logDebug("starting method getInputFields, Passed parameters: "
						+ ", registryType=" + registryType);
			RepositoryItem[] registryInputsByTypeList =null;
			RepositoryItem registryInputsByType =null;
			RepositoryView view = getSimplifyRegistryRepository().getView(BBBGiftRegistryConstants.REGISTRY_INPUTS_BY_REG_TYPE);
			RqlStatement statement = RqlStatement
					.parseRqlStatement(BBBGiftRegistryConstants.RQL_QUERY_SIMPLIFYREGISTRY);
		
			Object params[] = new Object[1];
			params[0] = registryType;

			try {
				registryInputsByTypeList = executeQuery(view, statement, params);

			} catch (IllegalArgumentException iLLArgExp) {
					
					logError(LogMessageFormatter.formatMessage(null, "getRegInputsByType:","catalog_1065" ),iLLArgExp);

			}
			
			if(registryInputsByTypeList!=null && registryInputsByTypeList.length>0){
				registryInputsByType=registryInputsByTypeList[0];
			}
			BBBPerformanceMonitor.end( SimplifyRegistryTools.class.getName() + " : " + "getInputFields(registryType)");
			
			return registryInputsByType;
		}




	protected RepositoryItem[] executeQuery(RepositoryView view, RqlStatement statement, Object[] params)
			throws RepositoryException {
		return statement.executeQuery(view, params);
	}
	
}