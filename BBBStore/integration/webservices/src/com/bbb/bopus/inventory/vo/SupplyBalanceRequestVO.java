package com.bbb.bopus.inventory.vo;

public class SupplyBalanceRequestVO extends
		com.bbb.framework.integration.ServiceRequestBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3679154741522147011L;

	/**
	 * property: serviceName to supplyBalance operation for getting BOPUS ITEM
	 * inventory
	 */
	private String serviceName = "supplyBalance";

	/**
	 * property: aTPXml-It will contain the request string of supplyBalance operation
	 */
	private org.xmlsoap.schemas.soap.encoding.String aTPXml;

	/**
	 * @return aTPXml
	 */
	public org.xmlsoap.schemas.soap.encoding.String getATPXml() {
		return this.aTPXml;
	}

	@Override
	public String toString() {
		return "SupplyBalanceRequestVO [serviceName=" + this.serviceName
				+ ", aTPXml=" + this.aTPXml + "]";
	}

	/**
	 * @param aTPXml
	 */
	public void setATPXml(final org.xmlsoap.schemas.soap.encoding.String aTPXml) {
		this.aTPXml = aTPXml;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bbb.framework.integration.ServiceRequestBase#getServiceName()
	 */
	@Override
	public String getServiceName() {
		return this.serviceName;
	}

	/**
	 * @param serviceName
	 */
	public void setServiceName(final String serviceName) {
		this.serviceName = serviceName;
	}

}
