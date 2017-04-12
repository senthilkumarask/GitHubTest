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
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/RedirectDroplet"/>
	<dsp:importbean bean="/com/bbb/framework/security/ValidateParametersDroplet" />

	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="stratgiesIndex" param="stratgiesIndex" />
	<dsp:getvalueof var="totalStratgies" param="totalStratgies" />
	<dsp:getvalueof var="BazaarVoiceOn" param="BazaarVoiceOn" />
	<dsp:getvalueof var="pageIdCertona" param="pageIdCertona" />
	<c:set var="viewAll" scope="request">false</c:set>
	
	<dsp:droplet name="ValidateParametersDroplet">
		<dsp:param value="PSPCertona;PSPCertona;PSPCertona" name="paramArray" />
		<dsp:param value="${stratgiesIndex};${BazaarVoiceOn};${pageIdCertona}" name="paramsValuesArray" />
		<dsp:oparam name="error">
		<dsp:droplet name="RedirectDroplet">
			<dsp:param name="url" value="/404.jsp" />
		</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
	<c:set var="viewAll" scope="request">
		<bbbc:config key="psViewAllOn" configName="ContentCatalogKeys" />
	</c:set>  
		
    <c:set var="howMany" value="${(totalStratgies-stratgiesIndex)+1}"/>
	
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
	
	<%-- Droplet to get the personal store details --%>
	  <dsp:droplet name="PersonalStoreDroplet">
			<dsp:param name="siteId" value="${currentSiteId}" />
			<dsp:param name="userProfile" bean="Profile" />
			<dsp:oparam name="output">
					<%-- Get the map of Strategy-JSP mapping --%>
					<dsp:getvalueof var="psStrategyJSPMap" varType=" java.util.map"
							param="personalStoreDetails.strategyJSPMap" />
					<dsp:getvalueof var="personalStoreDetails"
					param="personalStoreDetails" />
					<dsp:getvalueof var="psStrategyDetails"
							param="personalStoreDetails.strategyDetails" />
					
					<%-- Get the map of Strategy-ContextValue mapping --%>
					<dsp:getvalueof var="psStrategyContextMap" varType=" java.util.map"
							param="personalStoreDetails.strategyContextMap" />
					
					<%-- Get the map of Strategy-Targeter mapping --%>
					<dsp:getvalueof var="psStrategyTargeterMap"
							bean="com/bbb/personalstore/tools/PersonalStoreTools.personalStoreTargetMapping"
							varType="java.util.map" />
	
		  			
		  			<dsp:droplet name="/atg/dynamo/droplet/Range">
						<dsp:param value="${psStrategyDetails}" name="array" />
						<dsp:param name="howMany" value="${howMany}"/>
						<dsp:param name="start" value="${stratgiesIndex}"/>
						<dsp:oparam name="output">
						
						<dsp:getvalueof var="strategyTitle" param="element.strategyPageTitle" />
						<dsp:getvalueof var="strategyContextTitle" param="element.strategyContextTitle" />
						<dsp:getvalueof var="strategyId" param="element.id" />
						<dsp:getvalueof var="strategyName" param="element.strategyName" />
						<dsp:getvalueof var="strategyType" param="element.strategyType" />
						<dsp:getvalueof var="numOfProducts" param="element.strategyLayout.layoutNumProducts" />
						
						<%-- Strategy Code to get the Targeter value from Strategy-Targeter mapping --%>
						<dsp:getvalueof var="strategyCode" value="${strategyName}" />
						
						<%-- Calling the targeters to get the recommendations --%>
						<dsp:droplet name="TargetingArray">
						    <dsp:param bean="${psStrategyTargeterMap[strategyName]}"
										name="targeter" />
							<dsp:param name="userid" value="${userId}" />
							<dsp:param name="exitemid" value="${productList}" />
							<dsp:param name="siteId" value="${appid}" />
							<dsp:param name="number" value="${numOfProducts}" />
							<dsp:param name="sourceId" value="${strategyName}" />
							<dsp:param name="pageid" value="${pageIdCertona}" />
							<dsp:param name="linksCertona" value="${linksCertona}" />
							<%-- Pass the context in targeter only if type is recommendation --%>
							<c:if test="${strategyType == 'Recommendation'}">
								<dsp:param name="context" value="${psStrategyContextMap[strategyId]}" />
							</c:if>
							<dsp:oparam name="output">
							  	<dsp:getvalueof var="productArray" param="elements" />
							  	<dsp:getvalueof var="pageIdCertona" param="pageid" />
							  	<dsp:getvalueof var="linksCertona" param="linksCertona"/>
							  	<%-- Include the JSP from strategy-jsp map --%>
							<dsp:getvalueof var="strategyNameMapping" param="strategyNameMap" /> 		  
							<dsp:include page="${psStrategyJSPMap[strategyId]}">
								 	<dsp:param name="productsVOsList" value="${productArray}" />
								 	<dsp:param name="strategyTitle" value="${strategyTitle}" />
								 	<dsp:param name="strategyContextTitle" value="${strategyContextTitle}" />
								 	<dsp:param name="strategyContextValue" value="${psStrategyContextMap[strategyId]}" />
								 	<dsp:param name="strategyType" value="${strategyType}" />
								 	<dsp:param name="strategyId" value="${strategyId}" />
								 	<dsp:param name="viewAll" value="${viewAll}" />
								 	<dsp:param name="BazaarVoiceOn" value="${BazaarVoiceOn}" />
								 	<dsp:param name="strategyNameFromMap" value="${strategyNameMapping[strategyName]}" />
								 </dsp:include>
							</dsp:oparam>
					    </dsp:droplet>
					    
					  
					    </dsp:oparam>
					</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>	
		
	<%--Certona Tagging changes start --%>
        <script type="text/javascript">
        	certonaVariable.links = '${linksCertona}'
        </script>
	  <%--Certona Tagging changes end --%>
</dsp:page>