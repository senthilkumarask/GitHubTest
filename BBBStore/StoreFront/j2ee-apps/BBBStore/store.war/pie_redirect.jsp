<%@ taglib uri="http://www.atg.com/taglibs/json" prefix="json" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GetRegistryTypeNameDroplet"/>


<dsp:getvalueof var="appid" bean="Site.id" />
<c:set var="queryString" value="${pageContext.request.queryString}" />
<%-- Removing the customer type parameter as EMS not sending correct values  --%>
<dsp:getvalueof var="queryString" value="${fn:replace(queryString, 'customerType', 'origCustomerType')}" />
<dsp:getvalueof var="usePieCampaignId" value="true" scope="session"/>

<dsp:droplet name="/com/bbb/commerce/browse/droplet/ProductRedirectDroplet">
	<dsp:param name="siteId" value="${appid}"/>
	<dsp:param name="skuId" value="${param.sku}"/>
	<dsp:oparam name="output">
		<dsp:getvalueof var="productId" param="product.repositoryId"/>
	</dsp:oparam>
</dsp:droplet>
<dsp:getvalueof var="queryString" value="${queryString}&productId=${productId}" />
<c:choose>
	<c:when test="${not empty param.eventType}">
		<dsp:droplet name="GetRegistryTypeNameDroplet">
			<dsp:param name="siteId" value="${appid}"/>
			<dsp:param name="registryTypeCode" value="${param.eventType}"/>
			<dsp:oparam name="output">
				<dsp:getvalueof var="eventTypeName" param="registryTypeName"/>
			</dsp:oparam>
		</dsp:droplet>
		<dsp:getvalueof var="queryString" value="${queryString}&customerType=R&eventTypeName=${eventTypeName}" />
	</c:when>
	<c:otherwise>
		<dsp:getvalueof var="queryString" value="${queryString}&customerType=S" />
	</c:otherwise>
</c:choose>

<%--Write review Parameters ${pageLocation}-- --%>
<dsp:droplet name="/com/bbb/commerce/browse/droplet/RedirectDroplet">
	<dsp:param name="url" value="/process_pie_redirect.jsp?${queryString}&writeReview=true" />
</dsp:droplet>