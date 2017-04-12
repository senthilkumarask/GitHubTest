/*
 *
 * File  : BBBPerformanceMonitor.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.performance;

import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.Nucleus;
import atg.service.perfmonitor.PerfStackMismatchException;
import atg.service.perfmonitor.PerformanceMonitor;
import atg.servlet.ServletUtil;

import com.bbb.framework.performance.PerfMonitorVO.PerfTimesVO;
import com.bbb.framework.performance.logger.PerformanceLogger;


/**
 * TODO DOCUMENT ME!
 *
 * 
 * @version
 */
public final class BBBPerformanceMonitor {
    //~ Static variables/initializers ------------------------------------------

    /** TODO DOCUMENT ME! */
    private static final String PERF_MONITOR_PREFIX = "bbb_";

    /** TODO DOCUMENT ME! */
    private static ThreadLocal<PerfMonitorVO> perfMonitorThread =
        new ThreadLocal<PerfMonitorVO>();

    /** TODO DOCUMENT ME! */
    private static PerformanceLogger performanceLogger = null;

    
    private BBBPerformanceMonitor(){
    	
    }
    
    //~ Methods ----------------------------------------------------------------

    /**
     *
     * @return StringUtils
     */
    private static String getFlowName() {
        if (perfMonitorThread.get() == null) {
            PerfMonitorVO perfMonitor = new PerfMonitorVO();
            perfMonitorThread.set(perfMonitor);
        }

        return StringUtils.isBlank(perfMonitorThread.get().getFlowName())
        ? "UNKNOWN"
        : perfMonitorThread.get().getFlowName();
    }


    /**
     * Allows the first caller in the request processing chain to set the flow
     * Name.
     *
     * @return
     * @param flowName the string
     */
    public static void setFlowName(final String flowName) {
        if (perfMonitorThread.get() == null) {
            PerfMonitorVO perfMonitor = new PerfMonitorVO();
            perfMonitorThread.set(perfMonitor);
        }

        perfMonitorThread.get().setFlowName(flowName);
    }


    /**
     * Start.
     *
     * @param operationName
     *            the operation name
     */
    public static void start(final String operationName) {
        String s = PERF_MONITOR_PREFIX + getFlowName();

        if (getPerfLoggerInstance().isEnableCustomComponentsMonitoring()) {
            PerformanceMonitor.startOperation(operationName, s);
            perfMonitorThread.get().start(operationName);

	        if (isPerformMonitorXMLEnabled() && (getPerfLoggerInstance() != null)) {
	            getPerfLoggerInstance()
	                .logInfo(
	                "PerformMonitorXML$<" + getFlowName() + "-" + operationName
	                + ">");
	        }
        }
    }


    /**
     *  TODO DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static boolean isPerformMonitorXMLEnabled() {
        // TODO Auto-generated method stub
        if (getPerfLoggerInstance() != null) {
            return getPerfLoggerInstance().isPerformMonitorXMLEnabled();
        } else {
            return false;
        }
    }


    /**
     * Start.
     *
     * @param operationName
     *            the operationName
     * @param parameter
     *            the webservice call
     */
    public static void start(
        final String operationName,
        final String parameter) {
        String s = PERF_MONITOR_PREFIX + getFlowName() + "_" + parameter;

        if (getPerfLoggerInstance().isEnableCustomComponentsMonitoring()) {
            PerformanceMonitor.startOperation(operationName, s);
            perfMonitorThread.get().start(operationName+"::"+parameter);
        }
    }


    /**
     * End.
     *
     * @param operationName
     *            the operation name
     */
    public static void end(final String operationName) {
        if (getPerfLoggerInstance().isEnableCustomComponentsMonitoring()) {
        	String s = PERF_MONITOR_PREFIX + getFlowName();
        	try{
        		PerformanceMonitor.endOperation(operationName, s);
        	}catch(PerfStackMismatchException pme){
        		if ((getPerfLoggerInstance() != null)
        				&& getPerfLoggerInstance().isLoggingError()) {
        			getPerfLoggerInstance().logError("PerfStackMismatchException for "+operationName);
        	    }
        	}
	        boolean isSuccess = perfMonitorThread.get().end(operationName);
	        if (!isSuccess && (getPerfLoggerInstance() != null)
	            && getPerfLoggerInstance().isLoggingError()) {
	            getPerfLoggerInstance().logError("PerfMonitorVO details not available 1 operationName:" + operationName);
	        }
	
	        if (isPerformMonitorXMLEnabled() && (getPerfLoggerInstance() != null)) {
	            getPerfLoggerInstance()
	                .logInfo(
	                "PerformMonitorXML$<elapsedtime>"
	                + perfMonitorThread.get().getTimeelapsed(operationName)
	                + "</elapsedtime>");
	            getPerfLoggerInstance()
	                .logInfo(
	                "PerformMonitorXML$</" + getFlowName() + "-" + operationName
	                + ">");
	        }
        }
    }


