<%@ taglib uri="http://www.atg.com/taglibs/json" prefix="json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>


<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="queryString" value="${pageContext.request.queryString}" />
<dsp:getvalueof var="usePieCampaignId" value="true" scope="session"/>

<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductRedirectDroplet">
	<dsp:param name="siteId" value="${appid}"/>
	<dsp:param name="skuId" value="${param.purchasedItem}"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="productId" param="product.repositoryId"/>
	</dsp:oparam>
</dsp:droplet>
<c:choose>
	<c:when test="${fn:containsIgnoreCase(param.transactionType, 'R') and fn:containsIgnoreCase(param.customerType,'Registrant')}">
		<dsp:droplet name="GetRegistryTypeNameDroplet">
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="registryTypeCode" value="${param.eventType}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="eventTypeName" param="registryTypeName"/>
			</dsp:oparam>
		</dsp:droplet>
		<c:choose>
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIEREMINDER')}">
				<c:set var="campaignId"><bbbc:config key="BV_Registry_REMINDERPIE_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIE')}">
				<c:set var="campaignId"><bbbc:config key="BV_Registry_PIE_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>
		</c:choose>
		<c:set var="pageLocation" value="/giftregistry/view_registry_owner.jsp?registryId=${param.transactionID}&eventType=${eventTypeName}&productId=${productId}&pieCampaignId=${campaignId}&writeReview=${param.writeReview}" />
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIEREMINDER')}">
				<c:set var="campaignId"><bbbc:config key="BV_Order_PIE_REMINDER_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>		
			<c:when test="${fn:containsIgnoreCase(param.campaignType, 'PIE')}">
				<c:set var="campaignId"><bbbc:config key="BV_Order_PIE_campaign_id" configName="ThirdPartyURLs" /></c:set>
			</c:when>
		</c:choose>
		<c:set var="campaignId"><bbbc:config key="BV_track_order_campaign_id" configName="ThirdPartyURLs" /></c:set>	
		<c:set var="pageLocation" value="/account/track_order_guest.jsp?eid=${param.token}&orderId=${param.transactionID}&productId=${productId}&pieCampaignId=${campaignId}&writeReview=${param.writeReview}" />
	</c:otherwise>
</c:choose>

<%--Write review Parameters ${pageLocation}-- --%>
<dsp:droplet name="/com/bbb/commerce/browse/droplet/RedirectDroplet">
	<dsp:param name="url" value="${pageLocation}" />
</dsp:droplet>