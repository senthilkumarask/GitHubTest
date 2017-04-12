package com.bbb.commerce.giftregistry.tool

import com.bbb.account.vo.ProfileSyncRequestVO;
import com.bbb.account.vo.ProfileSyncResponseVO
import com.bbb.commerce.catalog.BBBCatalogConstants;
import com.bbb.commerce.catalog.BBBCatalogErrorCodes;
import com.bbb.commerce.catalog.BBBCatalogTools
import com.bbb.commerce.catalog.vo.RegistryTypeVO
import com.bbb.commerce.catalog.vo.SKUDetailVO;
import com.bbb.commerce.catalog.vo.StoreVO
import com.bbb.commerce.exim.bean.EximSessionBean;
import com.bbb.commerce.giftregistry.bean.AddItemsBean
import com.bbb.commerce.giftregistry.bean.GiftRegistryViewBean;
import com.bbb.commerce.giftregistry.utility.BBBGiftRegistryUtils
import com.bbb.commerce.giftregistry.vo.AddressVO;
import com.bbb.commerce.giftregistry.vo.EventVO
import com.bbb.commerce.giftregistry.vo.ForgetRegPassRequestVO;
import com.bbb.commerce.giftregistry.vo.ManageRegItemsResVO
import com.bbb.commerce.giftregistry.vo.RegCopyResVO;
import com.bbb.commerce.giftregistry.vo.RegNamesVO;
import com.bbb.commerce.giftregistry.vo.RegSearchResVO
import com.bbb.commerce.giftregistry.vo.RegStatusesResVO
import com.bbb.commerce.giftregistry.vo.RegistrantVO
import com.bbb.commerce.giftregistry.vo.RegistryBabyVO;
import com.bbb.commerce.giftregistry.vo.RegistryHeaderVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemVO;
import com.bbb.commerce.giftregistry.vo.RegistryItemsListVO
import com.bbb.commerce.giftregistry.vo.RegistryPrefStoreVO;
import com.bbb.commerce.giftregistry.vo.RegistryReqVO;
import com.bbb.commerce.giftregistry.vo.RegistryResVO
import com.bbb.commerce.giftregistry.vo.RegistrySearchVO
import com.bbb.commerce.giftregistry.vo.RegistrySkinnyVO;
import com.bbb.commerce.giftregistry.vo.RegistryStatusVO
import com.bbb.commerce.giftregistry.vo.RegistrySummaryVO
import com.bbb.commerce.giftregistry.vo.RegistryTypes
import com.bbb.commerce.giftregistry.vo.RegistryVO;
import com.bbb.commerce.giftregistry.vo.ServiceErrorVO
import com.bbb.commerce.giftregistry.vo.SetAnnouncementCardResVO
import com.bbb.commerce.giftregistry.vo.ValidateAddItemsResVO
import com.bbb.commerce.inventory.BBBInventoryManager;
import com.bbb.constants.BBBCoreConstants
import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.constants.BBBGiftRegistryConstants
import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.constants.BBBWebServiceConstants;
import com.bbb.email.BBBTemplateEmailSender
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.framework.integration.handlers.ServiceHandlerIF
import com.bbb.framework.integration.util.ServiceHandlerUtil
import com.bbb.framework.webservices.BBBAPIConstants;
import com.bbb.framework.webservices.handlers.ServiceHandler
import com.bbb.framework.webservices.vo.ErrorStatus
import com.bbb.logging.LogMessageFormatter;
import com.bbb.profile.session.BBBSessionBean
import com.bbb.utils.BBBUtility
import com.bea.common.security.ProvidersLogger;

import java.sql.CallableStatement;
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.Date
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.sql.DataSource
import javax.transaction.HeuristicMixedException
import javax.transaction.HeuristicRollbackException
import javax.transaction.NotSupportedException
import javax.transaction.RollbackException
import javax.transaction.SystemException
import javax.transaction.Transaction
import javax.transaction.TransactionManager

import org.apache.commons.beanutils.DynaBean
import org.apache.commons.beanutils.DynaClass
import org.apache.commons.beanutils.DynaProperty;

import atg.adapter.gsa.GSARepository
import atg.commerce.pricing.priceLists.PriceListManager
import atg.multisite.Site
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus
import atg.nucleus.registry.NucleusRegistry
import atg.repository.MutableRepository
import atg.repository.MutableRepositoryItem
import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView;
import atg.userprofiling.Profile
import net.sf.json.JSONObject;
import oracle.jdbc.OracleTypes;
import spock.lang.specification.BBBExtendedSpec;

class GiftRegistryToolsSpecification extends BBBExtendedSpec {

	GiftRegistryTools tools
	ServiceHandlerIF IF =Mock()
	GSARepository gsRep =Mock()
	MutableRepository mRep =Mock()
	CallableStatement cs =Mock()
	DataSource DS =Mock()
	Connection con =Mock()
	BBBGiftRegistryUtils utils =Mock()
	ResultSet rs =Mock()
	RegistrySummaryVO vo1 = Mock()
	RegistrySummaryVO vo2 = Mock()
	Profile pro =Mock()
	RegistryVO registryVO =Mock()
	MutableRepositoryItem pOwnerProfileItem =Mock()
	MutableRepositoryItem pCoRegProfileItem =Mock()
	RepositoryView view = Mock()
	RepositoryView view1 = Mock()
	MutableRepositoryItem rItem =Mock()
	EventVO eventVo =Mock()
	RegistryTypes rType =Mock()
	BBBCatalogTools catalogTools =Mock()
	GSARepository regInfoRepo =Mock()
	TransactionManager manager =Mock()
	BBBInventoryManager inventoryManager =Mock()
	MutableRepository pPrintAtHomeRepository =Mock()

	def setup(){
		tools = new GiftRegistryTools()
		tools.setRegistryInfoRepository(gsRep)
		tools.setGiftRegUtils(utils)
		tools.setGiftRepository(mRep)
		tools.setCatalogTools(catalogTools)
		tools.setTransactionManager(manager)
		tools.setInventoryManager(inventoryManager)
		tools.setPrintAtHomeRepository(pPrintAtHomeRepository)
	}

	private setParametrsForSpy(){
		tools =Spy()
		tools.setRegistryInfoRepository(gsRep)
		tools.setGiftRegUtils(utils)
		tools.setGiftRepository(mRep)
		tools.setCatalogTools(catalogTools)
		tools.setTransactionManager(manager)
		tools.setInventoryManager(inventoryManager)
		tools.setPrintAtHomeRepository(pPrintAtHomeRepository)
	}

