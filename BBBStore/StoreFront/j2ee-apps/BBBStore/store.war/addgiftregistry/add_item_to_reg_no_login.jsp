<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:getvalueof var="jasonObj" param="addItemResults" />
	<dsp:setvalue bean="GiftRegistryFormHandler.jasonCollectionObj" value="${jasonObj}" />
		<dsp:getvalueof var="addAllItems" param="addAll" />
	<c:if test="${not empty addAllItems}">
       <dsp:setvalue  bean="GiftRegistryFormHandler.kickStarterAddAllAction" value="${addAllItems}"/>
    </c:if>
	<dsp:setvalue bean="GiftRegistryFormHandler.addItemToGiftRegistry" />
</dsp:page>