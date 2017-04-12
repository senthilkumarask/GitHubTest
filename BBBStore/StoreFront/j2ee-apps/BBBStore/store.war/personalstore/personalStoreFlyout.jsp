<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
	<dsp:importbean bean="/atg/multisite/Site" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
	<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="pageVariation" value="bb" scope="request" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
		
	<c:set var="personalStoreImage" scope="request">
		<bbbc:config key="personalStoreImage" configName="ContentCatalogKeys" />
	</c:set>	
		
	<div class="container_12 clearfix personalStore">
	</div>
</dsp:page>
