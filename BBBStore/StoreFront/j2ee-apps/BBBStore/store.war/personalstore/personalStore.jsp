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
	<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
	<c:set var="scene7Path" scope="request"><bbbc:config key="scene7_url" configName="ThirdPartyURLs"/></c:set>
	<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductComparisonList"/>
	<dsp:importbean bean="/com/bbb/account/droplet/BBBSetCookieDroplet"/>
	<c:set var="personalStore">
	<bbbc:config key="PSP_homepageLink" configName="FlagDrivenFunctions" />
	</c:set> 
	<c:set var="pageIdCertona"></c:set>
	<c:set var="linksCertona"></c:set>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="stratgiesCount" param="stratgiesCount" />
	<dsp:getvalueof var="stratgiesStack" param="stratgiesStack" />
	
	<%--RM-28957 Adding logic for cookie generation for product comparison starts--%>
	<dsp:getvalueof var="contextRoot" bean="/OriginatingRequest.contextPath"/>
	<c:set var="lastSearchedUrl" value="${contextRoot}/personalstore/" />
	<jsp:useBean id="cookies" class="java.util.HashMap" scope="request"/>
	<c:set target="${cookies}" property="SAVEDURL">${lastSearchedUrl}</c:set>
	<dsp:setvalue bean="BBBSetCookieDroplet.cookies" value="${cookies}" />
	<dsp:droplet name="BBBSetCookieDroplet">
		<dsp:oparam name="output"></dsp:oparam>
	</dsp:droplet>
	<%--RM-28957 Adding logic for cookie generation for product comparison ends --%>

