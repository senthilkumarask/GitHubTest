
package com.bbb.commerce.catalog;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

import atg.multisite.SiteContextManager;

import com.bbb.account.vo.CouponListVo;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.constants.TBSConstants;

/**
 * @author 
 *
 */
public class CouponsComparator implements Comparator<CouponListVo>{

	/**
	 * This method is used to compare delivery charge in ShipMethodVO
	 */
	@Override
	public int compare(final CouponListVo obj1,
			final CouponListVo obj2) {

		DateFormat formatter ;
		Date date1=null;
		Date date2=null;
		String expDate1 = obj1.getExpiryDate();
		String expDate2 = obj2.getExpiryDate();
		String siteId = extractSiteId();
		if(siteId.equalsIgnoreCase(BBBCoreConstants.SITE_BAB_CA) || siteId.equalsIgnoreCase(TBSConstants.SITE_TBS_BAB_CA)){
			formatter = new SimpleDateFormat("dd/MM/yyyy");
		}else{
			formatter = new SimpleDateFormat("MM/dd/yyyy");
		}
		if(expDate1!=null && expDate2!=null){
			try {
				date1 = formatter.parse(expDate1);
				date2 = formatter.parse(expDate2);
				if ((date1.compareTo(date2) == -1)) {
					return -1;
				} else if((date1.compareTo(date2) == 1)){
					return 1;
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else{
			return 0;
		}
		return 0;

	}

	/**
	 * @return
	 */
	protected String extractSiteId() {
		return SiteContextManager.getCurrentSiteId();
	}

}