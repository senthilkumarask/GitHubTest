package com.bbb.commerce.giftregistry;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bbb.utils.BBBUtility;
import com.bedbathandbeyond.www.GetRegistryInfoByProfileIdResponseDocument;
import com.bedbathandbeyond.www.GetRegistryInfoByProfileIdResponseDocument.GetRegistryInfoByProfileIdResponse;
import com.bedbathandbeyond.www.RegProfileInfo;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author sk134
 * 
 */
public class GetRegistryByProfileIdServiceUnMarshaller extends ResponseUnMarshaller {

	/**
	 * default serial Id 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		logDebug("GetRegistryByProfileIdServiceUnMarshaller.processResponse() method start");

		BBBPerformanceMonitor
				.start("GetRegistryByProfileIdServiceUnMarshaller-processResponse");

			final RegSearchResVO regSearchResVO = new RegSearchResVO();
		if (responseDocument != null) {
			try {

				final GetRegistryInfoByProfileIdResponse  regInfoRes = ((GetRegistryInfoByProfileIdResponseDocument) responseDocument).getGetRegistryInfoByProfileIdResponse();
					

				if (regInfoRes != null && regInfoRes.getGetRegistryInfoByProfileIdResult() !=null && regInfoRes.getGetRegistryInfoByProfileIdResult().getRegProfileInfos()!=null) {
					if (!regInfoRes.getGetRegistryInfoByProfileIdResult().getStatus().getErrorExists()) {
						regSearchResVO.getServiceErrorVO()
						.setErrorExists(false);
						final RegProfileInfo[] regEntries = regInfoRes.getGetRegistryInfoByProfileIdResult().getRegProfileInfos().getRegProfileInfoArray();
						RegistrySummaryVO registrySummaryVO = null;
						final List<RegistrySummaryVO> listRegVO = new ArrayList<RegistrySummaryVO>();
						
						if(regEntries != null ){
							
							regSearchResVO.setTotEntries(regInfoRes.getGetRegistryInfoByProfileIdResult().getRegProfileInfos().sizeOfRegProfileInfoArray());
							
							for(int index=0; index<regEntries.length; index++){
								registrySummaryVO = new RegistrySummaryVO();
								
								registrySummaryVO.setTotEntries(regInfoRes.getGetRegistryInfoByProfileIdResult().getRegProfileInfos().sizeOfRegProfileInfoArray());
								
								registrySummaryVO.setRegistryId(""+regEntries[index].getRegNum());
								registrySummaryVO.setGiftPurchased(regEntries[index].getGiftsPurchased());
								registrySummaryVO.setGiftRegistered(regEntries[index].getGiftsRegistered());
								String date="";
								if (null!=regEntries[index].getEventDate()&&regEntries[index].getEventDate().length()>7)
								{
									date=BBBUtility.convertDateWSToAppFormat(regEntries[index].getEventDate());
								}
									
								registrySummaryVO.setEventDate(date);
								registrySummaryVO.setCoRegistrantFullName(regEntries[index].getCoFullName());
								registrySummaryVO.setPrimaryRegistrantFullName(regEntries[index].getReFullName());
								registrySummaryVO.setEventType(regEntries[index].getEventType());
								registrySummaryVO.setAddrSubType(regEntries[index].getAddrSubType());
								listRegVO.add(registrySummaryVO);
								
							}
							regSearchResVO.setListRegistrySummaryVO(listRegVO);
						}
						/*	
						DozerBeanMapper mapper = getDozerBean().getDozerMapper();
						try {
							mapper.map(getRegistryInfoByProfileIdResponse, regSearchResVO);

						} catch (MappingException me) {
							logError(me);
							throw new BBBSystemException(me.getMessage(), me);
						}*/
						
					} else {

						regSearchResVO.getServiceErrorVO()
								.setErrorExists(true);
						regSearchResVO.getServiceErrorVO().setErrorId(
								regInfoRes.getGetRegistryInfoByProfileIdResult().getStatus().getID());
						regSearchResVO.getServiceErrorVO()
								.setErrorMessage(regInfoRes.getGetRegistryInfoByProfileIdResult().getStatus().getErrorMessage());
						regSearchResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				regSearchResVO.getServiceErrorVO()
				.setErrorExists(false);
				BBBPerformanceMonitor
				.end("GetRegistryByProfileIdServiceUnMarshaller-processResponse");
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1370,e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("GetRegistryByProfileIdServiceUnMarshaller-processResponse");
			}
		}

		logDebug("GetRegistryByProfileIdServiceUnMarshaller.processResponse() method ends");
		return regSearchResVO;
	}
}
