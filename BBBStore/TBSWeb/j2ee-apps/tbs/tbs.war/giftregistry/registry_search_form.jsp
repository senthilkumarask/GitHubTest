<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsNull" />
	<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />

	<%-- Page Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="showOnlySearchFields" param="showOnlySearchFields" />
	<dsp:getvalueof param="commerceId" var="cId"/>
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:choose>
		<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
			<c:set var="stateLabel" value="Province"/>
		</c:when>
		<c:otherwise>
			<c:set var="stateLabel" value="State"/>
		</c:otherwise>
	</c:choose>

	<dsp:form id="regSearchForm" method="post" action="registry_search_form.jsp" >
		<div class="row search-form">
			<div class="small-6 large-2 columns">
				<div class="row">
					<div class="small-12 large-10 columns no-padding">
						<dsp:input type="text" bean="GiftRegistryFormHandler.registrySearchVO.registryId" id="bbRegistryNumber" name="bbRegistryNumber" value="">
							<dsp:tagAttribute name="placeholder" value="Registry ID" />
							<dsp:tagAttribute name="aria-required" value="false" />
							<dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryNumber errorbbRegistryNumber" />
						</dsp:input>
					</div>
					<div class="small-12 large-2 columns or">
						or
					</div>
				</div>
			</div>

			<div class="small-12 large-8 columns reg-name-search">
				<div class="row">
					<div class="small-6 large-3 columns">
						<dsp:input id="bbRegistryFirstName" name="bbRegistryFirstName" type="text" bean="GiftRegistryFormHandler.registrySearchVO.firstName" maxlength="30"  value="">
							<dsp:tagAttribute name="placeholder" value="First Name" />
							<dsp:tagAttribute name="aria-required" value="false" />
							<dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryFirstName errorbbRegistryFirstName" />
						</dsp:input>
					</div>
					<div class="small-6 large-3 columns no-padding-large">
						<dsp:input id="bbRegistryLastName" name="bbRegistryLastName" type="text" bean="GiftRegistryFormHandler.registrySearchVO.lastName" maxlength="30" value="">
							<dsp:tagAttribute name="placeholder" value="Last Name" />
							<dsp:tagAttribute name="aria-required" value="false" />
							<dsp:tagAttribute name="aria-labelledby" value="lblbbRegistryLastName errorbbRegistryLastName" />
						</dsp:input>
					</div>
					<div class="small-6 large-3 columns registry-state">
						<%-- KP COMMENT START: turned this into a list dropdown --%>
						<%--
						<dsp:select bean="GiftRegistryFormHandler.registrySearchVO.state" name="bbRegistryState" >
							<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
								<dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
								<dsp:oparam name="output">
									<dsp:option>
										<c:choose>
											<c:when test="${appid eq TBS_BedBathCanadaSite}">
												<bbbl:label key="lbl_bridalbook_select_province" language="${pageContext.request.locale.language}" />
											</c:when>
											<c:otherwise>
												<bbbl:label key="lbl_bridalbook_select_state" language="${pageContext.request.locale.language}" />
											</c:otherwise>
										</c:choose>
									</dsp:option>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="location" />
										<dsp:oparam name="output">
											<dsp:getvalueof param="element.stateName" id="stateName" />
											<dsp:getvalueof param="element.stateCode" id="stateCode" />
											<dsp:option value="${stateCode}">
												${stateName}
											</dsp:option>
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</dsp:select>
						--%>

						<a href="#" data-dropdown="bbRegistryStateDropdown" id="bbRegistryStateDropdownTrigger" class="small download radius button dropdown thin">${stateLabel}<span>&nbsp;</span></a>
						<ul id="bbRegistryStateDropdown" data-dropdown-content="" class="f-dropdown">
							<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
								<dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
								<dsp:oparam name="output">
									<li>
										<a href="#">
											<c:choose>
												<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
													<bbbl:label key="lbl_bridalbook_select_province" language="${pageContext.request.locale.language}" />
												</c:when>
												<c:otherwise>
													<bbbl:label key="lbl_bridalbook_select_state" language="${pageContext.request.locale.language}" />
												</c:otherwise>
											</c:choose>
										</a>
									</li>
									<dsp:droplet name="/atg/dynamo/droplet/ForEach">
										<dsp:param name="array" param="location" />
										<dsp:oparam name="output">
											<dsp:getvalueof param="element.stateName" id="stateName" />
											<dsp:getvalueof param="element.stateCode" id="stateCode" />
											<li>
												<a href="#" data-state="${stateCode}">
													${stateName}
												</a>
											</li>
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</ul>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.state" id="bbRegistryState" name="bbRegistryState" value="" />
					</div>
					<%-- KP COMMENT END --%>

					<%-- Registry Type Dropdown --%>
					<div class="small-6 large-3 columns registry-type no-padding-left-large">
						<a href="#" data-dropdown="regSearchType" class="small download radius button dropdown thin">Event Type<span>&nbsp;</span></a>
						<ul id="regSearchType" data-dropdown-content class="f-dropdown">
							<dsp:droplet name="GiftRegistryTypesDroplet">
								<dsp:param name="siteId" value="${appid}"/>
								<dsp:oparam name="output">
									<li class="selected"><a href="#">Event Type</a></li>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" param="registryTypes" />
										<dsp:oparam name="output">
											<dsp:param name="regTypes" param="element" />
											<dsp:getvalueof var="regTypesId" param="regTypes.registryName" />
											<dsp:getvalueof var="registryCode" param="regTypes.registryCode" />
											<li><a href="#" data-registry-code="${registryCode}"><dsp:valueof param="element.registryName" /></a></li>
										</dsp:oparam>
									</dsp:droplet>
								</dsp:oparam>
							</dsp:droplet>
						</ul>
						<dsp:input type="hidden" bean="GiftRegistryFormHandler.registrySearchVO.event" id="bbRegistryEvent" name="bbRegistryEvent" value="" />
					</div>

				</div>
			</div>
			<div class="small-6 large-2 right columns">
				<a class="button tiny service right expand submit-registry-search">SEARCH</a>
				<dsp:input type="hidden" bean="GiftRegistryFormHandler.hidden" value="8" />
				<dsp:input bean="GiftRegistryFormHandler.registrySearch" id="registrySearch" type="submit" value="SEARCH" iclass="hidden">
