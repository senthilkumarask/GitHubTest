/*
 *
 * File  : PerfMonitorVO.java
 * Project:     BBB
 * 
 */
package com.bbb.framework.performance;

import java.util.Map;


// TODO: Auto-generated Javadoc
//import com.bbb.common.BBBGenericService;
/**
 * TODO DOCUMENT ME!.
 * 
 *
 * @version 1.0
 */
public class PerfMonitorVO {
    //~ Instance variables -----------------------------------------------------

    /** TODO DOCUMENT ME!. */
    private Map<String, PerfTimesVO> mapOfTimes =
        new java.util.HashMap<String, PerfTimesVO>();

    /** TODO DOCUMENT ME!. */
    private String flowName;

    /** TODO DOCUMENT ME!. */
    private String role;

    //~ Methods ----------------------------------------------------------------

    /**
     * TODO DOCUMENT ME!.
     * 
     * @return DOCUMENT ME!
     */
    public String getFlowName() {
        return flowName;
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @param flowName DOCUMENT ME!
     */
    public void setFlowName(final String flowName) {
        this.flowName = flowName;
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @return DOCUMENT ME!
     */
    public String getRole() {
        return role;
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @param role DOCUMENT ME!
     */
    public void setRole(final String role) {
        this.role = role;
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @param operationName DOCUMENT ME!
     */
    public void start(final String operationName) {
        PerfTimesVO perfTimesVO = new PerfTimesVO();
        perfTimesVO.setStartTime(System.currentTimeMillis());
        mapOfTimes.put(operationName, perfTimesVO);
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @param operationName DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean end(final String operationName) {
        PerfTimesVO perfTimesVO = mapOfTimes.get(operationName);

        if (perfTimesVO == null) {
            return false;
        }

        perfTimesVO.setEndTime(System.currentTimeMillis());

        return true;
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @param operationName DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public long getTimeelapsed(final String operationName) {
        PerfTimesVO perfTimesVO = mapOfTimes.get(operationName);

        if (perfTimesVO == null) {
            return -1;
        }

        return perfTimesVO.getEndTime() - perfTimesVO.getStartTime();
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public String toString() {
        return "PerfMonitorVO [flowName=" + flowName + ", mapOfTimes="
        + mapOfTimes + ", role=" + role + "]";
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @return DOCUMENT ME!
     */
    public Map<String, PerfTimesVO> getMapOfTimes() {
        return mapOfTimes;
    }

    //~ Inner Classes ----------------------------------------------------------

    /**
     * The Class PerfTimesVO.
     */
    public class PerfTimesVO {
        
        /** The end time. */
        private long endTime;
        
        /** The start time. */
        private long startTime;
        
        /** The time elapsed. */
        private long timeElapsed;

        /**
         * Gets the start time.
         * 
         * @return the start time
         */
        public long getStartTime() {
            return startTime;
        }


        /**
         * Sets the start time.
         * 
         * @param startTime the new start time
         */
        public void setStartTime(final long startTime) {
            this.startTime = startTime;
        }


        /**
         * Gets the end time.
         * 
         * @return the end time
         */
        public long getEndTime() {
            return endTime;
        }


        /**
         * Sets the end time.
         * 
         * @param endTime the new end time
         */
        public void setEndTime(final long endTime) {
            this.endTime = endTime;
            this.timeElapsed = endTime - startTime;
        }


        /**
         * Gets the time elapsed.
         * 
         * @return the time elapsed
         */
        public long getTimeElapsed() {
            return timeElapsed;
        }


        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "PerfTimesVO [endTime=" + endTime + ", startTime="
            + startTime + ", timeElapsed=" + timeElapsed + "]";
        }
    }
}
