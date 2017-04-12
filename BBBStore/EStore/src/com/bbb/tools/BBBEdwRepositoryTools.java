package com.bbb.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;
import atg.adapter.gsa.GSARepository;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import com.bbb.account.profile.vo.ProfileEDWInfoVO;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBEximConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.edwData.EDWProfileDataVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.repositorywrapper.IRepositoryWrapper;
import com.bbb.repositorywrapper.RepositoryWrapperImpl;
import com.bbb.utils.BBBUtility;
/**
 * 
 * @author Velmurugan Moorthy
 * 
 * This class handles the repository operations of the EDW related repositories.
 * The repositories handled in this class are EDW-SiteSpectDataRepository and EDW-ProfileDataRepository
 *
 */
public class BBBEdwRepositoryTools extends BBBConfigToolsImpl {

	/** The edw site spect data repository. */
	private GSARepository edwSiteSpectDataRepository;
	
	/** The profile adapter repository. */
	private GSARepository edwProfileDataRepository;
	
	/** The sites. */
	private List<String> sites;
	
	/** The m user profile repository. */
	private MutableRepository mUserProfileRepository;

	/** The Constant EDW_DATA_RQL. */
	private static final String EDW_DATA_RQL = "profileId = ?0";
	
	
	/** The edw ttl. */
	private String edwTTL;
	
	/** The data center map. */
	private Map<String, String> dataCenterMap;
	
	/** The dc prefix. */
	private String dcPrefix;

	
	
	/**
	 * Set EDW profile data to session.
	 *
	 * @param profileId the profile id
	 * @param edwDataVO the edw data vo
	 * @return the profile edw info vo
	 * @throws NumberFormatException the number format exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 * @throws RepositoryException the repository exception
	 */
	
    public ProfileEDWInfoVO populateEDWProfileData(String profileId,ProfileEDWInfoVO edwDataVO) throws NumberFormatException, BBBSystemException, BBBBusinessException, RepositoryException{
		
		BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " populateEDWProfileData");
		RepositoryItem[] profileHeaderRepositoryItem =null;
		Map<String,List<String>> edwKeys = null;
		
			 
				       edwKeys = getEDWAttributes();
				       MutableRepositoryItem profileItem = (MutableRepositoryItem) getUserProfileRepository().getItem(profileId, BBBCoreConstants.USER);
				       edwDataVO.setATGProfileID(profileId);
					   edwDataVO.setEmail((String)profileItem.getPropertyValue(BBBCoreConstants.EMAIL));
				       
					   Map<String,String> profilekeys = new HashMap<String,String>();
					   profileHeaderRepositoryItem = getProfileHeaderItem(profileId);
					   
