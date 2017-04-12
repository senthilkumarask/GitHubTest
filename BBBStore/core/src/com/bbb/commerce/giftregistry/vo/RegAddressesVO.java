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
public class RegAddressesVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<RegNamesVO> regAddressVO=new ArrayList<RegNamesVO>();

	public List<RegNamesVO> getRegAddressVO() {
		return regAddressVO;
	}

	public void setRegAddressVO(List<RegNamesVO> regAddressVO) {
		this.regAddressVO = regAddressVO;
	}
	
}