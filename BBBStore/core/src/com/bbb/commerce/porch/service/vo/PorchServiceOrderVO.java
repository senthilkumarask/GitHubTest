/**
 * 
 */
package com.bbb.commerce.porch.service.vo;

import java.util.Date;

import com.bbb.framework.integration.ServiceRequestBase;


/**
 * @author sm0191
 *
 */
public class PorchServiceOrderVO extends ServiceRequestBase{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8593672303010143116L;

	private PorchServiceHeader header;
	
	private Date creationDate;
	
	private String serviceName;
	
	private String   jsonData;

	/**
	 * @return the header
	 */
	public PorchServiceHeader getHeader() {
		return header;
	}

	/**
	 * @param header the header to set
	 */
	public void setHeader(PorchServiceHeader header) {
		this.header = header;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	 

	/**
	 * @return the jsonData
	 */
	public String getJsonData() {
		return jsonData;
	}

	/**
	 * @param jsonData the jsonData to set
	 */
	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

 
	
	

}
