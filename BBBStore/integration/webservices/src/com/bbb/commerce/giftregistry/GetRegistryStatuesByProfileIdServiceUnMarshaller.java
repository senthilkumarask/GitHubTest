package com.bbb.commerce.giftregistry;

import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlObject;

import com.bbb.commerce.giftregistry.vo.RegStatusesResVO;
import com.bbb.commerce.giftregistry.vo.RegistryStatusVO;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.ResponseUnMarshaller;
import com.bedbathandbeyond.www.GetRegistryStatusesByProfileIdResponseDocument;
import com.bedbathandbeyond.www.GetRegistryStatusesByProfileIdResponseDocument.GetRegistryStatusesByProfileIdResponse;
import com.bedbathandbeyond.www.RegStatusInfo;


/**
 * This class contain methods used for unmarshalling the webservice response.
 * 
 * @author sk134
 * 
 */
public class GetRegistryStatuesByProfileIdServiceUnMarshaller extends ResponseUnMarshaller {
	
	/**
	 * default serial Id 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * This method is used to build a response.
	 */
	public ServiceResponseIF processResponse(final XmlObject responseDocument)
			throws BBBSystemException {
		
		logDebug("GetRegistryStatuesByProfileIdServiceUnMarshaller.processResponse() method start");
		final RegStatusesResVO regStatusesResVO = new RegStatusesResVO();
		BBBPerformanceMonitor
		.start("GetRegistryStatuesByProfileIdServiceUnMarshaller-processResponse");
		/*final String m_namespaceDeclaration = 
		        "declare namespace xq='http://www.bedbathandbeyond.com/';";
		BBBPerformanceMonitor
				.start("GetRegistryStatuesByProfileIdServiceUnMarshaller-processResponse");
		 String pathExpression = "$this/GetRegistryStatusesByProfileIdResult/xq:RegProfileInfos/xq:RegStatusInfo";
		
		 XmlObject[] returnEle =responseDocument.selectPath("declare namespace xq='http://www.bedbathandbeyond.com/';$this/xq:GetRegistryStatusesByProfileIdResponse/xq:GetRegistryStatusesByProfileIdResult");
		 XmlObject[] results = responseDocument.selectPath(m_namespaceDeclaration + pathExpression);*/
		
		
		try {
				final GetRegistryStatusesByProfileIdResponse  regStatusRes = ((GetRegistryStatusesByProfileIdResponseDocument) responseDocument).getGetRegistryStatusesByProfileIdResponse();
				
				if (regStatusRes != null 
						&& regStatusRes.getGetRegistryStatusesByProfileIdResult() !=null 
						&& regStatusRes.getGetRegistryStatusesByProfileIdResult().getRegProfileInfos()!=null) {
					
				if (!regStatusRes.getGetRegistryStatusesByProfileIdResult().getStatus().getErrorExists()) {
						regStatusesResVO .getServiceErrorVO().setErrorExists(false);
						final RegStatusInfo[] regEntries = regStatusRes.getGetRegistryStatusesByProfileIdResult().getRegProfileInfos().getRegStatusInfoArray();
						RegistryStatusVO regStatusVO = null;
						final List<RegistryStatusVO> listRegistryResVO = new ArrayList<RegistryStatusVO>();
						
						if(regEntries != null ){
							regStatusesResVO.setTotEntries(regStatusRes.getGetRegistryStatusesByProfileIdResult().getRegProfileInfos().getRegStatusInfoArray().length);
							for(int index=0; index<regEntries.length; index++){
								regStatusVO = new RegistryStatusVO();
								
								regStatusVO.setRegistryId(""+regEntries[index].getRegNum());
								regStatusVO.setStatusCode(regEntries[index].getStatusCode());
								regStatusVO.setStatusDesc(regEntries[index].getStatusDesc());
								listRegistryResVO.add(regStatusVO);
								
							}
							regStatusesResVO.setListRegistryStatusVO(listRegistryResVO);
						}
							
						/*DozerBeanMapper mapper = getDozerBean().getDozerMapper();
						try {
							mapper.map(getRegistryInfoByProfileIdResponse, regSearchResVO);

						} catch (MappingException me) {
							logError(me);
							throw new BBBSystemException(me.getMessage(), me);
						}*/
						
					} else {

						regStatusesResVO.getServiceErrorVO()
								.setErrorExists(true);
						regStatusesResVO.getServiceErrorVO().setErrorId(
								regStatusRes.getGetRegistryStatusesByProfileIdResult().getStatus().getID());
						regStatusesResVO.getServiceErrorVO()
								.setErrorMessage(regStatusRes.getGetRegistryStatusesByProfileIdResult().getStatus().getErrorMessage());
						regStatusesResVO.setWebServiceError(true);
					}

				}
			} catch (Exception e) {
				regStatusesResVO.getServiceErrorVO()
				.setErrorExists(false);
				BBBPerformanceMonitor
				.end("GetRegistryStatuesByProfileIdServiceUnMarshaller-processResponse");
				throw new BBBSystemException(e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor
				.end("GetRegistryStatuesByProfileIdServiceUnMarshaller-processResponse");
			}
		logDebug("GetRegistryStatuesByProfileIdServiceUnMarshaller.processResponse() method ends");
		return regStatusesResVO;
	}
}
