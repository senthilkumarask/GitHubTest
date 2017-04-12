package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;
import org.dozer.DozerBeanMapper;
import org.dozer.MappingException;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.AddItemsToRegistry2Document;
import com.bedbathandbeyond.www.AddItemsToRegistry2Document.AddItemsToRegistry2;

/**
 * 
 * @author asi162 Amandeep Singh Dhammu
 *
 */

public class AddItemsToRegistry2ServiceMarshaller extends RequestMarshaller{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
		
	public XmlObject buildRequest(ServiceRequestIF reqVO)
			throws BBBSystemException, BBBBusinessException {
		
		logDebug("AddItemsToRegistry2ServiceMarshaller.buildRequest method starts");
		
		AddItemsToRegistry2Document addItemsToRegistry2Document = null; 
		addItemsToRegistry2Document = AddItemsToRegistry2Document.Factory.newInstance();
		
		addItemsToRegistry2Document.setAddItemsToRegistry2(validateAddItemToRegistry2(reqVO));		
		
		return addItemsToRegistry2Document;
		
	}
	
	
	
	
	private AddItemsToRegistry2 validateAddItemToRegistry2(ServiceRequestIF pReqVO)throws BBBSystemException {

		logDebug("AddItemsToRegistry2ServiceMarshaller.validateAddItemToRegistry() method start");


		AddItemsToRegistry2 addItemsToRegistry2 = AddItemsToRegistry2.Factory.newInstance();
		
		DozerBeanMapper mapper = getDozerBean().getDozerMapper();
		try{
			mapper.map(pReqVO, addItemsToRegistry2);
		}
		catch(MappingException me){

			logError("MappingException from validateAddItemToRegistry2 from AddItemsToRegistry2ServiceMarshaller",me);
			
		}
	
		logDebug("AddItemsToRegistry2ServiceMarshaller.validateAddItemToRegistry2() method ends");

		return addItemsToRegistry2;
	}
	
	
	
	
}
