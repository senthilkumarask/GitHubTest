package com.bbb.commerce.giftregistry.vo;

import java.io.Serializable;
/*import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;*/
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;

//import com.bbb.certona.vo.CertonaGiftRegistryVO;

//import atg.core.util.StringUtils;



// TODO: Auto-generated Javadoc
/**
 * This class provides the Registry summary information properties.
 *
 * @author sku134
 */
public class RegSkuListVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<RegSkuDetailsVO> regAddressVO=new ArrayList<RegSkuDetailsVO>();

	public List<RegSkuDetailsVO> getRegAddressVO() {
		return regAddressVO;
	}

	public void setRegAddressVO(List<RegSkuDetailsVO> regAddressVO) {
		this.regAddressVO = regAddressVO;
	}

	
}