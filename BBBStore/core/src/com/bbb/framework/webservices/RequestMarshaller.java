/*
 * File  : RequestMarshaller.java
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
import com.bbb.framework.integration.RequestMarshallerIF;
import com.bbb.framework.integration.ServiceRequestIF;

/**
 * The Class RequestMarshaller.
 * 
 * 
 * @version 1.0
 */

public class RequestMarshaller extends BBBGenericService implements RequestMarshallerIF  {
	
	// TODO: RequestMarshaller - Things that are required to go in web service
	// message header should be provided by the static methods in this class.
	// Need to code this class, once the structure of request XML is forzen.
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2352350350618328294L;

	public RequestMarshaller() {
		super();
		mDozerBean=
                (BBBDozerBeanProvider)Nucleus.getGlobalNucleus().resolveName("/com/bbb/framework/webservices/BBBDozerBeanProvider");
	}

	private BBBDozerBeanProvider mDozerBean;

	@Override
	public XmlObject buildRequest(ServiceRequestIF reqVO)
			throws BBBSystemException, BBBBusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public XmlObject buildHeader()
			throws BBBSystemException, BBBBusinessException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the mDozerBean
	 */
	public BBBDozerBeanProvider getDozerBean() {
		return mDozerBean;
	}

	/**
	 * @param pDozerBean the mDozerBean to set
	 */
	public void setDozerBean(BBBDozerBeanProvider pDozerBean) {
		this.mDozerBean = pDozerBean;
	}
}
