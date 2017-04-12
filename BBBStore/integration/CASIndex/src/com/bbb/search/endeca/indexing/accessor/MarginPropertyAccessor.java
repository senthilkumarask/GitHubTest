package com.bbb.search.endeca.indexing.accessor;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.commerce.pricing.priceLists.PriceListManager;
import atg.commerce.search.IndexConstants;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.repository.search.indexing.Context;
import atg.repository.search.indexing.PropertyAccessorImpl;
import atg.repository.search.indexing.specifier.PropertyTypeEnum;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.utils.BBBUtility;

public class MarginPropertyAccessor extends PropertyAccessorImpl
		implements IndexConstants {

	private MutableRepository catalogRepository;
	private PriceListManager priceListManager;
	private Repository siteRepository;
	BBBCatalogTools catalogTools;

	@SuppressWarnings("unchecked")
	@Override
	public Object getTextOrMetaPropertyValue(Context pContext,
			RepositoryItem pItem, String pPropertyName, PropertyTypeEnum pType) {
		String margin = "";
		String siteId = (String)pContext.getAttribute("atg.siteVariantProdcer.siteValue");

		if (pItem != null) {
			try {
				
				boolean isCollection = (Boolean)(pItem.getPropertyValue(BBBCatalogConstants.COLLECTION_PRODUCT_PROPERTY_NAME));

				boolean isLeadProduct = (Boolean)(pItem.getPropertyValue(BBBCatalogConstants.LEAD_PRODUCT_PRODUCT_PROPERTY_NAME));
				
				  if(getCatalogTools().isProductActive(pItem, siteId)){
					  double finalMargin = 0;
					  if(isCollection){
						  List<RepositoryItem> productChildProducts  = (List<RepositoryItem>) pItem.getPropertyValue(BBBCatalogConstants.PRODUCT_CHILD_PRODUCTS);
						 
							  //logDebug("childProducts Number******** "+productChildProducts.size());
						  
						  
						  if(productChildProducts !=null && !(productChildProducts.isEmpty())){
						  for(RepositoryItem productChildProduct : productChildProducts){

							  RepositoryItem product = (RepositoryItem) productChildProduct.getPropertyValue(BBBCatalogConstants.PRODUCT_ID);
							  if(product == null){
								  continue;
							  }
							  
								  //logDebug("product ID******** "+product.getRepositoryId());
							  
							  double profitOnProduct = 0;
							  List<RepositoryItem> childSKUS = null;
							  childSKUS = (List<RepositoryItem>) product.getPropertyValue(BBBCatalogConstants.CHILD_SKUS);
							  if(childSKUS == null || childSKUS.isEmpty()){
								  continue;
							  }
							  for(RepositoryItem sku : childSKUS){
								 
									  //logDebug("sku id******** "+sku.getRepositoryId());
								  
								  Map<String,Double> priceMap = getMarginForSku(sku,siteId);
								  if(priceMap ==null || priceMap.isEmpty()){
									  continue;
								  }
								  Double profit = priceMap.get("profit");
								  profitOnProduct = profitOnProduct + profit;
							  }
							  profitOnProduct = profitOnProduct/childSKUS.size();
							  
							  DecimalFormat forma = new DecimalFormat(BBBCatalogConstants.DECIMAL_FORMAT);
							  String marginOnProduct = forma.format(profitOnProduct);
							  
								  //logDebug("final Margin******** ***************************"+marginOnProduct + "for site " +siteId);
							  
							
							  finalMargin = finalMargin + profitOnProduct;
						  }
						}
						  if(productChildProducts !=null && !productChildProducts.isEmpty()){
							  finalMargin = finalMargin/productChildProducts.size();
						  }
					  }
					  else {
						  List<RepositoryItem> childSKUS = null;
						  double profitOnProduct = 0;
						  childSKUS = (List<RepositoryItem>) pItem.getPropertyValue(BBBCatalogConstants.CHILD_SKUS);
						  for(RepositoryItem sku : childSKUS){
							  
								  //logDebug("SKu id ******** "+sku.getRepositoryId());
							  
							  Map<String,Double> priceMap = getMarginForSku(sku,siteId);
							  if(priceMap ==null || priceMap.isEmpty()){
								  continue;
							  }
							  Double profit = priceMap.get("profit");
							  profitOnProduct = profitOnProduct + profit;
						  }
						  if(childSKUS !=null && !childSKUS.isEmpty()){
							  profitOnProduct = profitOnProduct/childSKUS.size();
						  }
						  
							  //logDebug("Total profit  **********" + profitOnProduct+ "*** skus ===" +childSKUS.size() +"final profit ==="+profitOnProduct);
						  
						  finalMargin = profitOnProduct;
					  }

					  DecimalFormat format = new DecimalFormat("00.00");
					  if(finalMargin !=0)
						  margin = format.format(finalMargin);
					 
						  //logDebug("Margin on product till two decimal*******==== "+margin + "for site ==== " +siteId);
					   
				  }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return margin;
	}

	private Map<String, Double> getMarginForSku(RepositoryItem sku,
			String siteId) {
		  String siteSpecificCost = null;
		  String listPriceListId =null;
		  String SalePriceListId =null;
		  Map<String,Double> priceMap = new java.util.HashMap<String, Double>();
		  try{
		  RepositoryItem siteItem = getSiteRepository().getItem(siteId, BBBCatalogConstants.SITE_CONFIGURATION);
		  listPriceListId = ( (RepositoryItem)siteItem.getPropertyValue(BBBCatalogConstants.DEFAULT_PRICE_LIST)).getRepositoryId();
		  SalePriceListId = ( (RepositoryItem)siteItem.getPropertyValue(BBBCatalogConstants.DEFAULT_SALE_PRICE_LIST)).getRepositoryId();

		  if(siteId.equalsIgnoreCase("BedBathUS")){
			  siteSpecificCost = (String) sku.getPropertyValue(BBBCatalogConstants.COST_DEFAULT);
		  }
		  else{
			 @SuppressWarnings("unchecked")
			 Set<RepositoryItem> allTranslation =  (Set<RepositoryItem>) sku.getPropertyValue(BBBCatalogConstants.SKU_TRANSLATIONS);

			  for(RepositoryItem translation : allTranslation ){

				  RepositoryItem siteRepo = (RepositoryItem) translation.getPropertyValue(BBBCatalogConstants.SITE);
				  String attributeName = (String) translation.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_NAME);

				  String siteName = siteRepo.getRepositoryId();

				  if(siteId.equalsIgnoreCase(siteName) && attributeName.equalsIgnoreCase(BBBCatalogConstants.COST_DEFAULT)){
					  siteSpecificCost = (String) translation.getPropertyValue(BBBCatalogConstants.ATTRIBUTE_VALUE_STRING);
				  }
			  }
		  }
		  if(BBBUtility.isEmpty(siteSpecificCost)){
			  //logDebug("putting default cost  ");
			  if( siteId.equals("BuyBuyBaby")){
				  siteSpecificCost = (String) sku.getPropertyValue(BBBCatalogConstants.COST_DEFAULT);
			  }
			  if(BBBUtility.isEmpty(siteSpecificCost)){
				  
					  //logDebug("NO cost found for this sku in default property also ");
				  
				  return priceMap;
			  }
		  }
		  String priceList = null;
		  boolean onSale = (Boolean) sku.getPropertyValue(BBBCatalogConstants.ON_SALE);
		  if(onSale){
			  priceList = SalePriceListId;

		  }else{
			  priceList = listPriceListId;
		  }
		  RepositoryView view = getPriceListManager().getPriceListRepository().getView(BBBCatalogConstants.PRICE2);
		  RqlStatement statement = RqlStatement.parseRqlStatement("skuId = ?0 AND priceList =?1 ");
		  Object params[] = new Object[2];
		  params[0] = new String(sku.getRepositoryId());
		  params[1] = priceList;
		  RepositoryItem[] items = statement.executeQuery (view, params);
		 
			  //logDebug("items length **********" + items.length);
		 
		  double sellingPrice =  (Double) items[0].getPropertyValue(BBBCatalogConstants.LIST_PRICE); 

		  //logDebug("sellingPrice **********" + sellingPrice);
		  double costPrice = Double.parseDouble(siteSpecificCost);
		  //logDebug("costPrice **********" + costPrice);
		  double profit = sellingPrice - costPrice;

			  //logDebug("sellingPrice **********" + sellingPrice);
			  //logDebug("costPrice **********" + costPrice);
			  //logDebug("profit **********" + profit);
		  
		  if(sellingPrice!=0)
		  profit = (profit/sellingPrice)*100;

		  priceMap.put("sellingPrice", sellingPrice);
		  priceMap.put("costPrice", costPrice);
		  priceMap.put("profit", profit);
		  //return priceMap;
		  }catch (RepositoryException e1) {
	          logError("Catalog API Method Name [getProductDetails]: RepositoryException ");
	          e1.printStackTrace();
	          }
		return priceMap;
	  }
	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}
	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public PriceListManager getPriceListManager() {
		return priceListManager;
	}

	public void setPriceListManager(PriceListManager priceListManager) {
		this.priceListManager = priceListManager;
	}

	public Repository getSiteRepository() {
		return siteRepository;
	}

	public void setSiteRepository(Repository siteRepository) {
		this.siteRepository = siteRepository;
	}
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.catalogTools = pCatalogTools;
	}
}
