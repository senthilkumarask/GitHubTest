package com.bbb.account.profile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.bbb.account.profile.vo.ProfileVO;
import com.bbb.account.profile.vo.RegistryVO;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;

public class FeedFileParserImpl extends BBBGenericService implements FeedFileParser{

	private static final String CONTENT_CATALOG_CONFIG_KEY = "ContentCatalogKeys";
	private static final String BED_BATH_AND_BEYOND_US_SITE_ID = "BedBathUSSiteCode";
	private static final String BED_BATH_AND_BEYOND_CA_SITE_ID = "BedBathCanadaSiteCode";
	private static final String BUY_BUY_BABY_SITE_ID = "BuyBuyBabySiteCode";
	private static final String PROFILE_FEED_RESPONSE_HEADER = "ID|MEMBER_ID|EMAIL_ADDRESS|SITE_ID|FIRST_NAME|LAST_NAME|PHONE_NUMBER|MOBILE_NUMBER|EMAIL_OPTION|LAST_ACC_DATE|FAV_STORE_ID|PROFILE_ID|IS_ERROR|ERROR_CODE|RESPONSE_MESSAGE";
	private static final String REGISTRY_FEED_RESPONSE_HEADER = "ID|REGISTRY_ID|EMAIL_ADDRESS|SITE_ID|REGISTRY_TYPE|EVENT_DATE|IS_REGISTRANT|PROFILE_ID|IS_ERROR|ERROR_CODE|RESPONSE_MESSAGE";
	/**
	 * profile feed header map
	 */
	private Map<String, String> mProfileFeedHeader;

	/**
	 * Registry feed header map
	 */
	private Map<String, String> mRegistryFeedHeader;

	/**
	 * site association map
	 */
	private Map<String, String> mSiteAssociationMap;

	/**
	 * profile feed location
	 */
	private String mProfileFeedLocation;

	/**
	 * registry feed location
	 */
	private String mRegistryFeedLocation;

	/**
	 * profile feed location
	 */
	private String mProfileFeedSuccessResponseLocation;

	/**
	 * registry feed location
	 */
	private String mRegistryFeedSuccessResponseLocation;

	/**
	 * BBB Catalog tool instance
	 */
	private BBBCatalogTools mCatalogTools;

	/**
	 * co registrant feed response location
	 */
	private String mCoregistrantFeedSuccessResponseLocation;

	/**
	 * profile feed failure response location
	 */
	private String mProfileFeedFailureResponseLocation;

	/**
	 * registry feed failure response location
	 */
	private String mRegistryFeedFailureResponseLocation;

	/**
	 * co registrant feed failure response location
	 */
	private String mCoregistrantFeedFailureResponseLocation;

	/**
	 * @return the mCoregistrantFeedFailureResponseLocation
	 */
	public String getCoregistrantFeedFailureResponseLocation() {
		return this.mCoregistrantFeedFailureResponseLocation;
	}

	/**
	 * @param pCoregistrantFeedFailureResponseLocation the pCoregistrantFeedFailureResponseLocation to set
	 */
	public void setCoregistrantFeedFailureResponseLocation(
			final String pCoregistrantFeedFailureResponseLocation) {
		this.mCoregistrantFeedFailureResponseLocation = pCoregistrantFeedFailureResponseLocation;
	}

	/**
	 * @return the mRegistryFeedFailureResponseLocation
	 */
	public String getRegistryFeedFailureResponseLocation() {
		return this.mRegistryFeedFailureResponseLocation;
	}

	/**
	 * @param pRegistryFeedFailureResponseLocation the pRegistryFeedFailureResponseLocation to set
	 */
	public void setRegistryFeedFailureResponseLocation(
			final String pRegistryFeedFailureResponseLocation) {
		this.mRegistryFeedFailureResponseLocation = pRegistryFeedFailureResponseLocation;
	}

	/**
	 * @return the mProfileFeedFailureResponseLocation
	 */
	public String getProfileFeedFailureResponseLocation() {
		return this.mProfileFeedFailureResponseLocation;
	}

