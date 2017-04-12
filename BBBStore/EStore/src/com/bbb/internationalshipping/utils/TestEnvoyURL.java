package com.bbb.internationalshipping.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import atg.apache.soap.encoding.soapenc.Base64;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;


public class TestEnvoyURL {
	private static final String  module = TestEnvoyURL.class.getName();
	private static final ApplicationLogging logger = ClassLoggingFactory.getFactory().getLoggerForClassName(module);
	
	public static void main(String[] args) throws ProtocolException {

		StringBuffer fileData = new StringBuffer();
		String xmlFileOutput = "";
		BufferedReader reader = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader("D:/BBB/International Shipping/envoy.xml");
			reader = new BufferedReader(fileReader);
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		finally{
			try {
				if(reader!=null)
					reader.close();
				if(fileReader!=null)
					fileReader.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
		char[] buf = new char[5076];
		int numRead = 0;
		try {
			while ((numRead = reader.read(buf)) != -1) {

				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
				buf = new char[5076];
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}

		xmlFileOutput = fileData.toString();

		HttpURLConnection conn=null;
		
		
		try {
			conn = (HttpURLConnection) (new URL(
					"https://sandbox.borderfree.com/checkout/checkoutAPI-v2.srv"))
					.openConnection();
			conn.setDoInput(true); 
			conn.setDoOutput(true);
			String authStr = "bbb_api_stg:+OSTgexT";
	        String authEncoded = Base64.encode(authStr.getBytes());
			conn.setRequestProperty  ("Authorization", "Basic " + authEncoded);
			conn.setRequestProperty ( "Content-Type", "text/xml" );
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.setRequestMethod("POST");
		HttpURLConnection.setFollowRedirects(true);
		try {
			
			try {
				DataOutputStream output = new DataOutputStream( conn.getOutputStream() );
				output.writeBytes( xmlFileOutput );
				
				logger.logDebug("Resp Code:"+conn.getResponseCode()); 
				logger.logDebug("Resp Message:"+ conn.getResponseMessage()); 

				// get ready to read the response from the cgi script 
				DataInputStream input = new DataInputStream( conn.getInputStream() ); 
				
				for( int c = input.read(); c != -1; c = input.read() ) {
					logger.logDebug( String.valueOf(c)); 
				//input.close(); 
				} 
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
}
