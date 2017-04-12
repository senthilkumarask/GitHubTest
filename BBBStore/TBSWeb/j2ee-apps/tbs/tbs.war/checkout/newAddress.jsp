<dsp:page>

	<%-- Imports --%>
	<dsp:importbean bean="/com/bbb/commerce/order/formhandler/BBBShippingGroupFormhandler"/>
	<dsp:importbean bean="/com/bbb/commerce/order/paypal/PayPalSessionBean"/>
	<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
	<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
	<dsp:importbean bean="/atg/userprofiling/Profile"/>
	<dsp:importbean bean="/atg/multisite/Site"/>
	<dsp:importbean bean="/com/bbb/commerce/order/droplet/BBBPackNHoldDroplet"/>
	<dsp:importbean bean="/com/bbb/selfservice/StateDroplet" />
	<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
	<dsp:importbean bean="/com/bbb/commerce/checkout/droplet/BBBBeddingKitsAddrDroplet"/>
	<dsp:importbean bean="/com/bbb/commerce/order/purchase/OrderHasLTLItemDroplet"/>

	<%-- Variables --%>
	<dsp:getvalueof id="defaultCountry" bean="Site.defaultCountry" />
	<dsp:getvalueof param="selectedAddress" var="selectedAddress"/>
	<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO"/>
	<c:choose>
		<c:when test = "${beddingShipAddrVO != null }">
			<c:set var="beddingKit" value="true"/>
			<c:set var="beddingState" value="${beddingShipAddrVO.state}"/>
		</c:when>
		<c:otherwise>
			<c:set var="beddingKit" value="false"/>
		</c:otherwise>
	</c:choose>
	
	<dsp:droplet name="OrderHasLTLItemDroplet">
			 <dsp:param name="order" bean="ShoppingCart.current"/>
			 <dsp:oparam name="output">
				<dsp:getvalueof var="orderHasLTLItem" param="orderHasLTLItem" />
			 </dsp:oparam>
	</dsp:droplet>
	
	<dsp:droplet name="BBBPackNHoldDroplet">
		<dsp:param name="order" bean="ShoppingCart.current"/>
		<dsp:oparam name="output">
			<dsp:getvalueof param="isPackHold" var="isPackHold"/>
		</dsp:oparam>
	</dsp:droplet>
	<c:if test="${beddingShipAddrVO == null && isPackHold eq true}">
		<dsp:droplet name="BBBBeddingKitsAddrDroplet">
			<dsp:param name="order" bean="ShoppingCart.current"/>
			<dsp:param name="shippingGroup" param="order.shippingGroups"/>
			<dsp:param name="" bean="ShoppingCart.current"/>
			<dsp:param name="isPackHold" value="${true}"/>
			<dsp:oparam name="beddingKit">
				<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
				<c:set var="beddingKit" value="true"/>
				<c:set var="beddingState" value="${beddingShipAddrVO.state}"/>
			</dsp:oparam>
			<dsp:oparam name="weblinkOrder">
				<dsp:getvalueof var="beddingShipAddrVO" param="beddingShipAddrVO" />
				<c:set var="beddingState" value="${beddingShipAddrVO.state}"/>
			</dsp:oparam>
			<dsp:oparam name="notBeddingKit">
				<c:set var="beddingKit" value="false"/>
			</dsp:oparam>
		</dsp:droplet>
	</c:if>
	
	<c:set var="poBoxEnabled" scope="request"><bbbc:config key="shipTo_POBoxOn" configName="FlagDrivenFunctions"/></c:set>
	<c:set var="qasAddrLength" scope="request"><bbbc:config key="qas_address_length" configName="QASKeys"/></c:set>
	<c:choose>
		<c:when test="${not empty poBoxEnabled &&  poBoxEnabled && !orderHasLTLItem}">
			<c:set var="iclassValue" value=""/>
		</c:when>
		<c:otherwise>
			<c:set var="iclassValue" value="poBoxAddNotAllowed"/>
		</c:otherwise>
	</c:choose>

	<!-- Added as Part of PayPal Story - 83-N : Start -->
	<!-- Pre Populate fields in address fields if paypalAddress is true-->
	<dsp:getvalueof var= "address" bean = "PayPalSessionBean.address"/>
	<c:choose>
		<c:when test="${address != null}">
			<c:set var="payPalAddress" value="true"/>
			<dsp:getvalueof var="isInternationalOrPOError" bean="PayPalSessionBean.internationalOrPOError"/>
		</c:when>
		<c:otherwise>
			<c:set var="payPalAddress" value="false"/>
		</c:otherwise>
	</c:choose>
	<dsp:getvalueof var="paypal" param="paypal" />
	<c:if test="${paypal}">
		<c:set var="isChecked" value="false"/>
	</c:if>
	<!-- Added as Part of PayPal Story - 83-N : End -->
	<dsp:getvalueof var="cmo" param="cmo"/>
	
	<label class="inline-rc radio gray-panel shipping-address new-shipping-address-trigger <c:if test="${cmo}">disabled</c:if>" id="lbladdressToShip4n" for="addressToShip4nNormal">
		<c:choose>
			<c:when test="${empty preFillValues || preFillValues}">
				<!-- BBBSL-971 starts -->
				<c:choose>
					<c:when test="${cmo}">
						<dsp:input type="radio" name="addressToShip" id="addressToShip4nNormalCmo" value="true" checked="false" bean="BBBShippingGroupFormhandler.shipToAddressName" iclass="newAddOpt" disabled="disabled"/>
					</c:when>
					<c:when test="${empty selectedAddress && beddingKit eq true}">
						<dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="college" checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName"/>
						<dsp:input type="hidden" bean="BBBShippingGroupFormhandler.beddingKitOrder" value="beddingKit"/>
					</c:when>
					<c:when test="${isPackHold eq true}">
						<dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="college" checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName"/>
					</c:when>
					<c:when test="${(empty selectedAddress) &&  beddingKit eq false}">
						<dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="true" checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName" iclass="newAddOpt"/>
					</c:when>
					<c:otherwise>
						<%-- KP COMMENT START: changing this logic because it wasn't working with some registry addresses.
												now tracking any checked radios with "isChecked" variable (which was already in the code) --%>
						<dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="true" checked="${!isChecked}" iclass="newAddOpt" bean="BBBShippingGroupFormhandler.shipToAddressName"/>
						<%-- <dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="true" checked="${(isAnonymousProfile&&!containsaddressForGuest)||!containsaddressForUser || payPalAddress || isFormException}" iclass="newAddOpt" bean="BBBShippingGroupFormhandler.shipToAddressName"/> --%>
						<%-- KP COMMENT END --%>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<c:out value="otherwise"/>
				<c:choose>
					<c:when test="${cmo}">
						<dsp:input type="radio" name="addressToShip" id="addressToShip4nNormalCmo" value="true" checked="false" bean="BBBShippingGroupFormhandler.shipToAddressName" iclass="newAddOpt" disabled="disabled">
							<dsp:tagAttribute name="aria-checked" value="true"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4n"/>
						</dsp:input>
					</c:when>
					<c:otherwise>
						<dsp:input type="radio" name="addressToShip" id="addressToShip4nNormal" value="true" checked="true" bean="BBBShippingGroupFormhandler.shipToAddressName" iclass="newAddOpt">
							<dsp:tagAttribute name="aria-checked" value="true"/>
							<dsp:tagAttribute name="aria-labelledby" value="lbladdressToShip4n"/>
						</dsp:input>
					</c:otherwise>
				</c:choose>
			</c:otherwise>
		</c:choose>
		<span></span>

		<%-- label text --%>
		<c:choose>
			<c:when test="${beddingKit eq true || isPackHold eq true}">
				<bbbl:label key="lbl_college_address" language="${pageContext.request.locale.language}" />
			</c:when>
			<c:otherwise>
				<bbbl:label key="lbl_shipping_new_address" language="${pageContext.request.locale.language}" />
			</c:otherwise>
		</c:choose>
	</label>

	<dsp:input bean="BBBShippingGroupFormhandler.address.country" value="${defaultCountry}" type="hidden"/>
	<div class="row new-shipping-address <c:if test='${isChecked}'>hidden</c:if>">
		<div class="small-12 large-6 columns">
			<c:set var="placeholderText"><bbbl:label key="lbl_shipping_new_first_name" language="${pageContext.request.locale.language}"/></c:set>
			<c:choose>
				<c:when test="${beddingKit eq true}">
					<input type="text" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" aria-required="true" aria-labelledby="lblcheckoutfirstName errorcheckoutfirstName" placeholder="<c:out value='${placeholderText}'/>" />
				</c:when>
				<c:when test="${payPalAddress eq 'true'}">
					<dsp:getvalueof var= "firstName" bean="PayPalSessionBean.address.firstName"/>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName" value="${firstName}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
					</dsp:input>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
					</dsp:input>
				</c:when>
				<c:otherwise>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.firstName" maxlength="40" name="checkoutfirstName" id="checkoutfirstName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutfirstName errorcheckoutfirstName"/>
					</dsp:input>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 large-6 columns">
			<c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_last_name' language='${pageContext.request.locale.language}'/></c:set>
			<c:choose>
				<c:when test="${beddingKit eq true || isPackHold eq true}">
					<input type="text" maxlength="40" name="checkoutlastName" id="checkoutlastName" aria-required="true" aria-labelledby="lblcheckoutlastName errorcheckoutlastName" placeholder="<c:out value='${placeholderText}'/>" />
				</c:when>
				<c:when test="${payPalAddress eq 'true'}">
					<dsp:getvalueof var= "lastName" bean = "PayPalSessionBean.address.lastName"/>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName" value="${lastName}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
					</dsp:input>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
					</dsp:input>
				</c:when>
				<c:otherwise>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.lastName" maxlength="40" name="checkoutlastName" id="checkoutlastName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcheckoutlastName errorcheckoutlastName"/>
					</dsp:input>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 columns">
			<c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_company' language='${pageContext.request.locale.language}'/></c:set>
			<c:choose>
				<c:when test="${beddingKit eq true || isPackHold eq true}">
					<input type="text" disabled="true" maxlength="50" name="college" id="college" value="${beddingShipAddrVO.collegeName}" aria-required="true" aria-labelledby="lblcompany errorcompany" placeholder="" />
					<input type="hidden" disabled="true" maxlength="40" name="company" id="company" value="${beddingShipAddrVO.companyName}" aria-required="true" aria-labelledby="lblcompany errorcompany" placeholder="<c:out value='${placeholderText}'/>" />
				</c:when>
				<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
					<dsp:getvalueof var="companyName" bean = "PayPalSessionBean.address.companyName"/>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company" value="${companyName}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
					</dsp:input>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
					</dsp:input>
				</c:when>
				<c:otherwise>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.companyName" maxlength="40" name="company" id="company">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcompany errorcompany"/>
					</dsp:input>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 columns">
			<c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_line1' language='${pageContext.request.locale.language}'/></c:set>
			<c:choose>
				<c:when test="${beddingKit eq true || isPackHold eq true}">
					<input type="text" maxlength="40" name="checkoutlastName" id="checkoutlastName" aria-required="true" aria-labelledby="lblcheckoutlastName errorcheckoutlastName" placeholder="<c:out value='${placeholderText}'/>" />
				</c:when>
				<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
					<dsp:getvalueof var= "address1" bean = "PayPalSessionBean.address.address1"/>
					<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address1" maxlength="${qasAddrLength}" name="address1" id="address1" value="${address1}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
						<dsp:tagAttribute name="autocomplete" value="off"/>
					</dsp:input>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address1" maxlength="${qasAddrLength}" name="address1" id="address1">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
						<dsp:tagAttribute name="autocomplete" value="off"/>
					</dsp:input>
				</c:when>
				<c:otherwise>
					<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address1" maxlength="${qasAddrLength}" name="address1" id="address1">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress1 erroraddress1"/>
						<dsp:tagAttribute name="autocomplete" value="off"/>
					</dsp:input>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 columns">
			<c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_line2' language='${pageContext.request.locale.language}'/></c:set>
			<c:choose>
				<c:when test="${beddingKit eq true || isPackHold eq true}">
					<input type="text" iclass="${iclassValue}" disabled="true" maxlength="${qasAddrLength}" name="address2" id="address2" value= "${beddingShipAddrVO.addrLine2}" aria-required="true" aria-labelledby="lbladdress2 erroraddress2" placeholder="<c:out value='${placeholderText}'/>"/>
				</c:when>
				<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
					<dsp:getvalueof var= "address2" bean = "PayPalSessionBean.address.address2"/>
					<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address2" maxlength="${qasAddrLength}" name="address2" id="address2" value="${address2}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
					</dsp:input>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address2" maxlength="${qasAddrLength}" name="address2" id="address2">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
					</dsp:input>
				</c:when>
				<c:otherwise>
					<dsp:input type="text" iclass="${iclassValue}" bean="BBBShippingGroupFormhandler.address.address2" maxlength="50" name="address2" id="address2">
						<dsp:tagAttribute name="placeholder" value="${placeholderText}" />
						<dsp:tagAttribute name="aria-required" value="false"/>
						<dsp:tagAttribute name="aria-labelledby" value="lbladdress2 erroraddress2"/>
					</dsp:input>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 columns">
			<c:set var="placeholderText"><bbbl:label key='lbl_shipping_new_city' language='${pageContext.request.locale.language}'/></c:set>
			<c:choose>
				<c:when test="${beddingKit eq true || isPackHold eq true}">
					<input type="text" maxlength="25" disabled="true" name="cityName" id="cityName" value="${beddingShipAddrVO.city}" aria-required="true" aria-labelledby="lblcityName errorcityName" placeholder="<c:out value='${placeholderText}'/>" />
				</c:when>
				<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
					<dsp:getvalueof var= "city" bean = "PayPalSessionBean.address.city"/>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName" value="${city}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
					</dsp:input>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
					</dsp:input>
				</c:when>
				<c:otherwise>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.city" maxlength="25" name="cityName" id="cityName">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblcityName errorcityName"/>
					</dsp:input>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 large-6 columns">
			<c:choose>
				<c:when test="${beddingKit eq true || isPackHold eq true}">
					<dsp:select bean="BBBShippingGroupFormhandler.address.state" disabled="true" name="stateName" id="stateName">
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
										<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
										<c:choose>
											<c:when test="${fn:toLowerCase(beddingState) eq fn:toLowerCase(elementCode)}">
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
													${elementVal}
												</option>
											</c:when>
											<c:otherwise>
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
													${elementVal}
												</option>
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:select>
				</c:when>
				<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
					<dsp:getvalueof var= "state" bean = "PayPalSessionBean.address.state"/>
					<dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="stateName">
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
										<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
										<c:choose>
											<c:when test="${fn:toLowerCase(state) eq fn:toLowerCase(elementCode)}">
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
													${elementVal}
												</option>
											</c:when>
											<c:otherwise>
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
													${elementVal}
												</option>
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
					</dsp:select>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="stateName">
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
										<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
										<c:choose>
											<c:when test="${fn:toLowerCase(beddingState) eq fn:toLowerCase(elementCode)}">
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
													${elementVal}
												</option>
											</c:when>
											<c:otherwise>
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
													${elementVal}
												</option>
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
					</dsp:select>
				</c:when>
				<c:otherwise>
					<dsp:select bean="BBBShippingGroupFormhandler.address.state" name="stateName" id="stateName" nodefault="true">
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
										<dsp:getvalueof param="element.showOnBilling" id="showOnBilling"></dsp:getvalueof>
										<c:choose>
											<c:when test="${fn:toLowerCase(beddingState) eq fn:toLowerCase(elementVal)}" >
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}" selected="selected">
													${elementVal}
												</option>
											</c:when>
											<c:otherwise>
												<option data-saveToProfile="${showOnBilling}" value="${elementCode}">
													${elementVal}
												</option>
											</c:otherwise>
										</c:choose>
									</dsp:oparam>
								</dsp:droplet>
							</dsp:oparam>
						</dsp:droplet>
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblstateName errorstateName"/>
					</dsp:select>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 large-6 columns">
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
			<c:choose>
				<c:when test="${beddingKit eq true || isPackHold eq true}">
					<input type="text" disabled="true" maxlength="10" name="${zipCodeClass}" id="zip" value="${beddingShipAddrVO.zip}" aria-required="true" aria-labelledby="lblzip errorzip" placeholder="<c:out value='${placeholderText}'/>" />
				</c:when>
				<c:when test="${payPalAddress eq 'true' && isInternationalOrPOError eq false}">
					<dsp:getvalueof var= "postalCode" bean = "PayPalSessionBean.address.postalCode"/>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip" value="${postalCode}">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
					</dsp:input>
				</c:when>
				<c:when test="${empty preFillValues || preFillValues}">
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
					</dsp:input>
				</c:when>
				<c:otherwise>
					<dsp:input type="text" bean="BBBShippingGroupFormhandler.address.postalCode" maxlength="10" name="${zipCodeClass}" id="zip">
						<dsp:tagAttribute name="placeholder" value="${placeholderText} *" />
						<dsp:tagAttribute name="aria-required" value="true"/>
						<dsp:tagAttribute name="aria-labelledby" value="lblzip errorzip"/>
					</dsp:input>
				</c:otherwise>
			</c:choose>
		</div>
		<div class="small-12 large-6 columns">
			<dsp:droplet name="Switch">
				<dsp:param name="value" bean="Profile.transient"/>
				<dsp:oparam name="false">
					<dsp:getvalueof var="profileAddresses" param="profileAddresses" />
					<c:choose>
						<c:when test="${beddingKit eq true || isPackHold eq true}">
							<label class="inline-rc checkbox disabled" id="lblsaveToAccount" for="saveToAccount">
								<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="false" type="checkbox" disabled="true" name="saveToAccount" id="saveToAccount" checked="false" >
									<dsp:tagAttribute name="aria-checked" value="true"/>
									<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
								</dsp:input>
								<span></span>
								<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
							</label>
						</c:when>
						<c:otherwise>
							<c:choose>
								<c:when test="${empty profileAddresses}">
									<label class="inline-rc checkbox disabled" id="lblsaveToAccount" for="saveToAccount">
										<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" name="saveToAccount" id="saveToAccount" checked="false" >
											<dsp:tagAttribute name="aria-checked" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
										</dsp:input>
										<span></span>
										<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
									</label>
								</c:when>
								<c:otherwise>
									<label class="inline-rc checkbox" id="lblsaveToAccount" for="saveToAccount">
										<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" name="saveToAccount" id="saveToAccount" checked="false">
											<dsp:tagAttribute name="aria-checked" value="true"/>
											<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
										</dsp:input>
										<span></span>
										<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
									</label>
								</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<dsp:input type="hidden" name="updateAddress" id="updateAddress" value="false" bean="BBBShippingGroupFormhandler.updateAddress" />
				</dsp:oparam>
				<dsp:oparam name="true">
					<label class="inline-rc checkbox hidden" id="lblsaveToAccount" for="saveToAccount">
						<dsp:input bean="BBBShippingGroupFormhandler.saveShippingAddress" value="true" type="checkbox" name="saveToAccount" id="saveToAccount" checked="true">
							<dsp:tagAttribute name="aria-checked" value="true"/>
							<dsp:tagAttribute name="aria-labelledby" value="lblsaveToAccount"/>
						</dsp:input>
						<span></span>
						<bbbl:label key="lbl_shipping_new_save_account" language="${pageContext.request.locale.language}" />
					</label>
				</dsp:oparam>
			</dsp:droplet>
		</div>
	</div>

	<dsp:getvalueof var="sessionBeanNull" bean="PayPalSessionBean.sessionBeanNull"/>
		<script type="text/javascript">
		$(document).ready(function(){
			 var $targetSaveShipping = $('#saveToAccount');
			 $('#stateName').on('change', function() {
				 var $option = ($(":selected",$(this)));
				 console.log($option.data("savetoprofile"));
				 if($option.data("savetoprofile")) {
					 $targetSaveShipping.prop('checked',true);
					 $targetSaveShipping.val(true);
				     $('label[for=saveToAccount]').show();
				 }else{
					 $targetSaveShipping.prop('checked',false);
					 $targetSaveShipping.val(false);
				     $('label[for=saveToAccount]').hide();
				 }
			 });
			
		});
	
	</script>
</dsp:page>
