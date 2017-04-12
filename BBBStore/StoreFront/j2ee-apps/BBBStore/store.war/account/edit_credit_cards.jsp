<dsp:page>
	<c:set var="pageWrapper" value="creditCard myAccount" scope="request" />
	<bbb:pageContainer section="accounts" bodyClass="${themeName}" index="false" follow="false" >
	<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler" />
	<dsp:importbean bean="/atg/store/order/purchase/BillingFormHandler" />
	<dsp:importbean bean="atg/userprofiling/Profile" />
	<dsp:importbean bean="/atg/dynamo/droplet/Compare" />
	<dsp:importbean bean="/atg/commerce/util/MapToArrayDefaultFirst" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof id="currentSiteId" bean="Site.id" />
	<jsp:attribute name="pageWrapper">${pageWrapper}</jsp:attribute>
	
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
		<bbbl:label key="lbl_choose_cardtype" language="${pageContext.request.locale.language}" />
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
	<c:set var="edit_cc" scope="page">
		<bbbl:label key="lbl_editcreditcard_edittitle" language="${pageContext.request.locale.language}" />
	</c:set>

	
    <div id="content" class="container_12 clearfix" role="main">
        <div class="grid_12">
            <h1 class="account fl"><bbbl:label key="lbl_personalinfo_myaccount" language ="${pageContext.request.locale.language}"/></h1>
            <h3 class="subtitle"><bbbl:label key="lbl_header_creditcards" language="${pageContext.request.locale.language}" /></h3>
            <div class="clear"></div>
        </div>
        <div class="grid_2">
			<c:import url="/account/left_nav.jsp"/>
        </div>
        <div class="grid_8 prefix_1 suffix_1">
			<h3>${edit_cc}</h3>
			<div class="suffix_2 descWishList marBottom_20">
				${add_credit_card_title}
			</div>
			
			<div class="addCard">
				<div class="highlightSection">
					<dsp:form id="addCreditCard" name="form" method="post">
					
					<dsp:include page="/global/gadgets/errorMessage.jsp">
						<dsp:param name="formhandler" bean="ProfileFormHandler"/>
					</dsp:include>
					
						<fieldset>
						
							<div class="input text grid_3 alpha">
								<div class="label">
									<label id="lblcardType" for="cardType">${card_type}</label>
									<dsp:getvalueof var="err_cc_type_empty" bean="ProfileFormHandler.errorMap.creditCardType"></dsp:getvalueof>
								</div>	
								<div class="select">
									<dsp:select id="cardType" bean="ProfileFormHandler.editValue.creditCardType" name="cardType" iclass="uniform">
										<dsp:option value="">
											<%-- Use an empty value to trip required error handling --%>${chooseCardType}</dsp:option>
											<%-- <dsp:option value="${lbl_creditcardinfo_visa}">${lbl_creditcardinfo_visa}</dsp:option>
										<dsp:option value="${lbl_creditcardinfo_mastercard}">${lbl_creditcardinfo_mastercard}</dsp:option>
										<dsp:option value="${lbl_creditcardinfo_americanexpress}">${lbl_creditcardinfo_americanexpress}</dsp:option>
										<dsp:option value="${lbl_creditcardinfo_discover}">${lbl_creditcardinfo_discover}</dsp:option> --%>
										<dsp:droplet name="/atg/dynamo/droplet/ForEach">
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
									<c:if test="${not empty err_cc_type_empty}">
										<label id="errorcardType" class="error" for="firstName"><bbbe:error key="${err_cc_type_empty}" language="${pageContext.request.locale.language}"/></label>
									</c:if>
								</div>
							</div>

							<div class="cardType grid_3 suffix_2 omega">
								<dsp:droplet name="/atg/dynamo/droplet/ForEach">
									<dsp:param name="array" bean="ProfileFormHandler.creditCardTypes" />
									<dsp:param name="elementName" value="cardlist" />
									<dsp:oparam name="output">
										<dsp:getvalueof id="cardImage" param="cardlist.imageURL" />
										<dsp:getvalueof id="cardName" param="cardlist.name" />
										<img class="creditCardIcon" src="${imagePath}${cardImage}" width="41" height="26" alt="${cardName}" title="${cardName}" />	
									</dsp:oparam>
								</dsp:droplet>
							</div>
							
							<div class="input text grid_8 alpha">
								<div class="input grid_3 alpha">
									<dsp:getvalueof var="err_cc_number_empty" bean="ProfileFormHandler.errorMap.creditCardNumber"></dsp:getvalueof>
									<dsp:getvalueof var="err_cc_number_validation" bean="ProfileFormHandler.errorMap.creditCardValidation"></dsp:getvalueof>
                                    <div class="label">
                                        <label id="lblcredit" for="credit">${card_number}</label>
                                    </div>
                                    <div class="text width_3">
                                        <dsp:input type="hidden" bean="ProfileFormHandler.editValue.nickname" name="nickname" />
                                        <dsp:input type="text" iclass="required" name="cardNumber" id="credit" converter="creditCard" numcharsunmasked="4"	bean="ProfileFormHandler.editValue.creditCardNumber" maxlength="16">
                                            <dsp:tagAttribute name="aria-required" value="false"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lblcredit errorcredit"/>
                                        </dsp:input>
                                        <%-- sldkhfhsd -> ${err_cc_number_empty} --%>
                                    </div>
									<c:if test="${not empty err_cc_number_empty}">
                                    <div class="label">
										<label id="errorcredit" class="error" for="firstName"><bbbe:error key="${err_cc_number_empty}" language="${pageContext.request.locale.language}"/></label>
                                    <div>
									</c:if>
									<c:if test="${not empty err_cc_number_validation}">
										<label id="errorcredit" class="error" for="firstName"><bbbe:error key="${err_cc_number_validation}" language="${pageContext.request.locale.language}"/></label>
									</c:if>
								</div>
							</div>	
							<div class="expiryDate input text grid_8 alpha">
								<div class="label">
									<dsp:getvalueof var="cc_exp_month_empty" bean="ProfileFormHandler.errorMap.err_expirationMonth"></dsp:getvalueof>
									<dsp:getvalueof var="cc_exp_year_empty" bean="ProfileFormHandler.errorMap.err_expirationYear"></dsp:getvalueof>
									<label id="lblcreditCardMonth" for="creditCardMonth"> ${expires_on} </label>
								</div>
								<div class="select grid_2 marRight_20">
									<dsp:getvalueof var="err_cc_exp_year_empty"	bean="ProfileFormHandler.errorMap.err_cc_state_match"></dsp:getvalueof>
									<dsp:select bean="ProfileFormHandler.editValue.expirationMonth" name="creditCardMonth" iclass="uniform" id="creditCardMonth">
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
								<div class="select grid_2 suffix_4">
									
									<jsp:useBean id="now" class="java.util.Date" scope="request" />
									<fmt:formatDate var="year_val" value="${now}" pattern="yyyy" />
									<dsp:select bean="ProfileFormHandler.editValue.expirationYear" name="creditCardYear" iclass="uniform" id="creditCardYear">
										<dsp:option value="">${year}</dsp:option>
										<dsp:option value="${year_val}">${year_val}</dsp:option>
										<dsp:getvalueof var="cardMaxYear" bean="ProfileFormHandler.creditCardYearMaxLimit"></dsp:getvalueof>
										<c:forEach begin="0" end="${cardMaxYear-1}" step="1" varStatus="count">
											<dsp:option value="${year_val+count.count}">${year_val+count.count}</dsp:option>
										</c:forEach>
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="creditCardYear errorcreditCardYear"/>
									</dsp:select>
								</div>
								<c:if test="${not empty err_cc_exp_year_empty}">
									<label id="errorcreditCardYear" class="error" for="expYear"><bbbe:error key="${err_cc_exp_year_empty}" language="${pageContext.request.locale.language}"/></label>
								</c:if>
								<c:if test="${not empty err_cc_exp_month_empty}">
									<label id="errorcreditCardMonth" class="error" for="expMonth"><bbbe:error key="${err_cc_exp_month_empty}" language="${pageContext.request.locale.language}"/></label>
								</c:if>
							</div>
							
							<div class="input grid_8 alpha">
								<dsp:getvalueof var="err_cc_noc_empty" bean="ProfileFormHandler.errorMap.nameOnCard"></dsp:getvalueof>
								<dsp:getvalueof var="err_cc_noc_length_incorrect" bean="ProfileFormHandler.errorMap.lengthIncorrect"></dsp:getvalueof>
								<div class="label">
                                    <label id="lblnameCard" for="nameCard">${name_on_card}</label>
                                </div>
								<div class="text width_3">
                                    <dsp:input bean="ProfileFormHandler.editValue.nameOnCard" type="text" name="nameCard" maxlength="61" id="nameCard">
                                        <dsp:tagAttribute name="aria-required" value="false"/>
                                        <dsp:tagAttribute name="aria-labelledby" value="lblnameCard errornameCard"/>
                                    </dsp:input>
										<c:if test="${not empty err_cc_noc_empty}">
											<label id="errornameCard" class="error" for="expYear"><bbbe:error key="${err_cc_noc_empty}" language="${pageContext.request.locale.language}"/></label>
										</c:if>
										<c:if test="${not empty err_cc_noc_length_incorrect}">
											<label id="errornameCard" class="error" for="expYear"><bbbe:error key="${err_cc_noc_length_incorrect}" language="${pageContext.request.locale.language}"/></label>
										</c:if>
                                </div>
							</div>
						</fieldset>	
						<h6 class="marBottom_10">${select_billing_addr}</h6>
						
                        <fieldset class="radioGroup">
                           		<c:set var="radiobtn" value="true"/>
								<dsp:param name="defaultAddressId"	bean="Profile.billingAddress.firstName" />
								<%-- <dsp:input type="radio" name="address" bean="ProfileFormHandler.billAddrValue.newNickname"/> --%>
								<dsp:input type="hidden" name="address"	paramvalue="defaultAddressId" id="${shippingAddress.value.repositoryId}" bean="ProfileFormHandler.billAddrValue.newNickname" />
								<dsp:droplet name="MapToArrayDefaultFirst">
									<dsp:param name="map" bean="Profile.secondaryAddresses" />
									<dsp:param name="defaultId" bean="Profile.billingAddress.repositoryId" />
									<dsp:oparam name="output">
										<dsp:getvalueof var="sortedArray" vartype="java.lang.Object" param="sortedArray" />
										<c:if test="${not empty sortedArray}">
											<c:set var="radiobtn" value=""/>
											<c:set var="defaultID" value="true"/>
											<div class="billingAdd">
											<c:set var="counter" value="0" />
											<dsp:getvalueof var="defaultAddressId"	vartype="java.lang.String"	bean="Profile.billingAddress.repositoryId" />
											<c:forEach var="shippingAddress" items="${sortedArray}"	varStatus="status">
												<c:if test="${not empty shippingAddress}">
													<div class="radioItem input">
														<dsp:param name="shippingAddress" value="${shippingAddress}" />
														<c:set var="count" value="0" />
														<c:set var="counter" value="${counter + 1}" />
														<dsp:droplet name="Compare">
															<dsp:param name="obj1"	bean="Profile.billingAddress.repositoryId" />
															<dsp:param name="obj2" param="billingAddress.value.id" />
															<dsp:oparam name="equal">
																##<dsp:input type="radio" name="address" 	paramvalue="shippingAddress.key" id="${shippingAddress.value.repositoryId}"	disabled="true"	bean="ProfileFormHandler.billAddrValue.newNickname" >
                                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} error${shippingAddress.value.repositoryId}"/>
                                                                </dsp:input>
															</dsp:oparam>
															<dsp:oparam name="false">
																$$<dsp:input type="radio" name="address"	paramvalue="shippingAddress.key"	id="${shippingAddress.value.repositoryId}"	checked="true"	bean="ProfileFormHandler.billAddrValue.newNickname" >
                                                                    <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                    <dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} error${shippingAddress.value.repositoryId}"/>
                                                                </dsp:input>
															</dsp:oparam>
															<dsp:oparam name="default">
																
																<c:choose>
																	<c:when test="${shippingAddress.value.repositoryId eq defaultAddressId}">
																		<dsp:input type="radio" paramvalue="shippingAddress.key" bean="ProfileFormHandler.billAddrValue.newNickname" id="${shippingAddress.value.repositoryId}" name="addressToShip" checked="${defaultID}">
                                                                            <dsp:tagAttribute name="aria-checked" value="true"/>
                                                                            <dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} erroraddressToShip"/>
                                                                        </dsp:input>
                                                                        <%--  id="${shippingAddress.value.repositoryId} --%> <%--  disabled="true" --%>
																		<c:set var="defaultID" value="false"/>
																	</c:when>
																	<c:otherwise>
																		<dsp:input type="radio" paramvalue="shippingAddress.key" id="${shippingAddress.value.repositoryId}" bean="ProfileFormHandler.billAddrValue.newNickname" name="addressToShip" checked="${defaultID}">
                                                                            <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                            <dsp:tagAttribute name="aria-labelledby" value="lbl${shippingAddress.value.repositoryId} erroraddressToShip"/>
                                                                        </dsp:input>
																	</c:otherwise>
																</c:choose>
															</dsp:oparam>
														</dsp:droplet>
														<dsp:param name="address" param="shippingAddress.value" />
														<dsp:param name="private" value="false" />
														<dsp:getvalueof var="addressValue" param="address.country" />
														<c:choose>
															<c:when test="${addressValue == ''}" />
															<c:otherwise>
																<%-- U.S. Address format --%>
																<div class="label">
																	<label id="lbl${shippingAddress.value.repositoryId}" for="${shippingAddress.value.repositoryId}">
																	<span>
																		<dsp:valueof param="address.firstName" />
																		<dsp:valueof param="address.lastName" />
																	</span>
																	<span>
																		<dsp:valueof param="address.companyName" />
																	</span>																
																	<dsp:getvalueof var="private" param="private" />
																	<c:choose>
																		<c:when test="${private == 'true'}">
																			<%-- Do Not Display Address Details since it is private --%>
																		</c:when>
																		<c:otherwise>
																			<span>
																				<dsp:valueof param="address.address1" />
																			</span>
																			<dsp:getvalueof var="address2" param="address.address2" />
																			<c:if test="${not empty address2}">
																				<span>
																					<dsp:valueof param="address.address2" />
																				</span>	
																			</c:if>
																		</c:otherwise>
																	</c:choose>
																	<span>
																		<dsp:valueof param="address.city" />
																		<dsp:getvalueof var="state" param="address.state" />
																		<c:if test="${not empty state}">
																			<dsp:valueof param="address.state" />
																		</c:if>
																		<dsp:valueof param="address.postalCode" />
																	</span>
																	</label>
																</div>
																<div class="preferAdd">
																	<div class="input clearfix">
																		<%-- <label for="${shippingAddress.value.repositoryId}"> --%>
																		<dsp:getvalueof var="keyval" param="shippingAddress.key" />
																		<c:choose>
																			<c:when test="${shippingAddress.value.repositoryId eq defaultAddressId}">
																				<div class="checkbox fl">
																					<dsp:input type="checkbox" name="billingAdd2" bean="ProfileFormHandler.billAddrValue.makeBilling" id="billingAdd2" iclass="enableDisable" >
                                                                                        <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                                        <dsp:tagAttribute name="aria-labelledby" value="lblbillingAdd2 errorbillingAdd2"/>
                                                                                    </dsp:input>
																				</div>
																				<div class="label">
																					<label id="lblbillingAdd2" for="billingAdd2">${make_preferred_billing}</label>
																				</div>
																			</c:when>
																			<c:otherwise>
																			<div class="checkbox fl">
																				<dsp:input type="checkbox" name="billingAdd2" bean="ProfileFormHandler.billAddrValue.makeBilling" id="billingAdd2" iclass="enableDisable" >
                                                                                    <dsp:tagAttribute name="aria-checked" value="false"/>
                                                                                    <dsp:tagAttribute name="aria-labelledby" value="lblbillingAdd2 errorbillingAdd2"/>
                                                                                </dsp:input>
																			</div>
																			<div class="label">
																				<label id="lblbillingAdd2" for="billingAdd2">${make_preferred_billing}</label>
																			</div>
																			</c:otherwise>
																		</c:choose>
																	</div>
																</div>
															</c:otherwise>
														</c:choose>
													</div>
												</c:if>
											</c:forEach>
											</div>
										</c:if>
									</dsp:oparam>
								</dsp:droplet>

							<div class="radioItem input <c:if test="${radiobtn != 'true'}">last</c:if>">
								<div class="clearfix">
									<div class="radio">
										<dsp:input type="radio" name="addressToShip" id="addressToShip4" checked="${radiobtn}"	bean="ProfileFormHandler.billAddrValue.newNickname" value="newBillingAddress" iclass="newAddOpt" >
                                            <dsp:tagAttribute name="aria-checked" value="true"/>
                                            <dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4 erroraddressToShip4"/>
                                        </dsp:input>
									</div>
									<div class="label">
										<label id="lbladdressToShip4" for="addressToShip4"> <span>${add_new_billing_addr_radio}</span></label>
									</div>
								</div>	
								
                                <div class="subForm clearfix hidden">
                                	<div class="input grid_3"> 
                                       	<div class="label">
											<dsp:getvalueof var="err_profile_firstname"	bean="ProfileFormHandler.errorMap.firstName"></dsp:getvalueof>
											<label id="lblfirstName" for="firstName">${first_name}<span class="required">*</span></label>
										</div>
										<div class="text">
                                        	<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.firstName" name="firstName" maxlength="30" iclass="required"	required="${requiredTrueText}" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblfirstName errorfirstName"/>
                                            </dsp:input>
										</div>
										<c:if test="${not empty err_profile_firstname}"><label class="error" for="firstName"><bbbe:error key="${err_profile_firstname}" language="${pageContext.request.locale.language}"/></label></c:if>
									</div>

									<div class="input grid_3">
										<div class="label">
											<dsp:getvalueof var="err_cc_last_name_empty"	bean="ProfileFormHandler.errorMap.lastName"></dsp:getvalueof>
											<label id="lbllastName" for="lastName">${last_name}<span class="required">*</span></label>
										</div>
										<div class="text">
											<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.lastName" maxlength="30" name="lastName" iclass="required" id="lastName" required="${requiredTrueText}" >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lbllastName errorlastName"/>
                                            </dsp:input>
											${lastName}
											<c:if test="${not empty err_cc_last_name_empty}">
												<label id="errorlastName" class="error" for="lastName"><bbbe:error key="${err_cc_last_name_empty}" language="${pageContext.request.locale.language}"/></label>
											</c:if>
										</div>
									</div>
							 
									<div class="input grid_3 suffix_3">
										<div class="label">
											<dsp:getvalueof var="err_company_length_incorrect"	bean="ProfileFormHandler.errorMap.companyLengthIncorrect"></dsp:getvalueof>
											<label id="lblcompanyName" for="companyName"> ${company} </label>
										</div>
										<div class="text">
											<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.companyName" maxlength="20" id="companyName" name="companyName">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblcompanyName errorcompanyName"/>
                                            </dsp:input>
											<c:if test="${not empty err_company_length_incorrect}">
												<label id="errorcompanyName" class="error" for="companyName"><bbbe:error key="${err_company_length_incorrect}" language="${pageContext.request.locale.language}"/></label>
											</c:if>
                                       </div>
                                    </div>
									<div class="input grid_3"> 
										<div class="label">
											<dsp:getvalueof var="err_profile_address1"	bean="ProfileFormHandler.errorMap.address1"></dsp:getvalueof>
											<label id="lblshippingAddress1" for="shippingAddress1">${lbl_address1}<span class="required">*</span></label>
										</div>
										<div class="text">
                                           	<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.address1" maxlength="30"  id="shippingAddress1" name="shippingAddress1"  >
                                                <dsp:tagAttribute name="aria-required" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblshippingAddress1 errorshippingAddress1"/>
                                            </dsp:input>
											<c:if test="${not empty err_profile_address1}">
												<label id="errorshippingAddress1" class="error" for="address1"><bbbe:error key="${err_profile_address1}" language="${pageContext.request.locale.language}"/></label>
											</c:if>
										</div>
									</div>
									<div class="input grid_3">
										<div class="label">
											<dsp:getvalueof var="err_profile_address2"	bean="ProfileFormHandler.errorMap.address2"></dsp:getvalueof>
											<label id="lblshippingAddress2" for="shippingAddress2">${lbl_address2}</label>
										</div>
                                        <div class="text">
											<dsp:input type="text" 	bean="ProfileFormHandler.billAddrValue.address2" maxlength="30" id="shippingAddress2" name="shippingAddress2">
                                                <dsp:tagAttribute name="aria-required" value="false"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblshippingAddress2 errorshippingAddress2"/>
                                            </dsp:input>
											<c:if test="${not empty err_profile_address2}">
												<label id="errorshippingAddress2" class="error" for="shippingAddress2"><bbbe:error key="${err_profile_address2}" language="${pageContext.request.locale.language}"/></label>
											</c:if>
										</div>
									</div>
									<div class="grid_6">
										<div class="input grid_3 alpha omega">
											<dsp:getvalueof var="err_profile_city"	bean="ProfileFormHandler.errorMap.city"/>
											<div class="label"><label id="lblcityName" for="cityName"> ${city}</label>
												<span class="required">*</span> </label>
											</div>
											<div class="text">
												<dsp:input type="text" bean="ProfileFormHandler.billAddrValue.city" name="cityName" maxlength="25" iclass="required" required="${requiredTrueText}" id="cityName">
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
                                                </dsp:input>
												<c:if test="${not empty err_profile_city}">
													<label id="errorcityName" class="error" for="cityName"><bbbe:error key="${err_profile_city}" language="${pageContext.request.locale.language}"/></label>
												</c:if>
											</div>
										</div>
										<div class="input grid_3 alpha padLeft_20">
											<div class="label">
												<dsp:getvalueof var="err_profile_state"	bean="ProfileFormHandler.errorMap.state"></dsp:getvalueof>
												<dsp:getvalueof var="err_cc_last_name_empty" bean="ProfileFormHandler.errorMap.state"></dsp:getvalueof>
												<label id="lblstateName" for="stateName">${lbl_state}<span class="required">*</span></label>
											</div>
											<div class="select noMarTop">
												<dsp:select bean="ProfileFormHandler.billAddrValue.state" name="stateName" id="stateName" iclass="uniform">
													<dsp:option><bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" /></dsp:option>
													<dsp:droplet name="/atg/dynamo/droplet/ForEach">
														<dsp:param name="array" bean="ProfileFormHandler.stateList" />
														<dsp:param name="elementName" value="statelist" />
														<dsp:oparam name="output">
															<dsp:getvalueof id="stateName" param="statelist.stateName" />
															<dsp:getvalueof id="stateCode" param="statelist.stateCode" />
															<dsp:option value="${stateCode}">
																<c:out value="${stateName}" />
															</dsp:option>
														</dsp:oparam>
													</dsp:droplet>
                                                    <dsp:tagAttribute name="aria-required" value="true"/>
                                                    <dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
												</dsp:select>
												<c:if test="${not empty err_cc_state_empty}">
													<label id="errorstateName" class="error" for="stateName"><bbbe:error key="${err_cc_state_empty}" language="${pageContext.request.locale.language}"/></label>
												</c:if>
												<c:if test="${not empty err_cc_state_match}">
													<label id="errorstateName" class="error" for="stateName"><bbbe:error key="${err_cc_state_match}" language="${pageContext.request.locale.language}"/></label>
												</c:if>
												<dsp:input bean="ProfileFormHandler.billAddrValue.country"	value="${defaultCountry}" id="country" type="hidden" >
                                                    <dsp:tagAttribute name="aria-required" value="false"/>
                                                    <dsp:tagAttribute name="aria-describedby" value="country errorcountry"/>
                                                </dsp:input>
											</div>
										</div>
									</div>
                                    <div class="input grid_3 suffix_3">
                                        <div class="label">
                                           	<dsp:getvalueof var="err_cc_zip_empty" bean="ProfileFormHandler.errorMap.postalCode"></dsp:getvalueof>
                                            <label id="lblzipCode" for="zipCode">${zip} <span class="required">*</span> </label>
                                        </div>
                                        <c:set var="BedBathCanadaSite">
											<bbbc:config key="BedBathCanadaSiteCode" configName="ContentCatalogKeys" />
										</c:set>
                                        <div class="text">
                                            <c:choose>
						                		<c:when test="${currentSiteId eq BedBathCanadaSite}">
													<dsp:input bean="ProfileFormHandler.billAddrValue.postalCode" name="zipCA" id="zipCode" type="text" >
                                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="lblzipCode errorzipCode"/>
                                                    </dsp:input>
												</c:when>
												<c:otherwise>
													<dsp:input bean="ProfileFormHandler.billAddrValue.postalCode" name="zipUS" id="zipCode" type="text" >
                                                        <dsp:tagAttribute name="aria-required" value="true"/>
                                                        <dsp:tagAttribute name="aria-labelledby" value="lblzipCode errorzipCode"/>
                                                    </dsp:input>
												</c:otherwise>
											</c:choose>
											<c:if test="${not empty err_cc_zip_empty}">
												<label id="errorzipCode" class="error" for="zipCode"><bbbe:error key="${err_cc_zip_empty}" language="${pageContext.request.locale.language}"/></label>
											</c:if>
                                        </div>
                                    </div>
                                    
									<dsp:getvalueof bean="Profile.secondaryAddresses" id="addr"/>
										<c:if test="${empty addr}">
											<c:set var="mkprefaddrDisable" value="true"/>
										</c:if>
                                    <div class="input grid_3">
                                        <div class="checkbox fl">
                                            <dsp:input type="checkbox" bean="ProfileFormHandler.makePreferredSet"  name="billingAdd3" id="billingAdd3" checked="true" disabled="${mkprefaddrDisable}">
                                                <dsp:tagAttribute name="aria-checked" value="true"/>
                                                <dsp:tagAttribute name="aria-labelledby" value="lblbillingAdd3 errorbillingAdd3"/>
                                            </dsp:input>
                                        </div>
                                        <div class="label">
                                            <label id="lblbillingAdd3" for="billingAdd3">${make_preferred_billing}</label>
                                        </div>
                                    </div>
								</div>
							</div>
						</fieldset>
						<%--  BBBH-5852 : Desktop | While user saves edited credit card details it does not show in saved section --%>
						<dsp:input bean="ProfileFormHandler.fromPage" type="hidden" value="updateCard" />
						<div class="buttons">
							<div class="button button_active marRight_5">
								${Visa}
								<dsp:input type="submit" value="save" id="submitSaveBtnCreditCard" bean="ProfileFormHandler.updateCard">
                                    <dsp:tagAttribute name="aria-pressed" value="false"/>
                                    <dsp:tagAttribute name="aria-labelledby" value="submitSaveBtnCreditCard"/>
                                    <dsp:tagAttribute name="role" value="button"/>
                                </dsp:input>
							</div>
							<div class="button button_active">
								<a href="view_credit_card.jsp" role="link"><bbbl:label key="lbl_change_pass_model_cancel" language="${pageContext.request.locale.language}" /></a>
							</div>
						</div>
					</dsp:form>
				</div>
			</div>
		</div>
	</div>
    <jsp:attribute name="footerContent">
    <script type="text/javascript">
        $('#addCreditCard').on('submit', function(){
            if (typeof acctUpdate === "function") {
                acctUpdate('edited saved card info');
            }
        });
    </script>
    </jsp:attribute>
	</bbb:pageContainer>
</dsp:page>
