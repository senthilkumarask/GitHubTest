package com.bbb.cms.email;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import nl.captcha.Captcha;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.json.JSONException;
import atg.json.JSONObject;
import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.browse.BBBEmailSenderFormHandler;
import com.bbb.cms.manager.LblTxtTemplateManager;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;

public class EmailAPageFormHandler extends BBBEmailSenderFormHandler{

	private static final String INCORRECT_CAPTHA_MESSAGE_KEY = "Incorrect Captha Message Key";
	private static final String UTF_8 = "UTF-8";
	private static final String RECIPIENT_EMAIL = "&recipientEmail=";
	private static final String RESPONSE_TYPE_JSON = "application/json";
	private static final String GENERAL_ERROR = "general";
	private static final String SCHEME_APPEND ="://";
	
	private String captchaAnswer;
	private boolean validateCaptcha;
	private String mSubjectParamName;
	private String mLocale;
	private String mEmailType;
	private LblTxtTemplateManager mLblTxtTemplateManager;
	private BBBCatalogTools mBBBCatalogTools;
	private String incorrectCapthaMsgKey;
	private String mSiteId;
	private String pageTitle;
	private String fromPage;// Page Name set from JSP
	private Map<String,String> successUrlMap;
	private Map<String,String> errorUrlMap;
	
	//For BBBSL-3817, MessageCC to sender flag
	private boolean mCcFlag;
	
	/**
	 * @return the fromPage
	 */
	public String getFromPage() {
		return fromPage;
	}


	/**
	 * @param fromPage the fromPage to set
	 */
	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	/**
	 * @return the successUrlMap
	 */
	public Map<String, String> getSuccessUrlMap() {
		return successUrlMap;
	}


	/**
	 * @param successUrlMap the successUrlMap to set
	 */
	public void setSuccessUrlMap(Map<String, String> successUrlMap) {
		this.successUrlMap = successUrlMap;
	}


	/**
	 * @return the errorUrlMap
	 */
	public Map<String, String> getErrorUrlMap() {
		return errorUrlMap;
	}


	/**
	 * @param errorUrlMap the errorUrlMap to set
	 */
	public void setErrorUrlMap(Map<String, String> errorUrlMap) {
		this.errorUrlMap = errorUrlMap;
	}


	public String getCaptchaAnswer() {
		return captchaAnswer;
	}


	public void setCaptchaAnswer(String captchaAnswer) {
		this.captchaAnswer = captchaAnswer;
	}


	public String getSubjectParamName() {
		return mSubjectParamName;
	}


	public void setSubjectParamName(String mSubjectParamName) {
		this.mSubjectParamName = mSubjectParamName;
	}


	public void setValidateCaptcha(boolean validateCaptcha) {
		this.validateCaptcha = validateCaptcha;
	}
	
	public String getIncorrectCapthaMsgKey() {
		return incorrectCapthaMsgKey;
	}


	public void setIncorrectCapthaMsgKey(String incorrectCapthaMsgKey) {
		this.incorrectCapthaMsgKey = incorrectCapthaMsgKey;
	}


	public String getLocale() {
		return mLocale;
	}


	public void setLocale(String mLocale) {
		this.mLocale = mLocale;
	}

	public String getEmailType() {
		return mEmailType;
	}


	public void setEmailType(String pEmailType) {
		this.mEmailType = pEmailType;
	}


	public BBBCatalogTools getBbbCatalogTools() {
		return mBBBCatalogTools;
	}

	public String getSiteId() {
		return mSiteId;
	}


	public void setSiteId(String pSiteId) {
		this.mSiteId = pSiteId;
	}

	public String getPageTitle() {
		return pageTitle;
	}


	public void setPageTitle(String pageTitle) {
		this.pageTitle = pageTitle;
	}


	public void setBbbCatalogTools(BBBCatalogTools pBBBCatalogTools) {
		this.mBBBCatalogTools = pBBBCatalogTools;
	}

	public LblTxtTemplateManager getLblTxtTemplateManager() {
		return mLblTxtTemplateManager;
	}


