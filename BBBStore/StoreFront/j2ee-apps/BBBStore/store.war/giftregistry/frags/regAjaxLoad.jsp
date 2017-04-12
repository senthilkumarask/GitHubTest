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
    <%-- 		changes to show either checklist myitems or old my items  on the basis of checklist is enabled or not --%>
    <c:set var="myItemsFlag">
			<bbbc:config key="My_Items_Checklist_Flag" configName="FlagDrivenFunctions" />
		</c:set>
	<dsp:getvalueof var="isChecklistDisabled" value="${param.isChecklistDisabled}"/>
<div id="response">
	<div id="regData">
	<c:choose>
		<c:when test="${isChecklistDisabled or null eq isChecklistDisabled or empty isChecklistDisabled or !myItemsFlag}">
			<c:set var="isMyItemsCheckList">false</c:set>
		</c:when>
		<c:otherwise>
			<c:set var="isMyItemsCheckList">true</c:set>
		</c:otherwise>
	</c:choose>
		<dsp:include page="registry_items_owner.jsp" flush="true" >
			<dsp:param name="registryId" value="${fn:escapeXml(param.registryId)}"/>
			<dsp:param name="startIdx" value="0"/>
			<dsp:param name="isGiftGiver" value="false"/>
			<dsp:param name="blkSize" value="${param.maxBulkSize}"/>	
			<dsp:param name="isAvailForWebPurchaseFlag" value="true"/>
			<dsp:param name="sortSeq" value="${param.sortSeq}" />
			<dsp:param name="c1name" value="${fn:escapeXml(param.c1name)}" />
			<dsp:param name="c2name" value="${fn:escapeXml(param.c2name)}" />
			<dsp:param name="c3name" value="${fn:escapeXml(param.c3name)}" />
			<dsp:param name="qty" value="${fn:escapeXml(param.qty)}" />
			<dsp:param name="c1id" value="${fn:escapeXml(param.c1id)}" />
			<dsp:param name="c2id" value="${fn:escapeXml(param.c2id)}" />
			<dsp:param name="c3id" value="${fn:escapeXml(param.c3id)}" />
			<dsp:param name="view" value="${param.view}" />
			<dsp:param name="eventType" value="${fn:escapeXml(param.eventType)}"/>
			<dsp:param name="eventTypeCode" value="${param.eventTypeCode}"/>
			<dsp:param name="eventDate" value="${param.eventDate}"/>
			<dsp:param name="isMyItemsCheckList" value="${isMyItemsCheckList}"/>
		</dsp:include>
       	<div id ="certonaItems" >
		<c:if test="${!isMyItemsCheckList}">
			<dsp:include page="/giftregistry/kickstarters/top_registry_items.jsp" flush="true" >
				<dsp:param name="registryId" value="${fn:escapeXml(param.registryId)}"/>
				<dsp:param name="sorting" value="${param.sortSeq}"/>
				<dsp:param name="view" value="${param.view}"/>
				<dsp:param name="skuList" value="${skuList}"/>
				<dsp:param name="certonaSkuList" value="${certonaSkuList}"/>
			</dsp:include>

       </c:if>        
       </div>
	</div>

	
</div>
</dsp:page>