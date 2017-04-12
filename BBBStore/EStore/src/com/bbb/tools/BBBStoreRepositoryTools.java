package com.bbb.tools;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.EcoFeeSKUVO;
import com.bbb.commerce.catalog.vo.StoreBopusInfoVO;
import com.bbb.commerce.catalog.vo.StoreSpecialityVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.selfservice.common.StoreDetails;
import com.bbb.selfservice.manager.ScheduleAppointmentManager;
import com.bbb.utils.BBBUtility;

/**
 * @author Kumar Magudeeswaran
 * This class is created to refactor the code from BBBCatalogToolsImpl
 * Methods accessing Store repository are placed in this class from BBBCatalogToolsImpl
 *  
 */
public class BBBStoreRepositoryTools extends BBBConfigToolsImpl {
	
	/**	Instance of store Repository. */
	private MutableRepository storeRepository;
	/**	Instance of ScheduleAppointmentManager. */
	private ScheduleAppointmentManager mScheduleAppointmentManager;
	/**	String of canadaStoresQuery. */
	private String canadaStoresQuery;
	/**	String of storesQuery. */
	private String storesQuery;
	/**	String of bopusInEligibleStoreQuery. */
	private String bopusInEligibleStoreQuery;
	/**	String of bopusDisabledStoreQuery. */
	private String bopusDisabledStoreQuery;
	/**	Instance of GlobalRepositoryTools. */
	private GlobalRepositoryTools globalRepositoryTools;
	
    
    public final List<StoreVO> getCanadaStoreLocatorInfo() throws BBBSystemException, BBBBusinessException {
        this.logDebug("Catalog API Method Name [getCanadaStoreLocatorInfo]");
        final List<StoreVO> canadaStoreList = new ArrayList<StoreVO>();
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getCanadaStoreLocatorInfo");
            final RepositoryItem[] storeItem = this.executeRQLQuery(this.getCanadaStoresQuery(),
                            BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, this.getStoreRepository());
            if ((storeItem != null) && (storeItem.length > 0)) {
                for (final RepositoryItem element : storeItem) {
                    try {
                        canadaStoreList.add(this.getStoreDetails(element.getRepositoryId()));                       
                    } catch (final BBBBusinessException e) {
                        final StringBuilder logDebug = new StringBuilder();
                        logDebug.append("Ignore store ID : ").append(element.getRepositoryId())
                                        .append(" for canada store. Message : ").append(e.getMessage());
                        if (this.isLoggingError()) {
                            this.logError("catalog_1015: " + logDebug.toString(),e);
                        }
                    }
                }
            } else {
                throw new BBBBusinessException(BBBCatalogErrorCodes.NO_STORES_AVAILABLE_FOR_CANADA_IN_REPOSITORY,
                                BBBCatalogErrorCodes.NO_STORES_AVAILABLE_FOR_CANADA_IN_REPOSITORY);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getCanadaStoreLocatorInfo]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getCanadaStoreLocatorInfo");
        }
        
        String appointmentType= getAllValuesForKey(BBBCoreConstants.SKEDGE_ME , BBBCoreConstants.APPOINT_TYPE_STORE_LOCATOR).get(0);
    	int preSelectedServiceRef=0;
        
        this.logDebug("appointmentType "+appointmentType+" canadaStoreList "+ canadaStoreList);
        
