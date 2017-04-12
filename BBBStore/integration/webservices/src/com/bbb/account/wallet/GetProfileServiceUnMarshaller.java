package com.bbb.account.wallet;

import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.framework.webservices.vo.ValidationError;
import com.bedbathandbeyond.www.GetProfileResponseDocument;
import com.bedbathandbeyond.www.GetProfileResponseDocument.GetProfileResponse;
import com.bedbathandbeyond.www.GetProfileReturn;
import com.bedbathandbeyond.www.Status;
/**
 * Unmarshaller for Get Profile Service
 * 
 * @author jvishn
 *
 */
public class GetProfileServiceUnMarshaller extends ResponseUnMarshaller {

	private static final long serialVersionUID = 1L;
	
	public ServiceResponseIF processResponse(final XmlObject pResponseDocument)
			throws BBBSystemException {

		BBBPerformanceMonitor.start("GetProfileServiceUnMarshaller-processResponse");
		logDebug("GetProfileServiceUnMarshaller ServiceResponseIF processResponse starts");

		BBBGetProfileResponse getProfileRes = null;
		GetProfileResponse getProfileResponse = null;
		
		logDebug("Inside GetProfileServiceUnMarshaller:processResponse. Response Document is "+ pResponseDocument.toString());
		
		try{
			getProfileResponse = ((GetProfileResponseDocument) pResponseDocument).getGetProfileResponse();
			if (getProfileResponse != null) {
				GetProfileReturn getProfileReturn = getProfileResponse.getGetProfileResult();
				if (!getProfileReturn.getStatus().getErrorExists()) {
					getProfileRes = (BBBGetProfileResponse) getResponse(getProfileReturn);
				} else {
					getProfileRes = new BBBGetProfileResponse();
					ErrorStatus errorStatus = getErrorStatus(getProfileReturn.getStatus());
					getProfileRes.setErrorStatus(errorStatus);
					getProfileRes.setWebServiceError(true);
				}
			}
		} finally{
			BBBPerformanceMonitor
				.end("GetProfileServiceUnMarshaller-processResponse");
		}
		
		return getProfileRes;

	}
	
	/**
	 * 
	 * @param status
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ErrorStatus getErrorStatus(final Status status) {

		final ErrorStatus errorStatus= new ErrorStatus();
		errorStatus.setDisplayMessage(status.getDisplayMessage());
		errorStatus.setErrorExists(status.getErrorExists());
		errorStatus.setErrorId(status.getID());
		errorStatus.setErrorMessage(status.getErrorMessage());
		errorStatus.setValidationErrors((List<ValidationError>) status.getValidationErrors());
		
		return errorStatus;
	}

	/*
	 * 
	 */
	private ServiceResponseIF getResponse(final GetProfileReturn getProfileReturn ) {
		
		BBBGetProfileResponse getProfileRes = new BBBGetProfileResponse();
		
		if(getProfileReturn.getSubscriber() != null){
			getProfileRes.setEmail(getProfileReturn.getSubscriber().getEmail());
			getProfileRes.setFirstname(getProfileReturn.getSubscriber().getFirstname());
			getProfileRes.setLastname(getProfileReturn.getSubscriber().getLastname());
			getProfileRes.setZip(getProfileReturn.getSubscriber().getZip());
		}
		
		final Status status = getProfileReturn.getStatus();
		final ErrorStatus errorStatus = getErrorStatus(status);
		
		getProfileRes.setErrorStatus(errorStatus);
		getProfileRes.setWebServiceError(false);
		
		return getProfileRes;
	}
}
