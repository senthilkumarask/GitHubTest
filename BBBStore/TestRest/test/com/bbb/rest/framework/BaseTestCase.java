package com.bbb.rest.framework;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



import org.json.JSONException;
import org.json.JSONObject;

import junitx.ddtunit.DDTTestCase;
import junitx.ddtunit.util.ClassAnalyser;
import atg.core.util.StringUtils;
import atg.naming.NameResolver;
import atg.rest.client.BBBRestSession;
import atg.rest.client.RestClientException;
import atg.rest.client.RestResult;
import atg.rest.client.RestSession;

public abstract class BaseTestCase extends DDTTestCase implements NameResolver
{
	String prefix;
	String extension = ".xml";
	String expression = null;
	RestSession restSession=null;
	String host;
	Integer port;
	String userId;
	String password;
	
	
	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @return the port
	 */
	public Integer getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	public BaseTestCase(String name)
	{
		super(name);
		prefix = "/";
		extension = ".xml";
		expression = null;

	}

	protected void initContext()
	{
		initTestData(getXMLName(this), getClass().getName());		
	}

	public final String getXMLName(DDTTestCase testCase)
	{
		testCase = ((DDTTestCase) (testCase != null ? testCase : ((DDTTestCase) (this))));
		StringBuffer xmlName = new StringBuffer(getPrefix());
		prepareExpression(testCase);
		xmlName.append(getExpression());
		xmlName.append(getExtension());
		return xmlName.toString();
	}

	public String getPrefix()
	{
		return prefix;
	}

	protected void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public String getExpression()
	{
		return expression;
	}

	protected void setExpression(String expression)
	{
		this.expression = expression;
	}

	public String getExtension()
	{
		return extension;
	}

	protected void setExtension(String extension)
	{
		this.extension = extension;
	}


	protected void prepareExpression(DDTTestCase testCase)
	{
		if(expression != null)
		{
			return;
		} else
		{
			testCase = ((DDTTestCase) (testCase != null ? testCase : ((DDTTestCase) (this))));
			StringBuffer expressionBuffer = new StringBuffer(ClassAnalyser.classPackage(testCase));
			expressionBuffer.append(getPrefix());
			String simpleClassName = testCase.getClass().getName().substring(testCase.getClass().getName().lastIndexOf('.') + 1);
			expressionBuffer.append(simpleClassName);
			expression = expressionBuffer.toString();
			return;
		}
	}


	@Override
	public Object resolveName(String s) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public Map<String, Object> getControleParameters(){
		Map<String, Object> params = new HashMap<String, Object>();
		String atg_rest_depth_value;
		String atg_rest_input;
		String atg_rest_output; 
		
		if(!getObject("atg-rest-depth").equals("") ){
			  atg_rest_depth_value = (String) getObject("atg-rest-depth");
			  params.put("atg-rest-depth", atg_rest_depth_value);
		}
		if(!getObject("atg-rest-input").equals("") ){
			  atg_rest_input = (String) getObject("atg-rest-input");
			  params.put("atg-rest-input", atg_rest_input);
		}
		if(!getObject("atg-rest-output").equals("") ){
			atg_rest_output = (String) getObject("atg-rest-output");
			params.put("atg-rest-output", atg_rest_output);
		}
    	return params;
	}	
	
	public RestSession testGetCurrentOrderDetailsTransientUser(RestSession mSession) throws RestClientException, JSONException, IOException{
		mSession.setUseHttpsForLogin(false);
		mSession.setUseInternalProfileForLogin(false);
		RestResult pd =  null;
		HashMap params = new HashMap<String, Object>();
		try {
			mSession.login();
			params.put("atg-rest-depth", "3");
			params.put("atg-rest-input", "json");
			params.put("atg-rest-output", "json");
			params.put("arg1", true);
			pd = mSession.createHttpRequest("http://" + getHost() + ":" + getPort()
					+ "/rest/bean/com/bbb/rest/generic/BBBSessionManagerFacade/getCurrentOrderDetails", params, "POST");

			JSONObject jsonResponseObj = new JSONObject(pd.readInputStream());
			System.out.println(jsonResponseObj);

			String transientFlag = jsonResponseObj.getString("transientFlag");
			assertEquals("true", transientFlag);

			String itemCount = jsonResponseObj.getString("cartItemCount");
			assertEquals("0", itemCount);

		}
		finally {
			if(pd != null)
				pd = null;
		}
		
		return mSession;

	}
	
	protected RestSession getNewHttpSession(){
		
		host = "del2l160BBB";
		port = Integer.parseInt("7003");
		userId= "admin";
		password="admin";

	    RestSession session = BBBRestSession.createSession(host, port, userId, password);
	    session.setScheme("http"); 
		return session;
		
	}
	
	
	protected RestSession getHttpSession(){
		if(restSession!=null)
			return restSession;
		else {
			return getNewHttpSession();
		}			
	}
	
	protected RestSession getNewHttpSession(String siteId){
		
		RestSession session = null;
		
		if(!StringUtils.isEmpty(siteId)){
			
			if(siteId.equalsIgnoreCase("BedBathUS")){
				host = "@BedBathUSHostName@";
			}else if (siteId.equalsIgnoreCase("BedBathCanada")){
				host = "@BedBathCanadaHostName@";
			}else if (siteId.equalsIgnoreCase("BuyBuyBaby")){
				host = "@BuyBuyBabyHostName@";
			}
			
		}else{
			host = "@localhost@";
		}
		 
		port = Integer.parseInt("7003");
	    session = BBBRestSession.createSession(host, port, "admin", "admin");
		session.setScheme("http");
		
		return session;
		
	}
	
	
	
	
}
