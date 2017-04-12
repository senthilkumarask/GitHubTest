package com.bbb.pipeline;



/*
 *  Copyright 2011, The BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  BBBCollegeLinkPipeline.java
 *
 *  DESCRIPTION: A pipeline servlet handled the redirection logic for school promotions. 
 *   
 */


import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;

import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ComponentName;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.userprofiling.Profile;

import com.bbb.commerce.catalog.BBBCatalogTools;
import com.bbb.constants.BBBCoreConstants;
import com.bbb.exception.BBBBusinessException;
import com.bbb.exception.BBBSystemException;
import com.bbb.framework.performance.BBBPerformanceConstants;
import com.bbb.framework.performance.BBBPerformanceMonitor;
import com.bbb.logging.LogMessageFormatter;
import com.bbb.selfservice.vo.SchoolVO;
import com.bbb.utils.BBBUtility;

/**
 * DESCRIPTION: A pipeline servlet redirect to college_landing.It is used for college/schools
 * promotions.If passed school id is valid and promotion is valid url will be redirected to 
 * college landing page.
 * 
 * @author ajosh8
 */
public class BBBCollegeLinkPipeline extends InsertableServletImpl{

 

  private BBBCatalogTools bbbCatalogTools = null;

  private String mRedirectUrl;

  private RepositoryItem mUserProfile;

  private Boolean mEnabled;
  private String schoolCookieName;
  private int schoolCookieAge;
  private String schoolURLPatterns;
  private String failureUrl;
  private String schoolCookiePath;

 private List<String> lstCookieURLPatterns;

  private String testSapeUnit;
  private ComponentName mProfileComponentName;


  /**
   * @return the testSapeUnit
   */
  public String getTestSapeUnit() {
    return testSapeUnit;
  }



  /**
   * @param pTestSapeUnit the testSapeUnit to set
   */
  public void setTestSapeUnit(final String pTestSapeUnit) {
    testSapeUnit = pTestSapeUnit;
  }



  /**
   * @return the schoolCookiePath
   */
  public String getSchoolCookiePath() {
    return schoolCookiePath;
  }



  /**
   * @param pSchoolCookiePath the schoolCookiePath to set
   */
  public void setSchoolCookiePath(final String pSchoolCookiePath) {
    schoolCookiePath = pSchoolCookiePath;
  }



  /**
   * @return the lstCookieURLPatterns
   */
  public List<String> getLstCookieURLPatterns() {
    return lstCookieURLPatterns;
  }



  /**
   * @param pLstCookieURLPatterns the lstCookieURLPatterns to set
   */
  public void setLstCookieURLPatterns(final List<String> pLstCookieURLPatterns) {
    lstCookieURLPatterns = pLstCookieURLPatterns;
  }



  /**
   * @return the failureUrl
   */
  public String getFailureUrl() {
    return failureUrl;
  }



  /**
   * @param pFailureUrl the failureUrl to set
   */
  public void setFailureUrl(final String pFailureUrl) {
    failureUrl = pFailureUrl;
  }



  /**
   * @return the schoolURLPatterns
   */
  public String getSchoolURLPatterns() {
    return schoolURLPatterns;
  }



  /**
   * @param pSchoolURLPatterns the schoolURLPatterns to set
   */
  public void setSchoolURLPatterns(final String pSchoolURLPatterns) {
    schoolURLPatterns = pSchoolURLPatterns;
  }



  /**
   * @return the schoolCookieName
   */
  public String getSchoolCookieName() {
    return schoolCookieName;
  }



  /**
   * @param pSchoolCookieName the schoolCookieName to set
   */
  public void setSchoolCookieName(final String pSchoolCookieName) {
    schoolCookieName = pSchoolCookieName;
  }



  /**
   * @return the schoolCookieAge
   */
  public int getSchoolCookieAge() {
    return schoolCookieAge;
  }



  /**
   * @param pSchoolCookieAge the schoolCookieAge to set
   */
  public void setSchoolCookieAge(final int pSchoolCookieAge) {
    schoolCookieAge = pSchoolCookieAge;
  }



  /**
   * @return the enabled
   */
  public Boolean getEnabled() {
    return mEnabled;
  }



