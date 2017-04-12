<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ include file="/includes/taglibs.jspf" %>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor" prefix="compress" %>

<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/personalstore/droplet/PersonalStoreDroplet" />
	<dsp:importbean bean="/atg/targeting/TargetingArray" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />

	<!-- GETTING VALUE FOR AJAX CALL -->
			<dsp:getvalueof var="strategyTitle" param="element.strategyPageTitle" />
			<dsp:getvalueof var="strategyContextTitle" param="element.strategyContextTitle" />
			<dsp:getvalueof var="strategyId" param="element.id" />
			<dsp:getvalueof var="strategyName" param="element.strategyName" />
			<dsp:getvalueof var="strategyType" param="element.strategyType" />
			<dsp:getvalueof var="numOfProducts" param="element.strategyLayout.layoutNumProducts" />
			<dsp:getvalueof var="strategyCode" value="${strategyName}" />
			<dsp:getvalueof var="userId" param="userId" />
			<dsp:getvalueof var="productList" param="productList" />
			<dsp:getvalueof var="appid" param="appid" />
			<dsp:getvalueof var="sessionid" param="sessionid" />
			<dsp:getvalueof var="psStrategyContextMap" param="psStrategyContextMap" />psStrategyJSPMap
			<dsp:getvalueof var="psStrategyJSPMap" param="psStrategyJSPMap" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	
	<bbb:pageContainer index="false" follow="false">
		<%-- <jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="pageWrapper">personalStorePage useCertonaJs useLiveClicker</jsp:attribute>
		<jsp:attribute name="PageType">PersonalStorePage</jsp:attribute> --%>
		<jsp:body>

			<c:if test="${(strategyType == 'Recommendation' && not empty psStrategyContextMap[strategyId]) || strategyType == 'Trending'}">
				<dsp:droplet name="TargetingArray">
				    <dsp:param bean="${psStrategyTargeterMap[strategyCode]}"
								name="targeter" />
					<dsp:param name="userid" value="${userId}" />
					<dsp:param name="exitemid" value="${productList}" />
					<dsp:param name="siteId" value="${appid}" />
					<dsp:param name="number" value="${numOfProducts}" />
					<dsp:param name="sourceId" value="${strategyName}" />
					<dsp:param name="sessionid" value="${sessionid}" />
					<%-- Pass the context in targeter only if type is recommendation --%>
					<c:if test="${strategyType == 'Recommendation'}">
						<dsp:param name="context" value="${psStrategyContextMap[strategyId]}" />
					</c:if>
					<dsp:oparam name="output">
					  	<dsp:getvalueof var="productArray" param="elements" />
					  	
					  	<%-- Include the JSP from strategy-jsp map --%>
					  	<dsp:include page="${psStrategyJSPMap[strategyId]}">
						 	<dsp:param name="productsVOsList" value="${productArray}" />
						 	<dsp:param name="strategyTitle" value="${strategyTitle}" />
						 	<dsp:param name="strategyContextTitle" value="${strategyContextTitle}" />
						 	<dsp:param name="strategyContextValue" value="${psStrategyContextMap[strategyId]}" />
						 	<dsp:param name="strategyType" value="${strategyType}" />
						 </dsp:include>
					</dsp:oparam>
			    </dsp:droplet>
			    </c:if>   
	</jsp:body>

	<jsp:attribute name="footerContent">
    </jsp:attribute>
	</bbb:pageContainer>
	<c:if test="${TellApartOn}">
		<bbb:tellApart actionType="pv" />
	</c:if>
	
</dsp:page>