<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<dsp:importbean	bean="/com/bbb/profile/session/SessionBean" />
<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="BuyBuyBabySite">
		<bbbc:config key="BuyBuyBabySiteCode" configName="ContentCatalogKeys" />
	</c:set>

	<c:set var="BedBathUSSite">
		<bbbc:config key="BedBathUSSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof var="sessionBabyCA" bean="SessionBean.babyCA"/> 
	<dsp:getvalueof param="registrySummaryVO.registryId" var="ActRegistryId" />

<c:choose>
<c:when test="${(currentSiteId eq 'BedBathCanada') && (babyCAMode == 'true')}">
   <bbbt:textArea key="txt_bca_regflyout_registryToolsLinks" language="${pageContext.request.locale.language}" />
</c:when>
<c:otherwise>
   <bbbt:textArea key="txt_regflyout_registryToolsLinks" language="${pageContext.request.locale.language}" />
</c:otherwise>
</c:choose>



