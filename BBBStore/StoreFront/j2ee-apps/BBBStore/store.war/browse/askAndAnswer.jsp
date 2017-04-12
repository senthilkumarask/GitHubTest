<dsp:page>
	<c:if test="${fn:indexOf(pageWrapper, 'useBazaarVoice') > -1 && bExternalJSCSS && bPlugins && BazaarVoiceOn}">
    <c:choose>
        <c:when test="${currentSiteId eq BedBathUSSite}">
             <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_qa_us' configName='ThirdPartyURLs' />" />
        </c:when>
        <c:when test="${currentSiteId eq BuyBuyBabySite}">
             <link rel="stylesheet" type="text/css" href="<bbbc:config key='css_BazaarVoice_qa_baby' configName='ThirdPartyURLs' />" />
        </c:when>
        
    </c:choose>  
</c:if>

<div id="BVQASummaryContainer"></div>
	
	
</dsp:page>	