			           if (profileHeaderRepositoryItem != null) {
			        	   logDebug("EDW Data corresponding to profile:"+ Arrays.toString(profileHeaderRepositoryItem));
							JSONObject jsonObject = new JSONObject();
							
							Date modifiedDate =   (Date) profileHeaderRepositoryItem[0].
									getPropertyValue(BBBCoreConstants.LAST_MODIFIED_DATE);
							
							String profileAttributeItems = (String) profileHeaderRepositoryItem[0].
									getPropertyValue(BBBCoreConstants.PROFILE_ATTRIBUTE_VALUES);
							String[] profieAttriPairs = profileAttributeItems.split(Pattern.quote((String) BBBCoreConstants.PIPE_SYMBOL));
							for(String profileAttriKey : profieAttriPairs) {
								String[] profileAttriKeys = profileAttriKey.split(BBBCoreConstants.COLON);
								profilekeys.put(profileAttriKeys[0],profileAttriKeys[1]);
							}
							
						    String ttl = getEdwTTL();
						    if(getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL) != null)	
						    	ttl = getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, BBBCoreConstants.EDW_COFIG_TTL).get(0);
							boolean isDataStale = BBBUtility.isAfterExpDate(modifiedDate, ttl);
							edwDataVO.setEdwDataStale(isDataStale);
								
							
							
							logDebug("List of profile Attribute keys ready to send to SiteSpect :"+edwKeys);
							if(!BBBUtility.isMapNullOrEmpty(edwKeys)){
								for(String edwField : edwKeys.get(BBBCoreConstants.IS_SITESPECTDATA)) {
									if(profilekeys.keySet().contains(edwField)) {
										jsonObject.put(edwField,profilekeys.get(edwField));
									}
								 }
							}
							
							 logDebug("EDW Personalization JSON data:"+jsonObject.toString());
							 
							edwDataVO.setEdwDataJsonObject(jsonObject.toString());
							
							profileItem.setPropertyValue(BBBCoreConstants.PROFILE_EDW_DATA, profileAttributeItems);
							
						} 
			           boolean isTibcoEnable = true;
				          String isTibcoEnaledConfigKey = this.getAllValuesForKey(BBBCoreConstants.EDW_BCC_FIELDS, BBBCoreConstants.TIBCO_ENABLED_CONFIG_KEY).get(0);
				          if(BBBCoreConstants.FALSE.equalsIgnoreCase(isTibcoEnaledConfigKey)){
				        	  isTibcoEnable = false;
				          }
			          if((!edwDataVO.isSessionTIBCOcall() && (edwDataVO.isEdwDataStale() || profileHeaderRepositoryItem == null)) && isTibcoEnable){
			        	  //CALL TIBCO
			        	  edwDataVO.setEdwProfileAttributes(edwKeys.get(BBBCoreConstants.IS_TIBCODATA));
			        	  edwDataVO.setSessionTIBCOcall(true);
			        	  submitEDWProfileDataMesssage(edwDataVO);
			        	}
			         
		
		return edwDataVO;
	}

	protected RepositoryItem[] getProfileHeaderItem(String profileId)
			throws RepositoryException {
		RepositoryItem[] profileHeaderRepositoryItem;
		final RepositoryView profileEDWView = this.getEdwProfileDataRepository().getView(BBBCoreConstants.EDW_ITEM_DESCRIPTOR);
		   final RqlStatement statement = getEdwDataRqlStmt();
		   final Object params[] = new Object[1];
		   params[0] = profileId;
		   profileHeaderRepositoryItem = statement.executeQuery(profileEDWView, params);
		return profileHeaderRepositoryItem;
	}

    /**
	 * Method gets all the applicable edw attributes from EDWSiteSpectRepository.
	 *
	 * @return set of allowed siteSpectkeys
	 */
	public Map<String,List<String>> getEDWAttributes() {
		
		String siteId = getSiteId();
		if(BBBCoreConstants.SITE_BAB_CA.equals(siteId))
		{
			siteId = BBBCoreConstants.CA_CONSTANT;
		}
		Object[] params1 = null;
		String rql= BBBEximConstants.ALL;
		params1 = new Object[]{};
		Map<String,List<String>> edwKeys = new HashMap<String,List<String>>();
		List<String> edwSiteSpectKeys = new ArrayList<String>();
		List<String> edwTibcoKeys = new ArrayList<String>();
		boolean isSiteSpectvalue;
		boolean isTibcovalue;
		String profileAttrKey = null;
		String[] parts = null;
		String siteContext = null;
		
		IRepositoryWrapper iRepositoryWrapper = getRepositoryWrapper();		
		RepositoryItem[] items = getEdwRepoItems(params1, rql, iRepositoryWrapper);
			if (items != null && items.length > 0) {
				for(RepositoryItem item: items){
					profileAttrKey = (String) item.getPropertyValue(BBBCoreConstants.EDW_Data_Key);
					parts = profileAttrKey.split(BBBCoreConstants.UNDERSCORE);
					siteContext = parts[parts.length - 1];
					isSiteSpectvalue = (boolean) item.getPropertyValue(BBBCoreConstants.IS_SITESPECTDATA);
					isTibcovalue = (boolean) item.getPropertyValue(BBBCoreConstants.IS_TIBCODATA);
					if(isSiteSpectvalue && (siteId.equalsIgnoreCase(siteContext) || !getSites().contains(siteContext))) {
						 edwSiteSpectKeys.add(profileAttrKey);
					}
					if(isTibcovalue) {
						 edwTibcoKeys.add(profileAttrKey);
					}
					edwKeys.put(BBBCoreConstants.IS_SITESPECTDATA,edwSiteSpectKeys);
					edwKeys.put(BBBCoreConstants.IS_TIBCODATA,edwTibcoKeys);
				}
			}
		return edwKeys;
	}

		
	/**
	 * This method send message to TIBCO to populate EDW Repository.
	 *
	 * @param edwData the edw data
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public void submitEDWProfileDataMesssage(ProfileEDWInfoVO edwData) throws BBBBusinessException, BBBSystemException {
		logDebug("START: Submitting EDW Data Message");	
		EDWProfileDataVO edwDataRequest = new EDWProfileDataVO();
		edwData.setDataCentre(this.getDataCenterMap().get(this.getDcPrefix()));
		edwData.setUserToken(getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY,BBBWebServiceConstants.TXT_WSDLKEY_WSTOKEN).get(0));
		edwDataRequest.setProfileEDWData(edwData);
		
		edwDataRequest.setServiceName(BBBCoreConstants.EDW_DATA_SERVICE);
		ServiceHandlerUtil.sendTextMessage(edwDataRequest);
		
		logDebug("END: Submitting EDW Data Message");
	}
	
	/**
	 * The following methods are added for spock test case flexibility
	 */
	
	protected String getSiteId() {
		String siteId = SiteContextManager.getCurrentSiteId();
		return siteId;
	}

	protected RepositoryItem[] getEdwRepoItems(Object[] params1, String rql, IRepositoryWrapper iRepositoryWrapper) {
		RepositoryItem[] items = iRepositoryWrapper.queryRepositoryItems(BBBCoreConstants.EDW_SiteSpect_repo, rql, params1,true);
		return items;
	}

	protected RqlStatement getEdwDataRqlStmt() throws RepositoryException {
		return RqlStatement.parseRqlStatement(EDW_DATA_RQL);
	}
    
	protected RepositoryWrapperImpl getRepositoryWrapper() {
		return new RepositoryWrapperImpl(this.getEdwSiteSpectDataRepository());
	}

	
	/**
	 * Getters & setters section
	 */
	
	
	/**
	 * @return the edwSiteSpectDataRepository
	 */
	public GSARepository getEdwSiteSpectDataRepository() {
		return edwSiteSpectDataRepository;
	}

	/**
	 * @param edwSiteSpectDataRepository the edwSiteSpectDataRepository to set
	 */
	public void setEdwSiteSpectDataRepository(
			GSARepository edwSiteSpectDataRepository) {
		this.edwSiteSpectDataRepository = edwSiteSpectDataRepository;
	}

	/**
	 * @return the sites
	 */
	public List<String> getSites() {
		return sites;
	}

	/**
	 * @param sites the sites to set
	 */
	public void setSites(List<String> sites) {
		this.sites = sites;
	}
	/**
	 * Gets the user profile repository.
	 *
	 * @return the userProfileRepository
	 */
	public MutableRepository getUserProfileRepository() {
		return mUserProfileRepository;
	}

	/**
	 * Sets the user profile repository.
	 *
	 * @param pUserProfileRepository the new user profile repository
	 */
	public void setUserProfileRepository(final MutableRepository pUserProfileRepository) {
		this.mUserProfileRepository = pUserProfileRepository;
	}

	/**
	 * Gets the edw profile data repository.
	 *
	 * @return the edw profile data repository
	 */
	public GSARepository getEdwProfileDataRepository() {
		return edwProfileDataRepository;
	}

	/**
	 * Sets the edw profile data repository.
	 *
	 * @param edwProfileDataRepository the new edw profile data repository
	 */
	public void setEdwProfileDataRepository(GSARepository edwProfileDataRepository) {
		this.edwProfileDataRepository = edwProfileDataRepository;
	}
	
	/**
	 * Gets the edw ttl.
	 *
	 * @return the edw ttl
	 */
	public String getEdwTTL() {
		return edwTTL;
	}

	/**
	 * Sets the edw ttl.
	 *
	 * @param edwTTL the new edw ttl
	 */
	public void setEdwTTL(String edwTTL) {
		this.edwTTL = edwTTL;
	}

	/**
	 * Gets the data center map.
	 *
	 * @return the data center map
	 */
	public Map<String, String> getDataCenterMap() {
		return dataCenterMap;
	}

	/**
	 * Sets the data center map.
	 *
	 * @param dataCenterMap the data center map
	 */
	public void setDataCenterMap(Map<String, String> dataCenterMap) {
		this.dataCenterMap = dataCenterMap;
	}
	
	/**
	 * Gets the dc prefix.
	 *
	 * @return the dc prefix
	 */
	public String getDcPrefix() {
		return dcPrefix;
	}

	/**
	 * Sets the dc prefix.
	 *
	 * @param dcPrefix the new dc prefix
	 */
	public void setDcPrefix(String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}
	
}