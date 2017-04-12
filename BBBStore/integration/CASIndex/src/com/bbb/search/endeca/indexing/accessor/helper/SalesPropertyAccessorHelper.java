package com.bbb.search.endeca.indexing.accessor.helper;

import atg.nucleus.GenericService;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class SalesPropertyAccessorHelper extends GenericService {

	BBBCatalogTools catalogTools;
	MutableRepository catalogRepository;
	private Repository orderRepository;

	public Object getOrderSalesProperty(final RepositoryItem pItem) {
		return getOrderSales(pItem);
	}
	public Object getUnitSalesProperty(RepositoryItem pItem) {
		return getUnitSales(pItem);
	}
	public Object getTotalSalesProperty(RepositoryItem pItem) {
		return getTotalSales(pItem);
	}


	private Object getUnitSales(RepositoryItem pItem) {		
		if (pItem != null){
			try {
				String productId = pItem.getRepositoryId();
				if (getCatalogTools().isProductActive(productId)) {
				final RqlStatement statementDate =  RqlStatement.parseRqlStatement("(productId!=?0 )");
		    final RepositoryView viewSales = this.getOrderRepository().getView("salesData");
		    final Object[] paramDate = new Object[1];
		    paramDate[0] = "0";
		    final RepositoryItem[] salesItems = statementDate.executeQuery(viewSales, paramDate);
		    if((salesItems != null) && (salesItems.length > 0)){
			for(final RepositoryItem sales :  salesItems){
			    if ((sales != null) && (sales.getRepositoryId() !=null)){
			    	String unitSale = (String) sales.getPropertyValue("unitSales");
			    	return unitSale;
			    }
			}
		    }
				}
		    }catch (RepositoryException | BBBSystemException | BBBBusinessException e1) {
				e1.printStackTrace();
		}
			}
		return null;
	}
	private Object getOrderSales(RepositoryItem pItem) {		
		if (pItem != null){
			try {
				String productId = pItem.getRepositoryId();
				if (getCatalogTools().isProductActive(productId)) {
				final RqlStatement statementDate =  RqlStatement.parseRqlStatement("(productId!=?0 )");
		    final RepositoryView viewSales = this.getOrderRepository().getView("salesData");
		    final Object[] paramDate = new Object[1];
		    paramDate[0] = "0";
		    final RepositoryItem[] salesItems = statementDate.executeQuery(viewSales, paramDate);
		    if((salesItems != null) && (salesItems.length > 0)){
			for(final RepositoryItem sales :  salesItems){
			    if ((sales != null) && (sales.getRepositoryId() !=null)){
			    	String orderSales = (String) sales.getPropertyValue("orderSales");
			    	return orderSales;
			    }
			}
		    }
				}
		    }catch (RepositoryException | BBBSystemException | BBBBusinessException e1) {
				e1.printStackTrace();
		}
			}
		return null;
	}
	
	public BBBCatalogTools getCatalogTools() {
		return this.catalogTools;
	}

	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.catalogTools = pCatalogTools;
	}

	public MutableRepository getCatalogRepository() {
		return catalogRepository;
	}

	public void setCatalogRepository(MutableRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}

	public Repository getOrderRepository() {
		return orderRepository;
	}

	public void setOrderRepository(Repository orderRepository) {
		this.orderRepository = orderRepository;
	}
	public Object getTotalSales(RepositoryItem pItem) {		
		if (pItem != null){
			try {
				String productId = pItem.getRepositoryId();
				if (getCatalogTools().isProductActive(productId)) {
				final RqlStatement statementDate =  RqlStatement.parseRqlStatement("(productId!=?0 )");
		    final RepositoryView viewSales = this.getOrderRepository().getView("salesData");
		    final Object[] paramDate = new Object[1];
		    paramDate[0] = "0";
		    final RepositoryItem[] salesItems = statementDate.executeQuery(viewSales, paramDate);
		    if((salesItems != null) && (salesItems.length > 0)){
			for(final RepositoryItem sales :  salesItems){
			    if ((sales != null) && (sales.getRepositoryId() !=null)){
			    	String orderSales = (String) sales.getPropertyValue("totalSales");
			    	return orderSales;
			    }
			}
		    }
				}
		    }catch (RepositoryException | BBBSystemException | BBBBusinessException e1) {
				e1.printStackTrace();
		}
			}
		return null;
	}

}
