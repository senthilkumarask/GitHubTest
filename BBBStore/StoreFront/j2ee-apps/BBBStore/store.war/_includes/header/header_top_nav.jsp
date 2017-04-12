<dsp:page>
<dsp:importbean bean="/com/bbb/search/droplet/SearchDroplet" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:importbean bean="/com/bbb/profile/session/SessionBean" />
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA" />
<dsp:getvalueof id="isStagingServer" bean="/com/bbb/search/endeca/EndecaSearch.stagingServer"/>
<c:set var="flyoutCacheTimeout">
		<bbbc:config key="FlyoutCacheTimeout" configName="HTMLCacheKeys" />
</c:set>

<!-- Below site id get is added for story BBBI-3818. This parameter will be rendered when this 
	jsp gets invoked by InvalidateDropletCacheScheduler.  -->
	
 <dsp:getvalueof id="siteId" param="siteId"></dsp:getvalueof>
<c:if test="${not empty siteId}">
	<dsp:getvalueof id="currentSiteId" param="siteId"/>
</c:if>
								
<c:set var="babyCAVar" value=""/>
<c:if test="${sessionBabyCA eq 'true' && currentSiteId == BedBathCanadaSite}">
    	<c:set var="babyCAVar" value="_Baby_CA"/>
</c:if>
<%-- If staging server do not cache the jsp. --%>
	 <c:choose>
	  <c:when test="${isStagingServer}">
			<dsp:include page="navigation_content.jsp">
	          <dsp:param name="currentSiteId" value="${currentSiteId}"/>
	       </dsp:include>
	  </c:when>
	  <c:otherwise>
	   <dsp:droplet name="/atg/dynamo/droplet/Cache">
		 <dsp:param name="key" value="CacheNavigationContent_${currentSiteId}${babyCAVar}" />
		  <dsp:param name="cacheCheckSeconds" value="${flyoutCacheTimeout}"/>
		  <dsp:oparam name="output">
		   <dsp:include page="navigation_content.jsp">
	          <dsp:param name="currentSiteId" value="${currentSiteId}"/>
	       </dsp:include>
		 </dsp:oparam>
	   </dsp:droplet>
	  </c:otherwise>
	 </c:choose>

</dsp:page>
