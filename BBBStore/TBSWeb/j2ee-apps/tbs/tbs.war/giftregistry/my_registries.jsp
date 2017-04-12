<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/browse/droplet/StatesLookup" />
	<dsp:importbean bean="/atg/dynamo/droplet/Switch" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/MyRegistriesDisplayDroplet" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryTypesDroplet" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/formhandler/GiftRegistryFormHandler" />
	<dsp:importbean	bean="/com/bbb/commerce/giftregistry/droplet/DateCalculationDroplet" />

	<%-- Variables --%>
	<dsp:getvalueof var="appid" bean="Site.id" />
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<bbb:pageContainer>

		<jsp:attribute name="bodyClass">my-account my-registries</jsp:attribute>
		<jsp:attribute name="section">accounts</jsp:attribute>
		<jsp:attribute name="pageWrapper">manageRegistry useCertonaJs myAccount</jsp:attribute>
        <%-- <jsp:attribute name="headerRenderer">
           <dsp:include page="/giftregistry/gadgets/header.jsp" flush="true" />
        </jsp:attribute> --%>   
		<jsp:body>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_regsrchguest_my_account" language="${pageContext.request.locale.language}" />: <span class="subheader"><bbbl:label key="lbl_regsrchguest_registries" language="${pageContext.request.locale.language}" /></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage">
						<bbbl:label key="lbl_myaccount_registries" language="${pageContext.request.locale.language}" />
					</c:param>
					</c:import>
				</div>
				<div class="small-12 large-9 columns">
					<div class="row">
						<div class="small-12 columns small-scroll">
							<dsp:droplet name="MyRegistriesDisplayDroplet">
								<dsp:param name="profile" bean="/atg/userprofiling/Profile" />
								<dsp:param name="siteId" value="${appid}" />
								<dsp:oparam name="output">
									<dsp:getvalueof param="registryCount" var="registryCount" />
									<dsp:getvalueof param="registrySummaryVO" var="registrySummaryVO" />
									<dsp:getvalueof param="priRegSummVO" var="priRegSummVO" />
									<dsp:getvalueof param="coRegSummVO" var="coRegSummVO" />
									<dsp:getvalueof param="priRegListSize" var="priRegListSize" />
									<dsp:getvalueof param="coRegListSize" var="coRegListSize" />

									<table class="simpleTable registryHeader noMarTop">
										<c:if test="${registryCount >=1}">
											<thead>
												<tr>
													<th class="thCol1"><bbbl:label key="lbl_regsrchguest_EventType"	language="${pageContext.request.locale.language}" /></th>
													<th class="thCol2"><bbbl:label key="lbl_regsrchguest_CoRegistrant" language="${pageContext.request.locale.language}" /></th>
													<th class="thCol3"><bbbl:label key="lbl_regsrchguest_EventDate"	language="${pageContext.request.locale.language}" /></th>
													<th class="thCol4"><bbbl:label key="lbl_regsrchguest_GiftRegPur" language="${pageContext.request.locale.language}" /></th>
													<th class="thCol5"><bbbl:label key="lbl_regsrchguest_Registry" language="${pageContext.request.locale.language}" /></th>
													<th class="thCol6"></th>
												</tr>
											</thead>
										</c:if>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" param="registrySummaryVO" />
											<dsp:oparam name="output">
												<dsp:getvalueof param="element.eventType" var="eventType" />
												<dsp:getvalueof param="element.registryId" var="regId" />
												<dsp:getvalueof var="daysToGo" param="daysToGo"/>
												<c:set var="daysToGoPreText" value=""/>
												<c:set var="daysToGoText" value=""/>
												<dsp:getvalueof var="daysToGo" value=""/>
												<dsp:droplet name="DateCalculationDroplet">
													<dsp:param name="eventDate" param="element.eventDate"/>
													<dsp:oparam name="output">
														<dsp:droplet name="Switch">
															<dsp:param name="value" param="check" />
															<dsp:oparam name="true">
																<c:choose>
																	<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
																		<c:choose>
																			<c:when test="${not empty eventType && eventType eq 'Baby'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_baby_arrives' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Birthday'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:otherwise>
																			</c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${not empty eventType && eventType eq 'Wedding'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Baby'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_day_until_baby' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Birthday'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Retirement'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_day_until_wedding' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'College'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'University'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'College/University'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_only' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_daystogo' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</dsp:oparam>
															<dsp:oparam name="false">
																<c:choose>
																	<c:when test="${not empty appid && appid eq 'TBS_BuyBuyBaby'}">
																		<c:choose>
																			<c:when test="${not empty eventType && eventType eq 'Baby'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Birthday'}">
																				<dsp:getvalueof var="daysToGo" param="daysToNextCeleb"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:otherwise></c:otherwise>
																		</c:choose>
																	</c:when>
																	<c:otherwise>
																		<c:choose>
																			<c:when test="${not empty eventType && eventType eq 'Wedding'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Baby'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_since_life' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Birthday'}">
																				<dsp:getvalueof var="daysToGo" param="daysToNextCeleb"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_days_next_celeb' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Retirement'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_rest_of_life' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Anniversary'}">
																				<dsp:getvalueof var="daysToGo" param="daysToNextCeleb"/>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_another_year' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Housewarming'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_new_place' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																			<c:when test="${not empty eventType && eventType eq 'Commitment Ceremony'}">
																				<dsp:getvalueof var="daysToGo" param="daysToGo"/>
																				<c:set var="daysToGoPreText"><bbbl:label key='lbl_regflyout_daystogo_day' language="${pageContext.request.locale.language}" /></c:set>
																				<c:set var="daysToGoText"><bbbl:label key='lbl_regflyout_daystogo_happy_ever_after' language="${pageContext.request.locale.language}" /></c:set>
																			</c:when>
																		</c:choose>
																	</c:otherwise>
																</c:choose>
															</dsp:oparam>
														</dsp:droplet>
													</dsp:oparam>
												</dsp:droplet>
												<tbody>
													<tr>
														<td class="tdCol1"><dsp:valueof param="element.eventType" /></td>
														<td class="tdCol2"><dsp:valueof	param="element.coRegistrantFullName" /></td>
														<dsp:getvalueof param="element.eventDate" var="eventDateCheck" />
														<c:if test="${eventDateCheck != null}">
															<td class="eventDate tdCol3">
																<c:choose>
																	<c:when test="${appid eq 'TBS_BedBathCanada'}">
																		<dsp:valueof param="element.eventDateCanada" />
																	</c:when>
																	<c:otherwise>
																		<dsp:valueof param="element.eventDate" />
																	</c:otherwise>
																</c:choose>
																<c:if test="${not empty daysToGo}">
																	<span><bbbl:label key="lbl_regsrchguest_bracOpen" language ="${pageContext.request.locale.language}"/>
																	<c:if test="${not empty daysToGoPreText}">
																		<span>${daysToGoPreText}</span>
																	</c:if>
																	<span>${daysToGo}</span>
																	<c:if test="${not empty daysToGoText}">
																		<span>${daysToGoText}</span>
																	</c:if>
																	<bbbl:label key="lbl_regsrchguest_bracClose" language ="${pageContext.request.locale.language}"/></span>
																</c:if>
															</td>
														</c:if>
														<td class="tdCol4"><dsp:valueof param="element.giftRegistered" /> / <dsp:valueof param="element.giftPurchased" /></td>
														<td class="tdCol5"><dsp:valueof	param="element.registryId" /></td>
														<td class="textRight tdCol6">
															<dsp:a href="view_registry_owner.jsp" title="View/Edit" iclass="allCaps">
																<dsp:param name="registryId" value="${regId}" />
																<dsp:param name="eventType" value="${eventType}" />
																<strong><bbbl:label key="lbl_regflyout_view_edit" language="${pageContext.request.locale.language}" /></strong>
															</dsp:a>
														</td>
													</tr>
												</tbody>
											</dsp:oparam>
											<dsp:oparam name="error">
												<dsp:getvalueof param="errorMsg" var="errorMsg" />
												<div class="row">
													<div class="small-12 columns error">
														<bbbe:error key="${errorMsg}" language="${pageContext.request.locale.language}" />
													</div>
												</div>
											</dsp:oparam>
										</dsp:droplet>
									</table>
								</dsp:oparam>
							</dsp:droplet>
						</div>
					</div>
					<div class="row">
                        <!-- Create registry is not a TBS function -->
						<%-- <div class="small-12 large-6 columns">
							<c:choose>
								<c:when test="${registryCount == 0}">
									<bbbt:textArea key="txt_create_a_registry" language="${pageContext.request.locale.language}" />
								</c:when>
								<c:otherwise>
									<bbbt:textArea key="txt_another_registry" language="${pageContext.request.locale.language}" />
								</c:otherwise>
							</c:choose>
							<dsp:include page="/giftregistry/my_registry_type_select.jsp" />
						</div> --%>
						<div class="small-12 columns">
							<bbbt:textArea key="txt_why_registry" language="${pageContext.request.locale.language}" />
						</div>
					</div>
					<div class="row">
						<div class="small-12 columns">
							<h3><bbbl:label key="lbl_regflyout_looking_old_reg" language="${pageContext.request.locale.language}" /></h3>
							<p class="p-secondary"><bbbl:label key="lbl_regflyout_if_created" language="${pageContext.request.locale.language}" /></p>
							<dsp:form action="#" id="findOldRegistry" method="post">
								<h3 class="findOldRegistry"><bbbl:label key='lbl_regsearch_lnktxt' language="${pageContext.request.locale.language}" /></h3>
								<div class="row">
									<div class="small-12 large-3 columns">
										<div class="row">
											<div class="small-12 columns">
												<c:set var="placeholder"><bbbl:label key='lbl_reg_firstname' language="${pageContext.request.locale.language}" /></c:set>
												<dsp:input type="text" name="registryFirstName" id="registryFirstName" bean="GiftRegistryFormHandler.registrySearchVO.firstName">
													<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
													<dsp:tagAttribute name="aria-required" value="false"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblregistryFirstName errorregistryFirstName"/>
												</dsp:input>
											</div>
											<div class="small-12 columns">
												<c:set var="placeholder"><bbbl:label key='lbl_reg_lastname' language="${pageContext.request.locale.language}" /></c:set>
												<dsp:input type="text" name="registryLastName" id="registryLastName" bean="GiftRegistryFormHandler.registrySearchVO.lastName">
													<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
													<dsp:tagAttribute name="aria-required" value="false"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblregistryLastName errorregistryLastName"/>
												</dsp:input>
											</div>
											<div class="small-12 columns">
												<c:choose>
													<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
														<c:set var="placeholder"><bbbl:label key="lbl_bridalbook_select_province" language="${pageContext.request.locale.language}" /></c:set>
													</c:when>
													<c:otherwise>
														<c:set var="placeholder"><bbbl:label key="lbl_bridalbook_select_state" language="${pageContext.request.locale.language}" /></c:set>
													</c:otherwise>
												</c:choose>
												<dsp:select	bean="GiftRegistryFormHandler.registrySearchVO.state" name="bbRegistryState" id="stateName">
													<dsp:option>${placeholder}</dsp:option>
													<dsp:droplet name="/com/bbb/selfservice/StateDroplet">
														<dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
														<dsp:oparam name="output">
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
													<dsp:tagAttribute name="aria-required" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
												</dsp:select>
											</div>
										</div>
									</div>
									<div class="small-12 large-1 columns or">
										<bbbl:label key='lbl_regsearch_or' language="${pageContext.request.locale.language}" />
									</div>
									<div class="small-12 large-3 columns">
										<c:set var="placeholder"><bbbl:label key='lbl_regsearch_email' language="${pageContext.request.locale.language}" /></c:set>
										<dsp:input type="text" name="registryEmail" id="registryEmail" bean="GiftRegistryFormHandler.registrySearchVO.email">
											<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
											<dsp:tagAttribute name="aria-required" value="false"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblregistryEmail errorregistryEmail"/>
										</dsp:input>
									</div>
									<div class="small-12 large-1 columns or">
										<bbbl:label key='lbl_regsearch_or' language="${pageContext.request.locale.language}" />
									</div>
									<div class="small-12 large-3 columns">
										<c:set var="placeholder"><bbbl:label key='lbl_regsearch_registry_num' language="${pageContext.request.locale.language}" /></c:set>
										<dsp:input type="text" name="registryNumber" id="registryNumber" bean="GiftRegistryFormHandler.registrySearchVO.registryId">
											<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
											<dsp:tagAttribute name="aria-required" value="false"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblregistryNumber errorregistryNumber"/>
										</dsp:input>
									</div>
									<div class="small-12 columns">
										<c:set var="findRegistryBtn">Find Registry</c:set>
										<dsp:input bean="GiftRegistryFormHandler.hidden" type="hidden" value="2" />
										<dsp:input type="submit" bean="GiftRegistryFormHandler.registrySearchFromImportRegistryPage" value="${findRegistryBtn}" id="findRegistryBtn" iclass="small button primary">
											<dsp:tagAttribute name="aria-required" value="false"/>
											<dsp:tagAttribute name="aria-labelledby" value="findRegistryBtn"/>
										</dsp:input>
									</div>
								</div>
							</dsp:form>
						</div>
					</div>
				</div>
			</div>
			<div id="regFindOldRegistry" class="11 reveal-modal medium" data-reveal-ajax="true" data-reveal>
				<bbbe:error key='err_js_find_reg_multi_input' language='${language}'/>
                <a class="close-reveal-modal">&#215;</a>
            </div>
			<script type="text/javascript">
				var totalCount='${totalCount}';
			</script>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
					s.pageName ='My Account>My Registries';
					s.channel = 'My Account';
					s.prop1 = 'My Account';
					s.prop2 = 'My Account';
					s.prop3 = 'My Account';
					s.prop6='${pageContext.request.serverName}';
					s.eVar9='${pageContext.request.serverName}';
					if (totalCount > 0) {
						s.pageName= document.location.href;
						s.events="event29";
					}
					var s_code=s.t();
					if(s_code)document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
