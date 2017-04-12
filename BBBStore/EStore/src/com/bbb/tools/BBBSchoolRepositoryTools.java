package com.bbb.tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import atg.core.util.StringUtils;
import atg.multisite.SiteContextManager;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import com.bbb.commerce.browse.BBBSearchBrowseConstants;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBConfigToolsImpl;
import com.bbb.commerce.catalog.vo.CollegeVO;
import com.bbb.commerce.catalog.vo.StateVO;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.cache.BBBObjectCache;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.selfservice.vo.BeddingShipAddrVO;
import com.bbb.selfservice.vo.SchoolVO;
import com.bbb.utils.BBBUtility;
/**
 * @author Kumar Magudeeswaran
 * This class is created to refactor the code from BBBCatalogToolsImpl
 * Methods accessing school and schoolVer repository are placed in this class from BBBCatalogToolsImpl
 * 
 */
public class BBBSchoolRepositoryTools extends BBBConfigToolsImpl {
	
	/**	Constants for string mm/dd/yyyy. */
	private static final String MM_DD_YYYY = "MM/dd/yyyy";
	/**	Instance for School Repsoitory. */
	private MutableRepository schoolRepository;
	/**	Instance for School Ver Repsoitory. */
	private MutableRepository schoolVerRepository;
	/**	Instance for BBBObjectCache. */
	private BBBObjectCache mObjectCache;
	/**	Instance for site Repository. */
	private MutableRepository siteRepository;

	
    /**
     *  The method takes a school id and returns school details as schoolVO.
     *
     * @param schoolId the school id
     * @return the school details by id
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */

