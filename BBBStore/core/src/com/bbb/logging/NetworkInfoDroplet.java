package com.bbb.logging;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import com.bbb.common.BBBDynamoServlet;
import com.bbb.framework.performance.BBBPerformanceMonitor;

public class NetworkInfoDroplet
    extends BBBDynamoServlet {

	private static final String JVM_PROPERTY="weblogic.Name";
	private boolean mEnable = false;
	private String mLocalAddress;
    private String mJVMName;
   
	public NetworkInfoDroplet(){
		super();
		try {
			mJVMName = System.getProperty(JVM_PROPERTY);
			mLocalAddress = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logError(e);
		}
		
	}
	
    public boolean isEnable() {
		return mEnable;
	}


	public void setEnable(boolean enable) {
		this.mEnable = enable;
	}


	public void service(
        final DynamoHttpServletRequest pRequest,
        final DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    	
    	if(isEnable()){
    		BBBPerformanceMonitor.start("NetworkInfoDroplet");
	    	String fullSessionID = pRequest.getSession().getId();
	        String sessionID = null;
	        //String jvmName = null;	
	        
	        if (fullSessionID.contains(".")) {
	            sessionID = fullSessionID.substring(0, fullSessionID.indexOf('.'));
	            //jvmName = fullSessionID.substring(fullSessionID.indexOf('.') + 1);
	        } else {
	            sessionID = fullSessionID;
	        }
	        
	        pRequest.setParameter("SESSION_ID", sessionID);
	
	        pRequest.setParameter("HOST_NAME", mLocalAddress);
	        pRequest.setParameter("SERVER_NAME", pRequest.getServerName());
	        pRequest.setParameter("JVM_NAME", mJVMName);
	        pRequest.setParameter("TIME", printSystemTime(pRequest.getLocale()));
 	        BBBPerformanceMonitor.end("NetworkInfoDroplet");
 	        pRequest.serviceLocalParameter("output", pRequest, pResponse);
    	}
    	        
    }


    /**
     * TODO DOCUMENT ME!.
     * 
     * @return DOCUMENT ME!
     */
    private String printSystemTime(Locale locale) {

        //generates a simple date format
    	SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy", locale);

        //generates String that will get the formatter info with values
        String dayInfo = df.format(new Date());

        return dayInfo;
    }
}
