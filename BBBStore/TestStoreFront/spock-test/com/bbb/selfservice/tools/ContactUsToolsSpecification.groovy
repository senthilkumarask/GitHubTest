package com.bbb.selfservice.tools

import com.bbb.constants.BBBCoreErrorConstants;
import com.bbb.exception.BBBSystemException
import com.bbb.logging.LogMessageFormatter;

import atg.multisite.Site
import atg.multisite.SiteContext;
import atg.repository.MutableRepository
import atg.repository.Query;
import atg.repository.QueryBuilder
import atg.repository.QueryExpression;
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView
import atg.userprofiling.email.TemplateEmailException
import atg.userprofiling.email.TemplateEmailInfoImpl;
import atg.userprofiling.email.TemplateEmailSender;
import spock.lang.specification.BBBExtendedSpec

class ContactUsToolsSpecification extends BBBExtendedSpec {

	ContactUsTools tools 
	MutableRepository mContactUsRepository = Mock()
	TemplateEmailSender mTemplateEmailSender = Mock()
	SiteContext mSiteContext = Mock()
	
	def setup(){
		tools = new ContactUsTools()
		tools.setContactUsRepository(mContactUsRepository)
		tools.setSendEmailInSeparateThread(true)
		tools.setPersistEmails(true)
		tools.setTemplateEmailSender(mTemplateEmailSender)
		tools.setSiteContext(mSiteContext)
	}
	
	def "getContactUsItem method with items returned"(){
		
		given:
			Site site = Mock()
			RepositoryView view = Mock()
			QueryBuilder queryBuilder = Mock()
			QueryExpression pProperty = Mock()
			QueryExpression pValue = Mock()
			Query query = Mock()
			RepositoryItem item = Mock()
			
			mSiteContext.getSite() >> site
			site.getId() >> "siteId"
			mContactUsRepository.getView("contactus") >> view
			view.getQueryBuilder() >> queryBuilder
			queryBuilder.createPropertyQueryExpression("siteName") >> pProperty
			queryBuilder.createConstantQueryExpression("siteId") >> pValue
			queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS) >> query
			view.executeQuery(query) >> [item]
			
		when:
			RepositoryItem[] itemArray =  tools.getContactUsItem()
		
		then:
			itemArray!=null
			itemArray.length == 1
	}
	
	def "getContactUsItem method with no items returned"(){
		
		given:
			Site site = Mock()
			RepositoryView view = Mock()
			QueryBuilder queryBuilder = Mock()
			QueryExpression pProperty = Mock()
			QueryExpression pValue = Mock()
			Query query = Mock()
			
			mSiteContext.getSite() >> site
			site.getId() >> "siteId"
			mContactUsRepository.getView("contactus") >> view
			view.getQueryBuilder() >> queryBuilder
			queryBuilder.createPropertyQueryExpression("siteName") >> pProperty
			queryBuilder.createConstantQueryExpression("siteId") >> pValue
			queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS) >> query
			view.executeQuery(query) >> null
			
		when:
			RepositoryItem[] itemArray =  tools.getContactUsItem()
		
		then:
			itemArray ==null
	}
	
	def "getContactUsItem method with RepositoryException thrown"(){
		
		given:
			Site site = Mock()
			RepositoryView view = Mock()
			QueryBuilder queryBuilder = Mock()
			QueryExpression pProperty = Mock()
			QueryExpression pValue = Mock()
			Query query = Mock()
			
			mSiteContext.getSite() >> site
			site.getId() >> "siteId"
			mContactUsRepository.getView("contactus") >> view
			view.getQueryBuilder() >> queryBuilder
			queryBuilder.createPropertyQueryExpression("siteName") >> pProperty
			queryBuilder.createConstantQueryExpression("siteId") >> pValue
			queryBuilder.createComparisonQuery(pProperty, pValue, QueryBuilder.EQUALS) >> query
			view.executeQuery(query) >> {throw new RepositoryException("RepositoryException is thrown")}
			
		when:
			RepositoryItem[] itemArray =  tools.getContactUsItem()
		
		then:
			itemArray ==null
			BBBSystemException excep = thrown()
			excep.getMessage().equals("err_contactustools_getrepoitem_sys_exception:RepositoryException is thrown")
	}
	
	def "sendEmail method, mail is sent"(){
		
		given:
			TemplateEmailInfoImpl pTemplateInfo = Mock()
			
		when:
			tools.sendEmail(pTemplateInfo,["recepient"])
			
		then:
			1*mTemplateEmailSender.sendEmailMessage(_,_,true,true)
	}
	
	def "sendEmail method, template exception thrown"(){
		
		given:
			TemplateEmailInfoImpl pTemplateInfo = Mock()
			mTemplateEmailSender.sendEmailMessage(_,_,true,true) >> {throw new TemplateEmailException()}
			
		when:
			tools.sendEmail(pTemplateInfo,["recepient"])
			
		then:
			BBBSystemException excep = thrown()
			excep.getMessage().equals("err_contactustools_sendemail_sys_exception:null")
	}
}
