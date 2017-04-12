package com.bbb.commerce.catalog

import com.bbb.constants.BBBInternationalShippingConstants;
import com.bbb.exception.BBBSystemException

import atg.repository.Repository
import atg.repository.RepositoryException
import atg.repository.RepositoryItem
import atg.repository.RepositoryView;
import spock.lang.specification.BBBExtendedSpec;

class BBBInternationalCatalogToolsImplSpecification extends BBBExtendedSpec  {

	BBBInternationalCatalogToolsImpl impl
	Repository rep =Mock()
	RepositoryView attributeInfoView =Mock()
	RepositoryItem r1 =Mock()

	def setup(){
		impl = Spy()
		impl.setAttributeInfoRepository(rep)
		impl.setQueryForAttribute("age>30")
	}

	def"getAttributeInfo, returns the  sku attribute list with intl_flag != 'Y "(){

		given:
		1*rep.getView(BBBCatalogConstants.ATTRIBUTE_INFO) >> attributeInfoView
		1*impl.extractDBCall(attributeInfoView, _, _) >> [r1]
		1*r1.getPropertyValue(BBBInternationalShippingConstants.DISPLAY_DESCRIPTION) >> "key"
		1*r1.getPropertyValue(BBBInternationalShippingConstants.INTL_FLAG) >> "value"

		when:
		Map<String,String> map = impl.getAttributeInfo()

		then:
		map == ["key":"value"]
	}

	def"getAttributeInfo, when attributeListItemsList is empty "(){

		given:
		1*rep.getView(BBBCatalogConstants.ATTRIBUTE_INFO) >> attributeInfoView
		1*impl.extractDBCall(attributeInfoView, _, _) >> []
		0*r1.getPropertyValue(BBBInternationalShippingConstants.DISPLAY_DESCRIPTION)
		0*r1.getPropertyValue(BBBInternationalShippingConstants.INTL_FLAG)

		when:
		Map<String,String> map = impl.getAttributeInfo()

		then:
		map == [:]
	}

	def"getAttributeInfo, when attributeListItemsList is null "(){

		given:
		1*rep.getView(BBBCatalogConstants.ATTRIBUTE_INFO) >> attributeInfoView
		1*impl.extractDBCall(attributeInfoView, _, _) >> null
		0*r1.getPropertyValue(BBBInternationalShippingConstants.DISPLAY_DESCRIPTION)
		0*r1.getPropertyValue(BBBInternationalShippingConstants.INTL_FLAG)

		when:
		Map<String,String> map = impl.getAttributeInfo()

		then:
		map == [:]
	}

	def"getAttributeInfo, throws RepositoryException"(){

		given:
		1*rep.getView(BBBCatalogConstants.ATTRIBUTE_INFO) >> {throw new RepositoryException("")}
		0*impl.extractDBCall(attributeInfoView, _, _)
		0*r1.getPropertyValue(BBBInternationalShippingConstants.DISPLAY_DESCRIPTION)
		0*r1.getPropertyValue(BBBInternationalShippingConstants.INTL_FLAG)

		when:
		Map<String,String> map = impl.getAttributeInfo()

		then:
		map == null
		1*impl.logError("Catalog API Method Name [getAttributeInfo]: RepositoryException "+ BBBCatalogErrorCodes.UNABLE_TO_RETRIEVE_DATA_FROM_REPOSITORY_EXCEPTION,_)
		BBBSystemException e = thrown()
		e.getMessage().equals("2003")
	}

}
