package atg.servlet;

import atg.userprofiling.ProfileRequestLocale;

public class BBBRequestLocale extends ProfileRequestLocale {

//	private static String COUNTRY_LOCALE_COOKIE = "country-locale-cookie";
	
	private String mSelectedCurrency;
	
	public String getSelectedCurrency() {
		return mSelectedCurrency;
	}

	public void setSelectedCurrency(String pSelectedCurrency) {
		mSelectedCurrency = pSelectedCurrency;
	}

	
}
