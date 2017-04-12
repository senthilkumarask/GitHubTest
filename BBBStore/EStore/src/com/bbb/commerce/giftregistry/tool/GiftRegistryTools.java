package com.bbb.commerce.giftregistry.tool;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import oracle.jdbc.OracleTypes;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaClass;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.lang.StringUtils;

import atg.adapter.gsa.GSARepository;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.repository.ItemDescriptorImpl;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Query;
import atg.repository.QueryBuilder;
import atg.repository.QueryExpression;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;

import com.bbb.account.BBBProfileManager;
import com.bbb.account.vo.ProfileSyncRequestVO;
import com.bbb.account.vo.ProfileSyncResponseVO;
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.commerce.catalog.vo.RegistryTypeVO;
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.StoreVO;
import com.bbb.commerce.giftregistry.bean.AddItemsBean;
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.comparator.RegSearchComparator;
import com.bbb.commerce.giftregistry.comparator.RegistryComparable;
import com.bbb.commerce.giftregistry.utility.BBBGiftRegistryUtils;
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO;
import com.bbb.commerce.giftregistry.vo.ForgetRegPassRequestVO;
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO;
import com.bbb.commerce.giftregistry.vo.RegCopyResVO;
import com.bbb.commerce.giftregistry.vo.RegNamesVO;
import com.bbb.commerce.giftregistry.vo.RegSearchResVO;
import com.bbb.commerce.giftregistry.vo.RegStatusesResVO;
import com.bbb.commerce.giftregistry.vo.RegistrantVO;
import com.bbb.commerce.giftregistry.vo.RegistryBabyVO;
import com.bbb.commerce.giftregistry.vo.RegistryHeaderVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO;
import com.bbb.commerce.giftregistry.vo.RegistryPrefStoreVO;
import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO;
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistryStatusVO;
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO;
import com.bbb.commerce.giftregistry.vo.RegistryTypes;
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO;
import com.bbb.commerce.giftregistry.vo.SetAnnouncementCardResVO;
import com.bbb.commerce.giftregistry.vo.ShippingVO;
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO;
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.common.BBBGenericService;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants;
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPCallInvoker;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.integration.ServiceResponseIF;
import com.bbb.framework.integration.util.ServiceHandlerUtil;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.vo.ErrorStatus;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean;
import com.bbb.repository.util.RepositoryInvalidatorSevice;
import com.bbb.rest.output.BBBCustomTagComponent;
import com.bbb.utils.BBBUtility;

//import com.bedbathandbeyond.www.RegCopyResponseDocument.RegCopyResponse;

/**
 * 
 * This class provides the low level functionality for gift registry
 * creation/manipulation.
 * 
 * @author sku134
 * 
 */
public class GiftRegistryTools extends BBBGenericService {

	private static final String SESSION_BEAN = "/com/bbb/profile/session/SessionBean";

	private BBBCatalogTools mCatalogTools;
	private MutableRepository mGiftRepository;
	private BBBCustomTagComponent bbbCustomTagComponent;

	private String userRegistryQuery;
	private String giftRepoItemQuery;

	private BBBInventoryManager inventoryManager;
	private TransactionManager mTransactionManager;
	private MutableRepository siteRepository;
	private boolean stagingServer;
	/**
	 * Sets property TransactionManager
	 * 
	 * @param pTransactionManager
	 *            a <code>TransactionManager</code> value
	 */
	public void setTransactionManager(TransactionManager pTransactionManager) {
		mTransactionManager = pTransactionManager;
	}

	/**
	 * Returns property TransactionManager
	 * 
	 * @return a <code>TransactionManager</code> value
	 */
	public TransactionManager getTransactionManager() {
		return mTransactionManager;
	}

	public BBBInventoryManager getInventoryManager() {
		return this.inventoryManager;
	}

	public void setInventoryManager(BBBInventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	private RepositoryInvalidatorSevice repositoryInvalidatorSevice;
	/**
	 * to hold printAtHome
	 */
	private String printAtHome = "printAtHome";

	private BBBGiftRegistryUtils giftRegUtils;

	public BBBGiftRegistryUtils getGiftRegUtils() {
		return giftRegUtils;
	}

	public void setGiftRegUtils(BBBGiftRegistryUtils giftRegUtils) {
		this.giftRegUtils = giftRegUtils;
	}

	/**
	 * to hold sites
	 */
	private String sites = "sites";

	private String friendRegistryQuery;

	/**
	 * to hold announcementcard
	 */
	private String announcementcard = "announcementcard";

	/**
	 * to hold id
	 */
	private String id = "id";

	/**
	 * to hold Print At HomeRepository
	 */
	private MutableRepository mPrintAtHomeRepository;
	private MutableRepository mRegistryInfoRepository;
	private HTTPCallInvoker httpCallInvoker;

	/**
	 * BPS-1394
	 * 
	 * @return the repositoryInvalidatorSevice
	 */

	public RepositoryInvalidatorSevice getRepositoryInvalidatorSevice() {
		return repositoryInvalidatorSevice;
	}

	/**
	 * BPS-1394
	 * 
	 * @param repositoryInvalidatorSevice
	 *            the repositoryInvalidatorSevice to set
	 */
	public void setRepositoryInvalidatorSevice(
			RepositoryInvalidatorSevice repositoryInvalidatorSevice) {
		this.repositoryInvalidatorSevice = repositoryInvalidatorSevice;
	}

	// BPS-1394

	public BBBCustomTagComponent getBbbCustomTagComponent() {
		return bbbCustomTagComponent;
	}

	public void setBbbCustomTagComponent(
			BBBCustomTagComponent bbbCustomTagComponent) {
		this.bbbCustomTagComponent = bbbCustomTagComponent;
	}

	/**
	 * @return httpCallInvoker
	 */
	public HTTPCallInvoker getHttpCallInvoker() {

		if (this.httpCallInvoker == null) {
			this.httpCallInvoker = new HTTPCallInvoker();
		}
		return this.httpCallInvoker;
	}

	/**
	 * @param httpCallInvoker
	 */
	public void setHttpCallInvoker(final HTTPCallInvoker httpCallInvoker) {
		this.httpCallInvoker = httpCallInvoker;
	}

	/**
	 * BPS-1394
	 * 
	 * @return the repositoryInvalidatorSevice
	 */

	/**
	 * Filter the registry ids which are associated to given site id. Find the
	 * future registries, whose event date is greater present date. And return
	 * the very soonest registry among them.If future registries are null, then
	 * check the old registries, whose event date is lesser than present date.
	 * And return the very recent registry among them.
	 * 
	 * @param siteId
	 *            the site id
	 * @param registryIds
	 *            the registry ids
	 * @return registryIds
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws NumberFormatException
	 *             the number format exception
	 */
	public static String fetchUsersSoonestOrRecent(final String siteId,
			final String[] registryIds) throws RepositoryException,
			NumberFormatException {
		// pseudo code
		return registryIds[0];
	}

	/**
	 * Calling web service to set the announce card count
	 * 
	 * @param registryVO
	 * @return SetAnnouncementCardResVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public SetAnnouncementCardResVO assignAnnouncementCardCount(
			final RegistryVO registryVO) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("GiftRegistryTools.assignAnnouncementCardCount() method start");
		if (StringUtils.isEmpty(registryVO.getRegistryId())) {
			registryVO.setRegistryIdWS(0);
		} else {
			registryVO.setRegistryIdWS(Long.parseLong(registryVO
					.getRegistryId()));
		}
		final SetAnnouncementCardResVO setAnnouncementCardResVO = (SetAnnouncementCardResVO) ServiceHandlerUtil
				.invoke(registryVO);

		this.logDebug("GiftRegistryTools.assignAnnouncementCardCount() method ends");
		return setAnnouncementCardResVO;
	}

	/**
	 * Calling web service or stored procedure to create the registry. Deciding
	 * factor is regItemsWSCall
	 * 
	 * @param registryVO
	 *            the registry vo
	 * @return ValidateRegistryResVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */

	public RegistryResVO createRegistry(final RegistryVO registryVO)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryTools.createRegistry() method start");
		RegistryResVO createRegistryResVO = (RegistryResVO)extractUtilMethod(registryVO);
		this.logDebug("GiftRegistryTools.createRegistry() method ends");
		return createRegistryResVO;
	}

	/**
	 * Call the GiftRegistryTools class searchRegistries method to fetch the
	 * registry information through web service if flag 'regItemsWSCall' is true
	 * else perform db operation directly
	 * 
	 * @param registrySearchVO
	 *            the registry search vo
	 * @return registries
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */

	public RegSearchResVO searchRegistries(
			final RegistrySearchVO registrySearchVO, final String siteId)
			throws BBBSystemException, BBBBusinessException {

		this.logDebug("GiftRegistryTools.searchRegistries() method start");
		RegSearchResVO regSearchResVO = null;
		boolean regItemsWSCall = false;
		// invoking regSearch web service

		regSearchResVO = (RegSearchResVO) ServiceHandlerUtil
				.invoke(registrySearchVO);

		/*if (!regSearchResVO.getServiceErrorVO().isErrorExists()) {
			regSearchResVO = this.changedRegistryTypeName(regSearchResVO,
					siteId);
		}*/
		this.logDebug("GiftRegistryTools.searchRegistries() method ends");

		return regSearchResVO;
	}

	/**
	 * This method searches registry by profileID
	 * 
	 * @param registrySearchVO
	 * @param con
	 * @return RegSearchResVO
	 * @throws SQLException
	 */
	public RegSearchResVO searchRegistriesByProfileId(
			RegistrySearchVO registrySearchVO) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("GiftRegistryTools.searchRegistriesByProfileId() starts :");
		this.logDebug("Method Description : Searches for registries using profile ID and then convert result set to regSearchResVO");
		BBBPerformanceMonitor.start("GiftRegistryTools.searchRegistriesByProfileId()");
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		ServiceErrorVO serviceError = null;
		CallableStatement cs =null;
		Connection con = null;
		ResultSet resultSet = null;

		try {

			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				final String getRegInfobyPidStoredProc = BBBGiftRegistryConstants.GET_REG_INFO_BY_PROFILEID;
				cs = con
						.prepareCall(getRegInfobyPidStoredProc);
				cs.setString(1, registrySearchVO.getSiteId());
				cs.setString(2, registrySearchVO.getProfileId()
						.getRepositoryId());
				this.logDebug("GiftRegistryTools.GetRegistryInfoByProfileId() | parameters passed into procedure are profileId : "
						+ registrySearchVO.getProfileId().getRepositoryId()
						+ " and siteId : " + registrySearchVO.getSiteId());
				cs.registerOutParameter(3, OracleTypes.CURSOR);
				extractDBCall(cs);
				this.logDebug("GiftRegistryTools.GetRegistryStatusesByProfileId() | procedure "
						+ getRegInfobyPidStoredProc + " executed from method ");
				resultSet = (ResultSet) cs.getObject(3);
				if (resultSet.next()) {
					List<RegistrySummaryVO> regInfoResVo = new ArrayList<RegistrySummaryVO>();
					do {
						RegistrySummaryVO regSummaryVO = new RegistrySummaryVO();
						AddressVO shippingAddress = new AddressVO();
						regSummaryVO.setShippingAddress(shippingAddress);
						regSummaryVO.setRegistryId(resultSet
								.getString("REGISTRY_NUM"));
						regSummaryVO.setAddrSubType(resultSet
								.getString("NM_ADDR_SUB_TYPE"));
						regSummaryVO.setEventType(resultSet
								.getString("EVENT_TYPE"));
						regSummaryVO.setIsPublic(resultSet.getString("IS_PUBLIC"));
						String date = "";
						if (null != resultSet.getString("EVENT_DT")
								&& resultSet.getString("EVENT_DT").length() > 7) {
							date = BBBUtility
									.convertDateWSToAppFormat(resultSet
											.getString("EVENT_DT"));
						}
						regSummaryVO.setEventDate(date);
						regSummaryVO.setOwnerProfileID(resultSet
								.getString("ATG_PROFILE_ID"));

						regSummaryVO.setPrimaryRegistrantFirstName(resultSet
								.getString("FIRST_NM_COPY"));
						regSummaryVO.setPrimaryRegistrantLastName(resultSet
								.getString("LAST_NM_COPY"));
						regSummaryVO.setPrimaryRegistrantMaidenName(resultSet
								.getString("MAIDEN_COPY"));

						shippingAddress.setAddressLine1(resultSet
								.getString("ADDR1"));
						shippingAddress.setAddressLine2(resultSet
								.getString("ADDR2"));
						shippingAddress.setCompany(resultSet
								.getString("COMPANY"));
						shippingAddress.setCity(resultSet.getString("CITY"));
						shippingAddress.setState(resultSet
								.getString("STATE_CD"));
						shippingAddress.setZip(resultSet.getString("ZIP_CD"));
						regSummaryVO.setShippingAddress(shippingAddress);

						regSummaryVO.setPrimaryRegistrantFullName(resultSet
								.getString("REGISTRANT_NAME"));
						regSummaryVO.setCoRegistrantFullName(resultSet
								.getString("COREGISTRANT_NAME"));

						regSummaryVO.setPrimaryRegistrantEmail(resultSet
								.getString("EMAIL_ADDR"));

						regSummaryVO.setGiftRegistered(resultSet
								.getInt("GIFTSREGISTERED"));
						regSummaryVO.setGiftPurchased(resultSet
								.getInt("GIFTSPURCHASED"));

						regInfoResVo.add(regSummaryVO);
					} while (resultSet.next());
					regSearchResVO.setListRegistrySummaryVO(regInfoResVo);
				}
			}
		} catch (SQLException excep) {
			if (excep.getMessage() !=null && excep.getMessage().contains(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
				logInfo("GiftRegistryTools.searchRegistriesByProfileId() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
			} else {
			logError("SQL exception while registry header info "
					+ "in GiftRegistryTools", excep);
			}
			serviceError = (ServiceErrorVO) getGiftRegUtils()
					.logAndFormatError(registrySearchVO.getServiceName(), con,
							BBBGiftRegistryConstants.SERVICE_ERROR_VO, excep,
							registrySearchVO.getSiteId(),
							registrySearchVO.getProfileId().getRepositoryId());
			regSearchResVO.setServiceErrorVO(serviceError);
			return regSearchResVO;
		} finally {
			closeRegistryInfoRepoResources(cs, con, resultSet);
			this.logDebug("GiftRegistryTools.searchRegistriesByProfileId() ends.");
			BBBPerformanceMonitor.end("GiftRegistryTools.searchRegistriesByProfileId()");
		}
		return regSearchResVO;
	}

	/**
	 * @param cs
	 * @param con
	 * @param resultSet
	 * @throws BBBSystemException
	 */
	protected void closeRegistryInfoRepoResources(CallableStatement cs, Connection con, ResultSet resultSet)
			throws BBBSystemException {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if(cs != null){
				cs.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			throw new BBBSystemException(
					"An exception occurred while executing the sql statement.",
					e);
		}
	}

	/**
	 * @param cs
	 * @throws SQLException
	 */
	protected void extractDBCall(CallableStatement cs) throws SQLException {
		cs.executeQuery();
	}

	/**
	 * This method searches registry by registry number in case of gift giver
	 * 
	 * @param registrySearchVO
	 * @param con
	 * @return RegSearchResVO
	 * @throws SQLException
	 */
	public RegSearchResVO getRegistryListByRegNum(
			RegistrySearchVO registrySearchVO) throws Exception {
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryListByRegNum()");
		this.logDebug("GiftRegistryTools.getRegistryListByRegNum() starts :");
		this.logDebug("Method Description : Searches for registries using registry Number and then convert result set to List of RegistrySummaryVO.(For GiftGiver) with parameter registryNumber=" + registrySearchVO.getRegistryId());
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM);
				cs.setFetchSize(100);
				// set input parameters ...
				cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
				cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
				cs.setString(3, registrySearchVO.getSiteId());
				cs.setString(4,
						registrySearchVO.isReturnLeagacyRegistries() ? "Y"
								: "N");
				cs.registerOutParameter(5, OracleTypes.CURSOR);
				// execute stored proc
				extractDBCallforgetRegistryListByRegNum(cs);
				resultSet = (ResultSet) cs.getObject(5);
				List<RegistrySummaryVO> lRegistrySummaryVOs = convertResultSetToListForGiftGiver(
						resultSet, registrySearchVO.getGiftGiver());
				if (!BBBUtility.isListEmpty(lRegistrySummaryVOs)) {
					// remove duplicate rows if any
					lRegistrySummaryVOs = getGiftRegUtils()
							.removeDuplicateRows(lRegistrySummaryVOs);
					regSearchResVO.setTotEntries(lRegistrySummaryVOs.size());
					if (BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE.equalsIgnoreCase(registrySearchVO.getSort())) {
						Map<Integer, List<RegistrySummaryVO>> regListMap = seperateRegistriesWithEmptyDate(lRegistrySummaryVOs);
						lRegistrySummaryVOs = regListMap.get(Integer.valueOf(1));
						// sort result based on sort criteria set in request
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						
						List<RegistrySummaryVO> sortedRegistryList = new ArrayList<RegistrySummaryVO>();
						
						sortedRegistryList.addAll(lRegistrySummaryVOs);
						sortedRegistryList.addAll(regListMap.get(Integer.valueOf(0)));
						// request
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								sortedRegistryList,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					} else {
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								lRegistrySummaryVOs,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					}

					getGiftRegUtils().getPagedData(lRegistrySummaryVOs,
							registrySearchVO.getStartIdx(),
							registrySearchVO.getBlkSize());
					regSearchResVO
							.setListRegistrySummaryVO(lRegistrySummaryVOs);

				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null) {
					cs.close();
				}
				if (con != null) {
					con.close();
				}

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection", e);
			}
			this.logDebug("GiftRegistryTools.getRegistryListByRegNum() ends.");
			BBBPerformanceMonitor.end("GiftRegistryTools.getRegistryListByRegNum()");
		}

		return regSearchResVO;
	}

	/**
	 * @param cs
	 * @return
	 * @throws SQLException
	 */
	protected boolean extractDBCallforgetRegistryListByRegNum(CallableStatement cs) throws SQLException {
		return cs.execute();
	}

	/**
	 * This method searches registry by email for gift giver
	 * 
	 * @param registrySearchVO
	 * @param con
	 * @return RegSearchResVO
	 * @throws SQLException
	 */
	public RegSearchResVO getRegistryListByEmail(
			RegistrySearchVO registrySearchVO) throws Exception {
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryListByEmail()");
		this.logDebug("GiftRegistryTools.getRegistryListByEmail() starts :");
		this.logDebug("Method Description : Searches for registries using email for giftgiver and then convert result set to RegSearchResVO with parameter email=" + registrySearchVO.getEmail());
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_EMAIL2);
				cs.setFetchSize(100);
				// set input parameters ...
				cs.setString(1, registrySearchVO.getEmail());
				cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
				cs.setString(
						3,
						getFilterOptions(registrySearchVO.getEvent(),
								registrySearchVO.getState()));
				cs.setString(4, registrySearchVO.getSiteId());
				cs.setString(5,
						(registrySearchVO.isReturnLeagacyRegistries()) ? "Y"
								: "N");
				cs.registerOutParameter(6, OracleTypes.CURSOR);
				extractDBCallforgetRegistryListByRegNum(cs);

				resultSet = (ResultSet) cs.getObject(6);

				// convert result set to list
				List<RegistrySummaryVO> lRegistrySummaryVOs = convertResultSetToListForGiftGiver(
						resultSet, registrySearchVO.getGiftGiver());
				if (!BBBUtility.isListEmpty(lRegistrySummaryVOs)) {
					// remove duplicate rows from db if any
					lRegistrySummaryVOs = getGiftRegUtils()
							.removeDuplicateRows(lRegistrySummaryVOs);
					regSearchResVO.setTotEntries(lRegistrySummaryVOs.size());
					if (BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE.equalsIgnoreCase(registrySearchVO.getSort())) {
						Map<Integer, List<RegistrySummaryVO>> regListMap = seperateRegistriesWithEmptyDate(lRegistrySummaryVOs);
						lRegistrySummaryVOs = regListMap.get(Integer.valueOf(1));
						// sort result based on sort criteria set in request
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						
						List<RegistrySummaryVO> sortedRegistryList = new ArrayList<RegistrySummaryVO>();
						
						sortedRegistryList.addAll(lRegistrySummaryVOs);
						sortedRegistryList.addAll(regListMap.get(Integer.valueOf(0)));
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								sortedRegistryList,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					} else {
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								lRegistrySummaryVOs,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					}
					// get paged data based on index and no of records set in
					// request
					
					regSearchResVO
							.setListRegistrySummaryVO(lRegistrySummaryVOs);
				}

			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null)
					cs.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection", e);
			}
			BBBPerformanceMonitor.end("GiftRegistryTools.getRegistryListByEmail()");
			this.logDebug("GiftRegistryTools.getRegistryListByEmail() ends.");
		}

		return regSearchResVO;
	}

	/**
	 * This method searches registry by first name and last name with or without
	 * state for gift giver
	 * 
	 * @param registrySearchVO
	 * @param con
	 * @return RegSearchResVO
	 * @throws SQLException
	 */
	public RegSearchResVO getRegistryListByName(
			RegistrySearchVO registrySearchVO) throws Exception {
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryListByName()");
		this.logDebug("GiftRegistryTools.getRegistryListByName() starts :");
		this.logDebug("Method Description : Searches for registries using first name and last name with or without state For GiftGiver with parameter : firstName=" + registrySearchVO.getFirstName() + " ,lastName=" +registrySearchVO.getLastName());
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_NAME2);
				cs.setFetchSize(100);
				// set input parameters ...
				if(!BBBUtility.isEmpty(registrySearchVO.getFirstName())){
				cs.setString(1, registrySearchVO.getFirstName().trim());
				}
				if(!BBBUtility.isEmpty(registrySearchVO.getLastName())){
				cs.setString(2, registrySearchVO.getLastName().trim());
				}
				cs.setString(3, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
				cs.setString(
						4,
						getFilterOptions(registrySearchVO.getEvent(),
								registrySearchVO.getState()));
				cs.setString(5, registrySearchVO.getSiteId());
				cs.setString(6,
						(registrySearchVO.isReturnLeagacyRegistries()) ? "Y"
								: "N");
				cs.registerOutParameter(7, OracleTypes.CURSOR);

				// execute stored proc
				extractDBCallforgetRegistryListByRegNum(cs);
				resultSet = (ResultSet) cs.getObject(7);

				// convert result set to list
				List<RegistrySummaryVO> lRegistrySummaryVOs = convertResultSetToListForGiftGiver(
						resultSet, registrySearchVO.getGiftGiver());
				if (!BBBUtility.isListEmpty(lRegistrySummaryVOs)) {
					// remove duplicate rows from list if any
					lRegistrySummaryVOs = getGiftRegUtils()
							.removeDuplicateRows(lRegistrySummaryVOs);
					regSearchResVO.setTotEntries(lRegistrySummaryVOs.size());
					if (BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE.equalsIgnoreCase(registrySearchVO.getSort())) {
						Map<Integer, List<RegistrySummaryVO>> regListMap = seperateRegistriesWithEmptyDate(lRegistrySummaryVOs);
						lRegistrySummaryVOs = regListMap.get(Integer.valueOf(1));
						// sort result based on sort criteria set in request
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						
						List<RegistrySummaryVO> sortedRegistryList = new ArrayList<RegistrySummaryVO>();
						
						sortedRegistryList.addAll(lRegistrySummaryVOs);
						sortedRegistryList.addAll(regListMap.get(Integer.valueOf(0)));
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								sortedRegistryList,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());

					} else {
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								lRegistrySummaryVOs,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					}

					regSearchResVO
							.setListRegistrySummaryVO(lRegistrySummaryVOs);
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null)
					cs.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection", e);
			}
			this.logDebug("GiftRegistryTools.getRegistryListByName() ends.");
			BBBPerformanceMonitor.end("GiftRegistryTools.getRegistryListByName()");
		}
		return regSearchResVO;
	}

	/**
	 * This method searches registry by registry number (non gift giver)
	 * 
	 * @param registrySearchVO
	 * @param con
	 * @return RegSearchResVO
	 * @throws SQLException
	 */
	public RegSearchResVO regSearchByRegUsingRegNum(
			RegistrySearchVO registrySearchVO) throws Exception {
		BBBPerformanceMonitor.start("GiftRegistryTools.regSearchByRegUsingRegNum()");
		this.logDebug("GiftRegistryTools.regSearchByRegUsingRegNum() starts :");
		this.logDebug("Method Description : Searches for registries using registry Number and then convert result set to List of RegistrySummaryVO.(For GiftGiver)");
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_REGNUM);

				// set input parameters ...
				cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
				cs.setString(2, registrySearchVO.getSiteId());
				cs.setString(3,
						registrySearchVO.isReturnLeagacyRegistries() ? "Y"
								: "N");
				cs.setString(4, registrySearchVO.getProfileId()
						.getRepositoryId());
				cs.registerOutParameter(5, OracleTypes.CURSOR);
				// execute stored proc
				extractDBCallforgetRegistryListByRegNum(cs);
				resultSet = (ResultSet) cs.getObject(5);

				// convert result set to list
				List<RegistrySummaryVO> lRegistrySummaryVOs = convertResultSetToListForNonGiftGiver(
						resultSet, registrySearchVO.getGiftGiver());
				if (!BBBUtility.isListEmpty(lRegistrySummaryVOs)) {
					// remove duplicates from list if any
					lRegistrySummaryVOs = getGiftRegUtils()
							.removeDuplicateRows(lRegistrySummaryVOs);
					regSearchResVO.setTotEntries(lRegistrySummaryVOs.size());
					if (BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE.equalsIgnoreCase(registrySearchVO.getSort())) {
						Map<Integer, List<RegistrySummaryVO>> regListMap = seperateRegistriesWithEmptyDate(lRegistrySummaryVOs);
						lRegistrySummaryVOs = regListMap.get(Integer.valueOf(1));
						// sort result based on sort criteria set in request
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						
						List<RegistrySummaryVO> sortedRegistryList = new ArrayList<RegistrySummaryVO>();
						
						sortedRegistryList.addAll(lRegistrySummaryVOs);
						sortedRegistryList.addAll(regListMap.get(Integer.valueOf(0)));
						
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								sortedRegistryList,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					} else {
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								lRegistrySummaryVOs,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					}

					regSearchResVO
							.setListRegistrySummaryVO(lRegistrySummaryVOs);
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null)
					cs.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection", e);
			}
			BBBPerformanceMonitor.end("Searches for registries using registry number and then convert result set to RegSearchResVO.(Non GiftGiver)", "Method :: regSearchByRegUsingRegNum()");
			this.logDebug("GiftRegistryTools.regSearchByRegUsingRegNum() ends.");
		}

		return regSearchResVO;
	}

	/**
	 * This method searches registry based on email set in request (Gift giver
	 * false)
	 * 
	 * @param registrySearchVO
	 * @param con
	 * @return RegSearchResVO
	 * @throws SQLException
	 */
	public RegSearchResVO regSearchByRegUsingEmail(
			RegistrySearchVO registrySearchVO) throws Exception {
		BBBPerformanceMonitor.start("Searches for registries using email from request and then convert result set to RegSearchResVO.(GiftGiver false)", "Method :: regSearchByRegUsingEmail()");
		this.logDebug("GiftRegistryTools.regSearchByRegUsingEmail() starts :");
		this.logDebug("Method Description : Searches for registries using registry Number and then convert result set to List of RegistrySummaryVO.(For GiftGiver)");
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_EMAIL2);

				// set input parameters ...
				cs.setString(1, registrySearchVO.getEmail());
				cs.setString(
						2,
						getFilterOptions(registrySearchVO.getEvent(),
								registrySearchVO.getState()));
				cs.setString(3, registrySearchVO.getSiteId());
				cs.setString(4,
						registrySearchVO.isReturnLeagacyRegistries() ? "Y"
								: "N");
				cs.setString(5, registrySearchVO.getProfileId()
						.getRepositoryId());
				cs.registerOutParameter(6, OracleTypes.CURSOR);
				// execute stored proc
				extractDBCallforgetRegistryListByRegNum(cs);

				resultSet = (ResultSet) cs.getObject(6);

				// convert result set to list
				List<RegistrySummaryVO> lRegistrySummaryVOs = convertResultSetToListForNonGiftGiver(
						resultSet, registrySearchVO.getGiftGiver());

				if (!BBBUtility.isListEmpty(lRegistrySummaryVOs)) {
					// remove duplicate rows from list if any
					lRegistrySummaryVOs = getGiftRegUtils()
							.removeDuplicateRows(lRegistrySummaryVOs);
					regSearchResVO.setTotEntries(lRegistrySummaryVOs.size());
					
					if (BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE.equalsIgnoreCase(registrySearchVO.getSort())) {
						Map<Integer, List<RegistrySummaryVO>> regListMap = seperateRegistriesWithEmptyDate(lRegistrySummaryVOs);
						lRegistrySummaryVOs = regListMap.get(Integer.valueOf(1));
						// sort result based on sort criteria set in request
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						
						List<RegistrySummaryVO> sortedRegistryList = new ArrayList<RegistrySummaryVO>();
						
						sortedRegistryList.addAll(lRegistrySummaryVOs);
						sortedRegistryList.addAll(regListMap.get(Integer.valueOf(0)));
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								sortedRegistryList,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					} else {
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								lRegistrySummaryVOs,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					}

					// get paged data based on index and no of records set in
					// request
					

					regSearchResVO
							.setListRegistrySummaryVO(lRegistrySummaryVOs);
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null)
					cs.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection", e);
			}
			this.logDebug("GiftRegistryTools.regSearchByRegUsingEmail() ends.");
			BBBPerformanceMonitor.end("GiftRegistryTools.regSearchByRegUsingRegNum()");
		}
		return regSearchResVO;
	}

	/**
	 * This method searches registry based on First name , last name with or
	 * without state (when gift giver is false)
	 * 
	 * @param registrySearchVO
	 * @param con
	 * @return RegSearchResVO
	 * @throws SQLException
	 */
	public RegSearchResVO regSearchByRegUsingName(
			RegistrySearchVO registrySearchVO) throws Exception {
		BBBPerformanceMonitor.start("GiftRegistryTools.regSearchByRegUsingName()");
		this.logDebug("GiftRegistryTools.regSearchByRegUsingName() starts :");
		this.logDebug("Method Description : Searches for registries using first name and last name with or without state and then convert result set to RegSearchResVO.(GiftGiver false)");
		RegSearchResVO regSearchResVO = new RegSearchResVO();
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				// prepare the callable statement
				cs = con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_NAME2);

				// set input parameters ...
				if(!BBBUtility.isEmpty(registrySearchVO.getFirstName())){
				cs.setString(1, registrySearchVO.getFirstName().trim());
				}
				if(!BBBUtility.isEmpty(registrySearchVO.getLastName())){
				cs.setString(2, registrySearchVO.getLastName().trim());
				}
				cs.setString(
						3,
						getFilterOptions(registrySearchVO.getEvent(),
								registrySearchVO.getState()));
				cs.setString(4, registrySearchVO.getSiteId());
				cs.setString(5,
						registrySearchVO.isReturnLeagacyRegistries() ? "Y"
								: "N");
				cs.setString(6, registrySearchVO.getProfileId()
						.getRepositoryId());
				cs.registerOutParameter(7, OracleTypes.CURSOR);
				// execute stored proc
				extractDBCallforgetRegistryListByRegNum(cs);
				resultSet = (ResultSet) cs.getObject(7);

				// converting result set to list
				List<RegistrySummaryVO> lRegistrySummaryVOs = convertResultSetToListForNonGiftGiver(
						resultSet, registrySearchVO.getGiftGiver());

				if (!BBBUtility.isListEmpty(lRegistrySummaryVOs)) {
					// remove duplicate rows from list if any
					lRegistrySummaryVOs = getGiftRegUtils()
							.removeDuplicateRows(lRegistrySummaryVOs);
					regSearchResVO.setTotEntries(lRegistrySummaryVOs.size());
					
					if (BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE.equalsIgnoreCase(registrySearchVO.getSort())) {
						Map<Integer, List<RegistrySummaryVO>> regListMap = seperateRegistriesWithEmptyDate(lRegistrySummaryVOs);
						lRegistrySummaryVOs = regListMap.get(Integer.valueOf(1));
						// sort result based on sort criteria set in request
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						
						List<RegistrySummaryVO> sortedRegistryList = new ArrayList<RegistrySummaryVO>();
						
						sortedRegistryList.addAll(lRegistrySummaryVOs);
						sortedRegistryList.addAll(regListMap.get(Integer.valueOf(0)));
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								sortedRegistryList,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					} else {
						sortAndExcludeRegistries(lRegistrySummaryVOs, registrySearchVO);
						lRegistrySummaryVOs = getGiftRegUtils().getPagedData(
								lRegistrySummaryVOs,
								registrySearchVO.getStartIdx(),
								registrySearchVO.getBlkSize());
					}
					
					regSearchResVO
							.setListRegistrySummaryVO(lRegistrySummaryVOs);
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close();
				}
				if (cs != null)
					cs.close();
				if (con != null)
					con.close();

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection", e);
			}
			this.logDebug("GiftRegistryTools.regSearchByRegUsingName() ends.");
			BBBPerformanceMonitor.end("GiftRegistryTools.regSearchByRegUsingName()");
		}

		return regSearchResVO;
	}
	
	/**
	 * This method creates 2 list for registries based on the presence of event date
	 * @param lRegistrySummaryVOs
	 * @return Map<Integer, List<RegistrySummaryVO>>
	 */

	private Map<Integer, List<RegistrySummaryVO>> seperateRegistriesWithEmptyDate(List<RegistrySummaryVO> lRegistrySummaryVOs) {
		
		Map<Integer, List<RegistrySummaryVO>> regListMap = new HashMap<Integer, List<RegistrySummaryVO>>();
		List<RegistrySummaryVO> regListWithDate = new ArrayList<RegistrySummaryVO>();
		List<RegistrySummaryVO> regListWithoutDate = new ArrayList<RegistrySummaryVO>();
		
		if (!BBBUtility.isListEmpty(lRegistrySummaryVOs)) {
			for (RegistrySummaryVO registrySummaryVO : lRegistrySummaryVOs) {
				if (registrySummaryVO.getEventDateObject() == null) {
					regListWithoutDate.add(registrySummaryVO);
				} else {
					regListWithDate.add(registrySummaryVO);
				}
			}
			
		}
			
		regListMap.put(0, regListWithoutDate);
		regListMap.put(1, regListWithDate);
		return regListMap;
		
	}

	/**
	 * This method sorts db result corresponding to sort criterion set in
	 * request using comparator
	 * 
	 * @param lResultSet
	 * @param registrySearchVO
	 */
	@SuppressWarnings("unchecked")
	private void sortAndExcludeRegistries(List<RegistrySummaryVO> lResultSet,
			RegistrySearchVO registrySearchVO) {

		if (registrySearchVO.getSortSeqOrder() != null) {
			switch (registrySearchVO.getSortSeqOrder()) {

			case BBBGiftRegistryConstants.SORT_ORDER_DESC:
				if (registrySearchVO.getSort() != null) {
					switch (registrySearchVO.getSort()) {
					case BBBGiftRegistryConstants.DEFAULT_SORT_SEQ_KEY:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().firstNameDesc,
								new RegistryComparable().lastNameDesc,
								new RegistryComparable().eventDateDesc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().stateDesc,
								new RegistryComparable().firstNameDesc,
								new RegistryComparable().lastNameDesc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().eventDateDesc,
								new RegistryComparable().firstNameDesc,
								new RegistryComparable().lastNameDesc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_REGNUM:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().regIdDesc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_MAIDEN:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().maidenDesc,
								new RegistryComparable().firstNameDesc,
								new RegistryComparable().lastNameDesc,
								new RegistryComparable().eventDateDesc));
						break;
						
					case BBBGiftRegistryConstants.SORT_EVENT_TYPE:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().eventTypeDesc,
								new RegistryComparable().firstNameDesc,
								new RegistryComparable().lastNameDesc));
						break;

					default:
						break;
					}
					break;
				}
			default:
				if (registrySearchVO.getSort() != null) {
					switch (registrySearchVO.getSort()) {
					case BBBGiftRegistryConstants.DEFAULT_SORT_SEQ_KEY:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().firstNameAsc,
								new RegistryComparable().lastNameAsc,
								new RegistryComparable().eventDateAsc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().stateAsc,
								new RegistryComparable().firstNameAsc,
								new RegistryComparable().lastNameAsc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().eventDateAsc,
								new RegistryComparable().firstNameAsc,
								new RegistryComparable().lastNameAsc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_REGNUM:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().regIdAsc));
						break;

					case BBBGiftRegistryConstants.SORT_SEQ_KEY_MAIDEN:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().maidenAsc,
								new RegistryComparable().firstNameAsc,
								new RegistryComparable().lastNameAsc,
								new RegistryComparable().eventDateAsc));
						break;
						
					case BBBGiftRegistryConstants.SORT_EVENT_TYPE:
						Collections.sort(lResultSet, new RegSearchComparator(
								new RegistryComparable().eventTypeAsc,
								new RegistryComparable().firstNameAsc,
								new RegistryComparable().lastNameAsc));
						break;

					default:
						break;
					}
				}
			}
		} else {
			Collections.sort(lResultSet, new RegSearchComparator(
					new RegistryComparable().firstNameAsc,
					new RegistryComparable().lastNameAsc,
					new RegistryComparable().eventDateAsc));
		}
	}

	/**
	 * This method converts result set retrieved from db to list in case of gift
	 * giver
	 * 
	 * @param rs
	 * @param isGiftGiver
	 * @return List<RegistrySummaryVO>
	 * @throws SQLException
	 */
	private List<RegistrySummaryVO> convertResultSetToListForGiftGiver(
			ResultSet rs, boolean isGiftGiver) throws SQLException {
		// RegSearchResVO regSearchResVO = new RegSearchResVO();
		List<RegistrySummaryVO> lRegistrySummaryVOs = new ArrayList<RegistrySummaryVO>();
		RegistrySummaryVO registrySummaryVO = null;
		while (rs.next()) {
			registrySummaryVO = new RegistrySummaryVO();
			registrySummaryVO.setRegistryId(rs.getString("REGISTRY_NUM"));

			String eventDate = rs.getString("EVENT_DT");
			if (eventDate != null && eventDate.length() == 8) {
				registrySummaryVO.setEventDate(eventDate.substring(4, 6) + "/"
						+ rs.getString("EVENT_DT").substring(6, 8) + "/"
						+ rs.getString("EVENT_DT").substring(0, 4));
				registrySummaryVO.setEventDateObject(getGiftRegUtils().formatStringToDate(registrySummaryVO.getEventDate(), BBBCoreConstants.DATE_FORMAT));
			} else {
				registrySummaryVO.setEventDate(null);
			}
			registrySummaryVO.setEventType(rs.getString("EVENT_TYPE"));
			 registrySummaryVO.setEventDescription(rs.getString("EVENT_DESC"));
			registrySummaryVO.setPrimaryRegistrantFirstName(rs
					.getString("REG_NAME"));
			registrySummaryVO.setPrimaryRegistrantLastName(rs
					.getString("LAST_NM"));
			registrySummaryVO.setState(rs.getString("STATE_CD"));
			registrySummaryVO.setPrimaryRegistrantMaidenName(rs
					.getString("MAIDEN"));
			registrySummaryVO.setCoRegistrantFirstName(rs
					.getString("COREG_NAME"));
			registrySummaryVO.setCoRegistrantMaidenName(rs
					.getString("COREG_MAIDEN"));
			registrySummaryVO.setSubType(rs.getString("SEARCHED_REG_SUB_TYPE"));

			if ("RE".equals(registrySummaryVO.getSubType())) {
				registrySummaryVO.setPrimaryRegistrantMaidenName(rs
						.getString("MAIDEN"));
			} else if ("CO".equals(registrySummaryVO.getSubType())) {
				registrySummaryVO.setCoRegistrantMaidenName(rs
						.getString("MAIDEN"));
			}
			if (isGiftGiver) {
				registrySummaryVO.setPwsurl(rs.getString("PWS_URL"));
				if (("A".equals(rs.getString("ROW_STATUS")))
						&& ("Y".equals(rs.getString("SITE_PUBLISHED_CD")))) {
					registrySummaryVO.setPwsurl(rs.getString("PWS_URL"));
					registrySummaryVO.setPersonalWebsiteToken(getGiftRegUtils()
							.encryptRegNumForPersonalWebsite(
									Long.parseLong(registrySummaryVO
											.getRegistryId())));
				}
			}
			registrySummaryVO.setEventDateCanada(BBBUtility
					.convertUSDateIntoWSFormatCanada(registrySummaryVO
							.getEventDate()));
			// regEntries[index].getEventTypeDesc();

			lRegistrySummaryVOs.add(registrySummaryVO);

		}
		rs.close();
		// regSearchResVO.setTotEntries(rs.getFetchSize());
		// registrySearchReturn.CurrentEntriesReturned = regEntries.Count;
		// regSearchResVO.setListRegistrySummaryVO(lRegistrySummaryVOs);
		return lRegistrySummaryVOs;
	}

	/**
	 * This method converts result set retrieved from db to list in case gift
	 * giver is false
	 * 
	 * @param rs
	 * @param isGiftGiver
	 * @return List<RegistrySummaryVO>
	 * @throws SQLException
	 */
	private List<RegistrySummaryVO> convertResultSetToListForNonGiftGiver(
			ResultSet rs, boolean isGiftGiver) throws SQLException {
		List<RegistrySummaryVO> lRegistrySummaryVOs = new ArrayList<RegistrySummaryVO>();
		RegistrySummaryVO registrySummaryVO = null;
		while (rs.next()) {
			registrySummaryVO = new RegistrySummaryVO();
			registrySummaryVO.setRegistryId(rs.getString("REGISTRY_NUM"));
			String eventDate = rs.getString("EVENT_DT");
			if (eventDate != null && eventDate.length() == 8) {
				registrySummaryVO.setEventDate(eventDate.substring(4, 6) + "/"
						+ rs.getString("EVENT_DT").substring(6, 8) + "/"
						+ rs.getString("EVENT_DT").substring(0, 4));
				registrySummaryVO.setEventDateObject(getGiftRegUtils().formatStringToDate(registrySummaryVO.getEventDate(), BBBCoreConstants.DATE_FORMAT));
			} else {
				registrySummaryVO.setEventDate(null);
			}
			registrySummaryVO.setEventType(rs.getString("EVENT_TYPE"));
			// registrySummaryVO.setEventType(rs.getString("EVENT_DESC"));
			registrySummaryVO.setPrimaryRegistrantFirstName(rs
					.getString("REG_FULL_NAME"));
			registrySummaryVO.setPrimaryRegistrantLastName(rs
					.getString("REG_LAST_NM"));
			registrySummaryVO.setState(rs.getString("REG_STATE_CD"));
			registrySummaryVO.setPrimaryRegistrantMaidenName(rs
					.getString("REG_MAIDEN"));
			registrySummaryVO.setOwnerProfileID(rs
					.getString("REG_ATG_PROFILE_ID"));
			registrySummaryVO.setCoRegistrantFirstName(rs
					.getString("COREG_FULL_NAME"));
			registrySummaryVO.setCoRegistrantMaidenName(rs
					.getString("COREG_MAIDEN"));
			registrySummaryVO.setCoownerProfileID(rs
					.getString("COREG_ATG_PROFILE_ID"));
			registrySummaryVO.setSubType(rs.getString("SEARCHED_REG_SUB_TYPE"));
			registrySummaryVO
					.setRegistrantEmail(rs.getString("REG_EMAIL_ADDR"));
			registrySummaryVO.setCoRegistrantEmail(rs
					.getString("COREG_EMAIL_ADDR"));
			lRegistrySummaryVOs.add(registrySummaryVO);
		}
		rs.close();
		return lRegistrySummaryVOs;
	}

	/**
	 * This method sets the filter options if any
	 * 
	 * @param eventType
	 * @param state
	 * @return String
	 */
	public String getFilterOptions(String eventType, String state) {
		String filterOptions = null;
		if (BBBUtility.isEmpty(eventType) && BBBUtility.isEmpty(state)) {
			filterOptions = "state:All;eventType:All";
		} else if (BBBUtility.isEmpty(eventType) && !BBBUtility.isEmpty(state)) {
			filterOptions = "state:" + state + ";eventType:All";
		} else if (!BBBUtility.isEmpty(eventType) && BBBUtility.isEmpty(state)) {
			filterOptions = "state:All;eventType:" + eventType;
		} else if (!BBBUtility.isEmpty(eventType) && !BBBUtility.isEmpty(state)) {
			filterOptions = "state:" + state + ";eventType:"+eventType;
		}
		return filterOptions;
	}

	/**
	 * This method is used to associate registries with a user by adding or
	 * updating the registry.
	 * 
	 * @param registryVO
	 *            the registry vo
	 * @param pOwnerProfileItem
	 *            the owner profile item
	 * @param pCoRegProfileItem
	 *            the co reg profile item
	 * @return the mutable repository item
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */

	/**
	 * This method is used to associate registries with a user by adding or
	 * updating the registry.
	 * 
	 * @param registryVO
	 *            the registry vo
	 * @param pOwnerProfileItem
	 *            the owner profile item
	 * @param pCoRegProfileItem
	 *            the co reg profile item
	 * @return the mutable repository item
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public MutableRepositoryItem addORUpdateRegistry(
			final RegistryVO registryVO,
			final MutableRepositoryItem pOwnerProfileItem,
			final MutableRepositoryItem pCoRegProfileItem)
			throws BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.addORUpdateRegistry()");
		this.logDebug("GiftRegistryTools.addORUpdateRegistry() method start");
		this.logDebug("Method Description : associate registries with a user by adding or updating the registry repository");
		MutableRepositoryItem giftRegistryItem = null;
		final MutableRepository giftRegistryRepository = this
				.getGiftRepository();

		this.logDebug("pOwnerProfileItem :" + pOwnerProfileItem);
		this.logDebug("pCoRegProfileItem :" + pCoRegProfileItem);

		try {

			RepositoryItem[] grRepositoryItems = null;

			// Check if item already exist or not
			try {
				grRepositoryItems = this.fetchGiftRepositoryItem(
						registryVO.getSiteId(), registryVO.getRegistryId());
			} catch (final BBBBusinessException e) {
				this.logError(
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10130
								+ " BBBBusinessException from addORUpdateRegistry of GiftRegistryTools",
						e);
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						e);
			}
			if (null == grRepositoryItems) {
				this.logDebug("Create the registry entry in gift registry repository");
				/* Create the registry entry in gift registry repository */
				giftRegistryItem = this.getGiftRegistryItem(registryVO,
						pOwnerProfileItem, pCoRegProfileItem);
				giftRegistryRepository.addItem(giftRegistryItem);
			} else {

				giftRegistryItem = (MutableRepositoryItem) grRepositoryItems[0];
				this.logDebug("update existing registry Item"
						+ giftRegistryItem);
				this.logDebug("eventDate :"
						+ registryVO.getEvent().getEventDate());
				this.logDebug("registryOwner :"
						+ giftRegistryItem.getPropertyValue("registryOwner"));
				this.logDebug("registryCoOwner :"
						+ giftRegistryItem.getPropertyValue("registryCoOwner"));

				giftRegistryItem.setPropertyValue("regBG",
						registryVO.getRegBG());
				giftRegistryItem.setPropertyValue("coRegBG",
						registryVO.getCoRegBG());
				giftRegistryItem.setPropertyValue("eventDate", this
						.stringToDateConverter(registryVO.getEvent()
								.getEventDate(), registryVO.getSiteId()));
				giftRegistryItem.setPropertyValue("eventType", registryVO
						.getRegistryType().getRegistryTypeName());
				// if registryOwner was missing in the item then set new value
				// if (giftRegistryItem.getPropertyValue("registryOwner") ==
				// null) {
				if (pOwnerProfileItem != null) {
					giftRegistryItem.setPropertyValue("registryOwner",
							pOwnerProfileItem);
				}

				// if registryCoOwner was missing in the item then set new value
				// if (giftRegistryItem.getPropertyValue("registryCoOwner") ==
				// null) {
				if (pCoRegProfileItem != null) {
					giftRegistryItem.setPropertyValue("registryCoOwner",
							pCoRegProfileItem);
				}

				giftRegistryRepository.updateItem(giftRegistryItem);
			}

		} catch (final RepositoryException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131
							+ " RepositoryException from addORUpdateRegistry of GiftRegistryTools",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		} finally{
			BBBPerformanceMonitor.end("GiftRegistryTools.addORUpdateRegistry()");
			this.logDebug("GiftRegistryTools.addORUpdateRegistry() method ends");
		}
		return giftRegistryItem;
	}

	public boolean canScheduleAppointmentForRegType(String siteId,
			String registryType) {

		this.logDebug("GiftRegistryTools.canScheduleAppointment() method started");
		String configValue = null;
		boolean acceptsAppointment = false;
		try {
			/***
			 * BBB-99 | BBBSystemException | For TBS, skedgeMe config type does not
			 * contain any config value. This method is invoked from TBS Canada
			 * Store locator & Registry Owner page. We should return FALSE as
			 * default and not to throw exception.
			 */
			configValue = this.getCatalogTools().getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE);
			if (!BBBUtility.isEmpty(configValue) && !Boolean.parseBoolean(configValue)) {
				return acceptsAppointment;
			}
			final List<String> siteAppTypesConfigurations = this
					.getCatalogTools()
					.getAllValuesForKey(
							BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE,
							BBBGiftRegistryConstants.APPOINTMENT_TYPES_CONFIG_VALUE);
			if (BBBUtility.isListEmpty(siteAppTypesConfigurations)
					|| StringUtils.isEmpty(siteAppTypesConfigurations.get(0))) {
				return acceptsAppointment;
			}
			StringTokenizer siteAppTypeTokens = new StringTokenizer(
					siteAppTypesConfigurations.get(0), ",");
			while (siteAppTypeTokens.hasMoreTokens()) {
				if (registryType.equals(siteAppTypeTokens.nextElement())) {
					acceptsAppointment = true;
					break;
				}
			}
		} catch (final BBBSystemException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131
							+ " BBBSystemException from canScheduleAppointment of GiftRegistryManager",
					e);
		} catch (final BBBBusinessException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131
							+ " BBBBusinessException from canScheduleAppointment of GiftRegistryManager",
					e);
		}
		this.logDebug("GiftRegistryTools.canScheduleAppointment() method end");
		return acceptsAppointment;
	}

	public boolean canScheduleAppointmentForStore(String storeId,
			String registryType) {

		this.logDebug("GiftRegistryTools.canScheduleAppointmentForStore() method started");
		boolean isUserAllowedToScheduleAStoreAppointment = false;
		if (!StringUtils.isEmpty(storeId)) {

			StoreVO storeVO = getCatalogTools().getStoreAppointmentDetails(
					storeId);
			if (storeVO != null && storeVO.isAcceptingAppointments()
					&& storeVO.getRegAppointmentTypes() != null) {
				isUserAllowedToScheduleAStoreAppointment = storeVO
						.getRegAppointmentTypes().contains(registryType);
				logDebug("User is allowed to Schedule appointment for Current site with registry Type"
						+ registryType);
			}
		}
		this.logDebug("GiftRegistryTools.canScheduleAppointmentForStore() method Ended");
		return isUserAllowedToScheduleAStoreAppointment;
	}

	/**
	 * This method is used to fetch gift registry ingo from gift registry
	 * repository based on registryId.
	 * 
	 * @param siteId
	 *            the site id
	 * @param registryId
	 *            the registry id
	 * @return the repository item[]
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RepositoryItem[] fetchGiftRepositoryItem(final String siteId,
			final String registryId) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("GiftRegistryTools.fetchGiftRepositoryItem() method start");
		this.logDebug("Method Description : fetch gift registry ingo from gift registry repository based on registryId and return repository item");
		BBBPerformanceMonitor.start("GiftRegistryTools.fetchGiftRepositoryItem()");
		RepositoryItem[] giftRegistryRepoItem = null;
		if (!BBBUtility.isEmpty(registryId) && !BBBUtility.isEmpty(siteId) ) {
			final Object[] params = new Object[1];
			params[0] = registryId;

			giftRegistryRepoItem = this.executeRQLQuery(
					this.getGiftRepoItemQuery(), params, "giftregistry",
					this.getGiftRepository());
		}
		this.logDebug("GiftRegistryTools.fetchGiftRepositoryItem() method ends");
		BBBPerformanceMonitor.end("GiftRegistryTools.fetchGiftRepositoryItem()");
		return giftRegistryRepoItem;
	}

	/**
	 * This method is used to update token if registryId is present in
	 * RegistryRecommendations item descriptor or add the registryID and token
	 * 
	 * @param registryId
	 *            the registry id
	 * @return the repository item[]
	 * @throws BBBSystemException
	 *             the BBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */

	@SuppressWarnings({ "unchecked" })
	public Map<String, RepositoryItem> persistRecommendationToken(
			RegistryVO registryVO, Set<String> emailList)
			throws BBBSystemException {
		this.logDebug("GiftRegistryTools.persistRecommendationToken() method start");
		this.logDebug("Method Description : update token if registryId is present in RegistryRecommendations item descriptor or add the registryID and token, returns token map");
		BBBPerformanceMonitor.start("GiftRegistryTools.persistRecommendationToken()");
		Map<String, RepositoryItem> generatedTokens = new HashMap<String, RepositoryItem>();
		String registryId = registryVO.getRegistryId();
		this.logDebug("regisrtyId of Recommender :" + registryId);
		this.logDebug("List of emails for which recommendations to be sent in method emailRegistryRecommendation:"
				+ emailList);
		final Map<String, RepositoryItem> tokenCreationMap = new HashMap<String, RepositoryItem>();
		try {
			RepositoryItem[] giftRegRecommendRepositoryItems = null;
			MutableRepositoryItem recommendationRegistryItem = null;
			final MutableRepository giftRegistryRepository = this
					.getGiftRepository();
			// Map to hold token value and creation date corresponding to email
			// id entered by user Map<tokenid,creationDate>

			for (String email : emailList) {
				// DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				SecureRandom prng = SecureRandom
						.getInstance(BBBGiftRegistryConstants.SECURE_RANDOM);
				String randomNum = Integer.valueOf(prng.nextInt()).toString();
				this.logDebug("Unique TokenId generated for emailId : " + email
						+ "is :" + randomNum);
				RepositoryItem invitee = this.createInvitee(email);
				tokenCreationMap.put(randomNum, invitee);
			}

			// Check if regisrty id is present in RegistryRecommendations item
			// descriptor already exist or not
			try {
				giftRegRecommendRepositoryItems = this
						.fetchRegistryRecommendationItem(registryId);
			} catch (final BBBBusinessException e) {
				this.logError(
						BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR
								+ " BBBBusinessException from persistRecommendationToken of GiftRegistryTools",
						e);
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						e);
			}

			if (null == giftRegRecommendRepositoryItems) {
				this.logDebug("storing Recommendation token for registry id"
						+ registryId + "for the First time");
				recommendationRegistryItem = this.createRegRecommItem(
						registryVO, tokenCreationMap);
				this.logDebug("Adding recommendationRegistryItem in repository ");
				giftRegistryRepository.addItem(recommendationRegistryItem);
			} else {
				// adding more token to the existing registry present in
				// RegistryRecommendations Repository

				recommendationRegistryItem = (MutableRepositoryItem) giftRegRecommendRepositoryItems[0];

				generatedTokens = (Map<String, RepositoryItem>) recommendationRegistryItem
						.getPropertyValue(BBBCatalogConstants.INVITEES);
				generatedTokens.putAll(tokenCreationMap);
				this.logDebug("update existing registry id in RegistryRecommendations ItemDescriptor"
						+ registryId);
				recommendationRegistryItem.setPropertyValue(
						BBBCatalogConstants.REGISTRY_ID, registryId);
				recommendationRegistryItem.setPropertyValue(
						BBBCatalogConstants.INVITEES, generatedTokens);
				giftRegistryRepository.updateItem(recommendationRegistryItem);

			}
		} catch (final RepositoryException e) {
			this.logError(BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR
					+ " Error while adding token in repository ", e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		} catch (NoSuchAlgorithmException e) {
			this.logError(BBBCoreErrorConstants.ERROR_GENERATING_TOKEN
					+ "Error while generating unique token", e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}finally{
			this.logDebug("GiftRegistryTools.persistRecommendationToken() method ends.");
			BBBPerformanceMonitor.end("GiftRegistryTools.persistRecommendationToken()");
		}
		return tokenCreationMap;
	}

	public RepositoryItem[] fetchRegistryRecommendationItem(
			final String registryId) throws BBBSystemException,
			BBBBusinessException {
		BBBPerformanceMonitor.start("GiftRegistryTools.fetchRegistryRecommendationItem()");
		this.logDebug("GiftRegistryTools.fetchRegistryRecommendationItem() method start");
		this.logDebug("Method Description : to check whether registry id is present in recommnedation item, returns repositroyItem");
		RepositoryItem[] recommendRegistryRepoItem = null;
		if (!BBBUtility.isEmpty(registryId)) {
			final Object[] params = new Object[1];
			params[0] = registryId;

			recommendRegistryRepoItem = this.executeRQLQuery(
					this.getGiftRepoItemQuery(), params,
					"RegistryRecommendations", this.getGiftRepository());
		}
		this.logDebug("GiftRegistryTools.fetchRegistryRecommendationItem() method ends");
		BBBPerformanceMonitor.end("GiftRegistryTools.fetchRegistryRecommendationItem()");
		return recommendRegistryRepoItem;
	}

	/**
	 * This method is used to execute RQL query.
	 * 
	 * @param rqlQuery
	 *            the rql query
	 * @param params
	 *            the params
	 * @param viewName
	 *            the view name
	 * @param repository
	 *            the repository
	 * @return the repository item[]
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RepositoryItem[] executeRQLQuery(final String rqlQuery,
			final Object[] params, final String viewName,
			final MutableRepository repository) throws BBBSystemException,
			BBBBusinessException {

		RqlStatement statement = null;
		RepositoryItem[] queryResult = null;
		this.logDebug("GiftRegistryTools.executeRQLQuery() method start");

		if (rqlQuery != null) {
			if (repository != null) {
				try {
					statement = RqlStatement.parseRqlStatement(rqlQuery);
					final RepositoryView view = repository.getView(viewName);
					if (view == null) {
						this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10132
								+ viewName
								+ " view is null from executeRQLQuery of GiftRegistryTools");
					}
					queryResult = extractDBCall(params, statement, view);

					if (queryResult == null) {
						this.logDebug("No results returned for query ["
								+ rqlQuery + "]");
					}

				} catch (final RepositoryException e) {
					this.logError(
							BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10133
									+ " Repository Exception [Unable to retrieve data] from executeRQLQuery of GiftRegistryTools",
							e);
					throw new BBBSystemException(
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
							e);
				}
			} else {
				this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10134
						+ " Repository is null from executeRQLQuery of GiftRegistryTools");
			}
		} else {
			this.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10135
					+ " Query String is null from executeRQLQuery of GiftRegistryTools");
		}

		this.logDebug("GiftRegistryTools.executeRQLQuery() method ends");
		return queryResult;
	}

	/**
	 * @param params
	 * @param statement
	 * @param view
	 * @return
	 * @throws RepositoryException
	 */
	protected RepositoryItem[] extractDBCall(final Object[] params, RqlStatement statement, final RepositoryView view)
			throws RepositoryException {
		return statement.executeQuery(view, params);
	}

	/**
	 * This method is used to create giftregistryItem type with new parameter
	 * CoRegistrant profile.
	 * 
	 * @param registryVO
	 *            the registry vo
	 * @param pOwnerProfileItem
	 *            the owner profile item
	 * @param pCoRegProfileItem
	 *            the co reg profile item
	 * @return the gift registry item
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @author
	 */
	public MutableRepositoryItem getGiftRegistryItem(
			final RegistryVO registryVO,
			final RepositoryItem pOwnerProfileItem,
			final RepositoryItem pCoRegProfileItem) throws BBBSystemException {

		MutableRepositoryItem giftRegistryItem = null;
		final MutableRepository giftRegistryRepository = this
				.getGiftRepository();
		this.logDebug("GiftRegistryTools.getGiftRegistryItem() method start");
		final String cookieType = registryVO.getCookieType();
		String affiliateTag = registryVO.getAffiliateTag();
		try {
			giftRegistryItem = giftRegistryRepository
					.createItem("giftregistry");
			giftRegistryItem.setPropertyValue("regBG", registryVO.getRegBG());
			giftRegistryItem.setPropertyValue("coRegBG",
					registryVO.getCoRegBG());
			giftRegistryItem.setPropertyValue("registryId",
					registryVO.getRegistryId());
			giftRegistryItem.setPropertyValue("registryStatus",
					registryVO.getStatus());
			giftRegistryItem.setPropertyValue("eventType", registryVO
					.getRegistryType().getRegistryTypeName());
			if (null != registryVO.getEvent()
					&& !StringUtils.isEmpty(registryVO.getEvent().getEventDate()) ) {
				giftRegistryItem.setPropertyValue("eventDate", this
						.stringToDateConverter(registryVO.getEvent()
								.getEventDate(), registryVO.getSiteId()));
			}
			giftRegistryItem.setPropertyValue("registryOwner",
					pOwnerProfileItem);
			if (null != pCoRegProfileItem) {
				giftRegistryItem.setPropertyValue("registryCoOwner",
						pCoRegProfileItem);
			}
			giftRegistryItem.setPropertyValue("sessionId", "0");
			if ((cookieType != null)
					&& cookieType
							.equalsIgnoreCase(BBBCoreConstants.WED_CHANNEL_REF)) {
				affiliateTag = BBBGiftRegistryConstants.WEDDING_CHANNEL_VALUE;
			} else if ((cookieType != null)
					&& cookieType
							.equalsIgnoreCase(BBBCoreConstants.THEBUMP_REF)) {
				affiliateTag = BBBGiftRegistryConstants.BUMP_VALUE;
			}
			giftRegistryItem.setPropertyValue("affiliateTag", affiliateTag);
		} catch (final RepositoryException e) {
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}

		this.logDebug("GiftRegistryTools.getGiftRegistryItem() method ends");
		return giftRegistryItem;
	}

	/**
	 * This method is used to update registryId and token in repository if
	 * registry id is not present
	 * 
	 * @param registryId
	 *            the registryId
	 * @param tokenCreationMap
	 *            UidToken Map generated for emailId List
	 * @return the registry recommendation item
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @author
	 */

	public MutableRepositoryItem createRegRecommItem(RegistryVO registryVO,
			Map<String, RepositoryItem> tokenCreationMap)
			throws BBBSystemException {

		MutableRepositoryItem recommendationRegistryItem = null;
		final MutableRepository giftRegistryRepository = this
				.getGiftRepository();
		this.logDebug("GiftRegistryTools.createRegRecommItem() method start");

		try {
			// BPS-1112 Persist minimal details for registry | Social
			// recommendation surge
			if (BBBCoreConstants.SITE_BAB_CA.equalsIgnoreCase(registryVO
					.getSiteId())) {
				registryVO.setSiteId("3");
			}
			recommendationRegistryItem = giftRegistryRepository
					.createItem(BBBCatalogConstants.REGISTRY_RECOMMENDATIONS);
			recommendationRegistryItem
					.setPropertyValue(BBBCatalogConstants.REGISTRY_ID,
							registryVO.getRegistryId());
			recommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.EVENT_TYPE, registryVO
							.getRegistryType().getRegistryTypeName());
			recommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.EVENT_DATE,
					stringToDateConverter(registryVO.getEvent().getEventDate(),
							registryVO.getSiteId()));
			recommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.REGISTRANT_NAME, registryVO
							.getPrimaryRegistrant().getFirstName());
			recommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.INVITEES, tokenCreationMap);
			recommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.SITE_ID, extractSite().getId());

		} catch (final RepositoryException e) {
			logError(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION
					+ " inside createRegRecommItem method " + e.getMessage());
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}

		this.logDebug("GiftRegistryTools.createRegRecommItem() method ends");
		return recommendationRegistryItem;
	}

	/**
	 * @return
	 */
	protected Site extractSite() {
		return SiteContextManager
				.getCurrentSite();
	}

	/**
	 * This method is used to create invitees.
	 * 
	 * @param emailId
	 *            the emailId
	 * @return RepositoryItem the registry recommendtaion item
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @author
	 */

	public RepositoryItem createInvitee(String emailId)
			throws BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.createInvitee()");
		this.logDebug("GiftRegistryTools.createInvitee() method start");
		this.logDebug("Method Description : method creates/adds invitees, return repositoryItem");
		MutableRepositoryItem reommendationRegistryItem = null;
		final MutableRepository giftRegistryRepository = this
				.getGiftRepository();
		
		try {
			reommendationRegistryItem = giftRegistryRepository
					.createItem(BBBCatalogConstants.INVITEE);
			reommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.INVITEE_EMAILI_ID, emailId);
			reommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.TOKEN_CREATION_DATE, new Date());
			reommendationRegistryItem.setPropertyValue(
					BBBCatalogConstants.TOKEN_STATUS, 1);
			giftRegistryRepository.addItem(reommendationRegistryItem);

		} catch (final RepositoryException e) {
			logError(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION
					+ " inside createInvitee method " + e.getMessage());
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}finally{
			BBBPerformanceMonitor.end("GiftRegistryTools.createInvitee()");
			this.logDebug("GiftRegistryTools.createInvitee() method ends");
		}
		return reommendationRegistryItem;
	}

	/**
	 * This method is used to convert date in String format to date format.
	 * 
	 * @param pDate
	 *            the date
	 * @return the date
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	private Date stringToDateConverter(final String pDate, final String siteId)
			throws BBBSystemException {
		this.logDebug("GiftRegistryTools.StringToDateConverter() method start");

		SimpleDateFormat dateFormat = null;
		Date date = null;
		
		if(BBBUtility.isEmpty(pDate) || "0".equalsIgnoreCase(pDate) || siteId == null){
			   return null;
		}

		if (siteId.equalsIgnoreCase(BBBGiftRegistryConstants.CANADA_SITE_ID)) {
			dateFormat = new SimpleDateFormat(
					BBBGiftRegistryConstants.DATE_FORMAT_CANADA);
		} else {
			dateFormat = new SimpleDateFormat(
					BBBGiftRegistryConstants.DATE_FORMAT_DB);
		}

		try {
			date = dateFormat.parse(pDate);

		} catch (final ParseException e1) {
			throw new BBBSystemException(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10166,
					e1.getMessage(), e1);
		}

		this.logDebug("GiftRegistryTools.StringToDateConverter() method ends");
		return date;
	}

	/**
	 * get future registry list for the user.
	 * 
	 * @param profile
	 *            the profile
	 * @param siteId
	 *            the site id
	 * @return List<RegistrySkinnyVO>
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 */
	public List<RegistrySkinnyVO> getFutureRegistryList(final Profile profile,
			final String siteId) throws BBBBusinessException,
			BBBSystemException, RepositoryException {
		BBBPerformanceMonitor.start("GiftRegistryTools.getFutureRegistryList()");
		this.logDebug("GiftRegistryTools.getFutureRegistryList() method start");
		this.logDebug("Method Description : get future registry list for the user, using profile & site Id as parameter and returns list of RegistrySkinnyVO");
		final List<RegistrySkinnyVO> registrySkinnyVOList = new ArrayList<RegistrySkinnyVO>();
		this.logDebug("Get registy from the user profile");
		List<String> pListUserRegIds = new ArrayList<String>();
		RepositoryItem[] registryIdRepItems;
		registryIdRepItems = this.fetchUserRegistries(siteId,
				profile.getRepositoryId(), true);
		if (registryIdRepItems != null) {
			pListUserRegIds = new ArrayList<String>(registryIdRepItems.length);
			for (final RepositoryItem registryIdRepItem : registryIdRepItems) {
				pListUserRegIds.add(registryIdRepItem.getRepositoryId());
			}

		}

		try {
			if (!pListUserRegIds.isEmpty()) {
				for (final String iterateRegistry : pListUserRegIds) {
					final RegistrySkinnyVO skinnyVO = new RegistrySkinnyVO();

					final RepositoryItem repositoryItem = this
							.getGiftRepository().getItem(iterateRegistry,
									"giftregistry");

					final Date eventDate = (Date) repositoryItem
							.getPropertyValue("eventDate");
					final String eventType = (String) repositoryItem
							.getPropertyValue("eventType");
					skinnyVO.setEventDate(dateToStringConverter(eventDate, null));

					skinnyVO.setEventCode(eventType);

					skinnyVO.setEventType(this.getCatalogTools()
							.getRegistryTypeName(eventType, siteId));

					skinnyVO.setRegistryId(iterateRegistry);

					final String registryStatus = (String) repositoryItem
							.getPropertyValue("registryStatus");
					skinnyVO.setStatus(registryStatus);

					if (isValidDate(dateToStringConverter(eventDate, null),
							false, BBBCoreConstants.DATE_FORMAT)) {
						registrySkinnyVOList.add(skinnyVO);
					}
				}
			}
		} catch (final RepositoryException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10136
							+ " RepositoryException of getFutureRegistryList from GiftRegistryTools",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}finally{
			this.logDebug("GiftRegistryTools.getFutureRegistryList() method ends");
			BBBPerformanceMonitor.end("GiftRegistryTools.getFutureRegistryList()");
		}
		return registrySkinnyVOList;

	}

	/**
	 * get users all registry list for the user.
	 * 
	 * @param profile
	 *            the profile
	 * @param siteId
	 *            the site id
	 * @return List<RegistrySkinnyVO>
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws RepositoryException
	 */
	public List<RegistrySkinnyVO> getUserRegistryList(final Profile profile,
			final String siteId) throws BBBBusinessException,
			BBBSystemException, RepositoryException {
		BBBPerformanceMonitor.start("GiftRegistryTools.getUserRegistryList()");
		this.logDebug("GiftRegistryTools.getUserRegistryList() method start");
		this.logDebug("Method Description : get all registry list for the user, using profile & siteId as parameter and returns list of RegistrySkinnyVO");
		final List<RegistrySkinnyVO> registrySkinnyVOList = new ArrayList<RegistrySkinnyVO>();
		final List<RegistrySkinnyVO> regListWithEventDate = new ArrayList<RegistrySkinnyVO>();
		final List<RegistrySkinnyVO> regListWithoutEventDate = new ArrayList<RegistrySkinnyVO>();
		this.logDebug("Get registy from the user profile");
		List<String> pListUserRegIds = new ArrayList<String>();
		String dateFormat = BBBCoreConstants.DATE_FORMAT;

		if ((siteId != null) && this.isCanadaSite(siteId)) {
			dateFormat = BBBCoreConstants.CA_DATE_FORMAT;
		}
		try {
			RepositoryItem[] registryIdRepItems;

			registryIdRepItems = this.fetchUserRegistries(siteId,
					profile.getRepositoryId(), true);

			if (registryIdRepItems != null) {
				pListUserRegIds = new ArrayList<String>(
						registryIdRepItems.length);
				for (final RepositoryItem registryIdRepItem : registryIdRepItems) {
					final RegistrySkinnyVO skinnyVO = new RegistrySkinnyVO();
					try{
						RegistryResVO resVo = this
								.getRegistryInfoFromEcomAdmin(
										registryIdRepItem.getRepositoryId(),
										this.getCatalogTools()
												.getAllValuesForKey(
														BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,
														siteId).get(0));

						// PS-62346 - PDP Mobile for recognized user throwing 500 errors
						/***
						 * If recognized user has inactive registry in account
						 * and browsed to PDP, it throws NPE as ECOM DB returns
						 * No Registry Found.
						 */
						if(resVo == null || resVo.getRegistrySummaryVO() == null) {
							this.logError("ALERT: Registry Sync Issue - Inactive/Non-Exist RegsitryID in ECOM Database for recognized USER profile: "
									+ profile);
							continue;
						}

						final AddressVO shippingAddress= resVo.getRegistrySummaryVO().getShippingAddress();
						boolean isPOBoxAddress=BBBUtility.isPOBoxAddress(shippingAddress.getAddressLine1(), shippingAddress.getAddressLine2());
						if (BBBUtility.isNotEmpty(resVo.getRegistryVO()
								.getPrimaryRegistrant().getCellPhone())) {
							skinnyVO.setAlternatePhone(resVo.getRegistryVO()
									.getPrimaryRegistrant().getCellPhone());
						}
						final Date eventDate = (Date) registryIdRepItem
								.getPropertyValue("eventDate");
						final String eventType = (String) registryIdRepItem
								.getPropertyValue("eventType");
						
						skinnyVO.setPoBoxAddress(isPOBoxAddress);
						skinnyVO.setEventCode(eventType);

						skinnyVO.setEventType(this.getCatalogTools()
								.getRegistryTypeName(eventType, siteId));

						skinnyVO.setRegistryId(registryIdRepItem.getRepositoryId());
						skinnyVO.setStatus((String) registryIdRepItem
								.getPropertyValue("registryStatus"));
						
						skinnyVO.setPrimaryRegistrantFirstName(resVo.getRegistrySummaryVO().getPrimaryRegistrantFirstName());
						skinnyVO.setCoRegistrantFirstName(resVo.getRegistrySummaryVO().getCoRegistrantFirstName());

						if (eventDate!=null && isValidDate(
								dateToStringConverter(eventDate, dateFormat), true,
								dateFormat)) {							
							skinnyVO.setEventDate(dateToStringConverter(eventDate,
									dateFormat));
							regListWithEventDate.add(skinnyVO);							
						}else{
							skinnyVO.setEventDate(null);
							regListWithoutEventDate.add(skinnyVO);
						}
						pListUserRegIds.add(registryIdRepItem.getRepositoryId());
					}catch (final BBBSystemException e){
						this.logError("RegsitryID : " + registryIdRepItem.getRepositoryId() + " doesn't exists in Database.");
					}
				}
				registrySkinnyVOList.addAll(regListWithEventDate);
				registrySkinnyVOList.addAll(0, regListWithoutEventDate);
				
			}
		} catch (final RepositoryException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10137
							+ " RepositoryException of getUserRegistryList from GiftRegistryTools",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}finally{
			this.logDebug("GiftRegistryTools.getUserRegistryList() method ends");
			BBBPerformanceMonitor.end("GiftRegistryTools.getUserRegistryList()");
		}

		final DynamoHttpServletRequest pRequest = ServletUtil
				.getCurrentRequest();
		final BBBSessionBean sessionBean = (BBBSessionBean) pRequest
				.resolveName(SESSION_BEAN);
		sessionBean.getValues().put(
				BBBGiftRegistryConstants.USER_REGISTRIES_LIST, pListUserRegIds);
		sessionBean.getValues().put("registrySkinnyVOList",
				registrySkinnyVOList);
		return registrySkinnyVOList;

	}

	/**
	 * Check if the site is Canada.
	 * 
	 * @param siteId
	 *            the site id
	 * @return true, if is canada site
	 */
	public boolean isCanadaSite(final String siteId) {
		this.logDebug("GiftRegistryFormHandler.isCanadaSite() method starts");

		boolean isCanadaSite = false;
		String canadaSiteCode = null;

		try {
			canadaSiteCode = this.getCatalogTools()
					.getConfigValueByconfigType("ContentCatalogKeys")
					.get("BedBathCanadaSiteCode");
		} catch (final BBBSystemException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10138
							+ " BBBSystemException [Catalog Exception] from isCanadaSite of GiftRegistryTools",
					e);
			this.logError(
					"CLS[GiftRegistryFormHandler]/MSG=[Catalog Exception]"
							+ e.getMessage(), e);

		} catch (final BBBBusinessException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10139
							+ " BBBBusinessException [Catalog Exception] from isCanadaSite of GiftRegistryTools",
					e);
		}

		if ((siteId != null) && siteId.equalsIgnoreCase(canadaSiteCode)) {
			isCanadaSite = true;
		}
		this.logDebug("GiftRegistryFormHandler.isCanadaSite() method ends");

		return isCanadaSite;
	}

	/**
	 * 
	 * @param siteId
	 * @param profileId
	 * @return
	 * @throws RepositoryException
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public RepositoryItem[] fetchUserRegistries(final String siteId,
			final String profileId, boolean activeOnly) throws RepositoryException,
			BBBSystemException, BBBBusinessException {
		BBBPerformanceMonitor.start("GiftRegistryTools.fetchUserRegistries()");
		this.logDebug("GiftRegistryTools.fetchUserRegistries() method starts");
		this.logDebug("Method Description : get userRegistryItem for the user, using profile & siteId as parameter and returns list of RepositoryItem");
		final List<RegistryTypeVO> registryTypes = this.getCatalogTools()
				.getRegistryTypes(siteId);
		String rqlQuery = this.getUserRegistryQuery();

		String subQuery = "";
		for (final RegistryTypeVO registryTypeVO : registryTypes) {

			final String code = registryTypeVO.getRegistryCode();

			if (subQuery.trim().length() > 0) {
				subQuery += " or ";
			}
			subQuery += " eventType=\"" + code + "\"";
		}
		rqlQuery += "(" + subQuery + ")";
		if (activeOnly) {
			
			List <String> acceptableStatusesList = this.getCatalogTools().getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
					BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY);
			List<String> acceptableRegistryStatuses = new ArrayList<String>();
			String acceptableStatuses = null;
			if (!BBBUtility.isListEmpty(acceptableStatusesList)) {
				acceptableStatuses = acceptableStatusesList.get(0);
			}
			String subQueryForStatus = "";
			if(!BBBUtility.isEmpty(acceptableStatuses)) {
				final String[] statusesArray = acceptableStatuses.split(BBBCoreConstants.COMMA);
				acceptableRegistryStatuses.addAll(Arrays.asList(statusesArray));
			}
			if (!BBBUtility.isListEmpty(acceptableRegistryStatuses)) {
				int index = 0;
				for (String registryStatus : acceptableRegistryStatuses) {
					if (index != 0) {
						subQueryForStatus += " or ";
					}
					subQueryForStatus += " registryStatus=\"" + registryStatus + "\"";
					index++;
				}
				rqlQuery += " and (" + subQueryForStatus + ")";
			}
		}
		rqlQuery += " ORDER BY eventDate SORT ASC";

		this.logDebug("GiftRegistryTools.fetchUserRegistries() MSG= rqlQuery ::"
				+ rqlQuery);

		final Object[] params = new Object[1];
		params[0] = profileId;

		final RepositoryItem[] userRegistryItems = this.executeRQLQuery(
				rqlQuery, params, "giftregistry", this.getGiftRepository());

		int totalRegistries = 0;
		if (userRegistryItems != null) {
			totalRegistries = userRegistryItems.length;
		}

		this.logDebug("GiftRegistryTools.fetchUserRegistries() MSG= userRegistryItems: "
				+ Arrays.toString(userRegistryItems)
				+ " count: "
				+ totalRegistries);

		this.logDebug("GiftRegistryTools.fetchUserRegistries() method ends");
		BBBPerformanceMonitor.end("GiftRegistryTools.fetchUserRegistries()");

		return userRegistryItems;

	}

	/**
	 * validate date.
	 * 
	 * @param dateStr
	 *            the date str
	 * @param allowPast
	 *            the allow past
	 * @param formatStr
	 *            the format str
	 * @return flag
	 */
	public static boolean isValidDate(final String dateStr,
			final boolean allowPast, final String formatStr) {

		if (formatStr == null) {
			return false;
		}
		final SimpleDateFormat df = new SimpleDateFormat(formatStr, ServletUtil
				.getCurrentRequest().getLocale());
		Date testDate = null;
		try {
			testDate = df.parse(dateStr);
		} catch (final ParseException e) {
			// invalid date format
			return false;
		}
		if (!allowPast) {
			final Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			if (cal.getTime().after(testDate)) {
				return false;
			}
		}

		if (!df.format(testDate).equals(dateStr)) {
			return false;
		}
		return true;
	}

	/**
	 * Populate personalized and ltl registry item.
	 *
	 * @param registryItem the registry item
	 * @param registryItemVO the registry item vo
	 * @param isGiftGiver the is gift giver
	 * @return the registry item vo
	 * @throws ParseException the parse exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private RegistryItemVO populatePersonalizedAndLtlRegistryItem(
			RepositoryItem registryItem, RegistryItemVO registryItemVO, boolean isGiftGiver)
			throws ParseException, BBBSystemException, BBBBusinessException {
		return populatePersonalizedAndLtlRegistryItem(registryItem, registryItemVO, isGiftGiver, false);
	}

	/**
	 * Populate personalized and ltl registry item.
	 *
	 * @param registryItem the registry item
	 * @param registryItemVO the registry item vo
	 * @param isGiftGiver the is gift giver
	 * @param isRLV the is rlv
	 * @return the registry item vo
	 * @throws ParseException the parse exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private RegistryItemVO populatePersonalizedAndLtlRegistryItem(
			RepositoryItem registryItem, RegistryItemVO registryItemVO, boolean isGiftGiver, boolean isRLV)
			throws ParseException, BBBSystemException, BBBBusinessException {

		this.logDebug("GiftRegistryTools.populatePersonalizedAndLtlRegistryItem() method starts");
		populateRegistryItem(registryItem, isRLV,  registryItemVO);
		//populateRegistryItem(registryItem, registryItemVO);
		Double customizedPrice = 0.0;
		Double personalizedPrice = 0.0;
		String refNum = BBBCoreConstants.BLANK;
		String personalizationCode = BBBCoreConstants.BLANK;
		String personalizationDescription = BBBCoreConstants.BLANK;
		String personalizedImageUrls = BBBCoreConstants.BLANK;
		String imageUrlThumb = BBBCoreConstants.BLANK;
		String mobImageUrl = BBBCoreConstants.BLANK;
		String mobImageUrlThumb = BBBCoreConstants.BLANK;
		String itemType = BBBCoreConstants.BLANK;
		String ltlDeliveryServices = BBBCoreConstants.BLANK;
		String assemblySelected = BBBCoreConstants.BLANK;
		Integer qtyRequested = 0;
		Integer qtyFulfilled = 0;
		Integer qtyWebPurchased = 0;
		Integer qtyPurchased = 0;
		Integer skuId = null;
		SKUDetailVO skuVO=null;
		String siteId = extractSiteId();
		if (registryItem.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) != null) {
			qtyRequested = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.QTY_REQUESTED);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) != null) {
			qtyFulfilled = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.QTY_FULFILLED);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) != null) {
			qtyWebPurchased = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED);
		}
		qtyPurchased = qtyFulfilled + qtyWebPurchased;

		if (registryItem
				.getPropertyValue(BBBCoreConstants.PERSONALIZATION_CODE) != null) {
			personalizationCode = (String) registryItem
					.getPropertyValue(BBBCoreConstants.PERSONALIZATION_CODE);
		}
		if (registryItem
				.getPropertyValue(BBBCoreConstants.PERSONALIZATION_DESC) != null) {
			personalizationDescription = (String) registryItem
					.getPropertyValue(BBBCoreConstants.PERSONALIZATION_DESC);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.IMAGE_URL) != null) {
			personalizedImageUrls = (String) registryItem
					.getPropertyValue(BBBCoreConstants.IMAGE_URL);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.IMAGE_URL_THUMB) != null) {
			imageUrlThumb = (String) registryItem
					.getPropertyValue(BBBCoreConstants.IMAGE_URL_THUMB);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL) != null) {
			mobImageUrl = (String) registryItem
					.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL_THUMB) != null) {
			mobImageUrlThumb = (String) registryItem
					.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL_THUMB);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.ITEM_TYPE) != null) {
			itemType = (String) registryItem
					.getPropertyValue(BBBCoreConstants.ITEM_TYPE);
		}
		if (itemType != null
				&& (BBBCoreConstants.PER.equalsIgnoreCase(itemType))
				&& registryItem.getPropertyValue(BBBCoreConstants.REFNUM) != null) {
			refNum = (String) registryItem
					.getPropertyValue(BBBCoreConstants.REFNUM);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.CUSTOMIZATION_PRICE) != null) {
			customizedPrice = (Double) registryItem
					.getPropertyValue(BBBCoreConstants.CUSTOMIZATION_PRICE);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.SKUID) != null) {
			skuId = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.SKUID);
			skuVO = this.getSKUDetailsWithProductId(
					siteId, skuId.toString(),
					registryItemVO);
		}
		if (registryItem
				.getPropertyValue(BBBCoreConstants.PERSONALIZATION_PRICE) != null) {
			personalizedPrice = (Double) registryItem
					.getPropertyValue(BBBCoreConstants.PERSONALIZATION_PRICE);
			
			
			//BBBH-5246 | Personalized price overridden wrt to the list/sale/incart price
			personalizedPrice = calculatePersonalizedPrice(isGiftGiver,
					customizedPrice, personalizedPrice, skuVO, siteId);
		}
		if (registryItem
				.getPropertyValue(BBBCoreConstants.LTL_DELIVERY_SERVICES) != null) {
			ltlDeliveryServices = (String) registryItem
					.getPropertyValue(BBBCoreConstants.LTL_DELIVERY_SERVICES);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.ASSEMBLY_SELECTED) != null) {
			assemblySelected = (String) registryItem
					.getPropertyValue(BBBCoreConstants.ASSEMBLY_SELECTED);
		}
		registryItemVO.setPersonlisedDoublePrice(personalizedPrice);
		registryItemVO.setPersonlisedPrice(BigDecimal
				.valueOf(personalizedPrice));
		registryItemVO.setCustomizedDoublePrice(customizedPrice);
		registryItemVO.setPersonalisedCode(personalizationCode);
		registryItemVO.setCustomizationDetails(personalizationDescription);
		registryItemVO.setRefNum(refNum);
		registryItemVO.setPersonalizedImageUrls(personalizedImageUrls);
		registryItemVO.setPersonalizedImageUrlThumbs(imageUrlThumb);
		registryItemVO.setPersonalizedMobImageUrls(mobImageUrl);
		registryItemVO.setPersonalizedMobImageUrlThumbs(mobImageUrlThumb);
		registryItemVO.setItemType(itemType);
		registryItemVO.setLtlDeliveryServices(ltlDeliveryServices);
		registryItemVO.setAssemblySelected(assemblySelected);
		registryItemVO.setQtyRequested(qtyRequested.intValue());
		registryItemVO.setQtyFulfilled(qtyFulfilled.intValue());
		registryItemVO.setQtyWebPurchased(qtyWebPurchased.intValue());
		registryItemVO.setQtyPurchased(qtyPurchased.intValue());
		this.logDebug("GiftRegistryTools.populatePersonalizedAndLtlRegistryItem() method ends");
		return registryItemVO;
	}

	/**
	 * @return
	 */
	protected String extractSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

	/** This method calculated personalized price
	 * @param isGiftGiver
	 * @param customizedPrice
	 * @param personalizedPrice
	 * @param skuVO
	 * @param siteId
	 * @return
	 * @throws BBBSystemException
	 */
	public Double calculatePersonalizedPrice(boolean isGiftGiver,
			Double customizedPrice, Double personalizedPrice,
			SKUDetailVO skuVO, String siteId) throws BBBSystemException {
		logDebug("GiftRegistryTools:calculatePersonalizedPrice starts:");
		Double basePrice = null;
		String personalizationType = null;
		if(skuVO != null){
			if(skuVO.isInCartFlag()){
				if(siteId.contains(BBBCoreConstants.TBS) || !isGiftGiver){
					basePrice = this.getCatalogTools().getIncartPrice(skuVO.getParentProdId(), skuVO.getSkuId());
				} else {
					basePrice = this.getCatalogTools().getSalePrice(skuVO.getParentProdId(), skuVO.getSkuId());
					if(Double.compare(basePrice, 0.00) == BBBCoreConstants.ZERO){
						basePrice = this.getCatalogTools().getListPrice(skuVO.getParentProdId(), skuVO.getSkuId());
					}
				}
			} else {
				basePrice = this.getCatalogTools().getSalePrice(skuVO.getParentProdId(), skuVO.getSkuId());
				if(Double.compare(basePrice, 0.00) == BBBCoreConstants.ZERO){
					basePrice = this.getCatalogTools().getListPrice(skuVO.getParentProdId(), skuVO.getSkuId());
				}
			}
		}
		if(null != skuVO){
			personalizationType = skuVO.getPersonalizationType();
		}
		if(!BBBUtility.isEmpty(personalizationType)){
			if(personalizationType.equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_PY)){
				personalizedPrice = basePrice + customizedPrice;
			} else if(personalizationType.equalsIgnoreCase(BBBCoreConstants.PERSONALIZATION_CODE_PB)){
				personalizedPrice = basePrice;
			} else {
				personalizedPrice = customizedPrice;
			}
		}
		if(null != skuVO){
			logDebug("personalizationType:"+ personalizationType +" skuId:"+ skuVO.getSkuId() +" basePrice:"+ basePrice +"customizedPrice"+ customizedPrice +"personalizedPrice"+personalizedPrice);
		}
		logDebug("GiftRegistryTools:calculatePersonalizedPrice ends!");
		return personalizedPrice;
	}

	/**
	 * Adds the item to gift registry.
	 * 
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return error
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public ValidateAddItemsResVO addItemToGiftRegistry(
			final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.addItemToGiftRegistry()");
		ValidateAddItemsResVO addItemsResVO = null;
		CallableStatement cs = null;

		this.logDebug("GiftRegistryTools.addItemToGiftRegistry() method start");
		this.logDebug("Method Description : Adds item to Gift Registry using GiftRegistryViewBean as parameter");
		Connection con = null;
		addItemsResVO = new ValidateAddItemsResVO();
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();

			if (con != null) {
				final String getRegInfostoredProc = BBBGiftRegistryConstants.ADD_ITEM_TO_REG2;
				String createProgram = getCreateProgram(pGiftRegistryViewBean
						.getSiteFlag());
				// Step-3: prepare the callable statement
				cs = con.prepareCall(getRegInfostoredProc);

				// Step-4: set input parameters ...
				// first input argument
				cs.setString(1, pGiftRegistryViewBean.getRegistryId());
				cs.setString(2, pGiftRegistryViewBean.getSku());
				cs.setInt(3,
						Integer.valueOf(pGiftRegistryViewBean.getQuantity()));
				cs.setString(4, pGiftRegistryViewBean.getRefNum());
				cs.setString(5, pGiftRegistryViewBean.getItemTypes());
				cs.setString(6, pGiftRegistryViewBean.getAssemblySelections());
				cs.setString(7, pGiftRegistryViewBean.getAssemblyPrices());
				cs.setString(8, pGiftRegistryViewBean.getLtlDeliveryServices());
				cs.setString(9, pGiftRegistryViewBean.getLtlDeliveryPrices());
				cs.setString(10, pGiftRegistryViewBean.getPersonlizationCodes());
				cs.setString(11,
						pGiftRegistryViewBean.getPersonalizationPrices());
				cs.setString(12, pGiftRegistryViewBean.getCustomizationPrices());
				cs.setString(13,
						pGiftRegistryViewBean.getPersonalizationDescrips());
				cs.setString(14,
						pGiftRegistryViewBean.getPersonalizedImageUrls());
				cs.setString(15,
						pGiftRegistryViewBean.getPersonalizedImageUrlThumbs());
				cs.setString(16,
						pGiftRegistryViewBean.getPersonalizedMobImageUrls());
				cs.setString(17, pGiftRegistryViewBean
						.getPersonalizedMobImageUrlThumbs());
				cs.setString(18, getGiftRegUtils().getJDADateTime());
				cs.setString(19, createProgram);
				cs.setLong(20, getStoreNum(pGiftRegistryViewBean.getSiteFlag()));
				cs.setString(21,
						getCreateProgram(pGiftRegistryViewBean.getSiteFlag()));
				cs.setString(22, this.getRowXngUser());
				cs.setString(23, pGiftRegistryViewBean.getSiteFlag());
				this.logDebug("GiftRegistryTools.addItemToGiftRegistry() | parameters passed into procedure are registryId : "
						+ pGiftRegistryViewBean.getRegistryId()
						+ "sku:"
						+ pGiftRegistryViewBean.getSku()
						+ "quantity:"
						+ pGiftRegistryViewBean.getQuantity()
						+ "itemType:"
						+ pGiftRegistryViewBean.getItemTypes()
						+ "refNum:"
						+ pGiftRegistryViewBean.getRefNum()
						+ "AssemblySelection"
						+ pGiftRegistryViewBean.getAssemblySelections()
						+ "AssemblyPrices:"
						+ pGiftRegistryViewBean.getAssemblyPrices()
						+ "LTLDeliveryServices:"
						+ pGiftRegistryViewBean.getLtlDeliveryServices()
						+ "LTLDeliveryPrices:"
						+ pGiftRegistryViewBean.getLtlDeliveryPrices()
						+ "PersonalizationCode:"
						+ pGiftRegistryViewBean.getPersonlizationCodes()
						+ "PersonalizationPrices:"
						+ pGiftRegistryViewBean.getPersonalizationPrices()
						+ "CustomizationPrices:"
						+ pGiftRegistryViewBean.getCustomizationPrices()
						+ "PersDescrip:"
						+ pGiftRegistryViewBean.getPersonalizationDescrips()
						+ "PersonalizedImageUrls:"
						+ pGiftRegistryViewBean.getPersonalizedImageUrls()
						+ "PersonalizedImageUrlThumbs:"
						+ pGiftRegistryViewBean.getPersonalizedImageUrlThumbs()
						+ "PersonalizedMobImageUrls"
						+ pGiftRegistryViewBean.getPersonalizedMobImageUrls()
						+ "PersonalizedMobImageUrlThumbs()"
						+ pGiftRegistryViewBean
								.getPersonalizedMobImageUrlThumbs());
				// Step-6: execute the stored procedures: getRegInfostoredProc
				cs.executeUpdate();
				this.logDebug("GiftRegistryTools.addItemToGiftRegistry() | procedure "
						+ getRegInfostoredProc + " executed from method ");
				addItemsResVO.getServiceErrorVO().setErrorExists(false);

			}
		} catch (SQLException ex) {
			if (ex.getMessage()!= null && ex.getMessage().contains(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
				logInfo("GiftRegistryTools.addItemToGiftRegistry(): " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
			} 
			ServiceErrorVO serviceError = (ServiceErrorVO) getGiftRegUtils()
					.logAndFormatError(
							"AddItemsToRegistry2",
							con,
							BBBGiftRegistryConstants.SERVICE_ERROR_VO,
							ex,
							pGiftRegistryViewBean.getRegistryId(),
							pGiftRegistryViewBean.getSku(),
							pGiftRegistryViewBean.getQuantity(),
							pGiftRegistryViewBean.getItemTypes(),
							pGiftRegistryViewBean.getRefNum(),
							pGiftRegistryViewBean.getAssemblySelections(),
							pGiftRegistryViewBean.getAssemblyPrices(),
							pGiftRegistryViewBean.getLtlDeliveryServices(),
							pGiftRegistryViewBean.getLtlDeliveryPrices(),
							pGiftRegistryViewBean.getPersonlizationCodes(),
							pGiftRegistryViewBean.getPersonalizationPrices(),
							pGiftRegistryViewBean.getCustomizationPrices(),
							pGiftRegistryViewBean.getPersonalizationDescrips(),
							pGiftRegistryViewBean.getPersonalizedImageUrls(),
							pGiftRegistryViewBean
									.getPersonalizedImageUrlThumbs(),
							pGiftRegistryViewBean.getPersonalizedMobImageUrls(),
							pGiftRegistryViewBean
									.getPersonalizedMobImageUrlThumbs());
			addItemsResVO.setServiceErrorVO(serviceError);

		} finally {
			try {
				if (con != null)
					con.close();
				if (cs!=null)
					cs.close();
			} catch (SQLException e) {
				this.logError("Error occurred while closing connection");
			}
			BBBPerformanceMonitor.end("GiftRegistryTools.addItemToGiftRegistry()");
		}

		this.logDebug("GiftRegistryTools.addItemToGiftRegistry() method ends");
		return addItemsResVO;
	}

	public void removePersonalizedSkuFromSession(List<AddItemsBean> addItemsBean) {
		DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

		BBBSessionBean sessionBean = BBBProfileManager.resolveSessionBean(request);
//		BBBSessionBean sessionBean = (BBBSessionBean) request
//				.resolveName(BBBCoreConstants.SESSION_BEAN);
		
		for (AddItemsBean addItemBean : addItemsBean) {
			if (sessionBean.getPersonalizedSkus().containsKey(
					addItemBean.getSku())) {
				sessionBean.getPersonalizedSkus().remove(addItemBean.getSku());
				logDebug("Personalized Sku " + addItemBean.getSku()
						+ "removed from session.");
			} else {
				logDebug("Personalized Sku " + addItemBean.getSku()
						+ "not found in session.");
			}
		}
	}

	/**
	 * Adds all the item to gift registry with single web service call.
	 * 
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return error
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */

	/**
	 * Removes a registry item from Registry.
	 * 
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return error
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public ManageRegItemsResVO removeUpdateGiftRegistryItem(
			final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryTools.removeUpdateGiftRegistryItem() method start");
		final ManageRegItemsResVO mageItemsResVO = (ManageRegItemsResVO)extractUtilMethod(pGiftRegistryViewBean);
		this.logDebug("GiftRegistryTools.removeUpdateGiftRegistryItem() method ends");
		return mageItemsResVO;
	}

	/**
	 * Removes/updates a registry item from Registry without calling webservice.
	 * 
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return error
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */

	public ManageRegItemsResVO removeUpdateGiftRegistryItemInEcomAdmin(
			final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryTools.removeUpdateGiftRegistryItemInEcomAdmin() method starts...");
		ManageRegItemsResVO mageItemsResVO = new ManageRegItemsResVO();
		ServiceErrorVO errorVO = new ServiceErrorVO();

		// validate parameters, if fails return mageItemsResVO with errorExists
		// as true
		String[] paramsToValidate = { pGiftRegistryViewBean.getRegistryId(),
				pGiftRegistryViewBean.getRowId(),
				pGiftRegistryViewBean.getQuantity() };

		ErrorStatus errorStatus = new ErrorStatus();
		errorStatus = this.getGiftRegUtils().validateInput(paramsToValidate);
		
		if (errorStatus.isErrorExists()) {
			errorVO.setErrorExists(errorStatus.isErrorExists());
			errorVO.setErrorId(errorStatus.getErrorId());
			errorVO.setErrorMessage(errorStatus.getErrorMessage());
			mageItemsResVO.setServiceErrorVO(errorVO);
			this.logDebug("GiftRegistryTools.removeUpdateGiftRegistryItemInEcomAdmin() : : error occured while validating input parameters");
			return mageItemsResVO;
		}
		
		int qty = Integer.parseInt(pGiftRegistryViewBean.getQuantity());
		if (qty < 0 || qty > 9999) {
			errorVO.setErrorExists(true);
			errorVO.setErrorId(BBBGiftRegistryConstants.VALIDATION);
			//errorVO.setErrorMessage(BBBGiftRegistryConstants.INVALID_QUANTITY);
			mageItemsResVO.setServiceErrorVO(errorVO);
			this.logDebug("GiftRegistryTools.removeUpdateGiftRegistryItemInEcomAdmin() : : error occured while validating quantity");
			return mageItemsResVO;
		}
		mageItemsResVO = updateItemsInRegistry2(pGiftRegistryViewBean);

		this.logDebug("GiftRegistryTools.removeUpdateGiftRegistryItemInEcomAdmin() method ends");
		return mageItemsResVO;
	}

	/**
	 * This method is written to replace webservice call for updating
	 * registryItem in EomAdmin DB. For removing the item, quantity is set as
	 * zero. For updating/removing item from gift registry, this method calls
	 * PUT_REG_ITEM2 stored procedure.
	 * 
	 * @param pGiftRegistryViewBean
	 *            the gift registry view bean
	 * @return ManageRegItemsResVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 * 
	 */
	
	public ManageRegItemsResVO updateItemsInRegistry2(
			final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBSystemException, BBBBusinessException {
		this.logDebug("GiftRegistryTools.UpdateItemsInRegistry2() method start");
		this.logDebug("Method Description : updating/removing item from gift registry using GiftRegistryViewBean as parameter");
		BBBPerformanceMonitor.start("GiftRegistryTools.UpdateItemsInRegistry2()");
		ManageRegItemsResVO mageItemsResVO = new ManageRegItemsResVO();
		ServiceErrorVO error = new ServiceErrorVO();

		Connection con = null;
		CallableStatement cs = null;
		boolean isUpdateSuccess = false;
		boolean isError = false;
		Transaction pTransaction = null;

		try {
			pTransaction = this.ensureTransaction();
			// create connection
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			con.setAutoCommit(false);
			if (con != null) {
				final String UpdateItemsInRegistry2StoredProc = BBBGiftRegistryConstants.Update_Items_In_Registry2;
				this.logDebug("GiftRegistryTools.UpdateItemsInRegistry2() ::: stored proc to be executed :"
						+ UpdateItemsInRegistry2StoredProc);

				// prepare the callable statement
				cs = con.prepareCall(UpdateItemsInRegistry2StoredProc);

				// set input parameters ...
				cs.setInt(1,
						Integer.parseInt(pGiftRegistryViewBean.getRegistryId()));

				String rowId = pGiftRegistryViewBean.getRowId().replace(' ',
						'+');
				cs.setString(2, rowId);

				int qtyRequested = Integer.parseInt(pGiftRegistryViewBean
						.getQuantity());
				if (qtyRequested <= 0)
					qtyRequested = -1;
				cs.setInt(3, qtyRequested);
				String itemType = null;
				if(!BBBUtility.isEmpty(pGiftRegistryViewBean.getItemTypes())){
					itemType = pGiftRegistryViewBean.getItemTypes().trim();
				}
				
				if (BBBUtility.isEmpty(itemType)
						|| (!itemType.equalsIgnoreCase("PER") && !itemType
								.equalsIgnoreCase("LTL"))) {
					itemType = "REG";
				}

				cs.setString(4, itemType);
				cs.setString(5, pGiftRegistryViewBean.getAssemblySelections());

				String assemPrice = pGiftRegistryViewBean.getAssemblyPrices();
				BigDecimal assemPriceBD = null;
				if (null != assemPrice && assemPrice.length() != 0)
					assemPriceBD = new BigDecimal(assemPrice);

				cs.setBigDecimal(6, assemPriceBD);
				cs.setString(7, pGiftRegistryViewBean.getLtlDeliveryServices());

				String ltlDeliveryPrice = pGiftRegistryViewBean
						.getLtlDeliveryPrices();
				BigDecimal ltlDeliveryPriceBD = null;
				if (null != ltlDeliveryPrice && ltlDeliveryPrice.length() != 0)
					ltlDeliveryPriceBD = new BigDecimal(ltlDeliveryPrice);
				cs.setBigDecimal(8, ltlDeliveryPriceBD);

				Long findStoreNumber = getStoreNum(pGiftRegistryViewBean
						.getSiteFlag());
				Integer storeNum = Integer.valueOf(findStoreNumber.intValue());
				cs.setInt(9, storeNum);

				cs.setString(10,
						getCreateProgram(pGiftRegistryViewBean.getSiteFlag()));
				cs.setString(11, this.getRowXngUser());
				cs.setString(12, pGiftRegistryViewBean.getSiteFlag());

				// execute stored procedure
				cs.executeUpdate();

				// set errorExists as false if no exception is thrown from
				// procedure
				error.setErrorExists(false);
				mageItemsResVO.setServiceErrorVO(error);
				isUpdateSuccess = true;
				this.logDebug("GiftRegistryTools.UpdateItemsInRegistry2() ::: stored proc execution completed.");

			}
		} catch (Exception exp) {
			isError = true;
			error = (ServiceErrorVO) getGiftRegUtils().logAndFormatError(
					"UpdateItemsInRegistry2", con, "serviceErrorVO", exp,
					pGiftRegistryViewBean.getUserToken(),
					pGiftRegistryViewBean.getSiteFlag(),
					pGiftRegistryViewBean.getRegistryId(),
					pGiftRegistryViewBean.getRowId(),
					pGiftRegistryViewBean.getQuantity());
			mageItemsResVO.setServiceErrorVO(error);
		}

		finally {
			
			try {
				if (cs != null)
					cs.close();
				this.endTransaction(isError, pTransaction);
				if (con != null)
					con.close();

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection",e);
			}
			BBBPerformanceMonitor.end("GiftRegistryTools.UpdateItemsInRegistry2()");
		}

		this.logDebug("GiftRegistryTools.UpdateItemsInRegistry2() method ends.");
		return mageItemsResVO;
	}

	/**
	 * Validating input parameters for stored procedure calling
	 * 
	 * @param invalidCharsPattern
	 *            the pattern set that should not be present in a perticular
	 *            string
	 * @param sValue
	 *            the string to be validated
	 * 
	 * @return boolean
	 */
	public boolean hasInvalidChars(String invalidCharsPattern, String sValue) {
		boolean hasInvalidChars = false;

		if (null != sValue) {
			Pattern pattern = Pattern.compile(invalidCharsPattern);
			Matcher match = pattern.matcher(sValue);
			hasInvalidChars = match.find();
		}

		return hasInvalidChars;
	}

	/**
	 * Calling web service for import registry.
	 * 
	 * @param profile
	 *            the profile
	 * @param pRegistryVO
	 *            the registry vo
	 * @return errorExist
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO importRegistry(final Profile profile,
			final RegistryVO pRegistryVO) throws BBBBusinessException,
			BBBSystemException {

		RegistryResVO importResVO = null;
		if (StringUtils.isEmpty(pRegistryVO.getRegistryId())) {
			pRegistryVO.setRegistryIdWS(0);
		} else {
			pRegistryVO.setRegistryIdWS(Long.parseLong(pRegistryVO
					.getRegistryId()));
		}
		this.logDebug("GiftRegistryTools.importRegistry() method start");
		importResVO = (RegistryResVO)extractUtilMethod(pRegistryVO);
		this.logDebug("GiftRegistryTools.importRegistry() method ends");
		return importResVO;
	}

	/**
	 * Calling web service to retrieve password registry.
	 * 
	 * @param forgotRegPassRequestVO
	 *            the forgot reg pass request vo
	 * @return errorExist
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO forgotRegPasswordService(
			final ForgetRegPassRequestVO forgotRegPassRequestVO)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryTools.forgotRegPasswordService() method start");
		if (StringUtils.isEmpty(forgotRegPassRequestVO
				.getForgetPassRegistryId())) {
			forgotRegPassRequestVO.setRegistryIdWS(0);
		} else {
			forgotRegPassRequestVO
					.setRegistryIdWS(Long.parseLong(forgotRegPassRequestVO
							.getForgetPassRegistryId()));
		}
		final RegistryResVO forgetRegPasswordResponseVO = (RegistryResVO)extractUtilMethod(forgotRegPassRequestVO);

		this.logDebug("GiftRegistryTools.forgotPasswordService() method ends");
		return forgetRegPasswordResponseVO;
	}

	/**
	 * Fetch users soonest or recent.
	 * 
	 * @param registryIds
	 *            the registry ids
	 * @return registryId
	 * @throws RepositoryException
	 *             the repository exception
	 * @throws NumberFormatException
	 *             the number format exception
	 */
	public String fetchUsersSoonestOrRecent(final List<String> registryIds)
			throws RepositoryException, NumberFormatException {

		this.logDebug("GiftRegistryTools.fetchUsersSoonestOrRecent() method start");
		String registryId;
		String registryIdSoonest = null;
		String registryIdRecent = null;
		final Date todaysDate = new Date();
		final long todaysDateDays = todaysDate.getTime()
				/ (1000 * 60 * 60 * 24);
		int flag = -1;
		int minSoonestDaysDiff = -1;
		int minRecentDaysDiff = -1;

		for (final String iterateRegistry : registryIds) {

			this.logDebug("GiftRegistryTools.fetchUsersSoonestOrRecent() MSG = registryId : "
					+ iterateRegistry);

			final RepositoryItem repositoryItem = this.getGiftRepository()
					.getItem(iterateRegistry, "giftregistry");
			final Date eventDate = (Date) repositoryItem
					.getPropertyValue("eventDate");
			if (null != eventDate) {
				final long eventDateDays = eventDate.getTime()
						/ (1000 * 60 * 60 * 24);

				if (eventDateDays >= todaysDateDays) {

					flag = 1;
					final int daysDifference = (int) (eventDateDays - todaysDateDays);

					if ((minSoonestDaysDiff == -1)
							|| (minSoonestDaysDiff > daysDifference)) {

						minSoonestDaysDiff = daysDifference;
						registryIdSoonest = iterateRegistry;

					}
				} else if ((flag == -1) && (eventDateDays < todaysDateDays)) {

					final int daysDifference = (int) (todaysDateDays - eventDateDays);

					if ((minRecentDaysDiff == -1)
							|| (minRecentDaysDiff > daysDifference)) {

						minRecentDaysDiff = daysDifference;
						registryIdRecent = iterateRegistry;

					}

				}
			}
		}
		registryId = (flag == 1) ? registryIdSoonest : registryIdRecent;

		this.logDebug("GiftRegistryTools.fetchUsersSoonestOrRecent() MSG = SoonestOrRecentRegistry found is : "
				+ registryId);

		this.logDebug("GiftRegistryTools.fetchUsersSoonestOrRecent() method ends");

		return registryId;

	}

	/**
	 * This method is used to invoke web service to get registry info.
	 * 
	 * @param registryReqVO
	 *            the registry req vo
	 * @return RegistryResVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO getRegistryInfo(final RegistryReqVO registryReqVO)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryTools.getRegistryInfo() method start");

		RegistryResVO registryResVO = null;

		final RepositoryItem profileItem = extractUserProfile();
		String profileID = null;
		String userEmailID = null;

		if (profileItem != null) {
			profileID = profileItem.getRepositoryId();
			userEmailID = (String) profileItem.getPropertyValue("email");
		}

		if ((registryReqVO != null)
				&& BBBUtility.isValidNumber(registryReqVO.getRegistryId())) {

			// invoking getRegistryInfo web service
			registryResVO = (RegistryResVO)extractUtilMethod(registryReqVO);

			// Log error if any
			if ((registryResVO.getServiceErrorVO() != null)
					&& registryResVO.getServiceErrorVO().isErrorExists()) {

				if (!BBBUtility.isEmpty(registryResVO.getServiceErrorVO()
						.getErrorDisplayMessage())) {
					this.logError("CLS=[GiftRegistryTools]/MTHD=[getRegistryInfo]/ Error =["
							+ " RegistryID = "
							+ registryReqVO.getRegistryId()
							+ " |ProfileID = "
							+ registryReqVO.getProfileId()
							+ " |EmailID = "
							+ registryReqVO.getEmailId()
							+ " |ErrorID = "
							+ +registryResVO.getServiceErrorVO().getErrorId()
							+ " |ErrorMessag ="
							+ registryResVO.getServiceErrorVO()
									.getErrorDisplayMessage());
				}
			}

		} else {
			this.logError("CLS=[GiftRegistryTools]/MTHD=[getRegistryInfo]/ Error =["
					+ ((null == registryReqVO) ? "Null registryReqVO"
							: " Invalid RegistryID = "
									+ registryReqVO.getRegistryId())
					+ " |ProfileID = "
					+ profileID
					+ " |EmailID = "
					+ userEmailID);
		}

		this.logDebug("GiftRegistryTools.getRegistryInfo() method ends");
		return registryResVO;
	}

	/**
	 * @return
	 */
	protected RepositoryItem extractUserProfile() {
		return ServletUtil.getCurrentUserProfile();
	}

	public RepositoryItem[] getRegistryInfoFromDB(String registryId,
			String siteId) throws BBBBusinessException, BBBSystemException,
			RepositoryException {
		this.logDebug("GiftRegistryTools.getRegistryInfoFromDB() method start");
		this.logDebug("Method Description : updating/removing item from gift registry using registryId as parameter, returns RepositoryItem");
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryInfoFromDB()");

		RepositoryItem[] registryItems = null;
		RqlStatement statement = null;
		if (BBBUtility.isValidNumber(registryId)) {

			statement = RqlStatement.parseRqlStatement("registryNum=?0");

			final RepositoryView view = this.getRegistryInfoRepository()
					.getView("registryInfo");
			Object[] params = new Object[1];
			params[0] = registryId;
			registryItems = extractDBCall(params, statement, view);
			BBBPerformanceMonitor.end("GiftRegistryTools.getRegistryInfoFromDB()");
			return registryItems;
		}

		this.logDebug("GiftRegistryTools.getRegistryInfoFromDB() method ends");
		return registryItems;
	}

	public RepositoryItem[] getRegistryAddressesFromDB(String registryId)
			throws BBBBusinessException, BBBSystemException,
			RepositoryException {
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryAddressesFromDB()");
		this.logDebug("GiftRegistryTools.getRegistryAddressesFromDB() method start");
		this.logDebug("Method Description : fetching registry address using regsitryId as parameter, returns RepositoryItem");
		RepositoryItem[] registryItems = null;
		RqlStatement statement = null;
		if (BBBUtility.isValidNumber(registryId)) {

			statement = RqlStatement.parseRqlStatement("registryNum=?0");

			final RepositoryView view = this.getRegistryInfoRepository()
					.getView("regAddress");
			Object[] params = new Object[1];
			params[0] = registryId;
			registryItems = extractDBCall(params, statement, view);
			return registryItems;
		}

		this.logDebug("GiftRegistryTools.getRegistryAddressesFromDB() method ends");
		BBBPerformanceMonitor.end("GiftRegistryTools.getRegistryAddressesFromDB()");
		return registryItems;
	}

	public RepositoryItem[] getRegistrySkuDetailsFromDB(String registryId)
			throws BBBBusinessException, BBBSystemException,
			RepositoryException {
		this.logDebug("GiftRegistryTools.getRegistrySkuDetailsFromDB() method start");
		this.logDebug("Method Description : fetching registry sku details using regsitryId as parameter, returns RepositoryItems");
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistrySkuDetailsFromDB()");

		RepositoryItem[] registryItems = null;
		RqlStatement statement = null;
		if (BBBUtility.isValidNumber(registryId)) {

			statement = RqlStatement.parseRqlStatement("registryNum=?0");

			final RepositoryView view = this.getRegistryInfoRepository()
					.getView("regDetail");
			Object[] params = new Object[1];
			params[0] = registryId;
			registryItems = extractDBCall(params, statement, view);
			BBBPerformanceMonitor.end("GiftRegistryTools.getRegistrySkuDetailsFromDB()");
			return registryItems;
		}

		this.logDebug("GiftRegistryTools.getRegistryInfo() method ends");
		return registryItems;
	}

	/**
	 * This method is used invoke web service to fetch registry items .
	 * 
	 * @param pRegistrySearchVO
	 *            the registry search vo
	 * @return RegistryItemsListVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryItemsListVO fetchRegistryItems(
			final RegistrySearchVO pRegistrySearchVO)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryTools.fetchRegistryItems() method start");

		// invoking getRegistryItemList web service
		final RegistryItemsListVO registryItemsListVO = (RegistryItemsListVO)extractUtilMethod(pRegistrySearchVO);

		if (pRegistrySearchVO.getGiftGiver()) {
			this.logDebug("User is giftGiver so appending static key in images paths");
			personlizeImageUrl(registryItemsListVO);
		}

		this.logDebug("GiftRegistryTools.fetchRegistryItems() method ends");

		return registryItemsListVO;
	}


	public void personlizeImageUrl(RegistryItemsListVO registryItemsListVO)
			throws BBBSystemException, BBBBusinessException {

		List<String> moderateKeyValueList = new ArrayList<String>();
		moderateKeyValueList = getCatalogTools().getAllValuesForKey(
				BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
				BBBCoreConstants.MODERATED);
		String moderateKeyValue = "";
		if (!BBBUtility.isListEmpty(moderateKeyValueList)) {
			moderateKeyValue = moderateKeyValueList.get(0);
		}

		if (null != registryItemsListVO
				&& null != registryItemsListVO.getRegistryItemList()) {

			for (RegistryItemVO registryItemVO : registryItemsListVO
					.getRegistryItemList()) {

				String personalizeImgUrl = registryItemVO
						.getPersonalizedImageUrls();
				String personalizeImgThumbUrl = registryItemVO
						.getPersonalizedImageUrlThumbs();

				if (null != registryItemVO
						&& !(BBBUtility.isEmpty(registryItemVO.getRefNum()))) {

        		   if(!(BBBUtility.isEmpty(personalizeImgUrl)) && !personalizeImgUrl.contains(moderateKeyValue)){
						StringBuffer moderatedUrl = new StringBuffer();
						if (personalizeImgUrl
								.contains(BBBCoreConstants.DOT_EXTENTION)) {
							int indexOfDotExtension = personalizeImgUrl
									.lastIndexOf(BBBCoreConstants.DOT_EXTENTION);
							String imageName = personalizeImgUrl.substring(0,
									indexOfDotExtension);
							String extension = personalizeImgUrl
									.substring(indexOfDotExtension);
							moderatedUrl = moderatedUrl.append(imageName)
									.append(moderateKeyValue).append(extension);
						} else {
							moderatedUrl.append(personalizeImgUrl).append(
									moderateKeyValue);
						}
						registryItemVO.setPersonalizedImageUrls(moderatedUrl
								.toString());
						this.logDebug("prsonalizedImgURL : "
								+ moderatedUrl.toString());
					}
        		   if(!(BBBUtility.isEmpty(personalizeImgThumbUrl)) && !personalizeImgThumbUrl.contains(moderateKeyValue)){
						StringBuffer moderatedUrlThumb = new StringBuffer();
						if (personalizeImgThumbUrl
								.contains(BBBCoreConstants.DOT_EXTENTION)) {
							int indexOfDotExtension = personalizeImgThumbUrl
									.lastIndexOf(BBBCoreConstants.DOT_EXTENTION);
							String imageName = personalizeImgThumbUrl
									.substring(0, indexOfDotExtension);
							String extension = personalizeImgThumbUrl
									.substring(indexOfDotExtension);
							moderatedUrlThumb = moderatedUrlThumb
									.append(imageName).append(moderateKeyValue)
									.append(extension);
						} else {
							moderatedUrlThumb.append(personalizeImgThumbUrl)
									.append(moderateKeyValue);
						}
						registryItemVO
								.setPersonalizedImageUrlThumbs(moderatedUrlThumb
										.toString());
						this.logDebug("personalizeImgThumbUrl : "
								+ moderatedUrlThumb.toString());
					}
				}
			}
		}
	}

	public RegistryItemsListVO fetchRegistryItemsFromRepo(String registryId,
			String regView) throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryTools.fetchRegistryItemsFromRepo() method start");
		this.logDebug("Method Description : fetching registry items from repository using regsitryId and registry view as parameters and convert repo Items to RegistryItemsListVO, returns RegistryItemsListVO");
		BBBPerformanceMonitor.start("GiftRegistryTools.fetchRegistryItemsFromRepo()");
		RepositoryItem[] registryItems = null;
		RqlStatement statement = null;
		final RegistryItemsListVO regItemsListVO = new RegistryItemsListVO();
		final List<RegistryItemVO> registryItemsList = new ArrayList<RegistryItemVO>();
		String actionCD = "D";
		if (BBBUtility.isValidNumber(registryId)) {
			try {
				statement = RqlStatement
						.parseRqlStatement("registryNum=?0 AND actionCD!=?1");

				final RepositoryView view = this.getRegistryInfoRepository()
						.getView("regDetail");
				Object[] params = new Object[2];
				params[0] = registryId;
				params[1] = actionCD;
				registryItems = extractDBCall(params, statement, view);

				// Map to hold Sku specific Registry Item VO as
				// Map<SkuId,RegistryItemVO>
				final Map<String, RegistryItemVO> skuRegItemVOMap = new HashMap<String, RegistryItemVO>();

				RegistryItemVO registryItemVO = null;

				if (registryItems != null) {

					for (RepositoryItem item : registryItems) {

						int qtyRequested = (Integer) item
								.getPropertyValue("qtyRequested");
						int qtyFulfilled = (Integer) item
								.getPropertyValue("qtyFulfilled");
						int qtyWebPurchased = (Integer) item
								.getPropertyValue("qtyWebPurchased");
						int qtyPurchased = qtyFulfilled + qtyWebPurchased;

						if (regView.equalsIgnoreCase(BBBCoreConstants.VIEW_ALL)
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_REMAINING) && (qtyRequested > qtyPurchased))
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_PURCHASED) && (qtyRequested <= qtyPurchased))) {
							registryItemVO = new RegistryItemVO();

							registryItemVO.setQtyRequested(qtyRequested);
							registryItemVO.setQtyFulfilled(qtyFulfilled);
							registryItemVO.setQtyWebPurchased(qtyWebPurchased);
							registryItemVO.setQtyPurchased(qtyPurchased);
							registryItemVO.setSku((Integer) item
									.getPropertyValue("skuId"));
							registryItemVO.setRowID((String) item
									.getPropertyValue("ROWID"));

							registryItemsList.add(registryItemVO);
							skuRegItemVOMap
									.put(String.valueOf(item
											.getPropertyValue("skuId")),
											registryItemVO);
						}
					}
					regItemsListVO.setTotEntries(registryItemsList.size());
				}

				regItemsListVO.setRegistryItemList(registryItemsList);

				// Set the Map to hold Sku specific Registry Item VO.
				regItemsListVO.setSkuRegItemVOMap(skuRegItemVOMap);
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				logError(e.getMessage(), e);
			}finally{
				BBBPerformanceMonitor.end("GiftRegistryTools.fetchRegistryItemsFromRepo()");
				this.logDebug("GiftRegistryTools.fetchRegistryItemsFromRepo() method ends");
			}
		}
		return regItemsListVO;
	}

	/**
	 * This method fetch registry items from Ecom admin database on sorting
	 * basis of last maintained
	 * 
	 * @param registryId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public RegistryItemsListVO fetchRegistryItemsFromEcomAdmin(
			String registryId, String regView) throws BBBBusinessException,
			BBBSystemException {
		this.logDebug("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() method start");
		this.logDebug("Method Description : fetching registry items from Ecom admin using regsitryId and registry view as parameters and convert repo Items to RegistryItemsListVO, returns RegistryItemsListVO");
		BBBPerformanceMonitor.start("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin()");
		RepositoryItem[] registryItems = null;
		RepositoryItem[] registryItemsPersonalizedAndLtl = null;
		RqlStatement statement = null;
		RqlStatement statementForPersonalizedAndLtl = null;
		final RegistryItemsListVO regItemsListVO = new RegistryItemsListVO();
		final List<RegistryItemVO> registryItemsList = new ArrayList<RegistryItemVO>();
		String actionCD = "D";

		// Map to hold Sku specific Registry Item VO as
		// Map<SkuId,RegistryItemVO>
		final Map<String, RegistryItemVO> skuRegItemVOMap = new HashMap<String, RegistryItemVO>();

		if (BBBUtility.isValidNumber(registryId)) {
			try {
				statement = RqlStatement
						.parseRqlStatement("registryNum=?0 AND actionCD!=?1 ORDER BY lastMaintained SORT DESC");
				statementForPersonalizedAndLtl = RqlStatement
						.parseRqlStatement("registryNum=?0 AND actionCD!=?1 ORDER BY lastMaintained SORT DESC");
				final RepositoryView view = this.getRegistryInfoRepository()
						.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL);
				final RepositoryView viewPersonalizedAndLtl = this
						.getRegistryInfoRepository().getView(
								BBBCoreConstants.ITEM_DESC_REG_DETAIL2);
				Object[] params = new Object[2];
				params[0] = registryId;
				params[1] = actionCD;
				registryItems = extractDBCall(params, statement, view);
				registryItemsPersonalizedAndLtl = extractDBCall(params, statementForPersonalizedAndLtl, viewPersonalizedAndLtl);
				RegistryItemVO registryItemVO = null;

				if (registryItems != null) {
					for (RepositoryItem registryItem : registryItems) {
						RegistryItemVO registryVO = new RegistryItemVO();
						registryItemVO = populateRegistryItem(registryItem,
								registryVO);
						int qtyRequested = registryItemVO.getQtyRequested();
						int qtyFulfilled = registryItemVO.getQtyFulfilled();
						int qtyWebPurchased = registryItemVO
								.getQtyWebPurchased();
						int qtyPurchased = qtyFulfilled + qtyWebPurchased;
						if (regView.equalsIgnoreCase(BBBCoreConstants.VIEW_ALL)
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_REMAINING) && (qtyRequested > qtyPurchased))
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_PURCHASED) && (qtyRequested <= qtyPurchased))) {
							registryItemsList.add(registryItemVO);
							skuRegItemVOMap
									.put(String.valueOf(registryItem
											.getPropertyValue("skuId")),
											registryItemVO);
						}
					}
				}
				if (registryItemsPersonalizedAndLtl != null) {
					for (RepositoryItem registryItem : registryItemsPersonalizedAndLtl) {
						RegistryItemVO registryVO = new RegistryItemVO();
						//This method is invoked only in case of gift giver hence passing true in the below method call. | BBBH-5246 
						registryItemVO = populatePersonalizedAndLtlRegistryItem(
								registryItem, registryVO, true);
						int qtyRequested = registryItemVO.getQtyRequested();
						int qtyFulfilled = registryItemVO.getQtyFulfilled();
						int qtyWebPurchased = registryItemVO
								.getQtyWebPurchased();
						int qtyPurchased = qtyFulfilled + qtyWebPurchased;
						if (regView.equalsIgnoreCase(BBBCoreConstants.VIEW_ALL)
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_REMAINING) && (qtyRequested > qtyPurchased))
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_PURCHASED) && (qtyRequested <= qtyPurchased))) {
							registryItemsList.add(registryItemVO);
							if (BBBCoreConstants.LTL.equals(registryItemVO
									.getItemType())) {
								skuRegItemVOMap
										.put(String
												.valueOf(registryItemVO
														.getSku()
														+ "_"
														+ registryItemVO
														.getLtlDeliveryServices()
														+ registryItemVO.getAssemblySelected()),
												registryItemVO);
							} else {
								skuRegItemVOMap.put(
										String.valueOf(registryItemVO.getSku()
												+ "_"
												+ registryItemVO.getRefNum()),
										registryItemVO);
							}
						}

					}
				}
				regItemsListVO.setTotEntries(registryItemsList.size());
				regItemsListVO.setRegistryItemList(registryItemsList);
				regItemsListVO.setSkuRegItemVOMap(skuRegItemVOMap);
				personlizeImageUrl(regItemsListVO);

			} catch (RepositoryException e) {
				this.logError("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Repository exception while fetching reg items "
						+ e);
				throw new BBBBusinessException(
						"GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Repository exception while fetching reg items "
								+ e);
			} catch (ParseException e) {
				this.logError("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Parsing exception while parsing created and last maintained timstamp "
						+ e);
				throw new BBBBusinessException(
						"GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Parsing exception while parsing created and last maintained timstamp "
								+ e);
			}finally{
				BBBPerformanceMonitor.end("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin()");
				this.logDebug("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() method ends");
			}
		}
		return regItemsListVO;
	}

	/**
	 * Fetch registry items from ecom admin.
	 * 
	 * @param registryId the registry id
	 * @param isGiftGiver the is gift giver
	 * @param regView the reg view
	 * @return the registry items list vo
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public RegistryItemsListVO fetchRegistryItemsFromEcomAdmin(
			String registryId, boolean isGiftGiver, String regView)
			throws BBBBusinessException, BBBSystemException {
		
		return fetchRegistryItemsFromEcomAdmin(registryId, isGiftGiver, false, regView);
	}

	/**
	 * This method fetch registry items from Ecom admin database on sorting
	 * basis of last maintained.
	 *
	 * @param registryId the registry id
	 * @param isGiftGiver the is gift giver
	 * @param isRLV the is rlv
	 * @param regView the reg view
	 * @return the registry items list vo
	 * @throws BBBBusinessException the BBB business exception
	 * @throws BBBSystemException the BBB system exception
	 */
	public RegistryItemsListVO fetchRegistryItemsFromEcomAdmin(
			String registryId, boolean isGiftGiver, boolean isRLV, String regView)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() method start");
		this.logDebug("Method Description : fetching registry items from Ecom admin on sorting basis of last maintained using regsitryId and registry view as parameters and convert repo Items to RegistryItemsListVO, returns RegistryItemsListVO");
		BBBPerformanceMonitor.start("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin()");
		RepositoryItem[] registryItems = null;
		RepositoryItem[] registryItemsPersonalizedAndLtl = null;
		RqlStatement statement = null;
		RqlStatement statementForPersonalizedAndLtl = null;
		final RegistryItemsListVO regItemsListVO = new RegistryItemsListVO();
		final List<RegistryItemVO> registryItemsList = new ArrayList<RegistryItemVO>();
		String actionCD = "D";

		// Map to hold Sku specific Registry Item VO as
		// Map<SkuId,RegistryItemVO>
		final Map<String, RegistryItemVO> skuRegItemVOMap = new HashMap<String, RegistryItemVO>();

		if (BBBUtility.isValidNumber(registryId)) {
			try {
				statement = RqlStatement
						.parseRqlStatement("registryNum=?0 AND actionCD!=?1 ORDER BY lastMaintained SORT DESC");
				statementForPersonalizedAndLtl = RqlStatement
						.parseRqlStatement("registryNum=?0 AND actionCD!=?1 ORDER BY lastMaintained SORT DESC");
				final RepositoryView view = this.getRegistryInfoRepository()
						.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER);
				final RepositoryView viewPersonalizedAndLtl = this
						.getRegistryInfoRepository().getView(
								BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER);
				Object[] params = new Object[2];
				params[0] = registryId;
				params[1] = actionCD;
				registryItems = extractDBCall(params, statement, view);
				registryItemsPersonalizedAndLtl = extractDBCall(params, statementForPersonalizedAndLtl, viewPersonalizedAndLtl);
				RegistryItemVO registryItemVO = null;

				if (registryItems != null) {
					for (RepositoryItem registryItem : registryItems) {
						RegistryItemVO registryVO = new RegistryItemVO();
						registryItemVO = populateRegistryItem(registryItem, isRLV,
								registryVO);
						int qtyRequested = registryItemVO.getQtyRequested();
						int qtyFulfilled = registryItemVO.getQtyFulfilled();
						int qtyWebPurchased = registryItemVO
								.getQtyWebPurchased();
						int qtyPurchased = qtyFulfilled + qtyWebPurchased;
						if (regView.equalsIgnoreCase(BBBCoreConstants.VIEW_ALL)
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_REMAINING) && (qtyRequested > qtyPurchased))
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_PURCHASED) && (qtyRequested <= qtyPurchased))) {
							
							
								registryItemsList.add(registryItemVO);
								skuRegItemVOMap
										.put(String.valueOf(registryItem
												.getPropertyValue("skuId")),
												registryItemVO);
						
						}
					}
				}
				
				if (registryItemsPersonalizedAndLtl != null) {
					for (RepositoryItem registryItem : registryItemsPersonalizedAndLtl) {
						RegistryItemVO registryVO = new RegistryItemVO();
						registryItemVO = populatePersonalizedAndLtlRegistryItem(
								registryItem, registryVO, isGiftGiver, isRLV);
						int qtyRequested = registryItemVO.getQtyRequested();
						int qtyFulfilled = registryItemVO.getQtyFulfilled();
						int qtyWebPurchased = registryItemVO
								.getQtyWebPurchased();
						int qtyPurchased = qtyFulfilled + qtyWebPurchased;
						if (regView.equalsIgnoreCase(BBBCoreConstants.VIEW_ALL)
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_REMAINING) && (qtyRequested > qtyPurchased))
								|| (regView
										.equalsIgnoreCase(BBBCoreConstants.VIEW_PURCHASED) && (qtyRequested <= qtyPurchased))) {
							registryItemsList.add(registryItemVO);

							if (BBBCoreConstants.LTL.equals(registryItemVO
									.getItemType())) {
								skuRegItemVOMap
										.put(String
												.valueOf(registryItemVO
														.getSku()
														+ "_"
														+ registryItemVO
																.getLtlDeliveryServices()
														+ registryItemVO.getAssemblySelected()),
												registryItemVO);
							} else {
								skuRegItemVOMap.put(
										String.valueOf(registryItemVO.getSku()
												+ "_"
												+ registryItemVO.getRefNum()),
										registryItemVO);
							}
						}
					}
				}
				regItemsListVO.setTotEntries(registryItemsList.size());
				regItemsListVO.setRegistryItemList(registryItemsList);
				regItemsListVO.setSkuRegItemVOMap(skuRegItemVOMap);

			} catch (RepositoryException e) {
				this.logError("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Repository exception while fetching reg items "
						+ e);
				throw new BBBBusinessException(
						"GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Repository exception while fetching reg items "
								+ e);
			} catch (ParseException e) {
				this.logError("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Parsing exception while parsing created and last maintained timstamp "
						+ e);
				throw new BBBBusinessException(
						"GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() :: Parsing exception while parsing created and last maintained timstamp "
								+ e);
			}finally{
				BBBPerformanceMonitor.start("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin()");
				this.logDebug("GiftRegistryTools.fetchRegistryItemsFromEcomAdmin() method ends");
			}
		}
		return regItemsListVO;
	}

	/**
	 * Populate registry item.
	 *
	 * @param registryItem the registry item
	 * @param registryItemVO the registry item vo
	 * @return the registry item vo
	 * @throws ParseException the parse exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private RegistryItemVO populateRegistryItem(RepositoryItem registryItem,
			RegistryItemVO registryItemVO) throws ParseException,
			BBBSystemException, BBBBusinessException {
		return populateRegistryItem(registryItem, false, registryItemVO);
	}


	/**
	 * Populate registry item.
	 *
	 * @param registryItem the registry item
	 * @param isRLV the is rlv
	 * @param registryItemVO the registry item vo
	 * @return the registry item vo
	 * @throws ParseException the parse exception
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	private RegistryItemVO populateRegistryItem(RepositoryItem registryItem,
			boolean isRLV, RegistryItemVO registryItemVO)
			throws ParseException, BBBSystemException, BBBBusinessException {

		this.logDebug("GiftRegistryTools.populateRegistryItem() method starts");

		Integer qtyRequested = 0;
		Integer qtyFulfilled = 0;
		Integer qtyWebPurchased = 0;
		Integer qtyPurchased = 0;
		Long lastMaintained = 0L;
		Long createDate = 0L;
		Integer skuId = 0;
		String rowId = BBBCoreConstants.BLANK;
		if (registryItem.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) != null) {
			qtyRequested = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.QTY_REQUESTED);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) != null) {
			qtyFulfilled = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.QTY_FULFILLED);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) != null) {
			qtyWebPurchased = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED);
		}
		qtyPurchased = qtyFulfilled + qtyWebPurchased;
		if (registryItem.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) != null) {
			lastMaintained = (Long) registryItem
					.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) != null) {
			createDate = (Long) registryItem
					.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.ROWID) != null) {
			rowId = (String) registryItem
					.getPropertyValue(BBBCoreConstants.ROWID);
		}
		if (registryItem.getPropertyValue(BBBCoreConstants.SKUID) != null) {
			skuId = (Integer) registryItem
					.getPropertyValue(BBBCoreConstants.SKUID);
		}
		registryItemVO.setQtyRequested(qtyRequested.intValue());
		registryItemVO.setQtyFulfilled(qtyFulfilled.intValue());
		registryItemVO.setQtyWebPurchased(qtyWebPurchased.intValue());
		registryItemVO.setQtyPurchased(qtyPurchased.intValue());
		registryItemVO.setRowID(rowId);
		registryItemVO.setSku(skuId);

		SimpleDateFormat sd = new SimpleDateFormat(
				BBBCoreConstants.RKG_DATE_PATTERN);
		Date lastMaintainedDate = sd.parse(lastMaintained.toString());
		Timestamp lastMaintainedTimeStamp = new Timestamp(
				lastMaintainedDate.getTime());
		registryItemVO.setLastMaintained(lastMaintainedTimeStamp);
		Date createdDate = sd.parse(createDate.toString());
		Timestamp createTimestamp = new Timestamp(createdDate.getTime());
		registryItemVO.setCreateTimestamp(createTimestamp);
		
		if(!isRLV){
			
			logDebug("RLV Flag is not true, setting price and SKU VO for registry item");
			
		setPriceInRegItem(registryItemVO);
		
		SKUDetailVO skuVO = null;
        try{
              skuVO = this.getSKUDetailsWithProductId(
                    extractSiteId(), skuId.toString(),
                    registryItemVO);
              
        } catch(BBBBusinessException be){
              this.logError("Found a SKU that is present in registry, but not present in ATG Catalog : REgistry Id : and SKU Id: "+skuId);
        }
		registryItemVO.setsKUDetailVO(skuVO);
		}

		this.logDebug("GiftRegistryTools.populateRegistryItem() method ends");
		return registryItemVO;
	}

	public void setPriceInRegItem(RegistryItemVO regItem) {

		this.logDebug("GiftRegistryTools.setPriceInRegItem() method ends");
		final String skuId = String.valueOf(regItem.getSku());
		try {
			final String productId = this.getCatalogTools()
					.getParentProductForSku(skuId, true);
			final double salePrice = this.getCatalogTools().getSalePrice(
					productId, skuId);
			if (salePrice > 0) {
				regItem.setPrice(String.valueOf(salePrice));
			} else {
				final double listPrice = this.getCatalogTools().getListPrice(
						productId, skuId);
				regItem.setPrice(String.valueOf(listPrice));
			}
			
			
			// Setting In Cart price for the SKUs 
			final double inCartPrice=this.getCatalogTools().getIncartPrice(productId, skuId);
			regItem.setInCartPrice(String.valueOf(inCartPrice));
			
		} catch (final BBBBusinessException e) {
			this.logDebug("setPriceInRegItem :: Business Exception while fetching parent product Id for sku id: "
					+ skuId + "Exception: " + e);
		} catch (final BBBSystemException e) {
			this.logDebug("setPriceInRegItem :: System Exception while fetching parent product Id for sku id: "
					+ skuId + "Exception: " + e);
		}
		this.logDebug("GiftRegistryTools.setPriceInRegItem() method ends");
	}

	/**
	 * This method is a wrapper for getSKUDetails to include sku's parent
	 * product Id in the response
	 * 
	 * @param siteId
	 * @param skuId
	 * @return skuVO
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */

	public SKUDetailVO getSKUDetailsWithProductId(final String siteId,
			final String skuId, RegistryItemVO registryItemVO)
			throws BBBSystemException, BBBBusinessException {
		boolean productWebOffered = false;
		boolean skuWebOffered = false;
		SKUDetailVO skuVO = getCatalogTools().getSKUDetails(
				extractSiteId(), skuId.toString());
		RepositoryItem productRepoItem = getCatalogTools()
				.getParentProductItemForSku(skuId);
		// For Story BPSI-5384 - Update 3 flags (i.e. Disabled flag, Out of
		// stock flag, International shipping restriction flag.
		skuVO.setDisableFlag(((Boolean) skuVO.getSkuRepositoryItem()
				.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME)));
		if (!skuVO.isDisableFlag()) {
			try {
				int inStockStatus = getInventoryManager()
						.getProductAvailability(siteId, skuId,
								BBBInventoryManager.PRODUCT_DISPLAY, 0);
				if (inStockStatus == BBBInventoryManager.AVAILABLE
						|| inStockStatus == BBBInventoryManager.LIMITED_STOCK) {
					skuVO.setSkuInStock(true);
				}
			} catch (BBBBusinessException e) {
				logDebug("Product not available", e);
			}
		}
		if (productRepoItem != null) {
			String productId = (String) productRepoItem
					.getPropertyValue(BBBCatalogConstants.ID);
			skuVO.setParentProdId(productId);
			// Set Seo url of parent product if sku is active and weboffered
			if (skuVO.getSkuRepositoryItem().getPropertyValue(
					BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
				skuWebOffered = ((Boolean) skuVO.getSkuRepositoryItem()
						.getPropertyValue(
								BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME))
						.booleanValue();
			}
			if (productRepoItem
					.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
				productWebOffered = ((Boolean) productRepoItem
						.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME))
						.booleanValue();
			}
			if (skuWebOffered && productWebOffered) {
				String seoUrl = (String) productRepoItem
						.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME);
				if (null != seoUrl && registryItemVO != null) {
					registryItemVO.setProductURL(seoUrl
							+ BBBInternationalShippingConstants.SKU_ID_IN_URL
							+ skuId);
				}
			}
		}
		//changes to set url of sku which is added to registry
		if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || (BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel()))) {
			skuVO.setDisplayURL(skuVO.getDisplayName().replaceAll("[^A-Za-z0-9]+", "-"));
			
		}
		return skuVO;
	}

	/**
	 * This method is used to invoke web service to link coreg to reg.
	 * 
	 * @param regReqVO
	 *            the reg req vo
	 * @param email
	 *            the email
	 * @param profileId
	 *            the profile id
	 * @return RegistryResVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO linkCoRegistrantToRegistries(
			final RegistryReqVO regReqVO, final String email,
			final String profileId) throws BBBBusinessException,
			BBBSystemException {

		this.logDebug("GiftRegistryTools.linkCoRegistrantToRegistries() method start");

		// invoking LinkCoRegToReg web service
		if (StringUtils.isEmpty(regReqVO.getRegistryId())) {
			regReqVO.setRegistryIdWS(0);
		} else {
			regReqVO.setRegistryIdWS(Long.parseLong(regReqVO.getRegistryId()));
		}
		final RegistryResVO linkCoRegToRegVO = (RegistryResVO)extractUtilMethod(regReqVO);

		this.logDebug("GiftRegistryTools.linkCoRegistrantToRegistries() method ends");
		return linkCoRegToRegVO;
	}

	/**
	 * This method is used to invoke web service to link coreg to reg.
	 * 
	 * @param regReqVO
	 *            the reg req vo
	 * @param email
	 *            the email
	 * @param profileId
	 *            the profile id
	 * @return RegistryResVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO linkCoRegProfileToReg(final RegistryReqVO regReqVO,
			boolean regItemsWSCall) throws BBBBusinessException,
			BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.linkCoRegProfileToReg()");
		this.logDebug("GiftRegistryTools.linkCoRegProfileToReg() method start");
		this.logDebug("Method Description : linking Co regisrant profile with registry from webservice/DB depanding upon Flag, returns RegistryResVO");
		RegistryResVO linkCoRegProfileToReg = null;
		if (regItemsWSCall) {
			this.logDebug("Invoking ServiceHanlder invoke() method to invoke web service as RegItemsWSCall flag is :: "
					+ regItemsWSCall);
			linkCoRegProfileToReg = (RegistryResVO)extractUtilMethod(regReqVO);
		} else {
			this.logDebug("Invoking Stored procedure directly as RegItemsWSCall flag is :: "
					+ regItemsWSCall
					+ " With parameters Email Id :: "
					+ regReqVO.getEmailId()
					+ " Co Reg Profile Id :: "
					+ regReqVO.getProfileId());
			CallableStatement callableStmt = null;
			Connection connection = null;

			try {
				connection = ((GSARepository) getRegistryInfoRepository())
						.getDataSource().getConnection();
				if (connection != null) {
					callableStmt = connection
							.prepareCall(BBBGiftRegistryConstants.UPD_COREG_PROFILE_BY_EMAIL);
					callableStmt.setString(1, regReqVO.getEmailId());
					callableStmt.setString(2, regReqVO.getProfileId());
					callableStmt.setLong(3, getStoreNum(regReqVO.getSiteId()));
					callableStmt.setString(4,
							getCreateProgram(regReqVO.getSiteId()));
					callableStmt.setString(5, this.getRowXngUser());
					callableStmt.registerOutParameter(6, OracleTypes.CURSOR);
					callableStmt.registerOutParameter(7, OracleTypes.VARCHAR);
					callableStmt.registerOutParameter(8, OracleTypes.VARCHAR);
					extractDBCall(callableStmt);
					linkCoRegProfileToReg = new RegistryResVO();
					linkCoRegProfileToReg
							.setServiceErrorVO(new ServiceErrorVO());
				}
			} catch (SQLException exception) {
				if (exception.getMessage()!= null && exception.getMessage().contains(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
					logInfo("GiftRegistryTools.linkCoRegProfileToReg() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
				} else {

				logError(BBBGiftRegistryConstants.ERROR_LOG_SQL_EXCEPTION,
						exception);
				}
				linkCoRegProfileToReg = new RegistryResVO();
				ServiceErrorVO errorVO = (ServiceErrorVO) getGiftRegUtils()
						.logAndFormatError("linkCoRegProfileToRegistry",
								connection, "serviceErrorVO", exception,
								regReqVO.getSiteId(), regReqVO.getEmailId(),
								regReqVO.getProfileId());
				linkCoRegProfileToReg.setServiceErrorVO(errorVO);
			}finally{
				closeLinkCoRegToProfileRegResources(callableStmt, connection);
				this.logDebug("GiftRegistryTools.linkCoRegProfileToReg() method ends");
				BBBPerformanceMonitor.end("GiftRegistryTools.linkCoRegProfileToReg()");
			}
		}
		return linkCoRegProfileToReg;
	}

	/**
	 * @param callableStmt
	 * @param connection
	 * @throws BBBSystemException
	 */
	protected void closeLinkCoRegToProfileRegResources(CallableStatement callableStmt, Connection connection)
			throws BBBSystemException {
		try {
			if(callableStmt != null){
				callableStmt.close();
			}
			if(connection != null){
				connection.close();
			}
		} catch (SQLException e) {
				throw new BBBSystemException("Error in closing connection/Callable Statement", e.getMessage());
		}
	}

	/**
	 * Converts Date to String.
	 * 
	 * @param date
	 *            the date
	 * @return String
	 */
	static String dateToStringConverter(final Date date, String pDateFormat) {

		String dateFormat = pDateFormat;
		if (BBBUtility.isEmpty(dateFormat)) {
			dateFormat = BBBCoreConstants.DATE_FORMAT;
		}

		final SimpleDateFormat dateformatMMDDYYYY = new SimpleDateFormat(
				dateFormat, ServletUtil.getCurrentRequest().getLocale());
		if(date!=null)
		return (dateformatMMDDYYYY.format(date).toString());
		else{
			return null;
		}
	}

	/**
	 * Converts String to Date.
	 * 
	 * @param date
	 *            the date
	 * @return Date
	 * @throws ParseException
	 *             the parse exception
	 */
	public Date compareDate(final String date) throws ParseException {
		this.logDebug("GiftRegistryTools.compareDate() method start");
		final SimpleDateFormat dateFormat = new SimpleDateFormat(
				BBBCoreConstants.DATE_FORMAT, ServletUtil.getCurrentRequest()
						.getLocale());
		if (null == date || BBBCoreConstants.STRING_ZERO.equalsIgnoreCase(date) || BBBCoreConstants.BLANK.equals(date) ) {
			return null;
		}
		Date convertedDate;
		convertedDate = dateFormat.parse(date);
		this.logDebug("GiftRegistryTools.compareDate() method ends");

		return convertedDate;
	}

	/**
	 * This method is used to invoke webservice call to update gift registry.
	 * 
	 * @param pRegistryVO
	 *            the registry vo
	 * @return the registry res vo
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegistryResVO updateRegistry(final RegistryVO pRegistryVO)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryTools.updateRegistry() method start");

		final RegistryResVO registryResVO = (RegistryResVO)extractUtilMethod(pRegistryVO);

		this.logDebug("GiftRegistryTools.updateRegistry() method start");

		return registryResVO;
	}

	/**
	 * This method used to update registry using stored procedures.
	 * 
	 * @param registryVO
	 * @param registryHeaderVO
	 * @param registrantVOs
	 * @param shippingVOs
	 * @param registryBabyVO
	 * @param registryPrefStoreVO
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	public RegistryResVO updateRegistry(RegistryVO registryVO,
			RegistryHeaderVO registryHeaderVO, List<RegNamesVO> registrantVOs,
			List<RegNamesVO> shippingVOs, RegistryBabyVO registryBabyVO,
			RegistryPrefStoreVO registryPrefStoreVO) throws BBBSystemException,
			BBBBusinessException {

		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.updateRegistry() method start");
		}
		RegistryResVO registryResVO = new RegistryResVO();
		boolean isUpdateSuccess = false;
		Connection connection = null;
		boolean isError = false;
		Transaction pTransaction = null;
		try {
			pTransaction = this.ensureTransaction();
			String rowXNGUser = this.getRowXngUser();

			connection = ((GSARepository) getRegistryInfoRepository())
					.getDataSource().getConnection();
			if(connection!=null){
			connection.setAutoCommit(false);
			}
			/**
			 * Step 1: Update registry header
			 */
			this.updateRegistryHeader(registryHeaderVO, connection, rowXNGUser,
					registryHeaderVO.getRegNum(), registryHeaderVO.getJdaDate());
			/***
			 * Step 2: Update Registry Names Entry for registrant &
			 * co-registrant
			 */
			if (!BBBUtility.isEmpty(registryHeaderVO.getRegNum())) {
				this.insertOrUpdateRegNames(registrantVOs, connection,
						rowXNGUser, registryHeaderVO.getRegNum(),
						registryHeaderVO.getJdaDate(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.UPDATE_REG_NAMES);
			}

			/***
			 * Step 3: Update Shipping Registry Names Entry
			 */
			if (!BBBUtility.isEmpty(registryHeaderVO.getRegNum())) {
				this.insertOrUpdateRegNames(shippingVOs, connection,
						rowXNGUser, registryHeaderVO.getRegNum(),
						registryHeaderVO.getJdaDate(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.UPDATE_REG_NAMES);
			}

			/***
			 * Step 4: Disable Future Shipping, if future shipping is not opted.
			 */
			if (shippingVOs != null && 1 == shippingVOs.size()) {
				this.disableFutureShipping(connection, rowXNGUser,
						registryHeaderVO.getRegNum(),
						registryHeaderVO.getSiteId());
			}

			/***
			 * Step 5: Update Registry Baby Entry, if event type is BA1
			 */
			if (BBBGiftRegistryConstants.EVENT_TYPE_BABY
					.equalsIgnoreCase(registryHeaderVO.getEventType())) {
				this.insertOrUpdateRegBaby(registryBabyVO, connection,
						rowXNGUser, registryHeaderVO.getRegNum(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.UPDATE_REG_BABY);
			}

			/***
			 * Step 6: Update Registry Pref Store Entry, if store number is
			 * available.
			 */
			if (!BBBUtility.isEmpty(registryPrefStoreVO.getStoreNum())) {
				this.insertOrUpdateRegPrefStore(registryPrefStoreVO,
						connection, rowXNGUser, registryHeaderVO.getRegNum(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.UPDATE_REG_PREF_STORE);
			}
			registryResVO.setRegistryId(Long.parseLong(registryHeaderVO
					.getRegNum()));
			isUpdateSuccess = true;
		} catch (final SQLException e) {
			if (e.getMessage()!= null && e.getMessage().contains(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
				logInfo("GiftRegistryTools.updateRegistry() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
			} 
			else {
			isError = true;
			logError("Exception while update registry: " + e);
			}
			Object[] args = this.getGiftRegUtils()
					.populateInputToLogErrorOrValidate(registryVO);
			ServiceErrorVO errorVO = (ServiceErrorVO) getGiftRegUtils()
					.logAndFormatError("UpdateRegistry", null,
							"ServiceErrorVO", e, args);
			registryResVO.setServiceErrorVO(errorVO);
		} catch (NotSupportedException exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "NotSupportedException in GiftRegistryTools.UpdateRegistry", "UpdateRegistry"), exc);
		} catch (SystemException exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "SystemException in in GiftRegistryTools.UpdateRegistry", "UpdateRegistry"), exc);
		}  catch (Exception exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "Exception in in GiftRegistryTools.UpdateRegistry", "UpdateRegistry"), exc);
		} finally {
			this.endTransaction(isError, pTransaction);
			if (connection != null)
				closeUpdateRegistryConnection(connection);
		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.updateRegistry() method ends with success message: "
					+ isUpdateSuccess);
		}
		return registryResVO;
	}

	/**
	 * @param connection
	 * @throws BBBSystemException
	 */
	protected void closeUpdateRegistryConnection(Connection connection) throws BBBSystemException {
		try {
			connection.close();
		} catch (final SQLException e) {
			throw new BBBSystemException(
					"SQLException occured while commit/closing the connection",
					e);
		}
	}

	/**
	 * This method is used to invoke webservice call to update gift registry
	 * with ATG info if flag is true , else direct call to db
	 * 
	 * @param pProfileSyncRequestVO
	 *            the profile sync request vo ,regItemsWSCall
	 * @return the profile sync response vo
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public ProfileSyncResponseVO updateRegistryWithAtgInfo(
			final ProfileSyncRequestVO pProfileSyncRequestVO,
			boolean regItemsWSCall) throws BBBBusinessException,
			BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.updateRegistryWithAtgInfo()");
		this.logDebug("GiftRegistryTools.updateRegistryWithAtgInfo() method start");
		this.logDebug("Method Description : method is used to update gift registry using WebService/DBCall");
		ProfileSyncResponseVO profileSyncResponseVO = null;
		if (regItemsWSCall) {
			this.logDebug("Updating gift registry using WebServiceCall,Value for WSCallFlag : " + regItemsWSCall);
			profileSyncResponseVO = (ProfileSyncResponseVO)extractUtilMethod(pProfileSyncRequestVO);
		} else {
			this.logDebug("Updating gift registry using Stored Procedure, Value for WSCallFlag : " + regItemsWSCall);
			Connection con = null;
			CallableStatement cs = null;
			try {
				con = ((GSARepository) getRegistryInfoRepository())
						.getDataSource().getConnection();

				if (con != null) {
					// Step-3: prepare the callable statement
					 cs = con
							.prepareCall(BBBGiftRegistryConstants.UPD_REG_NAMES_BY_PROFILE_ID);

					// Step-4: set input parameters ...
					// first input argument
					cs.setString(1, pProfileSyncRequestVO.getProfileId());
					cs.setString(2, pProfileSyncRequestVO.getLastName());
					cs.setString(3, pProfileSyncRequestVO.getFirstName());
					cs.setString(4, pProfileSyncRequestVO.getPhoneNumber());
					cs.setString(5, pProfileSyncRequestVO.getMobileNumber());
					cs.setString(6, null);
					cs.setString(7, null);
					cs.setString(
							8,
							pProfileSyncRequestVO.getEmailAddress() != null ? pProfileSyncRequestVO
									.getEmailAddress().toUpperCase() : "");
					cs.setLong(9,
							getStoreNum(pProfileSyncRequestVO.getSiteFlag()));
					cs.setString(10, getCreateProgram(pProfileSyncRequestVO
							.getSiteFlag()));
					// Getting RowXng user corresponding to data center
					cs.setString(11, getRowXngUser());

					// execute the stored procedures:
					// UPD_REG_NAMES_BY_PROFILE_ID
					cs.executeUpdate();
					profileSyncResponseVO = new ProfileSyncResponseVO();
					ErrorStatus errorStatus = new ErrorStatus();
					errorStatus.setErrorExists(false);
					profileSyncResponseVO = new ProfileSyncResponseVO();
					profileSyncResponseVO.setErrorStatus(errorStatus);

				}
			} catch (SQLException ex) {
				if (ex.getMessage()!= null && ex.getMessage().contains(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
					logInfo("GiftRegistryTools.updateRegistryWithAtgInfo() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
				}
				profileSyncResponseVO = new ProfileSyncResponseVO();
				ErrorStatus errorStatus = (ErrorStatus) getGiftRegUtils()
						.logAndFormatError("UpdateRegistryWithAtgInfo", con,
								BBBGiftRegistryConstants.ERROR_STATUS, ex,
								pProfileSyncRequestVO.getProfileId(),
								pProfileSyncRequestVO.getEmailAddress(),
								pProfileSyncRequestVO.getLastName(),
								pProfileSyncRequestVO.getFirstName(),
								pProfileSyncRequestVO.getPhoneNumber(),
								pProfileSyncRequestVO.getMobileNumber());
				profileSyncResponseVO.setErrorStatus(errorStatus);

			} finally {
				try {
					if(cs != null)
					{
						cs.close();
					}
					if (con != null)
						con.close();
				} catch (SQLException e) {
					this.logError("Error occurred while closing connection");
				}
				this.logDebug("GiftRegistryTools.updateRegistryWithAtgInfo() method ends");
				BBBPerformanceMonitor.end("GiftRegistryTools.updateRegistryWithAtgInfo()");
			}
		}
		return profileSyncResponseVO;
	}

	/**
	 * @param ServiceRequestIF
	 * @return ServiceResponseIF
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected ServiceResponseIF extractUtilMethod(
			final ServiceRequestIF request) throws BBBBusinessException, BBBSystemException {
		return ServiceHandlerUtil.invoke(request);
	}

	public String getDefaultCreateProgram(String siteFlag) {
		String createProgram = "";
		if (siteFlag.equals("1")) {
			createProgram = BBBGiftRegistryConstants.US_COM;
		} else if (siteFlag.equals("2")) {
			createProgram = BBBGiftRegistryConstants.BAB_COM;
		} else if (siteFlag.equals("3")) {
			createProgram = BBBGiftRegistryConstants.CAN_COM;
		}
		return createProgram;
	}
	
	public String getCreateProgram(String siteFlag) {
		String createProgram = this.getDefaultCreateProgram(siteFlag);		
		try {

			final List<String> lastMaintProgram = this.getCatalogTools()
					.getAllValuesForKey( 
		    				BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,
							BBBCoreConstants.LAST_MAINT_PROGRAM);
			if (!BBBUtility.isListEmpty(lastMaintProgram)) {
				createProgram = lastMaintProgram.get(0);
			}
		}		
		catch (BBBSystemException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBSystemException from service of Create Program : getCreateProgram"), e);

		} catch (BBBBusinessException e) {
			logError(LogMessageFormatter.formatMessage(null,
					"BBBBusinessException from service of  Create Program : getCreateProgram"), e);

		}
		return createProgram;	
	}

	public long getStoreNum(String siteFlag) {

		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.getStoreNum() method start for site flag :: "
					+ siteFlag);
		}
		long storenum = 0;
		List<String> storeNumList = null;
		try {
			storeNumList = getCatalogTools().getAllValuesForKey(
					BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
					"DefaultStoreId");
			if (!BBBUtility.isListEmpty(storeNumList)) {
				storenum = Long.parseLong(storeNumList.get(0));
			}
		} catch (BBBSystemException e) {
			logError("BBBSystemException occurred while fetching Config Key "
					+ e);
		} catch (BBBBusinessException e) {
			logError("BBBBusinessException occurred while fetching Config Key "
					+ e);
		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.getStoreNum() method ends :: [Store Number] = "
					+ storenum);
		}
		return storenum;
	}

	/**
	 * This method will call getRegistryStatusesByProfileId web service.
	 * 
	 * @param registryVO
	 *            the registry vo
	 * @return ValidateRegistryResVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */

	public RegStatusesResVO getRegistriesStatus(
			final RegistryStatusVO registryStatusVO)
			throws BBBBusinessException, BBBSystemException {
		this.logDebug("GiftRegistryTools.getRegistriesStatus() method start");

		final RegStatusesResVO regStatusesResVO = (RegStatusesResVO)extractUtilMethod(registryStatusVO);

		this.logDebug("GiftRegistryTools.getRegistriesStatus() method ends");
		return regStatusesResVO;
	}


	/**
	 * This method will fetch getRegistryStatusesByProfileId .
	 * 
	 * @param registryVO
	 *            the registry vo
	 * @return ValidateRegistryResVO
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public RegStatusesResVO GetRegistryStatusesByProfileId(
			final RegistryStatusVO registryStatusVO) throws BBBSystemException,
			BBBBusinessException {
		BBBPerformanceMonitor.start("GiftRegistryTools.GetRegistryStatusesByProfileId()");
		this.logDebug("GiftRegistryTools.GetRegistryStatusesByProfileId() method start");
		this.logDebug("Method Description : method is used to fetch registry staus using profile Id");
		String profileId = registryStatusVO.getProfileId();
		String siteId = registryStatusVO.getSiteId();
		RegStatusesResVO regStatusResVo = new RegStatusesResVO();
		Connection con = null;
		ResultSet resultSet = null;
		CallableStatement cs = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				final String getRegStatusStoredProc = BBBGiftRegistryConstants.GET_REG_STATUSES_BY_PROFILEID;
				
				cs = con.prepareCall(getRegStatusStoredProc);
				cs.setString(1, siteId);
				cs.setString(2, profileId);
				this.logDebug("GiftRegistryTools.GetRegistryStatusesByProfileId() | parameters passed into procedure are profileId : "
						+ profileId + " and siteId : " + siteId);
				cs.registerOutParameter(3, OracleTypes.CURSOR);
				extractDBCall(cs);
				this.logDebug("GiftRegistryTools.GetRegistryStatusesByProfileId() | procedure "
						+ getRegStatusStoredProc + " executed from method ");
				resultSet = (ResultSet) cs.getObject(3);
				List<RegistryStatusVO> listRegistryStatusVO = new ArrayList<RegistryStatusVO>();
				if (null != resultSet) {
					while (resultSet.next()) {
						RegistryStatusVO tempRegistryStatusVO = new RegistryStatusVO();
						tempRegistryStatusVO.setRegistryId(resultSet
								.getString("REGISTRY_NUM"));
						tempRegistryStatusVO.setStatusCode(resultSet
								.getString("STATUS_CD"));
						tempRegistryStatusVO.setStatusDesc(resultSet
								.getString("STATUS_DESC"));
						listRegistryStatusVO.add(tempRegistryStatusVO);
					}
					regStatusResVo
							.setListRegistryStatusVO(listRegistryStatusVO);
				}
			}
		} catch (Exception excep) {
			logError("SQL exception while registry header info "
					+ "in GiftRegistryTools", excep);
			
			/*========== Code change for internal ticket BBB-101(ClasscastException) ===============*/
			ServiceErrorVO serviceErrorVO = (ServiceErrorVO) getGiftRegUtils()
					.logAndFormatError("GetRegistryStatusesByProfileId", con,
							BBBGiftRegistryConstants.SERVICE_ERROR_VO, excep,
							BBBCoreConstants.BLANK, profileId, siteId);
			regStatusResVo.setServiceErrorVO(serviceErrorVO);
			return regStatusResVo;
		} finally {
			closeRegistryInfoRepoResources(cs, con, resultSet);
			this.logDebug("GiftRegistryTools.GetRegistryStatusesByProfileId() method ends");
			BBBPerformanceMonitor.end("GiftRegistryTools.GetRegistryStatusesByProfileId()");
		}
		return regStatusResVo;
	}

	/**
	 * parsing JASON Objects.
	 * 
	 * @param giftRegistryViewBean
	 *            the gift registry view bean
	 * @param pJsonResultString
	 *            the json result string
	 * @return the gift registry view bean
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 * @throws BBBSystemException
	 *             the bBB system exception
	 */
	public GiftRegistryViewBean addItemJSONObjectParser(
			final GiftRegistryViewBean giftRegistryViewBean,
			final String pJsonResultString) throws BBBBusinessException,
			BBBSystemException {
		this.logDebug("GiftRegistryTools.addItemJSONObjectParser() method start");
		int totQuantity = 0;
		final String logMessage = this.getClass().getName()
				+ "StoreJSONObjectParser";
		this.logDebug(logMessage + " Starts here");
		this.logDebug(logMessage + " add item input parameters --> "
				+ pJsonResultString);

		JSONObject jsonObject = null;
		jsonObject = (JSONObject) JSONSerializer.toJSON(pJsonResultString);
		final DynaBean JSONResultbean = (DynaBean) JSONSerializer.toJava(jsonObject);
		final List<String> dynaBeanProperties = this
				.getPropertyNames(JSONResultbean);

		if (dynaBeanProperties.contains(BBBGiftRegistryConstants.ADD_ITEMS)) {
			final List<DynaBean> itemArray = (ArrayList<DynaBean>) JSONResultbean
					.get(BBBGiftRegistryConstants.ADD_ITEMS);
			// DynaBean productIdArray =
			final List<AddItemsBean> additemList = new ArrayList<AddItemsBean>();
			String omniProductList = "";
			for (final DynaBean item : itemArray) {
				final List<String> fieldsBeanProperties = this
						.getPropertyNames(item);
				final AddItemsBean addItemsBean = new AddItemsBean();
				addItemsBean.setSku(item.get(BBBGiftRegistryConstants.SKU_ID)
						.toString());
				if (fieldsBeanProperties.contains("paramRegID")) {
					final Object obj = item.get("paramRegID");
					if ((obj != null)
							&& !StringUtils.isBlank(obj.toString())
							&& !obj.toString().equalsIgnoreCase(
									BBBCoreConstants.NULL_VALUE)) {
						addItemsBean.setParamRegistryId(item.get("paramRegID")
								.toString());
					} else {
						addItemsBean.setParamRegistryId(null);
					}

				}
				if (fieldsBeanProperties.contains("skuId")) {
					addItemsBean.setSku(item.get("skuId").toString());

				}
				if (fieldsBeanProperties
						.contains(BBBCoreConstants.IS_CUSTOMIZATION_REQUIRED)) {
					if (item.get(BBBCoreConstants.IS_CUSTOMIZATION_REQUIRED) != null) {
						addItemsBean.setCustomizationRequired(item.get(
								BBBCoreConstants.IS_CUSTOMIZATION_REQUIRED)
								.toString());
					} else {
						addItemsBean
								.setCustomizationRequired(BBBCoreConstants.FALSE);
					}

				} else {
					addItemsBean
							.setCustomizationRequired(BBBCoreConstants.FALSE);
				}
				if (fieldsBeanProperties
						.contains(BBBCoreConstants.PERSONALIZATION_TYPE)) {
					if (item.get(BBBCoreConstants.PERSONALIZATION_TYPE) != null) {
						addItemsBean.setPersonalizationCode(item.get(
								BBBCoreConstants.PERSONALIZATION_TYPE)
								.toString());
					} else {
						addItemsBean.setPersonalizationCode(null);
					}
				}
				if (fieldsBeanProperties
						.contains(BBBCoreConstants.REFERENCE_NUMBER_PARAM)) {
					if (item.get(BBBCoreConstants.REFERENCE_NUMBER_PARAM) != null) {
						addItemsBean.setRefNum(item.get(
								BBBCoreConstants.REFERENCE_NUMBER_PARAM)
								.toString());
					}
				}
				// Added for populating LTL ship method
				if (fieldsBeanProperties
						.contains(BBBGiftRegistryConstants.LTL_SHIP_METHOD)
						&& item.get(BBBGiftRegistryConstants.LTL_SHIP_METHOD) != null) {
					if (BBBUtility.isNotEmpty((String) item
							.get(BBBGiftRegistryConstants.LTL_SHIP_METHOD))) {
						addItemsBean.setLtlDeliveryServices(item.get(
								BBBGiftRegistryConstants.LTL_SHIP_METHOD)
								.toString());
						addItemsBean
								.setLtlDeliveryPrices(BBBCoreConstants.BLANK
										+ (this.getCatalogTools().getDeliveryCharge(
												extractSiteId(),
												addItemsBean.getSku(),
												addItemsBean
														.getLtlDeliveryServices())));
					} else {
						addItemsBean
								.setLtlDeliveryServices(BBBCoreConstants.BLANK);
					}
				}
				if (fieldsBeanProperties
						.contains(BBBGiftRegistryConstants.REGISTRY_ID)) {
					addItemsBean.setRegistryId(item.get(
							BBBGiftRegistryConstants.REGISTRY_ID).toString());

				} else {
					addItemsBean.setRegistryId(null);
				}
				
				giftRegistryViewBean
						.setRegistryId(addItemsBean.getRegistryId());
				

				addItemsBean.setProductId(item.get(
						BBBGiftRegistryConstants.PROD_ID).toString());
				final double listPrice = this
						.getCatalogTools()
						.getListPrice(addItemsBean.getProductId(),
								addItemsBean.getSku()).doubleValue();
				final double salePrice = this
						.getCatalogTools()
						.getSalePrice(addItemsBean.getProductId(),
								addItemsBean.getSku()).doubleValue();
				if ((salePrice > 0)) {
					addItemsBean.setPrice(Double.toString(salePrice));
				} else {
					addItemsBean.setPrice(Double.toString(listPrice));
				}
				addItemsBean.setQuantity(item.get(
						BBBGiftRegistryConstants.QUANTITY).toString());
				if (item.get(BBBGiftRegistryConstants.QUANTITY).toString() != null) {
					totQuantity += Integer.parseInt(item.get(
							BBBGiftRegistryConstants.QUANTITY).toString());
				}
				
				//This code will execute only for non-personlized sku's. 
				//For personalized sku's the omniture string is created and set in GiftRegistryFormHandler
				if(BBBUtility.isEmpty(addItemsBean.getRefNum())){
					String totalPrice = this.totalPrice(addItemsBean.getQuantity(),
							addItemsBean.getPrice());
					omniProductList += ";" + addItemsBean.getProductId()
							+ ";;;event22=" + addItemsBean.getQuantity()
							+ "|event23=" + totalPrice + ";eVar30="
							+ addItemsBean.getSku() + ",";
					final DynamoHttpServletRequest pRequest = ServletUtil
							.getCurrentRequest();
					BBBSessionBean sessionBean = (BBBSessionBean) pRequest
							.resolveName(BBBCoreConstants.SESSION_BEAN);
					sessionBean.setRegistryEvar23Price(totalPrice);
				}
				if  (null != giftRegistryViewBean.getInternationalContext()
						&& giftRegistryViewBean.getInternationalContext()) {
					String userCountry = giftRegistryViewBean.getUserCountry();
					String userCurrency = giftRegistryViewBean
							.getUserCurrency();
					Properties p = new Properties();
					// totalPrice=
					// getBbbCustomTagComponent().format(totalPrice,userCurrency,
					// userCountry,"Y",p);
				}
				
						if (fieldsBeanProperties
						.contains(BBBCatalogConstants.IS_LTL_SKU)) {
					if(item.get(BBBCatalogConstants.IS_LTL_SKU) != null)
					{
						addItemsBean.setLtlFlag(item.get(BBBCatalogConstants.IS_LTL_SKU).toString());
					}
					else
					{
						addItemsBean.setLtlFlag(BBBCoreConstants.FALSE);
					}

				} 
				additemList.add(addItemsBean);

			}
			if (dynaBeanProperties.contains("parentProdId") && null!=JSONResultbean.get(BBBGiftRegistryConstants.PARENT_PROD_ID)) {
				final String parentProdId = JSONResultbean.get(
						BBBGiftRegistryConstants.PARENT_PROD_ID).toString();
				if (null != parentProdId) {
					giftRegistryViewBean.setParentProductId(parentProdId);
				}
				if (dynaBeanProperties.contains("registryName") && null!=JSONResultbean.get("registryName")) {
					final String registryName = JSONResultbean.get(
							"registryName").toString();
					if (null != registryName) {
						giftRegistryViewBean.setRegistryName(registryName);
					}
				}
				// adding consultant name
				if (dynaBeanProperties.contains("heading1") && null!=JSONResultbean.get("heading1")) {
					final String consultantName = JSONResultbean
							.get("heading1").toString();
					if (null != consultantName) {
						giftRegistryViewBean.setConsultantName(consultantName);
					}
				}

			}
			if (dynaBeanProperties.contains("repositoryId") && null!=JSONResultbean.get("repositoryId")) {
				final String repositoryId = JSONResultbean.get("repositoryId").toString();
				if (null != repositoryId)
					giftRegistryViewBean.setRepositoryId(repositoryId);

			}
			if (dynaBeanProperties.contains("isDeclined") && null!=JSONResultbean.get("isDeclined")) {
				final String isDeclined = JSONResultbean.get("isDeclined").toString();
				if (null != isDeclined)
					giftRegistryViewBean.setIsDeclined(isDeclined);

			}
			if (dynaBeanProperties.contains("isFromPendingTab") && null!=JSONResultbean.get("isFromPendingTab")) {
				final String isFromPendingTab = JSONResultbean.get(
						"isFromPendingTab").toString();
				if (null != isFromPendingTab)
					giftRegistryViewBean.setIsFromPendingTab(isFromPendingTab);
			}
			// BPS-1381 Integrate Declined Tab
			if (dynaBeanProperties.contains("isFromDeclinedTab") && null!=JSONResultbean.get("isFromDeclinedTab")) {
				final String isFromDeclinedTab = JSONResultbean.get(
						"isFromDeclinedTab").toString();
				if (null != isFromDeclinedTab)
					giftRegistryViewBean
							.setIsFromDeclinedTab(isFromDeclinedTab);
			}

			giftRegistryViewBean.setTotQuantity(totQuantity);
			if(BBBUtility.isNotEmpty(omniProductList)){
				omniProductList = omniProductList.substring(0,
					omniProductList.lastIndexOf(","));
				giftRegistryViewBean.setOmniProductList(omniProductList);
			}
			giftRegistryViewBean.setAdditem(additemList);

		}
		this.logDebug("GiftRegistryTools.addItemJSONObjectParser() method ends");

		return giftRegistryViewBean;
	}


	/**
	 * This method takes wishlist repository item and populated the add item
	 * bean with the properties of the wishlist item. This bean is then used to
	 * add wishlist item to registry
	 * 
	 * @param wishListItem
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */

	public AddItemsBean populateRegistryItemWIthWishListItem(
			final RepositoryItem wishListItem) throws BBBBusinessException,
			BBBSystemException {
		final AddItemsBean addItemsBean = new AddItemsBean();
		addItemsBean
				.setSku((String) wishListItem
						.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_CATALOG_REF_ID));
		addItemsBean
				.setProductId((String) wishListItem
						.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_PRODUCT_ID));
		final double listPrice = this
				.getCatalogTools()
				.getListPrice(addItemsBean.getProductId(),
						addItemsBean.getSku()).doubleValue();
		final double salePrice = this
				.getCatalogTools()
				.getSalePrice(addItemsBean.getProductId(),
						addItemsBean.getSku()).doubleValue();
		if ((salePrice > 0)) {
			addItemsBean.setPrice(Double.toString(salePrice));
		} else {
			addItemsBean.setPrice(Double.toString(listPrice));
		}
		final String quantity = wishListItem.getPropertyValue(
				BBBGiftRegistryConstants.WISHLIST_PROPERTY_QTY_DESIRED)
				.toString();
		addItemsBean.setQuantity(quantity);

		return addItemsBean;

	}

	/*
	 * To get the properties names from JSON result string
	 */
	/**
	 * Gets the property names.
	 * 
	 * @param pDynaBean
	 *            the dyna bean
	 * @return the property names
	 */
	public List<String> getPropertyNames(final DynaBean pDynaBean) {
		this.logDebug("GiftRegistryTools.getPropertyNames() method start");

		final DynaClass dynaClass = pDynaBean.getDynaClass();
		final DynaProperty properties[] = dynaClass.getDynaProperties();
		final List<String> propertyNames = new ArrayList<String>();
		for (final DynaProperty propertie : properties) {
			final String name = propertie.getName();
			propertyNames.add(name);
		}
		this.logDebug("GiftRegistryTools.getPropertyNames() method ends");

		return propertyNames;
	}

	/**
	 * Changed registry type name.
	 * 
	 * @param regSearchResVO
	 *            the reg search res vo
	 * @return the reg search res vo
	 * @throws BBBSystemException
	 *             the bBB system exception
	 * @throws BBBBusinessException
	 *             the bBB business exception
	 */
	public RegSearchResVO changedRegistryTypeName(
			final RegSearchResVO regSearchResVO, final String siteId)
			throws BBBSystemException, BBBBusinessException {
		this.logDebug("GiftRegistryTools.changedRegistryTypeName() method start");
		if (regSearchResVO.getListRegistrySummaryVO() != null) {
			for (int i = 0; i < regSearchResVO.getListRegistrySummaryVO()
					.size(); i++) {
				final RegistrySummaryVO registrySummaryVO = regSearchResVO
						.getListRegistrySummaryVO().get(i);

				final String eventCode = registrySummaryVO.getEventType();
				registrySummaryVO.setEventCode(eventCode);
				registrySummaryVO.setEventType(this.getCatalogTools()
						.getRegistryTypeName(eventCode, siteId));
			}
		}
		this.logDebug("GiftRegistryTools.changedRegistryTypeName() method ends");

		return regSearchResVO;
	}

	/**
	 * This method will update the registry status in database.
	 * 
	 * @param registrySkinnyVO
	 * @param siteId
	 * @param statusDesc
	 *            status from legacy system
	 * @throws BBBSystemException
	 */
	public void updateRegistryStatus(final RegistrySkinnyVO registrySkinnyVO,
			final String siteId, final String statusDesc)
			throws BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.updateRegistryStatus()");
		this.logDebug("GiftRegistryTools.updateRegistryStatus() method start");
		this.logDebug("Method Description : Updatign registry status in DB");
		MutableRepositoryItem giftRegistryItem = null;
		final MutableRepository giftRegistryRepository = this
				.getGiftRepository();
		try {

			RepositoryItem[] grRepositoryItems = null;

			// Check if item already exist or not
			try {
				grRepositoryItems = this.fetchGiftRepositoryItem(siteId,
						registrySkinnyVO.getRegistryId());
			} catch (final BBBBusinessException e) {
				this.logError(
						BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10130
								+ " BBBBusinessException from updateRegistryStatus of GiftRegistryTools",
						e);
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						e);
			}

			if (grRepositoryItems != null) {

				giftRegistryItem = (MutableRepositoryItem) grRepositoryItems[0];
				giftRegistryItem.setPropertyValue("registryStatus", statusDesc);
				giftRegistryRepository.updateItem(giftRegistryItem);
			}

		} catch (final RepositoryException e) {
			this.logError(
					BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131
							+ " RepositoryException from updateRegistryStatus of GiftRegistryTools",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}finally{
			BBBPerformanceMonitor.end("GiftRegistryTools.updateRegistryStatus()");
			this.logDebug("GiftRegistryTools.updateRegistryStatus() method ends.");
		}
	}

	/**
	 * Total price.
	 * 
	 * @param quantity
	 *            the quantity
	 * @param price
	 *            the price
	 * @return the string
	 */
	public String totalPrice(final String quantity, String pPrice) {
		this.logDebug("GiftRegistryTools.totalPrice() method start");

		double totalPrice = 0;
		String price = pPrice;
		if (!BBBUtility.isEmpty(quantity) && !BBBUtility.isEmpty(price)) {
			if (price.startsWith("$")) {
				price = price.substring(1);
			}
			totalPrice = Integer.parseInt(quantity) * Double.parseDouble(price);
		}
		this.logDebug("GiftRegistryTools.totalPrice() method ends");

		return String.valueOf(totalPrice);
	}

	/**
	 * @return the userRegistryQuery
	 */
	public String getUserRegistryQuery() {
		return this.userRegistryQuery;
	}

	/**
	 * @param userRegistryQuery
	 *            the userRegistryQuery to set
	 */
	public void setUserRegistryQuery(final String userRegistryQuery) {
		this.userRegistryQuery = userRegistryQuery;
	}

	/**
	 * Gets the gift repo item query.
	 * 
	 * @return the giftRepoItemQuery
	 */
	public String getGiftRepoItemQuery() {
		return this.giftRepoItemQuery;
	}

	/**
	 * Sets the gift repo item query.
	 * 
	 * @param pGiftRepoItemQuery
	 *            the giftRepoItemQuery to set
	 */
	public void setGiftRepoItemQuery(final String pGiftRepoItemQuery) {
		this.giftRepoItemQuery = pGiftRepoItemQuery;
	}

	/**
	 * Gets the catalog tools.
	 * 
	 * @return the mCatalogTools
	 */
	public BBBCatalogTools getCatalogTools() {
		return this.mCatalogTools;
	}

	/**
	 * Sets the catalog tools.
	 * 
	 * @param pCatalogTools
	 *            the new catalog tools
	 */
	public void setCatalogTools(final BBBCatalogTools pCatalogTools) {
		this.mCatalogTools = pCatalogTools;
	}

	/**
	 * Gets the gift repository.
	 * 
	 * @return the giftRepository
	 */
	public MutableRepository getGiftRepository() {
		return this.mGiftRepository;
	}

	/**
	 * Sets the gift repository.
	 * 
	 * @param pGiftRepository
	 *            the giftRepository to set
	 */
	public void setGiftRepository(final MutableRepository pGiftRepository) {
		this.mGiftRepository = pGiftRepository;
	}

	public RegCopyResVO copyRegistry(GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {

		RegCopyResVO regCopyResVO = (RegCopyResVO)extractUtilMethod(pGiftRegistryViewBean);
		pGiftRegistryViewBean.setTotQuantity(Integer.valueOf(regCopyResVO
				.gettotalNumOfItemsCopied()));

		this.logDebug("GiftRegistryTools.regCopy() method ends");

		return regCopyResVO;
	}

	
	/**
	 * To copy registry items from source to destination registry
	 * 
	 * @param pGiftRegistryViewBean
	 * @return regCopyResVO
	 * 
	 */

	public RegCopyResVO copyRegistryInEcomAdmin(
			GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryTools.copyRegistryInEcomAdmin() method starts....");

		RegCopyResVO regCopyResVO = new RegCopyResVO();
		ServiceErrorVO errorVO = new ServiceErrorVO();

		String[] paramsToValidate = {
				pGiftRegistryViewBean.getSourceRegistry(),
				pGiftRegistryViewBean.getTargetRegistry() };
		ErrorStatus errorStatus = new ErrorStatus();
		errorStatus = this.getGiftRegUtils().validateInput(paramsToValidate);

		if (errorStatus.isErrorExists()) {
			errorVO.setErrorExists(errorStatus.isErrorExists());
			errorVO.setErrorId(errorStatus.getErrorId());
			errorVO.setErrorMessage(errorStatus.getErrorMessage());
			regCopyResVO.setServiceErrorVO(errorVO);
			this.logDebug("GiftRegistryTools.copyRegistryInEcomAdmin() error occured while validating input parameters");
			return regCopyResVO;
		}

		// call method for rpoc call
		regCopyResVO = regCopy(pGiftRegistryViewBean);

		this.logDebug("GiftRegistryTools.copyRegistryInEcomAdmin() method ends");
		return regCopyResVO;
	}

	/**
	 * To call stored procedure for copying registry item from source registry
	 * to destination
	 * 
	 * @param pGiftRegistryViewBean
	 * @return regCopyResVO
	 * 
	 */

	public RegCopyResVO regCopy(final GiftRegistryViewBean pGiftRegistryViewBean)
			throws BBBSystemException, BBBBusinessException {
		BBBPerformanceMonitor.start("GiftRegistryTools.regCopy()");
		this.logDebug("GiftRegistryTools.regCopy() method starts....");
		this.logDebug("Method Description : copying registry item from source registry to bean");
		RegCopyResVO regCopyResVO = new RegCopyResVO();
		ServiceErrorVO error = new ServiceErrorVO();

		Connection con = null;
		CallableStatement cs = null;
		boolean isCopySuccess = false;
		boolean isError = false;
		Transaction pTransaction = null;

		try {
			pTransaction = this.ensureTransaction();
			// create connection
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();
			if (con != null) {
				con.setAutoCommit(false);
				final String copyRegistryStoreProc = BBBGiftRegistryConstants.COPY_REGISTRY;
				this.logDebug("GiftRegistryTools.regCopy() method ::: stored procedure name for copy registry:"
						+ copyRegistryStoreProc);

				// prepare the callable statement
				cs = con.prepareCall(copyRegistryStoreProc);

				// set input parameters ...
				cs.setString(1, pGiftRegistryViewBean.getSiteFlag());
				cs.setInt(2, Integer.parseInt(pGiftRegistryViewBean
						.getSourceRegistry()));
				cs.setInt(3, Integer.parseInt(pGiftRegistryViewBean
						.getTargetRegistry()));
				cs.setString(4,
						getCreateProgram(pGiftRegistryViewBean.getSiteFlag()));

				Long findStoreNumber = getStoreNum(pGiftRegistryViewBean
						.getSiteFlag());
				Integer storeNum = Integer.valueOf(findStoreNumber.intValue());
				cs.setInt(5, storeNum);

				cs.setString(6,
						getCreateProgram(pGiftRegistryViewBean.getSiteFlag()));
				cs.setString(7, getRowXngUser());

				// set output parameter
				cs.registerOutParameter(8, java.sql.Types.INTEGER);

				// execute stored procedure
				cs.executeUpdate();

				String itemsAdded = Integer.toString(cs.getInt(8));

				// set errorExists as false and itemsAdded if no exception is
				// thrown from procedure
				error.setErrorExists(false);
				regCopyResVO.setServiceErrorVO(error);
				regCopyResVO.settotalNumOfItemsCopied(itemsAdded);
				isCopySuccess = true;

				this.logDebug("GiftRegistryTools.regCopy() ::: stored proc execution completed. No of items copied : "
						+ itemsAdded);
			}
		} catch (Exception exp) {
			isError = true;
			this.logError("Error occurred while updating registry",exp);
			error = (ServiceErrorVO) this.getGiftRegUtils().logAndFormatError(
					"regCopy", null, "serviceErrorVO", exp,
					pGiftRegistryViewBean.getUserToken(),
					pGiftRegistryViewBean.getSiteFlag(),
					pGiftRegistryViewBean.getSourceRegistry(),
					pGiftRegistryViewBean.getTargetRegistry());
			regCopyResVO.setServiceErrorVO(error);
		}

		finally {
			try {
				if (cs != null)
					cs.close();
				this.endTransaction(isError, pTransaction);
				if (con != null)
					con.close();

			} catch (SQLException e) {
				this.logError("Error occurred while closing connection",e);
			}
			BBBPerformanceMonitor.end("GiftRegistryTools.regCopy()");
			this.logDebug("GiftRegistryTools.regCopy() method ends.");
		}
		return regCopyResVO;
	}

	/**
	 * To Get Print At home card templates.
	 * 
	 * @param pSiteId
	 *            - Site Id parameter
	 * @param pRegistryType
	 *            - registry type
	 */
	public RepositoryItem[] getPrintAtHomeCardTemplates(String pSiteId) {

		BBBPerformanceMonitor.start(GiftRegistryTools.class.getName() + ".getPrintAtHomeCardTemplates");
		logDebug("GiftRegistryTools : getPrintAtHomeCardTemplates : START");

		try {
			RepositoryView view = getPrintAtHomeRepository().getView(
					this.printAtHome);
			QueryBuilder queryBuilder = view.getQueryBuilder();
			QueryExpression printAtHomeProperty = queryBuilder
					.createPropertyQueryExpression(this.sites);
			QueryExpression printAtHomeValue = queryBuilder
					.createConstantQueryExpression(pSiteId);
			Query siteBasedTemplateQuery = queryBuilder.createComparisonQuery(
					printAtHomeProperty, printAtHomeValue, QueryBuilder.EQUALS);
			RepositoryItem[] repoItems = view
					.executeQuery(siteBasedTemplateQuery);

			BBBPerformanceMonitor.end(GiftRegistryTools.class.getName() + ".getPrintAtHomeCardTemplates");
			logDebug("GiftRegistryTools : getPrintAtHomeCardTemplates : END");

			return repoItems;
		} catch (RepositoryException e) {
			if (isLoggingError()) {
				logError(e.fillInStackTrace());
			}
		}
		return null;

	}

	/**
	 * To Get Thumbnail image details.
	 * 
	 * @param pSiteId
	 *            - Site Id parameter
	 * @param pRegistryType
	 *            - registry type
	 */
	public RepositoryItem[] getThumbnailTemplateDetails(String pId) {

		BBBPerformanceMonitor.start(GiftRegistryTools.class.getName() + ".getThumbnailTemplateDetails");
		logDebug("GiftRegistryTools : getThumbnailTemplateDetails : START");

		try {
			RepositoryView view = getPrintAtHomeRepository().getView(
					this.announcementcard);
			QueryBuilder queryBuilder = view.getQueryBuilder();
			QueryExpression idProperty = queryBuilder
					.createPropertyQueryExpression(this.id);
			QueryExpression idPropertyValue = queryBuilder
					.createConstantQueryExpression(pId);
			Query idQuery = queryBuilder.createComparisonQuery(idProperty,
					idPropertyValue, QueryBuilder.EQUALS);
			RepositoryItem[] repoItems = view.executeQuery(idQuery);

			BBBPerformanceMonitor.end(GiftRegistryTools.class.getName() + ".getThumbnailTemplateDetails");

			logDebug("GiftRegistryTools : getThumbnailTemplateDetails : END");

			return repoItems;
		} catch (RepositoryException e) {
			logError(e.getMessage(), e);

		}
		return null;

	}

	/**
	 * Gets GetRegistryInfo repo.
	 * 
	 * @return mRegistryInfoRepository
	 */
	public MutableRepository getRegistryInfoRepository() {
		return mRegistryInfoRepository;
	}

	/**
	 * Sets the Print At HomeRepository.
	 * 
	 * @param pPrintAtHomeRepository
	 */
	public void setRegistryInfoRepository(
			MutableRepository pRegistryInfoRepository) {
		this.mRegistryInfoRepository = pRegistryInfoRepository;
	}

	/**
	 * Gets the Print At HomeRepository.
	 * 
	 * @return mPrintAtHomeRepository
	 */
	public MutableRepository getPrintAtHomeRepository() {
		return mPrintAtHomeRepository;
	}

	/**
	 * Sets the Print At HomeRepository.
	 * 
	 * @param pPrintAtHomeRepository
	 */
	public void setPrintAtHomeRepository(
			MutableRepository pPrintAtHomeRepository) {
		this.mPrintAtHomeRepository = pPrintAtHomeRepository;
	}

	/**
	 * createRegistryRecommendationsItem() will accept
	 * RecommendationRegistryProductVO as argument and will create a new
	 * recomendation when recommneder adds item to recommended registry in PDP
	 * page. RecommendationRegistryProductVO needs to have following mandatory
	 * properties to create a registry recommendation product 1) sku_id 2)
	 * recommendedQuantity 3) comment The above properties need to be setted in
	 * RecommendationRegistryProductVO before calling
	 * createRegistryRecommendationsItem() method from the Formhandler
	 * 
	 * @param recommendationItemVO
	 * @throws BBBSystemException
	 */
	@SuppressWarnings("unchecked")
	public void createRegistryRecommendationsItem(
			final RecommendationRegistryProductVO recommendationItemVO)
			throws BBBSystemException {
		@SuppressWarnings("unused")
		MutableRepositoryItem giftRegistryItem = null;
		final MutableRepository giftRegistryRepository = this
				.getGiftRepository();
		BBBPerformanceMonitor.start("GiftRegistryTools.createRegistryRecommendationsItem()");
		this.logDebug("GiftRegistryTools.createRegistryRecommendationsItem() method start");
		this.logDebug("Method Description : Add new item to recommended registry");
		this.logDebug("registry Id :" + recommendationItemVO.getRegistryId());

		try {
			MutableRepositoryItem registryRecommendationItem = null;
			MutableRepositoryItem registryRecommendationProdctItem = null;
			registryRecommendationItem = getGiftRepository().getItemForUpdate(
					recommendationItemVO.getRegistryId(),
					BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS);
			if (null == registryRecommendationItem) {
				logError("Registry Recommanation is not yet invited");
				return;
			}
			Set<RepositoryItem> rocommendationItems = (Set<RepositoryItem>) registryRecommendationItem
					.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS);
			MutableRepositoryItem currentRecommondationItem = null;
			if (rocommendationItems == null) {
				return;
			}
			for (RepositoryItem ri : rocommendationItems) {
				RepositoryItem userItem = (RepositoryItem) ri
						.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID);
				if (userItem != null
						&& userItem.getRepositoryId().equals(
								recommendationItemVO.getRecommenderProfileId())) {
					currentRecommondationItem = (MutableRepositoryItem) ri;
					break;
				}
			}
			Set<RepositoryItem> rocommendations = null;
			if (currentRecommondationItem == null) {
				rocommendations = new HashSet<RepositoryItem>();
			} else {
				rocommendations = (Set<RepositoryItem>) currentRecommondationItem
						.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS);
			}
			registryRecommendationProdctItem = giftRegistryRepository
					.createItem(BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS);
			registryRecommendationProdctItem.setPropertyValue(
					BBBGiftRegistryConstants.RECOMMENDED_SKU,
					recommendationItemVO.getSkuId());
			registryRecommendationProdctItem.setPropertyValue(
					BBBGiftRegistryConstants.RECOMMENDED_QUANTITY,
					recommendationItemVO.getRecommendedQuantity());
			registryRecommendationProdctItem.setPropertyValue(
					BBBGiftRegistryConstants.RECOMMENDATION_COMMENT,
					recommendationItemVO.getComment());
			registryRecommendationProdctItem.setPropertyValue(
					BBBGiftRegistryConstants.RECOMMENDED_DATE, new Date());
			registryRecommendationProdctItem.setPropertyValue(
					BBBGiftRegistryConstants.DECLINED2, 0);
			registryRecommendationProdctItem.setPropertyValue(
					BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0L);
			getGiftRepository().addItem(registryRecommendationProdctItem);
			rocommendations.add(registryRecommendationProdctItem);
			if(currentRecommondationItem!=null){
			currentRecommondationItem.setPropertyValue(
					BBBGiftRegistryConstants.RECOMMENDATIONS, rocommendations);
		}
			rocommendationItems.add(currentRecommondationItem);
			registryRecommendationItem.setPropertyValue(
					BBBGiftRegistryConstants.RECOMMENDER_ITEMS,
					rocommendationItems);
			getGiftRepository().updateItem(registryRecommendationItem);
		} catch (RepositoryException e) {
			this.logDebug("Catalog API Method Name [createRegistryRecommendationsItem]: RepositoryException ");
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_CREATE_RECORD_IN_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_CREATE_RECORD_IN_REPOSITORY_EXCEPTION,
					e);
		} finally {
			BBBPerformanceMonitor.start("GiftRegistryTools.createRegistryRecommendationsItem()");
		}

	}

	/**
	 * getRegistryRecommendationItemsForTab() will accept registry ID and
	 * tabid(i.e Accepted/Pending/Declined) as argument and will return the list
	 * of RecommendationRegistryProductVO recomendations. This method will check
	 * for accepted qauntity =0 and declined is false in each of the
	 * recommendations to fetch the data for pending tab. The above properties
	 * need to be setted in RecommendationRegistryProductVO before calling
	 * createRegistryRecommendationsItem() method from the Formhandler
	 * 
	 * @param regisrtyId
	 * @param tabId
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 * @return List<RecommendationRegistryProductVO>
	 */

	@SuppressWarnings("unchecked")
	public List<RecommendationRegistryProductVO> getRegistryRecommendationItemsForTab(
			final String regisrtyId, final String tabId)
			throws BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryRecommendationItemsForTab()");
		String siteId = extractSiteId();
		List<RecommendationRegistryProductVO> recommnededProducts = new ArrayList<RecommendationRegistryProductVO>();
		this.logDebug("GiftRegistryTools.getRegistryRecommendationItemsForTab() method start");
		this.logDebug("Method Description : fetch recommendations usig registry Id as parameter, returns List<RecommendationRegistryProductVO>");
		this.logDebug("registry Id :" + regisrtyId + " tabId : " + tabId);
		final long startTimeForRepositoryCall = System.currentTimeMillis();
		try {
			MutableRepositoryItem registryRecommendationItem = null;

			registryRecommendationItem = (MutableRepositoryItem) getGiftRepository()
					.getItem(regisrtyId,
							BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS);

			if (null == registryRecommendationItem) {
				logError("Registry Recommanation is not yet invited");
			} else {

				updateReccommendationViewDate(registryRecommendationItem);
				Set<RepositoryItem> recommendedProfileItems = (Set<RepositoryItem>) registryRecommendationItem
						.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS);
				if (null != recommendedProfileItems)
					for (RepositoryItem recommendedProfileItem : recommendedProfileItems) {
						if (null == recommendedProfileItem
								.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS)) {
							logError("Inivations for Recoommendations are not yet submitted");
						} else {
							RepositoryItem profileItem = (RepositoryItem) recommendedProfileItem
									.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID);
							String isFromFB = (String) recommendedProfileItem
									.getPropertyValue(BBBCatalogConstants.IS_FROM_FACEBOOK);
							Set<RepositoryItem> recommendedProducts = (Set<RepositoryItem>) recommendedProfileItem
									.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS);
							final long totalTimeTakenForRepostioryCall = System
									.currentTimeMillis()
									- startTimeForRepositoryCall;
							logDebug("Total time taken for fetching repository data for the recommendations is: "
									+ totalTimeTakenForRepostioryCall
									+ " for registry id: "
									+ regisrtyId
									+ " and for the tab: " + tabId);
							final long startTimeForProcessingData = System
									.currentTimeMillis();
							if (!isRecommenderTab(tabId)) {
								if (null != recommendedProducts
										&& !recommendedProducts.isEmpty()) {
									if (isPendingTab(tabId)) {
										recommnededProducts = populatePendingTabProductList(
												recommendedProducts,
												profileItem, siteId,
												recommnededProducts, isFromFB);
										logDebug("Time taken for fetching pending Recommendations: "
												+ (System.currentTimeMillis() - startTimeForProcessingData));

									} else if (isAcceptedTab(tabId)) {
										// BPS - 1380 Integrate Accepted Tab
										recommnededProducts
												.addAll(fetchAcceptedRecommendations(
														recommendedProducts,
														profileItem, siteId,
														isFromFB));
										logDebug("Time taken for fetching accepted Recommendations: "
												+ (System.currentTimeMillis() - startTimeForProcessingData));
									} else if (isDeclinedTab(tabId)) {
										// BPS-1381 Integrate Declined Tab
										recommnededProducts = populateDeclinedTabProductList(
												recommendedProducts,
												profileItem, siteId,
												recommnededProducts, isFromFB);
										logDebug("Time taken for fetching declined Recommendations: "
												+ (System.currentTimeMillis() - startTimeForProcessingData));
									}
								} else {
									logError("Products are not yet recommended for this registry "
											+ regisrtyId
											+ "Profile ID"
											+ recommendedProfileItem
													.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID));
								}
								final long totalTimeTakenForProcessingData = System
										.currentTimeMillis()
										- startTimeForProcessingData;
								logDebug("Total time taken for processing  Recommendations List VO after fetching repository data for the recommendations is: "
										+ totalTimeTakenForProcessingData
										+ " for registry id: "
										+ regisrtyId
										+ " and for the tab: " + tabId);
							} else if (isRecommenderTab(tabId)) {
								recommnededProducts
										.add(displayRegistryRecommInfo(recommendedProfileItem));
								logDebug("Time taken for fetching Recommender Information: "
										+ (System.currentTimeMillis() - startTimeForProcessingData));
							}
						}
					}
			}

		} catch (RepositoryException e) {
			this.logDebug("Catalog API Method Name [getRegistryRecommendationItemsForTab]: RepositoryException ");
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		} finally {
			
			BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryRecommendationItemsForTab()");
		}

		return recommnededProducts;

	}

	/**
	 * 
	 * Persists the change in block status for the given recommenderProfileId.
	 * The Recommendations for the given registry are requested and then they
	 * are iterated until the requested recommenderProfileId matches one of the
	 * recommenderProfiles. <br>
	 * If the recommenderProfile Object is received, then the change is verified
	 * whether is valid or not i.e., the requestedFlag should be different from
	 * the flag already present for the user. Only if the both are different,
	 * then the change is persisted and the result returned true.
	 * 
	 * @param registryId
	 * @param requestProfileId
	 * @param requestedFlag
	 * @return
	 * @throws BBBSystemException
	 */
	public boolean persistChangeInBlockStatus(String registryId,
			String requestProfileId, int requestedFlag)
			throws BBBSystemException {
		RepositoryItem recommendedProducts = null;
		MutableRepository giftRegistryRepository = this.getGiftRepository();
		boolean result = false;
		try {
			logDebug("The values recieved are RegistryId:- " + registryId
					+ " requestProfile:- " + requestProfileId
					+ " requestedFlag:-" + requestedFlag);
			recommendedProducts = (MutableRepositoryItem) getGiftRepository()
					.getItem(registryId,
							BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS);
			if (recommendedProducts == null) {
				logError("Inivations for Recoommendations are not yet submitted");
			} else {
				Set<MutableRepositoryItem> profileSet = (Set<MutableRepositoryItem>) recommendedProducts
						.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS);
				if (profileSet == null || profileSet.isEmpty()) {
					logError("There are no profiles associated with a Recommended Product.");
				} else {
					MutableRepositoryItem requestedProfile = null;
					for (MutableRepositoryItem profile : profileSet) {
						String profileId = profile.getRepositoryId();
						if (profileId.equals(requestProfileId)) {
							requestedProfile = profile;
							break;
						}
					}
					if (requestedProfile != null) {
						int requiredFlag = (Integer) (requestedProfile
								.getPropertyValue(BBBGiftRegistryConstants.INVITEE_STATUS));
						if (requestedFlag != requiredFlag) {
							requestedProfile.setPropertyValue(
									BBBGiftRegistryConstants.INVITEE_STATUS,
									requestedFlag);
							giftRegistryRepository.updateItem(requestedProfile);
							result = true;
							logDebug("The InviteeStatus for Profile:- "
									+ requestedProfile.getRepositoryId()
									+ " is set to " + requestedFlag);
						} else {
							logError("Illegal operation is requested. A Flag change request to the same value is recieved. RequiredFlag is "
									+ requiredFlag);
						}
					} else {
						logError("No profile could be found matching the session profile and the requested profile. ");
					}
				}
			}
		} catch (RepositoryException e) {
			this.logError(
					"Catalog API Method Name [getRegistryRecommendationItemsForTab]: RepositoryException ",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}
		return result;
	}

	/**
	 * Counts the number of Accepted, Declined, Recommended Products by
	 * iterating over the recommendations made by the recommender. Returns a
	 * hashMap containing the quantites.
	 * 
	 * @param recommendedProfile
	 * @return
	 */
	public Map<String, Long> determineCountsForRecommendersTab(
			RepositoryItem recommendedProfile) {
		logDebug("Enterng determineCountsForRecommendersTab Method with  profile:- "
				+ recommendedProfile.getRepositoryId());
		Map<String, Long> resultMap = new HashMap<String, Long>();
		Set<RepositoryItem> recommendedProductsForProfile = (Set<RepositoryItem>) recommendedProfile
				.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS);
		if (recommendedProductsForProfile != null) {
			long recommended = 0, accepted = 0, declined = 0;
			for (RepositoryItem recommendedProduct : recommendedProductsForProfile) {
				/*
				 * To Display the quantities of the products. long
				 * acceptedQuantity =
				 * (Long)(recommendedProduct.getPropertyValue(
				 * BBBGiftRegistryConstants.ACCEPTED_QUANTITY)); long
				 * recommendedQuantity =
				 * (Long)(recommendedProduct.getPropertyValue
				 * (BBBGiftRegistryConstants.RECOMMENDED_QUANTITY)); boolean
				 * declinedFlag = false; declinedFlag =
				 * recommendedProduct.getPropertyValue
				 * (BBBGiftRegistryConstants.DECLINED2
				 * ).equals(BBBGiftRegistryConstants
				 * .PRODUCT_DECLINED)?true:false; if(acceptedQuantity!=0 &&
				 * declinedFlag==false){ accepted += acceptedQuantity; }else
				 * if(acceptedQuantity==0 && declinedFlag==true){ declined +=
				 * recommendedQuantity; } recommended += recommendedQuantity;
				 */
				// To Display the count of products.
				long acceptedQuantity = (Long) (recommendedProduct
						.getPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY));
				long recommendedQuantity = (Long) (recommendedProduct
						.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_QUANTITY));
				boolean declinedFlag = false;
				declinedFlag = recommendedProduct.getPropertyValue(
						BBBGiftRegistryConstants.DECLINED2).equals(
						BBBGiftRegistryConstants.PRODUCT_DECLINED) ? true
						: false;
				if (acceptedQuantity != 0 && declinedFlag == false) {
					accepted++;
				} else if (acceptedQuantity == 0 && declinedFlag == true) {
					declined++;
				}
				recommended++;
			}
			resultMap.put(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, accepted);
			resultMap.put(BBBGiftRegistryConstants.DECLINED2, declined);
			resultMap.put(BBBGiftRegistryConstants.RECOMMENDED_QUANTITY,
					recommended);
			logDebug("The count of items for profile:- " + recommendedProfile
					+ "is " + resultMap);
		} else {
			logError("There are no products associated with the profile for recommendations to this particular registry.");
		}
		return resultMap;
	}

	/**
	 * 
	 * Used to Populate VO(RecommendationRegistryProductVO) for Recommenders
	 * Tab. The number of items in different tabs are given by
	 * determineCountsForRecommendersTab(profile). <br>
	 * Populated Properties in the VO are
	 * <ul>
	 * <li>firstName</li>
	 * <li>lastName</li>
	 * <li>recommenderProfileId</li>
	 * <li>profileActive</li>
	 * <li>acceptedQuantity</li>
	 * <li>declinedQuantity</li>
	 * <li>recommendedQuantity</li>
	 * </ul>
	 * 
	 * @param recommendedProfile
	 * @return
	 */
	private RecommendationRegistryProductVO displayRegistryRecommInfo(
			RepositoryItem recommendedProfile) {
		RecommendationRegistryProductVO result = null;
		if (null != recommendedProfile) {
			logDebug("The Profile Item is "
					+ recommendedProfile.getRepositoryId());
			result = new RecommendationRegistryProductVO();
			RepositoryItem inviteeProfile = ((RepositoryItem) recommendedProfile
					.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID));
			result.setFirstName(String.valueOf(inviteeProfile
					.getPropertyValue(BBBGiftRegistryConstants.FIRST_NAME)));
			result.setLastName(String.valueOf(inviteeProfile
					.getPropertyValue(BBBGiftRegistryConstants.LAST_NAME)));
			result.setRecommenderProfileId(String.valueOf(recommendedProfile
					.getRepositoryId()));
			boolean profileStatus = (recommendedProfile
					.getPropertyValue(BBBGiftRegistryConstants.INVITEE_STATUS))
					.equals(BBBGiftRegistryConstants.INVITEE_ACTIVE) ? true
					: false;
			result.setProfileActive(profileStatus);
			result.setFullName();
			Map<String, Long> countMap = determineCountsForRecommendersTab(recommendedProfile);
			result.setAcceptedQuantity((Long) (countMap
					.get(BBBGiftRegistryConstants.ACCEPTED_QUANTITY)));
			result.setDeclinedQuantity((Long) (countMap
					.get(BBBGiftRegistryConstants.DECLINED2)));
			result.setRecommendedQuantity((Long) countMap
					.get(BBBGiftRegistryConstants.RECOMMENDED_QUANTITY));
			logDebug("The RegistryRecommenderProductVO formed is "
					+ result.toString());
		} else {
			logError("The profile item passed into the populateRegistryRecommender() is null.");
		}
		return result;
	}

	private void updateReccommendationViewDate(
			MutableRepositoryItem registryRecommendationItem)
			throws RepositoryException {
		registryRecommendationItem.setPropertyValue(
				BBBGiftRegistryConstants.RECOMMENDATION_VIEW_DATE, new Date());
		getGiftRepository().updateItem(registryRecommendationItem);
	}

	private boolean isRecommenderTab(final String tabId) {
		return null != tabId
				&& tabId.equalsIgnoreCase(BBBGiftRegistryConstants.RECOMMENDER_TAB);
	}

	private boolean isDeclinedTab(final String tabId) {
		return null != tabId
				&& tabId.equals(BBBGiftRegistryConstants.DECLINED_TAB);
	}

	private boolean isAcceptedTab(final String tabId) {
		return null != tabId
				&& tabId.equals(BBBGiftRegistryConstants.ACCEPTED_TAB);
	}

	private boolean isPendingTab(final String tabId) {
		return null != tabId
				&& tabId.equals(BBBGiftRegistryConstants.PENDING_TAB);
	}

	/**
	 * populateRecommendedProductList will polulate
	 * RecommendationRegistryProductVO and retruns VO
	 * 
	 * @param recommendedProduct
	 * @param recommendedProfileItem
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public RecommendationRegistryProductVO populateRecommendedProductList(
			RepositoryItem recommendedProduct,
			RepositoryItem recommendedProfileItem, String siteId,
			String isFromFB) {
		logDebug("Registry Recommendations VO Populate API call populateRecommendedProductList(); Params : recommendedProduct="
				+ recommendedProduct
				+ ",recommendedProfileItem"
				+ ",siteId"
				+ siteId);
		long startTime = System.currentTimeMillis();
		RecommendationRegistryProductVO recommendedProductVO = null;
		String skuId = (String) recommendedProduct
				.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_SKU);
		try {

			if (getCatalogTools().isSKUAvailable(siteId, skuId)) {
				recommendedProductVO = new RecommendationRegistryProductVO();
				recommendedProductVO
						.setComment(String.valueOf(recommendedProduct
								.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATION_COMMENT)));
				recommendedProductVO.setRepositoryId(recommendedProduct
						.getRepositoryId());
				recommendedProductVO
						.setAcceptedQuantity((Long) recommendedProduct
								.getPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY));

				recommendedProductVO
						.setRecommendedQuantity((Long) recommendedProduct
								.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_QUANTITY));
				recommendedProductVO
						.setRecommendedDate((Date) recommendedProduct
								.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_DATE));
				recommendedProductVO
						.setRecenltyModifiedDate((Date) recommendedProduct
								.getPropertyValue(BBBGiftRegistryConstants.LAST_MODIFIED_DATE));
				SKUDetailVO skuDetail = getCatalogTools().getSKUDetails(siteId,
						skuId);
				recommendedProductVO.setImageVO(skuDetail.getSkuImages());
				recommendedProductVO.setSkuDisplayName(skuDetail
						.getDisplayName());
				recommendedProductVO.setLtl(skuDetail.isLtlItem());
				//BBBH-4958 | Incart pricing in Recommendation tab -start
				this.getCatalogTools().updateShippingMessageFlag(skuDetail, skuDetail.isInCartFlag());
				//end
				recommendedProductVO.setDisplayShipMsg(skuDetail.getDisplayShipMsg());

				recommendedProductVO.setSkuAttributes(skuDetail
						.getSkuAttributes());
				recommendedProductVO.setSkuColor(skuDetail.getColor());
				recommendedProductVO.setSkuSize(skuDetail.getSize());
				recommendedProductVO.setSkuId(skuId);
				recommendedProductVO.setProductId(getCatalogTools()
						.getParentProductForSku(skuId));
				recommendedProductVO.setUpc(skuDetail.getUpc());
				recommendedProductVO.setBvProductVO(getCatalogTools()
						.getBazaarVoiceDetails(
								recommendedProductVO.getProductId(), siteId));
				recommendedProductVO.setSkuListPrice(getCatalogTools()
						.getListPrice(recommendedProductVO.getProductId(),
								recommendedProductVO.getSkuId()));
				recommendedProductVO.setSkuSalePrice(getCatalogTools()
						.getSalePrice(recommendedProductVO.getProductId(),
								recommendedProductVO.getSkuId()));
				//BBBH-4958 | Incart pricing in Recommendation tab - start
				if(skuDetail.isInCartFlag()){
					recommendedProductVO.setSkuIncartPrice(this.getCatalogTools().getIncartPrice(skuDetail.getParentProdId(), skuDetail.getSkuId()));
				}
				//end
				
				recommendedProductVO
						.setRecommenderProfileId(String
								.valueOf(recommendedProfileItem
										.getPropertyValue("id")));
				recommendedProductVO
						.setFirstName(String.valueOf(recommendedProfileItem
								.getPropertyValue(BBBGiftRegistryConstants.FIRST_NAME)));
				recommendedProductVO
						.setLastName(String.valueOf(recommendedProfileItem
								.getPropertyValue(BBBGiftRegistryConstants.LAST_NAME)));
				recommendedProductVO.setIsFromFB(isFromFB);
				SKUDetailVO skuVO = this.getSKUDetailsWithProductId(
						extractSiteId(),
						skuId.toString(), null);
				recommendedProductVO.setsKUDetailVO(skuVO);
				if (recommendedProductVO.isLtl()) {
					recommendedProductVO
							.setLtlShipMethod((String) recommendedProduct
									.getPropertyValue(BBBCatalogConstants.LTL_SHIP_METHOD));
					recommendedProductVO
							.setLtlAssemblySelected((String) recommendedProduct
									.getPropertyValue(BBBGiftRegistryConstants.ASEEMBLY_SELECTED));
					if (BBBUtility.isNotEmpty(recommendedProductVO
							.getLtlShipMethod())
							&& recommendedProductVO.getAcceptedQuantity() > 0) {
						updateRecommendedVOforLTL(recommendedProductVO);
					}
				}
				logDebug("Registry Recommendations VO :: populateRecommendedProductList() ::::"
						+ recommendedProductVO.toString());
			}
		} catch (BBBSystemException e) {
			logError(e.getErrorCode(), e);
		} catch (BBBBusinessException e) {
			logError(e.getErrorCode(), e);
		}
		logDebug("Time taken to make catalog API call for populating total recommendedProductVO :"
				+ (System.currentTimeMillis() - startTime));
		return recommendedProductVO;
	}

	/**
	 * 
	 * 
	 * @param recommendedProductVO
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	protected void updateRecommendedVOforLTL(
			RecommendationRegistryProductVO recommendedProductVO)
			throws BBBBusinessException, BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.updateRecommendedVOforLTL()");
		this.logDebug("Method Description : Updat recommended product VO for LTL product");
		this.logDebug("Populating the LTL specific information in the Recommnded Product with shipping method:"
				+ recommendedProductVO.getLtlShipMethod()
				+ " and accepted quantity: "
				+ recommendedProductVO.getAcceptedQuantity());
		String sku = recommendedProductVO.getSkuId();
		String siteId = extractSiteId();
		String shipMethod = recommendedProductVO.getLtlShipMethod();
		String assemblySelected = recommendedProductVO.getLtlAssemblySelected();
		recommendedProductVO.setDeliverySurcharge(getCatalogTools()
				.getDeliveryCharge(siteId, sku, shipMethod));

		if (BBBUtility.isNotEmpty(assemblySelected)) {
			boolean isShippingMethodExistsForSku = getCatalogTools()
					.isShippingMethodExistsForSku(siteId, sku, shipMethod,
							Boolean.parseBoolean(assemblySelected));
			recommendedProductVO
					.setShipMethodUnsupported(!isShippingMethodExistsForSku);
			if (Boolean.parseBoolean(assemblySelected)) {
				recommendedProductVO.setAssemblyFees(getCatalogTools()
						.getAssemblyCharge(siteId, sku));
			}
			RepositoryItem shippingMethod = getCatalogTools()
					.getShippingMethod(shipMethod.trim());
			String shippingMethodDesc = ((String) shippingMethod
					.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION))
					.trim();
			recommendedProductVO.setLtlShipMethodDesc(shippingMethodDesc
					+ BBBGiftRegistryConstants.WITH_ASSEMBLY);
			recommendedProductVO.setLtlShipMethod(shippingMethod
					.getRepositoryId().trim() + BBBCoreConstants.A);
		} else {
			boolean isShippingMethodExistsForSku = getCatalogTools()
					.isShippingMethodExistsForSku(siteId, sku, shipMethod,
							false);
			recommendedProductVO
					.setShipMethodUnsupported(!isShippingMethodExistsForSku);
			RepositoryItem shippingMethod = getCatalogTools()
					.getShippingMethod(shipMethod);
			String shippingMethodDesc = ((String) shippingMethod
					.getPropertyValue(BBBCoreConstants.SHIP_METHOD_DESCRIPTION))
					.trim();
			recommendedProductVO.setLtlShipMethodDesc(shippingMethodDesc);
		}
		BBBPerformanceMonitor.end("GiftRegistryTools.updateRecommendedVOforLTL()");

	}

	/**
	 * This method is used to populate pending recommendations tab.
	 * 
	 * Returns List of pending recommendations
	 * 
	 * @param recommendedProducts
	 * @param profileItem
	 * @param siteId
	 * @param recommnededProducts
	 * @throws BBBSystemException
	 */
	private List<RecommendationRegistryProductVO> populatePendingTabProductList(
			final Set<RepositoryItem> recommendedProducts,
			final RepositoryItem profileItem, final String siteId,
			final List<RecommendationRegistryProductVO> recommnededProducts,
			String isFromFB) {

		this.logDebug("GiftRegistryTools.populatePendingTabProductList() method start");

		for (RepositoryItem recommendedProduct : recommendedProducts) {
			RecommendationRegistryProductVO recommendedProductVO = null;
			if (null != recommendedProduct) {
				long acceptedQuantity = 0;
				acceptedQuantity = (Long) recommendedProduct
						.getPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY);
				boolean declined = false;
				declined = String
						.valueOf(
								recommendedProduct
										.getPropertyValue(BBBGiftRegistryConstants.DECLINED2))
						.equals("1") ? true : false;
				if (acceptedQuantity == 0 && !declined) {
					recommendedProductVO = populateRecommendedProductList(
							recommendedProduct, profileItem, siteId, isFromFB);
					if (null != recommendedProductVO) {
						recommnededProducts.add(recommendedProductVO);
					}
				}
			}
		}
		this.logDebug("GiftRegistryTools.populatePendingTabProductList() method end");
		return recommnededProducts;
	}

	// BPS-1381 Integrate Declined Tab
	/**
	 * This method is used to populate recommendations tab.
	 * 
	 * Returns List of declined recommendations
	 * 
	 * @param recommendedProducts
	 * @param profileItem
	 * @param siteId
	 * @param recommnededProducts
	 * @throws BBBSystemException
	 */
	public List<RecommendationRegistryProductVO> populateDeclinedTabProductList(
			final Set<RepositoryItem> recommendedProducts,
			final RepositoryItem profileItem, final String siteId,
			final List<RecommendationRegistryProductVO> recommnededProducts,
			String isFromFB) {

		this.logDebug("GiftRegistryTools.populateDeclinedTabProductList() method start");

		for (RepositoryItem recommendedProduct : recommendedProducts) {
			RecommendationRegistryProductVO recommendedProductVO = null;
			if (null != recommendedProduct) {
				boolean declined = false;
				declined = String
						.valueOf(
								recommendedProduct
										.getPropertyValue(BBBGiftRegistryConstants.DECLINED2))
						.equals("1") ? true : false;
				if (declined) {
					recommendedProductVO = populateRecommendedProductList(
							recommendedProduct, profileItem, siteId, isFromFB);
					if (null != recommendedProductVO) {
						recommnededProducts.add(recommendedProductVO);
					}
				}
			}
		}
		this.logDebug("GiftRegistryTools.populateDeclinedTabProductList() method end");
		return recommnededProducts;
	}

	// PSI6 Surge Social Recommendation BPS-456
	/**
	 * This method is used to validate token against registry
	 * 
	 * @param registryId
	 * @param token
	 * @return
	 * @throws BBBSystemException
	 */

	@SuppressWarnings({ "unchecked" })
	public int validateToken(String registryId, String token)
			throws BBBSystemException {
		logDebug("Entering validateToken== registryId:" + registryId
				+ " token:" + token);
		Map<String, RepositoryItem> generatedTokens = new HashMap<String, RepositoryItem>();
		RepositoryItem[] giftRegRecommendRepositoryItems = null;
		Calendar tokenCreationDate = Calendar.getInstance();
		Calendar daysOld = Calendar.getInstance();
		String siteId = extractSiteId();
		
		Date tempDate = null;

				
		try {
			String registryStatus=getRegistryStatus(registryId,this.getCatalogTools().getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG, siteId).get(0));
			if (!StringUtils.isEmpty(registryStatus) && (registryStatus).equalsIgnoreCase("N")) {
				logError("From validateToken of GiftRegistryTools: private Registry:"
						+ registryId);
				return BBBGiftRegistryConstants.PRIVATE_REGISTRY;
			}
			
			
			giftRegRecommendRepositoryItems = this
					.fetchRegistryRecommendationItem(registryId);
			// If registry is not present in DB
			if (null == giftRegRecommendRepositoryItems) {
				logError("From validateToken of GiftRegistryTools: Invalid Registry:"
						+ registryId);
				return BBBGiftRegistryConstants.INVALID_REGISTRY;
			}

			// Fetch registry details and check if registry is active i.e. valid
			// and not expired
			// If registry is still valid, then validate expiration of token
			RepositoryItem recommendationRegistryItem = (RepositoryItem) giftRegRecommendRepositoryItems[0];
			Timestamp eventDateTime = (Timestamp) recommendationRegistryItem
					.getPropertyValue(BBBCatalogConstants.EVENT_DATE);
			Calendar eventDate = Calendar.getInstance();
			eventDate.setTimeInMillis(eventDateTime.getTime());

			Calendar currentCal = Calendar.getInstance();
			currentCal.set(Calendar.HOUR_OF_DAY, 0);
			currentCal.set(Calendar.MINUTE, 0);
			currentCal.set(Calendar.SECOND, 0);
			currentCal.set(Calendar.MILLISECOND, 0);
			this.logDebug("CurrentDate:" + currentCal + " and event Date:"
					+ eventDate);
			if (currentCal.compareTo(eventDate) == 1) {
				return BBBGiftRegistryConstants.INVALID_REGISTRY;
			}

			generatedTokens = (Map<String, RepositoryItem>) recommendationRegistryItem
					.getPropertyValue(BBBCatalogConstants.INVITEES);
			RepositoryItem inviteeToken = generatedTokens.get(token);
			// Check for token status
			int tokenStatus = 0;
			if (null != inviteeToken
					&& null != inviteeToken
							.getPropertyValue(BBBGiftRegistryConstants.TOKEN_STATUS)) {
				tokenStatus = (Integer) inviteeToken
						.getPropertyValue(BBBGiftRegistryConstants.TOKEN_STATUS);
			}
			if (null == inviteeToken || tokenStatus == 0) {
				return BBBGiftRegistryConstants.INVALID_TOKEN;
			}

			// Check if token is expired or not
			tempDate = (Date) inviteeToken
					.getPropertyValue(BBBGiftRegistryConstants.TOKEN_CREATION_DATE);
			tokenCreationDate.setTimeInMillis(tempDate.getTime());
			List<String> expiryLimitVal = getCatalogTools().getAllValuesForKey(
					BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,
					BBBGiftRegistryConstants.RECOMMEND_TOKEN_EXPIRY_LIMIT);
			// expiry limit is configurable and should be positive integer in
			// BCC
			int expiryLimit = 30;
			if (!BBBUtility.isListEmpty(expiryLimitVal)) {
				String expiryLimitStr = expiryLimitVal.get(0);
				logDebug("Recommender Expiry Limit is " + expiryLimitStr);
				if (!StringUtils.isEmpty(expiryLimitStr)) {
					expiryLimit = Integer.parseInt(expiryLimitStr);
				}
			}
			// Check if token is expired or not
			daysOld.add(Calendar.DATE, -1 * expiryLimit);
			daysOld.set(Calendar.HOUR_OF_DAY, 0);
			daysOld.set(Calendar.MINUTE, 0);
			daysOld.set(Calendar.SECOND, 0);
			daysOld.set(Calendar.MILLISECOND, 0);
			logDebug("Token Creation Date" + tokenCreationDate
					+ " and Month Old " + daysOld);
			if ((expiryLimit == 0) || daysOld.compareTo(tokenCreationDate) == 1) {
				return BBBGiftRegistryConstants.TOKEN_EXPIRED;
			}

			/*if (generatedTokens.containsKey(token)) {
				return BBBGiftRegistryConstants.VALID_TOKEN;
			}*/

		} catch (final BBBBusinessException e) {
			this.logError(
					BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR
							+ " BBBBusinessException from validateToken of GiftRegistryTools",
					e);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					e);
		}
		this.logDebug("ValidateToken Ends");
		return BBBGiftRegistryConstants.VALID_TOKEN;
	}

	/**
	 * 
	 * @param registryId
	 * @param token
	 * @param bbbProfile
	 * @return
	 * @throws BBBSystemException
	 */
	@SuppressWarnings({ "unchecked" })
	public RepositoryItem persistRecommenderReln(String registryId,
			String token, Profile bbbProfile, String isFromFB)
			throws BBBSystemException {
		BBBPerformanceMonitor.start("GiftRegistryTools.persistRecommenderReln()");
		logDebug("Entering persistyRecommenderReln == registryId:" + registryId
				+ " token:" + token + " Profile:"
				+ bbbProfile.getRepositoryId());
		this.logDebug("Method Description : persists recommener relation in repository");
		Map<String, MutableRepositoryItem> generatedTokens = new HashMap<String, MutableRepositoryItem>();
		MutableRepositoryItem recommenderProfileItem;
		RepositoryItem[] giftRegRecommendRepositoryItems = null;
		MutableRepository giftRegistryRepository = this.getGiftRepository();
		boolean registryProfileRelnExist = false;
		Set<RepositoryItem> recommenderProfiles = new HashSet<RepositoryItem>();
		MutableRepositoryItem recommendationRegistryItem;
		try {
			giftRegRecommendRepositoryItems = this
					.fetchRegistryRecommendationItem(registryId);
			if (null == giftRegRecommendRepositoryItems) {
				logError("From persistyRecommenderReln of GiftRegistryTools: Invalid Registry:"
						+ registryId);
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
			}

			recommendationRegistryItem = (MutableRepositoryItem) giftRegRecommendRepositoryItems[0];
			generatedTokens = (Map<String, MutableRepositoryItem>) recommendationRegistryItem
					.getPropertyValue(BBBCatalogConstants.INVITEES);
			MutableRepositoryItem inviteeToken = generatedTokens.get(token);
			if (null == inviteeToken) {
				logError("From persistyRecommenderReln Invalid Token:" + token);
				throw new BBBSystemException(
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
						BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION);
			}

			// check if profile and registry relation exist | BPS-456
			String bbbProfileId = bbbProfile.getRepositoryId();
			this.logDebug("Logged in user profile id" + bbbProfileId);
			this.logDebug("Check if registry and profile relation exists");
			recommenderProfiles = (Set<RepositoryItem>) recommendationRegistryItem
					.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS);
			for (RepositoryItem profile : recommenderProfiles) {
				String atgProfileId = ((RepositoryItem) profile
						.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID))
						.getRepositoryId();
				this.logDebug("Invitee Profile Id" + atgProfileId);
				if (atgProfileId.equals(bbbProfileId)) {
					registryProfileRelnExist = true;
					break;
				}
			}

			// if profile relation exist then set status=0
			if (generatedTokens.containsKey(token) && registryProfileRelnExist) {
				this.logDebug("Registry and Profile reln exist: Set token status to inactive");
				inviteeToken.setPropertyValue(
						BBBGiftRegistryConstants.TOKEN_STATUS, 0);
				recommendationRegistryItem.setPropertyValue(
						BBBCatalogConstants.INVITEES, generatedTokens);
				giftRegistryRepository.updateItem(recommendationRegistryItem);
			} else if (generatedTokens.containsKey(token)
					&& !registryProfileRelnExist) {
				this.logDebug("Registry and Profile reln does not exist: Persisting new relation");
				recommenderProfileItem = giftRegistryRepository
						.createItem(BBBGiftRegistryConstants.RECOMMENDER_PROFILES);
				inviteeToken.setPropertyValue(
						BBBGiftRegistryConstants.TOKEN_STATUS, 0);
				recommenderProfileItem
						.setPropertyValue(
								BBBGiftRegistryConstants.INVITEE_PROFILE_ID,
								bbbProfile);
				recommenderProfileItem.setPropertyValue(
						BBBGiftRegistryConstants.INVITEE_STATUS, 1);
				recommenderProfileItem.setPropertyValue(
						BBBCatalogConstants.IS_FROM_FACEBOOK, isFromFB);
				getGiftRepository().addItem(recommenderProfileItem);
				recommenderProfiles.add(recommenderProfileItem);
				recommendationRegistryItem.setPropertyValue(
						BBBGiftRegistryConstants.RECOMMENDER_ITEMS,
						recommenderProfiles);
				recommendationRegistryItem.setPropertyValue(
						BBBCatalogConstants.INVITEES, generatedTokens);
				giftRegistryRepository.updateItem(recommendationRegistryItem);
			}

		} catch (final BBBBusinessException bbe) {
			this.logError(
					BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR
							+ " BBBBusinessException from persistRecommendationToken of GiftRegistryTools",
					bbe);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					bbe);
		} catch (final RepositoryException re) {
			this.logError(
					BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR
							+ " Error while updating profile and registry relation in repository ",
					re);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_ADD_DATA_IN_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_ADD_DATA_IN_REPOSITORY_EXCEPTION,
					re);
		} catch (BBBSystemException bse) {
			this.logError(
					BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR
							+ " BBBSystemException from persistRecommendationToken of GiftRegistryTools",
					bse);
			throw new BBBSystemException(
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
					bse);
		}finally{
			this.logDebug("persistyRecommenderReln Ends");
			BBBPerformanceMonitor.end("GiftRegistryTools.persistRecommenderReln()");
		}
		return recommendationRegistryItem;
	}

	public RepositoryItem[] getRegistryFromProfileId(String profileId)
			throws BBBSystemException, BBBBusinessException {
		BBBPerformanceMonitor.start("GiftRegistryTools.friendRegistry()");
		this.logDebug("GiftRegistryTools.friendRegistry() method start");
		this.logDebug("Method Description : Get regsitry details for profile with parameter profileId="+ profileId);
		RepositoryItem[] registryRecommendationList = {};
		if (!BBBUtility.isEmpty(profileId)) {
			final Object[] params = new Object[2];
			params[0] = profileId;
			params[1] = 1;
			registryRecommendationList = this.executeRQLQuery(
					this.getFriendRegistryQuery(), params,
					BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS,
					this.getGiftRepository());
		}
		this.logDebug("GiftRegistryTools.fetchRegistryRecommendationItem() method ends");
		BBBPerformanceMonitor.end("GiftRegistryTools.friendRegistry()");
		return registryRecommendationList;
	}

	/**
	 * @return the friendRegistryQuery
	 */
	public String getFriendRegistryQuery() {
		return friendRegistryQuery;
	}

	/**
	 * @param friendRegistryQuery
	 *            the friendRegistryQuery to set
	 */
	public void setFriendRegistryQuery(String friendRegistryQuery) {
		this.friendRegistryQuery = friendRegistryQuery;
	}

	// PSI6 Surge Social Recommendation BPS-456 BPS-1112

	private ProfileTools profileTools;

	public ProfileTools getProfileTools() {
		return profileTools;
	}

	public void setProfileTools(ProfileTools profileTools) {
		this.profileTools = profileTools;
	}

	/**
	 * This methods return the count of the recommendations which have been
	 * recommended from the last login
	 * 
	 * @param registryId
	 * @return
	 * @throws RepositoryException
	 */
	@SuppressWarnings({ "unchecked" })
	public int[] getRecommendationCount(String registryId) {

		this.logDebug("getRecommendationCount Method :- The registry id ="
				+ registryId);
		this.logDebug("Method Description : Get recommendation count for registry");
		BBBPerformanceMonitor.start("GiftRegistryTools.getRecommendationCount()");
		int[] count = new int[2];
		RepositoryItem registryRecommendations = null;
		count[0] = 0;
		count[1] = 0;
		final long startProcessingTime = System.currentTimeMillis();
		try {

			registryRecommendations = getGiftRepository().getItem(registryId,
					BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS);

			if (registryRecommendations != null) {
				Timestamp recommendationViewDate = (Timestamp) registryRecommendations
						.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATION_VIEW_DATE);
				Set<RepositoryItem> recommenderProfileItems = (Set<RepositoryItem>) registryRecommendations
						.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS);
				final long totalTimeforRepositoryCall = System
						.currentTimeMillis() - startProcessingTime;
				this.logDebug("Time taken for repository call"
						+ totalTimeforRepositoryCall);
				if (recommenderProfileItems != null
						&& recommendationViewDate != null) {

					for (RepositoryItem recommenderProfileItem : recommenderProfileItems) {

						Set<RepositoryItem> recommenderProducts = (Set<RepositoryItem>) recommenderProfileItem
								.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS);
						// count to get the recommendations of user, whether
						// they are accepted
						// declined or pending
						if (recommenderProducts.size() > 0) {
							count[1] = recommenderProducts.size();
						}
						for (RepositoryItem recommenderProduct : recommenderProducts) {

							Timestamp recommendedProductDate = (Timestamp) recommenderProduct
									.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_DATE);
							// count to get number of new recommendations
							// recommended by other user

							if (recommendedProductDate
									.after(recommendationViewDate)) {
								count[0]++;
							}
						}
					}
				}
			}

		} catch (RepositoryException e) {
			logError("Error fetching registry recommendation data", e);
		}
		final long totalTimeforProcessingData = System.currentTimeMillis()
				- startProcessingTime;
		BBBPerformanceMonitor.end("GiftRegistryTools.getRecommendationCount()");
		this.logDebug("Total time spend for processing the recommendation count"
				+ totalTimeforProcessingData);
		this.logDebug("New recommendation count is = " + count[0]);
		return count;
	}

	// BPS - 1380 Integrate Accepted Tab SURGE
	/**
	 * 
	 * Returns List of accepted Recommendations
	 * 
	 * @param recommendedProducts
	 * @param acceptedProductRecommendation
	 * @param recommendedProfileItem
	 * @param siteId
	 */
	public List<RecommendationRegistryProductVO> fetchAcceptedRecommendations(
			Set<RepositoryItem> recommendedProducts,
			RepositoryItem recommendedProfileItem, String siteId,
			String isFromFB) {
		logDebug("Method fetchAcceptedRecommendations START | siteId: "
				+ siteId);
		List<RecommendationRegistryProductVO> acceptedProductRecommendation = new ArrayList<RecommendationRegistryProductVO>();
		for (RepositoryItem recommendedProduct : recommendedProducts) {
			RecommendationRegistryProductVO recommendedProductVO = null;
			if (null != recommendedProduct) {
				long acceptedQuantity = 0;
				acceptedQuantity = (Long) recommendedProduct
						.getPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY);
				boolean declined = false;
				declined = String
						.valueOf(
								recommendedProduct
										.getPropertyValue(BBBGiftRegistryConstants.DECLINED2))
						.equals("1") ? true : false;
				if (acceptedQuantity > 0 && !declined) {
					recommendedProductVO = populateRecommendedProductList(
							recommendedProduct, recommendedProfileItem, siteId,
							isFromFB);
					if (null != recommendedProductVO) {
						acceptedProductRecommendation.add(recommendedProductVO);
					}

				}
			}
		}
		logDebug("Method fetchAcceptedRecommendations | END");
		return acceptedProductRecommendation;
	}

	// BPS - 1380 Integrate Accepted Tab SURGE

	/**
	 * BPS-1394 [Multiscan] - Cache Invalidation of registry items from local
	 * repository
	 * 
	 * @param giftRegistryViewBean
	 * @throws BBBBusinessException 
	 * @throws BBBSystemException 
	 */
	public void invalidateRegistry(GiftRegistryViewBean giftRegistryViewBean) throws BBBSystemException, BBBBusinessException {
		List<AddItemsBean> additemList = new ArrayList<AddItemsBean>();
		// fetch all sku
		this.logDebug("Inside invalidateRegistry() method start:GiftRegistryTools");
		String siteId = extractSiteId();
		
		if (giftRegistryViewBean != null
				&& giftRegistryViewBean.getAdditem() != null
				&& giftRegistryViewBean.getAdditem().size() > 0
				&& giftRegistryViewBean.getRegistryId() != null) {
			additemList = giftRegistryViewBean.getAdditem();
			for (AddItemsBean addItemsBean : additemList) {
				if (addItemsBean.getSku() != null) {
					getRepositoryInvalidatorSevice().invalidateCachedItem(
							giftRegistryViewBean.getRegistryId()
							+ BBBCoreConstants.COLON
							+ addItemsBean.getSku(),
							BBBCoreConstants.ITEM_DESC_REG_DETAIL,
							getRegistryInfoRepository());
					if (BBBUtility.isNotEmpty(giftRegistryViewBean.getRefNum())) {
						getRepositoryInvalidatorSevice().invalidateCachedItem(
								giftRegistryViewBean.getRegistryId()
								+ BBBCoreConstants.COLON
								+ addItemsBean.getSku()
								+ BBBCoreConstants.COLON
								+ giftRegistryViewBean.getRefNum(),
								BBBCoreConstants.ITEM_DESC_REG_DETAIL2,
								getRegistryInfoRepository());
					}
					if(getCatalogTools().isSkuLtl(siteId, addItemsBean.getSku())){
						String cachedItemId = BBBCoreConstants.BLANK;
						if(BBBUtility.isEmpty(giftRegistryViewBean.getLtlDeliveryServices())){
							cachedItemId = giftRegistryViewBean.getRegistryId()
									+ BBBCoreConstants.COLON
									+ addItemsBean.getSku() + 
									BBBCoreConstants.COLON + BBBCoreConstants.MINUS_ONE;
						} else{
							cachedItemId = giftRegistryViewBean.getRegistryId()
									+ BBBCoreConstants.COLON
									+ addItemsBean.getSku() + 
									BBBCoreConstants.COLON + giftRegistryViewBean
									.getLtlDeliveryServices();
						}
						getRepositoryInvalidatorSevice()
						.invalidateCachedItem(
								cachedItemId,
								BBBCoreConstants.ITEM_DESC_REG_DETAIL2,
								getRegistryInfoRepository());
						getRepositoryInvalidatorSevice().invalidateCachedItem(
								cachedItemId,
								BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER,
								getRegistryInfoRepository());
					}
					getRepositoryInvalidatorSevice().invalidateCachedItem(
							giftRegistryViewBean.getRegistryId()
							+ BBBCoreConstants.COLON
							+ addItemsBean.getSku(),
							BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER,
							getRegistryInfoRepository());
					if (BBBUtility.isNotEmpty(giftRegistryViewBean.getRefNum())) {
						getRepositoryInvalidatorSevice().invalidateCachedItem(
								giftRegistryViewBean.getRegistryId()
								+ BBBCoreConstants.COLON
								+ addItemsBean.getSku()
								+ BBBCoreConstants.COLON
								+ giftRegistryViewBean.getRefNum(),
								BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER,
								getRegistryInfoRepository());
					}
				}
			}
		} else if (giftRegistryViewBean != null
				&& giftRegistryViewBean.getSku() != null
				&& giftRegistryViewBean.getRegistryId() != null) {
			getRepositoryInvalidatorSevice().invalidateCachedItem(
					giftRegistryViewBean.getRegistryId()
					+ BBBCoreConstants.COLON
					+ giftRegistryViewBean.getSku(),
					BBBCoreConstants.ITEM_DESC_REG_DETAIL,
					getRegistryInfoRepository());
			if (BBBUtility.isNotEmpty(giftRegistryViewBean.getRefNum())) {
				getRepositoryInvalidatorSevice().invalidateCachedItem(
						giftRegistryViewBean.getRegistryId()
						+ BBBCoreConstants.COLON
						+ giftRegistryViewBean.getSku()
						+ BBBCoreConstants.COLON
						+ giftRegistryViewBean.getRefNum(),
						BBBCoreConstants.ITEM_DESC_REG_DETAIL2,
						getRegistryInfoRepository());
			}
			if(getCatalogTools().isSkuLtl(siteId, giftRegistryViewBean.getSku())){
				String cachedItemId = BBBCoreConstants.BLANK;
				if(BBBUtility.isEmpty(giftRegistryViewBean.getLtlDeliveryServices())){
					cachedItemId = giftRegistryViewBean.getRegistryId()
							+ BBBCoreConstants.COLON
							+ giftRegistryViewBean.getSku() + 
							BBBCoreConstants.COLON + BBBCoreConstants.MINUS_ONE;
				} else{
					cachedItemId = giftRegistryViewBean.getRegistryId()
							+ BBBCoreConstants.COLON
							+ giftRegistryViewBean.getSku() + 
							BBBCoreConstants.COLON + giftRegistryViewBean
							.getLtlDeliveryServices();
				}
				getRepositoryInvalidatorSevice()
				.invalidateCachedItem(
						cachedItemId,
						BBBCoreConstants.ITEM_DESC_REG_DETAIL2,
						getRegistryInfoRepository());
				getRepositoryInvalidatorSevice().invalidateCachedItem(
						cachedItemId,
						BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER,
						getRegistryInfoRepository());
			}
			getRepositoryInvalidatorSevice().invalidateCachedItem(
					giftRegistryViewBean.getRegistryId()
					+ BBBCoreConstants.COLON
					+ giftRegistryViewBean.getSku(),
					BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER,
					getRegistryInfoRepository());
			if (BBBUtility.isNotEmpty(giftRegistryViewBean.getRefNum())) {
				getRepositoryInvalidatorSevice().invalidateCachedItem(
						giftRegistryViewBean.getRegistryId()
						+ BBBCoreConstants.COLON
						+ giftRegistryViewBean.getSku()
						+ BBBCoreConstants.COLON
						+ giftRegistryViewBean.getRefNum(),
						BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER,
						getRegistryInfoRepository());
			}

		}
		this.logDebug("Inside invalidateRegistry() method end:GiftRegistryTools");
	}

	public void invalidateRegistryCache(
			List<GiftRegistryViewBean> registryViewBean) throws BBBSystemException, BBBBusinessException {
		// fetch all sku
		this.logDebug("Inside invalidateRegistryCache() method start");
		String siteId = extractSiteId();
		
		for (GiftRegistryViewBean giftRegistryViewBean : registryViewBean) {
			if (giftRegistryViewBean != null
					&& giftRegistryViewBean.getRegistryId() != null
					&& giftRegistryViewBean.getSku() != null) {
				this.logDebug("Sku to be invalidated"
						+ giftRegistryViewBean.getSku());
				getRepositoryInvalidatorSevice().invalidateCachedItem(
						giftRegistryViewBean.getRegistryId()
								+ BBBCoreConstants.COLON
								+ giftRegistryViewBean.getSku(),
						BBBCoreConstants.ITEM_DESC_REG_DETAIL,
						getRegistryInfoRepository());
				if (BBBUtility.isNotEmpty(giftRegistryViewBean.getRefNum())) {
					getRepositoryInvalidatorSevice().invalidateCachedItem(
							giftRegistryViewBean.getRegistryId()
									+ BBBCoreConstants.COLON
									+ giftRegistryViewBean.getSku()
									+ BBBCoreConstants.COLON
									+ giftRegistryViewBean.getRefNum(),
							BBBCoreConstants.ITEM_DESC_REG_DETAIL2,
							getRegistryInfoRepository());
				}
				if(getCatalogTools().isSkuLtl(siteId, giftRegistryViewBean.getSku())){
					String cachedItemId = BBBCoreConstants.BLANK;
					if(BBBUtility.isEmpty(giftRegistryViewBean.getLtlDeliveryServices())){
						cachedItemId = giftRegistryViewBean.getRegistryId()
								+ BBBCoreConstants.COLON
								+ giftRegistryViewBean.getSku() + 
								BBBCoreConstants.COLON + BBBCoreConstants.MINUS_ONE;
					} else{
						cachedItemId = giftRegistryViewBean.getRegistryId()
								+ BBBCoreConstants.COLON
								+ giftRegistryViewBean.getSku() + 
								BBBCoreConstants.COLON + giftRegistryViewBean
								.getLtlDeliveryServices();
					}
					getRepositoryInvalidatorSevice()
					.invalidateCachedItem(
							cachedItemId,
							BBBCoreConstants.ITEM_DESC_REG_DETAIL2,
							getRegistryInfoRepository());
					getRepositoryInvalidatorSevice().invalidateCachedItem(
							cachedItemId,
							BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER,
							getRegistryInfoRepository());
				}
				getRepositoryInvalidatorSevice().invalidateCachedItem(
						giftRegistryViewBean.getRegistryId()
								+ BBBCoreConstants.COLON
								+ giftRegistryViewBean.getSku(),
						BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER,
						getRegistryInfoRepository());
				if (BBBUtility.isNotEmpty(giftRegistryViewBean.getRefNum())) {
					getRepositoryInvalidatorSevice().invalidateCachedItem(
							giftRegistryViewBean.getRegistryId()
									+ BBBCoreConstants.COLON
									+ giftRegistryViewBean.getSku()
									+ BBBCoreConstants.COLON
									+ giftRegistryViewBean.getRefNum(),
							BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER,
							getRegistryInfoRepository());
				}
			}
		}
		this.logDebug("Inside invalidateRegistryCache() method ends");
	}

	public RepositoryItem[] fetchRecomEmail(int pageNo, int pageSize,
			String itemDescriptor) throws BBBSystemException,
			BBBBusinessException {
		this.logDebug("GiftRegistryTools.fetchRecomEmail() | pageNo" + pageNo
				+ ", pageSize" + pageSize + ", itemDescriptor" + itemDescriptor);
		String recomEmailQuery = "ALL RANGE ?0+?1";
		RepositoryItem[] recommendRegistryRepoItem = null;
		final Object[] params = new Object[2];
		params[0] = pageNo;
		params[1] = pageSize;
		recommendRegistryRepoItem = this.executeRQLQuery(recomEmailQuery,
				params, itemDescriptor, this.getGiftRepository());
		this.logDebug("GiftRegistryTools.fetchRegistryRecommendationItem() method ends");
		return recommendRegistryRepoItem;
	}

	public void refreshRepoItemCache(String itemDescriptor)
			throws BBBSystemException {
		try {
			((ItemDescriptorImpl) getGiftRepository().getItemDescriptor(
					itemDescriptor)).invalidateItemCache();
		} catch (RepositoryException e) {
			logError("Error on Updating Master Job Table ", e);
			throw new BBBSystemException(e.getMessage(), e);
		}
	}

	public void refreshMattView(int jobType) throws SQLException {
		logDebug("Refreshing Matt view for jobType:" + jobType);
		Connection connection = null;
		CallableStatement callableStatement = null;
		try {
			String sql = "";
			connection = ((GSARepository) this.getGiftRepository())
					.getDataSource().getConnection();
			switch (jobType) {
			case BBBGiftRegistryConstants.EMAIL_OPT_DAILY:
				sql = BBBGiftRegistryConstants.MATT_VIEW_REFRESH_SQL_DAILY;
				break;
			case BBBGiftRegistryConstants.EMAIL_OPT_WEEKLY:
				sql = BBBGiftRegistryConstants.MATT_VIEW_REFRESH_SQL_WEEKLY;
				break;
			case BBBGiftRegistryConstants.EMAIL_OPT_MONTHLY:
				sql = BBBGiftRegistryConstants.MATT_VIEW_REFRESH_SQL_MONTHLY;
				break;
			}
			callableStatement = connection.prepareCall(sql);
			extractDBCallforgetRegistryListByRegNum(callableStatement);
		} finally {
			try {
				if (callableStatement != null)
					callableStatement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				logError("Error in closing the prepared statement", e);
			}
		}
	}

	public void logBatchJobStatus(int jobType, Date schedulerStartDate)
			throws BBBSystemException {
		logDebug("Updating Master Job Table on successfull exection. Parameters: jobType:"
				+ jobType + ", schedulerStartDate" + schedulerStartDate);
		MutableRepositoryItem recommendationRegistryItem = null;
		final Object[] params = new Object[1];
		params[0] = jobType;
		RepositoryItem[] recommendRegistryRepoItem;
		try {
			recommendRegistryRepoItem = this.executeRQLQuery(
					BBBGiftRegistryConstants.RECOMM_JOB_MASTER_UPDATE_SQL,
					params, BBBGiftRegistryConstants.RECOMM_EMAIL_JOB_MASTER,
					this.getGiftRepository());
			recommendationRegistryItem = (MutableRepositoryItem) recommendRegistryRepoItem[0];
			recommendationRegistryItem
					.setPropertyValue(BBBGiftRegistryConstants.LAST_EXEC_DATE,
							schedulerStartDate);
			this.getGiftRepository().updateItem(recommendationRegistryItem);
		} catch (BBBBusinessException e) {
			logError("Error on Updating Master Job Table ", e);
			throw new BBBSystemException(e.getMessage(), e);
		} catch (RepositoryException e) {
			logError("Error on Updating Master Job Table ", e);
			throw new BBBSystemException(e.getMessage(), e);
		}
	}

	
	
	/**
	 * This method is used to replace the web service call for get registryInfo
	 * from method getRegistryInfo the method uses stored proc get_reg_info2 in
	 * order to retrieve the VO mapped to RegistryResVO which is returned from
	 * web service call
	 * 
	 * @param registryId
	 * @param siteId
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public RegistryResVO getRegistryInfoFromEcomAdmin(String registryId,
			String siteId) throws BBBBusinessException, BBBSystemException {

		this.logDebug("GiftRegistryTools.getRegistryInfoFromEcomAdmin() method start");
		this.logDebug("Method Description : get registryInfo from method getRegistryInfo the method uses stored proc get_reg_info2, returns RegistryResVO");
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryInfoFromEcomAdmin");
		RegistryResVO registryResVO = new RegistryResVO();

		if (BBBUtility.isEmpty(registryId) || BBBUtility.isEmpty(siteId)) {
			return registryResVO;
		}
		long start = System.currentTimeMillis();
		RegistryVO registryVO = new RegistryVO();
		RegistrySummaryVO registrySummaryVO = new RegistrySummaryVO();
		RegistrantVO registrantVO = new RegistrantVO();
		RegistrantVO coRegistrantVO = null;
		AddressVO contactAddressVO = new AddressVO();
		AddressVO coRegContactAddressVO = null;
		AddressVO shippingAddressVO = new AddressVO();
		AddressVO futureAaddressVO = new AddressVO();
		ShippingVO shippingVO = new ShippingVO();
		EventVO eventVO = new EventVO();
		RegistryTypes registryTypes = new RegistryTypes();

		registrySummaryVO.setRegistryId(registryId);

		Connection con = null;
		ResultSet resultSet = null;
		CallableStatement cs = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();

			if (con != null) {
				final String getRegInfostoredProc = "{call ECOMADMIN.ATGWS.GET_REG_INFO2(?,?,?,?,?,?,?,?)}";

				// Step-3: prepare the callable statement
				cs = con.prepareCall(getRegInfostoredProc);

				// Step-4: set input parameters ...
				// first input argument
				cs.setInt(1, Integer.parseInt(registryId));
				cs.setInt(2, Integer.parseInt(siteId));
				this.logDebug("GiftRegistryTools.getRegistryInfoFromEcomAdmin() | parameters passed into procedure are registryId : "
						+ registryId + " and siteId : " + siteId);
				// Step-5: register output parameters ...
				cs.registerOutParameter(3, java.sql.Types.INTEGER);
				cs.registerOutParameter(4, java.sql.Types.INTEGER);

				cs.registerOutParameter(5, OracleTypes.CURSOR);
				cs.registerOutParameter(6, OracleTypes.CURSOR);
				cs.registerOutParameter(7, OracleTypes.CURSOR);
				cs.registerOutParameter(8, OracleTypes.CURSOR);

				// Step-6: execute the stored procedures: getRegInfostoredProc
				long beforeExecute = System.currentTimeMillis();
				cs.executeUpdate();
				long afterExecute = System.currentTimeMillis();
				logDebug("Total time taken to execute stored procedure ECOMADMIN.ATGWS.GET_REG_INFO2 :: " + (afterExecute-beforeExecute));

				this.logDebug("GiftRegistryTools.getRegistryInfoFromEcomAdmin() | procedure "
						+ getRegInfostoredProc + " executed from method ");
				// Step-7: extract the output parameters gifts_registered
				// get gifts_registered as output
				int p_nGiftsRegistered = cs.getInt(3);
				// get p_nGiftsPurchased as output
				int p_nGiftsPurchased = cs.getInt(4);

				registryVO.setGiftPurchased(p_nGiftsPurchased);
				registryVO.setGiftRegistered(p_nGiftsRegistered);
				registrySummaryVO.setGiftPurchased(p_nGiftsPurchased);
				registrySummaryVO.setGiftRegistered(p_nGiftsRegistered);
				registrySummaryVO.setGiftRemaining((p_nGiftsRegistered - p_nGiftsPurchased) < 0 ? 0 : (p_nGiftsRegistered - p_nGiftsPurchased));

				this.logDebug("total number for gifts registered : "
						+ p_nGiftsRegistered + " for registry id : "
						+ registryId);
				this.logDebug("total number for gifts purchased : "
						+ p_nGiftsPurchased + " for registry id : "
						+ registryId);

				// get values from reg_names -- primary registrant and co
				// registrant details captured here.
				long beforeRegNames = System.currentTimeMillis();
				resultSet = (ResultSet) cs.getObject(5);
				registrantVO = new RegistrantVO();
				String reg = null;

				String addr1 = null;
				String addr2 = null;
				String city = null;
				String state = null;
				String zip_code = null;
				String prefRegContactMeth = null;

				while (resultSet.next()) {

					reg = resultSet
							.getString(BBBGiftRegistryConstants.NM_ADDR_SUB_TYPE);
					if (reg != null
							&& (BBBGiftRegistryConstants.SH.equals(reg)
									|| BBBGiftRegistryConstants.RE.equals(reg)
									|| BBBGiftRegistryConstants.CO.equals(reg) || BBBGiftRegistryConstants.FU
										.equals(reg))) {
						if (BBBGiftRegistryConstants.RE.equals(reg)) {
							this.logDebug("populating registrantVO for registry id : "
									+ registryId);
							registrantVO
									.setFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
														registrantVO
									.setLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							registrantVO
									.setPrimaryPhone(resultSet
											.getString(BBBGiftRegistryConstants.DAY_PHONE));
							registrantVO
									.setEmail(resultSet
											.getString(BBBGiftRegistryConstants.EMAIL_ADDR));
							registrantVO
									.setProfileId(resultSet
											.getString(BBBGiftRegistryConstants.ATG_PROFILE_ID));
							registrantVO
									.setBabyMaidenName(resultSet
											.getString(BBBGiftRegistryConstants.MAIDEN_COPY));
							registrantVO
									.setCellPhone(resultSet
											.getString(BBBGiftRegistryConstants.EVE_PHONE));
							this.logDebug("populating registrySummaryVO with primary registrant details for registry id : "
									+ registryId);
							registrySummaryVO
									.setPrimaryRegistrantFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
							registrySummaryVO
									.setPrimaryRegistrantLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							registrySummaryVO
									.setPrimaryRegistrantEmail(resultSet
											.getString(BBBGiftRegistryConstants.EMAIL_ADDR));
							registrySummaryVO
							.setRegistrantEmail(resultSet
									.getString(BBBGiftRegistryConstants.EMAIL_ADDR));
							registrySummaryVO
									.setPrimaryRegistrantPrimaryPhoneNum(resultSet
											.getString(BBBGiftRegistryConstants.DAY_PHONE));
							registrySummaryVO
									.setPrimaryRegistrantMobileNum(resultSet
											.getString(BBBGiftRegistryConstants.EVE_PHONE));
							registrySummaryVO
									.setState(resultSet
											.getString(BBBGiftRegistryConstants.STATE_CD));
							registrySummaryVO
									.setOwnerProfileID(resultSet
											.getString(BBBGiftRegistryConstants.ATG_PROFILE_ID));
							registrySummaryVO
									.setFavStoreId(resultSet
											.getString(BBBGiftRegistryConstants.STORE_NUM));

							addr1 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR1);
							addr2 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR2);
							city = resultSet
									.getString(BBBGiftRegistryConstants.CITY);
							state = resultSet
									.getString(BBBGiftRegistryConstants.STATE_CD);
							zip_code = resultSet
									.getString(BBBGiftRegistryConstants.ZIP_CD);

							this.logDebug("populating contactAddressVO with primary registrant contact details for registry id : "
									+ registryId);
							contactAddressVO
									.setFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
							contactAddressVO
									.setLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							contactAddressVO.setAddressLine1(StringUtils
									.isEmpty(addr1) ? addr1 : addr1.trim());
							contactAddressVO.setAddressLine2(StringUtils
									.isEmpty(addr2) ? addr2 : addr2.trim());
							contactAddressVO
									.setCity(StringUtils.isEmpty(city) ? city
											: city.trim());
							contactAddressVO.setState(StringUtils
									.isEmpty(state) ? state : state.trim());
							contactAddressVO.setZip(StringUtils
									.isEmpty(zip_code) ? zip_code : zip_code
									.trim());
							contactAddressVO
									.setCompany(resultSet
											.getString(BBBGiftRegistryConstants.COMPANY));
							contactAddressVO
									.setPrimaryPhone(resultSet
											.getString(BBBGiftRegistryConstants.DAY_PHONE));

							registrantVO.setContactAddress(contactAddressVO);
							prefRegContactMeth = resultSet
									.getString(BBBGiftRegistryConstants.PREF_REG_CONTACT_METHOD);
							if (!BBBUtility.isEmpty(prefRegContactMeth)) {
								registryVO.setPrefRegContMeth(Integer
										.parseInt(prefRegContactMeth));
							}
							registryVO
									.setPrefRegContTime(resultSet
											.getString(BBBGiftRegistryConstants.PREF_REG_CONTACT_TIME));
							registryVO
									.setOptInWeddingOrBump(resultSet
											.getString(BBBGiftRegistryConstants.RECEIVE_WEDDINGCHANNEL_EMAIL));

						}
						if (BBBGiftRegistryConstants.SH.equals(reg)) {

							addr1 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR1);
							addr2 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR2);
							city = resultSet
									.getString(BBBGiftRegistryConstants.CITY);
							state = resultSet
									.getString(BBBGiftRegistryConstants.STATE_CD);
							zip_code = resultSet
									.getString(BBBGiftRegistryConstants.ZIP_CD);

							this.logDebug("populating shippingAddressVO with primary registrant shipping details for registry id : "
									+ registryId);
							shippingAddressVO
									.setFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
							shippingAddressVO
									.setLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							shippingAddressVO.setAddressLine1(StringUtils
									.isEmpty(addr1) ? addr1 : addr1.trim());
							shippingAddressVO.setAddressLine2(StringUtils
									.isEmpty(addr2) ? addr2 : addr2.trim());
							shippingAddressVO
									.setCity(StringUtils.isEmpty(city) ? city
											: city.trim());
							shippingAddressVO.setState(StringUtils
									.isEmpty(state) ? state : state.trim());
							shippingAddressVO.setZip(StringUtils
									.isEmpty(zip_code) ? zip_code : zip_code
									.trim());
							shippingAddressVO
									.setCompany(resultSet
											.getString(BBBGiftRegistryConstants.COMPANY));
							shippingAddressVO
									.setPrimaryPhone(resultSet
											.getString(BBBGiftRegistryConstants.DAY_PHONE));

							registrySummaryVO
									.setShippingAddress(shippingAddressVO);
							shippingVO.setShippingAddress(shippingAddressVO);
							registryVO.setShipping(shippingVO);

						}
						if (BBBGiftRegistryConstants.CO.equals(reg)) {

							this.logDebug("populating coRegistrantVO for registry id : "
									+ registryId);
							coRegistrantVO = new RegistrantVO();
							coRegContactAddressVO = new AddressVO();
							coRegistrantVO
									.setFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
							coRegistrantVO
									.setLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							coRegistrantVO
									.setPrimaryPhone(resultSet
											.getString(BBBGiftRegistryConstants.DAY_PHONE));
							coRegistrantVO
									.setEmail(resultSet
											.getString(BBBGiftRegistryConstants.EMAIL_ADDR));
							coRegistrantVO
									.setProfileId(resultSet
											.getString(BBBGiftRegistryConstants.ATG_PROFILE_ID));
							coRegistrantVO
									.setCoRegEmailFlag(resultSet
											.getString(BBBGiftRegistryConstants.EMAIL_FLAG));
							coRegistrantVO
									.setBabyMaidenName(resultSet
											.getString(BBBGiftRegistryConstants.MAIDEN_COPY));

							this.logDebug("populating registrySummaryVO with co-registrant details for registry id : "
									+ registryId);
							registrySummaryVO
									.setCoRegistrantFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
							registrySummaryVO
									.setCoRegistrantLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							registrySummaryVO
									.setCoRegistrantEmail(resultSet
											.getString(BBBGiftRegistryConstants.EMAIL_ADDR));
							registrySummaryVO
									.setCoownerProfileID(resultSet
											.getString(BBBGiftRegistryConstants.ATG_PROFILE_ID));

							addr1 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR1);
							addr2 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR2);
							city = resultSet
									.getString(BBBGiftRegistryConstants.CITY);
							state = resultSet
									.getString(BBBGiftRegistryConstants.STATE_CD);
							zip_code = resultSet
									.getString(BBBGiftRegistryConstants.ZIP_CD);

							this.logDebug("populating coRegContactAddressVO with co-registrant contact details for registry id : "
									+ registryId);
							coRegContactAddressVO
									.setFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
							coRegContactAddressVO
									.setLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							coRegContactAddressVO.setAddressLine1(StringUtils
									.isEmpty(addr1) ? addr1 : addr1.trim());
							coRegContactAddressVO.setAddressLine2(StringUtils
									.isEmpty(addr2) ? addr2 : addr2.trim());
							coRegContactAddressVO.setCity(StringUtils
									.isEmpty(city) ? city : city.trim());
							coRegContactAddressVO.setState(StringUtils
									.isEmpty(state) ? state : state.trim());
							coRegContactAddressVO.setZip(StringUtils
									.isEmpty(zip_code) ? zip_code : zip_code
									.trim());
							coRegContactAddressVO
									.setCompany(resultSet
											.getString(BBBGiftRegistryConstants.COMPANY));
							coRegContactAddressVO
									.setPrimaryPhone(resultSet
											.getString(BBBGiftRegistryConstants.DAY_PHONE));

							coRegistrantVO
									.setContactAddress(coRegContactAddressVO);
							/*
							 * commented due to unknown error for registry id
							 * ---- 203537220 will be added afer investigation
							 * prefRegContactMeth =
							 * resultSet.getString(BBBGiftRegistryConstants
							 * .PREF_REG_CONTACT_METHOD); if
							 * (!StringUtils.isEmpty(prefRegContactMeth)) {
							 * registryVO.setPrefCoregContMeth(Integer.parseInt(
							 * prefRegContactMeth)); }
							 * registryVO.setPrefCoregContTime
							 * (resultSet.getString
							 * (BBBGiftRegistryConstants.PREF_REG_CONTACT_TIME
							 * ));
							 */

						} else if (BBBGiftRegistryConstants.FU.equals(reg)) {
							addr1 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR1);
							addr2 = resultSet
									.getString(BBBGiftRegistryConstants.ADDR2);
							city = resultSet
									.getString(BBBGiftRegistryConstants.CITY);
							state = resultSet
									.getString(BBBGiftRegistryConstants.STATE_CD);
							zip_code = resultSet
									.getString(BBBGiftRegistryConstants.ZIP_CD);

							this.logDebug("populating futureAaddressVO with primary registrant future shipping details for registry id : "
									+ registryId);
							futureAaddressVO
									.setFirstName(resultSet
											.getString(BBBGiftRegistryConstants.FIRST_NM_COPY));
							futureAaddressVO
									.setLastName(resultSet
											.getString(BBBGiftRegistryConstants.LAST_NM_COPY));
							futureAaddressVO.setAddressLine1(StringUtils
									.isEmpty(addr1) ? addr1 : addr1.trim());
							futureAaddressVO.setAddressLine2(StringUtils
									.isEmpty(addr2) ? addr2 : addr2.trim());
							futureAaddressVO
									.setCity(StringUtils.isEmpty(city) ? city
											: city.trim());
							futureAaddressVO.setState(StringUtils
									.isEmpty(state) ? state : state.trim());
							futureAaddressVO.setZip(StringUtils
									.isEmpty(zip_code) ? zip_code : zip_code
									.trim());
							futureAaddressVO
									.setCompany(resultSet
											.getString(BBBGiftRegistryConstants.COMPANY));
							futureAaddressVO
									.setPrimaryPhone(resultSet
											.getString(BBBGiftRegistryConstants.DAY_PHONE));
							shippingVO
									.setFutureshippingAddress(futureAaddressVO);
							shippingVO
									.setFutureShippingDate(resultSet
											.getString(BBBGiftRegistryConstants.AS_OF_DT));
							registryVO.setShipping(shippingVO);
							registrySummaryVO
									.setFutureShippingAddress(futureAaddressVO);
							registrySummaryVO
									.setFutureShippingDate(resultSet
											.getString(BBBGiftRegistryConstants.AS_OF_DT));
						}
					}
				}
				
				long afterRegNames = System.currentTimeMillis();
				logDebug("Total time taken to fetch details from REG_NAMES :: " + (afterRegNames-beforeRegNames));

				// get values from reg_header -- event details captured here
				long beforeRegHeader = System.currentTimeMillis();
				resultSet = (ResultSet) cs.getObject(6);
				String eventType = null;
				while (resultSet.next()) {
					this.logDebug("populating eventVO with event details and registryTypes with  type name for registry id : "
							+ registryId);
					eventType = resultSet
							.getString(BBBGiftRegistryConstants.COL_EVENT_TYPE);
					registryTypes.setRegistryTypeName(eventType);
					//registryTypes.setRegistryTypeDesc(getCatalogTools().getRegistryTypeName(eventType, siteId)); 
					if(("0").equalsIgnoreCase(resultSet.getString(BBBGiftRegistryConstants.EVENT_DT))){
					eventVO.setEventDate(null);
					}else{
						eventVO.setEventDate(resultSet
								.getString(BBBGiftRegistryConstants.EVENT_DT));
					}
					eventVO.setShowerDate(resultSet
							.getString(BBBGiftRegistryConstants.COL_SHOWER_DATE));
					eventVO.setGuestCount(resultSet
							.getString(BBBGiftRegistryConstants.NUMBER_OF_GUEST));
					if (null != eventType
							&& BBBGiftRegistryConstants.EVENT_TYPE_COLLEGE
									.equals(eventType)) {
						eventVO.setCollege(contactAddressVO.getCompany());
					}
					//adding code to get registry IS_PUBLIC value
					if(resultSet.getString(BBBGiftRegistryConstants.IS_PUBLIC)!=null && (resultSet.getString(BBBGiftRegistryConstants.IS_PUBLIC)).equalsIgnoreCase("N")){
					registryVO.setIsPublic(BBBGiftRegistryConstants.IS_PUBLIC_FALSE);
					registrySummaryVO.setIsPublic(BBBGiftRegistryConstants.IS_PUBLIC_FALSE);
					}else{
						registryVO.setIsPublic(BBBGiftRegistryConstants.IS_PUBLIC_TRUE);
						registrySummaryVO.setIsPublic(BBBGiftRegistryConstants.IS_PUBLIC_TRUE);
					}
					//End
					//Adding the code for bride and groom
					
					registryVO.setRegBG(resultSet.getString(BBBGiftRegistryConstants.REGISTRANT_BG));
					registryVO.setCoRegBG(resultSet.getString(BBBGiftRegistryConstants.COREGISTRANT_BG));
					
					registryVO
							.setNetworkAffiliation(resultSet
									.getString(BBBGiftRegistryConstants.NETWORK_AFFILIATE_FLAG));
					registryVO.setRegistryType(registryTypes);
					registryVO.setPassword(resultSet
							.getString(BBBGiftRegistryConstants.PASSWORD));
					registryVO.setHint(resultSet
							.getString(BBBGiftRegistryConstants.PASSWORD_HINT));
					registrySummaryVO.setEventDate(resultSet
							.getString(BBBGiftRegistryConstants.EVENT_DT));
					registrySummaryVO
							.setBridalToolkitToken(resultSet
									.getString(BBBGiftRegistryConstants.BRIDAL_TOOLKIT));

				}

				long afterRegHeader = System.currentTimeMillis();
				logDebug("Total time taken to fetch details from REG_HEADER :: " + (afterRegHeader-beforeRegHeader));
				// get values from reg_baby -- baby details
				long beforeRegBaby = System.currentTimeMillis();
				resultSet = (ResultSet) cs.getObject(7);
				while (resultSet.next()) {
					this.logDebug("populating baby details for registry id : "
							+ registryId);
					eventVO.setBabyGender(resultSet
							.getString(BBBGiftRegistryConstants.GENDER));
					eventVO.setBabyNurseryTheme(resultSet
							.getString(BBBGiftRegistryConstants.DECOR));
					eventVO.setBabyName(resultSet
							.getString(BBBGiftRegistryConstants.FIRST_NAME_COPY));

				}
				
				long afterRegBaby = System.currentTimeMillis();
				logDebug("Total time taken to fetch details from REG_BABY :: " + (afterRegBaby-beforeRegBaby));
				// get values from reg_pref_store -- preferred store details
				// captured here.
				
				long beforeRegStore = System.currentTimeMillis();
				resultSet = (ResultSet) cs.getObject(8);
				while (resultSet.next()) {
					this.logDebug("preferred store details for registry id : "
							+ registryId);
					registryVO.setPrefStoreNum(resultSet
							.getString(BBBGiftRegistryConstants.STORE_NUM));
					registryVO.setRefStoreContactMethod(resultSet
							.getString(BBBGiftRegistryConstants.CONTACT_FLAG));
				}
				
				long afterRegStore = System.currentTimeMillis();
				logDebug("Total time taken to fetch details from REG_PREF_STORE :: " + (afterRegStore-beforeRegStore));
			}

			registryVO.setRegistryId(registryId);
			registryVO.setEvent(eventVO);
			registryVO.setRegistryType(registryTypes);
			// registryVO.setRegistrantVO(registrantVO);
			registryVO.setPrimaryRegistrant(registrantVO);
			registryVO.setCoRegistrant(coRegistrantVO);
			registryResVO.setRegistryVO(registryVO);
			registryTypes.setRegistryTypeDesc(getCatalogTools().getRegistryTypeName(registryTypes.getRegistryTypeName(),extractSiteId()));
			registrySummaryVO.setRegistryType(registryTypes);
			registryResVO.setRegistrySummaryVO(registrySummaryVO);
		} catch (SQLException excep) {
			if (excep.getMessage()!=null && excep.getMessage().contains(
					BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
				logInfo("GiftRegistryTools.getRegistryInfoFromEcomAdmin() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
			} else {

			logError("SQL exception while registry header info "
					+ "in GiftRegistryTools", excep);
			throw new BBBSystemException(
					"An exception occurred while executing the sql statement.",
					excep);
			}
		}
			finally {
			closeEcomAdminResources(con, resultSet, cs);
		}
		long end = System.currentTimeMillis();
		logDebug("Total time taken to execute getRegistryInfoFromEcomAdmin :: " +  (end-start));
		return registryResVO;

	}

	/**
	 * @param con
	 * @param resultSet
	 * @param cs
	 * @throws BBBSystemException
	 */
	protected void closeEcomAdminResources(Connection con, ResultSet resultSet, CallableStatement cs)
			throws BBBSystemException {
		try {
			if (resultSet != null) {
				resultSet.close();
			}
			if (cs != null) {
				cs.close();
			}
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			throw new BBBSystemException(
					"An exception occurred while executing the sql statement.",
					e);
		}finally{
			BBBPerformanceMonitor.end("GiftRegistryTools.getRegistryInfoFromEcomAdmin()");
		}
	}
	
	
	
	public String getRegistryStatus(String registryId,
			String siteId)throws BBBBusinessException, BBBSystemException{
		BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryStatus()");
		this.logDebug("GiftRegistryTools.getRegistryStatus() starts.");
		this.logDebug("Method Description : get registry status usign registry Id and site id");
		String registryStatus=null;
		
		Connection con = null;
		CallableStatement cs = null;
		ResultSet resultSet = null;
		try {
			con = ((GSARepository) getRegistryInfoRepository()).getDataSource()
					.getConnection();

			if (con != null) {
				final String getRegInfostoredProc = "{call ECOMADMIN.ATGWS.GET_REG_INFO2(?,?,?,?,?,?,?,?)}";

				// Step-3: prepare the callable statement
				cs = con.prepareCall(getRegInfostoredProc);

				// Step-4: set input parameters ...
				// first input argument
				cs.setInt(1, Integer.parseInt(registryId));
				cs.setInt(2, Integer.parseInt(siteId));
				this.logDebug("GiftRegistryTools.getRegistryStatus() | parameters passed into procedure are registryId : "
						+ registryId + " and siteId : " + siteId);
				// Step-5: register output parameters ...
				cs.registerOutParameter(3, java.sql.Types.INTEGER);
				cs.registerOutParameter(4, java.sql.Types.INTEGER);

				cs.registerOutParameter(5, OracleTypes.CURSOR);
				cs.registerOutParameter(6, OracleTypes.CURSOR);
				cs.registerOutParameter(7, OracleTypes.CURSOR);
				cs.registerOutParameter(8, OracleTypes.CURSOR);

				// Step-6: execute the stored procedures: getRegInfostoredProc
				cs.executeUpdate();
				
				resultSet = (ResultSet) cs.getObject(6);
				while (resultSet.next()) {
					this.logDebug("Reading registryStatus : "
							+ registryId);
					if(resultSet.getString(BBBGiftRegistryConstants.IS_PUBLIC)!=null){
						registryStatus =resultSet.getString(BBBGiftRegistryConstants.IS_PUBLIC);
				}
				}
			}
			}catch (SQLException excep) {
			if (excep.getMessage().contains(
					BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
				logInfo("GiftRegistryTools.getRegistryStatus() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
			} else {

				logError("SQL exception while registry header info "
						+ "in GiftRegistryTools", excep);
				throw new BBBSystemException(
						"An exception occurred while executing the sql statement.",
						excep);
			}
			} finally {
				closeRegistryInfoRepoResources(cs, con, resultSet);
				BBBPerformanceMonitor.start("GiftRegistryTools.getRegistryStatus()");
			}
		
		return registryStatus;
	}

	public RegistryResVO createRegistry(RegistryVO registryVO,
			RegistryHeaderVO registryHeaderVO, List<RegNamesVO> registrantVOs,
			List<RegNamesVO> shippingVOs, RegistryBabyVO registryBabyVO,
			RegistryPrefStoreVO registryPrefStoreVO) throws BBBSystemException,
			BBBBusinessException {

		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.createRegistry() method starts in PROCEDURE mode");
		}
		boolean isError = false;
		Transaction pTransaction = null;
		RegistryResVO registryResVO = new RegistryResVO();
		Connection connection = null;
		boolean isCreateSuccess = false;
		try {
			pTransaction = this.ensureTransaction();
			String rowXNGUser = this.getRowXngUser();
			connection = ((GSARepository) getRegistryInfoRepository())
					.getDataSource().getConnection();
			connection.setAutoCommit(false);
			/**
			 * Step 1: Insert registry header
			 */
			insertRegistryHeader(registryHeaderVO, connection, rowXNGUser,
					BBBGiftRegistryConstants.INSERT_REG_HEADER);
			if (!BBBUtility.isEmpty(registryHeaderVO.getRegNum())) {
				/***
				 * Step 2: Insert Registry Names Entry for registrant &
				 * co-registrant
				 */
				insertOrUpdateRegNames(registrantVOs, connection, rowXNGUser,
						registryHeaderVO.getRegNum(),
						registryHeaderVO.getJdaDate(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.INSERT_REG_NAMES);
			}

			if (!BBBUtility.isEmpty(registryHeaderVO.getRegNum())) {
				/***
				 * Step 3: Insert Registry Names Entry for Shipping Addresses
				 * 
				 */

				insertOrUpdateRegNames(shippingVOs, connection, rowXNGUser,
						registryHeaderVO.getRegNum(),
						registryHeaderVO.getJdaDate(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.INSERT_REG_NAMES);
			}

			if (BBBGiftRegistryConstants.EVENT_TYPE_BABY
					.equalsIgnoreCase(registryHeaderVO.getEventType())) {
				/***
				 * Step 4: Insert Registry Baby Entry, if event type is BA1
				 */
				insertOrUpdateRegBaby(registryBabyVO, connection, rowXNGUser,
						registryHeaderVO.getRegNum(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.INSERT_REG_BABY);
			}

			if (!BBBUtility.isEmpty(registryPrefStoreVO.getStoreNum())) {
				/***
				 * Step 5: Insert Registry Pref Store Entry, if store number is
				 * available.
				 */
				insertOrUpdateRegPrefStore(registryPrefStoreVO, connection,
						rowXNGUser, registryHeaderVO.getRegNum(),
						registryHeaderVO.getSiteId(),
						BBBGiftRegistryConstants.INSERT_REG_PREF_STORE);
			}
			registryResVO.setRegistryId(Long.parseLong(registryHeaderVO
					.getRegNum()));
			isCreateSuccess = true;
		} catch (SQLException exception) {
			if (exception.getMessage()!=null && exception.getMessage().contains(
					BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
				logInfo("GiftRegistryTools.createRegistry() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND);
			} else {
			isError = true;
			logError(BBBGiftRegistryConstants.ERROR_LOG_SQL_EXCEPTION,
					exception);
			}
			Object[] args = getGiftRegUtils()
					.populateInputToLogErrorOrValidate(registryVO);
			ServiceErrorVO errorVO = (ServiceErrorVO) getGiftRegUtils()
					.logAndFormatError("CreateRegistry2", null,
							"serviceErrorVO", exception, args);
			registryResVO.setServiceErrorVO(errorVO);
		} catch (NotSupportedException exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "NotSupportedException in GiftRegistryTools.createRegistry", "createRegistry"), exc);
		} catch (SystemException exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "SystemException in in GiftRegistryTools.createRegistry", "createRegistry"), exc);
		} catch (Exception exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "Exception in in GiftRegistryTools.createRegistry", "createRegistry"), exc);
		} finally {
			endTransaction(isError, pTransaction);
			closeRegistryInfoRepositoryConnection(connection);
		}

		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.createRegistry() method ends with success flag :: "
					+ isCreateSuccess);
		}
		return registryResVO;

	}

	/**
	 * @param connection
	 * @throws BBBSystemException
	 */
	protected void closeRegistryInfoRepositoryConnection(Connection connection) throws BBBSystemException {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			throw new BBBSystemException("An exception occurred while closing the connection");
		}
	}

	private void insertOrUpdateRegPrefStore(
			RegistryPrefStoreVO registryPrefStoreVO, Connection connection,
			String rowXNGUser, String regNum, String siteId,
			String callableStatementStr) throws SQLException {
		BBBPerformanceMonitor.start("GiftRegistryTools.insertOrUpdateRegPrefStore()");
		this.logDebug("Method Description : Insert/Update registry prefered store");
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertOrUpdateRegPrefStore() method starts with callable statement string :: "
					+ callableStatementStr);
		}
		boolean success = false;
		if (connection != null) {
			CallableStatement cs = connection.prepareCall(callableStatementStr);
			if(null!=cs){
			try {
				cs.setLong(1, Long.parseLong(regNum));
				cs.setLong(
						2,
						!BBBUtility.isEmpty(registryPrefStoreVO.getStoreNum()) ? Integer
								.parseInt(registryPrefStoreVO.getStoreNum())
								: BBBCoreConstants.ZERO);
				cs.setString(
						3,
						!BBBUtility.isEmpty(registryPrefStoreVO
								.getContactFlag()) ? registryPrefStoreVO
								.getContactFlag() : BBBCoreConstants.BLANK);

				// execute the stored procedures: INSERT_REG_PREF_STORE or
				// UPDATE_REG_PREF_STORE
				cs.executeUpdate();
				success = true;
			} finally {
				if (!cs.isClosed()) {
					cs.close();
				}
				BBBPerformanceMonitor.end("GiftRegistryTools.insertOrUpdateRegPrefStore()");
			}
		}
		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertOrUpdateRegPrefStore() method ends with success :: "
					+ success);
		}
	}

	private void insertOrUpdateRegBaby(RegistryBabyVO registryBabyVO,
			Connection connection, String rowXNGUser, String regNum,
			String siteId, String callableStatementStr) throws SQLException {
		BBBPerformanceMonitor.start("GiftRegistryTools.insertOrUpdateRegBaby()");
		this.logDebug("Method Description : Create/Update baby registry");
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertOrUpdateRegBaby() method starts with callable statement string :: "
					+ callableStatementStr);
		}
		boolean success = false;
		if (connection != null) {

			CallableStatement cs = connection.prepareCall(callableStatementStr);
			if(null != cs){
			try {

				cs.setLong(1, Long.parseLong(regNum));
				cs.setString(2, BBBCoreConstants.BLANK);
				cs.setString(
						3,
						registryBabyVO.getGender() != null ? registryBabyVO
								.getGender() : BBBCoreConstants.BLANK);
				cs.setString(
						4,
						!BBBUtility.isEmpty(registryBabyVO.getDecor()) ? registryBabyVO
								.getDecor() : BBBCoreConstants.BLANK);
				cs.setLong(5, getStoreNum(siteId));
				cs.setString(6, getCreateProgram(siteId));
				cs.setString(7, rowXNGUser);

				// execute the stored procedures: INSERT_REG_BABY or
				// UPDATE_REG_BABY
				cs.executeUpdate();
				success = true;

			} finally {
				if (!cs.isClosed()) {
					cs.close();
				}
				BBBPerformanceMonitor.end("GiftRegistryTools.insertOrUpdateRegBaby()");
			}
			}	
		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertOrUpdateRegBaby() method ends with success :: "
					+ success);
		}

	}

	private void insertOrUpdateRegNames(List<RegNamesVO> regNamesVOs,
			Connection connection, String rowXNGUser, String regNum,
			Long jdaDate, String siteFlag, String callableStatementStr)
			throws SQLException {
		BBBPerformanceMonitor.start("GiftRegistryTools.insertOrUpdateRegNames()");
		this.logDebug("Method Description : Insert Registry Names Entry for registrant & co-registrant");
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertOrUpdateRegNames() method starts with callable statement string :: "
					+ callableStatementStr);
		}
		boolean success = false;
		if (connection != null && !BBBUtility.isListEmpty(regNamesVOs)) {

			CallableStatement cs = connection.prepareCall(callableStatementStr);
			try {

				for (RegNamesVO regNamesVO : regNamesVOs) {

					cs.setLong(1, Long.parseLong(regNum));
					cs.setString(2, regNamesVO.getNameAddrType().toUpperCase());
					cs.setString(3, !BBBUtility.isEmpty(regNamesVO
							.getLastName()) ? regNamesVO.getLastName()
							: BBBCoreConstants.BLANK);
					cs.setString(4, !BBBUtility.isEmpty(regNamesVO
							.getFirstName()) ? regNamesVO.getFirstName()
							: BBBCoreConstants.BLANK);
					cs.setString(
							5,
							!BBBUtility.isEmpty(regNamesVO.getCompany()) ? regNamesVO
									.getCompany() : BBBCoreConstants.BLANK);
					cs.setString(6, !BBBUtility.isEmpty(regNamesVO
							.getAddress1()) ? regNamesVO.getAddress1()
							.toUpperCase() : BBBCoreConstants.BLANK);
					cs.setString(7, !BBBUtility.isEmpty(regNamesVO
							.getAddress2()) ? regNamesVO.getAddress2()
							.toUpperCase() : BBBCoreConstants.BLANK);
					cs.setString(
							8,
							!BBBUtility.isEmpty(regNamesVO.getCity()) ? regNamesVO
									.getCity().toUpperCase()
									: BBBCoreConstants.BLANK);
					cs.setString(
							9,
							!BBBUtility.isEmpty(regNamesVO.getState()) ? regNamesVO
									.getState().toUpperCase()
									: BBBCoreConstants.BLANK);
					cs.setString(10, !BBBUtility.isEmpty(regNamesVO
							.getZipCode()) ? regNamesVO.getZipCode()
							: BBBCoreConstants.BLANK);
					cs.setString(
							11,
							getGiftRegUtils().stripNonAlphaNumerics(
									regNamesVO.getDayPhone()));
					cs.setString(
							12,
							getGiftRegUtils().stripNonAlphaNumerics(
									regNamesVO.getEvePhone()));
					cs.setString(13,
							regNamesVO.getEmailId() != null ? regNamesVO
									.getEmailId().toUpperCase()
									: BBBCoreConstants.BLANK);
					if (regNamesVO.getNameAddrType().equalsIgnoreCase(
							BBBGiftRegistryConstants.FU)) {
						cs.setLong(
								14,
								!BBBUtility.isEmpty(regNamesVO
										.getAsOfDateFtrShipping()) ? Long
										.parseLong(regNamesVO
												.getAsOfDateFtrShipping())
										: BBBCoreConstants.ZERO);
					} else {
						cs.setLong(14, BBBCoreConstants.ZERO);
					}
					cs.setLong(15, jdaDate);
					cs.setString(16, this.getCreateProgram(siteFlag));
					cs.setString(17, !BBBUtility.isEmpty(regNamesVO
							.getDayPhoneExt()) ? regNamesVO.getDayPhoneExt()
							: BBBCoreConstants.BLANK);
					cs.setString(18, !BBBUtility.isEmpty(regNamesVO
							.getEvePhoneExt()) ? regNamesVO.getEvePhoneExt()
							: BBBCoreConstants.BLANK);
					cs.setString(
							19,
							regNamesVO.getPrefContMeth() != null ? regNamesVO
									.getPrefContMeth() : BBBCoreConstants.BLANK);
					cs.setString(
							20,
							regNamesVO.getPrefContTime() != null ? regNamesVO
									.getPrefContTime() : BBBCoreConstants.BLANK);
					cs.setString(
							21,
							regNamesVO.getEmailFlag() != null ? regNamesVO
									.getEmailFlag() : BBBCoreConstants.BLANK);
					cs.setString(
							22,
							!BBBUtility.isEmpty(regNamesVO.getMaiden()) ? regNamesVO
									.getMaiden() : BBBCoreConstants.BLANK);
					cs.setLong(23, getStoreNum(siteFlag));
					cs.setString(24, getCreateProgram(siteFlag));
					cs.setString(25, !BBBUtility.isEmpty(regNamesVO
							.getAtgProfileId()) ? regNamesVO.getAtgProfileId()
							: BBBCoreConstants.BLANK);
					cs.setString(
							26,
							!BBBUtility.isEmpty(regNamesVO.getAffiliateOptIn()) ? regNamesVO
									.getAffiliateOptIn()
									: BBBCoreConstants.BLANK);
					cs.setString(27, getGiftRegUtils().getCountry(siteFlag));
					cs.setString(28, rowXNGUser);

					// execute the stored procedures: INSERT_REG_NAMES or
					// UPDATE_REG_NAMES
					cs.executeUpdate();
					success = true;
				}

			} finally {
				if (cs != null) {
					cs.close();
				}
				BBBPerformanceMonitor.end("GiftRegistryTools.insertOrUpdateRegNames()");
			}
		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertOrUpdateRegNames() method ends with success :: "
					+ success);
		}
	}

	private void insertRegistryHeader(RegistryHeaderVO registryHeaderVO,
			Connection connection, String rowXNGUser,
			String callableStatementStr) throws SQLException {
		BBBPerformanceMonitor.start("GiftRegistryTools.insertRegistryHeader()");
		this.logDebug("Method Description : Insert Registry Hedaer");
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertRegistryHeader() method starts with callable statement string :: "
					+ callableStatementStr
					+ "RegistryHeaderVO :: "
					+ registryHeaderVO);
		}
		boolean success = false;
		if (connection != null) {

			CallableStatement cs = connection.prepareCall(callableStatementStr);
			if (cs != null) {
			try {

				cs.setString(1, registryHeaderVO.getEventType());
				cs.setLong(
						2,
						!BBBUtility.isEmpty(registryHeaderVO.getEventDate()) ? Long
								.parseLong(registryHeaderVO.getEventDate())
								: BBBCoreConstants.ZERO);
				cs.setString(
						3,
						!BBBUtility.isEmpty(registryHeaderVO
								.getPromoEmailFlag()) ? registryHeaderVO
								.getPromoEmailFlag() : BBBCoreConstants.BLANK);
				cs.setString(4, !BBBUtility.isEmpty(registryHeaderVO
						.getPassword()) ? registryHeaderVO.getPassword()
						: BBBCoreConstants.BLANK);
				cs.setString(
						5,
						!BBBUtility.isEmpty(registryHeaderVO.getPasswordHint()) ? registryHeaderVO
								.getPasswordHint() : BBBCoreConstants.BLANK);
				cs.setLong(6, getStoreNum(registryHeaderVO.getSiteId()));
				cs.setLong(7, getStoreNum(registryHeaderVO.getSiteId()));
				cs.setString(
						8,
						!BBBUtility.isEmpty(registryHeaderVO.getGuestPassword()) ? registryHeaderVO
								.getGuestPassword() : BBBCoreConstants.BLANK);
				cs.setString(9, getCreateProgram(registryHeaderVO.getSiteId()));
				cs.setString(10, !BBBUtility.isEmpty(registryHeaderVO
						.getShowerDate()) ? registryHeaderVO.getShowerDate()
						: BBBCoreConstants.BLANK);
				cs.setString(11, !BBBUtility.isEmpty(registryHeaderVO
						.getOtherDate()) ? registryHeaderVO.getOtherDate()
						: BBBCoreConstants.BLANK);
				cs.setString(
						12,
						!BBBUtility.isEmpty(registryHeaderVO
								.getNetworkAffiliation()) ? registryHeaderVO
								.getNetworkAffiliation()
								: BBBCoreConstants.BLANK);
				if (!BBBUtility
						.isEmpty(registryHeaderVO.getEstimateNumGuests())) {
					cs.setLong(13, Integer.parseInt(registryHeaderVO
							.getEstimateNumGuests()));
				} else {
					cs.setLong(13, BBBCoreConstants.ZERO);
				}
				cs.setString(14, !BBBUtility.isEmpty(registryHeaderVO
						.getIsPublic()) ? registryHeaderVO.getIsPublic()
						: BBBCoreConstants.NO_CHAR);
				cs.setString(15, registryHeaderVO.getSiteId());
				cs.setLong(16, getStoreNum(registryHeaderVO.getSiteId()));
				cs.setString(17, getCreateProgram(registryHeaderVO.getSiteId()));
				cs.setString(18, rowXNGUser);
				cs.registerOutParameter(19, java.sql.Types.BIGINT);
				cs.registerOutParameter(20, java.sql.Types.BIGINT);

				extractDBCall(cs);
				registryHeaderVO.setRegNum(Long.toString(cs.getLong(19)));
				registryHeaderVO.setJdaDate(cs.getLong(20));
				success = true;
			} finally {
				cs.close();
				BBBPerformanceMonitor.end("GiftRegistryTools.insertRegistryHeader()");
			}
			}
		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.insertRegistryHeader() method ends with success :: "
					+ success);
		}
	}

	/**
	 * This method used to update registry header
	 * 
	 * @param registryHeaderVO
	 * @param connection
	 * @param rowXNGUser
	 * @param regNum
	 * @param jdaDate
	 * @throws SQLException
	 */
	private void updateRegistryHeader(final RegistryHeaderVO registryHeaderVO,
			Connection connection, final String rowXNGUser,
			final String regNum, final Long jdaDate) throws SQLException {
		BBBPerformanceMonitor.start("GiftRegistryTools.updateRegistryHeader()");
		this.logDebug("Method Description : Update Registry Hedaer");
		if (connection != null) {
			CallableStatement cs = null;
			try {
				cs = connection
						.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_HEADER);
				cs.setLong(1, Long.parseLong(regNum));
				cs.setLong(
						2,
						!BBBUtility.isEmpty(registryHeaderVO.getEventDate()) ? Long
								.parseLong(registryHeaderVO.getEventDate())
								: BBBCoreConstants.ZERO);
				cs.setString(
						3,
						!BBBUtility.isEmpty(registryHeaderVO
								.getPromoEmailFlag()) ? registryHeaderVO
								.getPromoEmailFlag() : BBBCoreConstants.BLANK);
				cs.setString(4, !BBBUtility.isEmpty(registryHeaderVO
						.getPassword()) ? registryHeaderVO.getPassword()
						: BBBCoreConstants.BLANK);
				cs.setString(
						5,
						!BBBUtility.isEmpty(registryHeaderVO.getPasswordHint()) ? registryHeaderVO
								.getPasswordHint() : BBBCoreConstants.BLANK);
				cs.setLong(6, getStoreNum(registryHeaderVO.getSiteId()));
				cs.setLong(7, getStoreNum(registryHeaderVO.getSiteId()));
				cs.setString(
						8,
						!BBBUtility.isEmpty(registryHeaderVO.getGuestPassword()) ? registryHeaderVO
								.getGuestPassword() : BBBCoreConstants.BLANK);
				cs.setString(9, getCreateProgram(registryHeaderVO.getSiteId()));
				cs.setString(10, !BBBUtility.isEmpty(registryHeaderVO
						.getShowerDate()) ? registryHeaderVO.getShowerDate()
						: BBBCoreConstants.BLANK);
				cs.setString(11, !BBBUtility.isEmpty(registryHeaderVO
						.getOtherDate()) ? registryHeaderVO.getOtherDate()
						: BBBCoreConstants.BLANK);
				cs.setString(
						12,
						!BBBUtility.isEmpty(registryHeaderVO
								.getNetworkAffiliation()) ? registryHeaderVO
								.getNetworkAffiliation()
								: BBBCoreConstants.BLANK);
				if (!BBBUtility
						.isEmpty(registryHeaderVO.getEstimateNumGuests())) {
					cs.setLong(13, Long.parseLong(registryHeaderVO
							.getEstimateNumGuests()));
				} else {
					cs.setLong(13, BBBCoreConstants.ZERO);
				}
				cs.setString(14, !BBBUtility.isEmpty(registryHeaderVO
						.getIsPublic()) ? registryHeaderVO.getIsPublic()
						: BBBCoreConstants.NO_CHAR);
				cs.setString(15, registryHeaderVO.getSiteId());
				cs.setLong(16, getStoreNum(registryHeaderVO.getSiteId()));
				cs.setString(17, getCreateProgram(registryHeaderVO.getSiteId()));
				cs.setString(18, rowXNGUser);
				cs.registerOutParameter(19, java.sql.Types.BIGINT);
				extractDBCall(cs);
				registryHeaderVO.setJdaDate(cs.getLong(19));
			} finally {
				if (cs != null) {
					cs.close();
				}
				BBBPerformanceMonitor.end("GiftRegistryTools.updateRegistryHeader()");
			}
		}
	}

	/**
	 * This method used to disable future shipping option.
	 * 
	 * @param connection
	 * @param rowXNGUser
	 * @param regNum
	 * @param siteId
	 * @throws SQLException
	 * @throws BBBSystemException
	 */
	private void disableFutureShipping(Connection connection,
			String rowXNGUser, String regNum, String siteId)
			throws SQLException, BBBSystemException {

		if (connection != null) {
			CallableStatement cs = null;
			try {
				cs = connection
						.prepareCall(BBBGiftRegistryConstants.DISABLE_FUTURE_SHIPPING);
				cs.setLong(1, Long.parseLong(regNum));
				cs.setLong(2, getStoreNum(siteId));
				cs.setString(3, getCreateProgram(siteId));
				cs.setString(4, rowXNGUser);
				cs.executeUpdate();
			} finally {
				if (cs != null) {
					cs.close();
				}
			}
		}
	}

	/**
	 * This method links registry to the ATG profile.
	 * 
	 * @param registryNum
	 * @param registrantType
	 * @param profileId
	 * @param email
	 * @return
	 * @throws BBBBusinessException
	 * @throws BBBSystemException
	 */
	public RegistryResVO linkRegistryToATGProfile(final long registryNum,
			final String registrantType, final String profileId,
			final String email) throws BBBBusinessException, BBBSystemException {
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.linkRegistryToATGProfile() method start");
		}
		BBBPerformanceMonitor.start("GiftRegistryTools.linkRegistryToATGProfile()");
		this.logDebug("Method Description : links registry to the ATG profile");
		RegistryResVO registryResVO = new RegistryResVO();
		boolean isUpdateSuccess = false;
		Connection connection = null;
		CallableStatement cs = null;
		boolean isError = false;
		Transaction pTransaction = null;
		try {
			pTransaction = this.ensureTransaction();
			String rowXNGUser = this.getRowXngUser();
			connection = ((GSARepository) getRegistryInfoRepository())
					.getDataSource().getConnection();
			
			if (connection != null) {
				connection.setAutoCommit(false);
				cs = connection
						.prepareCall(BBBGiftRegistryConstants.LINK_REGISTRY_TO_ATG_PROFILE);
				cs.setLong(1, registryNum);
				cs.setString(2, registrantType);
				cs.setString(3, profileId);
				cs.setString(4, email);
				cs.setString(5, rowXNGUser);
				cs.registerOutParameter(6, java.sql.Types.LONGVARCHAR);
				cs.registerOutParameter(7, java.sql.Types.LONGVARCHAR);
				extractDBCall(cs);

				if (BBBCoreConstants.YES_CHAR.equalsIgnoreCase(cs.getString(6))) {
					ServiceErrorVO errorVO = new ServiceErrorVO();
					errorVO.setErrorExists(true);
					errorVO.setErrorId(BBBGiftRegistryConstants.REGISTRY_INVALID_PASSWORD);
					errorVO.setErrorMessage(cs.getString(7));
					errorVO.setErrorDisplayMessage(BBBGiftRegistryConstants.ERROR_INVALID_REGISTRY_PASSWORD);
					registryResVO.setServiceErrorVO(errorVO);
				}
				isUpdateSuccess = true;
			}
		} catch (SQLException e) {
			if (e.getMessage() != null
					&& e.getMessage().contains(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)) {
				logInfo("GiftRegistryTools.linkRegistryToATGProfile():: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND );
			
			} else {
			isError = true;
			logError("GiftRegistryTools.linkRegistryToATGProfile exception occurred: "
					+ e);
			}
			Object[] args = { registryNum, registrantType, profileId, email };
			ServiceErrorVO errorVO = (ServiceErrorVO) getGiftRegUtils()
					.logAndFormatError("linkRegistryToATGProfile", null,
							"ServiceErrorVO", e, args);
			registryResVO.setServiceErrorVO(errorVO);
		} catch (NotSupportedException exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "NotSupportedException in GiftRegistryTools.linkRegistryToATGProfile", "linkRegistryToATGProfile"), exc);
		} catch (SystemException exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "SystemException in in GiftRegistryTools.linkRegistryToATGProfile", "linkRegistryToATGProfile"), exc);
		} catch (Exception exc) {
			isError = true;
			logError(LogMessageFormatter.formatMessage(null, "Exception in in GiftRegistryTools.linkRegistryToATGProfile", "linkRegistryToATGProfile"), exc);
		} finally {
			closeLinkRegToATGProfileResources(connection, cs, isError, pTransaction);
			BBBPerformanceMonitor.end("GiftRegistryTools.linkRegistryToATGProfile()");

		}
		if (isLoggingDebug()) {
			this.logDebug("GiftRegistryTools.linkRegistryToATGProfile() method ends with success message: "
					+ isUpdateSuccess);
		}
		return registryResVO;
	}

	/**
	 * @param connection
	 * @param cs
	 * @param isError
	 * @param pTransaction
	 * @throws BBBSystemException
	 */
	protected void closeLinkRegToATGProfileResources(Connection connection, CallableStatement cs, boolean isError,
			Transaction pTransaction) throws BBBSystemException {
		if (cs != null) {
			try {
				cs.close();
			} catch (SQLException e) {
				isError = true;
				throw new BBBSystemException(
						"Exception occured while closing the callable statement in GiftRegistryTools.linkRegistryToATGProfile:",
						e);
			}
		}
		this.endTransaction(isError, pTransaction);
		if (connection != null){
			try {
				connection.close();
			} catch (final SQLException e) {
				throw new BBBSystemException(
						"Exception occured while commit/closing the connection in GiftRegistryTools.linkRegistryToATGProfile:",
						e);
			}
		}	
	}

	/**
	 * This method gets RowXngUser from BCC
	 * 
	 * @return
	 * @throws BBBSystemException
	 * @throws BBBBusinessException
	 */
	private String getRowXngUser() throws BBBSystemException,
			BBBBusinessException {
		String rowXNGUser = "";
		List<String> rowXNGUserList = getCatalogTools().getAllValuesForKey(
				BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,
				BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR);

		if (!BBBUtility.isListEmpty(rowXNGUserList)) {
			rowXNGUser = rowXNGUserList.get(0);
		}
		else
		{
			logDebug("Row Xng Config Key is empty for " +BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR);
		}
		return rowXNGUser;
	}

	/**
	 * This method fetches the registryCode based on registryType and siteId
	 * 
	 * @return String
	 * @throws BBBSystemException 
	 * @throws BBBBusinessException 
	 */
	    public  String getRegistryTypeCode(final String registryType, final String siteId)
	                    throws BBBSystemException, BBBBusinessException {

	        this.logDebug("Catalog API Method Name [getRegistryTypeName] registryCode[" + registryType + "]");

	        String registryTypeCode = "";
	        try {
	            BBBPerformanceMonitor.start(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypeName");
	            final RepositoryItem siteConfiguration = this.getSiteRepository().getItem(siteId,
	                            BBBCatalogConstants.SITE_ITEM_DESCRIPTOR);
	            if (siteConfiguration != null) {
	                @SuppressWarnings ("unchecked")
	                final Set<RepositoryItem> registryRepositoryItem = (Set<RepositoryItem>) siteConfiguration
	                                .getPropertyValue(BBBCatalogConstants.REGISTRY_TYPES_SITE_PROPERTY_NAME);
	                if ((registryRepositoryItem != null) && !registryRepositoryItem.isEmpty()) {
	                    for (final RepositoryItem registryRepoItem : registryRepositoryItem) {
	                        if ((registryRepoItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME) != null)
	                                        && ((String) registryRepoItem.getPropertyValue(BBBCatalogConstants.REGISTRY_TYPE_NAME_REGISTRY_PROPERTY_NAME))
	                                                        .equalsIgnoreCase(registryType)) {

	                            registryTypeCode = (String) registryRepoItem
	                                            .getPropertyValue("registryTypeCode");
	                            break;
	                        }
	                    }
	                }
	            } else {
	                throw new BBBBusinessException(BBBCatalogErrorCodes.REGISTRY_NOT_AVAILABLE_IN_REPOSITORY,
	                                BBBCatalogErrorCodes.REGISTRY_NOT_AVAILABLE_IN_REPOSITORY);
	            }
	            return registryTypeCode;
	        } catch (final RepositoryException e) {
	            this.logError("Catalog API Method Name [getRegistryTypeCode]: RepositoryException ");
	            throw new BBBSystemException(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,
	                            BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION, e);
	        } finally {
	            BBBPerformanceMonitor.end(BBBPerformanceConstants.CATALOG_API_CALL + " getRegistryTypeName");
	        }

	    }

	/**
	 * This method ensures that a transaction exists before returning. If there
	 * is no transaction, a new one is started and returned. In this case, you
	 * must call commitTransaction when the transaction completes.
	 * 
	 * @return a <code>Transaction</code> value
	 * @throws NotSupportedException 
	 * @throws SystemException 
	 */
	protected Transaction ensureTransaction() throws NotSupportedException, SystemException {
		try {
			TransactionManager tm = getTransactionManager();
			Transaction t = tm.getTransaction();
			if (t == null) {
				tm.begin();
				t = tm.getTransaction();
				return t;
			}
			return null;
		} catch (NotSupportedException exc) {
			throw new NotSupportedException("NotSupportedException in GiftRegistryTools.ensureTransaction");
		} catch (SystemException exc) {
			throw new SystemException("SystemException in in GiftRegistryTools.ensureTransaction");
		}
	}

	/**
	 * This method ensures that the commit/rollback performed on transaction if
	 * it was returned from database operations.
	 * 
	 * @param isError
	 *            flag that represents error status
	 * @param pTransaction
	 *            transaction object
	 */
	private void endTransaction(final boolean isError,
			final Transaction pTransaction) {
		try {
			if (isError) {
				if (pTransaction != null) {
					pTransaction.rollback();
				}
			} else {
				if (pTransaction != null) {
					pTransaction.commit();
				}
			}
		} catch (SecurityException e) {
			logError(e);
		} catch (IllegalStateException e) {
			logError(e);
		} catch (RollbackException e) {
			logError(e);
		} catch (HeuristicMixedException e) {
			logError(e);
		} catch (HeuristicRollbackException e) {
			logError(e);
		} catch (SystemException e) {
			logError(e);
		}
	}

	public boolean isInventoryCheckRequired(RegistryItemVO regItemVO) {
		return false;
	}
	
	
	
	
	/**This method fetches the result from GET_REG_ITEM_LIST_BY_CATEGORY
	 * The input params for stored proc : siteId,eventCode,eventCode,isProdServer,p_GetDistinctSKUs : Set to true in case of giftGiver
	 * @param siteId
	 * @param eventCode
	 * @param registryId
	 * @return
	 * @throws SQLException
	 * @throws ParseException
	 */
	public Map<String,RegistryItemVO> fetchRegistryItemsBasedOnCategory(
			final String siteId, final String eventCode,final String registryId) throws SQLException, ParseException {
		BBBPerformanceMonitor
				.start("GiftRegistryTools.fetchRegistryItemsBasedOnCategory()");
		logDebug("Method Description : fetchRegistryItemsBasedOnCategory");
		Connection connection = null;
		connection = ((GSARepository) getRegistryInfoRepository())
				.getDataSource().getConnection();
		
		ResultSet resultSet = null;
		Map<String,RegistryItemVO> registryItemListMap=new HashMap<String,RegistryItemVO>();
		boolean isProd=!isStagingServer();
		if (connection != null) {
			connection.setAutoCommit(false);
			CallableStatement cs = null;
			try {
				cs = connection
						.prepareCall(BBBGiftRegistryConstants.GET_REG_ITEM_LIST_BY_CATEGORY);
				cs.setString(1, siteId);
				cs.setString(2, eventCode);
				cs.setLong(3, Long.parseLong(registryId));
				cs.setInt(4,isProd?1:0);
				cs.setInt(5,1);
				cs.registerOutParameter(6, OracleTypes.CURSOR);
				extractDBCall(cs);
				resultSet = (ResultSet) cs.getObject(6);
				while (resultSet.next()) {
					RegistryItemVO registryItemVO = new RegistryItemVO();
					populateRegistryItemForC1(resultSet,registryItemVO);
					//If the item type is LTL or personalised. Populate data related to that.
					if(BBBCoreConstants.LTL.equals(registryItemVO
							.getItemType())||BBBCoreConstants.PER.equalsIgnoreCase(registryItemVO.getItemType())){
					populatePersonalisedAndLTL(resultSet,registryItemVO);
					}
					if(BBBCoreConstants.LTL.equals(registryItemVO
							.getItemType())){

						registryItemListMap.put(String.valueOf(registryItemVO.getSku())+ "_"
														+ registryItemVO
																.getLtlDeliveryServices()
														+ registryItemVO.getAssemblySelected(),registryItemVO);	
					
						
					}else if(BBBCoreConstants.PER.equalsIgnoreCase(registryItemVO.getItemType())){

						registryItemListMap.put(
								String.valueOf(registryItemVO.getSku()
										+ "_"
										+ registryItemVO.getRefNum()),
								registryItemVO);
					
						
					}else{
						registryItemListMap.put(String.valueOf(registryItemVO.getSku()),registryItemVO);	
					}
					
				}
			} finally {
				if (cs != null) {
					cs.close();
				}
				if (connection != null  && !connection.isClosed()) {
					connection.close();
				}
				BBBPerformanceMonitor
						.end("GiftRegistryTools.updateRegistryHeader()");
			}
		}
		return registryItemListMap;
	
		}

	/**
	 * Populate personalised and ltl data from result set.
	 *
	 * @param resultSet the result set
	 * @param registryItemVO the registry item vo
	 * @throws SQLException the SQL exception
	 */
	public void populatePersonalisedAndLTL(ResultSet resultSet,
			RegistryItemVO registryItemVO) throws SQLException {
		this.logDebug("GiftRegistryTools.populatePersonalisedAndLTL() method starts");
		Double customizedPrice = 0.0;
		Double personalizedPrice = 0.0;
		String refNum = BBBCoreConstants.BLANK;
		String personalizationCode = BBBCoreConstants.BLANK;
		String personalizationDescription = BBBCoreConstants.BLANK;
		String personalizedImageUrls = BBBCoreConstants.BLANK;
		String imageUrlThumb = BBBCoreConstants.BLANK;
		String mobImageUrl = BBBCoreConstants.BLANK;
		String mobImageUrlThumb = BBBCoreConstants.BLANK;
		String ltlDeliveryServices = BBBCoreConstants.BLANK;
		String assemblySelected = BBBCoreConstants.BLANK;
		
		if (resultSet.getString(BBBGiftRegistryConstants.LTL_DELIVERY_SERVICE) != null) {
			ltlDeliveryServices=(String) resultSet.getString(BBBGiftRegistryConstants.LTL_DELIVERY_SERVICE);
			
		}
		registryItemVO.setLtlDeliveryServices(ltlDeliveryServices);
		
		if ((resultSet.getString(BBBGiftRegistryConstants.REFERENCE_ID) != null) && BBBCoreConstants.PER.equalsIgnoreCase(registryItemVO.getItemType())) {
			refNum=(String) resultSet.getString(BBBGiftRegistryConstants.REFERENCE_ID);
			
		}
		registryItemVO.setRefNum(refNum);
		
		if (resultSet.getString(BBBGiftRegistryConstants.ASSEMBLY_SELECTED) != null) {
			assemblySelected=(String) resultSet.getString(BBBGiftRegistryConstants.ASSEMBLY_SELECTED);
			
		}
		registryItemVO.setAssemblySelected(assemblySelected);
		
		if (resultSet.getString(BBBGiftRegistryConstants.PERSONALIZATION_CODE) != null) {
			personalizationCode=(String) resultSet.getString(BBBGiftRegistryConstants.PERSONALIZATION_CODE);
			
		}
		registryItemVO.setPersonalisedCode(personalizationCode);
		
		if (resultSet.getString(BBBGiftRegistryConstants.PERSONALIZATION_DESCRIP) != null) {
			personalizationDescription=(String) resultSet.getString(BBBGiftRegistryConstants.PERSONALIZATION_DESCRIP);
			
		}
		registryItemVO.setCustomizationDetails(personalizationDescription);
		
		if (resultSet.getString(BBBGiftRegistryConstants.IMAGE_URL) != null) {
			personalizedImageUrls=(String) resultSet.getString(BBBGiftRegistryConstants.IMAGE_URL);
			
		}
		registryItemVO.setPersonalizedImageUrls(personalizedImageUrls);
		
		
		customizedPrice = (Double)resultSet.getDouble(BBBGiftRegistryConstants.CUSTOMIZATION_PRICE);
		
		personalizedPrice = (Double) resultSet.getDouble(BBBGiftRegistryConstants.PERSONALIZATION_PRICE);
		
		registryItemVO.setCustomizedDoublePrice(customizedPrice);
		registryItemVO.setPersonlisedDoublePrice(personalizedPrice);
		if (resultSet.getString(BBBGiftRegistryConstants.IMAGE_URL_THUMB) != null) {
			imageUrlThumb=(String) resultSet.getString(BBBGiftRegistryConstants.IMAGE_URL_THUMB);
			
		}
		registryItemVO.setPersonalizedImageUrlThumbs(imageUrlThumb);
		
		if (resultSet.getString(BBBGiftRegistryConstants.MOB_IMAGE_URL) != null) {
			mobImageUrl=(String) resultSet.getString(BBBGiftRegistryConstants.MOB_IMAGE_URL);
			
		}
		registryItemVO.setPersonalizedMobImageUrlThumbs(mobImageUrl);
		
		this.logDebug("GiftRegistryTools.populatePersonalizedAndLtlRegistryItem() method ends");
	}

	/** Iterates over the result set and populates into Java bean
	 * @param resultSet
	 * @param registryItemVO
	 * @return
	 * @throws ParseException
	 * @throws SQLException
	 */
	private RegistryItemVO populateRegistryItemForC1(ResultSet resultSet,
			RegistryItemVO registryItemVO) throws ParseException, SQLException {

		this.logDebug("GiftRegistryTools.populateRegistryItem() method starts");
        if(resultSet==null){
        	return registryItemVO;
        }
		Integer qtyRequested = 0;
		Integer qtyFulfilled = 0;
		Integer qtyWebPurchased = 0;
		Integer qtyPurchased = 0;
		Integer isAboveTheLine = 1;
		Long lastMaintained = 0L;
		Long createDate = 0L;
		Integer sku = 0;
		String rowId = BBBCoreConstants.BLANK;
		String ephCatid = BBBCoreConstants.BLANK;
		String itemType = BBBCoreConstants.BLANK;
		
		if (resultSet.getString(BBBGiftRegistryConstants.C1) != null) {
			ephCatid=(String) resultSet.getString(BBBGiftRegistryConstants.C1);
		}
		
		registryItemVO.setEphCatId(ephCatid);
		sku = resultSet.getInt(BBBCoreConstants.SKU);
		registryItemVO.setSku(sku);
		
		isAboveTheLine = resultSet.getInt(BBBGiftRegistryConstants.IS_ABOVE_THE_LINE);
		registryItemVO.setAboveLine(isAboveTheLine!=0?true:false);
		
		if(registryItemVO.isAboveLine()){
			registryItemVO.setIsBelowLineItem(BBBCoreConstants.FALSE);
		}else{
			registryItemVO.setIsBelowLineItem(BBBCoreConstants.TRUE);
		}
		qtyRequested = resultSet.getInt(BBBGiftRegistryConstants.QTY_REQUESTED);
		registryItemVO.setQtyRequested(qtyRequested.intValue());
		
		qtyFulfilled = resultSet.getInt(BBBGiftRegistryConstants.QTY_FULFILLED);
		registryItemVO.setQtyFulfilled(qtyFulfilled.intValue());
		
		qtyWebPurchased=resultSet.getInt(BBBGiftRegistryConstants.QTY_PURCH_RESRV);
		qtyPurchased = qtyFulfilled + qtyWebPurchased;
		
		registryItemVO.setQtyPurchased(qtyPurchased);
		registryItemVO.setQtyWebPurchased(qtyWebPurchased);
		registryItemVO.setQtyPurchased(qtyPurchased);
		
		/*lastMaintained = resultSet.getLong(BBBCoreConstants.LAST_MAINTAINED);
		createDate = resultSet.getLong(BBBCoreConstants.CREATE_TIMESTAMP);
		*/
		if (resultSet.getString(BBBGiftRegistryConstants.ROW_ID) != null) {
			rowId = (String) resultSet.getString(BBBGiftRegistryConstants.ROW_ID);
		}
		registryItemVO.setRowID(rowId);
		
				
		if (resultSet.getString(BBBGiftRegistryConstants.ITEM_TYPE) != null) {
			itemType=(String) resultSet.getString(BBBGiftRegistryConstants.ITEM_TYPE);
		}
		registryItemVO.setItemType(itemType);
		

		SimpleDateFormat sd = new SimpleDateFormat(
				BBBCoreConstants.RKG_DATE_PATTERN);
	/*	Date lastMaintainedDate = sd.parse(lastMaintained.toString());
		Timestamp lastMaintainedTimeStamp = new Timestamp(
				lastMaintainedDate.getTime());
		registryItemVO.setLastMaintained(lastMaintainedTimeStamp);
		Date createdDate = sd.parse(createDate.toString());
		Timestamp createTimestamp = new Timestamp(createdDate.getTime());
		registryItemVO.setCreateTimestamp(createTimestamp);*/
		this.logDebug("GiftRegistryTools.populateRegistryItem() method ends");
		return registryItemVO;
	}
	
	public SKUDetailVO getSKUDetailsWithProductId(final String siteId,
			final String skuId, RegistryItemVO registryItemVO,boolean isInventoryCheckEnabled)
			throws BBBSystemException, BBBBusinessException {
		boolean productWebOffered = false;
		boolean skuWebOffered = false;
		SKUDetailVO skuVO = getCatalogTools().getSKUDetails(
				extractSiteId(), skuId.toString());
		RepositoryItem productRepoItem = getCatalogTools()
				.getParentProductItemForSku(skuId);
		
		//For checking items Inventory only to display sort order by category in guest view.
		if (getCatalogTools().isSKUBelowLine(siteId,skuId)) {
			skuVO.setSkuInStock(false);
		} else {
			skuVO.setSkuInStock(true);
		}
		
		// For Story BPSI-5384 - Update 3 flags (i.e. Disabled flag, Out of
		// stock flag, International shipping restriction flag.
		skuVO.setDisableFlag(((Boolean) skuVO.getSkuRepositoryItem()
				.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME)));
		skuVO.setActiveFlag(mCatalogTools.isSkuActive(skuVO.getSkuRepositoryItem()));
		/*if (!skuVO.isDisableFlag() && isInventoryCheckEnabled) {
			try {
				int inStockStatus = getInventoryManager()
						.getProductAvailability(siteId, skuId,
								BBBInventoryManager.PRODUCT_DISPLAY, 0);
				if (inStockStatus == BBBInventoryManager.AVAILABLE
						|| inStockStatus == BBBInventoryManager.LIMITED_STOCK) {
					skuVO.setSkuInStock(true);
				}
			} catch (BBBBusinessException e) {
				logDebug("Product not available", e);
			}
		}*/
		if(!isInventoryCheckEnabled && registryItemVO.isAboveLine()){
			skuVO.setSkuInStock(true);
		}
		if (productRepoItem != null) {
			String productId = (String) productRepoItem
					.getPropertyValue(BBBCatalogConstants.ID);
			skuVO.setParentProdId(productId);
			// Set Seo url of parent product if sku is active and weboffered
			if (skuVO.getSkuRepositoryItem().getPropertyValue(
					BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) != null) {
				skuWebOffered = ((Boolean) skuVO.getSkuRepositoryItem()
						.getPropertyValue(
								BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME))
						.booleanValue();
			}
			if (productRepoItem
					.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) != null) {
				productWebOffered = ((Boolean) productRepoItem
						.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME))
						.booleanValue();
			}
			if (skuWebOffered && productWebOffered) {
				String seoUrl = (String) productRepoItem
						.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME);
				if (null != seoUrl && registryItemVO != null) {
					registryItemVO.setProductURL(seoUrl
							+ BBBInternationalShippingConstants.SKU_ID_IN_URL
							+ skuId);
				}
			}
		}
		//changes to set url of sku which is added to registry
		if (BBBCoreConstants.MOBILEWEB.equalsIgnoreCase(BBBUtility.getChannel()) || 
				(BBBCoreConstants.MOBILEAPP.equalsIgnoreCase(BBBUtility.getChannel()))) {
			skuVO.setDisplayURL(skuVO.getDisplayName().replaceAll("[^A-Za-z0-9]+", "-"));
			
		}
		return skuVO;
	}

	public void setPriceInRegItem(RegistryItemVO regItem,String productId,boolean isGiftGiver) {

		this.logDebug("GiftRegistryTools.setPriceInRegItem() method ends");
		final String skuId = String.valueOf(regItem.getSku());
		try {
			final double salePrice = this.getCatalogTools().getSalePrice(
					productId, skuId);
			if (salePrice > 0) {
				regItem.setPrice(String.valueOf(salePrice));
			} else {
				final double listPrice = this.getCatalogTools().getListPrice(
						productId, skuId);
				regItem.setPrice(String.valueOf(listPrice));
			}
			
			
			// Setting In Cart price for the SKUs 
			if(!isGiftGiver){
			final double inCartPrice=this.getCatalogTools().getIncartPrice(productId, skuId);
			regItem.setInCartPrice(String.valueOf(inCartPrice));
			}
			
		} catch (final BBBSystemException e) {
			this.logDebug("setPriceInRegItem :: System Exception while fetching parent product Id for sku id: "
					+ skuId + "Exception: " + e);
		}
		this.logDebug("GiftRegistryTools.setPriceInRegItem() method ends");
	}
	
	public MutableRepository getSiteRepository() {
		return siteRepository;
}

	public void setSiteRepository(MutableRepository siteRepository) {
		this.siteRepository = siteRepository;
	}

	public void personlizeImageUrl(RegistryItemVO registryItemVO,String moderateKeyValue) {
	 if(null ==registryItemVO){
		 return;
}
				String personalizeImgUrl = registryItemVO
						.getPersonalizedImageUrls();
				String personalizeImgThumbUrl = registryItemVO
						.getPersonalizedImageUrlThumbs();

				if (!(BBBUtility.isEmpty(registryItemVO.getRefNum()))) {

        		   if(!(BBBUtility.isEmpty(personalizeImgUrl)) && !personalizeImgUrl.contains(moderateKeyValue)){
						StringBuffer moderatedUrl = new StringBuffer();
						if (personalizeImgUrl
								.contains(BBBCoreConstants.DOT_EXTENTION)) {
							int indexOfDotExtension = personalizeImgUrl
									.lastIndexOf(BBBCoreConstants.DOT_EXTENTION);
							String imageName = personalizeImgUrl.substring(0,
									indexOfDotExtension);
							String extension = personalizeImgUrl
									.substring(indexOfDotExtension);
							moderatedUrl = moderatedUrl.append(imageName)
									.append(moderateKeyValue).append(extension);
						} else {
							moderatedUrl.append(personalizeImgUrl).append(
									moderateKeyValue);
						}
						registryItemVO.setPersonalizedImageUrls(moderatedUrl
								.toString());
						this.logDebug("prsonalizedImgURL : "
								+ moderatedUrl.toString());
					}
        		   if(!(BBBUtility.isEmpty(personalizeImgThumbUrl)) && !personalizeImgThumbUrl.contains(moderateKeyValue)){
						StringBuffer moderatedUrlThumb = new StringBuffer();
						if (personalizeImgThumbUrl
								.contains(BBBCoreConstants.DOT_EXTENTION)) {
							int indexOfDotExtension = personalizeImgThumbUrl
									.lastIndexOf(BBBCoreConstants.DOT_EXTENTION);
							String imageName = personalizeImgThumbUrl
									.substring(0, indexOfDotExtension);
							String extension = personalizeImgThumbUrl
									.substring(indexOfDotExtension);
							moderatedUrlThumb = moderatedUrlThumb
									.append(imageName).append(moderateKeyValue)
									.append(extension);
						} else {
							moderatedUrlThumb.append(personalizeImgThumbUrl)
									.append(moderateKeyValue);
						}
						registryItemVO
								.setPersonalizedImageUrlThumbs(moderatedUrlThumb
										.toString());
						this.logDebug("personalizeImgThumbUrl : "
								+ moderatedUrlThumb.toString());
					}
				}
	}

	public boolean isStagingServer() {
		return stagingServer;
	}

	public void setStagingServer(boolean stagingServer) {
		this.stagingServer = stagingServer;
	}

	/**
	 * Fetch checklist reg items from ecom admin.
	 *
	 * @param registryId the registry id
	 * @return the registry items list vo
	 * @throws BBBSystemException the BBB system exception
	 * @throws BBBBusinessException the BBB business exception
	 */
	public RegistryItemsListVO fetchChecklistRegItemsFromEcomAdmin(
			String registryId) throws BBBSystemException, BBBBusinessException {

		this.logDebug("fetchChecklistRegItemsFromEcomAdmin");
		BBBPerformanceMonitor.start("GiftRegistryTools.fetchChecklistRegItemsFromEcomAdmin()");
		RepositoryItem[] registryItems = null;
		RepositoryItem[] registryItemsPersonalizedAndLtl = null;
		RqlStatement statement = null;
		RqlStatement statementForPersonalizedAndLtl = null;
		final RegistryItemsListVO regItemsListVO = new RegistryItemsListVO();
		final List<RegistryItemVO> registryItemsList = new ArrayList<RegistryItemVO>();
		String actionCD = "D";

		if (BBBUtility.isValidNumber(registryId)) {
			try {
				statement = RqlStatement
						.parseRqlStatement("registryNum=?0 AND actionCD!=?1");
				statementForPersonalizedAndLtl = RqlStatement
						.parseRqlStatement("registryNum=?0 AND actionCD!=?1");
				final RepositoryView view = this.getRegistryInfoRepository()
						.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL);
				final RepositoryView viewPersonalizedAndLtl = this
						.getRegistryInfoRepository().getView(
								BBBCoreConstants.ITEM_DESC_REG_DETAIL2);
				Object[] params = new Object[2];
				params[0] = registryId;
				params[1] = actionCD;
				registryItems = extractDBCall(params, statement, view);
				registryItemsPersonalizedAndLtl = extractDBCall(params, statementForPersonalizedAndLtl, viewPersonalizedAndLtl);
				if (registryItems != null) {
					for (RepositoryItem registryItem : registryItems) {
						RegistryItemVO registryVO = new RegistryItemVO();
						Integer skuId = 0;
						Integer qtyRequested = 0;
						if (registryItem.getPropertyValue(BBBCoreConstants.SKUID) != null) {
							skuId = (Integer) registryItem
									.getPropertyValue(BBBCoreConstants.SKUID);
						}
						if (registryItem.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) != null) {
							qtyRequested = (Integer) registryItem
									.getPropertyValue(BBBCoreConstants.QTY_REQUESTED);
						}
						registryVO.setSku(skuId);
						registryVO.setQtyRequested(qtyRequested);
						registryItemsList.add(registryVO);
					}
				}
				if (registryItemsPersonalizedAndLtl != null) {
					for (RepositoryItem registryItem : registryItemsPersonalizedAndLtl) {
						RegistryItemVO registryVO = new RegistryItemVO();
						Integer skuId = 0;
						Integer qtyRequested = 0;
						if (registryItem.getPropertyValue(BBBCoreConstants.SKUID) != null) {
							skuId = (Integer) registryItem
									.getPropertyValue(BBBCoreConstants.SKUID);
						}
						if (registryItem.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) != null) {
							qtyRequested = (Integer) registryItem
									.getPropertyValue(BBBCoreConstants.QTY_REQUESTED);
						}
						registryVO.setSku(skuId);
						registryVO.setQtyRequested(qtyRequested);
						registryItemsList.add(registryVO);
					}
				}
				regItemsListVO.setRegistryItemList(registryItemsList);

			} catch (RepositoryException e) {
				this.logError("GiftRegistryTools.fetchChecklistRegItemsFromEcomAdmin() :: Repository exception while fetching reg items "
						+ e);
				throw new BBBBusinessException(
						"GiftRegistryTools.fetchChecklistRegItemsFromEcomAdmin() :: Repository exception while fetching reg items "
								+ e);
			}finally{
				BBBPerformanceMonitor.end("GiftRegistryTools.fetchChecklistRegItemsFromEcomAdmin()");
				this.logDebug("GiftRegistryTools.fetchChecklistRegItemsFromEcomAdmin() method ends");
			}
		}
		return regItemsListVO;
	
	}

}

