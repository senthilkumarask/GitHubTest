/**
 * 
 */
package com.bbb.certona.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;

import com.bbb.certona.vo.CertonaCategoryVO;
import com.bbb.certona.vo.CertonaProductVO;
import com.bbb.certona.vo.CertonaSKUVO;
import com.bbb.certona.vo.SiteSpecificProductAttr;
import com.bbb.commerce.catalog.vo.BrandVO;
import com.sapient.common.tests.BaseTestCase;

/**
 * @author njai13
 * 
 */
public class TestCertonaCatalogFeedTools extends BaseTestCase {

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGetQueryForSku(){
		try{
			CertonaCatalogFeedTools catalogTools = (CertonaCatalogFeedTools) getObject("certonaCatalogTools");
			catalogTools.setLoggingDebug(true);
			Calendar calendar=Calendar.getInstance();
			calendar.set(112+1900, 00, 12, 9, 9, 9);
			Timestamp lastModifiedDate=new Timestamp(calendar.getTimeInMillis());
			System.out.println("date  "+lastModifiedDate);
			final RepositoryView catalogView = catalogTools.getCatalogRepository().getView("sku");
			final QueryBuilder queryBuilder = catalogView.getQueryBuilder();
			Query query=catalogTools.getIncrementalSkuQuery(queryBuilder,lastModifiedDate);
			System.out.println("query  for incremental feed of sku---"+query.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	public void testGetRepoForIncrementalFeed() throws Exception{
		CertonaCatalogFeedTools catalogTools = (CertonaCatalogFeedTools) getObject("certonaCatalogTools");
		catalogTools.setLoggingDebug(true);

		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);
		Timestamp lastModifiedDate=new Timestamp(calendar.getTimeInMillis());
		System.out.println("date  "+lastModifiedDate);

		try{
			RepositoryItem[] certonaRepo=catalogTools.getRepoForIncrementalFeed( lastModifiedDate, "category");
			System.out.println("certonaRepo  "+certonaRepo);
			certonaRepo=catalogTools.getRepoForFullFeed("category");
			System.out.println("certonaRepo 1 "+certonaRepo[0].getRepositoryId());
			System.out.println("size  "+certonaRepo.length);

			RepositoryItem productRepositoryItem=catalogTools.getCatalogRepository().getItem("17463667", "product");
			System.out.println("productRepositoryItem  "+productRepositoryItem);
			if(productRepositoryItem!=null){
				SiteSpecificProductAttr siteSpecificProductAttr=catalogTools.defaultSiteProdDetail(productRepositoryItem);
				System.out.println(" value name"+siteSpecificProductAttr.getName());
				System.out.println(" value college id"+siteSpecificProductAttr.getCollegeId());
				System.out.println(" value price range"+siteSpecificProductAttr.getPriceRangeDescription());
				System.out.println(" value is active "+siteSpecificProductAttr.isActiveProduct());
				System.out.println(" value sku high price "+siteSpecificProductAttr.getSkuHighPrice());
				System.out.println(" value shrt desc "+siteSpecificProductAttr.getShortDescription());
				System.out.println(" value low price"+siteSpecificProductAttr.getSkuLowPrice());
				System.out.println(" valuelong desc "+siteSpecificProductAttr.getLongDescription());
				Map <String,SiteSpecificProductAttr> map=catalogTools.getSiteProductAttr(productRepositoryItem,siteSpecificProductAttr);
				if(map!=null && !map.isEmpty()){
					Set<String> keys=map.keySet();
					for(String key: keys){
						System.out.println("key  "+key +" value name"+map.get(key).getName());
						System.out.println("key  "+key +" value college id"+map.get(key).getCollegeId());
						System.out.println("key  "+key +" value price range"+map.get(key).getPriceRangeDescription());
						System.out.println("key  "+key +" value is active "+map.get(key).isActiveProduct());
						System.out.println("key  "+key +" value sku high price "+map.get(key).getSkuHighPrice());
						System.out.println("key  "+key +" value shrt desc "+map.get(key).getShortDescription());
						System.out.println("key  "+key +" value low price"+map.get(key).getSkuLowPrice());
						System.out.println("key  "+key +" valuelong desc "+map.get(key).getLongDescription());
						System.out.println("key  "+key +" value tier "+map.get(key).getTier());
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void testGetCategoryDetails() throws Exception{
		CertonaCatalogFeedTools catalogTools = (CertonaCatalogFeedTools) getObject("certonaCatalogTools");
		catalogTools.setLoggingDebug(true);
		boolean full	= (Boolean) getObject("full");
		boolean incremental=(Boolean) getObject("incremental");
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);
		Timestamp lastModifiedDate=new Timestamp(calendar.getTimeInMillis());
		List<CertonaCategoryVO> certonaCatVO=catalogTools.getCategoryDetails(full, lastModifiedDate);
		System.out.println("certonaCatVO  "+certonaCatVO);
		if(certonaCatVO!=null){
			for(int i=0;i<certonaCatVO.size();i++){
				System.out.println("certonaCatVO "+certonaCatVO.get(i).getCategoryName());
				System.out.println(" cat id"+certonaCatVO.get(i).getCategoryId());
				System.out.println(" associated sites  "+certonaCatVO.get(i).getAssocSites());
			}
		}
		certonaCatVO=catalogTools.getCategoryDetails(incremental, lastModifiedDate);
		System.out.println("certonaCatVO  incremental"+certonaCatVO);
		assertNotNull("Certona category VO is null",certonaCatVO);
		if(certonaCatVO!=null){
			for(int i=0;i<certonaCatVO.size();i++){
				System.out.println("certonaCatVO "+certonaCatVO.get(i).getCategoryName());
				System.out.println(" cat id"+certonaCatVO.get(i).getCategoryId());
				System.out.println(" associated sites  "+certonaCatVO.get(i).getAssocSites());
			}
		}
	}

	public void testGetProductDetails() throws Exception{
		CertonaCatalogFeedTools catalogTools = (CertonaCatalogFeedTools) getObject("certonaCatalogTools");
		catalogTools.setLoggingDebug(true);
		boolean full	= (Boolean) getObject("full");
		boolean incremental=(Boolean) getObject("incremental");
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);
		Timestamp lastModifiedDate=new Timestamp(calendar.getTimeInMillis());
		List<RepositoryItem> certonaProductVO=catalogTools.getProductDetails(full, lastModifiedDate);
		assertNotNull("Certona Product VO is null",certonaProductVO);
		System.out.println("certonaCatVO  "+certonaProductVO);
		if(certonaProductVO!=null){
			for(int i=0;i<certonaProductVO.size();i++){
				System.out.println("certonaProductVO id "+certonaProductVO.get(i).getRepositoryId());
		
				
			}
		}
		certonaProductVO=catalogTools.getProductDetails(incremental, lastModifiedDate);
		assertNotNull("Certona Product VO is null",certonaProductVO);
		System.out.println("certonaCatVO  incremental"+certonaProductVO);
		if(certonaProductVO!=null){
			for(int i=0;i<certonaProductVO.size();i++){
				System.out.println("certonaProductVO id "+certonaProductVO.get(i).getRepositoryId());
				
			}
		}
	}

	public void testGetSkuDetails() throws Exception{
		CertonaCatalogFeedTools catalogTools = (CertonaCatalogFeedTools) getObject("certonaCatalogTools");
		catalogTools.setLoggingDebug(true);
		boolean full	= (Boolean) getObject("full");
		boolean incremental=(Boolean) getObject("incremental");
		Calendar calendar=Calendar.getInstance();
		calendar.set(112+1900, 00, 12, 9, 9, 9);
		Timestamp lastModifiedDate=new Timestamp(calendar.getTimeInMillis());
		List<RepositoryItem > certonaSkuRepoItemList=catalogTools.getSKUDetailsRepo(full, lastModifiedDate);
		assertNotNull("certonaSku repoItem is null",certonaSkuRepoItemList);
		System.out.println("certona Sku Repo Item List   "+certonaSkuRepoItemList);
		if(certonaSkuRepoItemList!=null){
			for(int i=0;i<certonaSkuRepoItemList.size();i++){
				RepositoryItem certonaSKURepoItem=certonaSkuRepoItemList.get(i);
				System.out.println("skuId  "+certonaSKURepoItem.getRepositoryId());
			}
		}
		CertonaSKUVO skuVO=catalogTools.populateSKUVO(certonaSkuRepoItemList.get(0));
		System.out.println("sku id from vo "+skuVO.getSkuId()+" sku id from repo "+certonaSkuRepoItemList.get(0).getRepositoryId());
		assertTrue(skuVO.getSkuId().equals(certonaSkuRepoItemList.get(0).getRepositoryId()));
		certonaSkuRepoItemList=catalogTools.getSKUDetailsRepo(incremental, lastModifiedDate);
		assertNotNull("certonaSku repoItem is null",certonaSkuRepoItemList);
		System.out.println("certona Sku Repo Item List   "+certonaSkuRepoItemList);
		if(certonaSkuRepoItemList!=null){
			for(int i=0;i<certonaSkuRepoItemList.size();i++){
				RepositoryItem certonaSKUVO=certonaSkuRepoItemList.get(i);
				System.out.println("skuId  "+certonaSKUVO.getRepositoryId());
			}
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
