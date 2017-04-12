<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<dsp:importbean bean="/atg/multisite/Site"/>
<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
<div class="loggedInNone">
    <div id="findARegistryInfo" class="grid_3 alpha omega">
        <p><bbbt:textArea key="txt_regflyout_registrants" language ="${pageContext.request.locale.language}"/></p>
        <h2><bbbl:label key="lbl_regflyout_createregistry" language ="${pageContext.request.locale.language}"/></h2>
        <p><bbbl:label key="lbl_regflyout_Vivamus" language ="${pageContext.request.locale.language}"/></p>
        <div id="findARegistrySelectType">
            <dsp:include page= "registry_type_select.jsp"/>
        </div>
    </div>
    <div id="findARegistryForm" class="grid_6 alpha omega">
        <div id="findARegistryFormTitle">
            <p><bbbt:textArea key="txt_regflyout_forguests" language ="${pageContext.request.locale.language}"/></p>
            <h2><bbbl:label key="lbl_regflyout_giveagift" language ="${pageContext.request.locale.language}"/></h2>
        </div>
        <div id="findARegistryFormForm" role="application">
            <c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="0" scope="request"/>
				<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
					<dsp:param name="findRegistryFormId" value="bridalFlyOut" />
					<dsp:param name="submitText" value="${findButton}" />
					<dsp:param name="handlerMethod" value="registrySearchFromFlyout" />
					<dsp:param name="bridalException" value="false" />
					<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
					<dsp:param name="flyout" value="true" />
				</dsp:include>
                <div class="clear"></div>
                <div id="findARegistryFormMessage">
                    <%-- <bbbl:label key="lbl_regflyout_information" language ="${pageContext.request.locale.language}"/>--%>
                </div>
            
        </div>
    </div>
    <div class="clear"></div>
   <dsp:include page="find_registry_links.jsp"></dsp:include></div>
</dsp:page>