
/**
 * Fault.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */

package com.qas.www.web_2010_04;

public class Fault extends java.lang.Exception{
    
    private com.qas.www.web_2010_04.QAFaultDocument faultMessage;
    
    public Fault() {
        super("Fault");
    }
           
    public Fault(java.lang.String s) {
       super(s);
    }
    
    public Fault(java.lang.String s, java.lang.Throwable ex) {
      super(s, ex);
    }
    
    public void setFaultMessage(com.qas.www.web_2010_04.QAFaultDocument msg){
       faultMessage = msg;
    }
    
    public com.qas.www.web_2010_04.QAFaultDocument getFaultMessage(){
       return faultMessage;
    }
}
    