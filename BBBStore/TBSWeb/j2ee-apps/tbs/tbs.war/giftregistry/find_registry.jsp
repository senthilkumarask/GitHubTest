<dsp:page>
<dsp:importbean
		bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
<dsp:importbean bean="/atg/multisite/Site"/>

<dsp:getvalueof var="siteId" bean="Site.id" />	
<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath"/>		

				<c:choose>
					<c:when test="${siteId == 'TBS_BuyBuyBaby'}">
<div class="small-12 columns logIn no-padding">
						<div class="guests small-12 columns" style="display:none;">
							<p><bbbl:label key='lbl_find_reg_for_guest' language ="${pageContext.request.locale.language}"/></p>						
							<h3><bbbl:label key='lbl_find_reg_find' language ="${pageContext.request.locale.language}"/></span>&nbsp;<bbbl:label key='lbl_find_reg_a_reg' language ="${pageContext.request.locale.language}"/></h3>
							<p class="registrantInfo"><bbbl:label key='lbl_find_reg_enter_gift' language ="${pageContext.request.locale.language}"/></p>
						</div>
						<c:set var="handlerMethodForSearch" value="registrySearchFromBabyLanding" scope="request" />
						<c:set var="largeClass" value="large-7 end"/>
					</c:when>
					<c:otherwise>
<div class="small-12 columns logIn no-padding">
						<div class="guests small-12 large-7 columns">						
							<h3><bbbl:label key="lbl_find_registry_registry_header" language ="${pageContext.request.locale.language}"/></h3>
							<p><span><bbbl:label key="lbl_regcreate_for" language ="${pageContext.request.locale.language}"/></span><bbbl:label key="lbl_regcreate_guests" language ="${pageContext.request.locale.language}"/></p>
						</div>
						<c:set var="handlerMethodForSearch" value="registrySearchFromBridalLanding" scope="request" />
						<c:set var="largeClass" value=""/>
					</c:otherwise>
				</c:choose>	
				<c:set var="findButton"><bbbl:label key='lbl_find_reg_submit_button' language ="${pageContext.request.locale.language}"></bbbl:label></c:set>
				<c:set var="findRegistryFormCount" value="1" scope="request"/>
				<dsp:include page="/addgiftregistry/find_registry_widget.jsp">
					<dsp:param name="findRegistryFormId" value="frmFindRegistry" />
					<dsp:param name="submitText" value="${findButton}" />
					<dsp:param name="handlerMethod" value="${handlerMethodForSearch}" />
					<dsp:param name="bridalException" value="false" />
					<dsp:param name="findRegistryFormCount" value="${findRegistryFormCount}" />
					<dsp:param name="largeClass" value="${largeClass}" />
				</dsp:include>
				</div>
</dsp:page>				