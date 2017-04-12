package com.bbb.commerce.giftregistry.droplet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;

// TODO: Auto-generated Javadoc
/**
 * The Class POBoxValidateDroplet.
 */
public class POBoxValidateDroplet extends BBBDynamoServlet {

	/** The Constant OPARAM_OUTPUT. */
	public final static String OPARAM_OUTPUT = "output";

	/** The Constant OPARAM_ISVALID. */
	public final static String OPARAM_ISVALID = "isValid";

	/** The Constant PARAM_ADDRESS. */
	public final static String PARAM_ADDRESS = "address";

	/** The pattern. */
	private String pattern;

	/** The is valid. */
	boolean isValid;

	/**
	 * Gets the pattern.
	 * 
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * Sets the pattern.
	 * 
	 * @param pattern
	 *            the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * This method get the address from the jsp and checks if the address is a
	 * PO BOX Address.
	 * 
	 * @param pRequest
	 *            the request
	 * @param pResponse
	 *            the response
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		logDebug(" POBoxValidateDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - start");

		final String address = pRequest.getParameter(PARAM_ADDRESS);

		Pattern regex = Pattern.compile(getPattern(), Pattern.DOTALL);
				
		if (null != address) {
			Matcher regexMatcher = regex.matcher(address);
			isValid = regexMatcher.find();
		}
		pRequest.setParameter(OPARAM_ISVALID, isValid);
		pRequest.serviceLocalParameter(OPARAM_OUTPUT, pRequest, pResponse);

		logDebug(" POBoxValidateDroplet service(DynamoHttpServletRequest, DynamoHttpServletResponse) - End");
	}

}
