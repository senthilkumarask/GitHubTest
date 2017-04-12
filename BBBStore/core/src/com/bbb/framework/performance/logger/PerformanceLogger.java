/*
 *
 * File  : PerformanceLogger.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.performance.logger;

import atg.service.perfmonitor.PerformanceMonitor;

import com.bbb.common.BBBGenericService;
/**
 * TODO DOCUMENT ME!
 *
 * 
 * @version
 */
public class PerformanceLogger
    extends BBBGenericService {
    //~ Instance variables -----------------------------------------------------

    /** TODO DOCUMENT ME! */
    private boolean performMonitorXMLEnabled;
    private boolean mEnableCustomComponentsMonitoring;

    //~ Methods ----------------------------------------------------------------

    /**
     *  TODO DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isPerformMonitorXMLEnabled() {
        return performMonitorXMLEnabled;
    }


    /**
     *  TODO DOCUMENT ME!
     *
     * @param performMonitorXMLEnabled DOCUMENT ME!
     */
    public void setPerformMonitorXMLEnabled(
        final boolean performMonitorXMLEnabled) {
        this.performMonitorXMLEnabled = performMonitorXMLEnabled;
    }


    public boolean isEnableCustomComponentsMonitoring() {
        if(PerformanceMonitor.isEnabled()) {
            return true;
        }
        return mEnableCustomComponentsMonitoring;
    }


    public void setEnableCustomComponentsMonitoring(
            boolean enableCustomComponentsMonitoring) {
        this.mEnableCustomComponentsMonitoring = enableCustomComponentsMonitoring;
    }
}
