package com.bbb.internationalshipping.vo.checkoutrequest;

import java.io.Serializable;


/**
 * The Class IntlShippingErrorMessage will hold all error messages which getting from international checkout.
 */
public class IntlShippingErrorMessage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** The name. */
	protected String name;

	/** The field. */
	protected String field;

	/** The message. */
	protected String message;

	/** The details. */
	protected String details;

	/** The id. */
	protected String id;


	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param value the new name
	 */
	public void setName(final String value) {
		this.name = value;
	}


	/**
	 * Gets the field.
	 *
	 * @return the field
	 */
	public String getField() {
		return field;
	}


	/**
	 * Sets the field.
	 *
	 * @param value the new field
	 */
	public void setField(final String value) {
		this.field = value;
	}


	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * Sets the message.
	 *
	 * @param value the new message
	 */
	public void setMessage(final String value) {
		this.message = value;
	}


	/**
	 * Gets the details.
	 *
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}


	/**
	 * Sets the details.
	 *
	 * @param value the new details
	 */
	public void setDetails(final String value) {
		this.details = value;
	}


	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}


	/**
	 * Sets the id.
	 *
	 * @param value the new id
	 */
	public void setId(final String value) {
		this.id = value;
	}

}
