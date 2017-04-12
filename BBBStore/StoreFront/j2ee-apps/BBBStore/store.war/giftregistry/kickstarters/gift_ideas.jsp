<dsp:getvalueof var="eventType" value="${fn:escapeXml(param.eventType)}"/>
<dsp:getvalueof var="registryId" param="registryId"/>
<dsp:getvalueof var="sortSeq" param="sortSeq"/>
<dsp:getvalueof var="view" value="${fn:escapeXml(param.view)}"/>		
<dsp:getvalueof var="certonaItemList" value="${fn:escapeXml(param.certonaItemList)}"/>		

<div id="giftIdeasContent" >

	<dsp:include page="popular_items_grid.jsp?eventType=${eventType}" flush="true" >
		<dsp:param name="omniDesc" value="Popular items (viewKickStarters)" />
	</dsp:include>

	
		<dsp:include page="/giftregistry/kickstarters/top_registry_items.jsp" flush="true" >
			<dsp:param name="registryId" value="${fn:escapeXml(param.registryId)}"/>
			<dsp:param name="sorting" value="${param.sortSeq}"/>
			<dsp:param name="view" value="${param.view}"/>
			<dsp:param name="skuList" value="${skuList}"/>
			<dsp:param name="certonaSkuList" value="${certonaSkuList}"/>
			<dsp:param name="itemList" value="${certonaItemList}"/>
		</dsp:include>					            
</div>