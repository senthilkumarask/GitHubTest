package com.bbb.account;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

import com.bbb.account.BBBProfileTools;
import com.bbb.account.vo.CreateWalletRespVo;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.utils.BBBUtility;

/*
 * This class assigns the wallet id to the profile.
 *
 */
public class AddWalletIdtoProfileDroplet extends BBBDynamoServlet {
	
	private BBBProfileTools profileTools;
	private Profile userProfile;
	private BBBGetCouponsManager couponsManager;
	private BBBProfileManager profileManager;
	private String  addWalletIdtoProfileSuccessURL;
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private BBBCatalogTools catalogTools;
	
	public BBBCatalogTools getCatalogTools() {
		return catalogTools;
	}

	public void setCatalogTools(BBBCatalogTools catalogTools) {
		this.catalogTools = catalogTools;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return mLblTxtTemplateManager;
	}

	public void setLblTxtTemplateManager(
			LblTxtTemplateManager mLblTxtTemplateManager) {
		this.mLblTxtTemplateManager = mLblTxtTemplateManager;
	}
	
	public Profile getUserProfile() {
		return this.userProfile;
	}

	public void setUserProfile(Profile userProfile) {
		this.userProfile = userProfile;
	}

	public BBBProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(BBBProfileTools profileTools) {
		this.profileTools = profileTools;
	}
	
	public BBBGetCouponsManager getCouponsManager() {
		return couponsManager;
	}
	public void setCouponsManager(BBBGetCouponsManager couponsManager) {
		this.couponsManager = couponsManager;
	}

	   /** @return mManager */
    public final BBBProfileManager getProfileManager() {
        return this.profileManager;
    }

    /** @param profileManager */
    public final void setProfileManager(final BBBProfileManager profileManager) {
        this.profileManager = profileManager;
    }
    
    public String getAddWalletIdtoProfileSuccessURL() {
		return addWalletIdtoProfileSuccessURL;
	}

	public void setAddWalletIdtoProfileSuccessURL(
			String addWalletIdtoProfileSuccessURL) {
		this.addWalletIdtoProfileSuccessURL = addWalletIdtoProfileSuccessURL;
	}
	
	/**
	 * This method is used to add the wallet id to the profile
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @throws ServletException
	 * @throws IOException
	 */
	
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		
		logDebug("AddWalletIdtoProfileDroplet.service() method Started");	     	
		
		String mobilePhone = null;
		String firstName =null;
		String lastName= null;
		String address =null;
		String city = null;
		String state = null;
		String postalCode = null;
		String wallet_id="";
			
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		RepositoryItem bbbUserProfile = getUserProfile();
		Profile profile =  (Profile)pRequest.resolveName("/atg/userprofiling/Profile");				
		if(profile!=null){
			wallet_id =(String) profile.getPropertyValue("walletId");
		}
		
		if(!BBBUtility.isEmpty(wallet_id)){			
			
		  logDebug("Wallet id is present in the profile\t:" + wallet_id);		      								
		  sessionBean.setCouponsWelcomeMsg(false);		
		}else{
			
			String pSiteId = SiteContextManager.getCurrentSiteId();
			boolean createWalletFlag = false;
			try{
			List<String> createWalletForCanada = this.getCatalogTools().getAllValuesForKey(BBBCoreConstants.FLAG_DRIVEN_FUNCTIONS, "createCouponWalletForCanada");
			if (createWalletForCanada != null && !createWalletForCanada.isEmpty()) {
				createWalletFlag = Boolean.parseBoolean(createWalletForCanada.get(0));
			}
			}catch (BBBSystemException bse) {
				this.logError("System Exception occured while fetching key(createWalletFlag) from FlagDrivenFunctions", bse);
			} catch (BBBBusinessException bbe) {
				this.logError("Business Exception occured while fetching key(createWalletFlag) from FlagDrivenFunctions", bbe);
			}					
			if(BBBCoreConstants.SITE_BAB_CA.equals(pSiteId) &&(!createWalletFlag)){
						
			 logDebug("no need to add the wallet id to Profile for Canada site\t:");		      								
				
			} else{
			
				String emailAddr =(String) bbbUserProfile.getPropertyValue("login");
				
				CreateWalletRespVo createWalletRespVo;
				try {
					createWalletRespVo = couponsManager.createWallet(emailAddr, mobilePhone, firstName, lastName, address, city, state, postalCode);
					if(createWalletRespVo.getmErrorStatus().isErrorExists()){        						
						// Web Service: On an unsuccessful call		
						ErrorStatus errStatus = createWalletRespVo.getmErrorStatus();						
						if(!BBBUtility.isEmpty(errStatus.getErrorMessage()))	//Technical Error
						{
							logError(LogMessageFormatter.formatMessage(null, "AddWalletIdtoProfileDroplet::service: Technical Error received while fetching createWallet System \n Error returned from service : " + errStatus.getErrorMessage() , BBBCoreErrorConstants.ACCOUNT_ERROR_1118));			               
						}		
					}else{
						
						// Web Service: On a successful call        						
						wallet_id =  createWalletRespVo.getWalletId();
						if(StringUtils.isEmpty(wallet_id)){								
						   logDebug("We got wallet_id from Webservice Call\t:" + wallet_id);		      								
								       							
						} else{
							//Persisting walletId on BBB_WALLET with current Profile
							if(null!=profile){
	    					getProfileManager().addWalletIdToProfile(profile.getRepositoryId(), wallet_id);
							}
	    					sessionBean.setCouponsWelcomeMsg(true);
						}
					}
				} catch (BBBSystemException e) {				
					this.logError(
							LogMessageFormatter
									.formatMessage(
											pRequest,
											"BBBSystemException from service method of AddWalletIdtoProfileDroplet",
											BBBCoreErrorConstants.COUPON_WALLET_ERROR_1002),e);	
				} catch (BBBBusinessException e) {				
					this.logError(
							LogMessageFormatter
									.formatMessage(
											pRequest,
											"BBBBusinessException from service method of AddWalletIdtoProfileDroplet",
											BBBCoreErrorConstants.COUPON_WALLET_ERROR_1002),e);	
				}
			}
		}
		pRequest.serviceLocalParameter("output", pRequest, pResponse);		
		logDebug("AddWalletIdtoProfileDroplet.service() method ended");
	}		
}