    public final SchoolVO getSchoolDetailsById(final String schoolId) throws BBBSystemException, BBBBusinessException {

        this.logDebug("Catalog API Method Name [getSchoolDetails]schoolId[" + schoolId + "]");
        SchoolVO schoolVO = null;
        RepositoryItem schoolRepositoryItem = null;
        RepositoryItem[] schoolVerRepositoryItem = null;
        DynamoHttpServletRequest pRequest = ServletUtil.getCurrentRequest();
		String channel = null;
		if(pRequest!=null && pRequest.getHeader(BBBCoreConstants.CHANNEL)!=null){
			channel=pRequest.getHeader(BBBCoreConstants.CHANNEL);
		}

        if ((schoolId != null) && !schoolId.isEmpty()) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");

                schoolRepositoryItem = this.getSchoolRepository().getItem(schoolId,
                                BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR);
                this.logDebug("schoolRepository value:" + schoolRepositoryItem);
                if (schoolRepositoryItem != null) {

                    final String schoolIdRepo = (String) schoolRepositoryItem.getPropertyValue(BBBCatalogConstants.ID);

                    schoolVO = new SchoolVO();
                    this.setSchoolAddressDetails(schoolVO, schoolRepositoryItem,
								schoolIdRepo);
                    final RepositoryView schoolVerView = this.getSchoolVerRepository().getView(
                                    BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR);
                    final RqlStatement statement = getRqlForSchoolId();
                    final Object params[] = new Object[1];
                    params[0] = schoolIdRepo;
                    schoolVerRepositoryItem = statement.executeQuery(schoolVerView, params);
                    if (schoolVerRepositoryItem != null) {

                        this.setSchoolVOInfo(schoolVO, schoolVerRepositoryItem);
                      // School Promo container from BCC START
						final RepositoryItem schoolPromoSet = (RepositoryItem) schoolVerRepositoryItem[0].getPropertyValue(BBBCatalogConstants.SCHOOL_PROMO_ID);

						if (null != schoolPromoSet) {
							if(null != schoolPromoSet.getPropertyValue(BBBCatalogConstants.HTML_SNIPPET)){
								schoolVO.setSchoolContent(schoolPromoSet.getPropertyValue(BBBCatalogConstants.HTML_SNIPPET).toString());
							}
							if(null != schoolPromoSet.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH)){
								schoolVO.setCssFilePath(schoolPromoSet.getPropertyValue(BBBCatalogConstants.CSS_FILE_PATH).toString());
							}
							if(null != schoolPromoSet.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH)){
								schoolVO.setJsFilePath(schoolPromoSet.getPropertyValue(BBBCatalogConstants.JS_FILE_PATH).toString());
							}
						}
					// School Promo container from BCC END
                        final RepositoryItem promotionItem = (RepositoryItem) schoolVerRepositoryItem[0]
                                        .getPropertyValue(BBBCatalogConstants.PROMOTION_ITEM_DESCRIPTOR);
                        schoolVO.setPromotionRepositoryItem(promotionItem);
                        return schoolVO;

                    } else if (BBBUtility.isNotEmpty(channel) && channel.equals(BBBCoreConstants.MOBILEWEB)) {
                    	return schoolVO;
                    } else {
                    	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
                    	throw new BBBBusinessException(BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY);
                    }
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY);
            } catch (final RepositoryException e) {
            	 BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
                this.logError("Catalog API Method Name [getSchoolDetails]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
            	this.logDebug("Catalog API Method Name [getSchoolDetails]schoolId[" + schoolId + "] Exit");
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);

    }

	/**
	 * @param schoolVO
	 * @param schoolVerRepositoryItem
	 */
	private void setSchoolVOInfo(SchoolVO schoolVO,RepositoryItem[] schoolVerRepositoryItem) {
		schoolVO.setSmallLogoURL((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.SMALL_LOGO_URL_SCHOOL_PROPERTY_NAME));
		schoolVO.setCollegeLogo((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.COLLEGE_LOGO_SCHOOL_PROPERTY_NAME));
		schoolVO.setCollegeTag((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.COLLEGE_TAG_SCHOOL_PROPERTY_NAME));
		if (schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.CREATION_DATE_SCHOOL_PROPERTY_NAME) != null) {
		    schoolVO.setCreationDate((Date) schoolVerRepositoryItem[0]
		                    .getPropertyValue(BBBCatalogConstants.CREATION_DATE_SCHOOL_PROPERTY_NAME));
		}
		schoolVO.setImageURL((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.IMAGE_URL_ATTRIBUTE_PROPERTY_NAME));
		schoolVO.setLargeLogoURL((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.LARGE_LOGO_URL_SCHOOL_PROPERTY_NAME));
		schoolVO.setLargeWelcomeMsg((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.LARGE_WELCOME_MSG_SCHOOL_PROPERTY_NAME));
		if (schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.LAST_MODIFIED_DATE_SCHOOL_PROPERTY_NAME) != null) {
		    schoolVO.setLastModifiedDate((Date) schoolVerRepositoryItem[0]
		                    .getPropertyValue(BBBCatalogConstants.LAST_MODIFIED_DATE_SCHOOL_PROPERTY_NAME));
		}
		schoolVO.setPdfURL((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.PDF_URL_SCHOOL_PROPERTY_NAME));
		schoolVO.setPrefStoreId((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.PREF_STORE_ID_SCHOOL_PROPERTY_NAME));
		schoolVO.setSmallWelcomeMsg((String) schoolVerRepositoryItem[0]
		                .getPropertyValue(BBBCatalogConstants.SMALL_WELCOME_MSG_SCHOOL_PROPERTY_NAME));
	}

	/**
	 * @param schoolVO
	 * @param schoolRepositoryItem
	 * @param schoolIdRepo
	 */
	private void setSchoolAddressDetails(SchoolVO schoolVO,
			RepositoryItem schoolRepositoryItem, final String schoolIdRepo) {
		schoolVO.setSchoolId(schoolIdRepo);
		schoolVO.setSchoolName((String) schoolRepositoryItem
		                .getPropertyValue(BBBCatalogConstants.SCHOOL_NAME_SCHOOL_PROPERTY_NAME));

		schoolVO.setAddrLine1((String) schoolRepositoryItem
		                .getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_SCHOOL_PROPERTY_NAME));
		schoolVO.setAddrLine2((String) schoolRepositoryItem
		                .getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_SCHOOL_PROPERTY_NAME));
		schoolVO.setCity((String) schoolRepositoryItem
		                .getPropertyValue(BBBCatalogConstants.CITY_SCHOOL_PROPERTY_NAME));

		final StateVO stateVO = new StateVO();
		stateVO.setStateCode((String) schoolRepositoryItem
		                .getPropertyValue(BBBCatalogConstants.STATE_ITEM_DESCRIPTOR));
		schoolVO.setState(stateVO);
		schoolVO.setZip((String) schoolRepositoryItem
		                .getPropertyValue(BBBCatalogConstants.ZIP_SCHOOL_PROPERTY_NAME));
	}

	protected RqlStatement getRqlForSchoolId() throws RepositoryException {
		return RqlStatement.parseRqlStatement("schools = ?0");
	}

    
    /**
	 *  Returns SchoolVO based on the SEO name of the school.
	 *
	 * @param schoolSeoName SEO name of the school
	 * @return schoolVO
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
    public final SchoolVO getSchoolDetailsByName(final String schoolSeoName)
                    throws BBBSystemException, BBBBusinessException {

        this.logDebug("Catalog API Method Name [getSchoolDetails]schoolName[" + schoolSeoName + "]");
        SchoolVO schoolVO = null;
        RepositoryItem schoolRepositoryItem = null;
        RepositoryItem[] schoolVerRepositoryItem = null;

        if (!StringUtils.isBlank(schoolSeoName)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");

                final RepositoryView schoolVerView = this.getSchoolVerRepository().getView(
                                BBBCatalogConstants.SCHOOLS_VER_ITEM_DESCRIPTOR);
                final RqlStatement statement = getRqlForSchoolName();
                final Object params[] = new Object[1];
                params[0] = schoolSeoName;
                schoolVerRepositoryItem = statement.executeQuery(schoolVerView, params);

                this.logDebug("schoolRepository value:" + schoolRepositoryItem);
                if (schoolVerRepositoryItem != null) {

                    final RepositoryItem schoolIdRepoItem = (RepositoryItem) schoolVerRepositoryItem[0]
                                    .getPropertyValue(BBBCatalogConstants.SCHOOLS_ITEM_PROPERTY_NAME);
                    final String schoolIdRepo = (String) schoolIdRepoItem.getPropertyValue(BBBCatalogConstants.ID);

                    schoolRepositoryItem = this.getSchoolRepository().getItem(schoolIdRepo,
                                    BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR);

                    if (schoolRepositoryItem != null) {

                        schoolVO = new SchoolVO();
                        this.setSchoolAddressDetails(schoolVO, schoolRepositoryItem,
								schoolIdRepo);

                        this.setSchoolVOInfo(schoolVO, schoolVerRepositoryItem);
                        schoolVO.setSchoolSeoName((String) schoolVerRepositoryItem[0]
                                        .getPropertyValue(BBBCatalogConstants.SCHOOL_SEO_NAME_SCHOOL_PROPERTY_NAME));

                        final RepositoryItem promotionItem = (RepositoryItem) schoolVerRepositoryItem[0]
                                        .getPropertyValue(BBBCatalogConstants.PROMOTION_ITEM_DESCRIPTOR);
                        schoolVO.setPromotionRepositoryItem(promotionItem);
                        BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
                        return schoolVO;

                    }
                    BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY,
                                    BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY);
                }
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
                throw new BBBBusinessException(BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY,
                                BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY);
            } catch (final RepositoryException e) {
            	BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
                this.logError("Catalog API Method Name [getSchoolDetails]: RepositoryException ");
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
                                BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getSchoolDetails");
            }
        }
        throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL,
                        BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
    }

	protected RqlStatement getRqlForSchoolName() throws RepositoryException {
		return RqlStatement.parseRqlStatement("schoolSeoName = ?0");
	}
   
   
    /**
     *  The method return the College Name and Id corresponding to given state .
     *
     * @param pStateCode the state code
     * @return collegeList
     * @throws BBBSystemException the BBB system exception
     * @throws BBBBusinessException the BBB business exception
     */
   
	@SuppressWarnings("unchecked")
	public List<CollegeVO> getCollegesByState(String pStateCode)
			throws BBBSystemException, BBBBusinessException {

		if (BBBUtility.isEmpty(pStateCode)) {
			throw new BBBSystemException(BBBSearchBrowseConstants.EMPTY_PARAMETER,
					BBBSearchBrowseConstants.EMPTY_PARAMETER);
		}
		
		List<CollegeVO> collegeList = null;
		final String cacheName = this.getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
				BBBCoreConstants.COLLEGE_CACHE_NAME).get(0);
		int cacheTimeout = 0;
		try {
			cacheTimeout = Integer.parseInt(this.getAllValuesForKey(BBBCoreConstants.OBJECT_CACHE_CONFIG_KEY,
					BBBCoreConstants.COLLEGE_CACHE_TIMEOUT).get(0));
		} catch (NumberFormatException exception) {
			logError("NumberFormatException in getCollegesByState while getting cache timeout"
							+ BBBCoreConstants.COLLEGE_CACHE_TIMEOUT, exception);
		} catch (NullPointerException exception) {
			logError("NullPointerExceptionin getCollegesByState while getting cache timeout"
							+ BBBCoreConstants.COLLEGE_CACHE_TIMEOUT, exception);
		}

		if (null != getObjectCache() && null != getObjectCache().get(pStateCode, cacheName)) {
			collegeList = (List<CollegeVO>) getObjectCache().get(pStateCode, cacheName);
		} else {

			RqlStatement statement = null;
			

			try {
				statement = getRqlCollegesByState();
				RepositoryView view = getSchoolRepository().getView("schools");
				Object[] params = new Object[2];
				params[0] = BBBCoreConstants.TRUE;
				params[1] = pStateCode;
				RepositoryItem[] items = statement.executeQuery(view, params);
				if (items != null && items.length > 0) {
					collegeList = new ArrayList<CollegeVO>();
					CollegeVO collegeVO = null;
					for (RepositoryItem item : items) {
						collegeVO = new CollegeVO();
						collegeVO.setCollegeId(item.getRepositoryId());
						collegeVO.setCollegeName((String) item
								.getPropertyValue("schoolName"));
						collegeList.add(collegeVO);
					}
				}

				getObjectCache().put(pStateCode, collegeList, cacheName, cacheTimeout);

			} catch (RepositoryException e) {
				this.logError("Catalog API Method Name [getCollegesByState]: RepositoryException "
						+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);

			}
		}
		return collegeList;
	}

	protected RqlStatement getRqlCollegesByState() throws RepositoryException {
		return RqlStatement.parseRqlStatement("hidden!=?0 and state=?1 ORDER BY schoolName SORT ASC");
	}
	
	
	/* (non-Javadoc)
     * @see com.bbb.commerce.catalog.BBBCatalogTools#getBeddingShipAddress(java.lang.String)
     */
    public BeddingShipAddrVO getBeddingShipAddress(final String schoolId)
                    throws BBBSystemException, BBBBusinessException {

        this.logDebug("Catalog API Method Name [getBeddingShipAddress]schoolId[" + schoolId + "]");
        BeddingShipAddrVO beddingShipAddrVO = null;
        RepositoryItem schoolRepositoryItem = null;

        if (!StringUtils.isBlank(schoolId)) {
            try {
                BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getBeddingShipAddress");

                schoolRepositoryItem = this.getSchoolRepository().getItem(schoolId,
                                BBBCatalogConstants.SCHOOLS_ITEM_DESCRIPTOR);
                this.logDebug("schoolRepository value:" + schoolRepositoryItem);
                if (schoolRepositoryItem != null) {

                    String schoolIdRepo = null;
                    if (schoolRepositoryItem.getPropertyValue(BBBCatalogConstants.ID) != null) {
                        schoolIdRepo = (String) schoolRepositoryItem.getPropertyValue(BBBCatalogConstants.ID);
                    }
                    @SuppressWarnings ("deprecation")
                    final RepositoryItem schoolVerItem = this.getSchoolVerRepository().getItem(schoolIdRepo);

					final String siteId = getCurrentSiteId();
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DAY_OF_MONTH, 1);
					Date tommrrowDate = cal.getTime();
					String pAHStartDate = null;
                    String pAHEndDate = null;
                    
                    final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
                            BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
                    Date shippingEndDateFromCollege = null;
                    Date shippingEndDateFromSite = null;
                   

                    if (schoolVerItem != null) {

                        final SimpleDateFormat dateformat = new SimpleDateFormat(MM_DD_YYYY);
                        beddingShipAddrVO = new BeddingShipAddrVO();
                        if (schoolRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLEGE_NAME) != null) {
                            final String collegeName = (String) schoolRepositoryItem.getPropertyValue(BBBCatalogConstants.COLLEGE_NAME);
                            beddingShipAddrVO.setCollegeName(collegeName);
                        }

                        if (schoolVerItem.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_BEDDING_PROPERTY_NAME) != null) {
                            beddingShipAddrVO.setAddrLine1((String) schoolVerItem
                                            .getPropertyValue(BBBCatalogConstants.ADDRESS_LINE1_BEDDING_PROPERTY_NAME));
                        }
                        if (schoolVerItem.getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_BEDDING_PROPERTY_NAME) != null) {
                            beddingShipAddrVO.setAddrLine2((String) schoolVerItem
                                            .getPropertyValue(BBBCatalogConstants.ADDRESS_LINE2_BEDDING_PROPERTY_NAME));
                        }
                        if (schoolVerItem.getPropertyValue(BBBCatalogConstants.CITY_BEDDING_PROPERTY_NAME) != null) {
                            beddingShipAddrVO.setCity((String) schoolVerItem
                                            .getPropertyValue(BBBCatalogConstants.CITY_BEDDING_PROPERTY_NAME));
                        }
                        if (schoolVerItem.getPropertyValue(BBBCatalogConstants.STATE_BEDDING_PROPERTY_NAME) != null) {
                            final RepositoryItem stateRepositoryItem = (RepositoryItem) schoolVerItem
                                            .getPropertyValue(BBBCatalogConstants.STATE_BEDDING_PROPERTY_NAME);
                            if (stateRepositoryItem != null) {
                                beddingShipAddrVO.setState(stateRepositoryItem.getRepositoryId());
                            }
                        }
                        if (schoolVerItem.getPropertyValue(BBBCatalogConstants.ZIP_BEDDING_PROPERTY_NAME) != null) {
                            beddingShipAddrVO.setZip((String) schoolVerItem
                                            .getPropertyValue(BBBCatalogConstants.ZIP_BEDDING_PROPERTY_NAME));
                        }
                        if (schoolVerItem.getPropertyValue(BBBCatalogConstants.COMPANY_NAME) != null) {
                            beddingShipAddrVO.setCompanyName((String) schoolVerItem
                                            .getPropertyValue(BBBCatalogConstants.COMPANY_NAME));
                        } 
                        //BBBSL-8022 - user can only select dates from "current date + 1" to end date
                        pAHStartDate = dateformat.format(tommrrowDate);
                        if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
							beddingShipAddrVO.setShippingStartDate(BBBUtility.convertAppFormatDateToCAFormat(pAHStartDate));
							}
						else{		
						beddingShipAddrVO.setShippingStartDate(pAHStartDate);
						}
                        if (schoolVerItem.getPropertyValue(BBBCatalogConstants.SHIPPING_END_DATE) != null) {
                        	shippingEndDateFromCollege = (Date) schoolVerItem.getPropertyValue(BBBCatalogConstants.SHIPPING_END_DATE);
                        }
                        if (siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME) != null) {
                        	shippingEndDateFromSite = (Date) siteConfiguration.getPropertyValue(BBBCatalogConstants.PACK_AND_HOLD_END_DATE_SITE_PROPERTY_NAME);
                        }	
                        if (shippingEndDateFromCollege != null) {
                            
                        	if (null != shippingEndDateFromSite){
                        		if (shippingEndDateFromSite.before(shippingEndDateFromCollege)) {
                        			pAHEndDate = dateformat.format(shippingEndDateFromSite);
                        		}else{
                        			pAHEndDate = dateformat.format(shippingEndDateFromCollege);
                        		}
                        	} else {
                        		pAHEndDate = dateformat.format(shippingEndDateFromCollege);
                        	}
                            
                        } else if(shippingEndDateFromSite != null){
                        	pAHEndDate = dateformat.format(shippingEndDateFromSite);
                        }
                        if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA)){
							beddingShipAddrVO.setShippingEndDate(BBBUtility.convertAppFormatDateToCAFormat(pAHEndDate));
							}
						else{		
						beddingShipAddrVO.setShippingEndDate(pAHEndDate);
						}
                    } else {
                        throw new BBBBusinessException(BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY);
                    }
                } else {
                    throw new BBBBusinessException(BBBCatalogErrorCodes.SCHOOL_ID_NOT_AVAILABLE_IN_REPOSITORY);
                }
            } catch (final RepositoryException e) {

                this.logError("Catalog API Method Name [getBeddingShipAddress]: RepositoryException ",e);
                throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
            } finally {
                BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getBeddingShipAddress");
            }
        } else {
            throw new BBBBusinessException(BBBCatalogErrorCodes.INPUT_PARAMETER_IS_NULL);
        }

        return beddingShipAddrVO;
    }

	/**
	 * @return
	 */
	protected String getCurrentSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}
    
    
    /**
	 * @return the mObjectCache
	 */
	public BBBObjectCache getObjectCache() {
		return this.mObjectCache;
	}

	/**
	 * @param mObjectCache the mObjectCache to set
	 */
	public void setObjectCache(final BBBObjectCache pObjectCache) {
		this.mObjectCache = pObjectCache;
	}
	
	  /** @return the schoolRepository */
    public final MutableRepository getSchoolVerRepository() {
        return this.schoolVerRepository;
    }

    /** @param schoolVerRepository schoolRepository the schoolRepository to set */
    public final void setSchoolVerRepository(final MutableRepository schoolVerRepository) {
        this.schoolVerRepository = schoolVerRepository;
    }
	
	 /** @return the schoolRepository */
    public final MutableRepository getSchoolRepository() {
        return this.schoolRepository;
    }

    /** @param schoolRepository the schoolRepository to set */
    public final void setSchoolRepository(final MutableRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

	public MutableRepository getSiteRepository() {
		return siteRepository;
	}

	public void setSiteRepository(MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}
   
}
