package com.bbb.search.droplet;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import com.bbb.common.BBBDynamoServlet;

import atg.core.util.NumberTable;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

/*
 * This method accepts the partial search keyword String and String a
 * array of split original search term. each word in original search 
 * term is iteratively pattern matched with partial serach keyword to
 *  identify the word to be striked off.
 */
public class StrikePartialSearchKeyword extends BBBDynamoServlet {
	
	private static final String SIZE = "size";
	private static final String COUNT = "count";

	private static final String SEARCH_WORD = "searchWord";
	private static final String OPARAM_STRIKE_WORD = "strike";
	private static final String OPARAM_SEARCH_WORD = "search";

	public void service(DynamoHttpServletRequest pRequest, 
             DynamoHttpServletResponse pResponse) 
       throws ServletException, IOException {
		// List<String> arrayParam = new ArrayList<String>();
		String[] origSearchList=(String[])pRequest.getObjectParameter("origSearchList");
		String partialSearchKeyword=pRequest.getParameter("partialSearchKeyword");
	 
		int origSearchListLength = origSearchList.length;
		pRequest.setParameter(SIZE, NumberTable.getInteger(origSearchListLength));
		logDebug("search length"+origSearchListLength);
		for(int i=0; i<origSearchListLength;++i){
			 	  pRequest.setParameter(COUNT, NumberTable.getInteger(i+1));
			 	  pRequest.setParameter(SEARCH_WORD, origSearchList[i]);
			 	  logDebug("search count"+i);

			 	  if(origSearchList[i].charAt(0)=='+')
			 	  {
			 		 origSearchList[i]=origSearchList[i].substring(1, origSearchList[i].length());
			 	  }			 		  
			   	  String patternString=".*\\b("+origSearchList[i].toLowerCase().replaceAll("[\"\']", "")+")\\b.*";
			   	  logDebug("pattern: "+patternString+"\n");
			   	  Pattern pattern=Pattern.compile(patternString);
			   	  if(pattern.matcher(partialSearchKeyword.toLowerCase().replaceAll("[\"\']", "")).matches()){
			   		  pRequest.serviceLocalParameter(OPARAM_SEARCH_WORD, pRequest, pResponse);
			   		  logDebug("Search Word"+origSearchList[i]);
			   	  }else{
			   		  pRequest.serviceLocalParameter(OPARAM_STRIKE_WORD, pRequest, pResponse);
			   		  logDebug("StrikeWord"+origSearchList[i]);		   		  
			   	  }
		}			
	}
}