<%--Fetching appId for Certona Tagging --%>
	<dsp:droplet name="Switch">
	 <dsp:param name="value" bean="Site.id"/>
	 	<dsp:oparam name="BedBathUS">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BedBathUS" configName="CertonaKeys"/></c:set>
	 	</dsp:oparam>
	 	<dsp:oparam name="BedBathCanada">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BedBathCanada" configName="CertonaKeys"/></c:set> 
	 	</dsp:oparam>
	 	<dsp:oparam name="BuyBuyBaby">
	 		<c:set var="appIdCertona" scope="request"><bbbc:config key="BuyBuyBaby" configName="CertonaKeys"/></c:set>
	 	</dsp:oparam>
	 </dsp:droplet>
	<c:set var="numPageLoadStrategy" scope="request"><bbbc:config key="numPageLoadStrategy" configName="ContentCatalogKeys" /></c:set>
	<c:set var="stratgiesIndex" scope="request">${numPageLoadStrategy + 1}</c:set>

	<bbb:pageContainer index="false" follow="false">
		<jsp:attribute name="section">browse</jsp:attribute>
		<jsp:attribute name="pageWrapper">personalStorePage useCertonaJs useLiveClicker</jsp:attribute>
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
	<%-- Droplet to get the personal store details --%>
	<c:if test="${personalStore}"> 
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
					<dsp:getvalueof var="strategyNameMapping"
							param="strategyNameMap" />
					
					<%-- Get the map of Strategy-ContextValue mapping --%>
					<dsp:getvalueof var="psStrategyContextMap" varType=" java.util.map"
							param="personalStoreDetails.strategyContextMap" />
					
					<%-- Get the map of Strategy-Targeter mapping --%>
					<dsp:getvalueof var="psStrategyTargeterMap"
							bean="com/bbb/personalstore/tools/PersonalStoreTools.personalStoreTargetMapping"
							varType="java.util.map" />
					
					<div class="grid_12 pspHeader">
                                        <h1 class="txtOffScreen">OUR RECOMMENDATIONS</h1>
			  			<dsp:valueof param="personalStoreDetails.personalStoreTitle" valueishtml="true"></dsp:valueof>
					</div>
					
					
		  			<dsp:droplet name="/atg/dynamo/droplet/ForEach">
						<dsp:param value="${psStrategyDetails}" name="array" />
						<dsp:oparam name="output">
						<dsp:getvalueof var="count" param="count" />
						<dsp:getvalueof var="size" param="size" />
						
						<dsp:getvalueof var="strategyTitle" param="element.strategyPageTitle" />
						<dsp:getvalueof var="strategyContextTitle" param="element.strategyContextTitle" />
						<dsp:getvalueof var="strategyId" param="element.id" />
						<dsp:getvalueof var="strategyName" param="element.strategyName" />
						<dsp:getvalueof var="strategyType" param="element.strategyType" />
						<dsp:getvalueof var="numOfProducts" param="element.strategyLayout.layoutNumProducts" />
						<dsp:getvalueof var="strategyCode" value="${strategyType}" />
						<%-- Calling the targeters to get the recommendations --%>
						
						<c:choose>
					  	<c:when test="${count le numPageLoadStrategy}">
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
							  	<dsp:getvalueof var="pageIdCertona" param="pageid" />
							  	<dsp:getvalueof var="linksCertona" param="linksCertona"/>
							  	<dsp:getvalueof var="productArray" param="elements" />
							  	<dsp:getvalueof var="requestURL" param="requestURL" />
							  	<dsp:getvalueof var="responseXML" param="responseXML"/>
							  	<%-- Include the JSP from strategy-jsp map --%>
							  	<dsp:include page="${psStrategyJSPMap[strategyId]}">
								 	<dsp:param name="productsVOsList" value="${productArray}" />
								 	<dsp:param name="strategyTitle" value="${strategyTitle}" />
								 	<dsp:param name="strategyContextTitle" value="${strategyContextTitle}" />
								 	<dsp:param name="strategyContextValue" value="${psStrategyContextMap[strategyId]}" />
								 	<dsp:param name="strategyType" value="${strategyType}" />
								 	<dsp:param name="strategyId" value="${strategyId}" />
								 	<dsp:param name="strategyNameFromMap" value="${strategyNameMapping[strategyName]}" />
								 </dsp:include>
				
							</dsp:oparam>
							<dsp:oparam name="empty">
								<dsp:getvalueof var="requestURL" param="requestURL" />
								<dsp:getvalueof var="errorMsg" param="errorMessages" />
							</dsp:oparam>
						
					    </dsp:droplet>
				     </c:when>
				     
					 <c:when test="${count eq stratgiesIndex}">
					    	<div class="loadMoreSection grid_12 clearfix centerTxt" 
	                            data-ajax-url="${contextPath}/personalstore/personalStoreStratgies.jsp?stratgiesIndex=${stratgiesIndex}&BazaarVoiceOn=${BazaarVoiceOn}&totalStratgies=${size}&pageIdCertona=${pageIdCertona}">
	                             <img width="20" height="20" alt="small loader" src="/_assets/global/images/widgets/small_loader.gif" />
	                        </div>
					 </c:when>
					 
					 </c:choose>
					</dsp:oparam>
					</dsp:droplet>
			</dsp:oparam>
		</dsp:droplet>	
		</c:if>
		 </div>
		<c:import url="../_includes/modules/change_store_form.jsp" />
		<c:import url="../selfservice/find_in_store.jsp" />
		<%--BBBSL-6574 | Printing Certona Web Service Calls on view source --%>
			<c:choose>
	            <c:when test="${not empty errorMsg }">
	            			   <!--
					 <div id="certonaRequestResponse" class="hidden"> 
						<ul> 
							<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
							<li id="errorMsg"><dsp:valueof value="${errorMsg}" valueishtml="true" /></li>
						</ul> 
					</div>  
					 --> 
	            </c:when>
	            <c:otherwise>
	             <!--
						 <div id="certonaRequestResponse" class="hidden"> 
							<ul> 
								<li id="requestURL"><dsp:valueof value="${requestURL}" valueishtml="true" /></li>
								<li id="responseXML"><dsp:valueof value="${responseXML}" valueishtml="true"/></li>  
							</ul>
						</div>
						 -->
	            </c:otherwise>
	         </c:choose>

	</jsp:body>
	
	<jsp:attribute name="footerContent">
    </jsp:attribute>
	</bbb:pageContainer>
	<c:if test="${TellApartOn}">
		<bbb:tellApart actionType="pv" />
	</c:if>
	<%--Certona Tagging changes start --%>
	<script type="text/javascript">
	  	var certonaVariable = new Object();
        certonaVariable.appid = "${appIdCertona}";
       	certonaVariable.pageid = "${pageIdCertona}";
        certonaVariable.customerid ="${userId}";
        certonaVariable.links = '${linksCertona}';
	</script>
     <%--Certona Tagging changes end --%>	
     
     <%--RM-28957 Adding logic for cookie generation for product comparison starts--%>	
      <dsp:setvalue bean="ProductComparisonList.url" value="${lastSearchedUrl}"/>
      <%--RM-28957 Adding logic for cookie generation for product comparison ends--%>

	 <script type="text/javascript"> 
	 if (typeof s !== 'undefined') { 
	 s.pageName = 'My Account>Personal Page'; 
	 s.channel = 'My Account'; 
	 s.prop1='My Account'; 
	 s.prop2='My Account'; 
	 s.prop3='My Account'; 
	 s.eVar1='Personal Store Page';  
	 var s_code = s.t(); 
	 if (s_code)
	 document.write(s_code); 
	 }
	 </script>

</dsp:page>
