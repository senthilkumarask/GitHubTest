package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.AddItemsToRegistry2ResponseDocument;
import com.bedbathandbeyond.www.AddItemsToRegistry2ResponseDocument.AddItemsToRegistry2Response;

/**
 * 
 * @author asi162 Amandeep Singh Dhammu
 *
 */

public class AddItemsToRegistry2ServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	public ServiceResponseIF processResponse(XmlObject responseDocument)
			throws BBBSystemException, BBBBusinessException {
		logDebug("AddItemsToRegistry2ServiceUnMarshaller.processResponse method begins");
		
		ValidateAddItemsResVO addItemsResVO = new ValidateAddItemsResVO();
		if(responseDocument!=null){
			
			AddItemsToRegistry2Response addItemsToRegistry2Response = ((AddItemsToRegistry2ResponseDocument)responseDocument).getAddItemsToRegistry2Response();
			
			if(addItemsToRegistry2Response !=null){
				if(!addItemsToRegistry2Response.getAddItemsToRegistry2Result().getStatus().getErrorExists()){
					addItemsResVO.getServiceErrorVO().setErrorExists(false);
				}else{
					
					addItemsResVO.getServiceErrorVO().setErrorExists(true);
					addItemsResVO.getServiceErrorVO().setErrorId(addItemsToRegistry2Response.getAddItemsToRegistry2Result().getStatus().getID());
					addItemsResVO.getServiceErrorVO().setErrorMessage(addItemsToRegistry2Response.getAddItemsToRegistry2Result().getStatus().getErrorMessage());
					addItemsResVO.setWebServiceError(true);
				}				
			}			
		}		
		return addItemsResVO;
		
	}
	
	
	
	
}
