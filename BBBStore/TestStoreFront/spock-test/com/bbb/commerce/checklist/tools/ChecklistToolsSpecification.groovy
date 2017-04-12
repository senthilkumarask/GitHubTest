package com.bbb.commerce.checklist.tools;
/*
 *  Copyright 2011, BBB  All Rights Reserved.
 *
 *  Reproduction or use of this file without express written
 *  consent is prohibited.
 *
 *  FILE:  SearchDropletSpecification.groovy
 *
 *  DESCRIPTION: Test Search droplet
 *
 *  HISTORY:
 *  Aug 5, 2016  Initial version
*/


import com.bbb.commerce.checklist.vo.CheckListVO
import com.bbb.constants.BBBCoreConstants;

import spock.lang.specification.BBBExtendedSpec
public class ChecklistToolsSpecification extends BBBExtendedSpec {
	def CheckListTools testObj;
	def CheckListVO checkListVOTestVO;
	def String[] entities;
	def setup(){
		testObj = new CheckListTools(entities:entities);
		testObj.setUrlNonAcceptableRegexPattern("[^a-zA-Z0-9]+");
		
		checkListVOTestVO= new CheckListVO();
		checkListVOTestVO.setDisplayName("Wedding Registry");
		checkListVOTestVO.setChecklistId("DC31");
	}
	
	def "Flow for formatUrlParam with param abc cde"() {
		given:
		 String urlParamString ="abc cde";
		when:"Calling formatUrlParam with sample string "
		String formattedURL=testObj.formatUrlParam(urlParamString)//calling the test methods
		then:
		formattedURL=="abc-cde";
		}
	
	def "Flow for formatUrlParam with param ! klm 98"() {
		given:
		 String urlParamString ="! klm 98";
		when:"Calling formatUrlParam with sample string "
		String formattedURL=testObj.formatUrlParam(urlParamString)//calling the test methods
		then:
		formattedURL=="klm-98";
		}
	
	def "Happy flow for fetchFormattedURL with params as baBY Regi8stry"() {
		// String checklistName, String c1name,String c2name, String displayName, String categoryId
		given:
		 String c1name ="baBY Regi*stry";
		 String c2name ="cook!ng";
		 String displayName ="dish asher";
		 String categoryId ="dC11";
		when:"Calling fetchFormattedURL with sample string "
		String formattedURL=testObj.fetchFormattedURL(checkListVOTestVO,c1name,c2name,displayName,categoryId)//calling the test methods
		then:
		formattedURL=="/checklist/wedding-registry/baby-regi-stry/cook-ng/dish-asher/dc11/dc31";
		}
	
	def "Happy flow for fetchFormattedURL with all params as null"() {
		// String checklistName, String c1name,String c2name, String displayName, String categoryId
		given:
		//testObj=Spy()
		 String c1name =null;
		 String c2name =null;
		 String displayName =null;
		 String categoryId =null;
		 checkListVOTestVO= new CheckListVO();
		 checkListVOTestVO.setDisplayName(null);
		 checkListVOTestVO.setChecklistId(null);
		when:"Calling fetchFormattedURL with null "
		String formattedURL=testObj.fetchFormattedURL(checkListVOTestVO,c1name,c2name,displayName,categoryId)//calling the test methods
		then:
		formattedURL=="/checklist";
		}
	def "Happy flow for fetchFormattedURL with params as wed-ding"() {
		// String checklistName, String c1name,String c2name, String displayName, String categoryId
		given:
		 String c1name ="BATHING";
		 String c2name ="mats";
		 String displayName ="green@#2&%#@";
		 String categoryId ="dC11";
		when:"Calling fetchFormattedURL with sample string "
		String formattedURL=testObj.fetchFormattedURL(checkListVOTestVO,c1name,c2name,displayName,categoryId)//calling the test methods
		then:
		formattedURL=="/checklist/wedding-registry/bathing/mats/green-2/dc11/dc31";
		}
	///***********************************
	def "Flow for escapeHtmlString with params as dhfj#490"() {
		given:
		 String urlParamString ="dhfj#490"
		 testObj.setEntities()
		when:"Calling escapeHtmlString with sample string "
		String formattedURL=testObj.escapeHtmlString(urlParamString)//calling the test methods
		then:
		formattedURL=="dhfj#490";
		}
	def "Flow for escapeHtmlString with params as dhfj90"() {
		given:
		 String urlParamString ="dhfj90"
		 testObj.setEntities(null)
		when:"Calling escapeHtmlString with sample string "
		String formattedURL=testObj.escapeHtmlString(urlParamString)//calling the test methods
		then:
		formattedURL=="dhfj90";
		}
	def "Flow for escapeHtmlString with params as sdfa490"() {
		given:
		 String urlParamString ="sdfa490"
		 testObj.setEntities("hi","sdf")
		
		when:"Calling escapeHtmlString with sample string "
		String formattedURL=testObj.escapeHtmlString(urlParamString)//calling the test methods
		then:
		formattedURL=="a490";
		}
	
	def "Flow for escapeHtmlString with params as null"() {
		given:
		 String urlParamString =null;
		when:"Calling escapeHtmlString with sample string "
		String formattedURL=testObj.escapeHtmlString(urlParamString)//calling the test methods
		then:
		formattedURL==null
		}
	
	def "Happy flow for formattedDisplayName with param 4673dhsjh-"() {
		given:
		 String displayName ="4673dhsjh-";
		when:"Calling formattedDisplayName with sample string "
		String formattedDisplayName=testObj.formattedDisplayName(displayName)//calling the test methods
		then:
		formattedDisplayName=="4673dhsjh";
		}
	def "Happy flow for formattedDisplayName with param null"() {
		given:
		 String displayName ="";
		when:"Calling formattedDisplayName with sample string "
		String formattedDisplayName=testObj.formattedDisplayName(displayName)//calling the test methods
		then:
		formattedDisplayName=="";
		}
	
	def "Happy flow for formattedDisplayName with param uiiLKD!u4673dhsjh-"() {
		given:
		 String displayName ="uiiLKD!u4673dhsjh-";
		when:"Calling formattedDisplayName with sample string "
		String formattedDisplayName=testObj.formattedDisplayName(displayName)//calling the test methods
		then:
		formattedDisplayName=="uiiLKD-u4673dhsjh";
		}
	
}