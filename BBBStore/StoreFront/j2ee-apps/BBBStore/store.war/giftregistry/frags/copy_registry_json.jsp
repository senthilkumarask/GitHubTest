<dsp:page>

	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/bean/GiftRegSessionBean" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
	
	<dsp:getvalueof var="registryId" param="registryId"/>
	
	<dsp:droplet name="RegistryInfoDisplayDroplet">
		<dsp:param value="${registryId}" name="registryId" />
		<dsp:param name="displayView" value="owner" />		
		<dsp:oparam name="output">
			<json:object>
				<json:property name="error"><dsp:valueof bean="GiftRegistryFormHandler.formError"/></json:property>
				<json:property name="regGiftsWanted" ><dsp:valueof param="regCopyresVO.getIsSetRegCopyResult()"/></json:property>
				<%--
					<json:property name="regGiftPurchased" ><dsp:valueof param="registrySummaryVO.giftPurchased"/></json:property>
					<json:property name="regGiftsRemaining"><dsp:valueof param="registrySummaryVO.giftRemaining"/></json:property>
				--%>
			</json:object> 
	  </dsp:oparam>
	</dsp:droplet>

</dsp:page>