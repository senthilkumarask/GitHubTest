<dsp:page>
    <div class="marLeft_10 fl" itemscope itemtype="http://schema.org/Organization">
        <c:choose>
            <c:when test="${currentSiteId eq 'BedBathCanada'}">
                <c:set var="altText" value="Bed Bath & Beyond"/>
                <c:set var="logoTheme" value="ca_149x39"/>
            </c:when>
            <c:when test="${currentSiteId eq 'BuyBuyBaby'}">
                <c:set var="altText" value="Buy Buy Baby"/>
                <c:set var="logoTheme" value="bb"/>
            </c:when>
            <c:otherwise>
                <c:set var="altText" value="Bed Bath & Beyond"/>
                <c:set var="logoTheme" value="by"/>
            </c:otherwise>
        </c:choose>
        <h1 class="noMar padTop_5">
            <a itemprop="url" href="/" title="${altText}">
                <img itemprop="logo" src="${imagePath}/_assets/global/images/logo/logo_bbb_${logoTheme}.png" alt="${altText}" />
            </a>
            
        	<%-- Baby Canada --%>
        	<c:if test="${not empty BedBathCanadaSite and currentSiteId == BedBathCanadaSite}">
	        	<a href="https://<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="padLeft_15">
	            	<img src="${imagePath}/_assets/global/images/logo/logo_baby_ca_84x37.png" alt="Buy Buy Baby" />
	            </a>
	        </c:if>
	        <%-- Baby Canada --%>
        </h1>
    </div>
</dsp:page>
