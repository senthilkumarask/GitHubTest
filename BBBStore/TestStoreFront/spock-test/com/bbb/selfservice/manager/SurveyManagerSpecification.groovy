package com.bbb.selfservice.manager

import java.util.List;

import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.StateVO
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.tibco.vo.SurveyVO;

import spock.lang.specification.BBBExtendedSpec

class SurveyManagerSpecification extends BBBExtendedSpec {

	SurveyManager mngr
	BBBCatalogTools catalogTools = Mock()
	
	def setup(){
		mngr = Spy()
		mngr.setCatalogTools(catalogTools)
	}
	
	def "getUserLocation method"(){
		
		given:
			catalogTools.getStates("pSiteId",true,"noShowPage") >> []
		when:
			List<StateVO> list = mngr.getUserLocation("pSiteId",true,"noShowPage")
		
		then:
			1*mngr.logDebug("SurveyManager.getUserLocation() method started")
			list.isEmpty() == true
			list.size() == 0
	}
	def "requestInfoTIBCO method with send method is null"(){
		
		given:
			SurveyVO pSurveyVO = new SurveyVO()
			mngr.send(pSurveyVO ) >> null
		expect:
			mngr.requestInfoTIBCO(pSurveyVO)
		
			
	}
	
	def "requestInfoTIBCO method with send method thrown exception"(){
		
		given:
			SurveyVO pSurveyVO = new SurveyVO()
			mngr.send(pSurveyVO ) >> {throw new BBBSystemException("err_survey_bsys_exception:BBBSystemException is thrown")}
		when:
			mngr.requestInfoTIBCO(pSurveyVO)
		
		then:
			1*mngr.logError(LogMessageFormatter.formatMessage(null, "BBBSystemException in SurveyManager while requestInfoTIBCO", BBBCoreErrorConstants.ACCOUNT_ERROR_1230 ), _)
			BBBBusinessException excep = thrown()
			excep.getMessage().equals("err_survey_bsys_exception:err_survey_bsys_exception:BBBSystemException is thrown")
	}
	
}
