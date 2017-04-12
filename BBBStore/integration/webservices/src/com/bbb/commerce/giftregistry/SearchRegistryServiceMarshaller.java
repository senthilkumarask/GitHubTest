package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import atg.core.util.StringUtils;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.RegSearchWithFilterDocument;
import com.bedbathandbeyond.www.RegSearchWithFilterDocument.RegSearchWithFilter;
import com.bedbathandbeyond.www.RegSortSeqOrder;
import com.bedbathandbeyond.www.RegistrySortType;
/**
 * 
 * This class contain methods used for marshalling the webservice request.
 * 
 * @author ssha53
 * 
 */
public class SearchRegistryServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String FILTER1_PREFIX = "state:";
	private static final String FILTER2_PREFIX = "eventType:";
	private static final String FILTERS_SEPARATOR = ";";
	
	/**
	 * This method is used to build a request.
	 */
	public XmlObject buildRequest(ServiceRequestIF pRreqVO)
			throws BBBBusinessException, BBBSystemException {

		BBBPerformanceMonitor
				.start("SearchRegistryServiceMarshaller-buildRequest");
		
		RegSearchWithFilterDocument regSearchDocument = null;

		regSearchDocument = RegSearchWithFilterDocument.Factory.newInstance();
		regSearchDocument.setRegSearchWithFilter(validateSearchType(pRreqVO));

		BBBPerformanceMonitor
				.end("SearchRegistryServiceMarshaller-buildRequest");

		return regSearchDocument;
		
	
	}

	/**
	 * This methos is used to map a page specific VO to web service VO.
	 * 
	 * @param validateAddressReqVO
	 *            the validate address req vo
	 * 
	 * @return the validate address type
	 * 
	 * @throws Exception
	 *             the exception
	 */
	private RegSearchWithFilter validateSearchType(ServiceRequestIF pReqVO) {

		BBBPerformanceMonitor
				.start("SearchRegistryServiceMarshaller-buildValidateAddressType");

		RegSearchWithFilter regSearch = RegSearchWithFilter.Factory.newInstance();
//		Mapper mapper = DozerBeanMapperSingletonWrapper.getInstance();
//		mapper.map(pReqVO, regSearch);
		int blkSize =24;
		if (((RegistrySearchVO)pReqVO).getBlkSize()>1)
		{
			blkSize=((RegistrySearchVO)pReqVO).getBlkSize();
		}
		regSearch.setBlkSize(blkSize);
		regSearch.setFirstName(((RegistrySearchVO)pReqVO).getFirstName());
		regSearch.setIsGiftGiver(((RegistrySearchVO)pReqVO).getGiftGiver());
		regSearch.setLastName(((RegistrySearchVO)pReqVO).getLastName());
		regSearch.setStartIdx(((RegistrySearchVO)pReqVO).getStartIdx());
		regSearch.setUserToken(((RegistrySearchVO)pReqVO).getUserToken());
		regSearch.setSiteFlag(((RegistrySearchVO)pReqVO).getSiteId());
		regSearch.setRegistryNum(((RegistrySearchVO)pReqVO).getRegistryId());
		regSearch.setEmailAddr(((RegistrySearchVO)pReqVO).getEmail());
		regSearch.setReturnLegacyRegistries(((RegistrySearchVO)pReqVO).isReturnLeagacyRegistries());
		regSearch.setExcludedRegNums(((RegistrySearchVO)pReqVO).getExcludedRegNums());
		String filterOptions= "", filterOptions1 = "", filterOptions2 =  "";
		if(!StringUtils.isEmpty(((RegistrySearchVO)pReqVO).getState())) {
			filterOptions = FILTER1_PREFIX+((RegistrySearchVO)pReqVO).getState();
		}
		if(!StringUtils.isEmpty(((RegistrySearchVO)pReqVO).getEvent())) {
			if(!StringUtils.isEmpty(filterOptions)) {
				filterOptions = filterOptions + FILTERS_SEPARATOR;
			}
			filterOptions = filterOptions + FILTER2_PREFIX+((RegistrySearchVO)pReqVO).getEvent();
		}
		regSearch.setFilterOptions(filterOptions);
		
		
		//if 0 put 1 for default
		String sortSeq="NAME";
		if (!StringUtils.isEmpty(((RegistrySearchVO)pReqVO).getSort()))
		{
			sortSeq=((RegistrySearchVO)pReqVO).getSort();
		}
		
		regSearch.setSortSeq(RegistrySortType.Enum.forString(sortSeq));
		
		String sortSeqOrder = ((RegistrySearchVO)pReqVO).getSortSeqOrder();
		if (StringUtils.isEmpty(sortSeqOrder)){
			sortSeqOrder = "ASCE";
		}
		regSearch.setSortSeqOrder(RegSortSeqOrder.Enum.forString(sortSeqOrder));
		if (((RegistrySearchVO)pReqVO).isReturnLeagacyRegistries())
		{
		regSearch.setProfileID(((RegistrySearchVO)pReqVO).getProfileId().getRepositoryId());
		}
		BBBPerformanceMonitor
				.end("SearchRegistryServiceMarshaller-buildValidateAddressType");

		return regSearch;
	}

}