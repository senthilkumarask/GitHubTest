<dsp:page>

	<%-- Imports --%>
	<dsp:importbean var="ProfileFormHandler" bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/userprofiling/Profile" />
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
	<dsp:importbean bean="/atg/dynamo/droplet/Compare" />
	<dsp:importbean bean="/atg/multisite/Site" />
	<dsp:importbean bean="/com/bbb/commerce/giftregistry/droplet/GiftRegistryFlyoutDroplet"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>

	<%-- Variables --%>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />
	<c:set var="section" value="accounts" scope="request" />
	<c:set var="pageWrapper" value="addressBook myAccount" scope="request" />
	<c:set var="enter_new_address_heading">
		<bbbl:label key="lbl_enter_new_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="edit_address_heading">
		<bbbl:label key="lbl_edit_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="update_address_heading">
		<bbbl:label key="lbl_update_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<c:set var="remove_address_heading">
		<bbbl:label key="lbl_remove_address_heading" language="${pageContext.request.locale.language}" />
	</c:set>
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<c:set var="TBS_BedBathCanadaSite">
		<bbbc:config key="TBS_BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
	</c:set>
	<c:set var="qasAddrLength" scope="request"><bbbc:config key="qas_address_length" configName="QASKeys"/></c:set>

	<dsp:getvalueof var="isOrder" value="true" />
	<dsp:getvalueof var="isRegistry" value="true" />
	
	<c:set var="poBoxEnabled" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
	<c:choose>
		<c:when test="${not empty poBoxEnabled &&  poBoxEnabled}">
			<c:set var="iclassValue" value=""/>
		</c:when>
		<c:otherwise>
			<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
		</c:otherwise>
	</c:choose>

	<dsp:droplet name="GiftRegistryFlyoutDroplet">
		<dsp:param name="profile" bean="Profile" />
		<dsp:oparam name="output">
		<dsp:droplet name="Switch">
			<dsp:param name="value" param="userStatus" />
					<dsp:oparam name="2">
						<dsp:getvalueof var="isRegistry" value="false" />
					</dsp:oparam>
			</dsp:droplet>
		</dsp:oparam>
	</dsp:droplet>
	<dsp:droplet name="OrderLookup">
		<dsp:param name="userId" bean="Profile.id"/>
		<dsp:param name="state" value="open"/>
		<dsp:oparam name="empty">
			<dsp:getvalueof var="isOrder" param="false" />
		</dsp:oparam>
	</dsp:droplet>
	<dsp:getvalueof var="contextPath" bean="/OriginatingRequest.contextPath" />

	<bbb:pageContainer index="false" follow="false">

		<jsp:attribute name="bodyClass">address-book</jsp:attribute>
		<jsp:attribute name="section">${section}</jsp:attribute>
		<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>

		<jsp:body>

			<div class="row" id="content">
				<div class="small-12 columns">
					<h1><bbbl:label key="lbl_personalinfo_myaccount" language="${pageContext.request.locale.language}" />: <span class="subheader"><bbbl:label key="lbl_addressbook_addressbooklabel" language="${pageContext.request.locale.language}" /></span></h1>
				</div>
				<div class="show-for-medium-down small-12">
					<a class="right-off-canvas-toggle secondary expand button">Account Menu</a>
				</div>
				<div class="large-3 columns small-medium-right-off-canvas-menu left-nav">
					<c:import url="/account/left_nav.jsp">
						<c:param name="currentPage">
							<bbbl:label key="lbl_myaccount_address_book" language="${pageContext.request.locale.language}" />
						</c:param>
					</c:import>
				</div>
				<div class="small-12 large-9 columns">
					<div class="row">
						<div class="small-12 columns">
							<p>
								<bbbt:textArea key="txt_addressbook_addressbooktxt" language="${pageContext.request.locale.language}" />
							</p>
						</div>
						<div>
							<dsp:include page="/global/gadgets/errorMessage.jsp">
								<dsp:param name="formhandler" bean="ProfileFormHandler" />
							</dsp:include>
						</div>
						<div class="small-12 columns">
							<c:set var="lbl_addressbook_addnewaddress">
								<bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
							</c:set>
							<a class="addAddressEntry small button primary" href="#addEntry" title="${lbl_addressbook_addnewaddress}">
								<bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
							</a>
						</div>
					</div>
					<div class="row">
						<c:set var="chkboxdisabled" value="true" />
						<dsp:droplet name="MapToArrayDefaultFirst">
							<dsp:param name="defaultId" bean="Profile.shippingAddress.repositoryId" />
							<dsp:param name="defaultId2" bean="Profile.billingAddress.repositoryId" />
							<dsp:param name="map" bean="Profile.secondaryAddresses" />
							<dsp:param name="sortByKeys" value="true" />
							<dsp:oparam name="output">
								
								<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray" />
								<dsp:getvalueof var="arraySize"  param="sortedArraySize" />
								
								<c:choose>
									<c:when test="${empty sortedArray}">
										<dsp:setvalue bean="ProfileFormHandler.useShippingAddressAsDefault" value="true" />
										<dsp:setvalue bean="ProfileFormHandler.useBillingAddressAsDefault" value="true" />
										<c:set var="chkboxdisabled" value="true" />
									</c:when>
									<c:otherwise>
										<dsp:form id="cardUpdate" action="address_book.jsp">
											<c:set var="counter" value="0" />
												<c:set var="chkboxdisabled" value="false" />
												<dsp:setvalue bean="ProfileFormHandler.useShippingAddressAsDefault" value="false" />
												<dsp:setvalue bean="ProfileFormHandler.useBillingAddressAsDefault" value="false" />
																			
											<c:set var="idcount" value="0" />
											
											<c:forEach var="shippingAddress" items="${sortedArray}">
												<c:set var="id_count" value="${id_count + 1}" />
												<dsp:setvalue param="shippingAddress" value="${shippingAddress}" />
												<c:if test="${not empty shippingAddress}">
													<dsp:droplet name="Compare">
														<dsp:param name="obj1" bean="Profile.billingAddress.repositoryId" />
														<dsp:param name="obj2" param="shippingAddress.value.id" />
														<dsp:oparam name="equal">
															<c:set var="bill_count" value="true"/>
														</dsp:oparam>
													</dsp:droplet>
												</c:if>
											</c:forEach>	
										
											<c:forEach var="shippingAddress" items="${sortedArray}">
												<c:set var="idcount" value="${idcount + 1}" />
												<dsp:setvalue param="shippingAddress" value="${shippingAddress}" />
												<c:if test="${not empty shippingAddress}">
													<c:set var="count" value="0" />
													<dsp:droplet name="Compare">
														<dsp:param name="obj1" bean="Profile.shippingAddress.repositoryId" />
														<dsp:param name="obj2" param="shippingAddress.value.id" />
														<dsp:oparam name="equal">
															<c:set var="isDefault" value="true" />
														</dsp:oparam>
														<dsp:oparam name="default">
															<c:choose>
															<c:when test="${arraySize == 1}">
																<c:set var="isDefault" value="true" />
															</c:when>
															<c:otherwise>
																<c:set var="isDefault" value="false" />
															</c:otherwise>
															</c:choose>
														</dsp:oparam>
													</dsp:droplet>
													<dsp:droplet name="Compare">
														<dsp:param name="obj1" bean="Profile.billingAddress.repositoryId" />
														<dsp:param name="obj2" param="shippingAddress.value.id" />
														<dsp:oparam name="equal">
															<c:set var="isBillDefault" value="true" />
														</dsp:oparam>
														<dsp:oparam name="default">
															<c:choose>
															<c:when test="${arraySize == 1}">
																<c:set var="isBillDefault" value="true" />
															</c:when>
															<c:otherwise>
																<c:set var="isBillDefault" value="false" />
															</c:otherwise>
															</c:choose>
														</dsp:oparam>
													</dsp:droplet>

													<%-- Display Address Details --%>
													<div class="small-12 large-6 columns">
														<div id="addressEntry-${idcount}" class="my-account-section ${isDefault? 'isPreferredShipping': ''} ${isBillDefault? 'isPreferredBilling': ''}">
															<c:set var="counter" value="${counter + 1}" />
															<dl>
																<dt>Shipping</dt>
																<dd>
																	<c:choose>
																		<c:when test="${isDefault}">
																			<bbbl:label key="lbl_addressbook_preferredaddress" language="${pageContext.request.locale.language}" />
																		</c:when>
																		<c:when test="${not isDefault and idcount eq 1}">
																			<bbbl:label key="lbl_addressbook_preferredaddress" language="${pageContext.request.locale.language}" />
																		</c:when>
																		<c:otherwise>
																			<c:set var="lbl_addressbook_makepreferredshipping">
																				<bbbl:label key="lbl_addressbook_makepreferredshipping" language="${pageContext.request.locale.language}" />
																			</c:set>
																			<dsp:a bean="ProfileFormHandler.defaultShippingAddress" title="${lbl_addressbook_makepreferredshipping}" href="${contextPath}/account/address_book.jsp" paramvalue="shippingAddress.key">
																				<bbbl:label key="lbl_addressbook_makepreferredshipping"  language="${pageContext.request.locale.language}" />
																			</dsp:a>
																		</c:otherwise>
																	</c:choose>
																</dd>
																<dt>Billing</dt>
																<dd>
																	<c:choose>
																		<c:when test="${isBillDefault}">
																			<bbbl:label key="lbl_addressbook_preferredbiiling" language="${pageContext.request.locale.language}" />
																		</c:when>
																		<c:when test="${not isBillDefault and not bill_count and idcount eq 1}">
																			<bbbl:label key="lbl_addressbook_preferredbiiling" language="${pageContext.request.locale.language}" />	
																		</c:when>
																		<c:otherwise>
																			<c:set var="lbl_addcreditcard_preferredbilling">
																				<bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
																			</c:set>
																			<dsp:a bean="ProfileFormHandler.defaultBillingAddress" title="${lbl_addcreditcard_preferredbilling}" href="${contextPath}/account/address_book.jsp" paramvalue="shippingAddress.key">
																				<bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
																			</dsp:a>
																		</c:otherwise>
																	</c:choose>
																</dd>
															</dl>

															<dsp:include page="displayAddress.jsp" flush="false">
																<dsp:param name="address" param="shippingAddress.value" />
																<dsp:param name="private" value="false" />
															</dsp:include>

															<c:set var="lbl_addressbook_edit">
																<bbbl:label key="lbl_addressbook_edit" language="${pageContext.request.locale.language}" />
															</c:set>
															<dsp:a iclass="editAddressEntry" href="#editEntry" title="${lbl_addressbook_edit}" paramvalue="shippingAddress.key">
																<bbbl:label key="lbl_addressbook_edit" language="${pageContext.request.locale.language}" />
															</dsp:a>
															<c:set var="lbl_addressbook_remove">
																<bbbl:label key="lbl_addressbook_remove" language="${pageContext.request.locale.language}" />
															</c:set>
															<dsp:a iclass="removeAddressEntry right" title="${lbl_addressbook_remove}" href="#removeEntry" paramvalue="shippingAddress.key">
																<bbbl:label key="lbl_addressbook_remove" language="${pageContext.request.locale.language}" />
															</dsp:a>
															<dsp:input iclass="cardId" name="cardId" type="hidden" bean="ProfileFormHandler.editValue.nickname" paramvalue="shippingAddress.key" />
															<dsp:input iclass="defaultShippingAddress" name="defaultShippingAddress" type="hidden" bean="ProfileFormHandler.useShippingAddressAsDefault" value="${isDefault}" />
															<dsp:input iclass="defaultBillingAddress" name="defaultBillingAddress" type="hidden" bean="ProfileFormHandler.useBillingAddressAsDefault" value="${isBillDefault}" />
														</div>
													</div>
												</c:if>
											</c:forEach>
										</dsp:form>
									</c:otherwise>
								</c:choose>
							</dsp:oparam>
						</dsp:droplet>
					</div>
					<%-- <div class="row">
						<div class="small-12 columns">
							<c:set var="lbl_addressbook_addnewaddress">
								<bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
							</c:set>
                            <a class="addAddressEntry small button primary" href="#addEntry" title="${lbl_addressbook_addnewaddress}">
                                <bbbl:label key="lbl_addressbook_addnewaddress" language="${pageContext.request.locale.language}" />
                            </a>
						</div>
					</div> --%>
				</div>
			</div>

			<%-- modals --%>
			<div id="addAddressModal" class="reveal-modal medium" data-reveal>
				<div class="row">
					<div class="small-12 large-6 columns">
						<h1>${enter_new_address_heading}</h1>
					</div>
				</div>
				<dsp:form id="addAddressForm" formid="addAddressForm" action="address_book.jsp" method="post">
					<div class="row">
						<div class="small-12 large-6 columns">
							<div class="row">
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_firstname" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:getvalueof bean="ProfileFormHandler.value.firstName" id="firstName">
										<dsp:input type="text" bean="ProfileFormHandler.editValue.firstName" name="txtAddressBookAddFirstNameName" maxlength="30" id="txtAddressBookAddFirstName" value="${firstName}">
											<dsp:tagAttribute name="placeholder" value="First Name *"/>
											<dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddFirstName errortxtAddressBookAddFirstName"/>
										</dsp:input>
									</dsp:getvalueof>
									<input type="hidden" name="hiddentxtAddressBookAddFirstNameName" value="${firstName}" data-prefill-elem="txtAddressBookAddFirstName">
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_lastname" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:getvalueof bean="ProfileFormHandler.value.lastName" id="lastName">
										<dsp:input type="text" bean="ProfileFormHandler.editValue.lastName" name="txtAddressBookAddLastNameName" maxlength="30" id="txtAddressBookAddLastName" value="${lastName}">
											<dsp:tagAttribute name="placeholder" value="Last Name *"/>
											<dsp:tagAttribute name="aria-required" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddLastName errortxtAddressBookAddLastName"/>
										</dsp:input>
									</dsp:getvalueof>
									<input type="hidden" name="hiddentxtAddressBookAddLastNameName" value="${lastName}" data-prefill-elem="txtAddressBookAddLastName">
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_company" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.companyName" name="txtAddressBookAddCompanyName" maxlength="20" value="" id="txtAddressBookAddCompany" >
										<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddCompany errortxtAddressBookAddCompany"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<label class="inline-rc checkbox" id="lblcbAddressBookAddPreferredShipping" for="cbAddressBookAddPreferredShipping">
										<dsp:input type="checkbox"  checked="checked" bean="ProfileFormHandler.useShippingAddressAsDefault" name="cbAddressBookAddPreferredShipping" id="cbAddressBookAddPreferredShipping" disabled="${chkboxdisabled}">
											<dsp:tagAttribute name="aria-checked" value="false"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookAddPreferredShipping errorcbAddressBookAddPreferredShipping"/>
										</dsp:input>
										<c:if test="${chkboxdisabled == true}">
											<dsp:input type="hidden" bean="ProfileFormHandler.useShippingAddressAsDefault" name="cbAddressBookAddPreferredShipping" id="cbAddressBookAddPreferredShipping" />
										</c:if>
										<span></span>
										<bbbl:label key="lbl_profile_makePreferredShipAddress" language="${pageContext.request.locale.language}" />
									</label>
								</div>
								<div class="small-12 columns">
									<label class="inline-rc checkbox"  id="lblcbAddressBookAddPreferredBilling" for="cbAddressBookAddPreferredBilling">
										<dsp:input type="checkbox" checked="checked" bean="ProfileFormHandler.useBillingAddressAsDefault" name="cbAddressBookAddPreferredBilling" id="cbAddressBookAddPreferredBilling" disabled="${chkboxdisabled}" >
											<dsp:tagAttribute name="aria-checked" value="false"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookAddPreferredBilling errorcbAddressBookAddPreferredBilling"/>
										</dsp:input>
										<c:if test="${chkboxdisabled == true}">
											<dsp:input type="hidden" bean="ProfileFormHandler.useBillingAddressAsDefault" name="cbAddressBookAddPreferredBilling" id="cbAddressBookAddPreferredBilling" />
										</c:if>
										<span></span>
										<bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
									</label>
								</div>
							</div>
						</div>
						<div class="small-12 large-6 columns">
							<div class="row">
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_address1" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" iclass="${iclassValue}" bean="ProfileFormHandler.editValue.address1" name="txtAddressBookAddAddress1Name" maxlength="${qasAddrLength}" value="" id="txtAddressBookAddAddress1">
										<dsp:tagAttribute name="placeholder" value="Address *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddAddress1 errortxtAddressBookAddAddress1"/>
										<dsp:tagAttribute name="autocomplete" value="off"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_address2" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input iclass="${iclassValue}" type="text" bean="ProfileFormHandler.editValue.address2" name="txtAddressBookAddAddress2Name" maxlength="${qasAddrLength}" value="" id="txtAddressBookAddAddress2" >
										<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddAddress2 errortxtAddressBookAddAddress2"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_city" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.city" maxlength="25" value="" id="txtAddressBookAddCity" name="txtAddressBookAddCityName" >
										<dsp:tagAttribute name="placeholder" value="City *"/>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddCity errortxtAddressBookAddCity"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<dsp:select bean="ProfileFormHandler.editValue.state" name="selAddressBookAddStateName" id="selAddressBookAddState" iclass="uniform">
										<dsp:droplet name="StateDroplet">
										<dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
											<dsp:oparam name="output">
												<c:choose>
													<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
												<dsp:option value="">
															Select <bbbl:label key="lbl_shipping_new_province" language="${pageContext.request.locale.language}"></bbbl:label>
														</dsp:option>
													</c:when>
													<c:otherwise>
														<dsp:option value="">
													<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
												</dsp:option>
													</c:otherwise>
												</c:choose>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="location" />
													<dsp:param name="sortProperties" value="+stateName" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="element.stateName" id="elementVal">
														<dsp:getvalueof param="element.stateCode" id="elementCode">
															<dsp:option value="${elementCode}">
																${elementVal}
															</dsp:option>
														</dsp:getvalueof>
														</dsp:getvalueof>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblselAddressBookAddState errorselAddressBookAddState"/>
									</dsp:select>
								</div>
								<div class="small-12 columns">
									<c:choose>
										<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
											<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_postal_code" language="${pageContext.request.locale.language}"/></c:set>
											<c:set var="zipCodeName" value="zipCA"/>
											<c:set var="maxLengthValue" value="7"/>
										</c:when>
										<c:otherwise>
											<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_zip" language="${pageContext.request.locale.language}" /></c:set>
											<c:set var="zipCodeName" value="zipUS"/>
											<c:set var="maxLengthValue" value="10"/>
										</c:otherwise>
									</c:choose>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.postalCode" name="${zipCodeName}" value="" id="txtAddressBookAddZip" maxlength="${maxLengthValue}">
										<dsp:tagAttribute name="placeholder" value="${placeholder} *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookAddZip errortxtAddressBookAddZip"/>
									</dsp:input>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="small-12 columns">
							<dsp:input bean="ProfileFormHandler.newAddress" id="newAddressButton" type="Submit" value="ADD ADDRESS" iclass="btnSubmit small button transactional" >
								<dsp:tagAttribute name="aria-pressed" value="false"/>
								<dsp:tagAttribute name="aria-labelledby" value="newAddressButton"/>
								<dsp:tagAttribute name="role" value="button"/>
							</dsp:input>
							<a href="#" class="close-modal small button secondary">Cancel</a>
						</div>
					</div>
				</dsp:form>
				<a class="close-reveal-modal">&#215;</a>
			</div>

			<div id="editAddressModal" class="reveal-modal medium" data-reveal>
				<h1>${edit_address_heading}</h1>
				<dsp:getvalueof var="defaultAddressId" bean="Profile.shippingAddress.repositoryId" />
				<dsp:getvalueof var="currentAddressId" param="currentAddressId" />
				<dsp:form id="editAddressForm" action="address_book.jsp" method="post">
					<div class="row">
						<div class="small-12 large-6 columns">
							<div class="row">
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_firstname" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.firstName" name="txtAddressBookEditFirstNameName" maxlength="30" id="txtAddressBookEditFirstName" >
										<dsp:tagAttribute name="placeholder" value="First Name *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditFirstName errortxtAddressBookEditFirstName"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_lastname" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.lastName" name="txtAddressBookEditLastNameName" maxlength="30" id="txtAddressBookEditLastName" >
										<dsp:tagAttribute name="placeholder" value="Last Name *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditLastName errortxtAddressBookEditLastName"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_company" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.companyName" name="txtAddressBookEditCompanyName" maxlength="20" id="txtAddressBookEditCompany" >
										<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditCompany errortxtAddressBookEditCompany"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<label class="inline-rc checkbox" id="lblcbAddressBookEditPreferredShipping" for="cbAddressBookEditPreferredShipping">
										<dsp:input type="checkbox" checked="checked" bean="ProfileFormHandler.useShippingAddressAsDefault" name="useShippingAddressAsDefault" id="cbAddressBookEditPreferredShipping" >
											<dsp:tagAttribute name="aria-checked" value="false"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookEditPreferredShipping errorcbAddressBookEditPreferredShipping"/>
										</dsp:input>
										<span></span>
										<bbbl:label key="lbl_profile_makePreferredShipAddress" language="${pageContext.request.locale.language}" />
									</label>
								</div>
								<div class="small-12 columns">
									<label class="inline-rc checkbox" id="lblcbAddressBookEditPreferredBilling" for="cbAddressBookEditPreferredBilling">
										<dsp:input type="checkbox" checked="checked" bean="ProfileFormHandler.useBillingAddressAsDefault" name="useBillingAddressAsDefault" id="cbAddressBookEditPreferredBilling" >
											<dsp:tagAttribute name="aria-checked" value="false"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblcbAddressBookEditPreferredBilling errorcbAddressBookEditPreferredBilling"/>
										</dsp:input>
										<span></span>
										<bbbl:label key="lbl_addcreditcard_preferredbilling" language="${pageContext.request.locale.language}" />
									</label>
								</div>
							</div>
						</div>
						<div class="small-12 large-6 columns">
							<div class="row">
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_address1" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" iclass="${iclassValue}" bean="ProfileFormHandler.editValue.address1" name="txtAddressBookEditAddress1Name" maxlength="${qasAddrLength}" id="txtAddressBookEditAddress1" >
										<dsp:tagAttribute name="placeholder" value="Address *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditAddress1 errortxtAddressBookEditAddress1"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_address2" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" iclass="${iclassValue}" bean="ProfileFormHandler.editValue.address2" name="txtAddressBookEditAddress2Name" maxlength="${qasAddrLength}" id="txtAddressBookEditAddress2" >
										<dsp:tagAttribute name="placeholder" value="${placeholder}"/>
										<dsp:tagAttribute name="aria-required" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditAddress2 errortxtAddressBookEditAddress2"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_city" language="${pageContext.request.locale.language}" /></c:set>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.city" maxlength="25" value="" id="txtAddressBookEditCity" name="txtAddressBookEditCityName" >
										<dsp:tagAttribute name="placeholder" value="City *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditCity errortxtAddressBookEditCity"/>
									</dsp:input>
								</div>
								<div class="small-12 columns">
									<dsp:select bean="ProfileFormHandler.editValue.state" name="selAddressBookEditStateName" id="selAddressBookEditState" iclass="selAddressBookEditState uniform">
										<dsp:droplet name="StateDroplet">
										<dsp:param name="NoShowUSTerr" value="noShowOnRegistry" />
											<dsp:oparam name="output">
												<dsp:option value="">
														<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
													</dsp:option>
												<dsp:droplet name="ForEach">
													<dsp:param name="array" param="location" />
													<dsp:param name="sortProperties" value="+stateName" />
													<dsp:oparam name="output">
														<dsp:getvalueof param="element.stateName" id="elementVal">
														<dsp:getvalueof param="element.stateCode" id="elementCode">
															<dsp:option value="${elementCode}">
																${elementVal}
															</dsp:option>
														</dsp:getvalueof>
														</dsp:getvalueof>
													</dsp:oparam>
												</dsp:droplet>
											</dsp:oparam>
										</dsp:droplet>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lblselAddressBookEditState errorselAddressBookEditState"/>
									</dsp:select>
								</div>
								<div class="small-12 columns">
									<c:choose>
										<c:when test="${currentSiteId eq TBS_BedBathCanadaSite}">
											<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_postal_code" language="${pageContext.request.locale.language}"/></c:set>
											<c:set var="zipCodeName" value="zipCA"/>
											<c:set var="maxLengthValue" value="7"/>
										</c:when>
										<c:otherwise>
											<c:set var="placeholder"><bbbl:label key="lbl_addcreditcard_zip" language="${pageContext.request.locale.language}" /></c:set>
											<c:set var="zipCodeName" value="zipUS"/>
											<c:set var="maxLengthValue" value="10"/>
										</c:otherwise>
									</c:choose>
									<dsp:input type="text" bean="ProfileFormHandler.editValue.postalCode" name="${zipCodeName}" value="" id="txtAddressBookEditZip" maxlength="${maxLengthValue}">
										<dsp:tagAttribute name="placeholder" value="Zip Code *"/>
										<dsp:tagAttribute name="aria-required" value="true"/>
										<dsp:tagAttribute name="aria-labelledby" value="lbltxtAddressBookEditZip errortxtAddressBookEditZip"/>
									</dsp:input>
								</div>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="small-12 columns">
							<dsp:input iclass="cardId" bean="ProfileFormHandler.editValue.nickname" name="txtAddressBookEditCardId" id="txtAddressBookEditCardId" type="hidden" value="" />
							<dsp:input bean="ProfileFormHandler.updateAddress" type="hidden" value="SAVE CHANGES" />
							<c:choose>
								<c:when test="${isOrder=='true' || isRegistry == 'true'}">
									<dsp:input bean="ProfileFormHandler.updateAddress" id="personalInfoBtnReg" type="Submit" value="SAVE CHANGES" iclass="btnSubmit small button transactional" >
										<dsp:tagAttribute name="aria-pressed" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="personalInfoBtnReg"/>
										<dsp:tagAttribute name="role" value="button"/>
									</dsp:input>
								</c:when>
								<c:otherwise>
									<dsp:input bean="ProfileFormHandler.updateAddress" id="personalInfoBtn" type="Submit" value="SAVE CHANGES" iclass="btnSubmit small button transactional" >
										<dsp:tagAttribute name="aria-pressed" value="false"/>
										<dsp:tagAttribute name="aria-labelledby" value="personalInfoBtn"/>
										<dsp:tagAttribute name="role" value="button"/>
									</dsp:input>
								</c:otherwise>
							</c:choose>
							<a href="#" class="close-modal small button secondary">Cancel</a>
						</div>
					</div>
				</dsp:form>
				<a class="close-reveal-modal">&#215;</a>
			</div>

			<div id="updateAddressWarningModal" class="reveal-modal medium" data-reveal>
				<h1>${update_address_heading}</h1>
				<p><bbbt:textArea key="txt_addressbook_updatetxt" language="${pageContext.request.locale.language}" /></p>
				<a class="update-address-ok small button transactional">OK</a>
				<dsp:a href="${contextPath}/giftregistry/my_registries.jsp" iclass="small button secondary">Go To My Registries</dsp:a>
				<a class="close-reveal-modal">&#215;</a>
			</div>

			<div id="removeAddressWarningModal" class="reveal-modal medium" data-reveal>
				<h1>${remove_address_heading}</h1>
				<p><bbbt:textArea key="txt_addressbook_removetxt" language="${pageContext.request.locale.language}" /></p>
				<dsp:form id="addressRemoveForm" action="address_book.jsp" method="post">
					<dsp:input id="defaultShippingAddressCard" bean="ProfileFormHandler.useShippingAddressAsDefault" type="hidden" />
					<dsp:input id="defaultBillingAddressCard" bean="ProfileFormHandler.useBillingAddressAsDefault" type="hidden" />
					<dsp:input iclass="cardId" bean="ProfileFormHandler.addressId" id="cardId" type="hidden" value="" />
					<dsp:input bean="ProfileFormHandler.removeAddress" id="removeBtn" type="Submit" value="OK" iclass="small button transactional">
						<dsp:tagAttribute name="aria-pressed" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="removeBtn"/>
						<dsp:tagAttribute name="role" value="button"/>
					</dsp:input>
					<a href="#" class="close-modal small button secondary">Cancel</a>
				</dsp:form>
				<a class="close-reveal-modal">&#215;</a>
			</div>

			<%-- QAS Modal --%>
			<dsp:include page="/_includes/modals/qasModal.jsp" />

			<dsp:getvalueof bean="ProfileFormHandler.addressAdded" id="addr" />
			<script type="text/javascript">
				$('#addAddressForm').on('submit', function(){
					if (typeof acctUpdate === "function") {
						acctUpdate('added new address');
					}
				});
				$('#addressRemoveForm').on('submit', function(){
					if (typeof acctUpdate === "function") {
						acctUpdate('removed saved address');
					}
				});
				$('#editAddressForm').on('submit', function(){
					if (typeof acctUpdate === "function") {
						acctUpdate('saved address info updated');
					}
				});
			</script>

		</jsp:body>

		<jsp:attribute name="footerContent">
			<script type="text/javascript">
				if (typeof s !== 'undefined') {
				s.pageName ='My Account>Address Book';
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