	public void setLblTxtTemplateManager(LblTxtTemplateManager pLblTxtTemplateManager) {
		this.mLblTxtTemplateManager = pLblTxtTemplateManager;
	}

	
	/**
	 * @return the validateCaptcha
	 */
	public boolean isValidateCaptcha() {
		return validateCaptcha;
	}
	
	
	
	
	/**
	 * @return the URL of the success page.
	 */
	public String getSuccessURL() {
		return super.getSuccessURL() + RECIPIENT_EMAIL
				+ getRecipientEmail().trim();
	}

	/**
	 * This method is overridden to validate the Captcha entered by the user.
	 * 
	 * @param pRequest
	 * @param pResponse
	 * @return boolean
	 */	

	public boolean handleSend(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {
		String siteId = SiteContextManager.getCurrentSiteId();
		if (StringUtils.isNotEmpty(getFromPage())) {
			setSuccessURL(pRequest.getContextPath()
					+ getSuccessUrlMap().get(getFromPage()));
			setErrorURL(pRequest.getContextPath()
					+ getErrorUrlMap().get(getFromPage()));

		}
				
		JSONObject responseJson = new JSONObject();
		pResponse.setContentType(RESPONSE_TYPE_JSON);
		
		try{
			if(validateCaptcha(pRequest,pResponse)){
				
				if(pRequest.getHeader(BBBCoreConstants.REFERRER) != null)
				{					
					this.setPageTitle(pRequest.getHeader(BBBCoreConstants.REFERRER));
				}
				if(SiteContextManager.getCurrentSiteId() != null)
				{
					this.setSiteId(SiteContextManager.getCurrentSiteId());
				}				
				Boolean isEmailSuccess = super.handleSend(pRequest, pResponse);
				
				if(!isEmailSuccess){

					responseJson.put(BBBCoreConstants.ERROR, BBBCoreConstants.SERVER);
					responseJson.put(BBBCoreConstants.ERROR_MESSAGES, getLblTxtTemplateManager().getErrMsg("err_email_internal_error", pRequest.getLocale().getLanguage(), null, null));
				} 
				else
				{					
					Map<String, String> placeHolderMap = new HashMap<String, String>();
					placeHolderMap.put(BBBCoreConstants.RECIPIENT_EMAIL, getRecipientEmail());
					placeHolderMap.put(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE, getMessage());
					responseJson.put(BBBCoreConstants.SUCCESS , getLblTxtTemplateManager().getPageTextArea("txt_email_sent_msg", pRequest.getLocale().getLanguage(), placeHolderMap, null));
				}
			}
			else{
				//when captch is incorrect
				responseJson.put(BBBCoreConstants.ERROR, GENERAL_ERROR);	
				String incorrectCapthaMsg = getLblTxtTemplateManager().getErrMsg(getIncorrectCapthaMsgKey(), pRequest.getLocale().getLanguage(), null, null);
				if(incorrectCapthaMsg == null)
				{
					responseJson.put(BBBCoreConstants.ERROR_MESSAGES, INCORRECT_CAPTHA_MESSAGE_KEY);
				}
				else
				{
					responseJson.put(BBBCoreConstants.ERROR_MESSAGES, incorrectCapthaMsg);
				}

			}

			final PrintWriter out = pResponse.getWriter();
			out.print(responseJson.toString());
			out.flush();
			out.close();
		} catch (JSONException e) {
			 
		      logError(LogMessageFormatter.formatMessage(pRequest, "EmailAPageFormHandler|handleSend()|JSONException","catalog_1055"),e);    
		     
			  addFormException(new DropletException("Exception while sending email at underlying layer",BBBCoreErrorConstants.CMS_ERROR_1000));
		}         

		return checkFormRedirect(null,
				getErrorURL(), pRequest, pResponse);
		

	}
	
	// -------------------------------------
		protected void redirectOrForward(DynamoHttpServletRequest pRequest,
				DynamoHttpServletResponse pResponse, String pURL) throws IOException,
				ServletException {
			pResponse.sendLocalRedirect(pURL, pRequest);
		}
	
	/**
	 * This Method validates the value of the captcha entered by the user and sends back the appropriate response.
	 * @param pRequest
	 * @param pResponse
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public boolean validateCaptcha(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException, IOException{

		boolean success = true;
//		Captcha captcha = (Captcha) pRequest.getSession().getAttribute(Captcha.NAME);
		BBBSessionBean sessionBean = (BBBSessionBean) pRequest.resolveName(BBBGiftRegistryConstants.SESSION_BEAN);
		Captcha captcha = sessionBean.getCaptcha();

		pRequest.setCharacterEncoding(UTF_8);

		//if captcha validation is enabled 
		if (isValidateCaptcha() && !captcha.isCorrect(getCaptchaAnswer())) {
			success = false;
		}
		return success;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Map collectParams(DynamoHttpServletRequest pRequest) {
		 Map emailParams = super.collectParams(pRequest); 
		 HashMap placeHolderValues = new HashMap();
		 String pageTitle = this.getPageTitle();
		 String emailSubject = "";
		 String pageTitleText = "";
		 		 
		 
		 final Calendar currentDate = Calendar.getInstance();				
		 long uniqueKeyDate = currentDate.getTimeInMillis();		 
		 
		 if(pageTitle != null)
		{
		 if(pageTitle.contains(BBBCmsConstants.EMAIL_REGISTRY_CHECKLIST))
		 {
			 emailSubject = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_SUBJECT_BRIDAL_REGISTRY_CHECKLIST, pRequest.getLocale().getLanguage(), null, null);
			 pageTitleText = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_BRIDAL_REGISTRY_CHECKLIST_PAGE, pRequest.getLocale().getLanguage(), null, null);
		 }
		 else if(pageTitle.contains(BBBCmsConstants.EMAIL_COLLEGE_CHECKLIST))
		 {
			 emailSubject = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_SUBJECT_COLLEGE_CHECKLIST, pRequest.getLocale().getLanguage(), null, null);
			 pageTitleText = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_COLLEGE_CHECKLIST_PAGE, pRequest.getLocale().getLanguage(), null, null);
		 }
		 else if(pageTitle.contains(BBBCmsConstants.EMAIL_BRIDAL_SHOW))
		 {
			 emailSubject = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_SUBJECT_BRIDAL_SHOWS, pRequest.getLocale().getLanguage(), null, null);
			 pageTitleText = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_BRIDAL_SHOWS_PAGE, pRequest.getLocale().getLanguage(), null, null);
		 }
		 else if(pageTitle.contains(BBBCmsConstants.EMAIL_BABY_EVENTS))
		 {
			 emailSubject = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_SUBJECT_BABY_EVENTS, pRequest.getLocale().getLanguage(), null, null);
			 pageTitleText = getLblTxtTemplateManager().getPageLabel(BBBCmsConstants.EMAIL_BABY_EVENTS_PAGE, pRequest.getLocale().getLanguage(), null, null);
		 }
		 
		 placeHolderValues.put(BBBCmsConstants.EMAIL_TYPE, this.getEmailType());
		 placeHolderValues.put(BBBCmsConstants.FRM_DATA_SITE_ID, this.getSiteId());	
		 placeHolderValues.put(BBBGiftRegistryConstants.FRMDATA_COMMENT_MESSAGE,getMessage());
		 placeHolderValues.put(BBBCmsConstants.EMAIL_SUBJECT, emailSubject);
		 placeHolderValues.put(BBBCmsConstants.FRM_DATA_SENDERS_EMAIL, emailParams.get(super.getSenderEmailParamName()).toString());
		 placeHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE_TEXT, pageTitleText);
		 placeHolderValues.put(BBBCmsConstants.WEBSITE_NAME, pRequest.getScheme() + SCHEME_APPEND + pRequest.getServerName());
		 placeHolderValues.put(BBBCoreConstants.EMAIL_PERSIST_ID , uniqueKeyDate);
		 
		 //For BBSL-3817, if checkbox is checked then only send the email to the sender.
		 if(isCcFlag()){
			 emailParams.put("messageCC", getSenderEmail());
		 }
		
			 placeHolderValues.put(BBBCmsConstants.FRM_DATA_PAGE_TITLE, pageTitle);
		 }
		 
		 emailParams.put(BBBCmsConstants.PLACE_HOLDER_VALUES, placeHolderValues);
		 return emailParams;
	}
	
	public boolean isCcFlag() {
		return mCcFlag;
	}


	public void setCcFlag(boolean mCcFlag) {
		this.mCcFlag = mCcFlag;
	}
}