	/**
	 * @param pProfileFeedFailureResponseLocation the pProfileFeedFailureResponseLocation to set
	 */
	public void setProfileFeedFailureResponseLocation(
			final String pProfileFeedFailureResponseLocation) {
		this.mProfileFeedFailureResponseLocation = pProfileFeedFailureResponseLocation;
	}

	/**
	 * @return the mCoregistrantFeedSuccessResponseLocation
	 */
	public String getCoregistrantFeedSuccessResponseLocation() {
		return this.mCoregistrantFeedSuccessResponseLocation;
	}

	/**
	 * @param pCoregistrantFeedSuccessResponseLocation the pCoregistrantFeedSuccessResponseLocation to set
	 */
	public void setCoregistrantFeedSuccessResponseLocation(
			final String pCoregistrantFeedSuccessResponseLocation) {
		this.mCoregistrantFeedSuccessResponseLocation = pCoregistrantFeedSuccessResponseLocation;
	}

	/**
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * @param mCatalogTools the mCatalogTools to set
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * @return the mProfileFeedLocation
	 */
	public String getProfileFeedLocation() {
		return this.mProfileFeedLocation;
	}

	/**
	 * @param pProfileFeedLocation the pProfileFeedLocation to set
	 */
	public void setProfileFeedLocation(final String pProfileFeedLocation) {
		this.mProfileFeedLocation = pProfileFeedLocation;
	}

	/**
	 * @return the mRegistryFeedLocation
	 */
	public String getRegistryFeedLocation() {
		return this.mRegistryFeedLocation;
	}

	/**
	 * @param mRegistryFeedLocation the mRegistryFeedLocation to set
	 */
	public void setRegistryFeedLocation(final String pRegistryFeedLocation) {
		this.mRegistryFeedLocation = pRegistryFeedLocation;
	}

	/**
	 * @return the mProfileFeedSuccessResponseLocation
	 */
	public String getProfileFeedSuccessResponseLocation() {
		return this.mProfileFeedSuccessResponseLocation;
	}

	/**
	 * @param pProfileFeedSuccessResponseLocation the pProfileFeedSuccessResponseLocation to set
	 */
	public void setProfileFeedSuccessResponseLocation(final String pProfileFeedSuccessResponseLocation) {
		this.mProfileFeedSuccessResponseLocation = pProfileFeedSuccessResponseLocation;
	}

	/**
	 * @return the mRegistryFeedSuccessResponseLocation
	 */
	public String getRegistryFeedSuccessResponseLocation() {
		return this.mRegistryFeedSuccessResponseLocation;
	}

	/**
	 * @param pRegistryFeedLocation the pRegistryFeedLocation to set
	 */
	public void setRegistryFeedSuccessResponseLocation(final String pRegistryFeedSuccessResponseLocation) {
		this.mRegistryFeedSuccessResponseLocation = pRegistryFeedSuccessResponseLocation;
	}

	/**
	 * @return the mProfileFeedHeader
	 */
	public Map<String, String> getProfileFeedHeader() {
		return this.mProfileFeedHeader;
	}

	/**
	 * @param mProfileFeedHeader the mProfileFeedHeader to set
	 */
	public void setProfileFeedHeader(final Map<String, String> pProfileFeedHeader) {
		this.mProfileFeedHeader = pProfileFeedHeader;
	}

	/**
	 * @return the mRegistryFeedHeader
	 */
	public Map<String, String> getRegistryFeedHeader() {
		return this.mRegistryFeedHeader;
	}

	/**
	 * @param mRegistryFeedHeader the mRegistryFeedHeader to set
	 */
	public void setRegistryFeedHeader(final Map<String, String> pRegistryFeedHeader) {
		this.mRegistryFeedHeader = pRegistryFeedHeader;
	}


	/**
	 * @return the mSiteAssociationMap
	 */
	public Map<String, String> getSiteAssociationMap() {
		return this.mSiteAssociationMap;
	}

	/**
	 * @param pSiteAssociationMap the pSiteAssociationMap to set
	 */
	public void setSiteAssociationMap(final Map<String, String> pSiteAssociationMap) {
		this.mSiteAssociationMap = pSiteAssociationMap;
	}

