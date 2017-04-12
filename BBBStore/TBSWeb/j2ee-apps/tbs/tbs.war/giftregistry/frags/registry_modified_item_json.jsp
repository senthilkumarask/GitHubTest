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
				<json:property name="regGiftsWanted" ><dsp:valueof param="registrySummaryVO.giftRegistered"/></json:property>
				<json:property name="regGiftPurchased" ><dsp:valueof param="registrySummaryVO.giftPurchased"/></json:property>
				<json:property name="regGiftsRemaining"><dsp:valueof param="registrySummaryVO.giftRemaining"/></json:property>
				<dsp:getvalueof var="updateDslFromModal" bean="GiftRegistryFormHandler.updateDslFromModal"/>
				<dsp:getvalueof var="updatedItemInfoMap" bean="GiftRegistryFormHandler.updatedItemInfoMap"/>
				<c:if test="${updateDslFromModal eq 'true'}">
				<dsp:droplet name="/atg/dynamo/droplet/ForEach">
					<dsp:param name="array" value="${updatedItemInfoMap}" />
					<dsp:oparam name="output">
					<dsp:getvalueof var="key" param="key"/>
					<dsp:getvalueof var="elementVal" param="element"/>
					<json:property name="${key}" value="${elementVal}" />
					</dsp:oparam>
				</dsp:droplet>
				</c:if>
			</json:object> 
	  </dsp:oparam>
	</dsp:droplet>

</dsp:page>