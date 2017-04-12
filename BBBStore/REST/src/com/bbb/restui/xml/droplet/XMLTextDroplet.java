package com.bbb.restui.xml.droplet;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.servlet.ServletException;
import com.bbb.commerce.giftregistry.droplet.BBBPresentationDroplet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * This class creates java.io.Reader object for XML file
 * placed in classpath
 *  
 * @author ikhan2
 *
 */
public class XMLTextDroplet extends BBBPresentationDroplet {
	
	/** String literal for xml file name input parameter*/
	private static final String XML_FILE_NAME = "xmlFileName";
	
	/** String literal for ouput parameter xml reader object*/
	private static final String XML_READER = "reader";
	
	private static final String CLS_NAME ="XMLTextDroplet"; 
			
	/**
	 * this methods create InputReader object for a XML file
	 * placed in the class path
	 * 
	 * The Reader object will then be used in the JSTL transformer 
	 * as doc xml to get transformed
	 * 
	 * passes in request
	 * 
	 * @param DynamoHttpServletRequest
	 * @param DynamoHttpServletResponse
	 * @return void
	 * @throws ServletException
	 *             , IOException
	 */
    public void service(final DynamoHttpServletRequest pRequest,
			final DynamoHttpServletResponse pResponse) throws ServletException,
			IOException {

		String fileName = (String)pRequest.getParameter(XML_FILE_NAME);
		
		Reader reader = null;
	
		
		logDebug(CLS_NAME +"/MSG=[Start service() fileName="+ fileName);
		
		//Load file from Context class loader
		InputStream stream = null; 
		if(fileName !=null){
			stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
		}
		
		if(stream ==null){
			pRequest.serviceParameter(OPARAM_EMPTY, pRequest, pResponse);
			return;
		} 
		
		reader = new InputStreamReader(stream);
		pRequest.setParameter(XML_READER, reader);
		pRequest.serviceParameter(OPARAM_OUTPUT, pRequest, pResponse);
	
		logDebug(CLS_NAME +"/MSG=[Start ends()]");
	}
}
