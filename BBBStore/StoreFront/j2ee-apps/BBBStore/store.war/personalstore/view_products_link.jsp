<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ include file="/includes/taglibs.jspf"%>
<%@ taglib uri="http://htmlcompressor.googlecode.com/taglib/compressor"
	prefix="compress"%>

<dsp:page>

	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean
		bean="/com/bbb/personalstore/droplet/PersonalStoreStrategyLookupDroplet" />
	<dsp:importbean bean="/atg/targeting/TargetingArray" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ProdToutDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/ExitemIdDroplet" />

	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />

	<dsp:getvalueof var="strategyId" param="strategyId" />
	<dsp:getvalueof var="strategyContextCode" param="strategyContextCode" />
	<dsp:getvalueof var="strategyContextValue" param="strategyContextValue" />
	 
	  <c:set var="viewAll" scope="request">
		<bbbc:config key="psViewAllOn" configName="ContentCatalogKeys" />
	</c:set>  

	<c:set var="numOfProducts" scope="request">
		<bbbc:config key="numProductsView" configName="ContentCatalogKeys" />
	</c:set> 
	 
	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="pageWrapper">personalStorePage useCertonaJs viewAllPage useLiveClicker</jsp:attribute>
		<jsp:attribute name="PageType">PersonalStorePage</jsp:attribute>
		<jsp:body>

		<%-- find userid (if logged-in) --%>
    <dsp:droplet name="Switch">
        <dsp:param name="value" bean="Profile.transient" />
        <dsp:oparam name="false">
            <dsp:getvalueof var="userId" bean="Profile.id" />
        </dsp:oparam>
        <dsp:oparam name="true">
            <dsp:getvalueof var="userId" value="" />
        </dsp:oparam>
    </dsp:droplet>
    
	<dsp:droplet name="ProdToutDroplet">
		<dsp:param value="lastviewed" name="tabList" />
		<dsp:param param="categoryId" name="id" />
		<dsp:param value="${appid}" name="siteId" />
		<dsp:param name="productId" param="productId" />
			<dsp:oparam name="output">
				<dsp:getvalueof var="lastviewedProductsList"
						param="lastviewedProductsList" />
				<dsp:droplet name="ExitemIdDroplet">
			          <dsp:param value="${lastviewedProductsList}"
							name="lastviewedProductsList" />
			          <dsp:oparam name="output">
					    <dsp:getvalueof var="productList" param="productList" />
                      </dsp:oparam>
				</dsp:droplet>
			</dsp:oparam>
	</dsp:droplet>
	
	<div id="content" class="container_12 clearfix personalStore">
	<%-- Droplet to get the strategy details based on the selected strategyId --%>
	  <dsp:droplet name="PersonalStoreStrategyLookupDroplet">
			<dsp:param name="strategyId" value="${strategyId}" />
			<dsp:param name="strategyContextCode" value="${strategyContextCode}" />
			<dsp:oparam name="output">
					
					<%-- Get the context value from the cookie --%>
					<dsp:getvalueof var="psStrategyContextVal"
							param="psStrategyDetails.strategyCookieValue" />
					
					<%-- Get the map of Strategy-Targeter mapping --%>
					<dsp:getvalueof var="psStrategyTargeterMap"
							bean="com/bbb/personalstore/tools/PersonalStoreTools.personalStoreTargetMapping"
							varType="java.util.map" />
	
		  	
				    <%-- Get the strategy details from a selected Strategy--%>
						<dsp:getvalueof var="strategyTitle"
							param="psStrategyDetails.strategyDetails.strategyPageTitle" />
						<dsp:getvalueof var="strategyContextTitle"
							param="psStrategyDetails.strategyDetails.strategyContextTitle" />
						<dsp:getvalueof var="strategyId"
							param="psStrategyDetails.strategyDetails.id" />
						<dsp:getvalueof var="strategyName"
							param="psStrategyDetails.strategyDetails.strategyName" />
						<dsp:getvalueof var="strategyType"
							param="psStrategyDetails.strategyDetails.strategyType" />
						<dsp:getvalueof var="strategyNameMap"
							param="strategyNameMap" />
					<%-- Strategy Code to get the Targeter value from Strategy-Targeter mapping --%>
						<dsp:getvalueof var="strategyCode" value="${strategyName}" />
						
						
					<h2 class="grid_12 viewAllPageHeading">${strategyTitle}</h2>
				    
				    
						<%-- Calling the targeters to get the recommendations --%>
						<dsp:droplet name="TargetingArray">
						    <dsp:param bean="${psStrategyTargeterMap[strategyCode]}"
									name="targeter" />
							<dsp:param name="userid" value="${userId}" />
							<dsp:param name="exitemid" value="${productList}" />
							<dsp:param name="siteId" value="${appid}" />
							<dsp:param name="number" value="${numOfProducts}" />
							<dsp:param name="sourceId" value="${strategyName}" />
							
							<%-- Pass the context in targeter only if type is recommendation --%>
							<c:if test="${strategyType == 'Recommendation'}">
								<dsp:param name="context" value="${psStrategyContextVal}" /> 
							</c:if>
							<dsp:oparam name="output">
								 	<dsp:getvalueof var="productArray" param="elements" />
								 			<c:if test="${viewAll eq 'TRUE'}">
								 			<%-- Include the JSP from strategy-jsp map --%>
								 			<dsp:include page="/personalstore/viewAllPage_product.jsp">
								 				<dsp:param name="productsVOsList" value="${productArray}" />
										   		<dsp:param name="strategyTitle" value="${strategyTitle}" />
											 	<dsp:param name="strategyContextTitle" value="${strategyContextTitle}" />
											 	<dsp:param name="strategyContextValue" value="${strategyContextValue}" />
											 	<dsp:param name="strategyContextCode"  value="${strategyContextCode}" />
											 	<dsp:param name="strategyType" value="${strategyType}" />
											 	<dsp:param name="strategyId" value="${strategyId}" /> 
											</dsp:include>
											</c:if>	
							</dsp:oparam>
					</dsp:droplet>
				 </dsp:oparam>
				</dsp:droplet>
			</div>
			<c:import url="../_includes/modules/change_store_form.jsp" />
			<c:import url="../selfservice/find_in_store.jsp" />   
	</jsp:body>

		<jsp:attribute name="footerContent">
    </jsp:attribute>
	</bbb:pageContainer>
	<c:if test="${TellApartOn}">
		<bbb:tellApart actionType="pv" />
	</c:if>
 <script type="text/javascript"> 
	 if (typeof s !== 'undefined') { 
	 s.pageName = 'My Account>Personal Page'; 
	 s.channel = 'My Account'; 
	 s.prop1='My Account'; 
	 s.prop2='My Account'; 
	 s.prop3='My Account'; 
	 s.eVar1='${strategyNameMap}(Personal Store Page)';  
	 var s_code = s.t(); 
	 if (s_code)
	 document.write(s_code); 
	 }
	 </script>
</dsp:page>