	private setCommonParameters(ResultSet rs){
		rs.next() >> true >> false
		0*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT)
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> null
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		1*rs.getString("MAIDEN") >> "7"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "!CO"
	}

	private setVOParams(RegistrySummaryVO vo1,RegistrySummaryVO vo2){
		vo1.getPrimaryRegistrantFirstName() >> "a"
		vo2.getPrimaryRegistrantFirstName() >> "b"
		vo1.getPrimaryRegistrantLastName() >> "a"
		vo2.getPrimaryRegistrantLastName() >> "b"
		vo1.getPrimaryRegistrantMaidenName() >> "a"
		vo2.getPrimaryRegistrantMaidenName() >> "b"
		vo1.getState() >> "a"
		vo2.getState() >> "b"
	}

	def"assignAnnouncementCardCount, when registry id is not empty "(){

		given:
		RegistryVO registryVO = Mock()
		SetAnnouncementCardResVO resVo =Mock()
		2*registryVO.getRegistryId() >> "1"
		ServiceHandlerUtil.setServicehandler(IF)
		1*IF.invoke(null, registryVO) >> resVo

		when:
		SetAnnouncementCardResVO vo =tools.assignAnnouncementCardCount(registryVO)

		then:
		vo==resVo
		1*registryVO.setRegistryIdWS(Long.parseLong("1"))
	}

	def"assignAnnouncementCardCount, when registry id is empty "(){

		given:
		RegistryVO registryVO = Mock()
		SetAnnouncementCardResVO resVo =Mock()
		ServiceHandlerUtil.setServicehandler(IF)
		1*IF.invoke(null, registryVO) >> resVo

		when:
		SetAnnouncementCardResVO vo =tools.assignAnnouncementCardCount(registryVO)

		then:
		vo==resVo
		1*registryVO.setRegistryIdWS(0)
	}

	def"createRegistry, creates a registry"(){

		given:
		RegistryVO registryVO = Mock()
		RegistryResVO resVo =Mock()
		ServiceHandlerUtil.setServicehandler(IF)
		1*IF.invoke(null, registryVO) >> resVo

		when:
		RegistryResVO vo =tools.createRegistry(registryVO)

		then:
		vo==resVo
	}

	def"searchRegistries, creates a registry"(){

		given:
		RegistrySearchVO registrySearchVO =Mock()
		RegSearchResVO resVo =Mock()
		String siteId ="tbs"
		ServiceHandlerUtil.setServicehandler(IF)
		1*IF.invoke(null, registrySearchVO) >> resVo

		when:
		RegSearchResVO vo =tools.searchRegistries(registrySearchVO,siteId)

		then:
		vo==resVo
	}

	def"searchRegistriesByProfileId, populates the RegistrySummaryVO "(){

		given:
		setParametrsForSpy()
		Profile pro =Mock()

		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		con.prepareCall(_) >> cs
		registrySearchVO.getSiteId() >> "tbs"
		registrySearchVO.getProfileId() >> pro
		pro.getRepositoryId() >> "repId"

		1* tools.extractDBCall(cs)
		cs.getObject(3) >> rs
		rs.next() >> true >> false

		rs.getString("REGISTRY_NUM") >> "Id"
		rs.getString("NM_ADDR_SUB_TYPE") >> "2"
		rs.getString("EVENT_TYPE") >> "3"
		rs.getString("IS_PUBLIC") >> "true"
		rs.getString("EVENT_DT") >> "2016/12/15"
		rs.getString("ATG_PROFILE_ID") >> "4"
		rs.getString("FIRST_NM_COPY") >> "5"
		rs.getString("LAST_NM_COPY") >> "6"
		rs.getString("MAIDEN_COPY") >> "7"
		rs.getString("ADDR1") >> "8"
		rs.getString("ADDR2") >> "9"
		rs.getString("COMPANY") >> "10"
		rs.getString("CITY") >> "city"
		rs.getString("STATE_CD") >> "11"
		rs.getString("ZIP_CD") >> "12"
		rs.getString("REGISTRANT_NAME") >> "13"
		rs.getString("COREGISTRANT_NAME") >> "14"
		rs.getString("EMAIL_ADDR") >> "15"
		rs.getInt("GIFTSREGISTERED") >> 16
		rs.getInt("GIFTSPURCHASED") >> 17

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		List<RegistrySummaryVO> listsumVo = vo.getListRegistrySummaryVO()
		RegistrySummaryVO regSummaryVO = listsumVo.get(0)
		regSummaryVO.getRegistryId() == "Id"
		regSummaryVO.getAddrSubType() == "2"
		regSummaryVO.getEventType() == "3"
		regSummaryVO.getIsPublic() == "true"
		regSummaryVO.getEventDate() == "2016/12/15"
		regSummaryVO.getOwnerProfileID() == "4"
		regSummaryVO.getPrimaryRegistrantFirstName() == "5"
		regSummaryVO.getPrimaryRegistrantLastName() == "6"
		regSummaryVO.getPrimaryRegistrantMaidenName() == "7"
		regSummaryVO.getPrimaryRegistrantFullName() == "13"
		regSummaryVO.getCoRegistrantFullName() =="14"
		regSummaryVO.getPrimaryRegistrantEmail()== "15"
		regSummaryVO.getGiftRegistered() == 16
		regSummaryVO.getGiftPurchased() == 17

		AddressVO shippingAddress=regSummaryVO.getShippingAddress()
		shippingAddress.getAddressLine1() == "8"
		shippingAddress.getAddressLine2() == "9"
		shippingAddress.getCompany() =="10"
		shippingAddress.getCity() =="city"
		shippingAddress.getState()=="11"
		shippingAddress.getZip() =="12"
	}

	def"searchRegistriesByProfileId, populates the RegistrySummaryVO when when eventDate is null"(){

		given:
		setParametrsForSpy()
		Profile pro =Mock()

		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		con.prepareCall(_) >> cs
		registrySearchVO.getSiteId() >> "tbs"
		registrySearchVO.getProfileId() >> pro
		pro.getRepositoryId() >> "repId"

		1* tools.extractDBCall(cs)
		cs.getObject(3) >> rs
		rs.next() >> true >> false

		rs.getString("REGISTRY_NUM") >> "Id"
		rs.getString("NM_ADDR_SUB_TYPE") >> "2"
		rs.getString("EVENT_TYPE") >> "3"
		rs.getString("IS_PUBLIC") >> "true"
		rs.getString("EVENT_DT") >> null
		rs.getString("ATG_PROFILE_ID") >> "4"
		rs.getString("FIRST_NM_COPY") >> "5"
		rs.getString("LAST_NM_COPY") >> "6"
		rs.getString("MAIDEN_COPY") >> "7"
		rs.getString("ADDR1") >> "8"
		rs.getString("ADDR2") >> "9"
		rs.getString("COMPANY") >> "10"
		rs.getString("CITY") >> "city"
		rs.getString("STATE_CD") >> "11"
		rs.getString("ZIP_CD") >> "12"
		rs.getString("REGISTRANT_NAME") >> "13"
		rs.getString("COREGISTRANT_NAME") >> "14"
		rs.getString("EMAIL_ADDR") >> "15"
		rs.getInt("GIFTSREGISTERED") >> 16
		rs.getInt("GIFTSPURCHASED") >> 17

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		List<RegistrySummaryVO> listsumVo = vo.getListRegistrySummaryVO()
		RegistrySummaryVO regSummaryVO = listsumVo.get(0)
		regSummaryVO.getRegistryId() == "Id"
		regSummaryVO.getAddrSubType() == "2"
		regSummaryVO.getEventType() == "3"
		regSummaryVO.getIsPublic() == "true"
		regSummaryVO.getEventDate() == ""
		regSummaryVO.getOwnerProfileID() == "4"
		regSummaryVO.getPrimaryRegistrantFirstName() == "5"
		regSummaryVO.getPrimaryRegistrantLastName() == "6"
		regSummaryVO.getPrimaryRegistrantMaidenName() == "7"
		regSummaryVO.getPrimaryRegistrantFullName() == "13"
		regSummaryVO.getCoRegistrantFullName() =="14"
		regSummaryVO.getPrimaryRegistrantEmail()== "15"
		regSummaryVO.getGiftRegistered() == 16
		regSummaryVO.getGiftPurchased() == 17

		AddressVO shippingAddress=regSummaryVO.getShippingAddress()
		shippingAddress.getAddressLine1() == "8"
		shippingAddress.getAddressLine2() == "9"
		shippingAddress.getCompany() =="10"
		shippingAddress.getCity() =="city"
		shippingAddress.getState()=="11"
		shippingAddress.getZip() =="12"
	}

	def"searchRegistriesByProfileId, populates the RegistrySummaryVO when when length of eventDate is less than 7"(){

		given:
		setParametrsForSpy()
		Profile pro =Mock()

		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		con.prepareCall(_) >> cs
		registrySearchVO.getSiteId() >> "tbs"
		registrySearchVO.getProfileId() >> pro
		pro.getRepositoryId() >> "repId"

		1* tools.extractDBCall(cs)
		cs.getObject(3) >> rs
		rs.next() >> true >> false

		rs.getString("REGISTRY_NUM") >> "Id"
		rs.getString("NM_ADDR_SUB_TYPE") >> "2"
		rs.getString("EVENT_TYPE") >> "3"
		rs.getString("IS_PUBLIC") >> "true"
		rs.getString("EVENT_DT") >> "27/12"
		rs.getString("ATG_PROFILE_ID") >> "4"
		rs.getString("FIRST_NM_COPY") >> "5"
		rs.getString("LAST_NM_COPY") >> "6"
		rs.getString("MAIDEN_COPY") >> "7"
		rs.getString("ADDR1") >> "8"
		rs.getString("ADDR2") >> "9"
		rs.getString("COMPANY") >> "10"
		rs.getString("CITY") >> "city"
		rs.getString("STATE_CD") >> "11"
		rs.getString("ZIP_CD") >> "12"
		rs.getString("REGISTRANT_NAME") >> "13"
		rs.getString("COREGISTRANT_NAME") >> "14"
		rs.getString("EMAIL_ADDR") >> "15"
		rs.getInt("GIFTSREGISTERED") >> 16
		rs.getInt("GIFTSPURCHASED") >> 17

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		List<RegistrySummaryVO> listsumVo = vo.getListRegistrySummaryVO()
		RegistrySummaryVO regSummaryVO = listsumVo.get(0)
		regSummaryVO.getRegistryId() == "Id"
		regSummaryVO.getAddrSubType() == "2"
		regSummaryVO.getEventType() == "3"
		regSummaryVO.getIsPublic() == "true"
		regSummaryVO.getEventDate() == ""
		regSummaryVO.getOwnerProfileID() == "4"
		regSummaryVO.getPrimaryRegistrantFirstName() == "5"
		regSummaryVO.getPrimaryRegistrantLastName() == "6"
		regSummaryVO.getPrimaryRegistrantMaidenName() == "7"
		regSummaryVO.getPrimaryRegistrantFullName() == "13"
		regSummaryVO.getCoRegistrantFullName() =="14"
		regSummaryVO.getPrimaryRegistrantEmail()== "15"
		regSummaryVO.getGiftRegistered() == 16
		regSummaryVO.getGiftPurchased() == 17

		AddressVO shippingAddress=regSummaryVO.getShippingAddress()
		shippingAddress.getAddressLine1() == "8"
		shippingAddress.getAddressLine2() == "9"
		shippingAddress.getCompany() =="10"
		shippingAddress.getCity() =="city"
		shippingAddress.getState()=="11"
		shippingAddress.getZip() =="12"
	}

	def"searchRegistriesByProfileId, when connection is null"(){

		given:
		setParametrsForSpy()
		CallableStatement cs =Mock()
		Profile pro =Mock()
		ResultSet rs =Mock()

		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> null

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		vo.getListRegistrySummaryVO() == null
	}

	def"searchRegistriesByProfileId, when prepareCall throws SQLException"(){

		given:
		setParametrsForSpy()
		ServiceErrorVO vo1 =Mock()
		Profile pro =Mock()
		RegistrySearchVO registrySearchVO =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		registrySearchVO.getSiteId() >> "tbs"
		registrySearchVO.getProfileId() >> pro
		pro.getRepositoryId() >> "repId"
		con.prepareCall(_) >> {throw new SQLException(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)}

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		vo.getListRegistrySummaryVO() == null
		1*tools.logInfo("GiftRegistryTools.searchRegistriesByProfileId() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)
		1*utils.logAndFormatError(null, con,BBBGiftRegistryConstants.SERVICE_ERROR_VO, _,"tbs","repId") >> vo1
		vo.getServiceErrorVO() == vo1
	}

	def"searchRegistriesByProfileId, when resultSet is empty"(){

		given:
		setParametrsForSpy()
		ServiceErrorVO vo1 =Mock()
		Profile pro =Mock()
		RegistrySearchVO registrySearchVO =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		registrySearchVO.getSiteId() >> "tbs"
		registrySearchVO.getProfileId() >> pro
		pro.getRepositoryId() >> "repId"
		con.prepareCall(_) >> cs
		1* tools.extractDBCall(cs)
		cs.getObject(3) >> rs
		rs.next() >> false
		1*rs.close() >>{throw new SQLException(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)}

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		vo == null
		BBBSystemException e = thrown()
		e.getMessage() == "An exception occurred while executing the sql statement."
	}

	def"searchRegistriesByProfileId, when prepareCall throws SQLException with error message null"(){

		given:
		setParametrsForSpy()
		ServiceErrorVO vo1 =Mock()
		Profile pro =Mock()
		RegistrySearchVO registrySearchVO =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		registrySearchVO.getSiteId() >> "tbs"
		registrySearchVO.getProfileId() >> pro
		pro.getRepositoryId() >> "repId"
		con.prepareCall(_) >> {throw new SQLException()}

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		vo.getListRegistrySummaryVO() == null
		1*tools.logError("SQL exception while registry header info "+ "in GiftRegistryTools", _)
		1*utils.logAndFormatError(null, con,BBBGiftRegistryConstants.SERVICE_ERROR_VO, _,"tbs","repId") >> vo1
		vo.getServiceErrorVO() == vo1
	}

	def"searchRegistriesByProfileId, when prepareCall throws SQLException with error message not equal to REGISTRY_NOT_FOUND "(){

		given:
		setParametrsForSpy()
		ServiceErrorVO vo1 =Mock()
		Profile pro =Mock()
		RegistrySearchVO registrySearchVO =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		registrySearchVO.getSiteId() >> "tbs"
		registrySearchVO.getProfileId() >> pro
		pro.getRepositoryId() >> "repId"
		con.prepareCall(_) >> {throw new SQLException("error")}

		when:
		RegSearchResVO vo = tools.searchRegistriesByProfileId(registrySearchVO)

		then:
		vo.getListRegistrySummaryVO() == null
		1*tools.logError("SQL exception while registry header info "+ "in GiftRegistryTools", _)
		1*utils.logAndFormatError(null, con,BBBGiftRegistryConstants.SERVICE_ERROR_VO, _,"tbs","repId") >> vo1
		vo.getServiceErrorVO() == vo1
	}

	def"getRegistryListByRegNum , when SORT EQUALS SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> true
		registrySearchVO.isReturnLeagacyRegistries() >> true
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> true >> false
		1*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> "20161215"
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		2*rs.getString("MAIDEN") >> "7"
		2*rs.getString("PWS_URL") >> "8"
		rs.getString("ROW_STATUS") >> "A"
		1*rs.getString("SITE_PUBLISHED_CD") >> "Y"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "RE"
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for seperateRegistriesWithEmptyDate
		vo1.getEventDateObject() >> null
		vo2.getEventDateObject() >> new Date()
		//end seperateRegistriesWithEmptyDate

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE
		2*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		1*utils.encryptRegNumForPersonalWebsite(50)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum , when SITE_PUBLISHED_CD is not Y and length of eventDate is greater than 8(convertResultSetToListForGiftGiver),SORT is not equal to SORT_SEQ_KEY_DATE1"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> true
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> true >> false
		0*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT)
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> "2016/12/15"
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		2*rs.getString("MAIDEN") >> "7"
		1*rs.getString("PWS_URL") >> "8"
		rs.getString("ROW_STATUS") >> "A"
		1*rs.getString("SITE_PUBLISHED_CD") >> "!Y"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "CO"
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >>BBBGiftRegistryConstants.DEFAULT_SORT_SEQ_KEY
		2*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum , when ROW_STATUS is not A, eventDate is null(convertResultSetToListForGiftGiver),SORT not equals to SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> true
		registrySearchVO.isReturnLeagacyRegistries() >> true
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> true >> false
		0*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT)
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> null
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		1*rs.getString("MAIDEN") >> "7"
		1*rs.getString("PWS_URL") >> "8"
		rs.getString("ROW_STATUS") >> "B"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "!CO"
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >>BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		2*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]
		1*con.close() >> {throw new SQLException("")}

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*tools.logError("Error occurred while closing connection", _)
	}

	def"getRegistryListByRegNum , isGiftGiver is false, eventDate is null(convertResultSetToListForGiftGiver),SORT not equals to SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		vo1.getRegistryId() >> "a"
		vo2.getRegistryId() >> "b"
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >>BBBGiftRegistryConstants.SORT_SEQ_KEY_REGNUM
		2*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,eventDate is null(convertResultSetToListForGiftGiver),SORT equals to SORT_SEQ_KEY_MAIDEN(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]
		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >>BBBGiftRegistryConstants.SORT_SEQ_KEY_MAIDEN
		2*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,eventDate is null(convertResultSetToListForGiftGiver),SORT equals to SORT_EVENT_TYPE(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >>BBBGiftRegistryConstants.SORT_EVENT_TYPE
		2*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,eventDate is null(convertResultSetToListForGiftGiver),SORT equals to defaultOrder(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]
		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >>"defaultOrder"
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,eventDate is null(convertResultSetToListForGiftGiver),SORT equals to null(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> null
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder not equals to SORT_ORDER_DESC,sort equals DEFAULT_SORT_SEQ_KEY(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> "default"
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.DEFAULT_SORT_SEQ_KEY
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder not equals to SORT_ORDER_DESC,sort equals SORT_SEQ_KEY_STATE(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> "default"
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder not equals to SORT_ORDER_DESC,sort equals SORT_SEQ_KEY_DATE(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> "default"
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder not equals to SORT_ORDER_DESC,sort equals SORT_SEQ_KEY_REGNUM(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> "default"
		vo1.getPrimaryRegistrantMaidenName() >> "a"
		vo2.getPrimaryRegistrantMaidenName() >> "b"
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_REGNUM
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder not equals to SORT_ORDER_DESC,sort equals SORT_SEQ_KEY_MAIDEN(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> "default"
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_MAIDEN
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder not equals to SORT_ORDER_DESC,sort equals SORT_EVENT_TYPE(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> "default"
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_EVENT_TYPE
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder not equals to SORT_ORDER_DESC,sort equals defaultOrder(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> "default"
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> ""
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum ,getSortSeqOrder equals to null(sortAndExcludeRegistries)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]
		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> null
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> ""
		2*utils.getPagedData([vo1,vo2],0,0) >> [vo1,vo2]

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo1,vo2]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum , when VoList passed is empty (seperateRegistriesWithEmptyDate)"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		setCommonParameters(rs)
		//end convertResultSetToListForGiftGiver
		1*utils.removeDuplicateRows(_) >> []
		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE >> null
		2*utils.getPagedData([],0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == []
		vo.getTotEntries() == 0
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum , when convertResultSetToListForGiftGiverMethod returns an emptyList"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM) >> cs

		registrySearchVO.getRegistryId() >>"2"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		registrySearchVO.getGiftGiver() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> false
		//end convertResultSetToListForGiftGiver
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setInt(1, Integer.parseInt(registrySearchVO.getRegistryId()));
		1*cs.setString(2, (registrySearchVO.getGiftGiver()) ? "Y" : "N");
		1*cs.setString(3, registrySearchVO.getSiteId());
		1*cs.setString(4,registrySearchVO.isReturnLeagacyRegistries() ? "Y": "N");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByRegNum , when connection is null "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> null
		0*con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		0*cs.setFetchSize(100)
		0*cs.setInt(1, _);
		0*cs.setString(2, _);
		0*cs.setString(3, _);
		0*cs.setString(4,_);
		0*cs.registerOutParameter(5, OracleTypes.CURSOR)
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"getRegistryListByRegNum , when Exception is thrown "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> {throw new Exception("")}
		0*con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_REG_NUM)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByRegNum(registrySearchVO)

		then:
		Exception e = thrown()
		0*cs.setFetchSize(100)
		0*cs.setInt(1, _);
		0*cs.setString(2, _);
		0*cs.setString(3, _);
		0*cs.setString(4,_);
		0*cs.registerOutParameter(5, OracleTypes.CURSOR)
		vo ==null
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"getRegistryListByEmail , when SORT EQUALS SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_EMAIL2) >> cs

		1*registrySearchVO.getEvent() >> "event"
		1*registrySearchVO.getSiteId() >> "tbs"
		1*registrySearchVO.getState() >>"state"
		2*registrySearchVO.getEmail() >> "email"
		registrySearchVO.getGiftGiver() >> true
		registrySearchVO.isReturnLeagacyRegistries() >> true
		1*tools.getFilterOptions("event", "state") >> "filterOptions"
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(6) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> true >> false
		1*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> "20161215"
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		2*rs.getString("MAIDEN") >> "7"
		2*rs.getString("PWS_URL") >> "8"
		rs.getString("ROW_STATUS") >> "A"
		1*rs.getString("SITE_PUBLISHED_CD") >> "Y"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "RE"
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for seperateRegistriesWithEmptyDate
		vo1.getEventDateObject() >> null
		vo2.getEventDateObject() >> new Date()
		//end seperateRegistriesWithEmptyDate

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.getRegistryListByEmail(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setString(1, "email");
		1*cs.setString(2, "Y");
		1*cs.setString(3, "filterOptions");
		1*cs.setString(4, "tbs");
		1*cs.setString(5,"Y");
		1*cs.registerOutParameter(6, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		1*utils.encryptRegNumForPersonalWebsite(50)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByEmail , when SORT is not equal to  SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_EMAIL2) >> cs

		1*registrySearchVO.getEvent() >> "event"
		1*registrySearchVO.getSiteId() >> "tbs"
		1*registrySearchVO.getState() >>"state"
		2*registrySearchVO.getEmail() >> "email"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.getFilterOptions("event", "state") >> "filterOptions"
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(6) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> true >> false
		1*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> "20161215"
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		2*rs.getString("MAIDEN") >> "7"
		rs.getString("ROW_STATUS") >> "A"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "RE"
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> null
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> "default"
		1*utils.getPagedData([vo1,vo2],0,0) >> [vo2,vo1]
		1*cs.close() >> {throw new SQLException("")}

		when:
		RegSearchResVO vo = tools.getRegistryListByEmail(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setString(1, "email");
		1*cs.setString(2, "N");
		1*cs.setString(3, "filterOptions");
		1*cs.setString(4, "tbs");
		1*cs.setString(5,"N");
		1*cs.registerOutParameter(6, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(50)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		0*con.close()
		1*tools.logError("Error occurred while closing connection",_)
	}

	def"getRegistryListByEmail , when convertResultSetToListForGiftGiverMethod returns an emptyList"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_EMAIL2) >> cs

		1*registrySearchVO.getEvent() >> "event"
		1*registrySearchVO.getSiteId() >> "tbs"
		1*registrySearchVO.getState() >>"state"
		2*registrySearchVO.getEmail() >> "email"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.getFilterOptions("event", "state") >> "filterOptions"
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(6) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> false
		//end convertResultSetToListForGiftGiver
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByEmail(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setString(1, "email");
		1*cs.setString(2, "N");
		1*cs.setString(3, "filterOptions");
		1*cs.setString(4, "tbs");
		1*cs.setString(5,"N");
		1*cs.registerOutParameter(6, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByEmail , when connection is null "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> null
		0*con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_EMAIL2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByEmail(registrySearchVO)

		then:
		0*cs.setFetchSize(100)
		0*cs.setInt(1, _);
		0*cs.setString(2, _);
		0*cs.setString(3, _);
		0*cs.setString(4,_);
		0*cs.registerOutParameter(5, )
		0*cs.registerOutParameter(6, _)
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"getRegistryListByEmail , when Exception is thrown "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> {throw new Exception("")}
		0*con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_EMAIL2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByEmail(registrySearchVO)

		then:
		Exception e = thrown()
		0*cs.setFetchSize(100)
		0*cs.setInt(1, _);
		0*cs.setString(2, _);
		0*cs.setString(3, _);
		0*cs.setString(4,_);
		0*cs.registerOutParameter(5, OracleTypes.CURSOR)
		vo ==null
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"getRegistryListByName , when SORT EQUALS SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_NAME2) >> cs

		2*registrySearchVO.getFirstName() >> "fName"
		2*registrySearchVO.getLastName() >> "lName"
		1*registrySearchVO.getEvent() >> "event"
		1*registrySearchVO.getState() >>"state"
		1*registrySearchVO.getSiteId() >>"tbs"
		registrySearchVO.getGiftGiver() >> true
		registrySearchVO.isReturnLeagacyRegistries() >> true
		1*tools.getFilterOptions("event", "state") >> "filterOptions"
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(7) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> true >> false
		1*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> "20161215"
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		2*rs.getString("MAIDEN") >> "7"
		2*rs.getString("PWS_URL") >> "8"
		rs.getString("ROW_STATUS") >> "A"
		1*rs.getString("SITE_PUBLISHED_CD") >> "Y"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "RE"
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for seperateRegistriesWithEmptyDate
		vo1.getEventDateObject() >> null
		vo2.getEventDateObject() >> new Date()
		//end seperateRegistriesWithEmptyDate

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.getRegistryListByName(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setString(1, "fName");
		1*cs.setString(2, "lName");
		1*cs.setString(3, "Y");
		1*cs.setString(4, "filterOptions");
		1*cs.setString(5, "tbs");
		1*cs.setString(6,"Y");
		1*cs.registerOutParameter(7, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		1*utils.encryptRegNumForPersonalWebsite(50)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByName , when SORT is not equal to  SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_NAME2) >> cs

		2*registrySearchVO.getFirstName() >> "fName"
		2*registrySearchVO.getLastName() >> "lName"
		1*registrySearchVO.getEvent() >> "event"
		1*registrySearchVO.getState() >>"state"
		1*registrySearchVO.getSiteId() >>"tbs"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.getFilterOptions("event", "state") >> "filterOptions"
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(7) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> true >> false
		1*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		1*rs.getString("EVENT_DESC") >> "desc"
		rs.getString("EVENT_DT") >> "20161215"
		1*rs.getString("REG_NAME") >> "4"
		1*rs.getString("LAST_NM") >> "5"
		1*rs.getString("STATE_CD") >> "6"
		2*rs.getString("MAIDEN") >> "7"
		rs.getString("ROW_STATUS") >> "A"
		1*rs.getString("COREG_NAME") >> "9"
		1*rs.getString("COREG_MAIDEN") >> "10"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "RE"
		//end convertResultSetToListForGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> null
		setVOParams(vo1, vo2)
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> "default"
		1*utils.getPagedData([vo1,vo2],0,0) >> [vo2,vo1]
		1*cs.close() >> {throw new SQLException("")}

		when:
		RegSearchResVO vo = tools.getRegistryListByName(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setString(1, "fName");
		1*cs.setString(2, "lName");
		1*cs.setString(3, "N");
		1*cs.setString(4, "filterOptions");
		1*cs.setString(5, "tbs");
		1*cs.setString(6,"N");
		1*cs.registerOutParameter(7, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(50)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		0*con.close()
		1*tools.logError("Error occurred while closing connection",_)
	}

	def"getRegistryListByName , when convertResultSetToListForGiftGiverMethod returns an emptyList"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_NAME2) >> cs

		2*registrySearchVO.getFirstName() >> "fName"
		2*registrySearchVO.getLastName() >> "lName"
		1*registrySearchVO.getEvent() >> "event"
		1*registrySearchVO.getState() >>"state"
		1*registrySearchVO.getSiteId() >>"tbs"
		registrySearchVO.getGiftGiver() >> false
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.getFilterOptions("event", "state") >> "filterOptions"
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(7) >> rs

		//for convertResultSetToListForGiftGiver
		rs.next() >> false
		//end convertResultSetToListForGiftGiver
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByName(registrySearchVO)

		then:
		1*cs.setFetchSize(100)
		1*cs.setString(1, "fName");
		1*cs.setString(2, "lName");
		1*cs.setString(3, "N");
		1*cs.setString(4, "filterOptions");
		1*cs.setString(5, "tbs");
		1*cs.setString(6,"N");
		1*cs.registerOutParameter(7, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*utils.encryptRegNumForPersonalWebsite(_)
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		1*cs.close()
		1*con.close()
	}

	def"getRegistryListByName , when connection is null "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> null
		0*con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_NAME2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByName(registrySearchVO)

		then:
		0*cs.setFetchSize(100)
		0*cs.setInt(1, _);
		0*cs.setString(2, _);
		0*cs.setString(3, _);
		0*cs.setString(4,_);
		0*cs.registerOutParameter(5, )
		0*cs.registerOutParameter(6, _)
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"getRegistryListByName , when Exception is thrown "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> {throw new Exception("")}
		0*con.prepareCall(BBBGiftRegistryConstants.GET_REG_LIST_BY_NAME2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.getRegistryListByName(registrySearchVO)

		then:
		Exception e = thrown()
		0*cs.setFetchSize(100)
		0*cs.setInt(1, _);
		0*cs.setString(2, _);
		0*cs.setString(3, _);
		0*cs.setString(4,_);
		0*cs.registerOutParameter(5, OracleTypes.CURSOR)
		vo ==null
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"regSearchByRegUsingRegNum , when SORT EQUALS SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_REGNUM) >> cs

		1*registrySearchVO.getRegistryId() >> "1"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> true >> false
		1*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		rs.getString("EVENT_DT") >> "20161215"
		1*rs.getString("REG_FULL_NAME") >> "4"
		1*rs.getString("REG_LAST_NM") >> "5"
		1*rs.getString("REG_MAIDEN") >> "6"
		1*rs.getString("REG_ATG_PROFILE_ID") >> "7"
		1*rs.getString("COREG_FULL_NAME") >> "8"
		rs.getString("COREG_MAIDEN") >> "A"
		1*rs.getString("COREG_ATG_PROFILE_ID") >> "Y"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "9"
		1*rs.getString("REG_EMAIL_ADDR") >> "10"
		1*rs.getString("COREG_EMAIL_ADDR") >> "RE"
		//end convertResultSetToListForNonGiftGiver

		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for seperateRegistriesWithEmptyDate
		vo1.getEventDateObject() >> null
		vo2.getEventDateObject() >> new Date()
		//end seperateRegistriesWithEmptyDate

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingRegNum(registrySearchVO)

		then:
		1*cs.setInt(1, 1);
		1*cs.setString(2, "tbs");
		1*cs.setString(3, "Y");
		1*cs.setString(4, "repId");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"regSearchByRegUsingRegNum ,when date is null(convertResultSetToListForNonGiftGiver) and sort is not equal to SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_REGNUM) >> cs

		1*registrySearchVO.getRegistryId() >> "1"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> true >> false
		0*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT)
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		rs.getString("EVENT_DT") >> null
		1*rs.getString("REG_FULL_NAME") >> "4"
		1*rs.getString("REG_LAST_NM") >> "5"
		1*rs.getString("REG_MAIDEN") >> "6"
		1*rs.getString("REG_ATG_PROFILE_ID") >> "7"
		1*rs.getString("COREG_FULL_NAME") >> "8"
		rs.getString("COREG_MAIDEN") >> "A"
		1*rs.getString("COREG_ATG_PROFILE_ID") >> "Y"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "9"
		1*rs.getString("REG_EMAIL_ADDR") >> "10"
		1*rs.getString("COREG_EMAIL_ADDR") >> "RE"
		//end convertResultSetToListForNonGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]
		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		setVOParams(vo1,vo2)
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]
		1*con.close() >>  {throw new SQLException("")}

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingRegNum(registrySearchVO)

		then:
		1*cs.setInt(1, 1);
		1*cs.setString(2, "tbs");
		1*cs.setString(3, "N");
		1*cs.setString(4, "repId");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*tools.logError("Error occurred while closing connection", _)
	}

	def"regSearchByRegUsingRegNum , lRegistrySummaryVOs list is empty"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_REGNUM) >> cs

		1*registrySearchVO.getRegistryId() >> "1"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(5) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> false
		//end convertResultSetToListForNonGiftGiver

		0*utils.removeDuplicateRows(_)
		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		0*utils.getPagedData(null,0,0)

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingRegNum(registrySearchVO)

		then:
		1*cs.setInt(1, 1);
		1*cs.setString(2, "tbs");
		1*cs.setString(3, "N");
		1*cs.setString(4, "repId");
		1*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		1*cs.close()
		1*con.close()
	}

	def"regSearchByRegUsingRegNum , when connection is null"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> null
		0*con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_REGNUM)
		0*tools.extractDBCallforgetRegistryListByRegNum(null)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(null,0,0)

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingRegNum(registrySearchVO)

		then:
		0*cs.setInt(1, null)
		0*cs.setString(2, null)
		0*cs.setString(3, null)
		0*cs.setString(4, null)
		0*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		0*cs.close()
		0*con.close()
	}

	def"regSearchByRegUsingRegNum , when Exception is thrown"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> {throw new SQLException("")}
		0*con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_REGNUM)
		0*tools.extractDBCallforgetRegistryListByRegNum(null)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(null,0,0)

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingRegNum(registrySearchVO)

		then:
		SQLException e = thrown()
		0*cs.setInt(1, null)
		0*cs.setString(2, null)
		0*cs.setString(3, null)
		0*cs.setString(4, null)
		0*cs.registerOutParameter(5, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		0*rs.close()
		// end convertResultSetToListForGiftGiver
		vo == null
		0*cs.close()
		0*con.close()
	}

	def"regSearchByRegUsingEmail , when SORT EQUALS SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_EMAIL2) >> cs

		1*registrySearchVO.getEmail() >> "1"
		1*registrySearchVO.getEvent() >>"event"
		1*registrySearchVO.getState() >>"state"
		1*tools.getFilterOptions("event", "state") >> "filerOption"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(6) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> true >> false
		0*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		rs.getString("EVENT_DT") >> ""
		1*rs.getString("REG_FULL_NAME") >> "4"
		1*rs.getString("REG_LAST_NM") >> "5"
		1*rs.getString("REG_MAIDEN") >> "6"
		1*rs.getString("REG_ATG_PROFILE_ID") >> "7"
		1*rs.getString("COREG_FULL_NAME") >> "8"
		rs.getString("COREG_MAIDEN") >> "A"
		1*rs.getString("COREG_ATG_PROFILE_ID") >> "Y"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "9"
		1*rs.getString("REG_EMAIL_ADDR") >> "10"
		1*rs.getString("COREG_EMAIL_ADDR") >> "RE"
		//end convertResultSetToListForNonGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for seperateRegistriesWithEmptyDate
		vo1.getEventDateObject() >> null
		vo2.getEventDateObject() >> new Date()
		//end seperateRegistriesWithEmptyDate

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingEmail(registrySearchVO)

		then:
		1*cs.setString(1, "1")
		1*cs.setString(2, "filerOption");
		1*cs.setString(3, "tbs");
		1*cs.setString(4, "Y");
		1*cs.setString(5, "repId")
		1*cs.registerOutParameter(6, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"regSearchByRegUsingEmail , sort is not equal to SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_EMAIL2) >> cs

		1*registrySearchVO.getEmail() >> "1"
		1*registrySearchVO.getEvent() >>"event"
		1*registrySearchVO.getState() >>"state"
		1*tools.getFilterOptions("event", "state") >> "filerOption"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(6) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> true >> false
		0*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT)
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		rs.getString("EVENT_DT") >> null
		1*rs.getString("REG_FULL_NAME") >> "4"
		1*rs.getString("REG_LAST_NM") >> "5"
		1*rs.getString("REG_MAIDEN") >> "6"
		1*rs.getString("REG_ATG_PROFILE_ID") >> "7"
		1*rs.getString("COREG_FULL_NAME") >> "8"
		rs.getString("COREG_MAIDEN") >> "A"
		1*rs.getString("COREG_ATG_PROFILE_ID") >> "Y"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "9"
		1*rs.getString("REG_EMAIL_ADDR") >> "10"
		1*rs.getString("COREG_EMAIL_ADDR") >> "RE"
		//end convertResultSetToListForNonGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]
		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		setVOParams(vo1,vo2)
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingEmail(registrySearchVO)

		then:
		1*cs.setString(1, "1")
		1*cs.setString(2, "filerOption");
		1*cs.setString(3, "tbs");
		1*cs.setString(4, "Y");
		1*cs.setString(5, "repId")
		1*cs.registerOutParameter(6, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"regSearchByRegUsingEmail , lRegistrySummaryVOs list is empty"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_EMAIL2) >> cs

		1*registrySearchVO.getEmail() >> "1"
		1*registrySearchVO.getEvent() >>"event"
		1*registrySearchVO.getState() >>"state"
		1*tools.getFilterOptions("event", "state") >> "filerOption"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(6) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> false
		//end convertResultSetToListForNonGiftGiver

		0*utils.removeDuplicateRows(_)
		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		0*utils.getPagedData(null,0,0)
		1*con.close() >> {throw new SQLException("")}

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingEmail(registrySearchVO)

		then:
		1*cs.setString(1, "1")
		1*cs.setString(2, "filerOption");
		1*cs.setString(3, "tbs");
		1*cs.setString(4, "N");
		1*cs.setString(5, "repId")
		1*cs.registerOutParameter(6, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		1*cs.close()
		1*tools.logError("Error occurred while closing connection", _)
	}

	def"regSearchByRegUsingEmail , when connection is null "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> null
		0*con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_EMAIL2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingEmail(registrySearchVO)

		then:
		0*cs.setString(1, null)
		0*cs.setString(2, null);
		0*cs.setString(3, null);
		0*cs.setString(4, null);
		0*cs.setString(5, null)
		0*cs.registerOutParameter(6, OracleTypes.CURSOR)
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"regSearchByRegUsingEmail , when Exception is thrown "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> {throw new Exception("")}
		0*con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_EMAIL2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingEmail(registrySearchVO)

		then:
		Exception e = thrown()
		0*cs.setString(2, null);
		0*cs.setString(3, null);
		0*cs.setString(4, null);
		0*cs.setString(5, null)
		0*cs.registerOutParameter(6, OracleTypes.CURSOR)
		vo ==null
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"regSearchByRegUsingName , when SORT EQUALS SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_NAME2) >> cs

		1*registrySearchVO.getFirstName() >> "fName"
		1*registrySearchVO.getLastName() >>"lName"
		1*registrySearchVO.getEvent() >>"event"
		1*registrySearchVO.getState() >>"state"
		1*tools.getFilterOptions("event", "state") >> "filerOption"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(7) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> true >> false
		1*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT) >> new Date()
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		rs.getString("EVENT_DT") >> "20161215"
		1*rs.getString("REG_FULL_NAME") >> "4"
		1*rs.getString("REG_LAST_NM") >> "5"
		1*rs.getString("REG_MAIDEN") >> "6"
		1*rs.getString("REG_ATG_PROFILE_ID") >> "7"
		1*rs.getString("COREG_FULL_NAME") >> "8"
		rs.getString("COREG_MAIDEN") >> "A"
		1*rs.getString("COREG_ATG_PROFILE_ID") >> "Y"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "9"
		1*rs.getString("REG_EMAIL_ADDR") >> "10"
		1*rs.getString("COREG_EMAIL_ADDR") >> "RE"
		//end convertResultSetToListForNonGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]

		//for seperateRegistriesWithEmptyDate
		vo1.getEventDateObject() >> null
		vo2.getEventDateObject() >> new Date()
		//end seperateRegistriesWithEmptyDate

		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_DATE
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingName(registrySearchVO)

		then:
		1*cs.setString(1, "fName")
		1*cs.setString(2, "lName")
		1*cs.setString(3, "filerOption");
		1*cs.setString(4, "tbs");
		1*cs.setString(5, "Y")
		1*cs.setString(6, "repId")
		1*cs.registerOutParameter(7, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*con.close()
	}

	def"regSearchByRegUsingName , lRegistrySummaryVOs list is empty"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_NAME2) >> cs

		1*registrySearchVO.getFirstName() >> "fName"
		1*registrySearchVO.getLastName() >>"lName"
		1*registrySearchVO.getEvent() >>"event"
		1*registrySearchVO.getState() >>"state"
		1*tools.getFilterOptions("event", "state") >> "filerOption"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> true
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(7) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> false
		//end convertResultSetToListForNonGiftGiver

		0*utils.removeDuplicateRows(_)
		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		0*utils.getPagedData(null,0,0)

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingName(registrySearchVO)

		then:
		1*cs.setString(1, "fName")
		1*cs.setString(2, "lName")
		1*cs.setString(3, "filerOption");
		1*cs.setString(4, "tbs");
		1*cs.setString(5, "Y")
		1*cs.setString(6, "repId")
		1*cs.registerOutParameter(7, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		1*cs.close()
	}

	def"regSearchByRegUsingName , sort is not equal to SORT_SEQ_KEY_DATE"(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_NAME2) >> cs

		1*registrySearchVO.getFirstName() >> "fName"
		1*registrySearchVO.getLastName() >>"lName"
		1*registrySearchVO.getEvent() >>"event"
		1*registrySearchVO.getState() >>"state"
		1*tools.getFilterOptions("event", "state") >> "filerOption"
		1*registrySearchVO.getSiteId() >>"tbs"
		1*registrySearchVO.getProfileId() >> pro
		1*pro.getRepositoryId() >> "repId"
		registrySearchVO.isReturnLeagacyRegistries() >> false
		1*tools.extractDBCallforgetRegistryListByRegNum(cs) >> {}
		cs.getObject(7) >> rs

		//for convertResultSetToListForNonGiftGiver
		rs.next() >> true >> false
		0*utils.formatStringToDate(_, BBBCoreConstants.DATE_FORMAT)
		1*rs.getString("REGISTRY_NUM") >> "50"
		1*rs.getString("EVENT_TYPE") >> "3"
		rs.getString("EVENT_DT") >> null
		1*rs.getString("REG_FULL_NAME") >> "4"
		1*rs.getString("REG_LAST_NM") >> "5"
		1*rs.getString("REG_MAIDEN") >> "6"
		1*rs.getString("REG_ATG_PROFILE_ID") >> "7"
		1*rs.getString("COREG_FULL_NAME") >> "8"
		rs.getString("COREG_MAIDEN") >> "A"
		1*rs.getString("COREG_ATG_PROFILE_ID") >> "Y"
		1*rs.getString("SEARCHED_REG_SUB_TYPE") >> "9"
		1*rs.getString("REG_EMAIL_ADDR") >> "10"
		1*rs.getString("COREG_EMAIL_ADDR") >> "RE"
		//end convertResultSetToListForNonGiftGiver
		1*utils.removeDuplicateRows(_) >> [vo1,vo2]
		//for sortAndExcludeRegistries
		registrySearchVO.getSortSeqOrder() >> BBBGiftRegistryConstants.SORT_ORDER_DESC
		// end sortAndExcludeRegistries

		registrySearchVO.getSort() >> BBBGiftRegistryConstants.SORT_SEQ_KEY_STATE
		setVOParams(vo1,vo2)
		1*utils.getPagedData([vo2,vo1],0,0) >> [vo2,vo1]
		1*con.close() >> {throw new SQLException("")}

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingName(registrySearchVO)

		then:
		1*cs.setString(1, "fName")
		1*cs.setString(2, "lName")
		1*cs.setString(3, "filerOption");
		1*cs.setString(4, "tbs");
		1*cs.setString(5, "N")
		1*cs.setString(6, "repId")
		1*cs.registerOutParameter(7, OracleTypes.CURSOR)
		// for convertResultSetToListForGiftGiver
		2*rs.close()
		// end convertResultSetToListForGiftGiver
		vo.getListRegistrySummaryVO() == [vo2,vo1]
		vo.getTotEntries() == 2
		1*cs.close()
		1*tools.logError("Error occurred while closing connection", _)
	}

	def"regSearchByRegUsingName , when connection is null "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> null
		0*con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_NAME2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingName(registrySearchVO)

		then:
		0*cs.setString(1, null)
		0*cs.setString(2, null);
		0*cs.setString(3, null);
		0*cs.setString(4, null);
		0*cs.setString(5, null)
		0*cs.registerOutParameter(6, null)
		0*cs.registerOutParameter(7, OracleTypes.CURSOR )
		vo.getListRegistrySummaryVO() == null
		vo.getTotEntries() == 0
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"regSearchByRegUsingName , when Exception is thrown "(){

		given:
		setParametrsForSpy()
		RegistrySearchVO registrySearchVO =Mock()
		1*gsRep.getDataSource() >> DS
		1*DS.getConnection() >> {throw new Exception("")}
		0*con.prepareCall(BBBGiftRegistryConstants.REGSEARCH_BY_REG_USING_NAME2)
		0*tools.extractDBCallforgetRegistryListByRegNum(_)
		0*utils.removeDuplicateRows(_)
		0*utils.getPagedData(_,0,0) >> []

		when:
		RegSearchResVO vo = tools.regSearchByRegUsingName(registrySearchVO)

		then:
		Exception e = thrown()
		0*cs.setString(2, null);
		0*cs.setString(3, null);
		0*cs.setString(4, null);
		0*cs.setString(5, null)
		0*cs.setString(6, null)
		0*cs.registerOutParameter(7, OracleTypes.CURSOR)
		vo ==null
		0*cs.close()
		0*con.close()
		0*rs.close
	}

	def"getFilterOptions, when event and state is empty"(){

		given:
		String state =""
		String event = ""

		when:
		String str =tools.getFilterOptions(event , state)

		then:
		str =="state:All;eventType:All"
	}

	def"getFilterOptions, when event and state is  not empty"(){

		given:
		String state ="state"
		String event = "event"

		when:
		String str =tools.getFilterOptions(event , state)

		then:
		str =="state:state;eventType:event"
	}

	def"getFilterOptions, when event is empty and state is not empty"(){

		given:
		String state ="state"
		String event = ""

		when:
		String str =tools.getFilterOptions(event , state)

		then:
		str =="state:state;eventType:All"
	}

	def"getFilterOptions, when event is not empty and state is empty"(){

		given:
		String state =""
		String event = "event"

		when:
		String str =tools.getFilterOptions(event , state)

		then:
		str =="state:All;eventType:event"
	}

	def"addORUpdateRegistry, when registry is updated"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"
		RegistryVO registryVO =Mock()
		MutableRepositoryItem pOwnerProfileItem =Mock()
		MutableRepositoryItem pCoRegProfileItem =Mock()
		RepositoryView view = Mock()
		MutableRepositoryItem rItem =Mock()
		EventVO eventVo =Mock()
		RegistryTypes rType =Mock()

		registryVO.getSiteId() >> BBBGiftRegistryConstants.CANADA_SITE_ID
		registryVO.getRegistryId() >> "regId"

		//for fetchGiftRepositoryItem
		tools.setGiftRepoItemQuery("age>30")
		//for executeRQLQuery
		1*mRep.getView("giftregistry") >> view
		1*tools.extractDBCall(_,_,view) >> [rItem]
		// end fetchGiftRepositoryItems

		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> "24/02/2016"
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,pOwnerProfileItem,pCoRegProfileItem)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("eventDate", _)
		1*rItem.setPropertyValue("eventType", registryVO.getRegistryType().getRegistryTypeName())
		1*rItem.setPropertyValue("registryOwner",pOwnerProfileItem)
		1*rItem.setPropertyValue("registryCoOwner",pCoRegProfileItem)
		1*mRep.updateItem(rItem)
	}

	def"addORUpdateRegistry, when registry is updated, site id is not CANADA_SITE_ID(stringToDateConverter)"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> "tbs"
		registryVO.getRegistryId() >> "regId"

		//for fetchGiftRepositoryItem
		tools.setGiftRepoItemQuery("age>30")
		//for executeRQLQuery
		1*mRep.getView("giftregistry") >> view
		1*tools.extractDBCall(_,_,view) >> [rItem]
		// end fetchGiftRepositoryItems

		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> "02/24/2016"
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,pOwnerProfileItem,pCoRegProfileItem)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("eventDate", _)
		1*rItem.setPropertyValue("eventType", registryVO.getRegistryType().getRegistryTypeName())
		1*rItem.setPropertyValue("registryOwner",pOwnerProfileItem)
		1*rItem.setPropertyValue("registryCoOwner",pCoRegProfileItem)
		1*mRep.updateItem(rItem)
	}

	def"addORUpdateRegistry, when registry is updated, date is equal to 0(stringToDateConverter)"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> "tbs"
		registryVO.getRegistryId() >> "regId"

		//for fetchGiftRepositoryItem
		tools.setGiftRepoItemQuery("age>30")
		//for executeRQLQuery
		1*mRep.getView("giftregistry") >> view
		1*tools.extractDBCall(_,_,view) >> [rItem]
		// end fetchGiftRepositoryItems

		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> "0"
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,pOwnerProfileItem,pCoRegProfileItem)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("eventDate", null)
		1*rItem.setPropertyValue("eventType", registryVO.getRegistryType().getRegistryTypeName())
		1*rItem.setPropertyValue("registryOwner",pOwnerProfileItem)
		1*rItem.setPropertyValue("registryCoOwner",pCoRegProfileItem)
		1*mRep.updateItem(rItem)
	}

	def"addORUpdateRegistry, when registry is updated, date is empty(stringToDateConverter)"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> "tbs"
		registryVO.getRegistryId() >> "regId"

		//for fetchGiftRepositoryItem
		tools.setGiftRepoItemQuery("age>30")
		//for executeRQLQuery
		1*mRep.getView("giftregistry") >> view
		1*tools.extractDBCall(_,_,view) >> [rItem]
		// end fetchGiftRepositoryItems

		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> ""
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,null,null)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("eventDate", null)
		1*rItem.setPropertyValue("eventType", registryVO.getRegistryType().getRegistryTypeName())
		0*rItem.setPropertyValue("registryOwner",null)
		0*rItem.setPropertyValue("registryCoOwner",null)
		1*mRep.updateItem(rItem)
	}

	def"addORUpdateRegistry, when registry is updated, exception is thrown while parsing date(stringToDateConverter)"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"
		registryVO.getSiteId() >> "tbs"
		registryVO.getRegistryId() >> "regId"

		//for fetchGiftRepositoryItem
		tools.setGiftRepoItemQuery("age>30")
		//for executeRQLQuery
		1*mRep.getView("giftregistry") >> view
		1*tools.extractDBCall(_,_,view) >> [rItem]
		// end fetchGiftRepositoryItems

		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> "2412"

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,pOwnerProfileItem,pCoRegProfileItem)

		then:
		BBBSystemException e = thrown()
		item == null
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
	}

	def"addORUpdateRegistry, when BBBBusinessException is thrown"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"
		registryVO.getSiteId() >> BBBGiftRegistryConstants.CANADA_SITE_ID
		registryVO.getRegistryId() >> "regId"
		1*tools.fetchGiftRepositoryItem(registryVO.getSiteId(), registryVO.getRegistryId()) >> {throw new BBBBusinessException("")}

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,pOwnerProfileItem,pCoRegProfileItem)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10130+ " BBBBusinessException from addORUpdateRegistry of GiftRegistryTools",_)
		item == null
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"addORUpdateRegistry, when item is added in the registry and getGiftRegistryItem is called "(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> null
		registryVO.getRegistryId() >> "regId"
		registryVO.getCookieType() >> BBBCoreConstants.WED_CHANNEL_REF
		registryVO.getAffiliateTag() >> "affTg"

		//for  getGiftRegistryItem
		1*mRep.createItem("giftregistry") >> rItem
		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getStatus() >> "status"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> "24/12/2016"
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"
		//end getGiftRegistryItem

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,null,pCoRegProfileItem)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("registryId",registryVO.getRegistryId())
		1*rItem.setPropertyValue("registryStatus", registryVO.getStatus())
		1*rItem.setPropertyValue("eventType", rType.getRegistryTypeName())
		1*rItem.setPropertyValue("eventDate", null)
		1*rItem.setPropertyValue("registryOwner",null)
		1*rItem.setPropertyValue("registryCoOwner",pCoRegProfileItem)
		1*rItem.setPropertyValue("sessionId","0")
		1*rItem.setPropertyValue("affiliateTag", BBBGiftRegistryConstants.WEDDING_CHANNEL_VALUE)
		1*mRep.addItem(rItem)
	}

	def"addORUpdateRegistry, when item is added in the registry and getGiftRegistryItem is called with event date null,registryCoOwner null and cookie equals THEBUMP_REF"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> null
		registryVO.getRegistryId() >> "regId"
		registryVO.getCookieType() >>BBBCoreConstants.THEBUMP_REF
		registryVO.getAffiliateTag() >> "affTg"

		//for  getGiftRegistryItem
		1*mRep.createItem("giftregistry") >> rItem
		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getStatus() >> "status"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> null
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"
		//end getGiftRegistryItem

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,null,null)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("registryId",registryVO.getRegistryId())
		1*rItem.setPropertyValue("registryStatus", registryVO.getStatus())
		1*rItem.setPropertyValue("eventType", rType.getRegistryTypeName())
		0*rItem.setPropertyValue("eventDate", null)
		1*rItem.setPropertyValue("registryOwner",null)
		0*rItem.setPropertyValue("registryCoOwner",null)
		1*rItem.setPropertyValue("sessionId","0")
		1*rItem.setPropertyValue("affiliateTag", BBBGiftRegistryConstants.BUMP_VALUE)
		1*mRep.addItem(rItem)
	}

	def"addORUpdateRegistry, when item is added in the registry and getGiftRegistryItem is called with event date null,registryCoOwner null and cookie equals null"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> null
		registryVO.getRegistryId() >> "regId"
		registryVO.getCookieType() >>null
		registryVO.getAffiliateTag() >> "affTg"

		//for getGiftRegistryItem
		1*mRep.createItem("giftregistry") >> rItem
		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getStatus() >> "status"
		registryVO.getEvent() >> eventVo
		eventVo.getEventDate() >> null
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"
		//end getGiftRegistryItem

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,null,null)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("registryId",registryVO.getRegistryId())
		1*rItem.setPropertyValue("registryStatus", registryVO.getStatus())
		1*rItem.setPropertyValue("eventType", rType.getRegistryTypeName())
		0*rItem.setPropertyValue("eventDate", null)
		1*rItem.setPropertyValue("registryOwner",null)
		0*rItem.setPropertyValue("registryCoOwner",null)
		1*rItem.setPropertyValue("sessionId","0")
		1*rItem.setPropertyValue("affiliateTag", "affTg")
		1*mRep.addItem(rItem)
	}

	def"addORUpdateRegistry, when item is added in the registry and getGiftRegistryItem is called with eventType null,registryCoOwner null and cookie equals IF"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> null
		registryVO.getRegistryId() >> "regId"
		registryVO.getCookieType() >>"IF"
		registryVO.getAffiliateTag() >> "affTg"

		//for getGiftRegistryItem
		1*mRep.createItem("giftregistry") >> rItem
		registryVO.getRegBG() >> "reg"
		registryVO.getCoRegBG() >> "CoReg"
		registryVO.getStatus() >> "status"
		registryVO.getEvent() >> null
		registryVO.getRegistryType() >> rType
		rType.getRegistryTypeName()  >> "typeName"
		//end getGiftRegistryItem

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,null,null)

		then:
		item == rItem
		1*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		1*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		1*rItem.setPropertyValue("registryId",registryVO.getRegistryId())
		1*rItem.setPropertyValue("registryStatus", registryVO.getStatus())
		1*rItem.setPropertyValue("eventType", rType.getRegistryTypeName())
		0*rItem.setPropertyValue("eventDate", null)
		1*rItem.setPropertyValue("registryOwner",null)
		0*rItem.setPropertyValue("registryCoOwner",null)
		1*rItem.setPropertyValue("sessionId","0")
		1*rItem.setPropertyValue("affiliateTag", "affTg")
		1*mRep.addItem(rItem)
	}

	def"addORUpdateRegistry, when item is added in the registry and getGiftRegistryItem method throws RepositoryException"(){

		given:
		setParametrsForSpy()
		String state =""
		String event = "event"

		registryVO.getSiteId() >> null
		registryVO.getRegistryId() >> "regId"
		registryVO.getCookieType() >>"IF"
		registryVO.getAffiliateTag() >> "affTg"

		//for getGiftRegistryItem
		1*mRep.createItem("giftregistry") >> {throw new RepositoryException("")}
		//end getGiftRegistryItem

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,null,null)

		then:
		item == null
		0*rItem.setPropertyValue("regBG",registryVO.getRegBG())
		0*rItem.setPropertyValue("coRegBG",registryVO.getCoRegBG())
		0*rItem.setPropertyValue("registryId",registryVO.getRegistryId())
		0*rItem.setPropertyValue("registryStatus", registryVO.getStatus())
		0*rItem.setPropertyValue("eventType", rType.getRegistryTypeName())
		0*rItem.setPropertyValue("eventDate", null)
		0*rItem.setPropertyValue("registryOwner",null)
		0*rItem.setPropertyValue("registryCoOwner",null)
		0*rItem.setPropertyValue("sessionId","0")
		0*rItem.setPropertyValue("affiliateTag", "affTg")
		0*mRep.addItem(rItem)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"addORUpdateRegistry, when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		registryVO.getSiteId() >> "tbs"
		registryVO.getRegistryId() >> ""
		registryVO.getCookieType() >>"IF"
		registryVO.getAffiliateTag() >> "affTg"

		//for getGiftRegistryItem
		1*tools.getGiftRegistryItem(registryVO, null, null) >> rItem
		1*mRep.addItem(rItem) >> {throw new RepositoryException("")}
		//end getGiftRegistryItem

		when:
		MutableRepositoryItem item =tools.addORUpdateRegistry(registryVO,null,null)

		then:
		item == null
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131+ " RepositoryException from addORUpdateRegistry of GiftRegistryTools",_)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"canScheduleAppointmentForRegType, configValue is not empty and is false"(){

		given:
		String siteId ="tbs"
		String registryType ="reg"

		1*catalogTools.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> "false"

		when:
		boolean flag =tools.canScheduleAppointmentForRegType(siteId,registryType)

		then:
		flag == false
	}

	def"canScheduleAppointmentForRegType, when siteAppTypesConfigurations contains empty string"(){

		given:
		String siteId ="tbs"
		String registryType ="reg"

		1*catalogTools.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> "true"
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE,BBBGiftRegistryConstants.APPOINTMENT_TYPES_CONFIG_VALUE) >> [""]

		when:
		boolean flag =tools.canScheduleAppointmentForRegType(siteId,registryType)

		then:
		flag == false
	}

	def"canScheduleAppointmentForRegType, when siteAppTypesConfigurations list is empty"(){

		given:
		String siteId ="tbs"
		String registryType ="reg"

		1*catalogTools.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> ""
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE,BBBGiftRegistryConstants.APPOINTMENT_TYPES_CONFIG_VALUE) >> []

		when:
		boolean flag =tools.canScheduleAppointmentForRegType(siteId,registryType)

		then:
		flag == false
	}

	def"canScheduleAppointmentForRegType, when siteAppTypesConfigurations list contains registryType"(){

		given:
		String siteId ="tbs"
		String registryType ="reg"

		1*catalogTools.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> ""
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE,BBBGiftRegistryConstants.APPOINTMENT_TYPES_CONFIG_VALUE) >> ["str1,reg"]

		when:
		boolean flag =tools.canScheduleAppointmentForRegType(siteId,registryType)

		then:
		flag == true
	}

	def"canScheduleAppointmentForRegType, when siteAppTypesConfigurations list does not contains registryType"(){

		given:
		String siteId ="tbs"
		String registryType ="reg"

		1*catalogTools.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> ""
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE,BBBGiftRegistryConstants.APPOINTMENT_TYPES_CONFIG_VALUE) >> ["str1"]

		when:
		boolean flag =tools.canScheduleAppointmentForRegType(siteId,registryType)

		then:
		flag == false
	}

	def"canScheduleAppointmentForRegType, when BBBSystemException is thrown"(){

		given:
		setParametrsForSpy()
		String siteId ="tbs"
		String registryType ="reg"

		1*catalogTools.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> ""
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE,BBBGiftRegistryConstants.APPOINTMENT_TYPES_CONFIG_VALUE) >> {throw new BBBSystemException("")}

		when:
		boolean flag =tools.canScheduleAppointmentForRegType(siteId,registryType)

		then:
		flag == false
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131+ " BBBSystemException from canScheduleAppointment of GiftRegistryManager",_)
	}

	def"canScheduleAppointmentForRegType, when BBBBusinessException is thrown"(){

		given:
		setParametrsForSpy()
		String siteId ="tbs"
		String registryType ="reg"

		1*catalogTools.getConfigKeyValue(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE, siteId, BBBCoreConstants.FALSE) >> ""
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.SKEDGE_ME_CONFIG_TYPE,BBBGiftRegistryConstants.APPOINTMENT_TYPES_CONFIG_VALUE) >> {throw new BBBBusinessException("")}

		when:
		boolean flag =tools.canScheduleAppointmentForRegType(siteId,registryType)

		then:
		flag == false
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131+ " BBBBusinessException from canScheduleAppointment of GiftRegistryManager",_)
	}

	def"canScheduleAppointmentForStore, when storeId is empty"(){

		given:
		String storeId =""
		String registryType ="reg"

		when:
		boolean flag =tools.canScheduleAppointmentForStore(storeId,registryType)

		then:
		flag == false
	}

	def"canScheduleAppointmentForStore, when RegAppointmentType List contains registryType"(){

		given:
		String storeId ="store"
		String registryType ="reg"
		StoreVO storeVO = Mock()
		storeVO.isAcceptingAppointments() >> true
		storeVO.getRegAppointmentTypes() >> ["reg"]

		1*catalogTools.getStoreAppointmentDetails(storeId) >> storeVO

		when:
		boolean flag =tools.canScheduleAppointmentForStore(storeId,registryType)

		then:
		flag == true
	}
	def"canScheduleAppointmentForStore, when storeVo is null"(){

		given:
		String storeId ="store"
		String registryType ="reg"
		1*catalogTools.getStoreAppointmentDetails(storeId) >> null

		when:
		boolean flag =tools.canScheduleAppointmentForStore(storeId,registryType)

		then:
		flag == false
	}
	def"canScheduleAppointmentForStore, when storeVO.isAcceptingAppointments() is false"(){

		given:
		String storeId ="store"
		String registryType ="reg"
		StoreVO storeVO = Mock()
		storeVO.isAcceptingAppointments() >> false

		1*catalogTools.getStoreAppointmentDetails(storeId) >> storeVO

		when:
		boolean flag =tools.canScheduleAppointmentForStore(storeId,registryType)

		then:
		flag == false
	}
	def"canScheduleAppointmentForStore, when RegAppointmentType List is null"(){

		given:
		String storeId ="store"
		String registryType ="reg"
		StoreVO storeVO = Mock()
		storeVO.isAcceptingAppointments() >> true
		storeVO.getRegAppointmentTypes() >>null
		1*catalogTools.getStoreAppointmentDetails(storeId) >> storeVO

		when:
		boolean flag =tools.canScheduleAppointmentForStore(storeId,registryType)

		then:
		flag == false
	}

	def"persistRecommendationToken, update token if registryId is present in RegistryRecommendations item descriptor or add the registryID and token"(){

		given:
		setParametrsForSpy()
		RegistryVO registryVO =Mock()
		Set<String> emailList = new HashSet()
		registryVO.getRegistryId() >> "registryId"
		emailList.add("email1")

		//for createInvitee
		MutableRepositoryItem mItem =Mock()
		1*mRep.createItem(BBBCatalogConstants.INVITEE) >> mItem
		// end createInvitee
		//for  fetchRegistryRecommendationItem
		MutableRepositoryItem rItem =Mock()
		MutableRepositoryItem rItem1 =Mock()
		tools.setGiftRepoItemQuery("age>30")
		1*tools.executeRQLQuery("age>30", _, "RegistryRecommendations", mRep) >> [rItem]
		// end fetchRegistryRecommendationItem
		1*rItem.getPropertyValue(BBBCatalogConstants.INVITEES) >> ["str":rItem1]

		when:
		Map<String, RepositoryItem> returnMap =tools.persistRecommendationToken(registryVO,emailList)

		then:
		returnMap.containsValue(mItem) == true
		//for createInvitee
		1*mItem.setPropertyValue(BBBCatalogConstants.INVITEE_EMAILI_ID, "email1")
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_CREATION_DATE,_)
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_STATUS, 1)
		1*mRep.addItem(mItem)
		//end createInvitee
		1*rItem.setPropertyValue(BBBCatalogConstants.REGISTRY_ID, "registryId");
		1*rItem.setPropertyValue(BBBCatalogConstants.INVITEES,_)
		1*mRep.updateItem(rItem)
	}

	def"persistRecommendationToken, when giftRegRecommendRepositoryItems is null and createRegRecommItem method is called"(){

		given:
		setParametrsForSpy()
		RegistryVO registryVO =Mock()
		Set<String> emailList = new HashSet()
		registryVO.getRegistryId() >> ""
		emailList.add("email1")

		//for createInvitee
		MutableRepositoryItem mItem =Mock()
		1*mRep.createItem(BBBCatalogConstants.INVITEE) >> mItem
		// end createInvitee
		//for  fetchRegistryRecommendationItem
		tools.setGiftRepoItemQuery("age>30")
		0*tools.executeRQLQuery("age>30", _, "RegistryRecommendations", mRep)
		// end fetchRegistryRecommendationItem

		//for createRegRecommItem
		MutableRepositoryItem mrItem =Mock()
		Site site =Mock()
		RegistryTypes types =Mock()
		RegistrantVO rVo =Mock()
		EventVO eventVO =Mock()

		registryVO.getSiteId() >> BBBCoreConstants.SITE_BAB_CA
		1*mRep.createItem(BBBCatalogConstants.REGISTRY_RECOMMENDATIONS) >> mrItem
		registryVO.getRegistryType() >> types
		types.getRegistryTypeName() >> "regName"
		registryVO.getEvent() >> eventVO
		eventVO.getEventDate() >> null
		registryVO.getPrimaryRegistrant() >> rVo
		rVo.getFirstName() >> "fName"
		1*tools.extractSite() >> site
		site.getId() >> "siteId"
		// end createRegRecommItem

		when:
		Map<String, RepositoryItem> returnMap =tools.persistRecommendationToken(registryVO,emailList)

		then:
		returnMap.containsValue(mItem) == true
		//for createInvitee
		1*mItem.setPropertyValue(BBBCatalogConstants.INVITEE_EMAILI_ID, "email1")
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_CREATION_DATE,_)
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_STATUS, 1)
		1*mRep.addItem(mItem)
		// end createInvitee
		0*mRep.updateItem(_)
		//for createRegRecommItem
		1*registryVO.setSiteId("3")
		1*mrItem.setPropertyValue(BBBCatalogConstants.REGISTRY_ID,registryVO.getRegistryId());
		1*mrItem.setPropertyValue(BBBCatalogConstants.EVENT_TYPE, "regName");
		1*mrItem.setPropertyValue(BBBCatalogConstants.EVENT_DATE,null)
		1*mrItem.setPropertyValue(BBBCatalogConstants.REGISTRANT_NAME, "fName")
		1*mrItem.setPropertyValue(BBBCatalogConstants.INVITEES, _)
		1*mrItem.setPropertyValue(BBBCatalogConstants.SITE_ID, "siteId")
		1*mRep.addItem(mrItem)
		//end createRegRecommItem
	}

	def"persistRecommendationToken, when BBBBusinessException is thrown by fetchRegistryRecommendationItem method"(){

		given:
		setParametrsForSpy()
		RegistryVO registryVO =Mock()
		Set<String> emailList = new HashSet()
		registryVO.getRegistryId() >> "registryId"
		emailList.add("email1")
		//for createInvitee
		MutableRepositoryItem mItem =Mock()
		1*mRep.createItem(BBBCatalogConstants.INVITEE) >> mItem
		// end createInvitee
		1*tools.fetchRegistryRecommendationItem("registryId") >> {throw new BBBBusinessException("")}

		when:
		Map<String, RepositoryItem> returnMap =tools.persistRecommendationToken(registryVO,emailList)

		then:
		returnMap == null
		//for createInvitee
		1*mItem.setPropertyValue(BBBCatalogConstants.INVITEE_EMAILI_ID, "email1")
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_CREATION_DATE,_)
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_STATUS, 1)
		1*mRep.addItem(mItem)
		//end createInvitee
		1*tools.logError(BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR+ " BBBBusinessException from persistRecommendationToken of GiftRegistryTools",_)
		0*mRep.updateItem(_)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"persistRecommendationToken, when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		RegistryVO registryVO =Mock()
		Set<String> emailList = new HashSet()
		registryVO.getRegistryId() >> "registryId"
		emailList.add("email1")

		//for createInvitee
		MutableRepositoryItem mItem =Mock()
		1*mRep.createItem(BBBCatalogConstants.INVITEE) >> mItem
		// end createInvitee
		//for  fetchRegistryRecommendationItem
		MutableRepositoryItem rItem =Mock()
		MutableRepositoryItem rItem1 =Mock()
		tools.setGiftRepoItemQuery("age>30")
		1*tools.executeRQLQuery("age>30", _, "RegistryRecommendations", mRep) >> [rItem]
		// end fetchRegistryRecommendationItem
		1*rItem.getPropertyValue(BBBCatalogConstants.INVITEES) >> ["str":rItem1]
		1*mRep.updateItem(rItem) >> {throw new RepositoryException("")}

		when:
		Map<String, RepositoryItem> returnMap =tools.persistRecommendationToken(registryVO,emailList)

		then:
		returnMap == null
		//for createInvitee
		1*mItem.setPropertyValue(BBBCatalogConstants.INVITEE_EMAILI_ID, "email1")
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_CREATION_DATE,_)
		1*mItem.setPropertyValue(BBBCatalogConstants.TOKEN_STATUS, 1)
		1*mRep.addItem(mItem)
		//end createInvitee
		1*rItem.setPropertyValue(BBBCatalogConstants.REGISTRY_ID, "registryId");
		1*rItem.setPropertyValue(BBBCatalogConstants.INVITEES,_)
		1*tools.logError(BBBCoreErrorConstants.RECOMMEND_GIFTREGISTRY_ERROR+ " Error while adding token in repository ", _)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"executeRQLQuery, when view is null and queryResult is null "(){

		given:
		setParametrsForSpy()
		String rqlQuery = "age>30"
		Object[] params
		String viewName ="view"
		MutableRepository repository =Mock()
		1*repository.getView(viewName) >> null
		1*tools.extractDBCall(params, _, null) >> null

		when:
		RepositoryItem [] ret =tools.executeRQLQuery(rqlQuery,params,viewName,repository)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10132+ viewName+ " view is null from executeRQLQuery of GiftRegistryTools")
	}

	def"executeRQLQuery, when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		String rqlQuery = "age>30"
		Object[] params
		String viewName ="view"
		MutableRepository repository =Mock()
		1*repository.getView(viewName) >> {throw new RepositoryException("")}
		0*tools.extractDBCall(params, _, null)

		when:
		RepositoryItem [] ret =tools.executeRQLQuery(rqlQuery,params,viewName,repository)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10133+ " Repository Exception [Unable to retrieve data] from executeRQLQuery of GiftRegistryTools",_)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"executeRQLQuery, when Repository is null"(){

		given:
		setParametrsForSpy()
		String rqlQuery = "age>30"
		Object[] params
		String viewName ="view"

		when:
		RepositoryItem [] ret =tools.executeRQLQuery(rqlQuery,params,viewName,null)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10134+ " Repository is null from executeRQLQuery of GiftRegistryTools")
	}

	def"executeRQLQuery, when rqlQuery is null"(){

		given:
		setParametrsForSpy()
		Object[] params
		String viewName ="view"

		when:
		RepositoryItem [] ret =tools.executeRQLQuery(null,params,viewName,null)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10135+ " Query String is null from executeRQLQuery of GiftRegistryTools")
	}

	def"createRegRecommItem, when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		RegistryVO registryVO =Mock()
		Map<String, RepositoryItem> tokenCreationMap = new HashMap()
		registryVO.getSiteId() >> "tbs"
		1*mRep.createItem(BBBCatalogConstants.REGISTRY_RECOMMENDATIONS) >> {throw new RepositoryException("")}

		when:
		MutableRepositoryItem ret =tools.createRegRecommItem(registryVO,tokenCreationMap)

		then:
		ret == null
		0*registryVO.setSiteId("3")
		1*tools.logError(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION+ " inside createRegRecommItem method " + "")
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"createInvitee, when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		String email = "email"
		1*mRep.createItem(BBBCatalogConstants.INVITEE) >> {throw new RepositoryException("")}

		when:
		RepositoryItem ret =tools.createInvitee(email)

		then:
		ret == null
		1*tools.logError(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION+ " inside createInvitee method " + "")
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"getFutureRegistryList, get future registry list for the user"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RegistryTypeVO vo1 =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()
		RepositoryItem repositoryItem =Mock()
		RepositoryItem repositoryItem1 =Mock()

		String siteId ="tbs"
		profile.getRepositoryId() >> "repId"
		requestMock.getLocale() >> {new Locale("en_US")}
		1*tools.fetchUserRegistries(siteId,"repId", true) >> [r1,r2]
		r1.getRepositoryId() >> "r1Id"
		r2.getRepositoryId() >> "r2Id"

		1*mRep.getItem("r1Id","giftregistry") >> repositoryItem
		repositoryItem.getPropertyValue("eventDate") >> new Date()
		repositoryItem.getPropertyValue("eventType") >> "eventType"
		1*catalogTools.getRegistryTypeName("eventType", siteId) >> "regTypeName"
		repositoryItem.getPropertyValue("registryStatus") >> "regStatus"

		1*mRep.getItem("r2Id","giftregistry") >> repositoryItem1
		repositoryItem1.getPropertyValue("eventDate") >> (new Date()-1)
		repositoryItem1.getPropertyValue("eventType") >> "eventType1"
		1*catalogTools.getRegistryTypeName("eventType1", siteId) >> "regTypeName1"
		repositoryItem1.getPropertyValue("registryStatus") >> "regStatus1"

		when:
		List<RegistrySkinnyVO> retList =tools.getFutureRegistryList(profile,siteId)

		then:
		RegistrySkinnyVO skinnyVO = retList.get(0)
		skinnyVO.getEventDate() != null
		skinnyVO.getEventCode() == "eventType"
		skinnyVO.getEventType() == "regTypeName"
		skinnyVO.getRegistryId() == "r1Id"
		skinnyVO.getStatus() == "regStatus"
	}

	def"getFutureRegistryList, when registryIdRepItems List is null"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RegistryTypeVO vo1 =Mock()

		String siteId ="tbs"
		profile.getRepositoryId() >> "repId"
		requestMock.getLocale() >> {new Locale("en_US")}
		1*tools.fetchUserRegistries(siteId,"repId", true) >> null
		0*mRep.getItem(null,"giftregistry")
		0*catalogTools.getRegistryTypeName(null, siteId)

		when:
		List<RegistrySkinnyVO> retList =tools.getFutureRegistryList(profile,siteId)

		then:
		retList.isEmpty()
	}

	def"getFutureRegistryList, when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RegistryTypeVO vo1 =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem repositoryItem =Mock()

		String siteId ="tbs"
		profile.getRepositoryId() >> "repId"
		requestMock.getLocale() >> {new Locale("en_US")}
		1*tools.fetchUserRegistries(siteId,"repId", true) >> [r1]
		r1.getRepositoryId() >> "r1Id"

		1*mRep.getItem("r1Id","giftregistry") >> {throw new RepositoryException("")}
		0*catalogTools.getRegistryTypeName(null, siteId)


		when:
		List<RegistrySkinnyVO> retList =tools.getFutureRegistryList(profile,siteId)

		then:
		retList == null
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10136+ " RepositoryException of getFutureRegistryList from GiftRegistryTools",_)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"getUserRegistryList, get users all registry list for the user"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RegistryResVO resVo =Mock()
		RegistryResVO resVo1 =Mock()
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()
		RepositoryItem r3 =Mock()
		RegistrySummaryVO vo1 =Mock()
		AddressVO shippingAddress =Mock()
		RegistryVO regVo =Mock()
		RegistrantVO registrantVo =Mock()
		BBBSessionBean sessionBean =Mock()

		String siteId ="canada"
		profile.getRepositoryId() >> "repId"
		1*tools.isCanadaSite(siteId) >> true
		1*tools.fetchUserRegistries(siteId, "repId", true) >> [r1,r2,r3]

		3*catalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> ["str"]
		1*tools.getRegistryInfoFromEcomAdmin(r1.getRepositoryId(),"str") >> null

		1*tools.getRegistryInfoFromEcomAdmin(r2.getRepositoryId(),"str") >> resVo
		resVo.getRegistrySummaryVO()  >> null

		1*tools.getRegistryInfoFromEcomAdmin(r3.getRepositoryId(),"str") >> resVo1
		resVo1.getRegistrySummaryVO()  >> vo1
		vo1.getShippingAddress() >>shippingAddress
		shippingAddress.getAddressLine1() >> "add1"
		shippingAddress.getAddressLine2() >> "add2"
		resVo1.getRegistryVO() >> regVo
		regVo.getPrimaryRegistrant() >> registrantVo
		registrantVo.getCellPhone() >> "phone"
		1*r3.getPropertyValue("eventDate") >> new Date()
		1*r3.getPropertyValue("eventType") >> "eventType"
		1*r3.getPropertyValue("registryStatus") >> "regStatus"
		1*catalogTools.getRegistryTypeName("eventType", siteId) >> "regTypeName"
		requestMock.getLocale() >> {new Locale("en_US")}

		Map<String,List<RegistrySkinnyVO>> map = new HashMap()
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getValues() >> map


		when:
		List<RegistrySkinnyVO> retList =tools.getUserRegistryList(profile,siteId)

		then:
		RegistrySkinnyVO skinnyVO = retList.get(0)
		skinnyVO.getAlternatePhone() == "phone"
		skinnyVO.isPoBoxAddress() == false
		skinnyVO.getEventCode() == "eventType"
		skinnyVO.getEventType() == "regTypeName"
		skinnyVO.getRegistryId() == null
		skinnyVO.getStatus() == "regStatus"
		skinnyVO.getPrimaryRegistrantFirstName() == null
		skinnyVO.getCoRegistrantFirstName()== null
		skinnyVO.getEventDate() != null
		map.size()==2
	}

	def"getUserRegistryList, get users all registry list for the user when isCanadaSite is false and eventDate is null"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RegistryResVO resVo1 =Mock()
		RepositoryItem r3 =Mock()
		RegistrySummaryVO vo1 =Mock()
		AddressVO shippingAddress =Mock()
		RegistryVO regVo =Mock()
		RegistrantVO registrantVo =Mock()
		BBBSessionBean sessionBean =Mock()

		String siteId ="canada"
		profile.getRepositoryId() >> "repId"
		1*tools.isCanadaSite(siteId) >> false
		1*tools.fetchUserRegistries(siteId, "repId", true) >> [r3]

		1*catalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,siteId) >> ["str"]
		1*tools.getRegistryInfoFromEcomAdmin(r3.getRepositoryId(),"str") >> resVo1
		resVo1.getRegistrySummaryVO()  >> vo1
		vo1.getShippingAddress() >>shippingAddress
		shippingAddress.getAddressLine1() >> "add1"
		shippingAddress.getAddressLine2() >> "add2"
		resVo1.getRegistryVO() >> regVo
		regVo.getPrimaryRegistrant() >> registrantVo
		registrantVo.getCellPhone() >> ""
		1*r3.getPropertyValue("eventDate") >> null
		1*r3.getPropertyValue("eventType") >> "eventType"
		1*r3.getPropertyValue("registryStatus") >> "regStatus"
		1*catalogTools.getRegistryTypeName("eventType", siteId) >> "regTypeName"
		requestMock.getLocale() >> {new Locale("en_US")}

		Map<String,List<RegistrySkinnyVO>> map = new HashMap()
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getValues() >> map

		when:
		List<RegistrySkinnyVO> retList =tools.getUserRegistryList(profile,siteId)

		then:
		RegistrySkinnyVO skinnyVO = retList.get(0)
		skinnyVO.getAlternatePhone() == null
		skinnyVO.isPoBoxAddress() == false
		skinnyVO.getEventCode() == "eventType"
		skinnyVO.getEventType() == "regTypeName"
		skinnyVO.getRegistryId() == null
		skinnyVO.getStatus() == "regStatus"
		skinnyVO.getPrimaryRegistrantFirstName() == null
		skinnyVO.getCoRegistrantFirstName()== null
		skinnyVO.getEventDate() == null
		map.size()==2
	}

	def"getUserRegistryList, get users all registry list for the user when siteId is null and registryIdRepItems"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		BBBSessionBean sessionBean =Mock()

		String siteId =null
		profile.getRepositoryId() >> "repId"
		1*tools.fetchUserRegistries(siteId, "repId", true) >> null

		0*catalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,null)
		0*tools.getRegistryInfoFromEcomAdmin(_,_)
		0*catalogTools.getRegistryTypeName(_, null)

		Map<String,List<RegistrySkinnyVO>> map = new HashMap()
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getValues() >> map

		when:
		List<RegistrySkinnyVO> retList =tools.getUserRegistryList(profile,siteId)

		then:
		retList.isEmpty() == true
		map.size()==2
	}

	def"getUserRegistryList, get users all registry list for the user when RepositoryException is thrown from fetchUserRegistries Method"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()

		String siteId =null
		profile.getRepositoryId() >> "repId"
		1*tools.fetchUserRegistries(siteId, "repId", true) >> {throw new RepositoryException("")}
		0*catalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,null)
		0*tools.getRegistryInfoFromEcomAdmin(_,_)
		0*catalogTools.getRegistryTypeName(_, null)

		when:
		List<RegistrySkinnyVO> retList =tools.getUserRegistryList(profile,siteId)

		then:
		retList == null
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10137+ " RepositoryException of getUserRegistryList from GiftRegistryTools",_)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003:2003")
	}

	def"getUserRegistryList,  throws BBBSystemException"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RepositoryItem r1 =Mock()
		BBBSessionBean sessionBean =Mock()

		String siteId =null
		profile.getRepositoryId() >> "repId"
		1*tools.fetchUserRegistries(siteId, "repId", true) >> [r1]
		1*catalogTools.getAllValuesForKey(BBBWebServiceConstants.TXT_WSDLKEY_WSSITEFLAG,null) >> {throw new BBBSystemException("")}
		0*catalogTools.getRegistryTypeName(_, null)

		Map<String,List<RegistrySkinnyVO>> map = new HashMap()
		1*requestMock.resolveName("/com/bbb/profile/session/SessionBean") >> sessionBean
		sessionBean.getValues() >> map

		when:
		List<RegistrySkinnyVO> retList =tools.getUserRegistryList(profile,siteId)

		then:
		retList == []
		1*tools.logError("RegsitryID : " + null + " doesn't exists in Database.")
		map.size()==2
	}

	def"isCanadaSite, when siteId is equal to canadaSiteId"(){

		given:
		String siteId ="10"
		1*catalogTools.getConfigValueByconfigType("ContentCatalogKeys") >> ["BedBathCanadaSiteCode":"10"]

		when:
		boolean flag =tools.isCanadaSite(siteId)

		then:
		flag == true
	}

	def"isCanadaSite, when siteId is not to canadaSiteId"(){

		given:
		String siteId ="11"
		1*catalogTools.getConfigValueByconfigType("ContentCatalogKeys") >> ["BedBathCanadaSiteCode":"10"]

		when:
		boolean flag =tools.isCanadaSite(siteId)

		then:
		flag == false
	}

	def"isCanadaSite, when siteId is null"(){

		given:
		String siteId =null
		1*catalogTools.getConfigValueByconfigType("ContentCatalogKeys") >> ["BedBathCanadaSiteCode":"10"]

		when:
		boolean flag =tools.isCanadaSite(siteId)

		then:
		flag == false
	}

	def"isCanadaSite, throws BBBSystemException"(){

		given:
		setParametrsForSpy()
		String siteId =null
		1*catalogTools.getConfigValueByconfigType("ContentCatalogKeys") >> {throw new BBBSystemException("")}

		when:
		boolean flag =tools.isCanadaSite(siteId)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10138+ " BBBSystemException [Catalog Exception] from isCanadaSite of GiftRegistryTools",_)
		1*tools.logError("CLS[GiftRegistryFormHandler]/MSG=[Catalog Exception]"+ "", _)
		flag == false
	}

	def"isCanadaSite, throws BBBBusinessException"(){

		given:
		setParametrsForSpy()
		String siteId =null
		1*catalogTools.getConfigValueByconfigType("ContentCatalogKeys") >> {throw new BBBBusinessException("")}

		when:
		boolean flag =tools.isCanadaSite(siteId)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10139+ " BBBBusinessException [Catalog Exception] from isCanadaSite of GiftRegistryTools",_)
		flag == false
	}

	def"fetchUserRegistries, whe is activeonly flag is false"(){

		given:
		setParametrsForSpy()
		RegistryTypeVO vo1 =Mock()
		RegistryTypeVO vo2 =Mock()
		RepositoryItem r1 =Mock()
		String siteId ="tbs"
		String profileId = "proId"
		boolean activeOnly = false
		1*catalogTools.getRegistryTypes(siteId) >> [vo1]
		tools.setUserRegistryQuery("age>30")
		vo1.getRegistryCode() >> "11"

		0*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY)
		1*tools.executeRQLQuery(_, _, "giftregistry", mRep) >> [r1]

		when:
		RepositoryItem[] ret =tools.fetchUserRegistries(siteId,profileId,activeOnly)

		then:
		ret ==[r1]
	}

	def"fetchUserRegistries, whe is activeonly flag is true"(){

		given:
		setParametrsForSpy()
		RegistryTypeVO vo1 =Mock()
		RegistryTypeVO vo2 =Mock()
		RepositoryItem r1 =Mock()
		String siteId ="tbs"
		String profileId = "proId"
		boolean activeOnly = true
		1*catalogTools.getRegistryTypes(siteId) >> [vo1]
		tools.setUserRegistryQuery("age>30")
		vo1.getRegistryCode() >> "11"

		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> ["str1,str2"]
		1*tools.executeRQLQuery(_, _, "giftregistry", mRep) >> null

		when:
		RepositoryItem[] ret =tools.fetchUserRegistries(siteId,profileId,activeOnly)

		then:
		ret ==null
	}

	def"fetchUserRegistries, whe is activeonly flag is true and acceptableStatuses is empty"(){

		given:
		setParametrsForSpy()
		RegistryTypeVO vo1 =Mock()
		RegistryTypeVO vo2 =Mock()
		RepositoryItem r1 =Mock()
		String siteId ="tbs"
		String profileId = "proId"
		boolean activeOnly = true
		1*catalogTools.getRegistryTypes(siteId) >> [vo1]
		tools.setUserRegistryQuery("age>30")
		vo1.getRegistryCode() >> "11"

		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> [""]
		1*tools.executeRQLQuery(_, _, "giftregistry", mRep) >> null

		when:
		RepositoryItem[] ret =tools.fetchUserRegistries(siteId,profileId,activeOnly)

		then:
		ret ==null
	}

	def"fetchUserRegistries, whe is activeonly flag is true and acceptableStatusesList is empty"(){

		given:
		setParametrsForSpy()
		RegistryTypeVO vo1 =Mock()
		RegistryTypeVO vo2 =Mock()
		RepositoryItem r1 =Mock()
		String siteId ="tbs"
		String profileId = "proId"
		boolean activeOnly = true
		1*catalogTools.getRegistryTypes(siteId) >> [vo1]
		tools.setUserRegistryQuery("age>30")
		vo1.getRegistryCode() >> "11"

		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.GIFT_REGISTRY_ACCEPTABLE_STATUSES_CONFIG_KEY) >> []
		1*tools.executeRQLQuery(_, _, "giftregistry", mRep) >> null

		when:
		RepositoryItem[] ret =tools.fetchUserRegistries(siteId,profileId,activeOnly)

		then:
		ret ==null
	}

	def"calculatePersonalizedPrice, when getPersonalizationType is PERSONALIZATION_CODE_PY"(){

		given:
		boolean isGiftGiver =true
		Double customizedPrice = 50.0
		Double personalizedPrice = 60.0
		SKUDetailVO skuVO =Mock()
		String siteId = BBBCoreConstants.TBS

		skuVO.isInCartFlag() >> true
		skuVO.getPersonalizationType() >> BBBCoreConstants.PERSONALIZATION_CODE_PY
		1*catalogTools.getIncartPrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 100.0

		when:
		Double price =tools.calculatePersonalizedPrice(isGiftGiver,customizedPrice,personalizedPrice,skuVO,siteId)

		then:
		price == 150.0
	}

	def"calculatePersonalizedPrice, when getPersonalizationType is PERSONALIZATION_CODE_PB"(){

		given:
		boolean isGiftGiver =false
		Double customizedPrice = 50.0
		Double personalizedPrice = 60.0
		SKUDetailVO skuVO =Mock()
		String siteId = ""

		skuVO.isInCartFlag() >> true
		skuVO.getPersonalizationType() >> BBBCoreConstants.PERSONALIZATION_CODE_PB
		1*catalogTools.getIncartPrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 100.0

		when:
		Double price =tools.calculatePersonalizedPrice(isGiftGiver,customizedPrice,personalizedPrice,skuVO,siteId)

		then:
		price == 100.0
	}

	def"calculatePersonalizedPrice, when basePrice is retreived from SalePrice"(){

		given:
		boolean isGiftGiver =true
		Double customizedPrice = 50.0
		Double personalizedPrice = 60.0
		SKUDetailVO skuVO =Mock()
		String siteId = ""

		skuVO.isInCartFlag() >> true
		skuVO.getPersonalizationType() >> "personalization"
		1*catalogTools.getSalePrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 80.0

		when:
		Double price =tools.calculatePersonalizedPrice(isGiftGiver,customizedPrice,personalizedPrice,skuVO,siteId)

		then:
		price == 50.0
	}

	def"calculatePersonalizedPrice, when basePrice is retreived from ListPrice"(){

		given:
		boolean isGiftGiver =true
		Double customizedPrice = 50.0
		Double personalizedPrice = 60.0
		SKUDetailVO skuVO =Mock()
		String siteId = ""

		skuVO.isInCartFlag() >> true
		skuVO.getPersonalizationType() >> ""
		1*catalogTools.getSalePrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 0.0
		1*catalogTools.getListPrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 120.0

		when:
		Double price =tools.calculatePersonalizedPrice(isGiftGiver,customizedPrice,personalizedPrice,skuVO,siteId)

		then:
		price == 60.0
	}

	def"calculatePersonalizedPrice, when skuVo.isInCartFlag is false and salePrice is zero"(){

		given:
		boolean isGiftGiver =true
		Double customizedPrice = 50.0
		Double personalizedPrice = 60.0
		SKUDetailVO skuVO =Mock()
		String siteId = ""

		skuVO.isInCartFlag() >> false
		skuVO.getPersonalizationType() >> ""
		1*catalogTools.getSalePrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 0.0
		1*catalogTools.getListPrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 120.0

		when:
		Double price =tools.calculatePersonalizedPrice(isGiftGiver,customizedPrice,personalizedPrice,skuVO,siteId)

		then:
		price == 60.0
	}

	def"calculatePersonalizedPrice, when skuVo.isInCartFlag is false"(){

		given:
		boolean isGiftGiver =true
		Double customizedPrice = 50.0
		Double personalizedPrice = 60.0
		SKUDetailVO skuVO =Mock()
		String siteId = ""

		skuVO.isInCartFlag() >> false
		skuVO.getPersonalizationType() >> ""
		1*catalogTools.getSalePrice(skuVO.getParentProdId(), skuVO.getSkuId()) >> 100.0

		when:
		Double price =tools.calculatePersonalizedPrice(isGiftGiver,customizedPrice,personalizedPrice,skuVO,siteId)

		then:
		price == 60.0
	}

	def"addItemToGiftRegistry, Adds the item to gift registry "(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "1"
		pGiftRegistryViewBean.getQuantity() >> "10"

		//for getCreateProgram
		2*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> ["createProgram"]

		con.prepareCall(BBBGiftRegistryConstants.ADD_ITEM_TO_REG2) >> cs

		//for getStoreNum
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["50"]

		//for getRowXngUser
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["ROWXNGUSER"]

		when:
		ValidateAddItemsResVO resVo = tools.addItemToGiftRegistry(pGiftRegistryViewBean)

		then:
		1*cs.setString(1, pGiftRegistryViewBean.getRegistryId());
		1*cs.setString(2, pGiftRegistryViewBean.getSku());
		1*cs.setInt(3,10)
		1*cs.setString(4, pGiftRegistryViewBean.getRefNum());
		1*cs.setString(5, pGiftRegistryViewBean.getItemTypes());
		1*cs.setString(6, pGiftRegistryViewBean.getAssemblySelections());
		1*cs.setString(7, pGiftRegistryViewBean.getAssemblyPrices());
		1*cs.setString(8, pGiftRegistryViewBean.getLtlDeliveryServices());
		1*cs.setString(9, pGiftRegistryViewBean.getLtlDeliveryPrices());
		1*cs.setString(10, pGiftRegistryViewBean.getPersonlizationCodes());
		1*cs.setString(11,pGiftRegistryViewBean.getPersonalizationPrices());
		1*cs.setString(12, pGiftRegistryViewBean.getCustomizationPrices());
		1*cs.setString(13,pGiftRegistryViewBean.getPersonalizationDescrips());
		1*cs.setString(14,pGiftRegistryViewBean.getPersonalizedImageUrls());
		1*cs.setString(15,pGiftRegistryViewBean.getPersonalizedImageUrlThumbs());
		1*cs.setString(16,pGiftRegistryViewBean.getPersonalizedMobImageUrls());
		1*cs.setString(17, pGiftRegistryViewBean.getPersonalizedMobImageUrlThumbs());
		1*cs.setString(18, utils.getJDADateTime())
		1*cs.setString(19, "createProgram");
		1*cs.setLong(20, 50)
		cs.setString(21,"createProgram")
		cs.setString(22, "ROWXNGUSER");
		cs.setString(23, "1")
		resVo.getServiceErrorVO().isErrorExists() == false
	}

	def"addItemToGiftRegistry, Adds the item to gift registry"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "2"
		pGiftRegistryViewBean.getQuantity() >> "10"

		//for getCreateProgram
		2*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []

		con.prepareCall(BBBGiftRegistryConstants.ADD_ITEM_TO_REG2) >> cs

		//for getStoreNum
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["50"]

		//for getRowXngUser
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >>[]

		when:
		ValidateAddItemsResVO resVo = tools.addItemToGiftRegistry(pGiftRegistryViewBean)

		then:
		1*cs.setString(1, pGiftRegistryViewBean.getRegistryId());
		1*cs.setString(2, pGiftRegistryViewBean.getSku());
		1*cs.setInt(3,10)
		1*cs.setString(4, pGiftRegistryViewBean.getRefNum());
		1*cs.setString(5, pGiftRegistryViewBean.getItemTypes());
		1*cs.setString(6, pGiftRegistryViewBean.getAssemblySelections());
		1*cs.setString(7, pGiftRegistryViewBean.getAssemblyPrices());
		1*cs.setString(8, pGiftRegistryViewBean.getLtlDeliveryServices());
		1*cs.setString(9, pGiftRegistryViewBean.getLtlDeliveryPrices());
		1*cs.setString(10, pGiftRegistryViewBean.getPersonlizationCodes());
		1*cs.setString(11,pGiftRegistryViewBean.getPersonalizationPrices());
		1*cs.setString(12, pGiftRegistryViewBean.getCustomizationPrices());
		1*cs.setString(13,pGiftRegistryViewBean.getPersonalizationDescrips());
		1*cs.setString(14,pGiftRegistryViewBean.getPersonalizedImageUrls());
		1*cs.setString(15,pGiftRegistryViewBean.getPersonalizedImageUrlThumbs());
		1*cs.setString(16,pGiftRegistryViewBean.getPersonalizedMobImageUrls());
		1*cs.setString(17, pGiftRegistryViewBean.getPersonalizedMobImageUrlThumbs());
		1*cs.setString(18, utils.getJDADateTime())
		1*cs.setString(19, BBBGiftRegistryConstants.BAB_COM);
		1*cs.setLong(20, 50)
		1*cs.setString(21,BBBGiftRegistryConstants.BAB_COM)
		1*cs.setString(22, "");
		1*cs.setString(23, "2")
		resVo.getServiceErrorVO().isErrorExists() == false
	}

	def"addItemToGiftRegistry, when SQLException is thrown"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ServiceErrorVO serviceError =Mock()
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "3"
		1*con.prepareCall(BBBGiftRegistryConstants.ADD_ITEM_TO_REG2) >> {throw new SQLException(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)}
		1*utils.logAndFormatError("AddItemsToRegistry2",con,BBBGiftRegistryConstants.SERVICE_ERROR_VO,_,pGiftRegistryViewBean.getRegistryId(),
				pGiftRegistryViewBean.getSku(),pGiftRegistryViewBean.getQuantity(),pGiftRegistryViewBean.getItemTypes(),pGiftRegistryViewBean.getRefNum(),
				pGiftRegistryViewBean.getAssemblySelections(),pGiftRegistryViewBean.getAssemblyPrices(),pGiftRegistryViewBean.getLtlDeliveryServices(),
				pGiftRegistryViewBean.getLtlDeliveryPrices(),pGiftRegistryViewBean.getPersonlizationCodes(),pGiftRegistryViewBean.getPersonalizationPrices(),pGiftRegistryViewBean.getCustomizationPrices(),
				pGiftRegistryViewBean.getPersonalizationDescrips(),pGiftRegistryViewBean.getPersonalizedImageUrls(),pGiftRegistryViewBean
				.getPersonalizedImageUrlThumbs(),pGiftRegistryViewBean.getPersonalizedMobImageUrls(),
				pGiftRegistryViewBean.getPersonalizedMobImageUrlThumbs()) >> serviceError

		1*con.close() >> {throw new SQLException("")}

		when:
		ValidateAddItemsResVO res = tools.addItemToGiftRegistry(pGiftRegistryViewBean)

		then:
		res.getServiceErrorVO() == serviceError
	}

	def"addItemToGiftRegistry, when SQLException is thrown with error message null"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ServiceErrorVO serviceError =Mock()
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "3"
		1*con.prepareCall(BBBGiftRegistryConstants.ADD_ITEM_TO_REG2) >> {throw new SQLException()}
		1*utils.logAndFormatError("AddItemsToRegistry2",con,BBBGiftRegistryConstants.SERVICE_ERROR_VO,_,pGiftRegistryViewBean.getRegistryId(),
				pGiftRegistryViewBean.getSku(),pGiftRegistryViewBean.getQuantity(),pGiftRegistryViewBean.getItemTypes(),pGiftRegistryViewBean.getRefNum(),
				pGiftRegistryViewBean.getAssemblySelections(),pGiftRegistryViewBean.getAssemblyPrices(),pGiftRegistryViewBean.getLtlDeliveryServices(),
				pGiftRegistryViewBean.getLtlDeliveryPrices(),pGiftRegistryViewBean.getPersonlizationCodes(),pGiftRegistryViewBean.getPersonalizationPrices(),pGiftRegistryViewBean.getCustomizationPrices(),
				pGiftRegistryViewBean.getPersonalizationDescrips(),pGiftRegistryViewBean.getPersonalizedImageUrls(),pGiftRegistryViewBean
				.getPersonalizedImageUrlThumbs(),pGiftRegistryViewBean.getPersonalizedMobImageUrls(),
				pGiftRegistryViewBean.getPersonalizedMobImageUrlThumbs()) >> serviceError

		1*con.close() >> {throw new SQLException("")}

		when:
		ValidateAddItemsResVO res = tools.addItemToGiftRegistry(pGiftRegistryViewBean)

		then:
		res.getServiceErrorVO() == serviceError
	}

	def"addItemToGiftRegistry, when SQLException is thrown with error message empty"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ServiceErrorVO serviceError =Mock()
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "3"
		1*con.prepareCall(BBBGiftRegistryConstants.ADD_ITEM_TO_REG2) >> {throw new SQLException("")}
		1*utils.logAndFormatError("AddItemsToRegistry2",con,BBBGiftRegistryConstants.SERVICE_ERROR_VO,_,pGiftRegistryViewBean.getRegistryId(),
				pGiftRegistryViewBean.getSku(),pGiftRegistryViewBean.getQuantity(),pGiftRegistryViewBean.getItemTypes(),pGiftRegistryViewBean.getRefNum(),
				pGiftRegistryViewBean.getAssemblySelections(),pGiftRegistryViewBean.getAssemblyPrices(),pGiftRegistryViewBean.getLtlDeliveryServices(),
				pGiftRegistryViewBean.getLtlDeliveryPrices(),pGiftRegistryViewBean.getPersonlizationCodes(),pGiftRegistryViewBean.getPersonalizationPrices(),pGiftRegistryViewBean.getCustomizationPrices(),
				pGiftRegistryViewBean.getPersonalizationDescrips(),pGiftRegistryViewBean.getPersonalizedImageUrls(),pGiftRegistryViewBean
				.getPersonalizedImageUrlThumbs(),pGiftRegistryViewBean.getPersonalizedMobImageUrls(),
				pGiftRegistryViewBean.getPersonalizedMobImageUrlThumbs()) >> serviceError

		1*con.close() >> {throw new SQLException("")}

		when:
		ValidateAddItemsResVO res = tools.addItemToGiftRegistry(pGiftRegistryViewBean)

		then:
		res.getServiceErrorVO() == serviceError
	}

	def"addItemToGiftRegistry, when connection is null"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		gsRep.getDataSource() >> DS
		DS.getConnection() >> null
		0*con.prepareCall(BBBGiftRegistryConstants.ADD_ITEM_TO_REG2)

		when:
		ValidateAddItemsResVO res = tools.addItemToGiftRegistry(pGiftRegistryViewBean)

		then:
		res.getServiceErrorVO().isErrorExists() == false
		0*con.close()
		0*cs.close()
	}

	def"removePersonalizedSkuFromSession, when personalised sku is found"(){

		given:
		AddItemsBean b1 =Mock()
		AddItemsBean b2 =Mock()
		EximSessionBean e1 =Mock()
		BBBSessionBean sessionBean =Mock()
		b1.getSku()  >> "1"
		b2.getSku()  >> "2"
		sessionBean.getPersonalizedSkus() >> ["1":e1]
		1*requestMock.getAttribute(BBBCoreConstants.SESSION_BEAN_NAME) >> sessionBean

		when:
		tools.removePersonalizedSkuFromSession([b1,b2])

		then:
		sessionBean.getPersonalizedSkus() == [:]
	}

	def"removeUpdateGiftRegistryItem, Adds all the item to gift registry with single web service call"(){/*-- check why failing --*/

		given:
		setParametrsForSpy()
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ManageRegItemsResVO mageItemsResVO =Mock()
		1*tools.extractUtilMethod(pGiftRegistryViewBean)  >> mageItemsResVO

		when:
		ManageRegItemsResVO resVo = tools.removeUpdateGiftRegistryItem(pGiftRegistryViewBean)

		then:
		resVo == mageItemsResVO
	}

	def"removeUpdateGiftRegistryItemInEcomAdmin, when isErrorExists is true"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ErrorStatus errorStatus =Mock()
		pGiftRegistryViewBean.getRegistryId() >> "1"
		pGiftRegistryViewBean.getRowId() >> "2"
		pGiftRegistryViewBean.getQuantity() >> "10"

		1*utils.validateInput(["1","2","10"]) >> errorStatus
		errorStatus.isErrorExists() >> true

		when:
		ManageRegItemsResVO res =tools.removeUpdateGiftRegistryItemInEcomAdmin(pGiftRegistryViewBean)

		then:
		ServiceErrorVO errorVO =res.getServiceErrorVO()
		errorVO.isErrorExists() == true
		errorVO.getErrorId() == 0
		errorVO.getErrorMessage() == null
	}

	def"removeUpdateGiftRegistryItemInEcomAdmin, when isErrorExists is false and quantity is less than 0"(){

		given:
		setParametrsForSpy()
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ErrorStatus errorStatus1 =Mock()
		pGiftRegistryViewBean.getRegistryId() >> "1"
		pGiftRegistryViewBean.getRowId() >> "2"
		pGiftRegistryViewBean.getQuantity() >> "-1"

		1*utils.validateInput(["1","2","-1"]) >> errorStatus1
		errorStatus1.isErrorExists() >> false

		when:
		ManageRegItemsResVO res =tools.removeUpdateGiftRegistryItemInEcomAdmin(pGiftRegistryViewBean)

		then:
		ServiceErrorVO errorVO =res.getServiceErrorVO()
		errorVO.isErrorExists() == true
		errorVO.getErrorId() == BBBGiftRegistryConstants.VALIDATION
		0*tools.updateItemsInRegistry2(pGiftRegistryViewBean)
	}

	def"removeUpdateGiftRegistryItemInEcomAdmin, when isErrorExists is false and quantity is greater than 9999"(){

		given:
		setParametrsForSpy()
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ErrorStatus errorStatus =Mock()
		pGiftRegistryViewBean.getRegistryId() >> "1"
		pGiftRegistryViewBean.getRowId() >> "2"
		pGiftRegistryViewBean.getQuantity() >> "10000"

		1*utils.validateInput(["1","2","10000"]) >> errorStatus
		errorStatus.isErrorExists() >> false

		when:
		ManageRegItemsResVO res =tools.removeUpdateGiftRegistryItemInEcomAdmin(pGiftRegistryViewBean)

		then:
		ServiceErrorVO errorVO =res.getServiceErrorVO()
		errorVO.isErrorExists() == true
		errorVO.getErrorId() == BBBGiftRegistryConstants.VALIDATION
		0*tools.updateItemsInRegistry2(pGiftRegistryViewBean)
	}

	def"removeUpdateGiftRegistryItemInEcomAdmin, when isErrorExists is false"(){

		given:
		setParametrsForSpy()
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		ErrorStatus errorStatus =Mock()
		pGiftRegistryViewBean.getRegistryId() >> "1"
		pGiftRegistryViewBean.getRowId() >> "2"
		pGiftRegistryViewBean.getQuantity() >> "50"

		1*utils.validateInput(["1","2","50"]) >> errorStatus
		errorStatus.isErrorExists() >> false

		when:
		ManageRegItemsResVO res =tools.removeUpdateGiftRegistryItemInEcomAdmin(pGiftRegistryViewBean)

		then:
		res == null
		1*tools.updateItemsInRegistry2(pGiftRegistryViewBean) >>{}
	}

	def"updateItemsInRegistry2, updates registryItem in EomAdmin DB"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> t
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "5"
		pGiftRegistryViewBean.getRowId()  >> "rowId"
		pGiftRegistryViewBean.getQuantity() >> "10"
		pGiftRegistryViewBean.getItemTypes() >> "itemTypes"
		pGiftRegistryViewBean.getAssemblyPrices() >> 100
		pGiftRegistryViewBean.getLtlDeliveryPrices() >> 200
		pGiftRegistryViewBean.getRegistryId() >> 50
		con.prepareCall(BBBGiftRegistryConstants.Update_Items_In_Registry2) >> cs
		//for getCreateProgram
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		//for getStoreNum
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["50"]
		//for getRowXngUser
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >>[]

		when:
		ManageRegItemsResVO resVo = tools.updateItemsInRegistry2(pGiftRegistryViewBean)

		then:
		1*cs.setInt(1, 50);
		1*cs.setString(2, "rowId")
		1*cs.setInt(3,10)
		1*cs.setString(4, "REG")
		1*cs.setString(5, pGiftRegistryViewBean.getAssemblySelections());
		1*cs.setBigDecimal(6, 100)
		1*cs.setString(7, pGiftRegistryViewBean.getLtlDeliveryServices());
		1*cs.setBigDecimal(8, 200)
		1*cs.setInt(9, 50)
		1*cs.setString(10, "")
		1*cs.setString(11,"")
		1*cs.setString(12, "5")
		resVo.getServiceErrorVO().isErrorExists() == false
	}

	def"updateItemsInRegistry2, updates registryItem in EomAdmin DB when quantity is less than zero "(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> t
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "5"
		pGiftRegistryViewBean.getRowId()  >> "rowId"
		pGiftRegistryViewBean.getQuantity() >> "-1"
		pGiftRegistryViewBean.getItemTypes() >> ""
		pGiftRegistryViewBean.getAssemblyPrices() >> ""
		pGiftRegistryViewBean.getLtlDeliveryPrices() >> ""
		pGiftRegistryViewBean.getRegistryId() >> 50
		con.prepareCall(BBBGiftRegistryConstants.Update_Items_In_Registry2) >> cs
		//for getCreateProgram
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		//for getStoreNum
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["50"]
		//for getRowXngUser
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >>[]

		when:
		ManageRegItemsResVO resVo = tools.updateItemsInRegistry2(pGiftRegistryViewBean)

		then:
		1*cs.setInt(1, 50);
		1*cs.setString(2, "rowId")
		1*cs.setInt(3,-1)
		1*cs.setString(4, "REG")
		1*cs.setString(5, pGiftRegistryViewBean.getAssemblySelections());
		1*cs.setBigDecimal(6, null)
		1*cs.setString(7, pGiftRegistryViewBean.getLtlDeliveryServices());
		1*cs.setBigDecimal(8, null)
		1*cs.setInt(9, 50)
		1*cs.setString(10, "")
		1*cs.setString(11,"")
		1*cs.setString(12, "5")
		resVo.getServiceErrorVO().isErrorExists() == false
	}

	def"updateItemsInRegistry2, updates registryItem in EomAdmin DB when AssemblyPrice and LTLDeliveryPrice is null"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> t
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "5"
		pGiftRegistryViewBean.getRowId()  >> "rowId"
		pGiftRegistryViewBean.getQuantity() >> "-1"
		pGiftRegistryViewBean.getItemTypes() >> "LTL"
		pGiftRegistryViewBean.getAssemblyPrices() >> null
		pGiftRegistryViewBean.getLtlDeliveryPrices() >> null
		pGiftRegistryViewBean.getRegistryId() >> 50
		con.prepareCall(BBBGiftRegistryConstants.Update_Items_In_Registry2) >> cs
		//for getCreateProgram
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		//for getStoreNum
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["50"]
		//for getRowXngUser
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >>[]

		when:
		ManageRegItemsResVO resVo = tools.updateItemsInRegistry2(pGiftRegistryViewBean)

		then:
		1*cs.setInt(1, 50);
		1*cs.setString(2, "rowId")
		1*cs.setInt(3,-1)
		1*cs.setString(4, "LTL")
		1*cs.setString(5, pGiftRegistryViewBean.getAssemblySelections());
		1*cs.setBigDecimal(6, null)
		1*cs.setString(7, pGiftRegistryViewBean.getLtlDeliveryServices());
		1*cs.setBigDecimal(8, null)
		1*cs.setInt(9, 50)
		1*cs.setString(10, "")
		1*cs.setString(11,"")
		1*cs.setString(12, "5")
		resVo.getServiceErrorVO().isErrorExists() == false
	}

	def"updateItemsInRegistry2, updates registryItem in EomAdmin DB when itemType is PER "(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> t
		gsRep.getDataSource() >> DS
		DS.getConnection() >>con
		pGiftRegistryViewBean.getSiteFlag() >> "5"
		pGiftRegistryViewBean.getRowId()  >> "rowId"
		pGiftRegistryViewBean.getQuantity() >> "-1"
		pGiftRegistryViewBean.getItemTypes() >> "PER"
		pGiftRegistryViewBean.getAssemblyPrices() >> null
		pGiftRegistryViewBean.getLtlDeliveryPrices() >> null
		pGiftRegistryViewBean.getRegistryId() >> 50
		con.prepareCall(BBBGiftRegistryConstants.Update_Items_In_Registry2) >> cs
		//for getCreateProgram
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		//for getStoreNum
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["50"]
		//for getRowXngUser
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >>[]

		1*con.close() >> {throw new SQLException("")}

		when:
		ManageRegItemsResVO resVo = tools.updateItemsInRegistry2(pGiftRegistryViewBean)

		then:
		1*cs.setInt(1, 50);
		1*cs.setString(2, "rowId")
		1*cs.setInt(3,-1)
		1*cs.setString(4, "PER")
		1*cs.setString(5, pGiftRegistryViewBean.getAssemblySelections());
		1*cs.setBigDecimal(6, null)
		1*cs.setString(7, pGiftRegistryViewBean.getLtlDeliveryServices());
		1*cs.setBigDecimal(8, null)
		1*cs.setInt(9, 50)
		1*cs.setString(10, "")
		1*cs.setString(11,"")
		1*cs.setString(12, "5")
		resVo.getServiceErrorVO().isErrorExists() == false
	}

	def"updateItemsInRegistry2, when Exception is thrown"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		Transaction t =Mock()
		ServiceErrorVO vo =Mock()
		manager.getTransaction() >> t
		gsRep.getDataSource() >> DS
		DS.getConnection() >> {throw new Exception("")}
		0*con.prepareCall(BBBGiftRegistryConstants.Update_Items_In_Registry2)
		1*utils.logAndFormatError("UpdateItemsInRegistry2", null, "serviceErrorVO", _,pGiftRegistryViewBean.getUserToken(),pGiftRegistryViewBean.getSiteFlag(),
				pGiftRegistryViewBean.getRegistryId(),pGiftRegistryViewBean.getRowId(),pGiftRegistryViewBean.getQuantity())  >>vo

		when:
		ManageRegItemsResVO resVo = tools.updateItemsInRegistry2(pGiftRegistryViewBean)

		then:
		resVo.getServiceErrorVO() != null
	}

	def"updateItemsInRegistry2, when Connection is null"(){ // --not covering as null pointer if connection is  null --

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		Transaction t =Mock()
		ServiceErrorVO vo =Mock()
		manager.getTransaction() >> t
		gsRep.getDataSource() >> DS
		DS.getConnection() >>null

		when:
		ManageRegItemsResVO resVo = tools.updateItemsInRegistry2(pGiftRegistryViewBean)

		then:
		resVo.getServiceErrorVO().isErrorExists() == false
	}

	def"hasInvalidChars, when sValue is not null"(){

		given:
		String invalidCharsPattern ="125"
		String sValue ="1233"

		when:
		boolean flag = tools.hasInvalidChars(invalidCharsPattern,sValue)

		then:
		flag ==false
	}

	def"hasInvalidChars, when sValue is null"(){

		given:
		String invalidCharsPattern ="125"
		String sValue =null

		when:
		boolean flag = tools.hasInvalidChars(invalidCharsPattern,sValue)

		then:
		flag ==false
	}

	def"importRegistry, when getRegistryId is  not null"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RegistryVO pRegistryVO =Mock()
		RegistryResVO regVo =Mock()
		pRegistryVO.getRegistryId() >> "50"
		1*tools.extractUtilMethod(pRegistryVO) >> regVo

		when:
		RegistryResVO resVo = tools.importRegistry(profile,pRegistryVO)

		then:
		1*pRegistryVO.setRegistryIdWS(50)
		resVo ==regVo
	}

	def"importRegistry, when when getRegistryId is null"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		RegistryVO pRegistryVO =Mock()
		RegistryResVO regVo =Mock()
		pRegistryVO.getRegistryId() >> null
		1*tools.extractUtilMethod(pRegistryVO) >> null

		when:
		RegistryResVO resVo = tools.importRegistry(profile,pRegistryVO)

		then:
		1*pRegistryVO.setRegistryIdWS(0)
		resVo ==null
	}

	def"forgotRegPasswordService, when getForgetPassRegistryId is not null"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		ForgetRegPassRequestVO forgotRegPassRequestVO =Mock()
		RegistryResVO regVo =Mock()
		forgotRegPassRequestVO.getForgetPassRegistryId() >> "50"
		1*tools.extractUtilMethod(forgotRegPassRequestVO) >> regVo

		when:
		RegistryResVO resVo = tools.forgotRegPasswordService(forgotRegPassRequestVO)

		then:
		1*forgotRegPassRequestVO.setRegistryIdWS(50)
		resVo ==regVo
	}

	def"forgotRegPasswordService, when getForgetPassRegistryId is null"(){

		given:
		setParametrsForSpy()
		Profile profile =Mock()
		ForgetRegPassRequestVO forgotRegPassRequestVO =Mock()
		forgotRegPassRequestVO.getForgetPassRegistryId() >> null
		1*tools.extractUtilMethod(forgotRegPassRequestVO) >> null

		when:
		RegistryResVO resVo = tools.forgotRegPasswordService(forgotRegPassRequestVO)

		then:
		1*forgotRegPassRequestVO.setRegistryIdWS(0)
		resVo ==null
	}

	def"fetchUsersSoonestOrRecent, Fetch users soonest or recent."(){

		given:
		RepositoryItem r1 =Mock()
		RepositoryItem r2= Mock()
		RepositoryItem r3= Mock()
		RepositoryItem r4= Mock()
		1*mRep.getItem("str1", "giftregistry") >> r1
		1*mRep.getItem("str2", "giftregistry") >> r2
		1*mRep.getItem("str3", "giftregistry") >> r3
		1*mRep.getItem("str4", "giftregistry") >> r4

		1*r1.getPropertyValue("eventDate") >> new Date()-1
		1*r2.getPropertyValue("eventDate") >> new Date() +1
		1*r3.getPropertyValue("eventDate") >> new Date()
		1*r4.getPropertyValue("eventDate") >> null

		when:
		String regId = tools.fetchUsersSoonestOrRecent(["str1","str2","str3","str4"])

		then:
		regId == "str3"
	}

	def"fetchUsersSoonestOrRecent, when eventDateDays is less than todaysDateDays"(){

		given:
		RepositoryItem r1 =Mock()
		1*mRep.getItem("str1", "giftregistry") >> r1
		1*r1.getPropertyValue("eventDate") >> new Date()-1

		when:
		String regId = tools.fetchUsersSoonestOrRecent(["str1"])

		then:
		regId == "str1"
	}

	def"getRegistryInfoFromDB, when regId is a validNumber"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		String registryId = "50"
		String siteId ="tbs"
		1*gsRep.getView("registryInfo") >> view
		1*tools.extractDBCall(_,_,view) >> [r1]

		when:
		RepositoryItem[] rep = tools.getRegistryInfoFromDB(registryId,siteId)

		then:
		rep == [r1]
	}

	def"getRegistryInfoFromDB, when regId is not a validNumber"(){

		given:
		setParametrsForSpy()
		String registryId = "abc"
		String siteId ="tbs"
		0*gsRep.getView("registryInfo")
		0*tools.extractDBCall(_,_,null)

		when:
		RepositoryItem[] rep = tools.getRegistryInfoFromDB(registryId,siteId)

		then:
		rep == null
	}

	def"getRegistryAddressesFromDB, when regId is a validNumber"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		String registryId = "50"
		1*gsRep.getView("regAddress") >> view
		1*tools.extractDBCall(_,_,view) >> [r1]

		when:
		RepositoryItem[] rep = tools.getRegistryAddressesFromDB(registryId)

		then:
		rep == [r1]
	}

	def"getRegistryAddressesFromDB, when regId is not a validNumber"(){

		given:
		setParametrsForSpy()
		String registryId = "abc"
		0*gsRep.getView("regAddress")
		0*tools.extractDBCall(_,_,null)

		when:
		RepositoryItem[] rep = tools.getRegistryAddressesFromDB(registryId)

		then:
		rep == null
	}

	def"getRegistrySkuDetailsFromDB, when regId is a validNumber"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		String registryId = "50"
		1*gsRep.getView("regDetail") >> view
		1*tools.extractDBCall(_,_,view) >> [r1]

		when:
		RepositoryItem[] rep = tools.getRegistrySkuDetailsFromDB(registryId)

		then:
		rep == [r1]
	}

	def"getRegistrySkuDetailsFromDB, when regId is not a validNumber"(){

		given:
		setParametrsForSpy()
		String registryId = "abc"
		0*gsRep.getView("regDetail")
		0*tools.extractDBCall(_,_,null)

		when:
		RepositoryItem[] rep = tools.getRegistrySkuDetailsFromDB(registryId)

		then:
		rep == null
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItems are not null,regView equals VIEW_ALL, registryItemsPersonalizedAndLtl are null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_ALL

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> "productId"
		1*catalogTools.getSalePrice("productId", "1") >> 100
		1*catalogTools.getIncartPrice("productId", "1") >> 200
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == "8"
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == "100.0"
		registryItemVO.getInCartPrice() == "200.0"
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItems are not null,regView equals VIEW_REMAINING, registryItemsPersonalizedAndLtl are null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> "productId"
		1*catalogTools.getSalePrice("productId", "1") >> 0
		1*catalogTools.getListPrice("productId", "1") >> 150.0
		1*catalogTools.getIncartPrice("productId", "1") >> 200
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 50
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == "8"
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == "150.0"
		registryItemVO.getInCartPrice() == "200.0"
	}

	def"fetchRegistryItemsFromEcomAdmin1, when registryItems are not null,regView equals VIEW_PURCHASED, registryItemsPersonalizedAndLtl are null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBSystemException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == "8"
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
	}

	def"fetchRegistryItemsFromEcomAdmin1, when registryItems are not null,regView equals defaultView"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView = BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBSystemException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() ==[]
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItems are not null,regView equals VIEW_REMAINING, Business and System Exceptions are thrown in populateRegistryItem method"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> [r2,r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r2.getPropertyValue(BBBCoreConstants.SKUID) >> 2
		r2.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r2.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("2", true) >> {throw new BBBBusinessException("")}
		1*catalogTools.getParentProductForSku("1", true) >> "productId"
		1*catalogTools.getSalePrice("productId", "1") >> 0
		1*catalogTools.getListPrice("productId", "1") >> 150.0
		1*catalogTools.getIncartPrice("productId", "1") >> 200
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs","2",_) >> {throw new BBBBusinessException("")}
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() == []
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItems are null, registryItemsPersonalizedAndLtl are not  null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_ALL

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.PERSONALIZATION_CODE) >> "PY"
		r3.getPropertyValue(BBBCoreConstants.PERSONALIZATION_DESC) >> "DESC"
		r3.getPropertyValue(BBBCoreConstants.IMAGE_URL) >> "URL"
		r3.getPropertyValue(BBBCoreConstants.IMAGE_URL_THUMB) >> "THUMB"
		r3.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL) >> "MOB_URL"
		r3.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL_THUMB) >> "MOB_THUMB"
		/*r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"*/
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "LTL"
		r3.getPropertyValue(BBBCoreConstants.CUSTOMIZATION_PRICE) >> new Double(500.0)
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		r3.getPropertyValue(BBBCoreConstants.PERSONALIZATION_PRICE) >> new Double(700.0)
		1*tools.calculatePersonalizedPrice(isGiftGiver, 500, 700, skuVO, "tbs") >> 800.0
		r3.getPropertyValue(BBBCoreConstants.LTL_DELIVERY_SERVICES) >> "ltlDelivery"
		r3.getPropertyValue(BBBCoreConstants.ASSEMBLY_SELECTED) >> "true"
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1_ltlDeliverytrue":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == ""
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
		registryItemVO.getPersonlisedDoublePrice() == 800.0
		registryItemVO.getPersonlisedPrice() == 800.0
		registryItemVO.getPersonalisedCode() == "PY"
		registryItemVO.getCustomizationDetails() == "DESC"
		registryItemVO.getRefNum() == ""
		registryItemVO.getPersonalizedImageUrls() == "URL"
		registryItemVO.getPersonalizedImageUrlThumbs() == "THUMB"
		registryItemVO.getPersonalizedMobImageUrls() == "MOB_URL"
		registryItemVO.getPersonalizedMobImageUrlThumbs() == "MOB_THUMB"
		registryItemVO.getItemType() == "LTL"
		registryItemVO.getLtlDeliveryServices() == "ltlDelivery"
		registryItemVO.getAssemblySelected() == "true"
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItemsPersonalizedAndLtl are not null,regView is VIEW_REMAINING and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		0*tools.calculatePersonalizedPrice(isGiftGiver, _, _, skuVO, "tbs")
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1_refNum":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 50
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == ""
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
		registryItemVO.getPersonlisedDoublePrice() == 0.0
		registryItemVO.getPersonlisedPrice() == 0.0
		registryItemVO.getPersonalisedCode() == ""
		registryItemVO.getCustomizationDetails() == ""
		registryItemVO.getRefNum() == "refNum"
		registryItemVO.getPersonalizedImageUrls() == ""
		registryItemVO.getPersonalizedImageUrlThumbs() == ""
		registryItemVO.getPersonalizedMobImageUrls() == ""
		registryItemVO.getPersonalizedMobImageUrlThumbs() == ""
		registryItemVO.getItemType() == "PER"
		registryItemVO.getLtlDeliveryServices() == ""
		registryItemVO.getAssemblySelected() == ""
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItemsPersonalizedAndLtl are not null,regView is VIEW_REMAINING,qtyRequested is less than qtyPurchased and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		0*tools.calculatePersonalizedPrice(isGiftGiver, _, _, skuVO, "tbs")
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() ==[]
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItemsPersonalizedAndLtl are not null,regView is VIEW_PURCHASED,qtyRequested is less than qtyPurchased and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		0*tools.calculatePersonalizedPrice(isGiftGiver, _, _, skuVO, "tbs")
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1_refNum":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == ""
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
		registryItemVO.getPersonlisedDoublePrice() == 0.0
		registryItemVO.getPersonlisedPrice() == 0.0
		registryItemVO.getPersonalisedCode() == ""
		registryItemVO.getCustomizationDetails() == ""
		registryItemVO.getRefNum() == "refNum"
		registryItemVO.getPersonalizedImageUrls() == ""
		registryItemVO.getPersonalizedImageUrlThumbs() == ""
		registryItemVO.getPersonalizedMobImageUrls() == ""
		registryItemVO.getPersonalizedMobImageUrlThumbs() == ""
		registryItemVO.getItemType() == "PER"
		registryItemVO.getLtlDeliveryServices() == ""
		registryItemVO.getAssemblySelected() == ""
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryItemsPersonalizedAndLtl are not null,regView is VIEW_PURCHASED,qtyRequested is greater than qtyPurchased and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		0*tools.calculatePersonalizedPrice(isGiftGiver, _, _, skuVO, "tbs")
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() ==[]
	}

	def"fetchRegistryItemsFromEcomAdmin, when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> {throw new RepositoryException("")}
		0*tools.extractDBCall(_, _, view1)
		0*tools.extractSiteId()

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList == null
		BBBBusinessException e = thrown()
	}

	def"fetchRegistryItemsFromEcomAdmin, when ParsingException is thrown "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		tools.extractSiteId() >> "tbs"
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 98568988L
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 201601252L
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		tools.extractSiteId() >> "tbs"
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		repList == null
		BBBBusinessException e = thrown()
	}

	def"fetchRegistryItemsFromEcomAdmin, when registryID is not valid "(){

		given:
		setParametrsForSpy()
		String registryId = ""
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_PURCHASED

		0*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAILOWNER)
		0*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2OWNER)
		0*tools.extractDBCall(_, _, view)
		0*tools.extractDBCall(_, _, view1)
		0*tools.extractSiteId()

		when:
		RegistryItemsListVO list = tools.fetchRegistryItemsFromEcomAdmin(registryId,isGiftGiver,regView)

		then:
		list.getSkuRegItemVOMap() == null
		list.getRegistryItemList() ==  null
	}

	def"getRegistryInfo, when service call throws error "(){

		given:
		setParametrsForSpy()
		RegistryReqVO registryReqVO = Mock()
		RepositoryItem profileItem = Mock()
		ServiceErrorVO vo =Mock()
		RegistryResVO resVo =Mock()

		1*tools.extractUserProfile() >> profileItem
		profileItem.getRepositoryId() >> "repId"
		profileItem.getPropertyValue("email") >>"userEmailID"

		registryReqVO.getRegistryId() >> "67"
		1*tools.extractUtilMethod(registryReqVO) >> resVo
		resVo.getServiceErrorVO() >> vo
		vo.isErrorExists() >>  true
		vo.getErrorDisplayMessage() >> "error"

		when:
		RegistryResVO resVo1 = tools.getRegistryInfo(registryReqVO)

		then:
		resVo1 == resVo
		1*tools.logError("CLS=[GiftRegistryTools]/MTHD=[getRegistryInfo]/ Error =["
				+ " RegistryID = "+ registryReqVO.getRegistryId()
				+ " |ProfileID = "+ registryReqVO.getProfileId()
				+ " |EmailID = "+ registryReqVO.getEmailId()
				+ " |ErrorID = "+ +vo.getErrorId()
				+ " |ErrorMessag ="+ vo.getErrorDisplayMessage())
	}

	def"getRegistryInfo, when service call throws error and errorDisplayMessage is empty"(){

		given:
		setParametrsForSpy()
		RegistryReqVO registryReqVO = Mock()
		RepositoryItem profileItem = Mock()
		ServiceErrorVO vo =Mock()
		RegistryResVO resVo =Mock()

		1*tools.extractUserProfile() >> profileItem
		profileItem.getRepositoryId() >> "repId"
		profileItem.getPropertyValue("email") >>"userEmailID"

		registryReqVO.getRegistryId() >> "67"
		1*tools.extractUtilMethod(registryReqVO) >> resVo
		resVo.getServiceErrorVO() >> vo
		vo.isErrorExists() >>  true
		vo.getErrorDisplayMessage() >> ""

		when:
		RegistryResVO resVo1 = tools.getRegistryInfo(registryReqVO)

		then:
		resVo1 == resVo
	}

	def"getRegistryInfo, whens isErrorExists is false"(){

		given:
		setParametrsForSpy()
		RegistryReqVO registryReqVO = Mock()
		RepositoryItem profileItem = Mock()
		ServiceErrorVO vo =Mock()
		RegistryResVO resVo =Mock()

		1*tools.extractUserProfile() >> profileItem
		profileItem.getRepositoryId() >> "repId"
		profileItem.getPropertyValue("email") >>"userEmailID"

		registryReqVO.getRegistryId() >> "67"
		1*tools.extractUtilMethod(registryReqVO) >> resVo
		resVo.getServiceErrorVO() >> vo
		vo.isErrorExists() >>  false
		vo.getErrorDisplayMessage() >> ""

		when:
		RegistryResVO resVo1 = tools.getRegistryInfo(registryReqVO)

		then:
		resVo1 == resVo
	}

	def"getRegistryInfo, whens getServiceErrorVO is null"(){

		given:
		setParametrsForSpy()
		RegistryReqVO registryReqVO = Mock()
		RepositoryItem profileItem = Mock()
		ServiceErrorVO vo =Mock()
		RegistryResVO resVo =Mock()

		1*tools.extractUserProfile() >> profileItem
		profileItem.getRepositoryId() >> "repId"
		profileItem.getPropertyValue("email") >>"userEmailID"

		registryReqVO.getRegistryId() >> "67"
		1*tools.extractUtilMethod(registryReqVO) >> resVo
		resVo.getServiceErrorVO() >> null

		when:
		RegistryResVO resVo1 = tools.getRegistryInfo(registryReqVO)

		then:
		resVo1 == resVo
	}

	def"getRegistryInfo, whens registryID is not valid"(){

		given:
		setParametrsForSpy()
		setParametrsForSpy()
		RegistryReqVO registryReqVO = Mock()
		RepositoryItem profileItem = Mock()
		RegistryResVO resVo =Mock()

		1*tools.extractUserProfile() >> profileItem
		profileItem.getRepositoryId() >> "repId"
		profileItem.getPropertyValue("email") >>"userEmailID"

		registryReqVO.getRegistryId() >> ""
		0*tools.extractUtilMethod(registryReqVO) >> resVo

		when:
		RegistryResVO resVo1 = tools.getRegistryInfo(registryReqVO)

		then:
		resVo1 == null
	}

	def"getRegistryInfo, whens registryReqVO is null"(){

		given:
		setParametrsForSpy()
		RepositoryItem profileItem = Mock()
		ServiceErrorVO vo =Mock()
		RegistryResVO resVo =Mock()

		1*tools.extractUserProfile() >> null
		0*tools.extractUtilMethod(_)

		when:
		RegistryResVO resVo1 = tools.getRegistryInfo(null)

		then:
		resVo1 == null
	}

	def"fetchRegistryItems,invoke web service to fetch registry items and calls personlizeImageUrlMethod"(){

		given:
		setParametrsForSpy()
		RegistryItemsListVO registryItemsListVO = Mock()
		RegistrySearchVO pRegistrySearchVO =Mock()

		1*tools.extractUtilMethod(pRegistrySearchVO) >> registryItemsListVO
		pRegistrySearchVO.getGiftGiver() >> true

		//for personlizeImageUrl
		RegistryItemVO vo1 =Mock()
		RegistryItemVO vo2 =Mock()
		RegistryItemVO vo3 =Mock()
		RegistryItemVO vo4 =Mock()
		RegistryItemVO vo5 =Mock()
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBCoreConstants.MODERATED) >> ["str"]
		registryItemsListVO.getRegistryItemList() >> [vo1,vo2,vo3,vo4,vo5]

		vo1.getPersonalizedImageUrls() >> "url."
		vo1.getPersonalizedImageUrlThumbs() >> "thumb_url."
		vo1.getRefNum() >> "1"

		vo2.getPersonalizedImageUrls() >> "url"
		vo2.getPersonalizedImageUrlThumbs() >> "thumb_url1"
		vo2.getRefNum() >> "2"

		vo3.getPersonalizedImageUrls() >> "str"
		vo3.getPersonalizedImageUrlThumbs() >> "str"
		vo3.getRefNum() >> "3"

		vo4.getPersonalizedImageUrls() >> ""
		vo4.getPersonalizedImageUrlThumbs() >> ""
		vo4.getRefNum() >> "4"

		vo5.getRefNum() >> ""
		// end personlizeImageUrl

		when:
		RegistryItemsListVO regVoList = tools.fetchRegistryItems(pRegistrySearchVO)

		then:
		regVoList == registryItemsListVO
		1*vo1.setPersonalizedImageUrls("urlstr.")
		1*vo2.setPersonalizedImageUrls("urlstr")
		1*vo1.setPersonalizedImageUrlThumbs("thumb_urlstr.")
		1*vo2.setPersonalizedImageUrlThumbs("thumb_url1str")
	}

	def"fetchRegistryItems,when user is not giftGiver"(){

		given:
		setParametrsForSpy()
		RegistryItemsListVO registryItemsListVO = Mock()
		RegistrySearchVO pRegistrySearchVO =Mock()

		1*tools.extractUtilMethod(pRegistrySearchVO) >> registryItemsListVO
		pRegistrySearchVO.getGiftGiver() >> false

		when:
		RegistryItemsListVO regVoList = tools.fetchRegistryItems(pRegistrySearchVO)

		then:
		regVoList == registryItemsListVO
		0*tools.personlizeImageUrl(registryItemsListVO)
	}

	def"fetchRegistryItems,invoke web service to fetch registry items and calls personlizeImageUrlMethod with registryItemList empty"(){

		given:
		setParametrsForSpy()
		RegistryItemsListVO registryItemsListVO = Mock()
		RegistrySearchVO pRegistrySearchVO =Mock()

		1*tools.extractUtilMethod(pRegistrySearchVO) >> registryItemsListVO
		pRegistrySearchVO.getGiftGiver() >> true
		//for personlizeImageUrl
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBCoreConstants.MODERATED) >> []
		registryItemsListVO.getRegistryItemList() >> null
		// end personlizeImageUrl

		when:
		RegistryItemsListVO regVoList = tools.fetchRegistryItems(pRegistrySearchVO)

		then:
		regVoList == registryItemsListVO
	}

	def"personlizeImageUrl,when registryItemsListVO is empty"(){

		given:
		RegistryItemsListVO registryItemsListVO = Mock()

		when:
		tools.personlizeImageUrl(null)

		then:
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBCoreConstants.MODERATED) >> []
	}

	def"fetchRegistryItemsFromRepo,when regView is VIEW_REMAINING"(){

		given:
		setParametrsForSpy()
		String registryId = "1"
		String regView=BBBCoreConstants.VIEW_REMAINING
		RepositoryItem item =Mock()
		RepositoryItem item1=Mock()
		1*gsRep.getView("regDetail") >> view
		1*tools.extractDBCall(_, _, view) >> [item, item1]

		item.getPropertyValue("qtyRequested") >> 60
		item.getPropertyValue("qtyFulfilled") >> 20
		item.getPropertyValue("qtyWebPurchased") >> 30
		item.getPropertyValue("skuId") >> 5
		item.getPropertyValue("ROWID") >> "row"

		item1.getPropertyValue("qtyRequested") >> 10
		item1.getPropertyValue("qtyFulfilled") >> 20
		item1.getPropertyValue("qtyWebPurchased") >> 30


		when:
		RegistryItemsListVO vo =tools.fetchRegistryItemsFromRepo(registryId,regView)

		then:
		vo.getTotEntries() == 1
		RegistryItemVO registryItemVO =vo.getRegistryItemList().get(0)
		vo.getSkuRegItemVOMap().size() ==1
		registryItemVO.getQtyRequested() == 60
		registryItemVO.getQtyFulfilled() == 20
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 50
		registryItemVO.getSku() == 5
		registryItemVO.getRowID() == "row"
	}

	def"fetchRegistryItemsFromRepo,when regView is VIEW_ALL"(){

		given:
		setParametrsForSpy()
		String registryId = "1"
		String regView=BBBCoreConstants.VIEW_ALL
		RepositoryItem item =Mock()
		1*gsRep.getView("regDetail") >> view
		1*tools.extractDBCall(_, _, view) >> [item]

		item.getPropertyValue("qtyRequested") >> 60
		item.getPropertyValue("qtyFulfilled") >> 20
		item.getPropertyValue("qtyWebPurchased") >> 30
		item.getPropertyValue("skuId") >> 5
		item.getPropertyValue("ROWID") >> "row"

		when:
		RegistryItemsListVO vo =tools.fetchRegistryItemsFromRepo(registryId,regView)

		then:
		vo.getTotEntries() == 1
		RegistryItemVO registryItemVO =vo.getRegistryItemList().get(0)
		vo.getSkuRegItemVOMap().size() ==1
		registryItemVO.getQtyRequested() == 60
		registryItemVO.getQtyFulfilled() == 20
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 50
		registryItemVO.getSku() == 5
		registryItemVO.getRowID() == "row"
	}

	def"fetchRegistryItemsFromRepo,when regView is VIEW_PURCHASED"(){

		given:
		setParametrsForSpy()
		String registryId = "1"
		String regView=BBBCoreConstants.VIEW_PURCHASED
		RepositoryItem item =Mock()
		RepositoryItem item1=Mock()
		1*gsRep.getView("regDetail") >> view
		1*tools.extractDBCall(_, _, view) >> [item,item1]

		item.getPropertyValue("qtyRequested") >> 10
		item.getPropertyValue("qtyFulfilled") >> 20
		item.getPropertyValue("qtyWebPurchased") >> 30
		item.getPropertyValue("skuId") >> 5
		item.getPropertyValue("ROWID") >> "row"

		item1.getPropertyValue("qtyRequested") >> 60
		item1.getPropertyValue("qtyFulfilled") >> 20
		item1.getPropertyValue("qtyWebPurchased") >> 30

		when:
		RegistryItemsListVO vo =tools.fetchRegistryItemsFromRepo(registryId,regView)

		then:
		vo.getTotEntries() == 1
		RegistryItemVO registryItemVO =vo.getRegistryItemList().get(0)
		vo.getSkuRegItemVOMap().size() ==1
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyFulfilled() == 20
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 50
		registryItemVO.getSku() == 5
		registryItemVO.getRowID() == "row"
	}

	def"fetchRegistryItemsFromRepo,throws RepositoryException"(){

		given:
		setParametrsForSpy()
		String registryId = "1"
		String regView=BBBCoreConstants.VIEW_PURCHASED
		1*gsRep.getView("regDetail") >> view
		1*tools.extractDBCall(_, _, view) >> {throw new RepositoryException("error")}

		when:
		RegistryItemsListVO vo =tools.fetchRegistryItemsFromRepo(registryId,regView)

		then:
		vo.getSkuRegItemVOMap() == null
		vo.getTotEntries() ==0
		vo.getRegistryItemList() ==null
		1*tools.logError("error", _)

	}

	def"fetchRegistryItemsFromRepo,when registryItems are null"(){

		given:
		setParametrsForSpy()
		String registryId = "1"
		String regView=BBBCoreConstants.VIEW_PURCHASED
		1*gsRep.getView("regDetail") >> view
		1*tools.extractDBCall(_, _, view) >> null

		when:
		RegistryItemsListVO vo =tools.fetchRegistryItemsFromRepo(registryId,regView)

		then:
		vo.getSkuRegItemVOMap() == [:]
		vo.getTotEntries() ==0
		vo.getRegistryItemList() ==[]

	}

	def"fetchRegistryItemsFromRepo,when registryId is not valid"(){

		given:
		setParametrsForSpy()
		String registryId = ""
		String regView=BBBCoreConstants.VIEW_PURCHASED
		0*gsRep.getView("regDetail")
		0*tools.extractDBCall(_, _, null)

		when:
		RegistryItemsListVO vo =tools.fetchRegistryItemsFromRepo(registryId,regView)

		then:
		vo.getSkuRegItemVOMap() == null
		vo.getTotEntries() ==0
		vo.getRegistryItemList() ==null
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItems are not null,regView equals VIEW_ALL, registryItemsPersonalizedAndLtl are null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_ALL

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> "productId"
		1*catalogTools.getSalePrice("productId", "1") >> 100
		1*catalogTools.getIncartPrice("productId", "1") >> 200
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == "8"
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == "100.0"
		registryItemVO.getInCartPrice() == "200.0"
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItems are not null,regView equals VIEW_REMAINING, registryItemsPersonalizedAndLtl are null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> "productId"
		1*catalogTools.getSalePrice("productId", "1") >> 0
		1*catalogTools.getListPrice("productId", "1") >> 150.0
		1*catalogTools.getIncartPrice("productId", "1") >> 200
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 50
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == "8"
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == "150.0"
		registryItemVO.getInCartPrice() == "200.0"
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItems are not null,regView equals VIEW_PURCHASED, registryItemsPersonalizedAndLtl are null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBSystemException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == "8"
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItems are not null,regView equals defaultView"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView = BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> [r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		1*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBSystemException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() ==[]
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItems are not null,regView equals VIEW_REMAINING, Business and System Exceptions are thrown in populateRegistryItem method"(){

		given:
		setParametrsForSpy()
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> [r2,r1]
		1*tools.extractDBCall(_, _, view1) >> null

		//for populateRegistryItem
		r2.getPropertyValue(BBBCoreConstants.SKUID) >> 2
		r2.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r2.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r1.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r1.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r1.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r1.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r1.getPropertyValue(BBBCoreConstants.ROWID) >> "8"
		r1.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"

		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("2", true) >> {throw new BBBBusinessException("")}
		1*catalogTools.getParentProductForSku("1", true) >> "productId"
		1*catalogTools.getSalePrice("productId", "1") >> 0
		1*catalogTools.getListPrice("productId", "1") >> 150.0
		1*catalogTools.getIncartPrice("productId", "1") >> 200
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs","2",_) >> {throw new BBBBusinessException("")}
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() == []
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItems are null, registryItemsPersonalizedAndLtl are not  null"(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		boolean isGiftGiver = true
		String regView =BBBCoreConstants.VIEW_ALL

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.PERSONALIZATION_CODE) >> "PY"
		r3.getPropertyValue(BBBCoreConstants.PERSONALIZATION_DESC) >> "DESC"
		r3.getPropertyValue(BBBCoreConstants.IMAGE_URL) >> "URL"
		r3.getPropertyValue(BBBCoreConstants.IMAGE_URL_THUMB) >> "THUMB"
		r3.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL) >> "MOB_URL"
		r3.getPropertyValue(BBBCoreConstants.MOB_IMAGE_URL_THUMB) >> "MOB_THUMB"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "LTL"
		r3.getPropertyValue(BBBCoreConstants.CUSTOMIZATION_PRICE) >> new Double(500.0)
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		r3.getPropertyValue(BBBCoreConstants.PERSONALIZATION_PRICE) >> new Double(700.0)
		1*tools.calculatePersonalizedPrice(isGiftGiver, 500, 700, skuVO, "tbs") >> 800.0
		r3.getPropertyValue(BBBCoreConstants.LTL_DELIVERY_SERVICES) >> "ltlDelivery"
		r3.getPropertyValue(BBBCoreConstants.ASSEMBLY_SELECTED) >> "true"
		//end populatePersonalizedAndLtlRegistryItem

		1*tools.personlizeImageUrl(_) >> {}

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1_ltlDeliverytrue":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == ""
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
		registryItemVO.getPersonlisedDoublePrice() == 800.0
		registryItemVO.getPersonlisedPrice() == 800.0
		registryItemVO.getPersonalisedCode() == "PY"
		registryItemVO.getCustomizationDetails() == "DESC"
		registryItemVO.getRefNum() == ""
		registryItemVO.getPersonalizedImageUrls() == "URL"
		registryItemVO.getPersonalizedImageUrlThumbs() == "THUMB"
		registryItemVO.getPersonalizedMobImageUrls() == "MOB_URL"
		registryItemVO.getPersonalizedMobImageUrlThumbs() == "MOB_THUMB"
		registryItemVO.getItemType() == "LTL"
		registryItemVO.getLtlDeliveryServices() == "ltlDelivery"
		registryItemVO.getAssemblySelected() == "true"
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItemsPersonalizedAndLtl are not null,regView is VIEW_REMAINING and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		//end populatePersonalizedAndLtlRegistryItem
		1*tools.personlizeImageUrl(_) >> {}

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1_refNum":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 50
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == ""
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
		registryItemVO.getPersonlisedDoublePrice() == 0.0
		registryItemVO.getPersonlisedPrice() == 0.0
		registryItemVO.getPersonalisedCode() == ""
		registryItemVO.getCustomizationDetails() == ""
		registryItemVO.getRefNum() == "refNum"
		registryItemVO.getPersonalizedImageUrls() == ""
		registryItemVO.getPersonalizedImageUrlThumbs() == ""
		registryItemVO.getPersonalizedMobImageUrls() == ""
		registryItemVO.getPersonalizedMobImageUrlThumbs() == ""
		registryItemVO.getItemType() == "PER"
		registryItemVO.getLtlDeliveryServices() == ""
		registryItemVO.getAssemblySelected() == ""
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItemsPersonalizedAndLtl are not null,regView is VIEW_REMAINING,qtyRequested is less than qtyPurchased and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_REMAINING

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		//end populatePersonalizedAndLtlRegistryItem
		1*tools.personlizeImageUrl(_) >> {}

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() ==[]
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItemsPersonalizedAndLtl are not null,regView is VIEW_PURCHASED,qtyRequested is less than qtyPurchased and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 10
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 1
		repList.getSkuRegItemVOMap() == ["1_refNum":repList.getRegistryItemList().get(0)]
		RegistryItemVO registryItemVO = repList.getRegistryItemList().get(0)
		registryItemVO.getQtyFulfilled() == 5
		registryItemVO.getQtyRequested() == 10
		registryItemVO.getQtyWebPurchased() == 30
		registryItemVO.getQtyPurchased() == 35
		registryItemVO.getRowID() == ""
		registryItemVO.getSku() == 1
		registryItemVO.getLastMaintained() !=null
		registryItemVO.getCreateTimestamp() !=null
		registryItemVO.getPrice() == null
		registryItemVO.getInCartPrice() == null
		registryItemVO.getPersonlisedDoublePrice() == 0.0
		registryItemVO.getPersonlisedPrice() == 0.0
		registryItemVO.getPersonalisedCode() == ""
		registryItemVO.getCustomizationDetails() == ""
		registryItemVO.getRefNum() == "refNum"
		registryItemVO.getPersonalizedImageUrls() == ""
		registryItemVO.getPersonalizedImageUrlThumbs() == ""
		registryItemVO.getPersonalizedMobImageUrls() == ""
		registryItemVO.getPersonalizedMobImageUrlThumbs() == ""
		registryItemVO.getItemType() == "PER"
		registryItemVO.getLtlDeliveryServices() == ""
		registryItemVO.getAssemblySelected() == ""
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryItemsPersonalizedAndLtl are not null,regView is VIEW_PURCHASED,qtyRequested is greater than qtyPurchased and itemType is not LTL "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		SKUDetailVO skuVO =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 20160125215020
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 20160125215025
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		2*tools.extractSiteId() >> "tbs"
		//for setPriceInRegItem
		1*catalogTools.getParentProductForSku("1", true) >> {throw new BBBBusinessException("")}
		//end setPriceInRegItem
		1*tools.getSKUDetailsWithProductId("tbs", "1",_) >> skuVO
		//end populateRegistryItem

		r3.getPropertyValue(BBBCoreConstants.QTY_REQUESTED) >> 50
		r3.getPropertyValue(BBBCoreConstants.QTY_FULFILLED) >> 5
		r3.getPropertyValue(BBBCoreConstants.QTY_WEBPURCHASED) >> 30
		r3.getPropertyValue(BBBCoreConstants.ITEM_TYPE) >> "PER"
		r3.getPropertyValue(BBBCoreConstants.REFNUM) >> "refNum"
		1*tools.getSKUDetailsWithProductId("tbs","1",_) >> skuVO
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList.getTotEntries() == 0
		repList.getSkuRegItemVOMap() == [:]
		repList.getRegistryItemList() ==[]
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when RepositoryException is thrown"(){

		given:
		setParametrsForSpy()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> {throw new RepositoryException("")}
		0*tools.extractDBCall(_, _, view1)
		0*tools.extractSiteId()

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList == null
		BBBBusinessException e = thrown()
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when ParsingException is thrown "(){

		given:
		setParametrsForSpy()
		RepositoryItem r3 =Mock()
		String registryId = "40"
		String regView =BBBCoreConstants.VIEW_PURCHASED

		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL) >> view
		1*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2) >> view1
		1*tools.extractDBCall(_, _, view) >> null
		1*tools.extractDBCall(_, _, view1) >> [r3]

		//for populatePersonalizedAndLtlRegistryItem
		//for populateRegistryItem
		tools.extractSiteId() >> "tbs"
		r3.getPropertyValue(BBBCoreConstants.LAST_MAINTAINED) >> 98568988L
		r3.getPropertyValue(BBBCoreConstants.CREATE_TIMESTAMP) >> 201601252L
		r3.getPropertyValue(BBBCoreConstants.SKUID) >> 1
		tools.extractSiteId() >> "tbs"
		//end populatePersonalizedAndLtlRegistryItem

		when:
		RegistryItemsListVO repList = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		repList == null
		BBBBusinessException e = thrown()
	}

	def"fetchRegistryItemsFromEcomAdmin(overloaded), when registryID is not valid "(){

		given:
		setParametrsForSpy()
		String registryId = ""
		String regView =BBBCoreConstants.VIEW_PURCHASED

		0*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL)
		0*gsRep.getView(BBBCoreConstants.ITEM_DESC_REG_DETAIL2)
		0*tools.extractDBCall(_, _, view)
		0*tools.extractDBCall(_, _, view1)
		0*tools.extractSiteId()

		when:
		RegistryItemsListVO list = tools.fetchRegistryItemsFromEcomAdmin(registryId,regView)

		then:
		list.getSkuRegItemVOMap() == null
		list.getRegistryItemList() ==  null
	}

	def"getSKUDetailsWithProductId, include sku's parent product Id in the response"(){

		given:
		setParametrsForSpy()
		String siteId = "tbs"
		String skuId ="10"
		RegistryItemVO registryItemVO =Mock()
		RepositoryItem productRepoItem =Mock()
		SKUDetailVO skuVO =Mock()
		RepositoryItem rItem =Mock()

		skuVO.getDisplayName() >> "SKU"
		1*tools.extractSiteId() >> "tbs"
		1*catalogTools.getSKUDetails("tbs", skuId.toString()) >> skuVO
		1*catalogTools.getParentProductItemForSku(skuId) >> productRepoItem
		skuVO.getSkuRepositoryItem() >> rItem
		rItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
		1*inventoryManager.getProductAvailability(siteId, skuId,BBBInventoryManager.PRODUCT_DISPLAY, 0) >> 0


		productRepoItem.getPropertyValue(BBBCatalogConstants.ID) >> "productId"
		rItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) >> "seo"

		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB

		when:
		SKUDetailVO sku = tools.getSKUDetailsWithProductId(siteId,skuId,registryItemVO)

		then:
		sku == skuVO
		1*skuVO.setDisableFlag(false)
		1*skuVO.setSkuInStock(true)
		1*skuVO.setParentProdId("productId")
		1*registryItemVO.setProductURL("seo"+ BBBInternationalShippingConstants.SKU_ID_IN_URL+ skuId)
		1*skuVO.setDisplayURL("SKU")
	}

	def"getSKUDetailsWithProductId, when RegistryItemVO is null"(){

		given:
		setParametrsForSpy()
		String siteId = "tbs"
		String skuId ="10"
		RegistryItemVO registryItemVO =Mock()
		RepositoryItem productRepoItem =Mock()
		SKUDetailVO skuVO =Mock()
		RepositoryItem rItem =Mock()

		skuVO.getDisplayName() >> "SKU"
		1*tools.extractSiteId() >> "tbs"
		1*catalogTools.getSKUDetails("tbs", skuId.toString()) >> skuVO
		1*catalogTools.getParentProductItemForSku(skuId) >> productRepoItem
		skuVO.getSkuRepositoryItem() >> rItem
		rItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
		1*inventoryManager.getProductAvailability(siteId, skuId,BBBInventoryManager.PRODUCT_DISPLAY, 0) >> 2


		productRepoItem.getPropertyValue(BBBCatalogConstants.ID) >> "productId"
		rItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) >> "seo"

		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEWEB

		when:
		SKUDetailVO sku = tools.getSKUDetailsWithProductId(siteId,skuId,null)

		then:
		sku == skuVO
		1*skuVO.setDisableFlag(false)
		1*skuVO.setSkuInStock(true)
		1*skuVO.setParentProdId("productId")
		1*skuVO.setDisplayURL("SKU")
	}

	def"getSKUDetailsWithProductId, when sku is not in stock"(){

		given:
		setParametrsForSpy()
		String siteId = "tbs"
		String skuId ="10"
		RegistryItemVO registryItemVO =Mock()
		RepositoryItem productRepoItem =Mock()
		SKUDetailVO skuVO =Mock()
		RepositoryItem rItem =Mock()

		skuVO.getDisplayName() >> "SKU"
		1*tools.extractSiteId() >> "tbs"
		1*catalogTools.getSKUDetails("tbs", skuId.toString()) >> skuVO
		1*catalogTools.getParentProductItemForSku(skuId) >> productRepoItem
		skuVO.getSkuRepositoryItem() >> rItem
		rItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
		1*inventoryManager.getProductAvailability(siteId, skuId,BBBInventoryManager.PRODUCT_DISPLAY, 0) >> 10


		productRepoItem.getPropertyValue(BBBCatalogConstants.ID) >> "productId"
		rItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) >> null

		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> BBBCoreConstants.MOBILEAPP

		when:
		SKUDetailVO sku = tools.getSKUDetailsWithProductId(siteId,skuId,null)

		then:
		sku == skuVO
		1*skuVO.setDisableFlag(false)
		0*skuVO.setSkuInStock(true)
		1*skuVO.setParentProdId("productId")
		1*skuVO.setDisplayURL("SKU")
	}

	def"getSKUDetailsWithProductId, when skuWebOffered is null"(){

		given:
		setParametrsForSpy()
		String siteId = "tbs"
		String skuId ="10"
		RegistryItemVO registryItemVO =Mock()
		RepositoryItem productRepoItem =Mock()
		SKUDetailVO skuVO =Mock()
		RepositoryItem rItem =Mock()

		skuVO.getDisplayName() >> "SKU"
		1*tools.extractSiteId() >> "tbs"
		1*catalogTools.getSKUDetails("tbs", skuId.toString()) >> skuVO
		1*catalogTools.getParentProductItemForSku(skuId) >> productRepoItem
		skuVO.getSkuRepositoryItem() >> rItem
		rItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
		1*inventoryManager.getProductAvailability(siteId, skuId,BBBInventoryManager.PRODUCT_DISPLAY, 0) >> {throw new BBBBusinessException("")}


		productRepoItem.getPropertyValue(BBBCatalogConstants.ID) >> "productId"
		rItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> null
		productRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) >> null

		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""

		when:
		SKUDetailVO sku = tools.getSKUDetailsWithProductId(siteId,skuId,null)

		then:
		sku == skuVO
		1*skuVO.setDisableFlag(false)
		0*skuVO.setSkuInStock(true)
		1*skuVO.setParentProdId("productId")
		0*skuVO.setDisplayURL("SKU")
		1*tools.logDebug("Product not available", _)
	}

	def"getSKUDetailsWithProductId, when productWebOffered is null"(){

		given:
		setParametrsForSpy()
		String siteId = "tbs"
		String skuId ="10"
		RegistryItemVO registryItemVO =Mock()
		RepositoryItem productRepoItem =Mock()
		SKUDetailVO skuVO =Mock()
		RepositoryItem rItem =Mock()

		skuVO.getDisplayName() >> "SKU"
		1*tools.extractSiteId() >> "tbs"
		1*catalogTools.getSKUDetails("tbs", skuId.toString()) >> skuVO
		1*catalogTools.getParentProductItemForSku(skuId) >> productRepoItem
		skuVO.getSkuRepositoryItem() >> rItem
		rItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> false
		1*inventoryManager.getProductAvailability(siteId, skuId,BBBInventoryManager.PRODUCT_DISPLAY, 0) >> {throw new BBBBusinessException("")}


		productRepoItem.getPropertyValue(BBBCatalogConstants.ID) >> "productId"
		rItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PROPERTY_NAME) >> true
		productRepoItem.getPropertyValue(BBBCatalogConstants.WEB_OFFERED_PRODUCT_PROPERTY_NAME) >> null
		productRepoItem.getPropertyValue(BBBCatalogConstants.SEO_URL_PROD_RELATION_PROPERTY_NAME) >> null

		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""

		when:
		SKUDetailVO sku = tools.getSKUDetailsWithProductId(siteId,skuId,null)

		then:
		sku == skuVO
		1*skuVO.setDisableFlag(false)
		0*skuVO.setSkuInStock(true)
		1*skuVO.setParentProdId("productId")
		0*skuVO.setDisplayURL("SKU")
		1*tools.logDebug("Product not available", _)
	}

	def"getSKUDetailsWithProductId, when productRepoItem is null"(){

		given:
		setParametrsForSpy()
		String siteId = "tbs"
		String skuId ="10"
		RegistryItemVO registryItemVO =Mock()
		RepositoryItem productRepoItem =Mock()
		SKUDetailVO skuVO =Mock()
		RepositoryItem rItem =Mock()

		skuVO.isDisableFlag() >> true
		skuVO.getDisplayName() >> "SKU"
		1*tools.extractSiteId() >> "tbs"
		1*catalogTools.getSKUDetails("tbs", skuId.toString()) >> skuVO
		1*catalogTools.getParentProductItemForSku(skuId) >> null
		skuVO.getSkuRepositoryItem() >> rItem
		rItem.getPropertyValue(BBBCatalogConstants.DISABLE_PROPERTY_NAME) >> true
		requestMock.getHeader(BBBCoreConstants.CHANNEL) >> ""

		when:
		SKUDetailVO sku = tools.getSKUDetailsWithProductId(siteId,skuId,null)

		then:
		sku == skuVO
		1*skuVO.setDisableFlag(true)
		0*skuVO.setSkuInStock(true)
		0*skuVO.setParentProdId(null)
		0*skuVO.setDisplayURL("SKU")
	}

	def"linkCoRegistrantToRegistries, when getRegistryId is empty"(){

		given:
		setParametrsForSpy()
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		String email ="email"
		String profileId ="pro"
		regReqVO.getRegistryId() >> ""
		1*tools.extractUtilMethod(regReqVO) >> linkCoRegToRegVO

		when:
		RegistryResVO resVo = tools.linkCoRegistrantToRegistries(regReqVO,email,profileId)

		then:
		resVo == linkCoRegToRegVO
		1*regReqVO.setRegistryIdWS(0)
	}

	def"linkCoRegistrantToRegistries, when getRegistryId is  not empty"(){

		given:
		setParametrsForSpy()
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		String email ="email"
		String profileId ="pro"
		regReqVO.getRegistryId() >> "50"
		1*tools.extractUtilMethod(regReqVO) >> linkCoRegToRegVO

		when:
		RegistryResVO resVo= tools.linkCoRegistrantToRegistries(regReqVO,email,profileId)

		then:
		resVo == linkCoRegToRegVO
		1*regReqVO.setRegistryIdWS(50)
	}

	def"linkCoRegProfileToReg, when regItemsWSCall is true"(){

		given:
		setParametrsForSpy()
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		boolean regItemsWSCall = true
		1*tools.extractUtilMethod(regReqVO) >> linkCoRegToRegVO

		when:
		RegistryResVO resVo= tools.linkCoRegProfileToReg(regReqVO,regItemsWSCall)

		then:
		resVo == linkCoRegToRegVO
	}

	def"linkCoRegProfileToReg, when regItemsWSCall is false"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		CallableStatement callableStmt =Mock()
		boolean regItemsWSCall = false
		regReqVO.getSiteId() >> "1"

		1*gsRep.getDataSource() >>DS
		DS.getConnection() >>con
		1*con.prepareCall(BBBGiftRegistryConstants.UPD_COREG_PROFILE_BY_EMAIL) >> callableStmt

		1* catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> {throw new BBBSystemException("")}
		1* catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> {throw new BBBSystemException("")}
		1* catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]

		when:
		RegistryResVO resVo= tools.linkCoRegProfileToReg(regReqVO,regItemsWSCall)

		then:
		resVo.getServiceErrorVO() != null
		1*callableStmt.setString(1, regReqVO.getEmailId());
		1*callableStmt.setString(2, regReqVO.getProfileId());
		1*callableStmt.setLong(3, 0)
		1*callableStmt.setString(4,BBBGiftRegistryConstants.US_COM);
		1*callableStmt.setString(5, "str")
		1*callableStmt.registerOutParameter(6, OracleTypes.CURSOR);
		1*callableStmt.registerOutParameter(7, OracleTypes.VARCHAR);
		1*callableStmt.registerOutParameter(8, OracleTypes.VARCHAR);
		1*tools.extractDBCall(callableStmt) >> {}
		1*con.close()
		1*callableStmt.close()
	}

	def"linkCoRegProfileToReg, when SQLException is thrown"(){

		given:
		setParametrsForSpy()
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		CallableStatement callableStmt =Mock()
		ServiceErrorVO errorVO =Mock()
		boolean regItemsWSCall = false
		regReqVO.getSiteId() >> "1"

		1*gsRep.getDataSource() >>DS
		DS.getConnection() >>con
		1*con.prepareCall(BBBGiftRegistryConstants.UPD_COREG_PROFILE_BY_EMAIL) >> {throw new SQLException(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)}

		1*utils.logAndFormatError("linkCoRegProfileToRegistry",con, "serviceErrorVO", _,"1", regReqVO.getEmailId(),regReqVO.getProfileId()) >> errorVO
		1*con.close() >> {throw new SQLException("")}

		when:
		RegistryResVO resVo= tools.linkCoRegProfileToReg(regReqVO,regItemsWSCall)

		then:
		resVo ==null
		1*tools.logInfo("GiftRegistryTools.linkCoRegProfileToReg() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)
		BBBSystemException ex = thrown()
		ex.getMessage().equals("Error in closing connection/Callable Statement:")
	}

	def"linkCoRegProfileToReg, when SQLException is thrown with error message not equal to REGISTRY_NOT_FOUND"(){

		given:
		setParametrsForSpy()
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		CallableStatement callableStmt =Mock()
		ServiceErrorVO errorVO =Mock()
		boolean regItemsWSCall = false
		regReqVO.getSiteId() >> "1"

		1*gsRep.getDataSource() >>DS
		DS.getConnection() >>con
		1*con.prepareCall(BBBGiftRegistryConstants.UPD_COREG_PROFILE_BY_EMAIL) >> {throw new SQLException("error")}

		1*utils.logAndFormatError("linkCoRegProfileToRegistry",con, "serviceErrorVO", _,"1", regReqVO.getEmailId(),regReqVO.getProfileId()) >> errorVO

		when:
		RegistryResVO resVo= tools.linkCoRegProfileToReg(regReqVO,regItemsWSCall)

		then:
		resVo.getServiceErrorVO()==errorVO
		1*tools.logError(BBBGiftRegistryConstants.ERROR_LOG_SQL_EXCEPTION,_)
		1*con.close()
	}

	def"linkCoRegProfileToReg, when SQLException is thrown with error message null"(){

		given:
		setParametrsForSpy()
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		CallableStatement callableStmt =Mock()
		ServiceErrorVO errorVO =Mock()
		boolean regItemsWSCall = false
		regReqVO.getSiteId() >> "1"

		1*gsRep.getDataSource() >>DS
		DS.getConnection() >>con
		1*con.prepareCall(BBBGiftRegistryConstants.UPD_COREG_PROFILE_BY_EMAIL) >> {throw new SQLException()}

		1*utils.logAndFormatError("linkCoRegProfileToRegistry",con, "serviceErrorVO", _,"1", regReqVO.getEmailId(),regReqVO.getProfileId()) >> errorVO

		when:
		RegistryResVO resVo= tools.linkCoRegProfileToReg(regReqVO,regItemsWSCall)

		then:
		resVo.getServiceErrorVO()==errorVO
		1*tools.logError(BBBGiftRegistryConstants.ERROR_LOG_SQL_EXCEPTION,_)
		1*con.close()
	}

	def"linkCoRegProfileToReg, when Connection is null"(){

		given:
		setParametrsForSpy()
		RegistryReqVO regReqVO = Mock()
		RegistryResVO linkCoRegToRegVO =Mock()
		CallableStatement callableStmt =Mock()
		ServiceErrorVO errorVO =Mock()
		boolean regItemsWSCall = false
		regReqVO.getSiteId() >> "1"

		1*gsRep.getDataSource() >>DS
		DS.getConnection() >>null

		when:
		RegistryResVO resVo= tools.linkCoRegProfileToReg(regReqVO,regItemsWSCall)

		then:
		resVo == null
	}

	def"compareDate, Converts String to Date"(){

		given:
		String date = "05/20/2000"
		requestMock.getLocale() >> new Locale("en_US")

		when:
		Date retDate= tools.compareDate(date)

		then:
		retDate != null
	}

	def"compareDate, when inputDate is null"(){

		given:
		requestMock.getLocale() >> new Locale("en_US")

		when:
		Date retDate= tools.compareDate(null)

		then:
		retDate == null
	}

	def"compareDate, when inputDate is 0"(){

		given:
		requestMock.getLocale() >> new Locale("en_US")

		when:
		Date retDate= tools.compareDate("0")

		then:
		retDate == null
	}

	def"compareDate, when inputDate is empty"(){

		given:
		requestMock.getLocale() >> new Locale("en_US")

		when:
		Date retDate= tools.compareDate("")

		then:
		retDate == null
	}

	def"updateRegistry, invoke webservice call to update gift registry"(){

		given:
		setParametrsForSpy()
		RegistryVO pRegistryVO =Mock()
		RegistryResVO registryResVO =Mock()
		1*tools.extractUtilMethod(pRegistryVO) >> registryResVO

		when:
		RegistryResVO resVo= tools.updateRegistry(pRegistryVO)

		then:
		resVo == registryResVO
	}

	def"updateRegistry(overloaded), invoke webservice call to update gift registry"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegNamesVO vo1 = Mock()
		RegNamesVO vo2 = Mock()
		RegNamesVO vo3 = Mock()
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[vo1,vo3]
		List<RegNamesVO> shippingVOs =[vo2]
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		Transaction t =Mock()
		CallableStatement cs1 =Mock()
		CallableStatement cs2 =Mock()
		CallableStatement cs3 =Mock()
		CallableStatement cs4 =Mock()
		CallableStatement cs5 =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con

		//for updateRegistryHeader
		con.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_HEADER) >> cs
		registryHeaderVO.getRegNum() >> "40"
		registryHeaderVO.getEventDate() >> "20252016"
		registryHeaderVO.getPromoEmailFlag() >> "false"
		registryHeaderVO.getPassword() >> "password"
		registryHeaderVO.getPasswordHint() >> "hint"
		registryHeaderVO.getGuestPassword() >> "guestPass"
		registryHeaderVO.getShowerDate() >> "showerDate"
		registryHeaderVO.getOtherDate() >> "otherDate"
		registryHeaderVO.getNetworkAffiliation() >> "networkAff"
		registryHeaderVO.getEstimateNumGuests() >> "500"
		registryHeaderVO.getIsPublic() >> "true"
		//for storeNum
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> []
		//for getCreateProgram
		registryHeaderVO.getSiteId() >> "1"
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> {throw new BBBBusinessException("")}
		//end updateRegistryHeader

		//for insertOrUpdateRegNames
		con.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_NAMES) >> cs1
		vo1.getNameAddrType() >> "FU"
		vo1.getLastName() >> "lName"
		vo1.getFirstName() >> "fName"
		vo1.getCompany() >> "company"
		vo1.getAddress1() >> "add1"
		vo1.getAddress2() >> "add2"
		vo1.getCity() >> "city"
		vo1.getState() >> "state"
		vo1.getZipCode() >> "15258"
		vo1.getDayPhone() >> "dayPhone"
		1*utils.stripNonAlphaNumerics("dayPhone") >> "dayPhone"
		vo1.getEvePhone() >> "evePhone"
		vo1.getEmailId() >> "EMAIL"
		1*utils.stripNonAlphaNumerics("evePhone") >> "evePhone"
		vo1.getAsOfDateFtrShipping() >>"100"
		registryHeaderVO.getJdaDate() >> 12242000
		vo1.getDayPhoneExt() >>"dayPhoneExt"
		vo1.getEvePhoneExt() >>"evePhoneExt"
		vo1.getPrefContMeth() >>"prefContMeth"
		vo1.getPrefContTime() >>"prefContTime"
		vo1.getEmailFlag() >>"emailFlag"
		vo1.getMaiden() >>"maiden"
		vo1.getAtgProfileId() >> "atfProfId"
		vo1.getAffiliateOptIn() >> "optIn"
		1*utils.getCountry("1") >> "con"

		vo3.getNameAddrType() >> "FU"
		vo3.getLastName() >> "lName"
		vo3.getFirstName() >> "fName"
		vo3.getCompany() >> "company"
		vo3.getAddress1() >> "add1"
		vo3.getAddress2() >> "add2"
		vo3.getCity() >> "city"
		vo3.getState() >> "state"
		vo3.getZipCode() >> "15258"
		vo3.getDayPhone() >> "dayPhone"
		1*utils.stripNonAlphaNumerics("dayPhone") >> "dayPhone"
		vo3.getEvePhone() >> "evePhone"
		vo3.getEmailId() >> "EMAIL"
		1*utils.stripNonAlphaNumerics("evePhone") >> "evePhone"
		vo3.getAsOfDateFtrShipping() >>""
		registryHeaderVO.getJdaDate() >> 12242000
		vo3.getDayPhoneExt() >>"dayPhoneExt"
		vo3.getEvePhoneExt() >>"evePhoneExt"
		vo3.getPrefContMeth() >>"prefContMeth"
		vo3.getPrefContTime() >>"prefContTime"
		vo3.getEmailFlag() >>"emailFlag"
		vo3.getMaiden() >>"maiden"
		vo3.getAtgProfileId() >> "atfProfId"
		vo3.getAffiliateOptIn() >> "optIn"
		1*utils.getCountry("1") >> "con"

		vo2.getNameAddrType() >> "nameAddType"
		vo2.getLastName() >> ""
		vo2.getFirstName() >> ""
		vo2.getCompany() >> ""
		vo2.getAddress1() >> ""
		vo2.getAddress2() >> ""
		vo2.getCity() >> ""
		vo2.getState() >> ""
		vo2.getZipCode() >> ""
		vo2.getDayPhone() >> "dayPhone"
		1*utils.stripNonAlphaNumerics("dayPhone") >> "dayPhone"
		vo2.getEvePhone() >> "evePhone"
		vo2.getEmailId() >> null
		1*utils.stripNonAlphaNumerics("evePhone") >> "evePhone"
		vo2.getAsOfDateFtrShipping() >>""
		registryHeaderVO.getJdaDate() >> 12242000
		vo2.getDayPhoneExt() >>""
		vo2.getEvePhoneExt() >>""
		vo2.getPrefContMeth() >>null
		vo2.getPrefContTime() >>null
		vo2.getEmailFlag() >>null
		vo2.getMaiden() >>""
		vo2.getAtgProfileId() >> ""
		vo2.getAffiliateOptIn() >> ""
		1*utils.getCountry("1") >> "con"
		//end insertOrUpdateRegNames

		//for disableFutureShipping
		con.prepareCall(BBBGiftRegistryConstants.DISABLE_FUTURE_SHIPPING) >> cs3
		//end disableFutureShipping

		//for insertOrUpdateRegBaby
		registryHeaderVO.getEventType() >> BBBGiftRegistryConstants.EVENT_TYPE_BABY
		con.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_BABY) >> cs4
		registryBabyVO.getGender() >> "male"
		registryBabyVO.getDecor() >> "decor"
		// for insertOrUpdateRegBaby

		//for insertOrUpdateRegPrefStore
		registryPrefStoreVO.getStoreNum() >> "200"
		con.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_PREF_STORE) >> cs5
		registryPrefStoreVO.getContactFlag() >> "flag"
		// end insertOrUpdateRegPrefStore

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		resVo.getRegistryId() == 40
		1*con.close()

		//for updateRegistryHeader
		1*con.setAutoCommit(false)
		1*cs.setLong(1, 40)
		1*cs.setLong(2,20252016)
		1*cs.setString(3,"false");
		1*cs.setString(4,"password")
		1*cs.setString(5,"hint")
		1*cs.setLong(6, 0)
		1*cs.setLong(7, 0)
		1*cs.setString(8,"guestPass");
		1*cs.setString(9, BBBGiftRegistryConstants.US_COM)
		1*cs.setString(10, "showerDate")
		1*cs.setString(11, "otherDate")
		1*cs.setString(12,"networkAff")
		1*cs.setLong(13, 500)
		1*cs.setString(14, "true");
		1*cs.setString(15, "1");
		1*cs.setLong(16, 0);
		1*cs.setString(17, BBBGiftRegistryConstants.US_COM);
		1*cs.setString(18, "str")
		1*cs.registerOutParameter(19, java.sql.Types.BIGINT)
		1*tools.extractDBCall(cs) >> {}
		1*cs.close()

		//for insertOrUpdateRegNames
		2*cs1.setLong(1, 40)
		2*cs1.setString(2, "FU")
		2*cs1.setString(3, "lName");
		2*cs1.setString(4, "fName")
		2*cs1.setString(5,"company");
		2*cs1.setString(6, "ADD1");
		2*cs1.setString(7,"ADD2");
		2*cs1.setString(8,"CITY");
		2*cs1.setString(9,"STATE");
		2*cs1.setString(10, "15258");
		2*cs1.setString(11,"dayPhone");
		2*cs1.setString(12,"evePhone");
		2*cs1.setString(13,"EMAIL");
		1*cs1.setLong(14,100)
		2*cs1.setLong(15, 12242000)
		2*cs1.setString(16, BBBGiftRegistryConstants.US_COM)
		2*cs1.setString(17,"dayPhoneExt")
		2*cs1.setString(18, "evePhoneExt");
		2*cs1.setString(19,"prefContMeth");
		2*cs1.setString(20,"prefContTime");
		2*cs1.setString(21,"emailFlag");
		2*cs1.setString(22,"maiden")
		2*cs1.setLong(23, 0);
		2*cs1.setString(24, BBBGiftRegistryConstants.US_COM)
		2*cs1.setString(25, "atfProfId")
		2*cs1.setString(26,"optIn");
		2*cs1.setString(27, "con")
		2*cs1.setString(28, "str")
		2*cs1.executeUpdate()

		1*cs1.setLong(1, 40)
		1*cs1.setString(2, "NAMEADDTYPE")
		1*cs1.setString(3, "");
		1*cs1.setString(4, "")
		1*cs1.setString(5,"");
		1*cs1.setString(6, "");
		1*cs1.setString(7,"");
		1*cs1.setString(8,"");
		1*cs1.setString(9,"");
		1*cs1.setString(10, "");
		1*cs1.setString(11,"dayPhone");
		1*cs1.setString(12,"evePhone");
		1*cs1.setString(13,"");
		2*cs1.setLong(14,0)
		1*cs1.setLong(15, 12242000)
		1*cs1.setString(16, BBBGiftRegistryConstants.US_COM)
		1*cs1.setString(17,"")
		1*cs1.setString(18, "");
		1*cs1.setString(19,"");
		1*cs1.setString(20,"");
		1*cs1.setString(21,"");
		1*cs1.setString(22,"")
		1*cs1.setLong(23, 0);
		1*cs1.setString(24, BBBGiftRegistryConstants.US_COM)
		1*cs1.setString(25, "")
		1*cs1.setString(26,"");
		1*cs1.setString(27, "con")
		1*cs1.setString(28, "str")
		1*cs1.executeUpdate()

		//for disableFutureShipping
		1*cs3.setLong(1, 40);
		1*cs3.setLong(2, 0);
		1*cs3.setString(3, BBBGiftRegistryConstants.US_COM)
		1*cs3.setString(4, "str")
		1*cs3.executeUpdate()
		1*cs3.close()

		//for insertOrUpdateRegBaby
		1*cs4.setLong(1, 40);
		1*cs4.setString(2, BBBCoreConstants.BLANK);
		1*cs4.setString(3,"male");
		1*cs4.setString(4,"decor");
		1*cs4.setLong(5,0)
		1*cs4.setString(6, BBBGiftRegistryConstants.US_COM);
		1*cs4.setString(7, "str")
		1*cs4.executeUpdate()

		//for insertOrUpdateRegPrefStore
		1*cs5.setLong(1, 40);
		1*cs5.setLong(2,200)
		1*cs5.setString(3,"flag")
		1*cs5.executeUpdate();
	}

	def"updateRegistry(overloaded), invoke webservice call to update gift registry when registrantVOsList and shippingVOsList is empty(insertOrUpdateRegNames)"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs =[]
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		Transaction t =Mock()
		CallableStatement cs2 =Mock()
		CallableStatement cs3 =Mock()
		CallableStatement cs4 =Mock()
		CallableStatement cs5 =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con

		//for updateRegistryHeader
		con.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_HEADER) >> cs
		registryHeaderVO.getRegNum() >> "40"
		registryHeaderVO.getEventDate() >> ""
		registryHeaderVO.getPromoEmailFlag() >> ""
		registryHeaderVO.getPassword() >> ""
		registryHeaderVO.getPasswordHint() >> ""
		registryHeaderVO.getGuestPassword() >> ""
		registryHeaderVO.getShowerDate() >> ""
		registryHeaderVO.getOtherDate() >> ""
		registryHeaderVO.getNetworkAffiliation() >> ""
		registryHeaderVO.getEstimateNumGuests() >> ""
		registryHeaderVO.getIsPublic() >> ""
		//for storeNum
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> []
		//for getCreateProgram
		registryHeaderVO.getSiteId() >> "1"
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> {throw new BBBBusinessException("")}
		//end updateRegistryHeader

		//for insertOrUpdateRegBaby
		registryHeaderVO.getEventType() >> BBBGiftRegistryConstants.EVENT_TYPE_BABY
		con.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_BABY) >> cs4
		registryBabyVO.getGender() >> null
		registryBabyVO.getDecor() >> ""
		1*cs4.isClosed() >> true
		// for insertOrUpdateRegBaby

		//for insertOrUpdateRegPrefStore
		registryPrefStoreVO.getStoreNum() >> "200" >> ""
		con.prepareCall(BBBGiftRegistryConstants.UPDATE_REG_PREF_STORE) >> cs5
		registryPrefStoreVO.getContactFlag() >> ""
		1*cs5.isClosed() >> false
		// end insertOrUpdateRegPrefStore

		1*con.close() >> {throw new SQLException("")}
		t.commit() >> {throw new HeuristicMixedException("")}

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		resVo == null
		BBBSystemException e = thrown()

		//for updateRegistryHeader
		1*con.setAutoCommit(false)
		1*cs.setLong(1, 40)
		1*cs.setLong(2,0)
		1*cs.setString(3,"");
		1*cs.setString(4,"")
		1*cs.setString(5,"")
		1*cs.setLong(6, 0)
		1*cs.setLong(7, 0)
		1*cs.setString(8,"");
		1*cs.setString(9, BBBGiftRegistryConstants.US_COM)
		1*cs.setString(10, "")
		1*cs.setString(11, "")
		1*cs.setString(12,"")
		1*cs.setLong(13, 0)
		1*cs.setString(14, "N");
		1*cs.setString(15, "1");
		1*cs.setLong(16, 0);
		1*cs.setString(17, BBBGiftRegistryConstants.US_COM);
		1*cs.setString(18, "str")
		1*cs.registerOutParameter(19, java.sql.Types.BIGINT)
		1*tools.extractDBCall(cs) >> {}
		1*cs.close()

		//for insertOrUpdateRegBaby
		1*cs4.setLong(1, 40);
		1*cs4.setString(2, BBBCoreConstants.BLANK);
		1*cs4.setString(3,"");
		1*cs4.setString(4,"");
		1*cs4.setLong(5,0)
		1*cs4.setString(6, BBBGiftRegistryConstants.US_COM);
		1*cs4.setString(7, "str")
		1*cs4.executeUpdate()

		//for insertOrUpdateRegPrefStore
		1*cs5.setLong(1, 40);
		1*cs5.setLong(2,0)
		1*cs5.setString(3,"")
		1*cs5.executeUpdate();
	}

	def"updateRegistry(overloaded), when connection is null"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs =[]
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> null
		registryHeaderVO.getRegNum() >> "40"
		registryHeaderVO.getEventType() >> BBBGiftRegistryConstants.EVENT_TYPE_BABY
		registryPrefStoreVO.getStoreNum() >> "200"
		t.commit() >> {throw new SecurityException("")}

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		0*tools.extractDBCall(_)
		resVo.getRegistryId() == 40
	}

	def"updateRegistry(overloaded), when regNum,eventType and storeNum is null"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs = null
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> null
		registryHeaderVO.getRegNum() >> ""
		registryHeaderVO.getEventType() >> ""
		registryPrefStoreVO.getStoreNum() >> ""

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		0*tools.extractDBCall(_)
		resVo.getRegistryId() == 0
	}

	def"updateRegistry(overloaded), when SQLException is thrown"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs = null
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		ServiceErrorVO errorVO =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> {throw new SQLException(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)}
		1*utils.logAndFormatError("UpdateRegistry", null,"ServiceErrorVO", _, _) >> errorVO
		1*utils.populateInputToLogErrorOrValidate(registryVO) >> []
		t.commit() >> {throw new RollbackException("")}

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		1*tools.logInfo("GiftRegistryTools.updateRegistry() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)
		0*tools.extractDBCall(_)
		resVo.getRegistryId() == 0
		resVo.getServiceErrorVO() == errorVO
	}

	def"updateRegistry(overloaded), when SQLException is thrown and HeuristicRollbackException is thrown while endingTransaction"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs = null
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		ServiceErrorVO errorVO =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> {throw new SQLException(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)}
		1*utils.logAndFormatError("UpdateRegistry", null,"ServiceErrorVO", _, _) >> errorVO
		1*utils.populateInputToLogErrorOrValidate(registryVO) >> []
		t.commit() >> {throw new HeuristicRollbackException("")}

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		1*tools.logInfo("GiftRegistryTools.updateRegistry() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)
		0*tools.extractDBCall(_)
		resVo.getRegistryId() == 0
		resVo.getServiceErrorVO() == errorVO
	}

	def"updateRegistry(overloaded), when SQLException is thrown with error message not equal to REGISTRY_NOT_FOUND"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs = null
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		ServiceErrorVO errorVO =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> {throw new SQLException("error")}
		1*utils.logAndFormatError("UpdateRegistry", null,"ServiceErrorVO", _, _) >> errorVO
		1*utils.populateInputToLogErrorOrValidate(registryVO) >> []
		t.rollback() >> {throw new IllegalStateException("")}

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		0*tools.extractDBCall(_)
		resVo.getRegistryId() == 0
		resVo.getServiceErrorVO() == errorVO
	}

	def"updateRegistry(overloaded), when SQLException is thrown with error mesaage null"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs = null
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		ServiceErrorVO errorVO =Mock()
		Transaction t =Mock()

		manager.getTransaction() >> null >> t
		catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> {throw new SQLException()}
		1*utils.logAndFormatError("UpdateRegistry", null,"ServiceErrorVO", _, _) >> errorVO
		1*utils.populateInputToLogErrorOrValidate(registryVO) >> []
		t.rollback() >>{throw new SystemException("")}

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		0*tools.extractDBCall(_)
		resVo.getRegistryId() == 0
		resVo.getServiceErrorVO() == errorVO
	}



	def"updateRegistry(overloaded), when SystemException is thrown while obtaining transaction"(){

		given:
		setParametrsForSpy()
		tools.setLoggingDebug(true)
		RegistryVO registryVO =Mock()
		RegistryHeaderVO registryHeaderVO =Mock()
		List<RegNamesVO> registrantVOs =[]
		List<RegNamesVO> shippingVOs = null
		RegistryBabyVO registryBabyVO =Mock()
		RegistryPrefStoreVO registryPrefStoreVO =Mock()
		1*manager.getTransaction() >> {throw new SystemException()}

		when:
		RegistryResVO resVo= tools.updateRegistry(registryVO,registryHeaderVO,registrantVOs,shippingVOs,registryBabyVO,registryPrefStoreVO)

		then:
		0*tools.extractDBCall(_)
		resVo.getRegistryId() == 0
		resVo.getServiceErrorVO() != null
	}

	def"updateRegistryWithAtgInfo,  used to invoke webservice call to update gift registry with ATG info if flag is false"(){

		given:
		ProfileSyncRequestVO pProfileSyncRequestVO =Mock()
		boolean regItemsWSCall = false
		ErrorStatus status =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.UPD_REG_NAMES_BY_PROFILE_ID) >>cs

		pProfileSyncRequestVO.getProfileId()  >> "1"
		pProfileSyncRequestVO.getLastName() >> "2"
		pProfileSyncRequestVO.getFirstName() >> "3"
		pProfileSyncRequestVO.getPhoneNumber() >>"4"
		pProfileSyncRequestVO.getMobileNumber() >>"5"
		pProfileSyncRequestVO.getEmailAddress() >> "addr"
		pProfileSyncRequestVO.getSiteFlag() >> "1"

		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> {throw new BBBBusinessException("")}
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]


		when:
		ProfileSyncResponseVO syncVo= tools.updateRegistryWithAtgInfo(pProfileSyncRequestVO,regItemsWSCall)

		then:
		syncVo.getErrorStatus().isErrorExists() == false
		1*cs.setString(1, "1")
		1*cs.setString(2, "2")
		1*cs.setString(3, "3")
		1*cs.setString(4, "4")
		1*cs.setString(5, "5")
		1*cs.setString(6, null)
		1*cs.setString(7, null)
		1*cs.setString(8,"ADDR")
		1*cs.setLong(9,0);
		1*cs.setString(10, BBBGiftRegistryConstants.US_COM)
		1*cs.setString(11, "str")
		1*cs.executeUpdate()
	}

	def"updateRegistryWithAtgInfo,  used to invoke webservice call to update gift registry with ATG info if flag is false and getEmailAddress null "(){

		given:
		setParametrsForSpy()
		ProfileSyncRequestVO pProfileSyncRequestVO =Mock()
		boolean regItemsWSCall = false
		ErrorStatus status =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.UPD_REG_NAMES_BY_PROFILE_ID) >>cs

		pProfileSyncRequestVO.getProfileId()  >> "1"
		pProfileSyncRequestVO.getLastName() >> "2"
		pProfileSyncRequestVO.getFirstName() >> "3"
		pProfileSyncRequestVO.getPhoneNumber() >>"4"
		pProfileSyncRequestVO.getMobileNumber() >>"5"
		pProfileSyncRequestVO.getEmailAddress() >> null
		pProfileSyncRequestVO.getSiteFlag() >> "1"

		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> {throw new BBBBusinessException("")}
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		1*catalogTools.getAllValuesForKey(BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		1*con.close() >> {throw new SQLException("")}

		when:
		ProfileSyncResponseVO syncVo= tools.updateRegistryWithAtgInfo(pProfileSyncRequestVO,regItemsWSCall)

		then:
		syncVo.getErrorStatus().isErrorExists() == false
		1*cs.setString(1, "1")
		1*cs.setString(2, "2")
		1*cs.setString(3, "3")
		1*cs.setString(4, "4")
		1*cs.setString(5, "5")
		1*cs.setString(6, null)
		1*cs.setString(7, null)
		1*cs.setString(8,"")
		1*cs.setLong(9,0);
		1*cs.setString(10, BBBGiftRegistryConstants.US_COM)
		1*cs.setString(11, "str")
		1*cs.executeUpdate()
		1*tools.logError("Error occurred while closing connection")
	}

	def"updateRegistryWithAtgInfo, used to invoke webservice call to update gift registry with ATG info if flag is true"(){

		given:
		setParametrsForSpy()
		ProfileSyncRequestVO pProfileSyncRequestVO =Mock()
		ProfileSyncResponseVO respVo =Mock()
		boolean regItemsWSCall = true
		1*tools.extractUtilMethod(pProfileSyncRequestVO) >> respVo

		when:
		ProfileSyncResponseVO syncVo= tools.updateRegistryWithAtgInfo(pProfileSyncRequestVO,regItemsWSCall)

		then:
		syncVo== respVo
	}

	def"updateRegistryWithAtgInfo,  when connection is null "(){

		given:
		setParametrsForSpy()
		ProfileSyncRequestVO pProfileSyncRequestVO =Mock()
		boolean regItemsWSCall = false
		ErrorStatus status =Mock()
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> null

		when:
		ProfileSyncResponseVO syncVo= tools.updateRegistryWithAtgInfo(pProfileSyncRequestVO,regItemsWSCall)

		then:
		syncVo == null
	}

	def"updateRegistryWithAtgInfo, when SQLException is thrown"(){

		given:
		setParametrsForSpy()
		ProfileSyncRequestVO pProfileSyncRequestVO =Mock()
		boolean regItemsWSCall = false
		ErrorStatus status =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> {throw new SQLException(BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)}
		1*utils.logAndFormatError("UpdateRegistryWithAtgInfo", null,
				BBBGiftRegistryConstants.ERROR_STATUS, _,
				pProfileSyncRequestVO.getProfileId(),
				pProfileSyncRequestVO.getEmailAddress(),
				pProfileSyncRequestVO.getLastName(),
				pProfileSyncRequestVO.getFirstName(),
				pProfileSyncRequestVO.getPhoneNumber(),
				pProfileSyncRequestVO.getMobileNumber()) >> status

		when:
		ProfileSyncResponseVO syncVo= tools.updateRegistryWithAtgInfo(pProfileSyncRequestVO,regItemsWSCall)

		then:
		1*tools.logInfo("GiftRegistryTools.updateRegistryWithAtgInfo() :: " + BBBGiftRegistryConstants.REGISTRY_NOT_FOUND)
		syncVo.getErrorStatus()  == status
	}

	def"updateRegistryWithAtgInfo, when SQLException is thrown with error message not equal to REGISTRY_NOT_FOUND"(){

		given:
		ProfileSyncRequestVO pProfileSyncRequestVO =Mock()
		boolean regItemsWSCall = false
		ErrorStatus status =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> {throw new SQLException("")}
		1*utils.logAndFormatError("UpdateRegistryWithAtgInfo", null,
				BBBGiftRegistryConstants.ERROR_STATUS, _,
				pProfileSyncRequestVO.getProfileId(),
				pProfileSyncRequestVO.getEmailAddress(),
				pProfileSyncRequestVO.getLastName(),
				pProfileSyncRequestVO.getFirstName(),
				pProfileSyncRequestVO.getPhoneNumber(),
				pProfileSyncRequestVO.getMobileNumber()) >> status

		when:
		ProfileSyncResponseVO syncVo= tools.updateRegistryWithAtgInfo(pProfileSyncRequestVO,regItemsWSCall)

		then:
		syncVo.getErrorStatus()  == status
	}

	def"updateRegistryWithAtgInfo, when SQLException is thrown when error message is null"(){

		given:
		ProfileSyncRequestVO pProfileSyncRequestVO =Mock()
		boolean regItemsWSCall = false
		ErrorStatus status =Mock()

		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> {throw new SQLException()}
		1*utils.logAndFormatError("UpdateRegistryWithAtgInfo", null,
				BBBGiftRegistryConstants.ERROR_STATUS, _,
				pProfileSyncRequestVO.getProfileId(),
				pProfileSyncRequestVO.getEmailAddress(),
				pProfileSyncRequestVO.getLastName(),
				pProfileSyncRequestVO.getFirstName(),
				pProfileSyncRequestVO.getPhoneNumber(),
				pProfileSyncRequestVO.getMobileNumber()) >> status

		when:
		ProfileSyncResponseVO syncVo= tools.updateRegistryWithAtgInfo(pProfileSyncRequestVO,regItemsWSCall)

		then:
		syncVo.getErrorStatus()  == status
	}

	def"getRegistriesStatus, when webService is invoked"(){

		given:
		setParametrsForSpy()
		RegistryStatusVO registryStatusVO =Mock()
		RegStatusesResVO vo =Mock()
		1*tools.extractUtilMethod(registryStatusVO) >>vo

		when:
		RegStatusesResVO statusVo= tools.getRegistriesStatus(registryStatusVO)

		then:
		statusVo  == vo
	}


	def"GetRegistryStatusesByProfileId, fetch RegistryStatuses ByProfileId"(){

		given:
		setParametrsForSpy()
		RegistryStatusVO registryStatusVO =Mock()

		1*gsRep.getDataSource() >>DS
		1*DS.getConnection() >>con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_STATUSES_BY_PROFILEID) >> cs

		registryStatusVO.getSiteId() >>"1"
		registryStatusVO.getProfileId() >> "2"

		cs.getObject(3) >>rs
		rs.next() >> true >> false
		rs.getString("REGISTRY_NUM") >>"1"
		rs.getString("STATUS_CD") >>"2"
		rs.getString("STATUS_DESC") >>"3"

		when:
		RegStatusesResVO regStatusResVo= tools.GetRegistryStatusesByProfileId(registryStatusVO)

		then:
		1*cs.setString(1, "1");
		1*cs.setString(2, "2");
		1*cs.registerOutParameter(3, OracleTypes.CURSOR);
		1*tools.extractDBCall(cs)
		RegistryStatusVO tempRegistryStatusVO = regStatusResVo.getListRegistryStatusVO().get(0)
		tempRegistryStatusVO.getRegistryId() == "1"
		tempRegistryStatusVO.getStatusCode() == "2"
		tempRegistryStatusVO.getStatusDesc() == "3"
	}

	def"GetRegistryStatusesByProfileId, when resultSet is null"(){

		given:
		setParametrsForSpy()
		RegistryStatusVO registryStatusVO =Mock()

		1*gsRep.getDataSource() >>DS
		1*DS.getConnection() >>con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_STATUSES_BY_PROFILEID) >> cs
		registryStatusVO.getSiteId() >>"1"
		registryStatusVO.getProfileId() >> "2"
		cs.getObject(3) >>null

		when:
		RegStatusesResVO regStatusResVo= tools.GetRegistryStatusesByProfileId(registryStatusVO)

		then:
		1*cs.setString(1, "1");
		1*cs.setString(2, "2");
		1*cs.registerOutParameter(3, OracleTypes.CURSOR);
		1*tools.extractDBCall(cs)
		regStatusResVo.getListRegistryStatusVO() == null
	}

	def"GetRegistryStatusesByProfileId, when SQLException is thrown while closingConnection"(){

		given:
		setParametrsForSpy()
		ServiceErrorVO serviceErrorVO =Mock()
		RegistryStatusVO registryStatusVO =Mock()
		1*gsRep.getDataSource() >>DS
		1*DS.getConnection() >>con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_STATUSES_BY_PROFILEID) >> {throw new Exception("")}
		1*utils.logAndFormatError("GetRegistryStatusesByProfileId", con,"serviceErrorVO", _,"", null, null) >> serviceErrorVO
		1*con.close() >> {throw new SQLException("")}

		when:
		RegStatusesResVO regStatusResVo= tools.GetRegistryStatusesByProfileId(registryStatusVO)

		then:
		BBBSystemException e = thrown()
		1*tools.logError("SQL exception while registry header info "+ "in GiftRegistryTools", _)
		0*tools.extractDBCall(cs)
		regStatusResVo == null
	}

	def"GetRegistryStatusesByProfileId, when Exception is thrown"(){

		given:
		setParametrsForSpy()
		ServiceErrorVO serviceErrorVO =Mock()
		RegistryStatusVO registryStatusVO =Mock()
		1*gsRep.getDataSource() >>DS
		1*DS.getConnection() >>con
		con.prepareCall(BBBGiftRegistryConstants.GET_REG_STATUSES_BY_PROFILEID) >> {throw new Exception("")}
		1*utils.logAndFormatError("GetRegistryStatusesByProfileId", con,"serviceErrorVO", _,"", null, null) >> serviceErrorVO

		when:
		RegStatusesResVO regStatusResVo= tools.GetRegistryStatusesByProfileId(registryStatusVO)

		then:
		1*tools.logError("SQL exception while registry header info "+ "in GiftRegistryTools", _)
		0*tools.extractDBCall(cs)
		regStatusResVo.getListRegistryStatusVO()== null
		regStatusResVo.getServiceErrorVO() == serviceErrorVO
	}

	def"GetRegistryStatusesByProfileId, when connection is null"(){

		given:
		setParametrsForSpy()
		RegistryStatusVO registryStatusVO =Mock()
		1*gsRep.getDataSource() >>DS
		1*DS.getConnection() >> null

		when:
		RegStatusesResVO regStatusResVo= tools.GetRegistryStatusesByProfileId(registryStatusVO)

		then:
		0*tools.extractDBCall(cs)
		regStatusResVo.getListRegistryStatusVO()== null
	}

	def"addItemJSONObjectParser, parsing JASON Objects"(){

		given:
		setParametrsForSpy()
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		String pJsonResultString = "{addItemResults:[{skuId:10217962,paramRegID:20,isCustomizationRequired:true,personalizationType:\"PY\",refNum:80,ltlShipMethod:\"express\",registryId:500,prodId:2,qty:10,ltlFlag:true}],parentProdId:\"200\",registryName:\"wedding\",heading1:\"heading\",repositoryId:\"90\",isDeclined:\"true\",isFromPendingTab:\"true\",isFromDeclinedTab:\"false\"}"

		1*tools.extractSiteId() >>"tbs"
		1*catalogTools.getDeliveryCharge("tbs","10217962","express") >> 50
		1*catalogTools.getListPrice("2","10217962") >> 600
		1*catalogTools.getSalePrice("2","10217962") >> 550

		giftRegistryViewBean.setInternationalContext(true)
		giftRegistryViewBean.setUserCountry("US")
		giftRegistryViewBean.setUserCurrency("dollars")

		when:
		GiftRegistryViewBean registryViewBean= tools.addItemJSONObjectParser(giftRegistryViewBean,pJsonResultString)

		then:
		registryViewBean.getRegistryId() == "500"
		registryViewBean.getParentProductId() == "200"
		registryViewBean.getRegistryName() =="wedding"
		registryViewBean.getConsultantName() == "heading"
		registryViewBean.getRepositoryId() == "90"
		registryViewBean.getIsFromDeclinedTab() == "false"
		registryViewBean.getIsFromPendingTab() == "true"
		registryViewBean.getIsDeclined() == "true"
		registryViewBean.getTotQuantity() == 10
		registryViewBean.getOmniProductList() == null

		AddItemsBean addItemsBean = registryViewBean.getAdditem().get(0)
		addItemsBean.getSku() == "10217962"
		addItemsBean.getParamRegistryId() == "20"
		addItemsBean.getCustomizationRequired() == "true"
		addItemsBean.getPersonalizationCode() == "PY"
		addItemsBean.getRefNum() == "80"
		addItemsBean.getLtlDeliveryServices() == "express"
		addItemsBean.getLtlDeliveryPrices() == "50.0"
		addItemsBean.getRegistryId() == "500"
		addItemsBean.getProductId() == "2"
		addItemsBean.getPrice() == "550.0"
		addItemsBean.getQuantity() == "10"
		addItemsBean.getLtlFlag() == "true"
	}

	def"addItemJSONObjectParser, parsing JASON Objects with certain feilds empty"(){

		given:
		setParametrsForSpy()
		BBBSessionBean sessionBean =Mock()
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		String pJsonResultString = "{addItemResults:[{skuId:10217962,paramRegID:\"null\",isCustomizationRequired:null,personalizationType:null,refNum:null,ltlShipMethod:\"\",prodId:2,qty:10,ltlFlag:null}],parentProdId:null,registryName:null,heading1:null,repositoryId:null,isDeclined:null,isFromPendingTab:null,isFromDeclinedTab:null}"

		0*tools.extractSiteId() >>"tbs"
		0*catalogTools.getDeliveryCharge("tbs","10217962","express")
		1*catalogTools.getListPrice("2","10217962") >> 550
		1*catalogTools.getSalePrice("2","10217962") >> 0

		1*requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean

		giftRegistryViewBean.setInternationalContext(false)
		giftRegistryViewBean.setUserCountry("US")
		giftRegistryViewBean.setUserCurrency("dollars")

		when:
		GiftRegistryViewBean registryViewBean= tools.addItemJSONObjectParser(giftRegistryViewBean,pJsonResultString)

		then:
		registryViewBean.getRegistryId() == null
		registryViewBean.getParentProductId() == null
		registryViewBean.getRegistryName() ==null
		registryViewBean.getConsultantName() == null
		registryViewBean.getRepositoryId() == null
		registryViewBean.getIsFromDeclinedTab() == "false"
		registryViewBean.getIsFromPendingTab() == "false"
		registryViewBean.getIsDeclined() == "true"
		registryViewBean.getTotQuantity() == 10
		registryViewBean.getOmniProductList() == ";2;;;event22=10|event23=5500.0;eVar30=10217962"
		AddItemsBean addItemsBean = registryViewBean.getAdditem().get(0)
		addItemsBean.getSku() == "10217962"
		addItemsBean.getParamRegistryId() == null
		addItemsBean.getCustomizationRequired() == "false"
		addItemsBean.getPersonalizationCode() == null
		addItemsBean.getRefNum() == null
		addItemsBean.getLtlDeliveryServices() == ""
		addItemsBean.getLtlDeliveryPrices() == null
		addItemsBean.getRegistryId() == null
		addItemsBean.getProductId() == "2"
		addItemsBean.getPrice() == "550.0"
		addItemsBean.getQuantity() == "10"
		addItemsBean.getLtlFlag() == "false"
		1*sessionBean.setRegistryEvar23Price("5500.0")
	}

	def"addItemJSONObjectParser, parsing JASON Objects with certain feilds empty1"(){

		given:
		setParametrsForSpy()
		BBBSessionBean sessionBean =Mock()
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		String pJsonResultString = "{addItemResults:[{skuId:10217962,paramRegID:\"\",ltlShipMethod:null,prodId:2,qty:10}],parentProdId:null}"

		0*tools.extractSiteId() >>"tbs"
		0*catalogTools.getDeliveryCharge("tbs","10217962","express")
		1*catalogTools.getListPrice("2","10217962") >> 550
		1*catalogTools.getSalePrice("2","10217962") >> 0

		1*requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean

		giftRegistryViewBean.setInternationalContext(null)
		giftRegistryViewBean.setUserCountry("US")
		giftRegistryViewBean.setUserCurrency("dollars")

		when:
		GiftRegistryViewBean registryViewBean= tools.addItemJSONObjectParser(giftRegistryViewBean,pJsonResultString)

		then:
		registryViewBean.getRegistryId() == null
		registryViewBean.getParentProductId() == null
		registryViewBean.getRegistryName() ==null
		registryViewBean.getConsultantName() == null
		registryViewBean.getRepositoryId() == null
		registryViewBean.getIsFromDeclinedTab() == "false"
		registryViewBean.getIsFromPendingTab() == "false"
		registryViewBean.getIsDeclined() == "true"
		registryViewBean.getTotQuantity() == 10
		registryViewBean.getOmniProductList() == ";2;;;event22=10|event23=5500.0;eVar30=10217962"
		AddItemsBean addItemsBean = registryViewBean.getAdditem().get(0)
		addItemsBean.getSku() == "10217962"
		addItemsBean.getParamRegistryId() == null
		addItemsBean.getCustomizationRequired() == "false"
		addItemsBean.getPersonalizationCode() == null
		addItemsBean.getRefNum() == null
		addItemsBean.getLtlDeliveryServices() == null
		addItemsBean.getLtlDeliveryPrices() == null
		addItemsBean.getRegistryId() == null
		addItemsBean.getProductId() == "2"
		addItemsBean.getPrice() == "550.0"
		addItemsBean.getQuantity() == "10"
		addItemsBean.getLtlFlag() == null
		1*sessionBean.setRegistryEvar23Price("5500.0")
	}

	def"addItemJSONObjectParser, parsing JASON Objects with certain feilds empty2"(){

		given:
		setParametrsForSpy()
		BBBSessionBean sessionBean =Mock()
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		String pJsonResultString = "{addItemResults:[{skuId:10217962,paramRegID:null,prodId:2,qty:10}]}"

		0*tools.extractSiteId() >>"tbs"
		0*catalogTools.getDeliveryCharge("tbs","10217962","express")
		1*catalogTools.getListPrice("2","10217962") >> 550
		1*catalogTools.getSalePrice("2","10217962") >> 0

		1*requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean

		giftRegistryViewBean.setInternationalContext(null)
		giftRegistryViewBean.setUserCountry("US")
		giftRegistryViewBean.setUserCurrency("dollars")

		when:
		GiftRegistryViewBean registryViewBean= tools.addItemJSONObjectParser(giftRegistryViewBean,pJsonResultString)

		then:
		registryViewBean.getRegistryId() == null
		registryViewBean.getParentProductId() == null
		registryViewBean.getRegistryName() ==null
		registryViewBean.getConsultantName() == null
		registryViewBean.getRepositoryId() == null
		registryViewBean.getIsFromDeclinedTab() == "false"
		registryViewBean.getIsFromPendingTab() == "false"
		registryViewBean.getIsDeclined() == "true"
		registryViewBean.getTotQuantity() == 10
		registryViewBean.getOmniProductList() == ";2;;;event22=10|event23=5500.0;eVar30=10217962"
		AddItemsBean addItemsBean = registryViewBean.getAdditem().get(0)
		addItemsBean.getSku() == "10217962"
		addItemsBean.getParamRegistryId() == null
		addItemsBean.getCustomizationRequired() == "false"
		addItemsBean.getPersonalizationCode() == null
		addItemsBean.getRefNum() == null
		addItemsBean.getLtlDeliveryServices() == null
		addItemsBean.getLtlDeliveryPrices() == null
		addItemsBean.getRegistryId() == null
		addItemsBean.getProductId() == "2"
		addItemsBean.getPrice() == "550.0"
		addItemsBean.getQuantity() == "10"
		addItemsBean.getLtlFlag() == null
		1*sessionBean.setRegistryEvar23Price("5500.0")
	}

	def"addItemJSONObjectParser, parsing JASON Object when paramRegID is not passed in the pJsonResultString"(){

		given:
		setParametrsForSpy()
		BBBSessionBean sessionBean =Mock()
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		String pJsonResultString = "{addItemResults:[{skuId:10217962,prodId:2,qty:10}]}"

		0*tools.extractSiteId() >>"tbs"
		0*catalogTools.getDeliveryCharge("tbs","10217962","express")
		1*catalogTools.getListPrice("2","10217962") >> 550
		1*catalogTools.getSalePrice("2","10217962") >> 0

		1*requestMock.resolveName(BBBCoreConstants.SESSION_BEAN) >> sessionBean

		giftRegistryViewBean.setInternationalContext(null)
		giftRegistryViewBean.setUserCountry("US")
		giftRegistryViewBean.setUserCurrency("dollars")

		when:
		GiftRegistryViewBean registryViewBean= tools.addItemJSONObjectParser(giftRegistryViewBean,pJsonResultString)

		then:
		registryViewBean.getRegistryId() == null
		registryViewBean.getParentProductId() == null
		registryViewBean.getRegistryName() ==null
		registryViewBean.getConsultantName() == null
		registryViewBean.getRepositoryId() == null
		registryViewBean.getIsFromDeclinedTab() == "false"
		registryViewBean.getIsFromPendingTab() == "false"
		registryViewBean.getIsDeclined() == "true"
		registryViewBean.getTotQuantity() == 10
		registryViewBean.getOmniProductList() == ";2;;;event22=10|event23=5500.0;eVar30=10217962"
		AddItemsBean addItemsBean = registryViewBean.getAdditem().get(0)
		addItemsBean.getSku() == "10217962"
		addItemsBean.getParamRegistryId() == null
		addItemsBean.getCustomizationRequired() == "false"
		addItemsBean.getPersonalizationCode() == null
		addItemsBean.getRefNum() == null
		addItemsBean.getLtlDeliveryServices() == null
		addItemsBean.getLtlDeliveryPrices() == null
		addItemsBean.getRegistryId() == null
		addItemsBean.getProductId() == "2"
		addItemsBean.getPrice() == "550.0"
		addItemsBean.getQuantity() == "10"
		addItemsBean.getLtlFlag() == null
		1*sessionBean.setRegistryEvar23Price("5500.0")
	}

	def"addItemJSONObjectParser, when dynaBeanProperties does not contain ADD_ITEMS"(){

		given:
		setParametrsForSpy()
		GiftRegistryViewBean giftRegistryViewBean = new GiftRegistryViewBean()
		String pJsonResultString = "{}"

		0*tools.extractSiteId() >>"tbs"
		0*catalogTools.getDeliveryCharge(null,null,null)
		0*catalogTools.getListPrice(null,null)
		0*catalogTools.getSalePrice(null,null)
		0*requestMock.resolveName(BBBCoreConstants.SESSION_BEAN)

		when:
		GiftRegistryViewBean registryViewBean= tools.addItemJSONObjectParser(giftRegistryViewBean,pJsonResultString)

		then:
		registryViewBean.getRegistryId() == null
		registryViewBean.getParentProductId() == null
		registryViewBean.getRegistryName() ==null
		registryViewBean.getConsultantName() == null
		registryViewBean.getRepositoryId() == null
		registryViewBean.getIsFromDeclinedTab() == "false"
		registryViewBean.getIsFromPendingTab() == "false"
		registryViewBean.getIsDeclined() == "true"
		registryViewBean.getTotQuantity() == 0
		registryViewBean.getOmniProductList() == null
		registryViewBean.getAdditem()==null
	}

	def"populateRegistryItemWIthWishListItem, when salePrice is greater than 0"(){

		given:
		RepositoryItem wishListItem = Mock()

		wishListItem.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_CATALOG_REF_ID) >> "1"
		wishListItem.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_PRODUCT_ID) >> "prod_id"
		1*catalogTools.getListPrice("prod_id","1") >> 100
		1*catalogTools.getSalePrice("prod_id","1") >> 50
		wishListItem.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_QTY_DESIRED) >> "10"

		when:
		AddItemsBean addItemsBean= tools.populateRegistryItemWIthWishListItem(wishListItem)

		then:
		addItemsBean.getSku() == "1"
		addItemsBean.getProductId() == "prod_id"
		addItemsBean.getPrice() == "50.0"
		addItemsBean.getQuantity() == "10"
	}

	def"populateRegistryItemWIthWishListItem, when salePrice is less than 0"(){

		given:
		RepositoryItem wishListItem = Mock()

		wishListItem.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_CATALOG_REF_ID) >> "1"
		wishListItem.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_PRODUCT_ID) >> "prod_id"
		1*catalogTools.getListPrice("prod_id","1") >> 100
		1*catalogTools.getSalePrice("prod_id","1") >> 0
		wishListItem.getPropertyValue(BBBGiftRegistryConstants.WISHLIST_PROPERTY_QTY_DESIRED) >> "10"

		when:
		AddItemsBean addItemsBean= tools.populateRegistryItemWIthWishListItem(wishListItem)

		then:
		addItemsBean.getSku() == "1"
		addItemsBean.getProductId() == "prod_id"
		addItemsBean.getPrice() == "100.0"
		addItemsBean.getQuantity() == "10"
	}

	def"changedRegistryTypeName, when getListRegistrySummaryVO is not null"(){

		given:
		RegSearchResVO regSearchResVO= Mock()
		RegistrySummaryVO registrySummaryVO =Mock()
		String siteId = "tbs"

		regSearchResVO.getListRegistrySummaryVO() >> [registrySummaryVO]
		registrySummaryVO.getEventType() >> "event"
		1*catalogTools.getRegistryTypeName("event", siteId) >> "reg_name"

		when:
		RegSearchResVO resVo= tools.changedRegistryTypeName(regSearchResVO,siteId)

		then:
		resVo == regSearchResVO
		1*registrySummaryVO.setEventCode("event")
		1*registrySummaryVO.setEventType("reg_name")
	}

	def"changedRegistryTypeName, when getListRegistrySummaryVO is null"(){

		given:
		RegSearchResVO regSearchResVO= Mock()
		RegistrySummaryVO registrySummaryVO =Mock()
		String siteId = "tbs"

		regSearchResVO.getListRegistrySummaryVO() >> null
		0*catalogTools.getRegistryTypeName("event", siteId) >> "reg_name"

		when:
		RegSearchResVO resVo= tools.changedRegistryTypeName(regSearchResVO,siteId)

		then:
		resVo == regSearchResVO
		0*registrySummaryVO.setEventCode(_)
		0*registrySummaryVO.setEventType(_)
	}

	def"updateRegistryStatus, updates the registry status in database."(){

		given:
		setParametrsForSpy()
		RegistrySkinnyVO registrySkinnyVO= Mock()
		MutableRepositoryItem giftRegistryItem =Mock()
		String siteId = "tbs"
		String statusDesc ="desc"
		1*tools.fetchGiftRepositoryItem(siteId,registrySkinnyVO.getRegistryId()) >> [giftRegistryItem]

		when:
		tools.updateRegistryStatus(registrySkinnyVO,siteId,statusDesc)

		then:
		1*giftRegistryItem.setPropertyValue("registryStatus", statusDesc);
		1*mRep.updateItem(giftRegistryItem)
	}

	def"updateRegistryStatus, throws RepositoryException."(){

		given:
		setParametrsForSpy()
		RegistrySkinnyVO registrySkinnyVO= Mock()
		MutableRepositoryItem giftRegistryItem =Mock()
		String siteId = "tbs"
		String statusDesc ="desc"
		1*tools.fetchGiftRepositoryItem(siteId,registrySkinnyVO.getRegistryId()) >> [giftRegistryItem]
		1*mRep.updateItem(giftRegistryItem) >> {throw new RepositoryException("")}

		when:
		tools.updateRegistryStatus(registrySkinnyVO,siteId,statusDesc)

		then:
		1*giftRegistryItem.setPropertyValue("registryStatus", statusDesc)
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10131+ " RepositoryException from updateRegistryStatus of GiftRegistryTools",_)
		BBBSystemException e = thrown()
		e.getMessage().contains(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION)
	}

	def"updateRegistryStatus, when grRepositoryItems is null."(){

		given:
		setParametrsForSpy()
		RegistrySkinnyVO registrySkinnyVO= Mock()
		MutableRepositoryItem giftRegistryItem =Mock()
		String siteId = "tbs"
		String statusDesc ="desc"
		1*tools.fetchGiftRepositoryItem(siteId,registrySkinnyVO.getRegistryId()) >> null

		when:
		tools.updateRegistryStatus(registrySkinnyVO,siteId,statusDesc)

		then:
		0*mRep.updateItem(_)
	}

	def"updateRegistryStatus, when BBBBusinessException is thrown"(){

		given:
		setParametrsForSpy()
		RegistrySkinnyVO registrySkinnyVO= Mock()
		MutableRepositoryItem giftRegistryItem =Mock()
		String siteId = "tbs"
		String statusDesc ="desc"
		1*tools.fetchGiftRepositoryItem(siteId,registrySkinnyVO.getRegistryId()) >> {throw new BBBBusinessException("")}

		when:
		tools.updateRegistryStatus(registrySkinnyVO,siteId,statusDesc)

		then:
		1*tools.logError(BBBCoreErrorConstants.GIFTREGISTRY_ERROR_10130+ " BBBBusinessException from updateRegistryStatus of GiftRegistryTools",_)
		BBBSystemException e = thrown()
		e.getMessage().contains(BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION)
	}

	def"totalPrice, when quantity is empty"(){

		given:
		String quantity = ""
		String pPrice = "50"

		when:
		String totalPrice = tools.totalPrice(quantity,pPrice)

		then:
		totalPrice =="0.0"
	}

	def"totalPrice, when price is empty"(){

		given:
		String quantity = "100"
		String pPrice = ""

		when:
		String totalPrice = tools.totalPrice(quantity,pPrice)

		then:
		totalPrice =="0.0"
	}

	def"copyRegistry, copies the registry"(){

		given:
		setParametrsForSpy()
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		RegCopyResVO resVo =Mock()
		1*tools.extractUtilMethod(pGiftRegistryViewBean) >> resVo
		resVo.gettotalNumOfItemsCopied() >> "20"

		when:
		RegCopyResVO regCopyResVO = tools.copyRegistry(pGiftRegistryViewBean)

		then:
		regCopyResVO == resVo
		1*pGiftRegistryViewBean.setTotQuantity(20)
	}

	def"copyRegistryInEcomAdmin, copy registry items from source to destination registry"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		RegCopyResVO resVo =Mock()
		ErrorStatus errorStatus =Mock()
		1*utils.validateInput(_) >> errorStatus
		errorStatus.isErrorExists() >> false

		//for regCopy
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.COPY_REGISTRY) >> cs
		pGiftRegistryViewBean.getSiteFlag() >> "1"
		pGiftRegistryViewBean.getSourceRegistry() >> "50"
		pGiftRegistryViewBean.getTargetRegistry() >> "20"
		2*catalogTools.getAllValuesForKey( BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		1*catalogTools.getAllValuesForKey( BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["5"]
		1*catalogTools.getAllValuesForKey( BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		cs.getInt(8) >> 10
		//end regCopy

		when:
		RegCopyResVO regCopyResVO = tools.copyRegistryInEcomAdmin(pGiftRegistryViewBean)

		then:
		1*con.setAutoCommit(false)
		1*cs.setString(1, "1")
		1*cs.setInt(2,50)
		1*cs.setInt(3,20)
		1*cs.setString(4,"BBBY.COM")
		1*cs.setInt(5, 5)
		1*cs.setString(6,"BBBY.COM")
		1*cs.setString(7, "str")
		1*cs.registerOutParameter(8, java.sql.Types.INTEGER);
		1*cs.executeUpdate()
		regCopyResVO.gettotalNumOfItemsCopied() == "10"
		regCopyResVO.getServiceErrorVO().isErrorExists() == false
	}

	def"copyRegistryInEcomAdmin, when exception is thrown in regCopy method"(){

		given:
		setParametrsForSpy()
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		RegCopyResVO resVo =Mock()
		ErrorStatus errorStatus =Mock()
		ServiceErrorVO errorVo =Mock()
		1*utils.validateInput(_) >> errorStatus
		errorStatus.isErrorExists() >> false

		//for regCopy
		1*gsRep.getDataSource()  >> DS
		1*DS.getConnection() >> con
		con.prepareCall(BBBGiftRegistryConstants.COPY_REGISTRY) >> cs
		pGiftRegistryViewBean.getSiteFlag() >> "1"
		pGiftRegistryViewBean.getSourceRegistry() >> "50"
		pGiftRegistryViewBean.getTargetRegistry() >> "20"
		2*catalogTools.getAllValuesForKey( BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG,BBBCoreConstants.LAST_MAINT_PROGRAM) >> []
		1*catalogTools.getAllValuesForKey( BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,"DefaultStoreId") >> ["5"]
		1*catalogTools.getAllValuesForKey( BBBGiftRegistryConstants.GIFT_REGISTRY_CONFIG_TYPE,BBBGiftRegistryConstants.CONFIG_KEY_ROW_XNG_USR) >> ["str"]
		cs.getInt(8) >> "st"
		1*utils.logAndFormatError("regCopy", null, "serviceErrorVO", _,pGiftRegistryViewBean.getUserToken(),pGiftRegistryViewBean.getSiteFlag(),
				pGiftRegistryViewBean.getSourceRegistry(),pGiftRegistryViewBean.getTargetRegistry()) >> errorVo
		1*con.close() >> {throw new SQLException("")}
		//end regCopy

		when:
		RegCopyResVO regCopyResVO = tools.copyRegistryInEcomAdmin(pGiftRegistryViewBean)

		then:
		1*con.setAutoCommit(false)
		1*cs.setString(1, "1")
		1*cs.setInt(2,50)
		1*cs.setInt(3,20)
		1*cs.setString(4,"BBBY.COM")
		1*cs.setInt(5, 5)
		1*cs.setString(6,"BBBY.COM")
		1*cs.setString(7, "str")
		1*cs.registerOutParameter(8, java.sql.Types.INTEGER);
		1*cs.executeUpdate()
		1*tools.logError("Error occurred while updating registry",_)
		1*tools.logError("Error occurred while closing connection",_);
		regCopyResVO.gettotalNumOfItemsCopied() == null
		regCopyResVO.getServiceErrorVO()==errorVo
	}

	def"copyRegistryInEcomAdmin, when isErrorExists is true"(){

		given:
		GiftRegistryViewBean pGiftRegistryViewBean =Mock()
		RegCopyResVO resVo =Mock()
		ErrorStatus errorStatus =Mock()
		1*utils.validateInput(_) >> errorStatus
		errorStatus.isErrorExists() >> true
		errorStatus.getErrorId() >> 1
		errorStatus.getErrorMessage() >> "error"

		when:
		RegCopyResVO regCopyResVO = tools.copyRegistryInEcomAdmin(pGiftRegistryViewBean)

		then:
		regCopyResVO.gettotalNumOfItemsCopied() == null
		regCopyResVO.getServiceErrorVO().isErrorExists() == true
		regCopyResVO.getServiceErrorVO().getErrorId() == 1
		regCopyResVO.getServiceErrorVO().getErrorMessage() == "error"
	}

	def"getPrintAtHomeCardTemplates, gets Print At home card templates"(){

		given:
		String pSiteId = "tbs"
		QueryBuilder qb =Mock()
		QueryExpression qe =Mock()
		Query query =Mock()

		1*pPrintAtHomeRepository.getView("printAtHome") >> view
		view.getQueryBuilder() >> qb
		qb.createConstantQueryExpression(pSiteId) >> qe
		qb.createPropertyQueryExpression("sites") >> qe
		qb.createComparisonQuery(qe, qe, QueryBuilder.EQUALS) >>query
		view.executeQuery(query) >> []

		when:
		RepositoryItem[] r = tools.getPrintAtHomeCardTemplates(pSiteId)

		then:
		r==[]
	}

	def"getPrintAtHomeCardTemplates, when RepositoryException is thrown"(){

		given:
		tools.setLoggingError(true)
		String pSiteId = "tbs"
		QueryBuilder qb =Mock()
		QueryExpression qe =Mock()
		Query query =Mock()

		1*pPrintAtHomeRepository.getView("printAtHome") >> view
		view.getQueryBuilder() >> qb
		qb.createConstantQueryExpression(pSiteId) >> qe
		qb.createPropertyQueryExpression("sites") >> qe
		qb.createComparisonQuery(qe, qe, QueryBuilder.EQUALS) >>query
		view.executeQuery(query) >> {throw new RepositoryException("")}

		when:
		RepositoryItem[] r = tools.getPrintAtHomeCardTemplates(pSiteId)

		then:
		r==null
	}

	def"getThumbnailTemplateDetails, gets Print At home card templates"(){

		given:
		String pId = "tbs"
		QueryBuilder qb =Mock()
		QueryExpression qe =Mock()
		Query query =Mock()

		1*pPrintAtHomeRepository.getView("announcementcard") >> view
		view.getQueryBuilder() >> qb
		qb.createConstantQueryExpression(pId) >> qe
		qb.createPropertyQueryExpression("id") >> qe
		qb.createComparisonQuery(qe, qe, QueryBuilder.EQUALS) >>query
		view.executeQuery(query) >> []

		when:
		RepositoryItem[] r = tools.getThumbnailTemplateDetails(pId)

		then:
		r==[]
	}

	def"getThumbnailTemplateDetails, when RepositoryException is thrown"(){

		given:
		tools.setLoggingError(true)
		String pId = "tbs"
		QueryBuilder qb =Mock()
		QueryExpression qe =Mock()
		Query query =Mock()

		1*pPrintAtHomeRepository.getView("announcementcard") >> view
		view.getQueryBuilder() >> qb
		qb.createConstantQueryExpression(pId) >> qe
		qb.createPropertyQueryExpression("id") >> qe
		qb.createComparisonQuery(qe, qe, QueryBuilder.EQUALS) >>query
		view.executeQuery(query) >> {throw new RepositoryException("")}

		when:
		RepositoryItem[] r = tools.getThumbnailTemplateDetails(pId)

		then:
		r==null
	}

	def"createRegistryRecommendationsItem, when registryRecommendationItem is null"(){

		given:
		setParametrsForSpy()
		RecommendationRegistryProductVO recommendationItemVO =Mock()
		1*mRep.getItemForUpdate(recommendationItemVO.getRegistryId(),BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> null

		when:
		tools.createRegistryRecommendationsItem(recommendationItemVO)

		then:
		1*tools.logError("Registry Recommanation is not yet invited")
	}

	def"createRegistryRecommendationsItem, when rocommendationItems is null"(){

		given:
		RecommendationRegistryProductVO recommendationItemVO =Mock()
		MutableRepositoryItem registryRecommendationItem =Mock()
		1*mRep.getItemForUpdate(recommendationItemVO.getRegistryId(),BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem
		1*registryRecommendationItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS) >> null

		when:
		tools.createRegistryRecommendationsItem(recommendationItemVO)

		then:
		true == true
	}

	def"createRegistryRecommendationsItem, create the Registry Recommendations Item"(){

		given:
		RecommendationRegistryProductVO recommendationItemVO =Mock()
		MutableRepositoryItem registryRecommendationItem =Mock()
		MutableRepositoryItem registryRecommendationProdctItem =Mock()
		MutableRepositoryItem r1 =Mock()
		RepositoryItem ritem1 =Mock()
		1*mRep.getItemForUpdate(recommendationItemVO.getRegistryId(),BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem

		Set<RepositoryItem> set = new HashSet()
		set.add(r1)
		1*registryRecommendationItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS) >> set
		r1.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID) >> ritem1
		ritem1.getRepositoryId() >> "rItemId"
		recommendationItemVO.getRecommenderProfileId() >> "rItemId"

		Set<RepositoryItem> set1 = new HashSet()
		r1.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS) >> set1

		1*mRep.createItem(BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> registryRecommendationProdctItem

		when:
		tools.createRegistryRecommendationsItem(recommendationItemVO)

		then:
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_SKU,recommendationItemVO.getSkuId());
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_QUANTITY,recommendationItemVO.getRecommendedQuantity());
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDATION_COMMENT,recommendationItemVO.getComment());
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_DATE, _);
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0L)
		1*mRep.addItem(registryRecommendationProdctItem)
		set1.contains(registryRecommendationProdctItem)
		1*r1.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS, set1)
		1*registryRecommendationItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS,set)
		1*mRep.updateItem(registryRecommendationItem)
	}

	/*def"createRegistryRecommendationsItem, when currentRecommondationItem is null"(){

		given:
		RecommendationRegistryProductVO recommendationItemVO =Mock()
		MutableRepositoryItem registryRecommendationItem =Mock()
		MutableRepositoryItem registryRecommendationProdctItem =Mock()
		MutableRepositoryItem r1 =Mock()
		RepositoryItem ritem1 =Mock()
		1*mRep.getItemForUpdate(recommendationItemVO.getRegistryId(),BBBGiftRegistryConstants.REGISTRY_RECOMMENDATIONS) >> registryRecommendationItem

		Set<RepositoryItem> set = new HashSet()
		set.add(r1)
		1*registryRecommendationItem.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS) >> set
		r1.getPropertyValue(BBBGiftRegistryConstants.INVITEE_PROFILE_ID) >> ritem1
		ritem1.getRepositoryId() >> "rItemId"
		recommendationItemVO.getRecommenderProfileId() >> "Id"

		Set<RepositoryItem> set1 = new HashSet()
		r1.getPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS) >> set1

		1*mRep.createItem(BBBGiftRegistryConstants.REGISTRY_RECOMMONDATION_PRODUCTS) >> registryRecommendationProdctItem

		when:
		tools.createRegistryRecommendationsItem(recommendationItemVO)

		then:
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_SKU,recommendationItemVO.getSkuId());
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_QUANTITY,recommendationItemVO.getRecommendedQuantity());
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDATION_COMMENT,recommendationItemVO.getComment());
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDED_DATE, _);
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.DECLINED2, 0);
		1*registryRecommendationProdctItem.setPropertyValue(BBBGiftRegistryConstants.ACCEPTED_QUANTITY, 0L)
		1*mRep.addItem(registryRecommendationProdctItem)
		1*r1.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDATIONS, _)
		1*registryRecommendationItem.setPropertyValue(BBBGiftRegistryConstants.RECOMMENDER_ITEMS,set)
		1*mRep.updateItem(registryRecommendationItem)
	}*/
}


