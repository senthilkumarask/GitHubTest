package atg.rest.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import atg.rest.client.RestSession.FORMAT;

/*
 * 
 * This class is written to handle case when HTTP connection throws 500 response code. We need to 
 * raise ATG ticket to fix this issue or find alternate solution. Till that time this is temporary class
 * to create client REST connection 
 */
public class BBBRestSession extends RestSession {
	private HttpURLConnection mConnection;
	private String SITE_ID = "X-bbb-site-id";
	private String CHANNEL_ID = "X-bbb-channel";
	private String STORE_ID = "X-bbb-store";
	
	BBBRestSession(String pHost, int pPort, String pUsername, String pPassword){
		super(pHost,pPort,pUsername,pPassword);
		
	}
	public static BBBRestSession createSession(String pHost, int pPort, String pUsername, String pPassword){
		return new BBBRestSession(pHost, pPort, pUsername, pPassword);		
	}
	
	/**
	 * Additional code to handle 500 response code. In this case read from error stream rather
	 * than input stream
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public RestResult createHttpRequest(String pURL, Map pParams, Object pArguments[], String pMethodType)
    throws RestClientException
    {
        RestResult result = null;


        try
	    {
	            Object paramObj = packageParameters(pParams, pArguments, pMethodType);
	            mConnection = createHttpConnection(pURL, paramObj, pMethodType);
	            mConnection.disconnect();
	              mConnection.addRequestProperty("clientID", "client123");
	              mConnection.addRequestProperty("token", "client123"); 
	              mConnection.addRequestProperty(CHANNEL_ID, "MobileWeb"); 
                if(pParams != null){
                
                	String siteId = (String)pParams.get(SITE_ID);
    	            if(siteId != null && !"".equals(siteId)){
    	            	 mConnection.addRequestProperty(SITE_ID, (String)pParams.get(SITE_ID));
    	            }
    	            
                	String channel = (String)pParams.get(CHANNEL_ID);
    	            if(channel != null && !"".equals(channel)){
    	            	 mConnection.addRequestProperty(CHANNEL_ID, (String)pParams.get(CHANNEL_ID));
    	            }
    	            
    	            String storeId = (String)pParams.get(STORE_ID);
    	            if(storeId != null && !"".equals(storeId)){
    	            	 mConnection.addRequestProperty(STORE_ID, (String)pParams.get(STORE_ID));
    	            }
                }
	            
	            mConnection.connect();
	            result = RestResult.create(this, mConnection);
	            if(mConnection.getResponseCode()!=200){
	            	String cause=readInputStream(mConnection.getErrorStream());
	            	throw new RestClientException(cause);
	            }
	            mConnection.getInputStream();
	    }
        catch(IOException e)
	    {
	            if(mConnection == null)
	                throw new RestClientException(e);
	            try
	            {
	               if(mConnection.getResponseCode() != 410)
	               {
	                    IOException ioe = new IOException((new StringBuilder()).append(mConnection.getResponseMessage()).append(" ").append(e.getMessage()).toString());
	                    ioe.setStackTrace(e.getStackTrace());
	                    throw new RestClientException(ioe);
	               }
	            }
	            catch(IOException ee)
	            {
	                throw new RestClientException(e);
	            }
	    }
        return result;
    }
    public String login()
    throws RestClientException
	{
	    String login=super.login();
	    /*update JSESSION ID in session as OOB fails in case there are multiple set-cookie headers
	     * 
	     */
		Map<String, List<String>> headerFields = mConnection.getHeaderFields();
		
		if(headerFields != null && headerFields.keySet() != null)
			
			for(String headerKey : headerFields.keySet()){
				
				System.out.println(headerKey +":::"+ headerFields.get(headerKey));
				if(headerKey != null && headerKey.equals("Set-Cookie")){
					if(headerFields.get(headerKey) != null && !headerFields.get(headerKey).isEmpty()){
						for(String cookieItem : headerFields.get(headerKey)){
							if(cookieItem != null && cookieItem.contains("JSESSIONID")){
								String[] array = cookieItem.split(";");
								for(String item: array){
									if(item!= null && item.contains("JSESSIONID")){
										String[] jsession = item.split("=");
										super.setSessionId("JSESSIONID="+jsession[1]);
										break;
									}
								}
							}
						}
						
					}					
				}				
			}
		
	    return login;
	    
	}
    
    public void ensureSessionConfirmation(Map<String, Object> reqParams)throws RestClientException{
        String hostString = getHostString();    
        StringBuffer buf = new StringBuffer(hostString);
    	Map<String, Object> params=new HashMap<String, Object>();
        params.clear();
        params.put("atg-rest-output", "json");
        buf.append(getRestContextRoot()).append("/bean").append("/atg/rest/SessionConfirmation/sessionConfirmationNumber");
        RestResult result = createHttpRequest(buf.toString(), params, "GET");
        try{
            JSONObject json = new JSONObject(result.readInputStream());
            String sessionConfNumber = json.getString("sessionConfirmationNumber");
            reqParams.put("_dynSessConf", sessionConfNumber);
        }catch(JSONException je){
        	//eat exception
        }catch(IOException ioe){
        	//eat exception
        }
    }

    
    static String readInputStream(InputStream is) throws IOException {
  	  char[] buf = new char[2048];
  	  Reader r = new InputStreamReader(is, "UTF-8");
  	  StringBuilder s = new StringBuilder();
  	  while (true) {
  	    int n = r.read(buf);
  	    if (n < 0)
  	      break;
  	    s.append(buf, 0, n);
  	  }
  	  return s.toString();
  	}

}
