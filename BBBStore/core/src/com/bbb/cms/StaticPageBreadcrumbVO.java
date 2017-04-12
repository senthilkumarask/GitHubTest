package com.bbb.cms;

import java.io.Serializable;
/*import java.util.List;
import java.util.Map;*/

public class StaticPageBreadcrumbVO implements Serializable   {
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String bbbPageName;
private String pageTitle;


public String getBbbPageName() {
	return bbbPageName;
}
public void setBbbPageName(String bbbPageName) {
	this.bbbPageName = bbbPageName;
}
public String getPageTitle() {
	return pageTitle;
}
public void setPageTitle(String pageTitle) {
	this.pageTitle = pageTitle;
}



}