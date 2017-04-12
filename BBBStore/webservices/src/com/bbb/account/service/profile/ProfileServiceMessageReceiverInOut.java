
/**
 * ProfileServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.1  Built on : Aug 31, 2011 (12:22:40 CEST)
 */
        package com.bbb.account.service.profile;

        /**
        *  ProfileServiceMessageReceiverInOut message receiver
        */

        public class ProfileServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


        public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
        throws org.apache.axis2.AxisFault{

        try {

        // get the implementation class for the Web Service
        Object obj = getTheImplementationObject(msgContext);

        ProfileService skel = (ProfileService)obj;
        //Out Envelop
        org.apache.axiom.soap.SOAPEnvelope envelope = null;
        //Find the axisOperation that has been set by the Dispatch phase.
        org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
        if (op == null) {
        throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
        }

        java.lang.String methodName;
        if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)){


        

            if("getProfileInfo".equals(methodName)){
                
                com.bedbathandbeyond.atg.ProfileInfoResponseVODocument profileInfoResponseVO17 = null;
	                        com.bedbathandbeyond.atg.ProfileInfoRequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.ProfileInfoRequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.ProfileInfoRequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               profileInfoResponseVO17 =
                                                   
                                                   
                                                         skel.getProfileInfo(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), profileInfoResponseVO17, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "getProfileInfo"));
                                    } else 

            if("createUser".equals(methodName)){
                
                com.bedbathandbeyond.atg.ProfileResponseVODocument profileResponseVO19 = null;
	                        com.bedbathandbeyond.atg.ProfileRequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.ProfileRequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.ProfileRequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               profileResponseVO19 =
                                                   
                                                   
                                                         skel.createUser(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), profileResponseVO19, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "createUser"));
                                    } else 

            if("linkCoRegistrant".equals(methodName)){
                
                com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument linkCoRegistrantResponseVO21 = null;
	                        com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               linkCoRegistrantResponseVO21 =
                                                   
                                                   
                                                         skel.linkCoRegistrant(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), linkCoRegistrantResponseVO21, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "linkCoRegistrant"));
                                    } else 

            if("login".equals(methodName)){
                
                com.bedbathandbeyond.atg.LoginResponseVODocument loginResponseVO23 = null;
	                        com.bedbathandbeyond.atg.LoginRequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.LoginRequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.LoginRequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               loginResponseVO23 =
                                                   
                                                   
                                                         skel.login(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), loginResponseVO23, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "login"));
                                    } else 

            if("getResponseVO".equals(methodName)){
                
                com.bedbathandbeyond.atg.ResponseVODocument responseVO25 = null;
	                        com.bedbathandbeyond.atg.RequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.RequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.RequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               responseVO25 =
                                                   
                                                   
                                                         skel.getResponseVO(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), responseVO25, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "getResponseVO"));
                                    } else 

            if("getBillingAddress".equals(methodName)){
                
                com.bedbathandbeyond.atg.BillingAddressResponseVODocument billingAddressResponseVO27 = null;
	                        com.bedbathandbeyond.atg.BillingAddressRequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.BillingAddressRequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.BillingAddressRequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               billingAddressResponseVO27 =
                                                   
                                                   
                                                         skel.getBillingAddress(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), billingAddressResponseVO27, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "getBillingAddress"));
                                    } else 

            if("linkProfileAndRegistry".equals(methodName)){
                
                com.bedbathandbeyond.atg.LinkProfileResponseVODocument linkProfileResponseVO29 = null;
	                        com.bedbathandbeyond.atg.LinkProfileRequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.LinkProfileRequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.LinkProfileRequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               linkProfileResponseVO29 =
                                                   
                                                   
                                                         skel.linkProfileAndRegistry(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), linkProfileResponseVO29, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "linkProfileAndRegistry"));
                                    } else 

            if("forgotPassword".equals(methodName)){
                
                com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument forgotPasswordResponseVO31 = null;
	                        com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument wrappedParam =
                                                             (com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument)fromOM(
                                    msgContext.getEnvelope().getBody().getFirstElement(),
                                    com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument.class,
                                    getEnvelopeNamespaces(msgContext.getEnvelope()));
                                                
                                               forgotPasswordResponseVO31 =
                                                   
                                                   
                                                         skel.forgotPassword(wrappedParam)
                                                    ;
                                            
                                        envelope = toEnvelope(getSOAPFactory(msgContext), forgotPasswordResponseVO31, false, new javax.xml.namespace.QName("http://atg.bedbathandbeyond.com/interfaces/ProfileService/v1/ProfileService.wsdl",
                                                    "forgotPassword"));
                                    
            } else {
              throw new java.lang.RuntimeException("method not found");
            }
        

        newMsgContext.setEnvelope(envelope);
        }
        }
        catch (java.lang.Exception e) {
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        }
        
        //

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.ProfileInfoRequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.ProfileInfoRequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.ProfileInfoResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.ProfileInfoResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.ProfileRequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.ProfileRequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.ProfileResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.ProfileResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.LoginRequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.LoginRequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.LoginResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.LoginResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.RequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.RequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.ResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.ResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.BillingAddressRequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.BillingAddressRequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.BillingAddressResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.BillingAddressResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.LinkProfileRequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.LinkProfileRequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.LinkProfileResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.LinkProfileResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        

            private  org.apache.axiom.om.OMElement  toOM(com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault{

            
                    return toOM(param);
                

            }

            private org.apache.axiom.om.OMElement toOM(final com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument param)
                    throws org.apache.axis2.AxisFault {

                org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
                xmlOptions.setSaveNoXmlDecl();
                xmlOptions.setSaveAggressiveNamespaces();
                xmlOptions.setSaveNamespacesFirst();
                org.apache.axiom.om.OMXMLParserWrapper builder = org.apache.axiom.om.OMXMLBuilderFactory.createOMBuilder(
                        new javax.xml.transform.sax.SAXSource(new org.apache.axis2.xmlbeans.XmlBeansXMLReader(param, xmlOptions), new org.xml.sax.InputSource()));
                try {
                    return builder.getDocumentElement(true);
                } catch (java.lang.Exception e) {
                    throw org.apache.axis2.AxisFault.makeFault(e);
                }
            }
        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.ProfileInfoResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.ProfileResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.LoginResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.ResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.BillingAddressResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.LinkProfileResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        
                            private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument param, boolean optimizeContent, javax.xml.namespace.QName methodQName)
                            throws org.apache.axis2.AxisFault {
                            org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
                            if (param != null){
                            envelope.getBody().addChild(toOM(param, optimizeContent));
                            }
                            return envelope;
                            }
                        


        /**
        *  get the default envelope
        */
        private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
        return factory.getDefaultEnvelope();
        }

        public org.apache.xmlbeans.XmlObject fromOM(
        org.apache.axiom.om.OMElement param,
        java.lang.Class type,
        java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{
        try{
        

            if (com.bedbathandbeyond.atg.ProfileInfoRequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.ProfileInfoRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.ProfileInfoRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.ProfileInfoResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.ProfileInfoResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.ProfileInfoResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.ProfileRequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.ProfileRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.ProfileRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.ProfileResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.ProfileResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.ProfileResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.LinkCoRegistrantRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.LinkCoRegistrantResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.LoginRequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.LoginRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.LoginRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.LoginResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.LoginResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.LoginResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.RequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.RequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.RequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.ResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.ResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.ResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.BillingAddressRequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.BillingAddressRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.BillingAddressRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.BillingAddressResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.BillingAddressResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.BillingAddressResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.LinkProfileRequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.LinkProfileRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.LinkProfileRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.LinkProfileResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.LinkProfileResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.LinkProfileResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.ForgotPasswordRequestVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        

            if (com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument.class.equals(type)){
            if (extraNamespaces!=null){
            return com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching(),
            new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
            }else{
            return com.bedbathandbeyond.atg.ForgotPasswordResponseVODocument.Factory.parse(
            param.getXMLStreamReaderWithoutCaching());
            }
            }

        
        }catch(java.lang.Exception e){
        throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
        }

        
        

        /**
        *  A utility method that copies the namepaces from the SOAPEnvelope
        */
        private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
        org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
        returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
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

        }//end of class
    