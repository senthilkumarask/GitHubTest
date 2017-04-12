package com.bbb.selfservice.droplet

import atg.repository.RepositoryItem
import com.bbb.cms.manager.LblTxtTemplateManager
import com.bbb.exception.BBBBusinessException
import com.bbb.exception.BBBSystemException
import com.bbb.selfservice.manager.ContactUsManager
import javax.servlet.ServletException
import spock.lang.specification.BBBExtendedSpec

class ContactUSCategoryDropletSpecification extends BBBExtendedSpec {

	def ContactUSCategoryDroplet cucDropelt
	def ContactUsManager cManagerMock = Mock()
	def RepositoryItem contactItem = Mock()
	def RepositoryItem contactItem1 = Mock()
	def LblTxtTemplateManager txtManager = Mock()
	def setup(){
		cucDropelt = new ContactUSCategoryDroplet(contactUsManager : cManagerMock, lblTxtTemplateManager : txtManager)
	}
	
	def"getAllSubjects. tc for content for subject drop down"(){
		given:
		RepositoryItem [] items = [contactItem, contactItem1]
			1*cManagerMock.getContactUsItem() >> items
			contactItem.getPropertyValue("subject") >> "item"
			contactItem1.getPropertyValue("subject") >> "item1"
			requestMock.getObjectParameter("subjectCategoryTypes") >> ["item", "item1"]
			
			
		when:
			Object[] value = cucDropelt.getAllSubjects(null)
		then:
		    value[0] == "item"
			value[1] == "item1"
			1*requestMock.setParameter("subjectCategoryTypes", ["item", "item1"])
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"getAllSubjects. tc when content item is empty"(){
		given:
		RepositoryItem [] items = []
			1*cManagerMock.getContactUsItem() >> items
			requestMock.getObjectParameter("subjectCategoryTypes") >> []
			
			requestMock.getLocale() >> {return new Locale("en_US")} 
			txtManager.getErrMsg("err_contactus_category_droplet",_,null, null) >> "error"
			
		when:
			Object[] value = cucDropelt.getAllSubjects(null)
		then:
		    value == null
			BBBSystemException excption = thrown()
			1*requestMock.setParameter("subjectCategoryTypes", [])
			1*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	
	def"getAllSubjects. tc for IOException exception"(){
		given:
			1*cManagerMock.getContactUsItem() >> null
			
			requestMock.serviceLocalParameter("output", requestMock, responseMock) >> {throw new IOException("exception")}
			requestMock.getLocale() >> {return new Locale("en_US")}
			txtManager.getErrMsg("err_contactus_category_droplet",_,null, null) >> "error"
			
		when:
			Object[] value = cucDropelt.getAllSubjects(null)
		then:
			value == null
			BBBSystemException excption = thrown()
			1*requestMock.setParameter("subjectCategoryTypes", [])
			0*requestMock.getObjectParameter("subjectCategoryTypes")
	}
	
	def"getAllSubjects. tc for BBBSystemException while getting contact items"(){
		given:
			1*cManagerMock.getContactUsItem() >> {throw new BBBSystemException("exception")}
			requestMock.getObjectParameter("subjectCategoryTypes") >> []
			
			requestMock.getLocale() >> {return new Locale("en_US")}
			txtManager.getErrMsg("err_contactus_category_droplet",_,null, null) >> "error"
			
		when:
			Object[] value = cucDropelt.getAllSubjects(null)
		then:
			value == null
			BBBSystemException excption = thrown()
			0*requestMock.setParameter("subjectCategoryTypes", [])
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}
	
	def"getAllSubjects. tc for BBBBusinessException while getting contact items"(){
		given:
			1*cManagerMock.getContactUsItem() >> {throw new BBBBusinessException("exception")}
			requestMock.getObjectParameter("subjectCategoryTypes") >> []
			
			requestMock.getLocale() >> {return new Locale("en_US")}
			txtManager.getErrMsg("err_contactus_category_droplet",_,null, null) >> "error"
			
		when:
			Object[] value = cucDropelt.getAllSubjects(null)
		then:
			value == null
			BBBSystemException excption = thrown()
			0*requestMock.setParameter("subjectCategoryTypes", [])
			0*requestMock.serviceLocalParameter("output", requestMock, responseMock)
	}

}
