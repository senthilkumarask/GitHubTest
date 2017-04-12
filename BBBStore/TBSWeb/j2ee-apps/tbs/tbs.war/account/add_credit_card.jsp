<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Compare" />
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/common/BBBPopulateStatesDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	
	<%-- Variables --%>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<dsp:getvalueof bean="Profile.secondaryAddresses" id="addr"/>
	<c:if test="${empty addr}">
		<c:set var="mkprefaddrDisable" value="true"/>
	</c:if>
	<c:set var="add_credit_card_title" scope="page">
		<bbbl:label key="lbl_creditcardinfo_creditcardstitle" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="add_new_credit_card" scope="page">
		<bbbl:label key="lbl_addcreditcard_addnewcreditcard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="card_type" scope="page">
		<bbbl:label key="lbl_addcreditcard_cardtype" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="chooseCardType" scope="page">
		<bbbl:label key="lbl_choose_cardtype" language="${pageContext.request.locale.language}"/>
	</c:set>
	<c:set var="card_number" scope="page">
		<bbbl:label key="lbl_addcreditcard_cardnumber" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="expires_on" scope="page">
		<bbbl:label key="lbl_addcreditcard_expireson" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="month" scope="page">
		<bbbl:label key="lbl_addcreditcard_month" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="year" scope="page">
		<bbbl:label key="lbl_addcreditcard_year" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="name_on_card" scope="page">
		<bbbl:label key="lbl_addcreditcard_nameoncard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="first_name" scope="page">
		<bbbl:label key="lbl_addcreditcard_firstname" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="last_name" scope="page">
		<bbbl:label key="lbl_addcreditcard_lastname" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="company" scope="page">
		<bbbl:label key="lbl_addcreditcard_company" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lbl_address1" scope="page">
		<bbbl:label key="lbl_addcreditcard_address1" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lbl_address2" scope="page">
		<bbbl:label key="lbl_addcreditcard_address2" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="city" scope="page">
		<bbbl:label key="lbl_addcreditcard_city" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lbl_state" scope="page">
		<bbbl:label key="lbl_addcreditcard_state" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="zip" scope="page">
		<bbbl:label key="lbl_addcreditcard_zip" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lbl_creditcardinfo_visa" scope="page">
		<bbbl:label key="lbl_creditcardinfo_visa" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lbl_creditcardinfo_mastercard" scope="page">
		<bbbl:label key="lbl_creditcardinfo_mastercard" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lbl_creditcardinfo_americanexpress" scope="page">
		<bbbl:label key="lbl_creditcardinfo_americanexpress" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="lbl_creditcardinfo_discover" scope="page">
		<bbbl:label key="lbl_creditcardinfo_discover" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="make_preferred_billing" scope="page">
		<bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="add_new_billing_addr_radio" scope="page">
		<bbbl:label key="lbl_addcreditcard_addnewbillingaddr" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="select_billing_addr" scope="page">
		<bbbl:label key="lbl_addcreditcard_sltccbillingaddr" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="pageWrapper" value="creditCard myAccount" scope="request" />

	<bbb:pageContainer section="accounts" bodyClass="${themeName} my-account credit-cards" index="false" follow="false">

		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<div class="row">
			<div class="small-12 columns">
				<h1><bbbl:label key="lbl_personalinfo_myaccount" language="${pageContext.request.locale.language}"/>: <span class="subheader"><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" /></span></h1>
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
						<h3>Add New Credit Card</h3>
						<p>${add_credit_card_title}</p>
					</div>
				</div>
				<div class="row">
					<dsp:form id="addCreditCard" name="form" method="post">
						<c:set var="radiobtn" value="true"/>
						<dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="createCard" />
						<!--<dsp:input bean="ProfileFormHandler.createCardSuccessURL" id="successURL" type="hidden" value="view_credit_card.jsp" />
						<dsp:input bean="ProfileFormHandler.createCardErrorURL"	id="errorURL" type="hidden" value="add_credit_card.jsp" />-->
						<div class="small-12 columns">
							<dsp:include page="/global/gadgets/errorMessage.jsp">
								<dsp:param name="formhandler" bean="ProfileFormHandler"/>
							</dsp:include>
						</div>
						<div class="small-12 large-6 columns">
							<div class="row">
								<div class="small-12 columns">
									<dsp:select id="cardType" bean="ProfileFormHandler.editValue.creditCardType" name="cardType" title="creditCardType">
										<dsp:option value="">${chooseCardType}</dsp:option>
										<dsp:droplet name="ForEach">
											<dsp:param name="array" bean="ProfileFormHandler.creditCardTypes" />
											<dsp:param name="elementName" value="cardlist" />
											<dsp:oparam name="output">
												<dsp:getvalueof id="cardName" param="cardlist.name" />
												<dsp:getvalueof id="cardCode" param="cardlist.code" />
												<dsp:option value="${cardCode}">
													<c:out value="${cardName}" />
												</dsp:option>
											</dsp:oparam>
										</dsp:droplet>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblcardType errorcardType"/>
									</dsp:select>
									<dsp:droplet name="ForEach">
										<dsp:param name="array" bean="ProfileFormHandler.creditCardTypes" />
										<dsp:param name="elementName" value="cardlist" />
										<dsp:oparam name="output">
											<dsp:getvalueof id="cardImage" param="cardlist.imageURL" />
											<dsp:getvalueof id="cardName" param="cardlist.name" />
											<img class="creditCardIcon" src="${imagePath}${cardImage}" width="41" height="26" alt="${cardName}" title="${cardName}" />
										</dsp:oparam>
									</dsp:droplet>
								</div>
								<div class="small-12 columns">
									<dsp:input type="hidden" bean="ProfileFormHandler.editValue.nickname" name="nickname" />
									<dsp:input type="text" name="cardNumber" id="credit" bean="ProfileFormHandler.editValue.creditCardNumber" maxlength="16" autocomplete="off">
										<dsp:tagAttribute name="placeholder" value="${card_number} *"/>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblcredit errorcredit"/>
									</dsp:input>
								</div>
								<div class="small-12 large-4 columns">
									<label class="left inline small-only-no-margin-bottom medium-only-no-margin-bottom">${expires_on}</label>
								</div>
								<div class="small-12 large-4 columns">
									<dsp:select bean="ProfileFormHandler.editValue.expirationMonth" iclass="uniform" id="creditCardMonth" name="creditCardMonth">
										<dsp:option>${month}</dsp:option>
										<dsp:option value="1">01</dsp:option>
										<dsp:option value="2">02</dsp:option>
										<dsp:option value="3">03</dsp:option>
										<dsp:option value="4">04</dsp:option>
										<dsp:option value="5">05</dsp:option>
										<dsp:option value="6">06</dsp:option>
										<dsp:option value="7">07</dsp:option>
										<dsp:option value="8">08</dsp:option>
										<dsp:option value="9">09</dsp:option>
										<dsp:option value="10">10</dsp:option>
										<dsp:option value="11">11</dsp:option>
										<dsp:option value="12">12</dsp:option>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblcreditCardMonth errorcreditCardMonth"/>
									</dsp:select>
								</div>
								<div class="small-12 large-4 columns">
									<jsp:useBean id="now" class="java.util.Date" scope="request" />
									<fmt:formatDate var="year_val" value="${now}" pattern="yyyy" />
									<dsp:select bean="ProfileFormHandler.editValue.expirationYear" title="year" iclass="uniform" id="creditCardYear" name="creditCardYear">
										<dsp:option value="">${year}</dsp:option>
										<dsp:option value="${year_val}">${year_val}</dsp:option>
										<dsp:getvalueof var="cardMaxYear" bean="ProfileFormHandler.creditCardYearMaxLimit" />
										<c:forEach begin="0" end="${cardMaxYear-1}" step="1" varStatus="count">
											<dsp:option value="${year_val+count.count}">${year_val+count.count}</dsp:option>
										</c:forEach>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="creditCardYear errorcreditCardYear"/>
									</dsp:select>
								</div>
								<div class="small-12 columns">
									<dsp:input bean="ProfileFormHandler.editValue.nameOnCard" type="text" maxlength="61" name="nameCard" id="nameCard" autocomplete="off" >
										<dsp:tagAttribute name="placeholder" value="${name_on_card} *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblnameCard errornameCard"/>
									</dsp:input>
								</div>
							</div>
						</div>
						<div class="small-12 columns">
							<div class="row">
								<div class="small-12 columns">
									<h3>${select_billing_addr}</h3>
								</div>
								<div class="small-12 columns">
									<dsp:param name="defaultAddressId"	bean="Profile.billingAddress.firstName" />
									<dsp:input type="hidden" name="address"	paramvalue="defaultAddressId" id="${shippingAddress.value.repositoryId}" bean="ProfileFormHandler.billAddrValue.newNickname" />
									<dsp:droplet name="MapToArrayDefaultFirst">
										<dsp:param name="map" bean="Profile.secondaryAddresses" />
										<dsp:param name="defaultId" bean="Profile.billingAddress.repositoryId" />
										<dsp:oparam name="output">
											<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray" />
											<c:if test="${not empty sortedArray}">
												<c:set var="radiobtn" value=""/>
												<c:set var="counter" value="0" />
												<dsp:getvalueof var="defaultAddressId"	vartype="java.lang.String"	bean="Profile.billingAddress.repositoryId" />
												<label id="erroraddressToShip" for="addressToShip" class="error" generated="true"></label>
												<ul class="small-block-grid-1 large-block-grid-2">
													<c:forEach var="shippingAddress" items="${sortedArray}"	varStatus="status">
														<dsp:param name="shippingAddress" value="${shippingAddress}" />
														<c:if test="${not empty shippingAddress}">
															<c:set var="count" value="0" />
															<c:set var="counter" value="${counter + 1}" />
															<li>
																<label class="inline-rc radio gray-panel billing-address" id="lbl${shippingAddress.value.repositoryId}" for="${shippingAddress.value.repositoryId}">
																	<dsp:droplet name="Compare">
																		<dsp:param name="obj1"	bean="Profile.billingAddress.repositoryId" />
																		<dsp:param name="obj2" param="billingAddress.value.id" />
																		<dsp:oparam name="equal">
																			<dsp:input type="radio" paramvalue="shippingAddress.key" bean="ProfileFormHandler.billAddrValue.newNickname" id="${shippingAddress.value.repositoryId}" name="address" iclass="radio required">
																				<dsp:tagAttribute name="aria-checked" value="false"/>
																				<dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} error${shippingAddress.value.repositoryId}"/>
																			</dsp:input>
																		</dsp:oparam>
																		<dsp:oparam name="false">
																			<dsp:input type="radio" paramvalue="shippingAddress.key" bean="ProfileFormHandler.billAddrValue.newNickname" id="${shippingAddress.value.repositoryId}" name="address" iclass="radio required" checked="true">
																				<dsp:tagAttribute name="aria-checked" value="true"/>
																				<dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} error${shippingAddress.value.repositoryId}"/>
																			</dsp:input>
																		</dsp:oparam>
																		<dsp:oparam name="default">
																			<c:choose>
																				<c:when test="${shippingAddress.value.repositoryId eq defaultAddressId}">
																					<dsp:input type="radio" paramvalue="shippingAddress.key" bean="ProfileFormHandler.billAddrValue.newNickname" id="${shippingAddress.value.repositoryId}" name="addressToShip" iclass="radio" checked="true">
																						<dsp:tagAttribute name="aria-checked" value="true"/>
																						<dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} erroraddressToShip"/>
																					</dsp:input>
																				</c:when>
																				<c:otherwise>
																					<dsp:input type="radio" paramvalue="shippingAddress.key" bean="ProfileFormHandler.billAddrValue.newNickname" id="${shippingAddress.value.repositoryId}" name="addressToShip" iclass="radio required">
																						<dsp:tagAttribute name="aria-checked" value="false"/>
																						<dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} erroraddressToShip"/>
																					</dsp:input>
																				</c:otherwise>
																			</c:choose>
																		</dsp:oparam>
																	</dsp:droplet>
																	<span></span>

																	<dsp:param name="address" param="shippingAddress.value" />
																	<dsp:param name="private" value="false" />
																	<dsp:getvalueof var="addressValue" param="address.country" />
																	<c:choose>
																		<c:when test="${addressValue == ''}" />
																		<c:otherwise>
																			<ul class="address">
																				<li><dsp:valueof param="address.firstName" /> <dsp:valueof param="address.lastName" /></li>
																				<li><dsp:valueof param="address.companyName" /></li>
																				<dsp:getvalueof var="private" param="private" />
																				<c:choose>
																					<c:when test="${private == 'true'}">
																						<%-- Do Not Display Address Details since it is private --%>
																					</c:when>
																					<c:otherwise>
																						<li><dsp:valueof param="address.address1" /></li>
																						<dsp:getvalueof var="address2" param="address.address2" />
																						<c:if test="${not empty address2}">
																							<li><dsp:valueof param="address.address2" /></li>
																						</c:if>
																						<li><dsp:valueof param="address.city" />, <dsp:valueof param="address.state" /> <dsp:valueof param="address.postalCode" /></li>
																					</c:otherwise>
																				</c:choose>
																			</ul>
																			<dsp:getvalueof var="keyval" param="shippingAddress.key" />
																			<label class="inline-rc checkbox" id="lblbillingAdd${counter}" for="billingAdd${counter}">
																				<c:choose>
																					<c:when test="${counter eq 1}">
																						<dsp:input type="checkbox" bean="ProfileFormHandler.billAddrValue.makeBilling" id="billingAdd${counter}" name="billingAdd${counter}" checked="true" value="true">
																							<dsp:tagAttribute name="aria-checked" value="false"/>
																							<dsp:tagAttribute name="aria-labelledby" value="lblbillingAdd${counter} errorbillingAdd${counter}"/>
																						</dsp:input>
																					</c:when>
																					<c:otherwise>
																						<dsp:input type="checkbox" bean="ProfileFormHandler.billAddrValue.makeBilling" id="billingAdd${counter}" name="billingAdd${counter}" value="true">
																							<dsp:tagAttribute name="aria-checked" value="false"/>
																							<dsp:tagAttribute name="aria-labelledby" value="lblbillingAdd${counter} errorbillingAdd${counter}"/>
																						</dsp:input>
																					</c:otherwise>
																				</c:choose>
																				<span></span>
																				${make_preferred_billing}
																			</label>
																		</c:otherwise>
																	</c:choose>
																</label>
															</li>
														</c:if>
													</c:forEach>
												</ul>
											</c:if>
										</dsp:oparam>
									</dsp:droplet>
								</div>
								<div class="small-12 large-6 columns">
									<label class="inline-rc radio gray-panel billing-address new-billing-address-trigger" id="lbladdressToShip4" for="addressToShip4">
										<dsp:input type="radio" bean="ProfileFormHandler.billAddrValue.newNickname" id="addressToShip4" name="addressToShip" value="newBillingAddress" iclass="newAddOpt required radio" checked="${radiobtn}">
											<dsp:tagAttribute name="aria-checked" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4 erroraddressToShip4"/>
										</dsp:input>
										<span></span>
										${add_new_billing_addr_radio}
									</label>
								</div>
								<div class="small-12 large-6 columns new-billing-address <c:if test="${not radiobtn}">hidden</c:if>">
									<div class="row">
										<div class="small-12 large-6 columns">
											<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.firstName" name="firstName" maxlength="30" iclass="required"	id="firstName" required="${requiredTrueText}" >
												<dsp:tagAttribute name="placeholder" value="${first_name} *"/>
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
											</dsp:input>
										</div>
										<div class="small-12 large-6 columns">
											<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.lastName" maxlength="30" name="lastName" iclass="required" id="lastName" required="${requiredTrueText}">
												<dsp:tagAttribute name="placeholder" value="${last_name} *"/>
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
											</dsp:input>
										</div>
										<div class="small-12 columns">
											<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.companyName" maxlength="20" id="companyName" name="companyName">
												<dsp:tagAttribute name="placeholder" value="${company}"/>
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblcompanyName errorcompanyName"/>
											</dsp:input>
										</div>
										<div class="small-12 columns">
											<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.address1" maxlength="30" id="shippingAddress1" name="shippingAddress1">
												<dsp:tagAttribute name="placeholder" value="${lbl_address1} *"/>
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblshippingAddress1 errorshippingAddress1"/>
											</dsp:input>
										</div>
										<div class="small-12 columns">
											<dsp:input type="text" 	bean="ProfileFormHandler.billAddrValue.address2" maxlength="30" id="shippingAddress2" name="shippingAddress2">
												<dsp:tagAttribute name="placeholder" value="${lbl_address2}"/>
												<dsp:tagAttribute name="aria-required" value="false"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblshippingAddress2 errorshippingAddress2"/>
											</dsp:input>
										</div>
										<div class="small-12 columns">
											<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.city" name="cityName" maxlength="25" iclass="required" required="${requiredTrueText}" id="cityName">
												<dsp:tagAttribute name="placeholder" value="${city} *"/>
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
											</dsp:input>
										</div>
										<div class="small-12 large-6 columns">
											<dsp:select bean="ProfileFormHandler.billAddrValue.state" name="stateName" id="stateName" iclass="uniform">
												<dsp:option><bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" /></dsp:option>
												<dsp:droplet name="BBBPopulateStatesDroplet">
											<dsp:param name="NoShowUSTerr" value="noShowOnBilling" />
											<dsp:oparam name="output">
																																											
											<dsp:droplet name="ForEach">
												<dsp:param name="array" param="states" />
												<dsp:param name="elementName" value="statelist" />
												<dsp:oparam name="output">
													<dsp:getvalueof id="stateName" param="statelist.stateName" />
													<dsp:getvalueof id="stateCode" param="statelist.stateCode" />
													<dsp:option value="${stateCode}">
														<c:out value="${stateName}" />
													</dsp:option>
												</dsp:oparam>
											</dsp:droplet>
										   </dsp:oparam>
										</dsp:droplet>
												<dsp:tagAttribute name="aria-required" value="true"/>
												<dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
											</dsp:select>
											<dsp:input bean="ProfileFormHandler.billAddrValue.country"	value="${defaultCountry}" id="country" type="hidden" />
										</div>
										<div class="small-12 large-6 columns">
											<c:choose>
												<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
													<dsp:input bean="ProfileFormHandler.billAddrValue.postalCode" name="zipCA" id="zipCode" type="text" >
														<dsp:tagAttribute name="placeholder" value="${zip} *"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblzipCode errorzipCode"/>
													</dsp:input>
												</c:when>
												<c:otherwise>
													<dsp:input bean="ProfileFormHandler.billAddrValue.postalCode" name="zipUS" id="zipCode" type="text" >
														<dsp:tagAttribute name="placeholder" value="${zip} *"/>
														<dsp:tagAttribute name="aria-required" value="true"/>
														<dsp:tagAttribute name="aria-labelledby" value="lblzipCode errorzipCode"/>
													</dsp:input>
												</c:otherwise>
											</c:choose>
										</div>
										<div class="small-12 columns">
											<label class="inline-rc checkbox" id="lblnewBillingAdd" for="newBillingAdd">
												<dsp:input type="checkbox" bean="ProfileFormHandler.makePreferredSet" name="newBillingAdd" id="newBillingAdd" checked="true" disabled="${mkprefaddrDisable}" value="true">
													<dsp:tagAttribute name="aria-checked" value="true"/>
													<dsp:tagAttribute name="aria-labelledby" value="lblnewBillingAdd errornewBillingAdd"/>
												</dsp:input>
												<span></span>
												${make_preferred_billing}
											</label>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="small-12 columns">
							<dsp:input type="submit" value="Save" id="submitSaveBtnCreditCard" bean="ProfileFormHandler.createCreditCard" iclass="small button transactional hidden">
								<dsp:tagAttribute name="aria-pressed" value="false"/>
								<dsp:tagAttribute name="aria-labelledby" value="submitSaveBtnCreditCard"/>
							</dsp:input>
							<a href="#" class="small button transactional save-cc">Save</a>
							<a href="view_credit_card.jsp" class="small button secondary">Cancel</a>
						</div>
					</dsp:form>
				</div>
			</div>
		</div>

		<dsp:include page="/_includes/modals/qasModal.jsp" />

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				$('#addCreditCard').on('submit', function(){
					if (typeof acctUpdate === "function") {
						acctUpdate('added new card info');
					}
				});
			</script>
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
				s.pageName ='My Account>Add Credit Cards';
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