  /**
   * @param pEnabled the enabled to set
   */
  public void setEnabled(final Boolean pEnabled) {
    mEnabled = pEnabled;
  }



  /**
   * @return the userProfile
   */
  public Profile getUserProfile() {
    return (Profile)mUserProfile;
  }



  /**
   * @param pRepositoryItem the userProfile to set
   */
  public void setUserProfile(final RepositoryItem pRepositoryItem) {
    mUserProfile = pRepositoryItem;
  }



  /**
   * @return the bbbCatalogTools
   */
  public BBBCatalogTools getBbbCatalogTools() {
    return bbbCatalogTools;
  }



  /**
   * @param pBbbCatalogTools the bbbCatalogTools to set
   */
  public void setBbbCatalogTools(final BBBCatalogTools pBbbCatalogTools) {
    bbbCatalogTools = pBbbCatalogTools;
  }






  /**
   * @return the redirectUrl
   */
  public String getRedirectUrl() {
    return mRedirectUrl;
  }



  /**
   * @param pRedirectUrl the redirectUrl to set
   */
  public void setRedirectUrl(final String pRedirectUrl) {
    mRedirectUrl = pRedirectUrl;
  }



  /**
   * This method does
   * redirection task according to the business rules i.e when the request is coming from specified url
   * and passed schoolId is valid and attached promotion is valid user will be redirected to college landing page. 
   * 
   * @param pRequest
   *            DynamoHttpServletRequest
   * @param pResponse
   *            DynamoHttpServletResponse
   * @throws ServletException
   *             if there was an error while executing the code
   * @throws IOException
   *             if there was an error with servlet io
   * @return void
   */
  public void service(final DynamoHttpServletRequest pRequest,
      final DynamoHttpServletResponse pResponse) throws IOException,
      ServletException {
	  
    
		boolean shouldContinue;	
		
		if (isLoggingDebug()) {
		  logDebug("BBBCollegeLinkPipeline.service() method started");
		}
		String omniSchoolId = pRequest.getParameter("schoolId");
	  	if(!BBBUtility.isEmpty(omniSchoolId)){
	  		pRequest.getSession().setAttribute("omniSchoolId", omniSchoolId);
	  	}
		//check that schools promotions are enabled or not
		if(!getEnabled()){
		  passRequest(pRequest,pResponse);
		} else{
		
		  try {
		
		   // shouldContinue = isSchoolURLPattern(pRequest, pResponse);
			shouldContinue = isSchoolURLPattern(pRequest, pResponse);
			  
		    if(shouldContinue){
		      shouldContinue = isSchoolCookieURLPattern(pRequest, pResponse);
		    }
		    if(shouldContinue){
		      passRequest(pRequest,pResponse);
		    }
		
		  } catch (BBBSystemException e) {
			  if (isLoggingDebug()) {
		    logDebug("RepositoryException");
		   
			  }
			  if(isLoggingError()){
		  			logError(LogMessageFormatter.formatMessage(pRequest, "BBBCollegeLinkPipeline.service() | BBBSystemException ","catalog_1073"), e);
		  			}
			  passRequest(pRequest,pResponse);
			 
		  } catch (BBBBusinessException e) {
			  if (isLoggingDebug()) {
				  logDebug("School Id is not present");
			  }
			   if(isLoggingError()){
		  			logError(LogMessageFormatter.formatMessage(pRequest, "BBBCollegeLinkPipeline.service() | BBBBusinessException ","catalog_1074"), e);
		  	   }
		    pResponse.sendRedirect(getRedirectUrl());
		 
		  }
		
		}
		if (isLoggingDebug()) {
		  logDebug("BBBCollegeLinkPipeline.service() method ends");
		}
    
  }
  
