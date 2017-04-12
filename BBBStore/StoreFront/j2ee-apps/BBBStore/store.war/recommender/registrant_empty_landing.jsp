<dsp:page>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<dsp:importbean bean="/com/bbb/cms/droplet/RecommendationLandingPageTemplateDroplet"/>
<dsp:importbean bean="/com/bbb/simplifyRegistry/droplet/RegistryStatusDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>

<dsp:getvalueof var="registryId" param="registryId"/>
<dsp:getvalueof var="registryType" param="registryType"/>
<!-- BPS-1112 Surge -->
<dsp:getvalueof var="regFirstName" param="regFirstName"/>
<dsp:getvalueof var="regEventDate" param="regEventDate"/>
<!-- BPS-1112 Surge -->

		<dsp:droplet name="RecommendationLandingPageTemplateDroplet">   
				<dsp:param name="siteId" param="siteId" />
				<dsp:param name="channel" param="channel" />				
				<dsp:param name="registryURL" value='/store/_includes/modals/inviteFriendsRegistry.jsp?eventType=${registryType}&registryId=${registryId}&regFirstName=${regFirstName}&emptyRegistrant=0&regEventDate=${regEventDate}' />
                <dsp:param name="registryType" param="registryType" />
                <dsp:param name="registryId" param="registryId" />
                
                
                <dsp:oparam name="output">
              <dsp:getvalueof var="registryURL" param="registryURL" />


 		<dsp:droplet name="RegistryStatusDroplet">
				<dsp:param value="${registryId}" name="registryId" />
				<dsp:oparam name="output">
					<dsp:getvalueof var="regPublic" param="regPublic"/>
				</dsp:oparam>
		</dsp:droplet>			  
<!-- <map name="recommendThings"><area alt="Recommend Things for your friends's registry" href="#" title="Recommend Things for your friends's registry" coords="1,0,998,579" shape="rect">
	</map> -->
<div id="recommendationsSection" class="tab_wrapper">
  <c:if test="${!regPublic}">
   <div class="alert alert-alert recommendation clearfix">
   		<bbbl:label key='lbl_registry_share_modal_private_alert' language="${pageContext.request.locale.language}" />
   </div>
   <div class="overlay recommendation"></div>
   </c:if>
    <div class="kickstarterSectionHeader grid_12 pending_recommendation_header registry_commendation_header">
         <div class="grid_5 alpha">
            <bbbt:textArea key="txt_registry_landing_recommendations" language="${pageContext.request.locale.language}"></bbbt:textArea>
         </div>
         <div class="grid_7 omega recommendationDesc">
              <bbbt:textArea key="txt_recommendations_description" language ="${pageContext.request.locale.language}"/>
         </div>
    </div>

		<dsp:param name="promoBox" param="RecommendationLandingPageTemplateVO.promoBox"/>
			<dsp:getvalueof var="promoBoxContent" param="promoBox.promoBoxContent" />
			<dsp:getvalueof var="altText" param="promoBox.imageAltText" />
			<dsp:getvalueof var="imageLink" param="promoBox.imageLink" />
			<dsp:getvalueof var="imageUrl" param="promoBox.imageUrl" />
			<dsp:getvalueof var="imageMapName" param="promoBox.imageMapName" />
			<dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="promoBox.imageMapContent"/>								
		<c:choose>		
			<c:when test="${not empty imageMapContent}">															  
				<%-- Display Image Map --%>				
					<a NAME="${imageMapName}">${imageMapContent}</a>		
			</c:when>
			<c:otherwise>			
				<dsp:getvalueof var="altText" param="promoBox.imageAltText" />
				<dsp:getvalueof var="imageLink" param="promoBox.imageLink" />
				<dsp:getvalueof var="imageUrl" param="promoBox.imageUrl" />
				<a title="${altText}" href="${imageLink}">
					<img alt="${altText}" src="${imageUrl}"/>
				</a>																			
			</c:otherwise>
		</c:choose>
	

<div class="grid_12 alpha contentWrapper">

		<dsp:droplet name="ForEach">
			<dsp:param name="array"  param="RecommendationLandingPageTemplateVO.promoBoxList"/>			
			
			<dsp:oparam name="output">			
			<dsp:getvalueof var="promoBoxContent" param="element.promoBoxContent" />
			<dsp:getvalueof var="altText" param="element.imageAltText" />
			<dsp:getvalueof var="imageLink" param="element.imageLink" />
			<dsp:getvalueof var="imageUrl" param="element.imageUrl" />
			<dsp:getvalueof var="imageMapName" param="element.imageMapName" />
			<dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="element.imageMapContent"/>								
		<c:choose>			
			<c:when test="${not empty imageMapContent}">															  
				<%-- Display Image Map --%>				
					<a NAME="${imageMapName}">${imageMapContent}</a>			
			</c:when>
			<c:otherwise>			
				<dsp:getvalueof var="altText" param="element.imageAltText" />
				<dsp:getvalueof var="imageLink" param="element.imageLink" />
				<dsp:getvalueof var="imageUrl" param="element.imageUrl" />
				<a title="${altText}" href="${imageLink}">
					<img alt="${altText}" src="${imageUrl}"/>
				</a>																			
			</c:otherwise>
		</c:choose>
	
		</dsp:oparam>	
		</dsp:droplet>
		
</div>
</div>
		<dsp:param name="promoBoxBottom" param="RecommendationLandingPageTemplateVO.promoBoxBottom"/>
			<dsp:getvalueof var="promoBoxContent" param="promoBoxBottom.promoBoxContent" />
			<dsp:getvalueof var="altText" param="promoBoxBottom.imageAltText" />
			<dsp:getvalueof var="imageLink" param="promoBoxBottom.imageLink" />
			<dsp:getvalueof var="imageUrl" param="promoBoxBottom.imageUrl" />
			<dsp:getvalueof var="imageMapName" param="promoBoxBottom.imageMapName" />
			<dsp:getvalueof var="imageMapContent" vartype="java.lang.String" param="promoBoxBottom.imageMapContent"/>								
		<c:choose>			
			<c:when test="${not empty imageMapContent}">															  
				<%-- Display Image Map --%>				
					<MAP NAME="${imageMapName}">${imageMapContent}</MAP>			
			</c:when>
			<c:otherwise>			
				<dsp:getvalueof var="altText" param="promoBoxBottom.imageAltText" />
				<dsp:getvalueof var="imageLink" param="promoBoxBottom.imageLink" />
				<dsp:getvalueof var="imageUrl" param="promoBoxBottom.imageUrl" />
				<a title="${altText}" href="${imageLink}">
					<img alt="${altText}" src="${imageUrl}"/>
				</a>																			
			</c:otherwise>
		</c:choose>	
	</dsp:oparam>
</dsp:droplet>
	</dsp:page>