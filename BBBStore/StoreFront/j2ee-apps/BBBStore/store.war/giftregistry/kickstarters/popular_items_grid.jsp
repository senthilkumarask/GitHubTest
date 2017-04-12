<dsp:page>
<dsp:importbean bean="/com/bbb/kickstarters/droplet/PopularItemsDetailsDroplet" />
<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="isTransient" bean="/atg/userprofiling/Profile.transient"/>
<dsp:getvalueof var="omniDesc" param="omniDesc"/>
<c:set var="scene7Path" scope="request">
		<bbbc:config key="scene7_url" configName="ThirdPartyURLs" />
</c:set>
<c:set var="kickStartersCacheTimeout"><bbbc:config key="kickStartersCacheTimeout" configName="HTMLCacheKeys" /></c:set>
<dsp:droplet name="/atg/dynamo/droplet/Cache">
    <dsp:param name="key" value="PopularItems_${currentSiteId}_${param.eventType}" />
    <dsp:param name="cacheCheckSeconds" value="${kickStartersCacheTimeout}"/>
    <dsp:oparam name="output">
   <dsp:droplet name="PopularItemsDetailsDroplet">
	<dsp:param name="registryType" value="${param.eventType}" />
	<dsp:param name="siteId" value="${currentSiteId}" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="productList" param="popularItems"/>	
	</dsp:oparam>
</dsp:droplet>
</dsp:oparam>
</dsp:droplet>

	
<div id="kickStarterPopularItems" class="grid_12">
    <c:if test="${not empty productList}">
	<h2><bbbl:label key="lbl_kickstarters_popular_items_header" language="${pageContext.request.locale.language}" /></h2>
	</c:if>

	<dsp:include page="/browse/certona_prod_carousel.jsp">
	 <dsp:param name="productsVOsList" value="${productList}"/>								  	
	 <dsp:param name="desc" value="${omniDesc}"/>
	 <dsp:param name="crossSellFlag" value="true"/> 
	 <dsp:param name="kickstarterItems" value="true"/>
 	</dsp:include>
</div>

</dsp:page>