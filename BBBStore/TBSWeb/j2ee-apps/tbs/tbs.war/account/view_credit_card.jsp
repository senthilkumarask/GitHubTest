<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst"/>

	<%-- Variables --%>
	<c:set var="view_credit_card_title" scope="page">
		<bbbl:label key="lbl_creditcardinfo_creditcardstitle" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="add_credit_card" scope="page">
		<bbbl:label key="lbl_creditcardinfo_addcreditcard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="make_preferred" scope="page">
		<bbbl:label key="lbl_creditcardinfo_makepreferredcreditcard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="preferred_card" scope="page">
		<bbbl:label key="lbl_creditcardinfo_preferredcreditcard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="name_on_card" scope="page">
		<bbbl:label key="lbl_creditcardinfo_nameoncard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="edit_card_title" scope="page">
		<bbbl:label key="lbl_creditcardinfo_edit" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="remove_card_title" scope="page">
		<bbbl:label key="lbl_creditcardinfo_remove" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="exp" scope="page">
		<bbbl:label key="lbl_creditcardinfo_exp" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="ending_with" scope="page">
		<bbbl:label key="lbl_creditcardinfo_endingwith" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="remove_card_heading" scope="page">
		<bbbl:label key="lbl_creditcardinfo_remove_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:getvalueof id="requestURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<dsp:getvalueof id="cancelURL" idtype="java.lang.String" bean="/OriginatingRequest.requestURI"/>
	<c:set var="pageWrapper" value="creditCard myAccount" scope="request" />

	<bbb:pageContainer section="accounts" bodyClass="${themeName} my-account credit-cards" index="false" follow="false">

		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<div class="row" id="content">
			<div class="small-12 columns">
				<h1><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/>: <span class="subheader"><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" /></span></h1>
			</div>
			<div class="show-for-medium-down small-12">
				<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
			</div>
			<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
				<c:import url="/account/left_nav.jsp">
					<c:param name="currentPage"><bbbl:label key="lbl_myaccount_credit_cards" language ="${pageContext.request.locale.language}"/></c:param>
				</c:import>
			</div>
			<div class="small-12 large-9 columns">

				<div class="row">
					<div class="small-12 columns">
						<p>${view_credit_card_title}</p>
						<a href="add_credit_card.jsp" title="${add_credit_card}" class="small button primary">${add_credit_card}</a>
					</div>
					<div class="small-12 columns">
						<div class="row">
							<c:set var="hasCards" value="${false}"/>

							<%-- Iterate through all this user's credit cards, sorting the array so that the  default credit card is first. --%>
							<dsp:droplet name="MapToArrayDefaultFirst">
								<dsp:param name="defaultId" bean="Profile.defaultCreditCard.repositoryId"/>
								<dsp:param name="sortByKeys" value="true"/>
								<%-- Profile <dsp:valueof bean="Profile.creditCards"/> --%>
								<dsp:param name="map" bean="Profile.creditCards"/>
								<dsp:oparam name="output">
									<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray"/>
									<c:choose>
										<c:when test="${not empty sortedArray}">
											<c:set var="count" value="0"/>
											<c:forEach var="element" items="${sortedArray}" varStatus="cardIndexCount">
												<dsp:setvalue param="creditCard" value="${element}"/>
												<dsp:getvalueof var="creditCard" param="creditCard"/>
												<c:if test="${not empty creditCard}">
													<div class="small-12 large-6 columns">
														<div class="my-account-section">
															<c:set var="hasCards" value="${true}"/>
															<c:set var="numberLinks" value="2"/>
															<%-- compares whether the credit-card id and the default profile credit-card id are EQUAL or not. if equal sets isDefault to true else to false--%>
															<dsp:droplet name="Compare">
																<dsp:param name="obj" bean="Profile.defaultCreditCard"/>
																<dsp:param name="obj1" bean="Profile.defaultCreditCard.repositoryId"/>
																<dsp:param name="obj2" param="creditCard.value.id"/>
																<dsp:oparam name="equal">
																	<c:set var="isDefault" value="true"/>
																</dsp:oparam>
																<dsp:oparam name="default">
																	<c:set var="isDefault" value="false"/>
																</dsp:oparam>
															</dsp:droplet>

															<%-- Show Default Label if it is the default value --%>
															<c:choose>
																<c:when test="${isDefault}">
																	<h2>${preferred_card}</h2>
																	<dsp:form method="post" action="edit_credit_cards.jsp">
																		<dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
																		<dsp:param name="cancelURL" value="${cancelURL}?preFillValues=false"/>
																		<dsp:input type="submit" id="editCardSubmit${cardIndexCount.count}" iclass="hidden" paramvalue="creditCard.key" bean="ProfileFormHandler.editCard" />
																	</dsp:form>
																	<dsp:form method="post" action="view_credit_card.jsp">
																		<div class="item">
																			<ul class="address">
																				<li>
																					<dsp:getvalueof var="creditCard" param="creditCard.value.creditCardNumber"/>
																					<h5 class="inline"><dsp:valueof param="creditCard.value.creditCardType" valueishtml="false"/> ${ending_with}: </h5>
																					<c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}"/>
																				</li>
																				<li>
																					<h5 class="inline">${name_on_card}:</h5>
																					<dsp:valueof param="creditCard.value.nameOnCard" valueishtml="false"/>
																				</li>
																				<li>
																					<h5 class="inline">${exp}:</h5>
																					<dsp:valueof param="creditCard.value.expirationMonth" converter="number" format="00" valueishtml="false"/>/<dsp:valueof param="creditCard.value.expirationYear" valueishtml="false"/>
																				</li>
																			</ul>
																		</div>
																		<a href="#" class="edit-card" data-submit-button="editCardSubmit${cardIndexCount.count}" alt="${edit_card_title}">${edit_card_title}</a>
																		<dsp:a bean="ProfileFormHandler.removeCard" onclick="javascript:acctUpdate('removed saved card');" iclass="removeCardEntry hidden" href="remove_card_json.jsp" paramvalue="creditCard.key" title="${remove_card_title}">
																			${remove_card_title}
																		</dsp:a>
																		<a href="#" class="removeCardEntry2 right" title="${remove_card_title}">${remove_card_title}</a>
																	</dsp:form>
																</c:when>
																<c:otherwise>
																	<dsp:form method="post" action="view_credit_card.jsp">
																		<dsp:param name="creditCardNumber" param="creditCard.key"/>
																		<dsp:getvalueof var="ccName" param="creditCardNumber"/>
																		<dsp:input bean="ProfileFormHandler.value.defaultCreditCard" type="hidden" value="${ccName}"/>
																		<dsp:input type="submit" id="cardSubmit${cardIndexCount.count}" iclass="hidden" value="${make_preferred}" bean="ProfileFormHandler.checkoutDefaults" />
																	</dsp:form>
																	<a href="#" class="make-preferred" onclick="javascript:acctUpdate('preferred card updated');" data-submit-button="cardSubmit${cardIndexCount.count}" alt="${make_preferred}">${make_preferred}</a>
																	<dsp:form method="post" action="edit_credit_cards.jsp">
																		<dsp:param name="successURL" bean="/OriginatingRequest.requestURI"/>
																		<dsp:param name="cancelURL" value="${cancelURL}?preFillValues=false"/>
																		<dsp:input type="submit" id="editCardSubmit${cardIndexCount.count}" iclass="hidden" paramvalue="creditCard.key" bean="ProfileFormHandler.editCard" />
																	</dsp:form>
																	<dsp:form method="post" action="view_credit_card.jsp">
																		<div class="item">
																			<ul class="address">
																				<li>
																					<dsp:getvalueof var="creditCard" param="creditCard.value.creditCardNumber"/>
																					<h5 class="inline"><dsp:valueof param="creditCard.value.creditCardType"/> ${ending_with}:</h5>
																					<c:out value="${fn:substring(creditCard,fn:length(creditCard)-4,fn:length(creditCard))}"/>
																				</li>
																				<li>
																					<h5 class="inline">${name_on_card}:</h5>
																					<dsp:valueof param="creditCard.value.nameOnCard" valueishtml="false"/>
																				</li>
																				<li>
																					<h5 class="inline">${exp}:</h5>
																					<dsp:valueof param="creditCard.value.expirationMonth" converter="number" format="00" valueishtml="false"/>/<dsp:valueof param="creditCard.value.expirationYear" valueishtml="false"/>
																				</li>
																			</ul>
																		</div>
																		<a href="#" class="edit-card" data-submit-button="editCardSubmit${cardIndexCount.count}" alt="${edit_card_title}">${edit_card_title}</a>
																		<dsp:a bean="ProfileFormHandler.removeCard" onclick="javascript:acctUpdate('removed saved card');"  iclass="removeCardEntry hidden" href="remove_card_json.jsp" paramvalue="creditCard.key" title="${remove_card_title}">
																			${remove_card_title}
																		</dsp:a>
																		<a href="#" class="removeCardEntry2 right" title="${remove_card_title}">${remove_card_title}</a>
																	</dsp:form>
																</c:otherwise>
															</c:choose>
														</div>
													</div>
													<c:set var="count" value="${count + 1}"/>
												</c:if>
											</c:forEach>
										</c:when>
									</c:choose>
								</dsp:oparam>
							</dsp:droplet>
							<%-- <c:if test="${hasCards}">
								<div class="small-12 columns">
									<a class="addCardEntry small button primary" title="${add_credit_card}" href="add_credit_card.jsp">${add_credit_card}</a>
								</div>
							</c:if> --%>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div id="removeCardWarningModal" class="reveal-modal medium" data-reveal>
			<h1>${remove_card_heading}</h1>
			<p><bbbl:label key="lbl_creditcardinfo_remove_description" language="${pageContext.request.locale.language}" /></p>
			<a class="ok-button small button transactional">OK</a>
			<a class="close-modal small button secondary">Cancel</a>
			<a class="close-reveal-modal">&#215;</a>
		</div>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
				s.pageName ='My Account>My Credit Cards';
				s.channel = 'My Account';
				s.prop1 = 'My Account';
				s.prop2 = 'My Account';
				s.prop3 = 'My Account';
				s.prop6='${pageContext.request.serverName}';
				s.eVar9='${pageContext.request.serverName}';
				var s_code = s.t();
				if (s_code)
					document.write(s_code);
				}
			</script>
		</jsp:attribute>

	</bbb:pageContainer>

</dsp:page>
