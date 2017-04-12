package com.bbb.commerce.catalog;

import java.util.HashMap;
import java.util.Map;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBSystemException;

public class BBBInternationalCatalogToolsImpl extends BBBGenericService implements BBBInternationalCatalogTools {
private Repository attributeInfoRepository;
private String queryForAttribute;
/**
 * @return the attributeInfoRepository
 */
public Repository getAttributeInfoRepository() {
	return attributeInfoRepository;
}
/**
 * @param attributeInfoRepository the attributeInfoRepository to set
 */
public void setAttributeInfoRepository(Repository attributeInfoRepository) {
	this.attributeInfoRepository = attributeInfoRepository;
}
/**
 * @return the queryForAttribute
 */
public String getQueryForAttribute() {
	return queryForAttribute;
}
/**
 * @param queryForAttribute the queryForAttribute to set
 */
public void setQueryForAttribute(String queryForAttribute) {
	this.queryForAttribute = queryForAttribute;
}

/** The method is returning the  sku attribute list with intl_flag != 'Y'
*
* @return AttributeList
* @throws BBBSystemException */
public Map<String,String> getAttributeInfo() throws BBBSystemException {
	logDebug("Inside BBBInternationalCatalogToolsImpl METHOD:getAttributeInfo STARTS");
	Map<String,String> attributeMap= new HashMap<String,String>();
	try {
		final RepositoryView attributeInfoView = getAttributeInfoRepository().getView(BBBCatalogConstants.ATTRIBUTE_INFO);
		RqlStatement statement;
		statement = RqlStatement.parseRqlStatement(getQueryForAttribute(), true);
		final Object[] params = {BBBCoreConstants.YES_CHAR };
        final RepositoryItem[] attributeListItems = extractDBCall(attributeInfoView, statement, params);
		if (attributeListItems != null && attributeListItems.length > 0) {
			for(RepositoryItem item:attributeListItems){
				attributeMap.put((String)item.getPropertyValue(BBBInternationalShippingConstants.DISPLAY_DESCRIPTION),(String)item.getPropertyValue(BBBInternationalShippingConstants.INTL_FLAG));
			}
		}
	} catch (RepositoryException e) {
		  this.logError("Catalog API Method Name [getAttributeInfo]: RepositoryException "
                  + BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,e);
		  throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
	}
	logDebug("Inside BBBInternationalCatalogToolsImpl METHOD:getAttributeInfo ENDS :attributeMap Size : "+attributeMap.size());
  return attributeMap;
}
/**
 * @param attributeInfoView
 * @param statement
 * @param params
 * @return
 * @throws RepositoryException
 */
protected RepositoryItem[] extractDBCall(final RepositoryView attributeInfoView, RqlStatement statement,
		final Object[] params) throws RepositoryException {
	return statement.executeQuery(
			attributeInfoView,params);
}

}