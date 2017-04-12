package com.bbb.commerce.giftregistry.bean;

import com.bbb.common.BBBGenericService;

import com.bbb.commerce.giftregistry.vo.RegistryVO;


// TODO: Auto-generated Javadoc
/**
 * The Class GiftRegSessionBean.
 */
public class GiftRegSessionBean  extends BBBGenericService {

    /** Refers to Object used to containg report in session at a time. */
    private Object responseHolder;
    
    /** The request vo. */
    private Object requestVO; 
    
    /** The registry operation. */
    private String registryOperation;
     
    /**
     * Instantiates a new gift reg session bean.
     */
    public GiftRegSessionBean(){
        super();
    }
    
    /**
     * Gets the response holder.
     *
     * @return the response holder
     */
    public Object getResponseHolder() {
		return responseHolder;
	}

	/**
	 * Sets the response holder.
	 *
	 * @param responseHolder the new response holder
	 */
	public void setResponseHolder(Object responseHolder) {
		this.responseHolder = responseHolder;
	}

	/**
	 * Gets CSPostingResponseBean wrapped inside responseHolder.
	 *
	 * @return the csPostingResponseBean
	 */
    public RegistryVO getRegistryVO() {

        //when request is made for RegistryVO       
        if(responseHolder == null
            || !(responseHolder instanceof RegistryVO) ){
            
            responseHolder = new RegistryVO();
        }
        
        return (RegistryVO)responseHolder;
    }

    /**
     * Clear responseHolder object
     * so that it can be used to store other kind of responses.
     */
    public void clear(){
        
    	RegistryVO registryVO = null;

        if(responseHolder != null 
        		&& responseHolder instanceof RegistryVO){
	            
        	registryVO = (RegistryVO)responseHolder;

        	registryVO.setCoRegistrant(null);
        	registryVO.setEvent(null);
        	registryVO.setPrimaryRegistrant(null);
        	registryVO.setShipping(null);
        	registryVO.setRegistryId(null);
        	registryVO.setRegistryIdWS(0);
        	registryVO.setRegistryType(null);
        	registryVO.setGiftPurchased(0);
        	registryVO.setGiftRegistered(0);
        	registryVO.setUserAddressList(null);
        	registryVO.setDisplayName(null);
        	registryVO.setSiteId(null);
        	registryVO.setRegistryToken(null);
        	registryVO.setNumRegAnnouncementCards(0);
        	registryVO.setServiceName(null);
        	registryVO.setUserAddressList(null);
        	registryOperation = null;
       }
    }
    
    /**
     * Clear responseHolder object
     * so that it can be used to store other kind of responses.
     */    
    public void clearRequest(){
    	this.setRequestVO(null);
    }
    
    /**
     * Sets the clear.
     *
     * @param clear the new clear
     */
    public void setClear(String clear){
    	clear();
    }

	/**
	 * Gets the registry operation.
	 *
	 * @return the registry operation
	 */
	public String getRegistryOperation() {
		return registryOperation;
	}

	/**
	 * Sets the registry operation.
	 *
	 * @param registryOperation the new registry operation
	 */
	public void setRegistryOperation(String registryOperation) {
		this.registryOperation = registryOperation;
	}

	/**
	 * Gets the request vo.
	 *
	 * @return the request vo
	 */
	public Object getRequestVO() {
		return requestVO;
	}

	/**
	 * Sets the request vo.
	 *
	 * @param requestVO the new request vo
	 */
	public void setRequestVO(Object requestVO) {
		this.requestVO = requestVO;
	}

}

