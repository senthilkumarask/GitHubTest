<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />
<dsp:page>

	<%-- Validate external parameters --%>
	<dsp:droplet name="ValidateParametersDroplet">
	    <dsp:param value="registryId;eventType" name="paramArray" />
	    <dsp:param value="${param.registryId};${param.eventType}" name="paramsValuesArray" />
	    <dsp:oparam name="error">
	      <dsp:droplet name="RedirectDroplet">
	        <dsp:param name="url" value="/404.jsp" />
	      </dsp:droplet>
	    </dsp:oparam>
    </dsp:droplet> 

<div id="response">
	<div id="regData">
	<dsp:include page="registry_items_owner.jsp" flush="true" >
		
		<dsp:param name="registryId" value="${fn:escapeXml(param.registryId)}"/>
		<dsp:param name="startIdx" value="0"/>
		<dsp:param name="isGiftGiver" value="false"/>
		<dsp:param name="blkSize" value="${param.maxBulkSize}"/>	
		<dsp:param name="isAvailForWebPurchaseFlag" value="true"/>
		<dsp:param name="sortSeq" value="${param.sortSeq}" />
		<dsp:param name="view" value="${param.view}" />
		<dsp:param name="eventType" value="${fn:escapeXml(param.eventType)}"/>
		<dsp:param name="eventTypeCode" value="${param.eventTypeCode}"/>
	</dsp:include>
	</div>
</div>
</dsp:page>