<dsp:page>
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" scope="session"/>
<dsp:getvalueof id="cssFile" bean="Site.cssFile"/>
<dsp:getvalueof id="siteURL" bean="Site.productionURL"/>
<dsp:getvalueof var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<dsp:getvalueof var="contentKey" param="contentKey"/>

<dsp:droplet name="/com/bbb/commerce/browse/droplet/ConfigURLDroplet">
	<dsp:param value="ThirdPartyURLs" name="configType" />
	<dsp:oparam name="output">
		<dsp:getvalueof var="imagePath" param="imagePath" scope="request"/>
		<dsp:getvalueof var="cssPath" param="cssPath" scope="request"/>
		<dsp:getvalueof var="jsPath" param="jsPath" scope="request"/>
		<dsp:getvalueof var="scene7Path" param="scene7Path" scope="request"/>
	</dsp:oparam>
</dsp:droplet>
<c:choose>	
    <c:when test="${currentSiteId == 'TBS_BedBathUS'}">
        <c:set var="themeFolder" value="${cssFile}" scope="session" />
        <c:set var="themeName" value="by" scope="session" />
    </c:when>
     <c:when test="${currentSiteId == 'TBS_BedBathCanada'}">
        <c:set var="themeFolder" value="${cssFile}" scope="session" />
        <c:set var="themeName" value="by" scope="session" />
    </c:when>
    <c:when test="${currentSiteId == 'TBS_BuyBuyBaby'}">
        <c:set var="themeFolder" value="${cssFile}" scope="session" />
        <c:set var="themeName" value="bb" scope="session" />
    </c:when>
    <c:otherwise>
        <c:set var="themeFolder" value="bbbaby" scope="session" />
        <c:set var="themeName" value="bb" scope="session" />
    </c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${pageVariation == 'br'}">
        <c:set var="themeFolder" value="bbregistry" scope="session" />
        <c:set var="themeName" value="br" scope="session" />
    </c:when>
    <c:when test="${pageVariation == 'bc'}">
        <c:set var="themeFolder" value="bbcollege" scope="session" />
        <c:set var="themeName" value="bc" scope="session" />
    </c:when>
</c:choose>
<dsp:include page="/_includes/third_party_on_of_tags.jsp"/>
</dsp:page>