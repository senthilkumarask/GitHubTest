<dsp:page>
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/RegistryInfoDisplayDroplet" />
		<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
 <dsp:droplet name="RegistryInfoDisplayDroplet">
		<dsp:param param="registryId" name="registryId" />
		<dsp:oparam name="output">
		 <dsp:getvalueof var="totalToCopy" param="registrySummaryVO.giftRegistered"/>
		</dsp:oparam>
 </dsp:droplet>
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.eventType" var="registryName"/>
<dsp:getvalueof bean="SessionBean.values.userRegistrysummaryVO.eventType" var="ownerRegEventType"/>
<div id="confirmCopyRegistryDialog" title="${totalToCopy} Items Will be Added to Your <c:if test="${not empty ownerRegEventType}">${ownerRegEventType}</c:if> Registry" class="hidden">
	<bbbt:textArea key="txt_copy_this_registry_confirm_modal_msg" language ="${pageContext.request.locale.language}"/>
</div>
</dsp:page>