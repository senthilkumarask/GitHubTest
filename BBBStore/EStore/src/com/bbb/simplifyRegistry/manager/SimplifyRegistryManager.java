package com.bbb.simplifyRegistry.manager;
/**
 * This is a manager for kickStarters, which contains all the methods
 * to retrieve the data from kickStarter repository, adds the business logic to it,
 * and sends filtered data to the droplets.
 *
 * @author dwaghmare
 *
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.targeting.DynamicContentTargeter;
import atg.targeting.TargetingException;
import atg.userprofiling.Profile;

import com.bbb.cms.PromoBoxVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.BazaarVoiceProductVO;
import com.bbb.commerce.catalog.vo.ImageVO;
import com.bbb.commerce.catalog.vo.ProductVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.manager.GiftRegistryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.search.endeca.EndecaSearch;
import com.bbb.simplifyRegistry.RegistryInputVO;
import com.bbb.simplifyRegistry.RegistryInputsByTypeVO;
import com.bbb.simplifyRegistry.tools.SimplifyRegistryTools;


public class SimplifyRegistryManager extends BBBGenericService {
	private SimplifyRegistryTools simplifyRegistryTools;


	public SimplifyRegistryTools getSimplifyRegistryTools() {
		return simplifyRegistryTools;
	}

	public void setSimplifyRegistryTools(SimplifyRegistryTools simplifyRegistryTools) {
		this.simplifyRegistryTools = simplifyRegistryTools;
	}



	/**
	 * getKickStarters method calls KickStarterTools and gets the repositoryData
	 *
	 */

	  @SuppressWarnings("unchecked")
	  public RegistryInputsByTypeVO getRegInputsByRegType(String registryType) throws RepositoryException{

		  BBBPerformanceMonitor.start( SimplifyRegistryManager.class.getName() + " : " + "getRegInputsByRegType(registryType)");


		  logDebug("SimplifyRegistryManager:getRestKickStrDataLoggedIn: START");

		  RegistryInputsByTypeVO registryInputsByTypeVO = new RegistryInputsByTypeVO();
		  DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		  RepositoryItem registryInputsByType =null;

		  registryInputsByType = this.simplifyRegistryTools.getRegInputsByType(registryType);


		  registryInputsByTypeVO= getPopulatedVO(registryInputsByType);


		  logDebug("SimplifyRegistryManager:getRegInputsByRegType: EXIT");


		  BBBPerformanceMonitor.end( SimplifyRegistryManager.class.getName() + " : " + "getRegInputsByRegType(registryType)");

		  return registryInputsByTypeVO;
	  }
	  
	  @SuppressWarnings("unchecked")
	public RegistryInputsByTypeVO getPopulatedVO(RepositoryItem registryInputsByType){

			 BBBPerformanceMonitor.start( SimplifyRegistryManager.class.getName() + " : " + "getPopulatedVO(registryInputsByType)");

		     logDebug("SimplifyRegistryManager:getPopulatedVO: Start");


			  List<RegistryInputVO> registryInputList =(List) new ArrayList<RegistryInputVO>();
			  RegistryInputsByTypeVO registryInputsByTypeVO = new RegistryInputsByTypeVO();
			  Set<RepositoryItem> regInputsSet= new HashSet<RepositoryItem>();
			  
			  if(registryInputsByType!=null){
				  if(registryInputsByType.getPropertyValue("id")!=null){
					  registryInputsByTypeVO.setId((String)registryInputsByType.getPropertyValue("id"));
				  }
				  if(registryInputsByType.getPropertyValue("eventType")!=null){
					  registryInputsByTypeVO.setEventType((String)registryInputsByType.getPropertyValue("eventType"));
				  }
				  if(registryInputsByType.getPropertyValue("isPublic")!=null){
					  registryInputsByTypeVO.setPublic((Boolean)registryInputsByType.getPropertyValue("isPublic"));
				  }
				  if(registryInputsByType.getPropertyValue("regInputList")!=null){
					  regInputsSet =   (Set<RepositoryItem>)registryInputsByType.getPropertyValue("regInputList");
					  for(RepositoryItem regInput:regInputsSet){
					  RegistryInputVO registryInputVO=new RegistryInputVO();
					  	if(regInput.getPropertyValue("id") !=null){
					  		registryInputVO.setId((String) regInput.getPropertyValue("id"));
					  	}
					  	if(regInput.getPropertyValue("fieldName") !=null){
					  		registryInputVO.setFieldName((String) regInput.getPropertyValue("fieldName"));
					  	}
					  	if(regInput.getPropertyValue("displayOnForm") !=null){
					  		registryInputVO.setDisplayOnForm(((Boolean) regInput.getPropertyValue("displayOnForm")).booleanValue());
					  	}
					  	if(regInput.getPropertyValue("requiredInputCreate") !=null){
					  		registryInputVO.setRequiredInputCreate(((Boolean) regInput.getPropertyValue("requiredInputCreate")).booleanValue());
					  	}
					  	if(regInput.getPropertyValue("requiredInputUpdate") !=null){
					  		registryInputVO.setRequiredInputUpdate(((Boolean) regInput.getPropertyValue("requiredInputUpdate")).booleanValue());
					  	}
					  	if(regInput.getPropertyValue("requiredToMakeRegPublic") !=null){
					  		registryInputVO.setRequiredToMakeRegPublic(((Boolean) regInput.getPropertyValue("requiredToMakeRegPublic")).booleanValue());
					  	}
					  	registryInputList.add(registryInputVO);
					  	registryInputVO=null;
					  }
					  registryInputsByTypeVO.setRegistryInputList(registryInputList);
				  }
			  
		  }
			  return registryInputsByTypeVO;
	  }
}