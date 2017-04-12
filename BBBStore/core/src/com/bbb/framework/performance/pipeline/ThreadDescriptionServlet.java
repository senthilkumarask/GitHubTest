/**
 * 
 */
package com.bbb.framework.performance.pipeline;

import java.io.IOException;

import javax.servlet.ServletException;

import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.utils.BBBConfigRepoUtils;
import com.bbb.utils.BBBUtility;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;

/**
 * @author pprave
 * 
 */
public class ThreadDescriptionServlet extends InsertableServletImpl {

    /** TODO DOCUMENT ME!. */
    private static final String OVERALL = "OVERALL";

    /** TODO DOCUMENT ME!. */
    private String mProfilePath;

    /**
     * TODO DOCUMENT ME!.
     * 
     * @param pRequest
     *            DOCUMENT ME!
     * @param pResponse
     *            DOCUMENT ME!
     * 
     * @throws IOException
     *             DOCUMENT ME!
     * @throws ServletException
     *             DOCUMENT ME!
     */
    public void service(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse) throws IOException, ServletException {
        String origName = Thread.currentThread().getName();

        try {
            String jsessionid = pRequest.getSession().getId();
            String requestURI = pRequest.getRequestURI();
            String remoteAddr = pRequest.getRemoteAddr();
            String trueClientIpHeaderName = BBBConfigRepoUtils.getStringValue("CartAndCheckoutKeys", "TRUE_IP_HEADER");
            String trueClientIp=null;
			if (BBBUtility.isNotEmpty(trueClientIpHeaderName)) {
				trueClientIp = pRequest.getHeader(trueClientIpHeaderName);
			}
            String profileId = null;
            if (getProfilePath() != null) {
                Profile profile = (Profile) pRequest.resolveName(getProfilePath());

                if (profile != null) {
                    profileId = profile.getRepositoryId();
                }
            }
            Thread.currentThread().setName(origName + "trueip=["+ trueClientIp +"] client=[" + remoteAddr + "] uri=[" + requestURI + "] session=[" + jsessionid + "] profile=[" + profileId + "]");
            BBBPerformanceMonitor.setFlowName(getPageName(requestURI, pRequest));
            BBBPerformanceMonitor.start(OVERALL);
            passRequest(pRequest, pResponse);
        } finally {
            BBBPerformanceMonitor.end(OVERALL);
            BBBPerformanceMonitor.printOut();
            Thread.currentThread().setName(origName);
        }
    }

    /**
     * Gets the profile path.
     * 
     * @return the mProfilePath
     */
    public String getProfilePath() {
        return mProfilePath;
    }

    /**
     * Sets the profile path.
     * 
     * @param pMProfilePath
     *            the mProfilePath to set
     */
    public void setProfilePath(final String pMProfilePath) {
        this.mProfilePath = pMProfilePath;
    }

    /**
     * TODO DOCUMENT ME!.
     * 
     * @param uriString
     *            DOCUMENT ME!
     * @param pRequest
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private String getPageName(final String uriString,
            final DynamoHttpServletRequest pRequest) {
        String pageName = null; 
//      String tempString = null;

        if (!BBBUtility.isBlank(uriString) && (uriString.lastIndexOf("/") >= 0)) {          
            pageName = uriString.substring(uriString.lastIndexOf("/") + 1);
            if(uriString.indexOf("product") > -1) {
                pageName = "Product: " + pageName;
            } else if(uriString.indexOf("category") > -1) {
                pageName = "Category: " + pageName;
            }
        }

        if ((pageName != null) && pageName.equalsIgnoreCase("BaseAjaxServlet")) {
            pageName = pRequest.getParameter("pageContext");
            /*
             * if ((ajaxMappings.getAjaxHandlerMap().get(pageName)) != null) {
             * tempString = ajaxMappings.getAjaxHandlerMap().get(pageName);
             * pageName = tempString.substring( tempString.lastIndexOf("/") +
             * 1); } else { pageName = "UNKNOWN_BASEAJAXSERVLET"; }
             */}

        return pageName;
    }
}
