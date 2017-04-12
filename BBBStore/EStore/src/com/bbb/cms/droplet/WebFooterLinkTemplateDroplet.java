package com.bbb.cms.droplet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;

import com.bbb.cms.BannerVO;
import com.bbb.cms.WebFooterTemplateVO;
import com.bbb.cms.manager.ContentTemplateManager;
import com.bbb.cms.vo.CMSResponseVO;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.constants.BBBCmsConstants;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.utils.BBBUtility;

/**
 * This Class is used to populate footer data on the site
 */

public class WebFooterLinkTemplateDroplet extends BBBDynamoServlet {

	private ContentTemplateManager mContentTemplateMgr;
	private static final String ARRAY_SEPARETOR = "}";
	private static final String ARRAY_SEPARETORSTART = "{";
	/**
	 * @return the mStaticTemplateManager
	 */
	public ContentTemplateManager getContentTemplateMgr() {
		return mContentTemplateMgr;
	}

	/**
	 * @param pStaticTemplateManager
	 *            the mStaticTemplateManager to set
	 */
	public void setContentTemplateMgr(
			ContentTemplateManager pStaticTemplateManager) {
		mContentTemplateMgr = pStaticTemplateManager;
	}

	/**
	 *  This Class is used to populate footer links.
	 */
	@Override
	public void service(DynamoHttpServletRequest pRequest,
			DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug("starting method WebFooterLinkTemplateDroplet");
		final String templateName = BBBCoreConstants.WEB_FOOTER_LINK_TEMPLATE;
		try {
			String jsonString = ARRAY_SEPARETORSTART + ARRAY_SEPARETOR;
			if (BBBUtility.isEmpty(pRequest
					.getParameter(BBBCoreConstants.CHANNEL))
					&& ServletUtil.getCurrentRequest() != null) {
				ServletUtil.getCurrentRequest().setParameter(
						BBBCoreConstants.CHANNEL,
						BBBCoreConstants.DEFAULT_CHANNEL_VALUE);
			}
			List<WebFooterTemplateVO> footerVO = new ArrayList<WebFooterTemplateVO>();
			final CMSResponseVO responseVO = getContentTemplateMgr()
					.getContent(templateName, jsonString);
			if (responseVO != null && responseVO.getResponseItems() != null) {
				for (RepositoryItem item : responseVO.getResponseItems()) {
					WebFooterTemplateVO vo = new WebFooterTemplateVO();
					vo.setFooterName((String) item
							.getPropertyValue(BBBCoreConstants.FOOTERNAME));
					List<RepositoryItem> list = (List) item
							.getPropertyValue(BBBCoreConstants.LINKS);
					List<BannerVO> banners = new ArrayList<BannerVO>();
					for (RepositoryItem link : list) {
						BannerVO banner = new BannerVO();
						banner.setBannerBackground((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_BACKGROUND));
						banner.setBannerLink((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_LINK));
						banner.setBannerText((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_TEXT));
						banner.setBannerForeground((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FOREGROUND));
						banner.setBannerFontSize((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FONTSIZE));
						banner.setBannerFontWeight((String) link
								.getPropertyValue(BBBCmsConstants.REST_HOMEPAGE_TEMPLATE_BANNER_FONTWEIGHT));
						banners.add(banner);
					}
					vo.setLinks(banners);
					footerVO.add(vo);
				}
				pRequest.setParameter(BBBCmsConstants.STATIC_TEMPLATE_DATA,
						footerVO);
				pRequest.serviceParameter(BBBCmsConstants.OUTPUT, pRequest,
						pResponse);
			} else {

				pRequest.serviceParameter(BBBCmsConstants.EMPTY, pRequest,
						pResponse);
			}
		} catch (RepositoryException repException) {

			logError(
					"catalog_1052: Repository Exception WebFooterLinkTemplateDroplet",
					repException);

		} catch (BBBSystemException e) {
			logError(
					"catalog_1052: BBBSystem Exception WebFooterLinkTemplateDroplet",
					e);
		} catch (BBBBusinessException e) {
			logError(
					"catalog_1052: BBBBusiness Exception WebFooterLinkTemplateDroplet",
					e);
		}

		logDebug("Existing method WebFooterLinkTemplateDroplet");

	}
}
