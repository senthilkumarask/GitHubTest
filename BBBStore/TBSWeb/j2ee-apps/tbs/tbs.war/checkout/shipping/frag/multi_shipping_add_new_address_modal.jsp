<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>

	<%-- Variables --%>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof id="currentSiteId" bean="/atg/multisite/Site.id" />
	<dsp:getvalueof var="addressSelectId" param="addressSelectId" />
	<dsp:getvalueof var="isLTL" param="isLTL" />
	<c:set var="qasAddrLength" scope="request"><bbbc:config key="qas_address_length" configName="QASKeys"/></c:set>
	<c:set var="poBoxEnabled" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
	<c:choose>
		<c:when test="${not empty poBoxEnabled &&  poBoxEnabled && !isLTL }">
			<c:set var="iclassValue" value=""/>
		</c:when>
		<c:otherwise>
			<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
		</c:otherwise>
	</c:choose>

	<%-- <div id="addNewAddressDialogWrapper" class="row addNewAddressDialogWrapper">--%>
	<div class="row new-shipping-address">
		<div class="small-12 columns">
			<h2><bbbl:label key="lbl_shipping_new_address" language="${pageContext.request.locale.language}" /></h2>
		</div>
        <div class="hidden small-12 columns" id="addressModalError"> <p class="error"></p> </div>
		<dsp:form id="addNewAddressForm" name="addNewAddressForm" action="#" method="post">

			<dsp:input bean="BBBShippingGroupFormhandler.address.country" value="${defaultCountry}" type="hidden"/>
			<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.addNewAddressURL" value="/tbs/checkout/shipping/frag/newAdd_MultiShip.jsp" />
			<dsp:input type="hidden" name="cisiIndex" bean="BBBShippingGroupFormhandler.cisiIndex" value="${addressSelectId}" iclass="addNewAddressIndexTarget" />
			<div class="small-12 large-6 columns">
				<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_first_name" language="${pageContext.request.locale.language}"/></c:set>
                <c:if test='${param["firstName"] ne ""}'>
                    <c:set var="fName">${param['firstName']}</c:set>
                </c:if>
				<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="${fName}">
					<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
				</dsp:input>
			</div>
			<div class="small-12 large-6 columns">
                <c:if test='${param["lastName"] ne ""}'>
                    <c:set var="lastName">${param['lastName']}</c:set>
                </c:if>
				<c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_last_name' language='${pageContext.request.locale.language}'/></c:set>
				<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="${lastName}">
					<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
				</dsp:input>
			</div>
			<div class="small-12 columns">
				<c:if test='${param["companyName"] ne ""}'>
                    <c:set var="companyName">${param['companyName']}</c:set>
                </c:if>
                <c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_company' language='${pageContext.request.locale.language}'/></c:set>
				<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" value="${companyName}">
					<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
					<dsp:tagAttribute name="aria-required" value="false"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
				</dsp:input>
			</div>
			<div class="small-12 columns">
				<c:if test='${param["address1"] ne ""}'>
                    <c:set var="address1">${param['address1']}</c:set>
                </c:if>
                <c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_line1' language='${pageContext.request.locale.language}'/></c:set>
				<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address1" maxlength="${qasAddrLength}" name="address1" id="checkoutaddress1" value="${address1}">
					<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
				</dsp:input>
			</div>
			<div class="small-12 columns">
				<c:if test='${param["address2"] ne ""}'>
                    <c:set var="address2">${param['address2']}</c:set>
                </c:if>
                <c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_line2' language='${pageContext.request.locale.language}'/></c:set>
				<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address2" maxlength="${qasAddrLength}" name="address2" id="checkoutaddress2" value="${address2}">
					<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress2"/>
				</dsp:input>
			</div>
			<div class="small-12 columns">
				<c:if test='${param["city"] ne ""}'>
                    <c:set var="city">${param['city']}</c:set>
                </c:if>
                <c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_city' language='${pageContext.request.locale.language}'/></c:set>
				<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="checkoutcityName" value="${city}">
					<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
				</dsp:input>
			</div>

			<div class="small-12 large-6 columns">
				<c:if test='${param["state"] ne ""}'>
                    <c:set var="state">${param['state']}</c:set>
                </c:if>
                <dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="checkoutstateName" nodefault="true">
				<dsp:droplet name="StateDroplet">
				<dsp:oparam name="output">
					<option value="">
						<bbbl:label key="lbl_addressBook_select_state" language="${pageContext.request.locale.language}" />
					</option>
					<dsp:droplet name="ForEach">
					<dsp:param name="array" param="location" />
					<dsp:param name="sortProperties" value="+stateName" />
					<dsp:oparam name="output">
						<dsp:getvalueof param="element.stateName" id="elementVal"></dsp:getvalueof>
						<dsp:getvalueof param="element.stateCode" id="elementCode"></dsp:getvalueof>
						<c:choose>
							<c:when test="${fn:toLowerCase(beddingState) eq fn:toLowerCase(elementVal)}" >
								<option value="${elementCode}" selected="selected">${elementVal}</option>
							</c:when>
							<c:otherwise>
								<c:choose>
                                    <c:when test="${fn:toLowerCase(state) eq fn:toLowerCase(elementCode)}" >
                                        <option value="${elementCode}" selected="selected">${elementVal}</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="${elementCode}">${elementVal}</option>
                                    </c:otherwise>
                                </c:choose>
							</c:otherwise>
						</c:choose>
					</dsp:oparam>
					</dsp:droplet>
				</dsp:oparam>
				</dsp:droplet>
				<dsp:tagAttribute name="aria-required" value="true"/>
				<dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
				</dsp:select>
			</div>
			<div class="small-12 large-6 columns">
				<c:if test='${param["zipCode"] ne ""}'>
                    <c:set var="zipCode">${param['zipCode']}</c:set>
                </c:if>
                <c:set var="placeholderText">
					<c:choose>
						<c:when test="${currentSiteId eq 'TBS_BedBathCanada'}">
							<bbbl:label key="lbl_subscribe_canadazipcode" language="${pageContext.request.locale.language}" />
						</c:when>
						<c:otherwise>
							<bbbl:label key="lbl_shipping_new_zip" language="${pageContext.request.locale.language}" />
						</c:otherwise>
					</c:choose>
				</c:set>
				<c:choose>
					<c:when test="${defaultCountry ne 'US'}">
						<c:set var="zipCodeClass" value="zipCA" scope="page"/>
					</c:when>
					<c:otherwise>
						<c:set var="zipCodeClass" value="zipUS" scope="page"/>
					</c:otherwise>
				</c:choose>
				<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="checkoutzip" value="${zipCode}">
					<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
					<dsp:tagAttribute name="aria-required" value="true"/>
					<dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
				</dsp:input>
				<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.ltlCommerceItem" value="false" iclass="ltlCommerceItem" />
			</div>

			<c:if test="${isLTL}">
				<c:if test='${param["email"] ne ""}'>
                    <c:set var="email">${param['email']}</c:set>
                </c:if>
                <div class="small-12 large-6 columns">
					<c:set var="placeholderText"><bbbl:textArea key="txt_ltl_shipping_email" language="<c:out param='${language}'/>"/></c:set>
					<dsp:input type="text" name="email" id="email" bean="BBBShippingGroupFormhandler.address.email" value="${email}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblemail erroremail"/>
						<dsp:tagAttribute name="role" value="textbox"/>
					</dsp:input>
				</div>
				<c:if test='${param["phonenumber"] ne ""}'>
                    <c:set var="phonenumber">${param['phonenumber']}</c:set>
                </c:if>
                <div class="small-12 large-6 columns">
					<c:set var="placeholderText"><bbbl:textArea key="txt_ltl_shipping_phone" language="<c:out param='${language}'/>"/></c:set>
					<dsp:input type="text" name="basePhoneFull"  id="basePhone" bean="BBBShippingGroupFormhandler.address.phoneNumber" maxlength="10" iclass="phoneField required" value="${phonenumber}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblphone errorphone"/>
						<dsp:tagAttribute name="role" value="textbox"/>
					</dsp:input>
				</div>
                <div class="small-12 large-6 right columns">
					<c:if test='${param["alternatePhoneNumber"] ne ""}'>
                        <c:set var="alternatePhoneNumber">${param['alternatePhoneNumber']}</c:set>
                    </c:if>
                    <c:set var="placeholderText"><bbbl:label key="lbl_shipping_alternate_phone" language="<c:out param='${language}'/>"/></c:set>
					<dsp:input type="text" name="alternatePhoneNumber"  id="altphone" bean="BBBShippingGroupFormhandler.address.alternatePhoneNumber" maxlength="10" value="${alternatePhoneNumber}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblaltphone erroraltphone"/>
						<dsp:tagAttribute name="role" value="textbox"/>
					</dsp:input>
				</div>
			</c:if>

			<div class="small-12 large-6 columns">
			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="false">
					<label class="checkbox" id="lblsaveToAccount" for="saveToAccount">
						<c:choose>
							<c:when test="${isLTL && not empty fName}">
								<input type="checkbox" disabled="disabled">
								<span></span>
							</c:when>
							<c:otherwise>
								<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" name="saveToAccount" id="saveToAccount" checked="true">
									<dsp:tagAttribute name="aria-checked" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
								</dsp:input>
								<span></span>
							</c:otherwise>
						</c:choose>
						<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
					</label>
				</dsp:oparam>
			</dsp:droplet>
		</div>
			<%-- Required to display Create and Cancel buttons in the same row even if there is error message in State field --%>
			<div class="small-12 columns">&nbsp;</div>

			<c:set var="lbl_button_create" scope="page">
				<bbbl:label key="lbl_shipping_button_create" language="${pageContext.request.locale.language}" />
			</c:set>

			<div class="small-12 large-6 columns">
				<dsp:input iclass="hidden" type="submit" bean="BBBShippingGroupFormhandler.addNewAddress" name="addNewAddressHiddenBtn" id="addNewAddressHiddenBtn"/>
				<a id="addNewAddressBtn" name="addNewAddressBtn" class="small button service expand">${lbl_button_create}</a>
			</div>
			<div class="small-12 large-6 columns">
				<a class="small button secondary column close-modal">Cancel</a>
			</div>

		</dsp:form>
	</div>

</dsp:page>
