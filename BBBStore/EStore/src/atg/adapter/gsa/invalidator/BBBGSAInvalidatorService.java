/**
 * 
 */
package atg.adapter.gsa.invalidator;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.invalidator.GSAInvalidatorService;
import atg.adapter.gsa.invalidator.MultiTypeInvalidationMessage;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

/**
 * Extented GSAInvalidatorService for Invalidating the cache 
 * for the VDC SKUs while doing the Inventory decrement
 * 
 * @author vchan5
 * 
 */
public class BBBGSAInvalidatorService extends GSAInvalidatorService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mInventoryPath;
	
	
	public String getInventoryPath() {
		return mInventoryPath;
	}

	public void setInventoryPath(String inventoryPath) {
		this.mInventoryPath = inventoryPath;
	}

	/**
	 * Product Catalog Repository
	 */
	
	private Repository productCatalog;

	public Repository getProductCatalog() {
		return productCatalog;
	}

	public void setProductCatalog(Repository productCatalog) {
		this.productCatalog = productCatalog;
	}

	public BBBGSAInvalidatorService() throws RemoteException {
		super();
	}

	public void invalidate(MultiTypeInvalidationMessage pInfoMessage) throws RemoteException {
		if (pInfoMessage != null && pInfoMessage.getRepositoryPath() != null) {
			if (!pInfoMessage.getRepositoryPath().equals(getInventoryPath()) || isVdcSKU(pInfoMessage)) {
				super.invalidate(pInfoMessage);
			}
		}
	}

	public boolean isVdcSKU(final MultiTypeInvalidationMessage pInfoMessage) {
		RepositoryItem inventoryRi = null;
		RepositoryItem skuRi = null;
		if (pInfoMessage.getRepositoryPath() != null && pInfoMessage.getRepositoryPath().equals(getInventoryPath())) {
			GSARepository repository = (GSARepository) resolveName(pInfoMessage.getRepositoryPath());

			if (isLoggingDebug()) {
				logDebug(" Reposiotry Path: " + pInfoMessage.getRepositoryPath());
				logDebug("Server ID: " + pInfoMessage.getServerId());
			}
			try {
				@SuppressWarnings("unchecked")
				Map<String, List<String>> invaladiationMap = pInfoMessage.getPerTypeInvalidationMap();
				if (invaladiationMap != null && invaladiationMap.containsKey(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR)) {
					String invId = (String) (invaladiationMap.get(BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR)).get(0);
					if (invId != null) {
						inventoryRi = repository.getItem(invId, BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR);
					}
				}

				if (pInfoMessage.getPerTypeInvalidationMap().containsKey(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME)) {
					Object[] params = new Object[1];
					params[0] = pInfoMessage.getPerTypeInvalidationMap().get(BBBCatalogConstants.ITEM_TRANSLATIONS_INVENTORY_PROPERTY_NAME);
					RepositoryItem[] ri = executeRQLQuery("translations INCLUDES ?0", params, BBBCatalogConstants.INVENTORY_ITEM_DESCRIPTOR, repository);
					if (ri != null && ri.length > 0) {
						inventoryRi = ri[0];
					}
				}
				if (inventoryRi != null) {
					if (isLoggingDebug()) {
						logDebug("Sku ID : " + inventoryRi.getPropertyValue((String) BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME));
					}

					skuRi = getProductCatalog().getItem((String) inventoryRi.getPropertyValue(BBBCatalogConstants.CATALOG_REF_ID_INVENTORY_PROPERTY_NAME),
							BBBCatalogConstants.SKU_ITEM_DESCRIPTOR);
					if (isLoggingDebug()) {
						logDebug("Is VDC sku --> " + skuRi.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME));
					}
				}

			} catch (RepositoryException e) {
				if (isLoggingError()) {
					logError("Error Whlle while the fetching the records from the repository" + e);
				}
			} catch (BBBSystemException e) {
				if (isLoggingError()) {
					logError("System Exception while the fetching the records from the repository" + e);
				}
			} catch (BBBBusinessException e) {
				if (isLoggingError()) {
					logError("Business Exception while the fetching the records from the repository" + e);
				}
			}
			if (skuRi != null && skuRi.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME) != null
					&& skuRi.getPropertyValue(BBBCatalogConstants.VDCSKU_TYPE_SKU_PROPERTY_NAME).equals("Y")) {
				return true;
			}
		}
		return false;
	}

	private RepositoryItem[] executeRQLQuery(final String rqlQuery, final Object[] params, final String viewName, final MutableRepository repository)
			throws RepositoryException, BBBSystemException, BBBBusinessException {
		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		if (rqlQuery != null) {
			if (repository != null) {
				try {
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					RepositoryView view = repository.getView(viewName);
					if (view == null && isLoggingError()) {
						logError("View " + viewName + " is null");
					}
					queryResult = statement.executeQuery(view, params);
					if (isLoggingDebug() && queryResult == null) {
						logDebug("No results returned for query [" + rqlQuery + "]");
					}
				} catch (RepositoryException e) {
					if (isLoggingError()) {
						logError("Unable to retrieve data");
					}
					throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
				}
			} else {
				if (isLoggingError()) {
					logError("Repository has no data");
				}
			}
		} else {
			if (isLoggingError()) {
				logError("Query String is null");
			}
		}
		return queryResult;
	}
}
