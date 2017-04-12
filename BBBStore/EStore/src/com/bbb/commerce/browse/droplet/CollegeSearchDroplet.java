package com.bbb.commerce.browse.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.ServletException;

import atg.json.JSONException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.rest.catalog.vo.CollegeMerchandizeVO;
import com.bbb.search.integration.SearchManager;
import com.bbb.utils.BBBUtility;

public class CollegeSearchDroplet extends BBBDynamoServlet {
	
	private static final String NO_RESULT = "noResult";
	private static final String NO_RESULT_FOUND = "No Result Found";
	private static final String EMPTY = "empty";
	private static final String LIST_COLLGE_NAME_PATTERN = "listCollgeNamePattern";
	private static final String COLLEGE_MAX_COUNT = "collegeMaxCount";
	private static final String COLLEGE_GROUP_LIST = "CollegeGroupList";
	private static final String OUTPUT = "output";
	private static final String COLLEGE_MAP = "collegesMap";
	private static final String COLLEGES_COUNT = "collegesCount";
	private static final String COLLEGE_NAME_GRP = "CollegeNameGrp";
	private static final String ZERO = "0";
	
	private SearchManager mSearchManager;
	private BBBCatalogTools mCatalogTools;
	private boolean mAlphabetFound;
	private String[] mAlphabetList;
	private String[] mCollegeGroupList;
	private String mStateId;
	private String mCollegeMaxCount;

