<dsp:page>
	<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryVODroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof var="appid" bean="Site.id" />
    	
	<dsp:getvalueof var="registryId" param="registryId"/>

	<dsp:droplet name="GetRegistryVODroplet">
		<dsp:param name="siteId" value="${appid}"/>
		<dsp:param value="${registryId}" name="registryId" />
		<dsp:oparam name="output">
	          <dsp:valueof param="registryVO.siteId"/>
		</dsp:oparam>
	</dsp:droplet>

</dsp:page>


