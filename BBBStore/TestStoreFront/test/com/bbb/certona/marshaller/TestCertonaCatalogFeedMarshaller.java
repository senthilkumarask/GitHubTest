/**
 * 
 */
package com.bbb.certona.marshaller;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.constants.BBBCertonaConstants;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author njai13
 * 
 */
public class TestCertonaCatalogFeedMarshaller extends BaseTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}
	public void testFullCategory(){
		CertonaCatalogFeedMarshaller catalogMarshaller = (CertonaCatalogFeedMarshaller) getObject("certonaCatalogMarshaller");
		catalogMarshaller.setLoggingDebug(true);
		boolean isFullDataFeed= (Boolean) getObject("isFullDataFeed");
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);

		Long time=calendar.getTimeInMillis();
		Timestamp schedulerStartDate=new Timestamp(time);
		System.out.println("*********************FULL CATEGORY FEED*****************************************");
		System.out.println("isFullDataFeed: "+isFullDataFeed+"schedulerStartDate:"+schedulerStartDate);
		try{
			catalogMarshaller.marshalCategory(isFullDataFeed,schedulerStartDate);
			assertEquals(true, isRecordExist(schedulerStartDate,catalogMarshaller));
		} catch (Exception e) {
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
	}

	public void testFullProductFeed(){
		CertonaCatalogFeedMarshaller catalogMarshaller = (CertonaCatalogFeedMarshaller) getObject("certonaCatalogMarshaller");
		catalogMarshaller.setLoggingDebug(true);
		boolean isFullDataFeed= (Boolean) getObject("isFullDataFeed");
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);

		Long time=calendar.getTimeInMillis();
		Timestamp schedulerStartDate=new Timestamp(time);
		System.out.println("*********************FULL Product FEED*****************************************");
		System.out.println("isFullDataFeed: "+isFullDataFeed+"schedulerStartDate:"+schedulerStartDate);
		try{
			catalogMarshaller.marshalProduct(isFullDataFeed,schedulerStartDate);
			assertEquals(true, isRecordExist(schedulerStartDate,catalogMarshaller));
		} catch (Exception e) {
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
	}
	
	public void testFullSKUFeed(){
		CertonaCatalogFeedMarshaller catalogMarshaller = (CertonaCatalogFeedMarshaller) getObject("certonaCatalogMarshaller");
		catalogMarshaller.setLoggingDebug(true);
		boolean isFullDataFeed= (Boolean) getObject("isFullDataFeed");
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);

		Long time=calendar.getTimeInMillis();
		Timestamp schedulerStartDate=new Timestamp(time);
		System.out.println("*********************FULL SKU FEED*****************************************");
		System.out.println("isFullDataFeed: "+isFullDataFeed+"schedulerStartDate:"+schedulerStartDate);
		try{
			catalogMarshaller.marshalSku(isFullDataFeed,schedulerStartDate);
			assertEquals(true, isRecordExist(schedulerStartDate,catalogMarshaller));
		} catch (Exception e) {
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
	}
	public void testIncrementalCategory(){
		CertonaCatalogFeedMarshaller catalogMarshaller = (CertonaCatalogFeedMarshaller) getObject("certonaCatalogMarshaller");
		catalogMarshaller.setLoggingDebug(true);
		boolean isFullDataFeed= (Boolean) getObject("isFullDataFeed");
		boolean modDate= (Boolean) getObject("modDate");
		System.out.println("isFullDataFeed:"+isFullDataFeed+"modDate:"+modDate);
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);

		Long time=calendar.getTimeInMillis();
		Timestamp schedulerStartDate=new Timestamp(time);


		catalogMarshaller.setRepoCatlastModDate(modDate);
		catalogMarshaller.setCatLastModDate(schedulerStartDate);
		System.out.println("*********************INCREMENTAL CATEGORY FEED*****************************************");
		try{
			catalogMarshaller.marshalCategory(isFullDataFeed,schedulerStartDate);
			assertEquals(true, isRecordExist(schedulerStartDate,catalogMarshaller));
		} catch (Exception e) {
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
	}
	public void testIncrementalProduct(){
		CertonaCatalogFeedMarshaller catalogMarshaller = (CertonaCatalogFeedMarshaller) getObject("certonaCatalogMarshaller");
		catalogMarshaller.setLoggingDebug(true);
		boolean isFullDataFeed= (Boolean) getObject("isFullDataFeed");
		boolean modDate= (Boolean) getObject("modDate");
		System.out.println("isFullDataFeed:"+isFullDataFeed+"modDate:"+modDate);
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);

		Long time=calendar.getTimeInMillis();
		Timestamp schedulerStartDate=new Timestamp(time);


		catalogMarshaller.setRepoProdlastModDate(modDate);
		catalogMarshaller.setProdLastModDate(schedulerStartDate);
		System.out.println("*********************INCREMENTAL PRODUCT FEED*****************************************");
		try{
			catalogMarshaller.marshalProduct(isFullDataFeed,schedulerStartDate);
			assertEquals(true, isRecordExist(schedulerStartDate,catalogMarshaller));
		} catch (Exception e) {
			e.printStackTrace();
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
	}
	
	public void testIncrementalSKU(){
		CertonaCatalogFeedMarshaller catalogMarshaller = (CertonaCatalogFeedMarshaller) getObject("certonaCatalogMarshaller");
		catalogMarshaller.setLoggingDebug(true);
		boolean isFullDataFeed= (Boolean) getObject("isFullDataFeed");
		boolean modDate= (Boolean) getObject("modDate");
		System.out.println("isFullDataFeed:"+isFullDataFeed+"modDate:"+modDate);
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);

		Long time=calendar.getTimeInMillis();
		Timestamp schedulerStartDate=new Timestamp(time);


		catalogMarshaller.setRepoSkulastModDate(modDate);
		catalogMarshaller.setSkuLastModDate(schedulerStartDate);
		System.out.println("*********************INCREMENTAL SKU FEED*****************************************");
		try{
			catalogMarshaller.marshalSku(isFullDataFeed,schedulerStartDate);
			assertEquals(true, isRecordExist(schedulerStartDate,catalogMarshaller));
		} catch (Exception e) {
			fail("Not Expecting any exception. Exception is : "+e.getMessage());
		}
	}

	public void testGetLastModifiedDate() throws Exception{
		CertonaCatalogFeedMarshaller catalogMarshaller = (CertonaCatalogFeedMarshaller) getObject("certonaCatalogMarshaller");
		catalogMarshaller.setLoggingDebug(true);

		String category= (String) getObject("categoryView");
		String product= (String) getObject("productView");
		String sku= (String) getObject("skuView");
		Timestamp lastModifiedDate=catalogMarshaller.getLastModifiedDate(category);
		System.out.println("lastModifiedDate for category:"+lastModifiedDate);
		assertNotNull(lastModifiedDate);
		lastModifiedDate=catalogMarshaller.getLastModifiedDate(product);
		System.out.println("lastModifiedDate for product:"+lastModifiedDate);
		assertNotNull(lastModifiedDate);
		lastModifiedDate=catalogMarshaller.getLastModifiedDate(sku);
		System.out.println("lastModifiedDate for sku:"+lastModifiedDate);

	}
	/**
	 * Checks if correct record is inserted in the repository
	 * 
	 * @param lastModDate
	 * @return
	 * @throws RepositoryException
	 */
	private boolean isRecordExist(java.sql.Timestamp lastModDate,CertonaCatalogFeedMarshaller catalogMarshaller)
			throws RepositoryException {

		MutableRepository certonaRepository = catalogMarshaller.getCertonaRepository();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

}
