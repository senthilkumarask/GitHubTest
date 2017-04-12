/*
 *
 * File  : ResponseUnMarshaller.java
 * Project:     BBB
 * 
 * 
 * HISTORY: 
 * Initial Version: 12/01/2011
 * Modified:	Lokesh Duseja:	12/06/2011:		Created default constructor and injected instance of global component BBBDozerBeanProvider
 */
package com.bbb.framework.webservices;

import org.apache.xmlbeans.XmlObject;

import com.bbb.common.BBBGenericService;
import atg.nucleus.Nucleus;

import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ResponseUnMarshallerIF;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.integration.vo.ResponseErrorVO;
import com.bbb.framework.performance.BBBPerformanceMonitor;

/**
 * The Class ResponseUnMarshaller.
 */
public class ResponseUnMarshaller extends BBBGenericService implements ResponseUnMarshallerIF {

	
	// TODO Manohar
	// TODO: ResponseUnMarshaller - general tasks while processing response
	// would go into this class and processing error would also go into this.

	private BBBDozerBeanProvider mDozerBean;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Parses the error.
	 * 
	 * @param faultDoc the fault doc
	 * 
	 * @return the response error vo
	 */
	public ResponseErrorVO parseError(XmlObject faultDoc) {

		BBBPerformanceMonitor
		.start("common webservice-parseError");
		
		ResponseErrorVO error = new ResponseErrorVO();
		
		// TODO Manohar
		// TODO code me to process error xml once the error XSD is frozen and if
		// the error XSD is common for all web services. As of now I am just a
		// skeleton. If the error XSD is different for different web services
		// then respective unmarshallers should override me.
		
		// Remember to type cast faultDoc to respective error doc element
		
		BBBPerformanceMonitor
		.end("common webservice-parseError");
		
		return error;
	}

	/**
	 * Generate error fields from error object.
	 * 
	 * @param faultDoc the fault doc
	 * 
	 * @return the string[]
	 */
/*	private String[] generateErrorFields(XmlObject faultDoc) {

		BBBPerformanceMonitor
		.start("common web service-generateErrorFields");
		
		// TODO Manohar
		// TODO code me to process error xml once the error XSD is frozen and if
		// the error XSD is common for all web services. As of now I am just a
		// skeleton. If the error XSD is different for different web services
		// then respective unmarshallers should override me.
	
		List<String> errorFields = new ArrayList<String>();
		
		// Remember to type cast faultDoc to respective error doc element
		
		for (ProviderErrorType errInstance : faultDoc.getErrorDetailItem().getProviderErrorArray()) {
			errorFields.add(errInstance.getFieldInError());
		}

		BBBPerformanceMonitor
		.end("common web service-generateErrorFields");
	
		return errorFields.toArray(new String[errorFields.size()]);
	}*/

	/**
	 * Translate error code.
	 * 
	 * @param providerErrorCode the provider error code
	 * 
	 * @return the service response error
	 */
/*	private ServiceResponseError translateErrorCode(final String providerErrorCode) {
		return ServiceResponseError.getErrorFromBackendCode(providerErrorCode);
	}
*/
	/* (non-Javadoc)
	 * @see com.bbb.integration.api.unmarshaller.ResponseUnMarshallerIF#processResponse(org.apache.xmlbeans.XmlObject)
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException, BBBBusinessException {
		return null;
	}
	
/*	private ServiceResponseError translateErrorCodeWithExactMatch(final String providerErrorCode) {
		return ServiceResponseError.getExactErrorFromBackendCode(providerErrorCode);
	}
*/	/**
	 * @return the mDozerBean
	 */
	public BBBDozerBeanProvider getDozerBean() {
		return mDozerBean;
	}

	public ResponseUnMarshaller() {
		super();
		mDozerBean=
                (BBBDozerBeanProvider)Nucleus.getGlobalNucleus().resolveName("/com/bbb/framework/webservices/BBBDozerBeanProvider");
	}

	/**
	 * @param pDozerBean the mDozerBean to set
	 */
	public void setDozerBean(BBBDozerBeanProvider pDozerBean) {
		this.mDozerBean = pDozerBean;
	}
	
}