	public final static ParameterName PARAMETER_STATE_ID = ParameterName.getParameterName("stateId");
	
	

	
	/*
	 * Standard Method which will set college map and college group
	 * (non-Javadoc)
	 * 
	 * @see
	 * atg.servlet.DynamoServlet#service(atg.servlet.DynamoHttpServletRequest,
	 * atg.servlet.DynamoHttpServletResponse)
	 */
	@Override
	public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws ServletException, IOException {

		logDebug("In Service Method");
		final String stateId = pRequest.getParameter(PARAMETER_STATE_ID);
		Map<String, ArrayList<CollegeVO>> alphabetCollegeListMap = new TreeMap<String, ArrayList<CollegeVO>>();
		Set<String> setCollegeName = new TreeSet<String>();
		String pCatalogId = ZERO;
		if (stateId != null) {
			pCatalogId = stateId;
		}
		logDebug("Catalog Id For Search Manager"+pCatalogId);
		List<CollegeVO> listAllColleges = null;
		try {
			logDebug("College Droplet::: Calling getColleges Method");
			listAllColleges = getSearchManager().getAllColleges(pCatalogId);
			if (listAllColleges != null){
			if (listAllColleges.size() > getCollegeMaxCount()) {
				logDebug("List of Colleges are More Than :: "+getCollegeMaxCount());
				setCollegeName = processGroupCollege(setCollegeName, listAllColleges);
				pRequest.setParameter(COLLEGE_NAME_GRP, setCollegeName);
			}
			logDebug("Processing Single Group College");
			alphabetCollegeListMap = processSingleCollege(alphabetCollegeListMap, listAllColleges);
			pRequest.setParameter(COLLEGES_COUNT, listAllColleges.size());
			pRequest.setParameter(COLLEGE_MAP, alphabetCollegeListMap);

			pRequest.serviceLocalParameter(OUTPUT, pRequest, pResponse);
			}else{
				logDebug("Endeca Returned Empty List");	
				pRequest.setParameter(NO_RESULT, NO_RESULT_FOUND);
				pRequest.serviceLocalParameter(EMPTY, pRequest, pResponse);
			}
			logDebug("Exiting Server Method of College Droplet");
		} catch (BBBBusinessException bbbbEx) {
			logError(" BusinessException While fetching College List From Endeca", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("SYstem Exception While fetching College List From Endeca", bbbsEx);
		}
	}
	/**
	 * Method to return college group from Config
	 * @return
	 */
	public String[] getCollegeGroupList() {
		List<String> collegeGroupList = new ArrayList<String>();
		try {
			collegeGroupList = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, COLLEGE_GROUP_LIST);
			if (!collegeGroupList.isEmpty()) {
				this.mCollegeGroupList = collegeGroupList.get(0).split(",");
			}
		} catch (BBBBusinessException bbbbEx) {
			logError(" BusinessException While fetching College Group List From Configure Keys", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("SystemException While fetching College Group List From Configure Keys", bbbsEx);
		}
		return this.mCollegeGroupList;
	}

	/**
	 * Method to return College Max Count
	 * @return the mCollegeMaxCount
	 */
	public int getCollegeMaxCount() {
		List<String> collegeMaxList = new ArrayList<String>();
		try {
			collegeMaxList = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, COLLEGE_MAX_COUNT);
			if (!collegeMaxList.isEmpty()) {
				this.mCollegeMaxCount = collegeMaxList.get(0);
			}
		} catch (BBBBusinessException bbbbEx) {
			logError(" BusinessException While fetching College Group List From Configure Keys", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("SystemException While fetching College Group List From Configure Keys", bbbsEx);
		}
		return Integer.parseInt(this.mCollegeMaxCount);
	}

	/**
	 * @param mCollegeMaxCount
	 *            the mCollegeMaxCount to set
	 */
	public void setCollegeMaxCount(String mCollegeMaxCount) {
		this.mCollegeMaxCount = mCollegeMaxCount;
	}

	/**
	 * Method to return college Name Pattern for filtering the colleges
	 * @return the mListCollegeNamePatern
	 */
	public List<String> getListCollegeNamePatern() {
		List<String> listCollegeNamePatern = new ArrayList<String>();
		List<String> listPattern = new ArrayList<String>();
		try {
			listCollegeNamePatern = getCatalogTools().getAllValuesForKey(BBBCmsConstants.CONTENT_CATALOG_KEYS, LIST_COLLGE_NAME_PATTERN);
			if (!listCollegeNamePatern.isEmpty()) {
				for(String collegePatern:listCollegeNamePatern.get(0).split(",")){
					listPattern.add(collegePatern);
				}
			}
		} catch (BBBBusinessException bbbbEx) {
			logError(" BusinessException While fetching College Group List From Configure Keys", bbbbEx);
		} catch (BBBSystemException bbbsEx) {
			logError("SystemException While fetching College Group List From Configure Keys", bbbsEx);
		}
		return listPattern;
	}

	/**
	 * method to process the Group Colleges
	 * @param setCollegeName
	 * @param listAllColleges
	 * @return
	 */
	private Set<String> processGroupCollege(Set<String> setCollegeName, List<CollegeVO> listAllColleges) {
		String collegeName = null;
		String[] lstGroup = getCollegeGroupList();
		for (final CollegeVO collegeVO : listAllColleges) {
			setAlphabetFound(false);
			collegeName = collegeVO.getCollegeName();
			for (int j = 0; j < lstGroup.length; j++) {
				setCollegeName = prefixGroup(setCollegeName, collegeVO, collegeName, lstGroup[j]);
				if (getAlphabetFound()) {
					break;
				}
			}

		}

		return setCollegeName;
	}

	/**
	 * method for  Finding the prefix group
	 * @param setCollegeName
	 * @param collegeVO
	 * @param collegeName
	 * @param alphabetGroup
	 * @return
	 */
	private Set<String> prefixGroup(Set<String> setCollegeName, CollegeVO collegeVO, String collegeName, String alphabetGroup) {
		logDebug("In prefix Group method");
		String prefix = null;
		String collegeNamePatern = null;

		final Iterator<String> iterator = getListCollegeNamePatern().iterator();

		while (iterator.hasNext()) {
			prefix = null;
			logDebug("prefixGroup():::In While loop");
			collegeNamePatern = (String) iterator.next();
			if (collegeName.contains(collegeNamePatern)) {				
				prefix = ((Character) collegeName.substring(collegeNamePatern.length(), collegeName.length()).charAt(0)).toString();
				if(prefix.equals(" ")){
					prefix = ((Character) collegeName.substring(collegeNamePatern.length()+1, collegeName.length()).charAt(0)).toString();
				}
				logDebug("In if:: Prefix::"+prefix);
				break;
			} else {
				prefix = ((Character) collegeName.charAt(0)).toString().toUpperCase();
				logDebug("In else:: Prefix::"+prefix);
			}
		}

		char start = alphabetGroup.charAt(0);
		char end = alphabetGroup.charAt(alphabetGroup.length() - 1);
		for (char i = start; i <= end; i++) {
			if (null != prefix && i == prefix.codePointAt(0)) {
				setCollegeName.add(alphabetGroup);
				setAlphabetFound(true);
				break;
			}
		}
		logDebug("Exting prefixGroup() ");
		return setCollegeName;

	}

	/**
	 * Method to process single group College
	 * @param alphabetCollegeListMap
	 * @param listAllColleges
	 * @return
	 */
	private Map<String, ArrayList<CollegeVO>> processSingleCollege(Map<String, ArrayList<CollegeVO>> alphabetCollegeListMap, List<CollegeVO> listAllColleges) {
		String prefix = null;
		String collegeNamePatern = null;
		boolean prefixFound=false;
		for (CollegeVO collegeVO : listAllColleges) {

			final Iterator<String> iterator = getListCollegeNamePatern().iterator();

			while (iterator.hasNext()) {
				prefix = null;
				prefixFound=false;
				logDebug("prefixGroup():::In While loop");
				collegeNamePatern = (String) iterator.next();
				if (collegeVO.getCollegeName().startsWith(collegeNamePatern)) {				
					//prefix = ((Character) collegeVO.getCollegeName().substring(collegeNamePatern.length(), collegeVO.getCollegeName().length()).charAt(0)).toString();
					String subString = collegeVO.getCollegeName().substring(collegeNamePatern.length(), collegeVO.getCollegeName().length());
					if(!BBBUtility.isEmpty(subString)){
						prefix = ((Character)subString.trim().charAt(0)).toString().toUpperCase();	
						prefixFound=true;
					}
					
					logDebug("In if:: Prefix::"+prefix);
					break;
				} 
				
			}
			if(!prefixFound) {
				prefix = ((Character) collegeVO.getCollegeName().trim().charAt(0)).toString().toUpperCase();
				logDebug("In else:: Prefix::"+prefix);
			}
			logDebug("Prefix for single group:::"+prefix);
			
			if (alphabetCollegeListMap.containsKey(prefix)) {
				alphabetCollegeListMap.get(prefix).add(collegeVO);
			}else {
				final ArrayList<CollegeVO> newCollegeList = new ArrayList<CollegeVO>();
				newCollegeList.add(collegeVO);
				alphabetCollegeListMap.put(prefix, newCollegeList);
			}
		}

		return alphabetCollegeListMap;
	}
	
	/** This method is to expose Service method of this droplet to REST API.
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws JSONException 
	 * @throws BBBBusinessException
	 */
	public CollegeMerchandizeVO getCollegeGroups(String pStateCode) throws ServletException, IOException, JSONException, BBBBusinessException, BBBSystemException{
		CollegeSearchDroplet csDroplet = (CollegeSearchDroplet) ServletUtil.getCurrentRequest().resolveName("/com/bbb/commerce/browse/droplet/CollegeLookup");
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
		DynamoHttpServletResponse response = ServletUtil.getCurrentResponse();
		
		String pStateId = null;
		
		if(BBBUtility.isEmpty(pStateCode)){
			pStateId = "0";
		}
		else{
			for(StateVO sVo : this.getCatalogTools().getStateList()){
				if(pStateCode.equalsIgnoreCase(sVo.getStateCode())){
					if(null != sVo.getStateName()){
						pStateId = this.getSearchManager().getCatalogId(BBBSearchBrowseConstants.SCHOOL_STATE, sVo.getStateName());
					}
				}
			}
			if(null == pStateId){
				throw new BBBBusinessException(BBBCoreErrorConstants.BROWSE_ERROR_1044,"No Colleges are available for this state code.");
			}
		}
		
		request.setParameter("stateId",pStateId);
		
		Map<String, List<CollegeVO>> alphabetCollegeListMap = new TreeMap<String, List<CollegeVO>>();
		csDroplet.service(request, response);
		alphabetCollegeListMap = (Map<String, List<CollegeVO>>) request.getObjectParameter(COLLEGE_MAP);
		
		Map<String, Map<String,CollegeVO>> alphabetCollegeMap = new TreeMap<String, Map<String,CollegeVO>>();;
		
		if(null != alphabetCollegeListMap && !alphabetCollegeListMap.isEmpty()){
			for(String key: alphabetCollegeListMap.keySet()){
				Map<String,CollegeVO> pMap = new TreeMap<String, CollegeVO>();;
				for(CollegeVO pVO : alphabetCollegeListMap.get(key)){
					pMap.put(pVO.getCollegeId(), pVO);
				}
				alphabetCollegeMap.put(key, pMap);
			}
		}
		
		Set<String> clgBucket = new TreeSet<String>();
		clgBucket = (Set<String>) request.getObjectParameter(COLLEGE_NAME_GRP);
		CollegeMerchandizeVO cmVo = new CollegeMerchandizeVO();
		cmVo.setCollegeBucket(clgBucket);
		cmVo.setAlphabetCollegeListMap(alphabetCollegeMap);
		return cmVo;
	}
	
	/**
	 * @param pCollegeGroupList
	 */
	public void setCollegeGroupList(String[] pCollegeGroupList) {
		mCollegeGroupList = pCollegeGroupList;
	}

	/**
	 * @return the searchManager
	 */
	public SearchManager getSearchManager() {
		return mSearchManager;
	}

	/**
	 * @param searchManager
	 *            the searchManager to set
	 */
	public void setSearchManager(SearchManager pSearchManager) {
		this.mSearchManager = pSearchManager;
	}

	/**
	 * @return the alphabetFound
	 */
	public boolean getAlphabetFound() {
		return mAlphabetFound;
	}

	/**
	 * @param pAlphabetFound
	 *            the alphabetFound to set
	 */
	public void setAlphabetFound(boolean pAlphabetFound) {
		this.mAlphabetFound = pAlphabetFound;
	}

	/**
	 * @return the alphabetList
	 */
	public String[] getAlphabetList() {
		return mAlphabetList;
	}

	/**
	 * @param pAlphabetList
	 *            the alphabetList to set
	 */
	public void setAlphabetList(String[] pAlphabetList) {
		this.mAlphabetList = pAlphabetList;
	}

	/**
	 * @return the stateId
	 */
	public String getStateId() {
		return mStateId;
	}

	/**
	 * @param pStateId
	 *            the stateId to set
	 */
	public void setStateId(String pStateId) {
		this.mStateId = pStateId;
	}

	/**
	 * @return the catalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return mCatalogTools;
	}

	/**
	 * @param catalogTools
	 *            the catalogTools to set
	 */
	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.mCatalogTools = catalogTools;
	}

}
