package com.bbb.simplifyRegistry.droplet;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.utils.StringUtils;

import atg.multisite.SiteContextManager;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.simplifyRegistry.RegistryInputVO;
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO;
import com.bbb.simplifyRegistry.manager.SimplifyRegistryManager;
import com.bbb.utils.BBBUtility;

/**
 *  This SimpleRegFieldsDroplet  would iterate over the list of fields obtained from SimplifyRegistry Repository and populate it in a map.
 *  Based on this map's key value pair the logic to display the fields on creation and mandatory checks  are made JSP.
 *  Example of expected return of map : {weddingDate={isDisplayonForm=true, isMandatoryOnCreate=true}, showFutureShippingAddr={isDisplayonForm=true, 
 *  isMandatoryOnCreate=false}, numberOfGuests={isDisplayonForm=true, isMandatoryOnCreate=true}}    
 */
public class SimpleRegFieldsDroplet extends BBBDynamoServlet {
	
	private SimplifyRegistryManager simplifyRegistryManager;
	public static final String IS_REQUIRED_ON_CREATE="isRequiredOnCreate";
	public static final String IS_DISPLAY_ON_FORM="isDisplayonForm";
	public static final String IS_MANDATORY_ON_CREATE="isMandatoryOnCreate";
	public static final String INPUT_LIST_MAP="inputListMap";
	public static final String REQUIRED_TO_MAKE_REG_PUBLIC = "requiredToMakeRegPublic";
	public static final String UNIVERSITY="University";
	public static final String COLLEGE_UNIVERSITY="College/University"; 
	
	HashMap h= new HashMap();

	public SimplifyRegistryManager getSimplifyRegistryManager() {
		return simplifyRegistryManager;
	}


	public void setSimplifyRegistryManager(
			SimplifyRegistryManager simplifyRegistryManager) {
		this.simplifyRegistryManager = simplifyRegistryManager;
	}


	/**
	 * to hold error variable
	 */
	public static final String OPARAM_ERROR = "error";
	

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
			throws javax.servlet.ServletException, java.io.IOException {
		String eventType = ((String) pRequest
				.getLocalParameter(BBBCoreConstants.EVENT_TYPE));
		String siteId = getSiteId();
		if((BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(siteId)||BBBCoreConstants.SITEBAB_CA_TBS.equalsIgnoreCase(siteId)) && UNIVERSITY.equalsIgnoreCase(eventType) ){
			eventType=COLLEGE_UNIVERSITY;
		}
		logDebug(new StringBuilder("Inside SimpleRegFieldsDroplet with eventtype : ").append(eventType).toString());
		RegistryInputsByTypeVO registryInputsByTypeVO = new RegistryInputsByTypeVO();
		List<RegistryInputVO> registryInputList = (List) new ArrayList<RegistryInputVO>();
		Map<String, Map<String, String>> inputListMap = new HashMap<String, Map<String, String>>();
		Boolean isDisplayOnForm =false;
		Boolean isMandatoryOnCreate= false;
		Boolean requiredToMakeRegPublic = false;
		String fieldname= null;
		try {
			registryInputsByTypeVO = getSimplifyRegistryManager().getRegInputsByRegType(eventType);
			if (registryInputsByTypeVO != null) {
				registryInputList = registryInputsByTypeVO.getRegistryInputList();
				if(!BBBUtility.isListEmpty(registryInputList)){
					for (RegistryInputVO registryInpVO : registryInputList) {
						Map<String, String> regInputVO = new HashMap<String, String>();
						fieldname = registryInpVO.getFieldName();
						if(!StringUtils.isEmpty(fieldname)){
						isDisplayOnForm = registryInpVO.isDisplayOnForm();				
						isMandatoryOnCreate = registryInpVO.isRequiredInputCreate();
						requiredToMakeRegPublic = registryInpVO.isRequiredToMakeRegPublic();
						regInputVO.put(IS_MANDATORY_ON_CREATE,Boolean.toString(isMandatoryOnCreate));
						regInputVO.put(IS_DISPLAY_ON_FORM, Boolean.toString(isDisplayOnForm));
						regInputVO.put(REQUIRED_TO_MAKE_REG_PUBLIC, Boolean.toString(requiredToMakeRegPublic));
						inputListMap.put(fieldname.trim(), regInputVO);
						}
					}
				}
				pRequest.setParameter(INPUT_LIST_MAP, inputListMap);
				pRequest.setParameter("registryInputsByTypeVO",registryInputsByTypeVO);
				pRequest.setParameter("registryInputList",registryInputList);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			}
		} catch (RepositoryException e) {
			logError("Repository Exception is thrown",e);

		}
	}


	protected String getSiteId() {
		return SiteContextManager.getCurrentSite().getId();
	}
}
