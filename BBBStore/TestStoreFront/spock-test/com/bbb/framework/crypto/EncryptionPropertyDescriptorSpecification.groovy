package com.bbb.framework.crypto

import atg.nucleus.Nucleus;
import atg.nucleus.registry.NucleusRegistry;
import atg.repository.RepositoryImpl;
import atg.repository.RepositoryItemDescriptor
import atg.repository.RepositoryItemImpl
import atg.servlet.ServletUtil;
import spock.lang.specification.BBBExtendedSpec;

class EncryptionPropertyDescriptorSpecification extends BBBExtendedSpec {

	EncryptionPropertyDescriptor desc
	RepositoryItemImpl pItem =Mock()
	RepositoryItemDescriptor itemdesc =Mock()
	RepositoryItemDescriptor itemdesc1 =Mock()
	RepositoryImpl repositoryImpl =Mock()
	NucleusRegistry sNucleusRegistry = Mock()
	Nucleus nucleusMock = Mock()
	String pValue = new String("val")
	AbstractEncryptorComponent comp =Mock()

	def setup(){
		desc = Spy()
		desc.setItemDescriptor(itemdesc)
		desc.setName("Site")
		Nucleus.setNucleusRegistry(sNucleusRegistry)
		sNucleusRegistry.getKey() >> "nKey"
		sNucleusRegistry.get("nKey") >> nucleusMock
		Nucleus.sUsingChildNucleii = true
	}

	def"setPropertyValue , when mEncryptorComponent is null"(){

		given:
		itemdesc.getItemDescriptorName() >> "name"
		itemdesc.getRepository() >> repositoryImpl
		repositoryImpl.isLoggingError() >> true
		desc.getPropertyItemDescriptor() >> itemdesc1
		itemdesc1.getItemDescriptorName() >> "name1"

		when:
		desc.setPropertyValue(pItem, pValue)

		then:
		1*desc.logError("Property Item Descriptor: " + "name" + "." + "Site"+ " not property configured.")
		IllegalArgumentException e = thrown()
	}

	def"setPropertyValue , when pValue is null"(){

		given:
		when:
		desc.setPropertyValue(pItem, null)

		then:
		0*desc.extractSuperCall(pItem, null)
	}

	def"getPropertyValue , when mEncryptorComponent is null"(){

		given:
		1*requestMock.getRequestURI() >>"dyn"
		itemdesc.getItemDescriptorName() >> "name"
		itemdesc.getRepository() >> repositoryImpl
		repositoryImpl.isLoggingError() >> false
		desc.getPropertyItemDescriptor() >> itemdesc1
		itemdesc1.getItemDescriptorName() >> "name1"

		when:
		Object obj =desc.getPropertyValue(pItem, pValue)

		then:
		obj == null
		1*desc.logError("Property Item Descriptor: " + "name" + "." + "Site"+ " not property configured.")
		IllegalArgumentException e = thrown()
	}

	def"getPropertyValue , when URI contains URL Pattern mEncryptorComponent is null"(){

		given:
		1*requestMock.getRequestURI() >>"dyn/admin"
		itemdesc1.getItemDescriptorName() >> "name1"
		desc.extractSuperGet(pItem, pValue) >> {new String("returnFromSuper")}

		when:
		Object obj =desc.getPropertyValue(pItem, pValue)

		then:
		obj.equals("returnFromSuper") == true
		0*desc.extractSuperGetCall(pItem, pValue)
	}

	def"getPropertyValue , when URI is not null and not , mEncryptorComponent is null"(){

		given:
		ServletUtil.setCurrentRequest(null)
		desc.getItemDescriptor() >> itemdesc >> null >> itemdesc
		itemdesc.getItemDescriptorName() >> "name"
		itemdesc.getRepository() >> null >> repositoryImpl

		when:
		Object obj =desc.getPropertyValue(pItem, pValue)

		then:
		obj == null
		1*desc.logError("Property Item Descriptor: " + "name" + "." + "Site"+ " not property configured.")
		IllegalArgumentException e = thrown()
	}

	def"getPropertyValue , when URI is null, mEncryptorComponent is null"(){

		given:
		ServletUtil.setCurrentRequest(null)
		desc.getItemDescriptor() >> itemdesc >> null >> itemdesc
		itemdesc.getItemDescriptorName() >> "name"
		itemdesc.getRepository() >> null >> repositoryImpl

		when:
		Object obj =desc.getPropertyValue(pItem, pValue)

		then:
		obj == null
		1*desc.logError("Property Item Descriptor: " + "name" + "." + "Site"+ " not property configured.")
		IllegalArgumentException e = thrown()
	}

	def"getPropertyValue , when pValue is null"(){

		given:

		when:
		Object obj = desc.getPropertyValue(pItem, null)

		then:
		obj == null
		0*desc.extractSuperCall(pItem, null)
	}

	def"getPropertyValue , when pValue is NULL_OBJECT"(){

		given:
		Object pValue = RepositoryItemImpl.NULL_OBJECT

		when:
		Object obj =desc.getPropertyValue(pItem, pValue)

		then:
		obj == null
		0*desc.extractSuperGetCall(pItem, pValue)
	}

