<dsp:page>
    <dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
    <dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
    <c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>
	<div class="pink-arrow"></div>
    <div class="loggedInNone">
        <div id="findARegistryInfoSelect" class="grid_3 alpha">
            <h2><bbbt:textArea key="txt_regflyout_createaregistry" language ="${pageContext.request.locale.language}"/></h2>
            <p><strong><bbbl:label key="lbl_regflyout_registryyet" language ="${pageContext.request.locale.language}"/></strong></p>
            <p><bbbl:label key="lbl_regflyout_positivelypainless" language ="${pageContext.request.locale.language}"/></p>
            <div id="findARegistrySelectType">
                <dsp:include page= "registry_type_select.jsp"/>
            </div>
            <div class="clear"></div>
        </div>
        <div class="grid_3">
            <div id="babyGiftRegistryItems"></div>
        </div>
        <c:choose>
			<c:when test="${enableRegSearchById == 'true'}">
				 <div id="findARegistryInfo" class="grid_3 omega babyWithRegistryIdFlyOut">
			</c:when>
			<c:otherwise>
				 <div id="findARegistryInfo" class="grid_3 omega">
			</c:otherwise>
		</c:choose>	
        
            <div id="findARegistryFormTitle">
                <h2><bbbt:textArea key="txt_regflyout_giveagift" language ="${pageContext.request.locale.language}"/></h2>
                <p><bbbl:label key="lbl_regflyout_information" language ="${pageContext.request.locale.language}"/></p>
            </div>
            <div id="findARegistryFormForm" role="application">
                <c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="0" scope="request"/>
				<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
					<dsp:param name="findRegistryFormId" value="babyFlyOut" />
					<dsp:param name="submitText" value="${findButton}" />
					<dsp:param name="handlerMethod" value="registrySearchFromFlyout" />
					<dsp:param name="bridalException" value="false" />
					<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
					<dsp:param name="flyout" value="true" />
				</dsp:include>
                <div class="clear"></div>
            </div>
        </div>
        <div class="clear"></div>
      <dsp:include page="find_registry_links.jsp"></dsp:include>
        <div class="clear"></div>
        <div id="findARegistryBottomBorder"></div>
    </div>
</dsp:page>