        if(appointmentType!=null && !canadaStoreList.isEmpty()) {
			Map<String, Boolean> appointmentMap = null;				
			try {
				appointmentMap = getScheduleAppointmentManager().checkAppointmentAvailabilityCanada(canadaStoreList,
						appointmentType);
				preSelectedServiceRef=getScheduleAppointmentManager().fetchPreSelectedServiceRef(appointmentType);
				if(appointmentMap!=null && !appointmentMap.isEmpty()){
					final boolean appointmentEligible = getScheduleAppointmentManager().canScheduleAppointmentForSiteId(SiteContextManager.getCurrentSiteId());
					for (final StoreVO storeDetails : canadaStoreList) {
						getScheduleAppointmentManager().checkAppointmentEligibleCanada( storeDetails,  appointmentMap, appointmentEligible);
						storeDetails.setPreSelectedServiceRef(preSelectedServiceRef);
					}
				}
			} catch (BBBSystemException bbbException) {
				logError("Error in Appointment Type for canada site"+bbbException);
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                        BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, bbbException);
			}

		}
        
      //BSL-3109
      	String channel = null;
		DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		if (pRequest != null
				&& pRequest.getHeader(BBBCoreConstants.CHANNEL) != null) {
			channel = pRequest.getHeader(BBBCoreConstants.CHANNEL);
		}
		if (BBBUtility.isNotEmpty(channel) && channel.equals(BBBCoreConstants.MOBILEWEB)) {
			Collections.sort(canadaStoreList, new Comparator<StoreVO>() {
				@Override
				public int compare(StoreVO s1, StoreVO s2) {
					return s1.getStoreName().compareTo(s2.getStoreName());
				}
			});
			return canadaStoreList;
		}
        return canadaStoreList;

    }
    
    public StoreDetails setSpecialityVO(final StoreDetails storeDetailsObj) throws RepositoryException {
        if (storeDetailsObj.getSunStoreTimings() != null) {
            storeDetailsObj.setSunStoreTimings(storeDetailsObj.getSunStoreTimings().replace(",", "").trim());
        }
        if (storeDetailsObj.getSatStoreTimings() != null) {
            storeDetailsObj.setSatStoreTimings(storeDetailsObj.getSatStoreTimings().replace(",", "").trim());
        }

        if (storeDetailsObj.getWeekdaysStoreTimings() != null) {
            storeDetailsObj.setWeekdaysStoreTimings(storeDetailsObj.getWeekdaysStoreTimings().replace(",", "").trim());
        }

        final RepositoryView view = this.getStoreRepository().getView("specialityCodeMap");

        final RqlStatement statement = getRqlSetSpecialityVO();

        final Object params[] = new Object[1];
        params[0] = storeDetailsObj.getSpecialtyShopsCd();
        final RepositoryItem[] items = statement.executeQuery(view, params);
        if ((items != null) && (items[0] != null)) {
            @SuppressWarnings ("unchecked")
            final List<RepositoryItem> specialityItemList = (List<RepositoryItem>) items[0]
                            .getPropertyValue("specialityCd");
            final Set<RepositoryItem> specialityItemSet = new HashSet<RepositoryItem>();
            if ((specialityItemList != null) && (!specialityItemList.isEmpty())) {
                for (final RepositoryItem item : specialityItemList) {
                    specialityItemSet.add(item);
                }

                final List<StoreSpecialityVO> specialityVOlist = getGlobalRepositoryTools().getStoreSpecialityList(specialityItemSet);
                storeDetailsObj.setStoreSpecialityVO(specialityVOlist);
            }
        }
        return storeDetailsObj;
    }

	protected RqlStatement getRqlSetSpecialityVO() throws RepositoryException {
		return RqlStatement.parseRqlStatement("specialityShopCd=?0");
	}
    
    
    /**
     *  This method returns the store details for a given store id.
     *
     * @param storeId the store id
     * @return Store Details
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final StoreVO getStoreDetails(final String storeId) throws BBBBusinessException, BBBSystemException {

            this.logDebug("Catalog API Method Name [getStoreDetails]storeId[" + storeId + "]");
        
        if ((storeId != null) && !StringUtils.isEmpty(storeId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getStoreDetails");
                final RepositoryItem storeRepositoryItem = this.getStoreRepository().getItem(storeId,
                                BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
                if (storeRepositoryItem != null) {
                    final StoreVO storeVO = new StoreVO();
                    storeVO.setStoreId(storeRepositoryItem.getRepositoryId());
                    storeVO.setStoreName(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.STORE_NAME_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.STORE_NAME_STORE_PROPERTY_NAME) : "");
                    storeVO.setAddress(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ADDRESS_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ADDRESS_STORE_PROPERTY_NAME) : "");
                    storeVO.setCity(storeRepositoryItem.getPropertyValue(BBBCatalogConstants.CITY_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CITY_STORE_PROPERTY_NAME) : "");
                    storeVO.setState(storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME) : "");
                    storeVO.setPostalCode(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.POSTAL_CODE_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.POSTAL_CODE_STORE_PROPERTY_NAME) : "");
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME) != null) {
                        final String provinceCode = (String) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME);
                        storeVO.setProvince(provinceCode);
                        storeVO.setProvinceName(this.getStateName(provinceCode));
                    }
                    storeVO.setCountryCode((String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.COUNTRYCODE_STORE_PROPERTY_NAME));
                    storeVO.setPhone((String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.PHONE_STORE_PROPERTY_NAME));
                    storeVO.setLongitude(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LONGITUDE_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LONGITUDE_STORE_PROPERTY_NAME) : "");
                    storeVO.setLatitude(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LATITUDE_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LATITUDE_STORE_PROPERTY_NAME) : "");
                    storeVO.setStoreType(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.STORE_TYPE_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.STORE_TYPE_STORE_PROPERTY_NAME) : "");
                    storeVO.setHours(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.HOURS_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.HOURS_STORE_PROPERTY_NAME) : "");
                    storeVO.setLatLongSrc(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LAT_LONG_SRC_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.LAT_LONG_SRC_STORE_PROPERTY_NAME) : "");
                    storeVO.setRowXngDt(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ROW_XNGDT_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ROW_XNGDT_STORE_PROPERTY_NAME) : "");
                    storeVO.setRowXngUser(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ROW_XNGUSER_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ROW_XNGUSER_STORE_PROPERTY_NAME) : "");
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.HIRINGIND_STORE_PROPERTY_NAME) != null) {
                        storeVO.setHiringInd(((Boolean) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.HIRINGIND_STORE_PROPERTY_NAME))
                                        .booleanValue());
                    }
                    storeVO.setFacadeStoreType(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.FACADE_STORE_TYPE_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.FACADE_STORE_TYPE_STORE_PROPERTY_NAME) : "");
                    storeVO.setCommonNamePhonetic(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.COMMON_NAME_PHONETIC_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.COMMON_NAME_PHONETIC_STORE_PROPERTY_NAME)
                                    : "");
                    storeVO.setAddressPhonetic(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ADDRESS_PHONETIC_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.ADDRESS_PHONETIC_STORE_PROPERTY_NAME) : "");
                    storeVO.setCityPhonetic(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CITY_PHONETIC_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.CITY_PHONETIC_STORE_PROPERTY_NAME) : "");
                    storeVO.setMqTransCode(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.MQ_TRANS_CODE_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.MQ_TRANS_CODE_STORE_PROPERTY_NAME) : "");
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.DISPLAY_ONLINE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setDisplayOnline(((Boolean) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.DISPLAY_ONLINE_STORE_PROPERTY_NAME))
                                        .booleanValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.MON_OPEN_STORE_PROPERTY_NAME) != null) {
                        storeVO.setMonOpen(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.MON_OPEN_STORE_PROPERTY_NAME)).intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.MON_CLOSE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setMonClose(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.MON_CLOSE_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.TUES_OPEN_STORE_PROPERTY_NAME) != null) {
                        storeVO.setTuesOpen(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.TUES_OPEN_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.TUES_CLOSE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setTuesClose(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.TUES_CLOSE_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.WED_OPEN_STORE_PROPERTY_NAME) != null) {
                        storeVO.setWedOpen(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.WED_OPEN_STORE_PROPERTY_NAME)).intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.WED_CLOSE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setWedClose(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.WED_CLOSE_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.THUR_OPEN_STORE_PROPERTY_NAME) != null) {
                        storeVO.setThursOpen(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.THUR_OPEN_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.THUR_CLOSE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setThursClose(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.THUR_CLOSE_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.FRI_OPEN_STORE_PROPERTY_NAME) != null) {
                        storeVO.setFriOpen(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.FRI_OPEN_STORE_PROPERTY_NAME)).intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.FRI_CLOSE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setFriClose(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.FRI_CLOSE_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.SAT_OPEN_STORE_PROPERTY_NAME) != null) {
                        storeVO.setSatOpen(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SAT_OPEN_STORE_PROPERTY_NAME)).intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.SAT_CLOSE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setSatClose(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SAT_CLOSE_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.SUN_OPEN_STORE_PROPERTY_NAME) != null) {
                        storeVO.setSunOpen(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SUN_OPEN_STORE_PROPERTY_NAME)).intValue());
                    }
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.SUN_CLOSE_STORE_PROPERTY_NAME) != null) {
                        storeVO.setSunClose(((Integer) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.SUN_CLOSE_STORE_PROPERTY_NAME))
                                        .intValue());
                    }
                    storeVO.setSpecialMsg(storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.SPECIAL_MSG_STORE_PROPERTY_NAME) != null ? (String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.SPECIAL_MSG_STORE_PROPERTY_NAME) : "");
                    if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.CONTACT_FLAG_STORE_PROPERTY_NAME) != null) {
                        storeVO.setContactFlag(((Boolean) storeRepositoryItem
                                        .getPropertyValue(BBBCatalogConstants.CONTACT_FLAG_STORE_PROPERTY_NAME))
                                        .booleanValue());
                    }

                    @SuppressWarnings ("unchecked")
                    final Set<RepositoryItem> specialityItemSet = (Set<RepositoryItem>) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.SPECIALITY_CODE_ID_STORE_PROPERTY_NAME);

                    if ((specialityItemSet != null) && !specialityItemSet.isEmpty()) {
                        storeVO.setStoreSpecialityVO(getGlobalRepositoryTools().getStoreSpecialityList(specialityItemSet));
                    }
                    @SuppressWarnings ("unchecked")
                    final Map<String, Boolean> bopusSiteMap = (Map<String, Boolean>) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.BOPUS_STORE_PROPERTY_NAME);
                    if ((bopusSiteMap != null) && !bopusSiteMap.isEmpty()) {
                        final List<StoreBopusInfoVO> storeBopusInfoVOList = new ArrayList<StoreBopusInfoVO>();
                        final Set<String> siteIdSet = bopusSiteMap.keySet();
                        for (final String siteId : siteIdSet) {
                            final StoreBopusInfoVO storeBopusInfoVO = new StoreBopusInfoVO();
                            storeBopusInfoVO.setSiteId(siteId);
                            storeBopusInfoVO.setBopusFlag(bopusSiteMap.get(siteId));
                            storeBopusInfoVOList.add(storeBopusInfoVO);
                        }
                        storeVO.setStoreBopusInfoVO(storeBopusInfoVOList);
                    }
					if(storeVO.getStoreId()!=null && storeId.startsWith(BBBCatalogConstants.STORE_ID_FOR_BABYCANADA)) {
						storeVO.setBabyCanadaFlag(true);
					}

                    return storeVO;
                }
                throw new BBBBusinessException(BBBCatalogErrorCodes.STORE_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.STORE_NOT_AVAILABLE_IN_REPOSITORY);
            } catch (final RepositoryException e) {
                this.logError("Catalog API Method Name [getStoreDetails]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStoreDetails");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }
   

    public StoreVO getStoreAppointmentDetails(final String storeId) {

        this.logDebug("Catalog API Method Name [getStoreAppointmentDetails]storeId[" + storeId + "]");

    StoreVO storeVO = null;
    if ((storeId != null) 
    		&& !StringUtils.isEmpty(storeId)) {
        try {
            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getStoreAppointmentDetails");
            final RepositoryItem storeRepositoryItem = this.getStoreRepository().getItem(storeId,
                            BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
            if (storeRepositoryItem != null) {
                storeVO = new StoreVO();
                storeVO.setStoreId(storeRepositoryItem.getRepositoryId());
                if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_ACCEPTING_APPOITMENTS_PROPERTY_NAME) != null) {
                	storeVO.setAcceptingAppointments((Boolean)storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_ACCEPTING_APPOITMENTS_PROPERTY_NAME));
            	
                }
                List<String> appointmentTypes = null;
                if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENT_TYPES_PROPERTY_NAME) != null) {
                	StringTokenizer str = new StringTokenizer((String)storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENT_TYPES_PROPERTY_NAME), ",");
                	while(str.hasMoreTokens()) {
                		if(appointmentTypes == null) {
                			appointmentTypes = new ArrayList<String>();
                		}
                		appointmentTypes.add((String)str.nextElement());
                	}
                	storeVO.setAppointmentTypes(appointmentTypes);
                }
                
                if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_REGISTRY_APPOITMENT_TYPES_PROPERTY_NAME) != null) {
                	StringTokenizer str = new StringTokenizer((String)storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_REGISTRY_APPOITMENT_TYPES_PROPERTY_NAME), ",");
                	while(str.hasMoreTokens()) {
                		if(appointmentTypes == null) {
                			appointmentTypes = new ArrayList<String>();
                		}
                		appointmentTypes.add((String)str.nextElement());
                	}
                	storeVO.setRegAppointmentTypes(appointmentTypes);
                }
                
                if (storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENTS_LAST_MODIFIED_DATE_PROPERTY_NAME) != null) {
                	storeVO.setLastModDate((java.sql.Timestamp)storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STORE_APPOITMENTS_LAST_MODIFIED_DATE_PROPERTY_NAME));
                }
                
                return storeVO;
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [getStoreAppointmentDetails]: RepositoryException ",e);
        } finally {
            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getStoreAppointmentDetails");
        }
    }
    return storeVO;
}   
   
   
    /**
     *  The method checks if the store is eligible for EcoFee.
     *
     * @param pStoreId the store id
     * @return Eco Free Eligibility
     * @throws BBBBusinessException the BBB business exception
     * @throws BBBSystemException the BBB system exception
     */
    public final boolean isEcoFeeEligibleForStore(final String pStoreId)
                    throws BBBBusinessException, BBBSystemException {
        final String methodName = "isEcoFeeEligibleForStore(String pStoreId)";
        try {
            if ((null != pStoreId) && !StringUtils.isEmpty(pStoreId)) {
                this.logDebug("Entering  " + methodName + " with StoreId: " + pStoreId);
                final RepositoryItem storeRepositoryItem = this.getStoreRepository().getItem(pStoreId,
                                BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
                this.logDebug("Item not found in the Repository");
                if (storeRepositoryItem == null) {
                    return false;
                }
                if ((null != (String) storeRepositoryItem.getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME))
                                && !StringUtils.isEmpty((String) storeRepositoryItem
                                                .getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME))) {

                    return this.isEcoFeeEligibleForState((String) storeRepositoryItem
                                    .getPropertyValue(BBBCatalogConstants.STATE_PROPERTY_NAME));
                }
                this.logDebug("State Id is null from repository");
            } else {
                this.logDebug("input parameter StoreId is null");
                throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                                BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
            }
        } catch (final RepositoryException e) {
            this.logError("Catalog API Method Name [isEcoFeeEligibleForStore]: RepositoryException ");
            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
        }
        this.logDebug("Exiting  " + methodName);
        return false;
    }
  
  
  /** The method gets the details for eco fee sku for store.
  *
  * @param pStoreId
  * @param pSkuId
  * @return EcoFeeSKUVO
  * @throws BBBBusinessException
  * @throws BBBSystemException */
 public final EcoFeeSKUVO getEcoFeeSKUDetailForStore(final String pStoreId, final String pSkuId)
                 throws BBBBusinessException, BBBSystemException {
     final String methodName = "getEcoFeeSKUDetailForStore(String pStoreId, String pSkuId)";
     try {
         if ((null != pStoreId) && !StringUtils.isEmpty(pStoreId) && (null != pSkuId)
                         && !StringUtils.isEmpty(pSkuId)) {
             this.logDebug("Entering  " + methodName + " with StoreId: " + pStoreId + "and skuId: " + pSkuId);
             final RepositoryItem storeRepositoryItem = this.getStoreRepository().getItem(pStoreId,
                             BBBCatalogConstants.STORE_ITEM_DESCRIPTOR);
             if ((storeRepositoryItem != null)
                             && (null != (String) storeRepositoryItem
                                             .getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME))
                             && !StringUtils.isEmpty((String) storeRepositoryItem
                                             .getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME))) {

                 return getGlobalRepositoryTools().getEcoFeeSKUDetailForState((String) storeRepositoryItem
                                 .getPropertyValue(BBBCatalogConstants.PROVINCE_STORE_PROPERTY_NAME), pSkuId);
             }
             this.logDebug("State Id is null from repository");
             throw new BBBBusinessException(BBBCatalogErrorCodes.ECO_FEE_SKU_FOUND,
                             BBBCatalogErrorCodes.ECO_FEE_SKU_FOUND);
         }
         this.logDebug("input parameters StoreId and SkuId are null");
         throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                         BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
     } catch (final RepositoryException e) {
         this.logError("Catalog API Method Name [getEcoFeeSKUDetailForStore]: RepositoryException ");
         throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                         BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
     }
 }
 
 
 public final List<StoreVO> getUSAStoreDetails() throws BBBBusinessException, BBBSystemException {

     this.logDebug("Catalog API Method Name [getAllStoreDetailsAPI]");
     final List<StoreVO> storeList = new ArrayList<StoreVO>();
     try {
         BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getAllStoreDetailsAPI");
         final RepositoryItem[] storeItem = this.executeRQLQuery(this.getStoresQuery(),
                         BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, this.getStoreRepository());
         if ((storeItem != null) && (storeItem.length > 0)) {
             for (final RepositoryItem element : storeItem) {
                 try {
                     storeList.add(this.getStoreDetails(element.getRepositoryId()));
                 } catch (final BBBBusinessException e) {
                     final StringBuilder logDebug = new StringBuilder();
                     logDebug.append("Ignore store ID : ").append(element.getRepositoryId())
                                     .append(" for USA store. Message : ").append(e.getMessage());
                         this.logError("catalog_1015: " + logDebug.toString(),e);
                     }
                 }
         } else {
             throw new BBBBusinessException(BBBCatalogErrorCodes.NO_STORES_AVAILABLE_FOR_USA_IN_REPOSITORY,
                             BBBCatalogErrorCodes.NO_STORES_AVAILABLE_FOR_USA_IN_REPOSITORY);
         }
     } catch (final RepositoryException e) {
         this.logError("Catalog API Method Name [getAllStoreDetailsAPI]: RepositoryException ",e);
         throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                         BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

     } finally {
         BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getAllStoreDetailsAPI");
     }
     return storeList;

 }
 
 /**
  *  Bopus exclusion check for Store.
  *
  * @param pStoreId the store id
  * @param pSiteId the site id
  * @return BOPUS in Eligible Stores
  * @throws BBBSystemException the BBB system exception
  * @throws BBBBusinessException the BBB business exception
  */
 public final List<String> getBopusInEligibleStores(final String pStoreId, final String pSiteId)
                 throws BBBSystemException, BBBBusinessException {
     final List<String> bopusInEligibleStores = new ArrayList<String>();
     RepositoryItem[] bopusRepositoryItem = null;
     final Object[] param = new Object[1];
     param[0] = pStoreId;
     bopusRepositoryItem = this.executeRQLQuery(this.getBopusInEligibleStoreQuery(), param,
                     BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, this.getStoreRepository());

     if ((bopusRepositoryItem != null) && (bopusRepositoryItem.length > 0)) {
         for (final RepositoryItem element : bopusRepositoryItem) {
             @SuppressWarnings ("unchecked")
             final Map<String, Boolean> bopusSiteMap = (Map<String, Boolean>) element
                             .getPropertyValue(BBBCatalogConstants.BOPUS_STORE_PROPERTY_NAME);
             if ((bopusSiteMap != null) && (bopusSiteMap.get(pSiteId) != null) && !bopusSiteMap.get(pSiteId).booleanValue()) {
                     bopusInEligibleStores.add(element.getRepositoryId());
             }
         }
     }
     return bopusInEligibleStores;
 }

   
 /**
  *  The method gives the expected min and max date string when a customer can expect teh delivery if time at which
  * order is submit is past the cutoff time for the shipping group then min expected date by which order is expected
  * will be (current date +1 + min days to ship) else it will be (current date+min days to ship) @ -8505,7 +8551,36 @@
  * return bopusInEligibleStores; } /** Get stores which are bopus disabled.
  *
  * @return BOPUS Disabled Stores
  * @throws BBBSystemException the BBB system exception
  * @throws BBBBusinessException the BBB business exception
  */
 public final List<String> getBopusDisabledStores() throws BBBSystemException, BBBBusinessException {
     final List<String> getBopusDisabledStores = new ArrayList<String>();
     RepositoryItem[] bopusRepositoryItem = null;
     try {
         bopusRepositoryItem = this.executeRQLQuery(this.getBopusDisabledStoreQuery(),
                         BBBCatalogConstants.STORE_ITEM_DESCRIPTOR, this.getStoreRepository());

     } catch (final RepositoryException e) {
         this.logError("Catalog API Method Name [BopusDisabledStores]:RepositoryException");
         throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
     }

     if ((bopusRepositoryItem != null) && (bopusRepositoryItem.length > 0)) {
         for (final RepositoryItem element : bopusRepositoryItem) {
             getBopusDisabledStores.add(element.getRepositoryId());
         }

     }
     return getBopusDisabledStores;
 }
 
 
 
 /** @return Bopus Disabled Store Query */
 public final String getBopusDisabledStoreQuery() {
     return this.bopusDisabledStoreQuery;
 }

 /** @param bopusDisabledStoreQuery */
 public final void setBopusDisabledStoreQuery(final String bopusDisabledStoreQuery) {
     this.bopusDisabledStoreQuery = bopusDisabledStoreQuery;
 }
	
	 /** @return the bopusInEligibleStoreQuery */
 public final String getBopusInEligibleStoreQuery() {
     return this.bopusInEligibleStoreQuery;
 }

 /** @param bopusInEligibleStoreQuery the bopusInEligibleStoreQuery to set */
 public final void setBopusInEligibleStoreQuery(final String bopusInEligibleStoreQuery) {
     this.bopusInEligibleStoreQuery = bopusInEligibleStoreQuery;
 }
	
	  /** @return Stores Query */
 public final String getStoresQuery() {
     return this.storesQuery;
 }

 /** @param storesQuery */
 public final void setStoresQuery(final String storesQuery) {
     this.storesQuery = storesQuery;
 }
	
 /**
	 * @return the mScheduleAppointmentManager
	 */
	public ScheduleAppointmentManager getScheduleAppointmentManager() {
		return mScheduleAppointmentManager;
	}

	/**
	 * @param mScheduleAppointmentManager the mScheduleAppointmentManager to set
	 */
	public void setScheduleAppointmentManager(
			ScheduleAppointmentManager mScheduleAppointmentManager) {
		this.mScheduleAppointmentManager = mScheduleAppointmentManager;
	}

	/** @return the canadaStoresQuery */
 public final String getCanadaStoresQuery() {
     return this.canadaStoresQuery;
 }

 /** @param canadaStoresQuery the canadaStoresQuery to set */
 public final void setCanadaStoresQuery(final String canadaStoresQuery) {
     this.canadaStoresQuery = canadaStoresQuery;
 }
	
 /** @return the storeRepository */
 public final MutableRepository getStoreRepository() {
     return this.storeRepository;
 }

 /** @param storeRepository the storeRepository to set */
 public final void setStoreRepository(final MutableRepository storeRepository) {
     this.storeRepository = storeRepository;
 }

/**
 * @return the globalRepositoryTools
 */
public GlobalRepositoryTools getGlobalRepositoryTools() {
	return globalRepositoryTools;
}

/**
 * @param globalRepositoryTools the globalRepositoryTools to set
 */
public void setGlobalRepositoryTools(GlobalRepositoryTools globalRepositoryTools) {
	this.globalRepositoryTools = globalRepositoryTools;
}
 
 

}
