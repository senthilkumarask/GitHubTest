package com.bbb.commerce.catalog

import atg.nucleus.logging.ApplicationLogging
import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.RepositoryItem
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor
import atg.repository.UnsupportedFeatureException
import atg.repository.dp.Derivation
import atg.repository.dp.DerivedPropertyDescriptor
import atg.repository.query.PropertyQueryExpression
import spock.lang.specification.BBBExtendedSpec;

class GenericSetTranslationSpecification extends BBBExtendedSpec {


	GenericSetTranslation testObj
	RepositoryItemImpl pItem =Mock()
	Derivation der = Mock()
	DerivedPropertyDescriptor pd =Mock()
	Set<RepositoryItem> rSet = new HashSet()
	ApplicationLogging mlog =Mock()


	def setup(){
		testObj =Spy() //toCoverLogging
		testObj.getLogger() >> mlog
		mlog.isLoggingDebug() >>  true
		testObj.setDerivation(der)
		der.getPropertyDescriptor() >> pd
	}

	def "derivePropertyValue. Determine the derived property value."(){

		given:
		RepositoryItem r1 =Mock()
		RepositoryItem r2 =Mock()

		1*pd.getValue("property") >> "prop"
		pd.getValue("itemDescProperty") >> "itemDesc"
		rSet.add(r1)
		rSet.add(r2)
		1*pItem.getPropertyValue("prop") >> rSet

		1*r1.getPropertyValue("itemDesc") >> "tempPropValue"
		1*r2.getPropertyValue("itemDesc") >> ""

		when:
		String returnValue = testObj.derivePropertyValue(pItem)

		then:
		returnValue == "tempPropValue"
		0*pItem.getPropertyValue("ID")
	}

	def "derivePropertyValue. when itemList is empty."(){

		given:
		1*pd.getValue("property") >> "prop"
		pd.getValue("itemDescProperty") >> "itemDesc"
		1*pItem.getPropertyValue("prop") >> rSet

		when:
		String returnValue = testObj.derivePropertyValue(pItem)

		then:
		returnValue == null
		0*pItem.getPropertyValue("ID")
	}

	def "derivePropertyValue. when itemList is null."(){

		given:
		1*pd.getValue("property") >> "prop"
		pd.getValue("itemDescProperty") >> "itemDesc"
		1*pItem.getPropertyValue("prop") >> null

		when:
		String returnValue = testObj.derivePropertyValue(pItem)

		then:
		returnValue == null
		0*pItem.getPropertyValue("ID")
	}

	def "derivePropertyValue. when itemDesc is empty."(){

		given:
		RepositoryItem r1 =Mock()

		1*pd.getValue("property") >> "prop"
		pd.getValue("itemDescProperty") >> ""
		rSet.add(r1)
		1*pItem.getPropertyValue("prop") >> rSet

		when:
		String returnValue = testObj.derivePropertyValue(pItem)

		then:
		returnValue == null
		0*pItem.getPropertyValue("ID")
	}

	def "derivePropertyValue. when propName is null."(){

		given:
		1*pd.getValue("property") >> null
		1*pItem.getPropertyValue("ID") >> "returnVal"
		0*pd.getValue("itemDescProperty")
		0*pItem.getPropertyValue(null)

		when:
		String returnValue = testObj.derivePropertyValue(pItem)

		then:
		returnValue == "returnVal"
		0*pd.getValue("itemDescProperty")
	}

	def "derivePropertyValue. when Exception is thrown."(){

		given:
		RepositoryItem r1 =Mock()
		1*pd.getValue("property") >> "prop"
		pd.getValue("itemDescProperty") >> "itemDesc"
		1*pItem.getPropertyValue("prop") >> [r1:"abc"]

		when:
		String returnValue = testObj.derivePropertyValue(pItem)

		then:
		returnValue == null
		0*pd.getValue("itemDescProperty")
	}

	def"createQuery"(){

		given:
		int pQueryType=1
		boolean pDerivedPropertyOnLeft=true
		boolean pCountDerivedProperty =true
		QueryExpression pOther= Mock()
		int pOperator =1
		boolean pIgnoreCase =true
		QueryExpression pMinScore =Mock()
		QueryExpression pSearchStringFormat =Mock()
		Query pItemQuery =Mock()
		QueryBuilder pBuilder =Mock()
		PropertyQueryExpression pParentProperty= Mock()
		List pChildPropertyList=[]

		when:
		testObj.createQuery(pQueryType, pDerivedPropertyOnLeft, pCountDerivedProperty,pOther,pOperator,  pIgnoreCase,
				pMinScore,pSearchStringFormat,pItemQuery,pBuilder,pParentProperty,pChildPropertyList)

		then:
		UnsupportedFeatureException e = thrown()
	}

	def"derivePropertyValue(overloaded)"(){

		given:
		Object pBean = Mock()
		testObj.setItemDescProperty("")

		when:
		Object returnValue = testObj.derivePropertyValue(pBean)

		then:
		returnValue == null
		UnsupportedFeatureException e = thrown()
	}

	def"getProperty, when mProperty is not null"(){

		given:

		testObj.setProperty("")

		when:
		String returnValue = testObj.getProperty()

		then:
		returnValue == ""
	}

	def"isQueryable"(){

		given:

		when:
		boolean flag =testObj.isQueryable()

		then:
		flag == false
	}



	/*def "derivePropertyValue. when pItem is null."(){
	 given:
	 0*pd.getValue(_)
	 0*pItem.getPropertyValue("ID")
	 0*pd.getValue(_)
	 0*pItem.getPropertyValue(null)
	 when:
	 String returnValue = testObj.derivePropertyValue(null)
	 then:
	 returnValue == null
	 }*/
}
