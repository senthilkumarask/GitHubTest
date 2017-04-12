package com.bbb.rest.test;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.multisite.SiteContextManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.common.BBBDynamoServlet;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.httpquery.HTTPQueryManager;
import com.bbb.kirsch.vo.ItemsFromWorkBookReqVO;
import com.bbb.kirsch.vo.ItemsFromWorkBookRespVO;
import com.bbb.tbs.framework.httpquery.TBSHTTPCallInvoker;

public class GetItemsFromWorkbookTestDroplet extends BBBDynamoServlet {

    private static final String THIRD_PARTY_URLS = "ThirdPartyURLs";

    private TBSHTTPCallInvoker mHttpCallInvoker;



    private BBBCatalogTools mCatalogTools;


	@Override
	public void service(DynamoHttpServletRequest pReq,
			DynamoHttpServletResponse pRes) throws ServletException,
			IOException {

        Object obj = null;
		ItemsFromWorkBookRespVO lBookRespVO = null;

		ItemsFromWorkBookReqVO lBookReqVO = new ItemsFromWorkBookReqVO();

		lBookReqVO.setServiceName("getItemsFromWorkbook");
		lBookReqVO.setServiceType("getItemsFromWorkbook");
		SiteContextManager.getCurrentSite().getId();
        String requestString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><getItemsFromWorkBookRequest><Access transkey=\"V11BBAAS52A119CJ39LQ078C06KS97DY\" api=\"1.0\" /><userID>kswamy@kpathsolutions.com</userID></getItemsFromWorkBookRequest>";
        try {

            String hostTargetURL = getCatalogTools().getConfigValueByconfigType(THIRD_PARTY_URLS).get("getItemsFromWorkbook");
		

			obj = getHttpCallInvoker().invokePostRequest(hostTargetURL, requestString);


		} catch (BBBSystemException e) {
				logError(e);
            pReq.serviceParameter("error", pReq, pRes);
		} catch (BBBBusinessException e) {
			logError(e);
            pReq.serviceParameter("error", pReq, pRes);
		}
        if(obj != null) {
            pReq.setParameter("obj", obj);
            pReq.serviceParameter("output", pReq, pRes);
        }

	}


    public TBSHTTPCallInvoker getHttpCallInvoker() {
        return mHttpCallInvoker;
    }

    public void setHttpCallInvoker(TBSHTTPCallInvoker pHttpCallInvoker) {
        this.mHttpCallInvoker = pHttpCallInvoker;
    }


    public BBBCatalogTools getCatalogTools() {
        return mCatalogTools;
    }

    public void setCatalogTools(BBBCatalogTools pCatalogTools) {
        this.mCatalogTools = pCatalogTools;
    }

}
