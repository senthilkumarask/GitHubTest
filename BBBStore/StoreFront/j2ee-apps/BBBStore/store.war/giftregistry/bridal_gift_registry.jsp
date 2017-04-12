<dsp:page>
<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/multisite/Site"/>
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>
<c:set var="enableRegSearchById" scope="request"><bbbc:config key="enableRegSearchById" configName="FlagDrivenFunctions" /></c:set>
<c:set var="BedBathCanadaSite">
		<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	 <c:set var="LabelRegFlyOutGiveGift">
			<bbbl:label key='lbl_regflyout_giveagift' language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="LabelRegFlyOutAlreadyRegistered">
			<bbbl:label key='lbl_regflyout_alreadyregistered' language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<div class="purple-arrow"></div>
<div class="notLoggedIn">
	<div id="findARegistryInfo" class="grid_3 alpha omega">
	    <p><bbbt:textArea key="txt_regflyout_registrants" language ="${pageContext.request.locale.language}"/></p>
	    <h2><bbbl:label key="lbl_regflyout_createregistry" language ="${pageContext.request.locale.language}"/></h2>
	    <div id="findARegistrySelectType">
	    	<dsp:include page= "registry_type_select.jsp"/>
	    </div>
	    <div class="clear"></div>
	    <c:if test="${(not empty LabelRegFlyOutAlreadyRegistered)}">
        <h2><bbbl:label key="lbl_regflyout_alreadyregistered" language ="${pageContext.request.locale.language}"/></h2>
        </c:if>
	    <p>
	        <a href="${contextPath}/giftregistry/my_registries.jsp"><bbbl:label key="lbl_regflyout_logintoview" language ="${pageContext.request.locale.language}"/></a>
	    </p>
	</div>
	 <c:choose>
	<c:when test="${enableRegSearchById == 'true'}">
		 <div id="findARegistryForm" class="grid_6 alpha omega enableRegSearchById">
	</c:when>
	<c:otherwise>
		 <div id="findARegistryForm" class="grid_6 alpha omega ">
	</c:otherwise>
</c:choose>	
	
	
	    <div id="findARegistryFormTitle">
	        <p><bbbt:textArea key="txt_regflyout_forguests" language ="${pageContext.request.locale.language}"/></p>
	        <c:if test="${(not empty LabelRegFlyOutGiveGift)}">
            <h2><bbbl:label key="lbl_regflyout_giveagift" language ="${pageContext.request.locale.language}"/></h2>
            </c:if>
	    </div>
	    <div id="findARegistryFormForm">
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
		            <%-- <bbbt:textArea key="txt_regflyout_enterinfo" language ="${pageContext.request.locale.language}"/>--%>
		        </div>
	    </div>
	</div>
	<div class="clear"></div>
	<dsp:include page="find_registry_links.jsp"></dsp:include>
</div>
</dsp:page>