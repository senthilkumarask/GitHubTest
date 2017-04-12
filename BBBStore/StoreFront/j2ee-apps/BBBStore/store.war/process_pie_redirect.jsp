<%@taglib uri="http://www.atg.com/taglibs/json" prefix="json"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<dsp:page>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
	<dsp:param value="ThirdPartyURLs" name="configType" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="jsPath" param="jsPath" scope="request"/>
	</dsp:oparam>
</dsp:droplet>
<dsp:droplet name="/com/bbb/utils/BBBLogBuildNumber">
	<dsp:oparam name="output">
		<dsp:getvalueof var="buildRevisionNumber" param="BUILD_TAG_NUM" />
	</dsp:oparam>
</dsp:droplet>
<c:set var="mobileRedirectOn" scope="request"><bbbc:config key="mobileRedirectJS" configName="ContentCatalogKeys" /></c:set>
<c:if test="${mobileRedirectOn}">
<script type="text/javascript" src="${jsPath}/_assets/global/js/mobileRedirect.js?v=${buildRevisionNumber}"></script>
</c:if>

<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="queryString" value="${pageContext.request.queryString}" />
<c:choose>
	<c:when test="${fn:containsIgnoreCase(param.customerType, 'R')}">
		<c:choose>
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIE_Reminder')}">
				<c:set var="campaignId"><bbbc:config key="BV_Registry_REMINDERPIE_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIE')}">
				<c:set var="campaignId"><bbbc:config key="BV_Registry_PIE_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>
		</c:choose>
		<dsp:include page="/giftregistry/view_registry_owner.jsp">
		     <dsp:param name="registryId" value="${param.transactionId}"/>
		     <dsp:param name="eventType" value="${param.eventTypeName}"/>
		     <dsp:param name="productId" value="${param.productId}"/>
		     <dsp:param name="pieCampaignId" value="${campaignId}"/>
		     <dsp:param name="writeReview" value="true"/>
		 </dsp:include>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIE_Reminder')}">
				<c:set var="campaignId"><bbbc:config key="BV_Order_PIE_REMINDER_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>		
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIE')}">
				<c:set var="campaignId"><bbbc:config key="BV_Order_PIE_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>
		</c:choose>
		<dsp:include page="/account/track_order_guest.jsp">
	     <dsp:param name="eid" value="${param.token}"/>
	     <dsp:param name="orderId" value="${param.transactionId}"/>
	     <dsp:param name="productId" value="${param.productId}"/>
	     <dsp:param name="pieCampaignId" value="${campaignId}"/>
	     <dsp:param name="writeReview" value="true"/>
	  </dsp:include>	

	</c:otherwise>
</c:choose>
</dsp:page>