	/*def"setPropertyValue , when mEncryptorComponent is not null"(){
	 given:
	 desc.mEncryptorComponent = comp
	 itemdesc.getItemDescriptorName() >> "name"
	 itemdesc.getRepository() >> repositoryImpl
	 repositoryImpl.isLoggingError() >> false
	 1*desc.extractSuperCall(pItem, pValue) >>{}
	 when:
	 desc.setPropertyValue(pItem, pValue)
	 then:
	 1*desc.logError("Failed to encrypt property: ", _)
	 IllegalArgumentException e = thrown()
	 }*/

	def"setValue, when pAttribute is equal to encryptorComopnent, pValue is not null"(){

		given:
		String pAttributeName ="encryptorComponent"
		1*desc.extractSuperSetCall(pAttributeName, pValue) >> {}

		when:
		desc.setValue(pAttributeName,pValue)

		then:
		1*nucleusMock.resolveName(pValue)
	}

	def"setValue, when pAttribute is not equal to encryptorComopnent, pValue is not null"(){

		given:
		String pAttributeName ="abc"
		1*desc.extractSuperSetCall(pAttributeName, pValue) >> {}

		when:
		desc.setValue(pAttributeName,pValue)

		then:
		0*nucleusMock.resolveName(pValue)
	}

	def"setValue, when pAttribute is null,"(){

		given:
		String pAttributeName =null
		1*desc.extractSuperSetCall(pAttributeName, pValue) >> {}

		when:
		desc.setValue(pAttributeName,pValue)

		then:
		0*nucleusMock.resolveName(pValue)
	}

	def"setValue, when pValue is null,"(){

		given:
		String pAttributeName ="abc"
		1*desc.extractSuperSetCall(pAttributeName, null) >> {}

		when:
		desc.setValue(pAttributeName,null)

		then:
		0*nucleusMock.resolveName(pValue)
	}

	def"logDebug, Logs a debug statement for the repository we are part of"(){

		given:
		String pMessage ="abc"
		itemdesc.getItemDescriptorName() >> "name"
		itemdesc.getRepository() >> repositoryImpl
		repositoryImpl. isLoggingDebug() >> true

		when:
		desc.logDebug(pMessage)

		then:
		1*repositoryImpl.logDebug("Repository property: " + "Site" + " item-descriptor "+ "name" + ": " + pMessage);
	}

	def"logDebug, when loggingDebug is disabled"(){

		given:
		String pMessage ="abc"
		itemdesc.getItemDescriptorName() >> "name"
		itemdesc.getRepository() >> repositoryImpl
		repositoryImpl. isLoggingDebug() >> false

		when:
		desc.logDebug(pMessage)

		then:
		0*repositoryImpl.logDebug("Repository property: " + "Site" + " item-descriptor "+ "name" + ": " + pMessage);
	}

	def"logDebug, when ItemDescriptor is null"(){

		given:
		String pMessage ="abc"
		desc.getItemDescriptor() >> null

		when:
		desc.logDebug(pMessage)

		then:
		0*repositoryImpl.logDebug("Repository property: " + "Site" + " item-descriptor "+ "name" + ": " + pMessage);
	}

	def"setPropertyType, when class is not StringClass "(){

		given:
		Class pClass = java.lang.Boolean.class

		when:
		desc.setPropertyType(pClass)

		then:
		IllegalArgumentException e = thrown()
	}

	def"setComponentPropertyType, when class is not null "(){

		given:
		Class pClass = java.lang.Boolean.class

		when:
		desc.setComponentPropertyType(pClass)

		then:
		IllegalArgumentException e = thrown()
	}

	def"setComponentPropertyType, when class is  null "(){

		given:
		Class pClass = java.lang.Boolean.class

		when:
		desc.setComponentPropertyType(null)

		then:
		true == true
	}

	def"setPropertyItemDescriptor, when RepositoryItemDescriptor is not null "(){

		given:
		RepositoryItemDescriptor itemdesc =Mock()

		when:
		desc.setPropertyItemDescriptor(itemdesc)

		then:
		IllegalArgumentException e = thrown()
	}

	def"setPropertyItemDescriptor, when RepositoryItemDescriptor is  null "(){

		given:
		when:
		desc.setPropertyItemDescriptor(null)

		then:
		true == true
	}

	def"setComponentItemDescriptor, when RepositoryItemDescriptor is not null "(){

		given:
		RepositoryItemDescriptor itemdesc =Mock()

		when:
		desc.setComponentItemDescriptor(itemdesc)

		then:
		IllegalArgumentException e = thrown()
	}

	def"setComponentItemDescriptor, when RepositoryItemDescriptor is  null "(){

		given:
		when:
		desc.setComponentItemDescriptor(null)

		then:
		true == true
	}

	def"isQueryable, "(){

		given:
		when:
		boolean flag =desc.isQueryable()

		then:
		flag == true
	}


}
