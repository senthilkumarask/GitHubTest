/**
 * PricingServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
package com.bbb.commerce.service.pricing;

/**
 * PricingServiceMessageReceiverInOut message receiver
 */

public class PricingServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {

	public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext) throws org.apache.axis2.AxisFault {

		try {

			// get the implementation class for the Web Service
			Object obj = getTheImplementationObject(msgContext);

			PricingService skel = (PricingService) obj;
			// Out Envelop
			org.apache.axiom.soap.SOAPEnvelope envelope = null;
			// Find the axisOperation that has been set by the Dispatch phase.
			org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
			if (op == null) {
				throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
			}

			java.lang.String methodName;
			if ((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)) {

				if ("performPricing".equals(methodName)) {

					com.bedbathandbeyond.atg.PricingResponseDocument pricingResponse3 = null;
					com.bedbathandbeyond.atg.PricingRequestDocument wrappedParam = (com.bedbathandbeyond.atg.PricingRequestDocument) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
							com.bedbathandbeyond.atg.PricingRequestDocument.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

					pricingResponse3 =

					skel.performPricing(wrappedParam);

					envelope = toEnvelope(getSOAPFactory(msgContext), pricingResponse3, false, new javax.xml.namespace.QName(
							"http://atg.bedbathandbeyond.com/interfaces/PricingService/v1/PricingService.wsdl", "performPricing"));

				} else {
					throw new java.lang.RuntimeException("method not found");
				}

				newMsgContext.setEnvelope(envelope);
			}
		} catch (SoapFault e) {

			msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "pricingError");
			org.apache.axis2.AxisFault f = createAxisFault(e);
			if (e.getFaultMessage() != null) {
				f.setDetail(toOM(e.getFaultMessage(), false));
			}
			throw f;
		}

		catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	//

	private org.apache.axiom.om.OMElement toOM(com.bedbathandbeyond.atg.PricingRequestDocument param, boolean optimizeContent) throws org.apache.axis2.AxisFault {

		return toOM(param);

	}

	private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.PricingRequestDocument param) throws org.apache.axis2.AxisFault {

		org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
		xmlOptions.setSaveNoXmlDecl();
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setSaveNamespacesFirst();
		org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(
				param, xmlOptions), new org.xml.sax.InputSource()));
		try {
			return builder.getDocumentElement(true);
		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.bedbathandbeyond.atg.PricingResponseDocument param, boolean optimizeContent) throws org.apache.axis2.AxisFault {

		return toOM(param);

	}

	private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.PricingResponseDocument param) throws org.apache.axis2.AxisFault {

		org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
		xmlOptions.setSaveNoXmlDecl();
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setSaveNamespacesFirst();
		org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(
				param, xmlOptions), new org.xml.sax.InputSource()));
		try {
			return builder.getDocumentElement(true);
		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.om.OMElement toOM(com.bedbathandbeyond.atg.PricingErrorDocument param, boolean optimizeContent) throws org.apache.axis2.AxisFault {

		return toOM(param);

	}

	private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.PricingErrorDocument param) throws org.apache.axis2.AxisFault {

		org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
		xmlOptions.setSaveNoXmlDecl();
		xmlOptions.setSaveAggressiveNamespaces();
		xmlOptions.setSaveNamespacesFirst();
		org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(
				param, xmlOptions), new org.xml.sax.InputSource()));
		try {
			return builder.getDocumentElement(true);
		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.PricingResponseDocument param, boolean optimizeContent,
			javax.xml.namespace.QName methodQName) throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null) {
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	/**
	 * get the default envelope
	 */
	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
		return factory.getDefaultEnvelope();
	}

	public org.apache.xmlbeans.XmlObject fromOM(org.apache.axiom.om.OMElement param, java.lang.Class type, java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {
		try {

			if (com.bedbathandbeyond.atg.PricingRequestDocument.class.equals(type)) {
				if (extraNamespaces != null) {
					return com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				} else {
					return com.bedbathandbeyond.atg.PricingRequestDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
				}
			}

			if (com.bedbathandbeyond.atg.PricingResponseDocument.class.equals(type)) {
				if (extraNamespaces != null) {
					return com.bedbathandbeyond.atg.PricingResponseDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				} else {
					return com.bedbathandbeyond.atg.PricingResponseDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
				}
			}

			if (com.bedbathandbeyond.atg.PricingErrorDocument.class.equals(type)) {
				if (extraNamespaces != null) {
					return com.bedbathandbeyond.atg.PricingErrorDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				} else {
					return com.bedbathandbeyond.atg.PricingErrorDocument.Factory.parse(param.getXMLStreamReaderWithoutCaching());
				}
			}

		} catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
		return null;
	}

	/**
	 * A utility method that copies the namepaces from the SOAPEnvelope
	 */
	private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env) {
		java.util.Map returnMap = new java.util.HashMap();
		java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
		while (namespaceIterator.hasNext()) {
			org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
			returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
		}
		return returnMap;
	}

	private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
		org.apache.axis2.AxisFault f;
		Throwable cause = e.getCause();
		if (cause != null) {
			f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
		} else {
			f = new org.apache.axis2.AxisFault(e.getMessage());
		}

		return f;
	}

}// end of class
