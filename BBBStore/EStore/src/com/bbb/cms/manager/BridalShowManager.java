package com.bbb.cms.manager;

import com.bbb.common.BBBGenericService;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;

/**
 *  This Class contains all the business logic for Bridal Show
 */
public class BridalShowManager extends BBBGenericService {
	
	/**
	 * This method creates different-2 include query based on passed parameters.  
	 * @param pQueryBuilder
	 * @param pPropertyQueryExpression
	 * @param pConstantQueryExpression
	 * @return Query object
	 * @throws RepositoryException
	 */

	public Query multiValueQuery(final QueryBuilder pQueryBuilder,
			final String pPropertyQueryExpression,
			final String pConstantQueryExpression) throws RepositoryException {

		final QueryExpression propertyExpression = pQueryBuilder
				.createPropertyQueryExpression(pPropertyQueryExpression);
		
		final QueryExpression constantItem = pQueryBuilder
				.createConstantQueryExpression(pConstantQueryExpression);
		
		final Query includesQuery = pQueryBuilder.createIncludesQuery(
				propertyExpression, constantItem);

		return includesQuery;
	}
	  
}