  /**
   * This method validate url form where request is coming.If it is valid
   * it would check about the passed schoolId.If schoolId is valid it will send cookie.
   * It will set the schoolId in profile.
   * @param pRequest
   * @param pResponse
   * @return
   * @throws IOException
   * @throws BBBSystemException
   * @throws BBBBusinessException
   * @throws ServletException
   */
  private boolean isSchoolURLPattern(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
		  throws IOException, BBBSystemException, BBBBusinessException {
	  
	  
	  final String methodName = "isSchoolURLPattern";
	  BBBPerformanceMonitor.start(BBBPerformanceConstants.COLLEGE_WEBLINK, methodName);
	  
	  if (isLoggingDebug()) {
	      logDebug("BBBCollegeLinkPipeline.handleSchoolURLPattern() method starts");
	  }
	  
	  SchoolVO schoolVO;	
	  String schoolSeoName;
	  boolean shouldContinue=true;
	  String currentUrl;
	  
	  try{
		  if(getTestSapeUnit()!=null){
			  currentUrl=getTestSapeUnit(); 
		  }else{
			  currentUrl = pRequest.getRequestURL().toString();
			  if (isLoggingDebug()) {
				  logDebug("Current URL is :"+currentUrl);
			  }
		  }
		  
		  
	
		  //checking request is coming from desired url or not.
		  if(currentUrl.contains(getSchoolURLPatterns())){
			  
			  final String[] parts = currentUrl.split("/");
	      
			  if(parts == null){
	    	  
		    	  if (isLoggingDebug()) {
		              logDebug("School name is not present in the URL");
		          }
		    	  pResponse.sendRedirect(getFailureUrl());
			  }else{
	    	  
		    	  if (isLoggingDebug()) {
		              logDebug("Total length of URL fragments : "+parts.length);
		          }
		    	  
		    	  mProfileComponentName = ComponentName.getComponentName(BBBCoreConstants.ATG_PROFILE);
				  final Profile profile = (Profile) pRequest.resolveName(mProfileComponentName);
				  
		    	  schoolSeoName = parts[parts.length - 1];
	    	  
		    	  if(!StringUtils.isBlank(schoolSeoName) && !schoolSeoName.equalsIgnoreCase("nwp")){ 
		    		  if (isLoggingDebug()) {
		    			  logDebug("School name : "+schoolSeoName);
		    		  }
		        	   
		    		 
		    	        //validating schoolID
		    	      schoolVO = (SchoolVO)getBbbCatalogTools().getSchoolDetailsByName(schoolSeoName);    	      
		
		    	        //add cookie
		    	      final Cookie cookie = new Cookie(getSchoolCookieName(), schoolVO.getSchoolId());
		    	      cookie.setMaxAge(getSchoolCookieAge());
		    	      cookie.setPath(getSchoolCookiePath());
		    	      //pResponse.addCookie(cookie);
		    	      BBBUtility.addCookie(pResponse, cookie, true);
		    	      // set schoolId as transient property
		
		    	      profile.setPropertyValue(BBBCoreConstants.SCHOOL_IDS, schoolVO.getSchoolId());
		    	      if (isLoggingDebug()) {
		    	        logDebug("School Id set in the profile :"+profile.getPropertyValue(BBBCoreConstants.SCHOOL_IDS));
		    	      }
		    	        
		    	      getPromotionDetails(pResponse,schoolVO,profile,cookie);
		
		    	      if(getTestSapeUnit()==null){
		    	          pResponse.sendRedirect(getRedirectUrl());
		    	      }
		    	      
		    	      shouldContinue=false;
		    	      
		    	  } else if(!StringUtils.isBlank(schoolSeoName) && schoolSeoName.equalsIgnoreCase(BBBCoreConstants.SCHOOL_GENERIC_SEO)){
		    		  
		    		  profile.setPropertyValue(BBBCoreConstants.SCHOOL_IDS, null);
		 	    	  final Cookie emptyCookie = new Cookie(getSchoolCookieName(), "");
		 			  emptyCookie.setMaxAge(0);
		 			  emptyCookie.setPath(getSchoolCookiePath());
		 			  pResponse.addCookie(emptyCookie);
		 			  
		 			 if(getTestSapeUnit()==null){
		    	          pResponse.sendRedirect(getRedirectUrl());
		    	      } 
		 			  
		 			 shouldContinue=false;
	    		  
		    	  } else {
		    		  if (isLoggingDebug()) {
		                  logDebug("School Id is not present in the URL");
		              }
		              pResponse.sendRedirect(getFailureUrl());
		    	  }
			  }
	    }else{
	    	 if (isLoggingDebug()) {
	            logDebug("Current Url["+currentUrl+"] does not match with School Url pattern["+getSchoolURLPatterns()+"]");
	          }
	    }  
	       
	    if (isLoggingDebug()) {
	      logDebug("BBBCollegeLinkPipeline.handleSchoolURLPattern() method ends");
	    }
   
	  }finally{
		   BBBPerformanceMonitor.end(BBBPerformanceConstants.COLLEGE_WEBLINK,
				methodName);
	  }
	  
	  return shouldContinue;
  }

  /**
   * This method check that attached promotion is valid or not.
   * If it is valid it will set it to user profile.
   * @param schoolVO
   * @throws IOException 
   */
  
  private void getPromotionDetails(final DynamoHttpServletResponse pResponse, final SchoolVO schoolVO, final Profile profile, final Cookie cookie) throws IOException {

    if (isLoggingDebug()) {
      logDebug("BBBCollegeLinkPipeline.getPromotionDetails() method starts");
    }
    //get promotion item from school repository
    final RepositoryItem promotionItem=(RepositoryItem)schoolVO.getPromotionRepositoryItem();

    //get current site id
    final String siteId = SiteContextManager.getCurrentSiteId();

    if(promotionItem !=null){

     
      final Calendar calendar = new GregorianCalendar();
      final Date endDate = (Date) promotionItem.getPropertyValue(BBBCoreConstants.END_PROMOTION_DATE);
      
      if(endDate==null){
        if (isLoggingDebug()) {
          logDebug("Promotion End Date is null");
        }
        chkPromotionForCurrentSite(schoolVO, promotionItem, siteId,profile);
      }
      else{
          calendar.setTime(endDate);
          
          if (isLoggingDebug()) {
          logDebug("Promotion End Date is: "+endDate);
          }
	      final Calendar currentCalenderInstance = new GregorianCalendar();
	      
	      if (isLoggingDebug()) {
	      logDebug(" Current  Date is: "+currentCalenderInstance.getTime());
	      }
      
	     // SimpleDateFormat formatterOne= new SimpleDateFormat(BBBCoreConstants.FORMAT);
	     // String endDatePromotion = formatterOne.format(endDate);   
	        
	     // String currentDatePromotion = formatterOne.format(currentDate);
	       
	      
	      //Integer compare=currentDatePromotion.compareTo(endDatePromotion);
	               
	       
	      if(calendar.after(currentCalenderInstance)){
	    	  if (isLoggingDebug()) {
	           logDebug("Promotion is Active");
	    	  }
	        chkPromotionForCurrentSite(schoolVO, promotionItem, siteId,profile);
	     }
	      else{
	        if (isLoggingDebug()) {
	          logDebug("Promotion is expired,It was active till["+endDate+"]");
	        }
	        profile.setPropertyValue(BBBCoreConstants.SCHOOL_PROMOTIONS,null);
	        pResponse.sendRedirect(getFailureUrl());
	      }
      }
    }
    else{

       profile.setPropertyValue(BBBCoreConstants.SCHOOL_PROMOTIONS,null);
    	
      //remove cookie
      //Cookie cookie = new Cookie(getSchoolCookieName(),schoolVO.getSchoolId());
      cookie.setMaxAge(0);
      
      if (isLoggingDebug()) {
        logDebug("Promotion is not applicable for this school: "+schoolVO.getSchoolId());
      }
    }

    if (isLoggingDebug()) {
      logDebug("BBBCollegeLinkPipeline.getPromotionDetails() method ends");
    }
  }


/**
 * This method will check that promotion is applicable to current site or not.
 * @param schoolVO
 * @param promotionItem
 * @param siteId
 */
  private void chkPromotionForCurrentSite(final SchoolVO schoolVO, final RepositoryItem promotionItem, final String siteId, final Profile profile) {
    boolean chkPromotionProfile=false;
	  
	if (isLoggingDebug()) {
      logDebug("BBBCollegeLinkPipeline.chkPromotionForCurrentSite() method starts");
    }
    @SuppressWarnings("unchecked")
   final Set<RepositoryItem> repoItemProSite=(Set<RepositoryItem>)promotionItem.getPropertyValue(BBBCoreConstants.SITES);
   for (RepositoryItem item : repoItemProSite) {

    if(item.getRepositoryId().equals(siteId)){
    	chkPromotionProfile=true;
      //set promotion as transient property
    	profile.setPropertyValue(BBBCoreConstants.SCHOOL_PROMOTIONS, schoolVO.getPromotionRepositoryItem().getRepositoryId());
      if (isLoggingDebug()) {
        logDebug("Promotion set in the profile"+profile.getPropertyValue(BBBCoreConstants.SCHOOL_PROMOTIONS));
      }
       
    }
    else{
      if (isLoggingDebug()) {
        logDebug("Promotion is applicable for site ["+item.getRepositoryId()+"]and current site is : ["+siteId+"]");
      }
    }
   }
   if(!chkPromotionProfile){
	   profile.setPropertyValue(BBBCoreConstants.SCHOOL_PROMOTIONS,null);
   }
   if (isLoggingDebug()) {
     logDebug("BBBCollegeLinkPipeline.chkPromotionForCurrentSite() method ends");
   }
  }


  /**
   * This method checks that next time request is coming from desired url or not.
   * If it is coming from desired url.It will validate attached schoolId.If schoolId is valid it will
   * set it into user profile.
   * @param pRequest
   * @param pResponse
   * @return
   * @throws IOException
   * @throws BBBSystemException
   * @throws BBBBusinessException
   */
  private boolean isSchoolCookieURLPattern(final DynamoHttpServletRequest pRequest, final DynamoHttpServletResponse pResponse)
      throws IOException, BBBSystemException, BBBBusinessException {
	
	final String methodName = "isSchoolCookieURLPattern";
	BBBPerformanceMonitor.start(BBBPerformanceConstants.COLLEGE_WEBLINK, methodName);
	  
	if (isLoggingDebug()) {
	      logDebug("BBBCollegeLinkPipeline.handleSchoolCookieURLPattern() method starts");
	}
    boolean shouldContinue=true;
    String schoolId;

    SchoolVO schoolVO;

    try{
	    final String currentUrl = pRequest.getRequestURL().toString();
	    final List<String> lstCookieURLPatterns=(List<String>)getLstCookieURLPatterns();
	
	    final Iterator<String> iterator=lstCookieURLPatterns.iterator();
	
	    while(iterator.hasNext()){
	      final String strCookieURLPattern=(String)iterator.next();
	
	      if(currentUrl.contains(strCookieURLPattern)){
	
	        Cookie cookie = null;
	        final Cookie[] cookies = pRequest.getCookies();
	
	        if(cookies != null) {
	
	          for (int i = 0; i < cookies.length; i++) {
	            if (cookies[i].getName().equals(getSchoolCookieName())) {
	              cookie = cookies[i];
	              schoolId= cookie.getValue();
	
	              if (isLoggingDebug()) {
	        	     logDebug("Cookie found with schoolId :"+schoolId);
	        	   }
	          
	              mProfileComponentName = ComponentName
	      				.getComponentName(BBBCoreConstants.ATG_PROFILE);
	      		  final Profile profile = (Profile) pRequest.resolveName(mProfileComponentName);
	
	              
	              if(schoolId !=null){
	                schoolVO=(SchoolVO)getBbbCatalogTools().getSchoolDetailsById(schoolId);
	
	                getPromotionDetails(pResponse,schoolVO,profile,cookie);
	                pResponse.sendRedirect(getRedirectUrl());
	                shouldContinue=false;
	              }else{
	                if (isLoggingDebug()) {
	                  logDebug("SchoolId : ["+schoolId+"] in Cookie is null");
	                }   
	              }
	            }
	            else{
	              if (isLoggingDebug()) {
	                logDebug("Cookie is not present with name: ["+getSchoolCookieName()+"]");
	              }    
	            }
	          }
	        }
	        else{
	          if (isLoggingDebug()) {
	            logDebug("No Cookie is present");
	          }
	        }
	      }
	    }
	    if (isLoggingDebug()) {
		      logDebug("BBBCollegeLinkPipeline.handleSchoolCookieURLPattern() method ends");
		}
	    
    }finally{
		   BBBPerformanceMonitor.end(BBBPerformanceConstants.COLLEGE_WEBLINK,
				methodName);
	}
    return shouldContinue;
  }

}