	@Override
	public Map<String, ProfileVO> readProfileFeed() throws BBBSystemException {

		this.logDebug("FeedFileParserImpl.readProfileFeed : Start");
		final Map<String, ProfileVO> profileFeed = new LinkedHashMap<String, ProfileVO>();
		BufferedReader bufferedReader = null;
		FileInputStream fstream = null;
		try {
			String strLine;
			fstream = new FileInputStream(this.getProfileFeedLocation());
			final DataInputStream inputStream = new DataInputStream(fstream);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			boolean headerCheckingFlag = true;
			while ((strLine = bufferedReader.readLine()) != null)   {

				if(!headerCheckingFlag){
					if(!"".equals(strLine.trim())){

						final ProfileVO profileVO = new ProfileVO();
						if(strLine.lastIndexOf("|") == (strLine.length() - 1)){
							strLine = strLine + " ";
						}
						final String[] profileInfoArray = strLine.split("\\|");
						profileVO.setId(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("id"))].trim());
						profileVO.setMemberId(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("member_id"))].trim());
						profileVO.setEmail(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("email_address"))].trim().toLowerCase());
						profileVO.setSiteId(this.getSiteAssociation(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("site_id"))].trim()));
						profileVO.setFirstName(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("first_name"))].trim());
						profileVO.setLastName(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("last_name"))].trim());
						profileVO.setPhoneNumber(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("phone_number"))].trim());
						profileVO.setMobileNumber(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("mobile_number"))].trim());
						profileVO.setOptInFlag(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("email_optin"))].trim());
						profileVO.setLastModifiedDateAsString(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("last_acc_date"))].trim());
						profileVO.setFavStoreId(profileInfoArray[Integer.parseInt(this.getProfileFeedHeader().get("fav_store_id"))].trim());
						profileFeed.put(profileVO.getId(), profileVO);

					}
				}
				headerCheckingFlag = false;
			}

		} catch (final FileNotFoundException e) {
			this.logError("File not found for profile feed", e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1282,"Error occured while parsing profile feed", e);
		}catch (final IOException e) {
			this.logError("Error occured while parsing profile feed", e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while parsing profile feed", e);
		}finally{
			if(bufferedReader != null){
				closeProfileFeedFileLocation(bufferedReader);
			}
			if(fstream!=null){
				try {
					fstream.close();
				} catch (IOException e) {
					this.logError("Error occured while closing fileInputStream  " , e);
				}
			}
		}

		this.logDebug("FeedFileParserImpl.readProfileFeed : End");
		return profileFeed;
	}

	/**
	 * @param bufferedReader
	 * @throws BBBSystemException
	 */
	protected void closeProfileFeedFileLocation(BufferedReader bufferedReader)
			throws BBBSystemException {
		try {
			bufferedReader.close();
		} catch (final IOException e) {
			this.logError("Error occured while closing profile feed file at location : " + this.getProfileFeedLocation(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while closing profile feed file at location : " + this.getProfileFeedLocation(), e);
		}
	}

	@Override
	public Map<String, RegistryVO> readRegistryFeed() throws BBBSystemException {
		this.logDebug("FeedFileParserImpl.readRegistryFeed : Start");
		final Map<String, RegistryVO> registryFeed = new LinkedHashMap<String, RegistryVO>();
		BufferedReader bufferedReader = null;
		FileInputStream fstream = null;
		try {
			String strLine;
			fstream = new FileInputStream(this.getRegistryFeedLocation());
			final DataInputStream inputStream = new DataInputStream(fstream);
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
			boolean headerCheckingFlag = true;
			while ((strLine = bufferedReader.readLine()) != null)   {

				if(!headerCheckingFlag){
					if(!"".equals(strLine.trim())){

						final RegistryVO registryVO = new RegistryVO();
						if(strLine.lastIndexOf("|") == (strLine.length() - 1)){
							strLine = strLine + " ";
						}
						final String[] registryInfoArray = strLine.split("\\|");

						registryVO.setId(registryInfoArray[Integer.parseInt(this.getRegistryFeedHeader().get("id"))].trim());
						registryVO.setRegistryId(registryInfoArray[Integer.parseInt(this.getRegistryFeedHeader().get("registry_id"))].trim());
						registryVO.setEmail(registryInfoArray[Integer.parseInt(this.getRegistryFeedHeader().get("email_address"))].trim().toLowerCase());
						registryVO.setSiteId(this.getSiteAssociation(registryInfoArray[Integer.parseInt(this.getRegistryFeedHeader().get("site_id"))].trim()));
						registryVO.setRegistryType(registryInfoArray[Integer.parseInt(this.getRegistryFeedHeader().get("registry_type"))].trim());
						registryVO.setEventDate(registryInfoArray[Integer.parseInt(this.getRegistryFeedHeader().get("event_date"))].trim());
						registryVO.setRegistrantAsString(registryInfoArray[Integer.parseInt(this.getRegistryFeedHeader().get("is_registrant"))].trim());

						registryFeed.put(registryVO.getId(), registryVO);

					}
				}
				headerCheckingFlag = false;
			}

		} catch (final FileNotFoundException e) {
			this.logError("File not found for registry feed", e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1283,"Error occured while parsing registry feed", e);
		}catch (final IOException e) {
			this.logError("Error occured while parsing registry feed", e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while parsing registry feed", e);
		}finally{
			if(bufferedReader != null){
				closeRegistryFileFeedLocation(bufferedReader);
			}
			if(fstream!=null){
				try {
					fstream.close();
				} catch (IOException e) {
					this.logError("Error occured while closing fileInputStream  " , e);
				}
			}
		}

		this.logDebug("FeedFileParserImpl.readRegistryFeed : End");
		return registryFeed;
	}

	/**
	 * @param bufferedReader
	 * @throws BBBSystemException
	 */
	protected void closeRegistryFileFeedLocation(BufferedReader bufferedReader)
			throws BBBSystemException {
		try {
			bufferedReader.close();
		} catch (final IOException e) {
			this.logError("Error occured while closing registry feed file at location : " + this.getRegistryFeedLocation(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while closing registry feed file at location : " + this.getRegistryFeedLocation(), e);
		}
	}

	@Override
	public boolean writeProfileFeedResponse(final Map<String, ProfileVO> profileResponseVOs) throws BBBSystemException {

		this.logDebug("FeedFileParserImpl.writeProfileFeedResponse : Start");
		BufferedWriter bufferedWriter = null;
		BufferedWriter bufferedWriterForFailure = null;
		FileWriter fileWriter = null;
		FileWriter fileWriterForFailure = null;
		if((profileResponseVOs != null) && !profileResponseVOs.isEmpty()){
			try{
				fileWriter = new FileWriter(new File(this.getProfileFeedSuccessResponseLocation()), true);
				bufferedWriter = new BufferedWriter(fileWriter);
				fileWriterForFailure = new FileWriter(new File(this.getProfileFeedFailureResponseLocation()), true);
				bufferedWriterForFailure = new BufferedWriter(fileWriterForFailure);
				bufferedWriter.write(PROFILE_FEED_RESPONSE_HEADER);
				bufferedWriterForFailure.write(PROFILE_FEED_RESPONSE_HEADER);

				bufferedWriter.newLine();
				bufferedWriterForFailure.newLine();

				for(Entry<String, ProfileVO> entry : profileResponseVOs.entrySet()){
					final ProfileVO profile = entry.getValue();
					if(!profile.isError()){
						bufferedWriter.write(profile.toString());
						bufferedWriter.newLine();
					}else{
						bufferedWriterForFailure.write(profile.toString());
						bufferedWriterForFailure.newLine();
					}

				}

			}catch(final IOException e){
				this.logError("Error occured while writing profile feed response", e);
				throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while writing profile feed response", e);
			}finally{
				if(bufferedWriter != null){
					closeProfileFeedSuccessResponseLocation(bufferedWriter);
				}
				if(bufferedWriterForFailure != null){
					closeProfileFeedFailureResponseLocation(bufferedWriterForFailure);
				}
				if(fileWriterForFailure != null){
					try {
						fileWriterForFailure.close();
					} catch (final IOException e) {
						this.logError("Error occured while closing file writer for failure", e);
					}
				}

				if(fileWriter != null){
					try {
						fileWriter.close();
					} catch (final IOException e) {
						this.logError("Error occured while closing file writer", e);
					}
				}
				
			}

			final File registryFeedFile = new File(this.getProfileFeedLocation());
			if(registryFeedFile.renameTo(new File(this.getProfileFeedLocation()+"_success"))){
				this.logDebug("File successfully renamed");
			}else{
				this.logDebug("Error renaming the file : " + this.getProfileFeedLocation());
			}

		}
		this.logDebug("FeedFileParserImpl.writeProfileFeedResponse : End");
		return true;
	}

	/**
	 * @param bufferedWriterForFailure
	 * @throws BBBSystemException
	 */
	protected void closeProfileFeedFailureResponseLocation(
			BufferedWriter bufferedWriterForFailure) throws BBBSystemException {
		try {
			bufferedWriterForFailure.close();
		} catch (final IOException e) {
			this.logError("Error occured while closing profile feed file at location : " + this.getProfileFeedFailureResponseLocation(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while closing profile feed file at location : " + this.getProfileFeedFailureResponseLocation(), e);
		}
	}

	/**
	 * @param bufferedWriter
	 * @throws BBBSystemException
	 */
	protected void closeProfileFeedSuccessResponseLocation(
			BufferedWriter bufferedWriter) throws BBBSystemException {
		try {
			bufferedWriter.close();
		} catch (final IOException e) {
			this.logError("Error occured while closing profile feed reponse file at location : " + this.getProfileFeedSuccessResponseLocation(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while closing profile feed file at location : " + this.getProfileFeedSuccessResponseLocation(), e);
		}
	}

	@Override
	public boolean writeRegistryFeedResponse(final Map<String, RegistryVO> registryResponseVOs, final boolean isRegistrantResponse) throws BBBSystemException {
		this.logDebug("FeedFileParserImpl.writeRegistryFeedResponse : Start");
		BufferedWriter bufferedWriter = null;
		BufferedWriter bufferedWriterForFailure = null;
		FileWriter registryFeedFileWriter = null;
		FileWriter registryFeedFailureFileWriter = null;
		FileWriter coregistrantFeedFileWriter = null;
		FileWriter coregistrantFeedFailureFileWriter = null;
		if((registryResponseVOs != null) && !registryResponseVOs.isEmpty()){
			try{
				if(isRegistrantResponse){
					registryFeedFileWriter = new FileWriter(new File(this.getRegistryFeedSuccessResponseLocation()), true);
					bufferedWriter = new BufferedWriter(registryFeedFileWriter);
					registryFeedFailureFileWriter = new FileWriter(new File(this.getRegistryFeedFailureResponseLocation()), true);
					bufferedWriterForFailure = new BufferedWriter(registryFeedFailureFileWriter);
				}else{
					coregistrantFeedFileWriter = new FileWriter(new File(this.getCoregistrantFeedSuccessResponseLocation()), true);
					bufferedWriter = new BufferedWriter(coregistrantFeedFileWriter);
					coregistrantFeedFailureFileWriter = new FileWriter(new File(this.getCoregistrantFeedFailureResponseLocation()), true);
					bufferedWriterForFailure = new BufferedWriter(coregistrantFeedFailureFileWriter);
				}

				bufferedWriter.write(REGISTRY_FEED_RESPONSE_HEADER);
				bufferedWriterForFailure.write(REGISTRY_FEED_RESPONSE_HEADER);
				bufferedWriter.newLine();
				bufferedWriterForFailure.newLine();

				for(Entry<String, RegistryVO> entry : registryResponseVOs.entrySet()){
					RegistryVO registry = entry.getValue();
					if(!registry.isError()){
						bufferedWriter.write(registry.toString());
						bufferedWriter.newLine();
					}else{
						bufferedWriterForFailure.write(registry.toString());
						bufferedWriterForFailure.newLine();
					}

				}

			}catch(final IOException e){
				if(isRegistrantResponse){
					this.logError("Error occured while writing registry feed response", e);
					throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while writing registry feed response", e);
				}else{
					this.logError("Error occured while writing co-registrant response", e);
					throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1285,"Error occured while writing co-registrant response", e);
				}
			}finally{
				if(bufferedWriter != null){
					closeRegistryFeedSuccessResponseLocation(bufferedWriter);
				}
				if(bufferedWriterForFailure != null){
					closeRegistryFeedFailureResponseLocation(bufferedWriterForFailure);
				}
				if(registryFeedFileWriter != null){
					try {
						registryFeedFileWriter.close();
					} catch (final IOException e) {
						this.logError("Error occured while closing registryFeedFileWriter", e);
					}
				}
				if(registryFeedFailureFileWriter != null){
					try {
						registryFeedFailureFileWriter.close();
					} catch (final IOException e) {
						this.logError("Error occured while closing registryFeedFailureFileWriter", e);
					}
				}
				if(coregistrantFeedFileWriter != null){
					try {
						coregistrantFeedFileWriter.close();
					} catch (final IOException e) {
						this.logError("Error occured while closing coregistrantFeedFileWriter", e);
					}
				}
				if(coregistrantFeedFailureFileWriter != null){
					try {
						coregistrantFeedFailureFileWriter.close();
					} catch (final IOException e) {
						this.logError("Error occured while closing coregistrantFeedFailureFileWriter", e);
					}
				}
			}

			if(!isRegistrantResponse){
				final File registryFeedFile = new File(this.getRegistryFeedLocation());
				if(registryFeedFile.renameTo(new File(this.getRegistryFeedLocation()+"_success"))){
					this.logDebug("File successfully renamed");
				}else{
					this.logDebug("Error renaming the file : " + this.getRegistryFeedLocation());
				}
			}

		}
		this.logDebug("FeedFileParserImpl.writeRegistryFeedResponse : End");
		return true;
	}

	/**
	 * @param bufferedWriterForFailure
	 * @throws BBBSystemException
	 */
	protected void closeRegistryFeedFailureResponseLocation(
			BufferedWriter bufferedWriterForFailure) throws BBBSystemException {
		try {
			bufferedWriterForFailure.close();
		} catch (final IOException e) {
			this.logError("Error occured while closing registry response feed file at location : " + this.getRegistryFeedFailureResponseLocation(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while closing registry response feed file at location : " + this.getRegistryFeedFailureResponseLocation(), e);
		}
	}

	/**
	 * @param bufferedWriter
	 * @throws BBBSystemException
	 */
	protected void closeRegistryFeedSuccessResponseLocation(
			BufferedWriter bufferedWriter) throws BBBSystemException {
		try {
			bufferedWriter.close();
		} catch (final IOException e) {
			this.logError("Error occured while closing registry response feed file at location : " + this.getRegistryFeedSuccessResponseLocation(), e);
			throw new BBBSystemException(BBBCoreErrorConstants.ACCOUNT_ERROR_1284,"Error occured while closing registry response feed file at location : " + this.getRegistryFeedSuccessResponseLocation(), e);
		}
	}

	private String getSiteAssociation(final String key) throws BBBSystemException {

		String siteId = "";
		if(this.getSiteAssociationMap().containsKey(key)){
			siteId = this.getSiteAssociationMap().get(key);
			try {
				if(siteId.equalsIgnoreCase("BedBathAndBeyond")){
					siteId = this.getCatalogTools().getAllValuesForKey(CONTENT_CATALOG_CONFIG_KEY , BED_BATH_AND_BEYOND_US_SITE_ID).get(0);
				}else if(siteId.equalsIgnoreCase("BuyBuyBaby")){
					siteId = this.getCatalogTools().getAllValuesForKey(CONTENT_CATALOG_CONFIG_KEY , BUY_BUY_BABY_SITE_ID).get(0);
				}else if(siteId.equalsIgnoreCase("BedBathAndBeyondCanada")){
					siteId = this.getCatalogTools().getAllValuesForKey(CONTENT_CATALOG_CONFIG_KEY , BED_BATH_AND_BEYOND_CA_SITE_ID).get(0);
				}
			} catch (final BBBSystemException e) {
				this.logError("Error occured while fetching site id from content catalog", e);
				throw e;
			} catch (final BBBBusinessException e) {
				this.logError("Error occured while fetching site id from content catalog", e);
			}
		}

		return siteId;
	}
}
