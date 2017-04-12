package com.bbb.feeds.hartehanks.utils;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.feeds.marketing.vo.MarketingFeedVO;
import com.bbb.feeds.utils.BBBFeedTools;
import com.bbb.profile.BBBPropertyManager;
import com.bbb.repositorywrapper.RepositoryItemWrapper;

public class HHUserPreferencesFeedTools extends BBBFeedTools {

	private transient BBBPropertyManager profilePropertyManager;	
	private Map<String, String> hhConceptMap = null;
	private Map<String, String> formatMap = null;
	private Repository  userProfileRepository = null;

	public List<MarketingFeedVO> getUserPreferences(boolean isFullDataFeed, String rqlQuery, Timestamp lastModDate, List<String> feedHeaders) throws BBBBusinessException, BBBSystemException {
		
		logDebug("HHUserPreferencesFeedTools [getUserPreferences] start");
		List<MarketingFeedVO> feedVOList = new ArrayList<MarketingFeedVO>();
		
		RepositoryItem[] profRepItems = getUserPreferencesForFeedGeneration(getUserProfileRepository(), rqlQuery, isFullDataFeed,lastModDate);
		if (profRepItems == null ||
				profRepItems.length == 0) {

			logDebug("No data available in repository for Commision Junction Marketing Feed");
			return null;
		}

		RepositoryItemWrapper pofileWrapper = null;	
		MarketingFeedVO feedVO = null;
		String tempStr = null;
		Timestamp timestamp = null;
		
		/*
		Feed content headers
		srcId,id,email,salutation,firstName,middleName,lastName,suffix(7),
		businessName,address1,address2,city,state,zipCode,country,phoneNumber,mobileNumber,addDate,updateDate(18),
		concept,format,phoneOptIn,smsOptIn,emailOptIn,dmOptIn,phoneOptInDate,smsOptInDate,emailOptInDate,dmOptInDate,newRecord
		*/
		for (RepositoryItem profileItem: profRepItems) {
			pofileWrapper = new RepositoryItemWrapper(profileItem);				
			feedVO = new MarketingFeedVO();
			logDebug("HHUserPreferencesFeedTools generating feed for :"+profileItem.getRepositoryId());

			//srcId
			feedVO.addFeedDataMap(feedHeaders.get(0), "79");
			//id
			feedVO.addFeedDataMap(feedHeaders.get(1), profileItem.getRepositoryId());			
			//email
			feedVO.addFeedDataMap(feedHeaders.get(2), pofileWrapper.getString(BBBCoreConstants.EMAIL));
			//salutation
			feedVO.addFeedDataMap(feedHeaders.get(3), "");			
			//firstName
			feedVO.addFeedDataMap(feedHeaders.get(4), pofileWrapper.getString(BBBCoreConstants.FIRST_NAME));
			//middleName
			feedVO.addFeedDataMap(feedHeaders.get(5), pofileWrapper.getString(BBBCoreConstants.MIDDLE_NAME));			
			//lastName
			feedVO.addFeedDataMap(feedHeaders.get(6), pofileWrapper.getString(BBBCoreConstants.LAST_NAME));			
			//suffix
			feedVO.addFeedDataMap(feedHeaders.get(7), "");
			
			RepositoryItem homeAddress = pofileWrapper.getRepositoryItem(BBBCoreConstants.BILLING_ADDRESS);
			if(homeAddress != null) {
				
				//businessName
				feedVO.addFeedDataMap(feedHeaders.get(8), homeAddress
						.getPropertyValue("companyName") == null ? ""
						: (String) homeAddress.getPropertyValue("companyName"));
				//address1
				feedVO.addFeedDataMap(feedHeaders.get(9), homeAddress
						.getPropertyValue("address1") == null ? ""
						: (String) homeAddress.getPropertyValue("address1"));
				//address2
				feedVO.addFeedDataMap(feedHeaders.get(10), homeAddress
						.getPropertyValue("address2") == null ? ""
						: (String) homeAddress.getPropertyValue("address2"));
				//city
				feedVO.addFeedDataMap(feedHeaders.get(11), homeAddress
						.getPropertyValue("city") == null ? ""
						: (String) homeAddress.getPropertyValue("city"));
				
				//state
				feedVO.addFeedDataMap(feedHeaders.get(12), homeAddress
						.getPropertyValue("state") == null ? ""
						: (String) homeAddress.getPropertyValue("state"));
				
				//zipCode
				feedVO.addFeedDataMap(feedHeaders.get(13), homeAddress
						.getPropertyValue("postalCode") == null ? ""
						: (String) homeAddress.getPropertyValue("postalCode"));
				
				//country
				feedVO.addFeedDataMap(feedHeaders.get(14), homeAddress
						.getPropertyValue("country") == null ? ""
						: (String) homeAddress.getPropertyValue("country"));
				
			}
			//phoneNumber
			tempStr = pofileWrapper.getString(BBBCoreConstants.PHONENUMBER);
			feedVO.addFeedDataMap(feedHeaders.get(15), tempStr == null ? "":tempStr.replaceAll("-", ""));
			
			//mobileNumber
			tempStr = pofileWrapper.getString(BBBCoreConstants.MOBILENUMBER);
			feedVO.addFeedDataMap(feedHeaders.get(16), tempStr == null ? "":tempStr);			
			
			//addDate
			timestamp = pofileWrapper.getTimestamp("registrationDate");
			feedVO.addFeedDataMap(feedHeaders.get(17), timestamp == null ? ""
					: new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(timestamp));

			//newRecord
			if(lastModDate == null) {
				feedVO.addFeedDataMap(feedHeaders.get(29), "Y");				
			}
			else {
				feedVO.addFeedDataMap(feedHeaders.get(29), timestamp.after(lastModDate)?"Y":"N");
			}
			
			//updateDate
			timestamp = pofileWrapper.getTimestamp("lastActivity");
			feedVO.addFeedDataMap(feedHeaders.get(18), timestamp == null ? ""
					: new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(timestamp));
			
			/*
			 * The following attributes are not implemented in web 2.0. 
			 * Sending them as blank.
			 */
			//phoneOptIn
			feedVO.addFeedDataMap(feedHeaders.get(21), "");				
			//smsOptIn
			feedVO.addFeedDataMap(feedHeaders.get(22), "");
			
			//dmOptIn
			feedVO.addFeedDataMap(feedHeaders.get(24), "");
			//phoneOptInDate
			feedVO.addFeedDataMap(feedHeaders.get(25), "");
			//smsOptInDate
			feedVO.addFeedDataMap(feedHeaders.get(26), "");
			//dmOptInDate
			feedVO.addFeedDataMap(feedHeaders.get(28), "");
				
			
			Map map = pofileWrapper.getMap(getProfilePropertyManager().getUserSiteItemsPropertyName());
			if(map == null) {
				continue;
			}
			Set<String> sites = map.keySet();
			Iterator<String> it = sites.iterator();
			while(it.hasNext()) {
				String siteId = it.next();
				MarketingFeedVO feedVOBySite = (MarketingFeedVO)feedVO.deepClone();
				//concept
				feedVOBySite.addFeedDataMap(feedHeaders.get(19),
						getHhConceptMap().get(siteId) == null ? ""
								: (String) getHhConceptMap().get(siteId));
				

				//format
				feedVOBySite.addFeedDataMap(feedHeaders.get(20), getFormatMap().get(siteId));				

				//emailOptIn		
				RepositoryItem siteAssItem = (RepositoryItem)map.get(siteId);
				Object obj = siteAssItem.getPropertyValue(getProfilePropertyManager().getEmailOptInPropertyName());
				if(obj != null) {
					tempStr = (Integer) siteAssItem.getPropertyValue(getProfilePropertyManager().getEmailOptInPropertyName())+"";
					feedVOBySite.addFeedDataMap(feedHeaders.get(23), tempStr.equals("0")?"":"Y");
				}
				else {
					feedVOBySite.addFeedDataMap(feedHeaders.get(23), "");					
				}
			
				//emailOptInDate
				tempStr = siteAssItem.getPropertyValue("timeStamp")==null?"":
								new SimpleDateFormat("yyyyMMdd HH:mm:ss").
									format((Timestamp)siteAssItem.getPropertyValue("timeStamp"));
				feedVOBySite.addFeedDataMap(feedHeaders.get(27), tempStr);
				feedVOList.add(feedVOBySite);					
			}
		}
		logDebug("HHUserPreferencesFeedTools [getUserPreferences] End");
		return feedVOList;		
	}

	public BBBPropertyManager getProfilePropertyManager() {
		return profilePropertyManager;
	}

	public void setProfilePropertyManager(BBBPropertyManager profilePropertyManager) {
		this.profilePropertyManager = profilePropertyManager;
	}

	public Map<String, String> getHhConceptMap() {
		return hhConceptMap;
	}

	public void setHhConceptMap(Map<String, String> hhConceptMap) {
		this.hhConceptMap = hhConceptMap;
	}

	public Map<String, String> getFormatMap() {
		return formatMap;
	}

	public void setFormatMap(Map<String, String> formatMap) {
		this.formatMap = formatMap;
	}

	public Repository getUserProfileRepository() {
		return userProfileRepository;
	}

	public void setUserProfileRepository(Repository userProfileRepository) {
		this.userProfileRepository = userProfileRepository;
	}
	
}
