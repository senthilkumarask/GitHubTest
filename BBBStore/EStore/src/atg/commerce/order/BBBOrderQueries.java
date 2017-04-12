package atg.commerce.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import atg.commerce.CommerceException;
import atg.core.util.ResourceUtils;
import atg.repository.Query;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.SortDirective;
import atg.repository.SortDirectives;

import com.bbb.commerce.catalog.BBBConfigTools;
import com.bbb.commerce.order.BBBOrderManager;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.ecommerce.order.BBBOrderTools;
import com.bbb.utils.BBBUtility;

public class BBBOrderQueries extends OrderQueries {

	private BBBConfigTools bbbCatalogTools;
	@SuppressWarnings("unchecked")
	@Override
	public List getOrdersForProfileInState(String pProfileId, int pStartIndex,
			int pNumOrders, int[] pStates, String pOrderByProperty,
			boolean pAscending, Collection<String> pSiteIds,
			Collection<String> pOrganizationIds) throws CommerceException {
		
		List orderList = new ArrayList();
		
		
		//Load orders from order Repository
		orderList = super.getOrdersForProfileInState(pProfileId, pStartIndex, pNumOrders,
				pStates, pOrderByProperty, pAscending, pSiteIds, pOrganizationIds);
		
		final String fetchArchivedOrder = getBbbCatalogTools().getConfigKeyValue(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, BBBCoreConstants.FETCH_ARCHIVED_ORDERS, BBBCoreConstants.FALSE);
		
		if(Boolean.valueOf(fetchArchivedOrder)) {
			// Load orders from archive order repository
			int numLoadedOrder = orderList.size();
			
			// In the case all orders are not loaded or 
			if(numLoadedOrder < pNumOrders || pStartIndex > pNumOrders) {
				
				List archiveOrderList = new ArrayList();
						
				archiveOrderList = ((BBBOrderManager) getOrderManager()).loadArchiveOrders(getArchiveOrderIdsForProfile(pProfileId, pStartIndex, 
						pNumOrders, pStates, pOrderByProperty, pAscending, pSiteIds, pOrganizationIds, numLoadedOrder));
				
				if(!BBBUtility.isListEmpty(archiveOrderList)) {
					
					if(!BBBUtility.isListEmpty(orderList)) {
						orderList.addAll(archiveOrderList);
					} else {
						orderList = archiveOrderList;
					}
					
				}
				
			}
		}
		return orderList;
	}
	
	
	/**
	 * Method to load order ids from archive repository
	 * @param pProfileId
	 * @param pStartIndex
	 * @param pNumOrders
	 * @param pStates
	 * @param pOrderByProperty
	 * @param pAscending
	 * @param pSiteIds
	 * @param pOrganizationIds
	 * @param alreadyLoadedOrderCount
	 * @return
	 * @throws CommerceException
	 */
	@SuppressWarnings("unchecked")
	public List<String> getArchiveOrderIdsForProfile(String pProfileId,	int pStartIndex, int pNumOrders, int[] pStates,
			String pOrderByProperty, boolean pAscending,Collection<String> pSiteIds, Collection<String> pOrganizationIds, int alreadyLoadedOrderCount)
	      throws CommerceException    {
		
		  if ((pProfileId == null)	|| (pProfileId.trim().length() == 0)) {
			  throw new InvalidParameterException(
					ResourceUtils.getMsgResource("InvalidProfileIdParameter","atg.commerce.order.OrderResources",
							sResourceBundle));
	      }
		      
		  boolean reverse_sort = false;
		  RepositoryItem[] items = null;
		      try {
			     
		       RepositoryView rv = ((BBBOrderTools) getOrderTools()).getArchiveOrderRepository().getView(getOrderTools().getOrderItemDescriptorName());   
			   Query query = getOrderQueryForProfile(pProfileId, pStates, pSiteIds, pOrganizationIds, rv);
			      
			   SortDirectives sds = new SortDirectives();
			  
			  // For sorting purpose
			  if (pOrderByProperty != null) {
				  sds.addDirective(new SortDirective(pOrderByProperty,1));
				      
				  if (!(pAscending))
					  reverse_sort = true;
				  
			 } else if (getOrderManager().getDefaultOrderByProperty() != null) {
				  sds.addDirective(new SortDirective(getOrderManager().getDefaultOrderByProperty(), 1));
				      
				  if (!(getOrderManager().getDefaultOrderByDirection()))
					  reverse_sort = true;
		     }
			      
			      // Count orders to load
				  int numOrders = pNumOrders - alreadyLoadedOrderCount;
				  
				  // adjust start index for archive repository
				  int startIndex = 0;
				  
				  if (pStartIndex > alreadyLoadedOrderCount) {
					  startIndex = pStartIndex - alreadyLoadedOrderCount;
				  }			      
				      
				  if (numOrders < 0)
					  items = rv.executeQuery(query, startIndex, sds);
				  else
					  items = rv.executeQuery(query, startIndex, startIndex + numOrders, sds);
				  
			      }  catch (RepositoryException repositoryException) {
			          throw new CommerceException(repositoryException);
			      }
		      
		  if (items == null) {
			  return new ArrayList();
		  }
		  
		  int size = items.length;
		  ArrayList orderIds = new ArrayList(size);
		  
		  if (reverse_sort) {
			  for (int i = size - 1; i >= 0; --i)
				  orderIds.add(items[i].getRepositoryId());
		  }  else {
			  for (int i = 0; i < size; ++i)
				  orderIds.add(items[i].getRepositoryId());
		}
		  return orderIds;
	}
	
	public BBBConfigTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}

	public void setBbbCatalogTools(BBBConfigTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}
}
