package com.bbb.certona.marshaller;

import java.sql.Timestamp;
import java.util.Date;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.constants.BBBCertonaConstants;
import com.sapient.common.tests.BaseTestCase;

public class TestCertonaInventoryFeedMarshaller extends BaseTestCase {

	public static final String INVENTORY_FEED_TYPE = "inventory";
	CertonaInventoryFeedMarshaller marshaller;
	private java.sql.Timestamp modInvDate;

	protected void setUp() throws Exception {
		super.setUp();
		marshaller = (CertonaInventoryFeedMarshaller) getObject("marshaller");
	}
	
	/**
	 * Tests full certona inventory feed.  
	 */
	public void testFullFeedMarshall() throws Exception {

		boolean isFullDataFeed = (Boolean) getObject("fullDataFeed");
		java.sql.Timestamp lastModDate = new java.sql.Timestamp(new Date().getTime());
		
		try {
			
			marshaller.marshall(isFullDataFeed, lastModDate);
			assertEquals(true, isRecordExist(lastModDate));
			
		} catch (Exception e) {
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
	}

	/**
	 * Tests incremental Certona inventory feed
	 */
	public void testIncrementalFeedMarshall() throws Exception {

		boolean isFullDataFeed = (Boolean) getObject("fullDataFeed");
		RepositoryItem invenItem = getInvItemToUpdate(); // prepares the data
		try {
			
			Timestamp lastModDate = new Timestamp(new Date().getTime());
			marshaller.marshall(isFullDataFeed, lastModDate );
			assertEquals(true, isRecordExist(lastModDate));
			
		} catch (Exception e) {
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
		
		updateInv(invenItem, modInvDate); // reverts back to original state
	}

	/**
	 * Prepares test data
	 * 
	 * @return
	 * @throws RepositoryException
	 */
	private RepositoryItem getInvItemToUpdate() throws RepositoryException {
		
		Repository inventory = (Repository)resolveName("/atg/commerce/inventory/InventoryRepository");
		RepositoryItem invenItem = null;		
			
		final RepositoryView inventoryView = inventory.getView(BBBCertonaConstants.INVENTORY);
		final QueryBuilder queryBuilder = inventoryView.getQueryBuilder();
		final Query getAllItemsQuery = queryBuilder.createUnconstrainedQuery();
		RepositoryItem[] items = inventoryView.executeQuery(getAllItemsQuery);	
		
		if(items != null && items.length > 0){				
			invenItem = items[0];
			modInvDate = (java.sql.Timestamp)invenItem.getPropertyValue("lastModifiedDate");
			java.sql.Timestamp updatedTimestamp = new java.sql.Timestamp(new Date().getTime());
			updateInv(invenItem, updatedTimestamp);				
		}  
		
		return invenItem;
	}

	/**
	 * Checks if correct record is inserted in the repository
	 * 
	 * @param lastModDate
	 * @return
	 * @throws RepositoryException
	 */
	private boolean isRecordExist(java.sql.Timestamp lastModDate)
			throws RepositoryException {

		MutableRepository certonaRepository = marshaller.getCertonaRepository();
		RepositoryView view = certonaRepository
				.getView(BBBCertonaConstants.FEED);

		RqlStatement statement = RqlStatement
				.parseRqlStatement("schedulerStartDate >= ?0 AND schedulerStartDate <= ?1");

		Object params[] = new Object[2];
		params[0] = lastModDate;
		params[1] = new java.sql.Timestamp(new Date().getTime());

		RepositoryItem[] certonaItems = statement.executeQuery(view, params);

		if (certonaItems != null && certonaItems.length > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates inventory repository for test data preparation 
	 * and rollback
	 * 
	 * @param reposItem
	 * @param timeStamp
	 * @throws RepositoryException
	 */
	private void updateInv(RepositoryItem reposItem, java.sql.Timestamp timeStamp) throws RepositoryException{
		
		MutableRepository mutableRepository = (MutableRepository)reposItem.getRepository();
		MutableRepositoryItem mutableInv = mutableRepository.getItemForUpdate(reposItem.getRepositoryId(),
															reposItem.getItemDescriptor().getItemDescriptorName());
				
		mutableInv.setPropertyValue("lastModifiedDate", timeStamp);		
		mutableRepository.updateItem(mutableInv);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
}
