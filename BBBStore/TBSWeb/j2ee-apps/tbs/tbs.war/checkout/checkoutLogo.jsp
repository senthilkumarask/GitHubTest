<dsp:page>
    <div class="marLeft_10 fl">
        <c:choose>
            <c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
                <c:set var="altText" value="Bed Bath & Beyond"/>
                <c:set var="logoTheme" value="ca_149x39"/>
            </c:when>
            <c:when test="${currentSiteId eq 'TBS_BuyBuyBaby'}">
                <c:set var="altText" value="Buy Buy Baby"/>
                <c:set var="logoTheme" value="bb"/>
            </c:when>
            <c:otherwise>
                <c:set var="altText" value="Bed Bath & Beyond"/>
                <c:set var="logoTheme" value="by"/>
            </c:otherwise>
        </c:choose>
        <h1 class="noMar padTop_5">
        	<a href="/tbs" title="${altText}">
        		<img src="${imagePath}/_assets/global/images/logo/logo_bbb_${logoTheme}.png" alt="${altText}" />
        	</a>
        	
        	<!-- Baby Canada -->
        	<c:if test="${not empty TBS_BedBathCanadaSite and currentSiteId == TBS_BedBathCanadaSite}">
	        	<a href="https://<bbbc:config key="BabyCanada_Source_URL" configName="ReferralControls"/>" title="Buy Buy Baby" class="padLeft_15">
	            	<img src="${imagePath}/_assets/global/images/logo/logo_baby_ca_84x37.png" alt="Buy Buy Baby" />
	            </a>
	        </c:if>
	        <!-- Baby Canada -->
        </h1>
    </div>
</dsp:page>
