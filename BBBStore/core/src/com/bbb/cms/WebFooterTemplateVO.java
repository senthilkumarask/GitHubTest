/**
 * 
 */
package com.bbb.cms;

import java.io.Serializable;
import java.util.List;

import com.bbb.commerce.catalog.vo.ProductVO;

/**
 * @author iteggi
 *
 */
public class WebFooterTemplateVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public List<BannerVO> getLinks() {
		return links;
	}
	public void setLinks(List<BannerVO> links) {
		this.links = links;
	}
	public String getFooterName() {
		return footerName;
	}
	public void setFooterName(String footerName) {
		this.footerName = footerName;
	}
	private List<BannerVO> links;
	private String footerName;
	
}
