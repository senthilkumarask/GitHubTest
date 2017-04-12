<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="TBS_BuyBuyBabySite">
		<bbbc:config key="TBS_BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<c:set var="TBS_BedBathUSSite">
		<bbbc:config key="TBS_BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/> 
	

<c:choose>
<c:when test="${(currentSiteId eq 'TBS_BedBathCanada') && (babyCAMode == 'true')}">
   <bbbt:textArea key="txt_bca_regflyout_registryToolsLinks" language="${pageContext.request.locale.language}" />
</c:when>
<c:otherwise>
   <bbbt:textArea key="txt_regflyout_registryToolsLinks" language="${pageContext.request.locale.language}" />
</c:otherwise>
</c:choose>



