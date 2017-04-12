package com.bbb.commerce.giftregistry;

import org.apache.xmlbeans.XmlObject;

import atg.core.util.StringUtils;

import com.bbb.commerce.giftregistry.vo.RegistrySearchVO;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.integration.ServiceRequestIF;
import com.bbb.framework.webservices.RequestMarshaller;
import com.bedbathandbeyond.www.GetRegistryItemList2Document;
import com.bedbathandbeyond.www.RegistryItemsSortType;
import com.bedbathandbeyond.www.RegistryItemsView;
import com.bedbathandbeyond.www.GetRegistryItemList2Document.GetRegistryItemList2;

/**
 * 
 * @author asi162 Amandeep Singh Dhammu
 *
 */
public class GetRegistryItemList2ServiceMarshaller extends RequestMarshaller {

	/**
	 * 
	 */
	private static final long serialVersionUID = 775777467632651349L;

	
	@Override
	public XmlObject buildRequest(ServiceRequestIF reqVO)
			throws BBBSystemException, BBBBusinessException {
		GetRegistryItemList2Document getRegistryItemList2Document = null;
		getRegistryItemList2Document = GetRegistryItemList2Document.Factory.newInstance();
		getRegistryItemList2Document.setGetRegistryItemList2(mapGetRegistryItemList2(reqVO));
		return getRegistryItemList2Document;
		
	}


	private GetRegistryItemList2 mapGetRegistryItemList2(ServiceRequestIF reqVO) {
		
		GetRegistryItemList2 getRegistryItemList2 = GetRegistryItemList2.Factory.newInstance();
		
		
		getRegistryItemList2.setBlkSize(((RegistrySearchVO)reqVO).getBlkSize());
		getRegistryItemList2.setIsGiftGiver(((RegistrySearchVO)reqVO).getGiftGiver());
		getRegistryItemList2.setStartIdx(((RegistrySearchVO)reqVO).getStartIdx());
		getRegistryItemList2.setUserToken(((RegistrySearchVO)reqVO).getUserToken());
		getRegistryItemList2.setSiteFlag(((RegistrySearchVO)reqVO).getSiteId());
		getRegistryItemList2.setRegistryNum(((RegistrySearchVO)reqVO).getRegistryId());
		getRegistryItemList2.setIsAvailForWebPurchaseFlag(((RegistrySearchVO)reqVO).getAvailForWebPurchaseFlag());	
		
		int view=1;
		if (((RegistrySearchVO)reqVO).getView()>0)
		{
			view=((RegistrySearchVO)reqVO).getView();
		}
		String sortSeq="DEPARTMENT";
		if (!StringUtils.isEmpty(((RegistrySearchVO)reqVO).getSortSeq()))
		{
			sortSeq=((RegistrySearchVO)reqVO).getSortSeq();
		}
		
		getRegistryItemList2.setView(RegistryItemsView.Enum.forInt(view));
		getRegistryItemList2.setSortSeq(RegistryItemsSortType.Enum.forString(sortSeq));
		
		
		return getRegistryItemList2;
	}
	
	
}
