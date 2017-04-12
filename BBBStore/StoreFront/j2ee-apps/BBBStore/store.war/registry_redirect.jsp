<dsp:page>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryIdDecriptorDroplet"/>	
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryPaginationDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />	
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/targeting/RepositoryLookup"/>
	
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="reg_encripted_id" param="wrn" />

	<dsp:droplet name="GiftRegistryIdDecriptorDroplet">
	<dsp:param name="registryEncriptedId" param="wrn"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="reg_decripted_id" param="registryId"/>
	</dsp:oparam>
	</dsp:droplet>
    <dsp:droplet name="/atg/targeting/RepositoryLookup">
    	<dsp:param name="repository" bean="/com/bbb/commerce/giftregistry/GiftRegistryRepository"/>
    	<dsp:param name="id" value="${reg_decripted_id}"/>
    	<dsp:param name="itemDescriptor" value="giftregistry"/>
    	<dsp:oparam name="output">
    	     <dsp:getvalueof var="reg_event_type" param="element.eventType"/></h1>
             <dsp:droplet name="GetRegistryTypeNameDroplet">
                <dsp:param name="siteId" value="${appid}"/>
			    <dsp:param name="registryTypeCode" value="${reg_event_type}"/>
    			<dsp:oparam name="output">
					<dsp:getvalueof var="eventType" param="registryTypeName"/>
					<%-- <h1>${reg_decripted_id}-${reg_event_type}-${reg_type_name}</h1> --%>
				</dsp:oparam>
			</dsp:droplet>
    	</dsp:oparam>
  	</dsp:droplet>
  	
  	<%-- Start new code --%>
  	<c:if test="${empty eventType }">
  	<dsp:droplet name="/com/bbb/commerce/giftregistry/droplet/GiftRegistrySessionExpiryDroplet">
	</dsp:droplet>
  	<dsp:droplet name="GiftRegistryPaginationDroplet">
	<dsp:param name="registryId" value="${reg_decripted_id}"/>
	<dsp:param name="siteId" value="${appid}"/>		
			<dsp:oparam name="output">
						<dsp:droplet name="IsEmpty">
								<dsp:param param="registrySummaryResultList" name="value" />
								<dsp:oparam name="false">
								<dsp:getvalueof var="registrySummaryResultList" param="registrySummaryResultList" />
								<dsp:droplet name="ForEach">				
										<dsp:param name="array" param="registrySummaryResultList" />			
										<dsp:oparam name="output">
										<dsp:getvalueof var="eventType" param="element.eventType" />
						</dsp:oparam>
						</dsp:droplet>
			</dsp:oparam>
	</dsp:droplet>
	</dsp:oparam>
	</dsp:droplet>
	</c:if>
  <%--	End new code --%>
  	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
  	<c:set var="url" value="${contextPath}" />
    <c:set var="urlParams" value="/giftregistry/view_registry_guest.jsp?pwsToken=&eventType=${eventType}&registryId=${reg_decripted_id}" />  	
	<c:set var="finalUrl" value="${url}${urlParams}" />
<script>
var registryUrl = '${finalUrl}';
window.location = registryUrl
</script>

</dsp:page>