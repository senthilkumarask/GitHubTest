package com.bbb.certona.utils;

import java.io.UnsupportedEncodingException;

import atg.core.util.StringUtils;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.certona.vo.CertonaRequestVO;
import com.bbb.constants.TBSConstants;

/**
 * The class fetches the certona response from the web
 * service. - siteId,isRobot,ResponseVO,schemenames
 * 
 */
public class TBSCertonaResponseUtils extends CertonaResponseUtils {
	
	private Repository mStoreRepository;
	/**
	 * Populate CertonaRequestVo with required parameters
	 * 
	 * @param pRequest
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@Override
	public CertonaRequestVO populateRequestVO(final String pSiteId, final String pSchemeNames, final DynamoHttpServletRequest pRequest, 
			final String shippingThreshold, final boolean isbabyCAMode) throws UnsupportedEncodingException {
		vlogDebug("TBSCertonaResponseUtils.populateRequestVO method start()");
		
		RepositoryItem lStoreRepoItem = null;
		String lStoreId = (String)ServletUtil.getCurrentRequest().getSession().getAttribute(TBSConstants.STORE_NUMBER_LOWER);
		vlogDebug("The Current Store Id is ::"+lStoreId);
		
		try {
			if(!StringUtils.isBlank(lStoreId)){
				lStoreRepoItem = getStoreRepository().getItem(lStoreId,"store");
				vlogDebug("The Store Repository Item is"+lStoreRepoItem);
			}
		} catch (RepositoryException e) {
			vlogError("Error getting StoreRepository Item"+e.getMessage()+"for store"+lStoreId);
		}
		if(null!=lStoreRepoItem){
			if(lStoreRepoItem.getPropertyValue(TBSConstants.LATITUDE)!=null){
			pRequest.setParameter(TBSConstants.LATITUDE, lStoreRepoItem.getPropertyValue(TBSConstants.LATITUDE));
			}
			if(lStoreRepoItem.getPropertyValue(TBSConstants.LONGITUDE)!=null){
			pRequest.setParameter(TBSConstants.LONGITUDE, lStoreRepoItem.getPropertyValue(TBSConstants.LONGITUDE));
			}
		}
		vlogDebug("TBSCertonaResponseUtils.populateRequestVO method end()");
		return super.populateRequestVO(pSiteId, pSchemeNames, pRequest, shippingThreshold, isbabyCAMode);
	}
	/**
	 * @return the storeRepository
	 */
	public Repository getStoreRepository() {
		return mStoreRepository;
	}
	/**
	 * @param pStoreRepository the storeRepository to set
	 */
	public void setStoreRepository(Repository pStoreRepository) {
		mStoreRepository = pStoreRepository;
	}
}
