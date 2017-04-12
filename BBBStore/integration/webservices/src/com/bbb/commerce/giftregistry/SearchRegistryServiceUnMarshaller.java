/*
 *
 * File  : CreateRegistryServiceUnMarshaller.java
 * Project:     BBB
 * 
 */
package com.bbb.commerce.giftregistry;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.utils.BBBUtility;
import com.bedbathandbeyond.www.RegEntry;
import com.bedbathandbeyond.www.RegSearchWithFilterResponseDocument;
import com.bedbathandbeyond.www.RegSearchWithFilterResponseDocument.RegSearchWithFilterResponse;

/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author ssha53
 * 
 */
public class SearchRegistryServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {

		BBBPerformanceMonitor
				.start("SearchRegistryServiceUnMarshaller-processResponse");

		final RegSearchResVO regSearchResVO = new RegSearchResVO();
		if (responseDocument != null) {

			final RegSearchWithFilterResponse regSearchResponse = ((RegSearchWithFilterResponseDocument) responseDocument)
					.getRegSearchWithFilterResponse();
					
			
			if (regSearchResponse != null && regSearchResponse.getRegSearchWithFilterResult() !=null) {
				if (!regSearchResponse.getRegSearchWithFilterResult().getStatus().getErrorExists()) {
					
					final RegEntry[] regEntries = regSearchResponse.getRegSearchWithFilterResult().getRegEntries().getRegEntryArray();
				
					RegistrySummaryVO registrySummaryVO = null;
					final List<RegistrySummaryVO> listRegVO = new ArrayList<RegistrySummaryVO>();
					
					if(regEntries != null ){
						
						regSearchResVO.setTotEntries(regSearchResponse.getRegSearchWithFilterResult().getTotEntries());
						
						for(int index=0; index<regEntries.length; index++){
							registrySummaryVO = new RegistrySummaryVO();
							
							registrySummaryVO.setTotEntries(regSearchResponse.getRegSearchWithFilterResult().getTotEntries());
							
							registrySummaryVO.setRegistryId(regEntries[index].getRegNum());
							registrySummaryVO.setState(regEntries[index].getState());
							registrySummaryVO.setEventType(regEntries[index].getEventType());
							registrySummaryVO.setPrimaryRegistrantFirstName(regEntries[index].getRegName());
							registrySummaryVO.setCoRegistrantFirstName(regEntries[index].getCoRegName());
							registrySummaryVO.setCoRegistrantEmail(regEntries[index].getCoRegEmail());
							registrySummaryVO.setRegistrantEmail(regEntries[index].getRegEmail());
							registrySummaryVO.setEventDate(regEntries[index].getEventDate());
							registrySummaryVO.setPwsurl(regEntries[index].getPwsUrl());
							registrySummaryVO.setSubType(regEntries[index].getSubType());
							registrySummaryVO.setPrimaryRegistrantMaidenName(regEntries[index].getMaidenName());
							registrySummaryVO.setPersonalWebsiteToken(regEntries[index].getPersonalWebsiteToken());
							registrySummaryVO.setEventDateCanada(
									BBBUtility.convertUSDateIntoWSFormatCanada(regEntries[index].getEventDate()));
							//regEntries[index].getEventTypeDesc();
							//regEntries[index].getRegToken();								
							//regEntries[index].getDisplayName()
							//regEntries[index].getSitePublishedCD()
							listRegVO.add(registrySummaryVO);
						}
					}

					regSearchResVO.setListRegistrySummaryVO(listRegVO);
					
				} else {

					regSearchResVO.getServiceErrorVO()
							.setErrorExists(true);
					regSearchResVO.getServiceErrorVO().setErrorId(
							regSearchResponse.getRegSearchWithFilterResult().getStatus().getID());
					regSearchResVO.getServiceErrorVO()
							.setErrorMessage(regSearchResponse
											.getRegSearchWithFilterResult().getStatus().getErrorMessage());
					regSearchResVO.setWebServiceError(true);
				}

			}

		}

		BBBPerformanceMonitor
				.end("SearchRegistryServiceUnMarshaller-processResponse");
		return regSearchResVO;
	}
}
