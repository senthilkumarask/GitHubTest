<dsp:page>
<dsp:importbean	bean="/com/bbb/commerce/browse/droplet/GetChildAndSiblingsProductsDroplet" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
<dsp:importbean bean="/com/bbb/certona/CertonaConfig" />
<dsp:importbean bean="/atg/multisite/Site" />
<dsp:getvalueof var="productId" param="prodId" />
<dsp:getvalueof var="categoryId" param="categoryId" />
<dsp:getvalueof var="type" param="type" />
<dsp:getvalueof var="registryId" param="registryId" />
<dsp:getvalueof var="registryName" param="registryName" />
<dsp:importbean bean="/com/bbb/commerce/browse/droplet/GetRelatedCategoriesDroplet" />
<c:set var="enableProductCrosuselATCATRModal" scope="request"><bbbc:config key="enableProductCrosuselATCATRModal" configName="FlagDrivenFunctions" /></c:set>
<c:set var="enableCategorySectionATCATRModal" scope="request"><bbbc:config key="enableCategorySectionATCATRModal" configName="FlagDrivenFunctions" /></c:set>
<c:set var="enableCertonaContainerATCATRModal" scope="request"><bbbc:config key="enableCertonaContainerATCATRModal" configName="FlagDrivenFunctions" /></c:set>
<c:set var="div_class" value="certona_like"></c:set>
<c:set var="showCatRecommendations" value="false"></c:set>
<c:if test="${enableCategorySectionATCATRModal}">
	<dsp:droplet name="GetRelatedCategoriesDroplet">
		<dsp:param name="productId" value="${productId}" />
		<dsp:param name="categoryId" value="${categoryId}" />
		<dsp:oparam name="output">
		<dsp:getvalueof var="recommCatVO" param="recommCatVO" />
		<dsp:getvalueof var="showRecommendations" param="showRecommendations" />
		</dsp:oparam>
	</dsp:droplet>
</c:if>

<dsp:getvalueof var="appid" bean="Site.id" />
<dsp:getvalueof id="appIdCertona" bean="CertonaConfig.siteIdAppIdMap.${appid}" />
<dsp:droplet name="Switch">
<dsp:param name="value" bean="Profile.transient" />
	<dsp:oparam name="false">
		<dsp:getvalueof var="userId" bean="Profile.id" />
	</dsp:oparam>
	<dsp:oparam name="true">
		<dsp:getvalueof var="userId" value="" />
	</dsp:oparam>
</dsp:droplet>

<c:set var="fullWidthClass" value=""></c:set>
<c:if test="${showRecommendations}">
<c:set var="fullWidthClass" value="halfWidthCarousel"></c:set>
<c:set var="showCatRecommendations" value="true"></c:set>
</c:if>
<input type="hidden" name="showCatRecommendations" value="${showCatRecommendations}" />
<c:if test="${enableProductCrosuselATCATRModal}">
	<dsp:droplet name="GetChildAndSiblingsProductsDroplet">
		<dsp:param name="productId" value="${productId}" />
		<dsp:oparam name="output">
			<dsp:getvalueof var="siblingProductDetails"	param="siblingProductDetails" />
			<dsp:getvalueof var="isCollection" param="isCollection" />
			<dsp:getvalueof var="isAccessoryLead" param="isAccessoryLead" />
			<dsp:getvalueof var="siblingProductSize" param="siblingProductSize" />
		</dsp:oparam>
	</dsp:droplet>
	

	<c:if test="${not empty siblingProductDetails}">
	<input type="hidden" name="siblingProductSize" value="${siblingProductSize}" />
	<div class="certona_youmaylike ${fullWidthClass}">
		<c:choose>
		<c:when test="${isCollection}">
		<h3><bbbl:label key="lbl_collection_recomm" language="${pageContext.request.locale.language}" /></h3>
		</c:when>
		<c:when test="${isAccessoryLead}">
		<h3><bbbl:label key="lbl_acc_lead_recomm" language="${pageContext.request.locale.language}" /></h3>
		</c:when>
		<c:otherwise>
		<h3><bbbl:label key="lbl_acc_sibling_recomm" language="${pageContext.request.locale.language}" /></h3>
		</c:otherwise>
		</c:choose>
		<div class="container_12 clearfix modalCertona">
			<dsp:form name="certonaProdForm" method="post" id="certonaProdForm">
			<div class="categoryProductTabs">
				<div id="pdp_cav" class="${div_class}">
					<div id="botCrossSell-tabs1" class="categoryProductTabsData noBorder">
						<dsp:include page="recommended_products.jsp">
							<dsp:param name="productsVOsList" value="${siblingProductDetails}" />
							<dsp:param name="crossSellFlag" value="true" />
							<dsp:param name="desc" value="You may also like (desc)" />
							<dsp:param name="type" param="type" />
							<dsp:param name="registryId" param="registryId" />
							<dsp:param name="productVOSize" value="${siblingProductSize}" />
							<dsp:param name="showRecommendations" value="${showCatRecommendations}" />
							<dsp:param name="registryName" value="${registryName}" />
							<dsp:param name="isCollection" value="${isCollection}" />
						</dsp:include>
					</div>
				</div>
			</div>
			</dsp:form>
		</div>
  </div>

		
	</c:if>
</c:if>

<c:if test="${showRecommendations}">
		<dsp:include page="recommended_categories.jsp">
			<dsp:param name="recommCatVO" value="${recommCatVO}" />
			<dsp:param name="siblingProductDetails" value="${siblingProductDetails}" />
		</dsp:include>
	</c:if>
<c:if test="${(enableCertonaContainerATCATRModal && !enableCategorySectionATCATRModal && !enableProductCrosuselATCATRModal)||(enableCertonaContainerATCATRModal && empty siblingProductDetails && !showRecommendations)}">
		<div class="certona_youmaylike">
			<dsp:include page="certona_carousel_with_cta.jsp">
			<dsp:param name="type" value="${type}" />
			<dsp:param name="registryId" value="${registryId}" />
			<dsp:param name="productId" value="${productId}" />
			<dsp:param name="categoryId" value="${categoryId}" />
			<dsp:param name="showRecommendations" value="${showCatRecommendations}" />
			<dsp:param name="registryName" value="${registryName}" />
			</dsp:include>
		</div>
</c:if>

<c:choose>
<c:when test="${appIdCertona eq 'bedbathandbeyond01' || appIdCertona eq 'bedbathandbeyond03' || appIdCertona eq 'bedbathandbeyond05' || appIdCertona eq 'bedbathandbeyond06'}">
  <c:set var="onPageEvent" value="registrywedding_op"/>
</c:when>
<c:otherwise>
  <c:set var="onPageEvent" value="registrybaby_op"/>
</c:otherwise>
</c:choose>


  <script type="text/javascript"> 
	  var resx = new Object(); 
	  resx.appid = "${appIdCertona}"; 
	  resx.links = "${linksCertona}";
	  
	  <c:choose>
	   <c:when test="${type eq 'cart'}">
	     resx.event = "addtocart_op"; 
	   </c:when>
	   <c:when test="${type eq 'registry'}">
	     resx.event = "${onPageEvent}";
	   </c:when>
	  </c:choose>
	  
	  resx.itemid = "${productId}";
	  resx.rec = true; 
	  resx.customerid = "${userId}"; 
	  resx.pageid = ""; 
	
		if (typeof certonaResx === 'object') {
			certonaResx.run();
		} else if (typeof certonaResx === "undefined") {
			if (typeof BBB.loadCertonaJS === "function") {
				BBB.loadCertonaJS();
			}
		}
	</script> 
	
 </dsp:page>