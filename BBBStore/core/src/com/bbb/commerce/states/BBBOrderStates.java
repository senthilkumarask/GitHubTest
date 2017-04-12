/**
 * 
 */
package com.bbb.commerce.states;

import atg.commerce.states.OrderStates;

/**
 * @author Pradeep Reddy
 *
 */
public class BBBOrderStates extends OrderStates {
	
    public static final String INVALID = "invalid";
    public static final String CANCELLED = "cancelled";
    public static final String CREATED = "created";
    public static final String SOURCING_FAILED = "sourcing_failed";
    public static final String SCHEDULING_FAILED = "scheduling_failed";
    public static final String ALLOCATION_FAILED = "allocation_failed";
    public static final String SOURCED = "sourced";
    public static final String PARTIALLY_ALLOCATED = "partially_allocated";
    public static final String ALLOCATED = "allocated";
    public static final String DO_PARTIALLY_ALLOCATED = "do_partially_allocated";
    public static final String DO_CREATED = "do_created";
    public static final String PARTIALLY_RELEASED = "partially_released";
    public static final String RELEASED = "released";
    public static final String PARTIALLY_DC_ALLOCATED = "partially_dc_allocated";
    public static final String DC_ALLOCATED = "dc_allocated";
    public static final String PARTIALLY_COMPLETED = "partially_completed";
    public static final String COMPLETED = "completed";
    public static final String OTHERS = "others";
    public static final String INTL_INCOMPLETE = "intl_incomlete";
}
