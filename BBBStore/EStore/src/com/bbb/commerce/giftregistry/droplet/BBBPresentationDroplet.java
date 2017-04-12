package com.bbb.commerce.giftregistry.droplet;

import com.bbb.common.BBBDynamoServlet;

// TODO: Auto-generated Javadoc
/**
 * This is the Common Droplet extend by all the newly created droplets.
 * 
 * @author ssha53
 *
 */
public class BBBPresentationDroplet extends BBBDynamoServlet{
	
	/** The oparam name rendered once during processing. */
	public static final String OPARAM_OUTPUT = "output";

	/** The Constant OPARAM_ERROR. */
	public static final String OPARAM_ERROR = "error";

	/** The Constant OPARAM_EMPTY. */
	public static final String OPARAM_EMPTY = "empty";	
	
	/** The Constant OUTPUT_ERROR_MSG. */
	public static final String OUTPUT_ERROR_MSG = "errorMsg";
	
	/**
	 * Instantiates a new bBB presentation droplet.
	 */
	public BBBPresentationDroplet(){
		super();
	}
}