<!-- 					<dsp:tagAttribute name="aria-pressed" value="false" /> -->
<!-- 					<dsp:tagAttribute name="aria-labelledby" value="searchRegistry" /> -->
<!-- 					<dsp:tagAttribute name="role" value="button" /> -->
				</dsp:input>
				        <dsp:input bean="GiftRegistryFormHandler.fromPage"  type="hidden" value="registrySearchForm" />
				        <dsp:input bean="GiftRegistryFormHandler.queryParam"  type="hidden" value="commerceId=${cId }&search=search" />
				<%-- <dsp:input bean="GiftRegistryFormHandler.registrySearchSuccessURL" type="hidden" value="${contextPath}/giftregistry/registry_search.jsp?commerceId=${cId }&search=search" />
				<dsp:input bean="GiftRegistryFormHandler.registrySearchErrorURL" type="hidden" value="${contextPath}/giftregistry/registry_search.jsp?commerceId=${cId }&search=search" /> --%>
			</div>
		</div>
		<div class="row highlightRed hidden regErrorMessage"><bbbe:error key='err_js_find_reg_multi_input2' language='${language}'/></div>
	</dsp:form>
	<div id="searchRegistryModal" class="reveal-modal medium" data-reveal-ajax="true" data-reveal>
		<bbbe:error key='err_js_find_reg_multi_input2' language='${language}'/>
    	<a class="close-reveal-modal">&#215;</a>
	</div>

</dsp:page>
