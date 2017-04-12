/**
 * 
 */
package com.bbb.commerce.porch.service.vo;

/**
 * @author sm0191
 *
 */
public class PorchValidateZipCodeResponseVO {

 
	
	private String serviceCode;
	
	private String name;
	
	private String question;
	
	private String description;
	
	private PorchServicesVO[] services;

	/**
	 * @return the serviceCode
	 */
	public String getServiceCode() {
		return serviceCode;
	}

	/**
	 * @param serviceCode the serviceCode to set
	 */
	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the question
	 */
	public String getQuestion() {
		return question;
	}

	/**
	 * @param question the question to set
	 */
	public void setQuestion(String question) {
		this.question = question;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the services
	 */
	public PorchServicesVO[] getServices() {
		return services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(PorchServicesVO[] services) {
		this.services = services;
	}
	
	
	
	
}
