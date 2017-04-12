
<%@ page import="com.bbb.constants.BBBCoreConstants" %>
<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/> 
<c:set var="BuyBuyBabySite">
	<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
</c:set>

<bbbt:textArea key="txt_registry_pagehead_one" language="${pageContext.request.locale.language}"></bbbt:textArea>

	<c:choose>
		 <c:when test="${siteId eq BuyBuyBabySite}">
	         <dsp:include page="baby_sub_header.jsp"/>
	    </c:when>
	    <c:otherwise>
	         <dsp:include page="baby_sub_header_us_canada.jsp"/>
	    </c:otherwise>
    </c:choose>
 <bbbt:textArea key="txt_registry_pagehead_two" language="${pageContext.request.locale.language}"></bbbt:textArea>		    
</dsp:page>