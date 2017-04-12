package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBUtility;

/**
 * This droplet is used for returning productId as string.
 */
public class ExitemIdDroplet extends BBBDynamoServlet {


	private static final String OUTPUT = "output";
	private static final String PRODUCT_LIST = "productList";
	private static final String LASTVIEWED_PRODUCTS_LIST = "lastviewedProductsList";
	private static final String CERTONA_EX_ITEMS = "certonaExcludedItems";

	@SuppressWarnings("unchecked")
	/**
	 * It will take product Id as input and create string of products. 
	 */
	public void service(final DynamoHttpServletRequest request,
			final DynamoHttpServletResponse response)
					throws ServletException, IOException{
		final String methodName="ExitemIdDroplet:service";
		BBBPerformanceMonitor.start(methodName);
		 
		StringBuilder productTempList=new StringBuilder("");
		
		String productList = "";
		List<ProductVO> lastviewedProductsList;

		
		logDebug("Method entry ["+methodName+"]");


		lastviewedProductsList= (List<ProductVO>)request.getLocalParameter(LASTVIEWED_PRODUCTS_LIST);
		
		
		String certonaItems = (String) request.getLocalParameter(CERTONA_EX_ITEMS);
		if(!BBBUtility.isEmpty(certonaItems)){
			for(String certonaString : certonaItems.split(";")){
				if(productTempList.indexOf(certonaString) == -1)
					productTempList.append(certonaString+';');
			}			
		}
		
		
		/*//PS-16306 fix start. Excluding accessories and collection child products from certona recommendation.
		final List<ProductVO> certonaExItems = (List<ProductVO>)request.getLocalParameter(CERTONA_EX_ITEMS);
		if(null != certonaExItems && !certonaExItems.isEmpty()){
			for(ProductVO productVO : certonaExItems) {
				productTempList=productTempList.append(productVO.getProductId()+';');
			}
		}
		//PS-16306 ends.
		*/	
		if (!BBBUtility.isListEmpty(lastviewedProductsList)){
        final Iterator<ProductVO> it =lastviewedProductsList.iterator();
	 
			while(it.hasNext()){
				final ProductVO productVO=it.next();
				if(productTempList.indexOf(productVO.getProductId()) == -1)
					productTempList=productTempList.append(productVO.getProductId()+';');
			}
		}
		
		if(productTempList.length()>0){
			productList=productTempList.substring(0, productTempList.length()-1);
		}
			
		request.setParameter(PRODUCT_LIST, productList);
		request.serviceParameter(OUTPUT, request, response);

	
		logDebug("Method exit ["+methodName+"]");
	
		BBBPerformanceMonitor.end(methodName);
	}
}
