package com.bbb.common.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.account.BBBProfileManager;
import com.bbb.account.profile.vo.ProfileEDWInfoVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/**
 * This class load EDW data
 * 
 * @author 
 *
 */

public class EDWProfileDataDroplet  extends BBBDynamoServlet {

	
    private BBBCatalogTools bbbCatalogTools;
    
    private int edwRepoCount;

	public BBBCatalogTools getBbbCatalogTools() {
		return bbbCatalogTools;
	}


	public void setBbbCatalogTools(BBBCatalogTools bbbCatalogTools) {
		this.bbbCatalogTools = bbbCatalogTools;
	}

	public int getEdwRepoCount() {
		return edwRepoCount;
	}

	public void setEdwRepoCount(int edwRepoCount) {
		this.edwRepoCount = edwRepoCount;
	} 
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException, IOException {
		
		logDebug("EDWProfileDataDroplet.service() method starts");
		
		getEDWData();
			
		logDebug("EDWProfileDataDroplet.service() method ends");
			
		}
	
	/**
	 * enter Java Doc
	 * @return
	 */
	public ProfileEDWInfoVO getEDWData() {

/*		BBBSessionBean session = (BBBSessionBean) ServletUtil
				.getCurrentRequest().resolveName(BBBCoreConstants.SESSION_BEAN);
*/		

		BBBSessionBean session = BBBProfileManager.resolveSessionBean(null);
		ProfileEDWInfoVO edwDataVO = session.getEdwDataVO();
		if (edwDataVO == null) {
			edwDataVO = new ProfileEDWInfoVO();
		}
		try {
			int repoRetryCount = edwDataVO.getRepoRetryCount();
			int maxEDWRepoRetry = getEdwRepoCount();
			if (null != getBbbCatalogTools().getAllValuesForKey(
					BBBCmsConstants.CONTENT_CATALOG_KEYS,
					BBBCoreConstants.MAX_EDW_REPO_RETRY)) {
				maxEDWRepoRetry = Integer.parseInt(getBbbCatalogTools()
						.getAllValuesForKey(
								BBBCmsConstants.CONTENT_CATALOG_KEYS,
								BBBCoreConstants.MAX_EDW_REPO_RETRY).get(0));
				logDebug("Max EDW Repository Counter:" + maxEDWRepoRetry);
			}
			if (repoRetryCount < maxEDWRepoRetry) {
				String profileId = ServletUtil.getCurrentRequest()
						.getCookieParameter(BBBCoreConstants.DYN_USER_ID);
				if ((edwDataVO.getEdwDataJsonObject() == null || edwDataVO
						.isEdwDataStale()) && !BBBUtility.isEmpty(profileId)) {
					edwDataVO = getBbbCatalogTools().populateEDWProfileData(
							profileId, edwDataVO);
					//if data is there and its not stale don't call EDW again.
					if (edwDataVO != null && ((edwDataVO.getEdwDataJsonObject() != null && !edwDataVO.isEdwDataStale()) || (repoRetryCount+1 == maxEDWRepoRetry))) {
						edwDataVO.setMaxRepoRetryFlag(true);
					}
				}
			} else {
				edwDataVO.setMaxRepoRetryFlag(true);
			}
		} catch (BBBSystemException | BBBBusinessException
				| RepositoryException be) {

			final String errMsg = "System Exception occured while fetching key(EDW keys) from EDWfields";
			this.logError(errMsg, be);

		} finally {
			if(null!=edwDataVO){
				edwDataVO.setRepoRetryCount(edwDataVO.getRepoRetryCount());
			}
			session.setEdwDataVO(edwDataVO);

		}
		return edwDataVO;
	}
		
		
	}

