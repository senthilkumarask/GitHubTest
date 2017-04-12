package com.bbb.utils;

import static com.bbb.constants.BBBAccountConstants.OPARAM_OUTPUT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import atg.nucleus.ServiceException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.certona.vo.CertonaResponseVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCertonaConstants;

public class BBBLogBuildNumber extends BBBDynamoServlet{

	private boolean enabled;
	private String versionFile;
	private String svnURLFile;
	private String svnBuildDateFile;
	private String svnTagNumberFile;
	
	private String versionNumber;
	private String buildDate;
	private String buildFrom;
	private String dcPrefix;
	
	public static final String BUILD_NUM_PREPEND = "| Build : ";
	public static final String BUILD_FROM_PREPEND = " | Build From : ";
	public static final String TAG_ID_PREPEND = " | Tag Id : ";
	public static final String BUILD_DATE_PREPEND = " | Build Created On : ";
	

	public void doStartService() throws ServiceException {
		
		if(isEnabled()){
			
			Properties buildProperty=getBuildProperties();
			
			try{
				buildProperty.load(getResourceAsStream(getVersionFile()));
				
				setVersionNumber(buildProperty.getProperty("revision.number"));
 
				buildProperty.load(getResourceAsStream(getSvnTagNumberFile()));
				
				setBuildFrom(buildProperty.getProperty("build.from"));			
				
				buildProperty.load(getResourceAsStream(getSvnBuildDateFile()));
				
				setBuildDate(buildProperty.getProperty("build.created.on"));
				
				
			}catch(IOException ioe){
				logError(ioe);
			}//catch any exception as we dont want to break page in any case
			catch(Exception e){
				logError(e);
			}
			
		}
		
	}
	/**
	 * @return
	 */
	public InputStream getResourceAsStream(String resource) {
		return Thread.currentThread().getContextClassLoader().
				getResourceAsStream(resource);
	}
	/**
	 * @return
	 */
	public Properties getBuildProperties() {
		return new Properties();
	}
	
	@Override
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse){

		if(isEnabled()){

			try{
				pRequest.setParameter("BUILD_NUM",BUILD_NUM_PREPEND +
						getVersionNumber());
				
				pRequest.setParameter("BUILD_FROM",BUILD_FROM_PREPEND +
						getBuildFrom());
				
				pRequest.setParameter("TAG_ID",TAG_ID_PREPEND +
						getVersionNumber());
				
				pRequest.setParameter("BUILD_DATE",BUILD_DATE_PREPEND +
						getBuildDate());
				
				pRequest.setParameter("BUILD_TAG_NUM", getBuildFrom() + getDcPrefix());

				pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);	
				
				
			}catch(IOException ioe){
				logError(ioe);
			}//catch any exception as we dont want to break page in any case
			catch(Exception e){
				logError(e);
			}
			
		}
		
	}	
	


	/**This will be used by mobile through web service call
	 * 
	 * @return String
	 */

	public String getBuildNumber() {

		final DynamoHttpServletRequest pRequest = ServletUtil
				.getCurrentRequest();
		final DynamoHttpServletResponse pResponse = ServletUtil
				.getCurrentResponse();
		String buildFrom = null;

		this.service(pRequest, pResponse);
		buildFrom = (String) pRequest.getParameter("BUILD_TAG_NUM");
		return buildFrom;
	}
	
	/**
	 * @return the enable
	 */
	public boolean isEnabled() {
		return enabled;
	}
	/**
	 * @param enable the enable to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	/**
	 * @return the versionFile
	 */
	public String getVersionFile() {
		return versionFile;
	}

	/**
	 * @param versionFile the versionFile to set
	 */
	public void setVersionFile(String versionFile) {
		this.versionFile = versionFile;
	}
	/**
	 * @return the versionFile
	 */
	public String getSvnURLFile() {
		return svnURLFile;
	}

	/**
	 * @param versionFile the versionFile to set
	 */
	public void setSvnURLFile(String pSvnURLFile) {
		this.svnURLFile = pSvnURLFile;
	}

	/**
	 * @return the svnBuildDateFile
	 */
	public String getSvnBuildDateFile() {
		return svnBuildDateFile;
	}

	/**
	 * @param svnBuildDateFile the svnBuildDate to set
	 */
	public void setSvnBuildDateFile(String svnBuildDateFile) {
		this.svnBuildDateFile = svnBuildDateFile;
	}
	/**
	 * @return the svnTagNumberFile
	 */
	public String getSvnTagNumberFile() {
		return svnTagNumberFile;
	}
	/**
	 * @param svnTagNumberFile the svnTagNumberFile to set
	 */

	public void setSvnTagNumberFile(String svnTagNumberFile) {
		this.svnTagNumberFile = svnTagNumberFile;
	}
	
	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber() {
		return versionNumber;
	}

	/**
	 * @return the buildDate
	 */
	public String getBuildDate() {
		return buildDate;
	}

	/**
	 * @param versionNumber the versionNumber to set
	 */
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	/**
	 * @param buildDate the buildDate to set
	 */
	public void setBuildDate(String buildDate) {
		this.buildDate = buildDate;
	}

	/**
	 * @return the buildFrom
	 */
	public String getBuildFrom() {
		return buildFrom;
	}

	/**
	 * @param buildFrom the buildFrom to set
	 */
	public void setBuildFrom(String buildFrom) {
		this.buildFrom = buildFrom;
	}

	/**
	 * @return the dcPrefix
	 */
	public String getDcPrefix() {
		return this.dcPrefix;
	}

	/**
	 * @param dcPrefix
	 *            the dcPrefix to set
	 */
	public void setDcPrefix(final String dcPrefix) {
		this.dcPrefix = dcPrefix;
	}
		
}
