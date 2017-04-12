<dsp:page>
 <dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:setvalue bean="GiftRegistryFormHandler.registryId" value="${param.registryId}" />
	<dsp:setvalue bean="GiftRegistryFormHandler.buyoffStartBrowsingSuccessURL" value="${param.startBrowsingSuccessURL}" />
				<dsp:setvalue bean="GiftRegistryFormHandler.buyOffStartBrowsing" />
</dsp:page>