/**
 * SoapFault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */

package com.bbb.commerce.service.pricing;

public class SoapFault extends java.lang.Exception {

	private static final long serialVersionUID = 1332216622144L;

	private com.bedbathandbeyond.atg.PricingErrorDocument faultMessage;

	public SoapFault() {
		super("SoapFault");
	}

	public SoapFault(java.lang.String s) {
		super(s);
	}

	public SoapFault(java.lang.String s, java.lang.Throwable ex) {
		super(s, ex);
	}

	public SoapFault(java.lang.Throwable cause) {
		super(cause);
	}

	public void setFaultMessage(com.bedbathandbeyond.atg.PricingErrorDocument msg) {
		faultMessage = msg;
	}

	public com.bedbathandbeyond.atg.PricingErrorDocument getFaultMessage() {
		return faultMessage;
	}
}