    /**
     * End.
     *
     * @param operationName
     *            the operationName
     * @param parameter
     *            the webservice call
     */
    public static void end(
        final String operationName,
        final String parameter) {
        String s = PERF_MONITOR_PREFIX + getFlowName() + "_" + parameter;
        
        if (getPerfLoggerInstance().isEnableCustomComponentsMonitoring()) {
        	try{
        		PerformanceMonitor.endOperation(operationName, s);
        	}catch(PerfStackMismatchException pme){
        		if ((getPerfLoggerInstance() != null)
        				&& getPerfLoggerInstance().isLoggingError()) {
        			getPerfLoggerInstance().logError("PerfStackMismatchException for "+operationName);
        	    }
        	}
	        boolean isSuccess = perfMonitorThread.get().end(operationName+"::"+parameter);
	
	        if (!isSuccess && (getPerfLoggerInstance() != null)
	            && getPerfLoggerInstance().isLoggingError()) {
	            getPerfLoggerInstance().logError("PerfMonitorVO details not available 2 operationName: " +operationName);
	        }
        }
  }


    /**
     * Cancel.
     *
     * @param operationName
     *            the operationName
     */
    public static void cancel(final String operationName) {
        String s = PERF_MONITOR_PREFIX + getFlowName() + "_";

        if (getPerfLoggerInstance().isEnableCustomComponentsMonitoring()) {
        	try{
            PerformanceMonitor.cancelOperation(operationName, s);
        	}catch(PerfStackMismatchException pme){
        		if ((getPerfLoggerInstance() != null)
        				&& getPerfLoggerInstance().isLoggingError()) {
        			getPerfLoggerInstance().logError("PerfStackMismatchException for "+operationName);
        	    }
        	}
        }
    }


    /**
     * Cancel.
     *
     * @param operationName
     *            the operationName
     * @param parameter
     *            the webservice call
     */
    public static void cancel(
        final String operationName,
        final String parameter) {
        String s = PERF_MONITOR_PREFIX + getFlowName() + "_" + parameter;

        if (getPerfLoggerInstance().isEnableCustomComponentsMonitoring()) {
        	try{
        		PerformanceMonitor.cancelOperation(operationName, s);
        	}catch(PerfStackMismatchException pme){
        		if ((getPerfLoggerInstance() != null)
        				&& getPerfLoggerInstance().isLoggingError()) {
        			getPerfLoggerInstance().logError("PerfStackMismatchException for "+operationName);
        	    }
        	}
        }
    }


    /**
     *  TODO DOCUMENT ME!
     */
	public static void printOut() {
		if (getPerfLoggerInstance().isEnableCustomComponentsMonitoring() && getPerfLoggerInstance() != null && (getPerfLoggerInstance().isLoggingError()
					&& (perfMonitorThread.get().getMapOfTimes() != null)
					&& !perfMonitorThread.get().getMapOfTimes().isEmpty())) {
				for (final Map.Entry<String, PerfTimesVO> entry : perfMonitorThread.get().getMapOfTimes().entrySet()) {
					if ((entry.getKey() != null) && (entry.getValue() != null)) {
						String flowName = (perfMonitorThread.get()
								.getFlowName() != null) ? perfMonitorThread
								.get().getFlowName() : "UNKNOWN";
						// TODO Need to change LogError to LogInfo in Production
						getPerfLoggerInstance().logInfo(
								"|" + flowName + "|" + entry.getKey() + "|NA|NA|"
										+ entry.getValue().getTimeElapsed()
										+ "|");
					}
				}
			}
		
		perfMonitorThread.set(null);
		perfMonitorThread.remove();
	}


    /**
     *  TODO DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static PerformanceLogger getPerfLoggerInstance() {
        if (performanceLogger != null) {
            return performanceLogger;
        } else {
            if (ServletUtil.getCurrentRequest() != null) {
                performanceLogger = (PerformanceLogger) ServletUtil.getCurrentRequest()
                                                                   .resolveName(
                        "/com/bbb/framework/performance/logger/PerformanceLogger");
            } else {
            	performanceLogger = (PerformanceLogger) Nucleus.getGlobalNucleus()
                .resolveName("/com/bbb/framework/performance/logger/PerformanceLogger");
            }
            return performanceLogger;
        }
    }
}
