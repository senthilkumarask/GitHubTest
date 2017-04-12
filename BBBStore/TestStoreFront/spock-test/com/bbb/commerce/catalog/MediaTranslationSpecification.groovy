package com.bbb.commerce.catalog

import atg.nucleus.logging.ApplicationLogging;
import atg.repository.Query
import atg.repository.QueryBuilder
import atg.repository.QueryExpression
import atg.repository.RepositoryItemImpl;
import atg.repository.RepositoryPropertyDescriptor
import atg.repository.UnsupportedFeatureException
import atg.repository.dp.Derivation
import atg.repository.dp.DerivedPropertyDescriptor;
import atg.repository.dp.PropertyExpression
import atg.repository.query.PropertyQueryExpression
import spock.lang.specification.BBBExtendedSpec;

class MediaTranslationSpecification extends BBBExtendedSpec {

	MediaTranslation media
	ApplicationLogging mLogger = Mock()
	Derivation pDerivation =Mock()
	DerivedPropertyDescriptor pd =Mock()
	RepositoryItemImpl pItem =Mock()
	PropertyExpression pe =Mock()
	String str = new String("StringObjectReturned")


	def setup(){
		media = Spy()
		media.setDerivation(pDerivation)
		pDerivation.getPropertyDescriptor() >> pd
		media.getLogger()  >> mLogger
		mLogger.isLoggingDebug() >> true
	}

	def"derivePropertyValue, when getExpressionList is null"(){

		given:
		1*pDerivation.getExpressionList() >> []

		when:
		Object obj = media.derivePropertyValue(pItem)

		then:
		obj == null
	}

	def"derivePropertyValue, Determine the derived property value"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()

		pDerivation.getExpressionList() >> [pe]
		pe.getPropertyDescriptor() >> rdesc
		rdesc.getName() >> "abc"
		pe.evaluate(pItem) >> "returnVal"

		pd.getValue(MediaTranslation.SCENE7_URL) >> "URL"
		pd.getValue(MediaTranslation.APPEND_VALUE) >> "append"
		pItem.getPropertyValue(MediaTranslation.SCENE7_URL) >> "www"
		pItem.getPropertyValue("URL") >> str

		when:
		Object obj = media.derivePropertyValue(pItem)

		then:
		obj.equals("wwwappend") == true
	}

	def"derivePropertyValue, Determine the derived property value when appendValue is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()

		pDerivation.getExpressionList() >> [null]
		0*pe.evaluate(pItem) >> "returnVal"
		pd.getValue(MediaTranslation.SCENE7_URL) >> "URL"
		pd.getValue(MediaTranslation.APPEND_VALUE) >> null
		pItem.getPropertyValue(MediaTranslation.SCENE7_URL) >> "www"
		pItem.getPropertyValue("URL") >> str

		when:
		Object obj = media.derivePropertyValue(pItem)

		then:
		obj.equals("www") == true
	}

	def"derivePropertyValue, Determine the derived property value when pItem.getPropertyValue(scene7URL) is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()

		pDerivation.getExpressionList() >> [pe]
		pe.getPropertyDescriptor() >> rdesc
		rdesc.getName() >> "abc"
		pe.evaluate(pItem) >> "returnVal"

		pd.getValue(MediaTranslation.SCENE7_URL) >> "URL"
		pd.getValue(MediaTranslation.APPEND_VALUE) >> "append"
		pItem.getPropertyValue(MediaTranslation.SCENE7_URL) >> "www"
		pItem.getPropertyValue("URL") >> null

		when:
		Object obj = media.derivePropertyValue(pItem)

		then:
		obj.equals("returnVal") == true
	}

	def"derivePropertyValue, Determine the derived property value when scene7URL is null"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()

		pDerivation.getExpressionList() >> [pe]
		pe.getPropertyDescriptor() >> rdesc
		rdesc.getName() >> "abc"
		pe.evaluate(pItem) >> "returnVal"

		pd.getValue(MediaTranslation.SCENE7_URL) >> null
		pd.getValue(MediaTranslation.APPEND_VALUE) >> "append"
		pItem.getPropertyValue(MediaTranslation.SCENE7_URL) >> "www"
		pItem.getPropertyValue(null) >> "www"

		when:
		Object obj = media.derivePropertyValue(pItem)

		then:
		obj.equals("returnVal") == true
	}

	def"derivePropertyValue, when Exception is thrown while obtaining propName"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()

		pDerivation.getExpressionList() >> [pe]
		pe.getPropertyDescriptor() >> rdesc
		rdesc.getName() >> {throw new Exception("")}
		0*pe.evaluate(pItem)

		when:
		Object obj = media.derivePropertyValue(pItem)

		then:
		obj == null
	}

	def"derivePropertyValue, when Exception is thrown"(){

		given:
		RepositoryPropertyDescriptor rdesc =Mock()

		pDerivation.getExpressionList() >> [pe]
		pe.getPropertyDescriptor() >> rdesc
		rdesc.getName() >> "abc"
		pe.evaluate(pItem) >> "returnVal"

		pd.getValue(MediaTranslation.SCENE7_URL) >> "URL"
		pd.getValue(MediaTranslation.APPEND_VALUE) >> "append"
		pItem.getPropertyValue(MediaTranslation.SCENE7_URL) >> {throw new Exception("")}
		pItem.getPropertyValue("URL") >> str

		when:
		Object obj = media.derivePropertyValue(pItem)

		then:
		obj.equals("returnVal") == true
	}

	def"getAppendValue, when mAppendValue is not null"(){

		given:
		media.setAppendValue("value")
		media.setScene7URL("scene7URL")

		when:
		String str =media.getAppendValue()

		then:
		str == "value"
	}

	def"derivePropertyValue(Object parameter)"(){

		given:
		String pBean = new String("")

		when:
		String str =media.derivePropertyValue(pBean)

		then:
		str == ""
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
		media.createQuery(pQueryType, pDerivedPropertyOnLeft, pCountDerivedProperty,pOther,pOperator,  pIgnoreCase,
				pMinScore,pSearchStringFormat,pItemQuery,pBuilder,pParentProperty,pChildPropertyList)

		then:
		UnsupportedFeatureException e = thrown()

	}

	/*def"derivePropertyValue, Determine the derived property value4"(){
	 given:
	 RepositoryPropertyDescriptor rdesc =Mock()
	 RepositoryItemImpl pItem1  = null
	 1*pDerivation.getExpressionList() >> [pe]
	 1*pe.getPropertyDescriptor() >> rdesc
	 1*rdesc.getName() >> "abc"
	 1*pe.evaluate(null) >> "returnVal"
	 when:
	 Object obj = media.derivePropertyValue(pItem1)
	 then:
	 obj.equals("returnVal") == true
	 }*/
}
